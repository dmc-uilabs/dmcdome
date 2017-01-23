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

import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import org.skunk.trace.Debug;
import org.skunk.util.GappedIntArray;

public class SyntaxDocument extends DefaultStyledDocument
{
    private SyntaxContent content;

    public SyntaxDocument()
    {
	super(new SyntaxContent(), StyleContext.getDefaultStyleContext());
	this.content=(SyntaxContent)getContent();
	//an unfortunate hack -- content needs a reference to the document class, at least
	//until the content class knows where line breaks are
	content.setSyntaxDocument(this);
    }
    
    public SyntaxTokenizer getSyntaxTokenizer()
    {
	return content.getSyntaxTokenizer();
    }
    
    public void setSyntaxTokenizer(SyntaxTokenizer syntaxTokenizer)
    {
	content.setSyntaxTokenizer(syntaxTokenizer);
    }

    public boolean isTokenizing()
    {
	return content.isTokenizing();
    }

    public void setTokenizing(boolean tokenizing)
    {
	content.setTokenizing(tokenizing);
    }

    public final GappedIntArray getStyleBuffer()
    {
	return content.getStyleBuffer(); 
    }

    /**
     * sets the file mode.
     * @param mode the file mode
     */
    public void setFileMode(FileMode mode)
    {
	content.setFileMode(mode);
    }

    /**
     * returns the file mode of this tokenizer.
     * @return the file mode, or null if the tokenizer currently has no mode
     */
    public FileMode getFileMode()
    {
	return content.getFileMode();
    }

    public void retokenizeAll()
    {
	if (isTokenizing())
	    getSyntaxTokenizer().tokenize(this, 0, getLength(), 0);
    }
}

/* $Log: SyntaxDocument.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.17  2001/02/14 19:15:37  smulloni
/* modified usage of the style buffer to pack state information into its ints.
/* Previously, I had stipulated that a style could only be used in one state
/* per mode; that restriction may now be lifted.
/*
/* Revision 1.16  2001/02/11 22:55:39  smulloni
/* in order to capture undo and redo events, moving the style buffer and the
/* tokenizer into the SyntaxContent class, a subclass of GapContent.
/*
/* Revision 1.15  2001/02/09 20:00:01  smulloni
/* fixed particularly nasty bug in GappedIntArray.set(int, int[]), and other
/* bugs in the syntax highlighting system.
/*
/* Revision 1.14  2001/02/02 23:30:33  smulloni
/* adding customization features to the text editor.
/*
/* Revision 1.13  2001/01/30 23:03:19  smulloni
/* beginning of integration of syntax highlighting into SimpleTextEditor.
/*
/* Revision 1.12  2001/01/30 18:01:27  smulloni
/* first working beta of syntax highlighting.  Nasty bug in
/* GappedIntArray.remove() fixed.
/*
/* Revision 1.11  2001/01/29 22:28:47  smulloni
/* syntax highlighting package now uses a custom view for painting the
/* highlights.  Fixed bug in get(int, int[]) in GappedIntArray.
/*
/* Revision 1.10  2001/01/25 22:50:48  smulloni
/* integrating JFlex into still-broken syntax highlighting package.
/*
/* Revision 1.9  2001/01/20 00:16:07  smulloni
/* moved style buffer to SyntaxDocument class.  I may move it out again.
/*
/* Revision 1.8  2001/01/18 22:29:21  smulloni
/* experimental work on syntax package.
/*
/* Revision 1.7  2001/01/17 23:02:29  smulloni
/* beginning to rework SyntaxDocument, SyntaxTokenizer, to shift the
/* responsibility for handling context requirements from the document
/* to the tokenizer.
/*
/* Revision 1.6  2001/01/16 21:13:24  smulloni
/* more work on syntax highlighting infrastructure.
/*
/* Revision 1.5  2001/01/15 23:31:29  smulloni
/* the syntax highlighting system crawls pitifully towards being less
/* contemptible.
/*
/* Revision 1.4  2001/01/12 23:30:06  smulloni
/* more hacking on syntax highlighting, which still sucks.
/*
/* Revision 1.3  2001/01/11 00:00:03  smulloni
/* work on syntax highlighting, still very rough.
/*
/* Revision 1.2  2000/12/28 21:58:57  smulloni
/* fiddling with syntax highlighting, not to much purpose.
/*
/* Revision 1.1  2000/12/27 22:05:09  smulloni
/* work on syntax highlighting.
/* */







