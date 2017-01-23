// ModelFolder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.functions.DeployFileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

import java.util.Vector;

public class DeployModelFolder extends Folder
{
	public static DeployModelFolder createFolder(Integer id, String name)
	{
		return new DeployModelFolder(FOLDER, id, name);
	}

	public static DeployModelFolder createUserFolder(Integer id, String name)
	{
		return new DeployModelFolder(USER_HOME, id, name);
	}

	public static DeployModelFolder createGroupFolder(Integer id, String name)
	{
		return new DeployModelFolder(GROUP_HOME, id, name);
	}

	public static DeployModelFolder createUserRootFolder(ServerConnection svrConn)
	{
		return new DeployModelFolder(USER_HOME, svrConn);
	}

	public static DeployModelFolder createUsersRootFolder(ServerConnection svrConn)
	{
		return new DeployModelFolder(USERS_ROOT, svrConn);
	}

	public static DeployModelFolder createGroupsRootFolder(ServerConnection svrConn)
	{
		return new DeployModelFolder(GROUPS_ROOT, svrConn);
	}

	public static DeployModelFolder createServerRootFolder(ServerConnection svrConn)
	{
		return new DeployModelFolder(SERVER_ROOT, svrConn);
	}

	protected DeployModelFolder(String folderType, Integer id, String name)
	{
		super(folderType, id, name);
	}

	protected DeployModelFolder(String folderType, ServerConnection svrConn)
	{
		super(folderType);
		listChildren(svrConn); // load root folders right away
	}


	protected void loadUserOrGroupHome(ServerConnection svrConn)
	{
		Vector v = null;

		if (getIntId() >= 0)
			v = DeployFileSystemFunctions.getUserGroupModelFolders(svrConn, getIntId()); //expand a user

		else
			v = DeployFileSystemFunctions.getUserModelSpace(svrConn); //expand the root

		if (!v.isEmpty()) {
			Integer folderDbId = (Integer) v.get(0);
			if (folderDbId.intValue() > 0) { //there is a Public Folder
				this.addContent(createFolder(folderDbId, "Public"));
				folderDbId = (Integer) v.get(1);
				if (folderDbId.intValue() > 0) { //there is a Private Folder

					this.addContent(createFolder(folderDbId, "Private"));
				}
			}
		}
	}

	protected void loadUsersRoot(ServerConnection svrConn)
	{
		Vector aUser;
		String userName;
		Integer userDbId;
		Vector v = DeployFileSystemFunctions.getUserModelSpacesList(svrConn);
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
		v = DeployFileSystemFunctions.getGroupModelSpacesList(svrConn);

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
		v = DeployFileSystemFunctions.getServerModelSpaceNoMembershipCheck(svrConn);
		//System.out.println(v);

		Integer folderDbId = (Integer) v.get(0);
		if (folderDbId.intValue() > 0) { //there is a Public Folder
			this.addContent(createFolder(folderDbId, "Public"));
			folderDbId = (Integer) v.get(1);
			this.addContent(createFolder(folderDbId, "Private"));
		}
		//System.out.println(content);
	}

	protected void loadFolder(ServerConnection svrConn)
	{
//folders and files (model)
		Vector v = null;
        String name, modelType;
		String idModel, description, idProject, idAnalysisTool;
		v = DeployFileSystemFunctions.getModelFolderContents(svrConn, this.getIntId());
		Vector vecFolder = (Vector) v.get(0);
		Vector vecModel = (Vector) v.get(1);
		Vector modifiedVec = new Vector();
		for (int i = 0; i < vecFolder.size(); i++) {
			Integer idFolder = (Integer) ((Vector) vecFolder.get(i)).get(0);
			name = (String) ((Vector) vecFolder.get(i)).get(1);
			this.addContent(createFolder(idFolder, name));
		}
		for (int i = 0; i < vecModel.size(); i++) {
			Vector modelInfo = (Vector) vecModel.get(i);
			idModel = (String) modelInfo.get(0);
			name = (String) modelInfo.get(1);
			description = (String) modelInfo.get(2);
			modelType = (String) modelInfo.get(3);
			modifiedVec = DeployFileSystemFunctions.getModelLastModified(svrConn, idModel);
			String lastModified = "";
			int version = 0;
			if (!modifiedVec.isEmpty()) {
				lastModified = modifiedVec.get(0).toString();
				version = ((Integer) modifiedVec.get(1)).intValue();
			}
			this.addContent(new DomeFile(DomeFile.MODEL_TYPE, idModel, name, description, lastModified, version));
		}

		v = FileSystemFunctions.getProjectFolderContents(svrConn, getIntId());
		for (int i = 0; i < v.size(); i++) {
			Vector projectInfo = (Vector) v.get(i);
			idProject = (String) projectInfo.get(0);
			name = (String) projectInfo.get(1);
			description = (String) projectInfo.get(2);

			modifiedVec = FileSystemFunctions.getProjectLastModified(svrConn, idProject);
			String lastModified = modifiedVec.get(0).toString();
			int version = ((Integer) modifiedVec.get(1)).intValue();

			this.addContent(new DomeFile(DomeFile.PROJECT_TYPE, idProject, name, description, svrConn.getServerPort(), lastModified, version));
		}
        v = FileSystemFunctions.getAnalysisToolFolderContents(svrConn, getIntId());
        for (int i = 0; i < v.size(); i++)
        {
            Vector analysisToolInfo = (Vector) v.get(i);
            idAnalysisTool = (String) analysisToolInfo.get(0);
            name = (String) analysisToolInfo.get(1);
            description = (String) analysisToolInfo.get(2);

            modifiedVec = FileSystemFunctions.getAnalysisToolLastModified(svrConn, idAnalysisTool);
            String lastModified = modifiedVec.get(0).toString();
            int version = ((Integer)modifiedVec.get(1)).intValue();

            this.addContent(new DomeFile(DomeFile.ANALYSIS_TOOL_TYPE, idAnalysisTool, name,
                description, svrConn.getServerPort(), lastModified, version));
        }
	}
}
