// ServerAdminMenus.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.mode.server;

import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ServerAdminMenus
{

	private static JMenuItem editUserMI, deleteUserMI, editGroupMI, deleteGroupMI;
	private static final JMenu[] menus = createMenus();

	private static JMenu[] createMenus()
	{
		JMenu usersMenu = MenuUtils.makeBoldMenu("Users");
		usersMenu.add(MenuUtils.makeMenuItem(ServerMode.showUsersListAction));
		usersMenu.addSeparator();
		usersMenu.add(MenuUtils.makeMenuItem(ServerMode.newUserAction));
		editUserMI = MenuUtils.makeMenuItem(ServerMode.editUserAction);
		usersMenu.add(editUserMI);
		deleteUserMI = MenuUtils.makeMenuItem(ServerMode.deleteUserAction);
		usersMenu.add(deleteUserMI);

		JMenu groupsMenu = MenuUtils.makeBoldMenu("Groups");
		groupsMenu.add(MenuUtils.makeMenuItem(ServerMode.showGroupsListAction));
		groupsMenu.addSeparator();
		groupsMenu.add(MenuUtils.makeMenuItem(ServerMode.newGroupAction));
		editGroupMI = MenuUtils.makeMenuItem(ServerMode.editGroupAction);
		groupsMenu.add(editGroupMI);
		deleteGroupMI = MenuUtils.makeMenuItem(ServerMode.deleteGroupAction);
		groupsMenu.add(deleteGroupMI);

		disableEditDeleteUserMIs();
		disableEditDeleteGroupMIs();


		JMenu manageFileSpacemenu = MenuUtils.makeBoldMenu("Options");
		manageFileSpacemenu.add(MenuUtils.makeMenuItem(ServerMode.manageFileSpaceAction));
		//todo Use the ServerPanelOld class as basis for this
		manageFileSpacemenu.addSeparator();
		manageFileSpacemenu.add(MenuUtils.makeMenuItem(ServerMode.changePasswordAction));
		manageFileSpacemenu.addSeparator();
		manageFileSpacemenu.add(MenuUtils.makeMenuItem(ServerMode.shutdownServerAction));

		JMenu logsMenu = MenuUtils.makeBoldMenu("Logs");
        logsMenu.setEnabled(false);
        //todo enable this when there is some sort of log support.

		return new JMenu[]{usersMenu, groupsMenu, manageFileSpacemenu, logsMenu};
	}

	// for outside users
	public static JMenu[] getMenus()
	{
		return menus;
	}

	public static void enableEditDeleteUserMIs()
	{
		editUserMI.setEnabled(true);
		deleteUserMI.setEnabled(true);
	}

	public static void disableEditDeleteUserMIs()
	{
		editUserMI.setEnabled(false);
		deleteUserMI.setEnabled(false);
	}

	public static void enableEditDeleteGroupMIs()
	{
		editGroupMI.setEnabled(true);
		deleteGroupMI.setEnabled(true);
	}

	public static void disableEditDeleteGroupMIs()
	{
		editGroupMI.setEnabled(false);
		deleteGroupMI.setEnabled(false);
	}

}
