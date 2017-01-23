// RunTree.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 11, 2003
 * Time: 2:50:33 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.guiutils.tree.run;

import mit.cadlab.dome3.gui.guiutils.tree.DomeModelTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import javax.swing.tree.TreePath;
import java.util.List;

public class RunTree extends DomeModelTree
{

	public RunTree(DomeObject dObj)
	{
		this(dObj, true, null);
	}

	public RunTree(DomeObject dObj, String view)
	{
		this(dObj, true, view);
	}

	protected RunTree(DomeObject dObj, boolean isEditable, String view)
	{
		super(new RunObjectTreeNode(dObj, view), isEditable);
		expandAllVisibleRows();
	}

	public RunTree(List domeObjsList)
	{
		super(new RunListObjectTreeNode(domeObjsList), true);
		expandAllVisibleRows();
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		RunObjectTreeNode node = (RunObjectTreeNode) path.getLastPathComponent();
		return !(((DomeTreeObject) node.getTreeObject()).getDomeObject() instanceof Filter);
	}
}
