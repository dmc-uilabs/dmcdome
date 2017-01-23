package mit.cadlab.dome3.gui.objectmodel.model.tool.run;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;

import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.*;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 26, 2004
 * Time: 3:06:40 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractAnalysisToolRunFileSystemTable extends JScrollPane
{
    public static final String PROPERTY_INTERFACES_CREATED = "propertyInterfaceForAnalysisToolCreated";
    protected static int ROW_HEIGHT = 22;
    protected static GridBagConstraints gbc;

    protected TableObjectFactory _tableObjFactory;
    protected ServerConnection _svrConn;
    protected ObjectTreeNode _rootNode;
    protected DomeTree _tree;
    protected RunTreeTable _table;
    public ClientObjectRecord _objectWithFocus;
    protected DomeFile _analysisToolStaticInfo;
    protected OptimizationToolClientRuntime _analysisTool;
    protected ClientPlayspaceRuntime _playspace;

    protected AbstractAnalysisToolRunFileSystemTable(ServerConnection conn, OptimizationToolClientRuntime analysisTool,
                                                     DomeFile df, ClientPlayspaceRuntime playspace)
    {
        _analysisToolStaticInfo = df;
        _playspace = playspace;
        init(conn, analysisTool, null);
    }

    private void init(ServerConnection conn, OptimizationToolClientRuntime analysisTool, TreeSelectionModel selectionModel)
    {
        _analysisTool = analysisTool;
        _svrConn = conn;
        _tableObjFactory = createTableObjectFactory();
        _rootNode = createRootNode();
        createTable(selectionModel);
        setViewportView(_table);
        getViewport().setBackground(Color.white);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * This method creates the treetable
     */
    protected void createTable(TreeSelectionModel selectionModel)
    {
        _tree = new DomeTree(_rootNode, false); // not editable
        _tree.setRowHeight(ROW_HEIGHT); // set a little bigger than normal
        if (selectionModel != null)
            _tree.setSelectionModel(selectionModel);
        _tree.addTreeWillExpandListener(new TreeWillExpandListener()
        {
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
            {
                Object owf = (((AbstractTreeObjectFactoryTreeNode) event.getPath().getLastPathComponent()).getTreeNodeObject());
                if (owf instanceof ClientObjectRecord)
                {
                    // expand the children, creating interfaces if necessary
                    _objectWithFocus = (ClientObjectRecord) owf;
                    _objectWithFocus.listChildren();

                    // tell whoever is interested about any new interfaces that were created
                    if (_objectWithFocus instanceof ClientModelRecord)
                    {
                        java.util.List iFaceList = ((ClientModelRecord) _objectWithFocus).getInterfaces();
                        firePropertyChange(PROPERTY_INTERFACES_CREATED, null, iFaceList);
                    }
                }

                // open project interfaces
                if (owf instanceof BrowseModelFolder)
                {
                    java.util.List ifaceList = new ArrayList();
                    BrowseModelFolder objectWithFocus = (BrowseModelFolder) owf;
                    java.util.List content = objectWithFocus.getContent();
                    for (Iterator iter = content.iterator(); iter.hasNext();)
                    {
                        Object next = iter.next();
                        if (next instanceof ClientInterfaceRecord)
                        {
                            ((ClientInterfaceRecord) next).listChildren();
                            ifaceList.add(next);
                        }
                    }
                    // tell whoever is interested about any new interfaces that were created
                    firePropertyChange(PROPERTY_INTERFACES_CREATED, null, ifaceList);
                }
            }

            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
            {
            }
        });

        _table = new RunTreeTable(_tree, getColumnNames().length,
                getColumnNames(), getColumnWidths(), _tableObjFactory);

        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    protected abstract TableObjectFactory createTableObjectFactory();

    /**
     * This method creates the root node for the tree table.
     */
    protected abstract ObjectTreeNode createRootNode();

    protected abstract String[] getColumnNames();

    protected abstract int[] getColumnWidths();


}
