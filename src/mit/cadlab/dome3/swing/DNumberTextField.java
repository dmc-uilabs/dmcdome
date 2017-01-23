/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Dec 6, 2002
 * Time: 11:54:59 AM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.swing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.*;
import java.awt.*;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class DNumberTextField extends JFormattedTextField implements GuiConstants
{
	protected static TextEditorPopupListener popupListener = new TextEditorPopupListener();
	protected boolean isNewFocus = false;
	NumberFormat m_format;
	HashSet m_actionKeys;

	public DNumberTextField(NumberFormat format)
	{
		super();
		m_format = format;
		m_actionKeys = new HashSet();
		m_actionKeys.add(new Integer(KeyEvent.VK_ENTER));
		m_actionKeys.add(new Integer(KeyEvent.VK_DELETE));
		m_actionKeys.add(new Integer(KeyEvent.VK_BACK_SPACE));
		m_actionKeys.add(new Integer(KeyEvent.VK_ESCAPE));
		_configure();
	}

	/**
	 * Add listeners and handlers.
	 */
	private void _configure()
	{
		addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
			}

			public void keyReleased(KeyEvent e)
			{
			}

			/**
			 * Consume keys that are not valid for this input field
			 * @param e Key event
			 */
			public void keyTyped(KeyEvent e)
			{
				int c = e.getKeyChar();

				if (!m_actionKeys.contains(new Integer(c))) {
					if (m_format.isParseIntegerOnly()) { // integer
						if (c < e.VK_0 || c > e.VK_9)
							e.consume();
					} else {  // real
						if ((c < e.VK_0 || c > e.VK_9) && c != e.VK_PERIOD && c != e.VK_MINUS)
							e.consume();
					}
				}
			}
		});

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
