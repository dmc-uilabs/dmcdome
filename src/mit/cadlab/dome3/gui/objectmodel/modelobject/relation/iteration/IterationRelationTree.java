// ProceduralRelationTree.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;

import javax.swing.tree.TreePath;

public abstract class IterationRelationTree extends DomeTree
{

	protected TreePath inputFilterPath, outputFilterPath;

	public IterationRelationTree(DomeObjectTreeNode relationNode, boolean isEditable)
	{
		super(relationNode, isEditable);
		inputFilterPath = new TreePath(((DomeObjectTreeNode) relationNode.getChildAt(0)).getPath());
		outputFilterPath = new TreePath(((DomeObjectTreeNode) relationNode.getChildAt(1)).getPath());
		expandAllVisibleRows();
	}

	public TreePath getInputFilterPath()
	{
		return inputFilterPath;
	}

	public TreePath getOutputFilterPath()
	{
		return outputFilterPath;
	}

}
