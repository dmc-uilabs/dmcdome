// DomeObjectTreeNode.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;

import javax.swing.tree.MutableTreeNode;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public abstract class DomeObjectTreeNode extends DefaultObjectTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time

	public DomeObjectTreeNode(DomeObject dObj)
	{
		this(dObj, null);
	}

	public DomeObjectTreeNode(DomeObject dObj, String view)
	{
		if (dObj == null)
			throw new IllegalArgumentException("DomeObjectTreeNode - null DomeObject");
		TreeObject tObj = createTreeObject(dObj);
		if (tObj == null)
			throw new NullPointerException("TreeObject creation failed for " + dObj.getNameIdString());
		setUserObject(tObj);
		listener = new DefaultTreeObjectListener();
		getTreeObject().addTreeObjectListener(listener);
		loadChildren(view);
	}

	public DomeObject getDomeObject()
	{
		return ((DomeTreeObject) getTreeObject()).getDomeObject();
	}

	// use factory for specific tree
	protected abstract TreeObject createTreeObject(Object obj);

	// use reflection to determine correct constructor to use
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		try {
			return (MutableTreeNode) getClass().
			        getConstructor(new Class[]{DomeObject.class}).
			        newInstance(new Object[]{obj});
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("DomeObjectTreeNode invalid DomeObject: " + obj);
		}
	}

}
