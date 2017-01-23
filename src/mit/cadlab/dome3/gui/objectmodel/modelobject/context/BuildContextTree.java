// BuildContextTree.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;

import javax.swing.tree.TreePath;

public class BuildContextTree extends ContextTree
{

	public BuildContextTree(DefaultContextBuilder cBuilder)
	{
		super(new BuildObjectTreeNode(cBuilder), true);
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		BuildObjectTreeNode node = (BuildObjectTreeNode) path.getLastPathComponent();
		return !(((DomeTreeObject) node.getTreeObject()).getDomeObject() instanceof Filter);
	}

}
