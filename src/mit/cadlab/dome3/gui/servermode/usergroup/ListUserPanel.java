// ListUserPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.gui.mode.server.ServerAdminMenus;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class ListUserPanel extends ListUserGroupPanel
{

	public ListUserPanel(ServerConnection svrConn)
	{
		super(svrConn);
	}

	protected void editAction(int id, String name)
	{
		Vector userInfo = UserGroupFunctions.getUserGroupInfo(svrConn, id);
		if (EditUserPanel.showPanel(this, svrConn, userInfo))
			loadData();
	}

	protected void deleteAction(int id, String name)
	{
		int choice = TwoButton2Msg.showOption(ListUserPanel.this, "Delete user confirmation", "is about to be deleted.", name,
		                                      "ok", "cancel", TwoButton2Msg.DEFAULT_SIZE);
		if (choice == 1) {
			try {
				UserGroupFunctions.deleteUserGroup(svrConn, id);
				loadData();
			} catch (UnreachableServerException ex) {
				showConnectionErrorMsg();
			} catch (ServerMethodException ex) {
				showDeleteWarning(name);
			}
		}
	}

	protected JLabel getImageLabel()
	{
		ImageIcon labelIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/user.gif");
		JLabel iconLabel = new JLabel(labelIcon, SwingConstants.LEFT);
		iconLabel.setOpaque(false);
		return iconLabel;
	}

	public WindowAdapter getWindowAdapter()
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				ServerAdminMenus.disableEditDeleteUserMIs();
				dispose();
			}

			public void windowActivated(WindowEvent e)
			{
				setDeleteMIState();
			}

			public void windowIconified(WindowEvent e)
			{
				ServerAdminMenus.disableEditDeleteUserMIs();
			}

		};
	}

	protected void setDeleteMIState()
	{
		if (listSelModel.isSelectionEmpty())
			ServerAdminMenus.disableEditDeleteUserMIs();
		else
			ServerAdminMenus.enableEditDeleteUserMIs();
	}

	public void loadData()
	{
		Vector v = UserGroupFunctions.getDetailedUserList(svrConn);
		loadData(UserGroupUtils.loadUsersFromList(v));
	}
}
