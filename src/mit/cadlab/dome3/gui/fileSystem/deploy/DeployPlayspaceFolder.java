// ModelFolder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.functions.DeployFileSystemFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

import java.util.Vector;

public class DeployPlayspaceFolder extends Folder
{
	public static DeployPlayspaceFolder createFolder(Integer id, String name)
	{
		return new DeployPlayspaceFolder(FOLDER, id, name);
	}

	public static DeployPlayspaceFolder createUserFolder(Integer id, String name)
	{
		return new DeployPlayspaceFolder(USER_HOME, id, name);
	}

	public static DeployPlayspaceFolder createGroupFolder(Integer id, String name)
	{
		return new DeployPlayspaceFolder(GROUP_HOME, id, name);
	}

	public static DeployPlayspaceFolder createUserRootFolder(ServerConnection svrConn)
	{
		return new DeployPlayspaceFolder(USER_HOME, svrConn);
	}

	public static DeployPlayspaceFolder createUsersRootFolder(ServerConnection svrConn)
	{
		return new DeployPlayspaceFolder(USERS_ROOT, svrConn);
	}

	public static DeployPlayspaceFolder createGroupsRootFolder(ServerConnection svrConn)
	{
		return new DeployPlayspaceFolder(GROUPS_ROOT, svrConn);
	}

	public static DeployPlayspaceFolder createServerRootFolder(ServerConnection svrConn)
	{
		return new DeployPlayspaceFolder(SERVER_ROOT, svrConn);
	}

	protected DeployPlayspaceFolder(String folderType, Integer id, String name)
	{
		super(folderType, id, name);
	}

	protected DeployPlayspaceFolder(String folderType, ServerConnection svrConn)
	{
		super(folderType);
		listChildren(svrConn); // load root folders right away
	}


	protected void loadUserOrGroupHome(ServerConnection svrConn)
	{
		Vector v = null;

		if (getIntId() >= 0)
			v = DeployFileSystemFunctions.getUserGroupPlayspaceFolders(svrConn, getIntId()); //expand a user
		else
			v = DeployFileSystemFunctions.getUserPlayspaceSpace(svrConn); //expand the root

		Integer folderDbId = (Integer) v.get(0);
		if (folderDbId.intValue() > 0) { //there is a Public Folder
			this.addContent(createFolder(folderDbId, "Public"));
			folderDbId = (Integer) v.get(1);
			if (folderDbId.intValue() > 0) { //there is a Private Folder

				this.addContent(createFolder(folderDbId, "Private"));
			}
		}
	}

	protected void loadUsersRoot(ServerConnection svrConn)
	{
		Vector aUser;
		String userName;
		String loginType = svrConn.getLoginType();
		String _connId = svrConn.getConnectionId();
		Integer userDbId;
		Vector v = DeployFileSystemFunctions.getUserPlayspaceSpacesList(svrConn);
		for (int i = 0; i < v.size(); i++) {
			try {
				aUser = (Vector) v.get(i);
				userDbId = (Integer) (aUser.get(0));
				userName = (String) aUser.get(1);
				this.addContent(createUserFolder(userDbId, userName));

			} catch (Exception e) {
				System.err.println("oups");
			}

		}
		//System.out.println(content);
	}

	protected void loadGroupsRoot(ServerConnection svrConn)
	{

		Integer userDbId;
		String userName;
		Vector v = new Vector();
		Vector aUser;
		String loginType = svrConn.getLoginType();
		String _connId = svrConn.getConnectionId();
		v = DeployFileSystemFunctions.getGroupPlayspaceSpacesList(svrConn);

		for (int i = 0; i < v.size(); i++) {
			try {
				aUser = (Vector) v.get(i);
				userDbId = (Integer) (aUser.get(0));
				userName = (String) aUser.get(1);
				this.addContent(createGroupFolder(userDbId, userName));
			} catch (Exception e) {
				System.err.println("oups");
			}
		}

	}

	protected void loadServerRoot(ServerConnection svrConn)
	{
//normal folder
		Vector v = null;
		v = DeployFileSystemFunctions.getServerPlayspaceSpaceNoMembershipCheck(svrConn);
		//System.out.println(v);

		if (v.size() > 0) {
			Integer folderDbId = (Integer) v.get(0);
			if (folderDbId.intValue() > 0) { //there is a Public Folder
				this.addContent(createFolder(folderDbId, "Public"));
				folderDbId = (Integer) v.get(1);
				this.addContent(createFolder(folderDbId, "Private"));
			}
			//System.out.println(content);
		}
	}

	protected void loadFolder(ServerConnection svrConn)
	{
//folders and files (playspace)
		String name;
		String idPlayspace, description;
		Vector v = DeployFileSystemFunctions.getPlayspaceFolderContents(svrConn, this.getIntId());
		Vector vecFolder = (Vector) v.get(0);
		Vector vecPlayspace = (Vector) v.get(1);
		Vector modifiedVec = new Vector();
		for (int i = 0; i < vecFolder.size(); i++) {
			Integer idFolder = (Integer) ((Vector) vecFolder.get(i)).get(0);
			name = (String) ((Vector) vecFolder.get(i)).get(1);
			this.addContent(createFolder(idFolder, name));
		}
		for (int i = 0; i < vecPlayspace.size(); i++) {
			Vector psInfo = (Vector) vecPlayspace.get(i);
			idPlayspace = (String) psInfo.get(0);
			name = (String) psInfo.get(1);
			description = (String) psInfo.get(2);
			modifiedVec = DeployFileSystemFunctions.getPlayspaceLastModified(svrConn, idPlayspace);
			String lastModified = "";
			int version = 0;
			if (!modifiedVec.isEmpty()) {
				lastModified = modifiedVec.get(0).toString();
				version = ((Integer) modifiedVec.get(1)).intValue();
			}
			this.addContent(new DomeFile(DomeFile.PLAYSPACE_TYPE, idPlayspace, name, description, lastModified, version));
		}
	}
}
