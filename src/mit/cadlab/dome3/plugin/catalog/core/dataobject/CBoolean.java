package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

/**
 * User: Sangmok Han
 * Date: Sep 19, 2006
 */
public class CBoolean extends CDataObject {
    /** create CBoolean with a given value and with no unit */
    public CBoolean(String value) {
        this(value, CConstant.NO_UNIT_STR);
    }

    /** create CBoolean instance that has the same value and unit of the given data. contructed CBoolean is not an interface parameter */
    public CBoolean(String value, String unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setBooleanValue(Boolean.getBoolean(value)); // String is stored as a "value" of CDataObject
        this.setUnit(unit);
    }

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CBoolean(String value, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setBooleanValue(Boolean.getBoolean(value));
        this.setUnit(unit);
    }

    /** create CBoolean instance corresponding to DomeReal parameter. contructed CBoolean is an interface parameter  */
    public CBoolean(Parameter strParam) {
        super(strParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        Boolean pluginDataObjValue = ((BooleanData) getDomePluginDataObject()).getBooleanValue();
        setBooleanValue(pluginDataObjValue.booleanValue());
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        ((BooleanData) getDomePluginDataObject()).setBooleanValue(Boolean.valueOf(getBooleanValue()));
    }

    public boolean getBooleanValue() {
        return ((Boolean) getValue()).booleanValue();
    }

    public void setBooleanValue(boolean value) {
        setValue(Boolean.valueOf(value));
    }

    /** plus operator on two files asuumes that both files are text files. */
    public Object plus(Object obj) {
	    throw new IllegalArgumentException("Can't add " + this + " to " + obj);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof CBoolean) {
            return ((CBoolean) obj).getBooleanValue() == this.getBooleanValue();
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() == this.getBooleanValue();
        } else if (obj instanceof CString) {
            String strValue = ((CString) obj).getStringValue();
            return strValue.equalsIgnoreCase(Boolean.toString(this.getBooleanValue()));
        } else if (obj instanceof String) {
            return ((String) obj).equalsIgnoreCase(Boolean.toString(this.getBooleanValue()));
        } else {
            return false;
        }
    }

    public Object leftShift(Object obj) {
        //checkUnitConversionRule(obj);

        if (obj instanceof CBoolean) {
            this.setBooleanValue(((CBoolean) obj).getBooleanValue());
		} else if (obj instanceof Boolean) {
            this.setBooleanValue(((Boolean) obj).booleanValue());
		} else if (obj instanceof CReal) {
            this.setBooleanValue(((CReal) obj).getDoubleValue() != 0);
		} else if (obj instanceof CInteger) {
            this.setBooleanValue(((CInteger) obj).getIntegerValue() != 0);
		} else if (obj instanceof Number) {
            this.setBooleanValue(((Number) obj).doubleValue() != 0);
		} else if (obj instanceof CString) {
			String strValue = ((CString) obj).getStringValue();
            this.setBooleanValue(strValue.equalsIgnoreCase(Boolean.toString(this.getBooleanValue())));
        } else if (obj instanceof String) {
            String strValue = (String) obj;
            this.setBooleanValue(strValue.equalsIgnoreCase(Boolean.toString(this.getBooleanValue())));
		} else {
			throw new IllegalArgumentException("error in LHR.leftShit(RHS)");
		}

        return this;
    }

    public String toString() {
        String mapped = "false";
        if (isAssociatedWithInterfaceInputParameter()) {
            mapped = "itf input param";
        }

        if (isAssociatedWithInterfaceOutputParameter()) {
            mapped = "itf output param";
        }
        return "[CBoolean: value=" + getValue() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
    }
}
