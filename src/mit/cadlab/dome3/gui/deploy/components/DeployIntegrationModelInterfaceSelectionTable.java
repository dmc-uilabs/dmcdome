// AbstractFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.deploy.components;


import mit.cadlab.dome3.gui.fileSystem.FileSystemObject;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.tree.AbstractFilterTreeSelectionModel;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.swing.treetable.TableObjectFactoryObjectTreeTableModel;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.*;
import java.awt.*;

public class DeployIntegrationModelInterfaceSelectionTable extends JScrollPane {
    protected static int ROW_HEIGHT = 25;
    protected static GridBagConstraints gbc;
    protected String[] columnNames = new String[]{"name", "value", "available", "description"};
    protected int[] columnWidths = new int[]{300, 200, 75, 200};
    // todo create a base class for DeployModelData and DeployProjectData
    protected DeployProjectData project;
    protected InterfaceTableModel _objectModel;

    protected TableObjectFactory _tableObjectFactory;
    protected ObjectTreeNode _rootNode;
    protected DomeTree _tree;
    protected ObjectTreeTable _table;

    /**
     * Allows only interfaces to be selected.
     */
    public static class IModelInterfacesFilterTreeSelectionModel extends AbstractFilterTreeSelectionModel {
        protected boolean isValidSelectionPath(TreePath path) {
            Object obj = ((AbstractTreeObjectFactoryTreeNode) path.getLastPathComponent()).getTreeNodeObject();
            if (obj instanceof DeployInterfaceData) {
                return true;
            }
            return false;
        }
    }


    public DeployIntegrationModelInterfaceSelectionTable(DeployProjectData model) {
        this(model, new IModelInterfacesFilterTreeSelectionModel());

    }


    protected DeployIntegrationModelInterfaceSelectionTable(DeployProjectData project, TreeSelectionModel selectionModel) {
        if (project == null)
            throw new IllegalArgumentException("null model");
        this.project = project;
        _tableObjectFactory = this.createTableObjectFactory();
        _rootNode = this.createRootNode();
        this.createTable(selectionModel);
        this.setViewportView(_table);

        ObjectTreeModel tm=(ObjectTreeModel)_tree.getModel();
        for(int i=0;i<_rootNode.getChildCount();i++){
              TreeNode iModelNode=_rootNode.getChildAt(i);
            _tree.expandPath(tm.getPath(iModelNode));
      }
   }


    protected TableObjectFactory createTableObjectFactory() {
        CachingTableObjectFactory factory = new CachingTableObjectFactory("DeployInterfaceSelectionTable");

        factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
                "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$DefaultTableObject",
                "mit.cadlab.dome3.objectmodel.DomeObject");
        factory.registerTableObjectInfo("mit.cadlab.dome3.gui.deploy.components.DeployProjectData", "mit.cadlab.dome3.gui.deploy.components.DeployProjectTableObject");
        factory.registerTableObjectInfo("mit.cadlab.dome3.gui.deploy.components.DeployModelData", "mit.cadlab.dome3.gui.deploy.components.DeployIntegrationModelTableObject");
        factory.registerTableObjectInfo("mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData", "mit.cadlab.dome3.gui.deploy.components.DeployInterfaceTableObject");

        //for filters
        factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter", "mit.cadlab.dome3.gui.deploy.components.DeployFilterTableObject"
                , "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");
        //for parameters
        factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime", "mit.cadlab.dome3.gui.deploy.components.DeployParameterTableObject");
        return factory;
    }

    /**
     * This method creates the root node for the tree table.
     */

    protected String[] getColumnNames() {
        return columnNames;
    }


    protected ObjectTreeNode createRootNode() {
        return new DeployIModelInterfacesTreeNode(this.project);
    }

    /**
     * This method creates the treetable
     */
    protected void createTable(TreeSelectionModel selectionModel) {
        _tree = new DomeTree(_rootNode, true); // not editable
        _tree.setRootVisible(true);
        _tree.setRowHeight(ROW_HEIGHT); // set a little bigger than normal
        if (selectionModel != null)
            _tree.setSelectionModel(selectionModel);


        _tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                //System.out.println("expend");
                Object objectWithFocus = (((AbstractTreeObjectFactoryTreeNode) event.getPath().getLastPathComponent()).getTreeNodeObject());
                if (objectWithFocus instanceof DeployInterfaceData) {
                    ((DeployInterfaceData) objectWithFocus).loadXml();
                }
            }

            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }

        });

        _objectModel = new InterfaceTableModel(_tree, getColumnNames().length, getColumnNames(), _tableObjectFactory);
        _table = new ObjectTreeTable(_objectModel);
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setInitialColumnWidths(columnWidths);
        _table.setDefaultRenderer(Object.class, new Renderers.NothingRenderer());
        DomeTable.customizeTable(_table);

    }

    /**
     * Note: column names array must be either static in the subclass or
     * created in this method since this method is called in the constructor method.
     * @return columnNames for this table; can not be null
     */

    // PUBLIC methods

    public DeployInterfaceData getSelectedInterface() {
        TreePath tp = _tree.getSelectionPath();
        if (tp == null) // nothing selected
            return null;
        return (DeployInterfaceData) ((AbstractTreeObjectFactoryTreeNode) tp.getLastPathComponent()).getTreeNodeObject();
    }

    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        _tree.addTreeSelectionListener(tsl);
    }

    public void removeTreeSelectionListener(TreeSelectionListener tsl) {
        _tree.removeTreeSelectionListener(tsl);
    }

    /**
     * @return human-friendly path
     */
    public String getSelectedPath() {
        if (_tree.isSelectionEmpty())
            return "";
        else {
            Object[] nodes = _tree.getSelectionPath().getPath();
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
        if (_tree.isSelectionEmpty())
            return null;
        else {
            return ((DeployModelData) ((AbstractTreeObjectFactoryTreeNode) _tree.getSelectionPath().getLastPathComponent()).getTreeNodeObject()).getId();
        }
    }

    public void setSelectionModel(TreeSelectionModel tsm) {
        _tree.setSelectionModel(tsm);
    }

    protected void setInitialColumnWidths(int[] widths) {
        TableColumn column = null;
        int minColumns = Math.min(_table.getModel().getColumnCount(), widths.length);
        for (int i = 0; i < minColumns; i++) {
            column = _table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
    }

    public void setTableEditable(boolean value) {
        _objectModel.setIsTableEditable(value);
    }

    class InterfaceTableModel extends TableObjectFactoryObjectTreeTableModel {
        public boolean _isTableEditable = true;

        public InterfaceTableModel(JTree tree, int numberColumns, Object[] columnNames, TableObjectFactory factory) {
            super(tree, numberColumns, columnNames, factory);
        }

        public InterfaceTableModel(ObjectTreeNode root, int numberColumns, Object[] columnNames, TableObjectFactory factory) {
            super(root, numberColumns, columnNames, factory);
        }

        // TableModel interface
        public boolean isCellEditable(int row, int column) {
            if (!_isTableEditable)
                return false;
            else
                return super.isCellEditable(row, column);
        }

        public boolean getIsTableEditable() {
            return _isTableEditable;
        }

        public void setIsTableEditable(boolean value) {
            _isTableEditable = value;
        }
    }


}
