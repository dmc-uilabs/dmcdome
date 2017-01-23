package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.runtime.RuntimeUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.Arrays;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 9.
 */
public class BaseCell extends JPanel {

    public static final int ITF_INPUT = 1;
    public static final int ITF_OUTPUT = 2;
    public static final int REL_INPUT = 3;
    public static final int REL_OUTPUT = 4;
    public static final int REL_DERIVED = 5;

    private final int DRV_CHG_LABEL_TAB = 88;
    private Icon enabledIcon = null;
    private Icon disabledIcon = null;
    private Icon disabledInputIcon = UIUtil.createImageIcon("images/inputparam_disable.gif", "disabled input parameter");
    private Icon disabledOutputIcon = UIUtil.createImageIcon("images/outputparam_disable.gif", "disabled output parameter");
    private Icon disabledDerivedIcon = UIUtil.createImageIcon("images/derivedparam_disable.gif", "disabled derived parameter");
    private Icon highlightedIcon = UIUtil.createImageIcon("images/highlight_param.gif", "affected parameter");

    /* These variables always mirror values in CModel.
     * When this object is constructed, below two variable assigned from CModel.
     * When model editor changes CModel and the change affects below variables, it should update these variable */
    protected String paramName; // shown by nameLabel
    protected String srcParamName;  // shown by srcParamLabel
    protected String dataType; // shown by typeLabel
    protected String unit; // shown by unitLabel
    protected String script; // shown by scriptDispPane
    /* end of mirrored variables */

    /* ui components */
    protected JLabel iconLabel;
    protected JTextArea nameLabel;
    protected JLabel unitLabel;
    protected JLabel typeLabel;

    private JLabel unitDrvLabel;
    private JLabel typeDrvLabel;

    private JLabel unitChgLabel;
    private JLabel typeChgLabel;

    protected JLabel srcParamLabel;
    protected JTextPane scriptDispPane;
    protected Dimension scriptEditorSize;
    protected int dotPosition = -1;
    protected ValueEditorPane valueEditorPane;

    protected Timer typeAndUnitHideTimer;

    /* ui related */
    protected int cellType;
    protected ComponentReference compRef;
    protected boolean isSelected;

    private final int UNIT_AND_TYPE_LABEL_TAB = 8;
    private MouseListenerForCellSelection mouseListenerForCellSelection;
    private MouseListenerForClickReference mouseListenerForClickReference;
    private MouseListenerForCellDragging mouseListenerForCellDragging;
    private MouseMotionListenerForCellDragging mouseMotionListenerForCellDragging;
    private MouseListenerForTypeAndUnitVisibility mouseListenerForTypeAndUnitVisibility;
    private boolean needCausalityUpdate = true;

    public BaseCell(int cellType, String relAlias, String paramName, String dataType, String unit, String srcParamName, String script, ComponentReference compRef) {

        this.cellType = cellType;
        this.paramName = paramName;
        this.dataType = dataType;
        this.unit = unit;
        this.srcParamName = srcParamName;
        this.script = script;
        this.compRef = compRef;

        this.mouseListenerForCellSelection = new MouseListenerForCellSelection(this);
        this.mouseListenerForClickReference = new MouseListenerForClickReference(this, mouseListenerForCellSelection);
        this.mouseListenerForCellDragging = new MouseListenerForCellDragging(this);
        this.mouseMotionListenerForCellDragging = new MouseMotionListenerForCellDragging(this);
        this.mouseListenerForTypeAndUnitVisibility = new MouseListenerForTypeAndUnitVisibility(this);

        initComponentsForAll();
        if (cellType == REL_OUTPUT) {
            initComponentsForRelOutputParam();
        } else if (cellType == REL_INPUT) {
            initComponentsForRelInputParam();
        } else if (cellType == REL_DERIVED) {
            initComponentsForRelDerivedParam();
        } else if (cellType == ITF_INPUT) {
            initComponentsForItfInputParam();
        } else if (cellType == ITF_OUTPUT) {
            initComponentsForItfOutputParam();
        }

        if (! (this instanceof PreviewParameterCell)) {
            initValueDispPane(relAlias + "." + paramName);
        } else {
            valueEditorPane = new ValueEditorPaneForPreviewParameterCell();
        }

        updateCellLayout(compRef.getCellConfig(), compRef.getModelEditor().isEvaluationMode());
        //setFocusable(true);
    }

    public int getCellType() {
        return cellType;
    }
    
    public boolean isOnLeftPanel() {
        return cellType == ITF_INPUT || cellType == REL_INPUT;
    }

    /** returns script display pane */
    public JTextPane getScriptDisplayPane() {
        return scriptDispPane;
    }

    /** returns value display pane */
    public ValueEditorPane getValueEditorPane() {
        return valueEditorPane;
    }

    /** scriptEditorSize stores the size of scriptEditor window. invoked when closing a script editor  */
    public void setScriptEditorSize(int width, int height) {
        this.scriptEditorSize = new Dimension(width, height);
    }

    /** invoked when open a script editor  */
    public Dimension getScriptEditorSize() {
        return this.scriptEditorSize;
    }

    public void cellClicked(boolean isControlDown) {
        compRef.getImplementationEditor().requestFocusInWindow();

        if (compRef.getImplementationEditor().isScriptEditorShown()) {
            compRef.getImplementationEditor().closeScriptEditor(true);
        }

        if (isControlDown) {
            if (compRef.isAnyBarSelected()) {
                return;
            }
            if (isSelected) {
                setSelected(false);
            } else {
                setSelected(true);
            }
        } else {
            if (! isSelected()) {
                compRef.clearBarAndCellSelection();
                setSelected(true);
            }
        }
    }

    public void setSelected(boolean isSelected) {
        if (isSelected) {
            this.isSelected = isSelected;
            this.setBorder(UIUtil.CELL_HL_BORDER);
        } else {
            this.isSelected = isSelected;
            this.setBorder(UIUtil.CELL_NM_BORDER);
        }
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
        //nameLabel.setText("<html>" + paramName + "</html>");
        nameLabel.setText(paramName);
        nameLabel.setToolTipText(paramName);
    }

    public String getParamName() {
        return paramName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getUnit() {
        return unit;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
        this.typeLabel.setText(dataType);
    }

    public void setUnit(String unit) {
        this.unit = unit;
        String desc = new CUnit(unit).getDescription();
        unitLabel.setText("<html><font color=#808080>U:</font><b><font color=#303030>" + desc + "</font></b></html>");
        unitLabel.setToolTipText(desc);
        //unitLabel.setText("<html><font color=#808080>unit:</font><b><font color=#303030>" + unit + "</font></b></html>");

    }

    public void setSourceParamName(String srcParamName) {
        this.srcParamName = srcParamName;
        srcParamLabel.setText(srcParamName);
    }

    public String getSourceParamName() {
        return srcParamName;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
        scriptDispPane.setText(script);
        this.needCausalityUpdate = true;
        validateMappingScriptReference();
    }

    public void setNeedCausalityUpdate(boolean needCausalityUpdate) {
        this.needCausalityUpdate = needCausalityUpdate;
    }

    /** check if mapping script has correct references */
    protected boolean validateMappingScriptReference() {
        try {
            /* apply change to CModel. script is stored in CMapping object of CRelationInputParameter and CInterfaceOutputParameter */
            CNamingService namingService = compRef.getCurrentCNamingService();
            String paramName = this.getParamName();
            String relAlias = this.getBar().getRelAlias();
            CParameter param = namingService.getParameter(relAlias + "." + paramName);

            Set qualifiedParamNameSet = CNamingService.createNameSet(namingService.getParameters(), true);
            String[] invalidRefs = RuntimeUtil.findInvalidReferences((String[]) qualifiedParamNameSet.toArray(new String[qualifiedParamNameSet.size()]), script);

            if (invalidRefs.length > 0) {
                setNeedCausalityUpdate(true);
                /* do not repeat setting red color and tooltip text */
                if (this.getScriptDisplayPane().getBorder() == UIUtil.SCRIPT_DISP_ER_1_BORDER || this.getScriptDisplayPane().getBorder() == UIUtil.SCRIPT_DISP_ER_2_BORDER) {
                    this.getScriptDisplayPane().setBorder(this.getScriptDisplayPane().getBorder() == UIUtil.SCRIPT_DISP_ER_1_BORDER ? UIUtil.SCRIPT_DISP_ER_2_BORDER : UIUtil.SCRIPT_DISP_ER_1_BORDER);
                    return false;
                }
                //speedup Clog.debug("the mapping script of cell '" + paramName + " has invalid references: " + Arrays.asList(invalidRefs));
                this.getScriptDisplayPane().setBorder(UIUtil.SCRIPT_DISP_ER_1_BORDER);
                ((ColorizedDocument) this.getScriptDisplayPane().getDocument()).highlightSyntax();
                StringBuffer sb = new StringBuffer();
                sb.append("<html><font color=gray>&nbsp;invalid references:</font><br><font color=black>");
                for (int i = 0; i < invalidRefs.length; i++) {
                    String invalidRef = invalidRefs[i];
                    sb.append("&nbsp;&nbsp;").append(invalidRef);
                    if (i != invalidRefs.length - 1) {
                        sb.append("<br>");
                    }
                }
                sb.append("</font></html>");
                this.getScriptDisplayPane().setToolTipText(sb.toString());
                return false;
            } else {
                if (needCausalityUpdate) {
                    if (param instanceof CRelationInputParameter) {
                        ((CRelationInputParameter) param).getMapping().setMappingScript(script, CoreUtil.getParamNames(script, namingService));
                    } else if (param instanceof CInterfaceOutputParameter) {
                        ((CInterfaceOutputParameter) param).getMapping().setMappingScript(script, CoreUtil.getParamNames(script, namingService));
                    }
                    setNeedCausalityUpdate(false);
                }
            }
        } catch (Exception e) {
            //speedup Clog.debug("the mapping script of cell '" + paramName + " has invalid references.");
            this.scriptDispPane.setBorder(UIUtil.SCRIPT_DISP_ER_1_BORDER);
            this.getScriptDisplayPane().setToolTipText(e.getMessage());
            setNeedCausalityUpdate(true);
            return false;
        }

        if (this.scriptDispPane.getBorder() != UIUtil.SCRIPT_DISP_NM_BORDER) {
            this.scriptDispPane.setBorder(UIUtil.SCRIPT_DISP_NM_BORDER);
            ((ColorizedDocument) this.getScriptDisplayPane().getDocument()).highlightSyntax();
            this.getScriptDisplayPane().setToolTipText(null);
        }
            return true;
    }

    public Dimension getPreferredSize() {
        return new Dimension(UIUtil.CG_CELL_WIDTH, UIUtil.CG_CELL_HEIGHT);
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public BaseBar getBar() {
        /* BaseCell.getParent() returns BarSidePanel, and BarSidePanel.getParent() returns BaseBar */
        return (BaseBar) this.getParent().getParent();
    }

    protected void stopTypeAndUnitHideTimer() {
        typeAndUnitHideTimer.stop();
    }

    protected void restartTypeAndUnitHideTimer() {
        typeAndUnitHideTimer.restart();
    }

    protected boolean isAllowedClickReference() {
        return compRef.getImplementationEditor().isScriptEditorShown() && ! (iconLabel.getIcon() == disabledInputIcon || iconLabel.getIcon() == disabledOutputIcon || iconLabel.getIcon() == disabledDerivedIcon);
    }

    protected void setEnabledOtherMouseListeners(boolean isEnabled) {
        mouseListenerForCellSelection.setEnabled(isEnabled);
        mouseListenerForCellDragging.setEnabled(isEnabled);
        mouseMotionListenerForCellDragging.setEnabled(isEnabled);
    }

    protected void initComponentsForAll() {
        SpringLayout ppLayout = new SpringLayout();
        this.setLayout(ppLayout);

        this.addMouseListener(mouseListenerForCellSelection);
        this.addMouseListener(mouseListenerForClickReference);
        this.addMouseListener(mouseListenerForCellDragging);
        this.addMouseMotionListener(mouseMotionListenerForCellDragging);

        typeAndUnitHideTimer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                setEnabledOtherMouseListeners(true);
                CellConfig cellConfig = compRef.getCellConfig();

                updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, compRef.getModelEditor().isTypeAndUnitQueryMode(), compRef.getModelEditor().isEvaluationMode());
            }
        });
        typeAndUnitHideTimer.setRepeats(false);

        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(12, 12));
        iconLabel.addMouseListener(mouseListenerForCellSelection);
        iconLabel.addMouseListener(mouseListenerForClickReference);
        iconLabel.addMouseListener(mouseListenerForCellDragging);
        iconLabel.addMouseMotionListener(mouseMotionListenerForCellDragging);

        nameLabel = UIUtil.createMultilineLabel(paramName, true, false);
        nameLabel.setFont(UIUtil.PARAM_NAME_FONT);
        nameLabel.setFocusable(false);
        nameLabel.setToolTipText(paramName);
        nameLabel.addMouseListener(mouseListenerForCellSelection);
        nameLabel.addMouseListener(mouseListenerForClickReference);
        nameLabel.addMouseListener(mouseListenerForCellDragging);
        nameLabel.addMouseMotionListener(mouseMotionListenerForCellDragging);
        nameLabel.addMouseListener(mouseListenerForTypeAndUnitVisibility);

        String shortDataType = "";
        if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
            shortDataType = "Int";
        } else if (CConstant.REAL_DATA_TYPE.equals(dataType)) {
            shortDataType = "Real";
        } else if (CConstant.VECTOR_DATA_TYPE.equals(dataType)) {
            shortDataType = "Vect";
        } else if (CConstant.MATRIX_DATA_TYPE.equals(dataType)) {
            shortDataType = "Matr";
        } else if (CConstant.ENUM_DATA_TYPE.equals(dataType)) {
            shortDataType = "Enum";
        } else if (CConstant.STRING_DATA_TYPE.equals(dataType)) {
            shortDataType = "Str";
        } else if (CConstant.BOOLEAN_DATA_TYPE.equals(dataType)) {
            shortDataType = "Bool";
        } else if (CConstant.FILE_DATA_TYPE.equals(dataType)) {
            shortDataType = "File";
        } else {
            throw new RuntimeException("invalid data type: " + dataType);
        }

        //typeLabel = new JLabel("<html><font color=#808080>type:</font><b><font color=#303030>" + shortDataType + "</font></b></html>");
        typeLabel = new JLabelWithCustomTooltipLocation("<html><font color=#808080>T:</font><b><font color=#303030>" + shortDataType + "</font></b></html>");
        //typeLabel.setPreferredSize(new Dimension(UIUtil.CG_CELL_WIDTH - 8, UIUtil.CELL_TYPE_AND_UNIT_LABEL_HEIGHT));
        //unitLabel = new JLabel("<html><font color=#808080>unit:</font><b><font color=#303030>" + new CUnit(unit).getDescription() + "</font></b></html>");
        //unitLabel = new JLabel("<html><font color=#808080>U:</font><b><font color=#303030>" + new CUnit(unit).getDescription() + "</font></b></html>");
        unitLabel = new JLabelWithCustomTooltipLocation("");
        setUnit(unit);

        //unitLabel.setPreferredSize(new Dimension(Short.MAX_VALUE, UIUtil.CELL_TYPE_AND_UNIT_LABEL_HEIGHT));
        unitLabel.setFont(UIUtil.PARAM_UNIT_FONT);
        typeLabel.setFont(UIUtil.PARAM_UNIT_FONT);

        unitLabel.addMouseListener(mouseListenerForCellSelection);
        unitLabel.addMouseListener(mouseListenerForClickReference);
        unitLabel.addMouseListener(mouseListenerForCellDragging);
        unitLabel.addMouseMotionListener(mouseMotionListenerForCellDragging);

        typeLabel.addMouseListener(mouseListenerForCellSelection);
        typeLabel.addMouseListener(mouseListenerForClickReference);
        typeLabel.addMouseListener(mouseListenerForCellDragging);
        typeLabel.addMouseMotionListener(mouseMotionListenerForCellDragging);

        typeLabel.setVerticalAlignment(SwingConstants.TOP);
        unitLabel.setVerticalAlignment(SwingConstants.TOP);

        this.setBackground(UIUtil.CELL_BG);
        this.setBorder(UIUtil.CELL_NM_BORDER);

        this.add(iconLabel);
        this.add(nameLabel);
        this.add(unitLabel);
        this.add(typeLabel);
    }

    /**
     * # cell components visibility rule #
     *
     * for TYPE 1 and 3

     *                                      not-always-show-unit-type            always-show-unit-type
     *                                      mouse-out      mouse-over            mouse-out
     *                                      build eval     build eval            build eval
     *
     *                                      (1)   (2)      (3)   (4)             (5)   (6)
     * cell with script    [script disp]    O     X        X     X               O     X
     *                     [eval disp]      X     O        X     X               X     O
     *                     [unit type]      X     X        O     O               O     O
     *
     *                                      (7)   (8)      (9)   (10)            (11)  (12)
     * cell with no script [script disp]    N     N        N     N               N     N
     *                     [eval disp]      X     O        X     X               X     O
     *                     [unit type]      O     X        O     O               O     O
     *
     *
     * for TYPE 2
     *
     *                                                                           (13)  (14)
     * cell with script    [script disp]                                         O     X
     *                     [eval disp]                                           X     O
     *                     [unit type]                                           O     O
     *
     *                                                                           (15)  (16)
     * cell with no script [script disp]                                         N     N
     *                     [eval disp]                                           X     O
     *                     [unit type]                                           O     O
     *
     * @param showUnitType
     * @param alignType
     * @param isMouseOverParamName
     * @param isEvaluationMode
     */
    public void updateVisibilityOfCellComponents(boolean showUnitType, int alignType, boolean isMouseOverParamName, boolean isEvaluationMode) {
        /* if mouse is over param name, always show type and unit label. other than that hide all components */
        if (alignType == 1 || alignType == 3) {
            if (cellType == ITF_OUTPUT || cellType == REL_INPUT) {
                if (! showUnitType) {
                    if (! isMouseOverParamName) {
                        if (! isEvaluationMode) {
                            /* case 1 */
                            scriptDispPane.setVisible(true);
                            valueEditorPane.setVisible(false);
                            typeLabel.setVisible(false);
                            unitLabel.setVisible(false);
                        } else {
                            /* case 2 */
                            scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(true);
                            typeLabel.setVisible(false);
                            unitLabel.setVisible(false);
                        }
                    } else {
                        if (! isEvaluationMode) {
                            /* case 3 */
                            scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(false);
                            typeLabel.setVisible(true);
                            unitLabel.setVisible(true);
                        } else {
                            /* case 4 */
                            scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(false);
                            typeLabel.setVisible(true);
                            unitLabel.setVisible(true);
                        }
                    }
                } else {
                    if (! isEvaluationMode) {
                        /* case 5 */
                        scriptDispPane.setVisible(true);
                        valueEditorPane.setVisible(false);
                        typeLabel.setVisible(true);
                        unitLabel.setVisible(true);
                    } else {
                        /* case 6 */
                        scriptDispPane.setVisible(false);
                        valueEditorPane.setVisible(true);
                        typeLabel.setVisible(true);
                        unitLabel.setVisible(true);
                    }
                }
            } else {
                if (! showUnitType) {
                    if (! isMouseOverParamName) {
                        if (! isEvaluationMode) {
                            /* case 7 */
                            //scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(false);
                            typeLabel.setVisible(true);
                            unitLabel.setVisible(true);
                        } else {
                            /* case 8 */
                            //scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(true);
                            typeLabel.setVisible(false);
                            unitLabel.setVisible(false);
                        }
                    } else {
                        if (! isEvaluationMode) {
                            /* case 9 */
                            //scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(false);
                            typeLabel.setVisible(true);
                            unitLabel.setVisible(true);
                        } else {
                            /* case 10 */
                            //scriptDispPane.setVisible(false);
                            valueEditorPane.setVisible(false);
                            typeLabel.setVisible(true);
                            unitLabel.setVisible(true);
                        }
                    }
                } else {
                    if (! isEvaluationMode) {
                        /* case 11 */
                        //scriptDispPane.setVisible(false);
                        valueEditorPane.setVisible(false);
                        typeLabel.setVisible(true);
                        unitLabel.setVisible(true);
                    } else {
                        /* case 12 */
                        //scriptDispPane.setVisible(false);
                        valueEditorPane.setVisible(true);
                        typeLabel.setVisible(true);
                        unitLabel.setVisible(true);
                    }
                }
            }
        } else if (alignType == 2) {
            if (cellType == ITF_OUTPUT || cellType == REL_INPUT) {
                if (! isEvaluationMode) {
                    /* case 13 */
                    scriptDispPane.setVisible(true);
                    valueEditorPane.setVisible(false);
                    typeLabel.setVisible(true);
                    unitLabel.setVisible(true);
                } else {
                    /* case 14 */
                    scriptDispPane.setVisible(false);
                    valueEditorPane.setVisible(true);
                    typeLabel.setVisible(true);
                    unitLabel.setVisible(true);
                }
            } else {
                if (! isEvaluationMode) {
                    /* case 15 */
                    //scriptDispPane.setVisible(false);
                    valueEditorPane.setVisible(false);
                    typeLabel.setVisible(true);
                    unitLabel.setVisible(true);
                } else {
                    /* case 16 */
                    //scriptDispPane.setVisible(false);
                    valueEditorPane.setVisible(true);
                    typeLabel.setVisible(true);
                    unitLabel.setVisible(true);
                }
            }
        }
    }

//    public void setTypeAndUnitVisibility(boolean visible) {
//        if (visible) {
//            typeLabel.setVisible(true);
//            unitLabel.setVisible(true);
//
//            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
//                if (compRef.getModelEditor().isEvaluationMode()) {
//                    scriptDispPane.setVisible(false);
//                } else {
//                    scriptDispPane.setVisible(false);
//                }
//            }
//
//            typeLabel.invalidate();
//            unitLabel.invalidate();
//        } else {
//            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
//                typeLabel.setVisible(false);
//                unitLabel.setVisible(false);
//            } else {
//                typeLabel.setVisible(true);
//                unitLabel.setVisible(true);
//            }
//
//            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
//                scriptDispPane.setVisible(true);
//                scriptDispPane.invalidate();
//            }
//
//            typeLabel.invalidate();
//            unitLabel.invalidate();
//        }
//
//        this.revalidate();
//    }

    /** this method assumes that getLineCount() returns a correct value */
    public void updateCellLayout(CellConfig cellConfig, boolean isEvaluationMode) {
        UIUtil.updateCellGeometryParameters(cellConfig);

//        int paramNameNeed = UIUtil.getLineCount(nameLabel); //nameLabel.getLineCount();
//        int scriptNeed = 0;
//        if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
//            scriptNeed = UIUtil.getLineCount(scriptDispPane);
//        }
//        System.out.println("paramNameNeed = "  + paramNameNeed + ", scriptNeed = " + scriptNeed);
//        int[] rowAssignments = UIUtil.getRowAssignmentForParamNameAndScript(cellConfig.height, paramNameNeed, scriptNeed, cellConfig.priorityOnParamName);

        this.setPreferredSize(new Dimension(UIUtil.CG_CELL_WIDTH, UIUtil.CG_CELL_HEIGHT));

        if (cellConfig.alignType == 1) {
            nameLabel.setPreferredSize(new Dimension(UIUtil.CG_NAME_LABEL_WIDTH, cellConfig.nameRow * UIUtil.CG_NAME_ROW_HEIGHT));
            nameLabel.invalidate();
            SpringLayout cellLayout = (SpringLayout) this.getLayout();

            Dimension dispPaneDim = new Dimension(UIUtil.CG_SCRIPT_DISP_WIDTH, cellConfig.scriptRow * UIUtil.CG_NAME_ROW_HEIGHT + 2);
            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                scriptDispPane.setPreferredSize(dispPaneDim);
                scriptDispPane.invalidate();
            }

            valueEditorPane.setPreferredSize(dispPaneDim);

            SpringLayout.Constraints iconLabelCons = cellLayout.getConstraints(iconLabel);
            SpringLayout.Constraints nameLabelCons = cellLayout.getConstraints(nameLabel);
            iconLabelCons.setX(Spring.constant(UIUtil.CG_ICON_LEFT_MARGIN));
            iconLabelCons.setY(Spring.constant(UIUtil.CG_ICON_TOP_MARGIN));
            nameLabelCons.setX(Spring.constant(UIUtil.CG_NAME_LEFT_MARGIN));
            nameLabelCons.setY(Spring.constant(UIUtil.CG_NAME_TOP_MARGIN));

            typeLabel.setPreferredSize(new Dimension(UIUtil.CG_TYPE_WIDTH, 1 * UIUtil.CG_TYPE_ROW_HEIGHT));
            unitLabel.setPreferredSize(new Dimension(UIUtil.CG_UNIT_WIDTH, 1 * UIUtil.CG_TYPE_ROW_HEIGHT));

            if (cellConfig.showTypeUnit) {
//                typeLabel.setVisible(true);
//                unitLabel.setVisible(true);

                SpringLayout.Constraints typeLabelCons = cellLayout.getConstraints(typeLabel);
                typeLabelCons.setX(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_LEFT_MARGIN));
                typeLabelCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

                SpringLayout.Constraints unitLabelCons = cellLayout.getConstraints(unitLabel);
                unitLabelCons.setX(cellLayout.getConstraint(SpringLayout.EAST, typeLabel));
                unitLabelCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

                if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                    SpringLayout.Constraints scriptDispCons = cellLayout.getConstraints(scriptDispPane);
                    scriptDispCons.setX(Spring.constant(UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN));
                    scriptDispCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, typeLabel));
                }

                SpringLayout.Constraints valueDispCons = cellLayout.getConstraints(valueEditorPane);
                valueDispCons.setX(Spring.constant(UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN));
                valueDispCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, typeLabel));

                typeLabel.invalidate();
                unitLabel.invalidate();

            } else {
//                if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
//                    typeLabel.setVisible(false);
//                    unitLabel.setVisible(false);
//                } else {
//                    typeLabel.setVisible(true);
//                    unitLabel.setVisible(true);
//                }

                SpringLayout.Constraints typeLabelCons = cellLayout.getConstraints(typeLabel);
                typeLabelCons.setX(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_LEFT_MARGIN));
                typeLabelCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

                SpringLayout.Constraints unitLabelCons = cellLayout.getConstraints(unitLabel);
                unitLabelCons.setX(cellLayout.getConstraint(SpringLayout.EAST, typeLabel));
                unitLabelCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

                if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                    SpringLayout.Constraints scriptDispCons = cellLayout.getConstraints(scriptDispPane);
                    scriptDispCons.setX(Spring.constant(UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN));
                    scriptDispCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));
                }

                SpringLayout.Constraints valueDispPaneCons = cellLayout.getConstraints(valueEditorPane);
                valueDispPaneCons.setX(Spring.constant(UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN));
                valueDispPaneCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

                typeLabel.invalidate();
                unitLabel.invalidate();
            }
        } else if (cellConfig.alignType == 2) {
            nameLabel.setPreferredSize(new Dimension(UIUtil.CG_NAME_LABEL_WIDTH, 1 * UIUtil.CG_NAME_ROW_HEIGHT));
            nameLabel.invalidate();
            SpringLayout cellLayout = (SpringLayout) this.getLayout();

            Dimension dispPaneDim = new Dimension(UIUtil.CG_SCRIPT_DISP_WIDTH, 2 * UIUtil.CG_NAME_ROW_HEIGHT + 2);
            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                scriptDispPane.setPreferredSize(dispPaneDim);
                scriptDispPane.invalidate();
            }

            valueEditorPane.setPreferredSize(dispPaneDim);

            SpringLayout.Constraints iconLabelCons = cellLayout.getConstraints(iconLabel);
            SpringLayout.Constraints nameLabelCons = cellLayout.getConstraints(nameLabel);
            iconLabelCons.setX(Spring.constant(UIUtil.CG_ICON_LEFT_MARGIN));
            iconLabelCons.setY(Spring.constant(UIUtil.CG_ICON_TOP_MARGIN));
            nameLabelCons.setX(Spring.constant(UIUtil.CG_NAME_LEFT_MARGIN));
            nameLabelCons.setY(Spring.constant(UIUtil.CG_NAME_TOP_MARGIN));

//            typeLabel.setVisible(true);
//            unitLabel.setVisible(true);
            typeLabel.setPreferredSize(new Dimension(UIUtil.CG_TYPE_WIDTH, 1 * UIUtil.CG_TYPE_ROW_HEIGHT));
            unitLabel.setPreferredSize(new Dimension(UIUtil.CG_UNIT_WIDTH, 1 * UIUtil.CG_TYPE_ROW_HEIGHT));

            SpringLayout.Constraints typeLabelCons = cellLayout.getConstraints(typeLabel);
            typeLabelCons.setX(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_LEFT_MARGIN));
            typeLabelCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

            SpringLayout.Constraints unitLabelCons = cellLayout.getConstraints(unitLabel);
            unitLabelCons.setX(cellLayout.getConstraint(SpringLayout.EAST, typeLabel));
            unitLabelCons.setY(cellLayout.getConstraint(SpringLayout.SOUTH, nameLabel));

            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                SpringLayout.Constraints scriptDispCons = cellLayout.getConstraints(scriptDispPane);
                scriptDispCons.setX(Spring.sum(cellLayout.getConstraint(SpringLayout.EAST, nameLabel), Spring.constant(UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN)));
                scriptDispCons.setY(Spring.constant(UIUtil.CG_SCRIPT_DISP_TOP_MARGIN));
            }

            SpringLayout.Constraints valueDispCons = cellLayout.getConstraints(valueEditorPane);
            valueDispCons.setX(Spring.sum(cellLayout.getConstraint(SpringLayout.EAST, nameLabel), Spring.constant(UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN)));
            valueDispCons.setY(Spring.constant(UIUtil.CG_SCRIPT_DISP_TOP_MARGIN));

            typeLabel.invalidate();
            unitLabel.invalidate();
        } else if (cellConfig.alignType == 3) {
            nameLabel.setPreferredSize(new Dimension(UIUtil.CG_NAME_LABEL_WIDTH, 1 * UIUtil.CG_NAME_ROW_HEIGHT));
            nameLabel.invalidate();
            SpringLayout cellLayout = (SpringLayout) this.getLayout();

            Dimension dispPaneDim = new Dimension(UIUtil.CG_SCRIPT_DISP_WIDTH, 1 * UIUtil.CG_NAME_ROW_HEIGHT + 2);
            if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                scriptDispPane.setPreferredSize(dispPaneDim);
                scriptDispPane.invalidate();
            }

            valueEditorPane.setPreferredSize(dispPaneDim);

            SpringLayout.Constraints iconLabelCons = cellLayout.getConstraints(iconLabel);
            SpringLayout.Constraints nameLabelCons = cellLayout.getConstraints(nameLabel);
            iconLabelCons.setX(Spring.constant(UIUtil.CG_ICON_LEFT_MARGIN));
            iconLabelCons.setY(Spring.constant(UIUtil.CG_ICON_TOP_MARGIN));
            nameLabelCons.setX(Spring.constant(UIUtil.CG_NAME_LEFT_MARGIN));
            nameLabelCons.setY(Spring.constant(UIUtil.CG_NAME_TOP_MARGIN));

            typeLabel.setPreferredSize(new Dimension(UIUtil.CG_TYPE_WIDTH, 1 * UIUtil.CG_TYPE_ROW_HEIGHT));
            unitLabel.setPreferredSize(new Dimension(UIUtil.CG_UNIT_WIDTH, 1 * UIUtil.CG_TYPE_ROW_HEIGHT));

            if (cellConfig.showTypeUnit) {
//                typeLabel.setVisible(true);
//                unitLabel.setVisible(true);

                SpringLayout.Constraints typeLabelCons = cellLayout.getConstraints(typeLabel);
                typeLabelCons.setX(Spring.sum(Spring.constant(UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN), cellLayout.getConstraint(SpringLayout.EAST, nameLabel)));
                typeLabelCons.setY(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_TOP_MARGIN));

                SpringLayout.Constraints unitLabelCons = cellLayout.getConstraints(unitLabel);
                unitLabelCons.setX(cellLayout.getConstraint(SpringLayout.EAST, typeLabel));
                unitLabelCons.setY(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_TOP_MARGIN));

                if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                    SpringLayout.Constraints scriptDispCons = cellLayout.getConstraints(scriptDispPane);
                    scriptDispCons.setX(Spring.sum(Spring.constant(UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN), cellLayout.getConstraint(SpringLayout.EAST, unitLabel)));
                    scriptDispCons.setY(Spring.constant(UIUtil.CG_SCRIPT_DISP_TOP_MARGIN));
                }

                SpringLayout.Constraints valueDispCons = cellLayout.getConstraints(valueEditorPane);
                valueDispCons.setX(Spring.sum(Spring.constant(UIUtil.CG_SCRIPT_DISP_SIDE_MARGIN), cellLayout.getConstraint(SpringLayout.EAST, unitLabel)));
                valueDispCons.setY(Spring.constant(UIUtil.CG_SCRIPT_DISP_TOP_MARGIN));

                typeLabel.invalidate();
                unitLabel.invalidate();
            } else {
//                if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
//                    typeLabel.setVisible(false);
//                    unitLabel.setVisible(false);
//                } else {
//                    typeLabel.setVisible(true);
//                    unitLabel.setVisible(true);
//                }

                SpringLayout.Constraints typeLabelCons = cellLayout.getConstraints(typeLabel);
                typeLabelCons.setX(Spring.sum(Spring.constant(UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN), cellLayout.getConstraint(SpringLayout.EAST, nameLabel)));
                typeLabelCons.setY(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_TOP_MARGIN));

                SpringLayout.Constraints unitLabelCons = cellLayout.getConstraints(unitLabel);
                unitLabelCons.setX(cellLayout.getConstraint(SpringLayout.EAST, typeLabel));
                unitLabelCons.setY(Spring.constant(UIUtil.CG_TYPE_AND_UNIT_TOP_MARGIN));

                if (cellType == REL_INPUT || cellType == ITF_OUTPUT) {
                    SpringLayout.Constraints scriptDispCons = cellLayout.getConstraints(scriptDispPane);
                    scriptDispCons.setX(Spring.sum(Spring.constant(UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN), cellLayout.getConstraint(SpringLayout.EAST, nameLabel)));
                    scriptDispCons.setY(Spring.constant(UIUtil.CG_SCRIPT_DISP_TOP_MARGIN));
                }

                SpringLayout.Constraints valueDispCons = cellLayout.getConstraints(valueEditorPane);
                valueDispCons.setX(Spring.sum(Spring.constant(UIUtil.CG_ICON_AND_NAME_SIDE_MARGIN), cellLayout.getConstraint(SpringLayout.EAST, nameLabel)));
                valueDispCons.setY(Spring.constant(UIUtil.CG_SCRIPT_DISP_TOP_MARGIN));

                typeLabel.invalidate();
                unitLabel.invalidate();
            }
        }
        this.invalidate();

        updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, compRef.getModelEditor().isTypeAndUnitQueryMode(), isEvaluationMode);
    }

    public void setNormalIcon() {
        iconLabel.setIcon(enabledIcon);
    }

    public void setHighlightIcon() {
        iconLabel.setIcon(highlightedIcon);
    }

    public void setDisabledIcon() {
        iconLabel.setIcon(disabledIcon);
    }

    private void initComponentsForRelOutputParam() {
        enabledIcon = UIUtil.createImageIcon("images/outputparam.gif", "relation output parameter");
        disabledIcon = disabledOutputIcon;
        iconLabel.setIcon(enabledIcon);
        //initDeriveLabels();
    }

    private void initComponentsForRelInputParam() {
        enabledIcon = UIUtil.createImageIcon("images/inputparam.gif", "relation input parameter");
        disabledIcon = disabledInputIcon;
        iconLabel.setIcon(enabledIcon);
        initScriptDispPane();
    }

    private void initComponentsForRelDerivedParam() {
        enabledIcon = UIUtil.createImageIcon("images/derivedparam.gif", "derived parameter");
        disabledIcon = disabledDerivedIcon;
        iconLabel.setIcon(enabledIcon);

        SpringLayout ppLayout = (SpringLayout) this.getLayout();
        srcParamLabel = new JLabel("<html><font color=#808080>src:</font><font color=#303030>" + srcParamName + "</font></html>");
        unitChgLabel = new JLabel(UIUtil.createImageIcon("images/chg_arrow.gif", "change unit"));
        unitChgLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { unitChgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }

            public void mouseClicked(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) { unitChgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
        });
        typeChgLabel = new JLabel(UIUtil.createImageIcon("images/chg_arrow.gif", "change type"));
        typeChgLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { typeChgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }

            public void mouseClicked(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) { typeChgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
        });

        this.setBackground(UIUtil.CELL_BG);
        this.setBorder(UIUtil.CELL_NM_BORDER);

        this.add(srcParamLabel);
        this.add(unitChgLabel);
        this.add(typeChgLabel);

        srcParamLabel.setFont(UIUtil.PARAM_UNIT_FONT);

        SpringLayout.Constraints nameLabelCons = ppLayout.getConstraints(nameLabel);
        SpringLayout.Constraints typeLabelCons = ppLayout.getConstraints(typeLabel);
        SpringLayout.Constraints unitLabelCons = ppLayout.getConstraints(unitLabel);

        SpringLayout.Constraints typeChgLabelCons = ppLayout.getConstraints(typeChgLabel);
        typeChgLabelCons.setX(Spring.constant(DRV_CHG_LABEL_TAB));
        typeChgLabelCons.setY(Spring.sum(Spring.constant(5), nameLabelCons.getConstraint(SpringLayout.SOUTH)));

        SpringLayout.Constraints unitChgLabelCons = ppLayout.getConstraints(unitChgLabel);
        unitChgLabelCons.setX(Spring.constant(DRV_CHG_LABEL_TAB));
        unitChgLabelCons.setY(Spring.sum(Spring.constant(5), typeLabelCons.getConstraint(SpringLayout.SOUTH)));

        SpringLayout.Constraints srcLabelCons = ppLayout.getConstraints(srcParamLabel);
        srcLabelCons.setX(Spring.constant(UNIT_AND_TYPE_LABEL_TAB));
        srcLabelCons.setY(Spring.sum(Spring.constant(0), unitLabelCons.getConstraint(SpringLayout.SOUTH)));
    }

    private void initComponentsForItfInputParam() {
        enabledIcon = UIUtil.createImageIcon("images/inputparam.gif", "interface input parameter");
        disabledIcon = disabledInputIcon;
        iconLabel.setIcon(enabledIcon);  // no icon fix
    }

    private void initComponentsForItfOutputParam() {
        enabledIcon = UIUtil.createImageIcon("images/outputparam.gif", "interface output parameter");
        disabledIcon = disabledOutputIcon;
        iconLabel.setIcon(enabledIcon);
        initScriptDispPane();
    }

    private void initValueDispPane(String qualifiedParamName) {
        if (CConstant.REAL_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new RealValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.INTEGER_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new IntegerValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.STRING_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new StringValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.BOOLEAN_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new BooleanValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.FILE_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new FileValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.ENUM_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new EnumValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.MATRIX_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new MatrixValueEditorPane(qualifiedParamName, compRef);
        } else if (CConstant.VECTOR_DATA_TYPE.equals(dataType)) {
            valueEditorPane = new VectorValueEditorPane(qualifiedParamName, compRef);
        }

        //valueEditorPane = UIUtil.createMultilineLabel("", true, true);

        //valueEditorPane.setEditable(true);
        valueEditorPane.setFocusable(true);
        valueEditorPane.setBackground(UIUtil.VALUE_EDITOR_BG);
        valueEditorPane.setFont(UIUtil.PARAM_EDITOR_FONT);
        valueEditorPane.setBorder(UIUtil.VALUE_EDITOR_NORMAL_BORDER);
//        valueEditorPane.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent event) {
//                System.out.println("mouse clicked");
//            }
//        });
        valueEditorPane.setFont(UIUtil.PARAM_EDITOR_FONT);
        //valueEditorPane.setText("0");
        valueEditorPane.setVisible(false);

        this.add(valueEditorPane);
    }

    private void initScriptDispPane() {
        scriptDispPane = new JTextPane(new ColorizedDocument(new CellScriptDelimiterList(compRef))); // new JEditorPane("text/html", "");
        scriptDispPane.setEditable(false);
        scriptDispPane.setFocusable(false);
        scriptDispPane.setFont(UIUtil.PARAM_EDITOR_FONT);
        scriptDispPane.setBorder(UIUtil.SCRIPT_DISP_NM_BORDER);
        scriptDispPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (compRef.getImplementationEditor().isScriptEditorShown()) {
                    compRef.getImplementationEditor().closeScriptEditor(true);
                }
                scriptDispPane.getCaret().setMagicCaretPosition(event.getPoint());
                compRef.getImplementationEditor().showScriptEditor(BaseCell.this, scriptDispPane.getCaret().getDot());
                /* show cell completion */
                if (event.isControlDown()) {
                    if (event.isShiftDown()) {
                        compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().setText("");
                    }
                    compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().getActionMap().get("showCellCompletion").actionPerformed(new ActionEvent(this, 0, "show cell completion by clicking with control-down"));
                } else if (! event.isShiftDown()) {
                    //compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().setText("");
                    // sometimes selecting-all can be more user-friendly; in that case, uncomment the below.
                    // compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane().selectAll();
                }
            }
        });
        scriptDispPane.setFont(UIUtil.PARAM_EDITOR_FONT);
        scriptDispPane.setText(script);

        this.add(scriptDispPane);
        /* set the default size of script editor */
        adjustScriptEditorSize();
    }

    protected void adjustScriptEditorSize() {
        /* init script editor size */
        if (scriptEditorSize == null) {
            this.scriptEditorSize = new Dimension(0, 0);
        }

        /* make script editor wider than the minimum width */
        if (UIUtil.CG_SCRIPT_DISP_WIDTH < UIUtil.CG_SCRIPT_EDITOR_MIN_WIDTH) {
            scriptEditorSize.width = UIUtil.CG_SCRIPT_EDITOR_MIN_WIDTH;
        } else {
            scriptEditorSize.width = UIUtil.CG_SCRIPT_DISP_WIDTH;
        }

        if (compRef.getCellConfig().scriptRow == 1) {
            scriptEditorSize.height = UIUtil.CG_SCRIPT_DISP_HEIGHT * 2 + UIUtil.SCRIPT_EDITOR_BT_HEIGHT;
        } else {
            scriptEditorSize.height = UIUtil.CG_SCRIPT_DISP_HEIGHT * 1 + UIUtil.SCRIPT_EDITOR_BT_HEIGHT;
        }

//
//        /* enlarge script editor width as needed */
//        if (scriptEditorSize.width < UIUtil.CG_SCRIPT_DISP_WIDTH) {
//            scriptEditorSize.width = UIUtil.CG_SCRIPT_DISP_WIDTH;
//        }
//
//        /* enlarge script editor height as needed */
//        if (scriptEditorSize.height < UIUtil.CG_SCRIPT_DISP_HEIGHT + UIUtil.SCRIPT_EDITOR_BT_HEIGHT) {
//            scriptEditorSize.height = UIUtil.CG_SCRIPT_DISP_HEIGHT + UIUtil.SCRIPT_EDITOR_BT_HEIGHT;
//        }
    }

    private void initDeriveLabels() {
        SpringLayout ppLayout = (SpringLayout) this.getLayout();

        typeDrvLabel = new JLabel(UIUtil.createImageIcon("images/drv_plus.gif", "change type"));
        typeDrvLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                typeDrvLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                RelationBar relationBar = (RelationBar) getBar();

                String srcParamName = paramName;
                String derivedParamName = paramName + "_" + (relationBar.getRelationDerivedCellCount(srcParamName) + 1);
                relationBar.addRelationDerivedCell(derivedParamName, "int", "km", srcParamName);
            }

            public void mouseExited(MouseEvent e) {
                typeDrvLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        unitDrvLabel = new JLabel(UIUtil.createImageIcon("images/drv_plus.gif", "change unit"));
        unitDrvLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                unitDrvLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                RelationBar relationBar = (RelationBar) getBar();

                String srcParamName = paramName;
                String derivedParamName = paramName + "_" + (relationBar.getRelationDerivedCellCount(srcParamName) + 1);
                relationBar.addRelationDerivedCell(derivedParamName, "Real", "cm", srcParamName);
            }

            public void mouseExited(MouseEvent e) {
                unitDrvLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        this.add(unitDrvLabel);
        this.add(typeDrvLabel);

        SpringLayout.Constraints nameLabelCons = ppLayout.getConstraints(nameLabel);
        SpringLayout.Constraints typeLabelCons = ppLayout.getConstraints(typeLabel);

        SpringLayout.Constraints typeChgLabelCons = ppLayout.getConstraints(typeDrvLabel);
        typeChgLabelCons.setX(Spring.constant(DRV_CHG_LABEL_TAB));
        typeChgLabelCons.setY(Spring.sum(Spring.constant(5), nameLabelCons.getConstraint(SpringLayout.SOUTH)));

        SpringLayout.Constraints unitChgLabelCons = ppLayout.getConstraints(unitDrvLabel);
        unitChgLabelCons.setX(Spring.constant(DRV_CHG_LABEL_TAB));
        unitChgLabelCons.setY(Spring.sum(Spring.constant(5), typeLabelCons.getConstraint(SpringLayout.SOUTH)));
    }


    /** returns row index and column index in the form of int array. int [2] { rowIdx, columnIdx } of this cell */
    public int[] getRowAndColumnIndex() {
        int[] ret = new int [2];
        int cellIndex = UIUtil.indexOfComponent(this);
        int colCount = ((BarSidePanel) this.getParent()).getColumnCount();

        int rowIndex = cellIndex / colCount;
        int colIndex = cellIndex % colCount;
        ret [0] = rowIndex;
        ret [1] = colIndex;

        return ret;
    }
}

class MouseListenerForTypeAndUnitVisibility extends MouseAdapter {
    BaseCell cell;

    public MouseListenerForTypeAndUnitVisibility(BaseCell cell) {
        this.cell = cell;
    }

    public void mouseEntered(MouseEvent e) {
        CellConfig cellConfig = cell.getComponentReference().getCellConfig();
        if (! cellConfig.showTypeUnit) {
            cell.stopTypeAndUnitHideTimer();
        }
        cell.updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, true, cell.getComponentReference().getModelEditor().isEvaluationMode());
    }


    public void mouseExited(MouseEvent e) {
        CellConfig cellConfig = cell.getComponentReference().getCellConfig();
        if (! cellConfig.showTypeUnit) {
            cell.restartTypeAndUnitHideTimer();
        }
    }
}

class MouseListenerForClickReference extends MouseAdapter {
    ComponentReference compRef;
    MouseListenerForCellSelection mouseListenerForCellSelection;
    BaseCell cell;

    MouseListenerForClickReference(BaseCell cell, MouseListenerForCellSelection mouseListenerForCellSelection) {
        this.cell = cell;
        this.compRef = cell.getComponentReference();
        this.mouseListenerForCellSelection = mouseListenerForCellSelection;
    }

    public void mouseEntered(MouseEvent e) {
        CellConfig cellConfig = compRef.getCellConfig();

//        if (! cellConfig.showTypeUnit) {
//            cell.stopTypeAndUnitHideTimer();
//        }

        if (cell.isAllowedClickReference()) {
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cell.nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            cell.iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cell.setEnabledOtherMouseListeners(false);
        } else if (compRef.getImplementationEditor().isScriptEditorShown()) {
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cell.nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            cell.iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cell.setEnabledOtherMouseListeners(false);
        } else {
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cell.nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cell.setEnabledOtherMouseListeners(true);
//            cell.iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

//        cell.updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, true, compRef.getModelEditor().isEvaluationMode());
    }

    public void mouseClicked(MouseEvent e) {
        if (cell.isAllowedClickReference()) {
            JTextPane textPane = compRef.getImplementationEditor().getScriptEditor().getScriptEditorPane();
            int[] caretRange = UIUtil.getCaretRange(textPane);
            try {
                textPane.getDocument().remove(caretRange[0], caretRange[1] - caretRange[0]);
                String insertedStr = cell.getBar().getRelAlias();
                textPane.getDocument().insertString(caretRange[0], insertedStr + "." + cell.paramName, SimpleAttributeSet.EMPTY);
            } catch (BadLocationException ble) { ble.printStackTrace(); }
        }

        if (e.getClickCount() == 2) {
            StringSelection stringSelection = new StringSelection(cell.paramName);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, new ClipboardOwner() {
                public void lostOwnership(Clipboard clipboard, Transferable transferable) { }
            });
        }
    }

    public void mouseExited(MouseEvent e) {
//        CellConfig cellConfig = compRef.getCellConfig();
//
//        if (! cellConfig.showTypeUnit) {
//            cell.restartTypeAndUnitHideTimer();
//        }
        cell.setEnabledOtherMouseListeners(true);

    }
}