package mit.cadlab.dome3.plugin.catalog.ui;

import edu.iupui.rg.ucum.CommonUnit;
import edu.iupui.rg.ucum.units.UnitTab;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public class UIUtil {


    //public static final int CELL_WIDTH = 110;
    //public static final int CELL_HEIGHT = 94; //86
    public static final int CELL_NAME_LABEL_HEIGHT = 18;
    public static final int CELL_TYPE_AND_UNIT_LABEL_HEIGHT = 15;
    public static final int CELL_BORDER_THICKNESS = 2;
    public static final int CENTER_PANEL_WIDTH = 110;
    public static final int CENTER_BT_WIDTH = 39;
    public static final int CENTER_BT_HEIGHT = 13;
    public static final int GAP_BETWEEN_BARS = 2;
    public static final int BAR_TOP_MARGIN = 3;
    public static final int BAR_LEFT_MARGIN = 3;
    public static final int BAR_BORDER_THICKNESS = 3;
    public static final int PAD_BETWEEN_CELLS = 1;
    public static final int BAR_BORDER_NM_THICKNESS = 1;
    public static final int BAR_BORDER_DR_THICKNESS = 3;
    public static final int BAR_BORDER_HL_THICKNESS = 3;
    public static final int IMPL_EDITOR_BORDER_NM_THICKNESS = 1;
    public static final int IMPL_EDITOR_BORDER_HL_THICKNESS = 1;
    public static final int REL_X_DIST_SCRIPT_EDIT = 7;
    public static final int REL_Y_DIST_SCRIPT_EDIT = 55;
    public static final int ITF_X_DIST_SCRIPT_EDIT = 7;
    public static final int ITF_Y_DIST_SCRIPT_EDIT = 55;
    public static final int CC_X_DIST = 1;
    public static final int CC_Y_DIST = 14;
    public static final int SCRIPT_DISP_HEIGHT = 37; // 29
//    public static final int SCRIPT_EDITOR_WIDTH = 98;
//    public static final int SCRIPT_EDITOR_HEIGHT = 36; // 28
    public static final int SCRIPT_EDITOR_BT_HEIGHT = 14;
//    public static final int SCRIPT_EDITOR_BORDER = 1;
    public static final int TOOL_PANEL_WIDTH = 4000;
    public static final int TOOL_PANEL_HEIGHT = 6;
    public static final int MIN_BAR_HEIGHT = 2 * UIUtil.PAD_BETWEEN_CELLS + UIUtil.CG_CELL_HEIGHT + 2 * UIUtil.BAR_BORDER_THICKNESS;
    public static final int MIN_EDITOR_HEIGHT = UIUtil.MIN_BAR_HEIGHT + 2 * UIUtil.BAR_TOP_MARGIN;
    public static final int CC_WIDTH = 200;
    public static final int CC_HEIGHT_PER_ITEM = 13;
    public static final int CC_BORDER_THICKNESS = 1;
    public static final int CC_MAX_ITEM_DISP = 10;
    public static final int NAV_BT_PANEL_HEIGHT = 40;
    public static final int NAV_BT_PANEL_WIDTH = 200;
    public static final int STATUS_BAR_HEIGHT = 22;
    public static final int LOCATION_LABEL_WIDTH = 100;
    public static final int MEMORY_LABEL_WIDTH = 70;
    public static final int GAP_BETWEEN_REL_NAME_AND_REL_ALIAS = 3;
    public static final int CODE_COMPLETION_POPUP_WIDTH = 150;

    public static final Color EVAL_DETAIL_BORDER_COLOR = Color.DARK_GRAY;

    public static final Color INSERTION_MARKER_COLOR = Color.DARK_GRAY;
    public static final Color BAR_BORDER_NM_COLOR = Color.GRAY;
    public static final Color BAR_BORDER_HL_COLOR = Color.BLUE;
    public static final Color BAR_BORDER_DR_COLOR = Color.BLUE.darker();
    public static final Color IMPL_EDITOR_BORDER_NM_COLOR = Color.LIGHT_GRAY;
    public static final Color IMPL_EDITOR_BORDER_HL_COLOR = Color.GRAY;
    public static final Color SIDE_PANEL_BG_COLOR = Color.LIGHT_GRAY;
    public static final Color REL_TOOLBAR_BG_COLOR = Color.GRAY;
    public static final Color REL_EDITOR_BG_COLOR = Color.WHITE;
    //public static final Color CELL_BG = new Color(255, 255, 204); // light yellow
    public static final Color CELL_BG = new Color(248, 248, 248); // light gray
    public static final Color NAV_PANEL_BG = Color.WHITE; // new Color(248, 248, 248); // light gray
//    public static final Color CC_NM_BG = new Color(0xEC, 0xF5, 0xFF);
//    public static final Color CC_HL_BG = new Color(0x00, 0x40, 0x80);
//    public static final Color CC_NM_FG = Color.BLACK;
//    public static final Color CC_HL_FG = Color.WHITE;
//    public static final Color CC_ER_BG = new Color(0xFF, 0xCA, 0xCA);
    public static final Color VALUE_EDITOR_BG = new Color(0xD7, 0xEF, 0xFD); // light gray

    //public static final Color[] REL_BG_COLORS = { new Color(0xCD, 0xE6, 0xF3), new Color(0xF9, 0xBE, 0x5B), new Color(0x73, 0xB0, 0xFB), new Color(0xA6, 0xEB, 0x7C), new Color(0xEF, 0x94, 0x9F), new Color(0xFF, 0xFF, 0x88), new Color(0xD3, 0xA3, 0xF8), new Color(0x78, 0xED, 0xD2), new Color(0xF3, 0xDD, 0x72), new Color(0xFF, 0x95, 0xCA), new Color(0x9F, 0xF7, 0x6A), new Color(0x86, 0xF7, 0x9F), new Color(0xA0, 0x9E, 0xF5), new Color(0xFF, 0xC2, 0x86), new Color(0x93, 0xC6, 0xEC), new Color(0xD9, 0x91, 0xF4) }; // first element is ITF_BG_COLOR, others are randomly chosen for relation bg color
    /* light blue 0xBF, 0xDF, 0xFF */
    public static final Color[] REL_BG_COLORS = { new Color(0x61, 0xCA, 0xDC), new Color(0xFF, 0x55, 0x2B), new Color(0xFF, 0xFF, 0x55), new Color(0xFF, 0xAA, 0xD5), new Color(0xFF, 0x7E, 0x40), new Color(0x55, 0xFF, 0x55), new Color(0x55, 0x55, 0xFF), new Color(0xFF, 0xCC, 0x00), new Color(0xBB, 0x55, 0xFF) };
    public static final Color REL_GREY_BG_COLOR = new Color(0xE2, 0xE2, 0xE2);
    public static final Color REL_ACTIVE_BG_COLOR = new Color(0xA4, 0xC4, 0xFF);
    public static final Color ITF_ERROR_BG_COLOR = new Color(0xFF, 0xBB, 0xBB);

    public static final Color RED_STATUS_BG = new Color(0xFF, 0xBF, 0xBF);
    public static final Color GREEN_STATUS_BG = new Color(0xB1, 0xF8, 0xA3);
    public static final Color YELLOW_STATUS_BG = new Color(0xFF, 0xFF, 0x80);
    public static final Color WHITE_STATUS_BG = new Color(0xFF, 0xFF, 0xFF);

    public static final Color DRAG_INFO_LABEL_BG_COLOR = new Color(0xFF, 0xFF, 0x80);

    public static final Font DIALOG_FONT = new Font("Dialog", Font.PLAIN, 11);
    public static final Font DRAG_INFO_LABEL_FONT = new Font("Dialog", Font.PLAIN, 11);

    public static final Font DIALOG_ITALIC_FONT = new Font("Dialog", Font.ITALIC, 11);
    public static final Font DIALOG_SMALL_FONT = new Font("Dialog", Font.PLAIN, 11); // 9
    public static final Font PARAM_NAME_FONT = new Font("Dialog", Font.BOLD, 11); // 10 Courier
    //public static final Font PARAM_NAME_FONT = new Font("Dialog", Font.PLAIN, 11); // 10 Courier
    public static final Font PARAM_UNIT_FONT = new Font("Dialog", Font.PLAIN, 10); // Courier
    public static final Font PARAM_EDITOR_FONT = new Font("Dialog", Font.PLAIN, 11); // 9 Courier
    public static final Font GROOVY_EDITOR_FONT = new Font("Dialog", Font.PLAIN, 11); // Courier
    //public static final Font GROOVY_POPUP_FONT = new Font("Dialog", Font.PLAIN, 11); // Courier
    public static final Font MENU_BAR_FONT = new Font("Dialog", Font.PLAIN, 11);
    public static final Font BAR_CENTER_FONT = new Font("Dialog", Font.PLAIN, 10); // 9 Courier
    //public static final Font REL_NAME_FONT = new Font("Dialog", Font.BOLD, 11); // 10 Courier
    public static final Font REL_NAME_FONT = new Font("Dialog", Font.PLAIN, 11); // 10 Courier
    public static final Font REL_ALIAS_FONT = new Font("Dialog", Font.PLAIN, 11); // 10 Courier
    public static final Font TOOL_BUTTON_FONT = new Font("Dialog", Font.ITALIC, 10);
    public static final Font TINY_DIALOG_FONT = new Font("Dialog", Font.PLAIN, 9);
    public static final Font STATUS_BAR_FONT = new Font("Dialog", Font.PLAIN, 11);
    public static final Font NAV_TREE_FONT = new Font("Dialog", Font.PLAIN, 11); // 10 Courier
    public static final Font SMALL_DIALOG_FONT = new Font("Dialog", Font.PLAIN, 10);

    public static final Border BAR_NM_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIUtil.REL_EDITOR_BG_COLOR, UIUtil.BAR_BORDER_HL_THICKNESS - UIUtil.BAR_BORDER_NM_THICKNESS), BorderFactory.createLineBorder(UIUtil.BAR_BORDER_NM_COLOR, UIUtil.BAR_BORDER_NM_THICKNESS));
    public static final Border BAR_DR_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIUtil.REL_EDITOR_BG_COLOR, UIUtil.BAR_BORDER_HL_THICKNESS - UIUtil.BAR_BORDER_DR_THICKNESS), BorderFactory.createLineBorder(UIUtil.BAR_BORDER_DR_COLOR, UIUtil.BAR_BORDER_DR_THICKNESS));
    public static final Border BAR_HL_BORDER = BorderFactory.createLineBorder(UIUtil.BAR_BORDER_HL_COLOR, UIUtil.BAR_BORDER_HL_THICKNESS);
    public static final Border CELL_NM_BORDER = BorderFactory.createCompoundBorder(new LineBorder(UIUtil.SIDE_PANEL_BG_COLOR, CELL_BORDER_THICKNESS - 1, false), new LineBorder(UIUtil.BAR_BORDER_NM_COLOR, 1, false));
    public static final Border CELL_DR_BORDER = new LineBorder(UIUtil.BAR_BORDER_DR_COLOR, CELL_BORDER_THICKNESS, false);
    public static final Border CELL_HL_BORDER = new LineBorder(UIUtil.BAR_BORDER_HL_COLOR, CELL_BORDER_THICKNESS, false);
    public static final Border SCRIPT_DISP_NM_BORDER = new LineBorder(Color.LIGHT_GRAY, 1, false);
    public static final Border SCRIPT_DISP_ER_1_BORDER = new LineBorder(Color.RED, 1, false);
    public static final Border SCRIPT_DISP_ER_2_BORDER = new LineBorder(new Color(0xFF, 0x4F, 0x4F), 1, false);
    public static final Border VALUE_EDITOR_NORMAL_BORDER = BorderFactory.createCompoundBorder(new LineBorder(Color.DARK_GRAY, 1, false), new EmptyBorder(0, 2, 0, 0));
    public static final Border VALUE_EDITOR_ERROR_BORDER = BorderFactory.createCompoundBorder(new LineBorder(Color.RED, 1, false), new EmptyBorder(0, 2, 0, 0));
    public static final Border CC_BORDER = new LineBorder(Color.LIGHT_GRAY, CC_BORDER_THICKNESS, false);
    public static final Border STATUS_LABEL_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIUtil.BAR_BORDER_NM_COLOR, 1), BorderFactory.createEmptyBorder(2, 2, 2, 2));
    public static final Border STATUS_BAR_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    public static final Border DIALOG_COMP_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(0, 5, 0, 5));
    public static final Border MODEL_NAME_LABEL_BORDER = BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1, false), new EmptyBorder(6, 8, 4, 4));
    public static final Border NAV_TREE_BORDER = BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1, false), new EmptyBorder(2, 2, 2, 2));

    public static final Border IMPL_EDITOR_NM_BORDER = BorderFactory.createLineBorder(UIUtil.IMPL_EDITOR_BORDER_NM_COLOR, UIUtil.IMPL_EDITOR_BORDER_NM_THICKNESS);
    public static final Border IMPL_EDITOR_HL_BORDER = BorderFactory.createLineBorder(UIUtil.IMPL_EDITOR_BORDER_HL_COLOR, UIUtil.IMPL_EDITOR_BORDER_HL_THICKNESS);

    public static final Border FLOATING_EDITOR_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(1, 1, 0, 1));
//    public static final Border FLOATING_EDITOR_BORDER = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
    public static final Border FLOATING_EDITOR_BUTTON_BORDER = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);


    public static final Border DRAGGED_BAR_BORDER = BorderFactory.createLineBorder(Color.GRAY, 3);
    public static final Border DRAGGED_CELL_BORDER = BorderFactory.createLineBorder(Color.GRAY, 2);
    //public static final Border DRAG_INFO_LABEL_BORDER = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
    public static final Border DRAG_INFO_LABEL_BORDER = BorderFactory.createLineBorder(new Color(0x95, 0x95, 0x00), 1);

    public static boolean isUnitLoaded = false;

    private static Map fontCache = new  HashMap();

    static int CG_ICON_TOP_MARGIN;
    static int CG_TYPE_AND_UNIT_LEFT_MARGIN; // applies to align type 1 and 2
    static int CG_TYPE_WIDTH; // applies to align type 3
    static int CG_UNIT_WIDTH; // applies to align type 3
    final static int CG_TYPE_AND_UNIT_TOP_MARGIN = 2; // applies to align type 3
    final static int CG_ICON_WIDTH = 11;
    static int CG_NAME_LEFT_MARGIN;
    static int CG_NAME_TOP_MARGIN = 0;
    final static int CG_SCRIPT_TOP_MARGIN = 0;
    static int CG_CELL_WIDTH = 0;
    static int CG_CELL_HEIGHT = 0;
    static int CG_NAME_LABEL_WIDTH;
    static int CG_NAME_ROW_HEIGHT; // row height of name label and script disp
    static int CG_TYPE_ROW_HEIGHT; // row height of name label and script disp
    static int CG_SCRIPT_DISP_WIDTH;
    static int CG_SCRIPT_DISP_HEIGHT;
    final static int CG_SCRIPT_EDITOR_MIN_WIDTH = 130;
    final static int CG_SCRIPT_DISP_SIDE_MARGIN = 2;
    final static int CG_SCRIPT_DISP_TOP_MARGIN = 1; // applies to align type 2 and 3
    final static int CG_ICON_AND_NAME_SIDE_MARGIN = 2;
    final static int CG_ICON_LEFT_MARGIN = CG_ICON_AND_NAME_SIDE_MARGIN;

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = UIUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static ImageIcon createImageIcon(String path) {
        return createImageIcon(path, null);
    }

    /** returns index of a component in its parent container. returns -1 if not found */
    public static int indexOfComponent(Component comp) {
        Container cont = comp.getParent();
        int ret = -1;
        for (int i = 0; i < cont.getComponentCount(); i++) {
            if (comp == cont.getComponent(i)) {
                return i;
            }
        }
        return -1;
    }

    /** returns if this component is the last one in its container */
    public static boolean isLastComponent(Component comp) {
        return indexOfComponent(comp) == comp.getParent().getComponentCount();
    }

    /** returns if this component is the first one in its container */
    public static boolean isFirstComponent(Component comp) {
        return indexOfComponent(comp) == 0;
    }

    /** returns the next component of a given component in its parent container. if returnsFirstIfLastCompGiven is false and the given component is the last one, it will return null */
    public static Component getNextComponent(Component comp, boolean returnsFirstIfLastCompGiven) {
        Container cont = comp.getParent();
        int compIndex = UIUtil.indexOfComponent(comp);
        if (compIndex == (cont.getComponentCount() - 1)) {
            if (returnsFirstIfLastCompGiven) {
                /* if it is pointing the last cell, move to the first */
                compIndex = 0;
            } else {
                return null;
            }
        } else {
            compIndex++;
        }
        return cont.getComponent(compIndex);
    }

    /** returns the previous component of a given component in its parent container. if returnsLastIfFirstCompGiven is false and the given component is the first one, it will return null */
    public static Component getPreviousComponent(Component comp, boolean returnsLastIfFirstCompGiven) {
        Container cont = comp.getParent();
        int compIndex = UIUtil.indexOfComponent(comp);
        if (compIndex == 0) {
            if (returnsLastIfFirstCompGiven) {
                /* if it is pointing the last cell, move to the first */
                compIndex = cont.getComponentCount() - 1;
            } else {
                return null;
            }
        } else {
            compIndex--;
        }
        return cont.getComponent(compIndex);
    }

    /** get plain text from a text having html tags */
    public static String getPlainText(String htmlText) {
        Pattern p = Pattern.compile(">([^<>]+)<");
        Matcher m = p.matcher(htmlText);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            sb.append(m.group(1));
        }
        return sb.toString();
    }

    /** count many lines will be needed to display whole charaters in the given text
     * when the text is diplayed within the given max width using a font metrics that is also given
     * if lineCount is larger than maxLineCount, lineCount will be the same as the maxLineCount, but
     * instead the index of the last char that can be displayed within lines limited by maxLineCount is returned.
     * if lineCount is equal to or smaller than maxLineCount, lineCount will be returned as counted, and
     * the lastCharIndex will be -1.
     * to allow infinitely many lines, set maxLineCount as -1
     * returns int[] { lineCount, lastCharIndex } */
    public static int[] getLineCount(String text, FontMetrics fm, int maxWidth, int maxLineCount) {
        int lineCounter = 1;
        int charWidthTotal = 0;
        for (int i = 0; i < text.length(); i++) {
            int charWidth = fm.charWidth(text.charAt(i));
            if (charWidthTotal + charWidth > maxWidth) {
                lineCounter++;
                if (maxLineCount < lineCounter && maxLineCount != -1) {
                    return new int[] { lineCounter - 1, i - 1};
                }
                charWidthTotal = charWidth;
            } else {
                charWidthTotal += charWidth;
            }
        }
        return new int[] { lineCounter, -1 };
    }

    /** returns (x, y) where the top left corner of interface bar (considering the bar border is a part of the interface bar) in the implementation panel */
    public static Point getInterfaceBarOrigin() {
        return new Point(BAR_LEFT_MARGIN, BAR_TOP_MARGIN);
    }

    /** returns (x, y) where the top left corner of cell (considering the cell border is a part of the cell) in the implementation panel */
    public static Point getCellOrigin(int rowIndex, int colIndex) {
        return new Point(BAR_BORDER_THICKNESS + PAD_BETWEEN_CELLS + colIndex * (CG_CELL_WIDTH + PAD_BETWEEN_CELLS), BAR_BORDER_THICKNESS + PAD_BETWEEN_CELLS + rowIndex * (CG_CELL_HEIGHT + PAD_BETWEEN_CELLS));
    }

    /** draggedY is usually the center line's Y value of a moved bar.
     * use this method to find int[] { Y value, location index } of the closest insertion point */
    public static int[] findTheClosestBarInsertionPoint(int currentY, ComponentReference compRef) {
        int closestY = -1;
        int closestIdx = -1;
        int closestGap = Short.MAX_VALUE;
        for (int i = 0; i <= compRef.getRelationBarCount(); i++) {
            int insertionY = UIUtil.getRelationBarInsertionPoint(i, compRef);
            if (Math.abs(insertionY - currentY) < closestGap) {
                closestGap = Math.abs(insertionY - currentY);
                closestY = insertionY;
                closestIdx = i;
            }
        }
        return new int [] { closestY, closestIdx };
    }

    /** currentX is the center point X value of a moved cell. currentY is the center point Y value of a moved cell.
     * use this method to find int[] { X value, Y value, location index } of the closest insertion point */
    public static int[] findTheClosestCellInsertionPoint(int currentBarIdx, boolean isLeftPanel, int currentX, int currentY, ComponentReference compRef) {
        int closestX = -1;
        int closestY = -1;
        int closestInsertionIdx = -1;
        int closestGap = Short.MAX_VALUE;

        BaseBar bar = null;
        if (currentBarIdx == -1) {
            bar = compRef.getInterfaceBar();
        } else {
            bar = compRef.getRelationBar(currentBarIdx);
        }

        int columnCount = 0;
        Component[] cells = null;
        if (isLeftPanel) {
            cells = bar.getLeftPanel().getComponents();
            columnCount = bar.getLeftPanel().getColumnCount();
        } else {
            cells = bar.getRightPanel().getComponents();
            columnCount = bar.getRightPanel().getColumnCount();
        }

        if (columnCount == 0) {
            int[] insertionXAndY = UIUtil.getCellInsertionPoint(currentBarIdx, isLeftPanel, 0, 1, bar.compRef);
            System.out.println("findTheClosestCellInsertionPoint : " + insertionXAndY [0] + ", " + insertionXAndY [1]);
            return new int [] { insertionXAndY [0], insertionXAndY [1], 0 };
        }

        /* find last cell's row index and col index */
        int rowIdx = cells.length / columnCount;
        int colIdx = cells.length % columnCount;
        int candidateCount = (columnCount + 1) * rowIdx + (colIdx == 0 ? 0 : colIdx + 1);

//        System.out.println("current X = " + currentX + ", current Y = " + currentY);

        int[] candX = new int[candidateCount];
        int[] candY = new int[candidateCount];

        for (int i = 0; i < candidateCount; i++) {
            int[] insertionXAndY = UIUtil.getCellInsertionPoint(currentBarIdx, isLeftPanel, i, columnCount, bar.compRef);

            int dist = (insertionXAndY [0] - currentX) * (insertionXAndY [0] - currentX) + (insertionXAndY [1] - currentY) * (insertionXAndY [1] - currentY);

//            System.out.println("X = " + insertionXAndY [0] + ", Y = " + insertionXAndY [1] + ", dist = " + dist);

            candX [i] = insertionXAndY [0];
            candY [i] = insertionXAndY [1];

            if (dist < closestGap) {
                closestGap = dist;
                closestX = insertionXAndY [0];
                closestY = insertionXAndY [1];
                closestInsertionIdx = i;
            }
        }

//        if (closestIdx == -1) {
//            compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
//        } else {
//            compRef.getModelEditor().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
//        }

        //compRef.getImplementationEditor().showSampleMarker(candX, candY);

//        System.out.println("closestX = " + closestX + ", closestY = " + closestY + ", closestIdx = " + closestIdx);

        /* find row & col of new insertion idx */
        int closestCellRowIdx = closestInsertionIdx / (columnCount + 1);
        int closestCellColIdx = closestInsertionIdx % (columnCount + 1);

        /* convert row & col of insertion idx into row & col of cell */
        if (closestCellColIdx == columnCount) {
            closestCellRowIdx = closestCellRowIdx + 1;
            closestCellColIdx = 0;
        }

        int closestCellIdx = closestCellRowIdx * columnCount + closestCellColIdx;


        return new int [] { closestX, closestY, closestCellIdx };
    }

    public static Point getRelationBarOrigin(int barIndex, ComponentReference compRef) {
        int y = BAR_TOP_MARGIN;

        y = y + compRef.getInterfaceEditor().getEditorSize().height + UIUtil.TOOL_PANEL_HEIGHT;

        for (int i = 0; i < barIndex; i++) {
            y = y + ((RelationBar) compRef.getRelationEditor().getComponent(i)).getBarSize().height + GAP_BETWEEN_BARS;
        }
        return new Point(BAR_LEFT_MARGIN,  y);
    }

    /** get Y value for the given insertion point index.
     * when we have three relation bars, the index of the position in front of [RelA] is 0.
     * the index of the position after [RelC] is 4.
     *
     * [RelA]
     * [RelB]
     * [RelC]
     */
    public static int getRelationBarInsertionPoint(int idx, ComponentReference compRef) {
        int y = BAR_TOP_MARGIN;

        y = y + compRef.getInterfaceEditor().getEditorSize().height + UIUtil.TOOL_PANEL_HEIGHT;

        for (int i = 0; i < idx; i++) {
            y = y + ((RelationBar) compRef.getRelationEditor().getComponent(i)).getBarSize().height + GAP_BETWEEN_BARS;
        }
        y = y - GAP_BETWEEN_BARS / 2;
        return y;
    }

    /**
     * for interface bar, set barIdx as -1, for the first relation bar, set barIdx as 0;
     * find (x, y) of cell insertion point. int[2] { x, y }
     */
    public static int[] getCellInsertionPoint(int barIdx, boolean isLeftPanel, int candidateIdx, int columnCount, ComponentReference compRef) {
        int candidateColumnWidth = columnCount + 1;
        int candidateRowIdx = (candidateIdx / candidateColumnWidth);
        int candidateColIdx = (candidateIdx % candidateColumnWidth);

        int x = BAR_BORDER_HL_THICKNESS;
        int y = BAR_BORDER_HL_THICKNESS + CELL_BORDER_THICKNESS / 2;

        if (barIdx != -1) {
            Point barOrigin = UIUtil.getRelationBarOrigin(barIdx, compRef);
            x += barOrigin.x;
            y += barOrigin.y;
        } else {
            Point barOrigin = UIUtil.getInterfaceBarOrigin();
            x += barOrigin.x;
            y += barOrigin.y;
        }

        if (barIdx >= 0) {
            if (isLeftPanel) {
                RelationBar bar = compRef.getRelationBar(barIdx);
                int cellWidth = UIUtil.CG_CELL_WIDTH;
                int cellHeight = UIUtil.CG_CELL_HEIGHT;
                x = x + (cellWidth + PAD_BETWEEN_CELLS) * candidateColIdx;
                y = y + (cellHeight + PAD_BETWEEN_CELLS) * candidateRowIdx + cellHeight / 2;
            } else {
                RelationBar bar = compRef.getRelationBar(barIdx);
                int cellWidth = UIUtil.CG_CELL_WIDTH;
                int cellHeight = UIUtil.CG_CELL_HEIGHT;
                x = x + bar.getLeftPanelSize().width + bar.getCenterPanelSize().width + (cellWidth + PAD_BETWEEN_CELLS) * candidateColIdx;
                y = y + (cellHeight + PAD_BETWEEN_CELLS) * candidateRowIdx + cellHeight / 2;
            }
        } else {
            if (isLeftPanel) {
                InterfaceBar bar = compRef.getInterfaceBar();
                int cellWidth = UIUtil.CG_CELL_WIDTH;
                int cellHeight = UIUtil.CG_CELL_HEIGHT;
                x = x + (cellWidth + PAD_BETWEEN_CELLS) * candidateColIdx;
                y = y + (cellHeight + PAD_BETWEEN_CELLS) * candidateRowIdx + cellHeight / 2;
            } else {
                InterfaceBar bar = compRef.getInterfaceBar();
                int cellWidth = UIUtil.CG_CELL_WIDTH;
                int cellHeight = UIUtil.CG_CELL_HEIGHT;
                x = x + bar.getLeftPanelSize().width + bar.getCenterPanelSize().width + (cellWidth + PAD_BETWEEN_CELLS) * candidateColIdx;
                y = y + (cellHeight + PAD_BETWEEN_CELLS) * candidateRowIdx + cellHeight / 2;
            }
        }
        return new int[] { x, y };
    }

    /** if the (current x, current y) belong to any relation or interface, return its index. returns -2 if not found. */
    public static int findBarIndexOnWhichMouseIsOver(int currentX, int currentY, ComponentReference compRef) {
        /* select to which bar the selected cells will be moved or copied */
        for (int i = 0; i < compRef.getRelationBarCount(); i++) {
            Point barOrigin = UIUtil.getRelationBarOrigin(i, compRef);
            int barHeight = compRef.getRelationBar(i).getHeight();
            if (currentY >= barOrigin.y && currentY <= (barOrigin.y + barHeight)) {
                return i;
            }
        }
        Point itfOrigin = UIUtil.getInterfaceBarOrigin();
        int barHeight = compRef.getInterfaceBar().getHeight();
        if (currentY >= itfOrigin.y && currentY <= (itfOrigin.y + barHeight)) {
            return -1;
        }

        return -2;
    }

    /** return -1 (left) 0 (not determinable), 1 (right) depending on mouse is on the left panel of the given bar. if barIdx == -1, this method will look into an interface bar. if barIdx >=0, this method will look into a relation bar */
    public static int findWhichSideMouseIsOver(int currentX, int barIndex, ComponentReference compRef) {
        Point barOrigin = UIUtil.getRelationBarOrigin(barIndex, compRef);

        BaseBar bar = null;
        if (barIndex == -1) {
            bar = compRef.getInterfaceBar();
        } else {
            bar = compRef.getRelationBar(barIndex);
        }

        int leftPanelWidth = bar.getLeftPanelSize().width;
        int centerPanelWidth = bar.getCenterPanelSize().width;
        int rightPanelWidth = bar.getRightPanelSize().width;

        int x1Left = barOrigin.x - UIUtil.CG_CELL_WIDTH / 2;
        int x2Left = barOrigin.x + leftPanelWidth + centerPanelWidth / 2;

        int x1Right = barOrigin.x + leftPanelWidth + centerPanelWidth - centerPanelWidth / 2;
        int x2Right = barOrigin.x + leftPanelWidth + centerPanelWidth + rightPanelWidth + UIUtil.CG_CELL_WIDTH / 2;

        if (x1Left <= currentX && currentX <= x2Left) {
            return -1;
        } else if (x1Right <= currentX && currentX <= x2Right) {
            return 1;
        }
        return 0;
    }

//    public static String getHtml(String text) {
//        return text;
////        String startsWith = "<html><head><style type=text/css><!--body{color:black;font-size:10pt;font-family:arial;}--></style></head><body>";
////        String endsWith = "</body></html>";
////        return startsWith + text + endsWith;
//    }

//    public static String getText(String html) {
//        return html;
////        String startsFrom = "<body>";
////        String endsAt = "</body>";
////        return html.substring(html.indexOf(startsFrom) + startsFrom.length(), html.indexOf(endsAt)).trim();
//    }

    public static void updateCellAndBarLayout(ComponentReference compRef) {
        InterfaceBar itfBar = compRef.getInterfaceEditor().getInterfaceBar();
        boolean isEvaluationMode = compRef.getModelEditor().isEvaluationMode();
        if (itfBar != null) {
            itfBar.getCenterPanel().updateRelNameLabelHeight();
            itfBar.getCenterPanel().invalidate();
            for (int j = 0; j < itfBar.getLeftPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) itfBar.getLeftPanel().getComponent(j);
                cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
            }
            itfBar.getLeftPanel().invalidate();
            for (int j = 0; j < itfBar.getRightPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) itfBar.getRightPanel().getComponent(j);
                cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
            }
            itfBar.getRightPanel().invalidate();
            itfBar.updateLayoutConstraintsOfSidePanels();
        }

        for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
            compRef.getRelationBar(i).getCenterPanel().updateRelNameLabelHeight();
            compRef.getRelationBar(i).getCenterPanel().invalidate();
            for (int j = 0; j < compRef.getRelationBar(i).getLeftPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) compRef.getRelationBar(i).getLeftPanel().getComponent(j);
                cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
            }
            compRef.getRelationBar(i).getLeftPanel().invalidate();
            for (int j = 0; j < compRef.getRelationBar(i).getRightPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) compRef.getRelationBar(i).getRightPanel().getComponent(j);
                cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
            }
            compRef.getRelationBar(i).getRightPanel().invalidate();
            compRef.getRelationBar(i).invalidate();
            compRef.getRelationBar(i).updateLayoutConstraintsOfSidePanels();
        }

        compRef.getInterfaceEditor().revalidate();
        compRef.getRelationEditor().revalidate();
    }

    /** compute bounds for interface editor bound, toolbar bound, relation editor bound, and update bounds for them. finally call revalidate(). returns {interface editor bound, toolbar bound, relation editor bound } */
    public static EditorBound[] updateEditorBounds(ComponentReference compRef) {
        EditorBound itfBound = new EditorBound();
        EditorBound tbBound = new EditorBound();
        EditorBound relBound = new EditorBound();

        Dimension itfEditorSize = compRef.getInterfaceEditor().getEditorSize();
        Dimension relEditorSize = compRef.getRelationEditor().getEditorSize();

        Dimension tbSize;
        if (compRef.getImplementationEditor().isImplLoaded()) {
            JLayeredPane layeredPane = (JLayeredPane) compRef.getRelationEditor().getParent();
            tbSize = new Dimension(Math.max(layeredPane.getBounds().width, TOOL_PANEL_WIDTH), TOOL_PANEL_HEIGHT);
        } else {
            tbSize = new Dimension(0, 0);
        }

        itfBound.x = 0;
        itfBound.y = 0;
        itfBound.width = itfEditorSize.width;
        itfBound.height = itfEditorSize.height;

        tbBound.x = 0;
        tbBound.y = itfBound.y + itfBound.height;
        tbBound.width = tbSize.width;
        tbBound.height = tbSize.height;

        relBound.x = 0;
        relBound.y = tbBound.y + tbBound.height;
        relBound.width = relEditorSize.width;
        relBound.height = relEditorSize.height;

        InterfaceEditor interfaceEditor = compRef.getInterfaceEditor();
        interfaceEditor.setBounds(itfBound.x, itfBound.y, itfBound.width, itfBound.height);

        RelationToolPanel toolPanel = compRef.getRelationToolPanel();
        toolPanel.setBounds(tbBound.x, tbBound.y, tbBound.width, tbBound.height);

        RelationEditor relationEditor = compRef.getRelationEditor();
        relationEditor.setBounds(relBound.x, relBound.y, relBound.width, relBound.height);

        int maxWidth = Math.max(itfBound.width, relBound.width);
        int totalHeight = itfBound.height + relBound.height + tbBound.height;
        ((JLayeredPane) interfaceEditor.getParent()).setPreferredSize(new Dimension(maxWidth, totalHeight));

        interfaceEditor.revalidate();
        toolPanel.revalidate();
        relationEditor.revalidate();

        return new EditorBound[] { itfBound, tbBound, relBound };
    }

    /**
     * returns upper left point of script editor of a given scriptCell object.
     * used to setBounds() of script editor
     * scriptCell is either InterfaceOutputCell or RelationInputCell
     */
    public static Point calculateScriptEditorPosition(BaseCell scriptCell) {
        CellConfig cellConfig = scriptCell.getComponentReference().getCellConfig();

        if (scriptCell instanceof InterfaceOutputCell) {
            InterfaceBar itfBar = (InterfaceBar) scriptCell.getBar();
            int scriptEditorX = 0;
            int scriptEditorY = 0;

            int cellIndex = indexOfComponent(scriptCell);
            int colCount = ((InterfaceBarRightPanel) scriptCell.getParent()).getColumnCount();

            int rowIndex = cellIndex / colCount;
            int colIndex = cellIndex % colCount;
            Point itfOrigin = getInterfaceBarOrigin();
            Point cellOrigin = getCellOrigin(rowIndex, colIndex);

            if (cellConfig.alignType == 1) {
                scriptEditorX = itfBar.getLeftPanelSize().width + itfBar.getCenterPanelSize().width + itfOrigin.x + cellOrigin.x + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN;
                scriptEditorY = itfOrigin.y + cellOrigin.y + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_NAME_TOP_MARGIN + cellConfig.nameRow * UIUtil.CG_NAME_ROW_HEIGHT;
                if (cellConfig.showTypeUnit) {
                    scriptEditorY += UIUtil.CG_TYPE_ROW_HEIGHT;
                }
            } else if (cellConfig.alignType == 2) {
                scriptEditorX = itfBar.getLeftPanelSize().width + itfBar.getCenterPanelSize().width + itfOrigin.x + cellOrigin.x + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_NAME_LEFT_MARGIN + UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN + UIUtil.CG_NAME_LABEL_WIDTH;
                scriptEditorY = itfOrigin.y + cellOrigin.y + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_SCRIPT_DISP_TOP_MARGIN;
            } else if (cellConfig.alignType == 3) {
                scriptEditorX = itfBar.getLeftPanelSize().width + itfBar.getCenterPanelSize().width + itfOrigin.x + cellOrigin.x + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_NAME_LEFT_MARGIN + UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN + UIUtil.CG_NAME_LABEL_WIDTH;
                if (cellConfig.showTypeUnit) {
                    scriptEditorX += (UIUtil.CG_TYPE_WIDTH + UIUtil.CG_UNIT_WIDTH) + UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN;
                }
                scriptEditorY = itfOrigin.y + cellOrigin.y + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_SCRIPT_DISP_TOP_MARGIN;
            }
            return new Point(scriptEditorX, scriptEditorY);
        } else {
            int scriptEditorX = 0;
            int scriptEditorY = 0;

            RelationBar relBar = (RelationBar) scriptCell.getBar();
            int cellIndex = indexOfComponent(scriptCell);
            int barIndex = indexOfComponent(relBar);
            int colCount = ((RelationBarLeftPanel) scriptCell.getParent()).getColumnCount();

            int rowIndex = cellIndex / colCount;
            int colIndex = cellIndex % colCount;
            Point cellOrigin = getCellOrigin(rowIndex, colIndex);
            Point barOrigin = getRelationBarOrigin(barIndex, scriptCell.getComponentReference());

            if (cellConfig.alignType == 1) {
                scriptEditorX = barOrigin.x + cellOrigin.x + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN;
                scriptEditorY = barOrigin.y + cellOrigin.y + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_NAME_TOP_MARGIN + cellConfig.nameRow * UIUtil.CG_NAME_ROW_HEIGHT;
                if (cellConfig.showTypeUnit) {
                    scriptEditorY += UIUtil.CG_TYPE_ROW_HEIGHT;
                }
            } else if (cellConfig.alignType == 2) {
                scriptEditorX = barOrigin.x + cellOrigin.x + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_NAME_LEFT_MARGIN + UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN + UIUtil.CG_NAME_LABEL_WIDTH;
                scriptEditorY = barOrigin.y + cellOrigin.y + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_SCRIPT_DISP_TOP_MARGIN;
            } else if (cellConfig.alignType == 3) {
                scriptEditorX = barOrigin.x + cellOrigin.x + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_NAME_LEFT_MARGIN + UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN + UIUtil.CG_NAME_LABEL_WIDTH;
                if (cellConfig.showTypeUnit) {
                    scriptEditorX += (UIUtil.CG_TYPE_WIDTH + UIUtil.CG_UNIT_WIDTH) + UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN;
                }
                scriptEditorY = barOrigin.y + cellOrigin.y + UIUtil.CELL_BORDER_THICKNESS + UIUtil.CG_SCRIPT_DISP_TOP_MARGIN;
            }
            return new Point(scriptEditorX, scriptEditorY);
        }
    }

    public static class EditorBound {
        int x;
        int y;
        int width;
        int height;
    }

    public static JTextArea createMultilineLabel(String text, boolean lineWrap, boolean wrapStyleWord) {
        JTextArea textArea = new JTextAreaWithCustomTooltipLocation(text, 1, 1);
        textArea.setEditable(false);
        textArea.setLineWrap(lineWrap);
        textArea.setWrapStyleWord(wrapStyleWord);
        textArea.setBackground(null);
        textArea.setBorder(null);
        return textArea;
    }

    public static JTextPane createCenterAlignedMultilineLabel(String text, boolean linewrap, boolean wrapStyleWord, Font font) {
        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        textPane.setFocusable(false);
        textPane.setBackground(null);
        textPane.setFont(font);
        textPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

//		//  Define a keyword attribute
//        MutableAttributeSet keyWord = new SimpleAttributeSet();
//        StyleConstants.setForeground(keyWord, Color.BLACK);
//        StyleConstants.setFontFamily(keyWord, font.getFamily());
//        StyleConstants.setFontSize(keyWord, font.getSize());
//        StyleConstants.setBold(keyWord, font.isBold());

		//  Add initial text
		textPane.setText(text);
//        try {
//            doc.insertString(0, text, keyWord);
//        } catch (BadLocationException e) { }
//        System.out.println("doc.getLength() = " + doc.getLength());
//        //doc.setCharacterAttributes(0, doc.getLength(), keyWord, true);

        //  Set alignment to be centered for all paragraphs
        MutableAttributeSet standard = new SimpleAttributeSet();
        StyleConstants.setAlignment(standard, StyleConstants.ALIGN_CENTER);
        StyleConstants.setForeground(standard, Color.BLACK);
        StyleConstants.setFontFamily(standard, font.getFamily());
        StyleConstants.setFontSize(standard, font.getSize());
        StyleConstants.setBold(standard, font.isBold());
        //doc.setParagraphAttributes(0, 0, standard, true);
        doc.setParagraphAttributes(0, doc.getLength(), standard, true);
        //doc.setParagraphAttributes(standard, true);


//
//		JScrollPane scrollPane = new JScrollPane( textPane );
//		scrollPane.setPreferredSize( new Dimension( 200, 200 ) );
        return textPane;
    }

    /** to get the first line of JTextArea, getMultilineText(textArea, 0, 0);
     * to get the second line to the fourth line of JTextArea, getMultilineText(textArea, 1, 3);  */
//    public static String getMultilineText(JTextArea textArea, int startRowIdx, int endRowIdx) {
//        StringBuffer sb = new StringBuffer();
//        Element paragraph = textArea.getDocument().getDefaultRootElement();
//
//        int contentCount = paragraph.getElementCount();
//
//        // Get index ranges for each content element.
//        // Each content element represents one line.
//        // Each line includes the terminating newline.
//        for (int i = 0; i < contentCount; i++) {
//            Element e = paragraph.getElement(i);
//            int rangeStart = e.getStartOffset();
//            int rangeEnd = e.getEndOffset();
//            if (i >= startRowIdx && i <= endRowIdx) {
//                try {
//                    String line = textArea.getText(rangeStart, rangeEnd-rangeStart);
//                    sb.append(line);
//                } catch (BadLocationException ex) { }
//            }
//        }
//        return sb.toString();
//    }

    public static int getLineCount(JTextComponent c) {
        int len = c.getDocument().getLength();
        int lineCount = 0;
        int offset = 0;
        try {
            while (offset < len) {
                int end = Utilities.getRowEnd(c, offset);
                if (end < 0) {
                    break;
                }
                end = Math.min(end + 1, len);
                lineCount++;
                offset = end;
            }
        } catch (BadLocationException e) { System.out.println("getLineCount(): " + e); }
        return (lineCount == 0) ? 2 : lineCount; // line count cannot be zero
    }

    // Returns null if comp does not have a size
    public static String getMultilineText(JTextComponent c, int startRowIdx, int endRowIdx) {
        int len = c.getDocument().getLength();
        int offset = 0;

        StringBuffer buf = new StringBuffer();

        int rowIdx = 0;
        try {
            while (offset < len) {
                int end = Utilities.getRowEnd(c, offset);
                if (end < 0) {
                    break;
                }

                // Include the last character on the line
                end = Math.min(end + 1, len);

                String s = c.getDocument().getText(offset, end - offset);
                if (rowIdx >= startRowIdx && rowIdx <= endRowIdx) {
                    buf.append(s);
                }
                rowIdx++;
                offset = end;
            }
        } catch (BadLocationException e) { System.out.println("getMultilineText(): " + e); }
        return buf.toString();
    }

    public static void markTextOmission(JTextArea textArea) {
        String contents = textArea.getText();
        textArea.setText(contents.substring(0, contents.length() - 2) + "...");
    }

    public static JButton createButton(String text) {
        JButton comp = new JButton(text);
        comp.setFocusPainted(false);
        comp.setMargin(new Insets(2, 10, 2, 10));
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JButton createSmallButton(String text) {
        JButton comp = new JButton(text);
        comp.setFocusPainted(false);
        comp.setMargin(new Insets(0, 5, 0, 5));
        comp.setFont(UIUtil.SMALL_DIALOG_FONT);
        return comp;
    }

    public static JButton createTinyButton(String text) {
        JButton comp = new JButton(text);
        comp.setFocusPainted(false);
        comp.setMargin(new Insets(0, 0, 0, 0));
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JToggleButton createToggleButton(String text, Icon icon) {
        JToggleButton comp = null;
        if (text != null && icon != null) {
            comp = new JToggleButton(text, icon);
        } else if (text !=null) {
            comp = new JToggleButton(text);
        } else if (icon !=null) {
            comp = new JToggleButton(icon);
        } else {
            return null;
        }

        comp.setFocusPainted(false);
        comp.setMargin(new Insets(2, 10, 2, 10));
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JLabel createLabel(String text) {
        JLabel comp = new JLabel(text);
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JLabel createSmallLabel(String text) {
        JLabel comp = new JLabel(text);
        comp.setFont(UIUtil.SMALL_DIALOG_FONT);
        return comp;
    }

    public static JLabel createItalicLabel(String text) {
        JLabel comp = new JLabel(text);
        comp.setFont(UIUtil.DIALOG_ITALIC_FONT);
        return comp;
    }

    public static JPasswordField createPasswordField(String text, int columns) {
        JPasswordField comp = new JPasswordField(text, columns);
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JTextField createTextField() {
        JTextField comp = new JTextField();
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JTextArea createTextArea(String text, int row, int col) {
        JTextArea comp = new JTextArea(text, row, col);
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JTextField createTextField(String text, int columns) {
        JTextField comp = new JTextField(text, columns);
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JFormattedTextField createFormattedTextField() {
        JFormattedTextField comp = new JFormattedTextField();
        comp.setFont(UIUtil.DIALOG_FONT);
        return comp;
    }

    public static JComboBox createComboBox() {
        JComboBox comp = new JComboBox();
        comp.setFont(UIUtil.DIALOG_FONT);
        setFixedSize(comp, comp.getPreferredSize());
        return comp;
    }

    public static JRadioButton createRadioButton(String text) {
        JRadioButton comp = new JRadioButton(text);
        comp.setFont(UIUtil.DIALOG_FONT);
        setFixedSize(comp, comp.getPreferredSize());
        return comp;
    }

    public static ButtonGroup groupButtons(JRadioButton[] buttons) {
        ButtonGroup ret = new ButtonGroup();
        for (int i = 0; i < buttons.length; i++) {
            JRadioButton button = buttons[i];
            ret.add(button);
        }
        return ret;
    }

    public static JPanel createButtonPanel(Component[] comps) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        for (int i = 0; i < comps.length; i++) {
             panel.add(comps[i]);
        }
        return panel;
    }

    public static JCheckBox createCheckBox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFocusPainted(false);
        cb.setFont(UIUtil.DIALOG_FONT);
        return cb;
    }

    public static JComboBox createComboBox(Object[] objs) {
        JComboBox comp = new JComboBox(objs);
        comp.setFont(UIUtil.DIALOG_FONT);
        setFixedSize(comp, comp.getPreferredSize());
        return comp;
    }

    public static JList createList() {
        JList l = new JList();
        l.setFont(UIUtil.DIALOG_FONT);
        return l;
    }

    public static void setFixedSize(JComponent comp, Dimension size) {
        comp.setMinimumSize(size);
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
    }

    /** (left margin) name (gap1) [    ] (gap2) address (gap1) [    ]. gap 2 is usually larger than gap 1. */
    public static void setConstraints(Component[] comps, SpringLayout layout, int leftMargin, int topMargin, int gap1, int gap2) {
        for (int i = 0; i < comps.length; i++) {
            if (i ==0) {
                SpringLayout.Constraints cons = layout.getConstraints(comps[i]);
                cons.setX(Spring.constant(leftMargin));
                cons.setY(Spring.constant(topMargin));
            } else {
                SpringLayout.Constraints cons = layout.getConstraints(comps[i]);
                SpringLayout.Constraints prevCons = layout.getConstraints(comps[i - 1]);
                cons.setX(Spring.sum(Spring.constant((i % 2 == 1 ? gap1 : gap2)), prevCons.getConstraint("East")));
                cons.setY(Spring.constant(topMargin));
            }
        }
    }

    /**
     * if left column is right-aligned, and right column is left-aligned, constraints for the following layout are generated.
     * (left column) (right column)
     *         name: [    ]
     * leftAlign is one of "left", "right", and "center"
     * rightAlign is one of "left", "right", and "center"
     */
    public static void setTwoColumnConstraints(Component leftComp, Component rightComp, SpringLayout layout, int leftColumnWidth, int rightColumnWidth, int leftMargin, int topMargin, int gapBetweenColumns, String leftAlign, String rightAlign) {
        SpringLayout.Constraints leftCons = layout.getConstraints(leftComp);
        SpringLayout.Constraints rightCons = layout.getConstraints(rightComp);

        if ("right".equalsIgnoreCase(leftAlign)) {
            leftCons.setX(Spring.constant(leftColumnWidth - leftComp.getPreferredSize().width + leftMargin));
        } else if ("left".equalsIgnoreCase(leftAlign)) {
            leftCons.setX(Spring.constant(leftMargin));
        } else if ("center".equalsIgnoreCase(leftAlign)) {
            leftCons.setX(Spring.constant((leftColumnWidth - leftComp.getPreferredSize().width) / 2 + leftMargin));
        }
        leftCons.setY(Spring.constant(topMargin));

        if ("right".equalsIgnoreCase(rightAlign)) {
            rightCons.setX(Spring.constant(rightColumnWidth - rightComp.getPreferredSize().width + leftMargin + leftColumnWidth + gapBetweenColumns));
        } else if ("left".equalsIgnoreCase(rightAlign)) {
            rightCons.setX(Spring.constant(leftMargin + leftColumnWidth + gapBetweenColumns));
        } else if ("center".equalsIgnoreCase(rightAlign)) {
            rightCons.setX(Spring.constant((rightColumnWidth - rightComp.getPreferredSize().width) / 2 + leftMargin + leftColumnWidth + gapBetweenColumns));
        }
        rightCons.setY(Spring.constant(topMargin));
    }

    /**
     * if right-aligned is given, constraints for the following layout are generated
     * (    column width     )
     *            comp1 comp2
     */
    public static void setFlowConstraints(Component[] comps, SpringLayout layout, int columnWidth, int leftMargin, int topMargin, int gapBetweenComps, String align) {
        int gapTotal = (comps.length - 1) * gapBetweenComps;
        int compWidthTotal = 0;
        for (int i = 0; i < comps.length; i++) {
            compWidthTotal += comps[i].getPreferredSize().width;
        }

        int startingX = 0;
        if ("right".equalsIgnoreCase(align)) {
            startingX = leftMargin + columnWidth - (compWidthTotal + gapTotal);
        } else if ("left".equalsIgnoreCase(align)) {
            startingX = leftMargin;
        } else if ("center".equalsIgnoreCase(align)) {
            startingX = leftMargin + (columnWidth - (compWidthTotal + gapTotal)) / 2;
        }

        int compX = startingX;
        for (int i = 0; i < comps.length; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(comps[i]);
            cons.setX(Spring.constant(compX));
            cons.setY(Spring.constant(topMargin));
            compX = compX + comps[i].getPreferredSize().width + gapBetweenComps;
        }
    }

    public static void addComponentsToPanel(Component[] comps, Container panel) {
        for (int i = 0 ; i < comps.length; i++) {
            panel.add(comps[i]);
        }
    }

    public static void initUnit() {
        /* initialize unit system */
        if (! isUnitLoaded) {
            try {
                InputStream is = new ByteArrayInputStream(CommonUnit.DATA.getBytes());
                UnitTab.read(is);
            } catch (IOException e) {
                System.out.println("Error reading unit data:" + e);
            }
            isUnitLoaded = true;
        }
    }

    public static int countQuery(String text, String query) {
        int nextIdx = -1 * query.length();
        int count = -1;
        do {
            nextIdx = text.indexOf(query, nextIdx + query.length());
            count++;
        } while (nextIdx != -1);
        return count;
    }

    public static int countLineBreak(String text) {
        return countQuery(text, "\n");
    }

    /** returns Object[] { row length, position of beginning of the row }  of { Integer, Integer } */
    public static Object[] getRow(int findingRowIdx, String text) {
        String query = "\r\n";
        if (text.indexOf("\r\n") == -1) {
            query = "\n";
        }
        int endIdx = -1 * query.length();
        int startIdx = 0;
        int rowIdx = -1;
        int charCount = 0;
        do {
            startIdx = endIdx + query.length();
            endIdx = text.indexOf(query, startIdx);
            rowIdx++;
            if (endIdx != -1) {
                if (rowIdx == findingRowIdx) {
                    return new Object[] { new Integer(endIdx - startIdx), new Integer(charCount) };
                }
                charCount += endIdx - startIdx + 1;
            } else {
                if (rowIdx == findingRowIdx) {
                    return new Object[] { new Integer(text.length() - startIdx), new Integer(charCount) };
                }
            }
        } while (endIdx != -1);

        throw new RuntimeException("given text doesn't have a row indexed as " + rowIdx);
//
//        Pattern pattern = Pattern.compile("(.*)(?:\\n)?");
//        Matcher matcher = pattern.matcher(text);
//        int charCount = 0;
//        String lastRow = "";
//        int rowIdx = 0;
//        while (matcher.find()) {
//            lastRow = matcher.group(1);
//            if (rowIdx == findingRowIdx) {
//                return new Object[] { lastRow, new Integer(charCount) };
//            }
//            charCount = charCount + lastRow.length() + 1;
//            rowIdx++;
//        }
//        throw new RuntimeException("given text doesn't have a row indexed as " + rowIdx);
    }

    /** based on style of given document, returns width of row */
    public static int getRowWidth(int rowIdx, String text, StyledDocument doc, Graphics graphics) {
        if (graphics == null) {
            return 0;
        }

        Object[] rowInfo = getRow(rowIdx, text);

        //String rowStr = (String) rowInfo[0];
        int rowLength = ((Integer) rowInfo[0]).intValue();
        int rowPos = ((Integer) rowInfo[1]).intValue();


        FontMetrics fontMetrics;
        int posX = 0;
        for (int i = rowPos; i < rowPos + rowLength; i++) {
            AttributeSet attrSet = doc.getCharacterElement(i).getAttributes();
            String fontFamily = StyleConstants.getFontFamily(attrSet);
            int fontSize = StyleConstants.getFontSize(attrSet);
            boolean isBold = StyleConstants.isBold(attrSet);
            boolean isItalic = StyleConstants.isItalic(attrSet);
            int fontStyle = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);

            Font curFont = (Font) fontCache.get(fontFamily + fontSize + isBold + isItalic);
            if (curFont == null) {
                curFont = new Font(fontFamily, fontStyle,  fontSize);
                fontCache.put(fontFamily + fontSize + isBold + isItalic, curFont);
            }

            fontMetrics = graphics.getFontMetrics(curFont);
            //posX = posX + fontMetrics.charWidth(rowStr.charAt(i - rowPos));
            posX = posX + fontMetrics.charWidth(text.charAt(i));
        }
        return posX;
    }

    /** return Object[] of { rowIndex, columnIndex, lastRowStartPos, stringLeftToCaret } of current caret { Integer, Integer, Integer, String } */
    public static Object[] getCaret(int caretPos, StyledDocument doc, Graphics graphics) {
        String text = null;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException("failed to get row width because of " + e.getMessage());
        }

        Pattern pattern = Pattern.compile("(.*)(?:\\n)?");
        Matcher matcher = pattern.matcher(text);
        int rowIdx = 0;
        int charCount = 0;
        int colIdx = 0;
        String leftStr = "";
        while (matcher.find()) {
            String g1 = matcher.group(1);
            if (charCount + g1.length() >= caretPos) {
                colIdx = caretPos - charCount;
                leftStr = g1.substring(0, colIdx);
                break;
            } else {
                charCount = charCount + g1.length() + 1;
            }
            rowIdx++;
        }
        return new Object[] { new Integer(rowIdx), new Integer(colIdx), new Integer(charCount), leftStr };
    }


    /** int[fromRow~toRow][0] contains row start pos, int[fromRow~toRow][1] contains row end pos */
    public static int[][] getRowStartEndPos(String text, int fromRow, int toRow) {
        int[][] getRowStartEndPos = new int[toRow - fromRow + 1][2];
        Pattern pattern = Pattern.compile("(.*)(?:\\r\\n)?");
        Matcher matcher = pattern.matcher(text);
        int rowIdx = 0;
        int charCount = 0;
        while (matcher.find()) {
            String g1 = matcher.group(1);
            if (rowIdx >= fromRow && rowIdx <= toRow) {
                getRowStartEndPos[rowIdx - fromRow][0] = charCount;
                getRowStartEndPos[rowIdx - fromRow][1] = charCount + g1.length();
            }
            charCount = charCount + g1.length() + 1;
            rowIdx++;
        }
        return getRowStartEndPos;
    }

    /** this method treats line break as one character, so it accurately position the ith character */
    private static char getCharAt(JTextPane textPane, int i) {
        try {
            return textPane.getText(i, 1).charAt(0);
        } catch (BadLocationException e) {};
        return 0;
    }

    /** create automatic query */
    public static String createAutomaticQuery(JTextPane textPane) {
        Caret caret = textPane.getCaret();
        StringBuffer sb = new StringBuffer();
        for (int i = caret.getMark() - 1;  i >= 0; i--) {
            if (! Character.isLetterOrDigit(UIUtil.getCharAt(textPane, i))) {
                String ret = sb.reverse().toString();
                return ret;
            } else {
                sb.append(UIUtil.getCharAt(textPane, i));
                if (i == 0) {
                    String ret = sb.reverse().toString();
                    return ret;
                }
            }
        }

        return null;
    }

    /** calculate caret position like Caret.getMagicCaretPosition(), but more accurate and convenient
     * option should be one of left, right, mark, dot */
    public static Point getCaretPosition(String option, JTextPane txtPane, Graphics graphics) {
        StyledDocument doc = (StyledDocument) txtPane.getDocument();

        int wantPos = 0;
        int markPos = txtPane.getCaret().getMark();
        int dotPos = txtPane.getCaret().getDot();

        if ("right".equalsIgnoreCase(option)) { wantPos = (markPos > dotPos) ? markPos : dotPos; }
        else if ("left".equalsIgnoreCase(option)) { wantPos = (markPos < dotPos) ? markPos : dotPos; }
        else if ("mark".equalsIgnoreCase(option)) { wantPos = markPos; }
        else if ("dot".equalsIgnoreCase(option)) { wantPos = dotPos; }

        Object[] rowColAndStr = UIUtil.getCaret(wantPos, doc, graphics);

        int rowIdx = ((Integer) rowColAndStr [0]).intValue();
        int colIdx = ((Integer) rowColAndStr [1]).intValue();
        int lastRowStartPos = ((Integer) rowColAndStr [2]).intValue();
        String leftStr = (String) rowColAndStr [3];

        FontMetrics fontMetrics = graphics.getFontMetrics(txtPane.getFont()); // height comes from default txt pane font. assuming other fonts have the same height
        int posY = fontMetrics.getHeight() * rowIdx;
        int posX = 0;
        for (int i = lastRowStartPos; i < lastRowStartPos + colIdx; i++) {
            AttributeSet attrSet = doc.getCharacterElement(i).getAttributes();
            String fontFamily = StyleConstants.getFontFamily(attrSet);
            int fontSize = StyleConstants.getFontSize(attrSet);
            boolean isBold = StyleConstants.isBold(attrSet);
            boolean isItalic = StyleConstants.isItalic(attrSet);
            int fontStyle = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);

            Font curFont = (Font) fontCache.get(fontFamily + fontSize + isBold + isItalic);
            if (curFont == null) {
                curFont = new Font(fontFamily, fontStyle,  fontSize);
                fontCache.put(fontFamily + fontSize + isBold + isItalic, curFont);
            }

            fontMetrics = graphics.getFontMetrics(curFont);
            posX = posX + fontMetrics.charWidth(leftStr.charAt(i - lastRowStartPos));
        }

        return new Point(posX, posY);
    }

    /** return [leftCaretPos, rightCaretPos] */
    public static int[] getCaretRange(JTextPane textPane) {
        int markPos = textPane.getCaret().getMark();
        int dotPos = textPane.getCaret().getDot();
        return (markPos > dotPos) ? new int[] { dotPos, markPos } : new int[] { markPos, dotPos };
    }

	public static List toList(String input, String delim){
		List ret = new ArrayList();
		StringTokenizer st = new StringTokenizer(input, delim);
		while (st.hasMoreTokens()) {
			ret.add(st.nextToken().trim());
		}
		return ret;
	}

    public static void adjustScriptEditorSizeOfAllCells(ComponentReference compRef) {
        /* adjust the script editor size of interface output cells */
        for (int j = 0; j < compRef.getInterfaceBar().getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) compRef.getInterfaceBar().getRightPanel().getComponent(j);
            cell.adjustScriptEditorSize();
        }

        /* adjust the script editor size of relation input cells */
        for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
            for (int j = 0; j < compRef.getRelationBar(i).getLeftPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) compRef.getRelationBar(i).getLeftPanel().getComponent(j);
                cell.adjustScriptEditorSize();
            }
        }
    }

    public static Iterator getIteratorForAllCells(ComponentReference compRef) {
        List ret = new ArrayList();
        for (int j = 0; j < compRef.getInterfaceBar().getLeftPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) compRef.getInterfaceBar().getLeftPanel().getComponent(j);
            ret.add(cell);
        }

        for (int j = 0; j < compRef.getInterfaceBar().getRightPanel().getComponentCount(); j++) {
            BaseCell cell = (BaseCell) compRef.getInterfaceBar().getRightPanel().getComponent(j);
            ret.add(cell);
        }

        for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
            for (int j = 0; j < compRef.getRelationBar(i).getLeftPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) compRef.getRelationBar(i).getLeftPanel().getComponent(j);
                ret.add(cell);
            }

            for (int j = 0; j < compRef.getRelationBar(i).getRightPanel().getComponentCount(); j++) {
                BaseCell cell = (BaseCell) compRef.getRelationBar(i).getRightPanel().getComponent(j);
                ret.add(cell);
            }
        }

        return ret.iterator();
    }

    public static void updateCellGeometryParameters(CellConfig cellConfig) {
        if (cellConfig.alignType == 1) {
            CG_NAME_TOP_MARGIN = 0;
            CG_ICON_TOP_MARGIN = CG_NAME_TOP_MARGIN + 2;

            CG_NAME_ROW_HEIGHT = 16;
            CG_TYPE_ROW_HEIGHT = 15;

            int cellWidth = -1;
            if (cellConfig.width == 1) {  cellWidth = 110;  }
            else if (cellConfig.width == 2) { cellWidth = 130; }
            else if (cellConfig.width == 3) { cellWidth = 150; }


            CG_CELL_WIDTH = cellWidth;
            CG_NAME_LEFT_MARGIN = CG_ICON_AND_NAME_SIDE_MARGIN + CG_ICON_WIDTH + CELL_BORDER_THICKNESS; // 3 for border insets on the left side
            CG_TYPE_AND_UNIT_LEFT_MARGIN = 3;
            CG_NAME_LABEL_WIDTH = CG_CELL_WIDTH - CG_ICON_AND_NAME_SIDE_MARGIN - CG_ICON_WIDTH - 2 - CG_ICON_AND_NAME_SIDE_MARGIN - CELL_BORDER_THICKNESS * 2; // 2 for left margin, 2 for icon horizontal gap, 2 for right margin, CELL_BORDER_THICKNESS * 2 for border insets on both sides
            CG_SCRIPT_DISP_WIDTH = CG_CELL_WIDTH - CG_SCRIPT_DISP_SIDE_MARGIN * 2 - CELL_BORDER_THICKNESS * 2; // 2 for left margin, 2 for right margin, 3 * 2 for border insets on both sides
            CG_SCRIPT_DISP_HEIGHT = cellConfig.scriptRow * UIUtil.CG_NAME_ROW_HEIGHT + 2;

            int typeUnitWidth = CG_CELL_WIDTH - CG_TYPE_AND_UNIT_LEFT_MARGIN - CG_ICON_AND_NAME_SIDE_MARGIN - CELL_BORDER_THICKNESS * 2;
            //CG_TYPE_WIDTH = Math.min((int) (typeUnitWidth * 0.4), 40);
            CG_TYPE_WIDTH = 40;
            CG_UNIT_WIDTH = typeUnitWidth - CG_TYPE_WIDTH;

            CG_CELL_HEIGHT = CG_NAME_ROW_HEIGHT * (cellConfig.nameRow + cellConfig.scriptRow) + 2 + 1 + (cellConfig.showTypeUnit ? CG_TYPE_ROW_HEIGHT : 0) + CELL_BORDER_THICKNESS * 2; // 2 for script border, 1 for bottom margin, CELL_BORDER_THICKNESS * 2 for border insets on top and bottom
        } else if (cellConfig.alignType == 2) {
            CG_NAME_TOP_MARGIN = 2;
            CG_ICON_TOP_MARGIN = CG_NAME_TOP_MARGIN + 2;

            CG_NAME_ROW_HEIGHT = 16;
            CG_TYPE_ROW_HEIGHT = 15;

            int cellWidth = -1;
            if (cellConfig.width == 1) {  cellWidth = 180;  }
            else if (cellConfig.width == 2) { cellWidth = 210; }
            else if (cellConfig.width == 3) { cellWidth = 240; }

            CG_CELL_WIDTH = cellWidth;
            CG_NAME_LEFT_MARGIN = CG_ICON_AND_NAME_SIDE_MARGIN + CG_ICON_WIDTH + 3; // 3 for border insets on the left side
            CG_TYPE_AND_UNIT_LEFT_MARGIN = 3;
            int nameAndScriptWidth = CG_CELL_WIDTH - CG_ICON_AND_NAME_SIDE_MARGIN - CG_ICON_WIDTH - 2 - CG_ICON_AND_NAME_SIDE_MARGIN - CG_SCRIPT_DISP_SIDE_MARGIN - CELL_BORDER_THICKNESS * 2; // 2 for icon horizontal gap, CELL_BORDER_THICKNESS * 2 for border insets on both sides
            CG_NAME_LABEL_WIDTH = (int) (nameAndScriptWidth * 0.55); // use 70% of available space for name label
            CG_SCRIPT_DISP_WIDTH = nameAndScriptWidth - CG_NAME_LABEL_WIDTH; // use the remaining of available space for script disp
            CG_SCRIPT_DISP_HEIGHT = cellConfig.scriptRow * UIUtil.CG_NAME_ROW_HEIGHT + 2;

            int typeUnitWidth = CG_NAME_LABEL_WIDTH;
            CG_TYPE_WIDTH = 40;
            CG_UNIT_WIDTH = typeUnitWidth - CG_TYPE_WIDTH;

            CG_CELL_HEIGHT = CG_NAME_ROW_HEIGHT * 2 + 2 + 1 * 2 + CELL_BORDER_THICKNESS * 2; // (this align type has always 2 rows) 2 for script border, 1 * 2 for top & bottom margin, CELL_BORDER_THICKNESS * 2 for border insets on top and bottom
        } else if (cellConfig.alignType == 3) {
            CG_NAME_TOP_MARGIN = 2;
            CG_ICON_TOP_MARGIN = CG_NAME_TOP_MARGIN + 2;

            CG_NAME_ROW_HEIGHT = 16;
            CG_TYPE_ROW_HEIGHT = 15;

            int cellWidth = -1;
            if (cellConfig.width == 1) {  cellWidth = 290;  }
            else if (cellConfig.width == 2) { cellWidth = 330; }
            else if (cellConfig.width == 3) { cellWidth = 360; }

            int typeUnitWidth = -1;
            if (cellConfig.showTypeUnit) {
                typeUnitWidth = (int) (cellWidth * 0.30);
                CG_CELL_WIDTH = cellWidth + typeUnitWidth;
            } else {
                CG_CELL_WIDTH = cellWidth;
            }

            CG_NAME_LEFT_MARGIN = CG_ICON_AND_NAME_SIDE_MARGIN + CG_ICON_WIDTH + CELL_BORDER_THICKNESS; // 3 for border insets on the left side
            int nameAndScriptWidth = -1;

            if (cellConfig.showTypeUnit) {
                nameAndScriptWidth = CG_CELL_WIDTH - CG_ICON_AND_NAME_SIDE_MARGIN - CG_ICON_WIDTH - 2 - CG_ICON_AND_NAME_SIDE_MARGIN - CG_SCRIPT_DISP_SIDE_MARGIN - CG_SCRIPT_DISP_SIDE_MARGIN - CELL_BORDER_THICKNESS * 2; // 2 for icon horizontal gap, CELL_BORDER_THICKNESS * 2 for border insets on both sides
                CG_NAME_LABEL_WIDTH = (int) (cellWidth * 0.40); // use 35% of available space for name label
                CG_SCRIPT_DISP_WIDTH = nameAndScriptWidth - CG_NAME_LABEL_WIDTH - typeUnitWidth; // use the remaining of available space for script disp
            } else {
                nameAndScriptWidth = CG_CELL_WIDTH - CG_ICON_AND_NAME_SIDE_MARGIN - CG_ICON_WIDTH - 2 - CG_ICON_AND_NAME_SIDE_MARGIN - CG_SCRIPT_DISP_SIDE_MARGIN - CELL_BORDER_THICKNESS * 2; // 2 for icon horizontal gap, CELL_BORDER_THICKNESS * 2 for border insets on both sides
                CG_NAME_LABEL_WIDTH = (int) (cellWidth * 0.40); // use 50% of available space for name label
                CG_SCRIPT_DISP_WIDTH = nameAndScriptWidth - CG_NAME_LABEL_WIDTH; // use the remaining of available space for script disp
            }
            CG_SCRIPT_DISP_HEIGHT = cellConfig.scriptRow * UIUtil.CG_NAME_ROW_HEIGHT + 2;

            if (! cellConfig.showTypeUnit) {
                typeUnitWidth = CG_SCRIPT_DISP_WIDTH;
            }

            CG_TYPE_WIDTH = 40;
            CG_UNIT_WIDTH = typeUnitWidth - CG_TYPE_WIDTH;

            CG_CELL_HEIGHT = CG_NAME_ROW_HEIGHT * 1 + 2 + 1 * 2 + CELL_BORDER_THICKNESS * 2; // (this align type has always 2 rows) 2 for script border, 1 * 2 for top & bottom margin, CELL_BORDER_THICKNESS * 2 for border insets on top and bottom
        }

//        Graphics g = ComponentReference.getGraphics();
//        if (g != null) {
//            FontMetrics fm1 = g.getFontMetrics(PARAM_NAME_FONT);
//            FontMetrics fm2 = g.getFontMetrics(PARAM_UNIT_FONT);
//            CG_NAME_ROW_HEIGHT = fm1.getHeight();
//            CG_TYPE_ROW_HEIGHT = fm2.getHeight();
//            System.out.println("good! Graphics object is not null. name row height and type row height are " + CG_NAME_ROW_HEIGHT + ", " + CG_TYPE_ROW_HEIGHT);
//        } else {
//            System.out.println("warning! Graphics object is null. Using default value for name row height and type row height");
//            CG_NAME_ROW_HEIGHT = 16;
//            CG_TYPE_ROW_HEIGHT = 15;
//        }

    }

    /** when we have a space for 4 rows, when paramName needs 2 lines, when script needs 1 lines and when we set a higher priority on displaying paramName,
     *  we get int[2] { paramNameRowAsmt, scriptRowAsmt } as a result telling how many rows will be assigned to paramName and script */
    static int[] getRowAssignmentForParamNameAndScript(int totalRowCount, int paramNameNeed, int scriptNeed, boolean priorityOnParamName) {
        int maxRowAsmt = (int) Math.min(totalRowCount - 1, Math.ceil(totalRowCount / 2.0) + 1); // maxRowAsmt: the max number of rows that can be assignmented to either paramName or script
        System.out.println("maxRowAsmt = " + maxRowAsmt);
        int paramNameRowAsmt = (int) Math.min(paramNameNeed, maxRowAsmt); // the smaller of (the number of rows paramName actually needs) and (maxRowAsmt)
        int scriptRowAsmt = (int) Math.min(totalRowCount - paramNameRowAsmt, Math.min(scriptNeed, maxRowAsmt)); // the smallest of (remaining space, the number of rows script actually needs, maxRowAsmt)
        int stillRemainingRow = totalRowCount - paramNameRowAsmt - scriptRowAsmt;
        if (stillRemainingRow >= 1) {
            scriptRowAsmt += stillRemainingRow;
//            int addedToParamName = (int) Math.ceil(stillRemainingRow / 2.0);
//            paramNameRowAsmt += addedToParamName;
//            scriptRowAsmt += (stillRemainingRow - addedToParamName);
        }
        return new int[] { paramNameRowAsmt, scriptRowAsmt };
    }

}
//class JTextPaneWithCustomTooltipLocation extends JTextPane {
//    public JTextPaneWithCustomTooltipLocation() {
//        super();
//    }
//
//    public Point getToolTipLocation(MouseEvent event) {
//        UIManager.put("ToolTip.font", UIUtil.REL_NAME_FONT);
//        return new Point(0, 0);
//    }
//}

class JTextAreaWithCustomTooltipLocation extends JTextArea {
    public JTextAreaWithCustomTooltipLocation() {
        super();
    }

    public JTextAreaWithCustomTooltipLocation(String text, int rows, int columns) {
        super(text, rows, columns);
    }

    public Point getToolTipLocation(MouseEvent event) {
        //UIManager.put("ToolTip.font", UIUtil.PARAM_NAME_FONT);
        //Point p = event.getPoint();
        return new Point(-4, -1);
    }
}

class JLabelWithCustomTooltipLocation extends JLabel {
    public JLabelWithCustomTooltipLocation(String text) {
        super(text);
    }

    public Point getToolTipLocation(MouseEvent event) {
        return new Point(7, -1);
    }
}