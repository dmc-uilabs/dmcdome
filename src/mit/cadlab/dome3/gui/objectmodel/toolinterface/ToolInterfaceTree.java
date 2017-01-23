package mit.cadlab.dome3.gui.objectmodel.toolinterface;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;

import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 29, 2003
 * Time: 9:15:39 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceTree extends DomeTree
{
    protected TreePath indepFilterPath, intermediateFilterPath, resultFilterPath;
	protected DomeObjectTreeNode ifaceNode;

	public ToolInterfaceTree(DomeObjectTreeNode ifaceNode, String view, boolean isEditable)
	{
		super(ifaceNode, isEditable);
		this.ifaceNode = ifaceNode;
		expandAllVisibleRows();
	}

	public TreePath getIndependentFilterPath()
	{
		return indepFilterPath;
	}

	public TreePath getIntermediateFilterPath()
	{
		return intermediateFilterPath;
	}

	public TreePath getResultFilterPath()
	{
		return resultFilterPath;
	}

    public void setPaths(String path)
    {

    }
}
