package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.util.DomeException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

public abstract class Parameter {
    protected ConcreteParameter concreteParam;

    public Parameter(ConcreteParameter concreteParam) {
        this.concreteParam = concreteParam;
    }

    public void setName(String name) {
        concreteParam.setName(name);
    }

    public String getName() {
        return concreteParam.getName();
    }

    protected DataObject getDataObject() {
        return concreteParam.getCurrentDataObject();
    }

    public ConcreteParameter getConcreteParameter() {
        return concreteParam;
    }

    public static List wrapParameterList(List concreteParameterList) {
        ArrayList wrapper = new ArrayList();
        for (int i = 0; i < concreteParameterList.size(); i++) {
            ConcreteParameter p = (ConcreteParameter) concreteParameterList.get(i);
            String datatype = p.getCurrentType();
            if (datatype.equals(DataTypeConstants.BOOLEAN))
                wrapper.add(new BooleanParameter(p));
            else if (datatype.equals(DataTypeConstants.ENUMERATION))
                wrapper.add(new EnumerationParameter(p));
            else if (datatype.equals(DataTypeConstants.FILE))
                wrapper.add(new FileParameter(p));
            else if (datatype.equals(DataTypeConstants.INTEGER))
                wrapper.add(new IntegerParameter(p));
            else if (datatype.equals(DataTypeConstants.MATRIX))
                wrapper.add(new MatrixParameter(p));
            else if (datatype.equals(DataTypeConstants.REAL))
                wrapper.add(new RealParameter(p));
            else if (datatype.equals(DataTypeConstants.STRING))
                wrapper.add(new StringParameter(p));
            else if (datatype.equals(DataTypeConstants.VECTOR))
                wrapper.add(new VectorParameter(p));
        }
        return Collections.unmodifiableList(wrapper);
    }

    public static Parameter wrapParameter(ConcreteParameter concreteParam) {
        String datatype = concreteParam.getCurrentType();
        if (datatype.equals(DataTypeConstants.BOOLEAN))
            return new BooleanParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.ENUMERATION))
            return new EnumerationParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.FILE))
            return new FileParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.INTEGER))
            return new IntegerParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.MATRIX))
            return new MatrixParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.REAL))
            return new RealParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.STRING))
            return new StringParameter(concreteParam);
        else if (datatype.equals(DataTypeConstants.VECTOR))
            return new VectorParameter(concreteParam);
        else
            return null;
    }

    public String toString() {
        return getName() + " (" + getConcreteParameter().getCurrentType() + ")";
    }

    public List getCompatableUnitNames() {
        Unit unit = getDataObject().getUnit();
        if (unit == null)  // for data types that don't support units
            return new ArrayList();
        else {
            List units = UnitAtom.getUnitsOfOneCategory(unit.getCategory());
            List names = new ArrayList();
            for (int i = 0; i < units.size(); i++) {
                names.add(((Unit) units.get(i)).getName());
            }
            return names;
        }
    }

    public List getCompatableUnitDescriptions() {
        Unit unit = getDataObject().getUnit();
        if (unit == null)  // for data types that don't support units
            return new ArrayList();
        else {
            List units = UnitAtom.getUnitsOfOneCategory(unit.getCategory());
            List names = new ArrayList();
            for (int i = 0; i < units.size(); i++) {
                names.add(UnitAtom.getUnitDescriptionFromName(((Unit) units.get(i)).getName()));
            }
            return names;
        }
    }

    public boolean isInterfaceParameter() {
        return concreteParam.getScope() instanceof ModelInterface;
    }

    public void setUnit(String unitId) {
        if (unitId == null)
            throw new DomeException("Invalid unit: " + unitId);
        else {
            String datatype = concreteParam.getCurrentType();
            if (datatype.equals(DataTypeConstants.INTEGER))
                ((IntegerData) getDataObject()).setUnit(unitId);
            else if (datatype.equals(DataTypeConstants.MATRIX))
                ((DomeMatrixData) getDataObject()).setUnit(new Unit(unitId));
            else if (datatype.equals(DataTypeConstants.REAL))
                ((RealData) getDataObject()).setUnit(unitId);
            else if (datatype.equals(DataTypeConstants.VECTOR))
                ((DomeVectorData) getDataObject()).setUnit(new Unit(unitId));
        }
    }

    public abstract String getDataType();
}
