// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project.run;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.FileSystemFilters;
import mit.cadlab.dome3.gui.fileSystem.FileSystemObject;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.treetable.TableObjectFactoryObjectTreeTableModel;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public abstract class AbstractRunProjectFileSystemTable extends JScrollPane {
    public static final String PROPERTY_INTERFACES_CREATED = "propertyInterfaceForProjectCreated";
    protected static int ROW_HEIGHT = 22;
    protected static GridBagConstraints gbc;

    protected TableObjectFactory tableObjFactory;
    protected ServerConnection svrConn;
    protected ObjectTreeNode rootNode;
    protected DomeTree tree;
    protected RunTreeTable table;
    public ClientObjectRecord objectWithFocus;
    protected DomeFile projectStaticInfo;
    protected IntegrationProjectClientRuntime project;
    protected ClientPlayspaceRuntime playspace;

    protected AbstractRunProjectFileSystemTable(ServerConnection conn, IntegrationProjectClientRuntime project,
                                                DomeFile df, ClientPlayspaceRuntime playspace) {
        projectStaticInfo = df;
        this.playspace = playspace;
        init(conn, project, null);
    }

    private void init(ServerConnection conn, IntegrationProjectClientRuntime project, TreeSelectionModel selectionModel) {
        this.project = project;
        this.svrConn = conn;
        tableObjFactory = createTableObjectFactory();
        rootNode = createRootNode();
        createTable(selectionModel);
        this.setViewportView(table);
        this.getViewport().setBackground(Color.white);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }


    protected abstract TableObjectFactory createTableObjectFactory();

    /**
     * This method creates the root node for the tree table.
     */
    protected abstract ObjectTreeNode createRootNode();

    /**
     * This method creates the treetable
     */
    protected void createTable(TreeSelectionModel selectionModel) {
        tree = new DomeTree(rootNode, false); // not editable
        tree.setRowHeight(ROW_HEIGHT); // set a little bigger than normal
        if (selectionModel != null)
            tree.setSelectionModel(selectionModel);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                Object owf = (((AbstractTreeObjectFactoryTreeNode) e.getPath().getLastPathComponent()).getTreeNodeObject());
                if (owf instanceof ClientObjectRecord) {
                    if (objectWithFocus instanceof ClientInterfaceRecord || objectWithFocus instanceof ClientAnalysisToolInterfaceRecord) {
                        if (!RunMenus.addBookMarkMenu.isEnabled()) RunMenus.addBookMarkMenu.setEnabled(true);
                    } else {
                        if (RunMenus.addBookMarkMenu.isEnabled()) RunMenus.addBookMarkMenu.setEnabled(false);
                    }
                }
                if (owf instanceof BrowseModelFolder) {
                    if (RunMenus.addBookMarkMenu.isEnabled()) RunMenus.addBookMarkMenu.setEnabled(false);
                }
            }
        });
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                Object owf = (((AbstractTreeObjectFactoryTreeNode) event.getPath().getLastPathComponent()).getTreeNodeObject());
                if (owf instanceof ClientObjectRecord) {
                    // expand the children, creating interfaces if necessary
                    objectWithFocus = (ClientObjectRecord) owf;
                    if (owf instanceof ClientInterfaceRecord) {
                        ((ClientInterfaceRecord) owf).listChildren();
                    } else if (owf instanceof ClientModelRecord) {
                        ((ClientModelRecord) owf).listChildren(false);
                    } else {
                        objectWithFocus.listChildren();
                    }
                    if (objectWithFocus instanceof ClientInterfaceRecord) {
                        ArrayList iFaceList = new ArrayList();
                        iFaceList.add(((ClientInterfaceRecord) objectWithFocus));
                        firePropertyChange(PROPERTY_INTERFACES_CREATED, null, iFaceList);
                    }
                }

                // open project interfaces
                if (owf instanceof BrowseModelFolder) {
//                    java.util.List ifaceList = new ArrayList();
                    BrowseModelFolder objectWithFocus = (BrowseModelFolder) owf;
                    java.util.List content = objectWithFocus.getContent();
                    for (Iterator iter = content.iterator(); iter.hasNext();) {
                        Object next = iter.next();
                        if (next instanceof ClientModelRecord) {
                            ((ClientModelRecord) next).listChildren(false);
                        }
                    }
                    // tell whoever is interested about any new interfaces that were created
// Currently we are not creating project inface in listChildren method. so following line has
//been commented.  Reinstate it back in if we create an iface on the server side in listChildren method again.
//                    firePropertyChange(PROPERTY_INTERFACES_CREATED, null, ifaceList);
                }
            }

            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            }
        });
        table = new RunTreeTable(tree, getColumnNames().length,
                getColumnNames(), getColumnWidths(), tableObjFactory);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Note: column names array must be either static in the subclass or
     * created in this method since this method is called in the constructor method.
     * @return columnNames for this table; can not be null
     */
    protected abstract String[] getColumnNames();

    protected abstract int[] getColumnWidths();

    // PUBLIC methods

    public void refresh() {
        super.repaint();
    }

    public Object getSelectedItem() {
        if (tree.isSelectionEmpty())
            return null;
        else
            return ((AbstractTreeObjectFactoryTreeNode) tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject();
    }

    public abstract void addFolder();

    public abstract void renameFolder();

    public abstract void deleteSelectedItem();

    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        tree.addTreeSelectionListener(tsl);
    }

    public void removeTreeSelectionListener(TreeSelectionListener tsl) {
        tree.removeTreeSelectionListener(tsl);
    }

    /**
     * @return human-friendly path
     */
    public String getSelectedPath() {
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
    public Object getSelectedObjectId() {
        if (tree.isSelectionEmpty())
            return null;
        else {
            return ((FileSystemObject) ((AbstractTreeObjectFactoryTreeNode) tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject()).getId();
        }
    }

    /**
     * @return selected object; returns null if nothing selected
     */
    public Object getSelectedObject() {
        if (tree.isSelectionEmpty())
            return null;
        else {
            return ((FileSystemObject) ((AbstractTreeObjectFactoryTreeNode) tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject());
        }
    }

    public TreePath getSelectedTreePath() {
        if (tree.isSelectionEmpty())
            return null;
        else {
            return tree.getSelectionPath();
        }
    }

    public boolean setSelection(Object domeFileId, String type) {
        String dbId = (String) domeFileId;
        Vector path = new Vector();

        try {
            if (type.equalsIgnoreCase("model"))
                path = FileSystemFunctions.getPathForModel(svrConn, dbId);
            else if (type.equalsIgnoreCase("project"))
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

        } catch (Exception e) {
            e.printStackTrace();
            e.toString();
            System.out.println("exception in setSelection");
            return false;
        }
        return true;
    }

    public void setSelectionModel(TreeSelectionModel tsm) {

        tree.setSelectionModel(tsm);

    }

    class InterfaceTableModel extends TableObjectFactoryObjectTreeTableModel {

        public InterfaceTableModel(JTree tree, int numberColumns, Object[] columnNames, TableObjectFactory factory) {
            super(tree, numberColumns, columnNames, factory);
        }

        public InterfaceTableModel(ObjectTreeNode root, int numberColumns, Object[] columnNames, TableObjectFactory factory) {
            super(root, numberColumns, columnNames, factory);
        }

        // TableModel interface
        public boolean isCellEditable(int row, int column) {
            if (column == 1) return super.isCellEditable(row, column);
            return false;

        }
    }

    //add a fireproperty change handle here
    public void fireInterfaceCreatedChange(ClientInterfaceRecord iface) {
        ArrayList ifaceList = new ArrayList();
        ifaceList.add(iface);
        firePropertyChange(PROPERTY_INTERFACES_CREATED, null, ifaceList);
    }
}
