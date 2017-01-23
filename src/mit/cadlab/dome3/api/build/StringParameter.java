package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;

public class StringParameter extends Parameter{
    public StringParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setValue(String val) {
        ((StringData) getDataObject()).setValue(val);
    }

    public String getValue() {
        return ((StringData) getDataObject()).getValue();
    }

    public String getDataType() {
        return DataTypeConstants.STRING;
    }
}
