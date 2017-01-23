// ObjectTreeTableCellEditor.java
package mit.cadlab.dome3.swing.treetable;

import mit.cadlab.dome3.swing.table.ObjectTable;
import mit.cadlab.dome3.swing.table.ObjectTableModel;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceBuildTreeObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.DomeObject;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class ObjectTreeTableCellEditor extends DefaultCellEditor
{

	protected JTree tree;
	protected ObjectTable table;

	public ObjectTreeTableCellEditor(JTree tree, ObjectTable table)
	{
		super(new TreeTableTextField());
		this.tree = tree;
		this.table = table;
	}

	/**
	 * Overriden to determine an offset that tree would place the
	 * editor at. The offset is determined from the
	 * <code>getRowBounds</code> JTree method, and additionaly
	 * from the icon DefaultTreeCellRenderer will use.
	 * <p>The offset is then set on the TreeTableTextField component
	 * created in the constructor, and returned.
	 */
	public Component getTableCellEditorComponent(JTable table,
	                                             Object value,
	                                             boolean isSelected,
	                                             int r, int c)
	{
		Component component = super.getTableCellEditorComponent
		        (table, value, isSelected, r, c);
		((TreeTableTextField) getComponent()).offset = getOffset(r);
		((TreeTableTextField) getComponent()).setCurrent();
		((TreeTableTextField) getComponent()).setFont(table.getFont());
		return component;
	}

	protected int getOffset(int row)
	{
		Rectangle bounds = tree.getRowBounds(row);
		int offset = bounds.x;
		TreeCellRenderer tcr = tree.getCellRenderer();
		if (tcr instanceof DefaultTreeCellRenderer) {
			Object node = tree.getPathForRow(row).getLastPathComponent();
			Icon icon;
			if (tree.getModel().isLeaf(node))
				icon = ((DefaultTreeCellRenderer) tcr).getLeafIcon();
			else if (tree.isExpanded(row))
				icon = ((DefaultTreeCellRenderer) tcr).getOpenIcon();
			else
				icon = ((DefaultTreeCellRenderer) tcr).getClosedIcon();
			if (icon != null) {
				offset += ((DefaultTreeCellRenderer) tcr).getIconTextGap() +
				        icon.getIconWidth();
			}
		}
		return offset;
	}

	/**
	 * This is overriden to forward single-click events to the tree.
	 * This will return true if the click count == 2, or the event is null.
	 * Click count functionality is overridden.
	 */
	public boolean isCellEditable(EventObject e)
	{
		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;
			// If the modifiers are not 0 (or the left mouse button),
			// tree may try and toggle the selection, and table
			// will then try and toggle, resulting in the
			// selection remaining the same. To avoid this, we
			// only dispatch when the modifiers are 0 (or the left mouse
			// button).
			if (SwingUtilities.isLeftMouseButton(me)) {
				// find which column tree is in
				int counter = table.getColumnCount() - 1;
				for (; counter >= 0; counter--) {
					if (table.getColumnClass(counter) == ObjectTreeTableModel.class) {
						break;
					}
				}
				if (counter == -1) {
					System.err.println("set table.getColumnClass to return tree class");
					counter = 0; // set to first column if not found
				}
				MouseEvent newME = new MouseEvent
				        (tree, me.getID(),
				         me.getWhen(), me.getModifiers(),
				         me.getX() - table.getCellRect(0, counter, true).x,
				         me.getY(), me.getClickCount(),
				         me.isPopupTrigger());
				int row = tree.getClosestRowForLocation(newME.getX(), newME.getY());
				if (((ObjectTableModel) table.getModel()).isCellEditable(row, 0))
					return newME.getClickCount() == 2 && newME.getX() > getOffset(row);
			}
			return false;
		}
		return e == null;
	}
}

// modification of TreeTableCellEditor.java
/**
 * An editor that can be used to edit the tree column. This extends
 * DefaultCellEditor and uses a JTextField (actually, TreeTableTextField)
 * to perform the actual editing.
 * <p>To support editing of the tree column we can not make the tree
 * editable. The reason this doesn't work is that you can not use
 * the same component for editing and renderering. The table may have
 * the need to paint cells, while a cell is being edited. If the same
 * component were used for the rendering and editing the component would
 * be moved around, and the contents would change. When editing, this
 * is undesirable, the contents of the text field must stay the same,
 * including the caret blinking, and selections persisting. For this
 * reason the editing is done via a TableCellEditor.
 * <p>Another interesting thing to be aware of is how tree positions
 * its render and editor. The render/editor is responsible for drawing the
 * icon indicating the type of node (leaf, branch...). The tree is
 * responsible for drawing any other indicators, perhaps an additional
 * +/- sign, or lines connecting the various nodes. So, the renderer
 * is positioned based on depth. On the other hand, table always makes
 * its editor fill the contents of the cell. To get the allusion
 * that the table cell editor is part of the tree, we don't want the
 * table cell editor to fill the cell bounds. We want it to be placed
 * in the same manner as tree places it editor, and have table message
 * the tree to paint any decorations the tree wants. Then, we would
 * only have to worry about the editing part. The approach taken
 * here is to determine where tree would place the editor, and to override
 * the <code>reshape</code> method in the JTextField component to
 * nudge the textfield to the location tree would place it. Since
 * JTreeTable will paint the tree behind the editor everything should
 * just work. So, that is what we are doing here. Determining of
 * the icon position will only work if the TreeCellRenderer is
 * an instance of DefaultTreeCellRenderer. If you need custom
 * TreeCellRenderers, that don't descend from DefaultTreeCellRenderer,
 * and you want to support editing in JTreeTable, you will have
 * to do something similiar.
 */
