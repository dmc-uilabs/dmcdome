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
 * $Id: UnitTab.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package units;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/** This class represents a definition table for a system of units. It
 * is used to make new static bindings to the classes Dimension,
 * Prefix, UnitAtom, etc.
 *
 * bug Currently there can only be one system of units active at the
 * same time, because the system definition is scattered throughout
 * the package in static global variables.
 *
 * @author Gunther Schadow
 * @version $Id: UnitTab.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 * */
public class UnitTab
{
	public static boolean verbose = false;
	public static Vector UnitVector = new Vector();
	public static Vector editorKeyword;
	public static DefaultTableModel editorTable;

	public static void read(String src) throws IOException
	{

		Dimension.reset();
		Prefix.reset();
		Unit.reset();
		UnitAtom.reset();

		try {
			InputStream is;

			try {
				is = new URL(src).openStream();
			}
			catch (MalformedURLException x) {
				is = new FileInputStream(src);
			}
			parse(is);
		}
		catch (ParseException x) {
			throw new IllegalArgumentException(src + ":" + x.line + ": " + x.getMessage());
		}
	}

	public static void readMore(String src) throws IOException
	{

		// you could, if you wanted to be able to save the original file
		// out again, insert some code to take each line and add it to the
		//UnitVector. as it is i'm only saving newly defined units.

		try {
			InputStream is;

			try {
				is = new URL(src).openStream();
			}
			catch (MalformedURLException x) {
				is = new FileInputStream(src);
			}

			parse(is);
		}
		catch (ParseException x) {
			throw new IllegalArgumentException(src + ":" + x.line + ": " + x.getMessage());
		}
	}

	public static void insert(String newUnit) throws IOException, ParseException
	{
		UnitVector.add(newUnit);
		parse(new StringBufferInputStream(newUnit));
	}

	// added by Sittha Sukkasi
	public static void remove(String newUnit, String newSymbol) throws IOException, ParseException
	{
		System.out.println("converter:removing " + newUnit);
		UnitVector.remove(newUnit);
		UnitAtom.remove(newSymbol);
		;
	}

	public static void insert(String newUnit, Vector keyword, DefaultTableModel table)
	        throws IOException, ParseException
	{
		editorTable = table;
		editorKeyword = keyword;
		UnitVector.add(newUnit);
		parse(new StringBufferInputStream(newUnit));
	}

	// end of addition


	private static void parse(InputStream is)
	        throws IOException, ParseException
	{
		//FIXME: parse should also read comments and add them to the
		//hashtable in a searchable form. (doesn't appear to at the
		//moment, though i can't tell.
		Reader reader = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer stok = new StreamTokenizer(reader);

		stok.resetSyntax();          // we want to be in charge of it all
		stok.wordChars(0x21, 0xff);   // everything is a word char ...
		stok.ordinaryChar('=');      // ... except for the assignment operator,
		stok.eolIsSignificant(true); //     the end of line is end of definition,
		stok.ordinaryChar('(');      //     the start of a function argument,
		stok.ordinaryChar(')');      //     the end of a function argument,
		stok.ordinaryChar('|');
		stok.commentChar('#');       //     and the comment char.
		stok.whitespaceChars(' ', ' ');   // whitespace is discarded
		stok.whitespaceChars('\t', '\t'); //

		stok.nextToken();
		//System.out.println("next token: `" + stok.toString() + "'");

		int dimensions = 0;
		int dimension = 0;
		int line = 0;

		while (stok.ttype != StreamTokenizer.TT_EOF) {
			line++;

			while (stok.ttype == StreamTokenizer.TT_EOL) {
				line++;
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
			}

			if (stok.ttype == StreamTokenizer.TT_EOF)
				break;

			//System.out.println("a new line begins ...");

			String symbol = null;
			String term = null;
			String func = null;
			double factor = 1;
			boolean metric = true;
			String unitDescription = "";
			String unitCategory = "";

			// 1. `case', `dimensions', `base', or the name of the new unit:
			if (stok.ttype != StreamTokenizer.TT_WORD)
				throw new ParseException("symbol expected at " + stok.toString(), line);

			//System.out.println("... with a new word.");

			if (stok.sval.equalsIgnoreCase("case")) {
				//System.out.println("this is the case declaration ...");

				// 0.1 next token is a keyword
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("number expected at " + stok.toString(), line);

				//System.out.println("... whose argument is a keyword " + stok.sval);

				if (stok.sval.equalsIgnoreCase("sensitive"))
					Unit.caseSensitive(true);
				else if (stok.sval.equalsIgnoreCase("insensitive"))
					Unit.caseSensitive(false);
				else
					throw new ParseException("a keyword `sensitive' or `insensitive' expected at " +
					        stok.toString(), line);

				// end of line or file
				stok.nextToken();
				if (stok.ttype != StreamTokenizer.TT_EOL && stok.ttype != StreamTokenizer.TT_EOF)
					throw new ParseException("trailing junk on line " + stok.toString(), line);

				//System.out.println("leaving the branch");

			}
			else if (stok.sval.equalsIgnoreCase("dimensions")) {
				//System.out.println("this is the dimensions declaration ...");

				// 1.1 next token is a number
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("number expected at " + stok.toString(), line);

				//System.out.println("... whose argument is a number: " + stok.sval);

				dimensions = Integer.valueOf(stok.sval).intValue();
				Dimension.setMax(dimensions);

				//System.out.println("dimensions: " + dimensions);

				// end of line or file
				stok.nextToken();
				if (stok.ttype != StreamTokenizer.TT_EOL && stok.ttype != StreamTokenizer.TT_EOF)
					throw new ParseException("trailing junk on line " + stok.toString(), line);

				//System.out.println("leaving the branch");

			}
			else if (stok.sval.equalsIgnoreCase("base")) {

				//System.out.println("this is a base unit declaration ...");

				// 1.2 next token is a symbol for a base unit
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("symbol expected at " + stok.toString(), line);

				//System.out.println("which takes a symbol: " + stok.sval);
				symbol = stok.sval;

				if (dimension >= dimensions)
					throw new ParseException("too few dimensions (" + dimensions + ") specified" +
					        stok.toString(), line);

				//******** read category and description *******//
				stok.nextToken();
				if (stok.ttype != '|')
					throw new ParseException("category delimiter `|' expected at " + stok.toString(), line);

				stok.nextToken();

				while (stok.ttype != '|' & stok.ttype != StreamTokenizer.TT_EOL & stok.ttype != StreamTokenizer.TT_EOF) {
					if (stok.ttype != StreamTokenizer.TT_WORD) {
						if (stok.ttype == '(') {
							unitCategory = unitCategory + " (";
						}
						else if (stok.ttype == ')') {
							unitCategory = unitCategory + ")";
						}
						else {
							throw new ParseException("category expected at " + stok.toString(), line);
						}
					} else {
					if (unitCategory.equals("")) {
						unitCategory = unitCategory + stok.sval;
					}
					else {
						unitCategory = unitCategory + " " + stok.sval;
					}
					}
					stok.nextToken();
				}

				if (stok.ttype == '|') {
					stok.nextToken();
					while (stok.ttype != StreamTokenizer.TT_EOL & stok.ttype != StreamTokenizer.TT_EOF) {
						if (stok.ttype != StreamTokenizer.TT_WORD) {
							if (stok.ttype == '(') {
								unitDescription = unitDescription + " (";
							}
							else if (stok.ttype == ')') {
								unitDescription = unitDescription + ")";
							}
							else {
								throw new ParseException("description expected at " + stok.toString(), line);
							}
						} else {

						if (unitDescription.equals("")) {
							unitDescription = unitDescription + stok.sval;
						}
						else {
							unitDescription = unitDescription + " " + stok.sval;
						}
						}
						stok.nextToken();

					}
				}

				Unit base = new UnitAtom(symbol, 1, new Dimension(dimension++), unitDescription, unitCategory);

				/*	UnitAtom myUnit =  UnitAtom.getUnit("m");
					System.out.println("Name:"+myUnit.name+";nu:"+myUnit.nu+"Dimen:"+myUnit.u_vec+"Func:"+myUnit.cnv+"Pfix:"+myUnit.cnv_pfx
					+"Category:"+myUnit.category+"Description:"+myUnit.description);
			  */

				if (verbose) {
					System.out.print("base\t");
					base.dump();
				}

				// end of line or file
				if (stok.ttype != StreamTokenizer.TT_EOL && stok.ttype != StreamTokenizer.TT_EOF)
					throw new ParseException("trailing junk on line " + stok.toString(), line);

				//System.out.println("leaving the branch");

			}
			else if (stok.sval.equalsIgnoreCase("prefix")) {

				//System.out.println("this is a prefix ...");

				// next token is a symbol for a prefix
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("symbol expected at " + stok.toString(), line);

				//System.out.println("which takes a symbol: " + stok.sval);

				String ps = stok.sval;

				// next token is a factor for the prefix
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("number expected at " + stok.toString(), line);

				Prefix pfx = new Prefix(ps, Double.valueOf(stok.sval).doubleValue());

				if (verbose) {
					pfx.dump();
				}

				// end of line or file
				stok.nextToken();
				if (stok.ttype != StreamTokenizer.TT_EOL && stok.ttype != StreamTokenizer.TT_EOF)
					throw new ParseException("trailing junk on line " + stok.toString(), line);

				//System.out.println("leaving the branch");

			}
			else { // a normal symbol

				//System.out.println("this should be a derived unit ...");

				// 1. the symbol to be defined
				symbol = stok.sval;

				//System.out.println("... named " + symbol);

				// 2. the equal sign
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != '=')
					throw new ParseException("assignment operator `=' expected at " + stok.toString(), line);

				//System.out.println("... yes, the equal sign is fine ...");

				// 3. the factor or function name
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");
				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("factor or function expected at " + stok.toString(), line);

				//System.out.println("now the factor or a function: " + stok.sval);

				String facts = stok.sval;

				// 4. a function is followed by '('
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");

				if (stok.ttype == '(') {
					func = facts;

					//System.out.println("... it was the function name " + func);

					// 5. we actually read a function name, the factor comes now
					stok.nextToken();
					//System.out.println("next token: `" + stok.toString() + "'");
					if (stok.ttype != StreamTokenizer.TT_WORD)
						throw new ParseException("term expected at " + stok.toString(), line);

					facts = stok.sval;

					stok.nextToken();
					//System.out.println("next token: `" + stok.toString() + "'");
				}

				// the factor (for sure)
				//System.out.println("the factor is: " + facts);

				// edited by Sittha Sukkasi
				try {
					factor = Double.valueOf(facts).doubleValue();
				}
				catch (NumberFormatException x) {
					ParseException y = new ParseException("definition format error: " + x.getMessage(), line);
					// remove the foul unit definition
					UnitVector.remove(UnitVector.lastElement());
					editorKeyword.remove(editorKeyword.lastElement());
					editorTable.removeRow(editorTable.getRowCount() - 1);
					throw y;
				}
				//end of edition


				// 4. the defining term

				if (stok.ttype != StreamTokenizer.TT_WORD)
					throw new ParseException("term expected at " + stok.toString(), line);

				term = stok.sval;

				//System.out.println("the term " + term + " ...");

				if (func != null) {
					// 6. a closing parenthesis
					stok.nextToken();
					//System.out.println("next token: `" + stok.toString() + "'");
					if (stok.ttype != ')')
						throw new ParseException("closing parentheses `)' expected at " +
						        stok.toString(), line);

					//System.out.println("... ended by a proper closing parenthesis.");
				}

				// 4. a final keyword, EOL or EOF
				stok.nextToken();
				//System.out.println("next token: `" + stok.toString() + "'");

				// metric or nonmetric qualifier
				if (stok.ttype == StreamTokenizer.TT_WORD) {
					//System.out.println("a final keyword " + stok.sval);
					if (stok.sval.equalsIgnoreCase("metric")) {
						//System.out.println("... turn on the metric flag");

						metric = true;
					}
					else if (stok.sval.equalsIgnoreCase("nonmetric")
					        || stok.sval.equalsIgnoreCase("custom")
					        || stok.sval.equalsIgnoreCase("other")) {
						//System.out.println("... turns off the metric flag");

						metric = false;
					}

					else
						throw new ParseException("`metric' or `nonmetric' expected" + stok.toString(), line);

					stok.nextToken();
					//System.out.println("next token: `" + stok.toString() + "'");
				}

				//System.out.println("start internalizing the definition");

				//******** read category and description *******//
				if (stok.ttype != '|')
					throw new ParseException("category delimiter `|' expected at " + stok.toString(), line);

				stok.nextToken();

				while (stok.ttype != '|' & stok.ttype != StreamTokenizer.TT_EOL & stok.ttype != StreamTokenizer.TT_EOF) {
					if (stok.ttype != StreamTokenizer.TT_WORD) {
						if (stok.ttype == '(') {
							unitCategory = unitCategory + " (";
						}
						else if (stok.ttype == ')') {
							unitCategory = unitCategory + ")";
						}
						else {
							throw new ParseException("category expected at " + stok.toString(), line);
						}
					} else {

					if (unitCategory.equals("")) {
						unitCategory = unitCategory + stok.sval;
					}
					else {
						unitCategory = unitCategory + " " + stok.sval;
					}
					}
					stok.nextToken();
				}

				if (stok.ttype == '|') {
					stok.nextToken();
					while (stok.ttype != StreamTokenizer.TT_EOL & stok.ttype != StreamTokenizer.TT_EOF) {
						if (stok.ttype != StreamTokenizer.TT_WORD) {
							if (stok.ttype == '(') {
								unitDescription = unitDescription + " (";
							}
							else if (stok.ttype == ')') {
								unitDescription = unitDescription + ")";
							}
							else {
								throw new ParseException("description expected at " + stok.toString(), line);
							}

						}
						else {

							if (unitDescription.equals("")) {
								unitDescription = unitDescription + stok.sval;
							}
							else {
								unitDescription = unitDescription + " " + stok.sval;
							}
						}
							stok.nextToken();

					}
				}

				try {
					// define
					Function cnv = null;
					if (func != null) {
						cnv = Function.forName(func);
					}

					Unit runit = new Unit(term);
					runit.mul(factor);

					//System.out.print("runit "); runit.dump();
					Unit lunit = new UnitAtom(symbol, runit, cnv, 1, metric, unitDescription, unitCategory);

					//System.out.print("symbol: " + symbol + " ;");
					//System.out.print("runit: " + runit + " ;");
					//System.out.print("cnv: " + cnv + " ;");
					//System.out.println("metric: " + metric);
					/*  UnitAtom myUnit =  UnitAtom.getUnit("10*");
					System.out.println("Name:"+myUnit.name+";nu:"+myUnit.nu+"Dimen:"+myUnit.u_vec+"Func:"+myUnit.cnv+"Pfix:"+myUnit.cnv_pfx
					+"Category:"+myUnit.category+"Description:"+myUnit.description);
			    */

					if (verbose) {
						System.out.print("derived\t");
						lunit.dump();
					}
				}
				catch (Exception x) {
					ParseException y = new ParseException(x.getMessage(), line);
					throw y;
				}

				//System.out.println("leaving the branch");
			}

			//System.out.println("make sure the rest of the line is junk-free ...");

			// end of line or file
			if (stok.ttype != StreamTokenizer.TT_EOL && stok.ttype != StreamTokenizer.TT_EOF)
				throw new ParseException("trailing junk on line " + stok.toString(), line);

			stok.nextToken();
			//System.out.println("next token: `" + stok.toString() + "'");
			//System.out.println("... and climb back up the cycle.");


		}
		//System.out.println(UnitAtom.elements());
		//System.out.println(UnitAtom.getAllUnits());

		//System.out.println(UnitAtom.getDescription("Ao"));
	/*	for (Enumeration e = UnitAtom.elements(); e.hasMoreElements();) {
			//System.out.println(e.nextElement());
			UnitAtom myUnit = (UnitAtom) e.nextElement();
			System.out.println("Name:" + myUnit.name + ";nu:" + myUnit.nu + ";Dimen:" + myUnit.u_vec + ";Func:" + myUnit.cnv + ";Pfix:" + myUnit.cnv_pfx
			        + ";Category:" + myUnit.category + ";Description:" + myUnit.description);

		} */

	/*	Hashtable myHashtable = UnitAtom.getUnitsWithSameCategory("time unit");
		for (Enumeration e = myHashtable.elements(); e.hasMoreElements();) {
			//System.out.println(e.nextElement());
			UnitAtom myUnit = (UnitAtom) e.nextElement();
			System.out.println("Name:" + myUnit.name + ";Category:" + myUnit.category + ";Description:" + myUnit.description);

		}
		//UnitAtom myUnit =  UnitAtom.getUnit("Bi");
		//System.out.println("Name:"+myUnit.name+";nu:"+myUnit.nu+"Dimen:"+myUnit.u_vec+"Func:"+myUnit.cnv+"Pfix:"+myUnit.cnv_pfx);
    */
		reader.close();

        UnitAtom.unitsCategorizing();

	}


	public static void save(String filename) throws IOException
	{
		File file = new File(filename);

		Enumeration A = UnitVector.elements();
		String ess;
		PrintWriter out = null;
		out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		while (A.hasMoreElements()) {
			ess = (String) A.nextElement();
			ess = ess.concat("\n");
			out.print(ess);
		}
		out.close();

		if (out != null)
			out.close();
	}

	public static void load(String src)
	        throws IOException, ClassNotFoundException
	{
		InputStream is;

		try {
			is = new URL(src).openStream();
		}
		catch (MalformedURLException x) {
			is = new FileInputStream(src);
		}

		ObjectInput ois = new ObjectInputStream(is);

		Dimension.load(ois);
		Unit.load(ois);
		Prefix.load(ois);
		UnitAtom.load(ois);

		ois.close();

		// for(Enumeration e = UnitAtom.elements(); e.hasMoreElements();)
		//   (new UnitAtom((String)e.nextElement())).dump();
	}

	/** Finds ambiguities in units definitions. An ambiguity exists if
	 * and only if there is a u1 and u2 from the set of unit atom names
	 * and a p1 and p2 from the set of prefix names (including the empty
	 * prefix) so that: p1+u1 = p2+u2, where u1 and u2 refer to
	 * different units.
	 *
	 * */
	static public void findAmbiguities()
	{
		Conflict.init();

		Enumeration unitatoms = UnitAtom.elements();
		while (unitatoms.hasMoreElements()) {
			String u1 = (String) unitatoms.nextElement();

			Enumeration prefices = Prefix.elements();
			while (prefices.hasMoreElements()) {
				String p1 = (String) prefices.nextElement();
				int p1_len = p1.length();

				String p1u1 = p1 + u1;

				// System.out.println("considering: " + p1u1);

				int len = p1u1.length();

				for (int i = 0; i < len; i++) {
					if (i == p1_len)
						continue; // of course, if p1 = p2, u1 = u2, that's trivial

					String p2 = p1u1.substring(0, i);

					// System.out.print("against: |" + p2 + "|");

					if (!Prefix.isDefined(p2)) {
						// System.out.println("-");
						continue;
					}

					String u2 = p1u1.substring(i);

					// System.out.println(u2 + "|");

					if (UnitAtom.isDefined(u2))
						new Conflict(p1, u1, p2, u2);
				}
			}
		}

		Conflict.report();
	}
}

class Conflict
{
	static Vector list = null;

	static void init()
	{
		list = new Vector();
	}

	String p1, p2, u1, u2;
	int type;

	static final int STRIAGHT_NAME_CLASH = 1; // e.g. ISO 2955 "a" (year) vs. "a" (are)
	static final int METRIC_METRIC = 2; // e.g. ISO 2955 "PE|V" vs. "P|EV"
	static final int NONMETRIC_NONMETRIC = 3; // e.g. ANSI X3.50 "NMI" vs. "N|MI"
	static final int METRIC_NONMETRIC = 4; // e.g. ISO 2955 "C|D" vs. "CD"
	static final int NONMETRIC_METRIC = 5; // e.g. ISO+ANSI "FT" vs. "F|T"
	static final int NONMETRIC_OTHER = 6; // e.g. ISO 2955 "C|D" vs. "CD"

	static final String typenames[] = {
		"I   (atom conflict)",
		"II  (metric-metric)",
		"III (nonmetric-nonmetric)",
		"IVa (metric-nonmetric)",
		"IVb (nonmetric-metric)",
		"V   (nonmetric other)"
	};

	Conflict(String _p1, String _u1, String _p2, String _u2)
	{

		// u1 always is the longer unit atom name
		if (_u1.length() >= _u2.length()) {
			p1 = _p1;
			p2 = _p2;
			u1 = _u1;
			u2 = _u2;
		}
		else {
			p1 = _p2;
			p2 = _p1;
			u1 = _u2;
			u2 = _u1;
		}

		if (!isKnown()) {
			UnitAtom u1a = new UnitAtom(u1);
			UnitAtom u2a = new UnitAtom(u2);

			// a straight clash can not even enter our database

			// metric vs. metric
			if (u1a.isMetric())
				if (u2a.isMetric())
					type = METRIC_METRIC;
				else
					type = METRIC_NONMETRIC;
			else if (u2a.isMetric())
				if (p1.length() == 0)
					type = NONMETRIC_METRIC;
				else
					type = NONMETRIC_OTHER;
			else
				type = NONMETRIC_NONMETRIC;

			list.addElement(this);
		}
	}

	boolean isKnown()
	{
		Enumeration e = list.elements();
		while (e.hasMoreElements()) {
			Conflict c = (Conflict) e.nextElement();

			if (c.u1.equals(u1) && c.p1.equals(p1) &&
			        c.u2.equals(u2) && c.p2.equals(p2))
				return true;
		}
		return false;
	}

	static void report()
	{
		Enumeration e = list.elements();
		while (e.hasMoreElements()) {
			Conflict c = (Conflict) e.nextElement();

			System.out.println("conflict: " +
			        (c.p1.equals("") ? "" : c.p1 + "-") + c.u1 +
			        " vs. " +
			        (c.p2.equals("") ? "" : c.p2 + "-") + c.u2 +
			        "\tType " + typenames[c.type - 1]);
		}
	}
}
