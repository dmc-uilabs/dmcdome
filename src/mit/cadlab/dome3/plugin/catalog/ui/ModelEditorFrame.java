package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.DomeClientApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Dictionary;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 31.
 */
public class ModelEditorFrame {

    private static long BACKGROUND_SERVICE_ACTIVATION_PERIOD = 1500;
    private static ComponentReference compRef;
    private static EditorStatusBar statusBar;
    private static JFrame frame;
    private static JPanel toolBarPanel;
    private static boolean isStandAloneMode;
    private static JSlider cellConfigSlider;
    protected static ButtonModel switchTypeAndUnitQueryModeMenuItemModel;
    protected static ButtonModel switchTypeAndUnitQueryModeToggleButtonModel;
    protected static ButtonModel switchNavigationPanelMenuItemModel;
    protected static ButtonModel switchNavigationPanelToggleButtonModel;

    protected static ButtonModel switchEvalutionModeMenuItemModel;
    protected static ButtonModel switchEvalutionModeToggleButtonModel;

    private static void initStandardToolbar(JFrame frame) {

        JToolBar standardToolBar = new JToolBar("Standard Toolbar");

        JButton button = null;
        JLabel label = null;

        label = makeToolbarLabel(" File ");
        //standardToolBar.add(label);

        button = makeToolbarButton(ModelEditorKit.NewModelAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.OpenAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.SaveAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.CloseAction);
        standardToolBar.add(button);

        standardToolBar.addSeparator(new Dimension(15, 24));
        label = makeToolbarLabel("Edit ");
        //standardToolBar.add(label);
        button = makeToolbarButton(ModelEditorKit.AddRelAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.DeleteRelationOrCellAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.EditRelationAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.MoveUpRelationAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.MoveDownRelationAction);
        standardToolBar.add(button);

        standardToolBar.addSeparator(new Dimension(15, 24));

        label = makeToolbarLabel("Simulate ");
        //standardToolBar.add(label);
        JToggleButton toggleButton = null;
        toggleButton = makeToolbarToggleButton(ModelEditorKit.SwitchEvaluationModeAction, false);
        switchEvalutionModeToggleButtonModel = toggleButton.getModel();
        standardToolBar.add(toggleButton);
        button = makeToolbarButton(ModelEditorKit.RunEvaluationAction);
        standardToolBar.add(button);
        button = makeToolbarButton(ModelEditorKit.StopEvaluationAction);
        standardToolBar.add(button);

        standardToolBar.addSeparator(new Dimension(15, 24));
        label = makeToolbarLabel("View ");
        //standardToolBar.add(label);

        button = makeToolbarButton(ModelEditorKit.UseSmallerCellAction);
        standardToolBar.add(button);
        JSlider slider = makeCellConfigSlider();
        standardToolBar.add(slider);
        button = makeToolbarButton(ModelEditorKit.UseLargerCellAction);
        standardToolBar.add(button);

        button = makeToolbarButton(ModelEditorKit.EditCellConfigAction);
        standardToolBar.add(button);

        toggleButton = makeToolbarToggleButton(ModelEditorKit.SwitchNavigationPanelAction, true);
        switchNavigationPanelToggleButtonModel = toggleButton.getModel();
        standardToolBar.add(toggleButton);

        toggleButton = makeToolbarToggleButton(ModelEditorKit.SwitchTypeAndUnitQueryModeAction, false);
        switchTypeAndUnitQueryModeToggleButtonModel = toggleButton.getModel();
        standardToolBar.add(toggleButton);

        standardToolBar.setFloatable(true);
        standardToolBar.setRollover(true);

        standardToolBar.setAlignmentX(0);
        toolBarPanel.add(standardToolBar);

        standardToolBar.setFocusable(false);

//        frame.getContentPane().add(toolBar, BorderLayout.PAGE_START);
    }

    protected static JButton makeToolbarButton(Action action) {
        JButton button = new JButton();
        button.setFont(UIUtil.TOOL_BUTTON_FONT);
        //button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.setAction(action);
        button.setText(null);
        button.setFocusable(false);
        //button.setMargin(new Insets(3, 3, 3, 3));
        return button;
    }

    public static JSlider getCellConfigSlider() {
        return cellConfigSlider;
    }

    protected static JSlider makeCellConfigSlider() {
        cellConfigSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, 2);
        cellConfigSlider.setMaximumSize(new Dimension(100, 50));
        cellConfigSlider.setFont(UIUtil.TOOL_BUTTON_FONT);
        cellConfigSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                JSlider source = (JSlider) event.getSource();
                if (compRef.getImplementationEditor().isImplLoaded()) {
                    int levelOfDetail = source.getValue();
                    compRef.getModelEditor().slideCellConfig(levelOfDetail);
                }
            }
        });

        Dictionary labelTable = cellConfigSlider.createStandardLabels(5, 0);
        labelTable.put(new Integer(0), UIUtil.createLabel("smallest"));
        labelTable.put(new Integer(10), UIUtil.createLabel("medium"));
        labelTable.put(new Integer(20), UIUtil.createLabel("largest"));
        cellConfigSlider.setLabelTable(labelTable);
        cellConfigSlider.setMajorTickSpacing(5);
        cellConfigSlider.setMinorTickSpacing(1);
        cellConfigSlider.setPaintTicks(false);
        cellConfigSlider.setPaintTrack(true);
        cellConfigSlider.setPaintLabels(false);
        cellConfigSlider.setSnapToTicks(true);
        cellConfigSlider.setFocusable(false);

        return cellConfigSlider;
    }

    protected static JToggleButton makeToolbarToggleButton(Action action, boolean isSelected) {
        JToggleButton button = new JToggleButton();
        button.setFont(UIUtil.TOOL_BUTTON_FONT);
        button.setSelected(isSelected);
        button.setAction(action);
        button.setText(null);
        button.setFocusable(false);

        return button;
    }

    protected static JLabel makeToolbarLabel(String text) {
        JLabel label = new JLabel();
        label.setFont(UIUtil.TOOL_BUTTON_FONT);
        label.setText(text);
        return label;
    }

    private static void initStatusBar(JFrame frame) {
        statusBar = new EditorStatusBar();
        frame.getContentPane().add(statusBar, BorderLayout.PAGE_END);
    }

    public static void setFrameTitle(String filePath) {
        if (filePath != null) {
            frame.setTitle("C-Model Builder - [" + filePath + "] - 'Excel for Engineers'");
        } else {
            frame.setTitle("C-Model Builder - 'Excel for Engineers'");
        }
    }

    public static boolean isStandAloneMode() {
        return isStandAloneMode;
    }

    public static void setStatusMessage(String msg) {
        statusBar.setMessage(msg);
    }

    private static JMenuItem createMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setFont(UIUtil.MENU_BAR_FONT);
        item.setIcon(null);
        return item;
    }

    private static JCheckBoxMenuItem createCheckBoxMenuItem(ModelEditorKit.BaseAction action, boolean isSelected) {
        JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(action);
        cbMenuItem.setFont(UIUtil.MENU_BAR_FONT);
        cbMenuItem.setIcon(null);
        cbMenuItem.setSelected(isSelected);
        return cbMenuItem;
    }

    private static void initMenuToolbar(JFrame frame) {
        //Where the GUI is created:
        JMenuBar menuBar;
        JMenu menu;
        JCheckBoxMenuItem cbMenuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();


        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setFont(UIUtil.MENU_BAR_FONT);
        menuBar.add(menu);

        menu.add(createMenuItem(ModelEditorKit.NewModelAction));
        menu.add(createMenuItem(ModelEditorKit.OpenAction));
        menu.add(createMenuItem(ModelEditorKit.SaveAction));
        menu.add(createMenuItem(ModelEditorKit.SaveAsAction));
        menu.add(createMenuItem(ModelEditorKit.CloseAction));

        menu.addSeparator();

        menu.add(createMenuItem(ModelEditorKit.ExitAction));


        //Build the first menu.
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.setFont(UIUtil.MENU_BAR_FONT);
        menuBar.add(menu);

//        menu.add(createMenuItem(cutAction));
//        menu.add(createMenuItem(copyAction));
//        menu.add(createMenuItem(pasteAction));
        menu.add(createMenuItem(ModelEditorKit.AddRelAction));
        menu.add(createMenuItem(ModelEditorKit.EditRelationAction));
        menu.add(createMenuItem(ModelEditorKit.DeleteRelationOrCellAction));
        menu.addSeparator();

        menu.add(createMenuItem(ModelEditorKit.MoveUpRelationAction));
        menu.add(createMenuItem(ModelEditorKit.MoveDownRelationAction));
        menu.addSeparator();

        menu.add(createMenuItem(ModelEditorKit.AddImplAction));
        menu.add(createMenuItem(ModelEditorKit.AddItfAction));
        menu.add(createMenuItem(ModelEditorKit.EditInterfaceAction));
        menu.add(createMenuItem(ModelEditorKit.RemoveImplOrItfAction));

        menu.addSeparator();
        menu.add(createMenuItem(ModelEditorKit.SelectAllRelationsAction));
        menu.add(createMenuItem(ModelEditorKit.UnselectAllRelationsAction));

        menu.addSeparator();
        menu.add(createMenuItem(ModelEditorKit.EditModelAction));


//        menu.add(createMenuItem(ModelEditorKit.RemoveImplAction));
//        menu.add(createMenuItem(ModelEditorKit.RemoveItfAction));

        menu = new JMenu("Simulate");
        menu.setMnemonic(KeyEvent.VK_S);
        menu.setFont(UIUtil.MENU_BAR_FONT);
        menuBar.add(menu);
        cbMenuItem = createCheckBoxMenuItem(ModelEditorKit.SwitchEvaluationModeAction, false);
        switchEvalutionModeMenuItemModel = cbMenuItem.getModel();
        menu.add(cbMenuItem);

        menu.add(createMenuItem(ModelEditorKit.RunEvaluationAction));
        menu.add(createMenuItem(ModelEditorKit.StopEvaluationAction));


        //Build the first menu.
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.setFont(UIUtil.MENU_BAR_FONT);
        menuBar.add(menu);

        cbMenuItem = createCheckBoxMenuItem(ModelEditorKit.SwitchCellWrapAction, true);
        menu.add(cbMenuItem);
        menu.add(createCheckBoxMenuItem(ModelEditorKit.SwitchNavigationPanelAction, true));
        switchNavigationPanelMenuItemModel = cbMenuItem.getModel();
        cbMenuItem = createCheckBoxMenuItem(ModelEditorKit.SwitchTypeAndUnitQueryModeAction, false);
        switchTypeAndUnitQueryModeMenuItemModel = cbMenuItem.getModel();
        menu.add(cbMenuItem);
        
        menu.add(createCheckBoxMenuItem(ModelEditorKit.SwitchDebugConsoleAction, false));

        menu.addSeparator();

        menu.add(createMenuItem(ModelEditorKit.EditCellConfigAction));
        menu.add(createMenuItem(ModelEditorKit.UseSmallerCellAction));
        menu.add(createMenuItem(ModelEditorKit.UseLargerCellAction));


        menu.addSeparator();
        menu.add(createMenuItem(ModelEditorKit.ExecuteImplAction));

        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.setFont(UIUtil.MENU_BAR_FONT);
        menuBar.add(menu);
        //menu.add(createMenuItem(ModelEditorKit.EditCellConfigAction));

        menuBar.add(Box.createHorizontalGlue());

        JToolBar menuToolBar = new JToolBar("Menu Toolbar");
        //menuBar.setBorder(null);
        menuToolBar.add(menuBar);
        menuToolBar.setAlignmentX(0);
        toolBarPanel.add(menuToolBar);

//        frame.setJMenuBar(menuBar);
    }

    /** if isStandAloneMode is true, closing window will shutdown JVM. while the other case would just dispose the current window. */
    public static void createAndShowGUI(boolean isStandAloneMode) {
        ModelEditorFrame.isStandAloneMode = isStandAloneMode;
        try {
            String laf = System.getProperty("swing.defaultlaf");
            if(laf == null) {
                laf = UIManager.getSystemLookAndFeelClassName();
            }
            UIManager.setLookAndFeel(laf);
            UIManager.put("ToolTip.font", UIUtil.PARAM_NAME_FONT);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        UIUtil.updateCellGeometryParameters(CellConfig.createDefaultCellConfig());


        if (isStandAloneMode) {
            DomeClientApplication.DOME_API_MODE = true;
            UIUtil.initUnit();
        }

        ModelEditor modelEditor = new ModelEditor();
        compRef = modelEditor.getComponentReference();
        //JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame();
        setFrameTitle(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                ModelEditorKit.ExitAction.actionPerformed(new ActionEvent(this, 0, "WindowClosing"));
            }
        });

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.PAGE_AXIS));
        frame.getContentPane().add(toolBarPanel, BorderLayout.PAGE_START);

        Container contentPane = frame.getContentPane();
        contentPane.add(modelEditor, BorderLayout.CENTER);

        initSelectionAccelerators(modelEditor);


        initMenuToolbar(frame);
        initStandardToolbar(frame);
        initStatusBar(frame);

        ModelEditorKit.setEnabledOfAllActions(false);
        ModelEditorKit.setComponentReferenceOfAllActions(compRef);

        frame.setIconImage(UIUtil.createImageIcon("images/greenDomeWindow.gif").getImage());

        frame.pack();

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        frame.setMaximizedBounds(env.getMaximumWindowBounds());

        /* topMargin is to create space for showing Menu of Dome Builder */
        int topMargin = (isStandAloneMode ? 0 : 48);

        Rectangle screen = env.getMaximumWindowBounds();
        int prefWidth = (screen.getWidth() < 1024) ? screen.width : 1024;
        int prefHeight = (screen.getHeight() < 768) ? screen.height - topMargin : 768 - topMargin;
        frame.setBounds(0, topMargin, prefWidth, prefHeight);

        //frame.setLocationByPlatform(true);
        Rectangle bounds = frame.getBounds();

        if (isStandAloneMode && bounds.width < screen.width && bounds.height < screen.height) {
            frame.setLocation((screen.width - frame.getBounds().width) / 2, (screen.height - frame.getBounds().height) / 2);
        }

        /* start UI timer */
        java.util.Timer uiTimer = new java.util.Timer(true);
        uiTimer.schedule(new UITimerTask(modelEditor), BACKGROUND_SERVICE_ACTIVATION_PERIOD, BACKGROUND_SERVICE_ACTIVATION_PERIOD);


        frame.setVisible(true);
    }

    private static void bindModelEditorAction(ModelEditorKit.BaseAction action, ModelEditor modelEditor) {
        String actionName = (String) action.getValue(AbstractAction.NAME);
        KeyStroke accelKey = (KeyStroke) action.getValue(AbstractAction.ACCELERATOR_KEY);
        modelEditor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelKey, actionName);
        modelEditor.getActionMap().put(actionName, action);

        compRef.getImplementationEditor().getInputMap(JComponent.WHEN_FOCUSED).put(accelKey, actionName);
        compRef.getImplementationEditor().getActionMap().put(actionName, action);

    }

    private static void initSelectionAccelerators(ModelEditor modelEditor) {
        bindModelEditorAction(ModelEditorKit.MoveUpSelectionCursorAction, modelEditor);
        bindModelEditorAction(ModelEditorKit.MoveDownSelectionCursorAction, modelEditor);
        bindModelEditorAction(ModelEditorKit.MoveLeftSelectionCursorAction, modelEditor);
        bindModelEditorAction(ModelEditorKit.MoveRightSelectionCursorAction, modelEditor);

        bindModelEditorAction(ModelEditorKit.OpenMappingScriptEditorAction, modelEditor);

        bindModelEditorAction(ModelEditorKit.ExpandUpwardRelationAction, modelEditor);
        bindModelEditorAction(ModelEditorKit.ExpandDownwardRelationAction, modelEditor);

        bindModelEditorAction(ModelEditorKit.SelectAllRelationsAction, modelEditor);
        bindModelEditorAction(ModelEditorKit.UnselectAllRelationsAction, modelEditor);

        bindModelEditorAction(ModelEditorKit.StartTypeAndUnitQueryModeAction, modelEditor);
        bindModelEditorAction(ModelEditorKit.ExitTypeAndUnitQueryModeAction, modelEditor);

    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(true);
            }
        });
    }

    public static ComponentReference getComponentReference() {
        return compRef;
    }

    public static JFrame getFrame() {
        return frame;
    }
}