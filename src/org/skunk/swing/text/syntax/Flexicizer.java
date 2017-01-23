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

import org.skunk.swing.text.TextEditorPane;
import org.skunk.trace.Debug;
import org.skunk.util.GappedIntArray;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;

public class Flexicizer implements SyntaxTokenizer {
    private Segment segment;
    private static HashMap scannerMap;

    static final String THIS_PACKAGE_PREFIX = "org.skunk.swing.text.syntax.";
    public static final int DEFAULT_REPARSE_DISTANCE = 50;
    private static int reparseDistance;

    static {
        scannerMap = new HashMap();
        try {
            reparseDistance = Integer.parseInt(System.getProperty("syntaxReparseDistance"));
            if (Debug.DEBUG) Debug.trace(Flexicizer.class, Debug.DP2, "syntax reparse distance set to " + reparseDistance);
        } catch (Exception e) {
            reparseDistance = DEFAULT_REPARSE_DISTANCE;
        }
    }

    public Flexicizer() {

        segment = new Segment();

    }

    private static FlexScanner _createScanner(FileMode fileMode, Reader r) throws ModeNotSupportedException {
        /*
          I use the convention that the name of the scanner
          class will be the name of the mode, capitalized,
          + "FlexScanner".  Does this suck?  Probably.
        */
        int firstClassIndex = THIS_PACKAGE_PREFIX.length();
        StringBuffer sb = new StringBuffer(THIS_PACKAGE_PREFIX).append(fileMode.getName()).append("FlexScanner");
        sb.setCharAt(firstClassIndex, Character.toUpperCase(sb.charAt(firstClassIndex)));
        try {
            Class c = Class.forName(sb.toString());
            Constructor struction = c.getConstructor(new Class[]{Reader.class});
            return (FlexScanner) struction.newInstance(new Object[]{r});
        } catch (Exception couldBeLots) {
            throw new ModeNotSupportedException(fileMode, couldBeLots);
        }
    }

    /**
     * obtain a scanner from the pool which matches the given file mode, installing the given reader.
     * @param fileMode the FileMode of the scanner
     * @param r the Reader which gives access to the text to be lexed
     * @return the scanner, or null if no scanner can be found for the file mode.
     */
    protected static final FlexScanner getScanner(FileMode fileMode, Reader r) {
        if (fileMode == null) return null;
        FlexScanner scanner = null;
        if (scannerMap.containsKey(fileMode))
            scanner = (FlexScanner) scannerMap.get(fileMode);
        if (scanner == null) {
            try {
                scanner = _createScanner(fileMode, r);
                scannerMap.put(fileMode, scanner);
            } catch (ModeNotSupportedException monster) {
                if (Debug.DEBUG) Debug.trace(Flexicizer.class, Debug.DP2, monster);
            }
        } else {
            try {
                scanner.yyreset(r);
            } catch (IOException oyVeh) {
                if (Debug.DEBUG) Debug.trace(Flexicizer.class, Debug.DP2, oyVeh);
                scanner = null;
            }
        }
        return scanner;
    }

    /**
     * callback to tokenizer, which then tokenizes the necessary area around the indicated change.
     * @param document the document
     * @param offset the offset of the change to the document
     * @param nInserted the numbers of characters inserted
     * @param nRemoved the number of characters removed
     */
    public void tokenize(SyntaxDocument document, int offset, int nInserted, int nRemoved) {
        if (Debug.DEBUG)
            Debug.trace(this, Debug.DP6,
                        "offset: " + offset + ", nInserted: " + nInserted + ", nRemoved" + nRemoved);
        int lastMod = offset + nInserted;
        if (nInserted == 0 && nRemoved == 0) return;
        GappedIntArray styleBuffer = document.getStyleBuffer();
        int beginParse = backwardContext(styleBuffer,
                                         document.getParagraphElement(offset).getStartOffset());
        int max = document.getLength() - 1;
        if (max <= 0) return;
        int endParse = forwardContext(styleBuffer,
                                      document.getParagraphElement(lastMod).getEndOffset(),
                                      max);
        if (Debug.DEBUG) Debug.trace(this, Debug.DP6, "beginParse: " + beginParse + ", endParse: " + endParse);
        customHighlight(beginParse, endParse);
        int lastStyle;
        Element rootElement = document.getDefaultRootElement();
        int lineNo = rootElement.getElementIndex(lastMod);

        while (!parseRange(document, beginParse, endParse)) {
            if (Debug.DEBUG) Debug.trace(this, Debug.DP4, "null returned from parseRange, will continue to parse");
            Element elem = rootElement.getElement(++lineNo);
            if (elem == null)
                break;
            beginParse = elem.getStartOffset();
            endParse = Math.min(max, elem.getEndOffset());
        }
    }

    protected void customHighlight(int beginParse, int endParse) {
        // to be overridden by subclasses
    }

    /**
     * how many characters around the insertion point should be reparsed.
     */
    public int getReparseDistance() {
        return reparseDistance;
    }

    private int forwardContext(GappedIntArray styleBuffer, int forward, int max) {
        //sanity check
        forward = Math.min(forward + reparseDistance, max);
        int tmp1 = styleBuffer.get(forward);
        int tmp2 = tmp1;
        boolean reachedBarrier = false;
        while (forward < max) {
            tmp1 = styleBuffer.get(forward);
            if (reachedBarrier && tmp1 != tmp2) {
                forward--;
                break;
            }
            if (tmp1 == SyntaxStyle.DEFAULT)
                reachedBarrier = true;
            tmp2 = tmp1;
            forward++;
        }
        return forward;
    }

    private int backwardContext(GappedIntArray styleBuffer, int back) {
        back = Math.max(0, Math.min(back - reparseDistance, styleBuffer.length() - 1));
        if (back == 0) return 0;
        int tmp1 = styleBuffer.get(back);
        int tmp2 = tmp1;
        boolean reachedBarrier = false;
        while (back > 0) {
            tmp1 = styleBuffer.get(back);
            if (reachedBarrier && tmp1 != tmp2) {
                back++;
                break;
            }
            if (tmp1 == SyntaxStyle.DEFAULT)
                reachedBarrier = true;
            tmp2 = tmp1;
            back--;
        }
        return back;
    }

    private boolean parseRange(SyntaxDocument doc, int beginParse, int endParse) {
        if (Debug.DEBUG) {
            Debug.trace(this,
                        Debug.DP6,
                        "in parseRange from {0} to {1}",
                        new Object[]{new Integer(beginParse), new Integer(endParse)});
            // commented out to prevent a dependency which would break the makefile.  for testing only
// 	    if (Debug.isDebug(this, Debug.DP8))
// 	    {
// 		//this highlights the region being reparsed.  For debugging only!
// 		Object o=doc.getProperty("editor");
// 		if (o!=null)
// 		{
// 		    ((org.skunk.dav.client.gui.editor.SimpleTextEditor)o).select(beginParse, endParse);
// 		}
// 	    }
        }
        try {
            doc.getText(beginParse, endParse - beginParse, segment);
        } catch (BadLocationException bellyache) {
            if (Debug.DEBUG) Debug.trace(this, Debug.DP2, bellyache);
            return true;
        }
        if (Debug.DEBUG) Debug.trace(this, Debug.DP8, segment);
        FlexScanner scanner = getScanner(doc.getFileMode(),
                                         new CharArrayReader(segment.array,
                                                             segment.offset,
                                                             segment.count));

        GappedIntArray styleBuffer = doc.getStyleBuffer();
        if (scanner == null) {
            if (Debug.DEBUG) Debug.trace(this, Debug.DP2, "scanner is null");
            return true;
        }

        scanner.setStyleBuffer(styleBuffer);
        scanner.setOffset(beginParse);
        int state = StyleBufferUtilities.getState(styleBuffer.get(beginParse));
        //int state=scanner.getStateCorrespondingToStyle(styleBuffer.get(beginParse));
        if (Debug.DEBUG) Debug.trace(this, Debug.DP6, "state at beginParse is " + state);
        scanner.yybegin(state);

        try {
            if (Debug.DEBUG) {
                Debug.trace(this, Debug.DP8, "BEFORE: {0}", styleBuffer);
                Debug.trace(this, Debug.DP8, "gapOffset: " + styleBuffer.getGapOffset());
                Debug.trace(this, Debug.DP8, "currentGapSize: " + styleBuffer.getCurrentGapSize());
            }
            scanner.scan();
            if (Debug.DEBUG) {
                Debug.trace(this, Debug.DP8, "AFTER: {0}", styleBuffer);
                Debug.trace(this, Debug.DP8, "gapOffset: " + styleBuffer.getGapOffset());
                Debug.trace(this, Debug.DP8, "currentGapSize: " + styleBuffer.getCurrentGapSize());
            }

        } catch (IOException oyVeh) {
            if (Debug.DEBUG) Debug.trace(this, Debug.DP2, oyVeh);
            return true;
        }

        if (scanner.yystate() > 0)
            return false;
        else
            return true;
    }

    public static class Test extends JPanel {
        private SyntaxDocument doc;
        private TextEditorPane texter;

        public Test(String s) {
            initComponents();

            doc.setFileMode(FileMode.getModeForFilename(s));
            doc.setTokenizing(false);
            texter.setText(slurp(s));
            doc.setTokenizing(true);
            doc.retokenizeAll();
        }

        private void initComponents() {
            texter = new TextEditorPane();
            texter.setEditorKit(new SyntaxEditorKit());
            doc = (SyntaxDocument) texter.getDocument();
            texter.setWordWrap(false);
            texter.setMargin(new Insets(15, 15, 15, 30));
            texter.setBackground(new Color(0, 51, 51));
            texter.setForeground(new Color(255, 255, 255));
            texter.setFont(new Font("Monospaced", Font.PLAIN, 14));
            texter.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_X) {
                        System.out.println(doc.getStyleBuffer());
                    }
                }
            });

            setLayout(new BorderLayout());
            JScrollPane pane = new JScrollPane(texter);
            add(pane, BorderLayout.CENTER);
            setPreferredSize(new Dimension(500, 500));
        }

        public static void main(String[] args) {
            String filename = args[0];
            //slurp file

            Test t = new Test(filename);
            JFrame frame = new JFrame();
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent woo) {
                    System.exit(0);
                }
            });
            frame.setContentPane(t);
            frame.pack();
            frame.setBounds(20, 20, 850, 675);
            frame.setVisible(true);
        }

        private static String slurp(String filename) {
            try {
                Reader rdr = new BufferedReader(new FileReader(filename));
                char[] buffer = new char[1 << 12];
                StringBuffer sb = new StringBuffer();
                int read;
                int offset = 0;
                while (-1 != (read = rdr.read(buffer, 0, buffer.length))) {
                    if (read == buffer.length)
                        sb.append(buffer);
                    else
                        for (int i = 0; i < read; i++)
                            sb.append(buffer[i]);

                }
                System.out.println("TOTAL LENGTH OF FILE: " + sb.length());
                return sb.toString();
            } catch (IOException oyVeh) {
                oyVeh.printStackTrace();
                return "";
            }
        }
    }
}