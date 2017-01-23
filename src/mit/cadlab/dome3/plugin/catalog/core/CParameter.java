package mit.cadlab.dome3.plugin.catalog.core;


import edu.iupui.rg.ucum.units.Unit;

import java.util.Iterator;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public abstract class CParameter {
    private String name;
    private String namespace;
    private String dataType;
    private String unit;
    private String defaultValue;
//    private String filePath; // applicable only for param with data type of File
//    private String fileType;
    //private Object value; // one of Double, Integer, File, Vector containing Real, Integer, or Vector, String, Enum
    private int status;

    public CParameter(String namespace, String name) {
        this.namespace  = namespace;
        this.name = name;

        /* data object is initialized as real with no unit with default value 0 */
        this.dataType = CConstant.REAL_DATA_TYPE;
        this.unit = CConstant.NO_UNIT_STR;
        //this.value = new Double(0);
        this.defaultValue = "0"; // may not be necessary
//        this.filePath = "temp.txt";
//        this.fileType = "Binary";

        this.status = CConstant.UNASSIGNED_STATUS;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getQualifiedName() {
        if ("".equals(namespace) || null == namespace) {
            return name;
        }
        return namespace + "." + name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        if (dataType.equals(this.dataType)) {
            return;
        }

        this.dataType = dataType;

//        if (CConstant.REAL_DATA_TYPE.equals(this.dataType) || CConstant.INTEGER_DATA_TYPE.equals(this.dataType)) {
//            this.defaultValue = "0";
//        } else if (CConstant.STRING_DATA_TYPE.equals(this.dataType)) {
//            this.defaultValue = "";
//        } else if (CConstant.ENUM_DATA_TYPE.equals(this.dataType)) {
//            this.defaultValue = "my first=1, my second=2, my third=3|-1, Integer";
//        } else if (CConstant.FILE_DATA_TYPE.equals(this.dataType)) {
//            this.defaultValue = "defaultname.txt";
//        }

//        if (CConstant.REAL_DATA_TYPE.equals(dataType)) {
//            if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
//                value = new Integer(((Double) value).intValue());
//            } else {
//                throw new RuntimeException("cannot change the data type from " + dataType + " to " + dataType);
//            }
//        } else if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
//            if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
//                value = new Double(((Integer) value).doubleValue());
//            } else {
//                throw new RuntimeException("cannot change the data type from " + dataType + " to " + dataType);
//            }
//        }
    }

    public String getUnit() {
        return unit;
    }

    public String getFilePath() {
        //return filePath;
        return getDefaultValue();
    }

//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }

    public String getFileType() {
        //return fileType;
        return "Binary";
    }

//    public void setFileType(String fileType) {
//        this.fileType = fileType;
//    }

    public void toGreenStatus() {
        this.status = CConstant.GREEN_STATUS;
    }

    public void toWhiteStatus() {
        this.status = CConstant.WHITE_STATUS;
    }

    public void toRedStatus() {
        this.status = CConstant.RED_STATUS;
    }

    /**
     * do unit conversion between from-value & from-unit and to-unit
     * @param fromValue from-value
     * @param fromUnit from-unit
     * @param toUnit to-unit
     * @return to-value
     */
    private double convertUnit(double fromValue, Unit fromUnit, Unit toUnit) {
        if (fromUnit.equivalent(toUnit)) {
            return toUnit.convertFrom(fromValue, fromUnit); // toValue
        } else {
            return fromValue;
        }
    }


    public void setUnit(String newUnit) {
        this.unit = newUnit;

//        double fromValue = 0;
//        double toValue = 0;
//
//        if (CConstant.REAL_DATA_TYPE.equals(dataType)) {
//            fromValue = ((Double) value).doubleValue();
//        } else if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
//            fromValue = ((Integer) value).intValue();
//        }
//
//
//
//        if (CConstant.NO_UNIT_STR.equals(unit) || CConstant.NO_UNIT_STR.equals(newUnit)) {
//            /* if current unit is no unit, just accept new unit no conversion needed */
//            /* if new unit is no unit, just accept new unit no conversion needed */
//            this.unit = newUnit;
//            toValue = fromValue;
//        } else {
//            Unit fromUnit = new Unit(unit);
//            Unit toUnit = new Unit(newUnit);
//
//            if (fromUnit.equivalent(toUnit)) {
//                /* if both are equivalent, convert unit */
//                toValue = convertUnit(fromValue, fromUnit, toUnit);
//                this.unit = newUnit;
//            } else {
//                /* if both are not equivalent, keep current value */
//                toValue = fromValue;
//                this.unit = newUnit;
//            }
//        }
//
//        if (CConstant.REAL_DATA_TYPE.equals(dataType)) {
//            value = new Double(toValue);
//        } else if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
//            value = new Integer((int) (toValue + 0.5));
//        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

//    public Object getValue() {
//        return value;
//    }
//
//    public void setValue(Object value) {
//        this.value = value;
//    }
//
//    public void setDoubleValue(double value) {
//        this.value = new Double(value);
//    }
//
//    public void setIntValue(int value) {
//        this.value = new Integer(value);
//    }
//
//    public double getDoubleValue() {
//        return ((Double) value).doubleValue();
//    }
//
//    public int getIntValue() {
//        return ((Integer) value).intValue();
//    }

    public boolean isDataTypeAllowedToHaveUnit() {
        if (dataType.equals(CConstant.INTEGER_DATA_TYPE) ||
                dataType.equals(CConstant.REAL_DATA_TYPE) ||
                dataType.equals(CConstant.VECTOR_DATA_TYPE) ||
                dataType.equals(CConstant.MATRIX_DATA_TYPE)) {
            return true;
        }
        return false;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getStatus() {
        return status;
    }

    public boolean isConsistent() {
        return (status == CConstant.GREEN_STATUS || status == CConstant.WHITE_STATUS);
        //return (status != CConstant.RED_STATUS);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /** copy current status of CParameter to its mapping nodes. getNamingService() is not null when CParameter is CRelationInput/OutputParameter or when CParameter is CInterfaceInput/OutputParameter that belongs to CImplementation. */
    public void copyCurrentStatusToMappingNodes() {
        if (getNamingService() != null) {
            for (Iterator j = getNamingService().findMappingNodes(this.getQualifiedName()).iterator(); j.hasNext(); ) {
                CMappingNode mappingNode = (CMappingNode) j.next();
                mappingNode.setStatus(status);
            }
        }
    }

    public abstract CNamingService getNamingService();

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CParameter)) return false;

        final CParameter cParameter = (CParameter) o;

        if (name != null ? !name.equals(cParameter.name) : cParameter.name != null) return false;
        if (namespace != null ? !namespace.equals(cParameter.namespace) : cParameter.namespace != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 29 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    public String toString() {
        //return "[param:name=" + getQualifiedName() + ", datatype= " + dataType + ", unit= " + unit + ", value= " + value + ", defaultvalue= " + defaultValue + ", status=" + CConstant.getStatusName(status) + "]";
        return "[param:name=" + getQualifiedName() + ", datatype= " + dataType + ", unit= " + unit + ", defaultvalue= " + defaultValue + ", status=" + CConstant.getStatusName(status) + "]";
    }

    /**
     * make a copy of CParameter
     */
    public static void copy(CParameter src, CParameter to) {
        to.setName(src.getName());
        to.setNamespace(src.getNamespace());
        to.setDataType(src.getDataType());
        to.setUnit(src.getUnit());
        to.setDefaultValue(src.getDefaultValue());
        to.setStatus(src.getStatus());
        //to.setValue(cloneValue(src.getDataType(), src.getValue()));
    }

//    private static Object cloneValue(String dataType, Object value) {
//        if (CConstant.REAL_DATA_TYPE.equals(dataType)) {
//            return value;
//        } else if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
//            return value;
//        } else if (CConstant.VECTOR_DATA_TYPE.equals(dataType)) {
//            return new CVector(((CVector) value));
//        } else if (CConstant.MATRIX_DATA_TYPE.equals(dataType)) {
//            return new CMatrix(((CMatrix) value));
//        } else if (CConstant.STRING_DATA_TYPE.equals(dataType)) {
//            return value;
//        } else if (CConstant.TEXT_DATA_TYPE.equals(dataType)) {
//            return value;
//        } else if (CConstant.ENUM_DATA_TYPE.equals(dataType)) {
//            return null; // todo: need implementation
//        } else if (CConstant.BOOLEAN_DATA_TYPE.equals(dataType)) {
//            return value;
//        } else if (CConstant.FILE_DATA_TYPE.equals(dataType)) {
//            return null; // todo: need implementation
//        }
//        return null;
//    }
}
