package toolinterfaceworkspace.gui.objectmodel.toolinterface.build;

import toolinterfaceworkspace.gui.objectmodel.toolinterface.ToolInterfaceTree;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 29, 2003
 * Time: 9:14:55 AM
 * To change this template use Options | File Templates.
 */
public class BuildToolInterfaceTree extends ToolInterfaceTree
{
    public BuildToolInterfaceTree(ToolInterface iface, String view)
	{
		super(new BuildObjectTreeNode(iface, view), view, true);
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		BuildObjectTreeNode node = (BuildObjectTreeNode) path.getLastPathComponent();
		DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
		return !((dObj instanceof Filter));
	}
}
