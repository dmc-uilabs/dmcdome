// DeployFilesFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;

/**
 * set of functions for deploying items on the server
 */
public class DeployFilesFunctions extends FileSystemFunctions
{

	public static Vector deployNewModel(ServerConnection svrConn, Vector modelInfo, Vector interfaceInfoVector)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.ADD_NEW_MODEL,
		                                Vectors.create(svrConn.getConnectionId(), modelInfo, interfaceInfoVector));
	}

	public static Vector redeployModel(ServerConnection svrConn, Vector modelInfo, Vector interfaceInfoVector)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.REDEPLOY_MODEL,
		                                Vectors.create(svrConn.getConnectionId(), modelInfo, interfaceInfoVector));
	}

	public static Vector getModelVersionData(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_MODEL_VERSION_DATA,
		                                Vectors.create(modelId));
	}

	public static Vector getProjectVersionData(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PROJECT_VERSION_DATA,
		                                Vectors.create(modelId));
	}

    public static Vector getAnalysisToolVersionData(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_ANALYSIS_TOOL_VERSION_DATA,
		                                Vectors.create(modelId));
	}

	public static Vector getPlayspaceVersionData(ServerConnection svrConn, String playspaceId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PLAYSPACE_VERSION_INFO,
		                                Vectors.create(playspaceId));
	}

	public static Vector deployNewPlayspace(ServerConnection svrConn, int folderId,
	                                        String description, String xmlDefinition,
	                                        Vector editPermissions, Vector usePermissions)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.ADD_NEW_PLAYSPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(folderId), description, xmlDefinition,
		                                               editPermissions, usePermissions));
	}

	public static Vector redeployPlayspace(ServerConnection svrConn, String playspaceId,
	                                       String description, String xmlDefinition,
	                                       Vector editPermissions, Vector usePermissions)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.REDEPLOY_PLAYSPACE,
		                                Vectors.create(svrConn.getConnectionId(), playspaceId, description, xmlDefinition,
		                                               editPermissions, usePermissions));
	}

	public static Hashtable getPathsForModels(ServerConnection svrConn, Vector modelIds)
	{
		return (Hashtable) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PATH_FOR_MODEL,
		                                   Vectors.create(svrConn.getConnectionId(), modelIds));
	}

	//todo
	public static Hashtable getPathsForProjects(ServerConnection svrConn, Vector modelIds)
	{
		return (Hashtable) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PATH_FOR_PROJECT,
		                                   Vectors.create(svrConn.getConnectionId(), modelIds));
	}

    public static Hashtable getPathsForAnalysisTools(ServerConnection svrConn, Vector analysisToolIds)
	{
		return (Hashtable) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PATH_FOR_ANALYSIS_TOOL,
		                                   Vectors.create(svrConn.getConnectionId(), analysisToolIds));
	}

	public static Hashtable getPathsForPlayspaces(ServerConnection svrConn, Vector playspaceIds)
	{
		return (Hashtable) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PATH_FOR_PLAYSPACE,
		                                   Vectors.create(svrConn.getConnectionId(), playspaceIds));
	}

	public static String getRootForModel(ServerConnection svrConn, String modelId)
	{
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_ROOT_FOR_MODEL,
		                                Vectors.create(svrConn.getConnectionId(), modelId));
	}

	public static String getRootForPlayspace(ServerConnection svrConn, String playspaceId)
	{
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_ROOT_FOR_PLAYSPACE,
		                                Vectors.create(svrConn.getConnectionId(), playspaceId));
	}

	public static String getRootForProject(ServerConnection svrConn, String projectId)
	{
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_ROOT_FOR_PROJECT,
		                                Vectors.create(svrConn.getConnectionId(), projectId));
	}

    public static String getRootForAnalysisTool(ServerConnection svrConn, String analysisToolId)
	{
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_ROOT_FOR_ANALYSIS_TOOL,
		                                Vectors.create(svrConn.getConnectionId(), analysisToolId));
	}

	public static Vector deployNewProject(ServerConnection svrConn, Vector projectInfo,
	                                      Vector projectInterfacesInfo, Vector integrationModelsInfo)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.ADD_NEW_PROJECT,
		                                Vectors.create(svrConn.getConnectionId(), projectInfo, projectInterfacesInfo, integrationModelsInfo));
	}

	public static Vector redeployProject(ServerConnection svrConn, Vector projectInfo,
	                                     Vector projectInterfacesInfo, Vector integrationModelsInfo)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.REDEPLOY_PROJECT,
		                                Vectors.create(svrConn.getConnectionId(), projectInfo, projectInterfacesInfo, integrationModelsInfo));
	}

	public static Vector uploadFile(ServerConnection svrConn, String modelId,Vector auxfilesContent)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.UPLOAD_AUXFILE,
			                                Vectors.create(svrConn.getConnectionId(), modelId, auxfilesContent));
	}

    public static Vector get_auxfile_info(ServerConnection svrConn, String auxFileId,String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_AUXFILE_INFO,
			                                Vectors.create(svrConn.getConnectionId(), auxFileId,modelId));
	}

    public static String get_customGuiFile_info(ServerConnection svrConn, String customGui_FileName,String interfaceId)
	{
		return (String) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_CUSTOMGUIFILE_INFO,
			                                Vectors.create(svrConn.getConnectionId(), customGui_FileName, interfaceId));
	}

    public static Vector uploadCustomGuiFiles(ServerConnection svrConn, String modelId,Vector customGuifilesContent){
         return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.UPLOAD_CUSTOMGUI_FILE,
			                                Vectors.create(svrConn.getConnectionId(), modelId, customGuifilesContent));
    }

    public static Vector deployNewTool(ServerConnection svrConn, Vector modelInfo, Vector interfaceInfoVector)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.ADD_NEW_ANALYSIS_TOOL,
		                                Vectors.create(svrConn.getConnectionId(), modelInfo, interfaceInfoVector));
	}

    public static Vector redeployAnalysisTool(ServerConnection svrConn, Vector analysisToolInfo,
	                                     Vector analysisToolInterfacesInfo)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.REDEPLOY_ANALYSIS_TOOL,
		                                Vectors.create(svrConn.getConnectionId(), analysisToolInfo, analysisToolInterfacesInfo));
	}

    public static Vector insertAnalysisToolProjectId(ServerConnection svrConn, String toolId, String projectId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.INSERT_ANALYSIS_TOOL_PROJECT_ID,
		                                Vectors.create(toolId, projectId));
	}

    public static Vector getProjectIdInsideAnalysisTool(ServerConnection svrConn, String analysisToolId)
    {
        return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOY_FILES + "." + DbConstants.GET_PROJECT_ID_INSIDE_ANALYSIS_TOOL,
                                        Vectors.create(svrConn.getConnectionId(), analysisToolId));
    }

}
