package mit.cadlab.dome3.plugin.catalog.runtime;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import mit.cadlab.dome3.api.*;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 1.
 */
public class RelationTracker extends Thread {
    private CRelation relation;
    private EvaluationTracker evalTracker;
    private EvaluationContext context;
    private long startTime, endTime;
    private int status;
    private boolean isValidationMode;
    private int trackerId;
    private Set paramNamesToBeDetermined;
    private RelationExecutionFailure relExecFailure;
    private RemoteRelationListener remoteRelListener; // not null when this relation tracker is executing CRemoteRelation

    /*
     * first define four type events:
     * A: solving finish event arrives at status listener
     * B: value change event arrives at value listener
     * C: status listener processes the solving finish event and notify to an evaluation tracker to determine if the current evaluation is successful or not.
     * D: value listener processes the value change event and notify to an relation tracker to update the parameter value
     *
     * In order for evaluation tracker to work, D should occur before C.
     * (1) To achieve this, we add a wait right before D occurs if B event is detected. the wait will be released when C event is detected.
     * (2) To increase the possibility of detecting B event, we also add a fix-time wait before an if-statement detecting the occurence of B event.
     */
//    protected boolean processingValueChangeEvent = false;

    public static long LOCAL_RELATION_SIMULATED_TOTAL_EVALUATION_TIME = 200;
    public static long REMOTE_RELATION_SIMULATED_LOADING_TIME = 200;
    public static long REMOTE_RELATION_SIMULATED_PARAMETER_UPDATE_TIME = 100;

    /** paramNamesToBeDetermined is a set of parameter names that will be determined as a result of executing this relation */
    public RelationTracker(CRelation relation, int trackerId, Set paramNamesToBeDetermined, EvaluationTracker evalTracker, boolean isValidationMode) {
        this.relation = relation;
        this.evalTracker = evalTracker;
        this.context = evalTracker.getContext();
        this.status = CConstant.RELATION_READY_STATUS;
        this.isValidationMode = isValidationMode;
        this.setDaemon(false);
        this.trackerId = trackerId;
        this.paramNamesToBeDetermined = paramNamesToBeDetermined;
    }

    public String getRelAlias() {
        return relation.getRelAlias();
    }

    public int getTrackerId() {
        return trackerId;
    }

    /** local param names to be determined by executing this relation */
    public Set getParameterNamesToBeDetermined() {
        return paramNamesToBeDetermined;
    }

    /** CConstant.RELATION_READY_STATUS, CConstant.RELATION_RUNNING_STATUS, or CConstant.RELATION_FINISHED_STATUS*/
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    /** when a relation is executed successfully, exit exception is set as null.
      * otherwise, exitException is set as the exception that has caused the relation to exit. */
    public RelationExecutionFailure getRelationExecutionFailure() {
        return relExecFailure;
    }

    public boolean hasNoRelationExectionFailure() {
        return (relExecFailure == null);
    }

    /** called when relation exits unsuccessfully. set relExecFailure */
    public void setRelationExecutionFailure(String message, Exception ex) {
        this.relExecFailure = new RelationExecutionFailure(relation.getRelAlias(), trackerId, message, evalTracker.getContext(), ex);
    }

    /** called when relation exits successfully. set-null relExecFailure */
    public void setNoRelationExecutionFailure() {
        this.relExecFailure = null;
    }

    /**
     * if the relation has finished, return the time taken until it finishes.
     * if the relation is running, return the time taken so far.
     * if the relation is ready but not running, return -1.
     */
    public long getEvaluationTime() {
        if (status == CConstant.RELATION_FINISHED_STATUS) {
            return endTime - startTime;
        } else if (status == CConstant.RELATION_RUNNING_STATUS) {
            long evalTime = System.currentTimeMillis() - startTime;
            return evalTime;
        } else {
            return -1;
        }
    }

    public synchronized void startRelationTracker() {
        evalTracker.registerRelationTracker(this);
        startTime = System.currentTimeMillis();
        start();
    }

    public void run() {
        if (isValidationMode) {
            simulateRelation();
        } else {
            executeRelation();
        }
    }

    public void executeRelation() {
        context.notifyRelationStarted(this);

        if (relation instanceof CLocalRelation) {
            executeLocalRelation();
        } else if (relation instanceof CRemoteRelation) {
            executeRemoteRelation();
        }
    }

    public void simulateRelation() {
        context.notifyRelationStarted(this);

        if (relation instanceof CLocalRelation) {
            simulateLocalRelation();
        } else if (relation instanceof CRemoteRelation) {
            simulateRemoteRelation();
        }
    }

    private void simulateLocalRelation() {
        CLocalRelation localRel = (CLocalRelation) relation;

        /* execute local relation script */
        try {
            CLog.log("simulating local relation evaluation");
            sleep(LOCAL_RELATION_SIMULATED_TOTAL_EVALUATION_TIME);
        } catch (InterruptedException e) { }

        Set roParamToBeNotified = new HashSet();
        List paramNameList = localRel.getInputParameterNames(); // local relation param name list
        for (Iterator i = paramNameList.iterator(); i.hasNext(); ) {
            String riParamName = (String) i.next();
            String qualifiedParamName = localRel.getRelAlias() + "." + riParamName;

            CParameter riParam = evalTracker.getContext().getNamingService().getParameter(qualifiedParamName);
            if (riParam.getStatus() == CConstant.GREEN_STATUS) {
                Set drivenParamNameSet = localRel.getDrivensBy(riParamName);
                roParamToBeNotified.addAll(drivenParamNameSet);
            }
        }

        /* some of affected parameters should not be notified if they does not belong to the paramNamesToBeDetermined set */
        roParamToBeNotified.retainAll(paramNamesToBeDetermined);

        /* 1. add new green parameters into the green list  */
        /* 2. update the status of relation output parameters */
        /* 3. randomly change the value of relation output parameters */
        for (Iterator i = roParamToBeNotified.iterator(); i.hasNext();) {
            String roParamName = (String) i.next();
            String qualifiedParamName = localRel.getRelAlias() + "." + roParamName;

            CDataObject dataObj = evalTracker.getContext().getDataObject(qualifiedParamName);
            if (dataObj instanceof CReal) {
                ((CReal) evalTracker.getContext().getDataObject(qualifiedParamName)).setDoubleValue(Math.random() * 100);
            } else if (dataObj instanceof CInteger) {
                ((CInteger) evalTracker.getContext().getDataObject(qualifiedParamName)).setIntegerValue((int) (Math.random() * 100));
            } else if (dataObj instanceof CString) {
                ((CString) evalTracker.getContext().getDataObject(qualifiedParamName)).setStringValue("random_string_" + (int) (Math.random() * 100));
            } else if (dataObj instanceof CEnumeration) {
                List enumList = ((CEnumeration) dataObj).getEnumList();
                int randomSelectedIdx = (int) Math.random() * enumList.size();
                DataObjectUtil.setSelectedIndex(randomSelectedIdx, enumList);
                ((CEnumeration) dataObj).setEnumList(enumList);
            } else if (dataObj instanceof CFile) {
                CRelationOutputParameter param = relation.getNamingService().getRelationOutputParameter(relation.getRelAlias() + "." + roParamName);
                Object fileValue = "random_file_" + (int) (Math.random() * 100);
                ((CFile) dataObj).setFileValue(((String) fileValue).getBytes());
                String[] fileNameAndFileValue = DataObjectUtil.getFileNameAndFileValue(param.getDefaultValue());
                String filePath = evalTracker.getContext().getWorkingDirectory() + File.separator + "relation_" + param.getRelation().getRelAlias() + File.separator + fileNameAndFileValue [0];
                ((CFile) context.getDataObject(param.getQualifiedName())).setFilePath(filePath);
            }

            context.notifyStatusChanged(CConstant.GREEN_STATUS, qualifiedParamName);
            //MAK: ORIG: context.notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName).getValue(), qualifiedParamName);
            context.notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName), qualifiedParamName);

            evalTracker.changeToGreenStatusAndAddToGreenList(qualifiedParamName);
        }
        evalTracker.stopWaitingBecauseOfNewGreenParameters();
        setNoRelationExecutionFailure();
        context.notifyRelationEnded(this, true);
        //evalTracker.stopWaitingBecauseOfFinishingRelation(); // should not be called when relation is executed successfullly


//        /* this code is redudant because a local relation should not be evaluated when no roParam needs to be re-evaluated.
//           Even when roParamToBeNotified is empty, we still have to notify that this relation has finished running.  */
//        if (roParamToBeNotified.isEmpty()) {
//            setStatus(CConstant.RELATION_FINISHED_STATUS);
//            context.notifyRelationEnded(relation.getRelAlias(), true);
//        }
    }

    private void executeLocalRelation() {
        CLocalRelation localRel = (CLocalRelation) relation;

        /* execute local relation script */
        String relationScript = null;
        try {
            Binding binding = new Binding();
            List paramNameList = new ArrayList();
            for (Iterator i = relation.getInputParameterNames().iterator(); i.hasNext();) {
                String paramName = (String) i.next();
                binding.setVariable(paramName, evalTracker.getContext().getDataObject(relation.getRelAlias() + "." + paramName));
                paramNameList.add(paramName);
            }
            for (Iterator i = relation.getOutputParameterNames().iterator(); i.hasNext();) {
                String paramName = (String) i.next();
                binding.setVariable(paramName, evalTracker.getContext().getDataObject(relation.getRelAlias() + "." + paramName));
                paramNameList.add(paramName);
            }
            GroovyShell shell = new GroovyShell(binding);

//            String scriptBeginsWith = "import mit.cadlab.dome3.api.*;\nimport mit.cadlab.dome3.plugin.catalog.core.dataobject.*;\nimport mit.cadlab.dome3.plugin.catalog.runtime.*;\npublic class RelationScript {\n\tEvaluationContext context = null;\n\tpublic RelationScript(EvaluationContext context) { this.context = context; }\n\tpublic CInteger number(int num) { return new CInteger(num); }\n\tpublic CReal number(double num) { return new CReal(num); }\n\tpublic CMatrix matrix(String num) { return new CMatrix(num); }\n\tpublic CVector vector(String num) { return new CVector(num); }\n\tpublic CReal pow(CReal first, double second) { return DataObjectUtil.pow(first, second); }\n\tpublic CReal pow(CInteger first, double second) { return DataObjectUtil.pow(first, second); }\n\n\tpublic void execute() {\n";
//            String scriptEndsWith = ";\n\t}\n}\n\ndef mapScript = new MappingScript(context);\nmapScript.execute();\n";
            String scriptBeginsWith = "import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;\ndef integer(str) { return new CInteger(str); }\ndef real(str) { return new CReal(str); }\ndef matrix(str) { return new CMatrix(str); }\ndef vector(str) { return new CVector(str); }\ndef pow(firstCRealOrCInteger, secondDouble) { return DataObjectUtil.pow(firstCRealOrCInteger, secondDouble); }\n\n";
            String convertedRelationScript = RuntimeUtil.convertRelationScript(paramNameList, localRel.getRelationScript());
            relationScript = scriptBeginsWith + convertedRelationScript;
            CLog.script("running relation script: \n<<beginning of the relation script>>\n" + relationScript + "\n<<end of the relation script>>");
            shell.evaluate(relationScript, "relation.groovy");
        } catch (CompilationFailedException e) {
            //speedup Clog.debug("CompilationFailedException occurred while executing a script: " + e + " / [relation script: \n" + relationScript + "]");
            setRelationExecutionFailure("groovy compilation error", e);
            context.notifyRelationEnded(this, false);
            evalTracker.stopWaitingBecauseOfFinishingRelation(false);
            CLog.info("error occurred from the following relation script: \n<<beginning of the relation script>>\n" + relationScript + "\n<<end of the relation script>>");
            return;
        } catch (groovy.lang.GroovyRuntimeException e) {
            setRelationExecutionFailure("groovy runtime error", e);
            //speedup Clog.debug("GroovyRuntimeException occurred while executing a script: " + e + " / use getExitException():Exception to access the exception");
            context.notifyRelationEnded(this, false);
            evalTracker.stopWaitingBecauseOfFinishingRelation(false);
            CLog.info("error occurred from the following relation script: \n<<beginning of the relation script>>\n" + relationScript + "\n<<end of the relation script>>");
            return;
        } catch (Exception e) {
            setRelationExecutionFailure("groovy runtime error", e);
            context.notifyRelationEnded(this, false);
            evalTracker.stopWaitingBecauseOfFinishingRelation(false);
            CLog.info("error occurred from the following relation script: \n<<beginning of the relation script>>\n" + relationScript + "\n<<end of the relation script>>");
            return;
        }

        Set roParamToBeNotified = new HashSet();
        List paramNameList = localRel.getInputParameterNames(); // local relation param name list
        for (Iterator i = paramNameList.iterator(); i.hasNext(); ) {
            String riParamName = (String) i.next();
            String qualifiedParamName = localRel.getRelAlias() + "." + riParamName;

            CParameter riParam = evalTracker.getContext().getNamingService().getParameter(qualifiedParamName);

            if (riParam.getStatus() == CConstant.GREEN_STATUS) {
                Set drivenParamNameSet = localRel.getDrivensBy(riParamName);
                roParamToBeNotified.addAll(drivenParamNameSet);
            }
        }

//        //speedup Clog.debug("*** roParamToBeNotified (before retaining) = " + roParamToBeNotified + ", this = " + this);
        /* some of affected parameters should not be notified if they does not belong to the paramNamesToBeDetermined set */
        roParamToBeNotified.retainAll(paramNamesToBeDetermined);
//        //speedup Clog.debug("*** roParamToBeNotified (after retaining) = " + roParamToBeNotified + ", this = " + this);

        /* 1. add new green parameters into the green list  */
        /* 2. update the status of relation output parameters */
        for (Iterator i = roParamToBeNotified.iterator(); i.hasNext();) {
            String roParamName = (String) i.next();
            String qualifiedParamName = localRel.getRelAlias() + "." + roParamName;

            context.notifyStatusChanged(CConstant.GREEN_STATUS, qualifiedParamName);
            //context.notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName).getValue(), qualifiedParamName);
            context.notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName), qualifiedParamName);

            evalTracker.changeToGreenStatusAndAddToGreenList(qualifiedParamName);
        }
        evalTracker.stopWaitingBecauseOfNewGreenParameters();

        setNoRelationExecutionFailure();
        context.notifyRelationEnded(this, true);
        evalTracker.stopWaitingBecauseOfFinishingRelation(true); // should not be called when relation is executed successfullly

//        /* this code is redudant because a local relation should not be evaluated when no roParam needs to be re-evaluated.
//           Even when roParamToBeNotified is empty, we still have to notify that this relation has finished running.  */
//        if (roParamToBeNotified.isEmpty()) {
//            setStatus(CConstant.RELATION_FINISHED_STATUS);
//            context.notifyRelationEnded(relation.getRelAlias(), true);
//        }
    }

    private void simulateRemoteRelation() {
        EvaluationContext context = evalTracker.getContext();

        CRemoteRelation remoteRel = (CRemoteRelation) relation;

        List iparamNames = remoteRel.getInputParameterNames();
        List oparamNames = remoteRel.getOutputParameterNames();

        CNamingService namingService = remoteRel.getNamingService();

        try {
            CLog.log("simulating remote relation connection");
            sleep(REMOTE_RELATION_SIMULATED_LOADING_TIME);
        } catch (InterruptedException e) { }


//        the below code is not commented because the value is not updated by the value listener in the simulation mode, RemoteRelationListener
        for (Iterator i = oparamNames.iterator(); i.hasNext();) {
            String paramName = (String) i.next();

            try {
                CLog.log("simulating output param updates");
                sleep(REMOTE_RELATION_SIMULATED_PARAMETER_UPDATE_TIME);
            } catch (InterruptedException e) { }

            CRelationOutputParameter param = namingService.getRelationOutputParameter(remoteRel.getRelAlias() + "." + paramName);
            String qualifiedParamName = param.getQualifiedName();
            if (CConstant.REAL_DATA_TYPE.equals(param.getDataType())) {
                ((CReal) context.getDataObject(param.getQualifiedName())).setDoubleValue(Math.random() * 100);
            } else if (CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                ((CInteger) context.getDataObject(param.getQualifiedName())).setIntegerValue((int) (Math.random() * 100));
            } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                ((CString) context.getDataObject(param.getQualifiedName())).setStringValue("random_string_" + (int) (Math.random() * 100));
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                ((CBoolean) context.getDataObject(param.getQualifiedName())).setBooleanValue(Math.random() > 0.5);
            } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                List enumList = ((CEnumeration) context.getDataObject(param.getQualifiedName())).getEnumList();
                int randomSelectedIdx = (int) Math.random() * enumList.size();
                DataObjectUtil.setSelectedIndex(randomSelectedIdx, enumList);
                ((CEnumeration) context.getDataObject(param.getQualifiedName())).setEnumList(enumList);
            } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                Object fileValue = "random_file_" + (int) (Math.random() * 100);
                ((CFile) context.getDataObject(param.getQualifiedName())).setFileValue(((String) fileValue).getBytes());
                String[] fileNameAndFileValue = DataObjectUtil.getFileNameAndFileValue(param.getDefaultValue());
                String filePath = evalTracker.getContext().getWorkingDirectory() + File.separator + "relation_" + param.getRelation().getRelAlias() + File.separator + fileNameAndFileValue [0];
                ((CFile) context.getDataObject(param.getQualifiedName())).setFilePath(filePath);
            }  else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                ((CVector) context.getDataObject(param.getQualifiedName())).setVectorValue("[" + Math.random() * 100 + " " + Math.random() * 100 + "]");
            } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                ((CMatrix) context.getDataObject(param.getQualifiedName())).setMatrixValue("[" + Math.random() * 100 + " " + Math.random() * 100 + "; " + Math.random() * 100 + " " + Math.random() * 100 + "]");
            }

            context.notifyStatusChanged(CConstant.GREEN_STATUS, qualifiedParamName);
            //MAK: ORIG: context.notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName).getValue(), qualifiedParamName);
            context.notifyValueChanged(evalTracker.getContext().getDataObject(qualifiedParamName), qualifiedParamName);

            /* add a parameter to the green list only when it belongs to the paramNamesToBeDetermined set */
            if (paramNamesToBeDetermined.contains(paramName)) {
                evalTracker.changeToGreenStatusAndAddToGreenList(qualifiedParamName);
            }
        }
        evalTracker.stopWaitingBecauseOfNewGreenParameters();

        /* notify that this relation has finished running and check if model evaluation is good to finish */
        setNoRelationExecutionFailure();
        context.notifyRelationEnded(this, true);
        // evalTracker.stopWaitingBecauseOfFinishingRelation(); // should not be called when relation is executed successfullly

//        /* this code is redudant because a local relation should not be evaluated when no roParam needs to be re-evaluated.
//           Even when roParamToBeNotified is empty, we still have to notify that this relation has finished running.  */
//        if (roParamToBeNotified.isEmpty()) {
//            setStatus(CConstant.RELATION_FINISHED_STATUS);
//            context.notifyRelationEnded(relation.getRelAlias(), true);
//        }
    }

    /** returns a RemoteRelationListener instance which is not null when this RelationTracker is tracking the execution of CRemoteRelation */
    public RemoteRelationListener getRemoteRelationListener() {
        return remoteRelListener;
    }

    private void executeRemoteRelation() {
        EvaluationContext context = evalTracker.getContext();

        CRemoteRelation remoteRel = (CRemoteRelation) relation;

        List iparamNames = remoteRel.getInputParameterNames();
        List oparamNames = remoteRel.getOutputParameterNames();

        DomeConnection conn = null;
        try {
            conn = context.getConnection(remoteRel.getUser(), remoteRel.getPassword(), remoteRel.getServerPort());
            if (conn == null) {
                setRelationExecutionFailure("fail to login to DOME server: server=" + remoteRel.getServerPort() + ", user=" + remoteRel.getUser(), null);
                context.notifyRelationEnded(this, false);
                return;
                //throw new RuntimeException("fail to get Dome Connection: server=" + remoteRel.getServerPort() + ", user=" + remoteRel.getUser());
            }
        } catch (RuntimeException e) {
            setRelationExecutionFailure("fail to login to DOME server: server=" + remoteRel.getServerPort() + ", user=" + remoteRel.getUser(), null);
            context.notifyRelationEnded(this, false);
            return;
        }

        RuntimeInterface runtimeInterface = null;
        try {
            runtimeInterface = context.getRuntimeInterface(conn, remoteRel.getSpace(), remoteRel.getInterfacePath(), remoteRel.getRelAlias(), trackerId);
        } catch (Exception e) { /* this exception implies invalid path */ }
        if (runtimeInterface == null) {
            setRelationExecutionFailure("fail to locate runtime interface: space=" + remoteRel.getSpace() + ", path=" + remoteRel.getInterfacePath() + ", relAlias=" + remoteRel.getRelAlias(), null);
            context.notifyRelationEnded(this, false);
            return;
            //throw new RuntimeException("fail to get Runtime Interface:
        }

        remoteRelListener = new RemoteRelationListener(this, context, runtimeInterface);
        runtimeInterface.clearValueAndStatusChangeListener();
        runtimeInterface.addParameterValueChangeListener(remoteRelListener);
        runtimeInterface.addParameterStatusChangeListener(remoteRelListener);

        CNamingService namingService = remoteRel.getNamingService();

        boolean hasFileParam = false;
        for (Iterator j = iparamNames.iterator(); j.hasNext();) {
            String paramName = (String) j.next();
            CRelationInputParameter param = namingService.getRelationInputParameter(remoteRel.getRelAlias() + "." + paramName);
            if (CConstant.REAL_DATA_TYPE.equals(param.getDataType())) {
                runtimeInterface.getParameterByName(paramName).setRealValue(((CReal) context.getDataObject(param.getQualifiedName())).getDoubleValue());
            } else if (CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                runtimeInterface.getParameterByName(paramName).setIntegerValue(((CInteger) context.getDataObject(param.getQualifiedName())).getIntegerValue());
            } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                runtimeInterface.getParameterByName(paramName).setStringValue(((CString) context.getDataObject(param.getQualifiedName())).getStringValue());
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                runtimeInterface.getParameterByName(paramName).setBooleanValue(((CBoolean) context.getDataObject(param.getQualifiedName())).getBooleanValue());
            } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                runtimeInterface.getParameterByName(paramName).setEnumerationValue(((CEnumeration) context.getDataObject(param.getQualifiedName())).getSelectedIndex());
            } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                hasFileParam = true;
                Object fileValueObj = ((CFile) context.getDataObject(param.getQualifiedName())).getFileValue();
                String fileValueStr = null;
                if (fileValueObj instanceof byte[])
                	runtimeInterface.getParameterByName(paramName).setFileValue( (byte[]) fileValueObj);
                else if (fileValueObj instanceof URL)
                	runtimeInterface.getParameterByName(paramName).setFileValue( ((URL) fileValueObj).toString());
                //runtimeInterface.getParameterByName(paramName).setFileValue(((CFile) context.getDataObject(param.getQualifiedName())).getFileValue());
            } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                List numList = ((CVector) context.getDataObject(param.getQualifiedName())).getVectorValue();
                runtimeInterface.getParameterByName(paramName).setVectorValue(new Vector(numList));
            } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                List rowList = ((CMatrix) context.getDataObject(param.getQualifiedName())).getMatrixValue();
                runtimeInterface.getParameterByName(paramName).setMatrixValue(new Vector(rowList));
            }
        }

        /* check output params for File typed one */
        for (Iterator j = oparamNames.iterator(); j.hasNext();) {
            String paramName = (String) j.next();
            CRelationOutputParameter param = namingService.getRelationOutputParameter(remoteRel.getRelAlias() + "." + paramName);
            if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                hasFileParam = true;
            }
        }

        if (hasFileParam) {
            /* set up a download folder if there are File parameters in this relation */
            //String downloadFolderStr = evalTracker.getContext().getWorkingDirectory() + File.separator + "relation_" + remoteRel.getRelAlias() + "_" + this.getTrackerId();
            String downloadFolderStr = evalTracker.getContext().getWorkingDirectory() + File.separator + "relation_" + remoteRel.getRelAlias();
            downloadFolderStr = downloadFolderStr.replace('\\', '/');

            /* create a download folder named like "relation_A" under the working directory of current model runtime */
            File downloadFolder = new File(downloadFolderStr);
            if (! downloadFolder.exists()) {
                downloadFolder.mkdirs();

            }
            runtimeInterface.setDownloadFolder(downloadFolderStr);
            CLog.info("files will be downloaded to '" + downloadFolder + "' In order to change the download folder, please use EvaluationContext.setWorkingDirectory().");
        }

        //speedup Clog.debug("start executing remote interface: " + runtimeInterface.getDomeInterface().getInterfaceName());
        try {
            CLog.info("Start solving parameter changes of \"" + runtimeInterface.getDomeInterface().getParentName() + " for \"" + context.getNamingService().getImplementation().getParentInterface().getParentModel().getName() + "\"");
            runtimeInterface.submit();
            CLog.info("Finish solving parameter changes of \"" + runtimeInterface.getDomeInterface().getParentName() + " for \"" + context.getNamingService().getImplementation().getParentInterface().getParentModel().getName() + "\"");
        } catch (RuntimeException e) {
            this.setRelationExecutionFailure("fail to execute remote relation", new RuntimeException("Fail to execute remote relation aliased as " + this.getRelAlias() + " : please see the server-side log for details"));
            context.notifyRelationEnded(this, false);
            context.getEvaluationTracker().stopWaitingBecauseOfFinishingRelation(false);
        }

        // if no exception is thrown at submit(), the method call is considered as a successful one.
        this.setNoRelationExecutionFailure();
        context.notifyRelationEnded(this, true);
        context.getEvaluationTracker().stopWaitingBecauseOfFinishingRelation(true);
    }

    public String toString() {
        String statusStr = "ready";
        if (status == CConstant.RELATION_RUNNING_STATUS) {
            statusStr = "running";
        } else if (status == CConstant.RELATION_FINISHED_STATUS) {
            statusStr = "finished";
        }

        return "[RelationTracker: relAlias=" + relation.getRelAlias() + ", trackerId=" + trackerId + ", status=" + statusStr + ", evalTime=" + getEvaluationTime() + ", paramToDetermine=" + paramNamesToBeDetermined + "]";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationTracker)) return false;

        final RelationTracker relationTracker = (RelationTracker) o;

        if (trackerId != relationTracker.trackerId) return false;
        if (! this.getRelAlias().equals(relationTracker.getRelAlias())) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = relation.getRelAlias().hashCode();
        result = 29 * result + trackerId;
        return result;
    }
}

class RemoteRelationListener implements ParameterValueChangeListener, ParameterStatusChangeListener {
    RelationTracker relTracker;
    EvaluationContext context;
    RuntimeInterface runtimeInterface;
//    String VALUE_CHANGE_LOCK = "VALUE_CHANGE_LOCK";
//    String STATUS_CHANGE_LOCK = "STATUS_CHANGE_LOCK";
//    String PROCESS_VALUE_CHANGE_EVENT_LOCK = "PROCESS_VALUE_CHANGE_EVENT_LOCK";

    public RemoteRelationListener(RelationTracker relTracker, EvaluationContext context, RuntimeInterface runtimeInterface) {
        this.context = context;
        this.relTracker = relTracker;
        this.runtimeInterface = runtimeInterface;
    }

    public String toString() {
        return "[RemoteRelationListener: relAlias=" + relTracker.getRelAlias() + "]";
    }

    public void valueChanged(ParameterValueChangeEvent event) {
//        synchronized (VALUE_CHANGE_LOCK) {
            /* mark that it starts processing the value change event.
             * because this method is synchronized with VALUE_CHANGE_LOCK,
             * only one value change event can be processed at a time,
             * which as a result enables relTracker.processingValueChangeEvent
             * to represent if any value change event is being processed or not. */
//            relTracker.processingValueChangeEvent = true;

        String relAlias = relTracker.getRelAlias();
        String paramName = event.getInterfaceParameterClient().getName();
        String qualifiedParamName = relAlias + "." + paramName;
        CParameter param = context.getNamingService().getParameter(relAlias + "." + paramName);

        /* update the CDataObejct value based on RuntimeParameter value and add the updated parameter to the green list
         * only when it belongs to the paramNamesToBeDetermined set */
        if (relTracker.getParameterNamesToBeDetermined().contains(paramName)) {
            if (! param.isConsistent()) {
                /* the value change event updates the value of data object */
                if (CConstant.REAL_DATA_TYPE.equals(param.getDataType())) {
                    ((CReal) context.getDataObject(param.getQualifiedName())).setDoubleValue(runtimeInterface.getParameterByName(paramName).getRealValue());
                } else if (CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                    ((CInteger) context.getDataObject(param.getQualifiedName())).setIntegerValue(runtimeInterface.getParameterByName(paramName).getIntegerValue());
                } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                    ((CString) context.getDataObject(param.getQualifiedName())).setStringValue(runtimeInterface.getParameterByName(paramName).getStringValue());
                } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                    ((CBoolean) context.getDataObject(param.getQualifiedName())).setBooleanValue(runtimeInterface.getParameterByName(paramName).getBooleanValue());
                } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                    ((CEnumeration) context.getDataObject(param.getQualifiedName())).setEnumList(runtimeInterface.getParameterByName(paramName).getEnumerationList());
                } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                    Object fileValue = runtimeInterface.getParameterByName(paramName).getFileValue();
                    if (fileValue instanceof byte[]) {
                        CFile fileDataObj = (CFile) context.getDataObject(param.getQualifiedName());
                        DomeFile domeFile = (DomeFile) event.getInterfaceParameterClient().getCurrentDataObject();
                        fileDataObj.setFilePath(domeFile.getFilePath());
                        fileDataObj.setFileValue((byte[]) fileValue);
                    } else {
                        CFile fileDataObj = (CFile) context.getDataObject(param.getQualifiedName());
                        DomeFile domeFile = (DomeFile) event.getInterfaceParameterClient().getCurrentDataObject();
                        fileDataObj.setFilePath(domeFile.getFilePath());
                        fileDataObj.setFileValue(((String) fileValue).getBytes());
                    }
                } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                    ((CVector) context.getDataObject(param.getQualifiedName())).setVectorValue(runtimeInterface.getParameterByName(paramName).getVectorValue());
                } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                    ((CMatrix) context.getDataObject(param.getQualifiedName())).setMatrixValue(runtimeInterface.getParameterByName(paramName).getMatrixValue());
                }

                //MAK: ORIG: context.notifyValueChanged(context.getDataObject(qualifiedParamName).getValue(), qualifiedParamName);
                context.notifyValueChanged(context.getDataObject(qualifiedParamName), qualifiedParamName);
                context.getEvaluationTracker().changeToGreenStatusAndAddToGreenList(qualifiedParamName);

                /* instead of using the statusChanged event,
                 * we use valueChanged event to determine the point
                 * when the status of a param is set to be green. */
                context.notifyStatusChanged(CConstant.GREEN_STATUS, qualifiedParamName);
                context.getEvaluationTracker().stopWaitingBecauseOfNewGreenParameters();
            } else {
                /* monitor if the execution of this remote relation modifies any value of data object that is not supposed to be modified */
                if (CConstant.REAL_DATA_TYPE.equals(param.getDataType())) {
                    double curDataObjValue = ((CReal) context.getDataObject(param.getQualifiedName())).getDoubleValue();
                    if (runtimeInterface.getParameterByName(paramName).getRealValue() != curDataObjValue) {
                        double diff = runtimeInterface.getParameterByName(paramName).getRealValue() - curDataObjValue;
                        double IGNORABLE_DIFFERENCE = curDataObjValue * 0.000001;
                        if (Math.abs(diff) < IGNORABLE_DIFFERENCE) {
                            // considered as no value change
                            CLog.info("Parameter '" + paramName + "' has been changed by " + diff + ", but the current execution will continue solving because the change is considered ignorable.");
                        } else {
                            handleIllegalValueChange(param, relTracker);
                        }
                    }
                } else if (CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                    int curDataObjValue = ((CInteger) context.getDataObject(param.getQualifiedName())).getIntegerValue();
                    if (runtimeInterface.getParameterByName(paramName).getIntegerValue() != curDataObjValue) {
                        handleIllegalValueChange(param, relTracker);
                    }
                } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                    String curDataObjValue = ((CString) context.getDataObject(param.getQualifiedName())).getStringValue();
                    if (! runtimeInterface.getParameterByName(paramName).getStringValue().equals(curDataObjValue)) {
                        handleIllegalValueChange(param, relTracker);
                    }
                } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                    boolean curDataObjValue = ((CBoolean) context.getDataObject(param.getQualifiedName())).getBooleanValue();
                    if (runtimeInterface.getParameterByName(paramName).getBooleanValue() != curDataObjValue) {
                        handleIllegalValueChange(param, relTracker);
                    }
                } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                    ((CEnumeration) context.getDataObject(param.getQualifiedName())).setEnumList(runtimeInterface.getParameterByName(paramName).getEnumerationList());
                    List curDataObjValue = ((CEnumeration) context.getDataObject(param.getQualifiedName())).getEnumList();
                    int curDataObjSelectedIdx = DataObjectUtil.getSelectedIndex(curDataObjValue);
                    String curDataObjEnumName = DataObjectUtil.getEnumName(curDataObjValue, curDataObjSelectedIdx);

                    List rtItfDataObjValue = runtimeInterface.getParameterByName(paramName).getEnumerationList();
                    int rtItfDataObjSelectedIdx = DataObjectUtil.getSelectedIndex(rtItfDataObjValue);
                    String rtItfDataObjEnumName = DataObjectUtil.getEnumName(rtItfDataObjValue, rtItfDataObjSelectedIdx);
                    if (! rtItfDataObjEnumName.equals(curDataObjEnumName)) {
                        handleIllegalValueChange(param, relTracker);
                    }
                } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                    Object fileValue = runtimeInterface.getParameterByName(paramName).getFileValue();
                    if (fileValue instanceof byte[]) {
                        CFile fileDataObj = (CFile) context.getDataObject(param.getQualifiedName());
                        if (! fileDataObj.getFileValue().equals(fileValue)) {
                            handleIllegalValueChange(param, relTracker);
                        }
                    } else {
                        CFile fileDataObj = (CFile) context.getDataObject(param.getQualifiedName());
                        DomeFile domeFile = (DomeFile) event.getInterfaceParameterClient().getCurrentDataObject();
                        if (! fileDataObj.getFileValue().equals(((String) fileValue).getBytes())) {
                            handleIllegalValueChange(param, relTracker);
                        }
                    }
//                    /* note that file path is okay to be (=should be) different because each runtime interface will use different the download folder. below code is not necessary. */
//                    DomeFile domeFile = (DomeFile) event.getInterfaceParameterClient().getCurrentDataObject();
//                    if (! fileDataObj.getFilePath().equals(domeFile.getFilePath())) {
//                        handleIllegalValueChange(param, relTracker);
//                    }
                } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                    List curDataObjValue = ((CVector) context.getDataObject(param.getQualifiedName())).getVectorValue();
                    List rtItfDataObjValue = runtimeInterface.getParameterByName(paramName).getVectorValue();

                    if (! DataObjectUtil.isEqualVectorValue(rtItfDataObjValue, curDataObjValue)) {
                        handleIllegalValueChange(param, relTracker);
                    }
                } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                    List curDataObjValue = ((CMatrix) context.getDataObject(param.getQualifiedName())).getMatrixValue();
                    List rtItfDataObjValue = runtimeInterface.getParameterByName(paramName).getMatrixValue();

                    if (! DataObjectUtil.isEqualMatrixValue(rtItfDataObjValue, curDataObjValue)) {
                        handleIllegalValueChange(param, relTracker);
                    }
                }
                context.getEvaluationTracker().stopWaitingBecauseOfConfirmedGreenParameters();
            }
        }


//            /* mark that it has finished processing the value change event */
//            relTracker.processingValueChangeEvent = false;
////            synchronized (PROCESS_VALUE_CHANGE_EVENT_LOCK) {
////                PROCESS_VALUE_CHANGE_EVENT_LOCK.notifyAll();
////            }
//        }
    }

    private void handleIllegalValueChange(CParameter param, RelationTracker relTracker) {
        throw new RuntimeException("To-be-unaffected parameter '" + param.getQualifiedName() + "' has been changed as a result of executing a relation assocated with " + relTracker);
    }

    public void statusChanged(ParameterStatusChangeEvent event) {
//        synchronized (STATUS_CHANGE_LOCK) {
//            String relAlias = relTracker.getRelAlias();
//            if (ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT.equals(event)) {
//                // todo: is below lock necessary. no solving have been helped by this block so far.
//                if (relTracker.processingValueChangeEvent) {
////                    synchronized (PROCESS_VALUE_CHANGE_EVENT_LOCK) {
////                        try {
////                            PROCESS_VALUE_CHANGE_EVENT_LOCK.wait();
////                        } catch (InterruptedException e) { }
////                    }
//                }
//                relTracker.setNoRelationExecutionFailure();
//                context.notifyRelationEnded(relTracker, true);
//                context.getEvaluationTracker().stopWaitingBecauseOfFinishingRelation(true);
//
//                /* in a case when a relation-finished-successfully-event comes at the end of an evaluation,
//                 * it should initiate a check loop (by invoking stop-waiting method) to see if the current evaluation is good to finish */
//                //System.out.println("this is the point where stop-waiting-because-of-finishing-rel could be suppressed");
//                Set qualifiedParamNamesToBeDetermined = CNamingService.convertToQualified(relTracker.getRelAlias(), relTracker.getParameterNamesToBeDetermined());
//                if (context.getEvaluationTracker().greenListContains(qualifiedParamNamesToBeDetermined)) {
//                    //System.out.println("but it was not suppressed.");
//                    //todo: research if below line is necessary
//                    context.getEvaluationTracker().stopWaitingBecauseOfFinishingRelation(true);
//                } else {
//                    //System.out.println("and it was suppressed.");
//                }
//            } else if (ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT.equals(event)) {
//                relTracker.setRelationExecutionFailure("fail to execute remote relation", new RuntimeException("Fail to execute remote relation aliased as " + relAlias + " : please see the server-side log for details"));
//                context.notifyRelationEnded(relTracker, false);
//                context.getEvaluationTracker().stopWaitingBecauseOfFinishingRelation(false);
//            } else {
//                String paramName = event.getInterfaceParameterClient().getName();
//                String qualifiedParamName = relAlias + "." + paramName;
//
//            /* It is a matter of policy on when to allow other relations use the value of this relation.
//               I chose to make it available as soon as its value is turned to green (VALUE_STATUS_WAITING_VALIDATION)
//               so the if-clause of VALUE_STATUS_CONSISTENT is commented and instead if-clause of VALUE_STATUS_WAITING_VALIDATION is not commented. */
//    //            if (Parameter.VALUE_STATUS_CONSISTENT.equals(event.getNewStatus())) {
//
//
//                /* below status notifcation code has been commented because now status notification is done when valueChanged event is received */
//                /* we don't have to notify that white status to green. one exception is when current status is white and new status is green. */
//    //            if (context.getNamingService().getParameter(qualifiedParamName).getStatus() == CConstant.WHITE_STATUS) {
//    //                return;
//    //            }
//    //
//    //            if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(event.getNewStatus())) {
//    //                //execTracker.changeToGreenStatusAndAddToGreenList(qualifiedParamName);
//    //                context.notifyStatusChanged(CConstant.GREEN_STATUS, qualifiedParamName);
//    //            }
//
//    //            if (Parameter.VALUE_STATUS_CONSISTENT.equals(event.getNewStatus())) {
//    //                if ("relC.actualModulus".equals(qualifiedParamName)) {
//    //                    //speedup Clog.debug("## FFFFFFFFFFFFFFFFFFF ## " + qualifiedParamName + " is consistent!!!!");
//    //                }
//    //            }
//
//    //              else if (Parameter.VALUE_STATUS_STALE.equals(event.getNewStatus())) {
//    //                newColor = YELLOW_BG;
//    //            } else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(event.getNewStatus())) {
//    //                newColor = RED_BG;
//    //            }
//            }
//        }
    }

    public RuntimeInterface getRuntimeInterface() {
        return runtimeInterface;
    }

}
