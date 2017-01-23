// BuildObjectTreeNode.java
package mit.cadlab.dome3.gui.guiutils.tree.build;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.tree.TreeObject;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public class BuildObjectTreeNode extends DomeObjectTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time

	public BuildObjectTreeNode(DomeObject dObj)
	{
		super(dObj);
	}

	public BuildObjectTreeNode(DomeObject dObj, String view)
	{
		super(dObj, view);
	}

	protected TreeObject createTreeObject(Object obj)
	{
		return BuildTreeObjectFactory.getTreeObject(obj);
	}

}
