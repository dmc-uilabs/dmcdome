/*
 *  Copyright (c) 2000, Jacob Smullyan.
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

import javax.swing.text.Segment;
import javax.swing.text.StyleContext;

public interface SyntaxTokenizer
{
    /**
     * updates the document's style buffer.
     * The tokenizer determines the correct parse context
     * from the int parameters.
     * @param doc the SyntaxDocument upon which to operate
     * @param offset the offset of a change to the document
     * @param nInserted the number of inserted characters
     * @param nRemoved the number of removed characters
     */
    void  tokenize(SyntaxDocument doc, int offset, int nInserted, int nRemoved);
}

/* $Log: SyntaxTokenizer.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.8  2001/01/30 23:03:19  smulloni
/* beginning of integration of syntax highlighting into SimpleTextEditor.
/*
/* Revision 1.7  2001/01/29 22:28:47  smulloni
/* syntax highlighting package now uses a custom view for painting the
/* highlights.  Fixed bug in get(int, int[]) in GappedIntArray.
/*
/* Revision 1.6  2001/01/20 00:16:07  smulloni
/* moved style buffer to SyntaxDocument class.  I may move it out again.
/* */
