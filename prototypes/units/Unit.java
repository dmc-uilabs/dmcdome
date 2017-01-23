/*
 * Copyright (c) 1998 The Regenstrief Institute.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * Written by Gunther Schadow.
 *
 * $Id: Unit.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package units;

import java.lang.*;
import java.io.*;
import java.util.Hashtable;

/** This class embodies the pair &lt;<I>v</I>, <I><B>u</B></I>&gt; and
 * is used directly by the application programmer. It has a parser
 * that generates the internal representation from the character
 * string expression of a unit. It also provides all the operators to
 * calculate with units, multiplication, division and raising to a
 * power.
 *
 * @author Gunther Schadow
 * @version $Id: Unit.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 * */
public class Unit
        implements Cloneable, Serializable
{
	private static byte case_sensitive = 0x00;

	static void reset()
	{
		case_sensitive = 0x00;
	}

	public static void caseSensitive(boolean cs)
	{
		if ((case_sensitive & 0x10) != 0)
			throw new IllegalStateException("caseSensitive(boolean) was called more than once");
		case_sensitive = (byte) (0x10 | (cs ? 0x01 : 0x00));
	}

	public static boolean caseSensitive()
	{
		if ((case_sensitive & 0x10) == 0)
			throw new IllegalStateException("caseSensitive(boolean) must be called first");
		return case_sensitive != 0x10;
	}

	protected String name;
	protected double nu;
	protected Dimension u_vec;
	protected Function cnv;
	/** This provides for the SI anomality of allowing prefixes on
	 the Degree celsius. */
	protected double cnv_pfx = 1;

	//added by Wei and corresponding modifications were made to functions
	protected String description;
	protected String category;


	// CTORS

	/** Instantiate the Unit "" = 1 (the unity)
	 *
	 * */
	public Unit()
	{
		name = "";
		nu = 1;
		u_vec = new Dimension();
		cnv = null;
		cnv_pfx = 1;
		description = "";
		category = "";
	}

	/** Instantiate a unit &lt;nu, u_vec&gt with name and conversion functions.
	 *
	 * */
	public Unit(String _name, double _nu, Dimension _u_vec,
	            Function _cnv, double _cnv_pfx, String _discription, String _category)
	{
		if (caseSensitive())
			name = _name;
		else
			name = _name.toUpperCase();
		nu = _nu;
		u_vec = _u_vec;
		cnv = _cnv;
		cnv_pfx = _cnv_pfx;
		description = _discription;
		category = _category;
	}

	/** Instantiate a unit &lt;nu, u_vec&gt with name.
	 *
	 * */
	public Unit(String _name, double _nu, Dimension _u_vec)
	{
		this(_name, _nu, _u_vec, null, 1, "", "");
	}

	/** Parse a unit term
	 *
	 *
	 * */
	public Unit(String term)
	{
		this();

		UnitParser parser = new UnitParser(term);
		try {
			parser.parse(this);
		}
		catch (ParseException x) {
			throw new IllegalArgumentException("parse error: " + x.getMessage());
		}

		if (caseSensitive())
			name = term;
		else
			name = term.toUpperCase();
	}

	public Object clone()
	{
		Unit that = new Unit();
		that.assign(this);
		return that;
	}

	// EQUALITY AND ASSIGNMENT

	/** Tests for equality of this unit u1 and another unit u2.
	 *
	 * @return true if u1 and u2 are equal, false otherwise.
	 * */
	public boolean equals(Unit u2)
	{
		return (nu == u2.nu) &&
		        (u_vec.equals(u2.u_vec)) &&
		        (cnv == u2.cnv) &&
		        (cnv_pfx == cnv_pfx);
	}

	/** Sets this unit to be equal to another unit u.
	 *
	 * @return this unit after assignment to u
	 * */
	public Unit assign(Unit u)
	{
		name = u.name;
		nu = u.nu;
		u_vec.assign(u.u_vec);
		cnv = u.cnv;
		cnv_pfx = u.cnv_pfx;
		description = u.description;
		category = u.category;

		return this;
	}

	/** Assign the unity (= dimensionless unit 1) to this unit.
	 *
	 * */
	public Unit assignUnity()
	{
		name = "";
		nu = 1;
		u_vec.assignZero();
		cnv = null;
		cnv_pfx = 1;
		description = "";
		category = "";
		return this;
	}

	// MISC

	/** Returns the dimension of this unit */
	public Dimension dim()
	{
		return u_vec;
	}

	/** Returns the string representation (i.e. the name) of the unit */
	public String toString()
	{
		return name;
	}

	public void dump()
	{
		System.out.print("unit " + name + " <" + nu + ", " + u_vec.toString() + ">");
	}

	/*
	public String dump() {
	  String dumpee;
	  dumpee = new String("unit " + name + " <" + nu + ", " + u_vec.toString() + ">");
	if(cnv != null)
	  dumpee.concat(" " + cnv.name + " * " + cnv_pfx);
	System.out.println(dumpee);
	return dumpee;
	}

	public static String getUnit() {
	  String foo = new String("unit " + name + " <" + nu + ", " + u_vec.toString() + ">");
	if(cnv != null)
	  foo.concat(" " + cnv.name + " * " + cnv_pfx);
	return foo;
	}
	*/
	// CONVERSION AND MUTATION

	/** Converts a measurement mu * u into this unit.
	 *
	 * @return the converted measurement value
	 * @exception  IllegalArgumentException if dimensions differ.
	 * */
	public double convertFrom(double mu1, Unit u1)
	{
		double mu2, x;
		Unit u2 = this;
		// check if dimensions are equal
		if (!u1.u_vec.equals(u2.u_vec))
			throw new IllegalArgumentException("dimensions are not equal");

		// if both units are on ratio scale
		if (u1.cnv == null && u2.cnv == null)
			mu2 = mu1 * u1.nu / u2.nu;
		else {
			if (u1.cnv != null) // turn mu1 * u1 into its ratio scale equivalent
				x = u1.cnv.f_from(mu1 * u1.cnv_pfx) * u1.nu;
			else
				x = mu1 * u1.nu;

			if (u2.cnv != null) // turn mu * u on ratio scale into its non-ratio scale
				mu2 = u2.cnv.f_to(x / u2.nu) / u2.cnv_pfx;
			else
				mu2 = x / u2.nu;
		}

		return mu2;
	}

	/** Converts a measurement mu * this unit into other unit u.
	 *
	 * @return the converted measurement value
	 * @exception  IllegalArgumentException if dimensions differ.
	 * */
	public double convertTo(double mu, Unit u)
	{
		return u.convertFrom(mu, this);
	}

	/** Converts measurement mu * this unit into a coherent unit.
	 *
	 * @return the converted measurement value
	 * @exception  IllegalArgumentException if dimensions differ.
	 * */
	public double convertCoherent(double mu)
	{
		// convert mu' * u' into canonical mu * u on ratio scale
		if (cnv == null)
			mu = cnv.f_from(mu / cnv_pfx) * nu;

		return mu;
	}

	/** Mutates this unit into a coherent unit and converts a measurement mu
	 *
	 * @return the converted measurement value
	 * @exception  IllegalArgumentException if dimensions differ.
	 * */
	public double mutateCoherent(double mu)
	{
		// convert mu' * u' into canonical mu * u on ratio scale
		if (cnv == null)
			mu = cnv.f_from(mu / cnv_pfx) * nu;

		// mutate to coherent unit
		nu = 1;
		cnv = null;
		cnv_pfx = 1;
		name = "";

		// build a name as a term of coherent base units
		for (int i = 0; i < Dimension.getMax(); i++) {
			int e = u_vec.elementAt(i);
			Unit u = UnitAtom.forDimension(new Dimension(i));
			if (u == null)
				throw new IllegalArgumentException("can't find base unit for dimension " + i);
			name = u.name + e;
		}

		return mu;
	}

	/** Mutates this unit into a unit on a ratio scale and converts measurement mu
	 *
	 * @return the converted measurement value
	 * @exception  IllegalArgumentException if dimensions differ.
	 * */
	public double mutateRatio(double mu)
	{
		if (cnv == null)
			return mutateCoherent(mu);
		else
			return mu;
	}


	// OPERATIONS

	/** Multiplies this unit u with a scalar s. Special meaning for
	 * special units so that (0.1*B) is 1 dB.
	 *
	 * @return the product u * s.
	 * @exception IllegalArgumentException if u is on a non-ratio scale.
	 * */
	public Unit mul(double s)
	{
		if (cnv != null)
			cnv_pfx *= s;
		else
			nu *= s;
		return this;
	}

	/** Multiplies this unit u1 with another unit u2. If one of the
	 * factors is a non-ratio unit the other must be dimensionless or
	 * else an exception is raised. This special case treatment allows
	 * to scale non-ratio units.
	 *
	 * @return the product u1 * u2
	 * @exception IllegalArgumentException if one of the unit is not on
	 *      a ratio-scale and the other is not dimensionless.
	 * */
	public Unit mul(Unit u2)
	{
		if (cnv != null)
			if (u2.cnv == null && u2.u_vec.isZero()) {
				cnv_pfx *= u2.nu;
				return this;
			}
			else
				throw new IllegalArgumentException("non-ratio unit " + this);

		if (u2.cnv != null)
			if (cnv == null && u_vec.isZero()) {
				double cp = nu;
				assign(u2);
				cnv_pfx *= cp;
				return this;
			}
			else
				throw new IllegalArgumentException("non-ratio unit " + u2);

		name = UnitString.mul(name, u2.name);

		nu *= u2.nu;
		u_vec.add(u2.u_vec);

		return this;
	}

	/** Divides this unit u1 with another unit u2. If u1 is not on a
	 * ratio scale an exception is raised. Mutating to a ratio scale unit
	 * is not possible for a unit, only for a measurement.
	 *
	 * @return the quotient u1 / u2
	 * @exception IllegalArgumentException if a unit is not on a ratio-scale.
	 * */
	public Unit div(Unit u2)
	{
		if (cnv != null)
			throw new IllegalArgumentException("non-ratio unit " + this);
		if (u2.cnv != null)
			throw new IllegalArgumentException("non-ratio unit " + u2);

		name = UnitString.div(name, u2.name);

		nu /= u2.nu;
		u_vec.sub(u2.u_vec);

		return this;
	}

	/** Invert this unit with respect to multiplication. If u1 is not on a
	 * ratio scale an exception is raised. Mutating to a ratio scale unit
	 * is not possible for a unit, only for a measurement.
	 *
	 * @return 1/u.
	 * @exception IllegalArgumentException if u is not on a ratio-scale.
	 * */
	public Unit inv()
	{
		if (cnv != null)
			throw new IllegalArgumentException("non-ratio unit " + this);

		name = UnitString.inv(name);

		nu = 1 / nu;
		u_vec.minus();
		return this;
	}

	/** Raises this unit u to a power p.  If u1 is not on a
	 * ratio scale an exception is raised. Mutating to a ratio scale unit
	 * is not possible for a unit, only for a measurement.
	 *
	 * @return the power u^p.
	 * @exception IllegalArgumentException if u is not on a ratio-scale.
	 * */
	public Unit pow(int p)
	{
		if (cnv != null)
			throw new IllegalArgumentException("non-ratio unit " + this);

		name = UnitString.pow(name, p);

		nu = Math.pow(nu, (double) p);
		u_vec.mul(p);
		return this;
	}

	/** Saves all static data (i.e. table of units all and lla) */
	static void save(ObjectOutput oos)
	        throws IOException
	{
		oos.writeObject(new Byte(case_sensitive));
	}

	/** Loads static data (i.e. table of units all and lla) */
	static void load(ObjectInput ois)
	        throws IOException, ClassNotFoundException
	{
		case_sensitive = ((Byte) ois.readObject()).byteValue();
	}
}
