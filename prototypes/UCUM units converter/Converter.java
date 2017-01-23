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
 * $Id: Converter.java,v 1.1.1.1 2003/05/05 16:12:32 renu Exp $
 */

import java.lang.*;
import java.io.*;
import java.math.BigDecimal;
import units.ParseException;
import units.UnitTab;
import units.Unit;

class Converter
{
  public static void main(String argv[])
       throws IOException, ParseException, ClassNotFoundException
  {
    if(argv.length < 3)
      {
	System.out.println(
   "usage: Converter [-r table] [-l database] value from_unit to_unit");
	return;
      }

    int argi = 0;

    if(argv[argi].equals("-r"))      // read a table
      {
	UnitTab.read(argv[++argi]);
	argi++;
      }
    else if(argv[argi].equals("-l")) // load a precompiled units
      {
	UnitTab.load(argv[++argi]);
	argi++;
      }
    else
      UnitTab.read("units.tab");

    if(argv.length - argi < 3)
      {
	System.out.println(
      "usage: Converter [-r table] [-l database] value from_unit to_unit");
	return;
      }

    // read a value
    double mu1 = Double.valueOf(argv[argi++]).doubleValue();

    // read from unit
    Unit u1 = new Unit(argv[argi++]);
    
    u1.dump();

    // read to unit
    Unit u2 = new Unit(argv[argi++]);

    u2.dump();

    // convert
    double mu2 = u1.convertTo(mu1, u2);

    // print out result
    System.out.println(mu1 + " " + u1 + " = " + mu2 + " " + u2);
  }
}
