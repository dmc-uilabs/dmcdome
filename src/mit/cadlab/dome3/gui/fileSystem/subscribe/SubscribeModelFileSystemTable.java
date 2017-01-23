// BrowseModelFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.subscribe;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import javax.swing.tree.TreeSelectionModel;

public class SubscribeModelFileSystemTable extends BrowseModelFileSystemTable
{
	protected static String[] columnNames = new String[]{"name", "value or version", "description"};

	private ServerConnection conn;

	public SubscribeModelFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
		this.conn = conn;
	}

	public SubscribeModelFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		super(conn, scope, selectionModel);
		this.conn = conn;
	}


	/**
	 * This method creates the root folder for the tree table.
	 * @param scope the scope for the tree table
	 * @return the root folder based on the scope specified
	 */
	protected Folder createRootFolder(String scope)
	{
		Folder f = null;
		if (scope.equals(Folder.USER_HOME)) {
			f = SubscribeModelFolder.createUserRootFolder(svrConn);
		} else if (scope.equals(Folder.USERS_ROOT)) {
			f = SubscribeModelFolder.createUsersRootFolder(svrConn);
		} else if (scope.equals(Folder.GROUPS_ROOT)) {
			f = SubscribeModelFolder.createGroupsRootFolder(svrConn);
		} else if (scope.equals(Folder.SERVER_ROOT)) {
			f = SubscribeModelFolder.createServerRootFolder(svrConn);
		} else {
			throw new IllegalArgumentException("invalid scope: " + scope);
		}
		f.listChildren(svrConn);
		return f;
	}

	public void addFolder()
	{
	}

	public void renameFolder()
	{
		//todo need to provide implementation of this
	}

	public void deleteSelectedItem()
	{
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

}
