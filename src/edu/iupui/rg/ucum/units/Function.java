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
 * $Id: Function.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package edu.iupui.rg.ucum.units;

import java.lang.*;
import java.io.*;
import java.util.Hashtable;

public abstract class Function
  implements Serializable
{
  String   name = null;

  public abstract double f_to  (double x);
  public abstract double f_from(double x);

  private static final Hashtable all = new Hashtable();

  protected Function(String s)
  {
    s = s.toUpperCase();
    
    name = s;
    
    // System.out.println("defining function: `" + name + "'"); 

    if(all.containsKey(s))
      throw new IllegalArgumentException("function `" + s + "' is already defined");
    else
      this.all.put(s, this);
  }

  public static final Function forName(String s)
  { 
    s = s.toUpperCase();
    
    Function f = (Function)all.get(s);
    if(f == null)
      throw new IllegalArgumentException("function `" + s + "' is not defined");

    return f;
  }
  
  public static final boolean isDefined(String s)
  {
    s = s.toUpperCase();

    return all.containsKey(s);
  }


  static final double LN10 = 2.30258509299404568402;
  static final double LN2  = .693147180559945309417;

  // public static final Function f_<T> = new F<T>;
  public static final Function f_cel  = new Fcel();
  public static final Function f_degf = new Fdegf();
  public static final Function f_pH   = new FpH();
  public static final Function f_ln   = new Fln();
  public static final Function f_2ln  = new F2ln();
  public static final Function f_lg   = new Flg();
  public static final Function f_10lg = new F10lg();
  public static final Function f_20lg = new F20lg();
  public static final Function f_2lg  = new F2lg();
  public static final Function f_ld   = new Fld();
  public static final Function f_inv  = new Finv();

  /*
  public static void main(String args[]) {
    System.out.println(all);
  }
  */
}

/*
final class <T>F extends Function {    
  <T>F() { super("<T>"); }
  public double f_to  (double x) { return x; }
  public double f_from(double x) { return x; }
}
*/

final class Fcel extends Function {    
  Fcel() { super("cel"); }
  public double f_to  (double x) { return x - 273.15; }
  public double f_from(double x) { return x + 273.15; }
}

final class Fdegf extends Function {    
  Fdegf() { super("degf"); }
  public double f_to  (double x) { return x - 459.67; }
  public double f_from(double x) { return x + 459.67; }
}

final class FpH extends Function {    
  FpH() { super("pH"); }
  public double f_to  (double x) { return - Math.log(x) / Function.LN10; }
  public double f_from(double x) { return   Math.pow(10, -x); }
}

final class Fln extends Function {    
  Fln() { super("ln"); }
  public double f_to  (double x) { return Math.log(x); }
  public double f_from(double x) { return Math.exp(x); }
}

final class F2ln extends Function {    
  F2ln() { super("2ln"); }
  public double f_to  (double x) { return 2 * Math.log(x); }
  public double f_from(double x) { return Math.exp(x / 2); }
}

final class Flg extends Function {    
  Flg() { super("lg"); }
  public double f_to  (double x) { return Math.log(x) / Function.LN10; }
  public double f_from(double x) { return Math.pow(10, x); }
}

final class F10lg extends Function {    
  F10lg() { super("10lg"); }
  public double f_to  (double x) { return 10 * Math.log(x) / Function.LN10; }
  public double f_from(double x) { return Math.pow(10, x / 10); }
}

final class F20lg extends Function {    
  F20lg() { super("20lg"); }
  public double f_to  (double x) { return 20 * Math.log(x) / Function.LN10; }
  public double f_from(double x) { return Math.pow(10, x / 20); }
}

final class F2lg extends Function {    
  F2lg() { super("2lg"); }
  public double f_to  (double x) { return 2 * Math.log(x) / Function.LN10; }
  public double f_from(double x) { return Math.pow(10, x / 2); }
}

final class Fld extends Function {    
  Fld() { super("ld"); }
  public double f_to  (double x) { return Math.log(x) / Function.LN2; }
  public double f_from(double x) { return Math.pow(2, x); }
}

final class Finv extends Function {    
  Finv() { super("inv"); }
  public double f_to  (double x) { return 1.0 / x; }
  public double f_from(double x) { return 1.0 / x; }
}
