// DTextArea.java
package mit.cadlab.dome3.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Customized JTextArea which changes background color upon user changes
 * and supports changing background color programmatically to reflect
 * status of data in textfield.
 */
public class DTextArea extends JTextArea implements GuiConstants
{

	protected static TextEditorPopupListener popupListener = new TextEditorPopupListener();
	protected boolean isNewFocus = false;

	public DTextArea()
	{
		_configure();
	}

	public DTextArea(int rows, int columns)
	{
		super(rows, columns);
		_configure();
	}

	public DTextArea(String text)
	{
		super(text);
		_configure();
	}

	public DTextArea(String text, int rows, int columns)
	{
		super(text, rows, columns);
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
	}

	public void setEditable(boolean isEditable)
	{
		super.setEditable(isEditable);
		removeMouseListener(popupListener); // ensure it is gone
		if (isEditable())
			addMouseListener(popupListener);
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

	// ActionListener support
	public synchronized void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class, l);
	}

	public synchronized void removeActionListener(ActionListener l)
	{
		listenerList.remove(ActionListener.class, l);
	}

	protected void fireActionPerformed()
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
		                                "");
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

}
