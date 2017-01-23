package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
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
public class CVector extends CDataObject {
    private String valueType;

    /** create CMatrix instance corresponding to DomeMatrixData parameter. contructed CMatrix is an interface parameter  */
    public CVector(Parameter vectorParam) {
        super(vectorParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();

        if (DomeReal.TYPE_INFO.getTypeName().equalsIgnoreCase(((DomeVector) getDomePluginDataObject()).getValueType())) {
            this.valueType = CConstant.REAL_DATA_TYPE;
        } else if (DomeInteger.TYPE_INFO.getTypeName().equalsIgnoreCase(((DomeVector) getDomePluginDataObject()).getValueType())) {
            this.valueType = CConstant.INTEGER_DATA_TYPE;
        }
    }

    /** create CMatrix instance. contructed CMatrix is not an interface parameter. valueType is determined by class type of instances in rowList */
    public CVector(List row, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        String valueType = CConstant.REAL_DATA_TYPE;
        if (row.size() > 0 && row.get(0) instanceof Integer) {
            valueType = CConstant.INTEGER_DATA_TYPE;
        }

        this.setVectorValue(row);
        this.setUnit(unit);
        this.valueType = valueType;
    }

    /** create CMatrix instance. contructed CMatrix is not an interface parameter. valueType is determined by class type of instances in rowList */
    public CVector(String valueType, int size, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setVectorValue(new Vector()); // changed from new ArrayList()
        this.prepareToSetValue(size - 1);
        this.setUnit(unit);
        this.valueType = valueType;
    }


    public CVector(String valueType, int size) {
        if (! valueType.equals(CConstant.REAL_DATA_TYPE) && ! valueType.equals(CConstant.INTEGER_DATA_TYPE)) {
            throw new RuntimeException("valueType should be either CParameter.REAL_DATA_TYPE or CParameter.INTEGER_DATA_TYPE");
        }
        this.valueType = valueType;
        setVectorValue(createZeroRowVector(valueType, size));
    }

    /** numList is a List of Numbers */
    public void setVectorValue(List numList) {
        setValue(numList);
    }

    /** returns a List of Numbers - either Double or Integer */
    public List getVectorValue() {
        return (List) getValue();
    }

    private static List createZeroRowVector(String valueType, int cols) {
        List newRow = new Vector(); // changed from new ArrayList()
        if (CConstant.REAL_DATA_TYPE.equals(valueType)) {
            for (int i = 0; i < cols; i++) {
                newRow.add(new Double(0.0));
            }
        } else {
            for (int i = 0; i < cols; i++) {
                newRow.add(new Integer(0));
            }
        }
        return newRow;
    }

    /**
     * since CVector contains either Double or Integer elements, which are immutable after instantiation,
     * we don't need to create a new instance of each element.
     * putting all Double and Integer into a new array effectively clones this CVector.
     */
    public CVector(CVector src) {
        this.valueType = src.getValueType();
        setVectorValue(new Vector(src.getVectorValue()));
    }

    /**
     * new CVector("[3.0 4.5 3.2]");
     * new CVector("[3.0 4.5 3.2]|Real");
     * new CVector("[3 4 3]");
     * new CVector("[3 4 3]|Integer");
     */
    public CVector(String vectorStr) {
        this(DataObjectUtil.createVector(vectorStr), CUnit.NO_UNIT);
    }

    public CVector(CMatrix src) {
        this.valueType = src.getValueType();
        if (src.getRowSize() == 1) {
            this.valueType = src.getValueType();
            setVectorValue(new Vector(src.getRow(0)));
        } if (src.getColumnSize() == 1) {
            this.valueType = src.getValueType();
            setVectorValue(new Vector(src.getColumn(0)));
        }
    }

    public void setDataType(String newValueType) {
        if (! newValueType.equals(this.valueType)) {
            List newDataList = new Vector();
            for (Iterator i = getVectorValue().iterator(); i.hasNext(); ) {
                if (newValueType.equals(CConstant.REAL_DATA_TYPE)) {
                    newDataList.add(new Double(((Integer) i.next()).doubleValue()));
                } else if (newValueType.equals(CConstant.INTEGER_DATA_TYPE)) {
                    newDataList.add(new Integer((int) (((Double) i.next()).doubleValue() + 0.5)));
                }
            }
            setVectorValue(newDataList);
            this.valueType = newValueType;
        }
    }

    public int size() {
        return getVectorValue().size();
    }

    public void addDouble(double value) {
        getVectorValue().add(new Double(value));
    }

    public void addInt(int value) {
        getVectorValue().add(new Integer(value));
    }

    public void addNumber(int index, Number value) {
        getVectorValue().add(index, value);
    }

    private void prepareToSetValue(int index) {
        while (size() <= index) {
            if (CConstant.INTEGER_DATA_TYPE.equals(valueType)) {
                getVectorValue().add(new Integer(0));
            } else {
                getVectorValue().add(new Double(0));
            }
        }
    }

    public void setDouble(int index, double value) {
        prepareToSetValue(index);
        getVectorValue().set(index, new Double(value));
    }

    public void setInt(int index, int value) {
        prepareToSetValue(index);
        getVectorValue().set(index, new Integer(value));
    }

    public void setNumber(int index, Number value) {
        prepareToSetValue(index);
        getVectorValue().set(index, value);
    }

    public double getDouble(int index) {
        return ((Number) getVectorValue().get(index)).doubleValue();
    }

    public int getInt(int index) {
        return ((Number) getVectorValue().get(index)).intValue();
    }

    public Number getNumber(int index) {
        return (Number) getVectorValue().get(index);
    }

    /* vectorStr is parsed using DataObjectUtil.createVector() method */
    public void setVectorValue(String vectorStr) {
        setVectorValue(DataObjectUtil.createVector(vectorStr));
    }

    /** valueType is either CConstant.REAL_DATA_TYPE or CConstant.INTEGER_DATA_TYPE. */
    public String getValueType() {
        return valueType;
    }

    /** convert the value of the given matrix into a unit given as toUnit */
    private static void convertUnit(CVector givenVector, CUnit toUnit) {
        int size = givenVector.size();
        if (CConstant.REAL_DATA_TYPE.equals(givenVector.getValueType())) {
            for (int idx = 0; idx < size; idx++) {
                double convertedDouble = convertUnit(givenVector.getDouble(idx), givenVector.getUnit(), toUnit);
                givenVector.setDouble(idx, convertedDouble);
            }
        } else {
            for (int idx = 0; idx < size; idx++) {
                int convertedInt = (int) convertUnit(givenVector.getInt(idx), givenVector.getUnit(), toUnit);
                givenVector.setInt(idx, convertedInt);
            }
        }
    }

    public CReal getAt(int index) {
        return new CReal(getDouble(index), getUnit());
    }

    public Object leftShift(Object obj) {
        /* by not checking this, we allow AAA with a unit to be assigned to BBB with no unit.
         * as a result of this assignment, BBB will keep the value of AAA, but won't keep the unit of AAA
         * for a case when we assign BBB to AAA, AAA will keep the value of BBB as is -- no unit conversion is performed -- and keep the current unit of AAA */
        //checkUnitConversionRule(obj);

        if (obj instanceof CVector) {
			CVector rhsVector = (CVector) obj;
            CVector lhsVector = new CVector(rhsVector);
            convertUnit(lhsVector, this.getUnit());
            this.setVectorValue(lhsVector.getVectorValue());
        } else if (obj instanceof CMatrix) {
            /* 1 x N matrix is converted into a vector */
			CMatrix matrixDataObj = (CMatrix) obj;
            CVector lhsVector = new CVector(matrixDataObj);
            convertUnit(lhsVector, this.getUnit());
            this.setVectorValue(lhsVector.getVectorValue());
        } else {
			throw new IllegalArgumentException("error in LHR.leftShit(RHS)");
		}

        return this;
    }

    public Object plus(Object obj) {
        CVector ret = null;
        int size = this.size();

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
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }

            double convertedDouble = convertUnit(((CReal) dataObj).getDoubleValue(), dataObj.getUnit(), this.getUnit());
            for (int i = 0; i < size; i++) {
                if (isIntegerMatrixWithSameUnit) {
                    ret.setInt(i, this.getInt(i) + ((CInteger) dataObj).getIntegerValue());
                } else {
                    ret.setDouble(i, this.getDouble(i) + convertedDouble);
                }
            }
            return ret;
		} else if (obj instanceof CVector) {
			CVector dataObj = (CVector) obj;
            if (dataObj.size() != this.size()) {
                throw new IllegalArgumentException("Can't add " + this + " to " + obj + " : both vectors should have the same size.");
            }

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }


            for (int i = 0; i < size; i++) {
                if (isIntegerMatrixWithSameUnit) {
                    ret.setInt(i, this.getInt(i) + dataObj.getInt(i));
                } else {
                    double convertedDouble = convertUnit(dataObj.getDouble(i), dataObj.getUnit(), this.getUnit());
                    ret.setDouble(i, this.getDouble(i) + convertedDouble);
                }
            }
            return ret;
		} else if (obj instanceof CMatrix) {
			CMatrix dataObj = (CMatrix) obj;
            boolean isColumnSizeOne = false;
            if (dataObj.getRowSize() == this.size() && dataObj.getColumnSize() == 1) {
                // can be added
            } else if (dataObj.getColumnSize() == this.size() && dataObj.getRowSize() == 1) {
                // can be added
                isColumnSizeOne = true;
            } else {
                throw new IllegalArgumentException("Can't add " + this + " to " + obj + " : the matrix should be a 1xN or Nx1 matrix having the same row size or column size as the vector.");
            }

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }


            for (int i = 0; i < size; i++) {
                if (isIntegerMatrixWithSameUnit) {
                    if (isColumnSizeOne) {
                        ret.setInt(i, this.getInt(i) + dataObj.getInt(i, 0));
                    } else {
                        ret.setInt(i, this.getInt(i) + dataObj.getInt(0, i));
                    }
                } else {
                    if (isColumnSizeOne) {
                        double convertedDouble = convertUnit(dataObj.getDouble(i, 0), dataObj.getUnit(), this.getUnit());
                        ret.setDouble(i, this.getDouble(i) + convertedDouble);
                    } else {
                        double convertedDouble = convertUnit(dataObj.getDouble(0, i), dataObj.getUnit(), this.getUnit());
                        ret.setDouble(i, this.getDouble(i) + convertedDouble);
                    }
                }
            }
            return ret;
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
    }

    public Object minus(Object obj) {
        CVector ret = null;
        int size = this.size();

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
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }

            double convertedDouble = convertUnit(((CReal) dataObj).getDoubleValue(), dataObj.getUnit(), this.getUnit());
            for (int i = 0; i < size; i++) {
                if (isIntegerMatrixWithSameUnit) {
                    ret.setInt(i, this.getInt(i) - ((CInteger) dataObj).getIntegerValue());
                } else {
                    ret.setDouble(i, this.getDouble(i) - convertedDouble);
                }
            }
            return ret;
		} else if (obj instanceof CVector) {
			CVector dataObj = (CVector) obj;
            if (dataObj.size() != this.size()) {
                throw new IllegalArgumentException("Can't add " + this + " to " + obj + " : both vectors should have the same size.");
            }

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }


            for (int i = 0; i < size; i++) {
                if (isIntegerMatrixWithSameUnit) {
                    ret.setInt(i, this.getInt(i) - dataObj.getInt(i));
                } else {
                    double convertedDouble = convertUnit(dataObj.getDouble(i), dataObj.getUnit(), this.getUnit());
                    ret.setDouble(i, this.getDouble(i) - convertedDouble);
                }
            }
            return ret;
		} else if (obj instanceof CMatrix) {
			CMatrix dataObj = (CMatrix) obj;
            boolean isColumnSizeOne = false;
            if (dataObj.getRowSize() == this.size() && dataObj.getColumnSize() == 1) {
                // can be added
            } else if (dataObj.getColumnSize() == this.size() && dataObj.getRowSize() == 1) {
                // can be added
                isColumnSizeOne = true;
            } else {
                throw new IllegalArgumentException("Can't add " + this + " to " + obj + " : the matrix should be a 1xN or Nx1 matrix having the same row size or column size as the vector.");
            }

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            boolean isIntegerMatrixWithSameUnit = false;
            if (CConstant.INTEGER_DATA_TYPE.equals(this.getValueType())
                    && CConstant.INTEGER_DATA_TYPE.equals(dataObj.getValueType())
                    && dataObj.getUnit().equals(this.getUnit())) {
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isIntegerMatrixWithSameUnit = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }


            for (int i = 0; i < size; i++) {
                if (isIntegerMatrixWithSameUnit) {
                    if (isColumnSizeOne) {
                        ret.setInt(i, this.getInt(i) + dataObj.getInt(i, 0));
                    } else {
                        ret.setInt(i, this.getInt(i) + dataObj.getInt(0, i));
                    }
                } else {
                    if (isColumnSizeOne) {
                        double convertedDouble = convertUnit(dataObj.getDouble(i, 0), dataObj.getUnit(), this.getUnit());
                        ret.setDouble(i, this.getDouble(i) - convertedDouble);
                    } else {
                        double convertedDouble = convertUnit(dataObj.getDouble(0, i), dataObj.getUnit(), this.getUnit());
                        ret.setDouble(i, this.getDouble(i) - convertedDouble);
                    }
                }
            }
            return ret;
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
    }

    public Object multiply(Object obj) {
        CVector ret = null;
        int size = this.size();

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
                ret = new CVector(CConstant.INTEGER_DATA_TYPE, size, resultUnit);
                isBothIntegerMatrix = true;
            } else {
                ret = new CVector(CConstant.REAL_DATA_TYPE, size, resultUnit);
            }

            for (int i = 0; i < size; i++) {
                    if (dataObj instanceof CInteger) {
                        if (isBothIntegerMatrix) {
                            ret.setInt(i, this.getInt(i) * ((CInteger) dataObj).getIntegerValue());
                        } else {
                            ret.setDouble(i, this.getDouble(i) * ((CInteger) dataObj).getIntegerValue());
                        }
                    } else {
                        ret.setDouble(i, this.getDouble(i) * ((CReal) dataObj).getDoubleValue());
                    }
            }
            return ret;
		} else {
			throw new IllegalArgumentException("Can't multiply " + this + " by " + obj);
		}
    }

    public String toString() {
        String ret = "[";
        for (Iterator i = getVectorValue().iterator(); i.hasNext(); ) {
            ret = ret + i.next();
            if (i.hasNext()) {
                ret = ret + " ";
            }
        }
        return ret + "]";
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        DomeVectorData domeVectorDataObj = (DomeVectorData) getDomePluginDataObject();
        List srcRow = domeVectorDataObj.getData();
        setVectorValue(new Vector(srcRow));
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        List catDataObjValue = (List) getValue();
        Vector valueBag = new Vector();
        valueBag.add(catDataObjValue);
        ((DomeVectorData) getDomePluginDataObject()).setValues(valueBag);
    }
}
