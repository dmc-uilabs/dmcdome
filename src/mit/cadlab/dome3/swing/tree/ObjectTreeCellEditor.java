// ObjectTreeCellEditor.java
package mit.cadlab.dome3.swing.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

/**
 * Click-count on text determines when to start cell editing.
 * Default click-count is 2.
 */
public class ObjectTreeCellEditor extends DefaultTreeCellEditor
{

	protected int clickCountToEdit = 2;

	public ObjectTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer)
	{
		super(tree, renderer);
	}

	public ObjectTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer,
	                            TreeCellEditor editor)
	{
		super(tree, renderer, editor);
	}

	public int getClickCountToEdit()
	{
		return clickCountToEdit;
	}

	public void setClickCountToEdit(int newClickCountToEdit)
	{
		clickCountToEdit = newClickCountToEdit;
	}

	public boolean isCellEditable(EventObject event)
	{
		if ((event instanceof MouseEvent) &&
		        // should not activate if modifiers present??
		        SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
			MouseEvent me = (MouseEvent) event;
			if ((me.getClickCount() == clickCountToEdit) &&
			        inHitRegion(me.getX(), me.getY())) { // clicked on text
				prepareForEditing();
				me.consume();
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
	                                            boolean isSelected,
	                                            boolean expanded,
	                                            boolean leaf, int row)
	{
		renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, false);
		return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
	}

}
