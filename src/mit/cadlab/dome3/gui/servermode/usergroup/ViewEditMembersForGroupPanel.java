// ViewEditMembersForGroupPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collections;

public class ViewEditMembersForGroupPanel extends UserGroupMembershipPanel
{

	// todo: abort if can't get detailed information
	// todo: optimize getting users and groups by getting them from client if it already has information

	public static void showPanel(Component parent, ServerConnection svrConn, int id, String name)
	{
		ViewEditMembersForGroupPanel p = new ViewEditMembersForGroupPanel(svrConn, id, name);
		JDialog d = DialogFactory.createDialog(parent, "View/Edit Members for group " + name, p.gui, true, true);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public ViewEditMembersForGroupPanel(ServerConnection svrConn, int id, String name)
	{
		super(svrConn, id, name);
	}

	protected void setData()
	{
		GroupInfo thisGroup = new GroupInfo(id, name);
		currentSelections = new DArrayList(UserGroupUtils.loadUsersGroupsFromList(UserGroupFunctions.getMembersForGroup(svrConn, id)));
		remainingSelections = new DArrayList();
		remainingSelections.addAll(UserGroupUtils.loadUsersFromList(UserGroupFunctions.getSimpleUserList(svrConn)));
		remainingSelections.addAll(UserGroupUtils.loadGroupsFromList(UserGroupFunctions.getSimpleGroupList(svrConn)));
		remainingSelections.removeAll(currentSelections);
		remainingSelections.remove(thisGroup);
		Collections.sort(currentSelections);
		Collections.sort(remainingSelections);
	}

	protected void setupLabels()
	{
		categoryLabel = Templates.makeLabel("group:");
		currentLabel = Templates.makeLabel("has members:");
		remainingLabel = Templates.makeLabel("other users and groups");
	}

	protected void commitSelectedItems()
	{
		try {
			UserGroupFunctions.editMembersForGroup(svrConn, id, UserGroupUtils.userGroupInfoToIdVector(currentSelections));
		} catch (Exception ex) {
			OneButton1Msg.showError(gui, "Edit groups for user error", ex.getMessage(), "ok",
			                        new Dimension(240, 100));
		}
		dispose();
	}
}
