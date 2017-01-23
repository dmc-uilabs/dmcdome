//BuildModelInterfaceTree.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTree;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import javax.swing.tree.TreePath;

public class BuildModelInterfaceTree extends ModelInterfaceTree
{
	//private ModelInterfaceBuilder iface;

	public BuildModelInterfaceTree(ModelInterface iface, String view)
	{
		super(new BuildObjectTreeNode(iface, view), view, true);
		//		!(((AbstractModelInterface)iface).isDefaultInterface()));
		// this.iface = (ModelInterfaceBuilder)iface;
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		BuildObjectTreeNode node = (BuildObjectTreeNode) path.getLastPathComponent();
		DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
		return !((dObj instanceof Filter));
		//	||(dObj.equals(iface.getViewOnlyContext())));
	}
}
