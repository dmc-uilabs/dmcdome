package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.util.DomeException;
import edu.iupui.rg.ucum.units.UnitAtom;

public class RealParameter extends Parameter{
    public RealParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setValue(double val) {
        ((RealData) getDataObject()).setValue(val);
    }

    public void setUnit(String unitId) {
        if (unitId == null)
            throw new DomeException("Invalid unit: " + unitId);
        else
            ((RealData) getDataObject()).setUnit(unitId);
    }

    public double getValue() {
        return ((RealData) getDataObject()).getValue();
    }

    public String getUnitLabel() {
        return UnitAtom.getUnitDescriptionFromName(getDataObject().getUnit().getName());
    }

    public String getDataType() {
        return DataTypeConstants.REAL;
    }
}
