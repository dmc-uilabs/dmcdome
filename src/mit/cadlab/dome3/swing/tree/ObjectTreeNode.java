// ObjectTreeNode.java
package mit.cadlab.dome3.swing.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public interface ObjectTreeNode extends TreeNode
{

	public DefaultTreeModel getTreeModel();

	public void setTreeModel(DefaultTreeModel model);

	public TreeObject getTreeObject(); // can not return null!

}
