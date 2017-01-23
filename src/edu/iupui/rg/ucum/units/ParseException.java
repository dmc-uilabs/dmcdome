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
 * $Id: ParseException.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package edu.iupui.rg.ucum.units;

public class ParseException extends Exception {
  int line = 0;

  public ParseException() { super(); }
  public ParseException(int line) { super(); this.line = line; }
  public ParseException(String s) { super("parse error: " + s); }
  public ParseException(String s, int line) { this(s); this.line = line; }
}
