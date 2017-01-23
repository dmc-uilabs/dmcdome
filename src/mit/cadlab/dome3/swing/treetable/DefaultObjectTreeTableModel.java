// DefaultObjectTreeTableModel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.ObjectTree;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;

import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

/**
 * This default implementation of the TreeTableModel assumes
 * that the tree nodes are all instances of TreeTableNode.
 */
public class DefaultObjectTreeTableModel extends AbstractObjectTreeTableModel
{

	public DefaultObjectTreeTableModel(JTree tree, int numberColumns,
	                                   Object[] columnNames)
	{
		super(tree, numberColumns);
		tree.setEditable(false);
		setColumnNames(columnNames);
	}

	public DefaultObjectTreeTableModel(ObjectTreeNode root, int numberColumns,
	                                   Object[] columnNames)
	{
		this(new ObjectTree(new ObjectTreeModel(root)),
		     numberColumns, columnNames);
	}

	protected TableObject getTableObjectAt(int row)
	{
		TreePath treePath = tree.getPathForRow(row);
		return ((TreeTableNode) treePath.getLastPathComponent()).getTableObject();
	}

	// TableModel interface
	public boolean isCellEditable(int row, int column)
	{
		return getTableObjectAt(row).isEditableAt(column);
	}

	public Object getValueAt(int row, int column)
	{
		return getTableObjectAt(row).getValueAt(column);
	}

	public void setValueAt(Object aValue, int row, int column)
	{
		getTableObjectAt(row).setValueAt(aValue, column);
	}

	public Class getColumnClass(int row, int column)
	{
		return getTableObjectAt(row).getClassAt(column);
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{
		return getTableObjectAt(row).getRendererAt(column);
	}

	public TableCellEditor getCellEditor(int row, int column)
	{
		return getTableObjectAt(row).getEditorAt(column);
	}

}
