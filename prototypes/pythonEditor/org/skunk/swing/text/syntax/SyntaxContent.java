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

import javax.swing.text.BadLocationException;
import javax.swing.text.GapContent;
import javax.swing.undo.UndoableEdit;
import org.skunk.trace.Debug;
import org.skunk.util.GappedIntArray;

public class SyntaxContent extends GapContent
{
    private GappedIntArray styleBuffer;
    private SyntaxTokenizer syntaxTokenizer;
    private FileMode fileMode;
    private boolean isTokenizing;
    private SyntaxDocument syntaxDocument;

    /**
     *  constructs a new SyntaxContent object, with a Flexicizer SyntaxTokenizer.
     */
    public SyntaxContent()
    {
	super();
	this.styleBuffer=new GappedIntArray();
	this.syntaxTokenizer=new Flexicizer();
    }

    /**
     * constructs a new SyntaxContent object with the given initial length.
     * @param initialLength the initial length
     */
    public SyntaxContent(int initialLength)
    {
	super(initialLength);
	this.styleBuffer=new GappedIntArray();
	this.syntaxTokenizer=new Flexicizer();
    }
    /**
     * constructs a new SyntaxContent object with the given SyntaxTokenizer.
     * @param tokenizer the SyntaxTokenizer to install
     */
    public SyntaxContent(SyntaxTokenizer tokenizer)
    {
	super();
	this.styleBuffer=new GappedIntArray();
	this.syntaxTokenizer=tokenizer;
    }

    /**
     * gives access to the style buffer
     */ 
    public GappedIntArray getStyleBuffer()
    {
	return styleBuffer;
    }

    /**
     * gives access to the syntax tokenizer
     */
    public SyntaxTokenizer getSyntaxTokenizer()
    {
	return syntaxTokenizer;
    }

    /**
     * applies a syntax tokenizer.
     * @param syntaxTokenizer the tokenizer
     */ 
    public void setSyntaxTokenizer(SyntaxTokenizer syntaxTokenizer)
    {
	this.syntaxTokenizer=syntaxTokenizer;
    }

    /**
     * gives the content object a reference to the document.
     * A hack, but it needs this at present to call the tokenizer's
     * tokenize() method.
     * @param syntaxDocument the SyntaxDocument which owns this content object
     */
    protected void setSyntaxDocument(SyntaxDocument syntaxDocument)
    {
	this.syntaxDocument=syntaxDocument;
    }
    /**
     * sets the file mode.
     * @param mode the file mode
     */
    public void setFileMode(FileMode mode)
    {
	this.fileMode=mode;
	this.isTokenizing=mode.getShouldHighlight();
    }

    /**
     * returns the file mode of this tokenizer.
     * @return the file mode, or null if the tokenizer currently has no mode
     */
    public FileMode getFileMode()
    {
	return fileMode;
    }

    /**
     * says whether tokenization is on.
     * If it returns true, the syntaxDocument and syntaxTokenizer must both be non-null
     * @return whether tokenization is on.
     */
    public boolean isTokenizing()
    {
	return (syntaxDocument!=null 
		&& syntaxTokenizer!=null 
		&& this.isTokenizing);
    }

    /**
     * sets tokenization on.
     * If there is no tokenizer installed, or if the syntaxDocument is null, 
     * isTokenizing() will return false, regardless of the value set here.
     * @param tokenizing whether to tokenize
     */
    public void setTokenizing(boolean tokenizing)
    {
	this.isTokenizing=tokenizing;
    }

    /**
     * insert string into the content at the given offset.
     * overridden to allocate space in the style buffer for the
     * insert, and to tokenize the document.
     * @param where the offset into the document where the insert should begin
     * @param str the string to insert
     * @exception BadLocationException if the offset is greater than the document length
     */
    public UndoableEdit insertString(int where, String str)
	throws BadLocationException
    {
	if (Debug.DEBUG) Debug.trace(this, 
				     Debug.DP4, 
				     "in insertString({0}, {1})",
				     new Object[] { new Integer(where), 
						    str });
	int growth=str.length();
	int[] tmp=new int[growth];
	for (int i=0;i<growth;i++)
	    tmp[i]=SyntaxStyle.UNFINISHED;
	styleBuffer.insertAt(where, tmp);
	UndoableEdit edit=super.insertString(where, str);
	if (isTokenizing())
	{
	    synchronized(styleBuffer)
	    {
		getSyntaxTokenizer().tokenize(syntaxDocument,
					      where, 
					      growth,
					      0);
	    }
	}
	return edit;
    }

    /**
     * removes characters from the content.
     * overridden to deallocate space from the styleBuffer,
     * and to tokenize the document.
     * @param where the offset of the removal
     * @param nitems the number of characters to remove
     * @exception BadLocationException if the parameters are out of bounds
     */
    public UndoableEdit remove(int where, int nitems)
	throws BadLocationException
    {
	if (Debug.DEBUG) Debug.trace(this, 
				     Debug.DP4, 
				     "in remove({0}, {1})",
				     new Object[] { new Integer(where), 
						    new Integer(nitems) });
	styleBuffer.remove(where, nitems);
	UndoableEdit edit=super.remove(where, nitems);
	if (isTokenizing())
	{
	    synchronized (styleBuffer)
	    {
		getSyntaxTokenizer().tokenize(syntaxDocument, 
					      where, 
					      0,
					      nitems);
	    }
	}
	return edit;
    }
}
