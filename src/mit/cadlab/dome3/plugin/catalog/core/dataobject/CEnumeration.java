package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 29.
 */
public class CEnumeration extends CDataObject {

    /** create CReal instance corresponding to DomeReal parameter. contructed CReal is an interface parameter  */
    public CEnumeration(Parameter enumParam) {
        super(enumParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();
    }

    public CEnumeration(List enumList, CUnit unit) {
        super();

        this.setEnumList(enumList);
        this.setUnit(unit);
    }

    /**
     * new CEnumeration("my first=1, my second=2, my third=3|-1, Integer")
     * new CEnumeration("my first=1, my second=2, my third=3|-1")
     */
    public CEnumeration(String enumStr) {
        this(DataObjectUtil.createEnumList(enumStr), CUnit.NO_UNIT);
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        int selectedIdx = ((EnumerationData) getDomePluginDataObject()).getLastSelection();
        String[] enumNames = ((EnumerationData) getDomePluginDataObject()).getNamesArray();
        List enumList = new ArrayList();

        for (int i = 0; i < enumNames.length; i++) {
            Object enumValue = ((EnumerationData) getDomePluginDataObject()).getElementValue(i);
            enumList.add(new Object[] { enumNames[i], enumValue, new Boolean((selectedIdx == i)) } );
        }
        setEnumList(enumList);
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        Integer catDataObjValue = (Integer) getValue();
        ((IntegerData) getDomePluginDataObject()).setIntegerValue(catDataObjValue);
    }

    /** enumList is a List of Object[] { String name, Object value, Boolean isSelected }.
     * for easier manipulation of EnumList, utility methods are provided
     * DataObjectUtil.getSelectedIndex()
     * DataObjectUtil.getEnumName()
     * DataObjectUtil.getEnumValue()
     * DataObjectUtil.createEnumList() */
    public List getEnumList() {
        return (List) getValue();
    }

    public void setEnumList(List enumList) {
        setValue(enumList);
    }

    /** returns selected index of this enumeration */
    public int getSelectedIndex() {
        return DataObjectUtil.getSelectedIndex(getEnumList());
    }

    /** returns the name of the selected index */
    public String getSelectedEnumName() {
        return DataObjectUtil.getEnumName(getEnumList(), getSelectedIndex());
    }

    /** returns the value object of the selected index */
    public Object getSelectedEnumValue() {
        return DataObjectUtil.getEnumValue(getEnumList(), getSelectedIndex());
    }

    public Object leftShift(Object obj) {
        if (obj instanceof CEnumeration) {
//			/* change the selected index of current enum to that of the source enum */
//            CEnumeration enumDataObj = (CEnumeration) obj;
//            List objEnumList = enumDataObj.getEnumList();
//            int objSelectedIdx = DataObjectUtil.getSelectedIndex(objEnumList);
//            List thisEnumValue = this.getEnumList();
//            DataObjectUtil.setSelectedIndex(objSelectedIdx, thisEnumValue);

            /* deep copy enumList of the source object */
            CEnumeration enumDataObj = (CEnumeration) obj;
            List objEnumList = enumDataObj.getEnumList();
            setEnumList(DataObjectUtil.cloneEnumList(objEnumList));
        } else if (obj instanceof CString) {
            String objEnumName = ((CString) obj).getStringValue();
            List thisEnumValue = this.getEnumList();
            DataObjectUtil.setSelectedIndex(objEnumName, thisEnumValue);
		} else if (obj instanceof String) {
            String objEnumName = (String) obj;
            List thisEnumValue = this.getEnumList();
            DataObjectUtil.setSelectedIndex(objEnumName, thisEnumValue);
		} else {
			throw new IllegalArgumentException("error in LHR.leftShit(RHS)");
		}

        return this;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof CString) {
            if (getSelectedIndex() != -1) {
                return getSelectedEnumName().equals(((CString) obj).getValue());
            } else {
                return false;
            }
        } else if (obj instanceof String) {
            return getSelectedEnumName().equals(obj);
        } else if (obj instanceof CEnumeration) {
            if (getSelectedIndex() != -1) {
                return getSelectedEnumName().equals(((CEnumeration) obj).getSelectedEnumName());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        String mapped = "false";
        if (isAssociatedWithInterfaceInputParameter()) {
            mapped = "itf input param";
        }

        if (isAssociatedWithInterfaceOutputParameter()) {
            mapped = "itf output param";
        }
        int selectedIdx = getSelectedIndex();
        if (selectedIdx != -1) {
            return "[CEnum: selectedIdx=" + getSelectedIndex() + ", selectedName=" + getSelectedEnumName() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
        } else {
            return "[CEnum: selectedIdx=" + getSelectedIndex() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
        }
    }
}
