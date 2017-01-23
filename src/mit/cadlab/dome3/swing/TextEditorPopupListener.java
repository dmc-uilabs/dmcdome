// TextEditorPopupListener.java
package mit.cadlab.dome3.swing;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

// JTextComponent, TextAction

public class TextEditorPopupListener extends MouseAdapter
{

	protected static JPopupMenu popup = createEditTextPopupMenu();

	public TextEditorPopupListener()
	{
	}

	protected static JPopupMenu createEditTextPopupMenu()
	{
		JPopupMenu menu = new JPopupMenu("textMenu");
		menu.add(MenuUtils.makeMenuItem(new TextAction("cut")
		{
			public void actionPerformed(ActionEvent e)
			{
				JTextComponent target = getTextComponent(e);
				if (target != null)
					target.cut();
			}
		}));
		menu.add(MenuUtils.makeMenuItem(new TextAction("copy")
		{
			public void actionPerformed(ActionEvent e)
			{
				JTextComponent target = getTextComponent(e);
				if (target != null)
					target.copy();
			}
		}));
		menu.add(MenuUtils.makeMenuItem(new TextAction("paste")
		{
			public void actionPerformed(ActionEvent e)
			{
				JTextComponent target = getTextComponent(e);
				if (target != null && target.isEditable())
					target.paste();
			}
		}));
		menu.addSeparator();
		menu.add(MenuUtils.makeMenuItem(new TextAction("select all")
		{
			public void actionPerformed(ActionEvent e)
			{
				JTextComponent target = getTextComponent(e);
				if (target != null && target.isEditable()) {
					Document doc = target.getDocument();
					target.setCaretPosition(0);
					target.moveCaretPosition(doc.getLength());
				}
			}
		}));
		menu.addMouseListener(new MouseAdapter()
		{
			// hide when mouse exits popup area
			public void mouseExited(MouseEvent e)
			{
				Rectangle bounds = e.getComponent().getBounds();
				if (e.getX() >= bounds.width || e.getY() >= bounds.height || e.getX() < 0 || e.getY() < 0)
					e.getComponent().setVisible(false);
			}
		});
		return menu;
	}

	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e)
	{
		if (e.isPopupTrigger()) {
			if (e.getSource() instanceof JTextComponent) {
				if (!((JTextComponent) e.getSource()).isEditable()) {
					return; // not editable
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

}
