// DeployPlayspaceFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.gui.fileSystem.deploy.AbstractDeployFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

import javax.swing.tree.TreeSelectionModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DeployPlayspaceFileSystemTable extends AbstractDeployFileSystemTable
{
	protected static GridBagConstraints gbc;
	private JButton actionbutton;
	private JButton cancelbutton = Templates.makeButton("cancel");
	private JTextField tf = Templates.makeDTextField("");
	private String newFolderName;
	private String action;
	private ServerConnection conn;

	public DeployPlayspaceFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
		this.conn = conn;
	}

	public DeployPlayspaceFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
	{
		super(conn, scope, selectionModel);
		this.conn = conn;
	}

	/**
	 * This method creates the root folder for the tree table.
	 * @param scope the scope for the tree table
	 * @return the root folder based on the scope specified
	 */
	protected Folder createRootFolder(String scope)
	{
		Folder f = null;
		if (scope.equals(Folder.USER_HOME)) {
			f = DeployPlayspaceFolder.createUserRootFolder(svrConn);
		} else if (scope.equals(Folder.USERS_ROOT)) {
			f = DeployPlayspaceFolder.createUsersRootFolder(svrConn);
		} else if (scope.equals(Folder.GROUPS_ROOT)) {
			f = DeployPlayspaceFolder.createGroupsRootFolder(svrConn);
		} else if (scope.equals(Folder.SERVER_ROOT)) {
			f = DeployPlayspaceFolder.createServerRootFolder(svrConn);
		} else {
			throw new IllegalArgumentException("invalid scope: " + scope);
		}
		f.listChildren(svrConn);
		return f;
	}

	public void addFolder()
	{
		this.action = "add";
		Object folderId = getSelectedObjectId();
		if (null != folderId) {
			DeployPlayspaceFolder folder = (DeployPlayspaceFolder) getSelectedItem();
			newFolderDialog();
			if (!newFolderName.equals("")) {
				int newFolderId = FileSystemFunctions.createPlayspaceFolder(conn, ((Integer) folderId).intValue(), newFolderName);
				folder.addContent(folder.createFolder(new Integer(newFolderId), newFolderName));
				folder.refresh(conn);
			}
		}

	}


	public void deleteSelectedItem()
	{
		//todo needs to work on more than just folders
		try {
			Object object = getSelectedItem();
			if (null != object) {
				if (object instanceof Folder) {
					Folder folder = (Folder) object;
					FileSystemFunctions.deletePlayspaceFolder(conn, folder.getIntId());
					folder.getParent().deleteContent(folder);
					folder.getParent().refresh(conn);
				} else if (object instanceof DomeFile) {
					DomeFile dFile = (DomeFile) object;
					if (dFile.getType().equalsIgnoreCase("playspace"))
						FileSystemFunctions.deletePlayspace(conn, (String) dFile.getId());
					else if (dFile.getType().equalsIgnoreCase("project"))
						FileSystemFunctions.deleteProject(conn, (String) dFile.getId());
					else if (dFile.getType().equalsIgnoreCase(DbConstants.INTERFACE_TYPE)) {
						System.out.println("cannot delete model interfaces");
						return;
					}
					dFile.getParent().deleteContent(dFile);
					dFile.getParent().refresh(conn);

				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void renameFolder()
	{
		this.action = "rename";
		Object folderId = getSelectedObjectId();
		if (null != folderId) {
			DeployPlayspaceFolder folder = (DeployPlayspaceFolder) getSelectedItem();
			tf.setText(folder.getName());
			newFolderDialog();
			if (!newFolderName.equals("")) {
				FileSystemFunctions.renamePlayspaceFolder(conn, ((Integer) folderId).intValue(), newFolderName);
				folder.getParent().refresh(conn);
			}
		}
	}

	/* public void refresh(){
	     Object folderId = getSelectedObjectId();
	     if (null != folderId) {
	         DeployPlayspaceFolder folder = (DeployPlayspaceFolder) getSelectedItem();
	         folder.refresh(conn);
	     }
	 }*/

	private void newFolderDialog()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {Templates.makeLabel("name:"), tf,
		                      makeButtonPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 5, 5), 0, 0)};
		Templates.layoutGridBag(p, comps, gbcs);

		final JDialog d = DialogFactory.createDialog(this, "New Folder", p, true, false);
		actionbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				d.dispose();
				newFolderName = tf.getText();
				tf.setBackground(Color.WHITE);
			}
		});

		cancelbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				d.dispose();
				newFolderName = "";
				tf.setBackground(Color.WHITE);
			}
		});
		d.show();
	}

	private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalGlue());
		p.add(actionbutton = Templates.makeButton(this.action));
		p.add(Box.createHorizontalStrut(5));
		p.add(cancelbutton);
		p.add(Box.createHorizontalStrut(5));
		return p;
	}

}
