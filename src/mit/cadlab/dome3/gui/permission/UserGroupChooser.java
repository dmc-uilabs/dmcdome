// UserGroupChooser.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.permission;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.gui.servermode.usergroup.MutableUserGroupListModel;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This gui allows one to select users and groups from a given list.
 */
public class UserGroupChooser extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(250, 250);
	public static final GridBagConstraints gbc = null;

	private JList list;
	private JButton addButton;
	private JPanel gui;
	private DArrayList selectedNames;

	/**
	 * Show the user/group chooser dialog.
	 * @param parent the parent component for the dialog.
	 * @param allUsersGroups list of all users/groups to choose from. Elements of list must be UserGroupCommonInfo
	 * @return list of selected users/groups (empty list if none selected)
	 */
	public static java.util.List showDialog(Component parent, DArrayList allUsersGroups)
	{
		UserGroupChooser c = new UserGroupChooser(allUsersGroups);
		JDialog d = DialogFactory.createDialog(parent, "User/Group Chooser", c.gui, true, true);
		d.getRootPane().setDefaultButton(c.addButton);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setSize(DEFAULT_SIZE);
		d.show();
		return c.selectedNames;
	}

	private UserGroupChooser(DArrayList allUsersGroups)
	{
		gui = new JPanel();
		JLabel userGroupsLabel = Templates.makeLabel("users or groups");
		list = Templates.makeDList(new MutableUserGroupListModel(allUsersGroups));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2) {
					addButton.doClick();
				}
			}
		});
		JScrollPane listScroller = new JScrollPane(list);

		JComponent[] comps = {userGroupsLabel, listScroller, makeOKPanel(allUsersGroups.isEmpty())};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(gui, comps, gbcs);
	}

	private JPanel makeOKPanel(boolean userGroupListIsEmpty)
	{
		JPanel p = new JPanel();
		addButton = Templates.makeButton("add", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedNames = new DArrayList(list.getSelectedValues());
				dispose();
			}
		});
		if (userGroupListIsEmpty)
			addButton.setEnabled(false);
		else
			addButton.setMnemonic(KeyEvent.VK_A);
		JButton cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		cancelButton.setMnemonic(KeyEvent.VK_C);

		JComponent[] comps = {addButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(gui).dispose();
	}

}
