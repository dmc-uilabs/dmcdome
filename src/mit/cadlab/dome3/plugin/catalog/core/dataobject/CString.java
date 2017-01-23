package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import java.net.URL;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

/**
 * User: Sangmok Han
 * Date: 2006. 4. 6.
 */
public class CString extends CDataObject {

    /** create CString with a given value and with no unit */
    public CString(String value) {
        this(value, CConstant.NO_UNIT_STR);
    }

    /** create CString instance that has the same value and unit of the given data. contructed CString is not an interface parameter */
    public CString(String value, String unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setStringValue(value); // String is stored as a "value" of CDataObject
        this.setUnit(unit);
    }

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CString(String value, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setStringValue(value);
        this.setUnit(unit);
    }

    /** create CString instance corresponding to DomeReal parameter. contructed CString is an interface parameter  */
    public CString(Parameter strParam) {
        super(strParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        String pluginDataObjValue = ((StringData) getDomePluginDataObject()).getValue();
        setStringValue(pluginDataObjValue);
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        ((StringData) getDomePluginDataObject()).setValue((String) getValue());
    }

    public String getStringValue() {
        return (String) getValue();
    }

    public void setStringValue(String value) {
        setValue(value);
    }

    /** plus operator on two files asuumes that both files are text files. */
    public Object plus(Object obj) {
        if (obj instanceof CString) {
            CString dataObj = (CString) obj;
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            return new CString(new String(this.getStringValue()) + dataObj.getStringValue(), resultUnit);
        } else if (obj instanceof CReal) {
			CReal dataObj = (CReal) obj;
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            return new CString(new String(this.getStringValue()) + Double.toString(dataObj.getDoubleValue()), resultUnit);
		} else if (obj instanceof CInteger) {
			CInteger dataObj = (CInteger) obj;
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            return new CString(new String(this.getStringValue()) + Integer.toString(dataObj.getIntegerValue()), resultUnit);
		} else if (obj instanceof CFile) {
			CFile dataObj = (CFile) obj;
			Object objValue = dataObj.getFileValue();
			byte[] byteValue = null;
			if (objValue instanceof byte[])
				byteValue = (byte[]) objValue;
            
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            return new CString(new String(this.getStringValue()) + new String(byteValue), resultUnit);
		} else if (obj instanceof CEnumeration) {
            CEnumeration dataObj = (CEnumeration) obj;
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            return new CString(new String(this.getStringValue()) + dataObj.getSelectedEnumName(), resultUnit);
        } else if (obj instanceof String) {
            String strValue = (String) obj;
            return new CString(new String(this.getStringValue()) + strValue, this.getUnit().cloneUnit());
		} else if (obj instanceof Object) {
            return new CString(new String(this.getStringValue()) + obj.toString(), this.getUnit().cloneUnit());
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof CString) {
            return getValue().equals(((CString) obj).getValue());
        } else if (obj instanceof String) {
            return getValue().equals(obj);
        } else if (obj instanceof CEnumeration) {
            return getValue().equals(((CEnumeration) obj).getSelectedEnumName());
        } else {
            return false;
        }
    }

    public Object leftShift(Object obj) {
        //checkUnitConversionRule(obj);

        if (obj instanceof CString) {
			CString dataObj = (CString) obj;
            this.setStringValue(dataObj.getStringValue());
        } else if (obj instanceof CFile) {
			CFile dataObj = (CFile) obj;
			Object objFileValue = dataObj.getFileValue();
			
			if (objFileValue instanceof byte[])
				this.setStringValue(new String((byte[]) objFileValue));
			else if (objFileValue instanceof URL)
				this.setStringValue(new String( ((URL) objFileValue).toString()));
			else 
				throw new IllegalArgumentException("Unknown class type for fileValue:" + objFileValue);
        } else if (obj instanceof CEnumeration) {
			CEnumeration dataObj = (CEnumeration) obj;
            this.setStringValue(dataObj.getSelectedEnumName());
        } else if (obj instanceof String) {
            String strValue = (String) obj;
            this.setStringValue(strValue);
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
        return "[CString: value=" + getValue() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
    }
}
