package mit.cadlab.dome3.plugin.catalog.runtime;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.RuntimeInterface;
import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;
import mit.cadlab.dome3.plugin.catalog.ui.IdlingThread;
import mit.cadlab.dome3.objectmodel.dataobject.FileTransport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.lang.System;

/**
 * User: Sangmok Han
 * Date: 2006. 6. 28.
 */
public class EvaluationContext {
    private CImplementation impl = null;
    private Map dataObjMap;
    protected Map connMap; // used for synchronization key
    private Map runtimeItfMap;
    private String workingDir;
    private EvaluationTracker evalTracker;
    private Set progressListeners;
    private boolean isValidationMode;
    private List mappingScriptFailureList;
    //private boolean isFirstEvaluation = true;

//    private String LOCK_A = "LOCK_BETWEEN_EVAL_COMPLETED_NOTIFICATION_AND_STATUS_CHANGE_NOTIFICATION";
//    private String LOCK_B = "LOCK_BETWEEN_EVAL_COMPLETED_NOTIFICATION_AND_VALUE_CHANGE_NOTIFICATION";

    /* EvaluationMode.init(): 1 -> 2, EvaluationMode.reset(): 3 -> 2, EvaluationMode.run(): 2 -> 3 */
    public static final int BEFORE_INITIALIZATION = 1; // value and status are not assigned yet
    public static final int WAITING_FIRST_EVALUATION = 2; // now interface input parameters are GREEN. other parameters are all RED. values are loaded from default value stored in the interface definition
    public static final int AFTER_FIRST_EVALUATION = 3; // now all parameters are expected to be consistent (WHITE). if not, call reset() to initialize the status of all parameters while keeping the current value of interface parameters
    public static final int ERROR_OCCURRED_DURING_EVALUATION = 4; // now some parameters are expected to be GREEN while others are RED.

    private int evalContextStatus = 1; // BEFORE_INITIALIZATION, WAITING_FIRST_EVALUATION or AFTER_FIRST_EVALUATION

    public EvaluationContext(CImplementation impl) {
        this.impl = impl;
        this.dataObjMap = new HashMap();
        this.connMap = new HashMap();
        this.runtimeItfMap = new HashMap();
        this.progressListeners = new HashSet();
        this.isValidationMode = false;
        mappingScriptFailureList = new ArrayList();
        this.workingDir = "C:/";
        impl.initializeParameterStatus();
        //this.isFirstEvaluation = true;
        this.evalContextStatus = BEFORE_INITIALIZATION;
    }

    /**
     * this method will clear runtime interface instances, failure reports, and parameter status
     * associated with the current EvaluationContext and CImplementation.
     * it also re-initialize values of interface input parameters into their default values.
     * It must be invoked when one wants to reuse EvaluationContext after the person has made a change that invalidates the current data object and parameter status.
     * For example, when one adds/removes a parameter or modifies the unit or type of a parameter (here the parameter is either CInterface parameter or CRelationParameter),
     * he or she must call this method to clear all data objects and old parameter status information stored in the EvaluationContext.
     *
     * call this method when one has made a structural change to the implementation
     * (such as adding/removing param or changing unit/type followed by synchronizeInterfaceParameters() call).
     * it will clear data objects and parameter status in the current evaluation context, which is not valid
     * after the structural changes, and set the value of interface input params to their default value.
     * note that evaluation context status will be set to BEFORE_INITIALIZATION.
     */
    public void refresh() {
        for (Iterator i = runtimeItfMap.values().iterator(); i.hasNext(); ) {
            RuntimeInterface runtimeItf = (RuntimeInterface) i.next();
            runtimeItf.close();
        }
        if (evalTracker != null) {
            evalTracker.stopTimer();
        }
        runtimeItfMap.clear();
        dataObjMap.clear();
        mappingScriptFailureList.clear();
        impl.initializeParameterStatus();
        evalContextStatus = BEFORE_INITIALIZATION;
    }

    public void setWorkingDirectory(String workingDir) {
        workingDir = workingDir.replace('\\', '/');
        for (Iterator i = dataObjMap.values().iterator(); i.hasNext(); ) {
            CDataObject dataObj = (CDataObject) i.next();
            /* when the working directory is changed, file path of existing data object needs to be updated accordingly */
            if (dataObj instanceof CFile && ((CFile) dataObj).getFilePath() != null) {
                File curFile = new File(((CFile) dataObj).getFilePath());
                String fileName = curFile.getName();
                String subWorkFolderName = curFile.getParentFile().getName();
                String subWorkFolderPath = workingDir + File.separator + subWorkFolderName;
                File subWorkFolder = new File(subWorkFolderPath);
                if (! subWorkFolder.exists()) {
                    subWorkFolder.mkdir();
                }
                String newFilePath = (subWorkFolderPath + File.separator + fileName).replace('\\', '/');
                ((CFile) dataObj).setFilePath(newFilePath);
            }
        }
        this.workingDir = workingDir;
    }


    /** validation mode is used to check consistency of causality in mappings. for normal, real evaluation, set it to false,  */
    public void setValidationMode(boolean isValidationMode) {
        this.isValidationMode = isValidationMode;
    }

    public boolean isAfterFirstEvaluation() {
        return evalContextStatus == AFTER_FIRST_EVALUATION;
    }

    public boolean isAfterErrorOccurred() {
        return evalContextStatus == ERROR_OCCURRED_DURING_EVALUATION;
    }

    public boolean isWaingFirstEvaluation() {
        return evalContextStatus == WAITING_FIRST_EVALUATION;
    }

    public boolean isBeforeInitialization() {
        return evalContextStatus == BEFORE_INITIALIZATION;
    }

    /** one of BEFORE_INITIALIZATION, WAITING_FIRST_EVALUATION, and AFTER_FIRST_EVALUATION */
    public int getEvaluationContextStatus() {
        return evalContextStatus;
    }

    /**
     * this method will initialize the value and status of parameters.
     * this method is used for UI when one needs to display default values for interface input parameters
     * before evaluating an implementation.
     *
     * 1) data object for interface input parameters will be instantiated
     *    based on the default values in the interface definition
     * 2) all parameters other than interface input parameters will be set RED.
     *    the interface input parameters will be set GREEN. */
    public void initializeDataObjectAndParameterStatusBasedOnInterfaceDefinition() {
        CNamingService namingService = getNamingService();
        Set params = namingService.getParameters();

        for (Iterator i = params.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            param.setStatus(CConstant.RED_STATUS);
            param.copyCurrentStatusToMappingNodes();
            this.getDataObject(param.getQualifiedName()); // this will instantiate a CDataObject in the EvaluationContext (1) it is initialized with its default value or (2) it just keeps the current value
        }

        Set iiParams = namingService.getInterfaceInputParameters();
        for (Iterator i = iiParams.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            param.setStatus(CConstant.GREEN_STATUS);
            param.copyCurrentStatusToMappingNodes();
        }

        evalContextStatus = WAITING_FIRST_EVALUATION;
    }

    public boolean isValidationMode() {
        return isValidationMode;
    }

    public EvaluationTracker getEvaluationTracker() {
        return evalTracker;
    }

    /** when evaluation exits unsuccessfully, use this method
     * to find a point where a certain relation has failed to execute */
    public RelationExecutionFailure[] getRelationExecutionFailures() {
        List ret = new ArrayList();
        Map relTrackerSetMap = this.getEvaluationTracker().getRelationTrackerSetMap();
        for (Iterator i = relTrackerSetMap.values().iterator(); i.hasNext(); ) {
            Set relTrackerSet = (Set) i.next();
            for (Iterator j = relTrackerSet.iterator(); j.hasNext();) {
                RelationTracker relTracker = (RelationTracker) j.next();
                if (relTracker.getRelationExecutionFailure() != null) {
                    ret.add(relTracker.getRelationExecutionFailure());
                }
            }
        }
        return (RelationExecutionFailure[]) ret.toArray(new RelationExecutionFailure[ret.size()]);
    }

    public void addEvaluationListener(EvaluationListener progressListener) {
        progressListeners.add(progressListener);
    }

    public Set getEvaluationListeners() {
        return progressListeners;
    }

    public void clearEvaluationListener() {
        progressListeners.clear();
    }

    protected void notifyMappingEvaluated(String qualifiedParamName) {
        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
            EvaluationListener progressListener = (EvaluationListener) i.next();
            progressListener.mappingEvaluated(qualifiedParamName);
        }
    }

    protected void notifyEvaluationStarted() {
        evalTracker.setStartTime(System.currentTimeMillis());
        evalTracker.setStatus(CConstant.EVALUATION_RUNNING_STATUS);

        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
            EvaluationListener progressListener = (EvaluationListener) i.next();
            progressListener.evaluationStarted();
        }
    }

    protected void notifyEvaluationEnded(boolean isSuccess) {
        if (isSuccess) {
            evalContextStatus = AFTER_FIRST_EVALUATION;
        } else {
            evalContextStatus = ERROR_OCCURRED_DURING_EVALUATION;
        }
        evalTracker.setEndTime(System.currentTimeMillis());
        evalTracker.setStatus(CConstant.EVALUATION_FINISHED_STATUS);

        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
            EvaluationListener progressListener = (EvaluationListener) i.next();
            progressListener.evaluationEnded(isSuccess);
        }
    }

    //protected void notifyRelationStarted(String relAlias) {
    protected void notifyRelationStarted(RelationTracker relTracker) {
//        evalTracker.getRelationTracker(relAlias).setStartTime(System.currentTimeMillis());
//        evalTracker.getRelationTracker(relAlias).setStatus(CConstant.RELATION_RUNNING_STATUS);
//
//        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
//            EvaluationListener progressListener = (EvaluationListener) i.next();
//            progressListener.relationStarted(relAlias);
//        }
        CLog.info("Start executing relation " + relTracker.getRelAlias() + " (id: " + relTracker.getTrackerId() + ") to determine " + relTracker.getParameterNamesToBeDetermined());

        relTracker.setStartTime(System.currentTimeMillis());
        relTracker.setStatus(CConstant.RELATION_RUNNING_STATUS);

        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
            EvaluationListener progressListener = (EvaluationListener) i.next();
            progressListener.relationStarted(relTracker.getRelAlias());
        }

    }

    protected void notifyRelationEnded(RelationTracker relTracker, boolean isSuccess) {
        CLog.info("Finish executing relation " + relTracker.getRelAlias() + " (id: " + relTracker.getTrackerId() + ") to determine " + relTracker.getParameterNamesToBeDetermined());

        relTracker.setEndTime(System.currentTimeMillis());
        relTracker.setStatus(CConstant.RELATION_FINISHED_STATUS);

        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
            EvaluationListener progressListener = (EvaluationListener) i.next();
            progressListener.relationEnded(relTracker.getRelAlias(), isSuccess);
        }
    }

    protected void notifyStatusChanged(int newStatus, String qualifiedParamName) {
//        synchronized (LOCK_A) {
            for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
                EvaluationListener progressListener = (EvaluationListener) i.next();
                progressListener.parameterStatusChanged(newStatus, qualifiedParamName);
            }
//        }
    }

    // send qualifiedParamName's newValue to all interested listeners
    protected void notifyValueChanged(CDataObject dataObj, String qualifiedParamName) {
//        synchronized (LOCK_B) {
    	    // if newValue is a file, send the new value object as a FileTransport
    		Object newValue = dataObj.getValue(); 
    		
    		if (dataObj instanceof CFile) {
    			CFile cFileObj = (CFile) dataObj;
				FileTransport fileTransportObj = new FileTransport();
				fileTransportObj.setFileName(cFileObj.getActualFilename());
				// If it is a large file, only the file path will be provided.
    			if (cFileObj.getFileSize() < FileTransport.MAXFILESIZE)
    				fileTransportObj.setFileContents( (byte[]) newValue );
    			
				newValue = fileTransportObj;
    			
    			// MAK: only do this here for debugging.Remove when done
                for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
                    EvaluationListener progressListener = (EvaluationListener) i.next();
                    progressListener.parameterValueChanged(newValue, qualifiedParamName);
                }
                return;
                //MAK: end of for-debug-only section
    		}

    		
            for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
                EvaluationListener progressListener = (EvaluationListener) i.next();
                progressListener.parameterValueChanged(newValue, qualifiedParamName);
            }
//        }
    }

    public String getWorkingDirectory() {
        return workingDir;
    }

    /** this method works as a pooled dome connection provider. it creates dome connection when necessary */
    public DomeConnection getConnection(String userId, String passwd, String serverPort) {
        synchronized (connMap) {
            DomeConnection conn = (DomeConnection) connMap.get(userId + "|" + serverPort);
            if (conn == null) {
                conn = new DomeConnection(userId, passwd, serverPort);
                connMap.put(userId + "|" + serverPort, conn);
            }
            return conn;
        }
    }

    /** this method works as a pooled runtime interface provider. it creates runtime interface when necessary */
    public RuntimeInterface getRuntimeInterface(DomeConnection conn, String space, String interfacePath, String relAlias, int trackerId) {
        synchronized (runtimeItfMap) {
            RuntimeInterface runtimeItf = (RuntimeInterface) runtimeItfMap.get(relAlias + "|" + trackerId);
            if (runtimeItf == null) {
                runtimeItf = conn.getInterfaceByPath(space, interfacePath).createRuntimeInterface();
                runtimeItfMap.put(relAlias + "|" + trackerId, runtimeItf);
            }
            return runtimeItf;
        }
    }

    /** clean up DomeConnection and RuntimeInterface associated with the current EvaluationContext */
    public void close() {
        refresh();
        for (Iterator i = connMap.values().iterator(); i.hasNext(); ) {
            DomeConnection conn = (DomeConnection) i.next();
            conn.close();
        }
        connMap.clear();
    }

    /** return the data object instance of an interface parameter with the given local param name
     * such as "width" or "height," which means it does not start with the interface alias "itf." */
    public CDataObject getInterfaceDataObject(String localParamName) {
        return getDataObject(CConstant.ITF_ALIAS + "." + localParamName);
    }

    /**
     * assign the given data object as the data object of a parameter with the given name.
     * This method is used when a user has an actual data object to be associated with an interface parameter.
     *
     * Note that when a user doesn't have an actual data object but has a value that needs to be set to an interface parameter,
     * use getInterfaceDataObject(localParamName) to get a reference to a new data object and modify the value of the returned data object.
     * (ex) ((CReal) context.getInterfaceDataObject("Y")).setDoubleValue(valueY);
     */
    public void putInterfaceDataObject(String localParamName, CDataObject dataObj) {
        dataObjMap.put(CConstant.ITF_ALIAS + "." + localParamName, dataObj);
    }

    /** if given qualified param name does not exist in dataObjMap, create a data object
     * corresponding to the given qualified param name in the evaluation context
     * a qualified param name like "itf.param X", "relA.area", or DomeSerialization.IMPL_SWITCH is passed as an argument
     */
    public CDataObject getDataObject(String qualifiedParamName) {
        CDataObject catDataObj = (CDataObject) dataObjMap.get(qualifiedParamName);

        CParameter param = getNamingService().getParameter(qualifiedParamName);

        if (catDataObj == null) {
            if (param == null) {
                throw new RuntimeException("parameter " + qualifiedParamName + " does not exist");
            }

            if (CConstant.REAL_DATA_TYPE.equals(param.getDataType())) {
                catDataObj = new CReal(Double.parseDouble(param.getDefaultValue()), param.getUnit());
            } else if (CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                catDataObj = new CInteger(Integer.parseInt(param.getDefaultValue()), param.getUnit());
            } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                catDataObj = new CString(param.getDefaultValue(), param.getUnit());
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                catDataObj = new CBoolean(param.getDefaultValue(), param.getUnit());
            } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                catDataObj = new CEnumeration(DataObjectUtil.createEnumList(param.getDefaultValue()), new CUnit(param.getUnit()));
            } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                String[] fileNameAndFileValue = DataObjectUtil.getFileNameAndFileValue(param.getDefaultValue());
                catDataObj = new CFile(fileNameAndFileValue [1], new CUnit(param.getUnit()));

                String subFolderName = null;
                if (param instanceof CInterfaceInputParameter || param instanceof CInterfaceOutputParameter) {
                    subFolderName = "interface";
                } else if (param instanceof CRelationInputParameter) {
                    subFolderName = "relation_" + ((CRelationInputParameter) param).getRelation().getRelAlias();
                } else if (param instanceof CRelationOutputParameter) {
                    subFolderName = "relation_" + ((CRelationOutputParameter) param).getRelation().getRelAlias();
                }

                String subWorkFolderPath = this.getWorkingDirectory() + File.separator + subFolderName;
                subWorkFolderPath = subWorkFolderPath.replace('\\', '/');
                File subWorkFolder = new File(subWorkFolderPath);
                if (! subWorkFolder.exists()) {
                    subWorkFolder.mkdir();
                }

                String filePath = subWorkFolderPath + File.separator + fileNameAndFileValue [0];
                filePath = filePath.replace('\\', '/');
                ((CFile) catDataObj).setFilePath(filePath);
            } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                //List rowList = DataObjectUtil.createMatrix(param.getDefaultValue(), CConstant.REAL_DATA_TYPE);
                List rowList = DataObjectUtil.createMatrix(param.getDefaultValue());
                catDataObj = new CMatrix(rowList, new CUnit(param.getUnit()));
            } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                //List rowList = DataObjectUtil.createVector(param.getDefaultValue(), CConstant.REAL_DATA_TYPE);
                List rowList = DataObjectUtil.createVector(param.getDefaultValue());
                catDataObj = new CVector(rowList, new CUnit(param.getUnit()));
            }
            dataObjMap.put(qualifiedParamName, catDataObj);
        }

        return catDataObj;
    }

    /** returns a Map of (qualified param name string) -> (CDataObject value of the parameter) */
    public Map getDataObjectMap() {
        return dataObjMap;
    }

    public CNamingService getNamingService() {
        return impl.getNamingService();
    }

    /**
     * evalute an implementation assuming all interface input parameters are modified.
     * this method is useful for the first-time evaluation of an implementation.
     * @see #evaluate(Set)
     * @see #evaluate(String[]) */
    public void evaluate() {
        List iParamNames = impl.getParentInterface().getInputParameterNames();
        evaluate(new HashSet(iParamNames));
    }


    /**
     * evaluate an implementation assuming that the parameters specified
     * by modifiedParamNames, which are local names such as "width", are modified.
     * @see #evaluate(Set)
     * @see #evaluate() */
    public void evaluate(String[] modifiedParamNames) {
        evaluate(new HashSet(Arrays.asList(modifiedParamNames)));
    }

    /**
     * this method is called to evaluate the implementation of an interface
     * after modifying the data objects of the interface input parameters
     * Note that one can modify a data object
     * by calling context.getDataObject(CConstant.ITF_ALIAS + "width")
     * and calling setXXXXValue() method such as setDoubleValue(double)
     *
     * parameter names in modifiedParamNameSet are non-qualified name such as "width" or "implementation switch"
     */
    public void evaluate(Set modifiedParamNameSet) {
        if (evalTracker != null) {
            evalTracker.stopTimer();
        }

        evalTracker = new EvaluationTracker(this);
        notifyEvaluationStarted();

        CNamingService namingService = getNamingService();
        CInterface targetInterface = impl.getParentInterface();

        /* 1. adjust modified parameters and turn their status to green */
        if (modifiedParamNameSet.contains(CConstant.IMPL_SWITCH)) {
            modifiedParamNameSet = CNamingService.createNameSet(namingService.getInterfaceInputParameters(), false);
        }
        for (Iterator i = modifiedParamNameSet.iterator(); i.hasNext();) {
            String modifiedParamName = (String) i.next();
            String qualifiedParamName = CConstant.ITF_ALIAS + "." + modifiedParamName;
            CInterfaceInputParameter modifiedParam = namingService.getInterfaceInputParameter(qualifiedParamName);
            modifiedParam.toGreenStatus();
            notifyStatusChanged(CConstant.GREEN_STATUS, modifiedParam.getQualifiedName());
//            if (modifiedParam.getStatus() != CConstant.GREEN_STATUS) {
//                notifyStatusChanged(CConstant.GREEN_STATUS, modifiedParam.getQualifiedName());
//            }
            turnAllMappingNodesToRed(modifiedParam);
        }

        //slowDown();

        /* 2. resolve the difference between the causality defined in the interface and the causality defined in the implementation */
        //speedup Clog.debug("/* 2. resolve the difference between the causality defined in the interface and the causality defined in the implementation */");
        Set drivenParamNameSetDefinedByInterface = new HashSet();
        Set drivenParamNameSetDefinedByImplementation = new HashSet();
        for (Iterator i = modifiedParamNameSet.iterator(); i.hasNext();) {
            String modifiedParamName = (String) i.next();
            CInterfaceInputParameter modifiedParam = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + modifiedParamName);

            /* collect driven param names defined by the interface (non-qualified name is stored) */
            drivenParamNameSetDefinedByInterface.addAll(targetInterface.getDrivensBy(modifiedParamName));

            /* collect driven param names defined by the implementation (non-qualified name is stored) */
            Set drivenParamSetDefinedByImplementation = modifiedParam.getDrivensBy(true);
            //drivenParamNameSetDefinedByImplementation.add(CNamingService.createNameSet(drivenParamSetDefinedByImplementation, false));
            drivenParamNameSetDefinedByImplementation.addAll(CNamingService.convertToLocal(drivenParamSetDefinedByImplementation));
        }

        //slowDown();

        /* find a remaining set, which is not affected by modified param from the implementation point of view,
         * but is expected to be affected from the interface point of view. drivenParamNameSetDefinedByInterface is the remaining set after the below operation */
        drivenParamNameSetDefinedByInterface.removeAll(drivenParamNameSetDefinedByImplementation);
        for (Iterator j = drivenParamNameSetDefinedByInterface.iterator(); j.hasNext(); ) {
            String remainingParamName = (String) j.next();
            CInterfaceOutputParameter remainingParam = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + remainingParamName);
            if (! remainingParam.isConsistent() && remainingParam.getMapping().getInputNodes().isEmpty()) {
                executeMappingScript(remainingParam.getMapping());
                remainingParam.toGreenStatus();
                notifyStatusChanged(CConstant.GREEN_STATUS, remainingParam.getQualifiedName());
                turnAllMappingNodesToGreen(remainingParam);
            }
        }

        //slowDown();

        /* 3. mark all inconsistent parameters and mapping nodes */
        //speedup Clog.debug("/* 3. mark all inconsistent parameters and mapping nodes */");
        for (Iterator i = modifiedParamNameSet.iterator(); i.hasNext();) {
            String modifiedParamName = (String) i.next();
            CInterfaceInputParameter modifiedParam = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + modifiedParamName);

            /* collect driven param names defined by the implementation (non-qualified name is stored) */
            Set drivenParamSet = modifiedParam.getDrivensBy(false);
            for (Iterator j = drivenParamSet.iterator(); j.hasNext(); ) {
                String drivenParamName = (String) j.next();
                CParameter drivenParam = namingService.getParameter(drivenParamName);
                drivenParam.toRedStatus();
                turnAllMappingNodesToRed(drivenParam);
                if (drivenParam.getStatus() != CConstant.RED_STATUS) {
                    notifyStatusChanged(CConstant.RED_STATUS, drivenParam.getQualifiedName());
                }
            }
            turnAllMappingNodesToRed(modifiedParam);
        }

        //slowDown();

        /* 4. seed green status propagation and start propagation */
        List greenListSeed = new ArrayList();
        //speedup Clog.debug("/* 4. seed green-status parameters from modified interface parameters */");
        for (Iterator i = modifiedParamNameSet.iterator(); i.hasNext();) {
            String modifiedParamName = (String) i.next();
            String qualifiedParamName = CConstant.ITF_ALIAS + "." + modifiedParamName;
            greenListSeed.add(qualifiedParamName);
        }

        //slowDown();

        /* 5. execute empty mappings */
        //speedup Clog.debug("/* 5. execute empty mappings and seed more green-status parameters */");
        Set relationInputParamSet = namingService.getRelationInputParameters();
        for (Iterator i = relationInputParamSet.iterator(); i.hasNext(); ) {
            CRelationInputParameter riParam = (CRelationInputParameter) i.next();
            if (! riParam.isConsistent() && riParam.getMapping().getInputNodes().isEmpty()) {
                executeMappingScript(riParam.getMapping());
                riParam.toGreenStatus();
                notifyStatusChanged(CConstant.GREEN_STATUS, riParam.getQualifiedName());
                greenListSeed.add(riParam.getQualifiedName());

                /* collect driven param names affected by executing empty mappings */
                Set drivenParamSet = riParam.getDrivensBy(false);
                for (Iterator j = drivenParamSet.iterator(); j.hasNext(); ) {
                    String drivenParamName = (String) j.next();
                    CParameter drivenParam = namingService.getParameter(drivenParamName);
                    drivenParam.toRedStatus();
                    turnAllMappingNodesToRed(drivenParam);
                    if (drivenParam.getStatus() != CConstant.RED_STATUS) {
                        notifyStatusChanged(CConstant.RED_STATUS, drivenParam.getQualifiedName());
                    }
                }
                turnAllMappingNodesToRed(riParam);

                //turnAllMappingNodesToGreen(riParam);
            }
        }
        Set interfaceOutputParamSet = namingService.getInterfaceOutputParameters();
        for (Iterator i = interfaceOutputParamSet.iterator(); i.hasNext(); ) {
            CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) i.next();
            if (! ioParam.isConsistent() && ioParam.getMapping().getInputNodes().isEmpty()) {
                executeMappingScript(ioParam.getMapping());
                ioParam.toGreenStatus();
                notifyStatusChanged(CConstant.GREEN_STATUS, ioParam.getQualifiedName());
                greenListSeed.add(ioParam.getQualifiedName());

                /* collect driven param names affected by executing empty mappings */
                Set drivenParamSet = ioParam.getDrivensBy(false);
                for (Iterator j = drivenParamSet.iterator(); j.hasNext(); ) {
                    String drivenParamName = (String) j.next();
                    CParameter drivenParam = namingService.getParameter(drivenParamName);
                    drivenParam.toRedStatus();
                    turnAllMappingNodesToRed(drivenParam);
                    if (drivenParam.getStatus() != CConstant.RED_STATUS) {
                        notifyStatusChanged(CConstant.RED_STATUS, drivenParam.getQualifiedName());
                    }
                }
                turnAllMappingNodesToRed(ioParam);
                //turnAllMappingNodesToGreen(ioParam);
            }
        }

//        if (evalTracker.isExitConditionSatisified()) {
//            //speedup Clog.debug("/* 6. no need for propagation */");
//            return;
//        }

        /* 6. start propagation */
        //speedup Clog.debug("/* 6. start propagation */");
        try {
            evalTracker.startPropagation(greenListSeed);
            evalTracker.join();
        } catch (InterruptedException e) {
            CLog.log("error occurred when joining the evaluation tracker thread: " + e.getMessage());
        }

        StringBuffer sb = new StringBuffer();
        sb.append("Summary of executed relations for \"" + impl.getParentInterface().getParentModel().getName() + "\" (itf=" + impl.getParentInterface().getName() + ", impl=" + impl.getName() + ", total=" + evalTracker.getEvaluationTime() + " ms)\n");
        for (Iterator i = evalTracker.getRelationTrackerSetMap().values().iterator(); i.hasNext(); ) {
            Set relTrackerSet = (Set) i.next();
            for (Iterator j = relTrackerSet.iterator(); j.hasNext();) {
                RelationTracker relTracker = (RelationTracker) j.next();
                CRelation rel = namingService.getRelation(relTracker.getRelAlias());
                String relType = null;

//                String statusStr = "ready";
//                if (relTracker.getStatus() == CConstant.RELATION_RUNNING_STATUS) {
//                    statusStr = "running";
//                } else if (relTracker.getStatus() == CConstant.RELATION_FINISHED_STATUS) {
//                    statusStr = "finished";
//                }

                if (rel instanceof CLocalRelation) {
                    relType = "<LOCAL>";
                } else if (rel instanceof CRemoteRelation) {
                    relType = "<REMOTE> ";
                } else {
                    relType = "<UNKNOWN>";
                }
                String successOrFailure = (relTracker.hasNoRelationExectionFailure() ? "success" : "error" );
                sb.append("    " + relType + " \"" + rel.getRelationName() + "\" (alias=" + rel.getRelAlias() + ", id=" + relTracker.getTrackerId() + ") took " + relTracker.getEvaluationTime() + " ms to determine " + relTracker.getParameterNamesToBeDetermined()).append(" --> " + successOrFailure).append('\n');
            }
        }
        CLog.info(sb.toString());
        // delete
    }

    private void slowDown() {
        try {
            CLog.println("[SLOW DOWN] --------------- [SLOW DOWN]");
            IdlingThread ht = new IdlingThread(330, new PrintWriter(System.out), new String[] { "Step1", "Step2", "Step3" });

            ht.start();

            try {
                ht.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** find all mapping nodes of the given parameter and turn their status to green */
    public void turnAllMappingNodesToGreen(CParameter param) {
        Set mappingNodes = getNamingService().findMappingNodes(param.getQualifiedName());
        for (Iterator k = mappingNodes.iterator(); k.hasNext(); ) {
            ((CMappingNode) k.next()).toGreenStatus();
        }
    }

    /** find all mapping nodes of the given parameter and turn their status to red */
    public void turnAllMappingNodesToRed(CParameter param) {
        /* find relation mapping nodes whose mapped parameters is red. then turn those node red */
        Set mappingNodes = getNamingService().findMappingNodes(param.getQualifiedName());
        for (Iterator k = mappingNodes.iterator(); k.hasNext(); ) {
            CMappingNode node = (CMappingNode) k.next();
            //speedup Clog.debug("[turn all mapping nodes to red] node.getMappedParameterName()= " + node.getMappedParameterName());
            node.toRedStatus();
            //((CMappingNode) k.next()).toRedStatus();
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            Binding binding = new Binding();
            long start = System.currentTimeMillis();
            Object ret = evaluateGroovyScript(binding, "5 + 4; \n return 5; \n 4 + 2;");
            long duration = System.currentTimeMillis() - start;
            System.out.println(ret);
            System.out.println("duration = " + duration);
        }
    }



    public String toString() {
        return "[EvaluationContext: model=" + impl.getParentInterface().getParentModel().getName() + ", itf=" + impl.getParentInterface().getName() + ", impl=" + impl.getName() + ", dataObjMap=" + dataObjMap + ", workingDir=" + workingDir + ", evalTracker=" + evalTracker + ", progressListeners=" + progressListeners + ", connMap=" + connMap + ", runtimeItfMap=" + runtimeItfMap + ", isValidationMode=" + isValidationMode + "]";
    }

    public static GroovyShell shell = null;

    /** create a Groovy shell and return the evaluation result of a script. returns null if an error occurs */
    public static Object evaluateGroovyScript(Binding binding, String script) {
        GroovyShell shell;
        try {
            shell = new GroovyShell(binding);
            Object result = shell.evaluate(script, "mappingscript.groovy");
            return result;
        } catch (CompilationFailedException e) {
            CLog.log("fail to execute mapping script \"" + script + "\" : " + e);
            return null;
        }
    }

    /** execute the given mapping script */
    protected void executeMappingScript(CMapping mapping) {
        /* when mapping script is null or empty we don't have to execute the script */
        if (mapping.getMappingScript() != null && ! "".equals(mapping.getMappingScript().trim())) {
            /* 1. transform a mapping script into a groovy executable form */
            //speedup Clog.debug("[mapping eval-1] original mapping script: " + mapping.getMappingScript());


            /* 3. if the script contains a text representation of matrix or vector, surround it with an appropriate utility functions such as matrix() and vector()
                  For example, 'context.getDataObject("itf.param A") + [3 4 5; 7 8 9]' will be converted into 'context.getDataObject("itf.param A") + matrix("[3 4 5; 7 8 9]")' */
            List paramNameList = new ArrayList();
            for (Iterator j = mapping.getInputNodes().iterator(); j.hasNext();) {
                CMappingNode mappingNode = (CMappingNode) j.next();
                paramNameList.add(mappingNode.getMappedParameterName());
            }

            /* longer param name should come first */
            Collections.sort(paramNameList, new Comparator() {
                public int compare(Object left, Object right) {
                    return ((String) right).length() - ((String) left).length();
                }
            });

            String[] paramNames = (String[]) paramNameList.toArray(new String[paramNameList.size()]);
            String convertedScript = RuntimeUtil.convertMappingScript(paramNames, mapping.getMappingScript());

            /* 2. execute the tranformed mapping script */
            Binding binding = new Binding();
            binding.setVariable("context", this);

//            CParameter outputNodeParam = getNamingService().getParameter(mapping.getOutputNode().getMappedParameterName());
//            if (CConstant.VECTOR_DATA_TYPE.equals(outputNodeParam.getDataType())) {
//                Pattern vectPattern = Pattern.compile("(\\[[0-9\\., ]*\\])");
//                StringBuffer newMappingScriptsb = new StringBuffer();
//                Matcher vectMatcher = vectPattern.matcher(mappingScriptSb);
//                while (vectMatcher.find()) {
//                    vectMatcher.appendReplacement(newMappingScriptsb, "matrix($1)");
//                }
//                vectMatcher.appendTail(newMappingScriptsb);
//            } else if (CConstant.MATRIX_DATA_TYPE.equals(outputNodeParam.getDataType())) {
//                Pattern matPattern = Pattern.compile("(\\[(?:[0-9\\., ]*;{0,1})*\\])");
//                Matcher matMatcher = matPattern.matcher(mappingScriptSb);
//                while (matMatcher.find()) {
//                    System.out.println(matMatcher.start(1));
//                    System.out.println(matMatcher.group(1));
//                    System.out.println(matMatcher.end(1));
//                }
//            }

            /* 4. surround the script with utility methods such as number(), vector(), and matrix() */
            String scriptBeginsWith = "import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;\ndef integer(str) { return new CInteger(str); }\ndef real(str) { return new CReal(str); }\ndef matrix(str) { return new CMatrix(str); }\ndef vector(str) { return new CVector(str); }\ndef pow(firstCRealOrCInteger, secondDouble) { return DataObjectUtil.pow(firstCRealOrCInteger, secondDouble); }\n\n";
//            String scriptBeginsWith = "import mit.cadlab.dome3.api.*;\nimport mit.cadlab.dome3.plugin.catalog.core.dataobject.*;\nimport mit.cadlab.dome3.plugin.catalog.runtime.*;\npublic class MappingScript {\n\tEvaluationContext context = null;\n\tpublic MappingScript(EvaluationContext context) { this.context = context; }\n\tpublic CInteger number(int num) { return new CInteger(num); }\n\tpublic CReal number(double num) { return new CReal(num); }\n\tpublic CMatrix matrix(String num) { return new CMatrix(num); }\n\tpublic CVector vector(String num) { return new CVector(num); }\n\tpublic CReal pow(CReal first, double second) { return DataObjectUtil.pow(first, second); }\n\tpublic CReal pow(CInteger first, double second) { return DataObjectUtil.pow(first, second); }\n\n\tpublic Object execute() {\n\t\treturn ";
//            String scriptEndsWith = ";\n\t}\n}\n\ndef mapScript = new MappingScript(context);\nreturn mapScript.execute();\n";
            convertedScript = scriptBeginsWith + convertedScript;

            //speedup Clog.debug("[mapping eval-2] converted mapping script: " + convertedScript);
            Object evalResult = evaluateGroovyScript(binding, convertedScript);
            //speedup Clog.debug("[mapping eval-3] evaluation result: " + evalResult);
            CLog.script("running mapping script: \n<<beginning of the mapping script>>\n" + convertedScript + "\n<<end of the mapping script>>");

            /* 3. Assign the evaluation result to a data object associated with the output node. */
            try {
                CParameter outputParam = impl.getNamingService().getParameter(mapping.getOutputNode().getMappedParameterName());
                CDataObject outputDataObj = getDataObject(mapping.getOutputNode().getMappedParameterName());
                outputDataObj.leftShift(evalResult);
            } catch (IllegalArgumentException e) {
                MappingScriptExecutionFailure failure = new MappingScriptExecutionFailure(mapping.getOutputNode().getMappedParameterName(), mapping.getMappingScript(), this, "fail to execute a mapping script", e);
                CLog.info("error occurred from the following mapping script: \n<<beginning of the mapping script>>\n" + convertedScript + "\n<<end of the mapping script>>");
                this.addMappingScriptExecutionFailure(failure);
            }
        }

//        /* 4. If an interface output parameter's value is updated, reflect the change to the dome plugin data object */
//        if (! isValidationMode() && mapping.getOutputNode().getMappedParameterName().startsWith(CConstant.ITF_ALIAS + ".")) {
//            CDataObject dataObj = getDataObject(mapping.getOutputNode().getMappedParameterName());
//
//            if (dataObj.isAssociatedWithInterfaceInputParameter() || dataObj.isAssociatedWithInterfaceOutputParameter()) {
////                /* when catalog data object is associated with DOME plugin data object, reflect the change */
////                CLog.println("copyFromCatalogDataObjectToDomePluginDataObject() : " + mapping.getOutputNode().getMappedParameterName());
////                getDataObject(mapping.getOutputNode().getMappedParameterName()).copyFromCatalogDataObjectToDomePluginDataObject();
//            } else {
//                /* when catalog data object is not associated with DOME plugin data object, do nothing */
//            }
//        }

        /* notify that the output node of this mapping is assigned an evaluation result */
        notifyMappingEvaluated(mapping.getOutputNode().getMappedParameterName());

        /* notify that the value of the output node of this mapping is changed to the current value */
        String qualifiedParamName = mapping.getOutputNode().getMappedParameterName();
        notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName), qualifiedParamName);

        evalTracker.stopWaitingBecauseOfMappingScriptExecution();
    }

    private void addMappingScriptExecutionFailure(MappingScriptExecutionFailure failure) {
        mappingScriptFailureList.add(failure);
    }

    public MappingScriptExecutionFailure[] getMappingScriptExecutionFailures() {
        return (MappingScriptExecutionFailure []) mappingScriptFailureList.toArray(new MappingScriptExecutionFailure[mappingScriptFailureList.size()]);
    }
}