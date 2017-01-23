// BrowsePlayspaceFolder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.BrowseFileSystemFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

import java.util.Vector;

public class BrowsePlayspaceFolder extends Folder
{
	public static BrowsePlayspaceFolder createFolder(Integer id, String name)
	{
		return new BrowsePlayspaceFolder(FOLDER, id, name);
	}

	public static BrowsePlayspaceFolder createUserFolder(Integer id, String name)
	{
		return new BrowsePlayspaceFolder(USER_HOME, id, name);
	}

	public static BrowsePlayspaceFolder createGroupFolder(Integer id, String name)
	{
		return new BrowsePlayspaceFolder(GROUP_HOME, id, name);
	}

	public static BrowsePlayspaceFolder createUserRootFolder(ServerConnection svrConn)
	{
		return new BrowsePlayspaceFolder(USER_HOME, svrConn);
	}

	public static BrowsePlayspaceFolder createUsersRootFolder(ServerConnection svrConn)
	{
		return new BrowsePlayspaceFolder(USERS_ROOT, svrConn);
	}

	public static BrowsePlayspaceFolder createGroupsRootFolder(ServerConnection svrConn)
	{
		return new BrowsePlayspaceFolder(GROUPS_ROOT, svrConn);
	}

	public static BrowsePlayspaceFolder createServerRootFolder(ServerConnection svrConn)
	{
		return new BrowsePlayspaceFolder(SERVER_ROOT, svrConn);
	}

	protected BrowsePlayspaceFolder(String folderType, Integer id, String name)
	{
		super(folderType, id, name);
	}

	protected BrowsePlayspaceFolder(String folderType, ServerConnection svrConn)
	{
		super(folderType);
	}


	protected void loadUserOrGroupHome(ServerConnection svrConn)
	{
		Vector v = null;
		if (getIntId() >= 0)
			v = BrowseFileSystemFunctions.getUserGroupPlayspaceFolders(svrConn, getIntId());
		else
			v = BrowseFileSystemFunctions.getUserPlayspaceSpace(svrConn);

		//System.out.println("vector size in loadUserOrGroupHome " + v.size());
		if (v.size() > 0) {
			Integer folderDbId = (Integer) v.get(0);
			if (folderDbId.intValue() > 0) { //there is a Public Folder
				this.addContent(createFolder(folderDbId, "Public"));
				if (v.size() > 1) {
					folderDbId = (Integer) v.get(1);
					if (folderDbId.intValue() > 0) { //there is a Private Folder
						this.addContent(createFolder(folderDbId, "Private"));
					}
				}
			}
			//System.out.println(content);
		}
	}

	protected void loadUsersRoot(ServerConnection svrConn)
	{
		Vector aUser;
		String userName;
		Integer userDbId;
		Vector v = BrowseFileSystemFunctions.getUserPlayspaceSpacesList(svrConn);
		if (v.size() > 0) {
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
	}

	protected void loadGroupsRoot(ServerConnection svrConn)
	{

		Integer userDbId;
		String userName;
		Vector v = new Vector();
		Vector aUser;
		v = BrowseFileSystemFunctions.getGroupPlayspaceSpacesList(svrConn);

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
		v = BrowseFileSystemFunctions.getServerPlayspaceSpace(svrConn);

		if (v.size() > 0) {
			Integer folderDbId = (Integer) v.get(0);
			if (folderDbId.intValue() > 0) { //there is a Public Folder
				this.addContent(createFolder(folderDbId, "Public"));
				if (v.size() > 1) {
					folderDbId = (Integer) v.get(1);
					if (folderDbId.intValue() > 0) { //there is a Private Folder
						this.addContent(createFolder(folderDbId, "Private"));
					}
				}
			}
		}
		//System.out.println(content);
	}

	protected void loadFolder(ServerConnection svrConn)
	{
		//folders and files (playspace)
		String name, modelType;
		String idPlayspace, description;
		Vector v = BrowseFileSystemFunctions.getPlayspaceFolderContents(svrConn, getIntId());
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
			modifiedVec = FileSystemFunctions.getPlayspaceLastModified(svrConn, idPlayspace);
			String lastModified = modifiedVec.get(0).toString();
			int version = ((Integer) modifiedVec.get(1)).intValue();
			this.addContent(new DomeFile(DomeFile.PLAYSPACE_TYPE, idPlayspace, name, description, lastModified, version));
		}
	}
}
