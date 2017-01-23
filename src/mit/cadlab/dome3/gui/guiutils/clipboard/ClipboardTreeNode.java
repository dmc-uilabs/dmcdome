// ClipboardTreeNode.java
package mit.cadlab.dome3.gui.guiutils.clipboard;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTreeObjectFactory;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;

import javax.swing.tree.MutableTreeNode;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public class ClipboardTreeNode extends DefaultObjectTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time

	public ClipboardTreeNode(Object obj)
	{
		if (obj == null)
			throw new IllegalArgumentException("ClipboardTreeNode - null Object");
		TreeObject tObj = createTreeObject(obj);
		if (tObj == null)
			throw new NullPointerException("TreeObject creation failed for " + obj);
		setUserObject(tObj);
		listener = new DefaultTreeObjectListener();
		getTreeObject().addTreeObjectListener(listener);
		loadChildren();
	}

	public DomeObject getDomeObject()
	{
		TreeObject tObj = getTreeObject();
		if (tObj instanceof DomeTreeObject)
			return ((DomeTreeObject) tObj).getDomeObject();
		else
			return null;
	}

	protected TreeObject createTreeObject(Object obj)
	{
		return BuildTreeObjectFactory.getTreeObject(obj);
	}

	// use reflection to determine correct constructor to use
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		return new ClipboardTreeNode(obj);
	}

}
