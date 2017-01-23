// ObjectTableModel.java
package mit.cadlab.dome3.swing.table;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * An ObjectTableModel is the model used by an ObjectTable.
 * An ObjectTable is used to represent a collection of objects in a table.
 * Each row represents one object and the value, renderer and editor
 * for each column in the table for that object is represented in
 * a TableObject.
 */
public interface ObjectTableModel extends TableModel
{
	// implement either getColumnClass or getCellEditor/Renderer
	// Table searches in following priority:
	// 1) getCellEditor/Renderer for object
	// 2) getCellEditor/Renderer from column model
	// 3) getDefaultEditor/Renderer for getColumnClass for object
	// 4) getDefaultEditor/Renderer for getColumnClass for model

	public Class getColumnClass(int row, int column);

	public TableCellRenderer getCellRenderer(int row, int column);

	public TableCellEditor getCellEditor(int row, int column);

}
