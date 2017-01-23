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
 * $Id: UnitString.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package units;

import java.lang.*;
import java.io.*;
import java.util.Hashtable;

class UnitString
{
  public static String mul(String s1, String s2) { return s1 + "." + s2; }

  /** Inverts a unit string, i.e. exchange the operators '/' and '.': 
   *
   * "kg.m/s2" -> "/kg/m.s2"
   *
   * This method does not produce beautiful results, but they are correct
   * and this is what is important here.
   * */

  public static String inv(String s)
  {
    if(s.length() == 0) return s;

    String t = s.replace('/',(char)1).replace('.','/').replace((char)1,'.');
    switch(t.charAt(0)) {
    case '.': return t.substring(1);
    case '/': return t;
    default:  return "/" + t;
    }
  }

  /** Divides two unit strings:
   *
   * "m2" / "kg.m/s2" -> "m2/kg/m.s2"
   *
   * This method does not produce beautiful results, but they are correct
   * and this is what is important here.
   * */
  public static String div(String s1, String s2)
  {
    if(s2.length() == 0) return s1;
    String t = s2.replace('/',(char)1).replace('.','/').replace((char)1,'.');
    switch(t.charAt(0)) {
    case '.': return s1 + t;
    case '/': return s1 + t;
    default:  return s1 + "/" + t;
    }
  }
  
  /** Raises a unit string to the exp-th power, i.e. add or update the exponents.
   *
   * kg.m/s2 (^ -2) --> kg-2.m-2/s-4 
   *
   * Note that pow(t, -1) produces different results from invert(t).
   * */
  public static String pow(String s, int exp)
  {
    // System.out.println("enter pow " + s + ", " + exp);

    StringBuffer b = new StringBuffer(s);
    b.append('\0');

    int state = 0;
    int expoi = 0;

    for(int i = 0; i < b.length(); i++)
      {
	// System.out.println("  at " + i + ": `" + b.charAt(i) + "'");

	switch(b.charAt(i)) {
	  // numerals: enter state 1 set expoi;
	case '-':
	case '+':
	case '0':
	case '1':
	case '2':
	case '3':
	case '4':
	case '5':
	case '6':
	case '7':
	case '8':
	case '9':
	  // System.out.println("  numeral");
	  if(state == 0)
	    {
	      state = 1;
	      expoi = i;
	    }
	  break;
	case '*':
	  // System.out.println("  was non-numeral");
	  state = 0;
	  break;
	case ']':
	  // System.out.println("  close bracket exiting depth " + state);
	  if(state > 0)
	    throw new IllegalArgumentException("unmatched `]' in `" + s + "'");
	  else
	    state++;
	  break;
	case '[':
	  // an arbitrary string, nested enter state -depth
	  if(state > 0)
	    state = -1;
	  else
	    state--;
	  // System.out.println("  open bracket entering depth " + state);
	  // FALLTHROUGH
	default:
	  // System.out.println("  non-numeral");
	  if(state == 1)
	    {
	      // end of exponent
	      int len = i - expoi; 	                // length is 
	      char cs[] = new char[len];                // we'll write the exponent here
	      b.getChars(expoi, i, cs, 0);              // get the exponent chars
	      String nums = new String(cs);             // convert to string
	      int num = Integer.valueOf(nums).intValue(); // convert to integer (ridiculous!)
	      num *= exp;                               // -- power --
	      nums = "" + num;                          // exponent back to string
	      int len2 = nums.length();                 // new length is
	      int diff = len2 - len;                    // differing by
	      if(diff > 0)                              // if need more space
		b.insert(expoi, new char[diff]);        // insert it
	      else if(diff < 0)                         // need less space
		nums = "+" + nums;                      // filling with a sign should do it
	      for(int j = 0; j < len2; j++)             // copy the exponent back
		b.setCharAt(expoi + j, nums.charAt(j));
	      if(state > 0)
		state = 0;                              // end
	    }
	}

	// System.out.println("  state: " + state);	  
      }

    b.setLength(b.length() - 1);
    return b.toString();
  }

  // System.out.println("exit pow");
}
