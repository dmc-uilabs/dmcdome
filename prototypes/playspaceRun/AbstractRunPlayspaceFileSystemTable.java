// AbstractFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package playspaceRun;

import mit.cadlab.dome.gui.swing.table.DomeTable;
import mit.cadlab.dome.gui.swing.tree.DomeTree;
import mit.cadlab.dome.gui.swing.treetable.Renderers;
import mit.cadlab.dome.gui.fileSystem.FileSystemObject;
import mit.cadlab.dome.gui.fileSystem.DomeFile;
import mit.cadlab.dome.gui.fileSystem.Folder;
import mit.cadlab.dome.gui.fileSystem.FileSystemFilters;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome.network.client.ClientObjectRecord;
import mit.cadlab.dome.network.client.ClientPlayspaceRuntime;
import mit.cadlab.dome.swing.table.TableObjectFactory;
import mit.cadlab.dome.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome.swing.tree.ObjectTreeNode;
import mit.cadlab.dome.swing.tree.DefaultTreeObject;
import mit.cadlab.dome.swing.treetable.ObjectTreeTable;
import mit.cadlab.dome.swing.treetable.TableObjectFactoryObjectTreeTableModel;
import mit.cadlab.dome.swing.treetable.TreeTableData;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;

public abstract class AbstractRunPlayspaceFileSystemTable extends JScrollPane
{
	protected static int ROW_HEIGHT = 20;
	protected static GridBagConstraints gbc;

	protected TableObjectFactory tableObjFactory;
	protected ServerConnection svrConn;
	protected ObjectTreeNode rootNode;
	protected DomeTree tree;
	protected ObjectTreeTable table;

	public ClientObjectRecord objectWithFocus;
	protected DomeFile runningPlayspace;
	protected ClientPlayspaceRuntime playspace = null;


	protected AbstractRunPlayspaceFileSystemTable(ServerConnection conn, String scope)
	{
		init(conn, scope, null);
	}

	protected AbstractRunPlayspaceFileSystemTable(ServerConnection conn, String scope, DomeFile df) {
		runningPlayspace = df;
		init(conn, scope, null);
	}

	protected AbstractRunPlayspaceFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		init(conn, scope, selectionModel);
	}

	private void init(ServerConnection conn, String scope, TreeSelectionModel selectionModel){
		if (scope == null)
			throw new IllegalArgumentException("null scope");

		this.svrConn = conn;
		tableObjFactory = createTableObjectFactory();
		rootNode = createRootNode(scope);
		createTable(selectionModel);
		this.setViewportView(table);

		//todo: maybe you don't want to set that
		table.setPreferredSize(new Dimension(500, 400));

		this.setPreferredSize(table.getPreferredSize());
	}




	protected abstract TableObjectFactory createTableObjectFactory();

	/**
	 * This method creates the root node for the tree table.
	 * @param scope
	 */
	protected abstract ObjectTreeNode createRootNode(String scope);

	/**
	 * This method creates the treetable
	 */
	protected void createTable(TreeSelectionModel selectionModel)
	{
		tree = new DomeTree(rootNode, false); // not editable
		tree.setRowHeight(ROW_HEIGHT); // set a little bigger than normal
		if (selectionModel != null)
			tree.setSelectionModel(selectionModel);
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				Object owf = (((AbstractTreeObjectFactoryTreeNode) e.getPath().getLastPathComponent()).getTreeNodeObject());
                if (owf instanceof FileSystemObject) {
	                //System.out.println("AbstarctFileSysyemTable.createTable: valueChanged");
                    objectWithFocus = (ClientObjectRecord) owf;
                }

			}
		});
		tree.addTreeWillExpandListener(new TreeWillExpandListener()
		{

			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{

				Object owf = (((AbstractTreeObjectFactoryTreeNode) event.getPath().getLastPathComponent()).getTreeNodeObject());
				if (owf instanceof ClientObjectRecord) {
					objectWithFocus = (ClientObjectRecord) owf;
					objectWithFocus.listChildren();
					//System.out.println(objectWithFocus);
				}
				
			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}

		});
		InterfaceTableModel objectModel = new InterfaceTableModel(tree, getColumnNames().length, getColumnNames(), tableObjFactory);
		table = new ObjectTreeTable(objectModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new Renderers.NothingRenderer());

		DomeTable.customizeTable(table);

		//  table = new FileSystemTreeTable(tree, getColumnNames(), tableObjFactory);
	}

	/**
	 * Note: column names array must be either static in the subclass or
	 * created in this method since this method is called in the constructor method.
	 * @return columnNames for this table; can not be null
	 */
	protected abstract String[] getColumnNames();

	// PUBLIC methods

	public void refresh()
	{
		super.repaint();
	}

	public Object getSelectedItem()
	{
		if (tree.isSelectionEmpty())
			return null;
		else
			return ((AbstractTreeObjectFactoryTreeNode) tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject();
	}

	public abstract void addFolder();

	public abstract void renameFolder();

	public abstract void deleteSelectedItem();

	public void addTreeSelectionListener(TreeSelectionListener tsl)
	{
		tree.addTreeSelectionListener(tsl);
	}

	public void removeTreeSelectionListener(TreeSelectionListener tsl)
	{
		tree.removeTreeSelectionListener(tsl);
	}

	/**
	 * @return human-friendly path
	 */
	public String getSelectedPath()
	{
		if (tree.isSelectionEmpty())
			return "";
		else {
			Object[] nodes = tree.getSelectionPath().getPath();
			StringBuffer path = new StringBuffer();
			for (int i = 1; i < nodes.length; i++) {
				AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) nodes[i];
				path.append("/" + ((FileSystemObject) node.getTreeNodeObject()).getName());
			}
			return path.toString().substring(1); // remove initial slash
		}
	}

	/**
	 * @return id of selected object (Integer for Folders, String for DomeFiles); returns null if nothing selected
	 */
	public Object getSelectedObjectId()
	{
		if (tree.isSelectionEmpty())
			return null;
		else {
			return ((FileSystemObject) ((AbstractTreeObjectFactoryTreeNode) tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject()).getId();
		}
	}

		/**
	 * @return selected object; returns null if nothing selected
	 */
	public Object getSelectedObject()
	{
		if (tree.isSelectionEmpty())
			return null;
		else {
			return ((FileSystemObject) ((AbstractTreeObjectFactoryTreeNode) tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject());
		}
	}

	public TreePath getSelectedTreePath()
	{
		if (tree.isSelectionEmpty())
			return null;
		else {
			return tree.getSelectionPath();
		}
	}

	public boolean setSelection(Object domeFileId, String type)
	{
		String dbId = (String) domeFileId;
		Vector path = new Vector();

		try {
			if (type.equalsIgnoreCase("model"))
				path = FileSystemFunctions.getPathForModel(svrConn, dbId);
			else if (type.equalsIgnoreCase("playspace"))
				path = FileSystemFunctions.getPathForPlayspace(svrConn, dbId);
			else
				System.out.println("AbstractFileSystemTable: path not defined");
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

		}
		catch (Exception e) {
			e.printStackTrace();
			e.toString();
			System.out.println("nop...");
			return false;
		}
		return true;
	}

	public void setSelectionModel(TreeSelectionModel tsm)
	{

		tree.setSelectionModel(tsm);

	}

	private Integer findDomeFileParent(String id)
	{
		Integer parentId;
		parentId = FileSystemFunctions.getParentDbId(svrConn, id, "dome_file");
		return parentId;
	}

	private Integer findFolderParent(Integer id)
	{
		Integer parentId;
		parentId = FileSystemFunctions.getParentDbId(svrConn, id, "folder");
		return parentId;
	}

	class InterfaceTableModel extends TableObjectFactoryObjectTreeTableModel
	{

		public InterfaceTableModel(JTree tree, int numberColumns, Object[] columnNames, TableObjectFactory factory)
		{
			super(tree, numberColumns, columnNames, factory);
		}

		public InterfaceTableModel(ObjectTreeNode root, int numberColumns, Object[] columnNames, TableObjectFactory factory)
		{
			super(root, numberColumns, columnNames, factory);
		}

		// TableModel interface
		public boolean isCellEditable(int row, int column)
		{
			if(column == 1) return super.isCellEditable(row, column);
			return false;

		}

		/*public TableCellRenderer getCellRenderer(int row, int column)
		{
			if(column==0) return super.getCellRenderer(row,column);
			if(getValueAt(row,column)==null)
				return new Renderers.NothingRenderer();
			return new DefaultTableCellRenderer();
		}*/

	}
}
