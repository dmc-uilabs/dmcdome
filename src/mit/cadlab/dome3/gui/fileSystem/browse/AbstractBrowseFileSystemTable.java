// AbstractBrowseFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.fileSystem.AbstractFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.FileSystemTreeNode;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import javax.swing.tree.TreeSelectionModel;

/**
 * Base table for browsing the DOME filesystem in browse mode.
 */
public abstract class AbstractBrowseFileSystemTable extends AbstractFileSystemTable
{

	protected static String[] columnNames = new String[]{"Names", "Status", "Modified", "Version"};
	protected static int[] columnWidths = new int[]{200, 200, 200, 250};


	public AbstractBrowseFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
	}

	public AbstractBrowseFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		super(conn, scope, selectionModel);
	}

	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("BrowseFileSystemTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.FileSystemObject", "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject");
		factory.registerTableObjectKeyLink("mit.cadlab.dome3.gui.fileSystem.Folder", "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.DomeFile", "mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFileTableObject");
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
		return new FileSystemTreeNode(createRootFolder(scope));
	}

	/**
	 * This method creates the root folder for the tree table.
	 * @param scope the scope for the tree table
	 * @return the root folder based on the scope specified
	 */
	protected abstract Folder createRootFolder(String scope);


	public boolean switchInterfaceView(String view)
	{
		if (objectWithFocus instanceof BrowseInterfaceDomeFile) {
			//refresh();
			((BrowseInterfaceDomeFile) objectWithFocus).switchView(svrConn, view);
			//check menu
			checkViewMenu(view);
			return true;
		}
		return false;
	}

	//to be override by subclasses
	public abstract void checkViewMenu(String view);

	public String getView()
	{
		return ((BrowseInterfaceDomeFile) objectWithFocus).getView();
	}

	/*public void refresh() {
        Object obj = getSelectedObject();
        if (obj != null) {
	       try{
		        ((FileSystemObject) obj).refresh(svrConn);
	        }
	        catch(Exception e){
		        e.printStackTrace();
	        }
      }
    }*/

	public void refresh()
	{
		tree.setModel(new ObjectTreeModel(createRootNode(scope)));
		table.repaint();
	}

}
