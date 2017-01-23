// AbstractDeployFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.gui.fileSystem.AbstractFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import javax.swing.tree.TreeSelectionModel;

/**
 * Base table for browsing the DOME filesystem in deploy mode.
 */
public abstract class AbstractDeployFileSystemTable extends AbstractFileSystemTable
{

	protected static String[] columnNames = new String[]{"Name", "Version", "Description", "Modified"};
	protected static int[] columnWidths = new int[]{200, 200, 250, 200};

	public AbstractDeployFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
		table.getColumnModel().getColumn(1).setMaxWidth(60);
		table.getColumnModel().getColumn(1).setResizable(false);

		//table.getColumnModel().getColumn(1).setResizable(false);
	}

	public AbstractDeployFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		super(conn, scope, selectionModel);
	}

	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("DeployFileSystemTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.FileSystemObject", "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject");
		factory.registerTableObjectKeyLink("mit.cadlab.dome3.gui.fileSystem.Folder", "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.DomeFile", "mit.cadlab.dome3.gui.fileSystem.deploy.DeployDomeFileTableObject");

		return factory;
	}

	/**
	 * Note: column names array must be either static in the subclass or
	 * created in this method since this method is called in the constructor method.
	 * @return columnNames for this table; can not be null
	 */
	protected String[] getColumnNames()
	{
		return columnNames;
	}

	protected int[] getColumnWidths()
	{
		return columnWidths;
	}

	/**
	 * This method creates the root node for the tree table.
	 * @param scope
	 */
	protected ObjectTreeNode createRootNode(String scope)
	{
		return new DeployFileSystemTreeNode(createRootFolder(scope));
	}

	/**
	 * This method creates the root folder for the tree table.
	 * @param scope the scope for the tree table
	 * @return the root folder based on the scope specified
	 */
	protected abstract Folder createRootFolder(String scope);

	/*public void refresh()
	{
		Object obj = getSelectedObject();
		if (obj != null) {
			try {
				((FileSystemObject) obj).refresh(svrConn);
			}
			catch (Exception e) {
			}
		}
	}*/

	public void refresh()
	{
		tree.setModel(new ObjectTreeModel(createRootNode(scope)));
		table.repaint();
	}

}
