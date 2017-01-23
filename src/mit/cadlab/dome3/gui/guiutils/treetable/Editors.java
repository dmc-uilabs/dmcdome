// Editors.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.units.UnitChooser;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.exceptions.BuildMappingValuePropagationException;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.GuiConstants;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.FormatUtils;
import mit.cadlab.dome3.util.UnitsException;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.EventObject;

/**
 * Collection of TableCellEditors.
 */
public class Editors
{

	/**
	 * Creates default cell editors for objects, numbers, and boolean values.
	 * @see DefaultCellEditor
	 */
	/*
	protected void createDefaultEditors() {
	  defaultEditorsByColumnClass = new UIDefaults();

	  // Objects
	  setLazyEditor(Object.class, "javax.swing.JTable$GenericEditor");

	  // Numbers
	  setLazyEditor(Number.class, "javax.swing.JTable$NumberEditor");

	  // Booleans
	  setLazyEditor(Boolean.class, "javax.swing.JTable$BooleanEditor");
	}
	*/

	public abstract static class NumberEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField magnitudeTextField = Templates.makeDTextField();
		protected DComboBox unitComboBox = makeComboBox();
		protected NumericQuantity data;
		protected Number magnitude;

		public NumberEditor()
		{
			editorPanel.setLayout(new GridLayout(1, 2));
			editorPanel.add(magnitudeTextField);
			editorPanel.add(unitComboBox);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
				}
			};
			magnitudeTextField.addActionListener(aListener);
		}

		protected DComboBox makeComboBox()
		{
			DComboBox cb = Templates.makeDComboBox();
			cb.setUI(new BasicComboBoxUI()
			{
				protected ComboPopup createPopup()
				{
					BasicComboPopup popup = new CustomBasicComboPopup(comboBox);
					popup.getAccessibleContext().setAccessibleParent(comboBox);
					return popup;
				}
			});
			cb.addActionListener(getComboBoxActionListener());
			return cb;
		}

		protected class CustomBasicComboPopup extends BasicComboPopup
		{
			public CustomBasicComboPopup(JComboBox combobox)
			{
				super(combobox);
			}

			protected MouseListener createMouseListener()
			{
				return new CustomInvocationMouseHandler();
			}

			protected class CustomInvocationMouseHandler
			        extends BasicComboPopup.InvocationMouseHandler
			{
				public void mousePressed(MouseEvent e)
				{
					if (magnitudeTextField.isStale()) {
						stopCellEditing(); // commit textfield
						editSelectedCell();
						((NumberEditor) table.getCellEditor()).unitComboBox.showPopup();
					} else {
						super.mousePressed(e);
					}
				}
			}
		}

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (NumericQuantity) value;
			setupTextField();
			setupComboBox();
		}

		protected abstract void setupTextField();

		protected void setupComboBox()
		{
			UnitComboBoxModel unitModel = new UnitComboBoxModel(data.getUnit(), false);
			unitComboBox.setModel(unitModel);
		}

		protected ActionListener getComboBoxActionListener()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setModelSelectedUnit();
				}
			};
		}

		protected void setModelSelectedUnit()
		{
			data.setUnit((Unit) unitComboBox.getSelectedItem());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return magnitude;
		}

		public boolean stopCellEditing()
		{
			String text = magnitudeTextField.getText().trim();
			if (text.equals("")) {
				cancelCellEditing();
                return false;
			} else {
				try {
					parseText(text);
					return super.stopCellEditing();
				} catch (UnitsException e) {
					OneButton1Msg.showError(null, "Unit assignment error",
					                        "Input and output units are incompatible",
					                        "ok", new Dimension());
					return false;
				} catch (BuildMappingValuePropagationException ex) {
					OneButton1Msg.showError(null, "Mapping value propagation error",
					                        ex.getMessage(),
					                        "ok", new Dimension());
					return false;
				} catch (Exception ex) {
					System.err.println("invalid data error: " + ex);
					return false;
				}
			}
		}

		protected abstract void parseText(String text);

	}

	public abstract static class NumberBuildEditor extends NumberEditor
	{
		public NumberBuildEditor()
		{
		};

		protected void setupComboBox()
		{
			UnitComboBoxModel unitModel = new UnitComboBoxModel(data.getUnit(), true);
			unitComboBox.setModel(unitModel);
		}

		protected ActionListener getComboBoxActionListener()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Object choice = unitComboBox.getSelectedItem();
					if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
						changeListAction();
					else
						setModelSelectedUnit();
				}
			};
		}

		protected void changeListAction()
		{
			Unit choice = UnitChooser.showDialog(editorPanel, data.getUnit());
			if (choice != null) {
				data.setUnit(choice);
			}
			fireEditingStopped();
		}

	}

	public abstract static class NumberRunEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField magnitudeTextField = Templates.makeDTextField();
		protected JLabel unitLabel = Templates.makeLabel();
		protected NumericQuantity data;
		protected Number magnitude;

		public NumberRunEditor()
		{
			editorPanel.setLayout(new GridLayout(1, 2));
			editorPanel.add(magnitudeTextField);
			editorPanel.add(unitLabel);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
				}
			};
			magnitudeTextField.addActionListener(aListener);
		}

		protected abstract void setupLabel();

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (NumericQuantity) value;
			setupTextField();
			setupLabel();
		}

		protected abstract void setupTextField();


		public Object getCellEditorValue()
		{
			return magnitude;
		}

		public boolean stopCellEditing()
		{
			String text = magnitudeTextField.getText().trim();
			if (text.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(text);
					return super.stopCellEditing();
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected abstract void parseText(String text);

	}

	public static class RealBuildEditor extends NumberBuildEditor
	{

		public RealBuildEditor()
		{
		}

		protected void setupTextField()
		{
			magnitudeTextField.setText(FormatUtils.formatSigFig(((DomeReal) data).getValue()));
            //magnitudeTextField.setText(((DomeReal) data).getRealValue().toString());
			magnitudeTextField.setCurrent();
		}

		protected void parseText(String text)
		{
			magnitude = new Double(text);
		}

	}

	public static class RealRunEditor extends NumberRunEditor
	{

		public RealRunEditor()
		{
		}

		protected void setupTextField()
		{
            magnitudeTextField.setText(FormatUtils.formatSigFig(((DomeReal) data).getValue()));
			//magnitudeTextField.setText(((DomeReal) data).getRealValue().toString());
			magnitudeTextField.setCurrent();
		}

		protected void parseText(String text)
		{
			magnitude = new Double(text);
		}

		protected void setupLabel()
		{
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeReal) data).getUnit().toString()));
		}

	}

	public static class IntegerBuildEditor extends NumberBuildEditor
	{

		public IntegerBuildEditor()
		{
		}

		protected void setupTextField()
		{
			magnitudeTextField.setText(((DomeInteger) data).getIntegerValue().toString());
			magnitudeTextField.setCurrent();
		}

		protected void parseText(String text)
		{
			magnitude = new Integer(text);
		}

	}

	public static class IntegerRunEditor extends NumberRunEditor
	{

		public IntegerRunEditor()
		{
		}

		protected void setupTextField()
		{
			magnitudeTextField.setText(((DomeInteger) data).getIntegerValue().toString());
			magnitudeTextField.setCurrent();
		}

		protected void parseText(String text)
		{
			magnitude = new Integer(text);
		}

		protected void setupLabel()
		{
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeInteger) data).getUnit().toString()));
		}

	}

	public static class BooleanCheckBoxEditor extends DefaultCellEditor
	{
		public BooleanCheckBoxEditor()
		{
			super(Templates.makeCheckBox());
			JCheckBox checkBox = (JCheckBox) getComponent();
			checkBox.setHorizontalAlignment(JCheckBox.CENTER);
			checkBox.setFocusPainted(false);
		}
	}

	public static class BooleanCheckBoxLeftEditor extends DefaultCellEditor {
		public BooleanCheckBoxLeftEditor() {
			super(Templates.makeCheckBox());
			JCheckBox checkBox = (JCheckBox) getComponent();
			checkBox.setHorizontalAlignment(JCheckBox.LEFT);
			checkBox.setFocusPainted(false);
		}
	}

	public static class BooleanComboBoxEditor extends DefaultCellEditor
	{
		protected static Object[] values = {Boolean.TRUE, Boolean.FALSE};

		public BooleanComboBoxEditor()
		{
			super(Templates.makeComboBox(values));
		}
	}

	public static class StringBuildEditor extends DefaultCellEditor
	{
		public StringBuildEditor()
		{
			super(Templates.makeDTextField());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			delegate.setValue(((DomeString) value).getValue());
			return editorComponent;
		}
	}

	/**
	 * Editor for the mapping column cell for parameters in a tree view.
	 * This editor captures double mouse click and displays the mapping tool panel.
	 */
	public static class MappingCellEditor extends DefaultCellEditor
	{
		Object m_object;

		/**
		 * Store the object that the editor is associated with.
		 * @param object Dome object.
		 */
		public MappingCellEditor(Object object)
		{
			super(Templates.makeDTextField());
			m_object = object;
		}

		/**
		 * Override of the javax.swing.DefaultCellEditor method. Used to capture
		 * and translateDefaultSubscriptions the mouse event for this editor.
		 * @param e Editor event.
		 * @return false indicating that the cell is not editable
		 */
		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) e;
				if (SwingUtilities.isLeftMouseButton(me) && me.getClickCount() == 1) {
					Parameter p = (Parameter) m_object;
					DomeGui gui = BuildMode.getCurrentModelFrame().getGui();
					if (gui instanceof DomeModelBuildPanel) {
						((DomeModelBuildPanel) gui).updateMappings(p);
					} else if (gui instanceof ProjectBuildPanel) {
						((ProjectBuildPanel) gui).updateMappings(p);
					}
				} else if (SwingUtilities.isLeftMouseButton(me) && me.getClickCount() == 2) {
					Parameter p = (Parameter) m_object;
					DomeGui gui = BuildMode.getCurrentModelFrame().getGui();
					if (gui instanceof DomeModelBuildPanel) {
						((DomeModelBuildPanel) gui).showMappings(p);
					} else if (gui instanceof ProjectBuildPanel) {
						((ProjectBuildPanel) gui).showMappings(p);
					}
				}
			}
			return false;
		}
	}

	//add for Vectors and Matrix stuff
	public static class vectorBuildEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField magnitudeTextField = Templates.makeDTextField();
		protected DComboBox unitComboBox = makeComboBox();
		protected DomeVector data;
		protected Integer size;

		public vectorBuildEditor()
		{

			editorPanel.setLayout(new GridLayout(1, 2));
			editorPanel.add(magnitudeTextField);
			editorPanel.add(unitComboBox);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
					setModelSize();
				}
			};
			magnitudeTextField.addActionListener(aListener);
		}

		protected DComboBox makeComboBox()
		{
			DComboBox cb = Templates.makeDComboBox();
			cb.setUI(new BasicComboBoxUI()
			{
				protected ComboPopup createPopup()
				{
					BasicComboPopup popup = new CustomBasicComboPopup(comboBox);
					popup.getAccessibleContext().setAccessibleParent(comboBox);
					return popup;
				}
			});
			cb.addActionListener(getComboBoxActionListener());
			return cb;
		}

		protected class CustomBasicComboPopup extends BasicComboPopup
		{
			public CustomBasicComboPopup(JComboBox combobox)
			{
				super(combobox);
			}

			protected MouseListener createMouseListener()
			{
				return new CustomInvocationMouseHandler();
			}

			protected class CustomInvocationMouseHandler
			        extends BasicComboPopup.InvocationMouseHandler
			{
				public void mousePressed(MouseEvent e)
				{
					if (magnitudeTextField.isStale()) {
						stopCellEditing(); // commit textfield
						editSelectedCell();
						((vectorBuildEditor) table.getCellEditor()).unitComboBox.showPopup();
					} else {
						super.mousePressed(e);
					}
				}
			}
		}

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (DomeVector) value;
			setupTextField();
			setupComboBox();
		}

		protected void setupTextField()
		{
			magnitudeTextField.setText(new Integer((data).getSize()).toString());
			magnitudeTextField.setCurrent();

		}

		protected void setupComboBox()
		{
			UnitComboBoxModel unitModel = new UnitComboBoxModel(data.getUnit(), true);
			unitComboBox.setModel(unitModel);
		}


		protected ActionListener getComboBoxActionListener()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Object choice = unitComboBox.getSelectedItem();
					if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
						changeListAction();
					else
						setModelSelectedUnit();
				}
			};
		}

		protected void changeListAction()
		{
			Unit choice = UnitChooser.showDialog(editorPanel, data.getUnit());
			if (choice != null) {
				data.setUnit(choice);
			}
			fireEditingStopped();
		}

		protected void setModelSelectedUnit()
		{
			data.setUnit((Unit) unitComboBox.getSelectedItem());
			fireEditingStopped();
		}

		protected void setModelSize()
		{
			data.setSize(size.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return data;
		}

		public boolean stopCellEditing()
		{
			String text = magnitudeTextField.getText().trim();
			if (text.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(text);
					return super.stopCellEditing();
				}
				catch (BuildMappingValuePropagationException ex) {
					OneButton1Msg.showError(null, "Mapping value propagation error",
					                        ex.getMessage(),
					                        "ok", new Dimension());
					return false;
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String text)
		{
			size = new Integer(text);
		}

        /**
         * sangmok
         * Clear reference to data object
         * should be called whenever the parameter editing frame window closes
         * also should be called whenever the parameter is set (when actionPerformed)
         */
        public void clearDataObject() {
            data = null;
        }

	}

	//TODO for fixed size vector magnitude should be label instead of uneditable text field?
	public static class VectorRunEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField magnitudeTextField = Templates.makeDTextField();
		protected JLabel unitLabel = Templates.makeLabel();
		protected DomeVector data;
		protected Integer size;

		public VectorRunEditor()
		{

			editorPanel.setLayout(new GridLayout(1, 2));
			editorPanel.add(magnitudeTextField);
			editorPanel.add(unitLabel);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
					setModelSize();
				}
			};
			magnitudeTextField.addActionListener(aListener);
		}

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (DomeVector) value;
			setupTextField();
			setupLabel();
		}

		protected void setupTextField()
		{
			magnitudeTextField.setText(new Integer((data).getSize()).toString());
			magnitudeTextField.setCurrent();
			if (data.isFixedSize()) {
				magnitudeTextField.setEditable(false);
			}

		}

		protected void setupLabel()
		{
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeVector) data).getUnit().toString()));
		}

		protected void setModelSize()
		{
			data.setSize(size.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return data;
		}

		public boolean stopCellEditing()
		{
			String text = magnitudeTextField.getText().trim();
			if (text.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(text);
					return super.stopCellEditing();
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String text)
		{
			size = new Integer(text);
		}

	}

	public static class matrixBuildEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField rowTextField = Templates.makeDTextField();
		protected DTextField columnTextField = Templates.makeDTextField();
		protected DComboBox unitComboBox = makeComboBox();
		protected DomeMatrix data;
		protected Integer rowsize;
		protected Integer colsize;

		public matrixBuildEditor()
		{

			editorPanel.setLayout(new GridLayout(1, 3));
			editorPanel.add(rowTextField);
			editorPanel.add(columnTextField);
			editorPanel.add(unitComboBox);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
                    stopCellEditing();
					setModelSize();
				}
			};
			rowTextField.addActionListener(aListener);
			columnTextField.addActionListener(aListener);
		}

		protected DComboBox makeComboBox()
		{
			DComboBox cb = Templates.makeDComboBox();
			cb.setUI(new BasicComboBoxUI()
			{
				protected ComboPopup createPopup()
				{
					BasicComboPopup popup = new CustomBasicComboPopup(comboBox);
					popup.getAccessibleContext().setAccessibleParent(comboBox);
					return popup;
				}
			});
			cb.addActionListener(getComboBoxActionListener());
			return cb;
		}

		protected class CustomBasicComboPopup extends BasicComboPopup
		{
			public CustomBasicComboPopup(JComboBox combobox)
			{
				super(combobox);
			}

			protected MouseListener createMouseListener()
			{
				return new CustomInvocationMouseHandler();
			}

			protected class CustomInvocationMouseHandler extends BasicComboPopup.InvocationMouseHandler
			{
                public void mousePressed(MouseEvent e)
				{
					if (rowTextField.isStale() || columnTextField.isStale()) {
						stopCellEditing(); // commit textfield
						editSelectedCell();
						((vectorBuildEditor) table.getCellEditor()).unitComboBox.showPopup();
					} else {
						super.mousePressed(e);
					}
				}
			}
		}

		protected void editSelectedCell()
		{
			if (table != null) {
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
            }
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (DomeMatrix) value;
			setupTextField();
			setupComboBox();
		}

		protected void setupTextField()
		{
			rowTextField.setText(new Integer((data).getRowCount()).toString());
			rowTextField.setCurrent();
			columnTextField.setText(new Integer((data).getColumnCount()).toString());
			columnTextField.setCurrent();
			if (rowTextField.getFont() != GuiConstants.FONT11)
				rowTextField.setFont(GuiConstants.FONT11);
			if (columnTextField.getFont() != GuiConstants.FONT11)
				columnTextField.setFont(GuiConstants.FONT11);
		}

		protected void setupComboBox()
		{
			UnitComboBoxModel unitModel = new UnitComboBoxModel(data.getUnit(), true);
			unitComboBox.setModel(unitModel);
		}


		protected ActionListener getComboBoxActionListener()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Object choice = unitComboBox.getSelectedItem();
					if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
						changeListAction();
					else
						setModelSelectedUnit();
				}
			};
		}

		protected void changeListAction()
		{
			Unit choice = UnitChooser.showDialog(editorPanel, data.getUnit());
			if (choice != null) {
				data.setUnit(choice);
			}
			fireEditingStopped();
		}

		protected void setModelSelectedUnit()
		{
			data.setUnit((Unit) unitComboBox.getSelectedItem());
			fireEditingStopped();
		}

		protected void setModelSize()
		{
			data.setRowCount(rowsize.intValue());
			data.setColumnCount(colsize.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return data;
		}

		public boolean stopCellEditing()
		{

			String rowtext = rowTextField.getText().trim();
			String coltext = columnTextField.getText().trim();
			if (rowtext.equals("") || coltext.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(rowtext, coltext);
					return super.stopCellEditing();
				}
				catch (BuildMappingValuePropagationException ex) {
					OneButton1Msg.showError(null, "Mapping value propagation error",
					                        ex.getMessage(),
					                        "ok", new Dimension());
					return false;
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String rowtext, String coltext)
		{
			rowsize = new Integer(rowtext);
			colsize = new Integer(coltext);
			if (rowsize.intValue() == 0 && colsize.intValue() != 0)
				rowsize = new Integer(1);
			else if (rowsize.intValue() != 0 && colsize.intValue() == 0)
				colsize = new Integer(1);
		}

        /**
         * sangmok
         * Clear reference to data object
         * should be called whenever the parameter editing frame window closes
         * also should be called whenever the parameter is set (when actionPerformed)
         */
        public void clearDataObject() {
            data = null;
        }
	}
//////////////////////////////////////////////////////////////////////////////////////////////////

	public static class PreferenceBuildEditor extends AbstractCellEditor
	        implements TableCellEditor {

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField rowTextField = Templates.makeDTextField();
		protected DTextField columnTextField = Templates.makeDTextField();
		protected DComboBox unitComboBox = makeComboBox();
		protected DomePreference data;
		protected Integer rowsize;
		protected Integer colsize;


		public PreferenceBuildEditor() {

			editorPanel.setLayout(new GridLayout(1, 3));
			editorPanel.add(rowTextField);
			editorPanel.add(columnTextField);
			editorPanel.add(unitComboBox);
			ActionListener aListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					stopCellEditing();
					setModelSize();
				}
			};
			rowTextField.addActionListener(aListener);
			columnTextField.addActionListener(aListener);
		}

		protected DComboBox makeComboBox() {
			DComboBox cb = Templates.makeDComboBox();
			cb.setUI(new BasicComboBoxUI() {
				protected ComboPopup createPopup() {
					BasicComboPopup popup = new CustomBasicComboPopup(comboBox);
					popup.getAccessibleContext().setAccessibleParent(comboBox);
					return popup;
				}
			});
			cb.addActionListener(getComboBoxActionListener());
			return cb;
		}

		protected class CustomBasicComboPopup extends BasicComboPopup {
			public CustomBasicComboPopup(JComboBox combobox) {
				super(combobox);
			}

			protected MouseListener createMouseListener() {
				return new CustomInvocationMouseHandler();
			}

			protected class CustomInvocationMouseHandler
			        extends BasicComboPopup.InvocationMouseHandler {
				public void mousePressed(MouseEvent e) {
					if (rowTextField.isStale() || columnTextField.isStale()) {
						stopCellEditing(); // commit textfield
						editSelectedCell();
						((vectorBuildEditor) table.getCellEditor()).unitComboBox.showPopup();
					} else {
						super.mousePressed(e);
					}
				}
			}
		}

		protected void editSelectedCell() {
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column) {
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value) {
			data = (DomePreference) value;
			setupTextField();
			setupComboBox();
		}

		protected void setupTextField() {
			rowTextField.setText(new Integer((data).getRowCount()).toString());
			rowTextField.setCurrent();
			columnTextField.setText(new Integer((data).getColumnCount()).toString());
			columnTextField.setCurrent();
			if (rowTextField.getFont() != GuiConstants.FONT11)
				rowTextField.setFont(GuiConstants.FONT11);
			if (columnTextField.getFont() != GuiConstants.FONT11)
				columnTextField.setFont(GuiConstants.FONT11);
		}

		protected void setupComboBox() {
			UnitComboBoxModel unitModel = new UnitComboBoxModel(data.getUnit(), true);
			unitComboBox.setModel(unitModel);
		}


		protected ActionListener getComboBoxActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object choice = unitComboBox.getSelectedItem();
					if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
						changeListAction();
					else
						setModelSelectedUnit();
				}
			};
		}

		protected void changeListAction() {
			Unit choice = UnitChooser.showDialog(editorPanel, data.getUnit());
			if (choice != null) {
				data.setUnit(choice);
			}
			fireEditingStopped();
		}

		protected void setModelSelectedUnit() {
			data.setUnit((Unit) unitComboBox.getSelectedItem());
			fireEditingStopped();
		}

		protected void setModelSize() {
			data.setRowCount(rowsize.intValue());
			data.setColumnCount(colsize.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue() {
			return data;
		}

		public boolean stopCellEditing() {

			String rowtext = rowTextField.getText().trim();
			String coltext = columnTextField.getText().trim();
			if (rowtext.equals("") || coltext.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(rowtext, coltext);
					return super.stopCellEditing();
				} catch (BuildMappingValuePropagationException ex) {
					OneButton1Msg.showError(null, "Mapping value propagation error",
					        ex.getMessage(),
					        "ok", new Dimension());
					return false;
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String rowtext, String coltext) {
			rowsize = new Integer(rowtext);
			colsize = new Integer(coltext);
			if (rowsize.intValue() == 0 && colsize.intValue() != 0)
				rowsize = new Integer(1);
			else if (rowsize.intValue() != 0 && colsize.intValue() == 0)
				colsize = new Integer(1);
		}

        /**
         * sangmok
         * Clear reference to data object
         * should be called whenever the parameter editing frame window closes
         * also should be called whenever the parameter is set (when actionPerformed)
         */
        public void clearDataObject() {
            data = null;
        }

	}



///////////////////////////////////////////////////////////////////////////////////////////////////

	//TODO for fixed size matrix row and col should be labels instead of uneditable text fields?
	public static class MatrixRunEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField rowTextField = Templates.makeDTextField();
		protected DTextField columnTextField = Templates.makeDTextField();
		protected JLabel unitLabel = Templates.makeLabel();
		protected DomeMatrix data;
		protected Integer rowsize;
		protected Integer colsize;


		public MatrixRunEditor()
		{

			editorPanel.setLayout(new GridLayout(1, 3));
			editorPanel.add(rowTextField);
			editorPanel.add(columnTextField);
			editorPanel.add(unitLabel);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
					setModelSize();
				}
			};
			rowTextField.addActionListener(aListener);
			columnTextField.addActionListener(aListener);
		}

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (DomeMatrix) value;
			setupTextField();
			setupLabel();
		}

		protected void setupTextField()
		{
			rowTextField.setText(new Integer((data).getRowCount()).toString());
			rowTextField.setCurrent();
			columnTextField.setText(new Integer((data).getColumnCount()).toString());
			columnTextField.setCurrent();
			if (rowTextField.getFont() != GuiConstants.FONT11)
				rowTextField.setFont(GuiConstants.FONT11);
			if (columnTextField.getFont() != GuiConstants.FONT11)
				columnTextField.setFont(GuiConstants.FONT11);
			if (data.isFixedSize()) {
				rowTextField.setEditable(false);
				columnTextField.setEditable(false);
			}
		}

		protected void setupLabel()
		{
			unitLabel.setText(UnitAtom.getUnitDescription(((DomeMatrix) data).getUnit().toString()));
		}

		protected void setModelSize()
		{
			data.setRowCount(rowsize.intValue());
			data.setColumnCount(colsize.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return data;
		}

		public boolean stopCellEditing()
		{

			String rowtext = rowTextField.getText().trim();
			String coltext = columnTextField.getText().trim();
			if (rowtext.equals("") || coltext.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(rowtext, coltext);
					return super.stopCellEditing();
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String rowtext, String coltext)
		{
			rowsize = new Integer(rowtext);
			colsize = new Integer(coltext);
			if (rowsize.intValue() == 0 && colsize.intValue() != 0)
				rowsize = new Integer(1);
			else if (rowsize.intValue() != 0 && colsize.intValue() == 0)
				colsize = new Integer(1);
		}

	}
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	public static class PreferenceRunEditor extends AbstractCellEditor
	        implements TableCellEditor {

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField rowTextField = Templates.makeDTextField();
		protected DTextField columnTextField = Templates.makeDTextField();
		protected JLabel unitLabel = Templates.makeLabel();
		protected DomePreference data;
		protected Integer rowsize;
		protected Integer colsize;


		public PreferenceRunEditor() {

			editorPanel.setLayout(new GridLayout(1, 3));
			editorPanel.add(rowTextField);
			editorPanel.add(columnTextField);
			editorPanel.add(unitLabel);
			ActionListener aListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					stopCellEditing();
					setModelSize();
				}
			};
			rowTextField.addActionListener(aListener);
			columnTextField.addActionListener(aListener);
		}

		protected void editSelectedCell() {
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column) {
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value) {
			data = (DomePreference) value;
			setupTextField();
			setupLabel();
		}

		protected void setupTextField() {
			rowTextField.setText(new Integer((data).getRowCount()).toString());
			rowTextField.setCurrent();
			columnTextField.setText(new Integer((data).getColumnCount()).toString());
			columnTextField.setCurrent();
			if (rowTextField.getFont() != GuiConstants.FONT11)
				rowTextField.setFont(GuiConstants.FONT11);
			if (columnTextField.getFont() != GuiConstants.FONT11)
				columnTextField.setFont(GuiConstants.FONT11);
			if (data.isFixedSize()) {
				rowTextField.setEditable(false);
				columnTextField.setEditable(false);
			}
		}

		protected void setupLabel() {
			unitLabel.setText(UnitAtom.getUnitDescription(((DomePreference) data).getUnit().toString()));
		}

		protected void setModelSize() {
			data.setRowCount(rowsize.intValue());
			data.setColumnCount(colsize.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue() {
			return data;
		}

		public boolean stopCellEditing() {

			String rowtext = rowTextField.getText().trim();
			String coltext = columnTextField.getText().trim();
			if (rowtext.equals("") || coltext.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(rowtext, coltext);
					return super.stopCellEditing();
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String rowtext, String coltext) {
			rowsize = new Integer(rowtext);
			colsize = new Integer(coltext);
			if (rowsize.intValue() == 0 && colsize.intValue() != 0)
				rowsize = new Integer(1);
			else if (rowsize.intValue() != 0 && colsize.intValue() == 0)
				colsize = new Integer(1);
		}

	}
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

	public static class EnumerationBuildEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		//protected JLabel select = Templates.makeLabel("select:");
		protected DComboBox chooseComboBox = Templates.makeDComboBox();
		protected DomeEnumeration data;


		public EnumerationBuildEditor()
		{

			editorPanel.setLayout(new GridLayout(1, 1));
			//editorPanel.add(select);
			editorPanel.add(chooseComboBox);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
					setModelSelection();
				}
			};
			chooseComboBox.addActionListener(aListener);
		}

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (DomeEnumeration) value;
			setupComboBox();
		}

		protected void setupComboBox()
		{
			chooseComboBox.setModel(new DefaultComboBoxModel(((EnumerationData) data).getNames()));
		}

		protected void setModelSelection()
		{
			data.setLastSelection(chooseComboBox.getSelectedIndex());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return data;
		}

		public boolean stopCellEditing()
		{
			int index = chooseComboBox.getSelectedIndex();
			if (index == -1) {
				cancelCellEditing();
				return false;
			} else {
				return super.stopCellEditing();
			}
		}

        /**
         * sangmok
         * Clear reference to data object
         * should be called whenever the parameter editing frame window closes
         * also should be called whenever the parameter is set (when actionPerformed)
         */
        public void clearDataObject() {
            data = null;
        }
	}

	//Qing --- Add for file datatype
	//add for Vectors and Matrix stuff
    public static class fileBuildEditor extends AbstractCellEditor
            implements TableCellEditor
    {

        protected JTable table;
        protected JPanel editorPanel = new JPanel();
        protected DTextField filePathTextField = Templates.makeDTextField();
        protected JButton browseButton = Templates.makeButton("choose...");
        protected DomeFile data;
        protected String filepath;


        public fileBuildEditor()
        {

            editorPanel.setLayout(new GridLayout(1, 2));
            editorPanel.add(filePathTextField);
            editorPanel.add(browseButton);


            filePathTextField.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    stopCellEditing();
                    setModel();
                }
            });
            browseButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(FileUtils.getFilterForType(data.getFileType()));

                    if (chooser.showDialog(browseButton, "choose") != JFileChooser.APPROVE_OPTION)
                        return;
                    else {
                        String newFileName = chooser.getSelectedFile().getAbsolutePath(); // never empty
                        String suffix = FileUtils.getDefaultSuffixForType(data.getFileType());
                        if (!newFileName.endsWith(suffix)) {//this means user pick another type of file
                            if (newFileName.indexOf(".") == -1) {
                                int answer = TwoButton2Msg.showOption(null, "No extension", "The file name has no extension " + suffix + "!", "",
                                                                      "add extension", "don't add extension", new Dimension(300, 100));
                                if (answer == TwoButton2Msg.LEFT_OPTION) {
                                    newFileName = newFileName + suffix;
                                } else {// otherwise, keep it
                                    data.setFileType(FileUtils.getTypeForFile(newFileName));
                                }

                            } else
                                data.setFileType(FileUtils.getTypeForFile(newFileName));
                        }

                        data.setFileType(FileUtils.getTypeForFile(newFileName));

                        filepath = newFileName;
                        setModel();
                    }
                }
            });
        }

        protected void editSelectedCell()
        {
            if (table != null)
                table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     int row, int column)
        {
            this.table = table;
            setupEditor(value);
            return editorPanel;
        }

        protected void setupEditor(Object value)
        {
            data = (DomeFile) value;
            setupTextField();
        }

        protected void setupTextField()
        {
            filePathTextField.setText(data.getFilePath());
            filePathTextField.setCurrent();

        }

        protected void setModel()
        {
            data.setFilePath(filepath);
            fireEditingStopped();
        }

        public Object getCellEditorValue()
        {
            return data;
        }

        public boolean stopCellEditing()
        {
            String text = filePathTextField.getText().trim();
            if (text.equals("")) {
                cancelCellEditing();
                return false;
            } else {
                try {
                    parseText(text);
                    return super.stopCellEditing();
                } catch (Exception ex) {
                    System.err.println("invalid data");
                    return false;
                }
            }
        }

        protected void parseText(String text)
        {
            String suffix = FileUtils.getDefaultSuffixForType(data.getFileType());
            if (text.equals("")) {
                filepath = text;
                return;
            }
            File file = new File(text);
            if (text.endsWith(File.separator)) {
                filepath = text + FileUtils.DEFAULTPREFIX + suffix;
                return;
            }
            if (file.getName().trim().equals("")) {
                filepath = text + File.separator + FileUtils.DEFAULTPREFIX + suffix;
                return;
            }
            if (!file.getName().endsWith(suffix)) {
                if (file.getName().indexOf(".") == -1)
                    filepath = text + suffix;
                else {
                    data.setFileType(FileUtils.getTypeForFile(text));
                    filepath = text;
                }
                return;
                //filepath = text + suffix;
                //return;
            }
            filepath = text;
        }

        /**
         * sangmok
         * Clear reference to data object
         * should be called whenever the parameter editing frame window closes
         * also should be called whenever the parameter is set (when actionPerformed)
         */
        public void clearDataObject() {
            data = null;
        }

    }

	public static class fileRunEditor extends fileBuildEditor
	{
		protected void setModel()
		{
            // data must be an instance of FileData at runtime
			((FileData) data).setFilePathAtRuntime(filepath);
			fireEditingStopped();
		}
	}

	public static class listBuildEditor extends AbstractCellEditor
	        implements TableCellEditor
	{

		protected JTable table;
		protected JPanel editorPanel = new JPanel();
		protected DTextField magnitudeTextField = Templates.makeDTextField();
		//protected DComboBox unitComboBox = makeComboBox();
		protected DomeList data;
		protected Integer size;

		public listBuildEditor()
		{

			editorPanel.setLayout(new GridLayout(1, 2));
			editorPanel.add(magnitudeTextField);
			//editorPanel.add(unitComboBox);
			ActionListener aListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					stopCellEditing();
					setModelSize();
				}
			};
			magnitudeTextField.addActionListener(aListener);
		}

		/*protected DComboBox makeComboBox()
			{
				DComboBox cb = Templates.makeDComboBox();
				cb.setUI(new BasicComboBoxUI()
				{
					protected ComboPopup createPopup()
					{
						BasicComboPopup popup = new CustomBasicComboPopup(comboBox);
						popup.getAccessibleContext().setAccessibleParent(comboBox);
						return popup;
					}
				});
				cb.addActionListener(getComboBoxActionListener());
				return cb;
			}
	    */
		/*protected class CustomBasicComboPopup extends BasicComboPopup
		{
			public CustomBasicComboPopup(JComboBox combobox)
			{
				super(combobox);
			}

			protected MouseListener createMouseListener()
			{
				return new CustomInvocationMouseHandler();
			}

			protected class CustomInvocationMouseHandler
			        extends BasicComboPopup.InvocationMouseHandler
			{
				public void mousePressed(MouseEvent e)
				{
					if (magnitudeTextField.isStale()) {
						stopCellEditing(); // commit textfield
						editSelectedCell();
						((vectorBuildEditor) table.getCellEditor()).unitComboBox.showPopup();
					}
					else {
						super.mousePressed(e);
					}
				}
			}
		}
        */

		protected void editSelectedCell()
		{
			if (table != null)
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected,
		                                             int row, int column)
		{
			this.table = table;
			setupEditor(value);
			return editorPanel;
		}

		protected void setupEditor(Object value)
		{
			data = (DomeList) value;
			setupTextField();
			//setupComboBox();
		}

		protected void setupTextField()
		{
			magnitudeTextField.setText(new Integer((data).getSize()).toString());
			magnitudeTextField.setCurrent();

		}

		/*protected void setupComboBox()
		{
			UnitComboBoxModel unitModel = new UnitComboBoxModel(data.getUnit(), true);
			unitComboBox.setModel(unitModel);
		}


		protected ActionListener getComboBoxActionListener()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Object choice = unitComboBox.getSelectedItem();
					if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
						changeListAction();
					else
						setModelSelectedUnit();
				}
			};
		}


		protected void changeListAction()
		{
			Unit choice = UnitChooser.showDialog(editorPanel, data.getUnit());
			if (choice != null) {
				data.setUnit(choice);
			}
			fireEditingStopped();
		}

		protected void setModelSelectedUnit()
		{
			data.setUnit((Unit) unitComboBox.getSelectedItem());
			fireEditingStopped();
		}
     */
		protected void setModelSize()
		{
			data.setSize(size.intValue());
			fireEditingStopped();
		}

		public Object getCellEditorValue()
		{
			return data;
		}

		public boolean stopCellEditing()
		{
			String text = magnitudeTextField.getText().trim();
			if (text.equals("")) {
				cancelCellEditing();
				return false;
			} else {
				try {
					parseText(text);
					return super.stopCellEditing();
				}
				catch (BuildMappingValuePropagationException ex) {
					OneButton1Msg.showError(null, "Mapping value propagation error",
					                        ex.getMessage(),
					                        "ok", new Dimension());
					return false;
				} catch (Exception ex) {
					System.err.println("invalid data");
					return false;
				}
			}
		}

		protected void parseText(String text)
		{
			size = new Integer(text);
		}

        /**
         * sangmok
         * Clear reference to data object
         * should be called whenever the parameter editing frame window closes
         * also should be called whenever the parameter is set (when actionPerformed)
         */
        public void clearDataObject() {
            data = null;
        }
	}

}
