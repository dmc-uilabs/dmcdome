package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * User: Sangmok Han
 * Date: 2006. 2. 15.
 */
public class InterfaceDialog extends JDialog {
    private ComponentReference compRef;
    private JPanel localRelPanel;
    private JPanel remoteRelPanel;

    private static int DIALOG_WIDTH = 610;
    private static int DIALOG_LR_HEIGHT = 320;
    private static int FORM_LR_HEIGHT = 200;
    private static int DIALOG_RR_HEIGHT = 570;
    private static int FORM_RR_HEIGHT = 400;
    private static int RADIOBUTTON_PANEL_HEIGHT = 30;

    private static int ROW_HEIGHT = 27;
    private static int LEFT_COLUMN_WIDTH = 65;
    private static int RIGHT_COLUMN_WIDTH = 500 - 6;
    private static int GAP_BETWEEN_COLUMNS = 10;
    private static int LEFT_MARGIN = 10;
    private static int GAP_BETWEEN_COMPS = 5;
    private static int ADD_REMOVE_BUTTON_WIDTH = 50;
    private static int ADD_REMOVE_BUTTON_TAB = 25;

    private JLabel implNameLb;
    private JTextField implNameFd;

//    private JToggleButton itfLockBt;

    private JLabel itfNameLb;
    private JTextField itfNameFd;

    private JButton okayLcBt;
    private JButton cancelLcBt;

    private JButton okayRtBt;
    private JButton cancelRtBt;

    private JButton addIParamBt;
    private JButton removeIParamBt;
    private JButton addOParamBt;
    private JButton removeOParamBt;


    private JButton checkAllBt;
    private JButton uncheckAllBt;
    private JButton checkSmartBt ;


    private DependencyCheckPanel implDepCheckPanel;
    private JLabel implDepCheckLb;

    private JLabel inputParamLb;
    private JLabel outputParamLb;
    protected  ParameterDefinitionPanel iParamDefPanel;
    protected ParameterDefinitionPanel oParamDefPanel;

    private Container contentPane;

    private DependencyCheckPanel depCheckPanel;
    private JLabel depCheckLb;
    private boolean isSubmitted;

    private JRadioButton localRelRb;
    private JRadioButton remoteRelRb;
    private boolean isForEditing = false;

//    private static Icon OPEN_LOCK = UIUtil.createImageIcon("images/LockOpen.gif");
//    private static Icon CLOSED_LOCK = UIUtil.createImageIcon("images/LockClosed.gif");

    /** this constructor should be used */
    public InterfaceDialog(ComponentReference compRef, boolean isForEditing) {
        this(ModelEditorFrame.getFrame(), compRef, isForEditing);
    }

    /** change label text in accordance with isAddingRelation
     * if it is true, label will be set for adding
     * if it is false, label will be set for editing */
    public void updateLabelsForEditing(boolean isEditing) {
        if (! isEditing) {
            localRelRb.setText("Add Implementation"); // this label is hidden
            remoteRelRb.setText("Add Interface");  // this label is hidden
            okayLcBt.setText("Add Implementation");
            okayRtBt.setText("Add Interface");
        } else {
            localRelRb.setText("Edit Implementation");
            remoteRelRb.setText("Edit Interface");
            okayLcBt.setText("Confirm Changes");
            okayRtBt.setText("Confirm Changes");
        }
    }

    /** constructor for testing */
    public InterfaceDialog(Frame owner, ComponentReference compRef, boolean isForEditing) {
        super(owner, true);
        this.isForEditing = isForEditing;

        setTitle("Interface Definition Dialog");
        this.compRef = compRef;
        this.setSize(DIALOG_WIDTH, DIALOG_LR_HEIGHT);
        this.setResizable(false);
        contentPane = this.getContentPane();

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel relTypeLabel = UIUtil.createLabel("");
        localRelRb = new JRadioButton("Edit Implementation");
        localRelRb.setMnemonic(KeyEvent.VK_P);
        localRelRb.setFont(UIUtil.DIALOG_FONT);
        localRelRb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setPanelVisibility(true);
            }
        });
        localRelRb.setSelected(false);
        localRelRb.setFocusable(false);

        remoteRelRb = new JRadioButton("Edit Interface (Note. All implementataions share the interface modified here)");
        remoteRelRb.setMnemonic(KeyEvent.VK_T);
        remoteRelRb.setFont(UIUtil.DIALOG_FONT);
        remoteRelRb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setPanelVisibility(false);
            }
        });
        remoteRelRb.setSelected(true);
        remoteRelRb.setFocusable(false);

        ButtonGroup group = new ButtonGroup();
        group.add(remoteRelRb);
        group.add(localRelRb);

        checkboxPanel.add(relTypeLabel);
        checkboxPanel.add(localRelRb);
        checkboxPanel.add(remoteRelRb);
        checkboxPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, RADIOBUTTON_PANEL_HEIGHT));
        checkboxPanel.setMinimumSize(checkboxPanel.getPreferredSize());
        checkboxPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, RADIOBUTTON_PANEL_HEIGHT));
        //checkboxPanel.setBackground(Color.RED);

        if (isForEditing) {
            contentPane.add(checkboxPanel, BorderLayout.PAGE_START);
        }
        localRelPanel = makeLeftPanel();
        remoteRelPanel = makeRightPanel();

        //contentPane.add(localRelPanel, BorderLayout.CENTER);
        setPanelVisibility(true);

        int x = owner.getX() + (owner.getWidth() - this.getWidth()) / 2;
        int y = owner.getY() + (owner.getHeight() - this.getHeight()) / 2 - 80;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        this.setLocation(x, y);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = true;
                dispose();
            }
        }, "SUBMIT", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public boolean isLocalPanelVisible() {
        return localRelRb.isSelected();
    }

    public void setPanelVisibility(boolean setLocalVisible) {
        if (setLocalVisible) {
            localRelRb.setSelected(true);
            this.setSize(DIALOG_WIDTH, DIALOG_LR_HEIGHT);
            contentPane.remove(remoteRelPanel);
            contentPane.add(localRelPanel, BorderLayout.CENTER);
            this.validate();
            this.repaint();
            implNameFd.requestFocusInWindow();
        } else {
            remoteRelRb.setSelected(true);
            this.setSize(DIALOG_WIDTH, DIALOG_RR_HEIGHT);
            contentPane.remove(localRelPanel);
            contentPane.add(remoteRelPanel, BorderLayout.CENTER);
            this.validate();
            this.repaint();
            itfNameFd.requestFocusInWindow();
        }
    }

    public ParameterDefinitionPanel getInputParamDefPanel() {
        return iParamDefPanel;
    }

    public ParameterDefinitionPanel getOutputParamDefPanel() {
        return oParamDefPanel;
    }

    public DependencyCheckPanel getDependencyCheckPanel() {
        return depCheckPanel;
    }

    private JPanel makeLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        implNameLb = UIUtil.createLabel("Implement Name");
        implNameFd = UIUtil.createTextField("", 61);

        implDepCheckLb = UIUtil.createLabel("Dependency");
        implDepCheckPanel = new DependencyCheckPanel();
        implDepCheckPanel.setEditable(true);
        implDepCheckPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 7 - 5));

        okayLcBt = UIUtil.createButton("Confirm Changes");
        okayLcBt.setPreferredSize(new Dimension(120, okayLcBt.getPreferredSize().height));
        cancelLcBt = UIUtil.createButton("Cancel");

        okayLcBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = true;
                dispose();
            }
        });

        cancelLcBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        });

        UIUtil.addComponentsToPanel(new Component[] {
                implNameLb, implNameFd, implDepCheckLb, implDepCheckPanel, okayLcBt, cancelLcBt
            }, panel);


        UIUtil.setTwoColumnConstraints(implNameLb, implNameFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 0 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(implDepCheckLb, implDepCheckPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 1 + 5, GAP_BETWEEN_COLUMNS, "right", "left");

        UIUtil.setFlowConstraints(new Component[] { okayLcBt, cancelLcBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 8 + 10, GAP_BETWEEN_COMPS,  "right");

        panel.setPreferredSize(new Dimension(DIALOG_WIDTH, FORM_RR_HEIGHT));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        panel.setMinimumSize(panel.getPreferredSize());
        panel.validate();

        implNameFd.requestFocusInWindow();

        return panel;
    }

    private JPanel makeRightPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        itfNameLb = UIUtil.createLabel("Interface Name");
        itfNameFd = UIUtil.createTextField("", 61);

        inputParamLb = UIUtil.createLabel("Input Param");
        iParamDefPanel = new ParameterDefinitionPanel(true);
        iParamDefPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 5));

        addIParamBt = UIUtil.createTinyButton("add");
        addIParamBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, addIParamBt.getPreferredSize().height));
        addIParamBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selRowIdx = (iParamDefPanel.getTable().getSelectedRow() >= 0 ? iParamDefPanel.getTable().getSelectedRow() : iParamDefPanel.getTable().getSelectedRowCount() - 1);
                iParamDefPanel.getParamDefTableModel().add(selRowIdx, "", CConstant.REAL_DATA_TYPE, new CUnit("No_Unit"), "0");
            }
        });
        removeIParamBt = UIUtil.createTinyButton("remove");
        removeIParamBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, removeIParamBt.getPreferredSize().height));
        removeIParamBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selRowIdx = iParamDefPanel.getTable().getSelectedRow();
                if (selRowIdx != -1) {
                    iParamDefPanel.getParamDefTableModel().remove(selRowIdx);
                } else {
                    JOptionPane.showMessageDialog(contentPane, "please select a row to be removed", "please select a row to be removed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        outputParamLb = UIUtil.createLabel("Output Param");
        oParamDefPanel = new ParameterDefinitionPanel(false);
        oParamDefPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 5));

        addOParamBt = UIUtil.createTinyButton("add");
        addOParamBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, addOParamBt.getPreferredSize().height));
        addOParamBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selRowIdx = (oParamDefPanel.getTable().getSelectedRow() >= 0 ? oParamDefPanel.getTable().getSelectedRow() : oParamDefPanel.getTable().getSelectedRowCount() - 1);
                oParamDefPanel.getParamDefTableModel().add(selRowIdx, "", CConstant.REAL_DATA_TYPE, new CUnit("No_Unit"), "0");
            }
        });
        removeOParamBt = UIUtil.createTinyButton("remove");
        removeOParamBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, removeOParamBt.getPreferredSize().height));
        removeOParamBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selRowIdx = oParamDefPanel.getTable().getSelectedRow();
                if (selRowIdx != -1) {
                    oParamDefPanel.getParamDefTableModel().remove(selRowIdx);
                } else {
                    JOptionPane.showMessageDialog(contentPane, "please select a row to be removed", "please select a row to be removed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


//        oParamDefPanel.getTable().addFocusListener(new FocusAdapter() {
//            public void focusLost(FocusEvent event) {
//                System.out.println("focus lost");
//                if (oParamDefPanel.getTable().getCellEditor() != null) {
//                    if (oParamDefPanel.getTable().getCellEditor() instanceof ParamNameEditor) {
//                        if (! ((ParamNameEditor) oParamDefPanel.getTable().getCellEditor()).isEditValid()) {
//                            return; // if it is invalid, just keep editing
//                        }
//                    }
//                    oParamDefPanel.getTable().getCellEditor().stopCellEditing();
//                }
//            }
//        });
//
//        iParamDefPanel.getTable().addFocusListener(new FocusAdapter() {
//            public void focusLost(FocusEvent event) {
//                System.out.println("focus lost");
//                if (iParamDefPanel.getTable().getCellEditor() != null) {
//                    if (iParamDefPanel.getTable().getCellEditor() instanceof ParamNameEditor) {
//                        if (! ((ParamNameEditor) iParamDefPanel.getTable().getCellEditor()).isEditValid()) {
//                            return; // if it is invalid, just keep editing
//                        }
//                    }
//                    iParamDefPanel.getTable().getCellEditor().stopCellEditing();
//                }
//            }
//        });

        depCheckLb = UIUtil.createLabel("Dependency");
        depCheckPanel = new DependencyCheckPanel(iParamDefPanel.getTable().getModel(), oParamDefPanel.getTable().getModel());
        depCheckPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 6 - 5));

        checkAllBt = UIUtil.createTinyButton("all");
        checkAllBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, checkAllBt.getPreferredSize().height));
        checkAllBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                DependencyCheckPanel.DepCheckTableModel depChkTblModel = (DependencyCheckPanel.DepCheckTableModel) depCheckPanel.getTable().getModel();
                depChkTblModel.setCheckedAll();
            }
        });

        uncheckAllBt = UIUtil.createTinyButton("none");
        uncheckAllBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, uncheckAllBt.getPreferredSize().height));
        uncheckAllBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                DependencyCheckPanel.DepCheckTableModel depChkTblModel = (DependencyCheckPanel.DepCheckTableModel) depCheckPanel.getTable().getModel();
                depChkTblModel.setUncheckedAll();
            }
        });

        checkSmartBt = UIUtil.createTinyButton("smart");
        checkSmartBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, checkSmartBt.getPreferredSize().height));
        checkSmartBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                DependencyCheckPanel.DepCheckTableModel depChkTblModel = (DependencyCheckPanel.DepCheckTableModel) depCheckPanel.getTable().getModel();
                depChkTblModel.setCheckedAll();
            }
        });
        checkSmartBt.setVisible(false); //todo: make this visible & implemented

        okayRtBt = UIUtil.createButton("Confirm Changes");
        okayRtBt.setPreferredSize(new Dimension(120, okayRtBt.getPreferredSize().height));
        cancelRtBt = UIUtil.createButton("Cancel");

        okayRtBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newItfName = itfNameFd.getText().trim();
                if (! isForEditing && compRef.getCurrentCModel().getInterface(newItfName) != null) {
                    /* there already exists an interface with the same name */
                    JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(itfNameFd), "Sorry, you can't use this interface name. It needs to be unique in a model.", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] { "OK" }, "OK");
                    itfNameFd.requestFocusInWindow();
                    return;
                }

                isSubmitted = true;
                dispose();
            }
        });

        cancelRtBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        });

        UIUtil.addComponentsToPanel(new Component[] {
                itfNameLb, itfNameFd, inputParamLb, iParamDefPanel, addIParamBt, removeIParamBt,
                outputParamLb, oParamDefPanel, addOParamBt, removeOParamBt,
                depCheckLb, depCheckPanel,
                okayRtBt, cancelRtBt,
                checkAllBt, uncheckAllBt, // checkSmartBt
            }, panel);


        //UIUtil.setTwoColumnConstraints(implNameLb, implNameFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 0 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        //UIUtil.setTwoColumnConstraints(itfLockLb, itfLockBt, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 1 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(itfNameLb, itfNameFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 0 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(inputParamLb, iParamDefPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 1 + 5, GAP_BETWEEN_COLUMNS, "right", "left");

        //UIUtil.setFlowConstraints(new Component[] { addIParamBt, removeIParamBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 4 + 5, GAP_BETWEEN_COMPS,  "right");

        UIUtil.setTwoColumnConstraints(outputParamLb, oParamDefPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 6 + 10, GAP_BETWEEN_COLUMNS, "right", "left");
        // UIUtil.setFlowConstraints(new Component[] { addOParamBt, removeOParamBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 9 + 10, GAP_BETWEEN_COMPS,  "right");

        UIUtil.setTwoColumnConstraints(depCheckLb, depCheckPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 11 + 15, GAP_BETWEEN_COLUMNS, "right", "left");
        SpringLayout.Constraints depCheckLbCons = layout.getConstraints(depCheckLb);
        SpringLayout.Constraints checkAllCons = layout.getConstraints(checkAllBt);
        SpringLayout.Constraints uncheckAllCons = layout.getConstraints(uncheckAllBt);
//        SpringLayout.Constraints checkSmartCons = layout.getConstraints(checkSmartBt);

        checkAllCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        checkAllCons.setY(Spring.sum(depCheckLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        uncheckAllCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        uncheckAllCons.setY(Spring.sum(depCheckLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(35)));
//        checkSmartCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
//        checkSmartCons.setY(Spring.sum(depCheckLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(35)));

        UIUtil.setFlowConstraints(new Component[] { okayRtBt, cancelRtBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 17 + 17, GAP_BETWEEN_COMPS,  "right");

        /* place add/remove buttons for input/output parameter */
        SpringLayout.Constraints iParamLbCons = layout.getConstraints(inputParamLb);
        SpringLayout.Constraints addIParamCons = layout.getConstraints(addIParamBt);
        SpringLayout.Constraints removeIParamCons = layout.getConstraints(removeIParamBt);
        addIParamCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        addIParamCons.setY(Spring.sum(iParamLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        removeIParamCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        removeIParamCons.setY(Spring.sum(iParamLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(35)));

        SpringLayout.Constraints oParamLbCons = layout.getConstraints(outputParamLb);
        SpringLayout.Constraints addOParamCons = layout.getConstraints(addOParamBt);
        SpringLayout.Constraints removeOParamCons = layout.getConstraints(removeOParamBt);
        addOParamCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        addOParamCons.setY(Spring.sum(oParamLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        removeOParamCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        removeOParamCons.setY(Spring.sum(oParamLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(35)));

        panel.setPreferredSize(new Dimension(DIALOG_WIDTH, FORM_LR_HEIGHT));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        panel.setMinimumSize(panel.getPreferredSize());
        panel.validate();

        itfNameFd.requestFocusInWindow();

        return panel;
    }

    public JTable getInputParameterTable() {
        return iParamDefPanel.getTable();
    }

    public JTable getOutputParameterTable() {
        return oParamDefPanel.getTable();
    }

    public void setInterfaceName(String itfName) {
        itfNameFd.setText(itfName);
    }

    public String getInterfaceName() {
        return itfNameFd.getText();
    }

    public void setImplementationName(String implName) {
        implNameFd.setText(implName);
    }

    public String getImplementationName() {
        return implNameFd.getText();
    }

    /**
     * get what is input param list
     * returns [{param name, data type, unit, defaultValue }, {param name, data type, unit, defaultValue }, {param name, data type, unit, defaultValue }]
     */
    public java.util.List getInputParamList() {
        List ret = new ArrayList();
        TableModel tableModel = iParamDefPanel.getTable().getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String paramName = ((String) tableModel.getValueAt(i, 0)).trim();
            String dataType = (String) tableModel.getValueAt(i, 1);
            String unit = ((CUnit) tableModel.getValueAt(i, 2)).getAbbreviation();
            String defaultValue = (String) tableModel.getValueAt(i, 3);
            ret.add(new String[] { paramName, dataType, unit, defaultValue });
        }
        return ret;
    }

    /** param info list is a List of String[] { paramName, dataType, unit, defaultValue } */
    public void setInputParamList(List paramInfoList) {
        ParameterDefinitionPanel.ParamDefTableModel tableModel = (ParameterDefinitionPanel.ParamDefTableModel) iParamDefPanel.getTable().getModel();
        for (int i = 0; i < paramInfoList.size(); i++) {
            String[] paramInfo = (String[]) paramInfoList.get(i);
            tableModel.addValue(paramInfo);
        }
    }

    /** param info list is a List of String[] { paramName, dataType, unit } */
    public void setOutputParamList(List paramInfoList) {
        ParameterDefinitionPanel.ParamDefTableModel tableModel = (ParameterDefinitionPanel.ParamDefTableModel) oParamDefPanel.getTable().getModel();
        for (int i = 0; i < paramInfoList.size(); i++) {
            String[] paramInfo = (String[]) paramInfoList.get(i);
            tableModel.addValue(paramInfo);
        }
    }

    /**
     * get what is input param list
     * returns [{param name, data type, unit, defaultValue }, {param name, data type, unit, defaultValue }, {param name, data type, unit, defaultValue }]
     */
    public java.util.List getOutputParamList() {
        List ret = new ArrayList();
        TableModel tableModel = oParamDefPanel.getTable().getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String paramName = ((String) tableModel.getValueAt(i, 0)).trim();
            String dataType = (String) tableModel.getValueAt(i, 1);
            String unit = ((CUnit) tableModel.getValueAt(i, 2)).getAbbreviation();
            String defaultValue = (String) tableModel.getValueAt(i, 3);
            ret.add(new String[] { paramName, dataType, unit, defaultValue });
        }
        return ret;
    }

    /**
     * returns an list of which output param depend on which input params
     * each item of the list is an Object[], and the Object[] has two elements of String, String[].
     * [{output-A, { input-X, input-Y }}, {output-B, { input-Y, input-W }}, {output-C, { input-W }}]
     * Sample code to retrieve the information inside its return
     * List depList = getDependency();
     * Object[] item = (Object[]) depList.get(0);
     * String outputName = (String) item[0];
     * String[] inputNames = (String[]) item[1];
     */
    public List getInterfaceDependency() {
        List ret = new ArrayList();
        TableModel tableModel = depCheckPanel.getTable().getModel();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            List drivers = new ArrayList();
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                boolean driving = ((Boolean) tableModel.getValueAt(i, j)).booleanValue();
                if (driving) {
                    drivers.add(tableModel.getColumnName(j).trim());
                }
            }
            String driven = ((String) tableModel.getValueAt(i, 0)).trim();
            ret.add(new Object[] { driven, drivers.toArray(new String[drivers.size()]) });
        }
        return ret;
    }

    /** populate relation dialog using the same data structure used in getInterfaceDependency() */
    public void setInterfaceDependency(List depInfoList) {
        TableModel tableModel = depCheckPanel.getTable().getModel();

        for (int i = 0; i < depInfoList.size(); i++) {
            Object[] depInfo = (Object[]) depInfoList.get(i);
            String outputName = (String) depInfo[0];
            String[] inputNames = (String[]) depInfo[1];
            for (int j = 0; j < inputNames.length; j++) {
                String inputName = inputNames[j];
                int rowIndex = getRowIndexByOutputParamName(outputName);
                int columnIndex = getColumnIndexByInputParamName(inputName);
                if (columnIndex != -1) {
                    tableModel.setValueAt(new Boolean(true), rowIndex, columnIndex);
                } else {
                    /*  sometimes columnIndex can be -1 because of the following case
                     *
                     *    A     B ->   X      Y
                     *                [=B]   [=X]
                     *
                     *   because X is driving Y, depInfoList would contain { "Y", { "B", "X" } }
                     *   however, "X" is not among input parameters, so getColumnIndexByInputParamName("X") returns -1;
                     *   we can just skip displaying this kind of dependecy in the matrix
                     *   because we still can see Y depends on B even after ignoring "X" from { "B", "X" }
                     */
                }

            }
        }
    }

    /** look into depCheckPanel to find the column index of a certain input paramter */
    private int getColumnIndexByInputParamName(String inputParamName) {
        for (int i = 0 ; i < depCheckPanel.getTable().getColumnCount(); i++) {
            String columnParamName = depCheckPanel.getTable().getColumnName(i);
            if (columnParamName.equals(inputParamName)) {
                return i;
            }
        }
        return -1;
    }

    /** look into depCheckPanel to find the column index of a certain input paramter */
    private int getRowIndexByOutputParamName(String outputParamName) {
        for (int i = 0 ; i < depCheckPanel.getTable().getRowCount(); i++) {
            String rowParamName = (String) depCheckPanel.getTable().getValueAt(i, 0);
            if (rowParamName.equals(outputParamName)) {
                return i;
            }
        }
        return -1;
    }

    /** populate relation dialog using the same data structure used in getInterfaceDependency() and String List of input/output parameters */
    public void setImplementationDependency(List iParamNames, List oParamNames, List depInfoList) {
        TableModel tableModel = implDepCheckPanel.getTable().getModel();
        implDepCheckPanel.setInputParamNames(iParamNames);
        implDepCheckPanel.setOutputParamNames(oParamNames);

        for (int i = 0; i < depInfoList.size(); i++) {
            Object[] depInfo = (Object[]) depInfoList.get(i);
            String outputName = (String) depInfo[0];
            String[] inputNames = (String[]) depInfo[1];
            for (int j = 0; j < inputNames.length; j++) {
                String inputName = inputNames[j];
                int rowIndex = getRowIndexByOutputParamName(outputName);
                int columnIndex = getColumnIndexByInputParamName(inputName);
                if (columnIndex != -1) {
                    tableModel.setValueAt(new Boolean(true), rowIndex, columnIndex);
                } else {
                    /*  sometimes columnIndex can be -1 because of the following case
                     *
                     *    A     B ->   X      Y
                     *                [=B]   [=X]
                     *
                     *   because X is driving Y, depInfoList would contain { "Y", { "B", "X" } }
                     *   however, "X" is not among input parameters, so getColumnIndexByInputParamName("X") returns -1;
                     *   we can just skip displaying this kind of dependecy in the matrix
                     *   because we still can see Y depends on B even after ignoring "X" from { "B", "X" }
                     */
                }
            }
        }
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }


    public ComponentReference getComponentReference() {
        return compRef;
    }
}
