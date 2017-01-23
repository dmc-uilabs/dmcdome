// TableObjectFactoryObjectTreeTableModel.java
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.table.TableObjectEvent;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.table.TableObjectListener;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This implementation of TreeTableModel assumes
 * that the tree nodes are all instances of subclasses of AbstractTreeObjectFactoryTreeNode
 * and that the tableobjects will be created by a TableObjectFactory.
 */
public class TableObjectFactoryObjectTreeTableModel extends DefaultObjectTreeTableModel
{

	protected TableObjectFactory tableObjectFactory;
	protected Hashtable tableObjects = new Hashtable(); // table objects keyed by tree paths
	protected Hashtable tableObjectTreePaths = new Hashtable(); // treePaths keyed by table objects
	protected DefaultTableObjectListener tObjListener = new DefaultTableObjectListener();

	public TableObjectFactoryObjectTreeTableModel(JTree tree, int numberColumns,
	                                              Object[] columnNames,
	                                              TableObjectFactory factory)
	{
		super(tree, numberColumns, columnNames);
		this.tableObjectFactory = factory;
	}

	public TableObjectFactoryObjectTreeTableModel(ObjectTreeNode root, int numberColumns,
	                                              Object[] columnNames,
	                                              TableObjectFactory factory)
	{
		this(new DomeTree(new ObjectTreeModel(root), false),
		     numberColumns, columnNames, factory);
	}

	// TableModel interface
	protected TableObject getTableObjectAt(int row)
	{
		TreePath treePath = tree.getPathForRow(row);
		TableObject tableObject = (TableObject) tableObjects.get(treePath);
		if (tableObject == null) {
			AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) treePath.getLastPathComponent();
			Object treeNodeObject = node.getTreeNodeObject();
			tableObject = tableObjectFactory.getTableObject(treeNodeObject);
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
