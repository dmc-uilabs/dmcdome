// ObjectTreeModel.java
package mit.cadlab.dome3.swing.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ObjectTreeModel extends DefaultTreeModel {
    // A tree model for a tree made up of ObjectTreeNodes.

    public ObjectTreeModel(ObjectTreeNode root) {
        super(root, true); // ask allowsChildren; invokes TreeObject.allowsChildren
        root.setTreeModel(this);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        if (newValue == null || newValue.toString().equals(""))
            return; // cancellation
        Object obj = path.getLastPathComponent();
        if (obj instanceof ObjectTreeNode) {
            ((ObjectTreeNode) obj).getTreeObject().setTreeValue(newValue == null ? null : newValue.toString());
            nodeChanged((ObjectTreeNode) obj);
        } else {
            throw new RuntimeException("[valueForPathChanged] Illegal node - not ObjectTreeNode: " + obj);
        }
    }

    public void setRoot(TreeNode root) {
        if (root == null)
            throw new IllegalArgumentException("Root of tree is not allowed to be null");
        if (!(root instanceof ObjectTreeNode)) {
            throw new IllegalArgumentException("Root of ObjectTreeModel must be instance of ObjectTreeNode");
        }
        ((ObjectTreeNode) this.root).setTreeModel(null);
        this.root = root;
        ((ObjectTreeNode) this.root).setTreeModel(this);
        nodeStructureChanged(root);
    }

    /**
     * Returns the path of a Node
     */
    public TreePath getPath(TreeNode nd) {
        return new TreePath(super.getPathToRoot(nd));
    }

}
