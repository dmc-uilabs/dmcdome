// FileSystemFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.util.Vector;

/**
 * set of functions for manipulating the virtual file system on the server
 */
public class FileSystemFunctions
{
	// TODO: convert vector results to usable data structures


	public static Integer getParentDbId(ServerConnection svrConn, Object Id, String type)
	{
		return ((Integer) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PARENT_ID,
		                                  Vectors.create(Id, type)));
	}


	/**
	 * Create a new folder and assign its parent id.
	 * @param parentFolderId Id of the parent folder
	 * @param folderName Name of the new folder
	 * @return Vector containing new folder Id / folder Name / parent Folder Id
	 */
	public static int createModelFolder(ServerConnection svrConn, int parentFolderId, String folderName)
	{
		return ((Integer) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.CREATE_FOLDER,
		                                  Vectors.create(new Integer(parentFolderId), folderName, DbConstants.MODEL_TYPE))).intValue();
	}

	/**
	 * Create a new folder and assign its parent id.
	 * @param parentFolderId Id of the parent folder
	 * @param folderName Name of the new folder
	 * @return Vector containing new folder Id / folder Name / parent Folder Id
	 */
	public static int createPlayspaceFolder(ServerConnection svrConn, int parentFolderId, String folderName)
	{
		return ((Integer) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.CREATE_FOLDER,
		                                  Vectors.create(new Integer(parentFolderId), folderName, DbConstants.PLAYSPACE_TYPE))).intValue();
	}

	/**
	 * Delete a model folder.
	 * @param folderId Folder Id.
	 */
	public static void deleteModelFolder(ServerConnection svrConn, int folderId)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.DELETE_FOLDER,
		                Vectors.create(new Integer(folderId), DbConstants.MODEL_TYPE));
	}

	/**
	 * Delete a playspace folder.
	 * @param folderId Folder Id.
	 */
	public static void deletePlayspaceFolder(ServerConnection svrConn, int folderId)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.DELETE_FOLDER,
		                Vectors.create(new Integer(folderId), DbConstants.PLAYSPACE_TYPE));
	}

	/**
	 * Delete a model folder.
	 * @param folderId Folder Id.
	 */
	public static void renameModelFolder(ServerConnection svrConn, int folderId, String newName)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.RENAME_FOLDER,
		                Vectors.create(new Integer(folderId), newName, DbConstants.MODEL_TYPE));
	}

	/**
	 * Delete a model folder.
	 * @param folderId Folder Id.
	 */
	public static void renamePlayspaceFolder(ServerConnection svrConn, int folderId, String newName)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.RENAME_FOLDER,
		                Vectors.create(new Integer(folderId), newName, DbConstants.PLAYSPACE_TYPE));
	}


	/**
	 * move folder
	 * @param folderId Id of the folder
	 * @param parentFolderId Id of the new parent folder
	 * @return Vector containing new folder Id / folder Name / parent Folder Id
	 */
	public static Vector moveFolder(ServerConnection svrConn, int folderId, int parentFolderId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.MOVE_FOLDER,
		                                Vectors.create(new Integer(folderId), new Integer(parentFolderId)));
	}


	/**
	 * get model or playspace folders for a user or group
	 * @param id User or group id
	 * @return
	 */
	public static Vector getUserGroupModelFolders(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_GROUP_FOLDERS,
		                                Vectors.create(new Integer(id), DbConstants.MODEL_TYPE));
	}

	/**
	 * get model or playspace folders for a user or group
	 * @param id User or group id
	 * @return
	 */
	public static Vector getUserGroupPlayspaceFolders(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_GROUP_FOLDERS,
		                                Vectors.create(new Integer(id), DbConstants.PLAYSPACE_TYPE));
	}

	/**
	 * This function gets the contents of a folder
	 * @param id Folder ID
	 * @return id, name, type
	 */
	public static Vector getModelFolderContents(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_MODEL_FOLDER_CONTENTS,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(id)));
	}
    /**
     * This function gets the contents of a analysis tool folder
     * @param svrConn
     * @param id
     * @return id, name, type
     */
    public static Vector getAnalysisToolFolderContents(ServerConnection svrConn, int id)
    {
        return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_ANALYSIS_TOOL_CONTENTS,
                                        Vectors.create(svrConn.getConnectionId(), new Integer(id)));
    }

    public static Vector getProjectInsideAnalysisTool(ServerConnection svrConn, String analysisToolId)
    {
        return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PROJECT_INSIDE_ANALYSIS_TOOL,
                                        Vectors.create(svrConn.getConnectionId(), analysisToolId));
    }

    public static Vector getProjectFolderContents(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PROJECTS_FOLDER_CONTENTS,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(id)));
	}

	public static Vector getSubscriptionModelFolderContents(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_SUBSCRIPTION_MODEL_FOLDER_CONTENTS,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(id)));
	}

	/**
	 * This function gets the contents of a folder
	 * @param id Folder ID
	 * @return
	 */
	public static Vector getPlayspaceFolderContents(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PLAYSPACE_FOLDER_CONTENTS,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(id)));
	}

	public static Vector deleteModel(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.DELETE_MODEL,
		                                Vectors.create(modelId));
	}

    public static Vector deleteAnalysisTool(ServerConnection svrConn, String analysisToolId)
    {
        return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.DELETE_ANALYSIS_TOOL,
                                        Vectors.create(analysisToolId));
    }

	public static Vector deletePlayspace(ServerConnection svrConn, String projectId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.DELETE_PLAYSPACE,
		                                Vectors.create(projectId));
	}

	public static Vector deleteProject(ServerConnection svrConn, String projectId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.DELETE_PROJECT,
		                                Vectors.create(projectId));
	}

	public static Vector getModelLastModified(ServerConnection svrConn, String id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_LAST_MODIFIED,
		                                Vectors.create(id, DbConstants.MODEL_TYPE));
	}

	public static Vector getProjectLastModified(ServerConnection svrConn, String id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_LAST_MODIFIED,
		                                Vectors.create(id, DbConstants.PROJECT_TYPE));
	}

    public static Vector getAnalysisToolLastModified(ServerConnection svrConn, String id)
    {
        return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_LAST_MODIFIED,
                                        Vectors.create(id, DbConstants.ANALYSIS_TOOL_TYPE));
    }

	public static Vector getPlayspaceLastModified(ServerConnection svrConn, String id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_LAST_MODIFIED,
		                                Vectors.create(id, DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getUserModelSpace(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(userId), DbConstants.MODEL_TYPE));
	}


	public static Vector getUserPlayspaceSpace(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(userId), DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getUserModelSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_SPACE_FOR_SESSION,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.MODEL_TYPE));
	}

	public static Vector getUserPlayspaceSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_PLAYSPACE_FOR_SESSION,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getGroupModelSpace(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_GROUP_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), DbConstants.MODEL_TYPE));
	}

	public static Vector getGroupPlayspaceSpace(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_GROUP_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getServerModelSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_SERVER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.MODEL_TYPE));
	}

	public static Vector getServerPlayspaceSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_SERVER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getUserModelSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create(DbConstants.USER_TYPE, DbConstants.MODEL_TYPE));
	}

	public static Vector getUserPlayspaceSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create(DbConstants.USER_TYPE, DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getGroupModelSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create(DbConstants.GROUP_TYPE, DbConstants.MODEL_TYPE));
	}

	public static Vector getGroupPlayspaceSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create(DbConstants.GROUP_TYPE, DbConstants.PLAYSPACE_TYPE));
	}


	public static Integer getUserIdFromSession(ServerConnection svrConn, String sessionId)
	{
		return (Integer) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_USER_ID_FROM_SESSION,
		                                 Vectors.create(sessionId));
	}


	public static Vector getInterfaces(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_INTERFACES,
		                                Vectors.create(modelId));
	}

	public static Vector getGroupModelSpaceNoMembershipCheck(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_GROUP_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), DbConstants.MODEL_TYPE));
	}

	public static Vector getGroupPlayspaceSpaceNoMembershipCheck(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_GROUP_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getServerModelSpaceNoMembershipCheck(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_SERVER_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.MODEL_TYPE));
	}

	public static Vector getServerPlayspaceSpaceNoMembershipCheck(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_SERVER_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.PLAYSPACE_TYPE));
	}

	public static Vector getAvailableInterfaces(ServerConnection svrConn, String modelId)
	{
		//Debug.trace(Debug.ALL, modelId);
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_AVAILABLE_INTERFACES,
		                                Vectors.create(svrConn.getConnectionId(), modelId));
	}

	public static Vector getAvailableInterfacesInfo(ServerConnection svrConn, String parentId, String permissionTypes)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getAvailableInterfacesInfo");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_AVAILABLE_INTERFACES_INFO,
		                                Vectors.create(svrConn.getConnectionId(), parentId, permissionTypes));
	}

	public static Vector getAvailableInterfaceIds(ServerConnection svrConn, String type, String modelOrProjectId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getAvailableInterfaceIds");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_AVAILABLE_INTERFACE_IDS,
		                                Vectors.create(type, modelOrProjectId));
	}

	/**
	 * returns project contents for a certain user and login
	 * @param svrConn
	 * @param projectId
	 * @param mode is either DbConstants.FILESYSTEM_BROWSE or DbConstants.FILESYSTEM_SUBSCRIBE
	 * @return if user doesn't have a permission to look inside, return an empty vector
	 * otherwise, return a vector of two elements <projectXml, imodelInfo>
	 * where imodelInfo is a vector of vectors, one vector for each imodel the user has permission for
	 * where imodel vector is <modelInfo, vectors of interfaces info>
	 * modelInfo is <name, id, description, version, date>
	 * and each interface info vector is <name, id, description, version, date>
	 */
	public static Vector getAvailableProjectContents(ServerConnection svrConn, String projectId, String mode)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getAvailableProjectContents");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_AVAILABLE_PROJECT_CONTENTS,
		                                Vectors.create(svrConn.getConnectionId(), projectId, mode));
	}

	public static String getInterfaceDescription(ServerConnection svrConn, String parentId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getInterfaceDescription");
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_INTERFACE_DESCRIPTION,
		                                Vectors.create(svrConn.getConnectionId(), parentId));
	}


	public static String getPlayspaceDescription(ServerConnection svrConn, String parentId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getPlayspaceDescription");
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PLAYSPACE_DESCRIPTION,
		                                Vectors.create(svrConn.getConnectionId(), parentId));
	}

	public static Vector getPlayspaceInfo(ServerConnection svrConn, String playspaceId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getPlayspaceInfo");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PLAYSPACE_INFO,
		                                Vectors.create(playspaceId));
	}

	public static String getProjectDescription(ServerConnection svrConn, String parentId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getProjectDescription");
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PROJECT_DESCRIPTION,
		                                Vectors.create(svrConn.getConnectionId(), parentId));
	}

	public static Vector getPathForModel(ServerConnection svrConn, String modelId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getPathForModel");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PATH_FOR_MODEL,
		                                Vectors.create(svrConn.getConnectionId(), modelId));
	}

	public static Vector getPathForPlayspace(ServerConnection svrConn, String playspaceId)
	{
		//Debug.trace(Debug.ALL, "FileSystemFunctions: getPathForPlayspace");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PATH_FOR_PLAYSPACE,
		                                Vectors.create(svrConn.getConnectionId(), playspaceId));
	}

	public static Vector getPathForProject(ServerConnection svrConn, String projectId)
	{
		//System.out.println("FileSystemFunctions: getPathForProject");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PATH_FOR_PROJECT,
		                                Vectors.create(svrConn.getConnectionId(), projectId));
	}

    public static Vector getPathForAnalysisTool(ServerConnection svrConn, String analysisToolId)
	{
		//System.out.println("FileSystemFunctions: getPathForAnalysisTool");
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PATH_FOR_ANALYSIS_TOOL,
		                                Vectors.create(svrConn.getConnectionId(), analysisToolId));
	}

	public static Vector getModelInfo(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_MODEL_INFO,
		                                Vectors.create(modelId));
	}

	public static Vector getProjectInfo(ServerConnection svrConn, String projectId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_PROJECT_INFO,
		                                Vectors.create(projectId));
	}

	public static Integer getInterfaceVersion(ServerConnection svrConn, String ifaceId)
	{
		return (Integer) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_INTERFACE_VERSION,
		                                Vectors.create(ifaceId));
	}

    public static Vector getAnalysisToolInfo(ServerConnection svrConn, String analysisToolId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.GET_ANALYSIS_TOOL_INFO,
		                                Vectors.create(analysisToolId));
	}

    public static Vector hasAnalysisToolPrototypeFolderBeenCreated(ServerConnection svrConn, Integer parentFolderId, String prototypeFolderName)
    {
        return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_FILESYSTEM + "." + DbConstants.HAS_ANALYSIS_TOOL_PROTOTYPE_FOLDER_BEEN_CREATED,
                                        Vectors.create(parentFolderId, prototypeFolderName));
    }
}
