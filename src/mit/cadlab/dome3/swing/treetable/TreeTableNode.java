// TreeTableNode.java
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.TableData;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;

import javax.swing.table.AbstractTableModel;

/*
 * A TreeTableNode represents a type of object that can be displayed
 * in a single row of a TreeTable as a node in the tree.
 */

public interface TreeTableNode extends ObjectTreeNode, TableData
{

	public AbstractTableModel getTableModel();

	public void setTableModel(AbstractTableModel model);

	public void notifyNodeColumnChanged(int column);

}
