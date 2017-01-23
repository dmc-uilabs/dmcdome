// BuildObjectTreeNode.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 2:32:15 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.guiutils.tree.run;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.tree.TreeObject;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public class RunObjectTreeNode extends DomeObjectTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time

	public RunObjectTreeNode(DomeObject dObj)
	{
		super(dObj);
	}

	public RunObjectTreeNode(DomeObject dObj, String view)
	{
		super(dObj, view);
	}

	protected TreeObject createTreeObject(Object obj)
	{
		return RunTreeObjectFactory.getTreeObject(obj);
	}
}
