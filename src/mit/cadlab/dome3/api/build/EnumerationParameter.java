package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.util.DomeException;

public class EnumerationParameter extends Parameter{
    public EnumerationParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void addElement(String valueName, Object value) {
        if (value instanceof Integer || value instanceof Double || value instanceof Boolean || value instanceof String) {
            EnumerationData enm = (EnumerationData) getDataObject();
            enm.addElement(valueName, value);
            enm.fireEnumerationPropertyChange();
        } else
            throw new DomeException("The object type is not supported as an enumeration value.");
    }

    public void setLastSelection(int i) {
        ((EnumerationData) getDataObject()).setLastSelection(i);
    }

    public void setLastSelectionToName(String name) {
        ((EnumerationData) getDataObject()).setLastSelectionToName(name);
    }

    public void setLastSelectionToValue(Object val) {
        ((EnumerationData) getDataObject()).setLastSelectionToValue(val);
    }

    public String getElementName(int index) {
        return ((EnumerationData) getDataObject()).getElementName(index);
    }

    public Object getElementValue(int index) {
        return ((EnumerationData) getDataObject()).getElementValue(index);
    }

    public Object getElementValue(String name) {
        return ((EnumerationData) getDataObject()).getElementValue(name);
    }

    public int getLastSelection() {
        return ((EnumerationData) getDataObject()).getLastSelection();
    }

    public String getDataType() {
        return DataTypeConstants.ENUMERATION;
    }

    public int getSize() {
        return ((EnumerationData) getDataObject()).getSize();
    }

    public void clear() {
        ((EnumerationData) getDataObject()).clear();
    }
}
