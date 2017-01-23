// BuildTree.java
package mit.cadlab.dome3.gui.guiutils.tree.build;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.DomeModelTree;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import java.util.List;
import javax.swing.tree.TreePath;

public class BuildTree extends DomeModelTree
{

	public BuildTree(DomeObject dObj)
	{
		this(dObj, true, null);
	}

	public BuildTree(DomeObject dObj, String view)
	{
		this(dObj, true, view);
	}

	protected BuildTree(DomeObject dObj, boolean isEditable, String view)
	{
		super(new BuildObjectTreeNode(dObj, view), isEditable);
		expandAllVisibleRows();
	}

	public BuildTree(List domeObjsList)
	{
		super(new BuildListObjectTreeNode(domeObjsList), true);
		expandAllVisibleRows();
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		BuildObjectTreeNode node = (BuildObjectTreeNode) path.getLastPathComponent();
		return !(((DomeTreeObject) node.getTreeObject()).getDomeObject() instanceof Filter);
	}

}
