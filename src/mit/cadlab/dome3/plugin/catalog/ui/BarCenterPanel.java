package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * User: Sangmok
 * Date: 2006. 1. 19
 */
public class BarCenterPanel extends JPanel {

    public static final int ITF_CENTER = 1;
    public static final int REL_CENTER = 2;

    protected JLabel iconLabel;
    protected JLabel modelNameLabel;
    protected JTextPane relNameLabel;
    protected JLabel relAliasLabel;
    private JButton editButton;

    private ComponentReference compRef;
    private int panelType;
    private boolean initialized = false;
    private MouseListener mouseListenerForBarDragging;
    private MouseMotionListener mouseMotionListenerForBarDragging;
    private MouseListener mouseListenerForSelection;

    public BarCenterPanel(int panelType, BaseBar parentBar, ComponentReference compRef) {
        this.panelType = panelType;
        this.compRef = compRef;

        this.mouseListenerForSelection = new MouseListenerForBarSelection(parentBar);
        this.mouseListenerForBarDragging = new MouseListenerForBarDragging(parentBar);
        this.mouseMotionListenerForBarDragging = new MouseMotionListenerForBarDragging(parentBar);

        /* parent bar needed because construction happens before it is added to a parent container */
        initComponents(parentBar);
        this.initialized = true;

    }

    public BaseBar getBar() {
        return (BaseBar) getParent();
    }

    public void updateRelNameLabelHeight() {
        if (! initialized) {
            return;
        }

        int lineCount = UIUtil.getLineCount(relNameLabel);
        relNameLabel.setPreferredSize(new Dimension(UIUtil.CENTER_PANEL_WIDTH - 8, UIUtil.CELL_NAME_LABEL_HEIGHT * ((lineCount > 3) ? 3 : lineCount)));

        SpringLayout layout = (SpringLayout) getLayout();
        SpringLayout.Constraints relNameCons = layout.getConstraints(relNameLabel);
        Insets insets = this.getInsets();
        int availableHeight = (int) (this.getPreferredSize().getHeight() - insets.top - insets.bottom - relAliasLabel.getHeight() - UIUtil.GAP_BETWEEN_REL_NAME_AND_REL_ALIAS);
        int neededHeight = UIUtil.CELL_NAME_LABEL_HEIGHT * ((lineCount > 4) ? 4 : lineCount);
        relNameCons.setHeight(Spring.constant(Math.min(neededHeight, availableHeight)));

//        SpringLayout.Constraints relAliasCons = ((SpringLayout) getLayout()).getConstraints(relAliasLabel);
//        relAliasCons.setY(layout.getConstraint(SpringLayout.SOUTH, relNameLabel));
//        relAliasCons.setHeight(Spring.constant(18));
//        relAliasLabel.setVisible(true);
    }

    public void initComponents(BaseBar parentBar) {
        iconLabel = new JLabel(UIUtil.createImageIcon("images/relation_arrow.gif", "relation"), JLabel.CENTER);
        iconLabel.setPreferredSize(new Dimension(UIUtil.CENTER_PANEL_WIDTH, 12));

        //relNameLabel = new JLabel(parentBar.getRelName(), UIUtil.createImageIcon("images/relation_arrow.gif", "relation"), JLabel.CENTER);
        relNameLabel = UIUtil.createCenterAlignedMultilineLabel(parentBar.getRelName(), true, false, UIUtil.REL_NAME_FONT);
//        relNameLabel.setVerticalTextPosition(JLabel.BOTTOM);
//        relNameLabel.setHorizontalTextPosition(JLabel.CENTER);
//        relNameLabel.setFont(UIUtil.REL_NAME_FONT);
        relNameLabel.setPreferredSize(new Dimension(UIUtil.CENTER_PANEL_WIDTH - 8, UIUtil.CELL_NAME_LABEL_HEIGHT * 2));
        //relNameLabel.setVerticalAlignment(SwingConstants.TOP);

        //relNameLabel.setIconTextGap(2);

        //relNameLabel.setVerticalAlignment(JLabel.TOP);
        relAliasLabel = new JLabel("", JLabel.CENTER);
        relAliasLabel.setFont(UIUtil.REL_ALIAS_FONT);
        relAliasLabel.setPreferredSize(new Dimension(UIUtil.CENTER_PANEL_WIDTH, 12));

        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        //this.add(iconLabel);
        this.add(relNameLabel);
        this.add(relAliasLabel);


        //editBtCons.setHeight(Spring.constant(UIUtil.CENTER_BT_HEIGHT));

        //editButton = new JButton(UIUtil.createImageIcon("images/center_nm_edit.gif", "edit"));
        editButton = new JButton("edit...");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (panelType == ITF_CENTER) {
                    compRef.getInterfaceEditor().editInterface();
                } else {
                    compRef.getRelationEditor().editRelation((RelationBar) getBar());
                }
            }
        });

        editButton.setOpaque(false);
        editButton.setFont(UIUtil.BAR_CENTER_FONT);
        //editButton.setBorder(null);
        //editButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        editButton.setBorder(BorderFactory.createEtchedBorder(1));
        editButton.setFocusable(false);

        //this.add(editButton);

        SpringLayout.Constraints parentCons = layout.getConstraints(this);
//        SpringLayout.Constraints iconCons = layout.getConstraints(iconLabel);
        SpringLayout.Constraints relNameCons = layout.getConstraints(relNameLabel);
        SpringLayout.Constraints relAliasCons = layout.getConstraints(relAliasLabel);
//        iconCons.setX(Spring.constant(0));
//        iconCons.setY(Spring.constant(6));
//        iconCons.setWidth(Spring.constant(UIUtil.CENTER_PANEL_WIDTH));

        relNameCons.setX(Spring.constant(3));
        relNameCons.setY(Spring.constant(0));
        relNameCons.setWidth(Spring.constant(UIUtil.CENTER_PANEL_WIDTH - 8));
        relAliasCons.setX(Spring.constant(0));
        //relAliasCons.setY(Spring.constant(36));
        //relAliasCons.setY(Spring.sum(Spring.constant(2), layout.getConstraint(SpringLayout.SOUTH, relNameLabel)));
        relAliasCons.setY(layout.getConstraint(SpringLayout.SOUTH, relNameLabel));
        //relAliasCons.setWidth(Spring.constant(UIUtil.CENTER_PANEL_WIDTH));

        SpringLayout.Constraints editBtCons = layout.getConstraints(editButton);
        editBtCons.setX(Spring.constant((UIUtil.CENTER_PANEL_WIDTH - 4 - UIUtil.CENTER_BT_WIDTH) / 2));
        editBtCons.setY(Spring.sum(Spring.constant(UIUtil.GAP_BETWEEN_REL_NAME_AND_REL_ALIAS), layout.getConstraint(SpringLayout.SOUTH, relAliasLabel)));
        editBtCons.setWidth(Spring.constant(UIUtil.CENTER_BT_WIDTH));

        this.setOpaque(true);

//        CompoundBorder compBorder = new CompoundBorder(new LineBorder(UIUtil.SIDE_PANEL_BG_COLOR, UIUtil.PAD_BETWEEN_CELLS), new BevelBorder(BevelBorder.RAISED));
        CompoundBorder compBorder = new CompoundBorder(new LineBorder(UIUtil.SIDE_PANEL_BG_COLOR, UIUtil.PAD_BETWEEN_CELLS), new MatteBorder(1, 1, 1, 1, UIUtil.BAR_BORDER_NM_COLOR));
        this.setBorder(compBorder);
//        if (parentBar.getBarType() == BaseBar.ITF_BAR) {
//            this.setToolTipText("double click to edit the interface");
//        } else {
//            this.setToolTipText("double click to edit this relation.");
//        }

        relNameLabel.addMouseListener(mouseListenerForSelection);
        relAliasLabel.addMouseListener(mouseListenerForSelection);
        this.addMouseListener(mouseListenerForSelection);

        if (parentBar.getBarType() == BaseBar.REL_BAR) {
            relNameLabel.addMouseListener(mouseListenerForBarDragging);
            relNameLabel.addMouseMotionListener(mouseMotionListenerForBarDragging);

            relAliasLabel.addMouseListener(mouseListenerForBarDragging);
            relAliasLabel.addMouseMotionListener(mouseMotionListenerForBarDragging);

            this.addMouseListener(mouseListenerForBarDragging);
            this.addMouseMotionListener(mouseMotionListenerForBarDragging);
        }
    }

    /**
     * when double clicked, all selection is cleared and relation editor or interface editor pops up.
     * when clicked, use control down to determine if previous selection to be cleared and highlight the selected bar
     */
    public void clicked(boolean isDoubleClicked, boolean isControlDown) {
        compRef.getImplementationEditor().requestFocusInWindow();
        compRef.getModelEditor().stopExpandingSelection();

        if (isDoubleClicked) {
            /* clear selection and close script editor */
//            compRef.clearBarAndCellSelection();
            if (compRef.getImplementationEditor().isScriptEditorShown()) {
                compRef.getImplementationEditor().closeScriptEditor(true);
            }

            /* opens relation or interface editor */
            if (panelType == ITF_CENTER) {
                compRef.getInterfaceEditor().editInterface();
            } else {
                compRef.getRelationEditor().editRelation((RelationBar) getBar());
            }
            return;
        } else {
            if (panelType == ITF_CENTER) {
                //compRef.clearBarAndCellSelection();
                /* close script editor */
                if (compRef.getImplementationEditor().isScriptEditorShown()) {
                    compRef.getImplementationEditor().closeScriptEditor(true);
                }
                compRef.clearBarAndCellSelection();
                getBar().setSelected(true);
            } else {
                /* close script editor */
                if (compRef.getImplementationEditor().isScriptEditorShown()) {
                    compRef.getImplementationEditor().closeScriptEditor(true);
                }
                if (isControlDown) {
                    if (compRef.isAnyCellSelected()) {
                        BaseCell[] cells = compRef.getSelectedCells();
                        for (int i = 0; i < cells.length; i++) {
                            BaseCell cell = cells[i];
                        }
                        return;
                    }
                    if (getBar().isSelected) {
                        getBar().setSelected(false);
                    } else {
                        getBar().setSelected(true);
                    }
                } else {
                    compRef.clearBarAndCellSelection();
                    getBar().setSelected(true);
                }
            }
        }
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public int getPanelType() {
        return panelType;
    }

    public Dimension getPreferredSize() {
        int height = Math.max(getBar().getLeftPanel().getPreferredSize().height, getBar().getRightPanel().getPreferredSize().height);
        return new Dimension(UIUtil.CENTER_PANEL_WIDTH, height);
    }

    public Dimension getMinimumSize() { return getPreferredSize(); }
    public Dimension getMaximumSize() { return getPreferredSize(); }

}
