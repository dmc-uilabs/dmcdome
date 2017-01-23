// AbstractFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseInterfaceDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.treetable.TableObjectFactoryObjectTreeTableModel;

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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.Enumeration;
import java.util.Vector;

public abstract class AbstractFileSystemTable extends JScrollPane
{
	protected static int ROW_HEIGHT = 22;
	protected static GridBagConstraints gbc;

	protected TableObjectFactory tableObjFactory;
	protected ServerConnection svrConn;
	protected ObjectTreeNode rootNode;
	protected DomeTree tree;
//	protected ObjectTreeTable table;
	protected RunTreeTable table;

	public FileSystemObject objectWithFocus;
	protected DomeFile runningPlayspace;
	protected String scope;

	protected AbstractFileSystemTable(ServerConnection conn, String scope)
	{
		init(conn, scope, null);
		this.scope = scope;
	}

	protected AbstractFileSystemTable(ServerConnection conn, String scope, DomeFile df)
	{
		runningPlayspace = df;
		init(conn, scope, null);
		this.scope = scope;
	}

	protected AbstractFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		init(conn, scope, selectionModel);
		this.scope = scope;
	}

	private void init(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		if (scope == null)
			throw new IllegalArgumentException("null scope");

		this.svrConn = conn;
		tableObjFactory = createTableObjectFactory();
		rootNode = createRootNode(scope);
		createTable(selectionModel);
		this.setViewportView(table);
		this.getViewport().setBackground(Color.white);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
		if (selectionModel != null) {
			tree.setSelectionModel(selectionModel);
		}
		if(selectionModel.getSelectionMode() == ServerPanel.Multiple_Selection_Mode) {
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		}
		else {
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				Object owf = (((AbstractTreeObjectFactoryTreeNode) e.getPath().getLastPathComponent()).getTreeNodeObject());
				if (owf instanceof FileSystemObject) {
					objectWithFocus = (FileSystemObject) owf;
                    if (objectWithFocus instanceof BrowseInterfaceDomeFile) {
					     if(!RunMenus.addBookMarkMenu.isEnabled()) RunMenus.addBookMarkMenu.setEnabled(true);
					}
                    else if (objectWithFocus instanceof BrowseDomeFile) { //for project
					     if(!RunMenus.addBookMarkMenu.isEnabled()) RunMenus.addBookMarkMenu.setEnabled(true);
					}
                    else if (objectWithFocus instanceof DomeFile) { //for playspace
					     if(!RunMenus.addBookMarkMenu.isEnabled()) RunMenus.addBookMarkMenu.setEnabled(true);
					}
                  }
            }
		});
		tree.addTreeWillExpandListener(new TreeWillExpandListener()
		{

			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{

				Object owf = (((AbstractTreeObjectFactoryTreeNode) event.getPath().getLastPathComponent()).getTreeNodeObject());
				if (owf instanceof FileSystemObject) {
					ServerConnection newConn = svrConn;
					objectWithFocus = (FileSystemObject) owf;
					if (objectWithFocus instanceof BrowseInterfaceDomeFile) {
						String url = ((BrowseInterfaceDomeFile) objectWithFocus).getUrl();
						newConn = LoginUtils.compareServersAndGetConnection(svrConn, url);
					}
                    if (objectWithFocus instanceof BrowseModelFolder)
                    {
                        if (tree.getSelectionModel() instanceof FileSystemFilters.ModelsFilterTreeSelectionModel)
                        {
                            ((BrowseModelFolder) objectWithFocus).modelProjectCase = 1;
                        }
                        if (tree.getSelectionModel() instanceof FileSystemFilters.ProjectsFilterTreeSelectionModel)
                        {
                            ((BrowseModelFolder) objectWithFocus).modelProjectCase = 2;
                        }
                        if (tree.getSelectionModel() instanceof FileSystemFilters.AnalysisToolFilterTreeSelectionModel)
                        {
                            ((BrowseModelFolder) objectWithFocus).modelProjectCase = 3;
                        }
                    }
                    if (objectWithFocus instanceof Folder)
					    ((Folder)objectWithFocus).listChildren(newConn, AbstractFileSystemTable.this);
				    else
                        objectWithFocus.listChildren(newConn);
                }

			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}

		});
		table = new RunTreeTable(tree, getColumnNames().length,
		                         getColumnNames(), getColumnWidths(), tableObjFactory);

		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

/*		InterfaceTableModel objectModel = new InterfaceTableModel(tree, getColumnNames().length, getColumnNames(), tableObjFactory);
    	table = new ObjectTreeTable(objectModel);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new Renderers.NothingRenderer());
		DomeTable.customizeTable(table);*/
	}

	/**
	 * Note: column names array must be either static in the subclass or
	 * created in this method since this method is called in the constructor method.
	 * @return columnNames for this table; can not be null
	 */
	protected abstract String[] getColumnNames();

	protected abstract int[] getColumnWidths();

	// PUBLIC methods
	public ObjectTreeNode getRootNode()
	{
		return rootNode;
	}

	public DomeTree getTree()
	{
		return tree;
	}

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

	public Object getSelectedItems()
	{
		if (tree.isSelectionEmpty())
			return null;
		else
			return tree.getSelectionPaths();
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
			for (int i = 0; i < nodes.length; i++) {
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
			else if (type.equalsIgnoreCase("project"))
				path = FileSystemFunctions.getPathForProject(svrConn, dbId);
            else if (type.equalsIgnoreCase("analysis tool"))
                path = FileSystemFunctions.getPathForAnalysisTool(svrConn, dbId);
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
				if (fObj instanceof DomeFile) {
					vPath.remove(vPath.lastElement());
					break;
				}

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
			return false;

		}

		public TableCellRenderer getCellRenderer(int row, int column)
		{
			if (column == 0) return super.getCellRenderer(row, column);
			if (getValueAt(row, column) == null)
				return new Renderers.NothingRenderer();
			return new DefaultTableCellRenderer();
		}

	}
}
