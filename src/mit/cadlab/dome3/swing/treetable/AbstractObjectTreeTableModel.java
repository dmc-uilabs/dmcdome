// AbstractObjectTreeTableModel.java
// Copyright (c) 2002 Massachusetts Insitute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.AbstractObjectTableModel;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public abstract class AbstractObjectTreeTableModel extends AbstractObjectTableModel
        implements ObjectTreeTableModel
{

	protected static Class[] treeTableClasses = {ObjectTreeTableModel.class};
	protected JTree tree;

	public AbstractObjectTreeTableModel(JTree tree, int numberColumns)
	{
		super(numberColumns);
		setColumnClasses(treeTableClasses);
		if (tree == null)
			throw new IllegalArgumentException("ObjectTreeTableModel - null tree!");
		this.tree = tree;

		tree.addTreeExpansionListener(new TreeExpansionListener()
		{
			// Don't use fireTableRowsInserted() here; the selection model
			// would get updated twice.
			public void treeExpanded(TreeExpansionEvent event)
			{
				fireTableDataChanged();
			}

			public void treeCollapsed(TreeExpansionEvent event)
			{
				fireTableDataChanged();
			}
		});

		// Install a TreeModelListener that can update the table when
		// tree changes. We use delayedFireTableDataChanged as we can
		// not be guaranteed the tree will have finished processing
		// the event before us.
		tree.getModel().addTreeModelListener(new TreeModelListener()
		{
			public void treeNodesChanged(TreeModelEvent e)
			{
				int parentRow = AbstractObjectTreeTableModel.this.tree.getRowForPath(e.getTreePath());
				int[] childIndices = e.getChildIndices();
				if (childIndices != null && childIndices.length > 0) {
					for (int i = 0; i < childIndices.length; ++i) {
						delayedFireTreeDataChanged(parentRow + childIndices[i] + 1);
					}
				}
			}

			public void treeNodesInserted(TreeModelEvent e)
			{
				delayedFireTableDataChanged();
			}

			public void treeNodesRemoved(TreeModelEvent e)
			{
				delayedFireTableDataChanged();
			}

			public void treeStructureChanged(TreeModelEvent e)
			{
				delayedFireTableDataChanged();
			}
		});
	}

	public void setColumnClasses(Class[] classes)
	{
		if (classes == null || classes.length == 0 || classes[0] != ObjectTreeTableModel.class)
			System.out.println("Warning: setColumnClass should have ObjectTreeTableModel.class as first element or table column reordering will mess up tree mouse events.");
		super.setColumnClasses(classes);
	}

	// ObjectTreeTableModel interface
	public JTree getTree()
	{
		return tree;
	}

	// TableModel interface
	public int getRowCount()
	{
		return tree.getRowCount();
	}

	/**
	 * Invokes fireTableDataChanged after all the pending events have been
	 * processed. SwingUtilities.invokeLater is used to handle this.
	 */
	protected void delayedFireTableDataChanged()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				fireTableDataChanged();
			}
		});
	}

	protected void delayedFireTreeDataChanged(int row)
	{
		SwingUtilities.invokeLater(new UpdateTreeCellThread(row));
	}

	class UpdateTreeCellThread implements Runnable
	{
		private int row;

		public UpdateTreeCellThread(int row)
		{
			this.row = row;
		}

		public void run()
		{
			fireTableCellUpdated(row, 0);
		}
	}

}

// modification of TreeTableModelAdapter.java
/*
* @(#)TreeTableModelAdapter.java	1.2 98/10/27
*
* Copyright 1997-1999 by Sun Microsystems, Inc.,
* 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
* All rights reserved.
*
* This software is the confidential and proprietary information
* of Sun Microsystems, Inc. ("Confidential Information").  You
* shall not disclose such Confidential Information and shall use
* it only in accordance with the terms of the license agreement
* you entered into with Sun.
*/
