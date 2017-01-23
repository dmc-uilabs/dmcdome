// DomeModelBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.model.dome;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.msg.*;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.*;
import mit.cadlab.dome3.gui.objectmodel.model.ModelBuilder;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextBuilderMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextBuilderTreeObject;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.subscription.SubscribeDialog;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.tools.MappingsBuildPanel;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.integrationwizards.integrationwizard.IntegrationWizardFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;


public class DomeModelBuildPanel extends AbstractDomeObjectGui implements Saveable {

    protected static GridBagConstraints gbc;

    public static final TypeInfo TYPE_INFO = new TypeInfo("DomeModelBuildPanel");
    public static final String XML_TAG = "domemodelbuildpanel";

    private static final String DOME_MODEL_TITLE = "Dome Model: ";
    private static final String IMODEL_TITLE = "Integration Model: ";

    protected DomeModelBuilder modelBuilder;
    protected NameTextField nameField;
    protected JButton messageLogButton;
    protected JTabbedPane contentTabs;
    protected DomeModelDefinitionBuildPanel defPanel;
    protected DocumentationBuildPanel docPanel;
    protected JTextField fileNameField;
    protected MappingsBuildPanel mappingTool = null;
    protected DFrame interfacesFrame = null;
    protected IntegrationWizardFrame autoMapFrame = null;
    protected DFrame projectInterfacesFrame = null;
    protected DomeFrame mappingsFrame = null;
    protected MessageLogDialog messageLog = null;
    protected boolean isClipboardEmpty;

    public DomeModelBuildPanel(DomeModelBuilder modelBuilder) {
        super(modelBuilder);
        this.modelBuilder = modelBuilder;
        this.modelBuilder.addPropertyChangeListener(DomeModelBuilder.FILENAME, new FileNameListener());
        createComponents();
        if (modelBuilder.isIntegrationModel()) {
            BuildMode.clipboard.addClipboardListener(new ClipboardListener());
            updateClipboardStatus();
        }
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
        defPanel = new DomeModelDefinitionBuildPanel(modelBuilder);
        docPanel = new DocumentationBuildPanel(modelBuilder.getDocumentation());
        contentTabs.addTab("definition", defPanel);
        JPanel setupPanel = makeSetupPanel();
        contentTabs.addTab("setup", setupPanel);
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

    protected JPanel makeSetupPanel() {
        JPanel pane = new JPanel();
        ArrayList more = new ArrayList();
        more.add(0, modelBuilder.getFileContext());
        BuildTree tree = new BuildTree(more);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent te) {
                TreePath path = te.getNewLeadSelectionPath();
                if (path == null) {
                    addFileAction.setEnabled(false);
                    return;
                }
                Object obj = path.getLastPathComponent();
                if (obj instanceof BuildObjectTreeNode) {
                    Object uObj = ((BuildObjectTreeNode) obj).getUserObject();
                    if (uObj instanceof ContextBuilderTreeObject) {
                        Object dObj = ((ContextBuilderTreeObject) uObj).getDomeObject();
                        if (modelBuilder.getFileContext().equals(dObj))
                            addFileAction.setEnabled(true);
                        else
                            addFileAction.setEnabled(false);
                    } else
                        addFileAction.setEnabled(false);
                } else
                    addFileAction.setEnabled(false);
            }
        });
        String[] columnNames = new String[]{"name", "value"};
        int[] columnWidths = new int[]{60, 160};
        BuildTreeTable table = new BuildTreeTable(tree, columnNames.length, columnNames, columnWidths, false);
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

    public void addNotify() {
        super.addNotify();
        createMessageLog(); // at this time, frame will be available
    }

    // DomeObjectGui interface
    public String getTitlePrefix() {
        return modelBuilder.isIntegrationModel() ? IMODEL_TITLE : DOME_MODEL_TITLE;
    }

    public String getHelpContext() {
        return null;
    }

    public void setMenuContext() {
        switch (contentTabs.getSelectedIndex()) {
            case 0: // definition
                defPanel.setMenuContext();
                return;
            case 1: //setup
                MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_SETUP);
                break;
            case 2: // documentation
                if (modelBuilder.getIntegrationProject() != null)
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
                else
                    MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION);
                break;
            default: // default for other tabs
                if (modelBuilder.getIntegrationProject() != null)
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT);
                else
                    MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
        }
        BuildFocusTracker.notifyInFocus(this, modelBuilder);
    }

    protected void createMessageLog() {
        messageLog = new MessageLogDialog(this);
        messageLog.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                if (modelBuilder.getIntegrationProject() != null)
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT);
                else
                    MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
            }
        });
        modelBuilder.setLogHandler(new MessageLogDialogLogHandler(modelBuilder, messageLog));
    }

    public Model getModel() {
        return modelBuilder;
    }

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
            if (modelBuilder.getIntegrationProject() != null)
                interfacesFrame = ModelInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_PROJECTMODEL_INTERFACES);
            else
                interfacesFrame = ModelInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_DOMEMODEL_INTERFACES);

            interfacesFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent event) {
                    interfacesFrame = null;
                }
            });
        }
        interfacesFrame.show();
    }

    public void showProjectInterfaces() {
        final IntegrationProjectBuilder project = (IntegrationProjectBuilder) modelBuilder.getIntegrationProject();
        if (project != null) {
            projectInterfacesFrame = ProjectBuildPanel.getFromProjectInterfaceMgrPanelMap(project);
            if (projectInterfacesFrame == null) {
                ModelInterfaceManagerBuilder iBuilder = (ModelInterfaceManagerBuilder) project.getProjectInterfacesManager();
                projectInterfacesFrame = ModelInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_PROJECT_INTERFACES);
                ProjectBuildPanel.insetInProjectInterfaceMgrPanelMap(project, projectInterfacesFrame);
                projectInterfacesFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent event) {
                        projectInterfacesFrame = null;
                        ProjectBuildPanel.removeFromProjectInterfaceMgrPanelMap(project);
                    }
                });
            }
        } else {
            throw new RuntimeException("Error creating project interface manager tool");
        }
        projectInterfacesFrame.show();
    }

    public void save(boolean closeAfterSave) {
        if (modelBuilder.isIntegrationModel())
            ((ProjectBuildPanel) ((DomeBuildFrame) BuildMode.getWindowTracker(modelBuilder)).getGui()).save(closeAfterSave);
        else {
            /*JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, getDomeObject().getName(), getStatusWindowLocation());
            DomeModelBuildPanel.saveModelWorker worker = new DomeModelBuildPanel.saveModelWorker(modelBuilder, waitWin);
            worker.start();*/
            modelBuilder.save(closeAfterSave);
        }
    }

    public void saveAs(boolean closeAfterSave) {
        if (modelBuilder.isIntegrationModel()) {
            ((ProjectBuildPanel) ((DomeBuildFrame) BuildMode.getWindowTracker(modelBuilder)).getGui()).saveAs(closeAfterSave);
        } else {
            /*JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, getDomeObject().getName(), getStatusWindowLocation());
            DomeModelBuildPanel.saveAsModelWorker worker = new DomeModelBuildPanel.saveAsModelWorker(modelBuilder, waitWin);
            worker.start();*/
            modelBuilder.saveAs(closeAfterSave);
        }
    }

    protected static final Dimension NOT_SAVED_WARNING_SIZE = new Dimension(300, 100);

    public void close() {
        if (modelBuilder.isIntegrationModel()) {
            return; // ok since model will be saved later
        }

        // remove javaPython reference to data object in the relation
        Relation rel = modelBuilder.getRelationToTest();
        if (rel != null) {
            ((AbstractProceduralRelation) rel).clearRelationExecutor();
        }

        Component Parent = SwingUtilities.getRoot(this);
        if (Parent instanceof JFrame)
        //WaitCursorUtils.setWaitCursor((JFrame)Parent,true);
            WaitCursorUtils.showWaitCursor(true, this);

        if (modelBuilder.getShouldSave() && !modelBuilder.isSaved()) {
            int answer = TwoButton2Msg.showOption(DomeModelBuildPanel.this, "Warning: unsaved changes",
                    "has not been saved", modelBuilder.getName(),
                    "save now", "skip save", NOT_SAVED_WARNING_SIZE);
            switch (answer) {
                case TwoButton2Msg.LEFT_OPTION:
                    // set closeAfterSave as true to call 'deleteAllConcreteParameters()' after saving
                    save(true);
                    break;
                default: // skip save
                    modelBuilder.setShouldSave(false);
                    if (Parent instanceof JFrame)
                        WaitCursorUtils.setWaitCursor((JFrame) Parent, false);
                    // even when user chooses not to save, we need to call 'deleteAllConcreteParameters()' before window closed
                    DomeModelBuilder.deleteAllConcreteParameters(modelBuilder);
                    return;
            }
        } else {
            // when window closes without saving or skipping it, we need to call 'deleteAllConcreteParameters()'
            DomeModelBuilder.deleteAllConcreteParameters(modelBuilder);
        }
        if (Parent instanceof JFrame)
            WaitCursorUtils.setWaitCursor((JFrame) Parent, false);

        messageLog.clearMessages();
    }

    public void test() {
        System.out.println(modelBuilder.getName() + ":test2");
    }

    public void testRelation() {
        Relation rel = modelBuilder.getRelationToTest();
        if (rel != null) {
            try {
                ((AbstractProceduralRelation) rel).execute();
            } catch (RelationExecutionException ex) {
                System.err.println(ex.getMessage());
                OneButton1Msg.showRelationExecutionError(this, ex);
            }
        }
    }

    public void testIterationRelation() {
        Relation rel = modelBuilder.getRelationToTest();
        if (rel != null) {
            if (rel instanceof AbstractProceduralRelation)
                try {
                    for (int i = 0; i < 100; i++) {
                        ((AbstractProceduralRelation) rel).execute();
                    }

                } catch (RelationExecutionException ex) {
                    System.err.println(ex.getMessage());
                    OneButton1Msg.showRelationExecutionError(this, ex);
                }
        }


    }


    public void subscribe() {
        if (!modelBuilder.isIntegrationModel())
            return;
        SubscribeDialog.show(this, modelBuilder);
    }

    public void addAndSubscribe() {
        if (!modelBuilder.isIntegrationModel())
            return;
        ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
        if (sel == null) return; // nothing in clipboard!
        List items = sel.getItems();
        items = filterForValidItems(items);
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            Object o = iter.next();
            if (o instanceof BrowseInterface) {
                BrowseInterface ifaceInfo = (BrowseInterface) o;
                modelBuilder.subscribe(ifaceInfo.getServerConnection(), ifaceInfo.getInterface(),
                        ifaceInfo.getInterfaceId(), ifaceInfo.getVersion(), ifaceInfo.getParentId());
            } else if (o instanceof ModelInterfaceBuilder) {
                ModelInterfaceBuilder mi = (ModelInterfaceBuilder) o;
                modelBuilder.subscribe((ServerConnection) null, mi, mi.getId().getIdString(),
                        mi.getVersion().getMajorVersion(), mi.getModel().getId().getIdString());
            }
        }
    }



    public static List filterForValidItems(List items) {
        boolean showDailog = false;
        List filteredItems = new ArrayList();
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            if (obj instanceof BrowseInterface) {
                filteredItems.add(obj);
            } else if (obj instanceof ModelInterfaceBuilder) {
                filteredItems.add(obj);
            } else {
                showDailog = true;
            }
        }
        if (showDailog) {
            String msg = "Only resource or iModel interfaces can be subscribed.  All other copied objects are ignored.";
            OneButton1Msg.showWarning(BuildFocusTracker.getCurrentWindow(),
                    "Add and subscibre warning", msg, "OK", new Dimension(1, 1));
        }
        return filteredItems;
    }

    protected class FileNameListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(DomeModelBuilder.FILENAME))
                fileNameField.setText(evt.getNewValue().toString());
        }
    }

    protected void switchSubscriptionView(String view) {
        defPanel.switchSubscriptionView(view);
        setSubscriptionView(view);
    }

    // --- focus tracking support --------------------
    public static abstract class FocusTrackerAction extends AbstractAction {

        public FocusTrackerAction(String name) {
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
        protected final DomeBuildFrame getDomeModelBuildFrame(ActionEvent e) {
            DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
            DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
            if (modelGui instanceof DomeModelBuildPanel)
                return modelFrame;
            throw new NullPointerException("No current DomeModelBuildFrame");
        }

        protected final DomeModelBuildPanel getDomeModelBuildPanel(ActionEvent e) {
            DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
            DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
            if (modelGui instanceof DomeModelBuildPanel)
                return (DomeModelBuildPanel) modelGui;
            throw new NullPointerException("No current DomeModelBuildFrame");
        }
    }

    // --- actions for menus and buttons --------------------
    public static void EnableLoopChecking(boolean enable) {
        DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
        DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
        if (modelGui instanceof DomeModelBuildPanel) {
            if (enable) {
                ((DomeModelBuildPanel) modelGui).modelBuilder.setLoopChecking(true);
            } else {
                ((DomeModelBuildPanel) modelGui).modelBuilder.setLoopChecking(false);
            }
        } else
            return;//do nothing
    }

    public static final AbstractAction saveAction = new FocusTrackerAction("Save") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).save(false);
        }
    };

    public static final AbstractAction saveAsAction = new FocusTrackerAction("Save as...") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).saveAs(false);
        }
    };

    public static final AbstractAction closeAction = new FocusTrackerAction("Close") {
        public void actionPerformed(ActionEvent e) {
            // We should call close() before calling selfClose(), because also it will prompt user to choose if save or skip the change and also will call deleteAllConcreteParameter()
            ((DomeModelBuildPanel) getDomeModelBuildFrame(e).getGui()).close();
            getDomeModelBuildFrame(e).selfClose();
        }
    };

    public static final AbstractAction testAction = new FocusTrackerAction("Test Model") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).test();
        }
    };

    public static final AbstractAction testRelationAction = new FocusTrackerAction("Test Relation") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).testRelation();
        }
    };

    public static final AbstractAction mapAction = new FocusTrackerAction("Mappings") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).showMappings(null);
        }
    };

    public static final AbstractAction interfacesAction = new FocusTrackerAction("Interfaces") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).showInterfaces();
        }
    };

    public static final AbstractAction iModelInterfacesAction = new FocusTrackerAction("iModel Interfaces") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).showInterfaces();
        }
    };

    public static final AbstractAction iModelProjectInterfacesAction = new FocusTrackerAction("Project Interfaces") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).showProjectInterfaces();
        }
    };

    public static final AbstractAction subscriptionsAction = new FocusTrackerAction("Subscriptions...") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).subscribe();
        }
    };

    public static final AbstractAction addAndSubscribeAction = new FocusTrackerAction("Add and subscribe") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).addAndSubscribe();
        }
    };

    public static final AbstractAction viewBuildAction = new FocusTrackerAction("Build View") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).switchSubscriptionView(Subscription.BUILD_VIEW);
        }
    };

    public static final AbstractAction viewInterfaceCausalityAction = new FocusTrackerAction("Interface Causality View") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).switchSubscriptionView(Subscription.INPUT_OUTPUT_VIEW);
        }
    };

    public static final AbstractAction viewSystemCausalityAction = new FocusTrackerAction("System Causality View") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).switchSubscriptionView(Subscription.MODEL_CAUSALITY_VIEW);
        }
    };

    public static final AbstractAction addFileAction = new FocusTrackerAction("File") {
        public void actionPerformed(ActionEvent e) {
            getDomeModelBuildPanel(e).addFile(DomeFile.TYPE_INFO.getTypeName());
        }
    };

    public static final JMenu menu = makeMenu();
    public static final JMenu toolsMenu = makeToolsMenu();
	public static final JMenu modelCausalityAddMenu = makeModelCausalityAddMenu();
    public static final JMenu fileAddMenu = makeFileAddMenu();
    public static final JMenu iModelToolsMenu = makeiModelToolsMenu();
    public static final JMenu iModelSubscriptionViewMenu = makeSubscripionViewMenu();
    private static final JMenuItem subscriptionMenuItem = MenuUtils.makeMenuItem(DomeModelBuildPanel.subscriptionsAction);
    private static final JMenuItem subscriptionMenuItem2 = MenuUtils.makeMenuItem(DomeModelBuildPanel.subscriptionsAction);
    private static final JMenuItem addAndSubscribeMenuItem = MenuUtils.makeMenuItem(DomeModelBuildPanel.addAndSubscribeAction);
    private static final JSeparator subscriptionSeparator = new JPopupMenu.Separator();
    public static ImageIcon icon;
    public static ImageIcon icon2;
    private static JMenuItem subscriptionBuildMenuItem;
    private static JMenuItem subscriptionCausalityMenuItem;
    private static JMenuItem systemCausalityMenuItem;
    private static JMenu viewMenu;

    protected static JMenu makeMenu() {
        JMenu m = MenuUtils.makeBoldMenu("Dome Model");
        m.add(MenuUtils.makeMenuItem(new BuildMode.NewModelAction("New", DomeModel.TYPE_INFO.getTypeName())));
        m.add(MenuUtils.makeMenuItem(new BuildMode.OpenModelAction("Open...", DomeModel.TYPE_INFO.getTypeName())));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.saveAction));
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.saveAsAction));
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.closeAction));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.testAction));
        //temporarily disabled
        DomeModelBuildPanel.testAction.setEnabled(false);
        //m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.testRelationAction));
        testRelationAction.setEnabled(false); //initially
        return m;
    }

    protected static JMenu makeToolsMenu() {
        JMenu m = MenuUtils.makeBoldMenu("Tools");
        m.add(MenuUtils.makeCheckBoxMenuItem("Automatic loop checking", true,new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED)
                    DomeModelBuildPanel.EnableLoopChecking(false);
                else
                    DomeModelBuildPanel.EnableLoopChecking(true);
            }
        }));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.mapAction));
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.interfacesAction));
        return m;
    }

	protected static JMenu makeModelCausalityAddMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Add");
		JMenu map = MenuUtils.makeMenu("Map");
		m.add(map);
		map.add(MenuUtils.makeMenuItem(StandardViewBuildPanel.mapLastSelectionAction));
		map.add(MenuUtils.makeMenuItem(StandardViewBuildPanel.mapClipboardAction));
		return m;
	}

    protected static JMenu makeFileAddMenu() {
        JMenu m = MenuUtils.makeBoldMenu("Add");
        m.add(MenuUtils.makeMenuItem(addFileAction));
        addFileAction.setEnabled(false);
        return m;
    }

    public void addFile(String type) {
        modelBuilder.newModelObject(type);
    }


    protected static JMenu makeiModelToolsMenu() {
        JMenu m = MenuUtils.makeBoldMenu("Tools");
         m.add(MenuUtils.makeCheckBoxMenuItem("Automatic loop checking", true, new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED)
                    DomeModelBuildPanel.EnableLoopChecking(false);
                else
                    DomeModelBuildPanel.EnableLoopChecking(true);
            }
        }));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.mapAction));
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.iModelInterfacesAction));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.iModelProjectInterfacesAction));
        m.add(MenuUtils.makeMenuItem(ContextTreeBuilderPanel.integrationWizardAction));
        return m;
    }

    protected static JMenu makeSubscripionViewMenu() {
        icon = DomeIcons.getIcon(DomeIcons.CHECKED);
        icon2 = DomeIcons.getIcon(DomeIcons.UNCHECKED);
        subscriptionBuildMenuItem = MenuUtils.makeMenuItem(DomeModelBuildPanel.viewBuildAction);
        subscriptionCausalityMenuItem = MenuUtils.makeMenuItem(DomeModelBuildPanel.viewInterfaceCausalityAction);
        systemCausalityMenuItem = MenuUtils.makeMenuItem(DomeModelBuildPanel.viewSystemCausalityAction);
        viewMenu = MenuUtils.makeBoldMenu("View");
        subscriptionBuildMenuItem.setIcon(icon2);
        subscriptionCausalityMenuItem.setIcon(icon2);
        systemCausalityMenuItem.setIcon(icon2);
        viewMenu.add(subscriptionBuildMenuItem);
        viewMenu.add(subscriptionCausalityMenuItem);
        viewMenu.add(systemCausalityMenuItem);
        disableViewMenu();
        return viewMenu;
    }

    public static void enableViewMenu() {
        viewMenu.setEnabled(true);
    }

    public static void disableViewMenu() {
        viewMenu.setEnabled(false);
    }

    public static void setSubscriptionView(String option) {
        if (option.equals(Subscription.INPUT_OUTPUT_VIEW)) {
            subscriptionBuildMenuItem.setIcon(icon2);
            subscriptionCausalityMenuItem.setIcon(icon);
            systemCausalityMenuItem.setIcon(icon2);
            return;
        } else if (option.equals(Subscription.MODEL_CAUSALITY_VIEW)) {
            subscriptionBuildMenuItem.setIcon(icon2);
            subscriptionCausalityMenuItem.setIcon(icon2);
            systemCausalityMenuItem.setIcon(icon);
            return;
        } else if (option.equals(Subscription.BUILD_VIEW)) {
            subscriptionBuildMenuItem.setIcon(icon);
            subscriptionCausalityMenuItem.setIcon(icon2);
            systemCausalityMenuItem.setIcon(icon2);
            return;
        }
    }

    //Added by Ligon for iModel Wizard mapping
    public ContextTreeBuilderPanel getContextPanel(){
        return defPanel.getContextPanel();
    }

    public static void addSubscriptionMenu() {
        ContextBuilderMenus.menus.getAddMenu().add(subscriptionMenuItem, 0);
        ContextBuilderMenus.menus.getAddMenu().add(addAndSubscribeMenuItem, 1);
        ContextBuilderMenus.menus.getAddMenu().add(subscriptionSeparator, 2);
    }

    public static void removeSubscriptionMenu() {
        ContextBuilderMenus.menus.getAddMenu().remove(subscriptionMenuItem);
        ContextBuilderMenus.menus.getAddMenu().remove(addAndSubscribeMenuItem);
        ContextBuilderMenus.menus.getAddMenu().remove(subscriptionSeparator);
    }

    protected void updateClipboardStatus() {
        if (isClipboardEmpty == BuildMode.clipboard.isEmpty()) return; // already consistent
        isClipboardEmpty = BuildMode.clipboard.isEmpty();
        if (isClipboardEmpty) {
            addAndSubscribeMenuItem.setEnabled(false);
        } else {
            addAndSubscribeMenuItem.setEnabled(true);
        }
    }

    class ClipboardListener implements DListListener {
        public void intervalChanged(DListEvent e) {
            updateClipboardStatus();
        }

        public void intervalAdded(DListEvent e) {
            updateClipboardStatus();
        }

        public void intervalRemoved(DListEvent e) {
            updateClipboardStatus();
        }

        public void itemsRemoved(DListEvent e) {
            updateClipboardStatus();
        }

        public void itemsReplaced(DListEvent e) {
            updateClipboardStatus();
        }
    }
}
