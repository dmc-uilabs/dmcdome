//ModelInterfaceTreeBuilderPanel.java

package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.ShiftSupport;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.util.DSet;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ModelInterfaceTreeBuilderPanel extends TreeBuilderPanel
{
	protected ModelInterface ifaceBuilder;
	protected BuildModelInterfaceTree tree;
	protected BuildTreeTable treeTable;

	public ModelInterfaceTreeBuilderPanel(ModelInterface ifaceBuilder,
	                                      String view)
	{
		this.ifaceBuilder = ifaceBuilder;
		tree = new BuildModelInterfaceTree(ifaceBuilder, view);
		tree.addTreeSelectionListener(new ModelInterfaceBuilderTreeSelectionListener());
		treeTable = new BuildTreeTable(tree);
		if (ifaceBuilder instanceof DomeModelInterface) {
			if (((DomeModelInterface) ifaceBuilder).isDefaultInterface()) {
				treeTable.setEnabled(false);
			}
		}
		layoutComponent(treeTable);
		upButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		downButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		upDownButtonPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
	}

	protected void moveUpAction()
	{ // implementation for multiple parents
		treeTable.stopEditing();
		SelectionInfo selectionInfo = getSelectionChildIndicesByParent();
		if (selectionInfo == null) return;
		HashMap selections = selectionInfo.selections;
		int[] parentIndices = selectionInfo.parentIndices;
		HashMap newChildIndicesByParent = new HashMap();
		for (int i = parentIndices.length - 1; i >= 0; --i) { // move items bottom up
			int parentIndex = parentIndices[i];
			List selectedChildren = (List) selections.get(new Integer(parentIndex));
			ObjectTreeNode parentNode = (ObjectTreeNode) selectedChildren.remove(0);
			DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
			int[] childIndices = listToIntArray(selectedChildren);
			if (parentObj instanceof ShiftSupport) {
				((ShiftSupport) parentObj).shiftLeft(childIndices);
			} else {
				DefaultContextBuilder.showWarning(this, "does not support reordering items", getObjName(parentObj));
				continue;
			}
			// calculate new child indices
			Arrays.sort(childIndices);
			int[] newIndices;
			if (childIndices[0] == 0) { // skip
				newIndices = new int[childIndices.length - 1];
				for (int j = 1; j < childIndices.length; ++j)
					newIndices[j - 1] = childIndices[j] - 1;
			} else { // shift all indices up
				newIndices = new int[childIndices.length];
				for (int j = 0; j < childIndices.length; ++j)
					newIndices[j] = childIndices[j] - 1;
			}
			newChildIndicesByParent.put(parentNode, newIndices);
		}
		setSelectionsByParent(newChildIndicesByParent);
	}

	protected void moveDownAction()
	{ // implementation for multiple parents
		treeTable.stopEditing();
		SelectionInfo selectionInfo = getSelectionChildIndicesByParent();
		if (selectionInfo == null) return;
		HashMap selections = selectionInfo.selections;
		int[] parentIndices = selectionInfo.parentIndices;
		HashMap newChildIndicesByParent = new HashMap();
		for (int i = parentIndices.length - 1; i >= 0; --i) { // move items bottom up
			int parentIndex = parentIndices[i];
			List selectedChildren = (List) selections.get(new Integer(parentIndex));
			ObjectTreeNode parentNode = (ObjectTreeNode) selectedChildren.remove(0);
			DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
			int[] childIndices = listToIntArray(selectedChildren);
			if (parentObj instanceof ShiftSupport) {
				((ShiftSupport) parentObj).shiftRight(childIndices);
			} else {
				DefaultContextBuilder.showWarning(this, "does not support reordering items", getObjName(parentObj));
				continue;
			}
			// calculate new child indices
			Arrays.sort(childIndices);
			int[] newIndices;
			if (childIndices[childIndices.length - 1] == parentNode.getChildCount() - 1) { // skip
				newIndices = new int[childIndices.length - 1];
				for (int j = 0; j < (childIndices.length - 1); ++j)
					newIndices[j] = childIndices[j] + 1;
			} else { // shift all indices down
				newIndices = new int[childIndices.length];
				for (int j = 0; j < childIndices.length; ++j)
					newIndices[j] = childIndices[j] + 1;
			}
			newChildIndicesByParent.put(parentNode, newIndices);
		}
		setSelectionsByParent(newChildIndicesByParent);
	}

	protected SelectionInfo getSelectionChildIndicesByParent()
	{
		if (tree.isSelectionEmpty()) return null;
		int[] selectedRows = tree.getSelectionRows();
		HashMap selections = sortSelectedChildIndicesByParent(selectedRows);
		int[] parentIndices = new int[selections.size()];
		Iterator it = selections.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			parentIndices[index++] = ((Integer) it.next()).intValue();
		}
		Arrays.sort(parentIndices);
		return new SelectionInfo(selections, parentIndices);
	}

	class SelectionInfo
	{
		public HashMap selections;
		public int[] parentIndices;

		public SelectionInfo(HashMap selections, int[] parentIndices)
		{
			this.selections = selections;
			this.parentIndices = parentIndices;
		}
	}

	protected HashMap sortSelectedObjectsByParent(int[] selectedRows)
	{
		HashMap selections = new HashMap();
		for (int i = 0; i < selectedRows.length; ++i) {
			int currentRow = selectedRows[i];
			TreePath currentTreePath = tree.getPathForRow(currentRow);
			DefaultObjectTreeNode currentNode = (DefaultObjectTreeNode) currentTreePath.getLastPathComponent();
			TreePath parentPath = currentTreePath.getParentPath();
			int parentRow = tree.getRowForPath(parentPath);
			DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) currentNode.getParent();
			// if parent is filter, get context
			DomeObject childObj = ((DomeTreeObject) currentNode.getTreeObject()).getDomeObject();
			Integer key = new Integer(parentRow);
			if (selections.containsKey(key)) { // add to it
				List selectedChildren = (List) selections.get(key);
				selectedChildren.add(childObj);
			} else { // create new key
				List selectedChildren = new ArrayList();
				DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
				selectedChildren.add(parentObj); // parent first
				selectedChildren.add(childObj);
				selections.put(key, selectedChildren);
			}
		}
		return selections;
	}

	protected HashMap sortSelectedChildIndicesByParent(int[] selectedRows)
	{
		HashMap selections = new HashMap();
		for (int i = 0; i < selectedRows.length; ++i) {
			int currentRow = selectedRows[i];
			TreePath currentTreePath = tree.getPathForRow(currentRow);
			DefaultObjectTreeNode currentNode = (DefaultObjectTreeNode) currentTreePath.getLastPathComponent();
			TreePath parentPath = currentTreePath.getParentPath();
			int parentRow = tree.getRowForPath(parentPath);
			DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) currentNode.getParent();
			int childIndex = parentNode.getIndex(currentNode);
			Integer key = new Integer(parentRow);
			if (selections.containsKey(key)) { // add to it
				List selectedChildren = (List) selections.get(key);
				selectedChildren.add(new Integer(childIndex));
			} else { // create new key
				List selectedChildren = new ArrayList();
				selectedChildren.add(parentNode); // parent first
				selectedChildren.add(new Integer(childIndex));
				selections.put(key, selectedChildren);
			}
		}
		return selections;
	}

	protected void setSelectionsByParent(HashMap selections)
	{
		List newSelections = new ArrayList();
		Iterator it = selections.keySet().iterator();
		while (it.hasNext()) {
			DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) it.next();
			int[] childIndices = (int[]) selections.get(parentNode);
			int parentIndex = tree.getRowForPath(new TreePath(parentNode.getPath()));
			for (int i = 0; i < childIndices.length; ++i)
				newSelections.add(new Integer(parentIndex + childIndices[i] + 1));
		}
		int[] newSelectionsArray = listToIntArray(newSelections);
		Arrays.sort(newSelectionsArray);
		tree.setSelectionRows(newSelectionsArray);
	}

	protected int[] listToIntArray(List v)
	{
		int[] ints = new int[v.size()];
		for (int i = 0; i < ints.length; ++i)
			ints[i] = ((Integer) v.get(i)).intValue();
		Arrays.sort(ints);
		return ints;
	}

	class ModelInterfaceBuilderTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			setEditMenusForSelection();
		}
	}

	protected void setEditMenusForSelection()
	{
		if (ifaceBuilder instanceof DomeModelInterface) {
			if (((DomeModelInterface) ifaceBuilder).isDefaultInterface()) {
				ModelInterfaceBuildMenus.menus.disableContextRelationMIs();
				ModelInterfaceBuildMenus.menus.disableAddMenus();
				ModelInterfaceBuildMenus.menus.disableMapMIs();
				ModelInterfaceBuildMenus.menus.disableRemoveMenus();
				ModelInterfaceBuildMenus.menus.disableSelectionMIs();
				setMoveEnabled(false);
				return;
			}
		}
		if (!validateTreeSelection()) return; // update at next tree selection event
		// add/insert only for single/no selection
		// delete/remove for one or more selection
		// copy to clipboard for one or more selection
		// paste only for single/no selection
		// all within non-filtered context -- move up/down
		String view = null;
		if (ifaceBuilder instanceof DomeModelInterface) {
			view = ((DomeModelInterface) ifaceBuilder).getCurrentView();
		}
		if (view.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
			ModelInterfaceBuildMenus.menus.disableAddMenus();
			//switching between model view window and interface window with
			//SYSTEM_CAUSALITY_VIEW somehow keeps the AddandMap enabled
			//so added the following line to disable AddandMap
			ModelInterfaceBuildMenus.menus.disableAddAndMapMIs();
		}
		ModelInterfaceBuildMenus.menus.disableContextRelationMIs();
		int selectionCount = tree.getSelectionCount();
		if (selectionCount > 1) {
			ModelInterfaceBuildMenus.menus.disableAddMenus();
		} else if (selectionCount == 1) {
			if (view.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
				ModelInterfaceBuildMenus.menus.enableAddMenus();
				ModelInterfaceBuildMenus.menus.disableContextRelationMIs();
			}
			if (isInterfaceParameterSelected()) {
				ModelInterfaceBuildMenus.menus.enableMapMIs();
			} else {
				ModelInterfaceBuildMenus.menus.disableMapMIs();
			}
		} else { // no selection, insert into interface
			if (view.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
				ModelInterfaceBuildMenus.menus.enableAddMenus();
				ModelInterfaceBuildMenus.menus.disableContextRelationMIs();
			}
			ModelInterfaceBuildMenus.menus.disableMapMIs();
		}
		if (selectionCount == 0) {
			ModelInterfaceBuildMenus.menus.disableRemoveMenus();
		} else {
			ModelInterfaceBuildMenus.menus.enableRemoveMenus();
		}
		if (selectionCount == 0) {
			setMoveEnabled(false);
		} else if (selectionCount == 1) {
			setMoveEnabled(canMoveSingleSelection());
		} else {
			setMoveEnabled(canMoveMultipleSelections());
		}
	}

	protected boolean isInterfaceParameterSelected()
	{
		DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) tree.getSelectionPath().getLastPathComponent();
		DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
		return dObj instanceof Parameter;
	}

	protected boolean canMoveSingleSelection()
	{
		// if parent has more than one child and parent instanceof ShiftSupport
		// doesn't enable one button and not the other
		TreePath selectedPath = tree.getSelectionPath();
		DefaultObjectTreeNode parent = (DefaultObjectTreeNode) ((TreeNode) selectedPath.getLastPathComponent()).getParent();
		DomeObject dObj = ((DomeTreeObject) parent.getTreeObject()).getDomeObject();
		return (parent.getChildCount() > 1 && (dObj instanceof ShiftSupport));
	}

	protected boolean canMoveMultipleSelections()
	{
		// if from same parent and parent instance of ShiftSupport
		TreePath[] selectedPaths = tree.getSelectionPaths();
		DefaultObjectTreeNode firstParent = (DefaultObjectTreeNode) ((TreeNode) selectedPaths[0].getLastPathComponent()).getParent();
		DomeObject parent = ((DomeTreeObject) firstParent.getTreeObject()).getDomeObject();
		if (!(parent instanceof ShiftSupport))
			return false;
		for (int i = 1; i < selectedPaths.length; ++i) {
			if (!firstParent.equals(((TreeNode) selectedPaths[i].getLastPathComponent()).getParent()))
				return false;
		}
		return selectedPaths.length != firstParent.getChildCount();
	}

	// returns true if tree is valid, false if not
	protected boolean validateTreeSelection()
	{
		// if tree selection is in inconsisten state, clear Selection
		int count = tree.getSelectionCount();
		int[] selectedRows = tree.getSelectionRows();
		if ((selectedRows == null && count != 0) ||
		        (selectedRows != null && (selectedRows.length != count))) {
			tree.clearSelection();
			return false;
		}
		return true;
	}

	protected void printTreeSelection()
	{
		int count = tree.getSelectionCount();
		System.out.println("tree selection count: " + count);
		int[] selectedRows = tree.getSelectionRows();
		if (selectedRows != null) {
			for (int i = 0; i < selectedRows.length; ++i)
				System.out.print(selectedRows[i] + "  ");
		}
		System.out.println();
	}

	public void addNewModelObject(String type)
	{
		treeTable.stopEditing();
		if (tree.isSelectionEmpty()) { // nothing selected, add to input filter in the tree
			ifaceBuilder.newModelObject(type);
		} else { // find insertion point
			int[] selectedIndices = tree.getSelectionRows();
			Arrays.sort(selectedIndices);
			int selectedIndex = selectedIndices[0]; // first item selected
			TreePath selectedPath = tree.getPathForRow(selectedIndex);
			setCausalityBasedonPath(selectedPath);
			DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
			if (tree.isExpanded(selectedPath)) { // must be a filter, so just add to proper filter
				ifaceBuilder.newModelObject(type);
			} else {
				DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
				int childIndex = parentNode.getIndex(selectedNode);
				if (ifaceBuilder instanceof ModelInterfaceBuilder) {
					((ModelInterfaceBuilder) ifaceBuilder).newModelObject(type, childIndex);
				}
			}
		}
	}

	private void setCausalityBasedonPath(TreePath selectedPath)
	{
		if (selectedPath.equals(tree.getInputFilterPath()) ||
		        selectedPath.equals(tree.getIndependentFilterPath())) {
			if (ifaceBuilder instanceof ModelInterfaceBuilder) {
				((ModelInterfaceBuilder) ifaceBuilder).setNewObjectCausality(CausalityStatus.INDEPENDENT);
			}
		} else if (selectedPath.equals(tree.getOutputFilterPath()) ||
		        selectedPath.equals(tree.getResultFilterPath())) {
			if (ifaceBuilder instanceof ModelInterfaceBuilder) {
				((ModelInterfaceBuilder) ifaceBuilder).setNewObjectCausality(CausalityStatus.RESULT);
			}
		}
//		else if (selectedPath.equals(tree.getIndeterminateFilterPath())) {
//			if (ifaceBuilder instanceof ModelInterfaceBuilder) {
//				((ModelInterfaceBuilder) ifaceBuilder).setNewObjectCausality(CausalityStatus.INDETERMINATE);
//			}
//		}
		else if (selectedPath.equals(tree.getIntermediateFilterPath())) {
			if (ifaceBuilder instanceof ModelInterfaceBuilder) {
				((ModelInterfaceBuilder) ifaceBuilder).setNewObjectCausality(CausalityStatus.INTERMEDIATE);
			}
		}
	}

	protected void setPaths(String view)
	{
		tree.setPaths(view);
	}

	public void copySelectedModelObjects()
	{
		if (tree.isSelectionEmpty()) return;
		// copy in order in tree
		int[] selectionRows = tree.getSelectionRows();
		Arrays.sort(selectionRows);
		List selectedDomeObjects = new ArrayList();
		for (int i = 0; i < selectionRows.length; ++i) {
			TreePath selectedPath = tree.getPathForRow(selectionRows[i]);
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
			DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
			if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter)) {
				//&& !(dObj.equals(ifaceBuilder.getViewOnlyContext()))  )
				selectedDomeObjects.add(dObj);
			}
		}
		if (selectedDomeObjects.isEmpty()) return; // nothing to copy
		BuildMode.clipboard.addSelection(selectedDomeObjects);
	}

	public void pasteCopyLastSelection()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		pasteCopies(sel.getItems());
	}

	protected void pasteCopies(List items)
	{
		treeTable.stopEditing();
		items = filterForValidItems(ifaceBuilder, items);
		if (tree.isSelectionEmpty()) { // nothing selected
			ifaceBuilder.newModelObjects(items);
		} else { // find insertion point
			int[] selectedIndices = tree.getSelectionRows();
			Arrays.sort(selectedIndices);
			int selectedIndex = selectedIndices[0]; // first item selected
			TreePath selectedPath = tree.getPathForRow(selectedIndex);
			setCausalityBasedonPath(selectedPath);
			DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
			if (tree.isExpanded(selectedPath)) { // must be a filter
				ifaceBuilder.newModelObjects(items);
			} else {
				DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
				int childIndex = parentNode.getIndex(selectedNode);
				if (ifaceBuilder instanceof ModelInterfaceBuilder) {
					((ModelInterfaceBuilder) ifaceBuilder).newModelObjects(items, childIndex);
				}
			}
		}
	}

	public void pasteCopyFromClipboard()
	{
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
		if (selections == null) return; // nothing selected in clipboard!
		ArrayList allSelections = new ArrayList(); // items can be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());
		pasteCopies(allSelections);
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
		items = filterForValidItems(ifaceBuilder, items);
		if (tree.isSelectionEmpty()) { // nothing to map to
			return;
		} else if (tree.getSelectionCount() != 1) { // to many selected
			return;
		}
		TreePath selectedPath = tree.getSelectionPath();
		DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
		DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
		if (dObj instanceof Parameter) {
			try {
				ConnectionMappingManager mm = null;
				Model mo = ifaceBuilder.getModel();
				if (mo instanceof DomeModel) {
					mm = ((DomeModel) mo).getMappingManager();
				} else if (mo instanceof IntegrationProject) {
					mm = ((IntegrationProject) mo).getMappingManager();
				}
				Collection mappings = mm.getMappingsForParameter((Parameter) dObj);
				//test2 if interface parameter rp is already mapped to another model parameter
				if (mappings == null || mappings.size() == 0) {
					mm.addMappings((Parameter) dObj, items);
				} else { //pop a warning dialog
					OneButton1Msg.showWarning(this, "Warning: mapping",
					                          "Interface parameter cannot be mapped to two model parameters.",
					                          "OK", new Dimension(150, 100));
				}
			} catch (RuntimeException e) {
				handleMappingErrors(e);
			}
		} // else can not map
	}

	protected void handleMappingErrors(RuntimeException ex)
	{
		System.err.println(ex);
		ex.printStackTrace();
	}

	public void addAndMapLastSelection()
	{
//		System.out.println("addAndMapLastSelection");
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
//		System.out.println("selected:" + sel);
		if (sel == null) return; // nothing in clipboard!
		addAndMapItems(sel.getItems());
	}

	public void addAndMapFromClipboard()
	{
//		System.out.println("addAndMapFromClipboard");
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
		if (selections == null) return; // nothing selected in clipboard!
//		System.out.println("selected: " + Names.getNameIds(Arrays.asList(selections)));
		DSet allSelections = new DSet(); // items can not be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());
		addAndMapItems(allSelections);
	}

	protected void addAndMapItems(List items)
	{
//		System.out.println("addAndMapItems: " + Names.getNameIds(items));
		treeTable.stopEditing();
		items = filterForValidItems(ifaceBuilder, items);
		try {
			if (tree.isSelectionEmpty()) { // paste in top container
				if (ifaceBuilder instanceof ModelInterfaceBuilder) {
					((ModelInterfaceBuilder) ifaceBuilder).addAndMapModelObjects(items);
				}
			} else { // find insertion point
				int[] selectedIndices = tree.getSelectionRows();
				Arrays.sort(selectedIndices);
				int selectedIndex = selectedIndices[0]; // first item selected
				TreePath selectedPath = tree.getPathForRow(selectedIndex);
				setCausalityBasedonPath(selectedPath);
				DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
				if (tree.isExpanded(selectedPath)) { // must be a filter, so just add to relation
					if (ifaceBuilder instanceof ModelInterfaceBuilder) {
						((ModelInterfaceBuilder) ifaceBuilder).addAndMapModelObjects(items);
					}
				} else {
					DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
					int childIndex = parentNode.getIndex(selectedNode);
					if (ifaceBuilder instanceof ModelInterfaceBuilder) {
						((ModelInterfaceBuilder) ifaceBuilder).addAndMapModelObjects(items, childIndex);
					}
				}
			}
		} catch (RuntimeException e) {
			handleMappingErrors(e);
		}
	}

	public void deleteSelectedModelObjects()
	{
		treeTable.stopEditing();
		if (tree.isSelectionEmpty()) return;
		TreePath[] selectedPaths = tree.getSelectionPaths();
		List selectedDomeObjects = new ArrayList();
		for (int i = 0; i < selectedPaths.length; ++i) {
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
			DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
			if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter)) {
				// && !(dObj.equals(ifaceBuilder.getViewOnlyContext())) ) {
				selectedDomeObjects.add(dObj);
			} else {
				showWarning(this, "System filters cannot be deleted.");
			}
		}
		if (selectedDomeObjects.isEmpty()) return; // nothing to delete
		ifaceBuilder.deleteModelObjects(selectedDomeObjects);
	}

	public void cutSelectedModelObjects()
	{
		// copy, then delete
		copySelectedModelObjects();
		removeSelectedModelObjects();
	}

	public void removeSelectedModelObjects()
	{
		treeTable.stopEditing();
		if (tree.isSelectionEmpty()) return;
		TreePath[] selectedPaths = tree.getSelectionPaths();
		List selectedDomeObjects = new ArrayList();
		for (int i = 0; i < selectedPaths.length; ++i) {
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
			DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
			if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter)) {
				// && !(dObj.equals(ifaceBuilder.getViewOnlyContext())) ) {
				selectedDomeObjects.add(dObj);
			} else {
				showWarning(this, "System filters cannot be cut.");
			}
		}
		if (selectedDomeObjects.isEmpty()) return; // nothing to delete

		if (ifaceBuilder instanceof ModelInterfaceBuilder) {
			((ModelInterfaceBuilder) ifaceBuilder).removeModelObjects(selectedDomeObjects);
		}
	}

	protected static final Dimension WARNING_SIZE1 = new Dimension(280, 130);

	public static void showWarning(Component comp, String msg)
	{
		OneButton1Msg.showWarning(comp, "Warning: illegal operation", msg, "OK", WARNING_SIZE1);
	}

	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final TreeBuilderPanel getModelInterfaceTreeBuilderPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof ModelInterfaceTreeBuilderPanel) {
					return (ModelInterfaceTreeBuilderPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof ModelInterfaceTreeBuilderPanel)
				return (ModelInterfaceTreeBuilderPanel) comp;
			else if (comp instanceof ContextTreeBuilderPanel)
				return (ContextTreeBuilderPanel) comp;
			else if(comp instanceof ModelViewBuildPanel)
				return ((ModelViewBuildPanel)comp).getModelViewPanel();
			/**/System.err.println("No current ModelInterfaceTreeBuilderPanel");
			throw new NullPointerException("No current ModelInterfaceTreeBuilderPanel");
		}
	}

	public static class AddItemAction extends FocusTrackerAction
	{
		public AddItemAction(String itemType)
		{
			super(itemType);
		}

		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).addNewModelObject((String) getValue(
				        AbstractAction.NAME));
			} else {
				((ContextTreeBuilderPanel) pan).addNewModelObject((String) getValue(
				        AbstractAction.NAME));
			}
		}
	}

	// --- actions for menus and buttons --------------------
	public static final AbstractAction copyAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).copySelectedModelObjects();
			} else {
				((ContextTreeBuilderPanel) pan).copySelectedModelObjects();
			}
		}
	};

	public static final AbstractAction cutAction = new FocusTrackerAction("Cut")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).cutSelectedModelObjects();
			} else {
				((ContextTreeBuilderPanel) pan).cutSelectedModelObjects();
			}
		}
	};

	public static final AbstractAction pasteCopyLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).pasteCopyLastSelection();
			} else {
				((ContextTreeBuilderPanel) pan).pasteCopyLastSelection();
			}
		}
	};

	public static final AbstractAction pasteCopyClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).pasteCopyFromClipboard();
			} else {
				((ContextTreeBuilderPanel) pan).pasteCopyFromClipboard();
			}
		}
	};

	public static final AbstractAction mapLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).mapLastSelection();
			} else {
				((ContextTreeBuilderPanel) pan).mapLastSelection();
			}
		}
	};

	public static final AbstractAction mapClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).mapFromClipboard();
			} else {
				((ContextTreeBuilderPanel) pan).mapFromClipboard();
			}
		}
	};

	public static final AbstractAction addAndMapLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			/*System.err.println("addAndMapStart");*/
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).addAndMapLastSelection();
			} else {
				((ContextTreeBuilderPanel) pan).addAndMapLastSelection();
			}
			/*System.err.println("addAndMapEnd"); */
		}
	};

	public static final AbstractAction addAndMapClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).addAndMapFromClipboard();
			} else {
				((ContextTreeBuilderPanel) pan).addAndMapFromClipboard();
			}
		}
	};

	public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).deleteSelectedModelObjects();
			} else {
				((ContextTreeBuilderPanel) pan).deleteSelectedModelObjects();
			}
		}
	};

	public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).tree.clearSelection();
			} else {
				((ContextTreeBuilderPanel) pan).getContextTree().clearSelection();
			}
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeBuilderPanel pan = getModelInterfaceTreeBuilderPanel(e);
			if (pan instanceof ModelInterfaceTreeBuilderPanel) {
				((ModelInterfaceTreeBuilderPanel) pan).tree.selectAllVisibleRows();
			} else {
				((ContextTreeBuilderPanel) pan).getContextTree().selectAllVisibleRows();
			}
		}
	};

}
