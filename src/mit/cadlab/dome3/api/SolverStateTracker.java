package mit.cadlab.dome3.api;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.network.server.Debug;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 19.
 *
 * This class do following things:
 *
 * 1) Start its own thread and stop the thread running
 *    when any of following two conditions is met.
 *    - condition 1: Solving is complete
 *    - condition 2: Solving expires (=Solving has run longer than time limit specified by a user)
 *
 * 2) Provide information about solving state: getState() returning RUNNING, SUCCESS OR FAILURE
 *
 * 3) This class is used with submitAndGo() to track the state of solving.
 *    Because submitAndGo() does not hold the parent thread, users should rely on this class
 *    to know when solving finishes.
 *
 * 4) This class is not necessary for cases when users submit changes using submit():void
 *    Because submit() holds the parent thread until it finishes or expires,
 *    a code that should be run after solving finishes comes at the line just below submit().
 */
public class SolverStateTracker extends Thread {

    protected String key = "GREEN_LIST_KEY";
    private boolean isSolved = false;
    private boolean isOverTimeLimit = false;
    private RuntimeInterface runtimeInterface;
    private StatusChangeListenerForCompletionChecking statusChangeListener = null;

    public final static int RUNNING = 1;
    public final static int SUCCESS = 2;
    public final static int FAILURE = 3;

    public SolverStateTracker(RuntimeInterface runtimeInterface) {
        this.runtimeInterface = runtimeInterface;
        this.setDaemon(false);
    }

    /**
     * returns current state of solving thread
     * @return SolverStateTracker.RUNNING or SolverStateTracker.SUCCESS or SolverStateTracker.FAILURE
     */
    public int status() {
        if (isSolved) {
            return SUCCESS;
        }

        if (isOverTimeLimit) {
            return FAILURE;
        }
        return RUNNING;
    }

    protected boolean checkIfGoodToFinishSolving() {
        if (statusChangeListener == null) {
            return false;
        }
        statusChangeListener.updateIsSolved();
        return isSolved;
    }

    public void run() {
        /* before start solving attach ParameterStatusChangeListener to current runtimeInterface,
         * which will be removed when stopping solving */
        statusChangeListener = new StatusChangeListenerForCompletionChecking(runtimeInterface);
        runtimeInterface.addParameterStatusChangeListener(statusChangeListener);
        runtimeInterface.addParameterValueChangeListener(statusChangeListener);

        /* wait if paramteric changes are not solved and time limit is not reached */
        while (! isSolved && ! isOverTimeLimit) {
            synchronized(key) {
                try {
                    key.wait();
                } catch (InterruptedException e) {
                    /* normally the program flow never reaches here */
                    System.err.println(e);
                }
            }
        }

        String modelName = "";
        if (getRuntimeInterface().getDomeInterface().isInterfaceOfModel()) {
            modelName = getRuntimeInterface().getDomeInterface().getParentModel().getModelName();
        } else if (getRuntimeInterface().getDomeInterface().isInterfaceOfProject()) {
            modelName = getRuntimeInterface().getDomeInterface().getParentProject().getProjectName();
        }

        /* notify statusChangeListener that solving finished successfully or not */
        if (isSolved) {
            for (Iterator i = runtimeInterface.getParameterStatusChangeListeners().iterator(); i.hasNext(); ) {
            	ParameterStatusChangeListener customStatusChangeListener = null;
                customStatusChangeListener = (ParameterStatusChangeListener) i.next();
                if (! (customStatusChangeListener instanceof StatusChangeListenerForCompletionChecking)) {

                    /* wait until some status and value changes broadcasted */
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) { }

                    Debug.trace(Debug.ALL, "[DOME API] solving success of \"" + modelName + "\" notified to : " + customStatusChangeListener);
                    customStatusChangeListener.statusChanged(ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT);
                }
            }
            runtimeInterface.isFirstRun = false;
        }
        if (isOverTimeLimit) {
            for (Iterator i = runtimeInterface.getParameterStatusChangeListeners().iterator(); i.hasNext(); ) {
                ParameterStatusChangeListener customStatusChangeListener = (ParameterStatusChangeListener) i.next();
                if (! (customStatusChangeListener instanceof StatusChangeListenerForCompletionChecking)) {
                    Debug.trace(Debug.ALL, "[DOME API] solving failure of \"" + modelName + "\" notified to : " + customStatusChangeListener);
                    customStatusChangeListener.statusChanged(ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT);
                }
            }
        }

        /* remove all ParameterValueChangeListener & ParameterStatusChangeListener from current runtimeInterface */
        runtimeInterface.disableParameterStatusChangeListener();
        runtimeInterface.disableParameterValueChangeListener();
        runtimeInterface.resetChangedParameter();
    }

    /**
     * exit SolverStateTracker thread
     */
    public void expire() {
        synchronized(key) {
            isOverTimeLimit = true;
            /* time expired. stop solving */
            runtimeInterface.killSolving();
            key.notifyAll();
        }
    }

    public boolean isSolved() {
        return isSolved;
    }

    public boolean isOverTimeLimit() {
        return isOverTimeLimit;
    }

    /**
     * This method provides access to RuntimeInterface to which
     * this SolverStateTracker is associated.
     * This method usually used when we work with submitAndGo().
     * submitAndGo() returns a SolverStateTracker so that
     * other thread can keep track the state and result of solving.
     * The state of solving is retrieved using SolverStateTracker.getState().
     * The result of solving is retrieved by SolverStateTracker.getRuntimeInterface().getParameters()
     */
    public RuntimeInterface getRuntimeInterface() {
        return runtimeInterface;
    }

    /**
     * This class listens to the status changes while the server solves the changes.
     * When a status change is made in the server, this class will invoke isSolved() to check if solving is completed.
     * If solving is completed, it sets isAllInterfaceOutputParameterConsistent true
     * and wakes up SolverStateTracker thread, which will make the thread exits running.
     */
    class StatusChangeListenerForCompletionChecking implements ParameterStatusChangeListener, ParameterValueChangeListener {

        // create a set of containing status updated param IDs
        // create a set of containing value updated param IDs
//        Set valueUpdatedParamIds = null;
//        Set statusUpdatedParamIds = null;
        Set affectedParamIdsForValueUpdateChecking = null;
        Set affectedParamIdsForGreenStatusUpdateChecking = null;
        Set affectedParamIdsForWhiteStatusUpdateChecking = null;
//        int statusChangeCounter = 0;
//        int valueChangeCounter = 0;

//        long startTime;

        private RuntimeInterface runtimeInterface;

        public StatusChangeListenerForCompletionChecking(RuntimeInterface runtimeInterface) {
            this.runtimeInterface = runtimeInterface;
            this.affectedParamIdsForValueUpdateChecking = new HashSet(32);
            this.affectedParamIdsForGreenStatusUpdateChecking = new HashSet(32);
            this.affectedParamIdsForWhiteStatusUpdateChecking = new HashSet(32);

            Set affectedParams = runtimeInterface.getAffectedParameters();
            for (Iterator i = affectedParams.iterator(); i.hasNext(); ) {
                RuntimeParameter runtimeParam = (RuntimeParameter) i.next();
                affectedParamIdsForValueUpdateChecking.add(runtimeParam.getParamId());
                affectedParamIdsForGreenStatusUpdateChecking.add(runtimeParam.getParamId());
                affectedParamIdsForWhiteStatusUpdateChecking.add(runtimeParam.getParamId());
//                affectedParamIdsForValueUpdateChecking.add(runtimeParam.getParamName());
//                affectedParamIdsForGreenStatusUpdateChecking.add(runtimeParam.getParamName());
//                affectedParamIdsForWhiteStatusUpdateChecking.add(runtimeParam.getParamName());
            }

//            this.startTime = System.currentTimeMillis();
        }

        protected void updateIsSolved() {
            if (! isSolved && isSolved()) {
                isSolved = true;
            }
//            if (2000 < System.currentTimeMillis() - startTime) {
//                System.out.println("[UPDATE IS-SOLVED] " + isSolved + " for \"" + runtimeInterface.getParentSimulation().getSimulationName() + "\": waiting for value update of " + affectedParamIdsForValueUpdateChecking + " & status update of " + affectedParamIdsForGreenStatusUpdateChecking + " \n\t\t redundant: " + affectedParamIdsForWhiteStatusUpdateChecking);
//            }
            key.notifyAll();
        }

        public void statusChanged(ParameterStatusChangeEvent event) {
            synchronized (key) {
                if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(event.getNewStatus())) {
                    affectedParamIdsForGreenStatusUpdateChecking.remove(event.getInterfaceParameterClient().getId().getIdString());
//                    affectedParamIdsForGreenStatusUpdateChecking.remove(event.getInterfaceParameterClient().getName());
                } else if (Parameter.VALUE_STATUS_CONSISTENT.equals(event.getNewStatus())) {
                    affectedParamIdsForWhiteStatusUpdateChecking.remove(event.getInterfaceParameterClient().getId().getIdString());
//                    affectedParamIdsForWhiteStatusUpdateChecking.remove(event.getInterfaceParameterClient().getName());
                }

                updateIsSolved();
            }
        }

        public void valueChanged(ParameterValueChangeEvent event) {
            synchronized (key) {
                affectedParamIdsForValueUpdateChecking.remove(event.getInterfaceParameterClient().getId().getIdString());
//                affectedParamIdsForValueUpdateChecking.remove(event.getInterfaceParameterClient().getName());
                updateIsSolved();
            }
        }

        /**
         * returns if every parameter is consistent, which means parametric changes are solved.
         */
        public boolean isSolved() {
//            for (Iterator i = runtimeInterface.getParameters().iterator(); i.hasNext(); ) {
//                RuntimeParameter param = (RuntimeParameter) i.next();
//                /* if any parameter is not CONSISTENT, returns false. */
//                if (! Parameter.VALUE_STATUS_CONSISTENT.equals(param.getValueStatus())) {
//                    return false;
//                }
//            }

            // sometimes green status is not notified to all parameters, but white status is notified to all parameters. below code accounts for this behavior and checks both status changes.
            if (affectedParamIdsForValueUpdateChecking.isEmpty() && (affectedParamIdsForGreenStatusUpdateChecking.isEmpty() || affectedParamIdsForWhiteStatusUpdateChecking.isEmpty())) {
                return true;
            } else {
                return false;
            }
        }

        public RuntimeInterface getRuntimeInterface() {
            return runtimeInterface;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StatusChangeListenerForCompletionChecking)) return false;
            return this.getRuntimeInterface() == ((StatusChangeListenerForCompletionChecking) o).getRuntimeInterface();
        }

        public int hashCode() {
            return (runtimeInterface != null ? runtimeInterface.hashCode() : 0);
        }
    }
}
