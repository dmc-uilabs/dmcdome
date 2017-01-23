// CausalityInfoTableBase.java
package mit.cadlab.dome3.gui.objectmodel.causality;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.VTextIcon;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.LoopException;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Common models, renderers and editors for CausalityInfoRendererTable
 * and DependencyEditorTables.
 */
public class CausalityInfoTableBase
{

	public static JTable createDataTable(TableModel dataModel)
	{
		JTable dataTable = new JTable(dataModel) {
			public Component prepareRenderer(TableCellRenderer renderer,
			                                 int rowIndex, int vColIndex)
			{
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					jc.setToolTipText(getColumnName(vColIndex));
				}
				return c;
			}
		};
		dataTable.setFont(Templates.FONT11);
		dataTable.setShowGrid(false);
		dataTable.setIntercellSpacing(new Dimension(0, 0));
		dataTable.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		JTableHeader tableHeader = dataTable.getTableHeader();
		tableHeader.setResizingAllowed(false);
		tableHeader.setReorderingAllowed(false);
		tableHeader.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		TableColumn column;
		for (int i = 0; i < dataTable.getColumnCount(); i++) {
			column = dataTable.getColumnModel().getColumn(i);
			column.setHeaderRenderer(new VerticalTextTableCellRenderer());
			column.setCellRenderer(new BooleanCheckBoxRenderer());
			// need to set renderer for each column in order for header
			// size to be calculated properly. Otherwise, it assumes that
			// the size is the same for all columns.
			column.setMaxWidth(18);
		}
		return dataTable;
	}

	public static JTable createRowHeaderTable(List rowNames)
	{
		TableModel rowModel = new RowHeaderModel(rowNames);
		JTable rowTable = new JTable(rowModel);
		rowTable.setFont(Templates.FONT11);
		rowTable.setShowGrid(false);
		rowTable.setIntercellSpacing(new Dimension(0, 0));
		JTableHeader rowTableHeader = rowTable.getTableHeader();
		rowTableHeader.setResizingAllowed(false);
		TableColumn column = rowTable.getColumnModel().getColumn(0);
		column.setCellRenderer(new RowHeaderTableCellRenderer());
		// find max width
		int maxWidth = 0;

		for (int i = 0; i < rowNames.size(); ++i) {
			JLabel l = Templates.makeLabel(rowNames.get(i).toString());
			l.setHorizontalAlignment(JLabel.RIGHT);
		//	l.setText(rowNames.get(i).toString());
			maxWidth = Math.max(maxWidth, l.getPreferredSize().width);
		}
		column.setPreferredWidth(maxWidth + 10);
		return rowTable;
	}

	public static class DarkerBackgroundPanel extends JPanel
	{
		public DarkerBackgroundPanel()
		{
			setBackground(Templates.DARKER_BACKGROUND_COLOR);
		}
	}

	public static class VerticalTextTableCellRenderer extends DefaultTableCellRenderer
	{
		protected Color headerColor = Templates.DARKER_BACKGROUND_COLOR;
		protected Color separatorColor;
		protected boolean drawSeparator = false;

		public VerticalTextTableCellRenderer()
		{
			setVerticalAlignment(SwingConstants.BOTTOM);
			separatorColor = new Color(headerColor.getRed() - 25,
			                           headerColor.getBlue() - 25,
			                           headerColor.getGreen() - 25);
			setBackground(headerColor);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			// draw separator on left and right sides
			// if clipbounds intersects with header bounds
			Rectangle headerBounds = getBounds(); // absolute
			Rectangle separatorLeft = new Rectangle(0, // relative to header
			                                        headerBounds.y, 1, headerBounds.height);
			Rectangle separatorRight = new Rectangle(headerBounds.width - 1, // relative to header
			                                         headerBounds.y, 1, headerBounds.height);
			Rectangle clipBounds = g.getClipBounds(); // relative to header
			Rectangle dirtySeparatorLeft = separatorLeft.intersection(clipBounds);
			if (dirtySeparatorLeft.width > 0 && dirtySeparatorLeft.height > 0) {
				g.setColor(separatorColor);
				g.fillRect(dirtySeparatorLeft.x, dirtySeparatorLeft.y,
				           dirtySeparatorLeft.width, dirtySeparatorLeft.height);
			}
			Rectangle dirtySeparatorRight = separatorRight.intersection(clipBounds);
			if (dirtySeparatorRight.width > 0 && dirtySeparatorRight.height > 0) {
				g.setColor(separatorColor);
				g.fillRect(dirtySeparatorRight.x, dirtySeparatorRight.y,
				           dirtySeparatorRight.width, dirtySeparatorRight.height);
			}
		}

		protected void setValue(Object value)
		{
			VTextIcon textIcon = new VTextIcon(this, (value == null) ? "" : value.toString(), VTextIcon.ROTATE_LEFT);
			setIcon(textIcon);
		}
	}

	public static class RowHeaderTableCellRenderer extends DefaultTableCellRenderer
	{
		protected Color headerColor = Templates.DARKER_BACKGROUND_COLOR;
		protected Color separatorColor;

		public RowHeaderTableCellRenderer()
		{
			setHorizontalAlignment(SwingConstants.RIGHT);
			separatorColor = new Color(headerColor.getRed() - 25,
			                           headerColor.getBlue() - 25,
			                           headerColor.getGreen() - 25);
			setBackground(headerColor);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int column)
		{
			if (isSelected) {
				super.setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				super.setForeground(table.getForeground());
				if (row % 2 == 0) {
					super.setBackground(headerColor);
				} else {
					super.setBackground(Color.white);
				}
			}

			setFont(table.getFont());

			/*if (hasFocus) {
			setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
			} else {
			setBorder(noFocusBorder);
			}*/
			setValue(value);
			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
			setOpaque(!colorMatch);
			// ---- end optimization to aviod painting background ----

			return this;
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			// draw separator on top and bottom sides
			// if clipbounds intersects with header bounds
			Rectangle headerBounds = getBounds(); // absolute
			Rectangle separatorTop = new Rectangle(0, 0, // relative to header
			                                       headerBounds.width, 1);
			Rectangle separatorBottom = new Rectangle(0, headerBounds.height - 1, // relative to header
			                                          headerBounds.width, 1);
			Rectangle clipBounds = g.getClipBounds(); // relative to header
			Rectangle dirtySeparatorTop = separatorTop.intersection(clipBounds);
			if (dirtySeparatorTop.width > 0 && dirtySeparatorTop.height > 0) {
				g.setColor(separatorColor);
				g.fillRect(dirtySeparatorTop.x, dirtySeparatorTop.y,
				           dirtySeparatorTop.width, dirtySeparatorTop.height);
			}
			Rectangle dirtySeparatorBottom = separatorBottom.intersection(clipBounds);
			if (dirtySeparatorBottom.width > 0 && dirtySeparatorBottom.height > 0) {
				g.setColor(separatorColor);
				g.fillRect(dirtySeparatorBottom.x, dirtySeparatorBottom.y,
				           dirtySeparatorBottom.width, dirtySeparatorBottom.height);
			}
		}
	}

	public static class BooleanCheckBoxRenderer implements TableCellRenderer
	{
		protected Color headerColor;
		protected JPanel noCheckBoxPanel = new JPanel();
		protected JCheckBox checkBox;

		public BooleanCheckBoxRenderer()
		{
			checkBox = new JCheckBox();
			checkBox.setFocusPainted(false);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (headerColor == null) {
				Window w = SwingUtilities.windowForComponent(table);
				headerColor = w.getBackground();
			}
			boolean isRowColumnSame = ((DependencyTableModel) table.getModel()).isRowColumnSame(row, column);
			if (isRowColumnSame) {
				if (isSelected)
					noCheckBoxPanel.setBackground(table.getSelectionBackground());
				else
					noCheckBoxPanel.setBackground((row % 2 == 0) ? headerColor : Color.white);
				return noCheckBoxPanel;
			}

			if (isSelected) {
				try {
					if (table.getModel() instanceof CausalityInfoEditorDialog.DependencyEditorTableModel) {
						//CausalityInfoEditorDialog.DependencyEditorTableModel model;
						CausalityInfoEditorDialog.DependencyEditorTableModel model;
						model = (CausalityInfoEditorDialog.DependencyEditorTableModel) table.getModel();
						DependencyInfo x = model.getDependencyInfo();
						//x.validate(); // if invalid, throws exceptions
						checkBox.setForeground(table.getSelectionForeground());
						checkBox.setBackground(table.getSelectionBackground());
					}
				} catch (Exception ex) {
					if (ex instanceof LoopException) {
						checkBox.setFont(table.getFont());
						table.setValueAt(new Boolean(false), row, column);
						return checkBox;
					}
				}
			} else {
				checkBox.setForeground(table.getForeground());
				checkBox.setBackground((row % 2 == 0) ? headerColor : Color.white);
			}
			checkBox.setFont(table.getFont());
			if (!isRowColumnSame)
				checkBox.setSelected((value != null && ((Boolean) value).booleanValue()));
			// ---- begin optimization to avoid painting background ----
			Color back = checkBox.getBackground();
			boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
			checkBox.setOpaque(!colorMatch);
			// ---- end optimization to aviod painting background ----
			return checkBox;
		}
	}

	public static class BooleanCheckBoxEditor extends DefaultCellEditor
	{
		protected Color headerColor;
		protected JPanel noCheckBoxPanel = new JPanel();
		protected JCheckBox checkBox;

		public BooleanCheckBoxEditor()
		{
			super(new JCheckBox());
			checkBox = (JCheckBox) editorComponent;
			checkBox.setFocusPainted(false);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			if (headerColor == null) {
				Window w = SwingUtilities.windowForComponent(table);
				headerColor = w.getBackground();
			}
			boolean isRowColumnSame = ((DependencyTableModel) table.getModel()).isRowColumnSame(row, column);
			if (isRowColumnSame) {
				if (isSelected)
					noCheckBoxPanel.setBackground(table.getSelectionBackground());
				else
					noCheckBoxPanel.setBackground((row % 2 == 0) ? headerColor : Color.white);
				return noCheckBoxPanel;
			}

			if (isSelected) {
				checkBox.setForeground(table.getSelectionForeground());
				checkBox.setBackground(table.getSelectionBackground());
			} else {
				checkBox.setForeground(table.getForeground());
				checkBox.setBackground((row % 2 == 0) ? headerColor : Color.white);
			}
			checkBox.setFont(table.getFont());
			delegate.setValue(value);
			return checkBox;
		}
	}

	static class RowHeaderModel extends AbstractTableModel
	{
		protected List rowHeaders;

		public RowHeaderModel(List rowHeaders)
		{
			this.rowHeaders = rowHeaders;
		}

		public int getRowCount()
		{
			return rowHeaders.size();
		}

		public int getColumnCount()
		{
			return 1;
		}

		public Object getValueAt(int row, int column)
		{
			return rowHeaders.get(row);
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public String getColumnName(int column)
		{
			return "";
		}

	}

	public interface DependencyTableModel
	{
		public boolean isRowColumnSame(int row, int column);
	}

}
