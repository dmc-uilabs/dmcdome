package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.swing.treetable.TableObjectFactoryObjectTreeTableModel;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 17, 2003
 * Time: 4:00:14 PM
 * To change this template use Options | File Templates.
 */
public class DeployToolInterfaceSelectionTable extends JScrollPane
{
    public static final GridBagConstraints gbc = null;

    protected static int ROW_HEIGHT = 25;
    protected String[] columnNames = new String[]{"name", "value", "available", "description"};
    protected int[] columnWidths = new int[]{300, 200, 75, 200};

    protected DeployAnalysisToolData _tool;
    protected InterfaceTableModel _objectModel;
    protected TableObjectFactory _tableObjectFactory;
    protected ObjectTreeNode _rootNode;
    protected DomeTree _tree;
    protected ObjectTreeTable _table;

    public DeployToolInterfaceSelectionTable(DeployAnalysisToolData tool)
    {
        this(tool, null);
    }

    protected DeployToolInterfaceSelectionTable(DeployAnalysisToolData tool, TreeSelectionModel selectionModel)
    {
        if (tool == null)
			throw new IllegalArgumentException("null tool");
		_tool = tool;
		_tableObjectFactory = this.createTableObjectFactory();
		_rootNode = this.createRootNode();
		createTable(selectionModel);
		setViewportView(_table);
    }

    protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("DeployToolInterfaceSelectionTable");

		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                                "mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable$DefaultTableObject",
		                                "mit.cadlab.dome3.objectmodel.DomeObject");

		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData",
                                        "mit.cadlab.dome3.gui.deploy.components.tool.DeployToolTableObject");

		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolInterfaceData",
                                        "mit.cadlab.dome3.gui.deploy.components.tool.DeployToolInterfaceTableObject");

        //for filters
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
                                        "mit.cadlab.dome3.gui.deploy.components.DeployFilterTableObject",
                                        "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");
		//for parameters
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime",
                                        "mit.cadlab.dome3.gui.deploy.components.DeployParameterTableObject");
		return factory;
	}

    protected String[] getColumnNames()
	{
		return columnNames;
	}

    protected ObjectTreeNode createRootNode()
	{
		return new DeployToolInterfaceTreeNode(_tool);
	}

    /**
	 * This method creates the treetable
	 */
	protected void createTable(TreeSelectionModel selectionModel)
	{
		_tree = new DomeTree(_rootNode, true); // not editable
		_tree.setRootVisible(true);
		_tree.setRowHeight(ROW_HEIGHT); // set a little bigger than normal
		if (selectionModel != null)
			_tree.setSelectionModel(selectionModel);
		_tree.addTreeWillExpandListener(new TreeWillExpandListener()
		{

			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{

				Object objectWithFocus = (((AbstractTreeObjectFactoryTreeNode) event.getPath().getLastPathComponent()).getTreeNodeObject());
				if (objectWithFocus instanceof DeployInterfaceData) {
					((DeployInterfaceData) objectWithFocus).loadXml();
				}
			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{

			}

		});

		_objectModel = new InterfaceTableModel(_tree, getColumnNames().length, getColumnNames(), _tableObjectFactory);
		_table = new ObjectTreeTable(_objectModel);
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setInitialColumnWidths(columnWidths);
		_table.setDefaultRenderer(Object.class, new Renderers.NothingRenderer());

		DomeTable.customizeTable(_table);

	}

    protected void setInitialColumnWidths(int[] widths)
	{
		TableColumn column = null;
        int minColumns = Math.min(_table.getModel().getColumnCount(), widths.length);
        for (int i = 0; i < minColumns; i++)
        {
            column = _table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
	}

	public void setTableEditable(boolean value)
	{
		_objectModel.setIsTableEditable(value);
	}

    class InterfaceTableModel extends TableObjectFactoryObjectTreeTableModel
	{
		public boolean _isTableEditable = true;

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
			if (!_isTableEditable)
				return false;
			else
				return super.isCellEditable(row, column);
		}

		public boolean getIsTableEditable()
		{
			return _isTableEditable;
		}

		public void setIsTableEditable(boolean value)
		{
			_isTableEditable = value;
		}
	}

}
