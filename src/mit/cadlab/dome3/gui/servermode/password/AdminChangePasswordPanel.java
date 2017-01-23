// AdminChangePasswordPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.password;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: elaine
 * Date: Feb 13, 2003
 * Time: 3:36:50 PM
 * To change this template use Options | File Templates.
 */
public class AdminChangePasswordPanel extends ChangePasswordPanel
{

	private int id;

	public static void showPanel(Component parent, ServerConnection svrConn, int id, String name)
	{
		AdminChangePasswordPanel np = new AdminChangePasswordPanel(svrConn, id, name);
		JDialog d = DialogFactory.createDialog(parent, "Change Password: " + name, np, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public AdminChangePasswordPanel(ServerConnection svrConn, int id, String name)
	{
		super(svrConn, name);
		this.id = id;
		layoutComps();
	}

	protected void layoutComps()
	{

		JComponent[] comps = {Templates.makeLabel("new password:"), newpwdfield1,
		                      newpwdfield2, makeButtonPanel()};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, mit.cadlab.dome3.gui.servermode.password.ChangePasswordPanel.gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	protected boolean changePasswordOnServer()
	{
		try {
			UserGroupFunctions.changeUserPassword(svrConn, id, LoginUtils.encryptPassword(newpwdfield1.getPassword()));
			// todo: if password is changed and login info is saved, update it
			return true;
		} catch (Exception e) {
			OneButton1Msg.showError(AdminChangePasswordPanel.this, "Server Password Error",
			                        e.getMessage(), "OK", new Dimension(220, 50));
			return false;
		}
	}
}
