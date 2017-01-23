// ChangePasswordPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.password;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Feb 12, 2003
 * Time: 4:42:36 PM
 * To change this template use Options | File Templates.
 */
public abstract class ChangePasswordPanel extends JPanel
{
	protected ServerConnection svrConn;
	protected String name;

	protected static GridBagConstraints gbc;
	protected JPasswordField oldpwdfield = new JPasswordField(20);
	protected JPasswordField newpwdfield1 = new JPasswordField(20);
	protected JPasswordField newpwdfield2 = new JPasswordField(20);
	protected JButton okbutton = Templates.makeButton("ok");
	protected JButton cancelbutton = Templates.makeButton("cancel");

	public ChangePasswordPanel(ServerConnection svrConn, String name)
	{
		this.svrConn = svrConn;
		this.name = name;
	}

	protected JPanel makeButtonPanel()
	{

		cancelbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SwingUtilities.windowForComponent(ChangePasswordPanel.this).dispose();
			}
		});

		okbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (checkPassword()) {
					if (changePasswordOnServer())
						SwingUtilities.windowForComponent(ChangePasswordPanel.this).dispose();
				}
			}
		});

		JPanel p = new JPanel();

		JComponent[] comps = {okbutton, cancelbutton};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	protected abstract boolean changePasswordOnServer();

	protected boolean checkPassword()
	{
		String pwd1 = new String(newpwdfield1.getPassword());
		String pwd2 = new String(newpwdfield2.getPassword());
		if (!pwd1.equals(pwd2)) {
			OneButton1Msg.showWarning(ChangePasswordPanel.this, "Mismatched passwords",
			                          "The passwords did not match. Please try again", "OK", new Dimension(125, 50));
			return false;
		} else if (pwd1.length() == 0) {
			OneButton1Msg.showWarning(ChangePasswordPanel.this, "Invalid passwords",
			                          "Passwords can not be empty. Please try again", "OK", new Dimension(125, 50));
			return false;
		} else {
			return true;
		}
	}
}