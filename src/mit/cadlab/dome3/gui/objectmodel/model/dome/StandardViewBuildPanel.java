// StandardViewBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.model.dome;

import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.util.DSet;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.Collection;

public class StandardViewBuildPanel extends JPanel
{

	protected static GridBagConstraints gbc;

	protected List viewMembers;
	protected BuildTree tree;
	protected BuildTreeTable treeTable;
	protected ModelObjectScope scope;

	public StandardViewBuildPanel(ModelObjectScope scope, List viewMembers)
	{
		this(scope, viewMembers, null, null);
	}

	public StandardViewBuildPanel(ModelObjectScope scope, List viewMembers, String[] colNames, int[] colWidths)
	{
		this.scope = scope;
		if (viewMembers == null)
			throw new NullPointerException("StandardViewBuildPanel - null viewMembers");
		this.viewMembers = viewMembers;
		layoutComponent(colNames, colWidths);
	}

	protected void layoutComponent(String[] colNames, int[] colWidths)
	{
		tree = new BuildTree(viewMembers);
		tree.addTreeSelectionListener(new BuilderTreeSelectionListener());
		if (colNames == null || colWidths == null)
			treeTable = new BuildTreeTable(tree);
		else
			treeTable = new BuildTreeTable(tree, Math.max(colNames.length, colWidths.length), colNames, colWidths);
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		JComponent[] comps = {scrollPane};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	public void copySelectedModelObjects()
	{
		Vector selectedObjects = getSelectedModelObjects();
		if (selectedObjects != null)
			BuildMode.clipboard.addSelection(selectedObjects);
	}

	public void deleteSelectedModelObjects()
	{
		treeTable.stopEditing();
		Vector selectedObjects = getSelectedModelObjects();
		if (selectedObjects != null)
			(((ModelObject) selectedObjects.get(0)).getModel()).deleteModelObjects(selectedObjects);
	}

	protected Vector getSelectedModelObjects()
	{
		if (tree.isSelectionEmpty() || tree.getSelectionCount() < 1) return null;
		int[] selectionRows = tree.getSelectionRows();
		Arrays.sort(selectionRows);
		Vector selectedModelObjects = new Vector();
		for (int i = 0; i < selectionRows.length; ++i) {
			TreePath selectedPath = tree.getPathForRow(selectionRows[i]);
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
			DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
			if (!(dObj instanceof Filter) &&
			        (dObj instanceof ModelObject) &&
			        !selectedModelObjects.contains(dObj))
				selectedModelObjects.add(dObj);
		}
		return selectedModelObjects;
	}

	public void clearSelection()
	{
		tree.clearSelection();
	}

	public void selectAll()
	{
		tree.selectAllVisibleRows();
	}

	public void mapLastSelection()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		mapItems(sel.getItems());
	}

	public void mapFromClipboard()
	{
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
		if (selections == null) return; // nothing selected in clipboard!
		DSet allSelections = new DSet(); // items can not be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());
		mapItems(allSelections);
	}

	protected void mapItems(List items)
	{
		treeTable.stopEditing();
		if (tree.isSelectionEmpty()) { // nothing to map to
			return;
		} else if (tree.getSelectionCount() != 1) { // to many selected
			return;
		}
		DomeModelBuilder mod = (DomeModelBuilder) scope;
		TreePath selectedPath = tree.getSelectionPath();
		DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
		DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
		if (dObj instanceof Parameter) {
			//only one param can be mapped to another at a time
			Object target = items.get(0);
			if (target instanceof Parameter) {
				if (((Parameter) target).getScope() instanceof Model) {
					OneButton1Msg.showWarning(this, "Warning: mapping", "Model parameters cannot" +
					                 " be directly mapped to each other.  Use a relation parameter instead.", "OK",
					                 new Dimension(150, 100));
					return;
				}
				try {
					IntegrationProject proj = mod.getIntegrationProject();
					ConnectionMappingManager mm = null;
					if(proj == null)
						mm = mod.getMappingManager();
					else
						mm = proj.getMappingManager();
					mm.addMapping((Parameter) target, (Parameter)dObj);
				}
				catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
		else {// else can not map
			OneButton1Msg.showWarning(this, "Warning: mapping", "Only parameters can be mapped to each other",
			                          "OK", new Dimension(150, 100));
		}
	}

	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
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
		protected final StandardViewBuildPanel getStandardViewBuildPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof StandardViewBuildPanel) {
					return (StandardViewBuildPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof StandardViewBuildPanel)
				return (StandardViewBuildPanel) comp;
			throw new NullPointerException("No current StandardViewBuildPanel");
		}
	}

	// --- actions for menus and buttons --------------------

	public static final AbstractAction copyAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			getStandardViewBuildPanel(e).copySelectedModelObjects();
		}
	};

	public static final AbstractAction clearAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getStandardViewBuildPanel(e).clearSelection();
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{
			getStandardViewBuildPanel(e).selectAll();
		}
	};

	public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			getStandardViewBuildPanel(e).deleteSelectedModelObjects();
		}
	};

	public static final AbstractAction mapLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getStandardViewBuildPanel(e).mapLastSelection();
		}
	};

	public static final AbstractAction mapClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getStandardViewBuildPanel(e).mapFromClipboard();
		}
	};


	public static final JMenu menu = makeMenu();

	protected static JMenu makeMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Edit definition");
		m.add(MenuUtils.makeMenuItem(StandardViewBuildPanel.copyAction));
		m.add(MenuUtils.makeMenuItem(StandardViewBuildPanel.clearAction));
		m.add(MenuUtils.makeMenuItem(StandardViewBuildPanel.selectAllAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(StandardViewBuildPanel.deleteAction));
		return m;
	}

	class BuilderTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			TreePath path = e.getNewLeadSelectionPath();
			if (path != null) {
				Object obj = path.getLastPathComponent();
				if (obj != null && obj instanceof BuildObjectTreeNode) {
					DomeObject dobject = ((BuildObjectTreeNode) obj).getDomeObject();
					if (dobject instanceof Relation) {
						//System.out.println("ENABLE TEST RELATION");
						((DomeModelBuilder) scope).setRelationToTest((Relation) dobject);
						DomeModelBuildPanel.testRelationAction.setEnabled(true);
					} else {
						//System.out.println("disable test2 relation");
						((DomeModelBuilder) scope).setRelationToTest(null);
						DomeModelBuildPanel.testRelationAction.setEnabled(false);
					}
				}
			} else {
				//System.out.println("disable test2 relation");
				((DomeModelBuilder) scope).setRelationToTest(null);
				DomeModelBuildPanel.testRelationAction.setEnabled(false);
			}
		}
	}
}
