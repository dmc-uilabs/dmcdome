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

import java.awt.Graphics;
import javax.swing.text.*;

public class SyntaxEditorKit extends StyledEditorKit implements ViewFactory {
    private SyntaxStyle.SyntaxStyleSet styleSet;

    public SyntaxEditorKit() {
        super();
        styleSet = SyntaxStyle.getDefaultStyleSet();
    }

    public SyntaxEditorKit(SyntaxStyle.SyntaxStyleSet styleSet) {
        super();
        this.styleSet = styleSet;
    }

    public SyntaxStyle.SyntaxStyleSet getStyleSet() {
        return this.styleSet;
    }

    public ViewFactory getViewFactory() {
        return this;
    }

    public View create(Element elem) {
        return new SyntaxView(elem, styleSet);
    }

    public Document createDefaultDocument() {
        return new SyntaxDocument();
    }


//     public static class WrappedSyntaxView extends WrappedPlainView
//     {
//     }


    public static class SyntaxView extends PlainView {
        private Segment segment = null;
        private SyntaxStyle.SyntaxStyleSet styleSet;

        public SyntaxView(Element elem, SyntaxStyle.SyntaxStyleSet styleSet) {
            super(elem);
            this.styleSet = styleSet;
            segment = new Segment();
        }

        protected void drawLine(int line, Graphics g, int x, int y) {
            Element elem = getElement().getElement(line);
            try {
                drawElement(elem, g, x, y);
            } catch (BadLocationException ballocks) {
                Debug.trace(this, Debug.DP2, ballocks);
                return;
            }
        }

        private int drawElement(Element elem, Graphics g, int x, int y)
            throws BadLocationException {
            int start = elem.getStartOffset();
            int end = elem.getEndOffset();

            SyntaxDocument document = (SyntaxDocument) getDocument();
            GappedIntArray styleBuffer = document.getStyleBuffer();
            document.getText(start, end - start, segment);
            if (!document.isTokenizing()) {
                JTextComponent jtc = (JTextComponent) getContainer();
                g.setFont(jtc.getFont());
                g.setColor(jtc.getForeground());
                return Utilities.drawTabbedText(segment, x, y, g, this, start);
            } else {
                return drawSyntaxLine(start, end, g, x, y, document, styleBuffer);
            }
        }

        private static void setStyle(Graphics g, SyntaxStyle style) {
            if (style == null) {
                if (Debug.DEBUG) Debug.trace(SyntaxEditorKit.SyntaxView.class, Debug.DP2, "null style");
                style = SyntaxStyle.getStyle(SyntaxStyle.DEFAULT_STYLE);
            }
//style reference was null for Relation GUI so added "if" below.
         if(style != null) {
                g.setColor(style.getForegroundColor());
                g.setFont(style.getFont());
         }
//	     else {
//                g.setColor(java.awt.Color.red);
//	            g.setFont(SyntaxStyle.DEFAULT_FONT);
//         }
        }

        private int drawSyntaxLine(int start,
                                   int end,
                                   Graphics g,
                                   int x,
                                   int y,
                                   SyntaxDocument doc,
                                   GappedIntArray styleBuffer) {
            if (start >= end) return x;
            synchronized (styleBuffer) {
                int styleBuffLen = styleBuffer.length();
                int[] buffArray;
                if (end > styleBuffLen) {
                    if (Debug.DEBUG)
                        Debug.trace(this, Debug.DP2, "end, " + end
                                                     + ", greater than the buffer length: " + styleBuffLen);
                    int diff = end - styleBuffLen;
                    int tmpLen = styleBuffLen - start;
                    buffArray = new int[diff + tmpLen];
                    int[] tmpArray = styleBuffer.get(start, tmpLen);
                    System.arraycopy(tmpArray, 0, buffArray, 0, tmpLen);
                    for (int i = 0; i < diff; i++) {
                        buffArray[i + tmpLen] = SyntaxStyle.DEFAULT;
                    }
                } else {
                    buffArray = styleBuffer.get(start, end - start);
                }

                if (buffArray == null || buffArray.length == 0)
                    return x;

                int id1 = StyleBufferUtilities.getStyle(buffArray[0]);
                int offset = 0;
                int buffLen = buffArray.length;
                int lastElem = buffLen - 1;

                for (int i = 0; i < buffLen; i++) {
                    int id2 = StyleBufferUtilities.getStyle(buffArray[i]);
                    if (id2 != id1 || i == lastElem) {
                        SyntaxStyle s = styleSet.getStyle(id1);
                        if (Debug.DEBUG)
                            Debug.trace(this, Debug.DP7, "style for index {0} is {1}",
                                        new Object[]{new Integer(i), s});
                        setStyle(g, s);
                        if (Debug.DEBUG && id1 == 0) {
                            System.err.println(styleBuffer);
                            System.err.println(GappedIntArray.toString(buffArray));
                        }

                        segment.count = offset;
                        x = Utilities.drawTabbedText(segment, x, y, g, this, start + i);
                        segment.offset += offset;
                        id1 = id2;
                        offset = 0;
                    }
                    offset++;
                }


            }
            return x;
        }
    }
}