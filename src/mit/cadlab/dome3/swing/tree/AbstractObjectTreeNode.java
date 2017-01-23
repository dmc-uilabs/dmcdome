// AbstractObjectTreeNode.java
package mit.cadlab.dome3.swing.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public abstract class AbstractObjectTreeNode extends DefaultMutableTreeNode
        implements ObjectTreeNode
{

	protected DefaultTreeModel model = null;

	public AbstractObjectTreeNode()
	{
	}

	public AbstractObjectTreeNode(Object data)
	{
		super(data);
	}

	public AbstractObjectTreeNode(Object data, boolean allowsChildren)
	{
		super(data, allowsChildren);
	}

	// ObjectTreeNode interface
	public DefaultTreeModel getTreeModel()
	{
		if (isRoot()) {
			return model;
		} else {
			TreeNode root = getRoot();
			if (root instanceof ObjectTreeNode)
				return ((ObjectTreeNode) root).getTreeModel();
			else
				return null;
		}
	}

	public void setTreeModel(DefaultTreeModel model)
	{
		this.model = model;
	}

	// overriding DefaultMutableTreeNode behavior
	// supports TreeModel setting different nodes to be root node
	// by setting TreeModel property of ObjectTreeNode
	public boolean isRoot()
	{
		return (model != null) || (super.isRoot());
	}

	public TreeNode getRoot()
	{
		AbstractObjectTreeNode ancestor = this;
		AbstractObjectTreeNode previous;
		do {
			previous = ancestor;
			ancestor = (AbstractObjectTreeNode) ancestor.getParent();
		} while ((!previous.isRoot()) && (ancestor != null));
		return previous;
	}

	// convenience methods for notifying TreeModel of node changes
	public void notifyNodeValueChanged()
	{
		DefaultTreeModel treeModel = getTreeModel();
		if (treeModel != null)
			treeModel.nodeChanged(this);
	}

	public void notifyNodeStructureChanged()
	{
		DefaultTreeModel treeModel = getTreeModel();
		if (treeModel != null)
			treeModel.nodeStructureChanged(this);
	}

	public void notifyChildrenChanged(int[] indices)
	{
		DefaultTreeModel treeModel = getTreeModel();
		if (treeModel != null)
			treeModel.nodesChanged(this, indices);
	}

	public void notifyChildrenAdded(int[] indices)
	{
		DefaultTreeModel treeModel = getTreeModel();
		if (treeModel != null)
			treeModel.nodesWereInserted(this, indices);
	}

	public void notifyChildrenRemoved(int[] indices, Object[] removedNodes)
	{

		DefaultTreeModel treeModel = getTreeModel();
		if (treeModel != null)
			treeModel.nodesWereRemoved(this, indices, removedNodes);
	}

}
