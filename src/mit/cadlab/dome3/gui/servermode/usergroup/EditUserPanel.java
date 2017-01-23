// EditUserPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.gui.servermode.password.AdminChangePasswordPanel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class EditUserPanel
{

	private static final Dimension DEFAULT_SIZE = new Dimension(300, 250);

	private JPanel gui;
	private JTextField namefield;
	private JTextField descriptionfield;
	private JButton pwdbutton = Templates.makeButton("change password");
	private JButton groupbutton = Templates.makeButton("view/edit groups");
	private JCheckBox saveModelCheckBox = Templates.makeCheckBox("can save models");
	private JCheckBox savePlayspaceCheckBox = Templates.makeCheckBox("can save playspaces");
	private JRadioButton activebutton = Templates.makeRadioButton("active");
	private JRadioButton inactivebutton = Templates.makeRadioButton("inactive");
	private JButton okbutton = Templates.makeButton("ok");
	private JButton cancelbutton = Templates.makeButton("cancel");


	protected static GridBagConstraints gbc;

	int id;
	String name;
	String desc;
	String status;
	boolean canSaveModels;
	boolean canSavePlayspaces;
	Vector grpInfo;
	private ServerConnection svrConn;
	private boolean editedUser = false;

	public static boolean showPanel(Component parent, ServerConnection svrConn, Vector data)
	{
		EditUserPanel np = new EditUserPanel(svrConn, data);
		JDialog d = DialogFactory.createDialog(parent, "Edit User", np.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return np.editedUser;
	}

	public EditUserPanel(ServerConnection svrConn, Vector data)
	{
		this(svrConn, ((Integer) data.get(0)).intValue(), // skip type
		     (String) data.get(2), (String) data.get(3), (String) data.get(4),
		     ((Boolean) data.get(5)).booleanValue(), ((Boolean) data.get(6)).booleanValue());
	}

	public EditUserPanel(ServerConnection svrConn, int id, String name, String desc,
	                     String status, boolean canSaveModels, boolean canSavePlayspaces)
	{
		this.svrConn = svrConn;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.status = status;
		this.canSaveModels = canSaveModels;
		this.canSavePlayspaces = canSavePlayspaces;
		this.gui = makeDataPanel();
		this.gui.setPreferredSize(DEFAULT_SIZE);
	}

	private JPanel makeDataPanel()
	{
		namefield = Templates.makeDTextField(name);
		namefield.setEditable(false);
		descriptionfield = Templates.makeDTextField(desc);
		if (canSaveModels)
			saveModelCheckBox.setSelected(true);
		if (canSavePlayspaces)
			savePlayspaceCheckBox.setSelected(true);
		if (status.equals("ACTIVE"))
			activebutton.setSelected(true);
		else
			inactivebutton.setSelected(true);

		JPanel p = new JPanel();
		JComponent[] comps = {Templates.makeLabel("name:"), namefield,
		                      Templates.makeLabel("description:"), descriptionfield,
		                      pwdbutton, groupbutton, saveModelCheckBox, savePlayspaceCheckBox,
		                      Templates.makeLabel("status:"), makeRadioButtonPanel(),
		                      makeButtonPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);

		pwdbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AdminChangePasswordPanel.showPanel(EditUserPanel.this.gui, svrConn, id, name);
			}
		});

		groupbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ViewEditGroupsForUserPanel.showPanel(EditUserPanel.this.gui, svrConn, id, name);
			}
		});

		okbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				editUser();
			}
		});

		cancelbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		ButtonGroup bg = new ButtonGroup();
		bg.add(activebutton);
		bg.add(inactivebutton);

		return p;
	}

	private JPanel makeRadioButtonPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(activebutton);
		p.add(Box.createHorizontalStrut(5));
		p.add(inactivebutton);
		return p;
	}

	private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {okbutton, cancelbutton};

		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0)};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private void editUser()
	{
		try {
			if (desc.equals(descriptionfield.getText()) && (canSaveModels == saveModelCheckBox.isSelected()) && (canSavePlayspaces == savePlayspaceCheckBox.isSelected()) &&
			        ((status.equals("ACTIVE") && activebutton.isSelected()) || (status.equals("INACTIVE") && inactivebutton.isSelected()))) {
				dispose(); // no changes
				return;
			}
			UserGroupFunctions.editUserGroupInfo(svrConn, id, namefield.getText(), descriptionfield.getText(), saveModelCheckBox.isSelected(), savePlayspaceCheckBox.isSelected(),
			                                     (activebutton.isSelected() ? "ACTIVE" : "INACTIVE"));
			editedUser = true;
			dispose();
		} catch (Exception ex) {
			OneButton1Msg.showError(gui, "Edit user error", ex.getMessage(), "ok",
			                        new Dimension(240, 100));
		}
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(gui).dispose();
	}

}
