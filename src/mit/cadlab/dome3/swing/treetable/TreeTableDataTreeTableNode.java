// TreeTableDataTreeTableNode.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.TableData;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.treetable.TreeTableData;

import javax.swing.tree.MutableTreeNode;

/**
 * TreeTableNode in which each object is an instance of TreeTableData
 */
public class TreeTableDataTreeTableNode extends DefaultObjectTreeNode implements TableData
{

	protected TreeTableData ttObj;

	public TreeTableDataTreeTableNode(TreeTableData data)
	{
		super(data);
		ttObj = data;
	}

	public TreeTableData getTreeTableData()
	{
		return ttObj;
	}

	protected MutableTreeNode makeTreeNode(Object obj)
	{
		// can add code here to make different kinds of tree nodes for different kinds of objects
		if (obj instanceof TreeTableData)
			return new TreeTableDataTreeTableNode((TreeTableData) obj);
		else
			throw new IllegalArgumentException("TreeTableDataTreeTableNode invalid TreeTableData: " + obj);
	}

	public TableObject getTableObject()
	{
		return ttObj.getTableObject();
	}

}
