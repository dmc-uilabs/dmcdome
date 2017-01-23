// FileTreeTableTest.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.treetableexample;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.swing.treetable.TableDataTreeTableModel;
import mit.cadlab.dome3.swing.treetable.TreeTableDataTreeTableNode;
import mit.cadlab.dome3.swing.tree.AbstractFilterTreeSelectionModel;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import test.treetableexample.FileObject;

/**
 * Example for how to use the treetable classes.
 * This example creates a treetable using classes which are instances of TreeTableData.
 * TreeTableData can be used in TreeTableDataTreeTableNodes to create nodes which are
 * suitable for both the tree and the table in the TreeTable.
 */
public class FileTreeTableTest
{
	public static FileObject createData() {
		FileObject root = FileObject.createFolder("root folder");
		FileObject f1 = FileObject.createFolder("models");
		FileObject f2 = FileObject.createFolder("playspaces");
		root.addContent(f1);
		root.addContent(f2);

		FileObject m1 = FileObject.createModel("My first model");
		FileObject p1 = FileObject.createParameter("real number");
		m1.addContent(p1);
		f1.addContent(m1);

		FileObject m2 = FileObject.createModel("Expert model");
		f1.addContent(m2);

		FileObject ps1 = FileObject.createPlayspace("Playground");
		FileObject psa = FileObject.createParameter("jack");
		FileObject psb = FileObject.createParameter("jill");
		ps1.addContent(psa);
		ps1.addContent(psb);
		f2.addContent(ps1);

		return root;
	}

	/**
	 * If any arguments are passed in, use Dome style formatting.
	 * @param args
	 */
	public static void main(String[] args)
	{
		boolean domeStyle = args.length>0;

		int nCols = 2;
		String[] colNames = new String[]{"names","size"};
		FileObject rootFolder = createData();
		ObjectTreeTable ott;
		if (domeStyle) { // DomeTree formats tree; DomeTable.customizeTable formats table
			DomeTree tree = new DomeTree(new TreeTableDataTreeTableNode(rootFolder),false);
			tree.setSelectionModel(new FoldersFilterTreeSelectionModel());
			ott = new ObjectTreeTable(new TableDataTreeTableModel(tree,nCols,colNames));
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

	public static class FoldersFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel {
		protected boolean isValidSelectionPath(TreePath path)
		{
			FileObject fObj = (FileObject)((TreeTableDataTreeTableNode)path.getLastPathComponent()).getTreeTableData();
            return fObj.getType().equals(FileObject.FOLDER_TYPE);
		}
	}

}
