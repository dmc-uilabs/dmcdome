package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;

public class BooleanParameter extends Parameter{
    public BooleanParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setValue(boolean val) {
        ((BooleanData) getDataObject()).setValue(val);
    }

    public boolean getValue() {
        return ((BooleanData) getDataObject()).getValue();
    }

    public String getDataType() {
        return DataTypeConstants.BOOLEAN;
    }
}
