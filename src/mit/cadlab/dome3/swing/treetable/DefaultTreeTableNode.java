// DefaultTreeTableNode.java
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.AbstractObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreeNode;

public class DefaultTreeTableNode extends AbstractObjectTreeNode
        implements TreeTableNode
{

	protected TableObject tableObject;
	protected AbstractTableModel tableModel;

	public DefaultTreeTableNode(Object data, boolean allowsChildren, TableObject tObj)
	{
		super(data, allowsChildren);
		tableObject = tObj;
	}

	public TableObject getTableObject()
	{
		return tableObject;
	}

	public void setTableModel(AbstractTableModel model)
	{
		if (this.tableModel == null) {
			this.tableModel = model;
		} else {
			throw new RuntimeException("TreeTableRootObject.setTableModel: " +
			                           "model already set!");
		}
	}

	public AbstractTableModel getTableModel()
	{
		if (isRoot()) {
			return tableModel;
		} else {
			TreeNode root = getRoot();
			if (root instanceof TreeTableNode) {
				return ((TreeTableNode) root).getTableModel();
			} else {
				return null;
			}
		}
	}

	public void notifyNodeColumnChanged(int column)
	{
	}

	public TreeObject getTreeObject()
	{
		return null;
	}
}
