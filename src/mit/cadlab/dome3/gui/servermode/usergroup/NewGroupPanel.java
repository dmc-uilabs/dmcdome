// NewGroupPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGroupPanel
{
	static final Dimension DEFAULT_SIZE = new Dimension(300, 150);

	private JPanel gui;
	private JTextField namefield = Templates.makeTextField("");
	private JTextField descriptionfield = Templates.makeTextField("");
	private JCheckBox saveModelCheckBox = Templates.makeCheckBox("can save models");
	private JCheckBox savePlayspaceCheckBox = Templates.makeCheckBox("can save playspaces");
	private JButton addbutton = Templates.makeButton("add");
	private JButton cancelbutton = Templates.makeButton("cancel");

	private ServerConnection svrConn;
	protected static GridBagConstraints gbc;
	private boolean createdGroup = false;

	public static boolean showPanel(Component parent, ServerConnection svrConn)
	{
		NewGroupPanel np = new NewGroupPanel(svrConn);
		JDialog d = DialogFactory.createDialog(parent, "New group", np.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return np.createdGroup;
	}

	public NewGroupPanel(ServerConnection svrConn)
	{
		this.svrConn = svrConn;
		gui = makeDataPanel();
		gui.setPreferredSize(DEFAULT_SIZE);
	}

	private JPanel makeDataPanel()
	{
		ImageIcon labelIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/group.gif");
		JLabel iconLabel = new JLabel(labelIcon, SwingConstants.LEFT);
		iconLabel.setOpaque(false);

		JPanel p = new JPanel();
		JComponent[] comps = {Templates.makeLabel("name:"), namefield,
		                      Templates.makeLabel("description:"), descriptionfield,
		                      saveModelCheckBox, savePlayspaceCheckBox, iconLabel,
		                      makeButtonPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 1, 2, 0.0, 1.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 5, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);

		addbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				createNewGroup();
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

	private void createNewGroup()
	{
		String name = new String(namefield.getText());

		if (name.equals("")) {
			OneButton1Msg.showWarning(NewGroupPanel.this.gui, "No user name",
			                          "The group name cannot be blank. Please try again", "OK", new Dimension(125, 50));
			return;
		}
		try {
			UserGroupFunctions.createNewGroup(svrConn, namefield.getText(), descriptionfield.getText(),
			                                  saveModelCheckBox.isSelected(), savePlayspaceCheckBox.isSelected());
			createdGroup = true;
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
		OneButton2Msg.showWarning(gui, "Create group warning", "already exists on the server", name,
		                          "ok", new Dimension(240, 100));
	}
}
