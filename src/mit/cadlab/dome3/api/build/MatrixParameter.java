package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.util.DomeException;
import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

import java.util.List;

public class MatrixParameter extends Parameter{
    public MatrixParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setUnit(String unitId) {
        if (unitId == null)
            throw new DomeException("Invalid unit: " + unitId);
        else
            ((DomeMatrixData) getDataObject()).setUnit(new Unit(unitId));
    }

    public void setSize(int numrow, int numcol) {
        DomeMatrixData mat = (DomeMatrixData) getDataObject();
        mat.setRowCount(numrow);
        mat.setColumnCount(numcol);
    }

    public void setValue(List vals) {
        getDataObject().setValues(vals);
    }

    public void setItem(int row, int col, Number val) {
        ((DomeMatrixData) getDataObject()).setItem(row, col, val);
    }

    public int[] getSize() {
        return ((DomeMatrixData) getDataObject()).getSize();
    }

    public List getValue() {
        return getDataObject().getValues();
    }

    public Number getItem(int row, int col) {
        return ((DomeMatrixData) getDataObject()).getItem(row, col);
    }

    public String getUnitLabel() {
        return UnitAtom.getUnitDescriptionFromName(getDataObject().getUnit().getName());
    }

    public String getDataType() {
        return DataTypeConstants.MATRIX;
    }
}
