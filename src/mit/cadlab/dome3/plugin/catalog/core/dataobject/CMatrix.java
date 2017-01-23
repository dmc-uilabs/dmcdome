package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 13.
 */
public class CMatrix extends CDataObject {
    private String valueType; // Real or Integer
    private int lastColSize = 0;

    /** create CMatrix instance corresponding to DomeMatrixData parameter. contructed CMatrix is an interface parameter  */
    public CMatrix(Parameter matrixParam) {
        super(matrixParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();

        if (DomeReal.TYPE_INFO.getTypeName().equalsIgnoreCase(((DomeMatrix) getDomePluginDataObject()).getValueType())) {
            this.valueType = CConstant.REAL_DATA_TYPE;
        } else if (DomeInteger.TYPE_INFO.getTypeName().equalsIgnoreCase(((DomeMatrix) getDomePluginDataObject()).getValueType())) {
            this.valueType = CConstant.INTEGER_DATA_TYPE;
        }
    }

    /** create CMatrix instance. contructed CMatrix is not an interface parameter. valueType is determined by class type of instances in rowList */
    public CMatrix(List rowList, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        String valueType = CConstant.REAL_DATA_TYPE;
        if (rowList.size() > 0) {
            List row = (List) rowList.get(0);
            if (row.size() > 0 && row.get(0) instanceof Integer) {
                valueType = CConstant.INTEGER_DATA_TYPE;
            }
        }

        this.setMatrixValue(rowList);
        this.setUnit(unit);
        this.valueType = valueType;
    }

    /** rowList should be a List of Lists of Numbers */
    public void setMatrixValue(List rowList) {
        this.setValue(rowList);
    }

    /** returns a List of Lists of Numbers */
    public List getMatrixValue() {
        return (List) this.getValue();
    }

    /** create CMatrix instance. contructed CMatrix is not an interface parameter. valueType is either CConstant.REAL_DATA_TYPE or CConstant.INTEGER_DATA_TYPE. */
    public CMatrix(String valueType, int rows, int cols, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        if (! valueType.equals(CConstant.REAL_DATA_TYPE) && ! valueType.equals(CConstant.INTEGER_DATA_TYPE)) {
            throw new RuntimeException("valueType should be either CParameter.REAL_DATA_TYPE or CParameter.INTEGER_DATA_TYPE");
        }
        List rowList = new Vector(); // changed from ArrayList
        for (int i = 0; i < rows; i++) {
            rowList.add(createZeroRowVector(cols));
        }
        this.setMatrixValue(rowList);
        this.setUnit(unit);
        this.valueType = valueType;
    }

    /** create CMatrix instance. contructed CMatrix is not an interface parameter. valueType is either CConstant.REAL_DATA_TYPE or CConstant.INTEGER_DATA_TYPE. */
    public CMatrix(String valueType, int rows, int cols, String unitStr) {
        this(valueType, rows, cols, new CUnit(unitStr));
    }

    /**
     * new CMatrix("[3.0 4.5; 3.2 5.5]");
     * new CMatrix("[3.0 4.5; 3.2 5.5]|Real");
     * new CMatrix("[3 4; 3 5]");
     * new CMatrix("[3 4; 3 5]|Integer");
     */
    public CMatrix(String matrixStr) {
        this(DataObjectUtil.createMatrix(matrixStr), CUnit.NO_UNIT);
    }

    public CMatrix(CMatrix src) {
        super(); // make this catalog data object is not associated with interface input parameter

        List rowList = new Vector(); // changed from ArrayList
        for (Iterator i = src.getMatrixValue().iterator(); i.hasNext(); ) {
            List row = (List) i.next();
            List newRow = new Vector(row); // changed from ArrayList
            rowList.add(newRow);
        }
        this.setMatrixValue(rowList);
        this.setUnit(src.getUnit());
        this.valueType = src.getValueType();
    }


    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        DomeMatrixData domeMatrixDataObj = (DomeMatrixData) getDomePluginDataObject();
        List srcRowList = domeMatrixDataObj.getData();
        List newRowList = new Vector(srcRowList.size());
        for (int i = 0; i < srcRowList.size(); i++) {
            List row = new Vector((List) srcRowList.get(i));
            newRowList.add(row);
        }
        setMatrixValue(newRowList);
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        List catDataObjValue = (List) getValue();
        Vector valueBag = new Vector();
        valueBag.add(catDataObjValue);
        ((DomeMatrixData) getDomePluginDataObject()).setValues(valueBag);
    }

    public List getRow(int rowIdx) {
        return (List) getMatrixValue().get(rowIdx);
    }

    /** reset current matrix value and make this matrix into a single row matrix */
    public void setRow(List numberList) {
        getMatrixValue().clear();
        getMatrixValue().add(numberList);
    }

    /** reset current matrix value and make this matrix into a single column matrix */
    public void setColumn(List numberList) {
        getMatrixValue().clear();
        for (int i = 0; i < numberList.size(); i++) {
            Number num = (Number) numberList.get(i);
            List row = new Vector();
            row.add(num);
            getMatrixValue().add(row);
        }
    }

    public List getColumn(int colIdx) {
        List rowList = getMatrixValue();
        List columnList = new Vector();
        for (int i = 0; i < rowList.size(); i++) {
            columnList.add(rowList.get(colIdx));
        }
        return columnList;
    }

    /* vectorStr is parsed using DataObjectUtil.matrixVector() method */
    public void setMatrixValue(String matrixStr) {
        setMatrixValue(DataObjectUtil.createMatrix(matrixStr));
    }

    /** valueType is either CConstant.REAL_DATA_TYPE or CConstant.INTEGER_DATA_TYPE. */
    public String getValueType() {
        return valueType;
    }

    private List createZeroRowVector(int cols) {
        List newRow = new Vector();
        for (int i = 0; i < cols; i++) {
            newRow.add(new Double(0.0));
        }
        return newRow;
    }

    /** convert the value of the given matrix into a unit given as toUnit */
    private static void convertUnit(CMatrix givenMatrix, CUnit toUnit) {
        int rowSize = givenMatrix.getRowSize();
        int colSize = givenMatrix.getColumnSize();
        if (CConstant.REAL_DATA_TYPE.equals(givenMatrix.getValueType())) {
            for (int rowIdx = 0; rowIdx < rowSize; rowIdx++) {
                for (int colIdx = 0; colIdx < colSize; colIdx++) {
                    double convertedDouble = convertUnit(givenMatrix.getDouble(rowIdx, colIdx), givenMatrix.getUnit(), toUnit);
                    givenMatrix.setDouble(rowIdx, colIdx, convertedDouble);
                }
            }
        } else {
            for (int rowIdx = 0; rowIdx < rowSize; rowIdx++) {
                for (int colIdx = 0; colIdx < colSize; colIdx++) {
                    int convertedInt = (int) convertUnit(givenMatrix.getInt(rowIdx, colIdx), givenMatrix.getUnit(), toUnit);
                    givenMatrix.setInt(rowIdx, colIdx, convertedInt);
                }
            }
        }
    }

    public Object leftShift(Object obj) {
        /* by not checking this, we allow AAA with a unit to be assigned to BBB with no unit.
         * as a result of this assignment, BBB will keep the value of AAA, but won't keep the unit of AAA
         * for a case when we assign BBB to AAA, AAA will keep the value of BBB as is -- no unit conversion is performed -- and keep the current unit of AAA */
        //checkUnitConversionRule(obj);

        if (obj instanceof CMatrix) {
			CMatrix rhsMatrix = (CMatrix) obj;
            CMatrix lhsMatrix = new CMatrix(rhsMatrix);
            convertUnit(lhsMatrix, this.getUnit());
            this.setMatrixValue(lhsMatrix.getMatrixValue());
        } else {
			throw new IllegalArgumentException("error in LHR.leftShit(RHS)");
		}
        return this;
    }

    public Object setDouble(int rowIdx, int colIdx, double value) {
        List rowList = (List) getValue();
        return ((List) rowList.get(rowIdx)).set(colIdx, new Double(value));
    }

    public Object setInt(int rowIdx, int colIdx, int value) {
        List rowList = (List) getValue();
        return ((List) rowList.get(rowIdx)).set(colIdx, new Integer(value));
    }

    public Object setNumber(int rowIdx, int colIdx, Number value) {
        List rowList = (List) getValue();
        return ((List) rowList.get(rowIdx)).set(colIdx, value);
    }

    public double getDouble(int rowIdx, int colIdx) {
        List rowList = (List) getValue();
        return ((Number) ((List) rowList.get(rowIdx)).get(colIdx)).doubleValue();
    }

    public int getInt(int rowIdx, int colIdx) {
        List rowList = (List) getValue();
        return ((Number) ((List) rowList.get(rowIdx)).get(colIdx)).intValue();
    }

    public Number getNumber(int rowIdx, int colIdx) {
        List rowList = (List) getValue();
        return (Number) ((List) rowList.get(rowIdx)).get(colIdx);
    }

    public int getRowSize() {
        return ((List) getValue()).size();
    }

    public CVector getAt(int rowIdx) {
        return new CVector(getRow(rowIdx), getUnit());
    }

    public int getColumnSize() {
        List rowList = (List) getValue();
        if (rowList.size() == 0) {
            return lastColSize;
        } else {
            lastColSize = ((List) rowList.get(0)).size();
            return lastColSize;
        }
    }

    public Object plus(Object obj) {
        CMatrix ret = null;
        int rowSize = this.getRowSize();
        int colSize = this.getColumnSize();

        if (obj instanceof Double) {
            obj = new CReal(((Double) obj).doubleValue(), CUnit.NO_UNIT);
		} else if (obj instanceof Integer) {
            obj = new CInteger(((Integer) obj).intValue(), CUnit.NO_UNIT);
        }

        /* convert vector into a single row or single column matrix */
        if (obj instanceof CVector) {
            CVector vecObj = (CVector) obj;
            if (rowSize == 1 && colSize == vecObj.size()) {
                obj = new CMatrix(vecObj.getValueType(), rowSize, colSize, vecObj.getUnit());
                ((CMatrix) obj).setRow(vecObj.getVectorValue());
            } else if (rowSize == vecObj.size() && colSize == 1) {
                obj = new CMatrix(vecObj.getValueType(), rowSize, colSize, vecObj.getUnit());
                ((CMatrix) obj).setColumn(vecObj.getVectorValue());
            } else {
                throw new IllegalArgumentException("Can't add " + this + " to " + obj + " : vector should be the size either the same as the row size or the column size of the matrix");
            }
        }

        if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject dataObj = (CDataObject) obj;

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && dataObj instanceof CInteger
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CMatrix(CConstant.INTEGER_DATA_TYPE, rowSize, colSize, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CMatrix(CConstant.REAL_DATA_TYPE, rowSize, colSize, resultUnit);
            }

            double convertedDouble = convertUnit(((CReal) dataObj).getDoubleValue(), dataObj.getUnit(), this.getUnit());
            for (int i = 0; i < rowSize; i++) {
                for (int j = 0; j < colSize; j++) {
                    if (isIntegerMatrixWithSameUnit) {
                        ret.setInt(i, j, this.getInt(i, j) + ((CInteger) dataObj).getIntegerValue());
                    } else {
                        ret.setDouble(i, j, this.getDouble(i, j) + convertedDouble);
                    }
                }
            }
            return ret;
		} else if (obj instanceof CMatrix) {
			CMatrix dataObj = (CMatrix) obj;
            if (dataObj.getRowSize() != this.getRowSize() || dataObj.getColumnSize() != this.getColumnSize()) {
                throw new IllegalArgumentException("Can't add " + this + " to " + obj + " : should have the same column and row size.");
            }

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CMatrix(CConstant.INTEGER_DATA_TYPE, rowSize, colSize, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CMatrix(CConstant.REAL_DATA_TYPE, rowSize, colSize, resultUnit);
            }

            for (int i = 0; i < rowSize; i++) {
                for (int j = 0; j < colSize; j++) {
                    if (isIntegerMatrixWithSameUnit) {
                        ret.setInt(i, j, this.getInt(i, j) + dataObj.getInt(i, j));
                    } else {
                        double convertedDouble = convertUnit(dataObj.getDouble(i, j), dataObj.getUnit(), this.getUnit());
                        ret.setDouble(i, j, this.getDouble(i, j) + convertedDouble);
                    }
                }
            }
            return ret;
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
    }

    public Object minus(Object obj) {
        CMatrix ret = null;
        int rowSize = this.getRowSize();
        int colSize = this.getColumnSize();

        /* convert vector into a single row or single column matrix */
        if (obj instanceof CVector) {
            CVector vecObj = (CVector) obj;
            if (rowSize == 1 && colSize == vecObj.size()) {
                obj = new CMatrix(vecObj.getValueType(), rowSize, colSize, vecObj.getUnit());
                ((CMatrix) obj).setRow(vecObj.getVectorValue());
            } else if (rowSize == vecObj.size() && colSize == 1) {
                obj = new CMatrix(vecObj.getValueType(), rowSize, colSize, vecObj.getUnit());
                ((CMatrix) obj).setColumn(vecObj.getVectorValue());
            } else {
                throw new IllegalArgumentException("Can't substract " + obj + " from " + this + " : vector should be the size either the same as the row size or the column size of the matrix");
            }
        }

        if (obj instanceof Double) {
            obj = new CReal(((Double) obj).doubleValue(), CUnit.NO_UNIT);
		} else if (obj instanceof Integer) {
            obj = new CInteger(((Integer) obj).intValue(), CUnit.NO_UNIT);
        }

        if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject dataObj = (CDataObject) obj;

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && dataObj instanceof CInteger
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CMatrix(CConstant.INTEGER_DATA_TYPE, rowSize, colSize, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CMatrix(CConstant.REAL_DATA_TYPE, rowSize, colSize, resultUnit);
            }

            double convertedDouble = convertUnit(((CReal) dataObj).getDoubleValue(), dataObj.getUnit(), this.getUnit());
            for (int i = 0; i < rowSize; i++) {
                for (int j = 0; j < colSize; j++) {
                    if (isIntegerMatrixWithSameUnit) {
                        ret.setInt(i, j, this.getInt(i, j) - ((CInteger) dataObj).getIntegerValue());
                    } else {
                        ret.setDouble(i, j, this.getDouble(i, j) - convertedDouble);
                    }
                }
            }
            return ret;
		} else if (obj instanceof CMatrix) {
			CMatrix dataObj = (CMatrix) obj;
            if (dataObj.getRowSize() != this.getRowSize() || dataObj.getColumnSize() != this.getColumnSize()) {
                throw new IllegalArgumentException("Can't substract " + obj + " from " + this + " : should have the same column and row size.");
            }

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CMatrix(CConstant.INTEGER_DATA_TYPE, rowSize, colSize, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CMatrix(CConstant.REAL_DATA_TYPE, rowSize, colSize, resultUnit);
            }

            for (int i = 0; i < rowSize; i++) {
                for (int j = 0; j < colSize; j++) {
                    if (isIntegerMatrixWithSameUnit) {
                        ret.setInt(i, j, this.getInt(i, j) - dataObj.getInt(i, j));
                    } else {
                        double convertedDouble = convertUnit(dataObj.getDouble(i, j), dataObj.getUnit(), this.getUnit());
                        ret.setDouble(i, j, this.getDouble(i, j) - convertedDouble);
                    }
                }
            }
            return ret;
		} else {
			throw new IllegalArgumentException("Can't substract " + obj + " from " + this);
		}
    }

    public Object multiply(Object obj) {
        CMatrix ret = null;
        int leftRowSize = this.getRowSize();
        int leftColSize = this.getColumnSize();

        /* convert vector into a single row or single column matrix */
        if (obj instanceof CVector) {
            CVector vecObj = (CVector) obj;
            if (leftColSize == vecObj.size()) {
                obj = new CMatrix(vecObj.getValueType(), leftColSize, 1, vecObj.getUnit());
                ((CMatrix) obj).setColumn(vecObj.getVectorValue());
            } else {
                throw new IllegalArgumentException("Can't multiply " + this + " with " + obj + " : vector should be the same size as the column size of the matrix");
            }
        }

        if (obj instanceof Double) {
            obj = new CReal(((Double) obj).doubleValue(), CUnit.NO_UNIT);
		} else if (obj instanceof Integer) {
            obj = new CInteger(((Integer) obj).intValue(), CUnit.NO_UNIT);
        }

        if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject dataObj = (CDataObject) obj;

            CUnit resultUnit = getResultantUnitAfterMutiply(dataObj);
            boolean isBothIntegerMatrix = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType()) && dataObj instanceof CInteger) {
                ret = new CMatrix(CConstant.INTEGER_DATA_TYPE, leftRowSize, leftColSize, resultUnit);
                isBothIntegerMatrix = true;
            } else {
                ret = new CMatrix(CConstant.REAL_DATA_TYPE, leftRowSize, leftColSize, resultUnit);
            }

            for (int i = 0; i < leftRowSize; i++) {
                for (int j = 0; j < leftColSize; j++) {
                    if (dataObj instanceof CInteger) {
                        if (isBothIntegerMatrix) {
                            ret.setInt(i, j, this.getInt(i, j) * ((CInteger) dataObj).getIntegerValue());
                        } else {
                            ret.setDouble(i, j, this.getDouble(i, j) * ((CInteger) dataObj).getIntegerValue());
                        }
                    } else {
                        ret.setDouble(i, j, this.getDouble(i, j) * ((CReal) dataObj).getDoubleValue());
                    }
                }
            }
            return ret;
		} else if (obj instanceof CMatrix) {
			CMatrix dataObj = (CMatrix) obj;
            int rightRowSize = dataObj.getRowSize();
            int rightColSize = dataObj.getColumnSize();

            if (leftColSize != rightRowSize) {
                throw new IllegalArgumentException("Can't multiply " + this + " by " + obj + " : the left matrix should have the same column size as the row size of the right matrix.");
            }

            CUnit resultUnit = getResultantUnitAfterMutiply(dataObj);
            boolean isBothIntegerMatrix = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType()) && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())) {
                ret = new CMatrix(CConstant.INTEGER_DATA_TYPE, leftRowSize, rightColSize, resultUnit);
                isBothIntegerMatrix = true;
            } else {
                ret = new CMatrix(CConstant.REAL_DATA_TYPE, leftRowSize, rightColSize, resultUnit);
            }

            for (int i = 0; i < leftRowSize; i++) {
                for (int j = 0; j < rightColSize; j++) {
                    double mulVal = 0;
                    double thisVal, thatVal;
                    for (int k = 0; k < leftColSize; k++) {
                        thisVal = this.getDouble(i, k);
                        thatVal = dataObj.getDouble(k, j);
                        mulVal += thisVal * thatVal;
                    }
                    if (isBothIntegerMatrix) {
                        ret.setInt(i, j, (int) mulVal);
                    } else {
                        ret.setDouble(i, j, mulVal);
                    }
                }
            }
            return ret;
		} else {
			throw new IllegalArgumentException("Can't multiply " + this + " by " + obj);
		}
    }

    public String toString() {
        String ret = "[";
        List rowList = (List) getValue();
        for (Iterator i = rowList.iterator(); i.hasNext(); ) {
            List aRow = (List) i.next();
            for (Iterator j = aRow.iterator(); j.hasNext(); ) {
                ret = ret + j.next();
                if (j.hasNext()) {
                    ret = ret + " ";
                }
            }
            if (i.hasNext()) {
                ret = ret + "; ";
            }
        }
        return ret + "]";
    }
}
