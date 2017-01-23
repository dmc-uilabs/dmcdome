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
 * $Id: Prefix.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package units;

import java.lang.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

/** This class implements the table of multiplier prefixes. It is
 * normally used only by the unit parser to resolve prefix names to their
 * values.
 *
 * @author Gunther Schadow
 * @version $Id: Prefix.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 * */
class Prefix
  implements Cloneable, Serializable
{
  private String name   = "";
  private double factor = 1;

  private static Hashtable all = init();
  
  static void reset()
  {
    all = init();
  }

  private static Hashtable init() 
  {
    all = new Hashtable();
    all.put("", new Prefix());
    return all;
  }

  /** Defines a new prefix */
  Prefix(String s, double f)
  {
    name   = s;
    factor = f;
    
    // never give handles to the hashtable out, we have to archive a clone
    Prefix u = (Prefix)all.put(name, clone());
    
    if(u != null)
      {
	all.put(name, u);
	throw new IllegalArgumentException("prefix `" + name + "' already defined");
      }
  }
  
  /** Tests whether a string is a valid prefix */
  public static final boolean isDefined(String s) 
  { 
    if(!Unit.caseSensitive())
      s  = s.toUpperCase();
    
    // System.out.println("Prefix.isDefined(" + s + ") returns " + all.containsKey(s));

    return all.containsKey(s); 
  }
  
  /** Returns the factor for the prefix
   *
   * @except IllegalArgumentException if the string is not a valid prefix
   * */
  public static final double factorFor(String s)
  {
    if(!Unit.caseSensitive())
      s  = s.toUpperCase();
    
    Prefix pfx = (Prefix)all.get(s);
    if(pfx == null)
      throw new IllegalArgumentException("`" + s + "' is not a valid prefix");
    else
      return pfx.factor;
  }
  
  /** Returns the prefix string for the factor, null if nothing matches */
  public static final String forFactor(double fact)
  {
    for(Enumeration e = all.elements(); e.hasMoreElements();)
      {
	Prefix pfx = (Prefix)e.nextElement();
	if(pfx.factor == fact)
	  return pfx.name;
      }

    return null;
  }

  public Prefix(String s)
  {
    if(!Unit.caseSensitive())
      s  = s.toUpperCase();
    
    Prefix p = (Prefix)all.get(s);
    
    // System.out.print("prefix found: "); p.dump();
    
    if(p == null)
      throw new IllegalArgumentException("prefix `" + s + "' is unknown");
    
    assign(p);
  }

  Prefix() { }

  public Prefix assign(Prefix p)
  {
    name = p.name;
    factor = p.factor;
    return this;
  }

  public Object clone()
  {
    Prefix that = new Prefix();
    that.assign(this);
    return that;
  }

  public String toString() { return name; }

  public void dump() { System.out.println("prefix " + name + " = " + factor); }

  /** Saves all static data (i.e. table of prefices all) */
  static void save(ObjectOutput oos)
       throws IOException
  {
    oos.writeObject(all);
  }

  /** Loads static data (i.e. table of prefices all) */
  static void load(ObjectInput ois)
       throws IOException, ClassNotFoundException
  {
    all = (Hashtable)ois.readObject();
  }

  static Enumeration elements() { return all.keys(); }

  public boolean equals(Prefix p) { return factor == p.factor; }
}
