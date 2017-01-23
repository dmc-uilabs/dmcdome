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
 * $Id: UnitParser.java,v 1.1.1.1 2003/05/05 16:12:33 renu Exp $
 */

package units;

import java.lang.*;
import java.io.*;

class UnitParser
{
  private StringReader   sread = null;
  private StreamTokenizer stok = null;


  public UnitParser(String s)
  {
    // set up the stream tokenizer
    sread = new StringReader(s);
    stok  = new StreamTokenizer(sread);
    stok.resetSyntax();        // we want to be in charge of it all
    stok.wordChars(0x21,0xff);
    stok.ordinaryChar('.');
    stok.ordinaryChar('/');
    stok.ordinaryChar('[');   // brackets are used to indicate that no prefix is used
    stok.ordinaryChar(']');   // ... or for mm[Hg] and cm[H2O]
    stok.ordinaryChar('{');   // braces can be used for meaningless annotations
    stok.ordinaryChar('}');   // 
    stok.ordinaryChar('(');   // parentheses are used for term nesting only
    stok.ordinaryChar(')');
    stok.ordinaryChars('0','9');     // numerals ...
    stok.ordinaryChar('+');          // ... include a positive sign ...
    stok.ordinaryChar('-');          // ... and of course the minus.
    stok.ordinaryChar('*');   // this allows special `10*'
    stok.whitespaceChars(' ',' ');   // whitespace is discarded, but currently two
    stok.whitespaceChars('\t','\t'); // tokens side by side w/o an operator are a
    stok.whitespaceChars('\n','\n'); // syntax error
    stok.whitespaceChars('\r','\r');        
  }

  /** Here we try to picture the error position. As always we realize
   * harshly what a bad programming environmaent Java is. The IO
   * library is of minor quality compared with C++ IOStreams. Even the
   * simplest task to get the current character position from a stream
   * is not supported.
   *
   * */
  private String errpos()
       throws IOException
  {    
    char rest[]  = new char[128];
    char head[] = new char[128];
    String heads = null;
    String rests = null;

    int nrest = sread.read(rest, 0, 128);
    if(nrest > -1)
      {
	rests = new String(rest, 0, nrest);    
	if(nrest == 128)
	  rests += "...";
      }
    sread.reset();
    int nhead = sread.read(head, 0, 128);
    if(nhead > -1)
      {
	heads = new String(head, 0, nhead);
	if(nhead == 128)
	  heads += "...";
      }
    if(rests != null)
      {
	int idx = heads.indexOf(rests);
	if(idx > -1)
	  {
	    heads = heads.substring(0,idx);
	    return heads + "|" + rests;
	  }
      }
    return heads + "|";
  }

  public final void parse(Unit unit)
       throws ParseException
  { 
    // System.out.print("enter parse "); unit.dump();

    unit.assignUnity();

    try
      {
	sread.mark(0);

	stok.nextToken();
	// System.out.println("next token: `" + stok.toString() + "'");

	if(! term(unit))
	  throw new ParseException("at `" + errpos() + "'");
	else
	  {
	    // System.out.print("exit parse "); unit.dump();
	    return;
	  }
      }
    catch(IOException x) {      
      throw new ParseException("IOException: " + x.getMessage());
    }
  }
  
  private final boolean term(Unit unit)
       throws ParseException, IOException
  {
    // System.out.print("enter term "); unit.dump();

    Unit power     = new Unit();
    final int op_asg =  0;
    final int op_mul =  1;
    final int op_div = -1;
    int operation  = op_asg;

    if(stok.ttype == '/')
      {
	// System.out.println("term will divide");	
	operation = op_div;

	stok.nextToken();
	// System.out.println("next token: `" + stok.toString() + "'");
      }

    while(power(power))
      {
	switch(operation) {
	case op_div: unit.div(power);    break;
	case op_mul: unit.mul(power);    break;
	case op_asg: unit.assign(power); break;
	}

	power.assignUnity();

	// System.out.print("term now "); unit.dump();
	// System.out.println("term token is `" + stok.toString() + "'");

	switch(stok.ttype) {
	case '/': 
	  // System.out.println("term will divide");
	  operation = op_div; 
	  stok.nextToken();
	  // System.out.println("next token: `" + stok.toString() + "'");
	  break;
	case '.': 
	  // System.out.println("term will multiply");
	  operation = op_mul; 
	  stok.nextToken();
	  // System.out.println("next token: `" + stok.toString() + "'");
	  break;
	case StreamTokenizer.TT_EOF:    // finish parsing
	  // System.out.print("exit term (t) "); unit.dump();
	  return true;
	case ')': // if I am a nested term
	  // System.out.print("exit nested term (t) "); unit.dump();
	  return true;
	default: 
	  throw new ParseException("unit or operators `/' or `.' required between powers" +
			       " at `" + errpos() + "'");
	}
      }

    // System.out.print("exit term (f) "); unit.dump();
    return false;
  }

  private final boolean power(Unit unit)
       throws ParseException, IOException
  {
    // System.out.print("enter power "); unit.dump();

    if(simple(unit))
      {
	if("+-0123456789".indexOf(stok.ttype) != -1)
	  {
	    Integer expI = number();
	    if(expI != null)
	      unit.pow(expI.intValue());
	  }
	
	// System.out.print("exit power (t) "); unit.dump();
	return true;
      }
    else
      {
	// System.out.print("exit power (f) "); unit.dump();	
	return false;
      }
  }

  private void real_simple(Unit unit, String S)
       throws ParseException
  {
    // System.out.print("enter real simple `" + S + "' "); unit.dump();
    int L = S.length();
    for(int I = 1; I <= L; I++)
      {
	String P = S.substring(0, L - I);
	// System.out.println("  prefix: `" + P + "'");

	if(Prefix.isDefined(P))
	  {
	    String U = S.substring(L - I);
	    // System.out.println("  atom: `" + U + "'");

	    if(UnitAtom.isDefined(U))
	      {
		// System.out.println("  take: `" + U + "'");

		UnitAtom ua = new UnitAtom(U);

		if(P.length() > 0)
		  {
		    // System.out.println("  prefix: `" + P + "'");

		    if(! ua.isMetric())
		      {
			// System.out.println("  non-metric!");
			continue;
		      }

		    unit.assign(ua);
		    double fact = Prefix.factorFor(P);		    
		    unit.mul(fact);
		  }
		else
		  unit.assign(ua);
		
		// System.out.print("exit real simple (t) "); unit.dump();    
		return;
	      }
	  }
      }
    
    // System.out.print("exit real simple (f) "); unit.dump();    
    throw new ParseException("no matching unit for `" + S + "'");
  }

  private final boolean simple(Unit unit)
       throws ParseException, IOException
  {
    // System.out.print("enter simple "); unit.dump();

    String S = "";
    int depth = 0;

    switch(stok.ttype) {
    case '{': // skip everything up to closing brace
      skipJunk();
      return true;
    case ']':
      throw new ParseException("mismatched brackets at " + stok.toString());      
      // NOTREACHED
    case '[':
      depth++;
      stok.sval = "[";
      // FALLTHROUGH
    case StreamTokenizer.TT_WORD:
      // System.out.print("the word was `" + S + "', we add `" + stok.sval + "' ");
      S = S + stok.sval;

      while(true) {
	// System.out.println("[" + depth + "] the word is now `" + S + "'");
	stok.nextToken();
	// System.out.println("next token: `" + stok.toString() + "'");
	switch(stok.ttype) {
	case '[':
	  S = S + "[";
	  depth++;
	  break;
	case ']':
	  S = S + "]";
	  depth --;
	  break;
	case '{': // skip everything up to closing brace
	  skipJunk();
	  break;
	case StreamTokenizer.TT_EOF:
	  if(depth>0)
	    throw new ParseException("mismatched brackets at " + stok.toString());
	  // FALLTHROUGH
	case StreamTokenizer.TT_WORD:
	default:
	  if(depth>0)
	    {
	      if(stok.sval == null)
		S = S + (char)stok.ttype;
	      else
		S = S + stok.sval;
	      break;
	    }

	  real_simple(unit, S);	  

	if(stok.ttype == StreamTokenizer.TT_WORD)
	  {
	    stok.nextToken();
	    // System.out.println("next token: `" + stok.toString() + "'");
	  }

	  return true;
	}
      }
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
    case '+': 
    case '-':
      Integer numI = number();
      if(numI != null) // we may have a number
	{
	  unit.assign(new Unit());
	  unit.mul(numI.intValue());
	  
	  // System.out.print("exit simple (number) (t) "); unit.dump();
	  return true;
	}
      else
	{ // or number found a trailing '*' and mutates into a word token
	  if(stok.ttype == StreamTokenizer.TT_WORD)
	    if(simple(unit)) // recurse in lack of goto
	      {
		// System.out.print("exit simple (*) (t) "); unit.dump();
		return true;
	      }	

	  // System.out.print("exit simple (f) "); unit.dump();
	  return false;	  
	}

    case '(':
      // System.out.print("nested term "); unit.dump();

      stok.nextToken();
      // System.out.println("next token: `" + stok.toString() + "'");
      
      if(term(unit))
	{
	  // System.out.print("nested term is "); unit.dump();
	  
	  if(stok.ttype != ')')
	    throw new ParseException("missing `)' at " + errpos());
	  else
	    {
	      stok.nextToken();
	      // System.out.println("next token: `" + stok.toString() + "'");
	      
	      // System.out.print("exit simple (nested term) (t) "); unit.dump();
	      return true;
	    }
	}
    default:
      // System.out.print("exit simple (f) "); unit.dump();
      return false;
    }
  }

  private final Integer number()
       throws ParseException, IOException
  {   
    // System.out.println("enter number");

    int state  = 0;
    int result = 0;
    int sign   = 0;
    String  s  = "";

    while(true)
      {
	char c = (char)stok.ttype;
	switch(c) {
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
	  state = 2;
	  result = result * 10 + ((int)c - (int)'0');
	  break;
	case '*': // the number was in fact a literal
	  if(state == 2)
	    {
	      stok.ttype = StreamTokenizer.TT_WORD;
	      stok.sval = s + c;
	      // System.out.println("exit number WORD = `" + stok.sval + "'");
	      return null;
	    }
	  else
	    {
	      // System.out.println("exit number");
	      return null;
	    }	    
	case '+':
	  if(state == 0) 
	    {
	      state = 1;
	      sign = +1;
	      break;
	    }
	  // *FALLTHROUGH*
	case '-':
	  if(state == 0)
	    {
	      state = 1;
	      sign = -1;
	      break;
	    }
	  // *FALLTHROUGH*
	default:
	  if(state == 2)
	    {
	      if(sign == 0) sign = 1;
	      // System.out.println("exit number = " + sign * result);	      
	      return new Integer(sign * result);
	    }
	  else
	    {
	      // System.out.println("exit number");
	      return null;
	    }
	}
	s = s + c;

	stok.nextToken();
	// System.out.println("next token: `" + stok.toString() + "'");
      }	
  }


  void skipJunk() 
       throws ParseException, IOException
  {
    // System.out.println("entering annotation");
    do
      {
	// System.out.println("token skipped in annotation " + stok);
	stok.nextToken();
	if(stok.ttype == '{')
	  throw new ParseException("annotations must not be nested");
	else if(stok.ttype == stok.TT_EOF)
	  throw new ParseException("end of input in annotation");	    
      }
    while(stok.ttype != '}');
    // System.out.println("leaving annotation");
    stok.nextToken();
    // System.out.println("next token: `" + stok.toString() + "'");
  }
}
