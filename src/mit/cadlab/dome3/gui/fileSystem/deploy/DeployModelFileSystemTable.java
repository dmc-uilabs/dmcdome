// DeployModelFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.fileSystem.deploy.AbstractDeployFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeployModelFileSystemTable extends AbstractDeployFileSystemTable
{
	protected static GridBagConstraints gbc;
	private JButton actionbutton;
	private JButton cancelbutton = Templates.makeButton("cancel");
	private JTextField tf = Templates.makeDTextField("");
	private String newFolderName;
	private String action;
	private ServerConnection conn;

	public DeployModelFileSystemTable(ServerConnection conn, String scope)
	{
		super(conn, scope);
		this.conn = conn;
	}

	public DeployModelFileSystemTable(ServerConnection conn, String scope, TreeSelectionModel selectionModel)
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
			f = DeployModelFolder.createUserRootFolder(svrConn);
		} else if (scope.equals(Folder.USERS_ROOT)) {
			f = DeployModelFolder.createUsersRootFolder(svrConn);
		} else if (scope.equals(Folder.GROUPS_ROOT)) {
			f = DeployModelFolder.createGroupsRootFolder(svrConn);
		} else if (scope.equals(Folder.SERVER_ROOT)) {
			f = DeployModelFolder.createServerRootFolder(svrConn);
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
			DeployModelFolder folder = (DeployModelFolder) getSelectedItem();
			this.newFolderDialog();
			if (!newFolderName.equals("")) {
				int newFolderId = FileSystemFunctions.createModelFolder(conn, ((Integer) folderId).intValue(), newFolderName);
				folder.addContent(folder.createFolder(new Integer(newFolderId), newFolderName));
				folder.refresh(conn);
			}
		}

	}

	public void deleteSelectedItem()
	{
		//todo needs to work on more than just folders
		try
        {
            Object object = getSelectedItem();
            if (null != object)
            {
                if (object instanceof Folder)
                {
                    Folder folder = (Folder) object;
                    FileSystemFunctions.deleteModelFolder(conn, folder.getIntId());
                    folder.getParent().deleteContent(folder);
                    folder.getParent().refresh(conn);
                }
                else if (object instanceof DomeFile)
                {
                    DomeFile dFile = (DomeFile) object;
                    if (dFile.getType().equalsIgnoreCase(DbConstants.MODEL_TYPE))
                        FileSystemFunctions.deleteModel(conn, (String) dFile.getId());
                    else if (dFile.getType().equalsIgnoreCase(DbConstants.PROJECT_TYPE))
                        FileSystemFunctions.deleteProject(conn, (String) dFile.getId());
                    //todo need to finish this (project)

                    else if (dFile.getType().equals(DomeFile.ANALYSIS_TOOL_TYPE))
                    {
                        FileSystemFunctions.deleteAnalysisTool(conn, (String) dFile.getId());
                    }

                    else if (dFile.getType().equalsIgnoreCase(DbConstants.INTERFACE_TYPE))
                    {
                        OneButton1Msg.showWarning(null, "server warning", "can not delete interfaces", "ok", new Dimension(150, 75));
                        return;
                    }


                    dFile.getParent().deleteContent(dFile);
                    dFile.getParent().refresh(conn);

                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
	}

	public void renameFolder()
	{
		this.action = "rename";
        Object objectId = getSelectedObjectId();
        if (null != objectId)
        {
            Object item = getSelectedItem();
            if (item instanceof Folder)
            {
                Folder folder = (Folder) item;
                tf.setText(folder.getName());
                newFolderDialog();
                if (!newFolderName.equals(""))
                {
                    FileSystemFunctions.renameModelFolder(conn, ((Integer) objectId).intValue(), newFolderName);
                    folder.getParent().refresh(conn);
                }
            }
            else
                OneButton1Msg.showWarning(null, "server warning",
                        "models must be re-deployed to be renamed", "ok", new Dimension(150, 75));
        }
	}

	/* public void refresh() {
	     Object folderId = getSelectedObjectId();
	     if (null != folderId) {
	         Folder folder = (Folder) getSelectedItem();
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

		final JDialog d = DialogFactory.createDialog(this, "New folder", p, true, false);
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
				tf.setBackground(Color.WHITE);
				newFolderName = "";
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
