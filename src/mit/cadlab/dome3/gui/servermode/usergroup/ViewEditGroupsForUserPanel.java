// ViewEditGroupsForUserPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collections;

public class ViewEditGroupsForUserPanel extends UserGroupMembershipPanel
{

	// todo: abort if can't get detailed information
	// todo: optimize getting users and groups by getting them from client if it already has information

	public static void showPanel(Component parent, ServerConnection svrConn, int id, String name)
	{
		ViewEditGroupsForUserPanel p = new ViewEditGroupsForUserPanel(svrConn, id, name);
		JDialog d = DialogFactory.createDialog(parent, "View/Edit Groups for user " + name, p.gui, true, true);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public ViewEditGroupsForUserPanel(ServerConnection svrConn, int id, String name)
	{
		super(svrConn, id, name);
	}

	protected void setData()
	{
		currentSelections = new DArrayList(UserGroupUtils.loadGroupsFromList(UserGroupFunctions.getGroupsForUser(svrConn, id)));
		remainingSelections = new DArrayList();
		remainingSelections.addAll(UserGroupUtils.loadGroupsFromList(UserGroupFunctions.getSimpleGroupList(svrConn)));
		remainingSelections.removeAll(currentSelections);
		Collections.sort(currentSelections);
		Collections.sort(remainingSelections);
	}

	protected void setupLabels()
	{
		categoryLabel = Templates.makeLabel("user:");
		currentLabel = Templates.makeLabel("is member of:");
		remainingLabel = Templates.makeLabel("other groups available:");
	}

	protected void commitSelectedItems()
	{
		try {
			UserGroupFunctions.editGroupsForUser(svrConn, id, UserGroupUtils.userGroupInfoToIdVector(currentSelections));
		} catch (Exception ex) {
			OneButton1Msg.showError(gui, "Edit groups for user error", ex.getMessage(), "ok",
			                        new Dimension(240, 100));
		}
		dispose();
	}

}
