// DomeFrame.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.swing.Closeable;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.DefaultWindowTracker;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

public abstract class DomeFrame extends DFrame implements Closeable
{

	protected DomeGui gui;

	public DomeFrame(DomeGui gui)
	{
		this(gui, null);
	}

	public DomeFrame(DomeGui gui, WindowTracker parent)
	{
		super(gui.getTitle());
		this.gui = gui;
		getContentPane().add((JComponent) gui);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(createWindowListener());
		if (parent == null)
			setWindowTracker(createWindowTracker());
		else
			setWindowTracker(parent);
		setLocation(getWindowLocation());
		setSize(getPreferredSize());
		pack();
		if (gui.getHelpContext() != null)
			DHelp.enableHelp(getRootPane(), gui.getHelpContext());
	}

	protected WindowListener createWindowListener()
	{
		return new DomeFrameWindowListener();
	}

	public void setTitle(String title)
	{
		String oldTitle=getTitle();
		super.setTitle(title);
		firePropertyChange(NameListener.NAME,oldTitle,title);
	}

	protected abstract WindowTracker createWindowTracker(); // ask someone!

	protected abstract Point getWindowLocation(); // ask for this, too!

	protected abstract void notifyNotInFocus(); // MenuManager & focus tracker notices

	public Object getGuiObject()
	{
		return gui.getGuiObject();
	}

	public DomeGui getGui()
	{
		return gui;
	}

	public void selfClose()
	{ // public for action...
		close();
		notifyNotInFocus();
	}

	public void close()
	{
		super.close();
	}

	protected class DomeFrameWindowListener extends WindowAdapter
	{
		public void windowActivated(WindowEvent e)
		{
			gui.setMenuContext();

		}

		public void windowIconified(WindowEvent e)
		{
			notifyNotInFocus();
		}

		public void windowClosing(WindowEvent e)
		{
			gui.close();
			selfClose();
		}
	}

	/**
	 * not used...maybe in some future frame?
	 */
	protected class ChildWindowTracker extends DefaultWindowTracker
	{
		protected HashMap childWindows = new HashMap();

		public void notifyInFront(Window w)
		{
			// if in queue, remove it
			if (!children.remove(w)) { // if not in queue, track it
				if (w instanceof DomeFrame) {
					childWindows.put(((DomeFrame) w).getGuiObject(), w);
				}
			}
			children.add(w);
		}

		public void removeChildWindow(Window w)
		{
			if (children.remove(w)) {
				if (w instanceof DomeFrame) {
					childWindows.remove(((DomeFrame) w).getGuiObject());
				}
			}
		}

		public Window getWindow(Object obj)
		{
			return (Window) childWindows.get(obj);
		}
	}
}
