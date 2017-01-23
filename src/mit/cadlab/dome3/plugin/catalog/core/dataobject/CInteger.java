package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;
import mit.cadlab.dome3.util.units.Quantity;

/**
 * plugin data object class that corresponds to dome integer
 * the biggest difference from other plugin data types like MatlabInteger is that
 * there is no JNI native object. we have just one java double member variable
 * that replaces the JNI native object and keeps the native-side value.
 *
 * User: Sangmok Han
 * Date: March 29, 2006
 */
public class CInteger extends CDataObject implements Comparable {

    /** create CReal instance with No_Unit */
    public CInteger(int value) {
        this(value, CConstant.NO_UNIT_STR);
    }

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CInteger(int value, String unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setIntegerValue(value);
        this.setUnit(unit);
    }

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CInteger(int value, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setIntegerValue(value);
        this.setUnit(unit);
    }

    /** create CReal instance corresponding to DomeReal parameter. contructed CReal is an interface parameter  */
    public CInteger(Parameter intParam) {
        super(intParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();
    }

    /** new CInteger("5"); */
    public CInteger(String intStr) {
        this(new Integer(intStr).intValue());
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        Integer pluginDataObjValue = ((IntegerData) getDomePluginDataObject()).getIntegerValue();
        setIntegerValue(pluginDataObjValue.intValue());
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        Integer catDataObjValue = (Integer) getValue();
        ((IntegerData) getDomePluginDataObject()).setIntegerValue(catDataObjValue);
    }

    public double getDoubleValue() {
        return ((Integer) getValue()).doubleValue();
    }

    public void setDoubleValue(double value) {
        setIntegerValue((int) value);
    }

    public int getIntegerValue() {
        return ((Integer) getValue()).intValue();
    }

    public void setIntegerValue(int value) {
        setValue(new Integer(value));
    }

    public Object plus(Object obj) {
        if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject dataObj = (CDataObject) obj;
            double objValue = 0;
            if (obj instanceof CReal) {
                objValue = ((CReal) dataObj).getDoubleValue();
            } else if (obj instanceof CInteger) {
                objValue = ((CInteger) dataObj).getDoubleValue();
            }
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            double convertedDouble = convertUnit(objValue, dataObj.getUnit(), this.getUnit());
            return new CReal(this.getDoubleValue() + convertedDouble, resultUnit);
		} else if (obj instanceof Number) {
            Number intValue = (Number) obj;
            return new CReal(this.getDoubleValue() + intValue.doubleValue(), this.getUnit().cloneUnit());
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
    }

    public Object minus(Object obj) {
        if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject dataObj = (CDataObject) obj;
            double objValue = 0;
            if (obj instanceof CReal) {
                objValue = ((CReal) dataObj).getDoubleValue();
            } else if (obj instanceof CInteger) {
                objValue = ((CInteger) dataObj).getDoubleValue();
            }
            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);
            double convertedDouble = convertUnit(objValue, dataObj.getUnit(), this.getUnit());
            return new CReal(this.getDoubleValue() - convertedDouble, resultUnit);
		} else if (obj instanceof Number) {
            Number intValue = (Number) obj;
            return new CReal(this.getDoubleValue() - intValue.doubleValue(), this.getUnit().cloneUnit());
		} else {
			throw new IllegalArgumentException("Can't substract " + obj + " from " + this);
		}
    }

    public Object multiply(Object obj) {
		if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject that = (CDataObject) obj;
			double v1 = this.getDoubleValue();
            double v2 = 0;
            if (obj instanceof CReal) {
                v2 = ((CReal) that).getDoubleValue();
            } else if (obj instanceof CInteger) {
                v2 = ((CInteger) that).getIntegerValue();
            }

			Unit u1 = this.getUnit().getUnit();
			Unit u2 = that.getUnit().getUnit();
			Unit u3 = null;

            if (u2.equals(Quantity.NO_UNIT)) {
                return new CReal(v2 * v1, new CUnit((Unit) u1.clone()));
            }
            if (u1.equals(Quantity.NO_UNIT)) {
                return new CReal(v2 * v1, new CUnit((Unit) u2.clone()));
            }

			double converted_v2 = 0;
			try { //convert value of "that" into unit of "this" .. possible if both have the same type
				converted_v2 = u1.convertFrom(v2, u2);
                u3 = ((Unit) u1.clone()).mul(u1); // use unit of real
            } catch (Exception e) { // units not the same type.. make compound unit
                converted_v2 = v2;

                Unit u1Non = u1;
                Unit u2Non = u2;
                if (u1.getFunction() != null) {
                    u1Non = u1.getNonFunctionVersion();
                    v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
                }
                if (u2.getFunction() != null) {
                    u2Non = u2.getNonFunctionVersion();
                    converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
                }
                u3 = ((Unit) u1Non.clone()).mul(u2Non);
            }
			return new CReal(v1 * converted_v2, new CUnit(u3));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getDoubleValue();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit().getUnit();
			return new CReal(v1 * v2, new CUnit((Unit) u1.clone()));
		} else {
			throw new IllegalArgumentException("Can't multiply " + this + " to " + obj);
		}
    }

    public Object divide(Object obj) {
        return this.div(obj);
    }

    public Object div(Object obj) {
		if (obj instanceof CReal || obj instanceof CInteger) {
			CDataObject that = (CDataObject) obj;
			double v1 = this.getDoubleValue();
            double v2 = 0;
            if (obj instanceof CReal) {
                v2 = ((CReal) that).getDoubleValue();
            } else if (obj instanceof CInteger) {
                v2 = ((CInteger) that).getIntegerValue();
            }
			Unit u1 = this.getUnit().getUnit();
			Unit u2 = that.getUnit().getUnit();

			double converted_v2 = 0;
			Unit u3 = null;
			try {
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				//no unit
				u3 = new Unit();
			} catch (Exception e) {
				converted_v2 = v2;
				if (u1.equals(Quantity.NO_UNIT)) {
					u3 = ((Unit) u2.clone()).inv();
                } else if (u2.equals(Quantity.NO_UNIT)) {
					u3 = (Unit) u1.clone();
                } else {
					Unit u1Non = u1;
					Unit u2Non = u2;
					if (u1.getFunction() != null) {
						u1Non = u1.getNonFunctionVersion();
						v1 = u1.isDegTempUnit() ? v1 : u1Non.convertFrom(v1, u1);
					}
					if (u2.getFunction() != null) {
						u2Non = u2.getNonFunctionVersion();
						converted_v2 = u2.isDegTempUnit() ? converted_v2 : u2Non.convertFrom(converted_v2, u2);
					}
					u3 = ((Unit) u1Non.clone()).div(u2Non);
				}
			}

			return new CReal(v1 / converted_v2, new CUnit(u3));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getDoubleValue();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit().getUnit();
			return new CReal(v1 / v2, new CUnit((Unit) u1.clone()));
		} else {
			throw new IllegalArgumentException("Can't divide " + this + " to " + obj);
		}
    }

    public boolean equals(Object obj) {
        return (((CReal) this.minus(obj)).getDoubleValue() == 0);
    }

    public int compareTo(Object obj) {
        return (int) ((CReal) this.minus(obj)).getDoubleValue();
    }

    public Object leftShift(Object obj) {
        /* by not checking this, we allow AAA with a unit to be assigned to BBB with no unit.
         * as a result of this assignment, BBB will keep the value of AAA, but won't keep the unit of AAA
         * for a case when we assign BBB to AAA, AAA will keep the value of BBB as is -- no unit conversion is performed -- and keep the current unit of AAA */
        //checkUnitConversionRule(obj);

        if (obj instanceof CReal) {
			CReal realDataObj = (CReal) obj;
            double convertedDouble = convertUnit(realDataObj.getDoubleValue(), realDataObj.getUnit(), this.getUnit());
            this.setIntegerValue((int) convertedDouble);
        } else if (obj instanceof CInteger) {
			CInteger realDataObj = (CInteger) obj;
            double convertedDouble = convertUnit(realDataObj.getDoubleValue(), realDataObj.getUnit(), this.getUnit());
            this.setIntegerValue((int) convertedDouble);
		} else if (obj instanceof Number) {
            Number numValue = (Number) obj;
            this.setIntegerValue((int) numValue.doubleValue());
		} else {
			throw new IllegalArgumentException("error in LHR.leftShit(RHS)");
		}
        return this;
    }

    public String toString() {
        String mapped = "false";
        if (isAssociatedWithInterfaceInputParameter()) {
            mapped = "itf input param";
        }

        if (isAssociatedWithInterfaceOutputParameter()) {
            mapped = "itf output param";
        }
        return "[CInteger: value=" + getValue() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
    }
}
