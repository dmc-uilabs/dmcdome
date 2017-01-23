package mit.cadlab.dome3.gui.objectmodel.model.tool.build;

import mit.cadlab.dome3.gui.guiutils.msg.*;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.*;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildListPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.tools.MappingsBuildPanel;
import mit.cadlab.dome3.gui.deploy.deployTool.FastDeployAnalysisTool;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.build.AnalysisToolInterfaceManagerBuild;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * Name: AnalysisToolModelBuildPanel
 * User: jacob
 * Date: Jul 15, 2003
 * Time: 7:36:26 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public abstract class AnalysisToolModelBuildPanel extends AbstractDomeObjectGui
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("AnalysisToolModelBuildPanel");
	public static final String XML_TAG = "toolmodelbuildpanel";
    public static final String TOOL_PROJECT = "project";

    public static final Dimension TEST_MODEL_SIZE = new Dimension(400, 250);

	protected static GridBagConstraints gbc;

    public static String _currentView;

    protected static JMenuItem projectInterfaces = null;

    protected DocumentationBuildPanel docPanel;

	protected NameTextField nameField;
	protected JButton messageLogButton;
	protected JTextField fileNameField;
	protected JTabbedPane contentTabs;
    protected JButton _closeButton;
	protected MessageLogDialog messageLog = null;

	protected AnalysisToolBase modelBuilder;
    protected ProjectBuildListPanel _toolProjectPanel;


	protected MappingsBuildPanel _mappingTool = null;
	protected DomeFrame mappingsFrame = null;
	protected DFrame _projectInterfacesFrame = null;
    protected DFrame _toolInterfacesFrame = null;


    protected static HashMap toolProjectInterfaceMgrPanelMap;
    protected static HashMap toolInterfaceManagerPanelMap;

    public AnalysisToolModelBuildPanel(AnalysisToolBase modelBuilder)
	{
		super(modelBuilder);
		this.modelBuilder = modelBuilder;
		this.modelBuilder.addPropertyChangeListener(DomeModelBuilder.FILENAME,
		                                            new FileNameListener());
        toolProjectInterfaceMgrPanelMap = new HashMap();
        toolInterfaceManagerPanelMap = new HashMap();
	}

	public String getToolTypeName()
	{
		return modelBuilder.getToolTypeName();
	}

	public String getTitlePrefix()
	{
		return modelBuilder.getToolTypeName() + ": ";
	}

	public String getHelpContext()
	{
		return null;
	}

	protected class FileNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(AnalysisToolBase.FILENAME))
				fileNameField.setText(evt.getNewValue().toString());
		}
	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), contentTabs, fileNameField, _closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 5), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
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

	protected void createMessageLog()
	{
		messageLog = new MessageLogDialog(this);
		messageLog.addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				MenuManager.setContext(ModeContexts.BUILD_TOOLMODEL);
			}
		});
		modelBuilder.setLogHandler(new MessageLogDialogLogHandler(modelBuilder, messageLog));
	}

	public void addNotify()
	{
		super.addNotify();
		createMessageLog(); // at this time, frame will be available
	}

	public Model getModel()
	{
		return modelBuilder;
	}

	// * -------------------------------------------------------------------------


	public static void setCurrentView(String currentView)
	{
		AnalysisToolModelBuildPanel._currentView = currentView;
	}

	public static abstract class ToolFocusTrackerAction extends AbstractAction
	{

		public ToolFocusTrackerAction(String name)
		{
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

		protected final DomeBuildFrame getToolModelBuildFrame(ActionEvent e)
		{
			DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
            DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
            if (modelGui instanceof AnalysisToolModelBuildPanel)
                return modelFrame;
            else
            {
                DomeObject obj = modelGui.getDomeObject();
                if (obj instanceof ModelComponent)
                {
                    Model m = ((ModelComponent) obj).getModel();
                    if (m instanceof IntegrationProject)
                    {
                        return (DomeBuildFrame) BuildMode.getWindowTracker(obj);
                    }
                    else if (m instanceof DomeModel)
                    {
                        DomeModel dm = (DomeModel) m;
                        if (dm.isIntegrationModel())
                        {
                            return (DomeBuildFrame) BuildMode.getWindowTracker(dm);
                        }
                    }
                }
            }
			throw new NullPointerException("No current QMOOModelBuildFrame");
		}

		protected final AnalysisToolModelBuildPanel getToolModelBuildPanel(ActionEvent e)
		{
			DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
			DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
			if (modelGui instanceof AnalysisToolModelBuildPanel)
				return (AnalysisToolModelBuildPanel) modelGui;
			else
            {
				DomeObject obj = modelGui.getDomeObject();
				if (obj instanceof ModelComponent) {
					Model m = ((ModelComponent) obj).getModel();
					if (m instanceof IntegrationProject) {
						DomeBuildFrame projectFrame = (DomeBuildFrame) BuildMode.getWindowTracker(obj);
						return (AnalysisToolModelBuildPanel) projectFrame.getGui();
					} else if (m instanceof DomeModel) {
						DomeModel dm = (DomeModel) m;
						if (dm.isIntegrationModel()) {
							DomeBuildFrame projectFrame = (DomeBuildFrame) BuildMode.getWindowTracker(dm);
							return (AnalysisToolModelBuildPanel) projectFrame.getGui();
						}
					}
				}
			}
			throw new NullPointerException("No current AnalysisToolModelBuildPanel");
		}
	}

	public static final AbstractAction newAction = new ToolFocusTrackerAction("New")
	{
		public void actionPerformed(ActionEvent e)
		{
			BuildMode.newModel(getToolModelBuildPanel(e).getToolTypeName());
		}
	};

	public static final AbstractAction openAction = new ToolFocusTrackerAction("Open...")
	{
		public void actionPerformed(ActionEvent e)
		{
			BuildMode.openTool(getToolModelBuildPanel(e).getToolTypeName(), null);
		}
	};

	public static final AbstractAction saveAction = new ToolFocusTrackerAction("Save")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolModelBuildPanel(e).save(false);
		}
	};

	public static final AbstractAction saveAsAction = new ToolFocusTrackerAction("Save as...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolModelBuildPanel(e).saveAs(false);
		}
	};

    public static final AbstractAction importProjectAction = new ToolFocusTrackerAction("Project...")
	{
		public void actionPerformed(ActionEvent e)
		{
			IntegrationProjectBuilder ipbuilder = BuildMode.importAnalysisToolProject(null);
            if (ipbuilder != null)
			    getToolModelBuildPanel(e).replaceToolProject(ipbuilder);
		}
	};

	public static final AbstractAction closeAction = new ToolFocusTrackerAction("Close")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolModelBuildPanel(e).close();
			this.getToolModelBuildFrame(e).selfClose();
		}
	};

	public static final AbstractAction testAction = new ToolFocusTrackerAction("Test Model")
	{
		public void actionPerformed(ActionEvent e)
		{
			int option = TwoButton2Msg.showOption(null, "Build Mode: Test Model", "Would you like to continue?", "The 'Test Model' option allows one to quickly " +
                    "deploy an analysis tool on a server, where \nthe user has the appropriate privilieges.  The analysis" +
                    " tool will be deployed in '/Private/Prototype'.", "OK", "Cancel",
                    TwoButton2Msg.DEFAULT_SIZE);

            if (option == TwoButton1Msg.LEFT_OPTION)
            {
                getToolModelBuildPanel(e).testModel();
            }
		}
	};

    public static final AbstractAction toolProjectInterfacesAction = new ToolFocusTrackerAction("Project Interfaces")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolModelBuildPanel(e).showToolProjectInterfaces();
		}
	};

	public static final AbstractAction toolInterfacesAction = new ToolFocusTrackerAction("Analysis Tool Interfaces")
	{
		public void actionPerformed(ActionEvent e)
		{
        	getToolModelBuildPanel(e).showToolInterfaces();
		}
	};

	public static final AbstractAction mappingAction = new ToolFocusTrackerAction("Mappings")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolModelBuildPanel(e).showMappings(null);
		}
	};

    // The following are menu actions activated when an integration model is opened in the AnalysisToolModelBuildPanel

    public static final AbstractAction saveToolProjectAction = new ToolFocusTrackerAction("Save")
    {
        public void actionPerformed(ActionEvent e)
        {
            getToolModelBuildPanel(e).save(false);
        }
    };

    public static final AbstractAction saveAsToolProjectAction = new ToolFocusTrackerAction("Save As")
    {
        public void actionPerformed(ActionEvent e)
        {
            getToolModelBuildPanel(e).saveAsAnalysisToolProject(false);
        }
    };

    public static final AbstractAction closeToolProjectAction = new ToolFocusTrackerAction("Close")
    {
        public void actionPerformed(ActionEvent e)
        {
           getToolModelBuildFrame(e).selfClose();
        }
    };

    public static final AbstractAction testToolProjectAction = new ToolFocusTrackerAction("Test Project")
    {
        public void actionPerformed(ActionEvent e)
        {
            OneButton1Msg.showWarning(null, "implementation warning", "not yet implemented", "OK", NOT_YET_IMPLEMENTED_SIZE);
        }
    };

	public void showMappings(Object paramOrRel)
	{
		if (_mappingTool == null)
		{
			mappingsFrame = MappingsBuildPanel.createMappingTool(modelBuilder.getMappingManager());
			_mappingTool = (MappingsBuildPanel) mappingsFrame.getGui();
			mappingsFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent event)
				{
					_mappingTool = null;
					mappingsFrame = null;
				}
			});
		}

		updateMappings(paramOrRel);

		mappingsFrame.show();
	}

	public void updateMappings(Object paramOrRel)
	{
		if (this._mappingTool != null)
		{
			if (paramOrRel instanceof Parameter)
			{
				this._mappingTool.setCurrentParameter((Parameter) paramOrRel);
			}
		}
	}

	public static final JMenu menu = makeMenu();
	public static final JMenu toolsMenu = makeToolsMenu();
    public static final JMenu toolsiModelMenu = makeToolsiModelMenu();
    public static final JMenu projectMenu = makeProjectMenu();
    public static final JMenu analysisToolProjectInterfaceToolsMenu = makeAnalysisToolProjectInterfaceToolsMenu();

	protected static JMenu makeMenu()
	{
        String type = "Analysis Tool";
		JMenu m = MenuUtils.makeBoldMenu(type);
		m.add(MenuUtils.makeMenuItem(newAction));
		m.add(MenuUtils.makeMenuItem(openAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(saveAction));
		m.add(MenuUtils.makeMenuItem(saveAsAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(closeAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(testAction));
		return m;
	}

	protected static JMenu makeToolsMenu()
	{
        projectInterfaces = MenuUtils.makeMenuItem(toolProjectInterfacesAction);
		JMenu m = MenuUtils.makeBoldMenu("Tools");
		m.add(MenuUtils.makeMenuItem(mappingAction));
		m.addSeparator();
        m.add(MenuUtils.makeMenuItem(toolInterfacesAction));
		m.add(projectInterfaces);
		return m;
	}

    protected static JMenu makeAnalysisToolProjectInterfaceToolsMenu()
    {
        JMenu m = MenuUtils.makeBoldMenu("Tools");
        m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.toolProjectInterfacesAction));
        m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.mappingAction));
        return m;
    }

    protected static JMenu makeProjectMenu()
    {
        JMenu m = MenuUtils.makeBoldMenu("Integration Project");
        m.add(MenuUtils.makeMenuItem(new BuildMode.NewModelAction("New", "Project")));
		m.add(MenuUtils.makeMenuItem(new BuildMode.OpenProjectAction("Open...")));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.saveToolProjectAction));
		m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.saveAsToolProjectAction));
		m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.closeToolProjectAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.testToolProjectAction));

        //temporarily disabled
		AnalysisToolModelBuildPanel.testToolProjectAction.setEnabled(false);

        return m;
    }

    protected static JMenu makeToolsiModelMenu()
    {
        JMenu m = MenuUtils.makeBoldMenu("Tools");
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.mapAction));
		m.addSeparator();
        m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.iModelInterfacesAction));
		m.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.toolProjectInterfacesAction));
        return m;
    }

    public void save(boolean closeAfterSave)
    {
        modelBuilder.save(closeAfterSave);
    }

	public void saveAs(boolean closeAfterSave)
	{
        modelBuilder.saveAs(closeAfterSave);
	}

    public void saveAsAnalysisToolProject(boolean closeAfterSave)
    {

		String newFileName = BuildMode.buildFileChooser.showSaveDialog(this, DomeFileChooser.DOME_PROJECT_FILTER);
		if (newFileName != null)
            ((IntegrationProjectBuilder)modelBuilder.getIntegrationProject()).saveAs(newFileName, closeAfterSave);
	}

    public void replaceToolProject(IntegrationProjectBuilder ipbuilder)
	{
		modelBuilder.setToolProject(ipbuilder);

        ((IntegrationProjectBuilder)modelBuilder.getIntegrationProject()).setIsToolProjectBuilder(true);
        ((IntegrationProjectBuilder)modelBuilder.getIntegrationProject()).setToolModel(modelBuilder);

        _toolProjectPanel.cleanup();
        _toolProjectPanel = new ProjectBuildListPanel((IntegrationProjectBuilder)modelBuilder.getIntegrationProject());

  	    contentTabs.setComponentAt(0, _toolProjectPanel);

	}

    public ProjectBuildListPanel getProjectBuildListPanel()
	{
		return _toolProjectPanel;
	}

    public void setToolProjectMenuContext()
    {
        MenuManager.setContext(ModeContexts.BUILD_TOOL_PROJECT_DEFINITION);
        projectInterfaces.setEnabled(true);
        JComponent comp =  _toolProjectPanel;
        BuildFocusTracker.notifyInFocus(comp, modelBuilder);
    }


	protected static final Dimension NOT_SAVED_WARNING_SIZE = new Dimension(280, 90);
    protected static final Dimension NOT_YET_IMPLEMENTED_SIZE = new Dimension(150, 75);
    protected static final Dimension HAPPY_MESSAGE_SIZE = new Dimension(150, 75);

	public void close()
	{
		if (modelBuilder.getShouldSave() && !modelBuilder.isSaved())
		{
			int answer = TwoButton2Msg.showWarning(this, "Warning: unsaved changes", "has not been saved", modelBuilder.getName(),
			                                       "save now", "skip save", NOT_SAVED_WARNING_SIZE);
			switch (answer)
			{
				case TwoButton2Msg.LEFT_OPTION:
					save(true);
					break;
				default: // skip save
					modelBuilder.setShouldSave(false);
                    // even when user chooses not to save, we need to call 'deleteAllConcreteParameters()' before window closed
                    AnalysisToolBase.deleteAllConcreteParameters(modelBuilder);
			}
		} else {
            // when window closes without saving or skipping it, we need to call 'deleteAllConcreteParameters()'
            AnalysisToolBase.deleteAllConcreteParameters(modelBuilder);
        }


		//remove imodel and resource DArrayList entries in the static tree object factories
        _toolProjectPanel.cleanup();
	}

	protected JPanel makeSetupPanel()
	{
		JPanel pane = new JPanel();
		BuildTree tree = new BuildTree(modelBuilder.getToolConfiguration().getSetupContextFolders());
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

	public abstract void setMenuContext();

    protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(modelBuilder);
		messageLogButton = Templates.makeButton("message log", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				messageLog.show();
			}
		});

        contentTabs = Templates.makeTabbedPane();

        if (modelBuilder.getIntegrationProject() == null)
        {
            modelBuilder.setToolProject(new IntegrationProjectBuilder(new Id(UUIDGenerator.create())));
        }

        ((IntegrationProjectBuilder)modelBuilder.getIntegrationProject()).setIsToolProjectBuilder(true);
        ((IntegrationProjectBuilder)modelBuilder.getIntegrationProject()).setToolModel(modelBuilder);
        _toolProjectPanel = new ProjectBuildListPanel((IntegrationProjectBuilder)modelBuilder.getIntegrationProject());
        contentTabs.addTab(TOOL_PROJECT, this._toolProjectPanel);

        fileNameField = Templates.makeTextField(modelBuilder.getFileName());
        fileNameField.setEditable(false);
        fileNameField.setBackground(new Color(105, 105, 105));

        _closeButton = Templates.makeButton("close");
        _closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                close();
                BuildMode.getCurrentModelFrame().selfClose();
            }
        });

	}

    public void showToolInterfaces()
    {
        _toolInterfacesFrame = getFromToolInterfaceMgrPanelMap(modelBuilder);
        if (_toolInterfacesFrame == null)
        {
            AnalysisToolInterfaceManagerBuild iBuilder = (AnalysisToolInterfaceManagerBuild) modelBuilder.getAnalysisToolInterfacesManager();
            _toolInterfacesFrame = ToolInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_TOOLMODEL_INTERFACES);
            _toolInterfacesFrame.addWindowListener(new WindowAdapter()
            {
                public void windowClosed(WindowEvent event)
                {
                    _toolInterfacesFrame = null;
                }
            });
        }
        _toolInterfacesFrame.show();
    }

    public void showToolProjectInterfaces()
	{
		_projectInterfacesFrame = getFromProjectInterfaceMgrPanelMap(modelBuilder.getIntegrationProject());
		if (_projectInterfacesFrame == null)
		{
			ModelInterfaceManagerBuilder iBuilder = (ModelInterfaceManagerBuilder) modelBuilder.getIntegrationProject().getProjectInterfacesManager();
			_projectInterfacesFrame = ModelInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_TOOL_PROJECT_INTERFACES);
			insetInProjectInterfaceMgrPanelMap(modelBuilder.getIntegrationProject(), _projectInterfacesFrame);
			_projectInterfacesFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent event)
				{
					_projectInterfacesFrame = null;
					removeFromProjectInterfaceMgrPanelMap(modelBuilder.getIntegrationProject());
				}
			});
		}
		_projectInterfacesFrame.show();
	}

    public void testModel()
    {
        if((modelBuilder.getFileName() == null) || (modelBuilder.getFileName().equals("")))
        {
             int option = TwoButton2Msg.showOption(null, "Build Mode: Test Model", "Would you like to continue?",
                     "The analysis tool has not been saved.  You must save this model before you are able to continue.",
                     "OK", "Cancel", TwoButton2Msg.DEFAULT_SIZE);
            if (option == TwoButton2Msg.LEFT_OPTION)
            {
                modelBuilder.save(false);
                if (modelBuilder.getFileName() == null || modelBuilder.getFileName().equals(""))
                {
                    OneButton1Msg.showWarning(null, "Build Mode: Test Model", "You must save the model before you can continue.", "OK", OneButton1Msg.DEFAULT_SIZE);
                    return;
                }
            }
            else
                return;
        }

        FastDeployAnalysisTool analysisTool = new FastDeployAnalysisTool(modelBuilder.getFileName());

        analysisTool.fastDeploy();

    }

	public static void insetInProjectInterfaceMgrPanelMap(IntegrationProject project,
	                                                      DFrame panel)
	{
		toolProjectInterfaceMgrPanelMap.put(project, panel);
	}

	public static DFrame getFromProjectInterfaceMgrPanelMap(IntegrationProject project)
	{
		return (DFrame) toolProjectInterfaceMgrPanelMap.get(project);
	}

	public static void removeFromProjectInterfaceMgrPanelMap(IntegrationProject project)
	{
		toolProjectInterfaceMgrPanelMap.remove(project);
	}

    public static DFrame getFromToolInterfaceMgrPanelMap(AnalysisToolBase model)
    {
        return (DFrame) toolInterfaceManagerPanelMap.get(model);
    }
}
