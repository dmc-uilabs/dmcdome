// RunObjectTreeNode.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 11, 2003
 * Time: 2:56:19 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.guiutils.tree.run;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectListTreeNode;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.tree.TreeObject;

import javax.swing.tree.MutableTreeNode;
import java.lang.reflect.Constructor;
import java.util.List;

public class RunListObjectTreeNode extends DomeObjectListTreeNode
{

	public RunListObjectTreeNode(List domeObjectList)
	{
		super(domeObjectList);
	}

	// use factory for specific tree
	protected TreeObject createTreeObject(Object obj)
	{
		return RunTreeObjectFactory.getTreeObject(obj);
	}

	// use reflection to determine correct constructor to use
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		try {
			Class[] clsArray = new Class[]{DomeObject.class};
			Constructor con = RunObjectTreeNode.class.getConstructor(clsArray);
			Object[] objArray = new Object[]{obj};
			Object bNode = con.newInstance(objArray);
			MutableTreeNode node = (MutableTreeNode) bNode;
			return node;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("DomeObjectListTreeNode invalid DomeObject: " + obj);
		}
	}
}
