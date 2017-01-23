// MenuManager.java
package mit.cadlab.dome3.gui.menu;

import java.util.Hashtable;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MenuManager
{

	// A menu context is a String identifier for a set of context menus
	// Setting the menu context changes the content of the DOME menu bar

	protected static final JMenu[] noContextMenus = {};
	protected static String[] modes = {};
	protected static JMenu[][] modeMenuArray = {};

	// context names must be unique
	protected static Hashtable menuContexts = new Hashtable();

	protected static int registerMode(String modeContext, JMenu[] modeMenus)
	{
		String[] newModes = new String[modes.length + 1];
		JMenu[][] newModeMenus = new JMenu[modes.length + 1][2];
		for (int i = 0; i < modes.length; ++i) {
			newModes[i] = modes[i];
			newModeMenus[i] = modeMenuArray[i];
		}
		int newModeId = modes.length;
		newModes[newModeId] = modeContext;
		newModeMenus[newModeId] = modeMenus;
		modes = newModes;
		modeMenuArray = newModeMenus;
		addMenuContext(modeContext, newModeId, noContextMenus);
		return newModeId;
	}

	public static void addModeContext(String modeContext,
	                                  JMenu[] modeMenus)
	{
		// must add in same order as in Modes!
		int modeId = registerMode(modeContext, modeMenus);
	}

	public static void addMenuContext(String contextName,
	                                  int modeId,
	                                  JMenu[] contextMenus)
	{
		checkContextName(contextName);
		menuContexts.put(contextName, new MenuContext(contextName, modeId, contextMenus));
	}

	protected static void checkContextName(String contextName)
	{
		if (menuContexts.containsKey(contextName))
			throw new RuntimeException("MenuContext.addMenuContext duplicate context name: " + contextName);
	}

	public static String getModeContextName(int modeId)
	{
		return modes[modeId];
	}

	public static int getModeId(String contextName)
	{
		try {
			return getMenuContext(contextName).modeId;
		} catch (Exception ex) {
			return -1;
		}
	}

	public static JMenu[] getModeMenus(int modeId)
	{
		return modeMenuArray[modeId];
	}

	public static JMenu[] getContextMenus(String contextName)
	{
		MenuContext mc = getMenuContext(contextName);
		if (mc == null)
			return noContextMenus;
		else
			return mc.contextMenus;
	}

	protected static MenuContext getMenuContext(String contextName)
	{
		// returns null if contextName not in hashtable
		return (MenuContext) menuContexts.get(contextName);
	}

	protected static class MenuContext
	{

		// instance variables
		protected String name;
		protected int modeId;
		protected JMenu[] contextMenus = noContextMenus;

		// constructors
		protected MenuContext(String name, int modeId, JMenu[] contextMenus)
		{
			this.name = name;
			setModeId(modeId);
			if (contextMenus != null)
				this.contextMenus = contextMenus;
		}

		protected void setModeId(int modeId)
		{
			if (modeId >= 0 && modeId < MenuManager.modes.length)
				this.modeId = modeId;
			else
				throw new RuntimeException("MenuContext.setModeId invalid modeId: " + modeId);
		}

	}

	// keeps track of DomeMenuBar
	protected static DomeMenuBar menubar;

	public static void setDomeMenuBar(DomeMenuBar menubar)
	{
		if (MenuManager.menubar != null)
			throw new RuntimeException("MenuManager can only handle 1 DomeMenuBar!");
		MenuManager.menubar = menubar;
	}

	public static JMenuBar getDomeMenuBar()
	{
		return (JMenuBar) MenuManager.menubar;
	}

	public static void setContext(String context)
	{
		if (menubar != null)
			menubar.setContext(context);
	}
}
