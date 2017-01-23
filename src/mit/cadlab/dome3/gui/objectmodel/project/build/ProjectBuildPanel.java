// ProjectBuildPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project.build;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.integrationwizards.imodelwizard.SkeletonResource;
import mit.cadlab.dome3.integrationwizards.imodelwizard.iModelWizardFrame;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.ProjectMatching;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.MatchedModelPair;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.MappedModels;
import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.*;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.*;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.tools.MappingsBuildPanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
import java.io.File;

public class ProjectBuildPanel extends AbstractDomeObjectGui implements Saveable
{

	protected static GridBagConstraints gbc;
	//key - projectBuilder instance, value - project interface manager panel
    protected static HashMap projectInterfaceMgrPanelMap;

	protected IntegrationProjectBuilder projBuilder;
	protected NameTextField nameField;
	protected JButton messageLogButton;
	protected JTabbedPane contentTabs;
	protected ProjectDefinitionBuildPanel defPanel;
	protected DocumentationBuildPanel docPanel;
	protected JTextField fileNameField;
	protected MessageLogDialog messageLog = null;
	protected DFrame interfacesFrame = null;
	protected MappingsBuildPanel mappingTool;
	protected DomeFrame mappingsFrame;
    protected iModelWizardFrame wizardFrame;

	public ProjectBuildPanel(IntegrationProjectBuilder projBuilder)
	{
		super(projBuilder);
		this.projBuilder = projBuilder;
		this.projBuilder.addPropertyChangeListener(IntegrationProjectBuilder.FILE_NAME, new FileNameListener());
		createComponents();
		projectInterfaceMgrPanelMap = new HashMap();
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(projBuilder);
		messageLogButton = Templates.makeButton("message log", new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				messageLog.show();
			}
		});
		contentTabs = Templates.makeTabbedPane();
		defPanel = new ProjectDefinitionBuildPanel(projBuilder);
		docPanel = new DocumentationBuildPanel(projBuilder.getDocumentation());
		contentTabs.addTab("definition", defPanel);
		contentTabs.addTab("documentation", docPanel);
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setMenuContext();
			}
		});
		fileNameField = Templates.makeTextField(projBuilder.getFileName());
		fileNameField.setEditable(false);
		fileNameField.setBackground(new Color(105, 105, 105));
		layoutComponent();
	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), contentTabs, fileNameField};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
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

	public void addNotify()
	{
		super.addNotify();
		createMessageLog(); // at this time, frame will be available
	}

	// DomeObjectGui interface
	public String getTitlePrefix()
	{
		return "Integration Project: ";
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
			case 0: // definition
				defPanel.setMenuContext();
				return;
			case 1: // documentation
				MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
				break;
			default: // default for other tabs
				MenuManager.setContext(ModeContexts.BUILD_PROJECT);
		}
		BuildFocusTracker.notifyInFocus(this, projBuilder);
	}

	protected void createMessageLog()
	{
		messageLog = new MessageLogDialog(this);
		messageLog.addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				MenuManager.setContext(ModeContexts.BUILD_PROJECT);
			}
		});
		projBuilder.setLogHandler(new MessageLogDialogLogHandler(projBuilder, messageLog));
	}

	public Model getModel()
	{
		return projBuilder;
	}

	public void save(boolean closeAfterSave)
	{
		if (projBuilder.getFileName().equals("")) { // never saved before
			saveAs(closeAfterSave);
		}
		else {
            File f = new File(projBuilder.getFileName());
			JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, f.getName(), getStatusWindowLocation());
			ProjectBuildPanel.saveModelWorker worker = new ProjectBuildPanel.saveModelWorker(projBuilder, waitWin, closeAfterSave);
			worker.start();
			//projBuilder.save();
		}
	}

	public void saveAs(boolean closeAfterSave)
	{
		String newFileName = BuildMode.buildFileChooser.showSaveDialog(this, DomeFileChooser.DOME_PROJECT_FILTER);
		if (newFileName != null) {
			projBuilder.saveAs(newFileName, closeAfterSave);
		}
	}

	protected static final Dimension NOT_SAVED_WARNING_SIZE = new Dimension(300, 100);

	public void close()
	{
        Component Parent = SwingUtilities.getRoot(this);
        if(Parent instanceof JFrame)
             WaitCursorUtils.setWaitCursor((JFrame)Parent,true);

        if (!projBuilder.isSaved()) {
			int answer = TwoButton2Msg.showOption(this, "Warning: unsaved changes",
			                                      "has not been saved", projBuilder.getName(),
			                                      "save now", "skip save", NOT_SAVED_WARNING_SIZE);
			switch (answer) {
				case TwoButton2Msg.LEFT_OPTION:
					// set closeAfterSave as true to call 'deleteAllConcreteParameters()' after saving
                    save(true);
                    break;
                default: // skip save
                    // even when user chooses not to save, we need to call 'deleteAllConcreteParameters()' before window closed
                    ProjectBuildPanel.deleteAllConcreteParameters(projBuilder);
			}
		} else {
            // when window closes without saving or skipping it, we need to call 'deleteAllConcreteParameters()'
            ProjectBuildPanel.deleteAllConcreteParameters(projBuilder);
        }
        
        if(Parent instanceof JFrame)
             WaitCursorUtils.setWaitCursor((JFrame)Parent,false);

		defPanel.cleanup(); //remove entries in the static tree object factories
		projBuilder.delete(null);
	}

	public void test()
	{
		System.out.println(projBuilder.getName() + ":test2");
	}

	public void showMappings(Object paramOrRel)
	{
		if (mappingTool == null) {
			mappingsFrame = MappingsBuildPanel.createMappingTool(projBuilder.getMappingManager());
			mappingTool = (MappingsBuildPanel) mappingsFrame.getGui();
			mappingsFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent event)
				{
					mappingTool = null;
					mappingsFrame = null;
				}
			});
		}

		updateMappings(paramOrRel);
		mappingsFrame.show();
	}

	public void updateMappings(Object paramOrRel)
	{
		if (mappingTool != null) {
			if (paramOrRel instanceof Relation) {
				mappingTool.setCurrentRelation((Relation) paramOrRel);
			} else if (paramOrRel instanceof Parameter) {
				mappingTool.setCurrentParameter((Parameter) paramOrRel);
			}
		}
	}


	protected class FileNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			fileNameField.setText((String) evt.getNewValue());
		}
	}

	public void showInterfaces()
	{
		interfacesFrame = getFromProjectInterfaceMgrPanelMap(projBuilder);
		if(interfacesFrame == null) {
			ModelInterfaceManagerBuilder iBuilder = (ModelInterfaceManagerBuilder) projBuilder.getProjectInterfacesManager();
			interfacesFrame = ModelInterfaceManagerBuildPanel.createInterfacesTool(iBuilder, ModeContexts.BUILD_PROJECT_INTERFACES);
			insetInProjectInterfaceMgrPanelMap(projBuilder, interfacesFrame);
			interfacesFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent event)
				{
					interfacesFrame = null;
					removeFromProjectInterfaceMgrPanelMap(projBuilder);
				}
			});
		}
		interfacesFrame.show();
	}

    public void wizard(){
        Point point = this.getRootPane().getLocationOnScreen();
        JFrame wait = StatusWindow.show("iModel Wizard Status:","Loading Resources",new Point((int)point.getX()-5,(int)point.getY()+25));
        List models = projBuilder.getResourceModels();
        BuildProjectResourceInfo resource;
        SkeletonResource rec;
        Collection ifaces;
        ArrayList resources = new ArrayList();
        boolean loaded = true;
        for(int modelIndex=0;modelIndex<models.size();modelIndex++){
            resource = (BuildProjectResourceInfo)models.get(modelIndex);
            //If the directed graph in the model interface contains and error it will not properly load and will throw an exception
            try{
                //You do not want to load iModels if one already exists
                if(resource.getType().equals("model") || resource.getType().equals("project")){
                    resource.loadResource();
                    ifaces = resource.getInterfaces().values();
                    rec = new SkeletonResource(resource.getResourceUniqueId(),ifaces);
                    rec.translateInterfaces();
                    resources.add(rec);
                }
            }
            catch(Exception e){
                loaded = false;
            }
        }
        wait.dispose();
        //If none of the resource interfaces loaded, the wizard terminates
        if(!loaded && resources.size()<2)
            OneButton1Msg.showError(null, "Error: Model Loading Error" ,"Resources Interfaces Failed to Load. Wizard Terminated."
                    , "Ok", OneButton1Msg.DEFAULT_SIZE);
        else if(!loaded && resources.size()>1){
            OneButton1Msg.showError(null, "Error: Model Loading Error" ,"Some Resources Interfaces Failed to Load. Wizard will Continue."
                    , "Ok", OneButton1Msg.DEFAULT_SIZE);
            loaded = true;
        }

        if(loaded){
            wizardFrame = new iModelWizardFrame(resources);
            wizardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            wizardFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent event) {
                    ProjectMatching project = wizardFrame.getAcceptedProject();
                    MappingMatrix mappingMatrix = wizardFrame.getAcceptedMappings();
                    if(project!=null)
                        addiModel(project,mappingMatrix);
                    wizardFrame = null;
                }
            });
            wizardFrame.show();
        }
    }

    private void addiModel(ProjectMatching project,MappingMatrix mappingMatrix){
        List models = projBuilder.getResourceModels();
        BuildProjectResourceInfo resource;
        DomeModelBuilder iModel = projBuilder.newIntegrationModel();
        ArrayList matchedModels = project.getMatchedModelPairs();
        ArrayList verifiedModelIds = project.getVerifiedModels();
        for(int modelIndex=0;modelIndex<matchedModels.size();modelIndex++){
            for(int idIndex=0;idIndex<verifiedModelIds.size();idIndex++){
                MatchedModelPair modelPair = (MatchedModelPair)matchedModels.get(modelIndex);
                String modelId = (String)verifiedModelIds.get(idIndex);
                if(modelId.equals(modelPair.getTemplateModel().id)){
                    String ifaceId = modelPair.getObjectiveModel().id;
                    for(int recIndex=0;recIndex<models.size();recIndex++){
                        resource = (BuildProjectResourceInfo)models.get(modelIndex);
                        BrowseInterface iface1 = resource.getInterface(ifaceId);
                        if(iface1!=null){
                            //Subscribe resource interface
                            iModel.subscribe(iface1.getServerConnection(), iface1.getInterface(),
                                                       iface1.getInterfaceId(), iface1.getVersion(),
                                                       iface1.getParentId());
                            break;
                        }
                    }
                }
            }
        }
        //open iModel
        DomeBuildFrame f = BuildMode.openIModel(iModel);
        f.show();
        if(mappingMatrix!=null)
            ((DomeModelBuildPanel)f.getGui()).getContextPanel().implementMappings(mappingMatrix,project.getObjectiveModels());
    }



	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final DomeBuildFrame getProjectBuildFrame(ActionEvent e)
		{
			DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
			DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
			if (modelGui instanceof ProjectBuildPanel)
				return modelFrame;
			else {
				DomeObject obj = modelGui.getDomeObject();
				if (obj instanceof ModelComponent) {
					Model m = ((ModelComponent) obj).getModel();
					if (m instanceof IntegrationProject) {
						return (DomeBuildFrame) BuildMode.getWindowTracker(obj);
					} else if (m instanceof DomeModel) {
						DomeModel dm = (DomeModel) m;
						if (dm.isIntegrationModel()) {
							return (DomeBuildFrame) BuildMode.getWindowTracker(dm);
						}
					}
				}
			}
			throw new NullPointerException("No current ProjectBuildFrame");
		}

		protected final ProjectBuildPanel getProjectBuildPanel(ActionEvent e)
		{
			DomeBuildFrame modelFrame = BuildMode.getCurrentModelFrame();
			DomeObjectGui modelGui = (DomeObjectGui) modelFrame.getGui();
			if (modelGui instanceof ProjectBuildPanel)
				return (ProjectBuildPanel) modelGui;
			else {
				DomeObject obj = modelGui.getDomeObject();
				if (obj instanceof ModelComponent) {
					Model m = ((ModelComponent) obj).getModel();
					if (m instanceof IntegrationProject) {
						DomeBuildFrame projectFrame = (DomeBuildFrame) BuildMode.getWindowTracker(obj);
						return (ProjectBuildPanel) projectFrame.getGui();
					} else if (m instanceof DomeModel) {
						DomeModel dm = (DomeModel) m;
						if (dm.isIntegrationModel()) {
							DomeBuildFrame projectFrame = (DomeBuildFrame) BuildMode.getWindowTracker(dm);
							return (ProjectBuildPanel) projectFrame.getGui();
						}
					}
				}
			}
			throw new NullPointerException("No current ProjectBuildPanel");
		}
	}

	// --- actions for menus and buttons --------------------

	public static final AbstractAction saveAction = new FocusTrackerAction("Save")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildPanel(e).save(false);
		}
	};

	public static final AbstractAction saveAsAction = new FocusTrackerAction("Save as...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildPanel(e).saveAs(false);
		}
	};

	public static final AbstractAction closeAction = new FocusTrackerAction("Close")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildFrame(e).selfClose();
		}
	};

	public static final AbstractAction testAction = new FocusTrackerAction("Test Project")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildPanel(e).test();
		}
	};

	public static final AbstractAction interfacesAction = new FocusTrackerAction("Project interfaces")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildPanel(e).showInterfaces();
		}
	};

    public static final AbstractAction wizardAction = new FocusTrackerAction("iModel Wizard")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildPanel(e).wizard();
		}
	};

	public static final AbstractAction mapAction = new FocusTrackerAction("Mappings")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildPanel(e).showMappings(null);
		}
	};

	public static final JMenu menu = makeMenu();
	public static final JMenu toolsMenu = makeToolsMenu();
	public static final JMenu projectInterfaceToolsMenu = makeInterfaceToolsMenu();

	protected static JMenu makeMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Integration Project");
		m.add(MenuUtils.makeMenuItem(new BuildMode.NewModelAction("New", "Project")));
		m.add(MenuUtils.makeMenuItem(new BuildMode.OpenProjectAction("Open...")));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.saveAction));
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.saveAsAction));
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.closeAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.testAction));
		//temporarily disabled
		ProjectBuildPanel.testAction.setEnabled(false);

		m.add(MenuUtils.makeMenuItem(DomeModelBuildPanel.testRelationAction));
		DomeModelBuildPanel.testRelationAction.setEnabled(false); //initially
		return m;
	}

	protected static JMenu makeToolsMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Tools");
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.interfacesAction));
        m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.wizardAction));
		return m;
	}

	protected static JMenu makeInterfaceToolsMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Tools");
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.interfacesAction));
		m.add(MenuUtils.makeMenuItem(ProjectBuildPanel.mapAction));
		return m;
	}

	public static void insetInProjectInterfaceMgrPanelMap(IntegrationProject project,
	                                                      DFrame panel) {
		projectInterfaceMgrPanelMap.put(project, panel);
	}

	public static DFrame getFromProjectInterfaceMgrPanelMap(IntegrationProject project)
	{
		return (DFrame)projectInterfaceMgrPanelMap.get(project);
	}

	public static void removeFromProjectInterfaceMgrPanelMap(IntegrationProject project)
	{
		projectInterfaceMgrPanelMap.remove(project);
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		IntegrationProjectBuilder pb = new IntegrationProjectBuilder(new Id("AA"));
		JFrame f = new JFrame("Project Gui");
		f.getContentPane().add(new ProjectBuildPanel(pb));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.show();
	}

	/**
	 * for determine the status window location
	 * @return
	 */
	public static Point getStatusWindowLocation() {
		JComponent comp = BuildFocusTracker.getCurrentComponent();
		if (comp == null) { // place in top left corner
			return new Point(0, DomeClientApplication.getBottomCoordinate());
		} else { // place offset to window of component
			Window win = BuildFocusTracker.getCurrentWindow();
			if (win instanceof DomeBuildFrame && win.isShowing()) {
				Point p = win.getLocationOnScreen();
				return new Point(p.x + 25, p.y + 25);
			} else { // what is it? place in top left corner
				return new Point(0, DomeClientApplication.getBottomCoordinate());
			}
		}
	}

	static class saveModelWorker extends SwingWorker {
		IntegrationProjectBuilder builder;
		JFrame waitWin;
        boolean closeAfterSave;

		public saveModelWorker(IntegrationProjectBuilder builder, JFrame waitWin, boolean closeAfterSave) {
			this.builder = builder;
			this.waitWin = waitWin;
            this.closeAfterSave = closeAfterSave;
		}

		public Object construct() {
			builder.save();
			return new Object();
		}

		public void finished() {
			waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(builder);
            }

			waitWin.dispose();
		}
	}

    /***
     * sangmok: memeory problem fix
     * There was a problem of not releasing data objects when a user closes window.
     * call delete() for every ConcreteParameter instance contained in this panel.
     * This will make each instance release reference to DataObject
     */
    public static void deleteAllConcreteParameters(IntegrationProjectBuilder modelBuilder) {

        /* cleanup interfaces in for the modelBuilder */
        for (Iterator i = modelBuilder.getInterfaces().iterator(); i.hasNext(); ) {
            ModelInterface aInterface = (ModelInterface) i.next();
            aInterface.cleanup();
        }

        // make a list of deleted model objects (small problems with iterator forced me to delete() in 2 step. I first tried delete() as I iterate through the model objects, but the iterator didn't worked very well. It jumped when an element is deleted. )
        // memory fix it should be done before interfaces get empty

        Set deletedObjectSet = new HashSet();
        Set deletedModelObjectScopeSet = new HashSet();


        for (Iterator i = modelBuilder.getModelObjects().iterator(); i.hasNext(); ) {
            ModelObject mObj = (ModelObject) i.next();
            if (mObj instanceof Parameter) {
                deletedObjectSet.add(mObj);
            } else if (mObj instanceof Subscription) {
                deletedModelObjectScopeSet.add(mObj);
            } else if (mObj instanceof Relation) {
                deletedModelObjectScopeSet.add(mObj);
            } else if (mObj instanceof DefaultContextBuilder) {
                List mObjReferences = ((DefaultContextBuilder) mObj).getModelObjectReferences();
                for (int j = 0; j < mObjReferences.size(); j++) {
                    ModelObject mObjInBuilder = (ModelObject) mObjReferences.get(j);

                    if (mObjInBuilder instanceof Parameter) {
                        deletedObjectSet.add(mObjInBuilder);
                    } else if (mObjInBuilder instanceof Subscription) {
                        deletedModelObjectScopeSet.add(mObjInBuilder);

                    } else if (mObjInBuilder instanceof Relation) {
                        deletedModelObjectScopeSet.add(mObjInBuilder);
                    }
                }
            }
        }

        for (Iterator j = deletedObjectSet.iterator(); j.hasNext(); ) {
            try {
                ((ModelObject) j.next()).delete(null);
            } catch (NoSuchElementException e) { System.err.println(e); }
        }

//        for (Iterator j = deletedModelObjectScopeSet.iterator(); j.hasNext(); ) {
//            try {
//                ModelObjectScope scope = (ModelObjectScope) j.next();
//                scope.deleteAllModelObjects();
//                //scope.delete(null);
//            } catch (NoSuchElementException e) { System.err.println(e); }
//        }

//        // make a list of deleted model objects (small problems with iterator forced me to delete() in 2 step. I first tried delete() as I iterate through the model objects, but the iterator didn't worked very well. It jumped when an element is deleted. )
//        List modelObjectTobeDeleted = new ArrayList();
//        for (Iterator i = modelBuilder.getModelObjects().iterator(); i.hasNext(); ) {
//            ModelObject modelObj = (ModelObject) i.next();
//            if (modelObj instanceof ConcreteParameter) {
//                modelObjectTobeDeleted.add(modelObj);
//            } else if (modelObj instanceof ConcreteProceduralRelation) {
//                /* procedural relation contains model objects that are not returned by modelBuilder.getModelObjects()
//                   we need to access those model objects to put them in the list of 'modelObjectTobeDeleted' */
//                ConcreteProceduralRelation relation = (ConcreteProceduralRelation) modelObj;
//                Collection modelObjectsInRelation = relation.getModelObjects();
//                for (Iterator j = modelObjectsInRelation.iterator(); j.hasNext(); ) {
//                    Object eachParam = j.next();
//                    if (eachParam instanceof ConcreteParameter) {
//                        modelObjectTobeDeleted.add(eachParam);
//                    }
//                }
//            }
//        }
//
//        // execute the deletion
//        for (Iterator i = modelObjectTobeDeleted.iterator(); i.hasNext(); ) {
//            ModelObject modelObj = (ModelObject) i.next();
//            modelObj.delete(null);
//        }
    }

}
