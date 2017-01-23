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

import org.skunk.trace.Debug;
import org.skunk.util.GappedIntArray;

public final class StyleBufferUtilities
{
    public static final int STYLE_BUFFER_STYLE_MASK=31;
    public static final int STYLE_BUFFER_STATE_MASK=992;

    public static final int getState(int styleBufferEntry)
    {
	return (styleBufferEntry & STYLE_BUFFER_STATE_MASK)>>5;
    }

    public static final int getStyle(int styleBufferEntry)
    {
	return (styleBufferEntry & STYLE_BUFFER_STYLE_MASK);
    }

    public static final int applyStyle(GappedIntArray styleBuffer, 
				       int style, 
				       int state, 
				       int offset, 
				       int length)
    {
	int type=style | (state<<5);
	if (styleBuffer!=null)
	{
	    int[] tmpBuff=new int[length];
	    for (int i=0;i<length;i++)
	    {
		tmpBuff[i]=type; 
	    }
	    
	    if (styleBuffer.length()>offset+length)
	    {
		styleBuffer.set(offset, tmpBuff);
	    }
	    else 
	    {
		Debug.trace(StyleBufferUtilities.class, 
			    Debug.DP2, 
			    "applyStyle tried to set style past end of style buffer");
	    }	    
	}
	return type;
    }
    private StyleBufferUtilities() {} //not meant to be instantiated
}

/* $Log: StyleBufferUtilities.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.1  2001/02/14 19:15:37  smulloni
/* modified usage of the style buffer to pack state information into its ints.
/* Previously, I had stipulated that a style could only be used in one state
/* per mode; that restriction may now be lifted.
/* */
