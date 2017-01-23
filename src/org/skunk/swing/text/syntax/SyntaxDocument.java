// added constructor to take in SyntaxContent
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

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

public class SyntaxDocument extends DefaultStyledDocument {
    private SyntaxContent content;

    public SyntaxDocument() {
        super(new SyntaxContent(), StyleContext.getDefaultStyleContext());
        this.content = (SyntaxContent) getContent();
        //an unfortunate hack -- content needs a reference to the document class, at least
        //until the content class knows where line breaks are
        content.setSyntaxDocument(this);
    }

    public SyntaxDocument(SyntaxContent synContent) {
        super(synContent, StyleContext.getDefaultStyleContext());
        this.content = synContent;
        //an unfortunate hack -- content needs a reference to the document class, at least
        //until the content class knows where line breaks are
        content.setSyntaxDocument(this);
    }

    public SyntaxTokenizer getSyntaxTokenizer() {
        return content.getSyntaxTokenizer();
    }

    public void setSyntaxTokenizer(SyntaxTokenizer syntaxTokenizer) {
        content.setSyntaxTokenizer(syntaxTokenizer);
    }

    public boolean isTokenizing() {
        return content.isTokenizing();
    }

    public void setTokenizing(boolean tokenizing) {
        content.setTokenizing(tokenizing);
    }

    public final GappedIntArray getStyleBuffer() {
        return content.getStyleBuffer();
    }

    /**
     * sets the file mode.
     * @param mode the file mode
     */
    public void setFileMode(FileMode mode) {
        content.setFileMode(mode);
    }

    /**
     * returns the file mode of this tokenizer.
     * @return the file mode, or null if the tokenizer currently has no mode
     */
    public FileMode getFileMode() {
        return content.getFileMode();
    }

    public void retokenizeAll() {
        if (isTokenizing())
            getSyntaxTokenizer().tokenize(this, 0, getLength(), 0);
    }
}