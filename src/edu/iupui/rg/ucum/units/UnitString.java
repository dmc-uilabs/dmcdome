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
 * $Id: UnitString.java,v 1.3 2003/07/30 15:25:48 sittha Exp $
 */

package edu.iupui.rg.ucum.units;



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
  public static String pow(String s, double exp)
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

		  char[] charArray = (new Double(exp)).toString().toCharArray();
		  int charArrayLen = charArray.length;
		  if (charArray[charArrayLen-1]=='0' && charArray[charArrayLen - 1] == '.')  // can be converted to integer
		    num *= (new Double(exp)).intValue();
		  else
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


	public static String createNewName(double nu, Dimension dim)
	{
		String prefix = getCommonPrefix(nu);
		String name = new String();
		boolean started = false;
		for (int i = 0; i < dim.getMax(); i++) {
			double num = dim.elementAt(i);
			if (num != 0) {
				switch (i) {
					case 0:
						name = name.concat(num > 0 ? "" : "/");
						name = name.concat(num == 1 ? "m" : "m" + num);
						break;
					case 1:
						name = name.concat(started ? (num > 0 ? "." : "/")  : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "g" : "g" + num);
						break;
					case 2:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "s" : "s" + num);
						break;
					case 3:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "rad" : "rad" + num);
						break;
					case 4:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "K" : "K" + num);
						break;
					case 5:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "C" : "C" + num);
						break;
					case 6:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "cd" : "cd" + num);
						break;
					case 7:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "$" : "$" + num);
						break;
					case 8:
						name = name.concat(started ? (num > 0 ? "." : "/") : (num > 0 ? "" : "/"));
						name = name.concat(num == 1 ? "individual" : "individual" + num);
						break;
				}
				started = true;
			}
		}
		return prefix.concat(name);
	}

	public static String getCommonPrefix(double nu)
	{
		String prefix = new String();
		if (nu == 1E24)
            prefix = "Y";
		else if (nu == 1E21)
			prefix = "Z";
		else if (nu == 1E18)
			prefix = "E";
		else if (nu == 1E15)
			prefix = "P";
		else if (nu == 1E12)
			prefix = "T";
		else if (nu == 1E9)
			prefix = "G";
		else if (nu == 1E6)
			prefix = "M";
		else if (nu == 1E3)
			prefix = "k";
		else if (nu == 1E2)
			prefix = "h";
		else if (nu == 1E1)
			prefix = "da";
		else if (nu == 1E-1)
			prefix = "d";
		else if (nu == 1E-2)
			prefix = "c";
		else if (nu == 1E-3)
			prefix = "m";
		else if (nu == 1E-6)
			prefix = "u";
		else if (nu == 1E-9)
			prefix = "n";
		else if (nu == 1E-12)
			prefix = "p";
		else if (nu == 1E-15)
			prefix = "f";
		else if (nu == 1E-18)
			prefix = "a";
		else if (nu == 1E-21)
			prefix = "z";
		else if (nu == 1E-24)
			prefix = "y";
		else if (nu == 1)
			prefix = "";
		else
			prefix = (new Double(nu)).toString()+" of ";
		return prefix;
	}
}
