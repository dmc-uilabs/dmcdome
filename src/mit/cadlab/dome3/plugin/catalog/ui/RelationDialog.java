package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


/**
 * User: Sangmok Han
 * Date: 2006. 2. 1.
 */
public class RelationDialog extends JDialog {
    private ComponentReference compRef;
    private JPanel localRelPanel;
    private JPanel remoteRelPanel;

    private static int DIALOG_WIDTH = 610;
    private static int DIALOG_LR_HEIGHT = 645;
    private static int DIALOG_RR_HEIGHT = 590;
    private static int RADIOBUTTON_PANEL_HEIGHT = 30;
    private static int FORM_LR_HEIGHT = 400;
    private static int FORM_RR_HEIGHT = 300;

    private static int ROW_HEIGHT = 27;
    private static int LEFT_COLUMN_WIDTH = 65;
    private static int RIGHT_COLUMN_WIDTH = 500 - 6;
    private static int GAP_BETWEEN_COLUMNS = 10;
    private static int LEFT_MARGIN = 10;
    private static int GAP_BETWEEN_COMPS = 5;
    private static int ADD_REMOVE_BUTTON_WIDTH = 50;
    private static int ADD_REMOVE_BUTTON_TAB = 25;

    private JLabel serverLb;
    private JLabel userNameLb;
    private JLabel passwordLb;
    private JLabel pathLb;
    private JLabel browseLb;
    private JLabel spaceLabel;
    private JComboBox spaceCb;
    private JToggleButton loginBt;
    private JButton addRtBt;
    private JButton cancelRtBt;
    private JButton addLcBt;
    private JButton cancelLcBt;
    private JLabel bookmarkLb;
    private JCheckBox bookmarkCh;
    private JTextField serverFd;
    private JTextField userNameFd;
    private JPasswordField passwordFd;
    private JTextField pathFd;
    private JComboBox bookmarkCb;

    private JButton addIParamBt;
    private JButton removeIParamBt;
    private JButton addOParamBt;
    private JButton removeOParamBt;

    private JButton checkAllBt ;
    private JButton uncheckAllBt ;

    private DefaultComboBoxModel bookmarkModel;

    private static Icon GREEN_CIRCLE = UIUtil.createImageIcon("images/GreenCircle.gif");
    private static Icon RED_CIRCLE = UIUtil.createImageIcon("images/RedCircle.gif");
    private static java.util.List RECENT_LOGINS = new ArrayList();

    private Map connMap;
    private DomeConnection domeConn;
    private ServerNavigationPanel serverNavPanel;

    private JLabel relNameLb;
    private JTextField relNameFd;
    private JLabel inputParamLb;
    protected  ParameterDefinitionPanel iParamDefPanel;
    protected ParameterDefinitionPanel oParamDefPanel;
    private Container contentPane;
    private JLabel outputParamLb;
    private JLabel groovyScriptEditorLb;
    private GroovyScriptEditor groovyScriptEditor;
    private DependencyCheckPanel depCheckPanel;
    private JLabel depCheckLb;
    private JRadioButton localRelRb;
    private JRadioButton remoteRelRb;
    private boolean isSubmitted;


    /** this constructor should be used */
    public RelationDialog(ComponentReference compRef) {
        this(ModelEditorFrame.getFrame(), compRef);
    }

    /** change label text in accordance with isAddingRelation
     * if it is true, label will be set for adding
     * if it is false, label will be set for editing */
    public void updateLabelsForEditing(boolean isEditing) {
        if (! isEditing) {
            localRelRb.setText("Add Local Relation");
            remoteRelRb.setText("Add Remote Relation");
            addLcBt.setText("Add Relation");
            addRtBt.setText("Add Relation");
        } else {
            localRelRb.setText("Edit Local Relation");
            remoteRelRb.setText("Edit Remote Relation");
            addLcBt.setText("Confirm Changes");
            addRtBt.setText("Confirm Changes");
        }
    }

    /** constructor for testing */
    public RelationDialog(Frame owner, ComponentReference compRef) {
        super(owner, true);
        connMap = new HashMap();
        setTitle("Relation Definition Dialog");
        this.compRef = compRef;
        this.setSize(DIALOG_WIDTH, DIALOG_LR_HEIGHT);
        this.setResizable(false);
        contentPane = this.getContentPane();
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel relTypeLabel = UIUtil.createLabel("");
        localRelRb = new JRadioButton("Edit Local Relation");
        localRelRb.setMnemonic(KeyEvent.VK_L);
        localRelRb.setFont(UIUtil.DIALOG_FONT);
        localRelRb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setPanelVisibility(true);
            }
        });
        localRelRb.setSelected(false);
        localRelRb.setFocusable(false);

        remoteRelRb = new JRadioButton("Edit Remote Relation");
        remoteRelRb.setMnemonic(KeyEvent.VK_R);
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

        contentPane.add(checkboxPanel, BorderLayout.PAGE_START);
        //JLayeredPane layeredPane = makeLayeredPane();
        //contentPane.add(layeredPane);
        localRelPanel = makeLocalRelationPanel();
        remoteRelPanel = makeRemoteRelationPanel();

        setPanelVisibility(true);

        int x = owner.getX() + (owner.getWidth() - this.getWidth()) / 2;
        int y = owner.getY() + (owner.getHeight() - this.getHeight()) / 2;
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

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (! isLocalPanelVisible()) {
                    loginBt.setSelected(! loginBt.isSelected());
                }
            }
        }, "TOGGLE LOGIN", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (isLocalPanelVisible()) {
                    if (makeSureNoBlankParamName() && makeSureSomeDependencyDefined()) {
                        isSubmitted = true;
                        dispose();
                    }
                } else {
                    isSubmitted = true;
                    dispose();
                }
            }
        }, "SUBMIT", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public boolean isLocalPanelVisible() {
        return localRelRb.isSelected();
    }

    public void setPanelVisibility(boolean setLocalVisible) {
        if (setLocalVisible) {
            localRelRb.setSelected(true);
            RelationDialog.this.setSize(DIALOG_WIDTH, DIALOG_LR_HEIGHT);
            contentPane.remove(remoteRelPanel);
            contentPane.add(localRelPanel, BorderLayout.CENTER);
            RelationDialog.this.validate();
            RelationDialog.this.repaint();
            relNameFd.requestFocusInWindow();
        } else {
            remoteRelRb.setSelected(true);
            RelationDialog.this.setSize(DIALOG_WIDTH, DIALOG_RR_HEIGHT);
            contentPane.remove(localRelPanel);
            contentPane.add(remoteRelPanel, BorderLayout.CENTER);
            RelationDialog.this.validate();
            RelationDialog.this.repaint();

            if (RECENT_LOGINS.size() > 0) {
                bookmarkCb.setSelectedIndex(0);
            }

            serverFd.requestFocusInWindow();
        }
    }

    public ParameterDefinitionPanel getInputParamDefPanel() {
        return iParamDefPanel;
    }

    public ParameterDefinitionPanel getOutputParamDefPanel() {
        return oParamDefPanel;
    }

    public GroovyScriptEditor getScriptEditor() {
        return groovyScriptEditor;
    }

    public DependencyCheckPanel getDependencyCheckPanel() {
        return depCheckPanel;
    }

    private JPanel makeLocalRelationPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        relNameLb = UIUtil.createLabel("Relation Name");
        relNameFd = UIUtil.createTextField("", 61);

        inputParamLb = UIUtil.createLabel("Input Param");
        iParamDefPanel = new ParameterDefinitionPanel(true);
        iParamDefPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 4));

        addIParamBt = UIUtil.createTinyButton("add");
        addIParamBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, addIParamBt.getPreferredSize().height));
        addIParamBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selRowIdx = (iParamDefPanel.getTable().getSelectedRow() >= 0 ? iParamDefPanel.getTable().getSelectedRow() : iParamDefPanel.getTable().getSelectedRowCount() - 1);
                iParamDefPanel.getParamDefTableModel().add(selRowIdx, "", CConstant.REAL_DATA_TYPE, new CUnit(CConstant.NO_UNIT_STR), "0");
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
        oParamDefPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 4));

        addOParamBt = UIUtil.createTinyButton("add");
        addOParamBt.setPreferredSize(new Dimension(ADD_REMOVE_BUTTON_WIDTH, addOParamBt.getPreferredSize().height));
        addOParamBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selRowIdx = (oParamDefPanel.getTable().getSelectedRow() >= 0 ? oParamDefPanel.getTable().getSelectedRow() : oParamDefPanel.getTable().getSelectedRowCount() - 1);
                oParamDefPanel.getParamDefTableModel().add(selRowIdx, "", CConstant.REAL_DATA_TYPE, new CUnit(CConstant.NO_UNIT_STR), "0");
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

        groovyScriptEditorLb = UIUtil.createLabel("Relation Script");
        groovyScriptEditor = new GroovyScriptEditor(this);
        groovyScriptEditor.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 7 - 5));

        depCheckLb = UIUtil.createLabel("Dependency");
        depCheckPanel = new DependencyCheckPanel(iParamDefPanel.getTable().getModel(), oParamDefPanel.getTable().getModel());
        depCheckPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 4 - 5));

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

        addLcBt = UIUtil.createButton("Okay");
        addLcBt.setPreferredSize(new Dimension(120, addLcBt.getPreferredSize().height));
        cancelLcBt = UIUtil.createButton("Cancel");

        addLcBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                /* check if no blank param name, then do submit+dispose */
                if (makeSureNoBlankParamName() && makeSureSomeDependencyDefined()) {
                    isSubmitted = true;
                    dispose();
                }
            }
        });

        cancelLcBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isSubmitted = false;
                dispose();
            }
        });

        UIUtil.addComponentsToPanel(new Component[] {
                relNameLb, relNameFd, inputParamLb, iParamDefPanel, addIParamBt, removeIParamBt,
                outputParamLb, oParamDefPanel, addOParamBt, removeOParamBt,
                groovyScriptEditorLb, groovyScriptEditor, depCheckLb, depCheckPanel,
                addLcBt, cancelLcBt, checkAllBt, uncheckAllBt
            }, panel);


        UIUtil.setTwoColumnConstraints(relNameLb, relNameFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 0 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(inputParamLb, iParamDefPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 1 + 5, GAP_BETWEEN_COLUMNS, "right", "left");

        //UIUtil.setFlowConstraints(new Component[] { addIParamBt, removeIParamBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 4 + 5, GAP_BETWEEN_COMPS,  "right");

        UIUtil.setTwoColumnConstraints(outputParamLb, oParamDefPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 5 + 10, GAP_BETWEEN_COLUMNS, "right", "left");
        // UIUtil.setFlowConstraints(new Component[] { addOParamBt, removeOParamBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 9 + 10, GAP_BETWEEN_COMPS,  "right");

        UIUtil.setTwoColumnConstraints(groovyScriptEditorLb, groovyScriptEditor, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 9 + 10, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(depCheckLb, depCheckPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 16 + 10, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setFlowConstraints(new Component[] { addLcBt, cancelLcBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 20 + 10, GAP_BETWEEN_COMPS,  "right");

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

        SpringLayout.Constraints depCheckLbCons = layout.getConstraints(depCheckLb);
        SpringLayout.Constraints checkAllCons = layout.getConstraints(checkAllBt);
        SpringLayout.Constraints uncheckAllCons = layout.getConstraints(uncheckAllBt);
        checkAllCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        checkAllCons.setY(Spring.sum(depCheckLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        uncheckAllCons.setX(Spring.constant(ADD_REMOVE_BUTTON_TAB));
        uncheckAllCons.setY(Spring.sum(depCheckLbCons.getConstraint(SpringLayout.SOUTH), Spring.constant(35)));

        panel.setPreferredSize(new Dimension(DIALOG_WIDTH, FORM_LR_HEIGHT));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        panel.setMinimumSize(panel.getPreferredSize());
        panel.validate();
        return panel;
    }

    /** get ready local relation panel for the next use */
    public void resetLocalRelationPanel() {
        relNameFd.setText("local relation");
        groovyScriptEditor.setText("");
        iParamDefPanel.reset();
        oParamDefPanel.reset();
        depCheckPanel.reset();
    }

    /** get ready remote relation panel for the next use */
    public void resetRemoteRelationPanel() {
//        serverFd.setText("");
//        userNameFd.setText("");
//        passwordFd.setText("");
        loginBt.setSelected(false);
    }

    private boolean makeSureSomeDependencyDefined() {
        TableModel iParamTableModel = iParamDefPanel.getTable().getModel();
        TableModel oParamTableModel = oParamDefPanel.getTable().getModel();

        if (iParamTableModel.getRowCount() == 0 || oParamTableModel.getRowCount() == 0) {
            return true; // we cannot set dependency in these two cases
        }

        List rowList = ((DependencyCheckPanel.DepCheckTableModel) depCheckPanel.getTable().getModel()).getValueRowList();

        if (rowList.size() == 0) {
            Object[] options = {"Please assign default dependency.", "Okay, let me modify dependency." };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(addLcBt), "Dependency between input and output parameters cannot be left empty.", "Blank Dependency Found", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            if (answer == 0) {
                ((DependencyCheckPanel.DepCheckTableModel) depCheckPanel.getTable().getModel()).setCheckedAll();
                return true;
            } else {
                depCheckPanel.getTable().requestFocusInWindow();
                return false;
            }
        }

        return true;
    }

    /** return true if no blank param name. return false if some blanks are found */
    private boolean makeSureNoBlankParamName() {
        TableModel iParamTableModel = iParamDefPanel.getTable().getModel();
        for (int i = 0; i < iParamTableModel.getRowCount(); i++) {
            String paramName = (String) iParamDefPanel.getTable().getModel().getValueAt(i, 0);
            if ("".equals(paramName.trim())) {
                Object[] options = {"Okay, let me assign a name on it." };
                iParamDefPanel.getTable().requestFocusInWindow();
                iParamDefPanel.getTable().editCellAt(i, 0);
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(addLcBt), "'Param Name' field cannot be left as blank.", "Blank Parameter Name Found", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return false; // do not submit or dispose
            }
        }

        TableModel oParamTableModel = oParamDefPanel.getTable().getModel();
        for (int i = 0; i < oParamTableModel.getRowCount(); i++) {
            String paramName = (String) oParamDefPanel.getTable().getModel().getValueAt(i, 0);
            if ("".equals(paramName.trim())) {
                Object[] options = {"Okay, let me assign a name on it!" };
                oParamDefPanel.getTable().requestFocusInWindow();
                oParamDefPanel.getTable().editCellAt(i, 0);
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(addLcBt), "'Param Name' field cannot be left as blank.", "Blank Parameter Name Found", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return false; // do not submit or dispose
            }
        }

        return true;
    }

    private JPanel makeRemoteRelationPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        serverLb = UIUtil.createLabel("Server");
        userNameLb = UIUtil.createLabel("User Name");
        passwordLb = UIUtil.createLabel("Password");
        pathLb = UIUtil.createLabel("Path");
        browseLb = UIUtil.createLabel("");
        spaceLabel = UIUtil.createLabel("Space");
        spaceCb = UIUtil.createComboBox(new String[] { "Server", "User", "Group" });
        loginBt = UIUtil.createToggleButton("Login", RED_CIRCLE);
        addRtBt = UIUtil.createButton("Okay");
        addRtBt.setPreferredSize(new Dimension(120, addRtBt.getPreferredSize().height));
        cancelRtBt = UIUtil.createButton("Cancel");

        addRtBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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

        spaceCb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                serverNavPanel.updateTree(domeConn);
            }
        });

        bookmarkLb = UIUtil.createLabel("Recent Logins");
        bookmarkCh = UIUtil.createCheckBox("remember login", true);

        serverFd = UIUtil.createTextField("", 32);
        userNameFd = UIUtil.createTextField("", 32);
        passwordFd = UIUtil.createPasswordField("", 32);

        pathFd = UIUtil.createTextField("" , 61);
        pathFd.setEditable(false);


        bookmarkCb = UIUtil.createComboBox();
        bookmarkModel = new DefaultComboBoxModel();
        bookmarkModel.addElement(RECENT_LOGINS.size() > 0 ? "choose..." : "none...");
        for (int i = 0; i < RECENT_LOGINS.size(); i++) {
            RecentLogin recentLogin = getRecentLogin(i);
            bookmarkModel.addElement("server: " + recentLogin.server + ", user: " + recentLogin.userName);
        }
        bookmarkCb.setModel(bookmarkModel);
        bookmarkCb.setFocusable(false);
        UIUtil.setFixedSize(bookmarkCb, new Dimension(262, bookmarkCb.getPreferredSize().height));
        bookmarkCb.setEditable(false);
        bookmarkCb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selectedIdx = bookmarkCb.getSelectedIndex();
                if (selectedIdx > 0) {
                    RecentLogin recentLogin = getRecentLogin(selectedIdx - 1);
                    serverFd.setText(recentLogin.server);
                    userNameFd.setText(recentLogin.userName);
                    passwordFd.setText(recentLogin.password);
                }
            }
        });

        loginBt.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (loginBt.isSelected()) {
                    tryLogin();
                } else {
                    pathFd.setText("");
                    setConnected(false);
                }
            }
        });

        loginBt.setIconTextGap(7);
        loginBt.setPreferredSize(new Dimension(80, loginBt.getPreferredSize().height));

        serverNavPanel = new ServerNavigationPanel(this, compRef);
        serverNavPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, ROW_HEIGHT * 12 - 5));

        UIUtil.addComponentsToPanel(new Component[] { bookmarkLb, bookmarkCb, serverLb, serverLb, userNameLb, passwordLb, serverFd, userNameFd, passwordFd, loginBt, addRtBt, cancelRtBt, spaceCb, spaceLabel, pathFd, pathLb, browseLb, bookmarkCh, serverNavPanel,  }, panel);

        UIUtil.setTwoColumnConstraints(bookmarkLb, bookmarkCb, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 0 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(serverLb, serverFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 1 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(userNameLb, userNameFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 2 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(passwordLb, passwordFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 3 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(spaceLabel, spaceCb, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 4 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setTwoColumnConstraints(pathLb, pathFd, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 5 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        //UIUtil.setFlowConstraints(new Component[] { bookmarkCh }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN + LEFT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, ROW_HEIGHT * 3 + 5, GAP_BETWEEN_COMPS, "right");
        //UIUtil.setFlowConstraints(new Component[] { connLabel }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN - 20, ROW_HEIGHT * 2 + 5, GAP_BETWEEN_COMPS, "right");
        UIUtil.setFlowConstraints(new Component[] { bookmarkCh, loginBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 3 + 5, 25, "right");
        UIUtil.setTwoColumnConstraints(browseLb, serverNavPanel, layout, LEFT_COLUMN_WIDTH, RIGHT_COLUMN_WIDTH, LEFT_MARGIN, ROW_HEIGHT * 6 + 5, GAP_BETWEEN_COLUMNS, "right", "left");
        UIUtil.setFlowConstraints(new Component[] { addRtBt, cancelRtBt }, layout, LEFT_COLUMN_WIDTH + RIGHT_COLUMN_WIDTH + GAP_BETWEEN_COLUMNS, LEFT_MARGIN, ROW_HEIGHT * 18 + 5 + 3, GAP_BETWEEN_COMPS, "right");

        panel.setPreferredSize(new Dimension(DIALOG_WIDTH, FORM_RR_HEIGHT));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        panel.setMinimumSize(panel.getPreferredSize());
        panel.revalidate();

        setConnected(false);

        return panel;
    }

    /** returns true if try was successful */
    public boolean tryLogin() {
        domeConn = getDomeConnection(serverFd.getText().trim(), userNameFd.getText(), new String(passwordFd.getPassword()));
        if (domeConn == null || ! domeConn.isConnected()) {
            JOptionPane.showMessageDialog(getOwner(), "<html>login failed: <br><br>host=" + serverFd.getText().trim() + "<br>username=" + userNameFd.getText() + "</html>", "login failed.", JOptionPane.ERROR_MESSAGE);
            loginBt.setSelected(false);
            return false;
        } else {
            if (bookmarkCh.isSelected()) {
                addRecentLogin(serverFd.getText().trim(), userNameFd.getText(), new String(passwordFd.getPassword()));
            }
            setConnected(true);
            return true;
        }
    }

    public void closeDomeConnections() {
        for (Iterator i = connMap.values().iterator(); i.hasNext(); ) {
            DomeConnection conn = (DomeConnection) i.next();
            conn.close();
        }
    }

    public JTable getInputParameterTable() {
        return iParamDefPanel.getTable();
    }

    public JTable getOutputParameterTable() {
        return oParamDefPanel.getTable();
    }

    private void setConnected(boolean isConnected) {
        bookmarkCb.setEnabled(! isConnected);
        serverFd.setEditable(! isConnected);
        userNameFd.setEditable(! isConnected);
        passwordFd.setEditable(! isConnected);

        spaceCb.setEnabled(isConnected);
        pathFd.setEnabled(isConnected);
        serverNavPanel.setEnabled(isConnected);

        addRtBt.setEnabled(false);
        loginBt.setSelected(isConnected);

        if (isConnected) {
            loginBt.setText("Login");
            loginBt.setIcon(GREEN_CIRCLE);
            pathFd.setBackground(Color.WHITE);
            serverNavPanel.updateTree(domeConn);
        } else {
            loginBt.setText("Login");
            loginBt.setIcon(RED_CIRCLE);
            pathFd.setBackground(Color.LIGHT_GRAY);
            serverNavPanel.clear();
        }
    }

    public String getServer() {
        return serverFd.getText();
    }

    public void setServer(String server) {
        serverFd.setText(server);
    }

    public String getSpace() {
        return (String) spaceCb.getSelectedItem();
    }

    /** one of "server", "user", and "group" */
    public void setSpace(String space) {
        if ("server".equalsIgnoreCase(space)) {
            spaceCb.setSelectedIndex(0);
        } else if ("user".equalsIgnoreCase(space)) {
            spaceCb.setSelectedIndex(1);
        } else if ("group".equalsIgnoreCase(space)) {
            spaceCb.setSelectedIndex(2);
        }
    }

    public void setRelationName(String relName) {
        relNameFd.setText(relName);
    }

    public String getRelationName() {
        return relNameFd.getText();
    }

    /**
     * get what is input param list
     * returns [{param name, data type, unit, defaultValue}, {param name, data type, unit, defaultValue}, {param name, data type, unit, defaultValue}]
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

    public void setRelationScript(String script) {
        groovyScriptEditor.setText(script);
    }

    /**
     * get what is input param list
     * returns [{param name, data type, unit, defaultValue}, {param name, data type, unit, defaultValue}, {param name, data type, unit, defaultValue}]
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

    public String getRelationScript() {
        return groovyScriptEditor.getText();
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
    public List getDependency() {
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

    /** populate relation dialog using the same data structure used in getDependency() */
    public void setDependency(List depInfoList) {
        TableModel tableModel = depCheckPanel.getTable().getModel();
        ((DependencyCheckPanel.DepCheckTableModel) tableModel).setUncheckedAll();

        for (int i = 0; i < depInfoList.size(); i++) {
            Object[] depInfo = (Object[]) depInfoList.get(i);
            String outputName = (String) depInfo[0];
            String[] inputNames = (String[]) depInfo[1];
            for (int j = 0; j < inputNames.length; j++) {
                String inputName = inputNames[j];
                int rowIndex = getRowIndexByOutputParamName(outputName);
                int columnIndex = getColumnIndexByInputParamName(inputName);
                tableModel.setValueAt(new Boolean(true), rowIndex, columnIndex);
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


    public String getUserName() {
        return userNameFd.getText();
    }

    public void setUserName(String userName) {
        userNameFd.setText(userName);
    }

    public String getPassword() {
        return new String(passwordFd.getPassword());
    }

    public void setPassword(String password) {
        passwordFd.setText(password);
    }

    public String getPath() {
        return pathFd.getText();
    }

    public void setPath(String path) {
        pathFd.setText(path);
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }


    /** make server navigation pane to pointing the same location as the value of path text field */
    public void updateServerNavigationPane() {
        serverNavPanel.openPath(getPath());
    }

    public ServerNavigationPanel getServerNavigationPanel() {
        return serverNavPanel;
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    private void addRecentLogin(String server, String userName, String password) {
        RecentLogin addedLogin = new RecentLogin(server, userName, password);
        if (RECENT_LOGINS.contains(addedLogin)) {
            RECENT_LOGINS.remove(addedLogin);
        }
        RECENT_LOGINS.add(0, addedLogin); // insert at the top

        bookmarkModel.removeAllElements();
        bookmarkModel.addElement("choose...");
        for (int i = 0; i < RECENT_LOGINS.size(); i++) {
            RecentLogin recentLogin = getRecentLogin(i);
            bookmarkModel.addElement("server: " + recentLogin.server + ", user: " + recentLogin.userName);
        }
    }

    private RecentLogin getRecentLogin(int index) {
        return (RecentLogin) RECENT_LOGINS.get(index);
    }

    private int getRecentLoginCount() {
        return RECENT_LOGINS.size();
    }

    /** returns if a new connection is created, which determines whether we need to redraw the server browsing tree */
    public DomeConnection getDomeConnection(String host, String userName, String passwd) {
        DomeConnection domeConn = (DomeConnection) connMap.get(host + "|" + userName);
        if (domeConn == null) {
            try {
                domeConn = new DomeConnection(userName, passwd, host);
                connMap.put(host + "|" + userName, domeConn);
            } catch (Exception e) {
                System.out.println("connection is not available");
                return null;
            }
        }
        return domeConn;
    }

    public DomeConnection getDomeConnection() {
        return domeConn;
    }

    /** invoked from ServerNavigationPanel */
    public void setAddButtonEnabled(boolean isEnabled) {
        addRtBt.setEnabled(isEnabled);
    }


    class RecentLogin {
        String server;
        String userName;
        String password;

        public RecentLogin(String server, String userName, String password) {
            this.server = server;
            this.userName = userName;
            this.password = password;
        }

        public boolean equals(Object obj) {
            if (obj instanceof RecentLogin) {
                RecentLogin compared = (RecentLogin) obj;
                if (server.equals(compared.server) && userName.equals(compared.userName) && password.equals(compared.password)) {
                    return true;
                }
            }
            return false;
        }
    }
}
