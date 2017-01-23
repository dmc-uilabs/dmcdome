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

import java.io.IOException;
import java.io.Reader;
import org.skunk.trace.Debug;
import org.skunk.util.GappedIntArray;

public abstract class FlexScanner
{
    protected int spanStart;
    protected int offset=0;
    protected GappedIntArray styleBuffer;

    protected void setOffset(int offset)
    {
	this.offset=offset;
    }

    protected int getOffset()
    {
	return this.offset;
    }

    public void setStyleBuffer(GappedIntArray styleBuffer)
    {
	this.styleBuffer=styleBuffer;
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

    protected int applyStyle(int style, int charOffset, int length)
    {
	return StyleBufferUtilities.applyStyle(styleBuffer, 
					       style, 
					       yystate(), 
					       offset+charOffset, 
					       length);
    }
}

/* $Log: FlexScanner.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.8  2001/02/14 19:15:37  smulloni
/* modified usage of the style buffer to pack state information into its ints.
/* Previously, I had stipulated that a style could only be used in one state
/* per mode; that restriction may now be lifted.
/*
/* Revision 1.7  2001/02/13 22:53:50  smulloni
/* adding a syntax highlighting mode for STML (Skunk Template Markup Language);
/* fixed a bug in SyntaxStyle in reading default.styles.
/*
/* Revision 1.6  2001/02/09 20:00:00  smulloni
/* fixed particularly nasty bug in GappedIntArray.set(int, int[]), and other
/* bugs in the syntax highlighting system.
/*
/* Revision 1.5  2001/02/02 23:30:33  smulloni
/* adding customization features to the text editor.
/*
/* Revision 1.4  2001/01/30 23:03:19  smulloni
/* beginning of integration of syntax highlighting into SimpleTextEditor.
/*
/* Revision 1.3  2001/01/30 18:01:27  smulloni
/* first working beta of syntax highlighting.  Nasty bug in
/* GappedIntArray.remove() fixed.
/*
/* Revision 1.2  2001/01/29 22:28:47  smulloni
/* syntax highlighting package now uses a custom view for painting the
/* highlights.  Fixed bug in get(int, int[]) in GappedIntArray.
/*
/* Revision 1.1  2001/01/25 22:50:48  smulloni
/* integrating JFlex into still-broken syntax highlighting package.
/* */
