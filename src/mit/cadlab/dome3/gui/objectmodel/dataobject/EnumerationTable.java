// EnumerationTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;

import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.table.ObjectTable;
import mit.cadlab.dome3.swing.table.ObjectTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

/**
 * to have customized table for enumeration
 */
public class EnumerationTable extends ObjectTable
{
	protected TextCellEditor textCellEditor = new TextCellEditor();


	public EnumerationTable()
	{
		super();

		textCellEditor.setClickCountToStart(2);
		setDefaultEditor(Object.class, textCellEditor);
		setDefaultEditor(Boolean.class, new Editors.BooleanComboBoxEditor());
		setDefaultRenderer(Boolean.class, new Renderers.BooleanComboBoxRenderer());
		DomeTable.customizeTable(this);
	}

	public EnumerationTable(ObjectTableModel dm)
	{
		super(dm);

		textCellEditor.setClickCountToStart(2);
		setDefaultEditor(Object.class, textCellEditor);
		setDefaultEditor(Boolean.class, new Editors.BooleanComboBoxEditor());
		setDefaultRenderer(Boolean.class, new Renderers.BooleanComboBoxRenderer());
		DomeTable.customizeTable(this);
	}


	public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
	{
		Object value = getValueAt(row, column);
		boolean isSelected = isCellSelected(row, column);
		boolean rowIsAnchor = (selectionModel.getAnchorSelectionIndex() == row);
		boolean colIsAnchor =
		        (columnModel.getSelectionModel().getAnchorSelectionIndex() == column);
		boolean hasFocus = false;
		//if (startpoint.x == row && startpoint.y == column) {
		//	hasFocus = true;
		//}
		//else {
		//	hasFocus = false; // (rowIsAnchor && colIsAnchor) && hasFocus();
		//}

		return renderer.getTableCellRendererComponent(this, value,
		                                              isSelected, hasFocus, row, column);
	}
/*
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		//System.out.println(editor.toString());
		Object value = getValueAt(row, column);
		if (value instanceof Boolean) editor = booleanEditor;

		boolean isSelected = isCellSelected(row, column);
		Component comp = editor.getTableCellEditorComponent(this, value, isSelected,
				row, column);
		if (comp instanceof JComponent) {
			JComponent jComp = (JComponent) comp;
			if (jComp.getNextFocusableComponent() == null) {
				jComp.setNextFocusableComponent(this);
			}
		}
		return comp;
	}

*/
}

