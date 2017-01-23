// DomeRunFrame.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.menu.RunWindowsMenu;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.runbrowser.RunBrowser;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.swing.WindowTracker;
import mit.cadlab.dome3.util.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class DomeRunFrame extends DomeFrame {
    protected static final Dimension DEFAULT_SIZE = new Dimension(DomeBuildFrame.DEFAULT_SIZE);
    protected boolean frameClosed = false;
    protected BrowserNameListener titleListener = new BrowserNameListener();

	public DomeRunFrame(DomeGui gui, WindowTracker parent)
	{
		super(gui, parent);
		getWindowsMenu().addWindow(this);
		if (gui instanceof RunBrowser)
			((JComponent) gui).addPropertyChangeListener(NameListener.NAME, titleListener);
		Object guiObj = gui.getGuiObject();
		if (guiObj instanceof Model || guiObj instanceof ToolInterface)
			this.setSize(DEFAULT_SIZE);
		else if (guiObj instanceof ModelInterface) {
			if (guiObj instanceof AbstractDomeModelInterface) {
				if (((AbstractDomeModelInterface) guiObj).hasCustomGui())
					this.pack();
				else
					this.setSize(DEFAULT_SIZE);
			}
			else
				this.setSize(DEFAULT_SIZE);
		}
	}

    protected WindowListener createWindowListener() {
        return new DomeRunFrameWindowListener();
    }

    protected WindowTracker createWindowTracker() {
        Window w = RunFocusTracker.getCurrentWindow();
        if (w instanceof DomeRunFrame) {
            if (((DomeRunFrame) w).getGui() instanceof RunBrowser)
                return RunMode.getWindowTracker();
        }
        if (w instanceof WindowTracker)
            return (WindowTracker) w;
        return RunMode.getWindowTracker();
    }

    protected Point getWindowLocation() {
        return RunMode.getWindowLocation();
    }

    protected void notifyNotInFocus() {
        MenuManager.setContext(ModeContexts.RUN_MODE);
        RunFocusTracker.notifyFocusRemoved();
    }

    protected class DomeRunFrameWindowListener extends DomeFrameWindowListener {
        public void windowClosed(WindowEvent e) {
            //Qing change made in Aug 19th, to solve menu strand in rum mode... exactly follow the solution in build mode
            /*if (frameClosed) {
                frameClosed = false;
                return;
            }
            super.windowClosed(e);
            getWindowsMenu().removeWindow(DomeRunFrame.this);

//give some time
            try {
                Thread.sleep(100);
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }

            //when a new model is closed, menus were getting stranded in the
            //application menu bar.
            //Following code avoids that menu stranding behavior
            if (DomeRunFrame.this.getGui() instanceof RunBrowser) {
notifyNotInFocus();
                //MenuManager.setContext(ModeContexts.RUN_MODE);
                //MenuManager.getDomeMenuBar().grabFocus();
                DomeRunFrame.this.close();
                frameClosed = true;
            }    */

            if (!frameClosed) frameClosed = true;
            getWindowsMenu().removeWindow(DomeRunFrame.this);
//give some time
            try {
                Thread.sleep(100);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            if (frameClosed) {
                notifyNotInFocus();
                RunMode.getWindowTracker().removeChildWindow(DomeRunFrame.this);
//give focus to main menu bar
	            if (MenuManager.getDomeMenuBar() != null)
                    MenuManager.getDomeMenuBar().grabFocus();
            }

        }
    }

    protected RunWindowsMenu getWindowsMenu() {
        return RunMenus.browser_windowsMenu;
    }

	public boolean isTopLevelRunFrame() {
		return (parent == RunMode.getWindowTracker());
	}

    public void close() {
        //moved this line here from "windowClosing" method
        //so that the children gui would also clean up properly
        gui.close();
        super.close();
    }

    public void selfClose() { // public for action...
        close();
        notifyNotInFocus();
	    if (isTopLevelRunFrame() && RunFocusTracker.getCurrentComponent() == null)
		    Log.setDefaultLog();
    }

    protected class DomeFrameWindowListener extends WindowAdapter {
        public void windowActivated(WindowEvent e) {
            gui.setMenuContext();
            RunFocusTracker.notifyInFocus((JComponent) gui);
        }

        public void windowIconified(WindowEvent e) {
            notifyNotInFocus();
        }

        public void windowClosing(WindowEvent e) {
            selfClose();
        }
    }

    protected class BrowserNameListener extends NameListener {
        public void nameChanged(String newName) {
            setTitle(newName);
        }
    }
}
