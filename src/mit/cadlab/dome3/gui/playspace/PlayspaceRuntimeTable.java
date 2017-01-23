// FileTreeTableTest.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace;

import mit.cadlab.dome3.gui.playspace.treeobject.PlayspaceInterfaceTreeObject;
import mit.cadlab.dome3.gui.playspace.treeobject.PlayspaceParameterTreeObject;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.swing.tree.AbstractFilterTreeSelectionModel;
import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.swing.treetable.TableDataTreeTableModel;
import mit.cadlab.dome3.swing.treetable.TreeTableData;
import mit.cadlab.dome3.swing.treetable.TreeTableDataTreeTableNode;

import javax.swing.*;
import javax.swing.tree.TreePath;

/**
 * Example for how to use the treetable classes.
 * This example creates a treetable using classes which are instances of TreeTableData.
 * TreeTableData can be used in TreeTableDataTreeTableNodes to create nodes which are
 * suitable for both the tree and the table in the TreeTable.
 */
public class PlayspaceRuntimeTable
{
	public static TreeTableData createData()
	{
		/*
		PlayspaceTreeObject root = PlayspaceTreeObject.createPlayspace("Playspace 1");

		PlayspaceModelTreeObject m1 = PlayspaceModelTreeObject.createModel("Model 1");
		root.addContent (m1);

		PlayspaceModelTreeObject m2 = PlayspaceModelTreeObject.createModel("Model 2");
		PlayspaceInterfaceTreeObject i1 = PlayspaceInterfaceTreeObject.createInterface("Interface 1");
		PlayspaceParameterTreeObject p1 = PlayspaceParameterTreeObject.createParameter("real number");
		root.addContent (m2);
		m2.addContent(i1);
		i1.addContent(p1);

		return root;
		*/
		return null;
	}

	public ObjectTreeTable createPlayspaceTable()
	{
		int nCols = 2;
		String[] colNames = new String[]{"names", "size"};

		ObjectTreeTable ott;
		TreeTableData root = createData();
		DomeTree tree = new DomeTree(new TreeTableDataTreeTableNode(root), false);
		tree.setSelectionModel(new FoldersFilterTreeSelectionModel());
		ott = new ObjectTreeTable(new TableDataTreeTableModel(tree, nCols, colNames));
		DomeTable.customizeTable(ott);

		return ott;
	}

	/**
	 * If any arguments are passed in, use Dome style formatting.
	 * @param args
	 */
	public static void main(String[] args)
	{
		boolean domeStyle = args.length > 0;

		int nCols = 2;
		String[] colNames = new String[]{"names", "size"};
		TreeTableData rootFolder = createData();
		ObjectTreeTable ott;
		if (domeStyle) { // DomeTree formats tree; DomeTable.customizeTable formats table
			DomeTree tree = new DomeTree(new TreeTableDataTreeTableNode(rootFolder), false);
			tree.setSelectionModel(new FoldersFilterTreeSelectionModel());
			ott = new ObjectTreeTable(new TableDataTreeTableModel(tree, nCols, colNames));
			DomeTable.customizeTable(ott);
		} else { // basic default ObjectTreeTable
			ott = new ObjectTreeTable(new TableDataTreeTableModel(new TreeTableDataTreeTableNode(rootFolder), nCols, colNames));
		}
		JFrame f = new JFrame("TreeTable Test");
		f.getContentPane().add(new JScrollPane(ott));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.show();
	}

	public static class FoldersFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			TreeTableData fObj = ((TreeTableDataTreeTableNode) path.getLastPathComponent()).getTreeTableData();
			if (fObj instanceof PlayspaceInterfaceTreeObject ||
			        fObj instanceof PlayspaceParameterTreeObject) {
				return true;
			}
			return false;
		}
	}

}
