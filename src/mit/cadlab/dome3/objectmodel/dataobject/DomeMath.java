// DomeMath.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.dataobject;

import Jama.LUDecomposition;
import Jama.Matrix;
import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.units.Quantity;
import cern.jet.math.Functions;

public class DomeMath
{
	public static final double LOG10 = Math.log(10);

	public static boolean isPrimitiveNumberType(Object obj)
	{
		if (obj instanceof Number)
			return true;
		return false;
	}

	/**
	 * sin()
	 * Returns a double for int, float, double inputs
	 * Returns a double for RealData, IntegerData inputs (Allowable input units = deg, rad, no unit (or equivalent constant unit))
	 * Result has no unit
	 */
	public static double sin(Object obj)
	{
        Unit deg = new Unit("deg");
        Unit rad = new Unit("rad");

		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if ((!u2.equals(deg)) && (!u2.equals(rad))
			        && (!u2.equivalent(Quantity.NO_UNIT)))
				throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find sin of " + obj);

			if (u2.equals(deg))
				v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

			// no unit for result
			return Math.sin(v2);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if ((!u2.equals(deg)) && (!u2.equals(rad))
			        && (!u2.equivalent(Quantity.NO_UNIT)))
				throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find sin of " + obj);

			if (u2.equals(deg))
				v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

			// no unit for result
			return Math.sin(v2);
		} else if (obj instanceof Number) { //assume that primitive numbers are of radian unit
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return Math.sin(v2);
		} else {
			throw new IllegalArgumentException("Can't find sin of " + obj);
		}
	}

	/**
	 * asin()
	 * Returns a double for int, float, double inputs
	 * Returns RealData for RealData, IntegerData inputs (Allowable input units = No_Unit)
	 * Input value has to be within (-1,1)
	 * result has rad unit (except for primitive number)
	 */
	public static Object asin(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find asin of " + obj);

			return new RealData(Math.asin(v2), new Unit("rad"));
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find asin of " + obj);

			return new RealData(Math.asin(v2), new Unit("rad"));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new RealData(Math.asin(v2), new Unit("rad"));
		} else {
			throw new IllegalArgumentException("Can't find asin of " + obj);
		}
	}

	/**
	 * cos()
	 * Returns a double for int, float, double inputs
	 * Returns a double for RealData, IntegerData inputs (Allowable input units = deg, rad, no unit (or equivalent constant unit))
	 * Result has no unit
	 */
	public static double cos(Object obj)
	{
        Unit deg = new Unit("deg");
        Unit rad = new Unit("rad");
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if ((!u2.equals(deg)) && (!u2.equals(rad))
			        && (!u2.equivalent(Quantity.NO_UNIT)))
				throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find cos of " + obj);

			if (u2.equals(deg))
				v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

			// no unit for result
			return Math.cos(v2);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if ((!u2.equals(deg)) && (!u2.equals(rad))
			        && (!u2.equivalent(Quantity.NO_UNIT)))
				throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find cos of " + obj);

			if (u2.equals(deg))
				v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

			// no unit for result
			return Math.cos(v2);
		} else if (obj instanceof Number) { //assume that primitive numbers are of radian unit
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return Math.cos(v2);
		} else {
			throw new IllegalArgumentException("Can't find cos of " + obj);
		}
	}

	/**
	 * acos()
	 * Returns a double for int, float, double inputs
	 * Returns RealData for RealData, IntegerData inputs (Allowable input units = No_Unit)
	 * Input value has to be within (-1,1)
	 * result has rad unit (except for primitive number)
	 */
	public static Object acos(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find acos of " + obj);

			return new RealData(Math.acos(v2), new Unit("rad"));
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find acos of " + obj);

			return new RealData(Math.acos(v2), new Unit("rad"));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new RealData(Math.acos(v2), new Unit("rad"));
		} else {
			throw new IllegalArgumentException("Can't find acos of " + obj);
		}
	}

	/**
	 * tan()
	 * Returns a double for int, float, double inputs
	 * Returns a double for RealData, IntegerData inputs (Allowable input units = deg, rad, no unit (or equivalent constant unit))
	 * Result has no unit
	 */
	public static double tan(Object obj)
	{
        Unit deg = new Unit("deg");
        Unit rad = new Unit("rad");
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if ((!u2.equals(deg)) && (!u2.equals(rad))
			        && (!u2.equivalent(Quantity.NO_UNIT)))
				throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find tan of " + obj);

			if (u2.equals(deg))
				v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

			// no unit for result
			return Math.tan(v2);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
				throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find tan of " + obj);

            if (u2.equals(deg))
				v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

			// no unit for result
			return Math.tan(v2);
		} else if (obj instanceof Number) { //assume that primitive numbers are of radian unit
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return Math.tan(v2);
		} else {
			throw new IllegalArgumentException("Can't find tan of " + obj);
		}
	}

	/**
	 * atan()
	 * Returns a double for int, float, double inputs
	 * Returns RealData for RealData, IntegerData inputs (Allowable input units = No_Unit)
	 * result has rad unit (except for primitive number)
	 */
	public static Object atan(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find atan of " + obj);

			return new RealData(Math.atan(v2), new Unit("rad"));
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find atan of " + obj);

			return new RealData(Math.atan(v2), new Unit("rad"));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new RealData(Math.atan(v2), new Unit("rad"));
		} else {
			throw new IllegalArgumentException("Can't find atan of " + obj);
		}
	}

    /**
     * sinh()
     * Returns a double for int, float, double inputs
     * Returns a double for RealData, IntegerData inputs (Allowable input units = deg, rad, no unit (or equivalent constant unit))
     * Result has no unit
     */
    public static double sinh(Object obj) {
        Unit deg = new Unit("deg");
        Unit rad = new Unit("rad");

        if (obj instanceof IntegerData) {
            IntegerData that = (IntegerData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
                throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find sinh of " + obj);

            if (u2.equals(deg))
                v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

            // no unit for result
            return Functions.sinh.apply(v2);
        } else if (obj instanceof RealData) {
            RealData that = (RealData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
                throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find sinh of " + obj);

            if (u2.equals(deg))
                v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

            // no unit for result
            return Functions.sinh.apply(v2);
        } else if (obj instanceof Number) { //assume that primitive numbers are of radian unit
            Number that = (Number) obj;
            double v2 = that.doubleValue();
            return Functions.sinh.apply(v2);
        } else {
            throw new IllegalArgumentException("Can't find sinh of " + obj);
        }
    }

    /**
     * asinh()
     * Returns a double for int, float, double inputs
     * Returns RealData for RealData, IntegerData inputs (Allowable input units = No_Unit)
     * result has rad unit (except for primitive number)
     */
    public static Object asinh(Object obj) {
        if (obj instanceof IntegerData) {
            IntegerData that = (IntegerData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if (!u2.equivalent(Quantity.NO_UNIT))
                throw new IllegalArgumentException(obj + " has unit. Can't find asinh of " + obj);

            return new RealData(Functions.asinh.apply(v2), new Unit("rad"));
        } else if (obj instanceof RealData) {
            RealData that = (RealData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if (!u2.equivalent(Quantity.NO_UNIT))
                throw new IllegalArgumentException(obj + " has unit. Can't find asinh of " + obj);

            return new RealData(Functions.asinh.apply(v2), new Unit("rad"));
        } else if (obj instanceof Number) {
            Number that = (Number) obj;
            double v2 = that.doubleValue();
            return new RealData(Functions.asinh.apply(v2), new Unit("rad"));
        } else {
            throw new IllegalArgumentException("Can't find asinh of " + obj);
        }
    }

    /**
     * cosh()
     * Returns a double for int, float, double inputs
     * Returns a double for RealData, IntegerData inputs (Allowable input units = deg, rad, no unit (or equivalent constant unit))
     * Result has no unit
     */
    public static double cosh(Object obj) {
        Unit deg = new Unit("deg");
        Unit rad = new Unit("rad");
        if (obj instanceof IntegerData) {
            IntegerData that = (IntegerData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
                throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find cosh of " + obj);

            if (u2.equals(deg))
                v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

            // no unit for result
            return Functions.cosh.apply(v2);
        } else if (obj instanceof RealData) {
            RealData that = (RealData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
                throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find cosh of " + obj);

            if (u2.equals(deg))
                v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

            // no unit for result
            return Functions.cosh.apply(v2);
        } else if (obj instanceof Number) { //assume that primitive numbers are of radian unit
            Number that = (Number) obj;
            double v2 = that.doubleValue();
            return Functions.cosh.apply(v2);
        } else {
            throw new IllegalArgumentException("Can't find cosh of " + obj);
        }
    }

    /**
     * acosh()
     * Returns a double for int, float, double inputs
     * Returns RealData for RealData, IntegerData inputs (Allowable input units = No_Unit)
     * result has rad unit (except for primitive number)
     */
    public static Object acosh(Object obj) {
        if (obj instanceof IntegerData) {
            IntegerData that = (IntegerData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if (!u2.equivalent(Quantity.NO_UNIT))
                throw new IllegalArgumentException(obj + " has unit. Can't find acosh of " + obj);

            return new RealData(Functions.acosh.apply(v2), new Unit("rad"));
        } else if (obj instanceof RealData) {
            RealData that = (RealData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if (!u2.equivalent(Quantity.NO_UNIT))
                throw new IllegalArgumentException(obj + " has unit. Can't find acosh of " + obj);

            return new RealData(Functions.acosh.apply(v2), new Unit("rad"));
        } else if (obj instanceof Number) {
            Number that = (Number) obj;
            double v2 = that.doubleValue();
            return new RealData(Functions.acosh.apply(v2), new Unit("rad"));
        } else {
            throw new IllegalArgumentException("Can't find acosh of " + obj);
        }
    }

    /**
     * tanh()
     * Returns a double for int, float, double inputs
     * Returns a double for RealData, IntegerData inputs (Allowable input units = deg, rad, no unit (or equivalent constant unit))
     * Result has no unit
     */
    public static double tanh(Object obj) {
        Unit deg = new Unit("deg");
        Unit rad = new Unit("rad");
        if (obj instanceof IntegerData) {
            IntegerData that = (IntegerData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
                throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find tanh of " + obj);

            if (u2.equals(deg))
                v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

            // no unit for result
            return Functions.tanh.apply(v2);
        } else if (obj instanceof RealData) {
            RealData that = (RealData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if ((!u2.equals(deg)) && (!u2.equals(rad))
                    && (!u2.equivalent(Quantity.NO_UNIT)))
                throw new IllegalArgumentException("Unit is neither deg nor rad nor no unit. Can't find tanh of " + obj);

            if (u2.equals(deg))
                v2 = rad.convertFrom(v2, u2);
            else if (u2.isConstantUnit())
                v2 = that.getComputationalValue();

            // no unit for result
            return Functions.tanh.apply(v2);
        } else if (obj instanceof Number) { //assume that primitive numbers are of radian unit
            Number that = (Number) obj;
            double v2 = that.doubleValue();
            return Functions.tanh.apply(v2);
        } else {
            throw new IllegalArgumentException("Can't find tanh of " + obj);
        }
    }

    /**
     * atanh()
     * Returns a double for int, float, double inputs
     * Returns RealData for RealData, IntegerData inputs (Allowable input units = No_Unit)
     * result has rad unit (except for primitive number)
     */
    public static Object atanh(Object obj) {
        if (obj instanceof IntegerData) {
            IntegerData that = (IntegerData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if (!u2.equivalent(Quantity.NO_UNIT))
                throw new IllegalArgumentException(obj + " has unit. Can't find atanh of " + obj);

            return new RealData(Functions.atanh.apply(v2), new Unit("rad"));
        } else if (obj instanceof RealData) {
            RealData that = (RealData) obj;
            double v2 = that.getValue();
            Unit u2 = that.getUnit();

            if (!u2.equivalent(Quantity.NO_UNIT))
                throw new IllegalArgumentException(obj + " has unit. Can't find atanh of " + obj);

            return new RealData(Functions.atanh.apply(v2), new Unit("rad"));
        } else if (obj instanceof Number) {
            Number that = (Number) obj;
            double v2 = that.doubleValue();
            return new RealData(Functions.atanh.apply(v2), new Unit("rad"));
        } else {
            throw new IllegalArgumentException("Can't find atanh of " + obj);
        }
    }

	/**
	 * exp()
	 * Returns a double for int, float, double inputs
	 * Returns a double for RealData, IntegerData inputs (Allowable input units = No_Unit)
	 * Result has no unit
	 */
	public static double exp(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find exp of " + obj);

			// no unit for result
			return Math.exp(v2);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find exp of " + obj);

			// no unit for result
			return Math.exp(v2);
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return Math.exp(v2);
		} else {
			throw new IllegalArgumentException("Can't find exp of " + obj);
		}
	}

	/**
	 * log() -- natural log
	 * Returns a double for int, float, double inputs
	 * Returns a double for RealData, IntegerData inputs (Allowable input units = no unit)
	 * Result has no unit
	 */
	public static double log(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find log of " + obj);

			return Math.log(v2);
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find log of " + obj);

			return Math.log(v2);
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return Math.log(v2);
		} else {
			throw new IllegalArgumentException("Can't find log of " + obj);
		}
	}

	/**
	 * log10() -- log base 10
	 * Returns a double for int, float, double inputs
	 * Returns a double for RealData, IntegerData inputs (Allowable input units = no unit)
	 * Result has no unit
	 */
	public static double log10(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find log10 of " + obj);

			return Math.log(v2)/LOG10;
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();

			if (!u2.equivalent(Quantity.NO_UNIT))
				throw new IllegalArgumentException(obj + " has unit. Can't find log10 of " + obj);

			return Math.log(v2)/LOG10;
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return Math.log(v2)/LOG10;
		} else {
			throw new IllegalArgumentException("Can't find log10 of " + obj);
		}
	}

	/**
	 * abs()
	 * Returns an int for int input
	 * Returns a double for float, double inputs
	 * Returns RealData for RealData input (Allowable input units = all units)
	 * Returns IntegerData for IntegerData input (Allowable input units = all units)
	 * Result has the same unit
	 */
	public static Object abs(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			int v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData(Math.abs(v2), (Unit)u2.clone());
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new RealData(Math.abs(v2), (Unit) u2.clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			int v2 = that.intValue();
			return new Integer(Math.abs(v2));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new Double(Math.abs(v2));
		} else
			throw new IllegalArgumentException("Can't find abs of " + obj);
	}

	/**
	 * sqrt()
	 * Returns a double for int, float, double inputs
	 * Returns RealData for RealData, IntegerData inputs (Allowable input units = all units)
	 * Result has the allowable derived unit
	 */
	public static Object sqrt(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new RealData(Math.sqrt(v2), ((Unit) u2.clone()).sqrt());
		} else if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new RealData(Math.sqrt(v2), ((Unit) u2.clone()).sqrt());
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new Double(Math.sqrt(v2));
		} else {
			throw new IllegalArgumentException("Can't find sqrt of " + obj);
		}
	}

	/**
	 * floor()
	 * Returns an int for int input
	 * Returns a double for float, double inputs
	 * Returns RealData for RealData input (Allowable input units = all units)
	 * Returns IntegerData for IntegerData input (Allowable input units = all units)
	 * Result has the same unit
	 */
	public static Object floor(Object obj)
	{
		/*if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			int v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData((int) Math.floor(v2), u2);
		} else*/ if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData((new Double(Math.floor(v2))).intValue(), (Unit) u2.clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			int v2 = that.intValue();
			return new Integer((int) Math.floor(v2));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new Double(Math.floor(v2));
		} else
			throw new IllegalArgumentException("Can't find floor of " + obj);
	}

	/**
	 * ceil()
	 * Returns an int for int input
	 * Returns a double for float, double inputs
	 * Returns RealData for RealData input (Allowable input units = all units)
	 * Returns IntegerData for IntegerData input (Allowable input units = all units)
	 * Result has the same unit
	 */
	public static Object ceil(Object obj)
	{
		/*if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			int v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData((int) Math.ceil(v2), u2);
		} else*/ if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData((new Double(Math.ceil(v2))).intValue(), (Unit) u2.clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			int v2 = that.intValue();
			return new Integer((int) Math.ceil(v2));
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new Double(Math.ceil(v2));
		} else
			throw new IllegalArgumentException("Can't find ceil of " + obj);
	}

	/**
	 * round()
	 * Returns an int for int input
	 * Returns a double for float, double inputs
	 * Returns RealData for RealData input (Allowable input units = all units)
	 * Returns IntegerData for IntegerData input (Allowable input units = all units)
	 * Result has the same unit
	 */
	public static Object round(Object obj)
	{
		/*if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			int v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData(v2, u2);
		} else*/ if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			double v2 = that.getValue();
			Unit u2 = that.getUnit();
			// same unit for result
			return new IntegerData((new Double(Math.round(v2))).intValue(), (Unit) u2.clone());
		} else if (obj instanceof Integer) {
			Integer that = (Integer) obj;
			int v2 = that.intValue();
			return new Integer(v2);
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			double v2 = that.doubleValue();
			return new Double(Math.round(v2));
		} else
			throw new IllegalArgumentException("Can't find round of " + obj);
	}

	public static boolean isMatrixOrVectorType(Object obj)
	{
		if ((obj instanceof DomeMatrixData) || (obj instanceof DomeVectorData))
			return true;
		return false;
	}

	public static boolean isMatrixType(Object obj)
	{
		if (obj instanceof DomeMatrixData)
			return true;
		return false;
	}

	public static boolean isPreferenceType(Object obj) {
		if (obj instanceof DomePreferenceData)
			return true;
		return false;
	}

	public static boolean isVectorType(Object obj)
	{
		if (obj instanceof DomeVectorData)
			return true;
		return false;
	}

	/**
	 * transpose()
	 * Returns DomeVectorData for DomeVectorData input (Allowable input units = all units)
	 * Returns DomeMatrixData for DomeMatrixData input (Allowable input units = all units)
	 * Result has the same unit
	 */
	public static Object transpose(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;
			DomeVectorData result = that;
			if (that.isRowVector())
				result.setRowVector(false);
			else
				result.setRowVector(true);
			// same unit for result
			return result;
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			Unit u = that.getUnit();
			double[][] v = that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			double[][] vt = (jMat.transpose()).getArray();

			DomeMatrixData result;
			if (that.getValueType().equalsIgnoreCase("integer")) {
				int[][] vt_int = Converters.doubleArrayToIntArray(vt);
				result = new DomeMatrixData(vt_int);
			} else {
				result = new DomeMatrixData(vt);
			}
			result.setUnit(u);
			return result;
		}
		else if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;
			Unit u = that.getUnit();
			double[][] v = that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			double[][] vt = (jMat.transpose()).getArray();

			DomePreferenceData result;
			if (that.getValueType().equalsIgnoreCase("integer")) {
				int[][] vt_int = Converters.doubleArrayToIntArray(vt);
				result = new DomePreferenceData(vt_int);
			} else {
				result = new DomePreferenceData(vt);
			}
			result.setUnit(u);
			return result;
		}
		else
			throw new IllegalArgumentException("Can't find transpose of " + obj);
	}

	/**
	 * Frobenius norm -- sqrt of sum of squares of all elements
	 * Returns RealData for DomeVectorData, DomeMatrixData input (Allowable input units = all units)
	 * Result has same unit
	 */
	public static Object normF(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;
			DomeMatrixData thatMat = new DomeMatrixData(that);
			double[][] v = that.getUnit().isConstantUnit() ? thatMat.getComputationalDoubleArrayData() : thatMat.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			return new RealData(jMat.normF(), (Unit) that.getUnit().clone());
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			return new RealData(jMat.normF(), (Unit) that.getUnit().clone());
		}
/*		  else if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;
			double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			return new RealData(jMat.normF(), (Unit) that.getUnit().clone());
		}
*/
		else
			throw new IllegalArgumentException("Can't find normF of " + obj);
	}

	/**
	 * sumOfSquares
	 * Returns RealData for DomeVectorData, DomeMatrixData input (Allowable input units = all units)
	 * Result has unit square
	 */
	public static Object sumOfSquares(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;
			double sum = 0;
            if (that.getUnit().isConstantUnit()) {
                for (int i = 0; i < that.getSize(); i++) {
                    sum = sum + Math.pow((that.getComputationalItem(i)).doubleValue(), 2);
                }
            } else {
                for (int i = 0; i < that.getSize(); i++) {
                    sum = sum + Math.pow((that.getItem(i)).doubleValue(), 2);
                }
            }
			return new RealData(sum, ((Unit) that.getUnit().clone()).pow(2));
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();
			double sum = 0;
			for (int i = 0; i < that.getRowCount(); i++) {
				for (int j = 0; j < that.getColumnCount(); j++) {
					sum = sum + Math.pow(v[i][j], 2);
				}
			}
			return new RealData(sum, ((Unit)that.getUnit().clone()).pow(2));
		}
/*		  else if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;
			double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();
			double sum = 0;
			for (int i = 0; i < that.getRowCount(); i++) {
				for (int j = 0; j < that.getColumnCount(); j++) {
					sum = sum + Math.pow(v[i][j], 2);
				}
			}
			return new RealData(sum, ((Unit) that.getUnit().clone()).pow(2));
		}
*/
		else
			throw new IllegalArgumentException("Can't find sumOfSquares of " + obj);
	}

	/**
	 * absSum
	 * Returns RealData for DomeVectorData, DomeMatrixData input (Allowable input units = all units)
	 * Result has same unit
	 */
	public static Object absSum(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;
			double sum = 0;
            if (that.getUnit().isConstantUnit()) {
                for (int i = 0; i < that.getSize(); i++) {
                    sum = sum + Math.abs((that.getComputationalItem(i)).doubleValue());
                }
            } else {
                for (int i = 0; i < that.getSize(); i++) {
                    sum = sum + Math.abs((that.getItem(i)).doubleValue());
                }
            }
			return new RealData(sum, (Unit) that.getUnit().clone());
		} else if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
            double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();
			double sum = 0;
			for (int i = 0; i < that.getRowCount(); i++) {
				for (int j = 0; j < that.getColumnCount(); j++) {
					sum = sum + Math.abs(v[i][j]);
				}
			}
			return new RealData(sum, (Unit) that.getUnit().clone());
		} else
			throw new IllegalArgumentException("Can't find absSum of " + obj);
	}

	/**
	 * fill
	 * Returns DomeVectorData for DomeVectorData input (Allowable input units = all units)
	 * Returns DomeMatrixData for DomeMatrixData input (Allowable input units = all units)
	 * Result has same unit or input's unit
	 */
	public static void fill(Object obj, Object x)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData vector = (DomeVectorData) obj;
            Number newValue;
            if (x instanceof RealData) {
                RealData num = (RealData) x;
                try { // see if the units of the matrix and the real number are compatible
                    newValue = new Double(vector.getUnit().convertFrom(num.getValue(), num.getUnit()));
                } catch (Exception e) {
                    newValue = new Double(num.getValue());
                    vector.setUnit(num.getUnit());
                }
                vector.setValueType("real");
            } else if (x instanceof IntegerData) {
                IntegerData num = (IntegerData) x;
                try { // see if the units of the matrix and the real number are compatible
                    newValue = new Double(vector.getUnit().convertFrom(num.getValue(), num.getUnit()));
                } catch (Exception e) {
                    newValue = new Double(num.getValue());
                    vector.setUnit(num.getUnit());
                    vector.setValueType("integer");
                }
            } else if (x instanceof Integer) {
                newValue = (Integer) x;
                vector.setValueType("integer");
            } else if (x instanceof Number) { // float
                newValue = new Double(((Number) x).doubleValue());
                vector.setValueType("real");
            } else
                throw new IllegalArgumentException("DomeMath.fill: cannot fill vector " + obj + " with " + x);

            for (int i = 0; i < vector.getSize(); i++) {
                    vector.setItem(i, newValue);
            }
		} else  if (obj instanceof DomeMatrixData) {
			DomeMatrixData matrix = (DomeMatrixData) obj;
            Number newValue;
            if (x instanceof RealData) {
                RealData num = (RealData) x;
                try { // see if the units of the matrix and the real number are compatible
                    newValue = new Double(matrix.getUnit().convertFrom(num.getValue(), num.getUnit()));
                } catch (Exception e) {
                    newValue = new Double(num.getValue());
                    matrix.setUnit(num.getUnit());
                }
                matrix.setValueType("real");
            } else if (x instanceof IntegerData) {
                IntegerData num = (IntegerData) x;
                try { // see if the units of the matrix and the real number are compatible
                    newValue = new Double(matrix.getUnit().convertFrom(num.getValue(), num.getUnit()));
                } catch (Exception e) {
                    newValue = new Double(num.getValue());
                    matrix.setUnit(num.getUnit());
                    matrix.setValueType("integer");
                }
            } else if (x instanceof Integer) {
                newValue = (Integer) x;
                matrix.setValueType("integer");
            } else if (x instanceof Number) { // float
                newValue = new Double(((Number) x).doubleValue());
                matrix.setValueType("real");
            } else
                throw new IllegalArgumentException("DomeMath.fill: cannot fill matrix " + obj + " with "+x);

			for (int i = 0; i < matrix.getRowCount(); i++) {
				for (int j = 0; j < matrix.getColumnCount(); j++) {
                    matrix.setItem(i, j, newValue);
				}
			}
		}

		else if (obj instanceof DomePreferenceData) {
			DomePreferenceData matrix = (DomePreferenceData) obj;
			Number newValue;
			if (x instanceof RealData) {
				RealData num = (RealData) x;
				try { // see if the units of the matrix and the real number are compatible
					newValue = new Double(matrix.getUnit().convertFrom(num.getValue(), num.getUnit()));
				} catch (Exception e) {
					newValue = new Double(num.getValue());
					matrix.setUnit(num.getUnit());
				}
				matrix.setValueType("real");
			} else if (x instanceof IntegerData) {
				IntegerData num = (IntegerData) x;
				try { // see if the units of the matrix and the real number are compatible
					newValue = new Double(matrix.getUnit().convertFrom(num.getValue(), num.getUnit()));
				} catch (Exception e) {
					newValue = new Double(num.getValue());
					matrix.setUnit(num.getUnit());
					matrix.setValueType("integer");
				}
			} else if (x instanceof Integer) {
				newValue = (Integer) x;
				matrix.setValueType("integer");
			} else if (x instanceof Number) { // float
				newValue = new Double(((Number) x).doubleValue());
				matrix.setValueType("real");
			} else
				throw new IllegalArgumentException("DomeMath.fill: cannot fill matrix " + obj + " with " + x);

			for (int i = 0; i < matrix.getRowCount(); i++) {
				for (int j = 0; j < matrix.getColumnCount(); j++) {
					matrix.setItem(i, j, newValue);
				}
			}
		}
		else
            throw new IllegalArgumentException("DomeMath.fill: cannot fill " + obj + " with " + x);
	}

	/**
	 * find sub vector
	 * Returns DomeVectorData for DomeVectorData input (Allowable input units = all units)
	 * Result has same unit
	 */
	public static DomeVectorData subVector(Object obj, int start, int end)
	{
		if (obj instanceof DomeVectorData) {
			DomeVectorData that = (DomeVectorData) obj;
			Unit u = that.getUnit();

			int newSize = end - start + 1;
			DomeVectorData sub = new DomeVectorData(newSize);
			sub.setUnit(u);
			sub.setValueType(that.getValueType());
            sub.setRowVector(that.isRowVector());
			for (int i = 0; i < newSize; i++)
				sub.setItem(i, that.getItem(i + start));
			return sub;
		} else
			throw new IllegalArgumentException("Can't find sub vector of " + obj);
	}

	/**
	 * inverse
	 * Returns DomeMatrixData for DomeMatrixData input (Allowable input units = all units)
	 * Result has inverse unit
	 */
	public static DomeMatrixData inverse(Object obj)
	{
		if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;

			if (that.getRowCount() != that.getColumnCount())
				throw new IllegalArgumentException("Matrix is not square. Can't find inverse of " + obj);
			Unit u = that.getUnit();
            double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			double[][] vt = (jMat.inverse()).getArray();

			DomeMatrixData result;
			result = new DomeMatrixData(vt);

			result.setUnit(((Unit) u.clone()).inv());
			return result;
		} else
			throw new IllegalArgumentException("Can't find inverse of " + obj);
	}




	/**
	 * rowEchelon()
	 * Returns DomeMatrixData for DomeMatrixData input (Allowable input units = all units)
	 * Result has the same unit
	 */
	public static Object rowEchelon(Object obj)
	{
		if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			Unit u = that.getUnit();
            double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();
			Matrix jMat = new Matrix(v);

			LUDecomposition lu = new LUDecomposition(jMat);
			double[][] vt = (lu.getU()).getArray();

			DomeMatrixData result = new DomeMatrixData(vt);
			result.setUnit(u);
			return result;
		} else
			throw new IllegalArgumentException("Can't find rowEchelon of " + obj);
	}

	/**
	 * find sub matrix
	 * Returns DomeMatrixData for DomeMatrixData input (Allowable input units = all units)
	 * Result has same unit
	 */
	public static DomeMatrixData subMatrix(Object obj, int i0, int j0, int i1, int j1)
	{
		if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			Unit u = that.getUnit();
			double[][] v = that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			double[][] vt = (jMat.getMatrix(i0, i1, j0, j1)).getArray();

			DomeMatrixData result = new DomeMatrixData(vt);
			result.setUnit(u);
			return result;
		} else
			throw new IllegalArgumentException("Can't find sub matrix of " + obj);
	}
/// subPreference is a preference subMatrix
	public static DomePreferenceData subPreference(Object obj, int i0, int j0, int i1, int j1) {
		if (obj instanceof DomePreferenceData) {
			DomePreferenceData that = (DomePreferenceData) obj;
			Unit u = that.getUnit();
			double[][] v = that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			double[][] vt = (jMat.getMatrix(i0, i1, j0, j1)).getArray();

			DomePreferenceData result = new DomePreferenceData(vt);
			result.setUnit(u);
			return result;
		} else
			throw new IllegalArgumentException("Can't find sub matrix of " + obj);
	}


	/**
	 * det
	 * Returns double for DomeMatrixData input (Allowable input units = all units)
	 * Result has no unit
	 */
	public static double det(Object obj)
	{
		if (obj instanceof DomeMatrixData) {
            DomeMatrixData that = (DomeMatrixData) obj;
            double[][] v = that.getUnit().isConstantUnit() ? that.getComputationalDoubleArrayData() : that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			return jMat.det();
		} else
			throw new IllegalArgumentException("Can't find det of " + obj);
	}

	/**
	 * trace
	 * Returns RealData for DomeMatrixData input (Allowable input units = all units)
	 * Result has same unit
	 */
	public static RealData trace(Object obj)
	{
		if (obj instanceof DomeMatrixData) {
			DomeMatrixData that = (DomeMatrixData) obj;
			double[][] v = that.getDoubleArrayData();

			Matrix jMat = new Matrix(v);
			return new RealData(jMat.trace(),that.getUnit());
		} else
			throw new IllegalArgumentException("Can't find trace of " + obj);
	}

	public static Object convertToInteger(Object obj)
	{
		if (obj instanceof RealData) {
			RealData that = (RealData) obj;
			// same unit for result
			return new IntegerData(new Double(Math.ceil(that.getValue())).intValue(), that.getUnit());
		}
		else if (obj instanceof Number) { // assume the same behavior as conversion from RealData
			Number that = (Number) obj;
			return new IntegerData(new Double(Math.ceil(that.doubleValue())).intValue());
		}
		else if (obj instanceof BooleanData) {
			BooleanData that = (BooleanData) obj;
			return that.getValue() ? new IntegerData(1) : new IntegerData(0);
		}
		else
			throw new IllegalArgumentException("Can't convert" + obj+" to Integer");
	}

	public static Object convertToReal(Object obj)
	{
		if (obj instanceof IntegerData) {
			IntegerData that = (IntegerData) obj;
			// same unit for result
			return new RealData(that.getValue(), that.getUnit());
		}
		else if (obj instanceof Number) {
			Number that = (Number) obj;
			return new RealData(that.doubleValue());
		}
		else if (obj instanceof BooleanData) {
			BooleanData that = (BooleanData) obj;
			return that.getValue() ? new RealData(1.0) : new RealData(0.0);
		}
		else
			throw new IllegalArgumentException("Can't convert" + obj + " to Real");
	}

	public static Object convertToMatrix(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			return new DomeMatrixData((DomeVectorData) obj);
		}
		else
			throw new IllegalArgumentException("Can't convert" + obj + " to Matrix");
	}

	public static Object convertToArray(Object obj)
	{
		if (obj instanceof DomeVectorData) {
			return ((DomeVectorData) obj).getValuesArray();
		}
		else if (obj instanceof DomeMatrixData) {
			return ((DomeMatrixData) obj).getNumberArrayData();
		}
		else
			throw new IllegalArgumentException("Can't convert" + obj + " to array");
	}

    public static Object convertToVector(Object obj) {
        if (obj instanceof DomeMatrixData) {
            DomeMatrixData matrix = (DomeMatrixData) obj;
            if (matrix.getRowCount()==1) // can become row vector
                return matrix.getRow(0);
            else if (matrix.getColumnCount() == 1) // can become column vector
                return matrix.getCol(0);
            else
                throw new IllegalArgumentException("DomeMath.convertToVector: "+ obj + " has more than one row and column");
        } else
            throw new IllegalArgumentException("DomeMath.convertToVector: Can't convert" + obj + " to Vector");
    }

}
