package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.util.DomeException;
import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

import java.util.List;

public class VectorParameter extends Parameter{
    public VectorParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setUnit(String unitId) {
        if (unitId == null)
            throw new DomeException("Invalid unit: " + unitId);
        else
            ((DomeVectorData) getDataObject()).setUnit(new Unit(unitId));
    }

    public void setSize(int size) {
        ((DomeVectorData) getDataObject()).setSize(size);
    }

    public void setValue(List vals) {
        getDataObject().setValues(vals);
    }

    public void setItem(int index, Number val) {
        ((DomeVectorData) getDataObject()).setItem(index, val);
    }

    public int getSize() {
        return ((DomeVectorData) getDataObject()).getSize();
    }

    public List getValue() {
        return getDataObject().getValues();
    }

    public Number getItem(int index) {
        return ((DomeVectorData) getDataObject()).getItem(index);
    }

    public String getUnitLabel() {
        return UnitAtom.getUnitDescriptionFromName(getDataObject().getUnit().getName());
    }

    public String getDataType() {
        return DataTypeConstants.VECTOR;
    }
}
