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


public interface SyntaxTokenizer {
    /**
     * updates the document's style buffer.
     * The tokenizer determines the correct parse context
     * from the int parameters.
     * @param doc the SyntaxDocument upon which to operate
     * @param offset the offset of a change to the document
     * @param nInserted the number of inserted characters
     * @param nRemoved the number of removed characters
     */
    void tokenize(SyntaxDocument doc, int offset, int nInserted, int nRemoved);
}