// BuildObjectTreeNode.java
package mit.cadlab.dome3.gui.guiutils.tree.build;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectListTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.objectmodel.DomeObject;

import java.util.List;
import java.lang.reflect.Constructor;
import javax.swing.tree.MutableTreeNode;

/**
 * Subclass each this node for each type of tree.
 * TreeObject is looked up from treefactory.
 */
public class BuildListObjectTreeNode extends DomeObjectListTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time
	public BuildListObjectTreeNode(List domeObjectList)
	{
		super(domeObjectList);
	}

	// use factory for specific tree
	protected TreeObject createTreeObject(Object obj)
	{
		return BuildTreeObjectFactory.getTreeObject(obj);
	}

	// use reflection to determine correct constructor to use
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		try {
			Class[] clsArray = new Class[]{DomeObject.class};
			Constructor con = BuildObjectTreeNode.class.getConstructor(clsArray);
			Object[] objArray = new Object[]{obj};
			Object bNode = con.newInstance(objArray);
			MutableTreeNode node = (MutableTreeNode) bNode;
			return node;
//            return (MutableTreeNode) BuildObjectTreeNode.class.getConstructor(new Class[]{DomeObject.class}).newInstance(new Object[]{obj});
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("DomeObjectListTreeNode invalid DomeObject: " + obj);
		}
	}

}
