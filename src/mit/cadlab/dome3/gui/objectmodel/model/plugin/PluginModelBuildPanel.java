//PluginModelBuildPanel.java

package mit.cadlab.dome3.gui.objectmodel.model.plugin;

import mit.cadlab.dome3.gui.guiutils.msg.MessageLogDialog;
import mit.cadlab.dome3.gui.guiutils.msg.MessageLogDialogLogHandler;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.*;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoEditorDialog;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoRendererTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.tools.MappingsBuildPanel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.PluginFilesContextBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PluginModelBuildPanel extends AbstractDomeObjectGui {

    protected static GridBagConstraints gbc;

    public static final TypeInfo TYPE_INFO = new TypeInfo("PluginModelBuildPanel");
    public static final String XML_TAG = "pluginmodelbuildpanel";

    public static final String DEFINITION = "definition";
    public static final String CAUSALITY = "causality";
    public static final String SETUP = "setup";

    protected PluginModelBuilder modelBuilder;
    protected ParameterNameListener nameListener; // update causality table when parameter name changes
    protected JTextField fileNameField;
    protected NameTextField nameField;
    protected JButton messageLogButton;
    protected JTabbedPane contentTabs;
    protected PluginModelDefinitionBuildPanel defPanel;
    protected CausalityInfoRendererTable dependencyTable;
    protected JButton editCausalityButton;
    protected DocumentationBuildPanel docPanel;
    protected MessageLogDialog messageLog = null;
    protected MappingsBuildPanel mappingTool = null;
    protected DomeFrame mappingsFrame = null;

    protected DFrame interfacesFrame = null;

    protected PluginFilesContextBuilder pluginfiles;


    public PluginModelBuildPanel(PluginModelBuilder modelBuilder) {
        super(modelBuilder);
        this.modelBuilder = modelBuilder;
        pluginfiles = new PluginFilesContextBuilder(modelBuilder);

        this.modelBuilder.addPropertyChangeListener(DomeModelBuilder.FILENAME,
                new PluginModelBuildPanel.FileNameListener());
        PropertyChangeListener propertyListener = getPropertyListener();
        this.modelBuilder.addPropertyChangeListener(propertyListener);
        nameListener = new ParameterNameListener();
        Iterator params = modelBuilder.getParameters().iterator();
        while (params.hasNext()) {
            Parameter p = (Parameter) params.next();
            p.addPropertyChangeListener(ModelObject.NAME, nameListener);
        }
        createComponents();

    }

    public String getPluginTypeName() {
        return modelBuilder.getPluginTypeName();
    }

    protected void createComponents() {
        nameField = new NameTextField();
        nameField.setDomeObject(modelBuilder);
        messageLogButton = Templates.makeButton("message log", new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                messageLog.show();
            }
        });
        contentTabs = Templates.makeTabbedPane();
        String[] columnNames = new String[]{"name", "value", modelBuilder.getPluginConfiguration().getMappingColumnName()};
        int[] columnWidths = new int[]{150, 150, modelBuilder.getPluginConfiguration().getMappingColumnSize()};
        defPanel = new PluginModelDefinitionBuildPanel(modelBuilder, 3,
                columnNames, columnWidths);
        contentTabs.addTab(DEFINITION, defPanel);

        JPanel causalityPanel = makeCausalityPanel();
        contentTabs.addTab(CAUSALITY, causalityPanel);

        JPanel setupPanel = makeSetupPanel();
        contentTabs.addTab(SETUP, setupPanel);

        docPanel = new DocumentationBuildPanel(modelBuilder.getDocumentation());
//  contentTabs.addTab("auxiliary files",new AuxFilesManagerPanel(modelBuilder));
        contentTabs.addTab("documentation", docPanel);
        contentTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setMenuContext();
            }
        });
        fileNameField = Templates.makeTextField(modelBuilder.getFileName());
        fileNameField.setEditable(false);
        fileNameField.setBackground(new Color(105, 105, 105));
        layoutComponent();
    }

    protected void layoutComponent() {
        JComponent[] comps = {makeControlPanel(), contentTabs, fileNameField};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBagB(this, comps, gbcs);
    }

    protected JPanel makeControlPanel() {
        JPanel p = new JPanel();
        JComponent[] comps = {Templates.makeLabel("name:"),
                              nameField,
                              messageLogButton
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    protected JPanel makeCausalityPanel() {
        JPanel p = new JPanel();
        dependencyTable = new CausalityInfoRendererTable(modelBuilder.getParameters(),
                modelBuilder.getDependencyInfo());
        editCausalityButton = Templates.makeButton("Edit causality information",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        DependencyInfo newInfo = CausalityInfoEditorDialog.showEditor(PluginModelBuildPanel.this,
                                modelBuilder.getParameters(), modelBuilder.getDependencyInfo());
                        if (newInfo != null)
                            modelBuilder.setDependencyInfo(newInfo);
                    }
                });
        if (modelBuilder.getParameterCount() < 2)
            editCausalityButton.setEnabled(false);
        modelBuilder.getFilter(DomeModel.PARAMETERS_FILTER).addFilterListener(new ParametersListener());
        JComponent[] comps = {dependencyTable,
                              editCausalityButton
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBagB(p, comps, gbcs);
        return p;
    }

    protected void resetCausalityButton() {
        editCausalityButton.setEnabled(modelBuilder.getParameterCount() > 1);
    }

    protected void createMessageLog() {
        messageLog = new MessageLogDialog(this);
        messageLog.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
            }
        });
        modelBuilder.setLogHandler(new MessageLogDialogLogHandler(modelBuilder, messageLog));
    }

    public void addNotify() {
        super.addNotify();
        createMessageLog(); // at this time, frame will be available
    }

    // DomeObjectGui interface
    public String getTitlePrefix() {
        return modelBuilder.getPluginTypeName() + ": ";
    }

    public void setMenuContext() {
        switch (contentTabs.getSelectedIndex()) {
            case 0: // definition
                defPanel.setMenuContext();
                return;
            case 3: // documentation
                MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL_DOCUMENTATION);
                break;
            default: // default for other tabs
                MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
        }
        BuildFocusTracker.notifyInFocus(this, modelBuilder);
    }

    public String getHelpContext() {
        return null;
    }
    // end DomeObjectGui interface

    public Model getModel() {
        return modelBuilder;
    }

    protected PropertyChangeListener getPropertyListener() {
        return new PluginModelPropertyChangeListener();
    }

    class PluginModelPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            //Object newValue = e.getNewValue();
            if (property.equals(PluginModel.DEPENDENCY_INFO)) {
                //dependencyTable.setDependencyInfo(new ArrayList(modelBuilder.getModelObjects()), (DependencyInfo) newValue);
                DependencyInfo dInfo = modelBuilder.getDependencyInfo();
                dependencyTable.setDependencyInfo(modelBuilder.getParameters(), dInfo);
            }
        }
    }

    class ParameterNameListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            if (property.equals(ModelObject.NAME)) {
                // update causality table
                Object obj = e.getSource();
                DependencyInfo dInfo = modelBuilder.getDependencyInfo();
                //Qing: why not intermediate parameter be taken care of?
                //if (dInfo.getCausality(obj) == CausalityStatus.INDEPENDENT ||
                //        dInfo.getCausality(obj) == CausalityStatus.RESULT) {
                if (dInfo.getCausality(obj) == CausalityStatus.INDEPENDENT ||
                        dInfo.getCausality(obj) == CausalityStatus.RESULT ||
                        dInfo.getCausality(obj) == CausalityStatus.INTERMEDIATE) {
                    dependencyTable.setDependencyInfo(modelBuilder.getParameters(), dInfo);
                }
            }
        }
    }

    // --- focus tracking support --------------------
    public static abstract class PluginFocusTrackerAction extends AbstractAction {

        public PluginFocusTrackerAction(String name) {
            super(name);
        }

        /**
         * Determines the component to use for the action.
         * This if fetched from the source of the ActionEvent
         * if it's not null and can be narrowed.  Otherwise,
         * the last focused component is used.
         *
         * @param e the ActionEvent
         * @return the component
         */
        protected final DomeBuildFrame getPluginModelBuildFrame(ActionEvent e) {
            DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
            DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
            if (modelGui instanceof PluginModelBuildPanel)
                return modelFrame;
            throw new NullPointerException("No current PluginModelBuildFrame");
        }

        protected final PluginModelBuildPanel getPluginModelBuildPanel(ActionEvent e) {
            DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
            DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
            if (modelGui instanceof PluginModelBuildPanel)
                return (PluginModelBuildPanel) modelGui;
            throw new NullPointerException("No current PluginModelBuildPanel");
        }
    }

    // --- actions for menus and buttons --------------------
    public static void EnableLoopChecking(boolean enable) {
        DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
        DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
        if (modelGui instanceof PluginModelBuildPanel)
            if (enable) {
                ((PluginModelBuildPanel) modelGui).modelBuilder.setLoopChecking(true);
            } else {
                ((PluginModelBuildPanel) modelGui).modelBuilder.setLoopChecking(false);
            }
        else
            return;//do nothing
    }


    public static final AbstractAction newAction = new PluginFocusTrackerAction("New") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.newModel(getPluginModelBuildPanel(e).getPluginTypeName());
        }
    };

    public static final AbstractAction openAction = new PluginFocusTrackerAction("Open...") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.openModel(getPluginModelBuildPanel(e).getPluginTypeName(), null);
        }
    };

    public static final AbstractAction saveAction = new PluginFocusTrackerAction("Save") {
        public void actionPerformed(ActionEvent e) {
            getPluginModelBuildPanel(e).save(false);
        }
    };

    public static final AbstractAction saveAsAction = new PluginFocusTrackerAction("Save as...") {
        public void actionPerformed(ActionEvent e) {
            getPluginModelBuildPanel(e).saveAs(false);
        }
    };

    public static final AbstractAction closeAction = new PluginFocusTrackerAction("Close") {
        public void actionPerformed(ActionEvent e) {
            if (!getPluginModelBuildPanel(e).isCausalityDefined()) {
                //pop up warning
                String msg = "The causality has not been defined\n" +
                        "in this model";
                int button = TwoButton1Msg.showOption(getPluginModelBuildPanel(e), "Option: Undefined causality", msg, "define it now",
                        "close model", new Dimension(230, 80));
                if (button == 1) return;
            }
            getPluginModelBuildPanel(e).close();
            getPluginModelBuildFrame(e).selfClose();
        }
    };

    public static final AbstractAction testAction = new PluginFocusTrackerAction("Test Model") {
        public void actionPerformed(ActionEvent e) {
            getPluginModelBuildPanel(e).test();
        }
    };
    public static final AbstractAction mapAction = new PluginFocusTrackerAction("Mappings") {
        public void actionPerformed(ActionEvent e) {
            getPluginModelBuildPanel(e).showMappings(null);
        }
    };

    public static final AbstractAction interfacesAction = new PluginFocusTrackerAction("Interfaces") {
        public void actionPerformed(ActionEvent e) {
            getPluginModelBuildPanel(e).showInterfaces();
        }
    };

    public void showMappings(Object paramOrRel) {
        if (mappingTool == null) {
            mappingsFrame = MappingsBuildPanel.createMappingTool(modelBuilder.getMappingManager());
            mappingTool = (MappingsBuildPanel) mappingsFrame.getGui();
            mappingsFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent event) {
                    mappingTool = null;
                    mappingsFrame = null;
                }
            });
        }

        updateMappings(paramOrRel);

        mappingsFrame.show();
    }

    public void updateMappings(Object paramOrRel) {
        if (mappingTool != null) {
            if (paramOrRel instanceof Relation) {
                mappingTool.setCurrentRelation((Relation) paramOrRel);
            } else if (paramOrRel instanceof Parameter) {
                mappingTool.setCurrentParameter((Parameter) paramOrRel);
            }
        }
    }

    public void showInterfaces() {
        if (interfacesFrame == null) {
            ModelInterfaceManagerBuilder iBuilder = (ModelInterfaceManagerBuilder) modelBuilder.getModelInterfacesManager();
            interfacesFrame = ModelInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_PLUGINMODEL_INTERFACES);
            interfacesFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent event) {
                    interfacesFrame = null;
                }
            });
        }
        interfacesFrame.show();
    }

    public static final JMenu menu = makeMenu();
    public static final JMenu toolsMenu = makeToolsMenu();

    protected static JMenu makeMenu() {
        String type = "Plugin Model";
        JMenu m = MenuUtils.makeBoldMenu(type);
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.newAction));
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.openAction));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.saveAction));
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.saveAsAction));
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.closeAction));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.testAction));
        return m;
    }

    protected static JMenu makeToolsMenu() {
        JMenu m = MenuUtils.makeBoldMenu("Tools");
        m.add(MenuUtils.makeCheckBoxMenuItem("Automatic loop checking", true, new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED)
                    PluginModelBuildPanel.EnableLoopChecking(false);
                else
                    PluginModelBuildPanel.EnableLoopChecking(true);
            }
        }));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.mapAction));
        m.add(MenuUtils.makeMenuItem(PluginModelBuildPanel.interfacesAction));
        return m;
    }


    public void save(boolean closeAfterSave) {
        saveAuxFilesFromGui();
        modelBuilder.save(closeAfterSave);
    }

    public void saveAs(boolean closeAfterSave) {
        saveAuxFilesFromGui();
        modelBuilder.saveAs(closeAfterSave);
    }

    protected static final Dimension NOT_SAVED_WARNING_SIZE = new Dimension(280, 90);

    public boolean isCausalityDefined() {
        return !((modelBuilder.getShouldSave() && !modelBuilder.isSaved()) && (modelBuilder.getDependencyInfo().isEmpty()));
    }

    public void close() {
        //check if plugin causality being defined or not
        //Qing 19:check if causality is defined or not

//Qing Jan 25th: the following code only checks for model xml representations,for the current implementation auxiliary file is implemented, we should check whether the auxiliary file is changed
        if ((modelBuilder.getShouldSave() && !modelBuilder.isSaved()) || (modelBuilder.isAuxiliaryFilesChanged())) {
            int answer = TwoButton2Msg.showWarning(this, "Warning: unsaved changes", "has not been saved", modelBuilder.getName(),
                    "save now", "skip save", NOT_SAVED_WARNING_SIZE);
            switch (answer) {
                case TwoButton2Msg.LEFT_OPTION:
                    // set closeAfterSave as true to call 'deleteAllConcreteParameters()' after saving
                    save(true);
                    break;
                default: // skip save
                    modelBuilder.setShouldSave(false);
                    // even when user chooses not to save, we need to call 'deleteAllConcreteParameters()' before window closed
                    DomeModelBuilder.deleteAllConcreteParameters(modelBuilder);
                    return;
            }
        } else {
            // when window closes without saving or skipping it, we need to call 'deleteAllConcreteParameters()'
            DomeModelBuilder.deleteAllConcreteParameters(modelBuilder);
        }
    }

    public void test() {
        System.out.println(modelBuilder.getName() + ":test2");
    }


    protected class FileNameListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(DomeModelBuilder.FILENAME))
                fileNameField.setText(evt.getNewValue().toString());
        }
    }

    class ParametersListener implements DListListener {
        public void intervalChanged(DListEvent e) {
            // do nothing
        }

        public void intervalAdded(DListEvent e) {
            addItems(e.getItems());
        }

        public void intervalRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsReplaced(DListEvent e) {
            // not supported
        }

        protected void addItems(List items) {
            Iterator it = items.iterator();
            while (it.hasNext()) {
                Parameter p = (Parameter) it.next();
                p.addPropertyChangeListener(ModelObject.NAME, nameListener);
            }
            if (modelBuilder.getParameterCount() > 1 && !editCausalityButton.isEnabled())
                editCausalityButton.setEnabled(true);
        }

        protected void removeItems(List items) {
            Iterator it = items.iterator();
            while (it.hasNext()) {
                Parameter p = (Parameter) it.next();
                p.removePropertyChangeListener(ModelObject.NAME, nameListener);
            }
            if (modelBuilder.getParameterCount() < 2 && editCausalityButton.isEnabled())
                editCausalityButton.setEnabled(false);
        }
    }

    protected JPanel makeSetupPanel() {
        JPanel pane = new JPanel();

        List configure_parameters = modelBuilder.getPluginConfiguration().getSetupParameters();
        ArrayList more = new ArrayList(configure_parameters);
        more.add(0, (DefaultContextBuilder) pluginfiles);
        BuildTree tree = new BuildTree(more);
        String[] columnNames = new String[]{"name", "value"};
        int[] columnWidths = new int[]{60, 160};
        BuildTreeTable table = new BuildTreeTable(tree, columnNames.length, columnNames, columnWidths, true);
        GridBagConstraints[] gbcs = {
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.white);
        Dimension d = table.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(d.width, 200));
        JComponent[] components = {scrollPane};
        Templates.layoutGridBagB(pane, components, gbcs);
        return pane;
    }


    protected void saveAuxFilesFromGui() {
        pluginfiles.addAuxFileInfos();
        modelBuilder.setAuxiliaryFilesChanged(false);
    }
}
