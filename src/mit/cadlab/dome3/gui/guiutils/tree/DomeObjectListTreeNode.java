// DomeObjectListTreeNode.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.util.DArrayList;

import java.util.List;
import javax.swing.tree.MutableTreeNode;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public abstract class DomeObjectListTreeNode extends DefaultObjectTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time

	public DomeObjectListTreeNode(List domeObjectList)
	{
		if (domeObjectList == null)
			throw new IllegalArgumentException("DomeObjectListTreeNode - null DomeObjectList");
		TreeObject tObj = createTreeObject(domeObjectList);
		if (tObj == null)
			throw new NullPointerException("TreeObject creation failed for " + domeObjectList.getClass().getName());
		setUserObject(tObj);
		if (domeObjectList instanceof DArrayList) {
			listener = new DefaultTreeObjectListener();
			getTreeObject().addTreeObjectListener(listener);
		}
		loadChildren();
	}

	// use factory for specific tree
	protected abstract TreeObject createTreeObject(Object obj);

}
