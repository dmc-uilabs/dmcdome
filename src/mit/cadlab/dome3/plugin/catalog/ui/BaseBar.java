package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 25
 */
public abstract class BaseBar extends JPanel {

    public final static int ITF_BAR = 1;
    public final static int REL_BAR = 2;

    /* These variables always mirror values in CModel.
     * When this object is constructed, below two variable assigned from CModel.
     * When model editor changes CModel and the change affects below variables, it should update these variable */
    protected String relName; // shown by BarCenterPanel.relNameLabel. it can be either interface name or relation name
    protected String relAlias; // shown by BarCenterPanel.relAliasLabel. either interface name or relation name's var name, which is unique in an implementation
    /* end of mirrored variables */

    protected int barType;
    protected BarSidePanel leftPanel;
    protected BarSidePanel rightPanel;
    protected BarCenterPanel centerPanel;
    protected ComponentReference compRef;

    public boolean isSelected = false;
    protected int lastEditedCellIndex = 0;


    /** construct base bar */
    public BaseBar(int barType, String relName, String relAlias, ComponentReference compRef) {
        this.barType = barType;
        this.compRef = compRef;

        /* assign a new color index for this relAlias. it is initialized here because it will be used by centerPanel, leftPanel, and rightPanel in the following lines*/
        int colorIdx = compRef.getColorIndexForRelAlias(relAlias, true);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        if (barType == ITF_BAR) {
            leftPanel = new InterfaceBarLeftPanel(this, compRef);
            rightPanel = new InterfaceBarRightPanel(this, compRef);
            centerPanel = new BarCenterPanel(BarCenterPanel.ITF_CENTER, this, compRef);
        } else if (barType == REL_BAR) {
            leftPanel = new RelationBarLeftPanel(this, compRef);
            rightPanel = new RelationBarRightPanel(this, compRef);
            centerPanel = new BarCenterPanel(BarCenterPanel.REL_CENTER, this, compRef);
        }

        setRelAlias(relAlias);
        setRelName(relName);

        this.add(leftPanel);
        this.add(centerPanel);
        this.add(rightPanel);

        centerPanel.setBackground(UIUtil.REL_BG_COLORS[colorIdx]);
        if (compRef.getModelEditor().isEvaluationMode()) {
            centerPanel.setBackground(UIUtil.REL_GREY_BG_COLOR);
        }

        this.setBorder(UIUtil.BAR_NM_BORDER);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getBarType() {
        return barType;
    }

    /** relName is either interface name (ITF_BAR) or relation name (REL_BAR) */
    public String getRelName() {
        return relName;
    }

    /** bar description is either interface name (ITF_BAR) or relation name (REL_BAR) */
    public void setRelName(String relName) {
        this.relName = relName;
        //getCenterPanel().relNameLabel.setText("<html><center>" + relName + "</center></html>");
        getCenterPanel().relNameLabel.setText(relName);
        int rowCount = (int) (relName.length() / 17.0 + 0.99);
        getCenterPanel().relNameLabel.setPreferredSize(new Dimension(UIUtil.CENTER_PANEL_WIDTH, UIUtil.CELL_NAME_LABEL_HEIGHT * (rowCount <= 3 ? rowCount : 3)));
        if (relName.indexOf("/") == -1) {
            getCenterPanel().relNameLabel.setToolTipText("<html>&nbsp;<font color=#808080>name:</font> " + relName + " <br>&nbsp;<font color=#808080>alias:</font> " + relAlias + "</html>");
        } else {
            int sepIdx = relName.indexOf("/");
            String modelName = relName.substring(0, sepIdx);
            String itfName = relName.substring(sepIdx + 1);
            getCenterPanel().relNameLabel.setToolTipText("<html>&nbsp;<font color=#808080>model:</font> " + modelName + "<br>&nbsp;<font color=#808080>interface:</font> " + itfName + " <br>&nbsp;<font color=#808080>alias:</font> " + relAlias + "</html>");
        }
    }

    /** returns relAlias of interface or relation depending on barType */
    public String getRelAlias() {
        return relAlias;
    }

    public void setRelAlias(String relAlias) {
        this.relAlias = relAlias;
        //getCenterPanel().relAliasLabel.setText("<html><i>" + relAlias + "</i></html>");
        getCenterPanel().relAliasLabel.setText("<html>" + relAlias + "</html>");
    }

    /** keepPrevSelection is meaningful only if isSelected is true. otherwise, it has no effect */
    public void setSelected(boolean isSelected) {
        if (isSelected) {
            this.isSelected = isSelected;
            this.setBorder(UIUtil.BAR_HL_BORDER);
        } else {
            this.isSelected = isSelected;
            this.setBorder(UIUtil.BAR_NM_BORDER);
        }
    }

    public Dimension getBarSize() {
        int leftColumnCount = leftPanel.getColumnCount();
        int leftSidePanelWidth = 2 * UIUtil.PAD_BETWEEN_CELLS + leftColumnCount * UIUtil.CG_CELL_WIDTH + (leftColumnCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        int rightColumnCount = rightPanel.getColumnCount();
        int rightSidePanelWidth = 2 * UIUtil.PAD_BETWEEN_CELLS + rightColumnCount * UIUtil.CG_CELL_WIDTH + (rightColumnCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        int maxRowCount = Math.max(1, Math.max(this.getRowCount(true), this.getRowCount(false))); // to make max row count be at least 1
        int maxSidePanelHeight = 2 * UIUtil.PAD_BETWEEN_CELLS + maxRowCount * UIUtil.CG_CELL_HEIGHT + (maxRowCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        return new Dimension(leftSidePanelWidth + rightSidePanelWidth + UIUtil.CENTER_PANEL_WIDTH + 2 * UIUtil.BAR_BORDER_THICKNESS, maxSidePanelHeight + 2 * UIUtil.BAR_BORDER_THICKNESS);
    }

    public Dimension getLeftPanelSize() {
        int leftColumnCount = leftPanel.getColumnCount();
        int leftSidePanelWidth = 2 * UIUtil.PAD_BETWEEN_CELLS + leftColumnCount * UIUtil.CG_CELL_WIDTH + (leftColumnCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        int maxRowCount = Math.max(1, Math.max(this.getRowCount(true), this.getRowCount(false))); // to make max row count be at least 1
        int maxSidePanelHeight = 2 * UIUtil.PAD_BETWEEN_CELLS + maxRowCount * UIUtil.CG_CELL_HEIGHT + (maxRowCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        return new Dimension(leftSidePanelWidth, maxSidePanelHeight + 2 * UIUtil.BAR_BORDER_THICKNESS);
    }

    public Dimension getRightPanelSize() {
        int rightColumnCount = rightPanel.getColumnCount();
        int rightSidePanelWidth = 2 * UIUtil.PAD_BETWEEN_CELLS + rightColumnCount * UIUtil.CG_CELL_WIDTH + (rightColumnCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        int maxRowCount = Math.max(1, Math.max(this.getRowCount(true), this.getRowCount(false))); // to make max row count be at least 1
        int maxSidePanelHeight = 2 * UIUtil.PAD_BETWEEN_CELLS + maxRowCount * UIUtil.CG_CELL_HEIGHT + (maxRowCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        return new Dimension(rightSidePanelWidth, maxSidePanelHeight + 2 * UIUtil.BAR_BORDER_THICKNESS);
    }

    public Dimension getCenterPanelSize() {
        int rightColumnCount = rightPanel.getColumnCount();
        int rightSidePanelWidth = 2 * UIUtil.PAD_BETWEEN_CELLS + rightColumnCount * UIUtil.CG_CELL_WIDTH + (rightColumnCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        int maxRowCount = Math.max(1, Math.max(this.getRowCount(true), this.getRowCount(false))); // to make max row count be at least 1
        int maxSidePanelHeight = 2 * UIUtil.PAD_BETWEEN_CELLS + maxRowCount * UIUtil.CG_CELL_HEIGHT + (maxRowCount - 1) * UIUtil.PAD_BETWEEN_CELLS;
        return new Dimension(UIUtil.CENTER_PANEL_WIDTH, maxSidePanelHeight + 2 * UIUtil.BAR_BORDER_THICKNESS);
    }

    public Dimension getPreferredSize() {
        int width = leftPanel.getPreferredSize().width + centerPanel.getPreferredSize().width + rightPanel.getPreferredSize().width + this.getInsets().left + this.getInsets().right;
        int height = Math.max(leftPanel.getPreferredSize().height, rightPanel.getPreferredSize().height) + this.getInsets().top + this.getInsets().bottom;
        return new Dimension(width, height);
    }

    public Dimension getMaximumSize() { return getPreferredSize(); }
    public Dimension getMinimumSize() { return getPreferredSize(); }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public int getRowCount(boolean isLeftPanel) {
        /* when cell wrap is not enabled, row count is always one */
        if (! compRef.getImplementationEditor().isCellWrapEnabled()) {
            return 1;
        }

        if (isLeftPanel) {
            return (int) Math.ceil((double) leftPanel.getComponentCount() / (double) getMaxColumnCount(true));
        } else {
            return (int) Math.ceil((double) rightPanel.getComponentCount() / (double) getMaxColumnCount(false));
        }
    }

    /** returns maximum column count for a side panel. when there is a space for 9 columns, leftPanel's max column count is 5 and rightPanel's max column count is 4 */
    public int getMaxColumnCount(boolean isLeftPanel) {
        /* when cell wrap is not enabled, max column can be as big as it could be */
        if (! compRef.getImplementationEditor().isCellWrapEnabled()) {
            return Integer.MAX_VALUE;
        }

        Insets containerInsets = compRef.getRelationEditor().getInsets();
        int containerWidth = compRef.getImplementationEditor().getBounds().width - containerInsets.right - containerInsets.left - 14;
        int totalColumnCount = (containerWidth - UIUtil.CENTER_PANEL_WIDTH) / (UIUtil.CG_CELL_WIDTH + UIUtil.PAD_BETWEEN_CELLS);

        int colCount = -1;
        if (isLeftPanel) {
            colCount = totalColumnCount - (totalColumnCount / 2);
        } else {
            colCount = (totalColumnCount / 2);
        }

        /* if window is too small just set colCount to 1*/
        return (colCount == 0) ? 1 : colCount;

    }

    public void updateLayoutConstraintsOfSidePanels() {
        leftPanel.updateLayoutConstraints();
        rightPanel.updateLayoutConstraints();
    }

    public BaseBar getPreviousBar() {
        if (this instanceof InterfaceBar) {
            if (compRef.getRelationBarCount() > 0) {
                return compRef.getRelationBar(compRef.getRelationBarCount() - 1);
            }
            return this;
        }
        
        int barIdx = UIUtil.indexOfComponent(this);
        if (barIdx == 0) {
            return compRef.getInterfaceBar();
        } else {
            return compRef.getRelationBar(barIdx - 1);
        }
    }

    public BaseBar getNextBar() {
        if (this instanceof InterfaceBar) {
            if (compRef.getRelationBarCount() > 0) {
                return compRef.getRelationBar(0);
            }
            return this;
        }

        int barIdx = UIUtil.indexOfComponent(this);
        if (barIdx == (compRef.getRelationBarCount() - 1)) {
            return compRef.getInterfaceBar();
        } else {
            return compRef.getRelationBar(barIdx + 1);
        }
    }

    public BarSidePanel getLeftPanel() {
        return leftPanel;
    }

    public BarSidePanel getRightPanel() {
        return rightPanel;
    }

    public BarSidePanel getPanel(boolean getLeftPanel) {
        return getLeftPanel ? leftPanel : rightPanel;
    }

    public BarCenterPanel getCenterPanel() {
        return centerPanel;
    }
}
