// DTextField.java
package mit.cadlab.dome3.swing;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Customized JTextField which changes background color upon user changes
 * and supports changing background color programmatically to reflect
 * status of data in textfield.
 */
public class DTextField extends JTextField implements GuiConstants
{
	// to do: check if listeners added are correct...login highlight problem.

	protected static TextEditorPopupListener popupListener = new TextEditorPopupListener();
	protected boolean isNewFocus = false;

	public DTextField()
	{
		super();
		_configure();
	}

	public DTextField(int columns)
	{
		super(columns);
		_configure();
	}

	public DTextField(String text)
	{
		super(text);
		_configure();
	}

	public DTextField(String text, int columns)
	{
		super(text, columns);
		_configure();
	}

	private void _configure()
	{
		addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				isNewFocus = true;
			}

			public void focusLost(FocusEvent e)
			{
				if (isEditable() && isStale())
					fireActionPerformed();
			}
		});
		addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				if (isEditable() && isNewFocus) {
					isNewFocus = false;
					selectAll();
				}
			}
		});
		getDocument().addDocumentListener(new DocumentListener()
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
				if (isEditable() && !isStale())
					setStale();
			}
		});
		// setEditable called in super constructor -- adds mouse listener
	}

	public void setEditable(boolean isEditable)
	{
		super.setEditable(isEditable);
		removeMouseListener(popupListener); // ensure it is gone
		if (isEditable()) {
			addMouseListener(popupListener);
		}
	}

	public boolean isStale()
	{
		return getBackground().equals(STALE_COLOR);
	}

	public void setStale()
	{
		setBackground(STALE_COLOR);
	}

	public void setInProcess()
	{
		setBackground(IN_PROCESS_COLOR);
	}

	public void setCurrent()
	{
		setBackground(isEditable() ? Color.white : NOT_EDITABLE_COLOR);
	}

}
