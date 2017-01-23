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
 * $Id: Dimension.java,v 1.3 2003/07/30 15:25:57 sittha Exp $
 */

package edu.iupui.rg.ucum.units;

import java.lang.*;
import java.io.*;

/** This class implements the vector <I><B>u</B></I> of exponents and its
 * operations for addition, subtraction, and multiplication with a
 * scalar.
 *
 * @author Gunther Schadow
 * @version $Id: Dimension.java,v 1.3 2003/07/30 15:25:57 sittha Exp $
 * */
public class Dimension
  implements Cloneable, Serializable
{
  private static int max = 0;

  static void reset() { max = 0; }

  public static void setMax(int n) {
    if(max != 0)
      throw new IllegalStateException("setMax was called more than once");
    max = n;
  }

  public static int getMax() {
    if(max == 0)
      throw new IllegalStateException("setMax must be called first");
    return max;
  }

  private double vec[];

  public Dimension()
  {
    if(max == 0)
      throw new IllegalStateException(
	   "setMax must be called before any dimension can be instantiated");

    vec = new double[max];
    assignZero();
  }

  /** Initializes a to the n-th base dimension.
   *
   * @except java.lang.ArrayIndexOutOfBoundsException
   *          if n < 0 or n > max;
   **/
  public Dimension(int n)
  {
    this();
    vec[n] = 1;
  }

  /** Get n-th component.
   *
   * @except java.lang.ArrayIndexOutOfBoundsException
   *          if n < 0 or n > max;
   **/
  public double elementAt(int n) { return vec[n]; }

  /** Return a string that represents the vector
   *
   * @return the string that represents the vector
   * */
  public String toString()
  {
    String result = "(" + vec[0];
    for(int i = 1; i < max; i++) result += ", " + vec[i];
    return result + ")";
  }

  /** Adds to this dimension vector d1 another dimension vector
   *  d2, which is used when two units u1 and u2 are multiplied.
   *
   * @return the sum d1 + d2.
   * */
  public Dimension add(Dimension d2)
  { for(int i = 0; i < max; i++) vec[i] += d2.vec[i]; return this; }

  /** Subtracts from this dimension vector d1 another dimension
   *  vector d2, which is used when two units u1 and u2 are
   *  divided.
   *
   * @return the difference d1 - d2.
   * */
  public Dimension sub(Dimension d2)
  { for(int i = 0; i < max; i++) vec[i] -= d2.vec[i]; return this; }

  /** Inverts this dimension vector d.
   *
   * @return -d.
   * */
  public Dimension minus()
  { for(int i = 0; i < max; i++) vec[i] = -vec[i]; return this; }

  /** Multiplies this dimension vector d with a scalar s, which is used
   *  when a unit is raised to a power.
   *
   * @return the product d1 * s.
   * */
  public Dimension mul(double s)
  { for(int i = 0; i < max; i++) vec[i] *= s; return this; }

  /** Tests for equality of this dimension d1 and another dimension d2.
   *
   * @return true if d1 and d2 are equal, false otherwise.
   * */
  public boolean equals(Dimension d2)
  {
    for(int i = 0; i < max; i++)
      if(vec[i] != d2.vec[i])
	return false;
    return true;
  }

  public Dimension assign(Dimension d)
  { for(int i = 0; i < max; i++) vec[i] = d.vec[i]; return this; }

  public Dimension assignZero()
  { for(int i = 0; i < max; i++) vec[i] = 0; return this; }

  /** Tests for zero dimension.
   *
   * @return true if exponents are all zero, false otherwise.
   * */
  public boolean isZero()
  {
    for(int i = 0; i < max; i++)
      if(vec[i] != 0)
	return false;
    return true;
  }

  public Object clone()
  {
    Dimension that = new Dimension();
    that.assign(this);
    return that;
  }

  /** Saves all static data (i.e. table of units all and lla) */
  static void save(ObjectOutput oos)
       throws IOException
  {
    oos.writeObject(new Integer(max));
  }

  /** Loads static data (i.e. table of units all and lla) */
  static void load(ObjectInput ois)
       throws IOException, ClassNotFoundException
  {
    max = ((Integer)ois.readObject()).intValue();
  }

	public void setElementAt(int index, double value)
	{
		vec[index]=value;
	}
}
