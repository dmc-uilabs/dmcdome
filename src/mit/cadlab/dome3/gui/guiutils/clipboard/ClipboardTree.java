// ClipboardTree.java
package mit.cadlab.dome3.gui.guiutils.clipboard;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;

import java.util.Vector;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class ClipboardTree extends DomeTree
{

	protected Clipboard clipboard;

	public ClipboardTree(Clipboard clipboard, boolean isEditable)
	{
		super(new ClipboardTreeNode(clipboard), isEditable);
		this.clipboard = clipboard;
		setSelectionModel(new ClipboardTreeSelectionModel());
		expandAllVisibleRows();
	}

	protected boolean areNewNodesExpanded()
	{
		return true;
	}

	public boolean isPathEditable(TreePath path)
	{ // only allow selection name editing
		if (!isEditable()) return false;
		DefaultObjectTreeNode node = (DefaultObjectTreeNode) path.getLastPathComponent();
		return (node.getTreeObject() instanceof ClipboardSelectionTreeObject);
	}

	public ClipboardSelection[] getSelectedClipboardSelections()
	{
		if (isSelectionEmpty()) return null;
		TreePath[] selectedPaths = getSelectionPaths();
		ClipboardSelection[] selections = new ClipboardSelection[selectedPaths.length];
		for (int i = 0; i < selections.length; ++i) {
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
			selections[i] = ((ClipboardSelectionTreeObject) node.getTreeObject()).getClipboardSelection();
		}
		return selections;
	}

	public void deleteSelectedClipboardSelections()
	{
		ClipboardSelection[] selections = getSelectedClipboardSelections();
		if (selections == null) return;
		clipboard.removeSelections(selections);
	}

	public void deleteAllClipboardSelections()
	{
		clipboard.empty();
	}

}

class ClipboardTreeSelectionModel extends DefaultTreeSelectionModel
{

	public ClipboardTreeSelectionModel()
	{
	}

	public void setSelectionPaths(TreePath[] paths)
	{
		Vector validPaths = new Vector();
		for (int i = 0; i < paths.length; ++i) {
			if (isValidSelectionPath(paths[i]))
				validPaths.add(paths[i]);
		}
		super.setSelectionPaths((TreePath[]) validPaths.toArray(new TreePath[validPaths.size()]));
	}

	public void addSelectionPaths(TreePath[] paths)
	{
		Vector validPaths = new Vector();
		for (int i = 0; i < paths.length; ++i) {
			if (isValidSelectionPath(paths[i]))
				validPaths.add(paths[i]);
		}
		super.addSelectionPaths((TreePath[]) validPaths.toArray(new TreePath[validPaths.size()]));
	}

	protected boolean isValidSelectionPath(TreePath path)
	{
		return ((DefaultObjectTreeNode) path.getLastPathComponent()).getTreeObject() instanceof ClipboardSelectionTreeObject;
	}

}
