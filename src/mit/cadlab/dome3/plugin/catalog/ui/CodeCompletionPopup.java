package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Iterator;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 11.
 */
public class CodeCompletionPopup extends JScrollPane {

    protected JList candidateList;
    private JTextPane scriptEditorPane;
    private int viewPortTopIndex = 0;
    protected int selectedIndex = -1;
    private java.util.List candidates;

    public Font POPUP_FONT;
    public int HEIGHT_PER_ITEM = -1;
    public int POPUP_WIDTH;
    public int POPUP_MAX_ITEM_DISP;
    public static final String CANDIDATE_NM_FG = "#000000";
    public static final String CANDIDATE_HL_FG = "#FFFFFF";
    public static final String CROPPING_NM_FG = "#C2279F";
    public static final String CROPPING_HL_FG = "#F3C7EA";

    public static final Border POPUP_BORDER = new LineBorder(Color.LIGHT_GRAY, 1, false);
    public static final Color POPUP_NM_BG = new Color(0xEC, 0xF5, 0xFF);
    public static final Color POPUP_HL_BG = new Color(0x00, 0x40, 0x80);
    public static final Color POPUP_ER_BG = new Color(0xFF, 0xCA, 0xCA);



    protected boolean hasSuggestion = false;
    private String NO_SUGGETION = "no suggestion";
    private java.util.List NO_SUGGETION_CANDIDATES = Arrays.asList(new CodeCompletionCandidate[] { new CodeCompletionCandidate(CodeCompletionCandidate.NO_SUGGESTION_TYPE, NO_SUGGETION, 0) } );

    /** (ex) new CodeCompletionPopup(new Font("Courier", Font.PLAIN, 9), 10) */
    public CodeCompletionPopup(Font popupFont, int popupWidth, int popupMaxItemDisp) {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        POPUP_FONT = popupFont;
        POPUP_WIDTH = popupWidth;
        POPUP_MAX_ITEM_DISP = popupMaxItemDisp;
        initComponents();
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && HEIGHT_PER_ITEM == -1) {
            HEIGHT_PER_ITEM = ModelEditorKit.compRef.getGraphics().getFontMetrics(POPUP_FONT).getHeight();
        }
    }

    private void initComponents() {

        candidateList = new JList(new DefaultListModel());
        candidateList.setFocusable(false);
        candidateList.setCellRenderer(new CodeCompletionRenderer());
        this.setBorder(POPUP_BORDER);

        this.getViewport().setView(candidateList);

        candidateList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                selectedIndex = candidateList.getSelectedIndex();
                if (event.getClickCount() >= 2) {
                    acceptSelection();
                    setVisible(false);
                }
            }
        });
    }

    public void setScriptEditorPane(JTextPane scriptEditorPane) {
        this.scriptEditorPane = scriptEditorPane;
    }

    public int getCandidateCount() {
        if (candidates == null) return 0;
        return candidates.size();
    }

    public java.util.List getCandidates() {
        return candidates;
    }

    /** called each time a code completion panel is prepared to be shown */
    public void setCandidates(java.util.List candidates, boolean isNarrowing) {
        Rectangle contentRect = new Rectangle(0, 0, POPUP_WIDTH, HEIGHT_PER_ITEM * Math.min(POPUP_MAX_ITEM_DISP, candidates.size()));
        candidateList.scrollRectToVisible(contentRect);

        if (candidates.size() == 0) {
            hasSuggestion = false;
            candidates = NO_SUGGETION_CANDIDATES;
        } else {
            hasSuggestion = true;
        }

        this.candidates = candidates;

        viewPortTopIndex = 0;
        candidateList.clearSelection();

        ((DefaultListModel) candidateList.getModel()).removeAllElements();

        for (Iterator i = candidates.iterator(); i.hasNext(); ) {
            CodeCompletionCandidate cand = (CodeCompletionCandidate) i.next();
            ((DefaultListModel) candidateList.getModel()).addElement(cand);
        }

        if (! isNarrowing || selectedIndex == -1) {
            selectedIndex = -1;
        } else {
            /* when narrowing, keep selection if it has enough candiates, or select top one */
            int elementSize = candidateList.getModel().getSize();

            if (hasSuggestion) {
                if (selectedIndex < elementSize) {
                    candidateList.setSelectedIndex(selectedIndex);
//                    System.out.println("selected : " + selectedIndex);
                } else {
                    selectedIndex = 0;
                    candidateList.setSelectedIndex(selectedIndex);
//                    System.out.println("selected top : ");
                }
            } else {
                selectedIndex = -1;
//                System.out.println("no candidate");
            }
        }

    }

    private int getViewPortTopIndex(Rectangle visibleRect) {
        return (int) visibleRect.getY() / HEIGHT_PER_ITEM;
    }

    /**
     *  since VARNAME_CC_TYPE and DIMENSION_CC_TYPE have the same crop position, to get cropping we just query the first occurence of those types
     *
     * get how many letters should be cropped at the beginning of inserted auto completion string
     * (ex) when we have ab| where a caret is at '|' position, if code completion of 'abcde' is found, first two letters should be cropped at insertion
     *      the amount of croping is equal to the length of query used in search.
     */
    public String getCropping() {
        String cropping = "";
        for (int i = 0; i < candidates.size(); i++) {
            CodeCompletionCandidate candidate = (CodeCompletionCandidate) candidates.get(i);
            if (candidate.getType() == CodeCompletionCandidate.VARNAME_CC_TYPE || candidate.getType() == CodeCompletionCandidate.DIMENSION_CC_TYPE) {
                cropping = candidate.getCroppedText();
                break;
            }
        }

        return cropping;
    }

    public boolean hasSuggestion() {
        return hasSuggestion;
    }

    public void moveSelection(boolean isUpward) {
        /* update selected index and view port top index from current state of JList */
        selectedIndex = candidateList.getSelectedIndex();
        viewPortTopIndex = this.getViewPortTopIndex(candidateList.getVisibleRect());

        if (isUpward) {
            selectedIndex--;
        } else {
            selectedIndex++;
        }

        if (selectedIndex < 0) {
            selectedIndex = candidates.size() - 1;
        } else if (selectedIndex >= candidates.size()) {
            selectedIndex = 0;
        }

        /* when candidate list should be scrolled */
        if (candidates.size() > POPUP_MAX_ITEM_DISP) {
            if (selectedIndex == candidates.size() - 1) {
                viewPortTopIndex = candidates.size() - POPUP_MAX_ITEM_DISP;
            } else if (selectedIndex == 0) {
                viewPortTopIndex = 0;
            } else if (! isUpward && selectedIndex >= viewPortTopIndex + POPUP_MAX_ITEM_DISP) {
                viewPortTopIndex++;
            } else if (isUpward && selectedIndex < viewPortTopIndex) {
                viewPortTopIndex--;
            }
        }

        candidateList.setSelectedIndex(selectedIndex);
        Rectangle contentRect = new Rectangle(0, viewPortTopIndex * HEIGHT_PER_ITEM,  POPUP_WIDTH, HEIGHT_PER_ITEM * Math.min(POPUP_MAX_ITEM_DISP, candidates.size()));
        candidateList.scrollRectToVisible(contentRect);
    }

    public void acceptSelection() {
        if (selectedIndex == -1 || ! hasSuggestion) {
            return;
        }

        int insertIndex = Math.max(scriptEditorPane.getCaret().getMark(), scriptEditorPane.getCaret().getDot());

        StyledDocument doc = (StyledDocument) scriptEditorPane.getDocument();

        //scriptEditorPane.setText((String) candidates.get(selectedIndex));
        //scriptEditorPane.setText((String) candidates.get(selectedIndex));

        CodeCompletionCandidate candidate = (CodeCompletionCandidate) candidates.get(selectedIndex);

        if (candidate.getType() == CodeCompletionCandidate.VARNAME_CC_TYPE || candidate.getType() == CodeCompletionCandidate.DIMENSION_CC_TYPE) {
            try {
                String insertStr = candidate.getInsertedText();
                doc.insertString(insertIndex, insertStr, SimpleAttributeSet.EMPTY);
                scriptEditorPane.getCaret().setDot(insertIndex + insertStr.length());
            } catch (BadLocationException e) { System.err.println("exception occured at acceptSelection() : " + e); }
        } else if (candidate.getType() == CodeCompletionCandidate.PATTERN_CC_TYPE) {
            try {
                doc.remove(0, doc.getLength());
                String insertStr = candidate.getInsertedText();
                doc.insertString(0, insertStr, SimpleAttributeSet.EMPTY);
                scriptEditorPane.getCaret().setDot(insertIndex + insertStr.length());
            } catch (BadLocationException e) { System.err.println("exception occured at acceptSelection() : " + e); }
        }
    }

    private int getDisplayedItemSize() {
        int numberOfItems = (this.getCandidateCount() > POPUP_MAX_ITEM_DISP) ? POPUP_MAX_ITEM_DISP : this.getCandidateCount();
        return numberOfItems;
    }

    public int getPopupHeight() {
        return getDisplayedItemSize() * HEIGHT_PER_ITEM + 1 * 2;
    }

    class CodeCompletionRenderer extends JLabel implements ListCellRenderer {
        public CodeCompletionRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        }
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setFont(POPUP_FONT);
            CodeCompletionCandidate candidate = (CodeCompletionCandidate) value;
            setText(candidate.getHtmlText(isSelected));
            setBackground(candidate.getBackground(isSelected));
            return this;
        }
    }

}

