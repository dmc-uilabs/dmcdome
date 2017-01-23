package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.DomeClientApplication;

import java.io.File;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 18.
 * //todo: April 1, 2005, RuntimeInterface does not work with a interface of DomeIModel.
 *         DomeIModel problem does not support
 *         It's createRuntimeInterface() returns a RuntimeInterface okay.
 *         But once we submit it, it doesn't solve chnages.
 *         The cause of this problem is
 *         that when it createRuntimeInterface(), server cannot find a interface corresponding to it.
 *         so server have nothing to do with submitted changes.
 */
public class RuntimeInterface {

    private ModelInterfaceRuntimeClient interfaceClient;
    private Hashtable changedParameters = new Hashtable();
    private Set changedRuntimeParams = new HashSet();
    private long executionTimeLimit = 0;
    private DomeConnection domeConn;
    private ClientPlayspaceRuntime playspaceClient;
    private DomeInterface domeInterface;
//    protected static final long FINISH_CHECK_POLLING_PERIOD = 330;

    private Set statusChangeListeners = new HashSet();
    private Set valueChangeListeners = new HashSet();


    /* this backup listers are used by restoreBackupChangeListener(), clearValueAndStatusChangeListener() */
    private Set backupStatusChangeListeners = new HashSet();
    private Set backupValueChangeListeners = new HashSet();

    /**
     * download folder for this RuntimeInterface instance. it is first initialized with DEFAULT_DOWNLOAD_FOLDER,
     * and can be later changed by setDownloadFolder(newFolderStr)
     */
    private File downloadFolder;

    /**
     * Files will be downloaded to DEFAULT_DOWNLOAD_FOLDER if users do not specify the download folder
     * by invoking runtimeInterface.setDownloadFolder()
     * Initial DEFAULT_DOWNLOAD_FOLDER is the root directory.
     */
    public static String DEFAULT_DOWNLOAD_FOLDER = "/";

    /** for the first execution of RuntimeInterface, all output parameters are assumed to be INCONSISTENT. isFirstRun variable will be used to to impose this consideration. */
    protected boolean isFirstRun = true;

    /**
     * construct a new RuntimeInterface instance in the transient playspace
     * if the RuntimeInterface instance should be in a specific playspace, use another constructor having RuntimePlayspace as a parameter.
     * @param domeInterface
     * @param domeConn
     */
    public RuntimeInterface(DomeInterface domeInterface, DomeConnection domeConn) {
        this.domeInterface = domeInterface;
        this.domeConn = domeConn;
        this.isFirstRun = true;

        /* let DomeConnection know a new RuntimeInterface instance is open, which should be closed when dome connection closes. */
        domeConn.addToRuntimeInterfaceList(this);

        boolean isProjectResource = false;

        CompoundId interfaceCompId = null;
        if (domeInterface.isInterfaceOfModel()) {
            interfaceCompId  = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentModel().getModelId() + "\nRUN.");
        } else if(domeInterface.isInterfaceOfIModel()) {
            /* todo: this CompoundId does not work. server cannot start an interface corresponding to the Id */
            interfaceCompId = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentIModel().getModelId() + "\nRUN.");
        } else if(domeInterface.isInterfaceOfProject()) {
            //isProjectResource = true;
            interfaceCompId = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentProject().getProjectId() + "\nRUN.");
        }

        DomeRuntimeClient drClient = domeConn.getDomeRuntimeClient();
        ServerConnection serverConn = domeConn.getServerConnection();

        /* create transient playspace and create interface */
        playspaceClient = drClient.createTransientPlayspace(domeConn.getServerConnection());
        interfaceCompId.setPlayspaceStaticId(playspaceClient.getCompoundId().getPlayspaceStaticId());
        interfaceCompId.setPlayspaceRuntimeId(playspaceClient.getCompoundId().getPlayspaceRuntimeId());
        interfaceClient = drClient.createInterface(interfaceCompId, playspaceClient, serverConn, isProjectResource);


        Debug.trace(Debug.ALL, "start creating interface : is loaded = " + interfaceClient.isLoaded());
        InterfaceLoadingTracker loadingTracker = new InterfaceLoadingTracker(interfaceClient);
        loadingTracker.start();

        try {
            loadingTracker.join();
        } catch (InterruptedException e) { System.err.println(e); }


        setDownloadFolder(DEFAULT_DOWNLOAD_FOLDER);

        Debug.trace(Debug.ALL, "complete creating interface : is loaded = " + interfaceClient.isLoaded());
    }

    /**
     * contruct a new RuntimeInterface in the give RuntimePlayspae.
     * @param domeInterface
     * @param runtimePlayspace
     * @param domeConn
     */
    public RuntimeInterface(DomeInterface domeInterface, RuntimePlayspace runtimePlayspace, DomeConnection domeConn) {
        this.domeInterface = domeInterface;
        this.domeConn = domeConn;
        this.isFirstRun = true;

        /* let DomeConnection know a new RuntimeInterface instance is open, which should be closed when dome connection closes. */
        domeConn.addToRuntimeInterfaceList(this);

        boolean isProjectResource = false;

        CompoundId interfaceCompId = null;
        if (domeInterface.isInterfaceOfModel()) {
            interfaceCompId  = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentModel().getModelId() + "\nRUN.");
        } else if(domeInterface.isInterfaceOfIModel()) {
            interfaceCompId = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentIModel().getModelId() + "\nRUN.");
        } else if(domeInterface.isInterfaceOfProject()) {
            interfaceCompId = new CompoundId("CID.STAT..IF" + domeInterface.getInterfaceId() + ".ML" + domeInterface.getParentProject().getProjectId() + "\nRUN.");
        }

        if (domeInterface.isInterfaceOfModel() && domeInterface.getParentModel().isProjectResource()) {
            isProjectResource = true;
        } else if (domeInterface.isInterfaceOfProject() && domeInterface.getParentProject().isProjectResource()) {
            isProjectResource = true;
        }

        DomeRuntimeClient drClient = domeConn.getDomeRuntimeClient();
        ServerConnection serverConn = domeConn.getServerConnection();

        /* initialize playspaceClient with given runtime playspace and create interface */
        playspaceClient = runtimePlayspace.getClientPlayspaceRuntime();
        interfaceCompId.setPlayspaceStaticId(playspaceClient.getCompoundId().getPlayspaceStaticId());
        interfaceCompId.setPlayspaceRuntimeId(playspaceClient.getCompoundId().getPlayspaceRuntimeId());
        interfaceClient = drClient.createInterface(interfaceCompId, playspaceClient, serverConn, isProjectResource);

        Debug.trace(Debug.ALL, "start creating interface : is loaded = " + interfaceClient.isLoaded());
        InterfaceLoadingTracker loadingTracker = new InterfaceLoadingTracker(interfaceClient);
        loadingTracker.start();
        try {
            loadingTracker.join();
        } catch (InterruptedException e) { System.err.println(e); }
        Debug.trace(Debug.ALL, "complete creating interface : is loaded = " + interfaceClient.isLoaded());
    }

    public List getIndependentParameters() {
        List ret = new ArrayList();
        List independentItems = interfaceClient.getItems(CausalityStatus.INDEPENDENT);
        for (int i = 0; i < independentItems.size(); i++) {
            InterfaceParameterClient param = (InterfaceParameterClient) independentItems.get(i);
            ret.add(new RuntimeParameter("INDEPENDENT", this, param));
        }
        return ret;
    }

    public List getIndeterminateParameters() {
        List ret = new ArrayList();
        List indeterminateItems = interfaceClient.getItems(CausalityStatus.INDETERMINATE);
        for (int i = 0; i < indeterminateItems.size(); i++) {
            InterfaceParameterClient param = (InterfaceParameterClient) indeterminateItems.get(i);
            ret.add(new RuntimeParameter("INDETERMINATE", this, param));
        }
        return ret;
    }

    public List getResultParameters() {
        List ret = new ArrayList();
        List resultItems = interfaceClient.getItems(CausalityStatus.RESULT);
        for (int i = 0; i < resultItems.size(); i++) {
            InterfaceParameterClient param = (InterfaceParameterClient) resultItems.get(i);
            ret.add(new RuntimeParameter("RESULT", this, param));
        }
        return ret;
    }

    public List getIntermediateParameters() {
        List ret = new ArrayList();
        List intermediateItems = interfaceClient.getItems(CausalityStatus.INTERMEDIATE);
        for (int i = 0; i < intermediateItems.size(); i++) {
            InterfaceParameterClient param = (InterfaceParameterClient) intermediateItems.get(i);
            ret.add(new RuntimeParameter("INTERMEDIATE", this, param));
        }
        return ret;
    }

    /** query dependency between two runtime parameters. returns if p1 drives p2 */
    public boolean getDependency(RuntimeParameter p1, RuntimeParameter p2) {
        return interfaceClient.getInterfaceGraph().areReachableNodes(p1.getInterfaceParameterClient(), p2.getInterfaceParameterClient());
    }

    public List getAffectedParams(RuntimeParameter p) {
        DirectedGraph graph =  interfaceClient.getInterfaceGraph();
        InterfaceParameterClient iclient = p.getInterfaceParameterClient();
        return graph.getAffectedNodes(iclient);
    }

    /** returns a list of RuntimeParameter instances */
    public List getAffectedParameters(RuntimeParameter p) {
        List ret = new ArrayList();
        DirectedGraph graph =  interfaceClient.getInterfaceGraph();
        InterfaceParameterClient iclient = p.getInterfaceParameterClient();
        List paramClientList = graph.getAffectedNodes(iclient);
        for (int i = 0; i < paramClientList.size(); i++) {
            if (! (paramClientList.get(i) instanceof InterfaceParameterClient)) {
                /* In some erroneous cases such as mismatch found between DML and DMI file,
                 * the list returned by graph.getAffectedNodes(iclient) can contain String. Such an element will be ignored. */
                continue;
            }
            InterfaceParameterClient paramClient = (InterfaceParameterClient) paramClientList.get(i);
            String paramId = paramClient.getId().getIdString();
            ret.add(getParameterById(paramId));
        }
        return ret;
    }

    public List getParameters() {
        List ret = new ArrayList();
        ret.addAll(getIndependentParameters());
        ret.addAll(getIndeterminateParameters());
        ret.addAll(getResultParameters());
        ret.addAll(getIntermediateParameters());
        return ret;
    }

    public RuntimeParameter getParameterByName(String paramName) {
        List paramList = getParameters();
        for (int i = 0; i < paramList.size(); i++) {
            RuntimeParameter param = (RuntimeParameter) paramList.get(i);
            if (paramName.equals(param.getParamName())) {
                return param;
            }
        }
        return null;
    }

    public RuntimeParameter getParameterById(String paramId) {
        List paramList = getParameters();
        for (int i = 0; i < paramList.size(); i++) {
            RuntimeParameter param = (RuntimeParameter) paramList.get(i);
            if (paramId.equals(param.getParamId())) {
                return param;
            }
        }
        return null;
    }

    /**
     * newValue accepts datatypes such as Integer, Double, Real, String, List(=Vector), Matrix(?), Preference(?)
     * @param changedParam
     * @param newValue
     */
    public void addChangedParameter(RuntimeParameter changedParam, Object newValue) {
        InterfaceParameterClient itfParamClient = changedParam.getInterfaceParameterClient();
        Object newValueVector = Vectors.create(newValue);
        itfParamClient.setValueStatus(Parameter.VALUE_STATUS_STALE);

        CompoundId interfaceRuntimeId = interfaceClient.getRuntimeId();
        CompoundId objectCompoundId = new CompoundId(interfaceRuntimeId);
        objectCompoundId.setDataObjectStaticId(itfParamClient.getId().getIdString());
        objectCompoundId.setDataObjectRuntimeId(itfParamClient.getId().getIdString());

        changedParameters.put(objectCompoundId.toString(), newValueVector);
        changedRuntimeParams.add(changedParam);
    }

    /** add all input parameters into changedRuntimeParams Set
     * This should be called before getAffectedParameters() is called
     * because this treatment is eventually to make all output parameters considered
     * affected for the first run of RuntimeInterface
     */
    private void assumeAllInputParameterChangedForTheFirstRun() {
        changedRuntimeParams.addAll(getIndependentParameters());
    }

    /**
     * returns Hashtable that contains changed parameters
     */
    private Hashtable getChangedParameterTable() {
        if (changedParameters.isEmpty()) {
            changedParameters.put(interfaceClient.getRuntimeId().toString(), Collections.EMPTY_LIST);
        }
        return changedParameters;
    }

    public Set getChangedParameters() {
        return changedRuntimeParams;
    }

    public Set getAffectedParameters() {
        Set affectedParams = new HashSet();
        for (Iterator i = changedRuntimeParams.iterator(); i.hasNext(); ) {
            RuntimeParameter runtimeParam = (RuntimeParameter) i.next();
            affectedParams.addAll(this.getAffectedParameters(runtimeParam));
        }
        return affectedParams;
    }

    /**
     * returns DomeInterface associated with the RuntimeInterface
     */
    public DomeInterface getDomeInterface() {
        return domeInterface;
    }

    /**
     * returns DomeSimulation (one of DomeModel, DomeProject, DomeIModel and DomeAnalysisModel) associated with the RuntimeInterface
     */
    public DomeSimulation getParentSimulation() {
        return domeInterface.getParentSimulation();
    }

    /**
     * returns runtime id of the RuntimeInterface instance
     * all static information is available through getDomeInterface();
     */
    public String getRuntimeId() {
        return interfaceClient.getId().getIdString();
    }

    /**
     * ParameterValueChangeListener instance passed to this method will be attached to every parameter in this interface
     * i.e. given ParameterValueChangeListener instance will be listening to all parametric changes in this interface.
     * Note. There is a method with the same name as addParameterValueChangeListener() in RuntimeParameter class.
     * @param valueChangeListener ParameterValueChangeListener to be attached to this interface
     */
    public void addParameterValueChangeListener(ParameterValueChangeListener valueChangeListener) {
        /* we don't have to backup StatusChangeListenerForCompletionChecking because it will be added each time submit() is called */
        if (! (valueChangeListener instanceof SolverStateTracker.StatusChangeListenerForCompletionChecking)) {
            backupValueChangeListeners.add(valueChangeListener);
        }

        valueChangeListeners.add(valueChangeListener);

        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            ((RuntimeParameter) i.next()).addParameterValueChangeListener(valueChangeListener);
        }
    }

    /**
     * ParameterStatusChangeListener instance passed to this method will be attached to every parameter in this interface
     * i.e. given ParameterValueChangeListener instance will be listening to all parametric changes in this interface.
     * Note. There is a method with the same name as addParameterStatusChangeListener() in RuntimeParameter class.
     * @param statusChangeListener ParameterStatusChangeListener to be attached to this interface
     */
    public void addParameterStatusChangeListener(ParameterStatusChangeListener statusChangeListener) {
        /* we don't have to backup StatusChangeListenerForCompletionChecking because it will be added each time submit() is called */
        if (! (statusChangeListener instanceof SolverStateTracker.StatusChangeListenerForCompletionChecking)) {
            backupStatusChangeListeners.add(statusChangeListener);
        }
        statusChangeListeners.add(statusChangeListener);

        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            ((RuntimeParameter) i.next()).addParameterStatusChangeListener(statusChangeListener);
        }
    }

    /**
     * returns a set of ParameterStatusChangeListeners, which have been added to this RuntimeInterface
     */
    public Set getParameterStatusChangeListeners() {
        if (statusChangeListeners.isEmpty() && ! backupStatusChangeListeners.isEmpty()) {
            return backupStatusChangeListeners;
        }
        return statusChangeListeners;
    }

    /**
     * returns a set of ParameterStatusChangeListeners, which have been added to this RuntimeInterface
     */
    public Set getParameterValueChangeListeners() {
        if (valueChangeListeners.isEmpty() && ! backupValueChangeListeners.isEmpty()) {
            return backupValueChangeListeners;
        }
        return valueChangeListeners;
    }

    /**
     * remove given ParameterValueChangeListener from all runtime parameter
     */
    public void removeParameterValueChangeListener(ParameterValueChangeListener valueChangeListener) {
        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            ((RuntimeParameter) i.next()).removeParameterValueChangeListener(valueChangeListener);
        }
        statusChangeListeners.remove(valueChangeListener);
        backupStatusChangeListeners.remove(valueChangeListener);
    }

    /**
     * remove given ParameterStatusChangeListener from all runtime parameter
     */
    public void removeParameterStatusChangeListener(ParameterStatusChangeListener statusChangeListener) {
        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            ((RuntimeParameter) i.next()).removeParameterStatusChangeListener(statusChangeListener);
        }
        statusChangeListeners.remove(statusChangeListener);
        backupValueChangeListeners.remove(statusChangeListener);
    }

    /**
     * restore all listeners in 'backup change listener'
     */
    public void restoreBackupChangeListener() {
        for (Iterator i = backupStatusChangeListeners.iterator(); i.hasNext(); ) {
            addParameterStatusChangeListener((ParameterStatusChangeListener) i.next());
        }

        for (Iterator i = backupValueChangeListeners.iterator(); i.hasNext(); ) {
            addParameterValueChangeListener((ParameterValueChangeListener) i.next());
        }
    }

    /**
     * remove all listeners in 'backup change listener'
     * this method is called by DomeConnection.close() to cleanup a RuntimeInterface instance.
     * this method is also when we want to remove all listeners and add a new listener for each rerun.
     */
    public void clearValueAndStatusChangeListener() {
        statusChangeListeners.clear();
        valueChangeListeners.clear();

        backupStatusChangeListeners.clear();
        backupValueChangeListeners.clear();

        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            RuntimeParameter runtimeParam = (RuntimeParameter) i.next();
            runtimeParam.clearParameterStatusChangeListener();
            runtimeParam.clearParameterValueChangeListener();
        }
    }

    /**
     * remove all ParameterValueChangeListener from all runtime parameter
     */
    public void disableParameterValueChangeListener() {
        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            ((RuntimeParameter) i.next()).clearParameterValueChangeListener();
        }
        valueChangeListeners.clear();
    }

    /**
     * remove all ParameterStatusChangeListener from all runtime parameter
     */
    public void disableParameterStatusChangeListener() {
        for (Iterator i = getParameters().iterator(); i.hasNext(); ) {
            ((RuntimeParameter) i.next()).clearParameterStatusChangeListener();
        }
        statusChangeListeners.clear();
    }

    /**
     * Specify how long 'submit() method' is allowed to run for solving parametric changes.
     * if you set this time limit as zero, 'submit() method' can run for the unlimited time.
     * for example, if you give 10000, the time limit will be 10 seconds.
     * Once submit() method is invoked, the flow of a program will be held at the line of submit().
     * If the submitted changes are solved in 10 seconds, the program will proceed as usual.
     * But when it takes longer, submit() method would throw an ExecutionTimeLimitException,
     * which should be handled by the program to notify user that 'solving failed because of the execution time limit'
     * @param timeLimitInMilliseconds time limit in milliseconds unit, 0 for unlimited time limit.
     */
    public void setExecutionTimeLimit(long timeLimitInMilliseconds) {
        this.executionTimeLimit = timeLimitInMilliseconds;
    }

    /**
     * returns executionTimeLimit
     */
    public long getExecutionTimeLimit() {
        return executionTimeLimit;
    }

    /**
     * returns the directory where the file associated with a DomeFile parameter will be downloaded
     * it is assumed that each file downloaded has a unique name, so we can access the downloaded file as follows:
     * RuntimeParameter runtimeParam = runtimeInterface.getParameterByName("my param name");
     * String fileName = runtimeParam.getFileName();
     * String myFile = new File(getDownloadFolder().getAbsolutePath() +  "/" + fileName);
     *
     * If you just want to get the contents of DomeFile, following codes is simpler to use.
     * String textData = (String) mit.cadlab.dome3.api.RuntimeParameter.getFileValue();
     * byte[] binaryData = (byte[]) mit.cadlab.dome3.api.RuntimeParameter.getFileValue();
     */
    public File getDownloadFolder() {
        return downloadFolder;
    }

    /**
     * change download folder member variable
     * after changing it, invoke updateDownloadFolderOfAllFileParameters()
     */
    public void setDownloadFolder(String downloadFolderStr) {
        File downloadFolder = new File(downloadFolderStr);
        if (! downloadFolder.exists() || ! downloadFolder.isDirectory()) {
            throw new RuntimeException("The given download folder does not exist or is not a directory:" + downloadFolderStr);
        }
        this.downloadFolder = downloadFolder;
        updateDownloadFolderOfAllFileParameters();

        Debug.trace(Debug.ALL, "setting download folder. files associated with file-type parameters in the current runtime interface will be downloaded to " + downloadFolder.getAbsolutePath());
    }

    /**
     * change static member variable RuntimeInterface.DEFAULT_DOWNLOAD_FOLDER
     * it will affect the initial download folder of RuntimeInterface instances created after calling this method.
     * (i.e. RuntimeInterface instances that already created are not affected. they will continue download to the same folder they were using)
     * @param defaultDownloadFolderStr
     */
    public static void setDefaultDownloadFolder(String defaultDownloadFolderStr) {
        RuntimeInterface.DEFAULT_DOWNLOAD_FOLDER = defaultDownloadFolderStr;
    }

    /**
     * when getting and setting file parameter, a file corresponding to the parameter should
     * be downloaded into the local file system. use setDownloadFolder() to specify the download folder
     */
    private void updateDownloadFolderOfAllFileParameters() {
        // iterate though all model objects
        // there are three (can be more depending on the number of procedural relations) instance of the same DomeFile which reside in three different scopes:
        //  1) ModelInterfaceRuntimeClient scope
        //  2) ConcreteProceduralRelation scope (there can be any number of procedural relations. if there is two relations that uses DomeFile, two DomeFile instance will be created.
        //  3) InterfaceModelView scope
        Iterator it = interfaceClient.getInterfaceObjectsFlatMap().values().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof InterfaceParameterClient && ((InterfaceParameterClient) o).getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName())) {
                InterfaceParameterClient interfaceParamClient = (InterfaceParameterClient) o;
                DomeFile fileData = (DomeFile) interfaceParamClient.getCurrentDataObject();
                File file = new File(fileData.getFilePath());

                String originFilePath = file.getAbsolutePath();

                //fileData.setFilePath(downloadFolder.getAbsolutePath() + "/" + fileData.getFilePath());
                if (downloadFolder.getAbsolutePath().indexOf("\\") != -1) {
                    fileData.setFilePath(downloadFolder.getAbsolutePath() + "\\" + file.getName());
                } else {
                    fileData.setFilePath(downloadFolder.getAbsolutePath() + "/" + file.getName());
                }
                Debug.trace(Debug.ALL, "Updated a DomeFile instance's file path to " + fileData.getFilePath() + " from " + originFilePath + " in scope of " + interfaceParamClient.getScope().getClass().getName() + " named as '" + interfaceParamClient.getScope().getName() + "'");
            }
        }
    }


    /**
     * submit the changes made in RuntimeParameter's, and wait until the server will solve it to make the whole parameters consistent.
     * While the server is solving the parametric changes, the server will send 'value change event' and 'status(=color) change event'
     * through 'value listeners' and 'status listeners' added to this class.
     * Also this submit() method is allowed to run for limited time specified by setExecutionTimeLimit().
     */
    public boolean submit() throws ExecutionTimeLimitException {
        DomeClientApplication.DOME_API_BUILD_MODE = false;

        if (isFirstRun) {
            assumeAllInputParameterChangedForTheFirstRun();
        }

        /* why backup change listener is used here?
           When solving finishes SolverStateTracker calls
              - runtimeInterface.disableParameterStatusChangeListener();
              - runtimeInterface.disableParameterValueChangeListener();
           to make no more change event transfer to listeners. (it should look strange if listeners still received some change notification even after solving had finished)
           However, this listeners should be restored to the original state when users resubmit.
           because it should listen to new change events from resubmission.
           For this reason, restoreBackupChangeListener() comes here.
           Note. For the first submission, this restore is meaningless
           because listers are already added by addParameterValueChangeListener() and addParameterStatusChangeListener().
           However, after the first submission, this restore is necessary.
        */
        restoreBackupChangeListener();

        String modelName = "";
        if (this.getDomeInterface().isInterfaceOfModel()) {
            modelName = this.getDomeInterface().getParentModel().getModelName();
        } else if (this.getDomeInterface().isInterfaceOfProject()) {
            modelName = this.getDomeInterface().getParentProject().getProjectName();
        }

        /* The following line initializes SolverStateTracker by passing an instance of this class.
         * While stateTracker is initialilzed, it will add its inner class 'StatusChangeListenerForCompletionChecking' as a status listener to this RuntimeInterface.
         * After solving exits (could be completed or expired), it will remove the listener from a listener list by calling clearParameterValueListener() & clearParameterStatusListener().
         * Because clearXXXX() method removes all listeners attached to this RuntimeInterface,
         * all custom listeners attached by a user will also be removed.
         * This makes more sense than just removing one status listener attached by stateTracker
         * because if solving completes for any reason, whether it is successful or not, it is natural that no more events are sent to any listeners.
         * i.e. if we removed only one status listener of stateTracker, custom listeners will continue to receive meaningless events even after solving completes, especially when solving exits because of the expiration of execution time .
         */
        final SolverStateTracker stateTracker = new SolverStateTracker(this);
        final long startTime = System.currentTimeMillis();
        stateTracker.start();

        Debug.trace(Debug.ALL, "[DOME API] running \"" + modelName + "\" to solve submitted parameter changes");

//        final Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                /* if execution time limit is zero, timer doesn't need to be scheduled. */
//                /* terminate SolverStateTracker thread because the execution time limit expired*/
//                if (executionTimeLimit != 0 && System.currentTimeMillis() - startTime > executionTimeLimit) {
//                    stateTracker.expire();
//                    timer.cancel();
//                } else {
//                    synchronized (stateTracker.key) {
//                        boolean isSolved = stateTracker.checkIfGoodToFinishSolving();
//                        if (isSolved) {
//                            timer.cancel();
//                        }
//                    }
//                }
//            }
//        }, FINISH_CHECK_POLLING_PERIOD, FINISH_CHECK_POLLING_PERIOD);

        /* start solving by submitting parametric changes to the server */
        RuntimeFunctionsClient.setItems(domeConn.getServerConnection(), getChangedParameterTable(), true);

        /* wait until the server completes solving successfully
           or fails to solve because the solver didn't solved within the execution time limit. */
        try {
            stateTracker.join();
        } catch (InterruptedException e) {
            /* normally the program flow never reaches here */
            System.err.println(e);
        }

        if (stateTracker.isSolved()) {
            Debug.trace(Debug.ALL, "[DOME API] \"" + modelName + "\" solved parameter changes successfully.");
        } else {
            // todo: should notify server to stop solving, which will also make server to quit sending status change events.
            // todo: to check this done correct, the parameters should remain inconsistent if completionCheker exited with isSolved() false
            Debug.trace(Debug.ALL, "[DOME API] \"" + modelName + "\" failed to solve parameter changes: could not be solved within the execution time limit (=" + executionTimeLimit + " milliseconds). The values in parameters might not be consistent.");
            throw new ExecutionTimeLimitException(executionTimeLimit);
        }

        return stateTracker.isSolved();
    }

    /**
     * submit the changes made in RuntimeParameter's, and proceeds(=go) to next program lines without waiting until solving finishes.
     * A new thread for solving is created at the moment a user invokes this method, and the thread is accessible to user as a return value of this method : a SolverStateTracker instance.
     * There is a difference from submit() in the way how setExecutionTimeLimit() works with this method.
     * This method doesn't throw ExecutionTimeLimitException exception if solving expires (=not completed in execution time limit).
     * Instead, users can access the state of the solving thread by using SolverStateTracker.getState().
     * There exist three states of the thread: SolverStateTracker.RUNNING (=solving), SolverStateTracker.SUCCESS (=solved in time), and SolverStateTracker.FAILURE (=time expired).
     * While the server is solving the parametric changes, the server will send 'value change event' and 'status(=color) change event'
     * through 'value listeners' and 'status listeners' added to this class.
     */
    public SolverStateTracker submitAndGo() {
        if (isFirstRun) {
            assumeAllInputParameterChangedForTheFirstRun();
        }

        /* why backup change listener is used here? please read the comment in submit() */
        restoreBackupChangeListener();

        /* what does solver state tracker do? please see a comment in submit() */
        final SolverStateTracker stateTracker = new SolverStateTracker(this);
        stateTracker.start();

        Debug.trace(Debug.ALL, "start solving by submitAndGo()");

//        final Timer timer = new Timer();
//        final long startTime = System.currentTimeMillis();
//        /* if execution time limit is zero, timer doesn't need to be scheduled. */
//        timer.schedule(new TimerTask() {
//            public void run() {
//                /* terminate SolverStateTracker thread because the execution time limit expired*/
//                if (executionTimeLimit != 0 && System.currentTimeMillis() - startTime > executionTimeLimit) {
//                    stateTracker.expire();
//                    timer.cancel();
//                } else {
//                    synchronized (stateTracker.key) {
//                        boolean isSolved = stateTracker.checkIfGoodToFinishSolving();
//                        if (isSolved) {
//                            timer.cancel();
//                        }
//                    }
//                }
//            }
//        }, FINISH_CHECK_POLLING_PERIOD, FINISH_CHECK_POLLING_PERIOD);

        /* start solving by submitting parametric changes to the server */
        RuntimeFunctionsClient.setItems(domeConn.getServerConnection(), getChangedParameterTable(), true);

        return stateTracker;
    }

    /**
     * This method is automatically called when DomeConnection close() is called.
     * invokes 'kill interface parent'
     */
    protected void closeInterfaceParent() {
        // todo: ask someone which one is more correct way of killing model & playspace. both work fine.
        RuntimeFunctionsClient.killInterfaceParent(domeConn.getServerConnection(), interfaceClient.getRuntimeId());
    }

    /**
     * This method is used to manually close RuntimeInterface.
     *  - when DomeConnection closes, it automatically closes all RuntimeInterfaces associated with the connection
     *  - this method can be useful when user want to close RuntimeInterface before closing DomeConnection, probably to spare some memory
     */
    public void close() {
        // todo: ask someone which one is more correct way of killing model & playspace. both work fine.
        closeInterfaceParent();
        domeConn.removeFromRuntimeInterfaceList(this);
        clearValueAndStatusChangeListener();
    }
    /**
     * This method is automatically called when DomeConnection close() is called.
     * invokes 'leave playspace'
     */
    protected void closePlayspace() {
        RuntimeFunctionsClient.leavePlayspace(domeConn.getServerConnection(), playspaceClient.getCompoundId());
    }

    /**
     * invoked by users or by SolverStateTracker when solving is no more necessary; ex. when execution time expires.
     * now server side 'kill solving' implementation does not exist. 'kill solving' request is just ignored.
     */
    public void killSolving() {
        RuntimeFunctionsClient.killSolving(domeConn.getServerConnection(), interfaceClient.getRuntimeId());
    }

    /** automatically invoked when submit() finishes to clear changedParameter information. this runtime interface is made to have no parameter changed */
    protected void resetChangedParameter() {
        changedParameters.clear();
        changedRuntimeParams.clear();
    }

    /**
     * returns the runtime playspace instance of the runtime interface
     */
    public RuntimePlayspace getPlayspace() {
        DomePlayspace playspace = new DomePlayspace(playspaceClient.getCompoundId().getPlayspaceRuntimeId(), (playspaceClient.getName() == null) ? "transient" : playspaceClient.getName(), "Playspace associated with " + this, null, 1, domeConn);
        return new RuntimePlayspace(playspace, playspaceClient, domeConn);
    }

    public String toString() {
        return "[RUNTIME INTERFACE: '" + domeInterface.getInterfaceName() + "' (RUNTIMEID = " + interfaceClient.getId().getIdString() + " / STATIC ID = " + domeInterface.getInterfaceId() + "), description = " + domeInterface.getDescription() + ", last modified at = " + domeInterface.getLastModified() + ", version = " + domeInterface.getVersion() + "]";
    }

    /**
     * returns if the given object is a RuntimeInterface instance with the same runtime id.
     * this method is mostly used to ensure duplicated RuntimeInterfaces not to exist in the runtimeInterfaceList of DomeConnection.
     */
    public boolean equals(Object comparedRuntimeInterface) {
        if (comparedRuntimeInterface instanceof RuntimeInterface) {
            if (((RuntimeInterface) comparedRuntimeInterface).getRuntimeId() != null
                    && ((RuntimeInterface) comparedRuntimeInterface).getRuntimeId().equals(getRuntimeId())) {
                return true;
            }
        }
        return false;
    }
}

class InterfaceLoadingTracker extends Thread {
    private final long LOADING_TIME_LIMIT = 120000;  // 120 seconds

    private ModelInterfaceRuntimeClient interfaceClient;
    private long CHECKING_PERIOD = 300;  // check if loaded every 300 miliseconds

    public InterfaceLoadingTracker(ModelInterfaceRuntimeClient interfaceClient) {
        this.interfaceClient = interfaceClient;
    }

    public void run() {
        loadInterface();
    }

    private void loadInterface() {
        long startTime = System.currentTimeMillis();
        while (! interfaceClient.isLoaded()) {
            try {
                sleep(CHECKING_PERIOD);
                interfaceClient.synchronizeInterfaceState();
                if ((System.currentTimeMillis() - startTime) > LOADING_TIME_LIMIT) {
                    System.err.println("Fail to load interface '" + interfaceClient.getName() + "' : it took longer than time limit (" + LOADING_TIME_LIMIT / 1000 + " seconds)");
                    return;
                }
            } catch (InterruptedException e) { System.out.println(e); }
        }
    }
}