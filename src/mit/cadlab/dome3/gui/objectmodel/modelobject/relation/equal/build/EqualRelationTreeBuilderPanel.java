// ProceduralRelationTreeBuilderPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build;

import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.ShiftSupport;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.BuildEqualRelationTree;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildMenus;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.util.DSet;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EqualRelationTreeBuilderPanel extends TreeBuilderPanel
{

	protected ConcreteEqualRelation relationBuilder;
	protected BuildEqualRelationTree tree;
	protected BuildTreeTable treeTable;


	public EqualRelationTreeBuilderPanel(ConcreteEqualRelation relationBuilder,
	                                     String view)
	{
		this.relationBuilder = relationBuilder;
//		this.relationBuilder.addModelObjectsListener(new RelationListener());
		tree = new BuildEqualRelationTree(relationBuilder, view);
		tree.addTreeSelectionListener(new EqualRelationBuilderTreeSelectionListener());
		treeTable = new BuildTreeTable(tree);
		layoutComponent(treeTable);
	}

	public BuildEqualRelationTree getTree()
	{
		return tree;
	}

	//slightly changed the layoutComponent function to get rid of the up and down panel
	protected void layoutComponent(Component treeTable)
	{
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		upDownButtonPanel = makeButtonPanel();
		JComponent[] comps = {scrollPane};
		//JComponent[] comps = {scrollPane, upDownButtonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			//	new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);

	}


	//no add, must be through map

	public void addNewModelObject(String type)
	{
		treeTable.stopEditing();
		if (tree.isSelectionEmpty()) { // nothing selected, add to tree
			relationBuilder.newModelObject(type);
		} else { // find insertion point
			int[] selectedIndices = tree.getSelectionRows();
			Arrays.sort(selectedIndices);
			int selectedIndex = selectedIndices[0]; // first item selected
			TreePath selectedPath = tree.getPathForRow(selectedIndex);
			DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
			if (tree.isExpanded(selectedPath)) { // must be a filter, so just add to relation
				relationBuilder.newModelObject(type);
			} else {
				DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
				DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
				if (relationBuilder.isInputFilter(parentObj)) { // selection is insertion point
					int childIndex = parentNode.getIndex(selectedNode);
					relationBuilder.newModelObject(type, childIndex);
				} else {
					relationBuilder.newModelObject(type);
				}
			}
		}
	}

	//can't delete
	/*
    public void deleteSelectedModelObjects() {
        treeTable.stopEditing();
        if (tree.isSelectionEmpty()) return;
        TreePath[] selectedPaths = tree.getSelectionPaths();
        List selectedDomeObjects = new ArrayList();
        for (int i = 0; i < selectedPaths.length; ++i) {
            DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
            DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
            if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter)) {
                selectedDomeObjects.add(dObj);
            }
        }
        if (selectedDomeObjects.isEmpty()) return; // nothing to delete
        relationBuilder.deleteModelObjects(selectedDomeObjects);
    }
*/
//  protected String getNamesOfChildren(List children) {
//    if (children == null || children.size()==0) return "";
//    if (children.size() == 1) return ((ModelObject)children.get(0)).getName();
//    if (children.size() == 2) return ((ModelObject)children.get(0)).getName()+
//			  " and "+((ModelObject)children.get(1)).getName();
//    // 3 or more objects
//    StringBuffer sb = new StringBuffer("");
//    for (int i=0; i<children.size()-1; ++i) {
//      sb.append(((ModelObject)children.get(i)).getName()+", ");
//    }
//    sb.append("and "+((ModelObject)children.get(children.size()-1)).getName());
//    return sb.toString();
//  }

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
			if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
				selectedDomeObjects.add(dObj);
		}
		if (selectedDomeObjects.isEmpty()) return; // nothing to copy
		BuildMode.clipboard.addSelection(selectedDomeObjects);
	}

	//paste copy is not allowed
	/*
    public void pasteCopyLastSelection() {
        ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
        if (sel == null) return; // nothing in clipboard!
        pasteCopies(sel.getItems());
    }

    public void pasteCopyFromClipboard() {
        ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
        if (selections == null) return; // nothing selected in clipboard!
        ArrayList allSelections = new ArrayList(); // items can be repeated
        for (int i = 0; i < selections.length; ++i)
            allSelections.addAll(selections[i].getItems());
        pasteCopies(allSelections);
    }

    protected void pasteCopies(List items) {
        treeTable.stopEditing();
        items = filterForValidItems(relationBuilder,items);
        if (tree.isSelectionEmpty()) { // paste in top container
            relationBuilder.newModelObjects(items);
        } else { // find insertion point
            int[] selectedIndices = tree.getSelectionRows();
            Arrays.sort(selectedIndices);
            int selectedIndex = selectedIndices[0]; // first item selected
            TreePath selectedPath = tree.getPathForRow(selectedIndex);
            DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
            if (tree.isExpanded(selectedPath)) { // must be a filter, so just add to relation
                relationBuilder.newModelObjects(items);
            } else {
                DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                DomeObject parentObj = ((DomeTreeObject) parentNode.getTreeObject()).getDomeObject();
                if (relationBuilder.isInputFilter(parentObj)) { // selection is insertion point
                    int childIndex = parentNode.getIndex(selectedNode);
                    relationBuilder.newModelObjects(items, childIndex);
                } else {
                    relationBuilder.newModelObjects(items);
                }
            }
        }
    }
 */
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
		items = filterForValidItems(relationBuilder, items);
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
				((DomeModel) relationBuilder.getModel()).getMappingManager().addMappings((Parameter) dObj, items);
			} catch (RuntimeException e) {
				handleMappingErrors(e);
			}
		} // else can not map
	}

	protected void handleMappingErrors(RuntimeException ex)
	{
		System.err.println(ex);
	}
//no need to move up, leave it here for interface need

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

	//no need to move down, leave it here for interface need

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


	protected SelectionInfo getSelectionObjectsByParent()
	{
		if (tree.isSelectionEmpty()) return null;
		int[] selectedRows = tree.getSelectionRows();
		HashMap selections = sortSelectedObjectsByParent(selectedRows);
		int[] parentIndices = new int[selections.size()];
		Iterator it = selections.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			parentIndices[index++] = ((Integer) it.next()).intValue();
		}
		Arrays.sort(parentIndices);
		return new SelectionInfo(selections, parentIndices);
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

	protected void setEditMenusForSelection()
	{
		if (!validateTreeSelection()) return; // update at next tree selection event
		// add/insert only for single/no selection
		// delete/remove for one or more selection
		// copy to clipboard for one or more selection
		// paste only for single/no selection
		// all within non-filtered context -- move up/down
		int selectionCount = tree.getSelectionCount();
		if (selectionCount > 1) {
			//EqualRelationBuildMenus.menus.disableAddMenus();
		} else if (selectionCount == 1) {
			//EqualRelationBuildMenus.menus.enableAddMenus();
			if (isRelationParameterSelected()) {
				EqualRelationBuildMenus.menus.enableMapMIs();
			} else {
				EqualRelationBuildMenus.menus.disableMapMIs();
			}
		} else { // no selection, insert into relation
			//EqualRelationBuildMenus.menus.enableAddMenus();
			EqualRelationBuildMenus.menus.disableMapMIs();
		}

		if (selectionCount == 0) {
			// EqualRelationBuildMenus.menus.disableRemoveMenus();
		} else {
			// EqualRelationBuildMenus.menus.enableRemoveMenus();
		}

		if (selectionCount == 0) {
			setMoveEnabled(false);
		} else if (selectionCount == 1) {
			setMoveEnabled(canMoveSingleSelection());
		} else {
			setMoveEnabled(canMoveMultipleSelections());
		}
	}

	protected boolean isRelationParameterSelected()
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

	class EqualRelationBuilderTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			setEditMenusForSelection();
		}
	}

/*
	class RelationListener implements DListListener
	{
		public void intervalChanged(DListEvent e){}

		public void intervalAdded(DListEvent e) {
			manipulateUpDownButtons();
		}

		public void intervalRemoved(DListEvent e) {
			manipulateUpDownButtons();
		}

		public void itemsRemoved(DListEvent e) {
			manipulateUpDownButtons();
		}

		public void itemsReplaced(DListEvent e){}

		protected void manipulateUpDownButtons()
		{
			if (relationBuilder.getModelObjects().size() < 2) {
				upButton.setEnabled(false);
				downButton.setEnabled(false);
			}
			else {
				upButton.setEnabled(true);
				downButton.setEnabled(true);
			}
		}
	}
*/

	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final EqualRelationTreeBuilderPanel getEqualRelationTreeBuilderPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof EqualRelationTreeBuilderPanel) {
					return (EqualRelationTreeBuilderPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof EqualRelationTreeBuilderPanel)
				return (EqualRelationTreeBuilderPanel) comp;
			/**/System.err.println("No current EqualRelationTreeBuilderPanel");
			throw new NullPointerException("No current EqualRelationTreeBuilderPanel");
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
			getEqualRelationTreeBuilderPanel(e).addNewModelObject((String) getValue(AbstractAction.NAME));
		}
	}

	// --- actions for menus and buttons --------------------
	public static final AbstractAction copyAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			getEqualRelationTreeBuilderPanel(e).copySelectedModelObjects();
		}
	};

	//paste copy is not allowed
	/*
    public static final AbstractAction pasteCopyLastSelectionAction = new FocusTrackerAction("Last selection") {
        public void actionPerformed(ActionEvent e) {
            getEqualRelationTreeBuilderPanel(e).pasteCopyLastSelection();
        }
    };

    public static final AbstractAction pasteCopyClipboardAction = new FocusTrackerAction("Clipboard...") {
        public void actionPerformed(ActionEvent e) {
            getEqualRelationTreeBuilderPanel(e).pasteCopyFromClipboard();
        }
    };
*/
	public static final AbstractAction mapLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getEqualRelationTreeBuilderPanel(e).mapLastSelection();
		}
	};

	public static final AbstractAction mapClipboardAction = new FocusTrackerAction("Clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getEqualRelationTreeBuilderPanel(e).mapFromClipboard();
		}
	};

	//Add and Map is not allowed
	/*
    public static final AbstractAction addAndMapLastSelectionAction = new FocusTrackerAction("Last selection") {
        public void actionPerformed(ActionEvent e) {
            System.err.println("addAndMapStart");
            getEqualRelationTreeBuilderPanel(e).addAndMapLastSelection();
            System.err.println("addAndMapEnd");
        }
    };

 */
	//Add and Map is not allowed
	/*
    public static final AbstractAction addAndMapClipboardAction = new FocusTrackerAction("Clipboard...") {
        public void actionPerformed(ActionEvent e) {
            getEqualRelationTreeBuilderPanel(e).addAndMapFromClipboard();
        }
    };
*/
	//delete is not allowed
	/*
    public static final AbstractAction deleteAction = new FocusTrackerAction("Delete") {
        public void actionPerformed(ActionEvent e) {
            getEqualRelationTreeBuilderPanel(e).deleteSelectedModelObjects();
        }
    };*/

	public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getEqualRelationTreeBuilderPanel(e).tree.clearSelection();
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{
			getEqualRelationTreeBuilderPanel(e).tree.selectAllVisibleRows();
		}
	};

	public static final AbstractAction TestAction = new FocusTrackerAction("Test")
	{
		public void actionPerformed(ActionEvent e)
		{
			EqualRelationTreeBuilderPanel p = getEqualRelationTreeBuilderPanel(e);
			if (p == null) {
				System.err.println("Test cancelled: unable to find a current (focused) EqualRelation");
				return;
			}
			try {
				p.relationBuilder.execute();
			}
			catch (RelationExecutionException ex) {
				System.err.println(ex.getMessage());
				OneButton1Msg.showRelationExecutionError(p, ex);
			}
		}
	};

}
