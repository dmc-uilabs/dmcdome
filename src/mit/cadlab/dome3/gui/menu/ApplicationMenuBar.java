// ApplicationMenuBar.java
package mit.cadlab.dome3.gui.menu;

import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.gui.mode.server.ServerMode;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.DomeClientApplication;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ApplicationMenuBar extends JMenuBar implements DomeMenuBar
{

	protected static final String WINDOWS = "Windows"; // Windows menu title
	protected static final String BOOKMARKS = "Bookmarks";
	JComboBox modeComboBox = makeModeComboBox();

	JSeparator beginContextMenus = makeSeparator();
	JSeparator endContextMenus = makeSeparator();

	int currentMode = -1;
	String currentContext = "";

	public ApplicationMenuBar(JMenu helpMenu)
	{
		MenuManager.setDomeMenuBar(this); // restricts so it can only happen once
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(new JMenu("Modes")); // filler for now
		add(new JMenu("Windows")); // filler for now
		if (helpMenu != null)
			add(helpMenu);
		add(Box.createHorizontalGlue());
		add(modeComboBox);
		setMode(0);
	}

	public int getMode()
	{
		return currentMode;
	}

	public void setMode(int mode)
	{
		if (mode == 3) { // server mode might have context menus
			//setMode(mode, true);
			currentContext = ServerMode.getMenuContext();
			if (currentContext == ModeContexts.LOGGED_OFF) {
				setMode(mode, false);
				refresh();
			} else {
				setMode(mode, true);
				updateContextMenus();
				refresh();
			}
		} else
			setMode(mode, false);
	}

	protected void setMode(int mode, boolean updateContext)
	{
		if (mode == currentMode) return;
		hideMode();
		currentMode = mode;
		updateModeMenus();
		showMode();
		// if not updating context menus, clear them and refresh display
		// otherwise, assume window refresh will happen after context menu is set
		if (!updateContext) {
			currentContext = MenuManager.getModeContextName(mode);
			removeContextMenus();
			refresh();
		}
	}

	public void hideMode()
	{
		Modes.hideMode(currentMode);
	}

	public void showMode()
	{
		Modes.showMode(currentMode);
	}

	public void setContext(String context)
	{
		if (context == currentContext) return;
		int modeId = MenuManager.getModeId(context);
		if (modeId == -1) { // not found
			System.err.print("Menu context not found: " + context);
			modeId = currentMode;
			context = MenuManager.getModeContextName(modeId);
			System.err.println("...\tswitching to " + context);
		}
		currentContext = context;
		setMode(modeId, true);
		updateContextMenus();
		refresh();
	}

	protected void updateModeMenus()
	{
		JMenu[] modeMenus = MenuManager.getModeMenus(currentMode);

		unselectMenu((JMenu) getComponent(0));
		remove(0);
		add(modeMenus[0], 0);
		//if there is a bookmark menu , remove it!
		removeBookmarksMenu();
		int index = removeWindowsMenu();
		if (currentMode == 2) {//run mode
			if (modeMenus[1] != null)
				add(modeMenus[1], index);
			if (modeMenus[2] != null)
				add(modeMenus[2], index + 1);
		} else {
			if (modeMenus[1] != null)
				add(modeMenus[1], index);
		}
		if (currentMode != modeComboBox.getSelectedIndex()) // skip combobox update if already correct mode
			modeComboBox.setSelectedIndex(currentMode);
		DomeClientApplication.modeHelp.setMode(currentMode); // should uncouple this via listeners
	}

	protected int removeWindowsMenu()
	{
		int index = getComponentIndex(endContextMenus);
		if (index == -1) {
			index = 1;
		} else {
			index = index + 1;
		}
		Component comp = getComponent(index);
		if ((comp instanceof JMenu) && ((JMenu) comp).getText().equals(WINDOWS)) {
			unselectMenu((JMenu) getComponent(index));
			remove(index);
		}
		return index;
	}

	//Qing
	protected void removeBookmarksMenu()
	{
		int index = getComponentIndex(endContextMenus);
		if (index == -1) {
			index = 1;
		} else {
			index = index + 1;
		}
		Component comp = getComponent(index);
		//if there is no such menu, no need to remove
		if ((comp instanceof JMenu) && ((JMenu) comp).getText().equals(WINDOWS)) {
			return;
		}
		//remove it
		if ((comp instanceof JMenu) && ((JMenu) comp).getText().equals(BOOKMARKS)) {
			unselectMenu((JMenu) getComponent(index));
			remove(index);
		}
	}


	protected void removeContextMenus()
	{
		if (getComponentCount() < 3) return;
		if (beginContextMenus == getComponent(1)) {
			remove(1); // remove beginContextMenus
			Object obj = getComponent(1);
			while (endContextMenus != obj) { // remove context menus
				unselectMenu((JMenu) getComponent(1));
				remove(1);
				obj = getComponent(1);
			}
			remove(1); // remove endContextMenus
		}
	}

	protected void updateContextMenus()
	{
		removeContextMenus();
		JMenu[] contextMenus = MenuManager.getContextMenus(currentContext);
		if (contextMenus != null && contextMenus.length > 0) {
			add(beginContextMenus, 1);
			for (int i = 0; i < contextMenus.length; ++i) {
				add(contextMenus[i], i + 2);
			}
			add(endContextMenus, contextMenus.length + 2);
		}
	}

	protected void unselectMenu(JMenu menu)
	{
		menu.setPopupMenuVisible(false);
		menu.setSelected(false);
	}

	protected void refresh()
	{
		revalidate();
		repaint();
	}

	protected JSeparator makeSeparator()
	{
		JSeparator s = new JSeparator(SwingConstants.VERTICAL);
		s.setMaximumSize(new Dimension(1, 26));
		return s;
	}

	protected JComboBox makeModeComboBox()
	{
		String[] modes = new String[Modes.countModes()];
		for (int i = 0; i < modes.length; ++i)
			modes[i] = Modes.getModeName(i);
		JComboBox cb = Templates.makeDComboBox(modes);
		cb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setMode(modeComboBox.getSelectedIndex());
			}
		});
		cb.setMinimumSize(cb.getPreferredSize());
		cb.setMaximumSize(cb.getPreferredSize());
		return cb;
	}

}
