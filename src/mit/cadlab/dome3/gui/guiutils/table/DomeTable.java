// DomeTable.java
package mit.cadlab.dome3.gui.guiutils.table;

import mit.cadlab.dome3.swing.GuiConstants;
import mit.cadlab.dome3.swing.table.ObjectTable;
import mit.cadlab.dome3.swing.table.ObjectTableModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class DomeTable extends ObjectTable
{

	public DomeTable(ObjectTableModel model)
	{
		super(model);
		customizeTable(this);
	}

	public static void customizeTable(JTable table)
	{
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setDefaultRenderer(makeDefaultTableHeaderRenderer());
		tableHeader.setReorderingAllowed(false);
		table.setGridColor(Color.lightGray);
		table.setFont(GuiConstants.FONT11);
		// should add property changelistener for when table header changes
	}

	public static TableCellRenderer makeDefaultTableHeaderRenderer()
	{
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer()
		{
			protected Color headerColor;
			protected Color separatorColor;
			protected boolean drawSeparator = false;

			public Component getTableCellRendererComponent(JTable table, Object value,
			                                               boolean isSelected, boolean hasFocus,
			                                               int row, int column)
			{
				drawSeparator = (column != (table.getColumnCount() - 1)); // draw except last column
				if (headerColor == null) {
					Window w = SwingUtilities.windowForComponent(table);
					headerColor = w.getBackground();
					setBackground(headerColor);
					separatorColor = new Color(headerColor.getRed() - 25,
					                           headerColor.getBlue() - 25,
					                           headerColor.getGreen() - 25);
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				if (drawSeparator) { // draw separator on right if clipbounds intersects with header bounds
					Rectangle headerBounds = getBounds(); // absolute
					Rectangle separatorRect = new Rectangle(headerBounds.width - 2, // relative to header
					                                        headerBounds.y, 2, headerBounds.height);
					Rectangle clipBounds = g.getClipBounds(); // relative to header
					Rectangle dirtySeparator = separatorRect.intersection(clipBounds);
					if (dirtySeparator.width > 0 && dirtySeparator.height > 0) {
						g.setColor(separatorColor);
						g.fillRect(dirtySeparator.x, dirtySeparator.y,
						           dirtySeparator.width, dirtySeparator.height);
					}
				}
			}
		};
		headerRenderer.setHorizontalTextPosition(SwingConstants.LEFT);
		return headerRenderer;
	}

}
