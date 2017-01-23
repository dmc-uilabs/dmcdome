// UserGroupMembershipPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DList;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Collections;

/**
 * Class used to view and edit the members of groups
 */
public abstract class UserGroupMembershipPanel
{
	public static final GridBagConstraints gbc = null;
	public static final Dimension DEFAULT_SIZE = new Dimension(550, 300);

	protected ServerConnection svrConn;
	protected int id;
	protected String name;
	protected JPanel gui;

	protected JLabel categoryLabel;
	protected JTextField nameTextField;
	protected JLabel currentLabel;
	protected JLabel remainingLabel;

	protected DArrayList currentSelections, remainingSelections;
	protected JList currentList, remainingList;

	public UserGroupMembershipPanel(ServerConnection svrConn, int id, String name)
	{
		this.svrConn = svrConn;
		this.id = id;
		this.name = name;
		setData();
		this.gui = makePanel();
		this.gui.setPreferredSize(DEFAULT_SIZE);
	}

	/**
	 * Get the data needed to populate this gui. Initialize currentSelections and remainingSelections
	 */
	protected abstract void setData();

	/**
	 * Initialize categoryLabel, currentLabel, remainingLabel
	 */
	protected abstract void setupLabels();

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		setupLabels();
		nameTextField = Templates.makeTextField(name);
		nameTextField.setEditable(false);

		currentList = Templates.makeDList(new MutableUserGroupListModel(currentSelections));
		remainingList = Templates.makeDList(new MutableUserGroupListModel(remainingSelections));
		JScrollPane currentScroll = new JScrollPane(currentList);
		JScrollPane remainingScroll = new JScrollPane(remainingList);

		JButton addButton = Templates.makeListArrowButton("left", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addItemsToSelection();
			}
		});
		JButton removeButton = Templates.makeListArrowButton("right", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeItemsFromSelection();
			}
		});
		JButton commitButton = Templates.makeButton("commit", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				commitSelectedItems();
			}
		});
		JButton cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		JComponent[] comps = {categoryLabel, nameTextField, currentLabel, remainingLabel,
		                      currentScroll, remainingScroll,
		                      addButton, removeButton,
		                      commitButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 8, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 4, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 8), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(8, 8, 0, 0), 0, 0),
			new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(8, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 8, 0, 0), 0, 0),
			new GridBagConstraints(3, 2, 2, 2, 0.8, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 8), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 4, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 0), 0, 0),
			new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 8), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	protected void dispose()
	{
		SwingUtilities.windowForComponent(gui).dispose();
	}

	protected void addItemsToSelection()
	{
		Object[] newItems = remainingList.getSelectedValues();
		remainingSelections.removeAll(remainingList.getSelectedIndices());
		currentSelections.addAll(newItems);
		Collections.sort(currentSelections);
		Collections.sort(remainingSelections);
	}

	protected void removeItemsFromSelection()
	{
		Object[] oldItems = currentList.getSelectedValues();
		currentSelections.removeAll(currentList.getSelectedIndices());
		remainingSelections.addAll(oldItems);
		Collections.sort(currentSelections);
		Collections.sort(remainingSelections);
	}

	/**
	 * send selected items to server
	 */
	protected abstract void commitSelectedItems();

}
