package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 4.
 */
public class JTextPaneWithCodeCompletionPopup extends JLayeredPane {
    private JTextPane txtPane;
    private CodeCompletionPopup ccPopup;
    private boolean isNarrowingCodeCompletion = false;
    // private Graphics graphics;
    private static String TAB = "    ";

    /** delimList is used for syntax highlighting. To turn syntax highlighting off, set delimList as null */
    public JTextPaneWithCodeCompletionPopup(Font mainFont, Font popupFont, int popupWidth, int popupMaxItemDisp, DelimiterList delimList) {
        super();

        this.setBorder(null);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                txtPane.requestFocusInWindow();
            }
        });

        if (delimList != null) {
            //txtPane = new JTextPane(new ColorizedDocument(delimList, mainFont.getFontName(), mainFont.getSize()));
            txtPane = new JTextPane(new ColorizedDocument(delimList));
        } else {
            txtPane = new JTextPane();
        }
        txtPane.setFont(mainFont);
        txtPane.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        ccPopup = new CodeCompletionPopup(popupFont, popupWidth, popupMaxItemDisp);
        add(txtPane, new Integer(1));
        add(ccPopup, new Integer(2));
        ccPopup.setVisible(false);
        ccPopup.setScriptEditorPane(txtPane);
        addBindings();
    }

    public void setLayerSize(int width, int height) {
        txtPane.setBounds(0, 0, width, height);
        this.setPreferredSize(new Dimension(width, height));
    }

    public CodeCompletionPopup getCodeCompletionPopup() {
        return ccPopup;
    }

    /** should be overrided to customize the list of candidates */
    public java.util.List populateCandidates() {
        //return Arrays.asList(new String[] { "to change this candidate list you can overide setCandidates()" });
        return Arrays.asList(new CodeCompletionCandidate[] { new CodeCompletionCandidate(CodeCompletionCandidate.VARNAME_CC_TYPE, "to change this candidate list you can overide setCandidates()", 0) });
    }

    public void showCodeCompletion() {
        ccPopup.setCandidates(populateCandidates(), isNarrowingCodeCompletion);
        ccPopup.setVisible(true);

        Point scriptEditorPos = new Point(-1, ccPopup.HEIGHT_PER_ITEM + 2);

        /* pick right edige of the caret selection */
        Point caretPos = UIUtil.getCaretPosition("right", txtPane, ComponentReference.getGraphics());

//        /* if txtPane's width is not wide enough to display cc popup, shift cc popup to left */
        int ccPopupX = scriptEditorPos.x + caretPos.x;
//        if (ccPopupX + ccPopup.POPUP_WIDTH > scrollPaneWidth && (scrollPaneWidth - ccPopup.POPUP_WIDTH) >= 0) {
//            ccPopupX = scrollPaneWidth - ccPopup.POPUP_WIDTH;
//        }

        if (ccPopup.getCropping() != null && ccPopup.hasSuggestion()) {
            FontMetrics fontMetrics = ComponentReference.getGraphics().getFontMetrics(ccPopup.POPUP_FONT);
            Rectangle2D rec = fontMetrics.getStringBounds(ccPopup.getCropping(), ComponentReference.getGraphics());
            ccPopupX -= (int) rec.getWidth();
        }

        if (isNarrowingCodeCompletion) {
            /* when narrowing keep x position. it is becuase getMagicCaretPosition() does not return up-to-date value for current caret position */
            ccPopup.setBounds(ccPopup.getX(), scriptEditorPos.y + caretPos.y, ccPopup.POPUP_WIDTH, ccPopup.getPopupHeight());
        } else {
            ccPopup.setBounds(ccPopupX, scriptEditorPos.y + caretPos.y, ccPopup.POPUP_WIDTH, ccPopup.getPopupHeight());
        }

//        lastPopupHeight = ccPopup.getPopupHeight() + 5;
//        int maxHeight = Math.max(txtPane.getPreferredSize().height, scriptEditorPos.y + caretPos.y + lastPopupHeight);
//
//        //setLayerSize(txtPane.getWidth(), maxHeight);
//        //System.out.println("text pane pref" + getTextPaneSize());
//        //setLayerSize(txtPane.getWidth(), maxHeight);
//        setLayerSize(txtPane.getWidth(), maxHeight);

        //ccPopup.scrollRectToVisible(ccPopup.getBounds());
        revalidate();


//        if (this.getParent() != null && this.getParent() instanceof JViewport) {
//            JViewport parentViewport= (JViewport) this.getParent();
//            Rectangle viewRect = parentViewport.getViewRect();
//            if (viewRect.getY() <= ccPopup.getY() && viewRect.getHeight() >= ccPopup.getHeight()) {
//                /* already showing bounds of ccPopup. we don't have to scroll to show ccPopup */
//                System.out.println("showing");
//            } else {
//                System.out.println("not showing - do scroll");
//                ccPopup.scrollRectToVisible(ccPopup.getBounds());
//            }
//        }
    }

    public void closeCodeCompletion() {
        isNarrowingCodeCompletion = false;
        ccPopup.setVisible(false);
    }

    public boolean isCodeCompletionShown() {
        return ccPopup.isVisible();
    }

    public JTextPane getTextPane() {
        return txtPane;
    }

//    public void paint(Graphics g) {
//        super.paint(g);
//        if (this.graphics == null) {
//            this.graphics = g;
//        }
//        if (ccPopup.HEIGHT_PER_ITEM == -1) {
//            ccPopup.HEIGHT_PER_ITEM = g.getFontMetrics(ccPopup.POPUP_FONT).getHeight();
//            // System.out.println("height per item set as : " + ccPopup.HEIGHT_PER_ITEM);
//        }
//
//    }

    protected void addBindings() {

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "caretDown");
        txtPane.getActionMap().put("caretDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isCodeCompletionShown()) { ccPopup.moveSelection(false); }
                else { txtPane.getActionMap().get(DefaultEditorKit.downAction).actionPerformed(e);                }
            }
        });

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "caretUp");
        txtPane.getActionMap().put("caretUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isCodeCompletionShown()) { ccPopup.moveSelection(true); }
                else { txtPane.getActionMap().get(DefaultEditorKit.upAction).actionPerformed(e); }
            }
        });

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tabInOrAcceptCC");
        txtPane.getActionMap().put("tabInOrAcceptCC", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isCodeCompletionShown()) { ccPopup.acceptSelection(); closeCodeCompletion(); }
                else {
                    try {
                        StyledDocument doc = (StyledDocument) txtPane.getDocument();
                        int markRowIdx = ((Integer) UIUtil.getCaret(txtPane.getCaret().getMark(), doc, ComponentReference.getGraphics()) [0]).intValue();
                        int dotRowIdx = ((Integer) UIUtil.getCaret(txtPane.getCaret().getDot(), doc, ComponentReference.getGraphics()) [0]).intValue();
                        if (markRowIdx == dotRowIdx) {
                            //txtPane.getActionMap().get(DefaultEditorKit.insertTabAction).actionPerformed(e);
                            txtPane.getDocument().insertString(txtPane.getCaret().getMark(), TAB, SimpleAttributeSet.EMPTY);
                        } else {
                            int fromRow = 0; int toRow = 0;
                            fromRow = Math.min(dotRowIdx, markRowIdx);
                            toRow = Math.max(dotRowIdx, markRowIdx);

                            System.out.println(fromRow + " , " + toRow);

                            int[][] rowStartEndPos = UIUtil.getRowStartEndPos(txtPane.getText(), fromRow, toRow);

                            for (int i = 0; i < rowStartEndPos.length; i++) {
                                int pos = rowStartEndPos[i][0] + i * TAB.length();
                                txtPane.getDocument().insertString(pos, TAB, SimpleAttributeSet.EMPTY);
                            }
                        }
                    } catch (BadLocationException ble) { ble.printStackTrace(); }

                }
            }
        });

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_MASK ), "tabOut");
        txtPane.getActionMap().put("tabOut", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    StyledDocument doc = (StyledDocument) txtPane.getDocument();
                    int markRowIdx = ((Integer) UIUtil.getCaret(txtPane.getCaret().getMark(), doc, ComponentReference.getGraphics()) [0]).intValue();
                    int dotRowIdx = ((Integer) UIUtil.getCaret(txtPane.getCaret().getDot(), doc, ComponentReference.getGraphics()) [0]).intValue();
                    if (markRowIdx == dotRowIdx) {
                        return;
                    } else {
                        int fromRow = 0; int toRow = 0;
                        fromRow = Math.min(dotRowIdx, markRowIdx);
                        toRow = Math.max(dotRowIdx, markRowIdx);

                        int[][] rowStartEndPos = UIUtil.getRowStartEndPos(txtPane.getText(), fromRow, toRow);

                        int removed = 0;
                        for (int i = 0; i < rowStartEndPos.length; i++) {
                            int startPos = rowStartEndPos[i][0];
                            int endPos = rowStartEndPos[i][1];
                            String rowStr = doc.getText(startPos - removed, endPos - startPos);
                            if (rowStr.startsWith(TAB)) {
                                doc.remove(startPos - removed, TAB.length());
                                removed += TAB.length();
                            } else {
                                int lastSpaceIndex = 0;
                                for (lastSpaceIndex = 0; lastSpaceIndex < rowStr.length(); lastSpaceIndex++) {
                                    if (rowStr.charAt(lastSpaceIndex) != ' ') {
                                        doc.remove(startPos - removed, lastSpaceIndex);
                                        removed += lastSpaceIndex;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (BadLocationException ble) { ble.printStackTrace(); }
            }
        });

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterOrAcceptCC");
        txtPane.getActionMap().put("enterOrAcceptCC", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isCodeCompletionShown()) { ccPopup.acceptSelection(); closeCodeCompletion(); }
                else { txtPane.getActionMap().get(DefaultEditorKit.insertBreakAction).actionPerformed(e);
                    txtPane.getSize();
                 }
            }
        });

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Event.CTRL_MASK), "showCodeCompletion");
        txtPane.getActionMap().put("showCodeCompletion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { showCodeCompletion(); }
        });

        txtPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeCodeCompletion");
        txtPane.getActionMap().put("closeCodeCompletion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isCodeCompletionShown()) { closeCodeCompletion(); }
            }
        });

        txtPane.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent event) {
                /* during code completion mode */
                if (isCodeCompletionShown()) {
                    if (Character.isLetterOrDigit(event.getKeyChar()) || event.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                        isNarrowingCodeCompletion = true;
                    } else {
                        isNarrowingCodeCompletion = false;
                    }
                }
            }
        });

        txtPane.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent event) {
                if (isCodeCompletionShown()) {
                    if (isNarrowingCodeCompletion) {
                        showCodeCompletion();
                    } else {
                        closeCodeCompletion();
                    }
                }
            }
        });

        txtPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                isNarrowingCodeCompletion = false;
            }
        });

        /* document listener is used to update the size of JTextPane */
        txtPane.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent event) { }

            public void insertUpdate(DocumentEvent event) {
                forceLayerResizing();
//                String text = null;
//                boolean isChanged = false;
//                try {
//                    String preceedingText = event.getDocument().getText(0, event.getOffset());
//                    String addedText = event.getDocument().getText(event.getOffset(), event.getLength());
//
//                    if (addedText.indexOf("\n") != -1) {
//                        isChanged = true;
//                    }
//
//                    text = event.getDocument().getText(0, event.getDocument().getLength());
//
//                    int affectedRowStart = UIUtil.countLineBreak(preceedingText);
//                    int affectedRowEnd = affectedRowStart + UIUtil.countLineBreak(addedText);
//
//                    System.out.println("insert affectedRow [Start, End] = [" + affectedRowStart + ", " + affectedRowEnd + "]");
//                    StyledDocument doc = (StyledDocument) event.getDocument();
//
//                    for (int rowIdx = affectedRowStart; rowIdx <= affectedRowEnd; rowIdx++) {
//                        int rowWidth = UIUtil.getRowWidth(rowIdx, preceedingText + addedText, doc, graphics);
//                        //System.out.println("rowWidth: " + rowWidth);
//                        if (maxWidth < rowWidth) {
//                            maxWidthRowIdx = rowIdx;
//                            maxWidth = rowWidth;
//                            isChanged = true;
//                        }
//                    }
//                } catch (BadLocationException e) { e.printStackTrace(); }
//
//                int lineBreakCount = UIUtil.countLineBreak(text) + 1;
//
//                if (graphics == null) {
//                    return; // skip resize when not visible
//                }
//
//                FontMetrics fontMetrics = graphics.getFontMetrics(txtPane.getFont()); // height comes from default txt pane font. assuming other fonts have the same height
//                int curHeight = lineBreakCount * fontMetrics.getHeight() ;
//                curHeight += (ccPopup.HEIGHT_PER_ITEM >= 0 ? ccPopup.HEIGHT_PER_ITEM : 0) * ccPopup.POPUP_MAX_ITEM_DISP + 5;
//
//                if (isChanged) {
//                    setLayerSize(maxWidth + 20, curHeight);
//                }
//                //System.out.println("maxRowWidth, curHeight: " + maxWidth + ", " + curHeight);
            }

            public void removeUpdate(DocumentEvent event) {
//                try {
//                    String preceedingText = event.getDocument().getText(0, event.getOffset());
//                    String addedText = event.getDocument().getText(event.getOffset(), event.getLength());
//
//                    int affectedRowStart = UIUtil.countQuery(preceedingText, "\n");
//                    int affectedRowEnd = affectedRowStart + UIUtil.countQuery(addedText, "\n");
//
//                    System.out.println("remove affectedRow [Start, End] = [" + affectedRowStart + ", " + affectedRowEnd + "]");
//                } catch (BadLocationException e) { e.printStackTrace(); }
            }
        });
    }

    /** used to force layer resizing */
    public void forceLayerResizing() {
        Graphics graphics = ModelEditorKit.compRef.getGraphics();
        int maxWidth = 450;
        try {
            Document document = getTextPane().getDocument();
            String fullText = document.getText(0, document.getLength());
            int lineBreakCount = UIUtil.countLineBreak(fullText) + 1;
            for (int rowIdx = 0; rowIdx < lineBreakCount; rowIdx++) {
                int rowWidth = UIUtil.getRowWidth(rowIdx, fullText, (StyledDocument) document, graphics);
                if (maxWidth < rowWidth) {
                    maxWidth = rowWidth;
                }
            }
            FontMetrics fontMetrics = graphics.getFontMetrics(txtPane.getFont()); // height comes from default txt pane font. assuming other fonts have the same height
            int curHeight = lineBreakCount * fontMetrics.getHeight() ;
            curHeight += (ccPopup.HEIGHT_PER_ITEM >= 0 ? ccPopup.HEIGHT_PER_ITEM : 0) * ccPopup.POPUP_MAX_ITEM_DISP + 5;
            setLayerSize(maxWidth + 20, curHeight);
        } catch (BadLocationException e) { e.printStackTrace(); }
    }
}