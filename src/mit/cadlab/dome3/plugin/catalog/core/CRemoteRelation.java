package mit.cadlab.dome3.plugin.catalog.core;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeInterface;
import mit.cadlab.dome3.api.RuntimeInterface;
import mit.cadlab.dome3.api.RuntimeParameter;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

/**
 * CRemoteRelation refers to a model that is not defined in this model,
 * so it can be a DOME model or a DOME project in the same server or in a remote one.
 *
 * User: Sangmok Han
 * Date: 2005. 11. 15.
 */
public class CRemoteRelation extends CRelation {

    private String user;
    private String password;
    private String serverPort;
    private String space;
    private String interfacePath;

    public static final String SERVER_SPACE = "server";
    public static final String GROUP_SPACE = "groups";
    public static final String USER_SPACE = "users";

    /* invoked by CImplementation.addRelation() */
    protected CRemoteRelation(String relationName, String relAlias, CImplementation parent, CNamingService namingService) {
        super(relationName, relAlias, parent, namingService);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getSpace() {
        return space;
    }

    /**
     * space can be SERVER_SPACE, USER_SPACE, or GROUP_SPACE
     * @param space
     */
    public void setSpace(String space) {
        this.space = space;
    }

    public String getInterfacePath() {
        return interfacePath;
    }
    /**
     * an example of interfacePath:
     * "Public/photovoltaic/PV array operation/PV operation - simple Interface"
     */
    public void setInterfacePath(String interfacePath) {
        this.interfacePath = interfacePath;
    }

    public void configureRemoteRelationUsingRuntimeInterfaceInformation(DomeConnection domeConn) {
        if (this.getUser() == null || this.getServerPort() == null || this.getSpace() == null || this.getInterfacePath() == null) {
            throw new RuntimeException("user, server port, space, and interface path is required to execute configureRemoteRelationUsingRuntimeInterfaceInformation()");
        }
        DomeInterface domeItf = domeConn.getInterfaceByPath(this.getSpace(), this.getInterfacePath());
        RuntimeInterface rtItf = domeItf.createRuntimeInterface();

        if (domeItf.isInterfaceOfModel()) {
            this.setRelationName(domeItf.getParentModel().getModelName() + "/" + domeItf.getInterfaceName());
        } else if (domeItf.isInterfaceOfProject()) {
            this.setRelationName(domeItf.getParentProject().getProjectName() + "/" + domeItf.getInterfaceName());
        }

        /* store RuntimeParameter instances of input parameters */
        java.util.List inputRtParams = new ArrayList();
        java.util.List inputParams = rtItf.getIndependentParameters();
        for (Iterator i = inputParams.iterator(); i.hasNext();) {
            RuntimeParameter rtParam = (RuntimeParameter) i.next();
            inputRtParams.add(rtParam);
            CRelationInputParameter param = this.addInputParameter(rtParam.getParamName());
            param.setDataType(rtParam.getDataType());

            Unit rtUnit = rtParam.getUnit();
            if (rtUnit != null) {
                param.setUnit(rtUnit.toString());
            } else {
                param.setUnit(CConstant.NO_UNIT_STR);
            }

            if (CConstant.REAL_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(Double.toString(rtParam.getRealValue()));
                //param.getMapping().setMappingScript(Double.toString(rtParam.getRealValue())); // default value is used as a mapping script
            } else if (CConstant.INTEGER_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(Integer.toString(rtParam.getIntegerValue()));
                //param.getMapping().setMappingScript(Integer.toString(rtParam.getIntegerValue())); // default value is used as a mapping script
            } else if (CConstant.STRING_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(rtParam.getStringValue());
                //param.getMapping().setMappingScript("\"" + rtParam.getStringValue() + "\""); // default value is used as a mapping script
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(Boolean.toString(rtParam.getBooleanValue()));
                //param.getMapping().setMappingScript("\"" + rtParam.getBooleanValue() + "\""); // default value is used as a mapping script (ex) "true" or "false"
            } else if (CConstant.ENUM_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(DataObjectUtil.getEnumString(rtParam.getEnumerationList()));
//                Object[] enumData = rtParam.getEnumerationValue();
//                int enumIndex = ((Integer) enumData[0]).intValue();
//                String[] enumNames = (String[]) enumData[1];
//                if (enumIndex != -1) {
//                    param.getMapping().setMappingScript("\"" + enumNames[enumIndex] + "\""); // default enum name value is used as a mapping script
//                }
            } else if (CConstant.FILE_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(rtParam.getFileName());
            } else if (CConstant.MATRIX_DATA_TYPE.equals(rtParam.getDataType())) {
                Vector matrixValue = rtParam.getMatrixValue();
                param.setDefaultValue(DataObjectUtil.toMatrixString(matrixValue));
                //param.getMapping().setMappingScript(DataObjectUtil.removeDataType(param.getDefaultValue())); // default value is used as a mapping script
            } else if (CConstant.VECTOR_DATA_TYPE.equals(rtParam.getDataType())) {
                Vector vectorValue = rtParam.getVectorValue();
                param.setDefaultValue(DataObjectUtil.toVectorString(vectorValue));
                //param.getMapping().setMappingScript(DataObjectUtil.removeDataType(param.getDefaultValue())); // default value is used as a mapping script
            } else {
                throw new RuntimeException("currently unsupported type: " + rtParam.getDataType());
            }

            String mappingScript = CoreUtil.convertDefaultValueIntoMappingScript(rtParam.getDataType(), param.getDefaultValue());
            param.getMapping().setMappingScript(mappingScript);
        }

        java.util.List outputParams = rtItf.getIntermediateParameters();
        outputParams.addAll(rtItf.getIndeterminateParameters());
        outputParams.addAll(rtItf.getResultParameters());
        for (Iterator i = outputParams.iterator(); i.hasNext();) {
            RuntimeParameter rtParam = (RuntimeParameter) i.next();
            CParameter param = this.addOutputParameter(rtParam.getParamName());
            param.setDataType(rtParam.getDataType());

            Unit rtUnit = rtParam.getUnit();
            if (rtUnit != null) {
                param.setUnit(rtUnit.toString());
            } else {
                param.setUnit(CConstant.NO_UNIT_STR);
            }

            if (CConstant.REAL_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(Double.toString(rtParam.getRealValue()));
            } else if (CConstant.INTEGER_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(Integer.toString(rtParam.getIntegerValue()));
            } else if (CConstant.STRING_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(rtParam.getStringValue());
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(Boolean.toString(rtParam.getBooleanValue()));
            } else if (CConstant.ENUM_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(DataObjectUtil.getEnumString(rtParam.getEnumerationList()));
            } else if (CConstant.FILE_DATA_TYPE.equals(rtParam.getDataType())) {
                param.setDefaultValue(rtParam.getFileName());
            } else if (CConstant.MATRIX_DATA_TYPE.equals(rtParam.getDataType())) {
                Vector matrixValue = rtParam.getMatrixValue();
                param.setDefaultValue(DataObjectUtil.toMatrixString(matrixValue));
            } else if (CConstant.VECTOR_DATA_TYPE.equals(rtParam.getDataType())) {
                Vector vectorValue = rtParam.getVectorValue();
                param.setDefaultValue(DataObjectUtil.toVectorString(vectorValue));
            } else {
                throw new RuntimeException("currently unsupported data type: " + rtParam.getDataType());
            }

            /* populate dependency of CRelation */
            for (Iterator j = inputRtParams.iterator(); j.hasNext();) {
                RuntimeParameter inputRtParam = (RuntimeParameter) j.next();
                /* if a input drives this output, assign dependency information into CRelation */
                if (rtItf.getDependency(inputRtParam, rtParam)) {
                    this.setDependency(inputRtParam.getParamName(), rtParam.getParamName());
                }
            }
        }
//
//        outputParams = rtItf.getResultParameters();
//        for (Iterator i = outputParams.iterator(); i.hasNext();) {
//            RuntimeParameter rtParam = (RuntimeParameter) i.next();
//            CParameter param = this.addOutputParameter(rtParam.getParamName());
//            param.setDataType(rtParam.getDataType());
//
//            Unit rtUnit = rtParam.getUnit();
//            if (rtUnit != null) {
//                param.setUnit(rtUnit.toString());
//            } else {
//                param.setUnit(CConstant.NO_UNIT_STR);
//            }
//
//            if (CConstant.REAL_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(Double.toString(rtParam.getRealValue()));
//            } else if (CConstant.INTEGER_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(Integer.toString(rtParam.getIntegerValue()));
//            } else if (CConstant.STRING_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(rtParam.getStringValue());
//            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(Boolean.toString(rtParam.getBooleanValue()));
//            } else if (CConstant.ENUM_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(DataObjectUtil.getEnumString(rtParam.getEnumerationList()));
//            } else if (CConstant.FILE_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(rtParam.getFileName());
//            } else if (CConstant.MATRIX_DATA_TYPE.equals(rtParam.getDataType())) {
//                Vector matrixValue = rtParam.getMatrixValue();
//                param.setDefaultValue(DataObjectUtil.toMatrixString(matrixValue));
//            } else if (CConstant.VECTOR_DATA_TYPE.equals(rtParam.getDataType())) {
//                Vector vectorValue = rtParam.getVectorValue();
//                param.setDefaultValue(DataObjectUtil.toVectorString(vectorValue));
//            } else {
//                throw new RuntimeException("currently unsupported type: " + rtParam.getDataType());
//            }
//
//            /* populate dependency of CRelation */
//            for (Iterator j = inputRtParams.iterator(); j.hasNext();) {
//                RuntimeParameter inputRtParam = (RuntimeParameter) j.next();
//                /* if a input drives this output, assign dependency information into CRelation */
//                if (rtItf.getDependency(inputRtParam, rtParam)) {
//                    this.setDependency(inputRtParam.getParamName(), rtParam.getParamName());
//                }
//            }
//        }


//        for (Iterator i = outputParams.iterator(); i.hasNext();) {
//            RuntimeParameter rtParam = (RuntimeParameter) i.next();
//            CParameter param = this.addOutputParameter(rtParam.getParamName());
//            param.setDataType(rtParam.getDataType());
//
//            Unit rtUnit = rtParam.getUnit();
//            if (rtUnit != null) {
//                param.setUnit(rtUnit.toString());
//            } else {
//                param.setUnit(CConstant.NO_UNIT_STR);
//            }
//
//            if (CConstant.REAL_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(Double.toString(rtParam.getRealValue()));
//            } else if (CConstant.INTEGER_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(Integer.toString(rtParam.getIntegerValue()));
//            } else if (CConstant.STRING_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(rtParam.getStringValue());
//            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(Boolean.toString(rtParam.getBooleanValue()));
//            } else if (CConstant.ENUM_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(DataObjectUtil.getEnumString(rtParam.getEnumerationList()));
//            } else if (CConstant.FILE_DATA_TYPE.equals(rtParam.getDataType())) {
//                param.setDefaultValue(rtParam.getFileName());
//            } else if (CConstant.MATRIX_DATA_TYPE.equals(rtParam.getDataType())) {
//                Vector matrixValue = rtParam.getMatrixValue();
//                param.setDefaultValue(DataObjectUtil.toMatrixString(matrixValue));
//            } else if (CConstant.VECTOR_DATA_TYPE.equals(rtParam.getDataType())) {
//                Vector vectorValue = rtParam.getVectorValue();
//                param.setDefaultValue(DataObjectUtil.toVectorString(vectorValue));
//            } else {
//                throw new RuntimeException("currently unsupported type: " + rtParam.getDataType());
//            }
//
//            /* populate dependency of CRelation */
//            for (Iterator j = inputRtParams.iterator(); j.hasNext();) {
//                RuntimeParameter inputRtParam = (RuntimeParameter) j.next();
//                /* if a input drives this output, assign dependency information into CRelation */
//                if (rtItf.getDependency(inputRtParam, rtParam)) {
//                    this.setDependency(inputRtParam.getParamName(), rtParam.getParamName());
//                }
//            }
//        }
        rtItf.close();
    }

    /** configure relation name, parameters and dependencies by querying subscribed interface
     * using runtime interface information of user, server port, space, and interface path */
    public void configureRemoteRelationUsingRuntimeInterfaceInformation() {
        if (this.getUser() == null || this.getServerPort() == null || this.getSpace() == null || this.getInterfacePath() == null) {
            throw new RuntimeException("user, server port, space, and interface path is required to execute configureRemoteRelationUsingRuntimeInterfaceInformation()");
        }
        DomeConnection domeConn = new DomeConnection(this.getUser(), this.getPassword(), this.getServerPort());
        configureRemoteRelationUsingRuntimeInterfaceInformation(domeConn);
        domeConn.close();
    }

    public String getRelationScript() {
        String script = "";
        script = script + "def conn = new DomeConnection(\"" + getUser() +  "\", \"" + getPassword() + "\", \"" + getServerPort() + "\");\n";
        script = script + "def runtimeItf = conn.getInterfaceByPath(\"" + getSpace() +  "\", \"" + getInterfacePath() + "\").createRuntimeInterface();\n";

        for (Iterator i = this.getInputParameterNames().iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter inputParam = getNamingService().getRelationInputParameter(getRelAlias() + "." + paramName);
            script = script + "runtimeInterface.getParameterByName(\"" + inputParam.getName() + "\").";
            if (CConstant.REAL_DATA_TYPE.equals(inputParam.getDataType())) {
                script = script + "setRealValue(" + inputParam.getQualifiedName() + ".getDoubleValue());\n";
            }
        }

        script = script + "runtimeItf.submit();";

        for (Iterator i = this.getInputParameterNames().iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter inputParam = getNamingService().getRelationInputParameter(getRelAlias() + "." + paramName);
            script = script + inputParam.getQualifiedName() + ".setDoubleValue(runtimeInterface.getParameterByName(\"" + inputParam.getName() + "\").";
            if (CConstant.REAL_DATA_TYPE.equals(inputParam.getDataType())) {
                script = script + "getRealValue());\n";
            }

        }

        script = script + "conn.close();";

        return script;
    }
}
