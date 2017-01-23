// TableObject.java
package mit.cadlab.dome3.swing.table;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A TableObject represents a type of object that can be displayed
 * in a single row of an ObjectTable.
 */
public interface TableObject
{

	public boolean isEditableAt(int column);

	public Object getValueAt(int column);

	public void setValueAt(Object value, int column);

	// Implement either getClassAt or getRendererAt/getEditorAt
	// return null to default to default table behavior
	public Class getClassAt(int column);

	public TableCellRenderer getRendererAt(int column);

	public TableCellEditor getEditorAt(int column);

	// TableObject change notification support
	public void addTableObjectListener(TableObjectListener l);

	public void removeTableObjectListener(TableObjectListener l);

}
