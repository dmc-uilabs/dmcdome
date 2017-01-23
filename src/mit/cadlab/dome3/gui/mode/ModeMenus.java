// ModeMenus.java
package mit.cadlab.dome3.gui.mode;

import mit.cadlab.dome3.gui.menu.MenuManager;

import javax.swing.JMenu;

/**
 * Individual ModeMenu classes should subclass this class and
 * have an "initialize" method.
 * public static void initialize(int modeId);
 */
public class ModeMenus implements ModeContexts
{

	protected static void addContextMenusToMode(_Context[] contexts, int modeId)
	{
		for (int i = 0; i < contexts.length; ++i) {
			_Context c = contexts[i];
			MenuManager.addMenuContext(c.name, modeId, c.menus);
		}
	}

	protected static class _Context
	{

		public String name;
		public JMenu[] menus;

		public _Context(String name, JMenu[] contextMenus)
		{
			this.name = name;
			this.menus = contextMenus;
		}

	}

}
