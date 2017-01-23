// PermissionsPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package Permissions;

import mit.cadlab.dome.swing.DialogFactory;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.swing.DList;
import mit.cadlab.dome.util.DArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

/**
 * This class is the main permissions panel.
 */
public class PermissionsPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(400, 400);
	public static final GridBagConstraints gbc = null;
	private static JDialog d;
	static Vector allUserGroup = new Vector();      //store the information of all users or groups
	private Vector permTypes = new Vector();      //Permission types
	private Vector permDepd = new Vector();      //Permission dependencies
	private Vector permLabels = new Vector();      //Permission Label componets
	private Vector permBoxes = new Vector();       //Permission CheckBox componets
	private int whichBox = 0;
	private JPanel gui;
	private Object answer = null; // default answer; set this when submit button is hit
	private JButton addButton;
	private JButton removeButton;
	private DList permUGList;
	private PermissionListModel listModel;
	private JButton okButton;
	private JButton cancelButton;
	private boolean permitted;

	public static Object showPanel(Component parent, Vector permissionTypes,
	                               Vector permissions, Vector usersAndGroups, Vector permDependencies)
	{
		PermissionsPanel p = new PermissionsPanel(permissionTypes, permissions, usersAndGroups, permDependencies);
		d = DialogFactory.createDialog(parent, "Permissions panel", p.gui, true, true);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setSize(DEFAULT_SIZE);
		d.show();
		return p.answer;
	}


	private PermissionsPanel(Vector permissionTypes, Vector permissions, Vector usersAndGroups, Vector permDependencies)
	{
		permTypes = permissionTypes;
		permDepd = permDependencies;
		allUserGroup = PermissionUtils.converttoUserGroupInfo(permissionTypes, permissions, usersAndGroups);
		// store and process necessary data
		gui = makePanel();
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		// add GUI items here
		JLabel userGroupsLabel = Templates.makeLabel("user or groups:");
		listModel = new PermissionListModel(makeListModel());
		permUGList = Templates.makeDList(listModel);
		permUGList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane userGroupScroll = new JScrollPane(permUGList);

		JScrollPane permissionsScroll = new JScrollPane(makePermissionPanel());
		JSplitPane dataPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		dataPane.add(userGroupScroll, JSplitPane.TOP);
		dataPane.add(permissionsScroll, JSplitPane.BOTTOM);
		dataPane.setDividerLocation(120);

		permUGList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting() == false) {

					if (permUGList.getSelectedIndex() == -1) {
						//No selection, disable button.
						removeButton.setEnabled(false);
						for (int i = 0; i < permBoxes.size(); i++) {
							((JCheckBox) permBoxes.elementAt(i)).setEnabled(false);
							((JCheckBox) permBoxes.elementAt(i)).setSelected(false);
						}

					}
					else {
						//Selection, update text field.
						removeButton.setEnabled(true);
						//int i = PermissionUtils.getUserGroup(allUserGroup, permUGList.getSelectedValue().toString());
						for (int j = 0; j < permBoxes.size(); j++) {
							((JCheckBox) permBoxes.elementAt(j)).setEnabled(true);
							((JCheckBox) permBoxes.elementAt(j)).setSelected(((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[j]);
						}

					}
				}
			}
		});

		//format
		JComponent[] comps = {userGroupsLabel, makeAddRemovePanel(), dataPane, makeOKPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};


		Templates.layoutGridBag(p, comps, gbcs);


		if (listModel.getSize() == 0) {
			removeButton.setEnabled(false);
			for (int i = 0; i < permBoxes.size(); i++) {
				((JCheckBox) permBoxes.elementAt(i)).setEnabled(false);
				((JCheckBox) permBoxes.elementAt(i)).setSelected(false);
			}

		}
		else {
			permUGList.setSelectedIndex(0);
		}

		return p;
	}

	private JPanel makePermissionPanel()
	{
		JPanel p = new JPanel();

		JLabel permissionTitle = Templates.makeLabel("permissions", Templates.FONT11I);
		JLabel allowTitle = Templates.makeLabel("allow", Templates.FONT11I);

		JComponent[] comps = new JComponent[permTypes.size() * 2 + 2];
		comps[0] = permissionTitle;
		comps[1] = allowTitle;
		GridBagConstraints[] gbcs = new GridBagConstraints[permTypes.size() * 2 + 2];
		gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
		gbcs[1] = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0);

		for (int i = 0; i < permTypes.size(); i++) {
			permLabels.addElement(Templates.makeLabel(((Vector) permTypes.elementAt(i)).elementAt(1).toString()));
			permBoxes.addElement(Templates.makeCheckBox());
			comps[i * 2 + 2] = (JComponent) permLabels.elementAt(i);
			comps[i * 2 + 3] = (JComponent) permBoxes.elementAt(i);
			gbcs[i * 2 + 2] = new GridBagConstraints(0, i + 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
			gbcs[i * 2 + 3] = new GridBagConstraints(1, i + 1, 1, 1, 0.0, 1.0, gbc.NORTH, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0);

		}

		Templates.layoutGridBag(p, comps, gbcs);

		for (whichBox = 0; whichBox < permBoxes.size(); whichBox++) {
			((JCheckBox) permBoxes.elementAt(whichBox)).addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int currentBox = permBoxes.indexOf(e.getSource());
					((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[currentBox] =
					        ((JCheckBox) permBoxes.elementAt(currentBox)).isSelected();
					//handle permission dependencies
					if (((JCheckBox) permBoxes.elementAt(currentBox)).isSelected() == true) {
						for (int m = 0; m < permDepd.size(); m++) {
							if (((UserGroupInfo) permUGList.getSelectedValue()).getPermissionID()[currentBox] ==
							        ((Integer) ((Vector) permDepd.elementAt(m)).elementAt(0)).intValue()) {
								int dependedIndex = PermissionUtils.getArrayIndex(((UserGroupInfo) permUGList.getSelectedValue()).getPermissionID(),
								        ((Integer) ((Vector) permDepd.elementAt(m)).elementAt(1)).intValue());
								if (dependedIndex != -1) {
									((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[dependedIndex] = true;
									((JCheckBox) permBoxes.elementAt(dependedIndex)).setSelected(true);
								}
							}
						}
					}
				}
			});
		}

		return p;
	}

	private JPanel makeOKPanel()
	{
		JPanel p = new JPanel();
		okButton = Templates.makeButton("ok", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Vector ugReturn = new Vector();
				for (int i = 0; i < allUserGroup.size(); i++) {
					for (int j = 0; j < ((UserGroupInfo) allUserGroup.elementAt(i)).getPermissionID().length; j++) {
						if (((UserGroupInfo) allUserGroup.elementAt(i)).getPermissions()[j] == true) {
							Vector temp = new Vector();
							temp.addElement(new Integer(((UserGroupInfo) allUserGroup.elementAt(i)).getId()));
							temp.addElement(new Integer(((UserGroupInfo) allUserGroup.elementAt(i)).getPermissionID()[j]));
							ugReturn.addElement(temp);
						}
					}
				}
				answer = ugReturn;
				dispose();
			}
		});
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		cancelButton.setMnemonic(KeyEvent.VK_C);

		JComponent[] comps = {okButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeAddRemovePanel()
	{
		JPanel p = new JPanel();
		addButton = Templates.makeButton("add", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				UserGroupChooser.initialize(d, "UserGroup Chooser", "user or groups", allUserGroup, listModel);

				Vector selectedNames = UserGroupChooser.showDialog(d);
				if (selectedNames.size() == 0) {

				}
				else {
					for (int i = 0; i < selectedNames.size(); i++) {
						listModel.addElement(selectedNames.elementAt(i));
					}
					permUGList.setSelectedIndex(listModel.getSize() - 1);
				}

			}
		});
		addButton.setMnemonic(KeyEvent.VK_A);

		removeButton = Templates.makeButton("remove", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int index = permUGList.getSelectedIndex();
				//clear the permissions of the selected item
				for (int j = 0; j < ((UserGroupInfo) permUGList.getSelectedValue()).getPermissions().length; j++) {
					((UserGroupInfo) permUGList.getSelectedValue()).getPermissions()[j] = false;
				}
				listModel.remove(index);
				//System.err.println(permUGList.getSelectedIndex() );
				//System.err.println(permUGList.getSelectedValue());
				int size = listModel.getSize();

				if (size == 0) {
					removeButton.setEnabled(false);
					for (int i = 0; i < permBoxes.size(); i++) {
						((JCheckBox) permBoxes.elementAt(i)).setEnabled(false);
						((JCheckBox) permBoxes.elementAt(i)).setSelected(false);
					}

				}
				else {
					//Adjust the selection. list compone select nothing after removing an item.
					if (index == listModel.getSize())//removed item in last position
						index--;
					permUGList.setSelectedIndex(index);   //otherwise select same index

				}

			}
		});
		removeButton.setMnemonic(KeyEvent.VK_R);

		JComponent[] comps = {addButton, removeButton};
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


	protected DArrayList makeListModel()
	{
		Vector result = new Vector();
		if (allUserGroup.size() != 0) {
			for (int i = 0; i < allUserGroup.size(); i++) {
				permitted = false;
				for (int j = 0; j < ((UserGroupInfo) allUserGroup.elementAt(i)).getPermissions().length; j++) {
					if (((UserGroupInfo) allUserGroup.elementAt(i)).getPermissions()[j] == true) {
						permitted = true;
					}
				}
				if (permitted == true) {
					result.addElement(allUserGroup.elementAt(i));
				}
			}
		}
		return new DArrayList(result);
	}

}
