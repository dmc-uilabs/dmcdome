/*
 *  Copyright (c) 2001, Jacob Smullyan.
 *
 *  This is part of SkunkDAV, a WebDAV client.  See http://skunkdav.sourceforge.net/ 
 *  for the latest version.
 * 
 *  SkunkDAV is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 * 
 *  SkunkDAV is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with SkunkDAV; see the file COPYING.  If not, write to the Free
 *  Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
*/


package org.skunk.swing.text.syntax;

//for main method
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

%%
%public
%class PythonFlexScanner
%extends FlexScanner

%unicode
%pack

%char

%type int

%{
  private String quote;

  public void scan() throws IOException
  {
     while ( !yy_atEOF )	
     {
       yylex();
     }
  }

  private int applyStyle(int type) 
  {
     return applyStyle(type, yychar, yylength());
  }
%}

 /* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]
LetterOrUnderscore=[a-zA-Z_]

/* comments */
//SingleLineComment = "#" {InputCharacter}* {LineTerminator}

/* identifiers */
Identifier = {LetterOrUnderscore}({LetterOrUnderscore}|[:jdigit:])*

/* integer literals */


DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]

ImaginaryLiteral=({DecIntegerLiteral}|{FloatLiteral})[jJ]
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}|{FLit4}) 

FLit1 = [0-9]+ \. [0-9]* {Exponent}?
FLit2 = \. [0-9]+ {Exponent}?
FLit3 = [0-9]+ {Exponent}
FLit4 = [0-9]+ {Exponent}?

Exponent = [eE] [+\-]? [0-9]+

/* string and character literals */
//SingleCharacter = [^\r\n\'\\]

%state STRING_STATE, MULTI_LINE_STRING_STATE, COMMENT_STATE

%%

<YYINITIAL> {


  /* keywords */

  "def"                          |
  "class"                        |
  "access"                       |
  "print"                        |
  "exec"                         |
  "lambda"                       |
  "global"                       |
  "assert"                       |
  "del"                          { return applyStyle(SyntaxStyle.KEYWORD1); }

  /* builtin functions */
  "abs"                          |
  "apply"                        |
  "callable"                     |
  "chr"                          |
  "cmp"                          |
  "coerce"                       |
  "compile"                      |
  "delattr"                      |
  "dir"                          |
  "divmod"                       |
  "eval"                         |
//  "exec" //i need a lookahead here to distinguish it from the exec statement
  "execfile"                     |
  "filter"                       |
  "float"                        |
  "getattr"                      |
  "globals"                      |
  "hasattr"                      |
  "hash"                         |
  "hex"                          |
  "id"                           |
  "input"                        |
  "int"                          |
  "len"                          |
  "locals"                       |
  "long"                         |
  "map"                          |
  "max"                          |
  "min"                          |
  "oct"                          |
  "open"                         |
  "ord"                          |
  "pow"                          |
  "range"                        |
  "raw_input"                    |
  "reduce"                       |
  "reload"                       |
  "repr"                         |
  "round"                        |
  "setattr"                      |
  "str"                          |
  "tuple"                        |
  "type"                         |
  "vars"                         |
  "xrange"                       { return applyStyle(SyntaxStyle.KEYWORD2); }

  /* flow control */ 
  "break"                        |
  "except"                       |
  "continue"                     |
  "elif"                         |
  "else"                         |
  "finally"                      |
  "for"                          |
  "if"                           |
  "return"                       |
  "pass"                         |
  "raise"                        |
  "try"                          |
  "while"                        { return applyStyle(SyntaxStyle.KEYWORD3); }

  "from"                         |
  "import"                       { return applyStyle(SyntaxStyle.INCLUDE);  }



  "None"                        { return applyStyle(SyntaxStyle.LITERAL4); }
    
  /* separators */
  "("                            |
  ")"                            |
  "{"                            |
  "}"                            |
  "["                            |
  "]"                            |
  ":"                            |
  ","                            |
  "."                            { return applyStyle(SyntaxStyle.SEPARATOR1); }
  
  /* operators */
  "="                            |
  ">"                            |
  "<"                            |
  "~"                            | 
  "<>"                           |
  "=="                           |
  "<="                           |
  ">="                           |
  "!="                           |
  "+"                            |
  "-"                            |
  "*"                            |
  "/"                            |
  "&"                            |
  "|"                            |
  "^"                            |
  "%"                            |
  "<<"                           |
  ">>"                           |
  "in"                           |
  "and"                          |
  "not"                          |
  "or"                           |
  "is"                           { return applyStyle(SyntaxStyle.OPERATOR); }

  /* numeric literals */

  {DecIntegerLiteral}            |
  {DecLongLiteral}               |
  {HexIntegerLiteral}            |
  {HexLongLiteral}               |
  {OctIntegerLiteral}            |
  {OctLongLiteral}               |
  {FloatLiteral}                 |
  {ImaginaryLiteral}             { return applyStyle(SyntaxStyle.LITERAL2); }
  
  /* empty strings */
  [rR]?(\'\'|\"\")               |
  [rR]?\'\'\'\'\'\'              |
  [rR]?\"\"\"\"\"\"              { return applyStyle(SyntaxStyle.LITERAL1); }

  /* string literals */
  [rR]?[\"\']                    { 
                                    yybegin(STRING_STATE); 
                                    spanStart=yychar; 
                                    quote=yytext();
                                    return applyStyle(SyntaxStyle.LITERAL1);
                                 }


  [rR]?(\'\'\'|\"\"\")           { 
                                    yybegin(MULTI_LINE_STRING_STATE); 
                                    spanStart=yychar; 
                                    quote=yytext();
                                    return applyStyle(SyntaxStyle.LITERAL1);	
                                 }

  /* comments */

  #                              { 
                                   yybegin(COMMENT_STATE);
                                   spanStart=yychar;
                                   return applyStyle(SyntaxStyle.COMMENT1); 
                                 }

  /* whitespace */
  {WhiteSpace}                   { return applyStyle(SyntaxStyle.DEFAULT);}

  /* identifiers */ 
  {Identifier}                   { return applyStyle(SyntaxStyle.IDENTIFIER1); }  


}

<STRING_STATE> {
  [^\\]\"                        { 
				   if (quote.equals("\"") 
                                       || (quote.length()==2 
                                            && quote.substring(1).equals("\"")))
                                   {
                                     yybegin(YYINITIAL); 
                                     return applyStyle(SyntaxStyle.LITERAL1, 
                                                       spanStart, 
                                                       yylength()+yychar-spanStart); 
                                   }
                                 }

  [^\\]\'                        { 
                                  
                                    if (quote.equals("'") 
                                       || (quote.length()==2 
                                            && quote.substring(1).equals("'")))
                                   {
                                     yybegin(YYINITIAL); 
                                     return applyStyle(SyntaxStyle.LITERAL1, 
                                                       spanStart, 
                                                       yylength()+yychar-spanStart); 
                                   }
                                 }
    
  /* error cases.  We never throw an exception. */
  {LineTerminator}               { 
                                   yybegin(YYINITIAL); 
                                   return applyStyle(SyntaxStyle.ERROR, 
                                                     spanStart, 
                                                     yylength()+yychar-spanStart); 
                                 }
}

<MULTI_LINE_STRING_STATE> {
  [^\\]\"\"\"                    {
                                    if (quote.equals("\"\"\"")
                                        || (quote.length()==4
                                            && quote.substring(1).equals("\"\"\"")))
                                    {
                                       yybegin(YYINITIAL);
                                       return applyStyle(SyntaxStyle.LITERAL3,
                                                         spanStart,
                                                         yylength()+yychar-spanStart);
                                    }

                                 }
  [^\\]\'\'\'                    {
                                    if (quote.equals("'''")
                                        || (quote.length()==4
                                            && quote.substring(1).equals("'''")))
                                    {
                                       yybegin(YYINITIAL);
                                       return applyStyle(SyntaxStyle.LITERAL3,
                                                         spanStart,
                                                         yylength()+yychar-spanStart);
                                    }

                                 }

}

<COMMENT_STATE> {
  {InputCharacter}               { applyStyle(SyntaxStyle.COMMENT1); }
  {LineTerminator}               { 
	                            yybegin(YYINITIAL);
                                    return applyStyle(SyntaxStyle.COMMENT1,
                                                      spanStart,
                                                      yylength()+yychar-spanStart);
                                 }

}

/* error fallback */
.|\n                             { return applyStyle(SyntaxStyle.DEFAULT); }

<<EOF>>	                         { return -1;}
