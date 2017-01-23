// BrowseModelFolder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.BrowseFileSystemFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

import java.util.Vector;

public class BrowseModelFolder extends Folder
{
	public int modelProjectCase = 0;

	public static Folder createFolder(Integer id, String name)
	{
		return new BrowseModelFolder(FOLDER, id, name);
	}

	public static Folder createUserFolder(Integer id, String name)
	{
		return new BrowseModelFolder(USER_HOME, id, name);
	}

	public static Folder createGroupFolder(Integer id, String name)
	{
		return new BrowseModelFolder(GROUP_HOME, id, name);
	}

	public static Folder createUserRootFolder(ServerConnection svrConn)
	{
		return new BrowseModelFolder(USER_HOME, svrConn);
	}

	public static Folder createUsersRootFolder(ServerConnection svrConn)
	{
		return new BrowseModelFolder(USERS_ROOT, svrConn);
	}

	public static Folder createGroupsRootFolder(ServerConnection svrConn)
	{
		return new BrowseModelFolder(GROUPS_ROOT, svrConn);
	}

	public static Folder createServerRootFolder(ServerConnection svrConn)
	{
		return new BrowseModelFolder(SERVER_ROOT, svrConn);
	}

	public BrowseModelFolder(String folderType, Integer id, String name)
	{
		super(folderType, id, name);
	}

	protected BrowseModelFolder(String folderType, ServerConnection svrConn)
	{
		super(folderType);
	}


	protected void loadUserOrGroupHome(ServerConnection svrConn)
	{
		Vector v = null;
		if (getIntId() >= 0)
			v = BrowseFileSystemFunctions.getUserGroupModelFolders(svrConn, getIntId());
		else
			v = BrowseFileSystemFunctions.getUserModelSpace(svrConn);

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
		Vector v = BrowseFileSystemFunctions.getUserModelSpacesList(svrConn);
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
		v = BrowseFileSystemFunctions.getGroupModelSpacesList(svrConn);

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
		v = BrowseFileSystemFunctions.getServerModelSpace(svrConn);

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
		//folders and files (model)
		String name, modelType;
        String idModel, description, idProject, idAnalysisTool;
        Vector v = BrowseFileSystemFunctions.getModelFolderContents(svrConn, getIntId());
        Vector vecFolder = (Vector) v.get(0);
        Vector vecModel = (Vector) v.get(1);
        Vector modifiedVec = new Vector();
        for (int i = 0; i < vecFolder.size(); i++)
        {
            Integer idFolder = (Integer) ((Vector) vecFolder.get(i)).get(0);
            name = (String) ((Vector) vecFolder.get(i)).get(1);
            this.addContent(createFolder(idFolder, name));
        }
        if ((modelProjectCase == 0) || (modelProjectCase == 1))
        {
            for (int i = 0; i < vecModel.size(); i++)
            {
                Vector modelInfo = (Vector) vecModel.get(i);
                idModel = (String) modelInfo.get(0);
                name = (String) modelInfo.get(1);
                description = (String) modelInfo.get(2);
                modelType = (String) modelInfo.get(3);
                modifiedVec = FileSystemFunctions.getModelLastModified(svrConn, idModel);
                String lastModified = modifiedVec.get(0).toString();
                int version = ((Integer) modifiedVec.get(1)).intValue();
                this.addContent(new BrowseDomeFile(DomeFile.MODEL_TYPE, idModel, name, description, svrConn.getServerPort(), lastModified, version));
            }
        }

        if ((modelProjectCase == 0) || (modelProjectCase == 2))
        {
            v = FileSystemFunctions.getProjectFolderContents(svrConn, getIntId());
            for (int i = 0; i < v.size(); i++)
            {
                Vector projectInfo = (Vector) v.get(i);
                idProject = (String) projectInfo.get(0);
                name = (String) projectInfo.get(1);
                description = (String) projectInfo.get(2);

                modifiedVec = FileSystemFunctions.getProjectLastModified(svrConn, idProject);
                String lastModified = modifiedVec.get(0).toString();
                int version = ((Integer) modifiedVec.get(1)).intValue();

                this.addContent(new BrowseDomeFile(DomeFile.PROJECT_TYPE, idProject, name, description, svrConn.getServerPort(), lastModified, version));
            }
        }
        // analysis  tools
        if ((modelProjectCase == 0) || (modelProjectCase == 3))
        {
            v = FileSystemFunctions.getAnalysisToolFolderContents(svrConn, getIntId());
            for (int i = 0; i <v.size(); i++)
            {
                Vector analysisToolInfo = (Vector) v.get(i);
                idAnalysisTool = (String) analysisToolInfo.get(0);
                name = (String) analysisToolInfo.get(1);
                description = (String) analysisToolInfo.get(2);

                modifiedVec = FileSystemFunctions.getAnalysisToolLastModified(svrConn, idAnalysisTool);
                String lastModified = modifiedVec.get(0).toString();
                int version = ((Integer) modifiedVec.get(1)).intValue();

                addContent(new BrowseDomeFile(DomeFile.ANALYSIS_TOOL_TYPE, idAnalysisTool, name, description, svrConn.getServerPort(), lastModified, version));
            }
        }
	}

}
