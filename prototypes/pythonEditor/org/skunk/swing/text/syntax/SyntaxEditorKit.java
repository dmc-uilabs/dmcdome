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

import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import org.skunk.trace.Debug;
import org.skunk.util.GappedIntArray;

public class SyntaxEditorKit extends StyledEditorKit implements ViewFactory
{
    private  SyntaxStyle.SyntaxStyleSet styleSet;

    public SyntaxEditorKit()
    {
	super();
	styleSet=SyntaxStyle.getDefaultStyleSet();
    }

    public SyntaxEditorKit(SyntaxStyle.SyntaxStyleSet styleSet)
    {
	super();
	this.styleSet=styleSet;
    }

    public SyntaxStyle.SyntaxStyleSet getStyleSet()
    {
	return this.styleSet;
    }

    public ViewFactory getViewFactory()
    {
	return this;
    }
    
    public View create(Element elem)
    {
	return new SyntaxView(elem, styleSet);
    }
    
    public Document createDefaultDocument()
    {
	return new SyntaxDocument();
    }

    
//     public static class WrappedSyntaxView extends WrappedPlainView
//     {
//     }


    public static class SyntaxView extends PlainView 
    {
	private Segment segment = null;
	private SyntaxStyle.SyntaxStyleSet styleSet;
	
	public SyntaxView(Element elem, SyntaxStyle.SyntaxStyleSet styleSet)
	{
	    super(elem);
	    this.styleSet=styleSet;
	    segment = new Segment();
	}

	protected void drawLine(int line, Graphics g, int x, int y)
	{
	    Element elem = getElement().getElement(line);
	    try
	    {
		drawElement(elem, g, x, y);
	    }
	    catch (BadLocationException ballocks)
	    {
		Debug.trace(this, Debug.DP2, ballocks);
		return;	
	    }
	}

	private int  drawElement(Element elem, Graphics g, int x, int y) 
	    throws BadLocationException
	{
	    int start = elem.getStartOffset();
	    int end = elem.getEndOffset();

	    SyntaxDocument document=(SyntaxDocument) getDocument();
	    GappedIntArray styleBuffer=document.getStyleBuffer();
	    document.getText(start, end - start, segment);
	    if (!document.isTokenizing())
	    {
		JTextComponent jtc=(JTextComponent) getContainer();
		g.setFont(jtc.getFont());
		g.setColor(jtc.getForeground());
		return Utilities.drawTabbedText(segment, x, y, g, this, start);
	    }
	    else
	    {
		return drawSyntaxLine(start, end, g, x, y, document, styleBuffer);
	    }
	}

	private static void setStyle(Graphics g, SyntaxStyle style)
	{
	    if (style==null)
	    {
		if (Debug.DEBUG) Debug.trace(SyntaxEditorKit.SyntaxView.class, Debug.DP2, "null style");	
		style =SyntaxStyle.getStyle(SyntaxStyle.DEFAULT_STYLE);
	    }
	    g.setColor(style.getForegroundColor());
	    g.setFont(style.getFont());
	}

	private int drawSyntaxLine(int start, 
				   int end,
				   Graphics g, 
				   int x, 
				   int y, 
				   SyntaxDocument doc, 
				   GappedIntArray styleBuffer)
	{
	    if (start>=end) return x;
	    synchronized(styleBuffer)
	    {
		int styleBuffLen=styleBuffer.length();
		int[] buffArray;
		if (end>styleBuffLen)
		{
		    if (Debug.DEBUG) Debug.trace(this, Debug.DP2, "end, "+end
						 +", greater than the buffer length: "+styleBuffLen);
		    int diff=end-styleBuffLen;
		    int tmpLen=styleBuffLen-start;
		    buffArray=new int[diff+tmpLen];
		    int[] tmpArray=styleBuffer.get(start, tmpLen);
		    System.arraycopy(tmpArray, 0, buffArray, 0, tmpLen);
		    for (int i=0;i<diff;i++)
		    {
			buffArray[i+tmpLen]=SyntaxStyle.DEFAULT;
		    }
		}
		else
		{
		    buffArray=styleBuffer.get(start, end-start);
		}
	
		if (buffArray==null || buffArray.length==0)
		    return x;
		
		int id1=StyleBufferUtilities.getStyle(buffArray[0]);
		int offset=0;
		int buffLen=buffArray.length;
		int lastElem=buffLen-1;
	
		for (int i=0;i<buffLen;i++)
		{
		    int id2=StyleBufferUtilities.getStyle(buffArray[i]);
		    if (id2!=id1 || i==lastElem)
		    {
			SyntaxStyle s=styleSet.getStyle(id1);
			if (Debug.DEBUG)
			    Debug.trace(this, Debug.DP7, "style for index {0} is {1}",
					new Object[] {new Integer(i), s});
			setStyle(g, s);
			if (Debug.DEBUG && id1==0) 
			{
			    System.err.println(styleBuffer);
			    System.err.println(GappedIntArray.toString(buffArray));
			}
			
			segment.count=offset;
			x=Utilities.drawTabbedText(segment, x, y, g, this, start+i);
			segment.offset+=offset;
			id1=id2;
			offset=0;
		    }
		    offset++;
		}
                
                 
	    }
	    return x;
	}
    }
}

/* $Log: SyntaxEditorKit.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.7  2001/02/14 19:15:37  smulloni
/* modified usage of the style buffer to pack state information into its ints.
/* Previously, I had stipulated that a style could only be used in one state
/* per mode; that restriction may now be lifted.
/*
/* Revision 1.6  2001/02/09 20:00:01  smulloni
/* fixed particularly nasty bug in GappedIntArray.set(int, int[]), and other
/* bugs in the syntax highlighting system.
/*
/* Revision 1.5  2001/02/08 18:57:23  smulloni
/* fixed several syntax highlighting bugs.
/*
/* Revision 1.4  2001/02/06 22:13:41  smulloni
/* first more-or-less working version of syntax highlighting, with customization.
/*
/* Revision 1.3  2001/02/02 23:30:33  smulloni
/* adding customization features to the text editor.
/*
/* Revision 1.2  2001/01/30 18:01:27  smulloni
/* first working beta of syntax highlighting.  Nasty bug in
/* GappedIntArray.remove() fixed.
/*
/* Revision 1.1  2001/01/29 22:30:20  smulloni
/* support for custom views for syntax highlighting.
/* */
