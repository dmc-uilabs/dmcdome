// BrowseModelFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.fileSystem.browse.AbstractBrowseFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import javax.swing.tree.TreeSelectionModel;

public class BrowseModelFileSystemTable extends AbstractBrowseFileSystemTable
{
	protected static String[] columnNames = new String[]{"name", "value or version", "description"};

	private ServerConnection conn;

	public BrowseModelFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
		this.conn = conn;
	}

	public BrowseModelFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		super(conn, scope, selectionModel);
		this.conn = conn;
	}

	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("DeployFileSystemTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.FileSystemObject", "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject");
		factory.registerTableObjectKeyLink("mit.cadlab.dome3.gui.fileSystem.Folder", "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.DomeFile", "mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelDomeFileTableObject");
		//for filters
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter", "mit.cadlab.dome3.gui.deploy.components.DeployFilterTableObject"
		                                , "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.FunctionFilter", "mit.cadlab.dome3.gui.deploy.components.DeployFilterTableObject",
		                                "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");
		//for DefaultContextBuilder
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                                "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$DefaultTableObject",
		                                "mit.cadlab.dome3.objectmodel.DomeObject");
		//for parameters
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime", "mit.cadlab.dome3.gui.fileSystem.browse.BrowseParameterTableObject");
		return factory;
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
			f = BrowseModelFolder.createUserRootFolder(svrConn);
		} else if (scope.equals(Folder.USERS_ROOT)) {
			f = BrowseModelFolder.createUsersRootFolder(svrConn);
		} else if (scope.equals(Folder.GROUPS_ROOT)) {
			f = BrowseModelFolder.createGroupsRootFolder(svrConn);
		} else if (scope.equals(Folder.SERVER_ROOT)) {
			f = BrowseModelFolder.createServerRootFolder(svrConn);
		} else {
			throw new IllegalArgumentException("invalid scope: " + scope);
		}
		f.listChildren(svrConn);
		return f;
	}

	//to be override by subclasses
	public void checkViewMenu(String view)
	{
		//check view menu
		if (view.equalsIgnoreCase("Interface"))
			RunMenus.checkViewMenu(RunMode.INTERFACECAUSALITYVIEW, "model");
		else if (view.equalsIgnoreCase("System"))
			RunMenus.checkViewMenu(RunMode.SYSTEMCAUSALITYVIEW, "model");
		else if (view.equalsIgnoreCase("Build"))
			RunMenus.checkViewMenu(RunMode.BUILDVIEW, "model");
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
