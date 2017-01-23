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

import org.skunk.util.GappedIntArray;

import java.io.IOException;

public abstract class FlexScanner {
    protected int spanStart;
    protected int offset = 0;
    protected GappedIntArray styleBuffer;

    protected void setOffset(int offset) {
        this.offset = offset;
    }

    protected int getOffset() {
        return this.offset;
    }

    public void setStyleBuffer(GappedIntArray styleBuffer) {
        this.styleBuffer = styleBuffer;
    }

    public abstract void scan() throws IOException;

    /**
     * resets the scanner to use a new Reader.  JFlex implements this for you.
     */
    public abstract void yyreset(java.io.Reader reader) throws IOException;

    public abstract void yybegin(int state);

    /**
     * returns the current state of the scanner.  JFlex implements this for you.
     * @return the current state of the scanner.
     */
    public abstract int yystate();

    protected int applyStyle(int style, int charOffset, int length) {
        return StyleBufferUtilities.applyStyle(styleBuffer,
                                               style,
                                               yystate(),
                                               offset + charOffset,
                                               length);
    }
}