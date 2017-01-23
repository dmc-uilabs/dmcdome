// TextCellEditor.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import mit.cadlab.dome3.swing.GuiConstants;
import mit.cadlab.dome3.swing.TextEditorPopupListener;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Color, Component

public class TextCellEditor extends DefaultCellEditor
{
	// single-click to edit
	// focus gained selects entire textfield
	// focus lost stops cell editing
	// system popup trigger displays text editing popup menu

	protected boolean selectTextField = false;

	public TextCellEditor()
	{
		super(Templates.makeTextField(""));
		setClickCountToStart(1);
		JTextField tf = (JTextField) editorComponent;
		tf.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				selectTextField = true;
			}

			public void focusLost(FocusEvent e)
			{
				selectTextField = false; // make sure
				stopCellEditing();
			}
		});
		tf.addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				if (selectTextField) {
					selectTextField = false;
					highlightTextField();
				}
			}
		});
		tf.addMouseListener(new TextEditorPopupListener());
		tf.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				setStaleIfNecessary();
			}

			public void removeUpdate(DocumentEvent e)
			{
				setStaleIfNecessary();
			}

			public void changedUpdate(DocumentEvent e)
			{
				setStaleIfNecessary();
			}

			protected void setStaleIfNecessary()
			{
				JTextField tf = (JTextField) editorComponent;
				if (tf.isEditable() && !isStale())
					setStale();
			}
		});

	}

	public void highlightTextField()
	{
		JTextField tf = (JTextField) editorComponent;
		tf.selectAll();
	}

	public boolean isStale()
	{
		JTextField tf = (JTextField) editorComponent;
		return tf.getBackground().equals(GuiConstants.STALE_COLOR);
	}

	public void setStale()
	{
		JTextField tf = (JTextField) editorComponent;
		tf.setBackground(GuiConstants.STALE_COLOR);
	}

	public void setCurrent()
	{
		JTextField tf = (JTextField) editorComponent;
		tf.setBackground(tf.isEditable() ? Color.white : GuiConstants.NOT_EDITABLE_COLOR);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
	                                             int row, int column)
	{
		Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		setCurrent();
		return comp;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected,
	                                            boolean expanded, boolean leaf, int row)
	{
		Component comp = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		setCurrent();
		return comp;
	}

}
