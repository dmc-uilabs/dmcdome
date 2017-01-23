// ObjectTreeTableModel.java
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.ObjectTableModel;

import javax.swing.JTree;

/**
 * An ObjectTreeTableModel is an ObjectTableModel implemented
 * on top of an ObjectTree. Possible implementations include
 * using an ObjectTree with TreeTableObject nodes or looking up
 * TreeTableObject wrappers for the nodes of the tree.
 */
public interface ObjectTreeTableModel extends ObjectTableModel
{

	public JTree getTree();

}
