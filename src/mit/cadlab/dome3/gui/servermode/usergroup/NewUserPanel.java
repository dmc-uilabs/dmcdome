// NewUserPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewUserPanel
{
	static final Dimension DEFAULT_SIZE = new Dimension(300, 190);

	private JPanel gui;
	private JTextField namefield = Templates.makeTextField("");
	private JTextField descriptionfield = Templates.makeTextField("");
	private JPasswordField pwdfield1 = new JPasswordField(25);
	private JPasswordField pwdfield2 = new JPasswordField(25);
	private JCheckBox saveModelCheckBox = Templates.makeCheckBox("can save models");
	private JCheckBox savePlayspaceCheckBox = Templates.makeCheckBox("can save playspaces");
	private JButton addbutton = Templates.makeButton("add");
	private JButton cancelbutton = Templates.makeButton("cancel");

	private ServerConnection svrConn;
	protected static GridBagConstraints gbc;
	private boolean createdUser = false;

	public static boolean showPanel(Component parent, ServerConnection svrConn)
	{
		NewUserPanel np = new NewUserPanel(svrConn);
		JDialog d = DialogFactory.createDialog(parent, "New user", np.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return np.createdUser;
	}

	public NewUserPanel(ServerConnection svrConn)
	{
		this.svrConn = svrConn;
		gui = makeDataPanel();
		gui.setPreferredSize(DEFAULT_SIZE);
	}

	private JPanel makeDataPanel()
	{
		JPanel p = new JPanel();

		ImageIcon labelIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/user.gif");
		JLabel iconLabel = new JLabel(labelIcon, SwingConstants.LEFT);
		iconLabel.setOpaque(false);

		JComponent[] comps = {Templates.makeLabel("name:"), namefield,
		                      Templates.makeLabel("description:"), descriptionfield,
		                      Templates.makeLabel("password:"), pwdfield1, pwdfield2,
		                      saveModelCheckBox, savePlayspaceCheckBox, iconLabel,
		                      makeButtonPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 6, 1, 2, 0.0, 1.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 6, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 5, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);

		addbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				createNewUser();
			}
		});

		cancelbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		return p;
	}

	private void createNewUser()
	{
		String pwd1 = new String(pwdfield1.getPassword());
		String pwd2 = new String(pwdfield2.getPassword());
		String name = new String(namefield.getText());

		if (name.equals("")) {
			OneButton1Msg.showWarning(NewUserPanel.this.gui, "No user name",
			                          "The user name cannot be blank. Please try again", "OK", new Dimension(125, 50));
			return;
		}
		if (!pwd1.equals(pwd2)) {
			OneButton1Msg.showWarning(NewUserPanel.this.gui, "Mismatched passwords",
			                          "The passwords did not match. Please try again", "OK", new Dimension(125, 50));
		} else if (pwd1.equals("")) {
			OneButton1Msg.showWarning(NewUserPanel.this.gui, "Invalid passwords",
			                          "Passwords can not be empty. Please try again", "OK", new Dimension(125, 50));
		} else {
			try {
				UserGroupFunctions.createNewUser(svrConn, namefield.getText(), descriptionfield.getText(),
				                                 LoginUtils.encryptPassword(pwd1),
				                                 saveModelCheckBox.isSelected(), savePlayspaceCheckBox.isSelected());
				createdUser = true;
				dispose();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnreachableServerException ex) {
				showConnectionErrorMsg();
			} catch (ServerMethodException ex) {
				// todo: check this is the only kind of error
				showDuplicateNameWarning(namefield.getText());
			}
		}
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(gui).dispose();
	}

	private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalGlue());
		p.add(addbutton);
		p.add(Box.createHorizontalStrut(5));
		p.add(cancelbutton);
		p.add(Box.createHorizontalStrut(5));
		return p;
	}

	protected void showConnectionErrorMsg()
	{
		OneButton1Msg.showError(gui, "Connection error", "Cannot connect to the specified server!", "ok",
		                        new Dimension(240, 100));
	}

	protected void showDuplicateNameWarning(String name)
	{
		OneButton2Msg.showWarning(gui, "Create user warning", "already exists on the server", name,
		                          "ok", new Dimension(240, 100));
	}
}
