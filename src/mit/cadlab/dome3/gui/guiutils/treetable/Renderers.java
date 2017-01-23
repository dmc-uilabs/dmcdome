// Renderers.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.GenericObjectTreeNode;
import mit.cadlab.dome3.gui.objectmodel.project.run.ProjectRunTreeNode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.tool.VariableParameterTreeObject;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.tool.ObjectiveParameterTreeObject;
import mit.cadlab.dome3.gui.playspace.run.PlayspaceRunTreeNode;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.swing.Templates;
import com.sun.java.ObjectTreeTable;
import mit.cadlab.dome3.util.FormatUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.text.DateFormat;

/**
 * Collection of TableCellRenderers.
 */
public class Renderers
{

	/**
	 * Creates default cell renderers for objects, numbers, doubles, dates,
	 * booleans, and icons.
	 * @see javax.swing.table.DefaultTableCellRenderer
	 *
	 */
	/*
	protected void createDefaultRenderers() {
	  defaultRenderersByColumnClass = new UIDefaults();

	  // Objects
	  setLazyRenderer(Object.class, "javax.swing.table.DefaultTableCellRenderer");

	  // Numbers
	  setLazyRenderer(Number.class, "javax.swing.JTable$NumberRenderer");

	  // Doubles and Floats
	  setLazyRenderer(Float.class, "javax.swing.JTable$DoubleRenderer");
	  setLazyRenderer(Double.class, "javax.swing.JTable$DoubleRenderer");

	  // Dates
	  setLazyRenderer(Date.class, "javax.swing.JTable$DateRenderer");

	  // Icons and ImageIcons
	  setLazyRenderer(Icon.class, "javax.swing.JTable$IconRenderer");
	  setLazyRenderer(ImageIcon.class, "javax.swing.JTable$IconRenderer");

	  // Booleans
	  setLazyRenderer(Boolean.class, "javax.swing.JTable$BooleanRenderer");
	}
	*/

	public static Color getParameterBackgroundColor(JTable table, int row)
	{
		TreeNode node = (TreeNode) ((ObjectTreeTable) table).getTree().getPathForRow(row).getLastPathComponent();
		Object obj = null;
		if (node instanceof DomeObjectTreeNode) {
			obj = ((DomeObjectTreeNode) node).getDomeObject();
		}
		else if (node instanceof PlayspaceRunTreeNode) {
			obj = ((PlayspaceRunTreeNode) node).getTreeNodeObject();
		}
		else if (node instanceof ProjectRunTreeNode) {
			obj = ((ProjectRunTreeNode) node).getTreeNodeObject();
		}
        else if (node instanceof GenericObjectTreeNode)
        {
            obj = ((GenericObjectTreeNode) node).getTreeObject();
            if (obj instanceof VariableParameterTreeObject)
            {
                obj = ((VariableParameterTreeObject) obj).getDomeObject();
            }
            else if (obj instanceof ObjectiveParameterTreeObject)
            {
                obj = ((ObjectiveParameterTreeObject) obj).getDomeObject();
            }
        }
		if (obj instanceof Parameter) {
				String valueStatus = ((Parameter) obj).getValueStatus();
				if (Parameter.VALUE_STATUS_STALE.equals(valueStatus))
					return Templates.STALE_COLOR;
				else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(valueStatus))
					return Templates.INCONSISTENT_COLOR;
				else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(valueStatus))
					return Templates.WAITING_VALIDATION_COLOR;
				else if (Parameter.VALUE_STATUS_CONSISTENT.equals(valueStatus))
					return Templates.CONSISTENT_COLOR;
		}
		return table.getBackground();
	}

	public static class DateRenderer extends DefaultTableCellRenderer
	{
		DateFormat formatter;

		public DateRenderer()
		{
			super();
		}

		public void setValue(Object value)
		{
			if (formatter == null) {
				formatter = DateFormat.getDateInstance();
			}
			setText((value == null) ? "" : formatter.format(value));
		}
	}

	public static class IconRenderer extends DefaultTableCellRenderer
	{
		public IconRenderer()
		{
			super();
			setHorizontalAlignment(JLabel.CENTER);
		}

		public void setValue(Object value)
		{
			setIcon((value instanceof Icon) ? (Icon) value : null);
		}
	}

	public static class BooleanComboBoxRenderer extends JComboBox implements TableCellRenderer
	{
		protected static Object[] values = {Boolean.TRUE, Boolean.FALSE};

		public BooleanComboBoxRenderer()
		{
			super(values);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			//if (isSelected) {
			//setForeground(table.getSelectionForeground());
			//setBackground(table.getSelectionBackground());
			//}
			//else {
			setForeground(table.getForeground());
			setBackground(getParameterBackgroundColor(table, row));
			//}
			setSelectedItem(value);
			return this;
		}
	}

	public static class NothingRenderer extends DefaultTableCellRenderer
	{
		public NothingRenderer()
		{
			super();
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			return super.getTableCellRendererComponent(table, value, false, false, row, column);
		}
	}

	public static class DefaultRenderer extends DefaultTableCellRenderer
	{
		public DefaultRenderer()
		{
			super();
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{

			super.setForeground(table.getForeground());
			super.setBackground(getParameterBackgroundColor(table, row));

			setFont(table.getFont());

			if (hasFocus || isSelected) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			} else {
				setBorder(noFocusBorder);
			}

			setValue(value);

			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
			setOpaque(!colorMatch);
			// ---- end optimization to aviod painting background ----

			return this;
		}
	}

	public static class NumberRenderer extends JPanel implements TableCellRenderer
	{
		// don't use in tables that allow multiple selection!
		protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
		protected JLabel magnitudeLabel = new JLabel();
		protected JLabel unitLabel = new JLabel();

		public NumberRenderer()
		{
			setLayout(new GridLayout(1, 2));
			add(magnitudeLabel);
			add(unitLabel);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{

			magnitudeLabel.setFont(table.getFont());
			unitLabel.setFont(table.getFont());

			if (hasFocus || isSelected) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			} else {
				setBorder(noFocusBorder);
			}
			super.setForeground(table.getForeground());
			super.setBackground(getParameterBackgroundColor(table, row));

			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
			setOpaque(!colorMatch);
			// ---- end optimization to aviod painting background ----
			return this;
		}
	}

	public static class DomeRealValueRenderer extends NumberRenderer
	{
		public DomeRealValueRenderer()
		{
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			//magnitudeLabel.setText(((DomeReal) value).getRealValue().toString());
            magnitudeLabel.setText(FormatUtils.formatSigFig(((DomeReal) value).getValue()));
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeReal) value).getUnit().toString()));
			return super.getTableCellRendererComponent(table, value,
			                                           isSelected, hasFocus,
			                                           row, column);
		}
	}

	public static class DomeIntegerValueRenderer extends NumberRenderer
	{
		public DomeIntegerValueRenderer()
		{
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			magnitudeLabel.setText(((DomeInteger) value).getIntegerValue().toString());
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeInteger) value).getUnit().toString()));
			return super.getTableCellRendererComponent(table, value,
			                                           isSelected, hasFocus,
			                                           row, column);
		}
	}

	public static class DomeStringValueRenderer extends DefaultRenderer
	{
		public DomeStringValueRenderer()
		{
		}

		protected void setValue(Object value)
		{
			setText(((DomeString) value).getValue());
		}
	}

	//Qing-- add for file datatype
	public static class DomeFileValueRenderer extends DefaultRenderer
	{

		public DomeFileValueRenderer()
		{
		}

		protected void setValue(Object value)
		{
			setText(((DomeFile) value).getFilePath());
		}
	}

	public static class DomeVectorValueRenderer extends NumberRenderer
	{
		public DomeVectorValueRenderer()
		{
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			magnitudeLabel.setText((new Integer(((DomeVector) value).getSize())).toString() + " elements");
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeVector) value).getUnit().toString()));
			return super.getTableCellRendererComponent(table, value,
			                                           isSelected, hasFocus,
			                                           row, column);
		}
	}


	public static class DomeMatrixRenderer extends NumberRenderer
	{
		public DomeMatrixRenderer()
		{
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			magnitudeLabel.setText((new Integer(((DomeMatrix) value).getRowCount())).toString() + " x " + (new Integer(((DomeMatrix) value).getColumnCount())).toString());
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeMatrix) value).getUnit().toString()));
			return super.getTableCellRendererComponent(table, value,
			                                           isSelected, hasFocus,
			                                           row, column);
		}
	}
	public static class DomePreferenceRenderer extends NumberRenderer {
		public DomePreferenceRenderer() {
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column) {
			magnitudeLabel.setText((new Integer(((DomePreference) value).getRowCount())).toString() + " x " + (new Integer(((DomePreference) value).getColumnCount())).toString());
			unitLabel.setText(UnitAtom.getUnitDescription(((DomePreference) value).getUnit().toString()));
			return super.getTableCellRendererComponent(table, value,
			        isSelected, hasFocus,
			        row, column);
		}
	}
	public static class DomeEnumerationRenderer extends JPanel implements TableCellRenderer
	{
		protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
		protected JLabel magnitudeLabel = new JLabel();

		public DomeEnumerationRenderer()
		{
			setLayout(new GridLayout(1, 1));
			add(magnitudeLabel);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (!(((EnumerationData) value).getLastSelection() == -1)) {
				magnitudeLabel.setText(((EnumerationData) value).getElementName(((EnumerationData) value).getLastSelection()).toString());

			} else {
				magnitudeLabel.setText("");

			}

			magnitudeLabel.setFont(table.getFont());

			if (hasFocus || isSelected) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			} else {
				setBorder(noFocusBorder);
			}
			super.setForeground(table.getForeground());
			super.setBackground(getParameterBackgroundColor(table, row));

			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
			setOpaque(!colorMatch);
			// ---- end optimization to aviod painting background ----
			return this;
		}

	}

	public static class DomeListValueRenderer extends NumberRenderer
	{
		public DomeListValueRenderer()
		{
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column)
		{
			magnitudeLabel.setText((new Integer(((DomeList) value).getSize())).toString() + " elements");
			//unitLabel.setText(UnitAtom.getUnitDescription(((DomeVector) value).getUnit().toString()));
			return super.getTableCellRendererComponent(table, value,
			                                           isSelected, hasFocus,
			                                           row, column);
		}
	}

    public static class BooleanCheckBoxRenderer extends DefaultRenderer
	{
        private JCheckBox _checkBox;

		public BooleanCheckBoxRenderer()
		{
            this._checkBox = Templates.makeCheckBox();
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column)
        {
			this._checkBox.setHorizontalAlignment(JCheckBox.CENTER);
            this._checkBox.setBackground(Color.WHITE);
			this._checkBox.setFocusPainted(false);
            this._checkBox.setSelected(((Boolean)value).booleanValue());
            return this._checkBox;
        }
	}

	public static class BooleanCheckBoxLeftRenderer extends DefaultRenderer {
		private JCheckBox _checkBox;

		public BooleanCheckBoxLeftRenderer() {
			this._checkBox = Templates.makeCheckBox();
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		                                               boolean hasFocus, int row, int column) {
			this._checkBox.setHorizontalAlignment(JCheckBox.LEFT);
			this._checkBox.setBackground(Color.WHITE);
			this._checkBox.setFocusPainted(false);
			this._checkBox.setSelected(((Boolean) value).booleanValue());
			return this._checkBox;
		}
	}

	/*
	public static class DomeMatrixRenderer extends JPanel implements TableCellRenderer {
			// don't use in tables that allow multiple selection!
			protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			protected JLabel rowLabel = new JLabel();
		    protected JLabel columnLabel= new JLabel();
			protected JLabel unitLabel = new JLabel();

			public DomeMatrixRenderer() {
				setLayout(new GridLayout(1, 3));
				add(rowLabel);
				add(columnLabel);
				add(unitLabel);
			}

			public Component getTableCellRendererComponent(JTable table, Object value,
														   boolean isSelected, boolean hasFocus, int row, int column) {

				System.out.println(value.toString());
				rowLabel.setFont(table.getFont());
				columnLabel.setFont(table.getFont());
				unitLabel.setFont(table.getFont());

				rowLabel.setText(String.valueOf(((DomeMatrix)value).getRowCount()));
				columnLabel.setText(String.valueOf(((DomeMatrix)value).getColumnCount()));
				unitLabel.setText(((DomeMatrix) value).getUnit().toString());

				if (hasFocus || isSelected) {
					setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				} else {
					setBorder(noFocusBorder);
				}
				super.setForeground(table.getForeground());
				super.setBackground(table.getBackground());

				// ---- begin optimization to avoid painting background ----
				Color back = getBackground();
				boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
				setOpaque(!colorMatch);
				// ---- end optimization to aviod painting background ----
				return this;
			}
		}
*/
}
