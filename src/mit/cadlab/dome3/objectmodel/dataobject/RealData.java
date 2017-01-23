// RealData.java
package mit.cadlab.dome3.objectmodel.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.NumericQuantity;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.units.Quantity;
import mit.cadlab.dome3.util.UnitsException;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

public class RealData extends AbstractDataObject
        implements DomeReal
{

	protected Quantity quantity;
	// constraints

	public RealData()
	{
		quantity = new Quantity(new Double(0.0), Quantity.NO_UNIT);
	}

	public RealData(double v)
	{
		quantity = new Quantity(new Double(v), Quantity.NO_UNIT);
	}

	public RealData(Double v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeReal - null parameter");
		quantity = new Quantity(v, Quantity.NO_UNIT);
	}

	public RealData(DomeReal v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeReal - null parameter");
		quantity = new Quantity(v.getQuantity());
	}

	public RealData(double v, Unit unit)
	{
		quantity = new Quantity(new Double(v), unit);
	}

	public RealData(double v, String unit)
	{
		if (unit.equals(Quantity.NO_UNIT_STR))
			quantity = new Quantity(new Double(v), Quantity.NO_UNIT);
		else
			quantity = new Quantity(new Double(v), new Unit(unit));
	}

	public RealData(String unit)
	{
		this(0, unit);
	}

	public RealData(Element xmlElement)
	{
		super(xmlElement);
		Element q = (Element) xmlElement.selectSingleNode("/dataobject/quantity");
		quantity = new Quantity(q);
		if (quantity == null || !(quantity.getMagnitude() instanceof Double))
			throw new IllegalArgumentException("DomeReal - invalid xml quantity: " +
			                                   xmlElement.selectSingleNode("/dataobject/quantity").asXML());
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return new RealValueShadowListener();
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return new RealValueUnitShadowListener();
	}

	// DataObject interface
	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof NumericQuantity);
	}

	protected TypeInfo getTypeInfo()
	{
		return DomeReal.TYPE_INFO;
	}

	public DataObject duplicate()
	{
		return new RealData(this);
	}

	// DomeRealinterface
	public DomeReal getDomeReal()
	{
		return this;
	}

	public double getValue()
	{
        return quantity.getMagnitude().doubleValue();
	}

    /**
     * used for computation math functions such as sine
     * it is unit-dependent. for example, if the unit is pi, return the real value multiplied by 3.14..
     * if the unit is percent, return the real value multiplied by 0.01
     */
    public double getComputationalValue() {
        Unit unit = quantity.getUnit();
        return unit.isConstantUnit() ? getValueConstantUnit(unit) : quantity.getMagnitude().doubleValue();
    }

	public Object getValuesForXmlRpcUse ()
	{
		return quantity.getMagnitude();
	}

    private double getValueConstantUnit(Unit unit) {
        return unit.getConstantUnitFactor() * quantity.getMagnitude().doubleValue();
    }

	public List getValues()
	{
		return Collections.singletonList(new Double(getValue()));
	}

	public void setValues(List values)
	{
		if (values.size() > 0) {
			Object value = values.get(0);
			if (value instanceof Double)
				setRealValue((Double) value);
			else if (value instanceof Integer)
				setValue(((Integer) value).intValue());
			else if (value instanceof Boolean)
				setValue(((Boolean) value).booleanValue() ? 1 : 0);
		}
	}

	public void setValues(DataObject newObj)
	{
		Unit fromUnit = null;
		Number newValue = null;

		if (newObj instanceof NumericQuantity) { // DomeInteger or DomeReal
			fromUnit = newObj.getUnit();
			newValue = ((NumericQuantity) newObj).getQuantity().getMagnitude();
			if (!fromUnit.equals(getUnit())) // only convert if units are different
				newValue = new Double(getUnit().convertFrom(newValue.doubleValue(), fromUnit));
			if (newValue instanceof Integer)
				newValue = new Double(newValue.doubleValue());
		}
		else if (newObj instanceof BooleanData)
			newValue = new Double(((BooleanData) newObj).getBooleanValue().booleanValue() ? 1 : 0);
		setRealValue((Double)newValue);
	}

	public void setValue(double value)
	{
		setRealValue(new Double(value));
	}

	public Double getRealValue()
	{
		return (Double) quantity.getMagnitude();
	}

	public void setRealValue(Double value)
	{
		if (value == null) return;
		Double oldValue = getRealValue();
		quantity.setMagnitude(value);
		firePropertyChange(VALUE, oldValue, value);
	}

	// NumericQuantity interface
	public Unit getUnit()
	{
        return quantity.getUnit();
	}

	public void setUnit(Unit unit)
	{
		if (unit == null) return;
		Unit oldUnit = quantity.getUnit();
		quantity = quantity.changeUnit((Unit)unit.clone());
		firePropertyChange(UNIT, oldUnit, unit);
	}

	public void setUnit(String unitId)
	{
		setUnit(new Unit(unitId));
	}

	public Quantity getQuantity()
	{
		return quantity;
	}

	public void setQuantity(Quantity v)
	{
		setValue(v.getMagnitude().doubleValue());
		setUnit(v.getUnit());
	}

	public String toString()
	{
		return getQuantity().toString();
	}

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		xml.add(quantity.toXmlElement());
		return xml;
	}

	protected class RealValueShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof DataObject) {
				if (property.equals(NumericQuantity.VALUE)) {
					setValues((DataObject) obj);
				}
				if (property.equals(NumericQuantity.UNIT)) { // check for unit compatibility; do not set
					Unit thisUnit = getUnit();
					Unit newUnit = ((DataObject) obj).getUnit();
					if (thisUnit == null && newUnit == null)
						return; // compatible
					else if (thisUnit == null || newUnit == null || (!thisUnit.equivalent(newUnit)))
						throw new UnitsException(thisUnit.toString(), newUnit.toString());
				}
			}
		}
	}

	protected class RealValueUnitShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof DataObject) {
				if (property.equals(NumericQuantity.VALUE)) {
					setValues((DataObject) obj);
				}
				if (property.equals(NumericQuantity.UNIT)) {
					setUnit(((DataObject) obj).getUnit());
				}
			}
		}
	}

	public Object __add__(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v1 = this.getValue();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			//convert value of "that" into unit of "this"
			double converted_v2 = u1.convertFrom(v2, u2);

			// use unit of RealData
			return new RealData(v1 + converted_v2, (Unit)u1.clone());
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v1 = this.getValue();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			//convert value of "that" into unit of "this"
			double converted_v2 = u1.convertFrom(v2, u2);

			// use unit of LHS
			return new RealData(converted_v2 + v1, (Unit) u1.clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			double v1 = this.getValue();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			return new RealData(v1 + v2, (Unit) u1.clone());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
			double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			return new RealData(v1 + v2, (Unit) u1.clone());
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
	}

	public Object __sub__(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v1 = this.getValue();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			//convert value of "that" into unit of "this"
			double converted_v2 = u1.convertFrom(v2, u2);

			// use unit of RealData
			return new RealData(v1 - converted_v2, (Unit) u1.clone());
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v1 = this.getValue();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			//convert value of "that" into unit of "this"
			double converted_v2 = u1.convertFrom(v2, u2);

			// use unit of LHS
			return new RealData(v1 - converted_v2, (Unit) u1.clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			double v1 = this.getValue();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			return new RealData(v1 - v2, (Unit) u1.clone());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			return new RealData(v1 - v2, (Unit) u1.clone());
		} else {
			throw new IllegalArgumentException("Can't substract " + this + " to " + obj);
		}
	}

	public Object __mul__(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v1 = this.getValue();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();
			Unit u3 = null;

            if (u2.equals(Quantity.NO_UNIT))
                return new RealData(v2 * v1, (Unit) u1.clone());
            if (u1.equals(Quantity.NO_UNIT))
                return new RealData(v2 * v1, (Unit) u2.clone());

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

			return new RealData(v1 * converted_v2, u3);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v1 = this.getValue();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();
            Unit u3 = null;

            if (u2.equals(Quantity.NO_UNIT))
                return new RealData(v2 * v1, (Unit) u1.clone());
			if (u1.equals(Quantity.NO_UNIT))
				return new RealData(v2 * v1, (Unit) u2.clone());

			double converted_v2 = 0;
			try { //convert value of "that" into unit of "this" .. possible if both have the same type
				converted_v2 = u1.convertFrom(v2, u2);
                u3 = ((Unit) u1.clone()).mul(u1); // use unit of LHS
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

			return new RealData(converted_v2 * v1, u3);
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			double v1 = this.getValue();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			return new RealData(v1 * v2, (Unit) u1.clone());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();
			//System.out.println("v1 " + v1);
			//System.out.println("v2 " + v2);

			return new RealData(v1 * v2, (Unit) u1.clone());
		} else {
			throw new IllegalArgumentException("Can't multiply " + this + " to " + obj);
		}
	}

	public Object __div__(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v1 = this.getValue();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double converted_v2 = 0;
			Unit u3 = null;
			try {
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				//no unit
				u3 = new Unit();
			} catch (Exception e) {
				converted_v2 = v2;
				if (u1.equals(Quantity.NO_UNIT))
					u3 = ((Unit) u2.clone()).inv();
				else if (u2.equals(Quantity.NO_UNIT))
					u3 = (Unit) u1.clone();
				else {
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

			return new RealData(v1 / converted_v2, u3);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v1 = this.getValue();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			double converted_v2 = 0;
			Unit u3 = null;
			try {
				//convert value of "that" into unit of "this"
				converted_v2 = u1.convertFrom(v2, u2);
				//no unit
				u3 = new Unit();
			} catch (Exception e) {
				converted_v2 = v2;
				if (u1.equals(Quantity.NO_UNIT))
					u3 = ((Unit) u2.clone()).inv();
				else if (u2.equals(Quantity.NO_UNIT))
					u3 = (Unit) u1.clone();
				else {
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

			return new RealData(v1 / converted_v2, u3);
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			double v1 = this.getValue();
			double v2 = that.intValue();
			Unit u1 = this.getUnit();

			return new RealData(v1 / v2, (Unit) u1.clone());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();
			return new RealData(v1 / v2, (Unit) u1.clone());
		} else {
			throw new IllegalArgumentException("Can't divide " + this + " to " + obj);
		}
	}

	public Object __pow__(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v1 = this.getValue();
			int v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			if (!u2.equals(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit and can't be used as a power");

            //convert the unit itself
            Unit u3 = ((Unit) u1.clone()).pow(v2);

			double v3 = Math.pow(v1, v2);
			return new RealData(v3, u3);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v1 = this.getValue();
			double v2 = that.getValue();
			Unit u1 = this.getUnit();
			Unit u2 = that.getUnit();

			if (!u2.equals(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit and can't be used as a power");

			//convert the unit itself
			// if the result has units, all exponents for the units must be whole number
			/*if (!u1.equals(Quantity.NO_UNIT))
				if ((v2 % 1) != 0) // v2 is not a whole number
					throw new IllegalArgumentException(this + " has unit, and " + obj + " is not a whole number -> can't do power");*/
            Unit u3 = ((Unit) u1.clone()).pow(v2);

			double v3 = Math.pow(v1, v2);
			return new RealData(v3, u3);
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			double v1 = this.getValue();
			int v2 = that.intValue();
			Unit u1 = this.getUnit();

			//convert the unit itself
			Unit u3 = ((Unit) u1.clone()).pow(v2);

			double v3 = Math.pow(v1, v2);
			return new RealData(v3, u3);
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
			double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			//convert the unit itself

			// if the result has units, all exponents for the units must be whole number
/*			if (!u1.equals(Quantity.NO_UNIT))
				if ((v2 % 1) != 0) // v2 is not a whole number
					throw new IllegalArgumentException(obj + " is not a whole number -> can't do power");*/
            Unit u3 = ((Unit) u1.clone()).pow(v2);
			double v3 = Math.pow(v1, v2);
			return new RealData(v3, u3);
		} else {
			throw new IllegalArgumentException("Can't raise power " + this + " to " + obj);
		}
	}

	public Object __neg__()
	{
		double v1 = this.getValue();
		Unit u1 = this.getUnit();
		return new RealData(-v1, (Unit) u1.clone());
	}

	public Object __radd__(Object obj)
	{
		return this.__add__(obj);
	}

	public Object __rsub__(Object obj)
	{
		if (obj instanceof Number) {
			Number that = (Number) obj;
			RealData neg_this = (RealData) this.__neg__();
			return neg_this.__add__(that);
		} else {
			throw new IllegalArgumentException("Can't substract " + obj + " to " + this);
		}
	}

	public Object __rmul__(Object obj)
	{
		return this.__mul__(obj);
	}

	public Object __rdiv__(Object obj)
	{
		if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
            double v2 = that.doubleValue();
			Unit u1 = this.getUnit();

			Unit u3 = ((Unit) u1.clone()).inv();

			return new RealData(v2 / v1, u3);
		} else {
			throw new IllegalArgumentException("Can't divide " + obj + " to " + this);
		}
	}

	public Object __rpow__(Object obj)
	{
		Unit u1 = this.getUnit();
		if (!u1.equals(Quantity.NO_UNIT))
			throw new IllegalArgumentException(this + " has unit and can't be used as a power");

		if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
            double v2 = that.doubleValue();
			return new Double(Math.pow(v2, v1));
		} else {
			throw new IllegalArgumentException("Can't raise power " + obj + " to " + this);
		}
	}

	public Object __mod__(Object obj) {
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v1 = this.getValue();
			int v2 = that.getValue();
			// use unit of RealData
			return new RealData(v1%v2, (Unit) this.getUnit().clone());
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v1 = this.getValue();
			double v2 = that.getValue();
			// use unit of LHS
			return new RealData(v1%v2, (Unit) this.getUnit().clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			double v1 = this.getValue();
			int v2 = that.intValue();
			return new RealData(v1 % v2, (Unit) this.getUnit().clone());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v1 = this.getValue();
			double v2 = that.doubleValue();
			return new RealData(v1 % v2, (Unit) this.getUnit().clone());
		} else {
			throw new IllegalArgumentException("Can't mod " + this + " with " + obj);
		}
	}

	public double __double__()
	{
		return (new Double(this.getValue())).doubleValue();
	}

	public int __int__()
	{
		return (new Long(Math.round(this.getValue()))).intValue();
	}

    //add comparision

    public Object __lt__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getValue()<0.0) return new BooleanData(true);
       else return new BooleanData(false);
    }

    public Object __gt__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getValue() >0.0) return new BooleanData(true);
       else return new BooleanData(false);
    }


   public Object __ge__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getValue() >=0.0) return new BooleanData(true);
       else return new BooleanData(false);
    }

   public Object __le__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getValue() <=0.0) return new BooleanData(true);
       else return new BooleanData(false);
    }

    public Object __eq__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getValue() ==0.0) return new BooleanData(true);
       else return new BooleanData(false);
    }

    public Object __ne__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getValue() ==0.0) return new BooleanData(false);
       else return new BooleanData(true);
    }
    /*
     public boolean __lt__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getRealValue().doubleValue()<0.0) return true;
       else return false;
    }

    public boolean __gt__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getRealValue().doubleValue()>0.0) return true;
       else return false;
    }


   public boolean __ge__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getRealValue().doubleValue()>=0.0) return true;
       else return false;
    }

   public boolean __le__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getRealValue().doubleValue()<=0.0) return true;
       else return false;
    }

    public boolean __eq__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getRealValue().doubleValue()==0.0) return true;
       else return false;
    }

    public boolean __ne__(Object obj){
       RealData result=(RealData)__sub__(obj);
       if(result.getRealValue().doubleValue()==0.0) return false;
       else return true;
    }  */
}
