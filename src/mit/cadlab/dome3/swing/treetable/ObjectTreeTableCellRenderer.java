// ObjectTreeTableCellRenderer.java
// Copyright (c) 2002 Massachusetts Insitute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class ObjectTreeTableCellRenderer extends JComponent
        implements TableCellRenderer
{

	protected JTree tree;
	protected JTable table;
	protected int visibleRow; // Last table/tree row asked to renderer

	public ObjectTreeTableCellRenderer(JTree tree, JTable table)
	{
		this.tree = tree;
		this.table = table;
	}

	/**
	 * updateUI is overridden to set the colors of the Tree's renderer
	 * to match that of the table.
	 */
	public void updateUI()
	{
		tree.updateUI();
		// Make the tree's cell renderer use the table's cell selection
		// colors.
		TreeCellRenderer tcr = tree.getCellRenderer();
		if (tcr instanceof DefaultTreeCellRenderer) {
			DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
			// For 1.1 uncomment this, 1.2 has a bug that will cause an
			// exception to be thrown if the border selection color is
			// null.
			// dtcr.setBorderSelectionColor(null);
			dtcr.setTextSelectionColor(UIManager.getColor
			                           ("Table.selectionForeground"));
			dtcr.setBackgroundSelectionColor(UIManager.getColor
			                                 ("Table.selectionBackground"));
		}
	}

	public void setRowHeight(int rowHeight)
	{
		tree.setRowHeight(rowHeight);
	}

	public int getRowHeight()
	{
		return tree.getRowHeight();
	}

	/**
	 * This is overridden to set the height to match that of the JTable.
	 */
	public void setBounds(int x, int y, int w, int h)
	{
		tree.setBounds(x, 0, w, table.getHeight());
	}

	/**
	 * Subclassed to translate the graphics such that the last visible
	 * row will be drawn at 0,0.
	 */
	public void paint(Graphics g)
	{
		g.translate(0, -visibleRow * getRowHeight());
		tree.paint(g);
	}

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table,
	                                               Object value,
	                                               boolean isSelected,
	                                               boolean hasFocus,
	                                               int row, int column)
	{
		Color background;
		Color foreground;

		if (isSelected) {
			background = table.getSelectionBackground();
			foreground = table.getSelectionForeground();
		} else {
			background = table.getBackground();
			foreground = table.getForeground();
		}
		visibleRow = row;

		TreeCellRenderer tcr = tree.getCellRenderer();
		if (tcr instanceof DefaultTreeCellRenderer) {
			DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
			if (isSelected) {
				dtcr.setTextSelectionColor(foreground);
				dtcr.setBackgroundSelectionColor(background);
			} else {
				dtcr.setTextNonSelectionColor(foreground);
				dtcr.setBackgroundNonSelectionColor(background);
			}
		}
		return this;
	}

}
