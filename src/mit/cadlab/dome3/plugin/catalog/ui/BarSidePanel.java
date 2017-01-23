package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public abstract class BarSidePanel extends JPanel {

    public static final int ITF_LEFT = 1;
    public static final int ITF_RIGHT = 2;
    public static final int REL_LEFT = 3;
    public static final int REL_RIGHT = 4;

    protected int panelType;
    protected ComponentReference compRef;

    public BarSidePanel(int panelType, BaseBar parentBar, ComponentReference compRef) {
        this.panelType = panelType;
        this.compRef = compRef;
        this.setLayout(new SpringLayout());
        this.setBackground(UIUtil.SIDE_PANEL_BG_COLOR);

        this.addMouseListener(new MouseListenerForBarSelection(parentBar));
    }

    public BaseBar getBar() {
        return (BaseBar) getParent();
    }

    public int getPanelType() {
        return panelType;
    }

    public Dimension getPreferredSize() {
        int colCount = this.getColumnCount();
        int rowCount = Math.max(getBar().getRowCount(true), getBar().getRowCount(false));
        int width = colCount * UIUtil.CG_CELL_WIDTH + (colCount + 1) * UIUtil.PAD_BETWEEN_CELLS;
        int height = rowCount * UIUtil.CG_CELL_HEIGHT + (rowCount + 1) * UIUtil.PAD_BETWEEN_CELLS;

        /* minimum height of relation bar */
        if (rowCount == 0) {
            height = UIUtil.CG_CELL_HEIGHT + 2 * UIUtil.PAD_BETWEEN_CELLS;
        }

        return new Dimension(width, height);
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public int getRowCount() {
        return (int) Math.ceil((double) getComponentCount() / (double) getColumnCount());
    }

    public BaseCell getCell(int rowIdx, int colIdx) {
        return (BaseCell) getComponent(rowIdx * getColumnCount() + colIdx);
    }

    public boolean isValidCellIndex(int rowIdx, int colIdx) {
        int rowCount = getRowCount();
        int colCount = getColumnCount();

        if (this.getComponentCount() == 0) {
            return false;
        }

        if (rowIdx >= rowCount || rowIdx < 0) {
            return false;
        }

        if (colIdx >= colCount || colIdx < 0) {
            return false;
        }

        if (rowIdx * colCount + colIdx >= this.getComponentCount()) {
            return false;
        }

        return true;
    }

    public int getColumnCountOfRow(int rowIdx) {
        int colCount = getColumnCount();
        if (rowIdx == (getRowCount() - 1)) {
            int odd = this.getComponentCount() % colCount;
            return (odd == 0) ? colCount : odd;
        } else if (rowIdx >= 0 && rowIdx < (getRowCount() - 1)) {
            return colCount;
        } else {
            return 0;
        }
    }

    /** returns how many columns should be drawn, which is calculated from a member var, rowCount */
    public int getColumnCount() {
        boolean isLeftPanel = true;
        if (panelType == ITF_LEFT || panelType == REL_LEFT) {
            isLeftPanel = true;
        } else {
            isLeftPanel = false;
        }

        int compoCount = getComponents().length;
        return (compoCount < getBar().getMaxColumnCount(isLeftPanel)) ? compoCount : getBar().getMaxColumnCount(isLeftPanel);
    }

    public void removeCell(Component cell) {
        this.remove(cell);
        getBar().updateLayoutConstraintsOfSidePanels(); // this will update contraints among cells
        UIUtil.updateEditorBounds(compRef);
    }

    public void addCell(Component cell) {
        this.add(cell);
        getBar().updateLayoutConstraintsOfSidePanels(); // this will validate left, right panels
        UIUtil.updateEditorBounds(compRef);
    }

    /** called after add or remove cells */
    protected void updateLayoutConstraints() {
        SpringLayout layout = (SpringLayout) getLayout();
        SpringLayout.Constraints parentCons = layout.getConstraints(this);
        Component[] components = this.getComponents();
        SpringLayout.Constraints prevCons = null;
        int rowIdx = -1;
        for (int i = 0; i < components.length; i++) {
            int colCount = this.getColumnCount();
            if (colCount == 0) { colCount = 1; } // this is a case when panel width is too small to display even one column.
            int colIdx = i % colCount;
            Component comp = components[i];
            SpringLayout.Constraints compCons = layout.getConstraints(comp);
            if (colIdx == 0) {
                compCons.setX(Spring.constant(UIUtil.PAD_BETWEEN_CELLS));
                rowIdx++;

            } else {
                compCons.setX(Spring.sum(Spring.constant(UIUtil.PAD_BETWEEN_CELLS), prevCons.getConstraint(SpringLayout.EAST)));
            }
            compCons.setY(Spring.constant((rowIdx + 1)* UIUtil.PAD_BETWEEN_CELLS + rowIdx * UIUtil.CG_CELL_HEIGHT));
            compCons.setWidth(Spring.constant(UIUtil.CG_CELL_WIDTH));
            compCons.setHeight(Spring.constant(UIUtil.CG_CELL_HEIGHT));
            prevCons = compCons;
        }

        if (prevCons != null) {
            parentCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(UIUtil.PAD_BETWEEN_CELLS), prevCons.getConstraint(SpringLayout.EAST)));
        }
    }
}
