package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.util.DomeException;
import edu.iupui.rg.ucum.units.UnitAtom;

public class IntegerParameter extends Parameter{
    public IntegerParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setValue(int val) {
        ((IntegerData) getDataObject()).setValue(val);
    }

    public void setUnit(String unitId) {
        if (unitId==null)
            throw new DomeException("Invalid unit: " + unitId);
        else
            ((IntegerData) getDataObject()).setUnit(unitId);
    }

    public int getValue() {
        return ((IntegerData) getDataObject()).getValue();
    }

    public String getUnitLabel() {
        return UnitAtom.getUnitDescriptionFromName(getDataObject().getUnit().getName());
    }

    public String getDataType() {
        return DataTypeConstants.INTEGER;
    }
}
