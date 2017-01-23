// DomeRunFrame.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.menu.PlayspaceWindowsMenu;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildMenus;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.mode.build.BuildPlayspaceFocusTracker;
import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.swing.Closeable;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

//public class DomeBuildPlayspaceFrame extends DFrame implements Closeable,Saveable {

public class DomeBuildPlayspaceFrame extends DFrame implements Closeable
{
	public static final Dimension DEFAULT_SIZE = new Dimension(DomeBuildFrame.DEFAULT_SIZE);

	protected DomePlayspaceGui gui;
	protected boolean frameClosed = false;
	protected BrowserNameListener titleListener = new BrowserNameListener();


	public DomeBuildPlayspaceFrame(DomePlayspaceGui gui)
	{
		super("Dome Playspace: " + gui.getTitle());
		this.gui = gui;
		((JComponent) gui).addPropertyChangeListener(NameListener.NAME, titleListener);
		getContentPane().add((JComponent) gui);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(createWindowListener());
		setWindowTracker(createWindowTracker());
		setLocation(getWindowLocation());
		setSize(DEFAULT_SIZE);

		if (gui.getHelpContext() != null)
			DHelp.enableHelp(getRootPane(), gui.getHelpContext());
		getWindowsMenu().addWindow(this);

	}

	public Dimension getPreferredSize()
	{
		return new Dimension(400, 300);
	}

	protected WindowListener createWindowListener()
	{
		return new DomePlayspaceFrameWindowListener();
	}

	protected WindowTracker createWindowTracker()
	{
		return BuildMode.getWindowTracker();
	}

	protected Point getWindowLocation()
	{
		return BuildMode.getWindowLocation();
	}

	protected void notifyNotInFocus()
	{
		MenuManager.setContext(ModeContexts.BUILD_MODE);
		BuildPlayspaceFocusTracker.notifyFocusRemoved();
	}


	protected void setMenuContext()
	{
		gui.setMenuContext();
	}

	protected class DomePlayspaceFrameWindowListener extends DomeFrameWindowListener
	{
		public void windowClosed(WindowEvent e)
		{
			if (!frameClosed) frameClosed = true;
			getWindowsMenu().removeWindow(DomeBuildPlayspaceFrame.this);
			//give some time
			try {
				Thread.sleep(100);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			if (frameClosed) {
				notifyNotInFocus();
				BuildMode.getWindowTracker().removeChildWindow(DomeBuildPlayspaceFrame.this);
				MenuManager.getDomeMenuBar().grabFocus();
			}
		}
	}

	protected PlayspaceWindowsMenu getWindowsMenu()
	{
		return BuildMenus.playspace_windowsMenu;
	}

	public void close()
	{
		super.close();
	}

	public DomePlayspaceGui getGui()
	{
		return gui;
	}

	public void selfClose()
	{ // public for action...
		close();
		notifyNotInFocus();

	}

	protected class DomeFrameWindowListener extends WindowAdapter
	{
		public void windowActivated(WindowEvent e)
		{
			gui.setMenuContext();
			BuildPlayspaceFocusTracker.notifyInFocus((JComponent) gui);

			if (frameClosed) selfClose();
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

	protected class BrowserNameListener extends NameListener
	{
		public void nameChanged(String newName)
		{
			setTitle("Dome Playspace: " + getGui().getTitle());
		}
	}
}
