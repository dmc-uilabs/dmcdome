// DeployFilesHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.functions.DeployFilesDbFunctions;
import mit.cadlab.dome3.network.server.functions.FileEventDbFunction;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.util.FileUtils;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * The XML-RPC handler for functions related to deploying files on the server
 */
public class DeployFilesHandler extends AbstractXmlRpcHandler {

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
    public Object execute(String methodName, Vector params) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "DeployFilesHandler.execute: " + methodName);
        try {
            if (methodName.equals(DbConstants.ADD_NEW_MODEL))
                return deployNewModel(params);
            else if (methodName.equals(DbConstants.REDEPLOY_MODEL))
                return redeployModel(params);
            else if (methodName.equals(DbConstants.ADD_NEW_PLAYSPACE))
                return deployNewPlayspace(params);
            else if (methodName.equals(DbConstants.REDEPLOY_PLAYSPACE))
                return redeployPlayspace(params);
            else if (methodName.equals(DbConstants.GET_MODEL_VERSION_DATA))
                return getModelVersionData(params);
            else if (methodName.equals(DbConstants.GET_PROJECT_VERSION_DATA))
                return getProjectVersionData(params);
            else if (methodName.equals(DbConstants.GET_ANALYSIS_TOOL_VERSION_DATA))
                return getAnalysisToolVersionData(params);
            else if (methodName.equals(DbConstants.GET_PATH_FOR_MODEL))
                return getPathsForModels(params);
            else if (methodName.equals(DbConstants.GET_PATH_FOR_PROJECT))
                return getPathsForProjects(params);
            else if (methodName.equals(DbConstants.GET_PATH_FOR_ANALYSIS_TOOL))
                return getPathsForAnalysisTools(params);
            else if (methodName.equals(DbConstants.GET_PROJECT_ID_INSIDE_ANALYSIS_TOOL))
                return getProjectIdInsideAnalysisTool(params);
            else if (methodName.equals(DbConstants.GET_ROOT_FOR_MODEL))
                return getRootForModel(params);
            else if (methodName.equals(DbConstants.GET_ROOT_FOR_PLAYSPACE))
                return getRootForPlayspace(params);
            else if (methodName.equals(DbConstants.GET_PATH_FOR_PLAYSPACE))
                return getPathsForPlayspace(params);
            else if (methodName.equals(DbConstants.GET_PLAYSPACE_VERSION_INFO))
                return getPlayspaceVersionData(params);
            else if (methodName.equals(DbConstants.ADD_NEW_PROJECT))
                return deployNewProject(params);
            else if (methodName.equals(DbConstants.REDEPLOY_PROJECT))
                return redeployProject(params);
            else if (methodName.equals(DbConstants.REDEPLOY_ANALYSIS_TOOL))
                return redeployAnalysisTool(params);
            else if (methodName.equals(DbConstants.GET_ROOT_FOR_PROJECT))
                return getRootForProject(params);
            else if (methodName.equals(DbConstants.GET_ROOT_FOR_ANALYSIS_TOOL))
                return getRootForAnalysisTool(params);
            else if (methodName.equals(DbConstants.UPLOAD_AUXFILE))
                uploadFile(params);
            else if (methodName.equals(DbConstants.UPLOAD_CUSTOMGUI_FILE))
                uploadCustomGuiFiles(params);
            else if (methodName.equals(DbConstants.GET_AUXFILE_INFO))
                return get_auxfile_info(params);
            else if (methodName.equals(DbConstants.GET_CUSTOMGUIFILE_INFO))
                return get_customGuiFile_info(params);
            else if (methodName.equals(DbConstants.ADD_NEW_ANALYSIS_TOOL))
                return deployNewAnalysisTool(params);
            else if (methodName.equals(DbConstants.INSERT_ANALYSIS_TOOL_PROJECT_ID))
                insertAnalysisToolProjectId(params);
            else
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
            return RuntimeConstants.NO_VECTOR;
        } catch (XmlRpcException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmlRpcException(0, e.getMessage());
        }
    }


    private Vector deployNewModel(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deployNewModel(String sessionId, Vector modelInfo, Vector interfacesInfo)");
        // todo validate information
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        Vector modelInfo = (Vector) params.get(1);
        Vector interfaceInfo = (Vector) params.get(2);
        Vector modelVerInfo = DeployFilesDbFunctions.deployNewModel(modelInfo, fileEventId);
        String modelId = (String) modelVerInfo.get(0);
        Vector ifaceVerInfo = DeployFilesDbFunctions.deployNewInterfaces(modelId, DbConstants.IFACE_PARENT_TYPE_MODEL,
                interfaceInfo, fileEventId);
        return Vectors.create(modelVerInfo, ifaceVerInfo);
    }

    private Vector redeployModel(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for redeployModel(String sessionId, Vector modelInfo, Vector interfacesInfo)");
        // todo validate information
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        Vector modelInfo = (Vector) params.get(1);
        Vector interfaceInfo = (Vector) params.get(2);
        Vector modelVerInfo = DeployFilesDbFunctions.updateModel(modelInfo, fileEventId);
        String modelId = (String) modelVerInfo.get(0);
        Vector ifaceVerInfo = DeployFilesDbFunctions.reDeployInterfaces(modelId, DbConstants.IFACE_PARENT_TYPE_MODEL,
                interfaceInfo, fileEventId);
        return Vectors.create(modelVerInfo, ifaceVerInfo);
    }

    private Vector deployNewPlayspace(Vector params) throws XmlRpcException {
        if (params.size() != 6)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR, PARAM_STR, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deployNewPlayspace(String sessionId, int folderId, " +
                    "String description, String xmlDefinition, Vector editPermissions, Vector usePermissions)");
        // todo validate information
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        return DeployFilesDbFunctions.deployNewPlayspace(getInt(params.get(1)), (String) params.get(2),
                (String) params.get(3), (Vector) params.get(4), (Vector) params.get(5), fileEventId);
    }

    private Vector redeployPlayspace(Vector params) throws XmlRpcException {
        if (params.size() != 6)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR, PARAM_STR, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for redeployPlayspace(String sessionId, String runtimeId, " +
                    "String description, String xmlDefinition, Vector editPermissions, Vector usePermissions)");
        // todo validate information
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        return DeployFilesDbFunctions.updatePlayspace((String) params.get(1), (String) params.get(2),
                (String) params.get(3), (Vector) params.get(4), (Vector) params.get(5), fileEventId);
    }

    private Vector getModelVersionData(Vector params) throws XmlRpcException {
        if (params.size() != 1)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getModelVersionData(String modelId)");
        return DeployFilesDbFunctions.getModelVersionData((String) params.elementAt(0));
    }

    private Vector getProjectVersionData(Vector params) throws XmlRpcException {
        if (params.size() != 1)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getModelVersionData(String modelId)");
        return DeployFilesDbFunctions.getProjectVersionData((String) params.elementAt(0));
    }

    private Vector getAnalysisToolVersionData(Vector params) throws XmlRpcException {
        if (params.size() != 1)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getAnalysisToolVersionData(String analysisToolId)");
        return DeployFilesDbFunctions.getAnalysisToolVersionData((String) params.elementAt(0));
    }

    private Vector getPlayspaceVersionData(Vector params) throws XmlRpcException {
        if (params.size() != 1)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getPlayspaceVersionData(String playspaceDeployId)");
        return DeployFilesDbFunctions.getPlayspaceVersionInfo((String) params.elementAt(0));
    }

    private Vector getProjectIdInsideAnalysisTool(Vector params) throws XmlRpcException
    {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                    DbErrors.XMLRPC_INVALID_ARGUMENT_LIST_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getAnalysisToolFolderContents" +
                    "(Strong connectionId, String analysisToolId)");
        String analysisToolId = (String) params.get(1);

        return FileSystemDbFunctions.getProjectIdInsideAnalysisTool(analysisToolId);

    }

    private Hashtable getPathsForModels(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getPathForModels(String connectionId, Vector modelIds)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        Vector modelIds = (Vector) params.elementAt(1);
        Hashtable pathsForModelIds = new Hashtable();
        for (int i = 0; i < modelIds.size(); i++) {
            String pathForModel = DeployFilesDbFunctions.getPathForModel((String) modelIds.elementAt(i), requestorId);
            if (!pathForModel.equals(""))
                pathsForModelIds.put(modelIds.elementAt(i), pathForModel);
        }
        return pathsForModelIds;
    }

    private Hashtable getPathsForProjects(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getPathForProjects(String connectionId, Vector projectIds)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        Vector projectsIds = (Vector) params.elementAt(1);
        Hashtable pathsForModelIds = new Hashtable();
        for (int i = 0; i < projectsIds.size(); i++) {
            String pathForProject = DeployFilesDbFunctions.getPathForProject((String) projectsIds.elementAt(i), requestorId);
            if (!pathForProject.equals(""))
                pathsForModelIds.put(projectsIds.elementAt(i), pathForProject);
        }
        return pathsForModelIds;
    }

    private Hashtable getPathsForAnalysisTools(Vector params) throws XmlRpcException
    {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getPathForAnalysisTools(String connectionId, Vector projectIds)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        Vector analysisToolIds = (Vector) params.elementAt(1);
        Hashtable pathsForAnalysisToolIds = new Hashtable();
        for (int i = 0; i < analysisToolIds.size(); i++)
        {
            String pathForAnalysisTool = DeployFilesDbFunctions.getPathForAnalysisTool(
                    (String) analysisToolIds.elementAt(i), requestorId);
            if (!pathForAnalysisTool.equals(""))
                pathsForAnalysisToolIds.put(analysisToolIds.elementAt(i), pathForAnalysisTool);
        }
        return pathsForAnalysisToolIds;
    }

    private Hashtable getPathsForPlayspace(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getPathForPlayspace(String connectionId, Vector modelIds)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        Vector playspaceIds = (Vector) params.elementAt(1);
        Hashtable pathsForPlayspaceIds = new Hashtable();
        for (int i = 0; i < playspaceIds.size(); i++) {
            String pathForPlayspace = DeployFilesDbFunctions.getPathForPlayspace((String) playspaceIds.elementAt(i), requestorId);
            if (!pathForPlayspace.equals(""))
                pathsForPlayspaceIds.put(playspaceIds.elementAt(i), pathForPlayspace);
        }
        return pathsForPlayspaceIds;
    }

    private String getRootForModel(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getRootForModel(String connectionId, String modelId)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        String modelId = (String) params.elementAt(1);
        String pathForModel = DeployFilesDbFunctions.getPathForModel(modelId, requestorId);
        if (pathForModel != null)
            return pathForModel.substring(0, pathForModel.indexOf(":"));
        else
            return null;
    }

    private String getRootForPlayspace(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getRootForPlayspace(String connectionId, String runtimeId)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        String playspaceId = (String) params.elementAt(1);
        //System.out.println(playspaceId);
        String pathForPlayspace = DeployFilesDbFunctions.getPathForPlayspace(playspaceId, requestorId);
        //System.out.println(pathForPlayspace);
        if (pathForPlayspace != null)
            return pathForPlayspace.substring(0, pathForPlayspace.indexOf(":"));
        else
            return null;
    }

    private String getRootForProject(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getRootForProject(String connectionId, String runtimeId)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        String projectId = (String) params.elementAt(1);
        //System.out.println(projectId);
        String pathForProject = DeployFilesDbFunctions.getPathForProject(projectId, requestorId);
        //System.out.println(pathForProject);
        if (pathForProject != null)
            return pathForProject.substring(0, pathForProject.indexOf(":"));
        else
            return null;
    }

    private String getRootForAnalysisTool(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for getRootForAnalysisTool(String connectionId, String runtimeId)");
        String sessionId = (String) params.get(0);
        int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
        String analysisToolId = (String) params.elementAt(1);
        //System.out.println(projectId);
        String pathForAnalysisTool = DeployFilesDbFunctions.getPathForAnalysisTool(analysisToolId, requestorId);
        //System.out.println(pathForProject);
        if (pathForAnalysisTool != null)
            return pathForAnalysisTool.substring(0, pathForAnalysisTool.indexOf(":"));
        else
            return null;
    }

    private Vector deployNewProject(Vector params) throws XmlRpcException {
        if (params.size() != 4)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deployNewProject(String sessionId, Vector projectInfo," +
                    "Vector projectInterfacesInfo, Vector integrationModelsInfo)");
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        Vector projectInfo = (Vector) params.get(1);
        Vector projectInterfacesInfo = (Vector) params.get(2);
        Vector projectVerInfo = DeployFilesDbFunctions.deployNewProject(projectInfo, fileEventId);
        String projectId = (String) projectVerInfo.get(0);
        Vector projIfaceVerInfo = DeployFilesDbFunctions.deployNewInterfaces(projectId, DbConstants.IFACE_PARENT_TYPE_PROJECT,
                projectInterfacesInfo, fileEventId);
        Vector iModelVectors = (Vector) params.get(3);
        Vector allIModelVerInfo = new Vector();
        for (int i = 0; i < iModelVectors.size(); i++) {
            Vector iModelInfo = (Vector) ((Vector) iModelVectors.get(i)).get(0);
            iModelInfo.add(0, projectId);
            Vector iModelVerInfo = DeployFilesDbFunctions.deployNewIModel(iModelInfo, fileEventId);
            String iModelId = (String) iModelVerInfo.get(0);

            Vector iModelInterfaceInfo = (Vector) ((Vector) iModelVectors.get(i)).get(1);
            Vector iModelInterfaceVerInfo = DeployFilesDbFunctions.deployNewInterfaces(iModelId, DbConstants.IFACE_PARENT_TYPE_IMODEL,
                    iModelInterfaceInfo, fileEventId);
            allIModelVerInfo.add(Vectors.create(iModelVerInfo, iModelInterfaceVerInfo));
        }
        return Vectors.create(projectVerInfo, projIfaceVerInfo, allIModelVerInfo);
    }

    private Vector redeployAnalysisTool (Vector params) throws XmlRpcException
    {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_VEC}))
                    throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                            "invalid arguments for redeployAnalysisTool(String sessionId, Vector analysisToolInfo," +
                                        "Vector analysisToolInterfacesInfo)");

        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        Vector analysisToolInfo = (Vector) params.get(1);
        Vector analysisToolInterfacesInfo = (Vector) params.get(2);

        Vector analysisToolVersionInfo = DeployFilesDbFunctions.updateAnalysisTool(analysisToolInfo, fileEventId);

        String analysisToolId = (String) analysisToolVersionInfo.get(0);

        Vector analysisToolIfaceVersionInfo = DeployFilesDbFunctions.reDeployInterfaces(analysisToolId, DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL,
                                                    analysisToolInterfacesInfo, fileEventId);

        return Vectors.create(analysisToolVersionInfo, analysisToolIfaceVersionInfo);
    }

    private Vector redeployProject(Vector params) throws XmlRpcException {
        if (params.size() != 4)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for redeployProject(String sessionId, Vector projectInfo," +
                    "Vector projectInterfacesInfo, Vector integrationModelsInfo)");

        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        Vector projectInfo = (Vector) params.get(1);
        Vector projectInterfacesInfo = (Vector) params.get(2);
        Vector projectVerInfo = DeployFilesDbFunctions.updateProject(projectInfo, fileEventId);
        String projectId = (String) projectVerInfo.get(0);
        Vector projIfaceVerInfo = DeployFilesDbFunctions.reDeployInterfaces(projectId, DbConstants.IFACE_PARENT_TYPE_PROJECT,
                projectInterfacesInfo, fileEventId);

        Vector iModelVectors = (Vector) params.get(3);
        // need to delete old IModels before adding/updating any iModels to avoid name conflicts within a project
        // first sort information into new, updated, and old iModels
        Vector oldIModelIds = FileSystemDbFunctions.getIModelIdsFromProject(projectId);
        Vector newIModels = new Vector();
        Vector updatedIModels = new Vector();
        for (int i = 0; i < iModelVectors.size(); i++) {
            Vector iModelInfo = (Vector) ((Vector) iModelVectors.get(i)).get(0);
            Vector iModelInterfaceInfo = (Vector) ((Vector) iModelVectors.get(i)).get(1);
            String iModelDeployId = (String) iModelInfo.get(0);
            if (iModelDeployId.equals("")) // new iModel -> deploy
            {
                iModelInfo.setElementAt(projectId, 0);
                newIModels.add(new Vector[]{iModelInfo, iModelInterfaceInfo});
            } else {    // previously deployed iModel -> redeploy
                oldIModelIds.remove(iModelDeployId);
                updatedIModels.add(new Vector[]{iModelInfo, iModelInterfaceInfo});
            }
        }
        // delete iModels that no longer exist
        FileSystemDbFunctions.deleteIModels(oldIModelIds);

        Vector allIModelVerInfo = new Vector();
        Vector iModelVerInfo, iModelInterfaceVerInfo;
        String iModelId;
        for (int i = 0; i < updatedIModels.size(); i++) {
            Vector[] iModelAndInterfaceInfo = (Vector[]) updatedIModels.elementAt(i);
            iModelVerInfo = DeployFilesDbFunctions.updateIModel(iModelAndInterfaceInfo[0], fileEventId);
            iModelId = (String) iModelVerInfo.get(0);
            iModelInterfaceVerInfo = DeployFilesDbFunctions.reDeployInterfaces(iModelId, DbConstants.IFACE_PARENT_TYPE_IMODEL,
                    iModelAndInterfaceInfo[1], fileEventId);
            allIModelVerInfo.add(Vectors.create(iModelVerInfo, iModelInterfaceVerInfo));
        }
        for (int i = 0; i < newIModels.size(); i++) {
            Vector[] iModelAndInterfaceInfo = (Vector[]) newIModels.elementAt(i);
            iModelVerInfo = DeployFilesDbFunctions.deployNewIModel(iModelAndInterfaceInfo[0], fileEventId);
            iModelId = (String) iModelVerInfo.get(0);
            iModelInterfaceVerInfo = DeployFilesDbFunctions.reDeployInterfaces(iModelId, DbConstants.IFACE_PARENT_TYPE_IMODEL,
                    iModelAndInterfaceInfo[1], fileEventId);
            allIModelVerInfo.add(Vectors.create(iModelVerInfo, iModelInterfaceVerInfo));
        }

        return Vectors.create(projectVerInfo, projIfaceVerInfo, allIModelVerInfo);
    }

    private void uploadFile(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for uploadFile(svrConn.getConnectionId(), modelId, auxfilesContent)");
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        String modelId = (String) params.elementAt(1);
        Vector auxFiles = (Vector) params.get(2);
        for (Iterator i = auxFiles.iterator(); i.hasNext();) {
            Vector auxF = (Vector) i.next();
            String id = (String) auxF.get(0);
            String name = (String) auxF.get(1);
            String filepath = ((String) auxF.get(2)).replace("\\", "/");
            String filename = new File(filepath).getName();
            //boolean shouldUpload = ((Boolean) auxF.get(3)).booleanValue();       //always upload
            byte[] content = (byte[]) auxF.get(4);

            //vector of auxiliary file information: id, name, filepath, upload,filecontent
//first check is there any record
            if (DeployFilesDbFunctions.getMostRecentAuxFileVersion(id, modelId) == 0) //no record
            {
//first write to auxiliary database
                DeployFilesDbFunctions.initially_insertAuxiliaryFileInfo(id, modelId,"",filename, fileEventId);
                // if (shouldUpload) {
                String location_on_server = createFileOnServer(modelId, filename, content);
                DeployFilesDbFunctions.update_location_AuxiliaryFileInfo(id, modelId, location_on_server);
                // }
            } else {
                Vector mostRecentInfo = DeployFilesDbFunctions.getMostRecentAuxFileInfo(id, modelId);
                if (mostRecentInfo == null) {
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
                            "can not get most recent info for auxiliary file " + id);
                }
//check if any info has changed:
//content of vector (FILE_NAME,LOCATION_ON_SERVER,CONTENT,VERSION)
                String old_filename = (String) mostRecentInfo.get(0);
                String old_location_on_server = (String) mostRecentInfo.get(1);
                if (!old_location_on_server.equalsIgnoreCase("")) {
                    //clean up old file
                    cleanFileOnServer(modelId, filename);
                }
                //that is upgrade version infomation
                DeployFilesDbFunctions.update_content_AuxiliaryFileInfo(id, modelId, filename, fileEventId);
                //if (shouldUpload) {
                String location_on_server = createFileOnServer(modelId, filename, content);
                DeployFilesDbFunctions.update_location_AuxiliaryFileInfo(id, modelId, location_on_server);
                // }
            }
        }
    }

    private String createFileOnServer(String model_Id, String filename, byte[] content) {
        String root = DomeServer.getServerAuxFileRoot();
        String modelFolderpath = root + File.separator + model_Id;
        File modelFolder = new File(modelFolderpath);
        try {
            if (!modelFolder.exists() || (modelFolder.exists() && !modelFolder.isDirectory())) {
                modelFolder.mkdir();
            }
            String filepath = modelFolder.getPath() + File.separator + filename;
            File AuxFile = new File(filepath);
            System.out.println(filepath);
            //clean up before creating
            if (AuxFile.exists() && AuxFile.isFile()) AuxFile.delete();

            AuxFile.createNewFile();
            FileUtils.writeByteArrayAsBinaryFile(content, filepath);
            return model_Id + File.separator + filename;
        } catch (IOException e) {
            System.out.println("AuxFile write to server failed");
            e.printStackTrace();
            return "";
        }
    }

    //Qing ---- added Aug 5th, for customGUI
    private String createFileOnServer(String model_Id, String Interface_Id, String filename, byte[] content) {
        String root = DomeServer.getServerAuxFileRoot();
        String modelFolderpath = root + File.separator + model_Id;
        File modelFolder = new File(modelFolderpath);
        try {
            if (!modelFolder.exists() || (modelFolder.exists() && !modelFolder.isDirectory())) {
                modelFolder.mkdir();
            }
            File interfaceFolder=new File(modelFolderpath + File.separator + Interface_Id);
            if (!interfaceFolder.exists() || (interfaceFolder.exists() && !interfaceFolder.isDirectory())) {
                interfaceFolder.mkdir();
            }
            String filepath = interfaceFolder.getPath() + File.separator +filename;
            File AuxFile = new File(filepath);
            System.out.println(filepath);
            //clean up before creating
            if (AuxFile.exists() && AuxFile.isFile()) AuxFile.delete();

            AuxFile.createNewFile();
            FileUtils.writeByteArrayAsBinaryFile(content, filepath);
            return model_Id + File.separator +Interface_Id+ File.separator+ filename;
        } catch (IOException e) {
            System.out.println("AuxFile write to server failed");
            e.printStackTrace();
            return "";
        }
    }


    private void cleanFileOnServer(String model_Id, String filename) {
        String root = DomeServer.getServerAuxFileRoot();
        String filepath = root + File.separator + model_Id + File.separator + filename;
        File AuxFile = new File(filepath);
        //clean up before creating
        if (AuxFile.exists() && AuxFile.isFile()) AuxFile.delete();
    }


    private static Vector get_auxfile_info(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "get_auxfile_info(ServerConnection svrConn, String auxFileId,String modelId)");
        String auxId = (String) params.get(1);
        String modelId = (String) params.elementAt(2);

        return DeployFilesDbFunctions.getMostRecentAuxFileInfo(auxId, modelId);

    }

    private static String get_customGuiFile_info(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "get_customGuiFile_info(ServerConnection svrConn, String customGui_FileName, String modelId)");
        String customGuifilename = (String) params.get(1);
        String iface_deploy_Id = (String) params.elementAt(2);

        return DeployFilesDbFunctions.getMostRecentCustomGuiFileLocation(customGuifilename, iface_deploy_Id);

    }

    //Qing --- added Aug 5th, for upload customGuiFile,
    private void uploadCustomGuiFiles(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for uploadCustomGuiFiles(ServerConnection svrConn, String modelId,Vector customGuifilesContent)");
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        String modelId = (String) params.elementAt(1);
        Vector customGuiFiles = (Vector) params.get(2);
        for (Iterator i = customGuiFiles.iterator(); i.hasNext();) {
            Vector customguiF = (Vector) i.next();
            String id = (String) customguiF.get(0);
            String name = (String) customguiF.get(1);
            String filepath = (String) customguiF.get(2);
            String filename = new File(filepath).getName();
            String interfaceId = (String) customguiF.get(3);
            byte[] content = (byte[]) customguiF.get(4);

            //vector of auxiliary file information: id, name, filepath, upload,filecontent
//first check is there any record
            if (DeployFilesDbFunctions.getMostRecentCustomGuiFileVersion(filename, interfaceId) == 0) //no record
            {
//first write to auxiliary database
                DeployFilesDbFunctions.initially_insertAuxiliaryFileInfo(id,modelId,interfaceId, filename, fileEventId);
                // if (shouldUpload) {
                String location_on_server = createFileOnServer(modelId, interfaceId,filename, content);
                DeployFilesDbFunctions.update_location_customGuiFileInfo(interfaceId,filename,location_on_server);
                // }
            } else {
                int maxVersion = DeployFilesDbFunctions.getMostRecentCustomGuiFileVersion(filename, interfaceId);
                //no need to clean up bcz the following code will clean up the old file,
                String location_on_server = createFileOnServer(modelId, interfaceId,filename, content);

                //that is upgrade version infomation
                DeployFilesDbFunctions.update_content_customGuiFileInfo(id, modelId, interfaceId, filename, maxVersion, fileEventId);

                DeployFilesDbFunctions.update_location_customGuiFileInfo(interfaceId,filename,location_on_server);

            }
        }
    }

    private Vector deployNewAnalysisTool(Vector params) throws XmlRpcException {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deployNewTool(String sessionId, Vector modelInfo, Vector interfacesInfo)");
        // todo validate information
        String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createUploadFileEvent(sessionId);
        Vector toolInfo = (Vector) params.get(1);
        Vector interfaceInfo = (Vector) params.get(2);
        Vector analysisToolVerInfo = DeployFilesDbFunctions.deployNewAnalysisTool(toolInfo, fileEventId);
        String analysisToolId = (String) analysisToolVerInfo.get(0);
        Vector ifaceVerInfo = DeployFilesDbFunctions.deployNewInterfaces(analysisToolId, DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL,
                interfaceInfo, fileEventId);
        return Vectors.create(analysisToolVerInfo, ifaceVerInfo);
    }

    private void insertAnalysisToolProjectId(Vector params) throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for insertAnalysisToolProjectId(String analysisToolId, String projectId)");
        String analysisToolId = (String) params.get(0);
        String projectId = (String) params.get(1);
        DeployFilesDbFunctions.insertAnalysisToolProjectId(analysisToolId, projectId);
    }

}