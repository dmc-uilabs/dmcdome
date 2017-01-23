package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;
import mit.cadlab.dome3.util.units.Quantity;

/**
 * plugin data object class that corresponds to dome real
 * the biggest difference from other plugin data types like MatlabReal is that
 * there is no JNI native object. we have just one java double member variable
 * that replaces the JNI native object and keeps the native-side value.
 *
 * User: Sangmok Han
 * Date: March 29, 2006
 */
public class CReal extends CDataObject implements Comparable {

    /** create CReal instance with No_Unit */
    public CReal(double value) {
        this(value, CConstant.NO_UNIT_STR);
    }

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CReal(double value, String unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setDoubleValue(value);
        this.setUnit(unit);
    }

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CReal(double value, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setDoubleValue(value);
        this.setUnit(unit);
    }

    /** new CReal("5.3"); */
    public CReal(String realStr) {
        this(new Double(realStr).doubleValue());
    }

    /** create CReal instance corresponding to DomeReal parameter. contructed CReal is an interface parameter  */
    public CReal(Parameter realParam) {
        super(realParam);

        this.copyFromDomePluginDataObjectToCatalogDataObject();
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
        Double pluginDataObjValue = ((RealData) getDomePluginDataObject()).getRealValue();
        setDoubleValue(pluginDataObjValue.doubleValue());
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        Double catDataObjValue = (Double) getValue();
        ((RealData) getDomePluginDataObject()).setRealValue(catDataObjValue);
    }

    public double getDoubleValue() {
        return ((Double) getValue()).doubleValue();
    }

    public void setDoubleValue(double value) {
        setValue(new Double(value));
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
		} else if (obj instanceof CMatrix) {
            return ((CMatrix) obj).multiply(this);
        } else if (obj instanceof CVector) {
            return ((CVector) obj).multiply(this);
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
        double compared = ((CReal) this.minus(obj)).getDoubleValue();
        if (compared < 0) {
            return -1;
        } else if (compared > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public Object power(Object obj) {
        if (obj instanceof CInteger) {
            obj = new Integer(((CInteger) obj).getIntegerValue());
        }
        if (obj instanceof Integer) {
            int powerCount = ((Integer) obj).intValue();
            if (powerCount == 0) {
                return new CReal(1, CUnit.NO_UNIT);
            }
            CReal ret = this;
            for (int i = 0; i < powerCount - 1; i++) {
                ret = (CReal) ret.multiply(this);
            }
            return ret;
        } else {
            throw new RuntimeException("CReal.power() takes an argument of Integer or CInteger type: " + obj.getClass().getName() + " is not acceptable.");
        }
    }

    public Object leftShift(Object obj) {
        /* by not checking this, we allow AAA with a unit to be assigned to BBB with no unit.
         * as a result of this assignment, BBB will keep the value of AAA, but won't keep the unit of AAA
         * for a case when we assign BBB to AAA, AAA will keep the value of BBB as is -- no unit conversion is performed -- and keep the current unit of AAA */
        //checkUnitConversionRule(obj);

        if (obj instanceof CReal) {
			CReal realDataObj = (CReal) obj;
            double convertedDouble = convertUnit(realDataObj.getDoubleValue(), realDataObj.getUnit(), this.getUnit());
            this.setDoubleValue(convertedDouble);
        } else if (obj instanceof CInteger) {
			CInteger intDataObj = (CInteger) obj;
            double convertedDouble = convertUnit(intDataObj.getDoubleValue(), intDataObj.getUnit(), this.getUnit());
            this.setDoubleValue(convertedDouble);
		} else if (obj instanceof Number) {
            Number numValue = (Number) obj;
            this.setDoubleValue(numValue.doubleValue());
		} else {
			throw new IllegalArgumentException("error in LHR.leftShit(RHS): " + obj);
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
        return "[CReal: value=" + getValue() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
    }
}
