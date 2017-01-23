// DomeObjectTreeTableModel.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import mit.cadlab.dome3.gui.guiutils.table.DomeTableObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.GenericDomeTreeObject;
import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTreeObject;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.table.TableObjectEvent;
import mit.cadlab.dome3.swing.table.TableObjectListener;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.treetable.DefaultObjectTreeTableModel;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;

import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * This implementation of TreeTableModel assumes
 * that the tree nodes are all instances of DefaultObjectTreeNode
 * and that the TreeObject is a DomeTreeObject
 */
public class DomeObjectTreeTableModel extends DefaultObjectTreeTableModel
{

	protected DomeTableObjectFactory tableObjectFactory;
	protected Hashtable tableObjects = new Hashtable(); // table objects keyed by tree paths
	protected Hashtable tableObjectTreePaths = new Hashtable(); // treePaths keyed by table objects
	protected DefaultTableObjectListener tObjListener = new DefaultTableObjectListener();
	protected boolean defaultInterfaceCellExists = false;
	protected boolean isModelView = false;
	protected boolean isPluginModel = false;

	public DomeObjectTreeTableModel(JTree tree, int numberColumns,
	                                Object[] columnNames,
	                                DomeTableObjectFactory factory)
	{
		super(tree, numberColumns, columnNames);
		this.tableObjectFactory = factory;
	}

	public DomeObjectTreeTableModel(JTree tree,
	                                int numberColumns,
	                                Object[] columnNames,
	                                DomeTableObjectFactory factory,
	                                boolean isPluginModel)
	{
		super(tree, numberColumns, columnNames);
		this.tableObjectFactory = factory;
		this.isPluginModel = isPluginModel;
	}

	public DomeObjectTreeTableModel(JTree tree, int numberColumns,
	                                Object[] columnNames,
	                                DomeTableObjectFactory factory,
	                                boolean defaultInterfaceCellExists,
	                                boolean isModelView)
	{
		this(tree, numberColumns, columnNames, factory);
		this.defaultInterfaceCellExists = defaultInterfaceCellExists;
		this.isModelView = isModelView;
	}

	public DomeObjectTreeTableModel(ObjectTreeNode root, int numberColumns,
	                                Object[] columnNames,
	                                DomeTableObjectFactory factory)
	{
		this(new DomeTree(new ObjectTreeModel(root), false),
		     numberColumns, columnNames, factory);
	}

	// TableModel interface
	//overrrides method in DefaultObjectTreeTableModel
	public boolean isCellEditable(int row, int column)
	{
		Object obj = null;
		if (isModelView) {
			if (column > 0) {
				return false; //value and mapping cols uneditable
			} else if (column == 0) {
				TableObject tobj = getTableObjectAt(row);
				if (tobj instanceof BuildTreeTable.DefaultTableObject) {
					obj = ((BuildTreeTable.DefaultTableObject) tobj).getDomeObject();
				}
				if (obj != null && obj instanceof Context) {
					return true;
				} else {
					return false;
				}
			}
		} else if (isPluginModel) {
			if (column != 0) {
				return true;
			} else
				return false;
		}
		return super.isCellEditable(row, column);
	}

	protected TableObject getTableObjectAt(int row)
	{
		TreePath treePath = tree.getPathForRow(row);
		TableObject tableObject = (TableObject) tableObjects.get(treePath);
		if (tableObject == null) {
			ObjectTreeNode node = (ObjectTreeNode) treePath.getLastPathComponent();
			TreeObject treeObject = node.getTreeObject();
			if(treeObject instanceof DomeTreeObject) {
				tableObject = tableObjectFactory.getTableObject(((DomeTreeObject)treeObject).getDomeObject());
			}
			else if(treeObject instanceof GenericDomeTreeObject) {
				tableObject = tableObjectFactory.getTableObject(((GenericDomeTreeObject) treeObject).getData());
			}
			else if (treeObject instanceof FileSystemObjectTreeObject) {
				tableObject = tableObjectFactory.getTableObject(((FileSystemObjectTreeObject) treeObject).getData());
			}
			tableObject.addTableObjectListener(tObjListener);
			tableObjects.put(treePath, tableObject);
			Vector tableObjectPaths = (Vector) tableObjectTreePaths.get(tableObject);
			if (tableObjectPaths == null) {
				tableObjectPaths = new Vector();
				tableObjectTreePaths.put(tableObject, tableObjectPaths);
			}
			tableObjectPaths.add(treePath);
		}
		return tableObject;
	}

	// supports the same table object in multiple rows of table
	protected int[] getRowsForTableObject(TableObject tObj)
	{
		Vector result = new Vector();
		Vector treePaths = (Vector) tableObjectTreePaths.get(tObj);
		if (treePaths != null) {
			for (int i = treePaths.size() - 1; i >= 0; --i) {
				TreePath path = (TreePath) treePaths.get(i);
				int row = tree.getRowForPath(path);
				if (row == -1) { // hidden or gone
					if (!isValidPath(path)) { // gone
						tableObjects.remove(path);
						treePaths.remove(path);
					} // else hidden
				} else { // valid and showing
					result.add(new Integer(row));
				}
			}
		}
		if (treePaths.isEmpty()) { // object not in table
			tObj.removeTableObjectListener(tObjListener);
			tableObjectTreePaths.remove(tObj);
		}
		int[] rows = new int[result.size()];
		for (int r = 0; r < rows.length; ++r) {
			rows[r] = ((Integer) result.elementAt(r)).intValue();
		}
		return rows;
	}

	protected boolean isValidPath(TreePath path)
	{
		// assumes all elements are DefaultObjectTreeNodes
		// invalid element has null user object; start searching from end
		Object[] pathElements = path.getPath();
		for (int i = pathElements.length - 1; i >= 0; --i) {
			if (((DefaultObjectTreeNode) pathElements[i]).getUserObject() == null) {
				return false;
			}
		}
		return true;
	}

	class DefaultTableObjectListener implements TableObjectListener
	{
		public void tableObjectChanged(TableObjectEvent event)
		{
			TableObject tableObject = (TableObject) event.getSource();
			int[] rows = getRowsForTableObject(tableObject);
			if (rows.length == 0) { // object not in visible table
				return;
			}
			int column = event.getColumn();
			if (column == TableObjectEvent.ALL_COLUMNS)
				for (int i = 0; i < rows.length; ++i) {
					fireTableRowsUpdated(rows[i], rows[i]);
				}
			else
				for (int i = 0; i < rows.length; ++i) {
					fireTableCellUpdated(rows[i], column);
				}
		}
	}

}
