package mit.cadlab.dome3.plugin.catalog.runtime;

import mit.cadlab.dome3.plugin.catalog.core.*;

import java.util.*;
import java.lang.Thread;
import java.lang.InterruptedException;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 1.
 */
public class EvaluationTracker extends Thread {
    private EvaluationContext context;
    private List greenList;
    private int greenIdx;
    private Map relTrackerSetMap;
    private int status;
    private long startTime, endTime;
    private Timer timer;
    private Timer forceFinishGraceTimer = null;

//    public String GREEN_LIST_KEY = "GREEN_LIST_KEY";
    GreenListKey GREEN_LIST_KEY = new GreenListKey();
    public String REL_TRACKERS_KEY = "REL_TRACKERS_KEY";
    //private Timer forceFinishTimer;
//    public static final int FORCE_FINISH_TICK_PERIOD = 4000;
    public static final int FORCE_FINISH_GRACE_PERIOD = 1000;
    public static final int GREEN_LIST_INIT_CAPACITY = 300;

    public static final int FINISH_CHECK_POLLING_PERIOD = 2000;
    public static final int FINISH_CHECK_GRACE_PERIOD = 4000;

    long lastCheckTime = 0;
//    public static final int GRACE_PERIOD_FOR_RUNNING_RELATIONS_TO_FINISH = 1000;

    public EvaluationTracker(EvaluationContext context) {
        this.context = context;
        relTrackerSetMap = new HashMap();
        this.status = CConstant.EVALUATION_READY_STATUS;
        this.setDaemon(false);
        this.greenList = new ArrayList(GREEN_LIST_INIT_CAPACITY);
        this.greenIdx = 0;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public synchronized void startPropagation(List greenListSeed) {
        synchronized (GREEN_LIST_KEY) {
            greenList.clear();
            greenList.addAll(greenListSeed);
        }
        start();
    }

    public synchronized void simulatePropagation(List greenListSeed) {
        this.greenList = greenListSeed;
        start();
    }

    /**
     * if the evaluation has finished, return the time taken until it finishes.
     * if the evaluation is running, return the time taken so far.
     * if the evaluation is ready but not running, return -1.
     */
    public long getEvaluationTime() {
        if (status == CConstant.EVALUATION_FINISHED_STATUS) {
            return endTime - startTime;
        } else if (status == CConstant.EVALUATION_RUNNING_STATUS) {
            return System.currentTimeMillis() - startTime;
        } else {
            return -1;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        String statusStr = "ready";
        if (status == CConstant.RELATION_RUNNING_STATUS) {
            statusStr = "running";
        } else if (status == CConstant.RELATION_FINISHED_STATUS) {
            statusStr = "finished";
        }

        sb.append("[EvaluationTracker: \n");
        sb.append("\tgreenList (idx=" + greenIdx + "): " + greenList + "\n");
        sb.append("\trelTrackers: " + relTrackerSetMap + "\n");
        sb.append("\tstatus: " + statusStr + "\n");
        sb.append("\tevalTime: " + getEvaluationTime() + "\n");
        sb.append("]");
        return sb.toString();
    }

    public void changeAllParametersToWhiteStatusAndNotifyStatusChanged() {
        Set paramSet = getContext().getNamingService().getParameters();
        for (Iterator i = paramSet.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            if (param.getStatus() != CConstant.WHITE_STATUS) {
                param.toWhiteStatus();
                context.notifyStatusChanged(CConstant.WHITE_STATUS, param.getQualifiedName());
            }
        }

        Set nodeSet = getContext().getNamingService().findAllMappingNodes();
        for (Iterator i = nodeSet.iterator(); i.hasNext();) {
            CMappingNode node = (CMappingNode) i.next();
            node.toWhiteStatus();
        }
    }

    public void changeToGreenStatusAndAddToGreenList(String qualifiedParamName) {
        synchronized (GREEN_LIST_KEY) {
            getContext().getNamingService().getParameter(qualifiedParamName).toGreenStatus();
            /* because of the buggy behavior of the remote relation, some parameter repeatedly notifies status and value changes.
             * it results in adding the parameter multiple times in the green list. to get around this problem,
             * we check if an added parameter is new to the greenList. */
            if (! greenList.contains(qualifiedParamName)) {
                greenList.add(qualifiedParamName);
                //speedup Clog.debug("a new green param '" + qualifiedParamName  + "' is added: " + greenList);
            }
            //GREEN_LIST_KEY.notifyAll();
        }
    }

    public void stopWaitingBecauseOfForceFinishedRelations() {
//        System.out.println("******************************************************************************************************");
//        System.out.println("*********************** stopWaitingBecauseOfForceFinishedRelations() in action ***********************");
//        System.out.println("******************************************************************************************************");
        CLog.info("FORCE-FINISH OF RELATION(S) USED");
        stopWaiting("force finished relations");
    }

    public void stopWaitingBecauseOfNewGreenParameters() {
        stopWaiting("new green parameters");
    }

    public void stopWaitingBecauseOfConfirmedGreenParameters() {
        stopWaiting("confirmed green parameters");
    }

    public void stopWaitingBecauseOfFinishCheckPolling() {
//        System.out.println("####################################################################################################");
//        System.out.println("######################## stopWaitingBecauseOfFinishCheckPolling() in action ########################");
//        System.out.println("####################################################################################################");
        stopWaiting("finish check polling");
    }

    /**this method is not used now. it does nothing */
    public void stopWaitingBecauseOfFinishingRelation(boolean isSuccess) {
    //        try {
    //            Thread.sleep(100);
    //        } catch (InterruptedException e) { }
    //
        if (isSuccess) {
            stopWaiting("a relation execution finishes successfully");
        } else {
            stopWaiting("a relation execution finishes with failure");
        }
    }

    public void stopWaitingBecauseOfMappingScriptExecution() {
        stopWaiting("a mapping script executed");
    }

    /** invoked when a relation finishes its evaluation or when some parameters are added to the green list */
    private void stopWaiting(String msg) {
        //speedup Clog.debug("[stop waiting] " + msg);
//        synchronized (GREEN_LIST_KEY) {
//            System.out.println("[trying to stop waiting] " + msg);
//            GREEN_LIST_KEY.notify();
            GREEN_LIST_KEY.notifyIfSomeoneIsWaiting();
//            System.out.println("[did stop waiting] " + msg);
//        }
    }

    /** check to see if a group of param is already in the green list.
     * now this method is used to check a set of parameters to be determined by a relation
     * is all determined & added to the green list */
    protected boolean greenListContains(Set qualifiedParamNames) {
        synchronized (GREEN_LIST_KEY) {
            return greenList.containsAll(qualifiedParamNames);
        }
    }

    public List getGreenList() {
        return greenList;
    }

    public EvaluationContext getContext() {
        return context;
    }

    /* this method is invoked by run() method of RelationTracker. GREEN_LIST_KEY is synchronized so that only one thread */
    public void registerRelationTracker(RelationTracker relTracker) {
        synchronized (REL_TRACKERS_KEY) {
            Set relTrackerSet = (Set) relTrackerSetMap.get(relTracker.getRelAlias());
            if (relTrackerSet == null) {
                relTrackerSet = new HashSet();
                relTrackerSetMap.put(relTracker.getRelAlias(), relTrackerSet);
            }
            relTrackerSet.add(relTracker);
        }
    }

    /* this method is invoked by run() method of EvaluationTracker. GREEN_LIST_KEY is synchronized so that only one thread */
    public void clearRelationTrackerSetMap() {
        synchronized (REL_TRACKERS_KEY) {
            relTrackerSetMap.clear();
        }
    }

    /* find a RelationTracker instances with relAlias */
    public Set getRelationTrackerSet(String relAlias) {
        return (Set) relTrackerSetMap.get(relAlias);
    }

    /* get a Map of (a String relAlias) -> (a Set of RelationTracker instances) */
    public Map getRelationTrackerSetMap() {
        return relTrackerSetMap;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void iterateGreenListToPropagateStatus() {
        CNamingService namingService = context.getNamingService();

//        StringBuffer sb = new StringBuffer();
//        for (int i = greenIdx; i < greenList.size(); i++) {
//            sb.append(greenList.get(i));
//            if (i != greenList.size() - 1) {
//                sb.append(", ");
//            }
//        }
//        if (sb.length() == 0) {
//            sb.append("<none>");
//        }
        //speedup Clog.debug("[iterate & propagate-1] seed params for this iteration=" + sb.toString()  + " (idx=" + greenIdx + " / size=" + greenList.size() + ")");

        /* we look for executable relations */
        Set executableRelationSet = new HashSet();
        Map paramNamesToBeDeterminedMap = new HashMap();

        while(greenIdx < greenList.size()) {
            String greenParamName = (String) greenList.get(greenIdx);
            Set mappingNodesToGreenParam = namingService.findMappingNodes(greenParamName);

            //speedup Clog.debug("[iterate & propagate-2] a green param=" + greenParamName);

            for (Iterator j = mappingNodesToGreenParam.iterator(); j.hasNext(); ) {
                CMappingNode mappingNodeToGreenParam = (CMappingNode) j.next();

                /* if any node is mapped to curGreenParam, it should turn GREEN */
                mappingNodeToGreenParam.toGreenStatus();

                /* if a mapping is executable, the mapping is executed
                   and then its output parameter name be added to the end of the green list */
                CMapping mapping = mappingNodeToGreenParam.getMapping();
                if (mappingNodeToGreenParam.isInputNode() && mapping.isExecutable() && ! mapping.getOutputNode().isConsistent()) {
                    String mappedParamName = mapping.getOutputNode().getMappedParameterName();

                    context.executeMappingScript(mapping);

                    CParameter outputParam = namingService.getParameter(mappedParamName);
                    mapping.getOutputNode().toGreenStatus(); // since we know any output node's status should be the same as its mapped output param, we do not use this status in solving calculation

                    /* turn green the associated parameter, and put it at the end of the green list if the param is CRelationInputParameter. we don't have to */
                    outputParam.toGreenStatus();

                    context.notifyStatusChanged(CConstant.GREEN_STATUS, outputParam.getQualifiedName());

                    greenList.add(mappedParamName);
                }
            }
            /* move to the next param name in green list */
            greenIdx++;

            /* if there is no more in green list find all service relations that are executable */
            Set relations = namingService.getRelations();
            for (Iterator j = relations.iterator(); j.hasNext(); ) {
                CRelation relation = (CRelation) j.next();

                Set determinableSet = relation.getDeterminableOutputParametersNames();
                if (determinableSet.size() > 0) {
                    Set curDeterminableSet = (Set) paramNamesToBeDeterminedMap.get(relation.getRelAlias());
                    if (curDeterminableSet == null) {
                        curDeterminableSet = new HashSet();
                        paramNamesToBeDeterminedMap.put(relation.getRelAlias(), curDeterminableSet);
                    }
                    curDeterminableSet.addAll(determinableSet);

                    executableRelationSet.add(relation);
                }
            }
        }

        if (executableRelationSet.size() > 0) {
//            sb = new StringBuffer();
//            for (Iterator r = executableRelationSet.iterator(); r.hasNext();) {
//                CRelation rel = (CRelation) r.next();
//                sb.append(rel.getRelAlias());
//                if (r.hasNext()) {
//                    sb.append(", ");
//                }
//            }
//            if (sb.length() == 0) {
//                sb.append("<none>");
//            }
            //speedup Clog.debug("[iterate & propagate-3] executable relations found=" + sb);

            for (Iterator j = executableRelationSet.iterator(); j.hasNext(); ) {
                CRelation relation = (CRelation) j.next();
                Set paramNamesToBeDetermined = (Set) paramNamesToBeDeterminedMap.get(relation.getRelAlias());
                //speedup Clog.debug("[iterate & propagate-4] relAlias=" + relation.getRelAlias() + ", paramNamesToBeDetermined=" + paramNamesToBeDetermined);

                /* check if paramNamesToBeDetermined is empty */
                if (paramNamesToBeDetermined.size() == 0) {
                    continue;
                }

                /* check if any other RelationTracker is already running to determine the same or bigger parameter set
                 * than the paramNamesToBeDetermined set that we just created. If so, we don't have to start a new RelationTracker.
                 * We just need to wait until the existing RelationTracker finishes. */
                if (checkDuplicatedRelationTracker(relation.getRelAlias(), paramNamesToBeDetermined)) {
                    continue;
                }

                /* if paramNamesToBeDetermined is not empty and no other RelationTracker is trying to determine parameters in the paramNamesToBeDetermined set, start a new RelationTracker for the set. */
                Set relTrackerSet = (Set) relTrackerSetMap.get(relation.getRelAlias());
                int trackerId = 0;
                if (relTrackerSet != null) {
                    trackerId = relTrackerSet.size();
                }
                RelationTracker relationTracker = new RelationTracker(relation, trackerId, paramNamesToBeDetermined, this, context.isValidationMode());
                relationTracker.startRelationTracker();
                //speedup Clog.debug("[iterate & propagate-5] starting a new thread: relTracker=" + relationTracker);
            }
        }
    }

    /** returns true if there exist a running instance of RelationTracker that is going to determine parameters in the paramNamesToBeDetermined set */
    private boolean checkDuplicatedRelationTracker(String relAlias, Set paramNamesToBeDetermined) {
        Set relTrackerSet = (Set) relTrackerSetMap.get(relAlias);
        if (relTrackerSet != null) {
            for (Iterator k = relTrackerSet.iterator(); k.hasNext();) {
                RelationTracker relTracker = (RelationTracker) k.next();
                if (relTracker.getParameterNamesToBeDetermined().containsAll(paramNamesToBeDetermined)) {
                    //speedup Clog.debug("[iterate & propagate-5] no need to start a new thread. skip spawning a new thread for relAlias=" + relAlias + " because new paramToDetermine=" + paramNamesToBeDetermined + " is smaller than existing paramToDetermine=" + relTracker.getParameterNamesToBeDetermined());
                    return true; // found duplicated one
                }
            }
        }
        return false; // no duplicated one
    }

    /** returns true if evaluation should exit. returns false if the evaluation thread should keep running. */
    public boolean isExitConditionSatisified() {
        // commenting hasAnyRunningRelations() checking will make the solving thread exits immediately when all interface output params are made consistent
        if (! hasAnyRunningRelations()) {
            /* tick timer exists to force-finish completed relations. now that it is no use, it is canceled */
//            if (forceFinishTimer != null) {
//                forceFinishTimer.cancel();
//            }

            if (isAllInterfaceOutputParameterConsistent()) {
                /* check if it is good to finish the current evaluation */
                //speedup Clog.debug("good to finish this evaluation thread. all interface output parameters are consistent and no remaining relation is in the running status. The following RelationTracker were used: " + relTrackerSetMap);
                /* notify listeners that an evaluation ended successfully */
                /* set all parameters to white */
                changeAllParametersToWhiteStatusAndNotifyStatusChanged();
                endTime = System.currentTimeMillis();
                context.notifyEvaluationEnded(true);

//                if (hasAnyRunningRelations()) {
//                    //speedup Clog.debug("[warning when exiting] there exist running relation(s)");
//                }
                return true;
            } else {
                /* notify listeners that an evaluation ended unsuccessfully */
                endTime = System.currentTimeMillis();
                context.notifyEvaluationEnded(false);
                return true;
            }
        } else {
            if (isAllInterfaceOutputParameterConsistent()) {
//                for (Iterator i = relTrackerSetMap.values().iterator(); i.hasNext(); ) {
//                    Set relTrackerSet = (Set) i.next();
//                    for (Iterator j = relTrackerSet.iterator(); j.hasNext();) {
//                        RelationTracker relTracker = (RelationTracker) j.next();
//                        if (relTracker.getStatus() != CConstant.RELATION_FINISHED_STATUS) {
//                            System.out.println("YOU SEE THIS RELATION IS RUNNING: relTracker=" + relTracker);
//                        }
//                    }
//                }
//                System.out.println("WOW IT IS ALL CONSISTENT!!!");
                if (forceFinishGraceTimer == null) {
                    forceFinishGraceTimer = new Timer(true);
                    forceFinishGraceTimer.schedule(new TimerTask() {
                        public void run() {
                            synchronized (GREEN_LIST_KEY) {
                                boolean forceFinished = tryForceFinishingRelations();
                                if (forceFinished) {
//                                    CLog.info("###### force finish used ######");
                                    stopWaitingBecauseOfForceFinishedRelations();
                                }
                            }
                        }
                    }, FORCE_FINISH_GRACE_PERIOD);
                }

            }
//            else {
//                System.out.println("Yes, SOME ARE NOT CONSISTENT!!!");
//            }

//            if (isAllInterfaceOutputParameterConsistent()) {
//                /* check if it is good to finish the current evaluation */
//                //speedup Clog.debug("good to finish this evaluation thread. all interface output parameters are consistent, but some remaining relation(s) are in the running status. The following RelationTracker were used: " + relTrackerSetMap);
//                /* notify listeners that an evaluation ended successfully */
//                /* set all parameters to white */
//
//                System.out.println("giving grace period");
//                try {
//                    synchronized (GREEN_LIST_KEY) {
//                        GREEN_LIST_KEY.wait(GRACE_PERIOD_FOR_RUNNING_RELATIONS_TO_FINISH);
//                    }
//                } catch (Exception e) { e.printStackTrace(); }
//
//                if (hasAnyRunningRelations()) {
//                    System.out.println("killing running relations");
//                    //speedup Clog.debug("[warning when exiting] there exist running relation(s)");
//                    this.killAllRunningRemoteRelations();
//                }
//
//                changeAllParametersToWhiteStatusAndNotifyStatusChanged();
//                endTime = System.currentTimeMillis();
//                context.notifyEvaluationEnded(true);
//
//                return true;
//            }
        }

        return false;
    }

    public void waitUntilGreenParamIsAddedOrRelationFinishes() {
//        try {
            //speedup Clog.debug("[wait-1] start waiting: last=" + greenList.get(greenList.size() - 1) + " (idx=" + greenIdx + " / size=" + greenList.size() + ")");
//            System.out.println("start waiting!!!!");
//            GREEN_LIST_KEY.wait();

            GREEN_LIST_KEY.waitIfNotWaiting();
//            System.out.println("resumed!!!!");
//            forceFinishTimer.cancel();
            //speedup Clog.debug("[wait-2] quit waiting");
//        } catch (InterruptedException e) {
//            /* normally the program flow never reaches here */
//            CLog.log(e.getMessage());
//            context.notifyEvaluationEnded(false);
//        }
    }

    public void stopTimer() {
        if (timer != null) {
            try {
                timer.cancel();
            } catch (Exception e) { }
        }
    }

    public void run() {
        startTime = System.currentTimeMillis();
        lastCheckTime = System.currentTimeMillis();
        clearRelationTrackerSetMap();

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                synchronized (GREEN_LIST_KEY) {
                    if (System.currentTimeMillis() - lastCheckTime >= FINISH_CHECK_GRACE_PERIOD) {
                        lastCheckTime = System.currentTimeMillis();
                        stopWaitingBecauseOfFinishCheckPolling();
                    }
                }
            }
        }, FINISH_CHECK_POLLING_PERIOD, FINISH_CHECK_POLLING_PERIOD);

        greenIdx = 0;
        synchronized (GREEN_LIST_KEY) {
            iterateGreenListToPropagateStatus();
            while (! isExitConditionSatisified()) {
//                forceFinishTimer = new Timer(true);
//                forceFinishTimer.schedule(new TimerTask() {
//                    public void run() {
////                        todo: find out why below code necessary --> we don't need it anymore
////                        System.out.println("checking any possibility of forced finish.............");
////                        boolean forceFinishedRelationsExist = tryForceFinishingRelations();
////                        if (forceFinishedRelationsExist) {
////                            System.out.println("found some forced finish.............");
////                            stopWaitingBecauseOfForceFinishedRelations();
////                        } else {
////                            System.out.println("didn't find any forced finish.............");
////                        }
//                    }
//                }, FORCE_FINISH_TICK_PERIOD, FORCE_FINISH_TICK_PERIOD);

                waitUntilGreenParamIsAddedOrRelationFinishes();
                iterateGreenListToPropagateStatus();
                lastCheckTime = System.currentTimeMillis();
                //slowDown();
            }
        }

        stopTimer();
    }

    private void slowDown() {
        try {
            synchronized (GREEN_LIST_KEY) {
                CLog.log("[SLOW DOWN] --------------------------------------------- [SLOW DOWN]");
                GREEN_LIST_KEY.wait(1000);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean isAllInterfaceOutputParameterConsistent() {
        Set ioParams = context.getNamingService().getInterfaceOutputParameters();
        for (Iterator i = ioParams.iterator(); i.hasNext();) {
            CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) i.next();
            if (! ioParam.isConsistent()) {
                //speedup Clog.debug("[check exit condition-2] not all interface output param consistent: " + ioParam.getQualifiedName() + " is not consistent");
                return false;
            }
        }
        //speedup Clog.debug("[check exit condition-2] all interface output param are consistent");
        return true;
    }

    /** returns if evaluation tracker is running any relation */
    public boolean hasAnyRunningRelations() {
        for (Iterator i = relTrackerSetMap.values().iterator(); i.hasNext(); ) {
            Set relTrackerSet = (Set) i.next();
            for (Iterator j = relTrackerSet.iterator(); j.hasNext();) {
                RelationTracker relTracker = (RelationTracker) j.next();
                if (relTracker.getStatus() != CConstant.RELATION_FINISHED_STATUS) {
                    //speedup Clog.debug("[check exit condition-1] one or more than one relations are running: relTracker=" + relTracker);
                    return true;
                }
            }
        }
        //speedup Clog.debug("[check exit condition-1] no running relation");
        return false;
    }

    /** when some of the relations have completed its execution and thus have all consistent output parameters,
     * mark the relation as finished. this extra way of finishing a completed relation is necessary because
     * we found that sometimes a remote relation does not give relation-finish-event appropriately.
     * therefore, we added a timer that polls if there are such problematic relations and that also forces the relation to finish.
     * returns true if one or more than one relations have been forced to finish. */
    public boolean tryForceFinishingRelations() {
        boolean forceFinishedRelationsExist = false;
        for (Iterator i = relTrackerSetMap.values().iterator(); i.hasNext(); ) {
            Set relTrackerSet = (Set) i.next();
            for (Iterator j = relTrackerSet.iterator(); j.hasNext();) {
                RelationTracker relTracker = (RelationTracker) j.next();
                if (relTracker.getStatus() != CConstant.RELATION_FINISHED_STATUS) {
                    //speedup Clog.debug("[force finish relation] probing relation starts: relAlias = " + relTracker.getRelAlias());
                    boolean isOkayToFinish = true;
                    for (Iterator k = relTracker.getParameterNamesToBeDetermined().iterator(); k.hasNext(); ) {
                        String paramName = (String) k.next();
                        CParameter param = context.getNamingService().getParameter(relTracker.getRelAlias() + "." + paramName);
                        if (param.getStatus() != CConstant.GREEN_STATUS &&  param.getStatus() != CConstant.WHITE_STATUS) {
                            //speedup Clog.debug("[force finish relation] relation '" + relTracker.getRelAlias() + "' has more than one non-consistent (RED) parameters such as: " + param.getQualifiedName());
                            isOkayToFinish = false;
                            break;
                        }
                    }
                    /* if a relation is found to be force finisheable, finish it. */
                    if (isOkayToFinish) {
                        //speedup Clog.debug("[force finish relation] relation '" + relTracker.getRelAlias() + "' has all consistent output parameters. now force finish this relation");

                        context.notifyRelationEnded(relTracker, true);
                        forceFinishedRelationsExist = true;
//                        System.out.println("NOTE!!! relTracker " + relTracker + " is forced to quit");

//                        /* do clean-up */
//                        if (relTracker.getRemoteRelationListener() != null) {
//                            relTracker.getRemoteRelationListener().getRuntimeInterface().killSolving();
//                        }
                    } else {
                        //speedup Clog.debug("[force finish relation] relation '" + relTracker.getRelAlias() + "' is not force-finishable because of its non-consistent output parameters.");
                    }
                    //speedup Clog.debug("[force finish relation] probing relation ends: relAlias = " + relTracker.getRelAlias());
                }
            }
        }
        return forceFinishedRelationsExist;
    }

    /** kill all running remote relations */
    public void killAllRunningRemoteRelations() {
        for (Iterator i = relTrackerSetMap.values().iterator(); i.hasNext(); ) {
            Set relTrackerSet = (Set) i.next();
            for (Iterator j = relTrackerSet.iterator(); j.hasNext();) {
                RelationTracker relTracker = (RelationTracker) j.next();
                if (relTracker.getStatus() != CConstant.RELATION_FINISHED_STATUS && context.getNamingService().getRelation(relTracker.getRelAlias()) instanceof CRemoteRelation) {
                    //speedup Clog.debug("[clean up still-running relation] " + relTracker);
                    if (relTracker.getRemoteRelationListener() != null) {
                        relTracker.getRemoteRelationListener().getRuntimeInterface().killSolving();
                    }
                }
            }
        }
    }
}

class GreenListKey {
    boolean isWaiting = false;
    synchronized void waitIfNotWaiting() {
        if (! isWaiting) {
            try {
                isWaiting = true;
//                System.out.println("now started to wait");
                this.wait();
                isWaiting = false;
            } catch (InterruptedException e) { }
        }
//        else {
//            System.out.println("i was already waiting");
//        }
    }

    synchronized void notifyIfSomeoneIsWaiting() {
        if (isWaiting) {
//            System.out.println("start doing notification");
            this.notify();
//            System.out.println("done doing notification");
        }
//        else {
//            System.out.println("none waiting - do not notify");
//        }
    }
}