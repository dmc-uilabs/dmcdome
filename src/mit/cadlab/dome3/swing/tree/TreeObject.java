// TreeObject.java
package mit.cadlab.dome3.swing.tree;

import javax.swing.Icon;

/**
 * Class provides additional information about
 * a particular object instance which is necessary
 * for displaying the data in an ObjectTree
 */
public interface TreeObject
{
	// Suggested use note: register TreeObject as property
	// change listener for data object.
	// When data changed, notify nodes of change using one
	// of the notification methods.

	// to do: isEditable on a per node basis
	// should be in node or object?

	// standard types of tree icons
	public final int LEAF_ICON = 0;
	public final int OPEN_ICON = 1;
	public final int CLOSED_ICON = 2;

	// Tree support for ObjectTreeNode
	public boolean allowsChildren();

	public String getTreeValue();

	public void setTreeValue(String value);

	// TreeObject change notification support
	public void addTreeObjectListener(TreeObjectListener l);

	public void removeTreeObjectListener(TreeObjectListener l);

	// Support for custom icons for each object
	public Icon getIcon(int type);

	//public boolean isEditable();

}
