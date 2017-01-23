/*
 * CatalogPlugin.java
 *
 * Created on August 19, 2005, 4:23 PM
 *
 */

package mit.cadlab.dome3.plugin.catalog;

import groovy.lang.GroovyShell;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationListener;

import java.util.*;
import java.io.File;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 6.
 */
public class CatalogPlugin extends AbstractPlugin {
//    GroovyScriptEngine gse;
    GroovyShell shell;
    //Binding binding;
    Vector data;
    CatalogModelRuntime modelRuntime;
    private boolean isLoaded = false;
    /** implName -> paramMap */
    //private Map implToParamMap;
    private Map implToEvalContext;

    private CImplementation impl = null;

    public CatalogPlugin(CatalogModelRuntime modelRuntime) {
        this.modelRuntime = modelRuntime;
        data = new Vector();
    }

    public void createModel() {

        // no initialization needed
        Debug.trace(Debug.ALL, "catalog model has been created");
    }

    public void deleteModel() {
        /* closing a connection will automatically close all runtime interfaces associated with the connection. */
        if (implToEvalContext != null) {
            for (Iterator i = implToEvalContext.values().iterator(); i.hasNext(); ) {
                EvaluationContext evalContext = (EvaluationContext) i.next();
                evalContext.close();
            }
        }

        Debug.trace(Debug.ALL, "catalog model '" + modelRuntime.getCatalogModel().getName() + "' has been deleted");
    }

    public void loadModel() {
//        Debug.trace(Debug.ALL, "start loading catalog model");
//            String[] roots = new String[] { groovyFile.getParent() };
//            gse = new GroovyScriptEngine(roots);

        /* create a new binding for this execution */
        //binding = new Binding();
        implToEvalContext = new HashMap();
//        implToConnectionMap = new HashMap();
//        implToRuntimeInterfaceMap = new HashMap();

//        try {
//            String[] roots = new String[] { "c:/" };
//            gse = new GroovyScriptEngine(roots);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("fail to load groovy model: " + e);
//        }

//        Debug.trace(Debug.ALL, "catalog model is loaded successfully");
        isLoaded = true;
    }

    public synchronized void execute(List affectedOutputParams) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = affectedOutputParams.iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            sb.append(param.getName());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }

        if (affectedOutputParams.size() == 0) {
            Debug.trace(Debug.ALL, "[CATALOG PLUGIN] No need for running \"" + impl.getParentInterface().getParentModel().getName() + "\": no affected parameters found");
            return;
        }

        /* find selected interface name */
        String selectedImplName = null;
        String selectedItfName = null;
        String firstAffectedParamName = ((Parameter) affectedOutputParams.get(0)).getName(); // expected to be a form of itfA/param X
        if (firstAffectedParamName.indexOf("/") != -1) {
            selectedItfName = firstAffectedParamName.substring(0, firstAffectedParamName.indexOf("/"));
        } else {
            throw new RuntimeException("invalid parameter name or no parameter");
        }

        /* 1. find selected implmementation name
         * 2. create a set of modified parameter names
         * 3. create an evaluation context */
        Set modifiedParamNames = new HashSet();
        EvaluationContext evalContext = null;
        for (int i = 0; i < data.size(); i++) {
            CDataObject catDataObj = (CDataObject) data.get(i);
            String pluginParamName = catDataObj.getDomePluginParameter().getName();

            if (pluginParamName.equals(selectedItfName + "/" + CConstant.IMPL_SWITCH)) {

                catDataObj.getDomePluginParameter().getCurrentDataObject().getValues();

                catDataObj.copyFromDomePluginDataObjectToCatalogDataObject();
                List enumList = ((CEnumeration) catDataObj).getEnumList();
                int selectedIdx = DataObjectUtil.getSelectedIndex(enumList);

                if (selectedIdx == -1) {
                    throw new RuntimeException("implementation switch not selected when executing an interface named as '" + selectedItfName + "'");
                    //selectedIdx = 0; // assume that user selected the first item of the implementations
                }

                selectedImplName = DataObjectUtil.getEnumName(enumList, selectedIdx);

                System.out.println("currently selected impl name = " + selectedImplName);

                impl = modelRuntime.getCatalogModel().getInterface(selectedItfName).getImplementation(selectedImplName);

                if (impl == null) {
                    throw new RuntimeException("implementation not selected: selectedItfName=" + selectedItfName + ", implName=" + selectedImplName);
                } else {
//                    Debug.trace(Debug.ALL, "found implementation: " + impl);
                }

                evalContext = (EvaluationContext) implToEvalContext.get(selectedItfName + "|" + selectedImplName);
                if (evalContext == null) {
                    evalContext = new EvaluationContext(impl);
                    evalContext.addEvaluationListener(new EvaluationListenerForCatalogPluginModel(evalContext));
                    evalContext.setWorkingDirectory(modelRuntime.getWorkingDirectory().getAbsolutePath());
                    implToEvalContext.put(selectedItfName + "|" + selectedImplName, evalContext);
                }

                catDataObj.copyFromDomePluginDataObjectToCatalogDataObject();
                evalContext.putInterfaceDataObject(CConstant.IMPL_SWITCH, catDataObj);

                /* set isImplSwitchChanged by checking parameter status of IMPL_SWITCH */
                if (! Parameter.VALUE_STATUS_CONSISTENT.equals(catDataObj.getDomePluginParameter().getValueStatus())) {
                    modifiedParamNames.add(CConstant.IMPL_SWITCH);
                }

                break;
            }
        }

        /* 2. create a set of modified parameter names */
        for (int i = 0; i < data.size(); i++) {
            CDataObject catDataObj = (CDataObject) data.get(i);
            String pluginParamName = catDataObj.getDomePluginParameter().getName();

            if (pluginParamName.startsWith(selectedItfName + "/")) {
                /* put both itf input param and itf output param into evaluation context */
                int itfNameEndsAt = (selectedItfName + "/").length();
                String localParamName = pluginParamName.substring(itfNameEndsAt);
                evalContext.putInterfaceDataObject(localParamName, catDataObj);

                /* update the value of both itf input param and itf output param so that they are the same as the plugin data object */
                catDataObj.copyFromDomePluginDataObjectToCatalogDataObject();

                /* find inconsistant itf input params and add them to a modified param set */
                if (catDataObj.isAssociatedWithInterfaceInputParameter() && ! Parameter.VALUE_STATUS_CONSISTENT.equals(catDataObj.getDomePluginParameter().getValueStatus())) {
                    modifiedParamNames.add(localParamName);
                }

                /* this condition is necessary for a file-type interface parameter because its CFile object has null for file path.
                 * the file path needs to be updated at this moment because now we know its working directory */
                if (catDataObj instanceof CFile) {
                    CFile fileObj = (CFile) catDataObj;
                    CParameter param = evalContext.getNamingService().getParameter(CConstant.ITF_ALIAS + "." + localParamName);
                    String workingDirPath = evalContext.getWorkingDirectory();
                    String[] fileNameAndFileValue = DataObjectUtil.getFileNameAndFileValue(param.getDefaultValue());
                    String subFolderName = "interface";
                    String subWorkFolderPath = workingDirPath + File.separator + subFolderName;
                    subWorkFolderPath = subWorkFolderPath.replace('\\', '/');
                    File subWorkFolder = new File(subWorkFolderPath);
                    if (! subWorkFolder.exists()) {
                        subWorkFolder.mkdir();
                    }
                    String filePath = subWorkFolderPath + File.separator + fileNameAndFileValue [0];
                    filePath = filePath.replace('\\', '/');
                    fileObj.setFilePath(filePath);
                }
            }
        }

//        Debug.trace(Debug.ALL, "evaluation context: " + evalContext);
//        Debug.trace(Debug.ALL, "modified parameter names: " + modifiedParamNames);

        Debug.trace(Debug.ALL, "[CATALOG PLUGIN] Running for \"" + impl.getParentInterface().getParentModel().getName() + "\" (implementation: " + selectedImplName + ", interface: " + selectedItfName + ")\n\tmodified params: " + modifiedParamNames + "\n\taffected params: [" + sb.toString() + "]");

        evalContext.evaluate(modifiedParamNames);

        Debug.trace(Debug.ALL, "[CATALOG PLUGIN] Done for \"" + impl.getParentInterface().getParentModel().getName() + "\" (implementation: " + selectedImplName + ", interface: " + selectedItfName + ")");
    }

    public void executeBeforeInput() {
        /* should ask wei about what is done in C++ side */
    }

    public void executeAfterOutput() {
        /* should ask wei about what is done in C++ side */
    }

    public void unloadModel() {
        /* should ask wei about what is done in C++ side */
    }

    public CReal createReal(Parameter realParam) {
        CReal real = new CReal(realParam);
        data.addElement(real);
        return real;
    }

    public CInteger createInteger(Parameter intParam) {
        CInteger cint = new CInteger(intParam);
        data.addElement(cint);
        return cint;
    }

    public CString createString(Parameter strParam) {
        CString str = new CString(strParam);
        data.addElement(str);
        return str;
    }

    public CBoolean createBoolean(Parameter boolParam) {
        CBoolean str = new CBoolean(boolParam);
        data.addElement(str);
        return str;
    }

    public CEnumeration createEnumeration(Parameter enumParam) {
        CEnumeration enm =  new CEnumeration(enumParam);
        data.addElement(enm);
        return enm;
    }

    public CFile createFile(Parameter fileParam) {
        CFile file = new CFile(fileParam);
        data.addElement(file);
        return file;
    }

    public CMatrix createMatrix(Parameter matrixParam) {
        CMatrix matrix = new CMatrix(matrixParam);
        data.addElement(matrix);
        return matrix;
    }

    public CVector createVector(Parameter vectorParam) {
        CVector vector = new CVector(vectorParam);
        data.addElement(vector);
        return vector;
    }

    public boolean isModelLoaded() {
        return isLoaded;
    }
}

class EvaluationListenerForCatalogPluginModel implements EvaluationListener {
    EvaluationContext evalContext;

    public EvaluationListenerForCatalogPluginModel(EvaluationContext evalContext) {
        this.evalContext = evalContext;
    }

    public void mappingEvaluated(String qualifiedParamName) { }

    public void evaluationStarted() { }

    public void evaluationEnded(boolean isSuccess) {
//        Set testedIoParamSet = evalContext.getNamingService().getInterfaceOutputParameters();
//        for (Iterator i = testedIoParamSet.iterator(); i.hasNext();) {
//            CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) i.next();
//            String qualifiedParamName = ioParam.getQualifiedName();
//            System.out.println("status of " + qualifiedParamName + " = " + evalContext.getDataObject(qualifiedParamName).getDomePluginParameter().getValueStatus());
//        }

        if (isSuccess) {
            Set ioParamSet = evalContext.getNamingService().getInterfaceOutputParameters();
            for (Iterator i = ioParamSet.iterator(); i.hasNext();) {
                CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) i.next();
                String qualifiedParamName = ioParam.getQualifiedName();
                if (Parameter.VALUE_STATUS_INCONSISTENT.equals(evalContext.getDataObject(qualifiedParamName).getDomePluginParameter().getValueStatus())) {
                    //speedup Clog.debug("[catalog plugin listener] Evaluataion ended. Some of plugin data objects are not status-synchronized with catalog data objects. Their status is set CONSISTENT at the end of evaluation: paramName=" + qualifiedParamName);
                    evalContext.getDataObject(qualifiedParamName).copyFromCatalogDataObjectToDomePluginDataObject();
                }
            }
        }
    }

    public void relationStarted(String relAlias) {
        //speedup Clog.debug("[catalog plugin listener] Relation started: relAlias=" + relAlias);
    }

    public void relationEnded(String relAlias, boolean isSuccess) {
        //speedup Clog.debug("[catalog plugin listener] Relation ended: relAlias=" + relAlias + ", isSuccess=" + isSuccess);
    }

    public void parameterStatusChanged(int newStatus, String qualifiedParamName) {
//        String statusStr = "white";
//        if (newStatus == CConstant.GREEN_STATUS) {
//            statusStr = "green";
//        } else if (newStatus == CConstant.RED_STATUS) {
//            statusStr = "red";
//        }
//
//        if (qualifiedParamName.startsWith(CConstant.ITF_ALIAS + ".")) {
//            ////speedup Clog.debug("[catalog plugin listener] Value changed. Synchronize the value of catalog data object with that of plugin data object: paramName=" + qualifiedParamName + ", newValue=" + newValue);
//            System.out.println("status updating ... " + qualifiedParamName + " : " + statusStr);
//        }

        //speedup Clog.debug("[catalog plugin listener] Status changed: newStatus=" + statusStr + ", paramName=" + qualifiedParamName);

//        if (evalContext.getNamingService().getParameter(qualifiedParamName) instanceof CInterfaceOutputParameter && newStatus == CConstant.GREEN_STATUS) {
//            //speedup Clog.debug("[catalog plugin listener] Value changed. Synchronize the value of catalog data object with that of plugin data object: paramName=" + qualifiedParamName + ", newValue=" + evalContext.getDataObject(qualifiedParamName).getValue());
//            evalContext.getDataObject(qualifiedParamName).copyFromCatalogDataObjectToDomePluginDataObject();
//        }
    }

    public void parameterValueChanged(Object newValue, String qualifiedParamName) {
        if (qualifiedParamName.startsWith(CConstant.ITF_ALIAS + ".")) {
            ////speedup Clog.debug("[catalog plugin listener] Value changed. Synchronize the value of catalog data object with that of plugin data object: paramName=" + qualifiedParamName + ", newValue=" + newValue);
            evalContext.getDataObject(qualifiedParamName).copyFromCatalogDataObjectToDomePluginDataObject();
//            System.out.println("updating ... " + qualifiedParamName + " -> now its status is " + evalContext.getDataObject(qualifiedParamName).getDomePluginParameter().getValueStatus());
        }
    }
}