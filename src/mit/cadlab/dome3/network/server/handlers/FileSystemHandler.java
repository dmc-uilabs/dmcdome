// FileSystemHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.util.FileUtils;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;
import java.io.File;

/**
 * The XML-RPC handler for functions related to virtual file management.
 */
public class FileSystemHandler extends AbstractXmlRpcHandler
{

	/**
	 * This is the only method that an XMLRPC handler has to implement.
	 *
	 * @param methodName - name of the method on the sever that the client wants to invoke
	 * @param params - arguments to the method on the server
	 *
	 * @return results of the method execution on the server.
	 * @throws org.apache.xmlrpc.XmlRpcException wraps up any exceptions thrown by the method on the server or
	 * 					if a particular method is not found on the server.
	 */
	public Object execute(String methodName, Vector params) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.CREATE_FOLDER))
				return new Integer(createFolder(params));
			else if (methodName.equals(DbConstants.DELETE_FOLDER)) {
				deleteFolder(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.RENAME_FOLDER)) {
				renameFolder(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.GET_PARENT_ID))
				return getParentDbId(params);
			else if (methodName.equals(DbConstants.GET_PLAYSPACE_FOLDER_CONTENTS))
				return getPlayspaceFolderContents(params);
			else if (methodName.equals(DbConstants.GET_MODEL_FOLDER_CONTENTS))
				return getModelFolderContents(params);
            else if (methodName.equals(DbConstants.GET_ANALYSIS_TOOL_CONTENTS))
                return getAnalysisToolContents(params);
            else if (methodName.equals(DbConstants.GET_PROJECT_INSIDE_ANALYSIS_TOOL))
                return getProjectInsideAnalysisTool(params);
            else if (methodName.equals(DbConstants.GET_PROJECTS_FOLDER_CONTENTS))
				return getProjectsContents(params);
			else if (methodName.equals(DbConstants.GET_SUBSCRIPTION_MODEL_FOLDER_CONTENTS))
				return getSubscriptionModelFolderContents(params);
			else if (methodName.equals(DbConstants.MOVE_FOLDER))
				return moveFolder(params);
			else if (methodName.equals(DbConstants.GET_USER_GROUP_FOLDERS))
				return getUserGroupFolders(params);
			else if (methodName.equals(DbConstants.GET_LAST_MODIFIED))
				return getLastModified(params);
			else if (methodName.equals(DbConstants.GET_USER_SPACE))
				return getUserSpace(params);
			else if (methodName.equals(DbConstants.GET_USER_SPACE_FOR_SESSION))
				return getUserSpaceForSession(params);
			else if (methodName.equals(DbConstants.GET_USER_PLAYSPACE_FOR_SESSION))
				return getUserSpaceForSession(params);
			else if (methodName.equals(DbConstants.GET_GROUP_SPACE))
				return getGroupSpace(params);
			else if (methodName.equals(DbConstants.GET_SERVER_SPACE))
				return getServerSpace(params);
			else if (methodName.equals(DbConstants.GET_GROUP_SPACE_NO_MEMBERSHIP_CHECK))
				return getGroupSpaceNoMembershipCheck(params);
			else if (methodName.equals(DbConstants.GET_SERVER_SPACE_NO_MEMBERSHIP_CHECK))
				return getServerSpaceNoMembershipCheck(params);
			else if (methodName.equals(DbConstants.GET_USER_GROUP_SPACES_LIST))
				return getUserGroupSpacesList(params);
			else if (methodName.equals(DbConstants.GET_USER_ID_FROM_SESSION))
				return getUserIdFromSession(params);
			else if (methodName.equals(DbConstants.GET_INTERFACES))
				return getInterfaces(params);
			else if (methodName.equals(DbConstants.GET_AVAILABLE_INTERFACES))
				return getAvailableInterfaces(params);
			else if (methodName.equals(DbConstants.GET_AVAILABLE_INTERFACES_INFO))
				return getAvailableInterfacesInfo(params);
			else if (methodName.equals(DbConstants.GET_AVAILABLE_INTERFACE_IDS))
				return getAvailableInterfaceIds(params);
			else if (methodName.equals(DbConstants.GET_INTERFACE_DESCRIPTION))
				return getInterfaceDescription(params);
			else if (methodName.equals(DbConstants.GET_PLAYSPACE_DESCRIPTION))
				return getPlayspaceDescription(params);
			else if (methodName.equals(DbConstants.GET_PATH_FOR_MODEL))
				return getPathForModel(params);
			else if (methodName.equals(DbConstants.GET_PATH_FOR_PLAYSPACE))
				return getPathForPlayspace(params);
			else if (methodName.equals(DbConstants.GET_PATH_FOR_PROJECT))
				return getPathForProject(params);
            else if (methodName.equals(DbConstants.GET_PATH_FOR_ANALYSIS_TOOL))
                return getPathForAnalysisTool(params);
			else if (methodName.equals(DbConstants.GET_AVAILABLE_PROJECT_CONTENTS))
				return getAvailableProjectContents(params);
			else if (methodName.equals(DbConstants.GET_MODEL_INFO))
				return getModelInfo(params);
			else if (methodName.equals(DbConstants.GET_PROJECT_INFO))
				return getProjectInfo(params);
			else if (methodName.equals(DbConstants.GET_INTERFACE_VERSION))
				return getInterfaceVersion(params);
            else if (methodName.equals(DbConstants.GET_ANALYSIS_TOOL_INFO))
                return getAnalysisToolInfo(params);
            else if (methodName.equals(DbConstants.HAS_ANALYSIS_TOOL_PROTOTYPE_FOLDER_BEEN_CREATED))
                return hasAnalysisToolPrototypeFolderBeenCreated(params);
			else if (methodName.equals(DbConstants.GET_PLAYSPACE_INFO))
				return getPlayspaceInfo(params);
			else if (methodName.equals(DbConstants.DELETE_MODEL)) {
				deleteModel(params);
				return DbUtils.NO_VECTOR;
			}
            else if (methodName.equals(DbConstants.DELETE_ANALYSIS_TOOL))
            {
                deleteAnalysisTool(params);
                return DbUtils.NO_VECTOR;
            }
            else if (methodName.equals(DbConstants.DELETE_PROJECT)) {
				deleteProject(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.DELETE_PLAYSPACE)) {
				deletePlayspace(params);
				return DbUtils.NO_VECTOR;
			} else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	private Integer getParentDbId(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_OBJ, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getParentDbId(Object Id, String type)");
		return FileSystemDbFunctions.getParentDbId(params.get(0), (String) params.get(1));
	}

	private int createFolder(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createFolder(int parentFolderId, String folderName, String type)");
		return FileSystemDbFunctions.createFolder(getInt(params.get(0)), (String) params.get(1), (String) params.get(2));
	}

	private void deleteFolder(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deleteFolder(int folderId, String type)");
		FileSystemDbFunctions.deleteFolder(getInt(params.get(0)), (String) params.get(1));
	}

	private void renameFolder(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for renameFolder(int folderId, String newName, String type)");
		FileSystemDbFunctions.renameFolder(getInt(params.get(0)), (String) params.get(1), (String) params.get(2));
	}

	private Vector moveFolder(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for moveFolder(int folderId, int parentFolderId)");
		return FileSystemDbFunctions.moveFolder(getInt(params.get(0)), getInt(params.get(1)));
	}

	private Vector getUserGroupFolders(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserGroupFolders(int userId, String type)");
		return FileSystemDbFunctions.getUserGroupFolders(getInt(params.get(0)), (String) params.get(1));
	}
/*MOST LIKELY THIS FUNCTION WILL CEASE TO EXIST, TO BE REPLACED BY THE 3 MORE SPECIFIC FUNCTIONS, NAMELY
getSubscriptionModelFolderContents, getModelFolderContents, and getPlayspaceFolderContents*/
/*
	private Vector getFolderContents(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for getFolderContents(Strong connectionId, String parentFolderId, String type)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getFolderContents(userId,loginType, getInt(params.get(1)), (String) params.get(2));
	}
*/
    private Vector getAnalysisToolContents(Vector params) throws XmlRpcException
    {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                                      DbErrors.XMLRPC_INVALID_ARGUMENT_LIST_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT}))
                    throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                                  "invalid arguments for getAnalysisToolFolderContents" +
                                            "(Strong connectionId, String parentFolderId)");
        String sessionId = (String) params.get(0);

        int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

        return FileSystemDbFunctions.getAnalysisToolContents(userId, loginType, getInt(params.get(1)));
    }

    private Vector getProjectInsideAnalysisTool(Vector params) throws XmlRpcException
    {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                                      DbErrors.XMLRPC_INVALID_ARGUMENT_LIST_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
                            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                                          "invalid arguments for getAnalysisToolFolderContents" +
                                                    "(Strong connectionId, String analysisToolId)");
        String sessionId = (String) params.get(0);
        String analysisToolId = (String) params.get(1);

        int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

        return FileSystemDbFunctions.getProjectInfoInsideAnalysisTool(userId, loginType, analysisToolId);

    }

    private Vector getModelFolderContents(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getFolderContents(Strong connectionId, String parentFolderId)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getModelFolderContents(userId, loginType, getInt(params.get(1)));
	}

	private Vector getProjectsContents(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getProjectsContents(Strong connectionId, String parentFolderId)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getProjectsContents(userId, loginType, getInt(params.get(1)));
	}

	private Vector getPlayspaceFolderContents(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getFolderContents(Strong connectionId, String parentFolderId)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getPlayspaceFolderContents(userId, loginType, getInt(params.get(1)));
	}

	private Vector getSubscriptionModelFolderContents(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getFolderContents(Strong connectionId, String parentFolderId)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getSubscriptionModelFolderContents(userId, loginType, getInt(params.get(1)));
	}

	private Vector getLastModified(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getLastModified(String id, String type)");
		return FileSystemDbFunctions.getLastModified((String) params.get(0), (String) params.get(1));
	}

	private void deleteModel(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deleteModel(String modelId)");
		FileSystemDbFunctions.deleteModel((String) params.get(0));
        //Qing add here
        deleteAuxFilesForModel((String) params.get(0));
	}

    private void deleteAnalysisTool(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deleteModel(String modelId)");
		FileSystemDbFunctions.deleteAnalysisTool((String) params.get(0));
	}

	private void deletePlayspace(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deletePlayspace(String runtimeId)");
		FileSystemDbFunctions.deletePlayspace((String) params.get(0));
	}

	private void deleteProject(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deleteProject(String projectId)");
		FileSystemDbFunctions.deleteProject((String) params.get(0));
	}

	private Vector getUserSpace(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserSpace(int userId, String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getUserSpace(getInt(params.get(1)), requestorId, (String) params.get(2));
	}

	private Vector getUserSpaceForSession(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserSpaceForSession(String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getUserSpace(requestorId, requestorId, (String) params.get(1));
	}

	private Vector getGroupSpace(Vector params) throws XmlRpcException
	{
		//System.out.println(params);
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getGroupSpace(int groupId, String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getGroupSpace(getInt(params.get(1)), requestorId, (String) params.get(2));
	}

	private Vector getGroupSpaceNoMembershipCheck(Vector params) throws XmlRpcException
	{
		//System.out.println(params);
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getGroupSpaceNoMembershipCheck(int groupId, String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getGroupSpaceNoMembershipCheck(getInt(params.get(1)), requestorId, (String) params.get(2));
	}

	private Vector getServerSpace(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getServerSpace(String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getServerSpace(requestorId, (String) params.get(1));
	}

	private Vector getServerSpaceNoMembershipCheck(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getServerSpaceNoMembershipCheck(String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getServerSpaceNoMembershipCheck(requestorId, (String) params.get(1));
	}

	private Vector getUserGroupSpacesList(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserGroupSpacesList(String userOrGroup, String type)");
		return FileSystemDbFunctions.getUserGroupSpacesList((String) params.get(0), (String) params.get(1));
	}

	private Integer getUserIdFromSession(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserIdFromSession(String sessisonId)");
		return FileSystemDbFunctions.getUserIdFromSession((String) params.get(0));
	}

	private Vector getInterfaces(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getInterfaces(String modelId)");
		return FileSystemDbFunctions.getInterfaces((String) params.get(0));
	}

	private Vector getAvailableInterfaces(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getAvailableInterfaces(String modelId)");
		return FileSystemDbFunctions.getAvailableInterfaces((String) params.get(1));
	}

	private Vector getAvailableProjectContents(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getAvailableProjectContents(String sessionId, String projectId, String mode)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getAvailableProjectContents(userId, loginType, (String) params.get(1),
		                                                         (String) params.get(2));
	}

	private Vector getAvailableInterfacesInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getAvailableInterfacesInfo(String sessionId, String parentId, String permissionType)");

		String sessionId = (String) params.get(0);
		int userId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return FileSystemDbFunctions.getAvailableInterfacesInfo(userId, loginType, (String) params.get(1), (String) params.get(2));

	}

	private Vector getAvailableInterfaceIds(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getAvailableInterfaceIds(String type, String modelOrProjectId)");

		return FileSystemDbFunctions.getAvailableInterfaceIds((String) params.get(0), (String) params.get(1));

	}

	private String getInterfaceDescription(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getInterfaceDescription(String objectId)");
		return FileSystemDbFunctions.getInterfaceDescription((String) params.get(1));
	}

	private String getPlayspaceDescription(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPlayspaceDescription(String playpaceId)");
		return FileSystemDbFunctions.getPlayspaceDescription((String) params.get(1));
	}

	private Vector getPlayspaceInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPlayspaceInfo(String playpaceId)");
		return FileSystemDbFunctions.getPlayspaceInfo((String) params.elementAt(0));
	}

	private String getProjectDescription(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getProjectDescription(String objectId)");
		return FileSystemDbFunctions.getProjectDescription((String) params.get(1));
	}

	private Vector getPathForModel(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPathForModel(String sessionId, String modelId)");
		String sessionId = (String) params.get(0);
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);
		if (loginType.equalsIgnoreCase("GUEST")) {
			return new Vector();
		}
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getPathForModel((String) params.get(1), requestorId, loginType);
	}

	private Vector getPathForPlayspace(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPathForPlayspace(String sessionId, String playspaceId)");
		String sessionId = (String) params.get(0);
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);
		if (loginType.equalsIgnoreCase("GUEST")) {
			return new Vector();
		}
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getPathForPlayspace((String) params.get(1), requestorId, loginType);
	}

	private Vector getPathForProject(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPathForProject(String sessionId, String projectId)");
		String sessionId = (String) params.get(0);
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);
		if (loginType.equalsIgnoreCase("GUEST")) {
			return new Vector();
		}
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getPathForProject((String) params.get(1), requestorId, loginType);
	}

    private Vector getPathForAnalysisTool(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPathForAnalysisTool(String sessionId, String analysisToolId)");
		String sessionId = (String) params.get(0);
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);
		if (loginType.equalsIgnoreCase("GUEST")) {
			return new Vector();
		}
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return FileSystemDbFunctions.getPathForAnalysisTool((String) params.get(1), requestorId, loginType);
	}

	private Vector getModelInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getModelInfo(String id)");
		return FileSystemDbFunctions.getModelInfo((String) params.get(0));
	}

	private Vector getProjectInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getProjectInfo(String id)");
		return FileSystemDbFunctions.getProjectInfo((String) params.get(0));
	}

	private Integer getInterfaceVersion(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getInterfaceVersion(String id)");
		return FileSystemDbFunctions.getInterfaceVersion((String) params.get(0));
	}

    private Vector getAnalysisToolInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getAnalysisToolInfo(String id)");
		return FileSystemDbFunctions.getAnalysisToolInfo((String) params.get(0));
	}

    private Vector hasAnalysisToolPrototypeFolderBeenCreated(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getAnalysisToolInfo(Integer folderId, String folderName)");

        Integer folderId = (Integer) params.get(0);
        String folderName = (String) params.get(1);
		return FileSystemDbFunctions.hasAnalysisToolPrototypeFolderBeenCreated(folderId.intValue(), folderName);
	}

     public static void deleteAuxFilesForModel(String modelId) {
        System.out.println("deleting auxiliary folder for model :"+modelId);
        String path=DomeServer.getServerAuxFileRoot()+File.separator+modelId;
        File modelFolder=new File(path);
        if(modelFolder.exists()&&modelFolder.isDirectory())
        {
            FileUtils.deleteDirectoryContents(modelFolder,true);
        }
     }
	/*
	private String getObjectXmlDescription(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for getObjectXmlDescription(String modelId, String objectId, Integer objectType)");
		return FileSystemDbFunctions.getObjectDescription (((String) params.get(1)), getInt(params.get(2)));
	}
	*/
}
