// FileSystemTreeTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.swing.treetable.TableObjectFactoryObjectTreeTableModel;

import javax.swing.JTree;
import javax.swing.ListSelectionModel;

/**
 *
 */
public class FileSystemTreeTable extends ObjectTreeTable
{

	private TableObjectFactoryObjectTreeTableModel tofottm;

	public FileSystemTreeTable(JTree tree, String[] colNames, TableObjectFactory tableObjFactory,
	                           int[] initialColumnWidths)
	{
		this(tree, colNames, tableObjFactory);
		setInitialColumnWidths(initialColumnWidths);
	}

	public FileSystemTreeTable(JTree tree, String[] colNames, TableObjectFactory tableObjFactory)
	{
		//tofottm = new TableObjectFactoryObjectTreeTableModel(tree, colNames.length, colNames, tableObjFactory);
		super(new TableObjectFactoryObjectTreeTableModel(tree, colNames.length, colNames, tableObjFactory));
		DomeTable.customizeTable(this);
		setDefaultRenderer(Object.class, new Renderers.NothingRenderer());

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}


}
