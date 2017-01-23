// BrowsePlayspaceFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.fileSystem.browse.AbstractBrowseFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.FileSystemObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.FileSystemFilters;

import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import java.util.Vector;
import java.util.Enumeration;

public class BrowsePlayspaceFileSystemTable extends AbstractBrowseFileSystemTable
{
	protected static String[] columnNames = new String[]{"Names", "Active Members", "Description"};
	protected static int[] columnWidths = new int[]{250, 100, 250};

	private ServerConnection conn;


	public BrowsePlayspaceFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
		this.conn = conn;
	}

	public BrowsePlayspaceFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		super(conn, scope, selectionModel);
		this.conn = conn;
	}

	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("BrowsePlayspaceFileSystemTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.FileSystemObject", "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject");
		factory.registerTableObjectKeyLink("mit.cadlab.dome3.gui.fileSystem.Folder", "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.DomeFile", "mit.cadlab.dome3.gui.fileSystem.browse.BrowsePlayspaceDomeFileTableObject");
		//for filters
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter", "mit.cadlab.dome3.gui.deploy.components.DeployFilterTableObject"
		                                , "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

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
			f = BrowsePlayspaceFolder.createUserRootFolder(svrConn);
		} else if (scope.equals(Folder.USERS_ROOT)) {
			f = BrowsePlayspaceFolder.createUsersRootFolder(svrConn);
		} else if (scope.equals(Folder.GROUPS_ROOT)) {
			f = BrowsePlayspaceFolder.createGroupsRootFolder(svrConn);
		} else if (scope.equals(Folder.SERVER_ROOT)) {
			f = BrowsePlayspaceFolder.createServerRootFolder(svrConn);
		} else {
			throw new IllegalArgumentException("invalid scope: " + scope);
		}
		f.listChildren(svrConn);
		return f;
	}

	public boolean setSelection(Object domeFileId)
	{
		String dbId = (String) domeFileId;
		Vector path = new Vector();

		try {
			path = FileSystemFunctions.getPathForPlayspace(svrConn, dbId);
			path.add(domeFileId);
			Vector vPath = new Vector();
			AbstractTreeObjectFactoryTreeNode otn;
			Enumeration children;
			Object id;
			vPath.add(rootNode);

			for (int i = 0; i < path.size(); i++) {
				children = ((ObjectTreeNode) vPath.lastElement()).children();
				FileSystemObject fObj = null;
				while (children.hasMoreElements()) {
					otn = (AbstractTreeObjectFactoryTreeNode) children.nextElement();
					fObj = (FileSystemObject) otn.getTreeNodeObject();
					id = fObj.getId();
					if (id.equals(path.get(i))) {
						vPath.add(otn);
						//you don't want to expand the last element since it can be dome_file not a folder
						if (fObj instanceof Folder) fObj.listChildren(svrConn);
						//System.out.println("add: " + fObj + " -> " + otn);
						break;
					}
				}
				if (fObj instanceof DomeFile)
					break;
			}

			TreePath tp = new TreePath(vPath.toArray());
			tree.setSelectionModel(new FileSystemFilters.FixedSelectionTreeSelectionModel(tp));
			//tree.expandPath(tp);
			tree.setSelectionPath(tp);

		} catch (Exception e) {
			e.printStackTrace();
			e.toString();
			System.out.println("nop...");
			return false;
		}
		return true;
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

	protected int[] getColumnWidths()
	{
		return columnWidths;
	}


	public void checkViewMenu(String view)
	{
		//check view menu
		if (view.equalsIgnoreCase("Interface"))
			RunMenus.checkViewMenu(RunMode.INTERFACECAUSALITYVIEW, "playspace");
		else if (view.equalsIgnoreCase("System"))
			RunMenus.checkViewMenu(RunMode.SYSTEMCAUSALITYVIEW, "playspace");
		else if (view.equalsIgnoreCase("Build"))
			RunMenus.checkViewMenu(RunMode.BUILDVIEW, "playspace");
	}

}
