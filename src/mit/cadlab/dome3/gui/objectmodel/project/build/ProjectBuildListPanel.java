package mit.cadlab.dome3.gui.objectmodel.project.build;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.GenericDomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.GenericObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.browse.BrowseTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTreeObject;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.project.AddResourceDialog;
import mit.cadlab.dome3.gui.objectmodel.project.BrowseInterfaceTreeObject;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Array;

/**
  * When the container frame gets closed calling "cleanup" method is very critical.
 * therwise new resources and iModels may stop appearing in the GUI.
 */
public class ProjectBuildListPanel extends JSplitPane
{
	public static TreeObjectFactory resourcesTreeFactory = makeResourcesTreeFactory();
	protected static TreeObjectFactory iModelsTreeFactory = makeIModelsTreeFactory();
	protected static GridBagConstraints gbc;

	protected IntegrationProjectBuilder projBuilder;
	protected AddResourceDialog addDialog = null;
	protected AddResourceDialog relocateDialog = null;
	protected DomeTree resourceTree, iModelTree;

	public ProjectBuildListPanel(IntegrationProjectBuilder projBuilder)
	{
		super(JSplitPane.VERTICAL_SPLIT);
		this.projBuilder = projBuilder;
		this.setLeftComponent(makeResourcesPanel());
		this.setRightComponent(makeIntegrationModelsPanel());
		this.setDividerLocation(150);
	}

	public void addResource()
	{
		if (addDialog == null)
			addDialog = new AddResourceDialog((JFrame) SwingUtilities.windowForComponent(this), projBuilder, false , ": add resources");
		addDialog.show();
	}

	public void addIntegrationModel()
	{
		projBuilder.newIntegrationModel();
	}

	public void removeResource()
	{
		if (resourceTree.isSelectionEmpty())
			return;
		TreePath[] paths = resourceTree.getSelectionPaths();
		for(int i = 0; i < paths.length; i++) {
			Object o = ((GenericObjectTreeNode) paths[i].getLastPathComponent()).getObject();
			if (o instanceof ProjectResourceInfo) {
				if(!((ProjectResourceInfo)o).isSubScribed()) {  // no subscriptions
					projBuilder.removeResourceModel((ProjectResourceInfo) o, false);
				}
				else {
					String msg = "This resource is subscribed by one or more integration models.  Do you want to remove it?";
					int answer = TwoButton1Msg.showOption(this, "Remove resource", msg, "OK", "Cancel", new Dimension(1, 1));
					if(answer == TwoButton1Msg.LEFT_OPTION) {
						projBuilder.removeResourceModel((ProjectResourceInfo) o, true);
					}
				}
			}
		}
	}

	public void relocateResource() {
		Object o = ((GenericObjectTreeNode) resourceTree.getSelectionPath().getLastPathComponent()).getObject();
		if (o instanceof ProjectResourceInfo) {
			if (relocateDialog == null)  {
				relocateDialog = new AddResourceDialog((JFrame) SwingUtilities.windowForComponent(this),
				                                       projBuilder, true, ": relocate resource",
				                                       (BuildProjectResourceInfo)o);
			}
			else {
				relocateDialog.changeResourceInfo((BuildProjectResourceInfo) o);
			}
			relocateDialog.show();
		}
	}

	public void deleteIntegrationModel()
	{
		if (iModelTree.isSelectionEmpty())
			return;
		Object o = ((GenericObjectTreeNode) iModelTree.getSelectionPath().getLastPathComponent()).getObject();
		if (o instanceof ProjectIntegrationModelInfo) {
			if(!((ProjectIntegrationModelInfo)o).isSubscribed()) {// no subscriptions
				projBuilder.removeIntegrationModel((ProjectIntegrationModelInfo) o);
			}
			else {
				String msg = "This iModel is subscribed by one or more integration models.  Do you want to remove it?";
				int answer = TwoButton1Msg.showOption(this, "Remove iModel", msg, "OK", "Cancel", new Dimension(1, 1));
				if (answer == TwoButton1Msg.LEFT_OPTION) {
					projBuilder.removeIntegrationModel((ProjectIntegrationModelInfo) o);
				}
			}
		}
	}

	public void openIntegrationModel()
	{
		if (iModelTree.isSelectionEmpty())
			return;
		Object o = ((GenericObjectTreeNode) iModelTree.getSelectionPath().getLastPathComponent()).getObject();
		if (o instanceof BuildProjectIntegrationModelInfo) {
			DomeBuildFrame f = BuildMode.openIModel((DomeModelBuilder) ((BuildProjectIntegrationModelInfo) o).getModel());
			f.show();
		}
	}

	private JPanel makeResourcesPanel()
	{
		JPanel p = new JPanel();
        //exclude i-models from resource tree - they will only be visible as subscription interfaces _i

      //resourceTree = new DomeTree(new GenericObjectTreeNode(projBuilder.getResourceModels(), resourcesTreeFactory), true);
        resourceTree = new DomeTree(new GenericObjectTreeNode(projBuilder.getExternalResourceModels(), resourcesTreeFactory), true);
		resourceTree.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				Object o = ((GenericObjectTreeNode) event.getPath().getLastPathComponent()).getObject();
				if (o instanceof BuildProjectResourceInfo) {
					((BuildProjectResourceInfo) o).loadResource();
				} else if (o instanceof BrowseInterface) {
					((BrowseInterface) o).loadInterface(true);
				}

			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}
		});
		resourceTree.addTreeSelectionListener(new ResourceTreeSelectionListener());
		JComponent[] comps = {Templates.makeLabel("resources available in project:"),
		                      new ResourceBuilderPanel(resourceTree)
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeIntegrationModelsPanel()
	{
		JPanel p = new JPanel();
		iModelTree = new DomeTree(new GenericObjectTreeNode(projBuilder.getIntegrationModels(), iModelsTreeFactory), true);
		iModelTree.addTreeSelectionListener(new IModelTreeSelectionListener());
		JComponent[] comps = {Templates.makeLabel("integration models in project:"),
		                      new IntegrationModelListPanel(iModelTree)
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	class ResourceBuilderPanel extends TreeBuilderPanel
	{
		private BuildTreeTable treeTable;
		public ResourceBuilderPanel(DomeTree tree)
		{
			String[] colNames = new String[] {"Name", "Value", "Location"};
			int[] colWidths = new int[] {200, 150, 100};
			treeTable = new BuildTreeTable(tree, 3, colNames, colWidths);
			layoutComponent(treeTable);
		}

		protected void moveDownAction()
		{
		}

		protected void moveUpAction()
		{
		}
	}

	class IntegrationModelListPanel extends TreeBuilderPanel
	{
		private BuildTreeTable treeTable;
		public IntegrationModelListPanel(DomeTree tree)
		{
			String[] colNames = new String[]{"Name", "Value", "Resources"};
			int[] colWidths = new int[]{200, 150, 100};
			treeTable = new BuildTreeTable(tree, 3, colNames, colWidths);
			layoutComponent(treeTable);
		}

		protected void moveDownAction()
		{
		}

		protected void moveUpAction()
		{
		}


	}

	public void close()
	{
		if (addDialog != null)
			addDialog.close(); // release ServerConnections in browser
	}

	public void copySelectedObjects(boolean isResourceOp)
	{
		DomeTree tree;
		if(isResourceOp)
			tree = resourceTree;
		else
			tree = iModelTree;
		if (tree.isSelectionEmpty()) return;
		// copy in order in tree
		int[] selectionRows = tree.getSelectionRows();
		Arrays.sort(selectionRows);
		DSet selectedObjects = new DSet();
		for (int i = 0; i < selectionRows.length; ++i) {
			TreePath selectedPath = tree.getPathForRow(selectionRows[i]);
			GenericObjectTreeNode node = (GenericObjectTreeNode) selectedPath.getLastPathComponent();
			Object dObj = null;
			if(node.getTreeObject() instanceof GenericDomeTreeObject) {
                dObj = ((GenericDomeTreeObject) node.getTreeObject()).getData();
			}
			else if (node.getTreeObject() instanceof DomeTreeObject) {
				dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
			}
			selectedObjects.add(dObj);
		}
		if (selectedObjects.isEmpty()) return; // nothing to copy
		BuildMode.clipboard.addSelection(selectedObjects);
	}

	public void pasteLastSelection(boolean isResourceOp)
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		pasteCopies(sel.getItems(), isResourceOp);
	}

	public void pasteFromClipboard(boolean isResourceOp)
	{
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
		if (selections == null) return; // nothing selected in clipboard!
		ArrayList allSelections = new ArrayList(); // items can be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());
		pasteCopies(allSelections, isResourceOp);
	}

	protected void pasteCopies(List items, boolean isResourceOp)
	{
		DomeTree tree;
		if (isResourceOp)
			tree = resourceTree;
		else
			tree = iModelTree;
		tree.stopEditing();
		items = filterForValidItems(items, isResourceOp, true);
		if (tree.isSelectionEmpty()) { // nothing selected
			//copy and add resouce
			projBuilder.copyPasteObjects(items);
		}
		else { // find insertion point
			int[] selectedIndices = tree.getSelectionRows();
			Arrays.sort(selectedIndices);
			int selectedIndex = selectedIndices[0]; // first item selected
			TreePath selectedPath = tree.getPathForRow(selectedIndex);
			GenericObjectTreeNode selectedNode = (GenericObjectTreeNode) selectedPath.getLastPathComponent();
			GenericObjectTreeNode parentNode = (GenericObjectTreeNode) selectedNode.getParent();
			int childIndex = parentNode.getIndex(selectedNode);
			projBuilder.copyPasteObjects(items, childIndex);
		}
	}

	protected void addAndSubscribe()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		iModelTree.stopEditing();
		if (iModelTree.isSelectionEmpty()) { // nothing selected
			showIModelWarning();
		}
		else {
			List items = sel.getItems();
			items = filterForValidItems(items, false, false);
			int[] selectedIndices = iModelTree.getSelectionRows();
			Arrays.sort(selectedIndices);
			int selectedIndex = selectedIndices[0]; // first item selected
			TreePath selectedPath = iModelTree.getPathForRow(selectedIndex);
			GenericObjectTreeNode selectedNode = (GenericObjectTreeNode) selectedPath.getLastPathComponent();
			TreeObject tobj = selectedNode.getTreeObject();
			if(tobj instanceof BuildProjectIntegrationModelTreeObject) {
				BuildProjectIntegrationModelInfo info =
				        (BuildProjectIntegrationModelInfo)((BuildProjectIntegrationModelTreeObject)tobj).getData();
				DomeModelBuilder modelBuilder = (DomeModelBuilder)info.getModel();
				for (Iterator iter = items.iterator(); iter.hasNext(); ) {
					Object o = iter.next();
					if(o instanceof BrowseInterface) {
						BrowseInterface ifaceInfo = (BrowseInterface) o;
						modelBuilder.subscribe(ifaceInfo.getServerConnection(), ifaceInfo.getInterface(),
											   ifaceInfo.getInterfaceId(), ifaceInfo.getVersion(),
						                       ifaceInfo.getParentId());
					}
					else if (o instanceof ModelInterfaceBuilder) {
						ModelInterfaceBuilder mi = (ModelInterfaceBuilder) o;
						modelBuilder.subscribe((ServerConnection) null, mi, mi.getId().getIdString(),
											   mi.getVersion().getMajorVersion(),
						                       mi.getModel().getId().getIdString());
   					}
				}
			}
			else {
				showIModelWarning();
			}
		}
	}

	private static void showIModelWarning() {
		OneButton1Msg.showWarning(BuildFocusTracker.getCurrentComponent(), "Subscribe warning",
		                          "Please choose an iModel to add the interface subscription",
		                          "OK", new Dimension(1, 1));
	}

	public static List filterForValidItems(List items, boolean isResourceOp, boolean isPasteOp) {
		boolean showDailog = false;
		List filteredItems = new ArrayList();
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			if (isResourceOp) {
				if(obj instanceof ProjectResourceInfo) {
					filteredItems.add(obj);
				}
				else {
					showDailog = true;
				}
			}
			else {
				if(obj instanceof ProjectIntegrationModelInfo) {
					filteredItems.add(obj);
				}
				else if (obj instanceof BrowseInterface) {
					filteredItems.add(obj);
				}
				else if (obj instanceof ModelInterfaceBuilder) {
					filteredItems.add(obj);
				}
				else {
					showDailog = true;
				}
			}
		}
		if(showDailog)
		{
			String msg;
			String title;
			if(isPasteOp) {
				if(isResourceOp) {
					msg = "Only project resources can be pasted.  All other copied objects will ignored.";
				}
				else {
					msg = "Only iModels can be pasted.  All other copied objects are ignored.";
				}
				title = "Paste warning";
			}
			else {
				if (isResourceOp) {
					msg = "Only project resource interfaces can be subscribed.  All other copied objects will ignored.";
				} else {
					msg = "Only iModel interfaces can be subscribed.  All other copied objects are ignored.";
				}
				title = "Subscribe warning";
			}
			OneButton1Msg.showWarning(BuildFocusTracker.getCurrentComponent(), title, msg, "OK", new Dimension(1, 1));
		}
		return filteredItems;
	}

	public void switchInterfaceView(String view) {
        int resourceSelCount = resourceTree.getSelectionCount();
		int iModelSelCount = iModelTree.getSelectionCount();
		if(resourceSelCount == 1 && iModelSelCount == 0) {
			//resource interface
			GenericObjectTreeNode selectedNode = (GenericObjectTreeNode) resourceTree.getSelectionPath().getLastPathComponent();
			TreeObject tob = selectedNode.getTreeObject();
			if (tob instanceof BrowseInterfaceTreeObject) {
				BrowseInterfaceTreeObject tobj = (BrowseInterfaceTreeObject)tob;
				Object o = tobj.getData();
				((BrowseInterface)o).setView(view);
			}
			else {
				showinterfaceViewdialog1();
			}
		}
		else if (iModelSelCount == 1 && resourceSelCount == 0) {
			//iModel interface
			GenericObjectTreeNode selectedNode = (GenericObjectTreeNode) iModelTree.getSelectionPath().getLastPathComponent();
			TreeObject tob = selectedNode.getTreeObject();
			if (tob instanceof ModelInterfaceTreeObject) {
				ModelInterfaceTreeObject tobj = (ModelInterfaceTreeObject)tob;
				Object o = tobj.getDomeObject();
//				((ModelInterfaceBuilder) o).setView(view);
			}
			else {
				showinterfaceViewdialog1();
			}
		}
		else {
			showinterfaceViewdialog();
		}
		ProjectBuildMenus.menus.setView(view);
	}

	private void showinterfaceViewdialog1() {
		String msg = "Please select an interface to change the view.";
		OneButton1Msg.showWarning(BuildFocusTracker.getCurrentComponent(), "View warning",
		                          msg, "OK", new Dimension(1, 1));
	}

	private void showinterfaceViewdialog()
	{
		String msg = "Please select either resource interface or an iModel interface.";
		OneButton1Msg.showWarning(BuildFocusTracker.getCurrentComponent(), "View warning",
		                          msg, "OK", new Dimension(1, 1));
	}

	//removed the cached DArrayList objects when the project is closed
	//bug (when you add an iModel or resurce it does not show up in the GUI) fix
	public void cleanup() {
		((DomeTreeObjectFactory)resourcesTreeFactory).removeCachedTreeObject(projBuilder.getResourceModels());
		((DomeTreeObjectFactory) iModelsTreeFactory).removeCachedTreeObject(projBuilder.getIntegrationModels());
	}

	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final ProjectBuildListPanel getProjectBuildListPanel(ActionEvent e)
		{
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof ProjectBuildListPanel)
				return (ProjectBuildListPanel) comp;
			else
				throw new NullPointerException("No current ProjectBuildListPanel");
		}
	}

	public static final AbstractAction addResourceAction = new FocusTrackerAction("Resource")
	{
		public void actionPerformed(ActionEvent e)
		{

			getProjectBuildListPanel(e).addResource();
		}
	};

	public static final AbstractAction resourceClearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{

			getProjectBuildListPanel(e).resourceTree.clearSelection();
		}
	};

	public static final AbstractAction resourceSelectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{

			getProjectBuildListPanel(e).resourceTree.selectAllVisibleRows();
		}
	};

	public static final AbstractAction addIModelAction = new FocusTrackerAction("Integration model")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).addIntegrationModel();
		}
	};

	public static final AbstractAction iModelClearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{

			getProjectBuildListPanel(e).iModelTree.clearSelection();
		}
	};

	public static final AbstractAction iModelSelectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{

			getProjectBuildListPanel(e).iModelTree.selectAllVisibleRows();
		}
	};

	public static final AbstractAction removeResourceAction = new FocusTrackerAction("Remove resource")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).removeResource();
		}
	};

	public static final AbstractAction relocateResourceAction = new FocusTrackerAction("Relocate resource ...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).relocateResource();
		}
	};

	public static final AbstractAction deleteIModelAction = new FocusTrackerAction("Delete integration model")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).deleteIntegrationModel();
		}
	};

	public static final AbstractAction openIModelAction = new FocusTrackerAction("Open integration model")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).openIntegrationModel();
		}
	};

	public static final AbstractAction copyResourceAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).copySelectedObjects(true);
		}
	};

	public static final AbstractAction pasteLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).pasteLastSelection(true);
		}
	};

	public static final AbstractAction pasteFromClipboardAction = new FocusTrackerAction("From clipboard")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).pasteFromClipboard(true);
		}
	};

	public static final AbstractAction copyIModelAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).copySelectedObjects(false);
		}
	};

	public static final AbstractAction pasteLastISelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).pasteLastSelection(false);
		}
	};

	public static final AbstractAction pasteFromIClipboardAction = new FocusTrackerAction("From clipboard")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).pasteFromClipboard(false);
		}
	};

	public static final AbstractAction addAndSubScribeAction = new FocusTrackerAction("Add and subscribe")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).addAndSubscribe();
		}
	};

	public static final AbstractAction viewBuildAction = new FocusTrackerAction("Build View")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).switchInterfaceView(DomeModelInterface.BUILD_VIEW);
		}
	};

	public static final AbstractAction viewInterfaceCausalityAction = new FocusTrackerAction("Interface Causality View")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).switchInterfaceView(DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
		}
	};

	public static final AbstractAction viewSystemCausalityAction = new FocusTrackerAction("System Causality View")
	{
		public void actionPerformed(ActionEvent e)
		{
			getProjectBuildListPanel(e).switchInterfaceView(DomeModelInterface.SYSTEM_CAUSALITY_VIEW);
		}
	};

	private static TreeObjectFactory makeResourcesTreeFactory()
	{
		DomeTreeObjectFactory factory = new DomeTreeObjectFactory("ProjectResourceBuildTreeObjectFactory");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo",
		                               "mit.cadlab.dome3.gui.objectmodel.project.ProjectResourceTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo$LocationInfo",
		                               "mit.cadlab.dome3.gui.objectmodel.project.ProjectResourceLocationTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.BrowseInterface",
		                               "mit.cadlab.dome3.gui.objectmodel.project.BrowseInterfaceTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.fileSystem.Folder",
		                               "mit.cadlab.dome3.gui.fileSystem.FolderTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.fileSystem.DomeFile",
		                               "mit.cadlab.dome3.gui.fileSystem.DomeFileTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo",
		                               "mit.cadlab.dome3.gui.objectmodel.project.build.BuildProjectIntegrationModelTreeObject");
		BrowseTreeObjectFactory.registerDomeBrowseTreeObjects(factory);
		return factory;
	}

	private static TreeObjectFactory makeIModelsTreeFactory()
	{
		DomeTreeObjectFactory factory = new DomeTreeObjectFactory("ProjectIModelBuildTreeObjectFactory");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo",
		                               "mit.cadlab.dome3.gui.objectmodel.project.build.BuildProjectIntegrationModelTreeObject",
		                               "mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo");
		BrowseTreeObjectFactory.registerDomeBrowseTreeObjects(factory);
		return factory;
	}

	private class ResourceTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent te) {
			int selectioncount = resourceTree.getSelectionCount();
			if(selectioncount == 0) {
				removeResourceAction.setEnabled(false);
				relocateResourceAction.setEnabled(false);
				copyResourceAction.setEnabled(false);
				ProjectBuildMenus.menus.editResourceMenu.setEnabled(false);
			}
			else if(selectioncount == 1) {
				iModelTree.clearSelection();
				Object o = ((GenericObjectTreeNode) resourceTree.getSelectionPath().getLastPathComponent()).getObject();
				if (o instanceof ProjectResourceInfo) {   //entire ressource
					removeResourceAction.setEnabled(true);
					relocateResourceAction.setEnabled(true);
					copyResourceAction.setEnabled(true);
				}
				else {
					if(o instanceof BrowseInterface) {     //interface
						copyResourceAction.setEnabled(true);
					}
					else {
						copyResourceAction.setEnabled(false);
					}
					removeResourceAction.setEnabled(false);
					relocateResourceAction.setEnabled(false);
				}
				ProjectBuildMenus.menus.editResourceMenu.setEnabled(true);
			}
			else {
				iModelTree.clearSelection();
				removeResourceAction.setEnabled(true);
				relocateResourceAction.setEnabled(false);
				copyResourceAction.setEnabled(true);
				ProjectBuildMenus.menus.editResourceMenu.setEnabled(true);
			}
		}
	}

	private class IModelTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent te)
		{
			int selectioncount = iModelTree.getSelectionCount();
			if (selectioncount == 0) {
				deleteIModelAction.setEnabled(false);
				openIModelAction.setEnabled(false);
				copyIModelAction.setEnabled(false);
				ProjectBuildMenus.menus.editIModelMenu.setEnabled(false);
			}
			else if (selectioncount == 1) {
				resourceTree.clearSelection();
				Object o = ((GenericObjectTreeNode) iModelTree.getSelectionPath().getLastPathComponent()).getObject();
				if (o instanceof ProjectIntegrationModelInfo) {  //integration model
					deleteIModelAction.setEnabled(true);
					openIModelAction.setEnabled(true);
					copyIModelAction.setEnabled(true);
				}
				else {
					if (o instanceof ModelInterface) {    //interface
						copyIModelAction.setEnabled(true);
					} else {
						copyIModelAction.setEnabled(false);
					}
					deleteIModelAction.setEnabled(false);
					openIModelAction.setEnabled(false);
				}
				ProjectBuildMenus.menus.editIModelMenu.setEnabled(true);
			}
			else {
				resourceTree.clearSelection();
				deleteIModelAction.setEnabled(false);
				openIModelAction.setEnabled(false);
				copyIModelAction.setEnabled(false);
				ProjectBuildMenus.menus.editIModelMenu.setEnabled(true);
			}
		}
	}
}
