// TableDataTreeTableModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.TableData;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * This implementation of the TreeTableModel assumes
 * that the tree nodes are all instances of TableData
 */
public class TableDataTreeTableModel extends DefaultObjectTreeTableModel
{
	public TableDataTreeTableModel(JTree tree, int numberColumns, Object[] columnNames)
	{
		super(tree, numberColumns, columnNames);
	}

	public TableDataTreeTableModel(ObjectTreeNode root, int numberColumns, Object[] columnNames)
	{
		super(root, numberColumns, columnNames);
	}

	protected TableObject getTableObjectAt(int row)
	{
		TreePath treePath = tree.getPathForRow(row);
		return ((TableData) treePath.getLastPathComponent()).getTableObject();
	}
}
