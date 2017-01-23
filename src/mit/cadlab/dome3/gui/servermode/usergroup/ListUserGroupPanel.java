// ListUserGroupPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Feb 11, 2003
 * Time: 3:23:53 PM
 * To change this template use Options | File Templates.
 */
public abstract class ListUserGroupPanel extends JPanel
{

	private static final Dimension DEFAULT_SIZE = new Dimension(400, 300);
	protected List data;
	protected ServerConnection svrConn;

	protected static GridBagConstraints gbc;
	protected JTable t;
	protected ListSelectionModel listSelModel;
	protected ListSelectionListener listSelListener;
	protected JButton closeButton = Templates.makeButton("close");

	public ListUserGroupPanel(ServerConnection svrConn)
	{
		this.svrConn = svrConn;
		this.setLayout(new BorderLayout());
		t = new JTable();
		loadData();
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.addMouseListener(createMouseListener());
		listSelModel = t.getSelectionModel();
		listSelListener = createListSelectionListener();
		t.getSelectionModel().addListSelectionListener(listSelListener);
		JScrollPane sp = new JScrollPane(t);
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		JLabel iconLabel = getImageLabel();

		JComponent[] comps = {sp, iconLabel, closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(this, comps, gbcs);

		this.setPreferredSize(DEFAULT_SIZE);
	}

	public void dispose()
	{
		SwingUtilities.windowForComponent(ListUserGroupPanel.this).dispose();
	}


	protected MouseListener createMouseListener()
	{
		return new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2) {
					edit();
				}
			}
		};
	}

	public int getCurrentId()
	{
		int rowIndex = t.getSelectedRow();
		if (rowIndex == -1)
			return -1;
		return ((UserGroupCommonInfo) data.get(rowIndex)).getId();
	}

	public String getCurrentName()
	{
		int rowIndex = t.getSelectedRow();
		if (rowIndex == -1)
			return "";
		return ((UserGroupCommonInfo) data.get(rowIndex)).getName();
	}

	protected ListSelectionListener createListSelectionListener()
	{
		return new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				setDeleteMIState();
			}
		};
	}

	protected abstract JLabel getImageLabel();

	protected abstract void setDeleteMIState();

	public void edit()
	{
		int id = getCurrentId();
		if (id == -1)
			return;
		String name = getCurrentName();
		editAction(id, name);
	}

	protected abstract void editAction(int id, String name);

	public void delete()
	{
		int id = getCurrentId();
		if (id == -1)
			return;
		String name = getCurrentName();
		deleteAction(id, name);
	}

	protected abstract void deleteAction(int id, String name);

	public abstract void loadData();

	protected void loadData(List data)
	{
		this.data = data;
		t.setModel(new UserGroupInfoTableModel(data));

		//format the table
		t.getColumnModel().getColumn(0).setMaxWidth(21);
		t.getColumnModel().getColumn(0).setResizable(false);
		DomeTable.customizeTable(t);
		t.setRowHeight(20);
	}

	protected void showConnectionErrorMsg()
	{
		OneButton1Msg.showError(this, "Login error", "Cannot connect to the specified server!", "ok",
		                        new Dimension(240, 100));
	}

	protected void showDeleteWarning(String name)
	{
		OneButton2Msg.showWarning(this, "Delete warning", "does not exist on the server", name,
		                          "ok", new Dimension(240, 100));
	}
}
