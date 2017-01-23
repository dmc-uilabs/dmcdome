// RunMenus.java
package mit.cadlab.dome3.gui.mode.server;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeMenus;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ServerMenus extends ModeMenus
{

	public static final JMenu windowsMenu = makeWindowsMenu();
	private static boolean initialized = false;
	private static JMenu serverMenu = null;
	private static JMenuItem loginLogoutMI = null;

	protected static JMenu makeWindowsMenu()
	{
		JMenu menu = MenuUtils.makeMenu("Windows");
		menu.add(MenuUtils.makeMenuItem("Server Windows"));
		return menu;
	}

	public static void initialize(int runModeId)
	{
		if (initialized) {
			System.err.println("ServerMenus already initialized");
			return;
		}
		MenuManager.addModeContext(SERVER_MODE, new JMenu[]{makeServerMenu(), null}); // no windows menu

		_Context[] serverContexts = {
			new _Context(SERVER_ADMIN, makeServerAdminMenus()),
			new _Context(SERVER_USER, makeServerUserMenus()),
		};

		addContextMenusToMode(serverContexts, runModeId);
		initialized = true;
	}

	public static JMenu makeServerMenu()
	{
		serverMenu = MenuUtils.makeMenu("Server");
		DHelp.enableHelp(serverMenu, DHelp.SERVER_MODE);
		loginLogoutMI = MenuUtils.makeMenuItem(ServerMode.loginLogoutAction);
		serverMenu.add(loginLogoutMI);
		serverMenu.addSeparator();
		serverMenu.add(MenuUtils.makeMenuItem(ServerMode.closeAllAction));
		serverMenu.addSeparator();
		serverMenu.add(MenuUtils.makeMenuItem(Modes.exitAction));
		return serverMenu;
	}

	private static JMenu[] makeServerAdminMenus()
	{
		return ServerAdminMenus.getMenus(); // users, groups, option, log
	}

	private static JMenu[] makeServerUserMenus()
	{
		JMenu menu = MenuUtils.makeBoldMenu("Options");
		menu.add(MenuUtils.makeMenuItem(ServerMode.manageFileSpaceAction));
		//todo Use the ServerPanelOld class as basis for this
		menu.addSeparator();
		menu.add(MenuUtils.makeMenuItem(ServerMode.changePasswordAction));
		return new JMenu[]{menu};
	}

	public static void setLoggedInMenus(String svrName, boolean isAdmin)
	{
		serverMenu.setText(svrName);
		loginLogoutMI.setText("Logout");
		MenuManager.setContext(isAdmin ? SERVER_ADMIN : SERVER_USER);
	}

	public static void setLoggedOutMenus()
	{
		serverMenu.setText("Server");
		loginLogoutMI.setText("Login");
		MenuManager.setContext(SERVER_MODE);
	}

}
