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
 * $Id: Measurement.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package edu.iupui.rg.ucum.units;

import java.lang.*;
import java.io.*;

/** This class implements any quantity as the pair &lt;<I>&micro;</I>,
 * <B>u</B>&gt;.  It provides the full set of operators including
 * addition and subtration of equally dimensioned quantities. It also
 * has a method <CODE>convert(Unit u_prime)</CODE> to convert a
 * measurement value <I>&micro;</I> to <I>&micro;</I>' for another
 * commensurable unit <B>u</B>'.
 *
 * @author Gunther Schadow
 * @version $Id: Measurement.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 * */
public class Measurement
  implements Cloneable, Serializable
{
  private double mu;
  private Unit    u;
  
  public Measurement(double _mu, Unit _u)   { mu = _mu; u  = _u; }
  public Measurement(double _mu, String _u) { mu = _mu; u  = new Unit(_u); }
  public Measurement(double _mu)            { mu = _mu; u  = new Unit(); }
  public Measurement()                      { mu = 0; u  = new Unit(); }

  /** Parses a string into value and unit
   *
   *
  public Measurement(String s)
  {
    // to be implemented

  }
  */

  /** Adds to this measurement m1 another measurement m2.
   *
   * @return the sum m1 + m2.
   * @except IllegalArgumentException if m2 is not of same dimension
   * */
  public Measurement add(Measurement m2)
  {
    if(u.dim().equals(u.dim()))
      mu += m2.mu * m2.u.nu / u.nu;
    else
      throw new IllegalArgumentException("dimensions are not equal");
    
    return this;
  }
  
  /** Subtracts from this measurement m1 another measurement m2.
   *
   * @return the difference m1 - m2.
   * @except IllegalArgumentException if m2 is not of same dimension
   * */
  public Measurement sub(Measurement m2)
  { 
    if(u.dim().equals(u.dim()))
      mu -= m2.mu * m2.u.nu / u.nu;
    else
      throw new IllegalArgumentException("dimensions are not equal");    

    return this;
  }

  /** Invert this measurement m with respect to addition.
   *
   * @return -m.
   * */
  public Measurement minus() { mu = -mu; return this; }

  /** Multiplies this measurement m with a scalar s.
   *
   * @return the product m * s.
   * */
  public Measurement mul(double s) { mu *= s; return this; }
  
  /** Multiplies this measurement m1 with another measurement m2
   *
   * @return the product m1 * m2
   * */
  public Measurement mul(Measurement m2) { mu *= m2.mu; u.mul(m2.u); return this; }
  
  /** Divides this measurement m1 with another measurement m2
   *
   * @return the quotient m1 / m2
   * */
  public Measurement div(Measurement m2) { mu /= m2.mu; u.div(m2.u); return this; }
  
  /** Invert this measurement with respect to multiplication.
   *
   * @return 1/u.
   * */
  public Measurement inv() { mu = 1/mu; u.inv(); return this; }
  
  /** Raises this measurement m to a power p.
   *
   * @return the power u^p.
   * */
  public Measurement pow(int p)
  { mu = Math.pow(mu, (double)p); u.pow(p); return this; }
  
  /** Tests for equality of this measurement m1 and another measurement m2.
   *
   * @return true if m1 and m2 are equal, false otherwise.
   * */
  public boolean equals(Measurement m2)
  {
    if(dim().equals(m2.dim()))
      return mu * u.nu == m2.mu * m2.u.nu;
    else
      return false;
  }

  /** Sets this measurement to be equal to another measurement m.
   *
   * @return this measurement after assignment to m
   * */
  public Measurement assign(Measurement m)
  { 
    mu = m.mu; 
    u.assign(m.u); 
    return this;
  }  

  public Object clone()
  {
    Measurement that = new Measurement();
    that.assign(this);
    return that;
  }

  /** Returns the dimension of this measurement */
  public Dimension dim() { return u.dim(); }
}
