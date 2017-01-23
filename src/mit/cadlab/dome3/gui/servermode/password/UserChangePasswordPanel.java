// UserChangePasswordPanel.java
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
public class UserChangePasswordPanel extends ChangePasswordPanel
{

	public static void showPanel(Component parent, ServerConnection svrConn)
	{
		UserChangePasswordPanel np = new UserChangePasswordPanel(svrConn, svrConn.getLoginName());
		JDialog d = DialogFactory.createDialog(parent, "Change Password: " + svrConn.getLoginName(), np, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public UserChangePasswordPanel(ServerConnection svrConn, String name)
	{
		super(svrConn, name);
		layoutComps();
	}

	protected void layoutComps()
	{

		JComponent[] comps = {Templates.makeLabel("old password:"), oldpwdfield,
		                      Templates.makeLabel("new password:"), newpwdfield1,
		                      newpwdfield2, makeButtonPanel()};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	protected boolean changePasswordOnServer()
	{
		try {
			UserGroupFunctions.changeUserPassword(svrConn,
			                                      LoginUtils.encryptPassword(oldpwdfield.getPassword()),
			                                      LoginUtils.encryptPassword(newpwdfield1.getPassword()));
			return true;
		} catch (Exception e) {
			OneButton1Msg.showError(UserChangePasswordPanel.this, "Server Password Error",
			                        e.getMessage(), "OK", new Dimension(220, 50));
			return false;
		}
	}
}
