// DeployFilesDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.DomeXmlData;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;

import org.apache.xmlrpc.XmlRpcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 *  Functions to deploy/redeploy files in DOME.
 */
public class DeployFilesDbFunctions {

    public static final String slash = "/";
    private static final AbstractXmlRpcHandler HDL = null; // used to access parameter validation functions
    private static final String _EMPTY_STRING = "";

    private static final String insertModelInfoQuery = "insert into MODELS (ID,TYPE,NAME,DESCRIPTION,FOLDER_ID) values (?,?,?,?,?)";
    private static final String getMostRecentModelVersionQuery = "select MAX(VERSION) from MODEL_VERSIONS where MODEL_ID=?";
    private static final String insertModelVersionInfoQuery = "insert into MODEL_VERSIONS (MODEL_ID,VERSION,BUILD_ID,BUILD_VERSION,CONTENT,FILE_EVENT_ID) values (?,?,?,?,?,?)";
    private static final String updateModelVersionIdQuery = "update MODELS set LAST_VERSION_ID=? where ID=?";
    private static final String getModelVersionInfo = "select MODEL_ID,VERSION from MODELS,MODEL_VERSIONS where LAST_VERSION_ID=MODEL_VERSIONS.ID and MODEL_ID=?";
    private static final String getModelVersionContentQuery = "select CONTENT from MODELS,MODEL_VERSIONS where MODELS.ID=? and LAST_VERSION_ID=MODEL_VERSIONS.ID";
    private static final String getProjectVersionContentQuery = "select CONTENT from PROJECTS,PROJECT_VERSIONS where PROJECTS.ID=? and LAST_VERSION_ID=PROJECT_VERSIONS.ID";
    private static final String getIModelVersionContentQuery = "select CONTENT from INTEGRATION_MODELS,INTEGRATION_MODEL_VERSIONS where INTEGRATION_MODELS.ID=? and LAST_VERSION_ID=INTEGRATION_MODEL_VERSIONS.ID";
    private static final String checkIfModelExists = "select * from MODELS where NAME = ? and FOLDER_ID = ?";
    private static final String getRedeployModelInfo = "select NAME, FOLDER_ID from MODELS where ID = ?";
    private static final String getModelNameConflicts = "select ID from MODELS where FOLDER_ID = ? and NAME=?";

    // analysis tool
    private static final String checkIfAnalysisToolExists = "select * from ANALYSIS_TOOLS where NAME = ? and FOLDER_ID = ?";
    private static final String getAnalysisToolVersionInfo = "select ANALYSIS_TOOL_ID, VERSION from ANALYSIS_TOOLS, ANALYSIS_TOOL_VERSIONS where LAST_VERSION_ID=ANALYSIS_TOOL_VERSIONS.ID and ANALYSIS_TOOL_ID=?";
    private static final String getMostRecentAnalysisToolVersionQuery = "select MAX(VERSION) from ANALYSIS_TOOL_VERSIONS where ANALYSIS_TOOL_ID=?";
    private static final String insertAnalysisToolInfoQuery = "insert into ANALYSIS_TOOLS (ID, TYPE, NAME, DESCRIPTION, PROJECT_ID, FOLDER_ID) values (?,?,?,?,?,?)";
    private static final String insertAnalysisToolVersionInfoQuery = "insert into ANALYSIS_TOOL_VERSIONS (ANALYSIS_TOOL_ID,VERSION,BUILD_ID,BUILD_VERSION,CONTENT,FILE_EVENT_ID) values (?,?,?,?,?,?)";
    private static final String updateAnalysisToolVersionIdQuery = "update ANALYSIS_TOOLS set LAST_VERSION_ID=? where ID=?";
    private static final String insertAnalysisToolProjectId = "update ANALYSIS_TOOLS set PROJECT_ID=? where ID=?";
    private static final String getRedeployAnalysisToolInfo = "select NAME, FOLDER_ID from ANALYSIS_TOOLS where ID = ?";
    private static final String getAnalysisToolNameConflicts = "select ID from ANALYSIS_TOOLS where FOLDER_ID = ? and NAME=?";
    private static final String getAnalysisToolVersionContentQuery = "select CONTENT from ANALYSIS_TOOLS, ANALYSIS_TOOL_VERSIONS " +
                                                                            "where ANALYSIS_TOOLS.ID=? and LAST_VERSION_ID=ANALYSIS_TOOL_VERSIONS.ID";

    private static final String insertInterfaceInfoQuery = "insert into INTERFACES (ID,STATUS,NAME,DESCRIPTION,PARENT_ID,PARENT_TABLE) values (?,?,?,?,?,?)";
    private static final String getMostRecentInterfaceVersionQuery = "select MAX(VERSION) from INTERFACE_VERSIONS where INTERFACE_ID=?";
    private static final String insertInterfaceVersionInfoQuery = "insert into INTERFACE_VERSIONS (INTERFACE_ID,VERSION,BUILD_ID,BUILD_VERSION,CONTENT,MAPPINGS,FILE_EVENT_ID) values (?,?,?,?,?,?,?)";
    private static final String updateInterfaceVersionIdQuery = "update INTERFACES set LAST_VERSION_ID=? where ID=?";
    private static final String getInterfacesVersionInfoQuery = "select INTERFACE_ID,VERSION,BUILD_ID from INTERFACES,INTERFACE_VERSIONS where PARENT_ID=? and (STATUS='" + DbConstants.INTERFACE_STATUS_AVAILABLE +
            "' or STATUS='" + DbConstants.INTERFACE_STATUS_UNAVAILABLE + "') and LAST_VERSION_ID=INTERFACE_VERSIONS.ID";


    private static final String insertPlayspaceInfoQuery = "insert into PLAYSPACES (ID,NAME,DESCRIPTION,FOLDER_ID) values (?,?,?,?)";
    private static final String getMostRecentPlayspaceVersionQuery = "select MAX(VERSION) from PLAYSPACE_VERSIONS where PLAYSPACE_ID=?";
    private static final String insertPlayspaceVersionInfoQuery = "insert into PLAYSPACE_VERSIONS (PLAYSPACE_ID,VERSION,BUILD_ID,BUILD_VERSION,CONTENT,FILE_EVENT_ID) values (?,?,?,?,?,?)";
    private static final String updatePlayspaceVersionIdQuery = "update PLAYSPACES set LAST_VERSION_ID=? where ID=?";
    private static final String getPlayspaceVersionInfo = "select PLAYSPACE_ID,VERSION from PLAYSPACES,PLAYSPACE_VERSIONS where LAST_VERSION_ID=PLAYSPACE_VERSIONS.ID and PLAYSPACE_ID=?";
    private static final String getPlayspaceVersionContentQuery = "select CONTENT from PLAYSPACES,PLAYSPACE_VERSIONS where PLAYSPACES.ID=? and LAST_VERSION_ID=PLAYSPACE_VERSIONS.ID";
    private static final String checkIfPlayspaceExists = "select * from PLAYSPACES where NAME = ? and FOLDER_ID = ?";
    private static final String getRedeployPlayspaceInfo = "select NAME, FOLDER_ID from PLAYSPACES where ID = ?";
    private static final String getPlayspaceNameConflicts = "select ID from PLAYSPACES where FOLDER_ID = ? and NAME=?";

    private static final String checkIfProjectExists = "select * from PROJECTS where NAME = ? and FOLDER_ID = ?";
    private static final String insertProjectInfoQuery = "insert into PROJECTS (ID,NAME,DESCRIPTION,FOLDER_ID) values (?,?,?,?)";
    private static final String insertProjectVersionInfoQuery = "insert into PROJECT_VERSIONS (PROJECT_ID,VERSION,BUILD_ID,BUILD_VERSION,CONTENT,FILE_EVENT_ID) values (?,?,?,?,?,?)";
    private static final String getMostRecentProjectVersionQuery = "select MAX(VERSION) from PROJECT_VERSIONS where PROJECT_ID=?";
    private static final String updateProjectVersionIdQuery = "update PROJECTS set LAST_VERSION_ID=? where ID=?";
    private static final String getProjectVersionInfo = "select PROJECT_ID,VERSION from PROJECTS,PROJECT_VERSIONS where LAST_VERSION_ID=PROJECT_VERSIONS.ID and PROJECT_ID=?";
    private static final String getRedeployProjectInfo = "select NAME, FOLDER_ID from PROJECTS where ID = ?";
    private static final String getProjectNameConflicts = "select ID from PROJECTS where FOLDER_ID = ? and NAME=?";

    private static final String checkIfIModelExists = "select * from INTEGRATION_MODELS where NAME = ? and PROJECT_ID = ?";
    private static final String insertIModelInfoQuery = "insert into INTEGRATION_MODELS (ID,NAME,DESCRIPTION,PROJECT_ID) values (?,?,?,?)";
    private static final String insertIModelVersionInfoQuery = "insert into INTEGRATION_MODEL_VERSIONS (MODEL_ID,VERSION,BUILD_ID,BUILD_VERSION,CONTENT,FILE_EVENT_ID) values (?,?,?,?,?,?)";
    private static final String getMostRecentIModelVersionQuery = "select MAX(VERSION) from INTEGRATION_MODEL_VERSIONS where MODEL_ID=?";
    private static final String updateIModelVersionIdQuery = "update INTEGRATION_MODELS set LAST_VERSION_ID=? where ID=?";
    private static final String getIModelVersionInfo = "select MODEL_ID,VERSION, BUILD_ID from INTEGRATION_MODELS,INTEGRATION_MODEL_VERSIONS where LAST_VERSION_ID=INTEGRATION_MODEL_VERSIONS.ID and MODEL_ID=?";
    private static final String getRedeployIModelInfo = "select NAME, PROJECT_ID from INTEGRATION_MODELS where ID = ?";
    private static final String getIModelNameConflicts = "select ID from INTEGRATION_MODELS where PROJECT_ID = ? and NAME=?";

    public static String getPathForAnalysisTool(String analysisToolId, int requestorId) throws XmlRpcException
    {
        try
        {
            String privateOrPublic = null;
            StringBuffer filePath = new StringBuffer();
            if (analysisToolId == null)
            {
                OneButton1Msg.showError(null, "server error", "invalid analysis tool id passed" +
                        "to DeployFilesDbFunction.getPathForAnalysisTool", "ok", OneButton1Msg.DEFAULT_SIZE);
                return null;
            }
            String query = "select FOLDER_ID, NAME from ANALYSIS_TOOLS where ID = '" + analysisToolId + "'";
            String modelFoldersQuery = "select NAME, PARENT_ID from MODEL_FOLDERS where ID = '";
            Vector v1 = DbUtils.executeQuery(query, true);

            if (v1.isEmpty())
                return _EMPTY_STRING;

            filePath.insert(0, slash + ((String) v1.elementAt(1)));

            if (((Integer) v1.elementAt(0)).intValue() != DbConstants.NULL)
            {
                int temp = ((Integer) v1.elementAt(0)).intValue();
                Vector v2 = DbUtils.executeQuery(modelFoldersQuery
                        + ((Integer) v1.elementAt(0)).intValue() + "'", true);

                if (((String) v2.elementAt(0)).endsWith("_public"))
                {
                    filePath.insert(0, "Public");
                    privateOrPublic = "PUBLIC";
                }
                else if (((String) v2.elementAt(0)).endsWith("_private"))
                {
                    filePath.insert(0, "Private");
                    privateOrPublic = "PRIVATE";
                }
                else
                    filePath.insert(0, slash + ((String) v2.elementAt(0)));
                if (((Integer) v2.elementAt(1)).intValue() != DbConstants.NULL)
                {
                    temp = ((Integer) v2.elementAt(1)).intValue();
                    Vector v3 = DbUtils.executeQuery(modelFoldersQuery
                            + ((Integer) v2.elementAt(1)).intValue() + "'", true);
                    int newTemp = 0;
                    while (((Integer) v3.elementAt(1)).intValue() != DbConstants.NULL)
                    {
                        filePath.insert(0, slash + ((String) v3.elementAt(0)));
                        newTemp = ((Integer) v3.elementAt(1)).intValue();
                        if (newTemp != DbConstants.NULL)
                                                temp = newTemp;
                        v3 = DbUtils.executeQuery(modelFoldersQuery + newTemp + "'", true);
                    }
                    if (((String) v3.elementAt(0)).endsWith("_public"))
                    {
                        filePath.insert(0, "Public");
                        privateOrPublic = "PUBLIC";
                    }
                    if (((String) v3.elementAt(0)).endsWith("_private"))
                    {
                        filePath.insert(0, "Private");
                        privateOrPublic = "PRIVATE";
                    }
                }

                Vector vFinal = DbUtils.executeQuery("select TYPE, NAME, ID from users_groups, " +
                            "user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic +
                                        "_MODEL_FOLDER_ID='" + temp + "'", true);

                if (((Integer) vFinal.elementAt(2)).intValue() == requestorId)
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                else if (((Integer) vFinal.elementAt(2)).intValue() == 1)
                    filePath.insert(0, "Server: ");
                else
                {
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                    if (((String) vFinal.elementAt(0)).equalsIgnoreCase(DbConstants.USER_TYPE))
                        filePath.insert(0, "Users" + slash);
                    else
                        filePath.insert(0, "Groups" + slash);
                }
            }
            return filePath.toString();
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static String getPathForProject(String projectId, int requestorId) throws XmlRpcException {
        try {
            String privateOrPublic = null;
            StringBuffer filePath = new StringBuffer();
            if (projectId == null) {
                System.out.println("Invalid modelId passed to DeployFilesDbFunctions.getPathForProject");
                return null;
            }
            String query = "Select FOLDER_ID, NAME from PROJECTS where ID = '" + projectId + "'";
            Vector v = DbUtils.executeQuery(query, true);
            if (v.isEmpty()) return "";
            filePath.insert(0, slash + ((String) v.elementAt(1)));
            if (((Integer) v.elementAt(0)).intValue() != DbConstants.NULL) {
                int temp = ((Integer) v.elementAt(0)).intValue();
                Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + ((Integer) v.elementAt(0)).intValue() + "'", true);
                if (((String) v2.elementAt(0)).endsWith("_public")) {
                    filePath.insert(0, "Public");
                    privateOrPublic = "PUBLIC";
                } else if (((String) v2.elementAt(0)).endsWith("_private")) {
                    filePath.insert(0, "Private");
                    privateOrPublic = "PRIVATE";
                } else
                    filePath.insert(0, slash + ((String) v2.elementAt(0)));
                if (((Integer) v2.elementAt(1)).intValue() != DbConstants.NULL) {
                    temp = ((Integer) v2.elementAt(1)).intValue();
                    Vector v3 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + ((Integer) v2.elementAt(1)).intValue() + "'", true);
                    int newTemp = 0;
                    while (((Integer) v3.elementAt(1)).intValue() != DbConstants.NULL) {
                        filePath.insert(0, slash + ((String) v3.elementAt(0)));
                        newTemp = ((Integer) v3.elementAt(1)).intValue();
                        if (newTemp != DbConstants.NULL) {
                            temp = newTemp;
                        }
                        v3 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + newTemp + "'", true);
                    }
                    if (((String) v3.elementAt(0)).endsWith("_public")) {
                        filePath.insert(0, "Public");
                        privateOrPublic = "PUBLIC";
                    }
                    if (((String) v3.elementAt(0)).endsWith("_private")) {
                        filePath.insert(0, "Private");
                        privateOrPublic = "PRIVATE";
                    }
                }

                Vector vFinal =
                        DbUtils.executeQuery("select TYPE, NAME, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_MODEL_FOLDER_ID='" + temp + "'", true);
                if (((Integer) vFinal.elementAt(2)).intValue() == requestorId)
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                else if (((Integer) vFinal.elementAt(2)).intValue() == 1)
                    filePath.insert(0, "Server: ");
                else {
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                    if (((String) vFinal.elementAt(0)).equalsIgnoreCase(DbConstants.USER_TYPE))
                        filePath.insert(0, "Users" + slash);
                    else
                        filePath.insert(0, "Groups" + slash);
                }
            }
            return filePath.toString();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static String getPathForModel(String modelId, int requestorId) throws XmlRpcException {
        try {
            String privateOrPublic = null;
            StringBuffer filePath = new StringBuffer();
            if (modelId == null) {
                System.out.println("Invalid modelId passed to DeployFilesDbFunctions.getPathForModel");
                return null;
            }
            String query = "Select FOLDER_ID, NAME from MODELS where ID = '" + modelId + "'";
            Vector v = DbUtils.executeQuery(query, true);
            if (v.isEmpty()) return "";
            filePath.insert(0, slash + ((String) v.elementAt(1)));
            if (((Integer) v.elementAt(0)).intValue() != DbConstants.NULL) {
                int temp = ((Integer) v.elementAt(0)).intValue();
                Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + ((Integer) v.elementAt(0)).intValue() + "'", true);
                if (((String) v2.elementAt(0)).endsWith("_public")) {
                    filePath.insert(0, "Public");
                    privateOrPublic = "PUBLIC";
                } else if (((String) v2.elementAt(0)).endsWith("_private")) {
                    filePath.insert(0, "Private");
                    privateOrPublic = "PRIVATE";
                } else
                    filePath.insert(0, slash + ((String) v2.elementAt(0)));
                if (((Integer) v2.elementAt(1)).intValue() != DbConstants.NULL) {
                    temp = ((Integer) v2.elementAt(1)).intValue();
                    Vector v3 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + ((Integer) v2.elementAt(1)).intValue() + "'", true);
                    int newTemp = 0;
                    while (((Integer) v3.elementAt(1)).intValue() != DbConstants.NULL) {
                        filePath.insert(0, slash + ((String) v3.elementAt(0)));
                        newTemp = ((Integer) v3.elementAt(1)).intValue();
                        if (newTemp != DbConstants.NULL) {
                            temp = newTemp;
                        }
                        v3 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + newTemp + "'", true);
                    }
                    if (((String) v3.elementAt(0)).endsWith("_public")) {
                        filePath.insert(0, "Public");
                        privateOrPublic = "PUBLIC";
                    }
                    if (((String) v3.elementAt(0)).endsWith("_private")) {
                        filePath.insert(0, "Private");
                        privateOrPublic = "PRIVATE";
                    }
                }

                Vector vFinal =
                        DbUtils.executeQuery("select TYPE, NAME, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_MODEL_FOLDER_ID='" + temp + "'", true);
                if (((Integer) vFinal.elementAt(2)).intValue() == requestorId)
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                else if (((Integer) vFinal.elementAt(2)).intValue() == 1)
                    filePath.insert(0, "Server: ");
                else {
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                    if (((String) vFinal.elementAt(0)).equalsIgnoreCase(DbConstants.USER_TYPE))
                        filePath.insert(0, "Users" + slash);
                    else
                        filePath.insert(0, "Groups" + slash);
                }
            }
            return filePath.toString();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static String getPathForPlayspace(String playspaceId, int requestorId) throws XmlRpcException {
        try {
            String privateOrPublic = null;
            StringBuffer filePath = new StringBuffer();
            if (playspaceId == null) {
                System.out.println("Invalid modelId passed to DeployFilesDbFunctions.getPathForModel");
                return null;
            }
            String query = "Select FOLDER_ID, NAME from PLAYSPACES where ID = '" + playspaceId + "'";
            Vector v = DbUtils.executeQuery(query, true);
            //System.out.println(v);
            if (v.isEmpty()) return "";
            filePath.insert(0, slash + ((String) v.elementAt(1)));
            if (((Integer) v.elementAt(0)).intValue() != DbConstants.NULL) {
                int temp = ((Integer) v.elementAt(0)).intValue();
                Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from PLAYSPACE_FOLDERS where ID = '" + ((Integer) v.elementAt(0)).intValue() + "'", true);
                if (((String) v2.elementAt(0)).endsWith("_public")) {
                    filePath.insert(0, "Public");
                    privateOrPublic = "PUBLIC";
                } else if (((String) v2.elementAt(0)).endsWith("_private")) {
                    filePath.insert(0, "Private");
                    privateOrPublic = "PRIVATE";
                } else
                    filePath.insert(0, slash + ((String) v2.elementAt(0)));
                if (((Integer) v2.elementAt(1)).intValue() != DbConstants.NULL) {
                    temp = ((Integer) v2.elementAt(1)).intValue();
                    Vector v3 = DbUtils.executeQuery("select NAME, PARENT_ID from PLAYSPACE_FOLDERS where ID = '" + ((Integer) v2.elementAt(1)).intValue() + "'", true);
                    int newTemp = 0;
                    while (((Integer) v3.elementAt(1)).intValue() != DbConstants.NULL) {
                        filePath.insert(0, slash + ((String) v3.elementAt(0)));
                        newTemp = ((Integer) v3.elementAt(1)).intValue();
                        if (newTemp != DbConstants.NULL) {
                            temp = newTemp;
                        }
                        v3 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" + newTemp + "'", true);
                    }
                    if (((String) v3.elementAt(0)).endsWith("_public")) {
                        filePath.insert(0, "Public");
                        privateOrPublic = "PUBLIC";
                    }
                    if (((String) v3.elementAt(0)).endsWith("_private")) {
                        filePath.insert(0, "Private");
                        privateOrPublic = "PRIVATE";
                    }
                }
                Vector vFinal =
                        DbUtils.executeQuery("select TYPE, NAME, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_PLAYSPACE_FOLDER_ID='" + temp + "'", true);
                if (((Integer) vFinal.elementAt(2)).intValue() == requestorId)
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                else if (((Integer) vFinal.elementAt(2)).intValue() == 1)
                    filePath.insert(0, "Server: ");
                else {
                    filePath.insert(0, ((String) vFinal.elementAt(1)) + ": ");
                    if (((String) vFinal.elementAt(0)).equalsIgnoreCase(DbConstants.USER_TYPE))
                        filePath.insert(0, "Users" + slash);
                    else
                        filePath.insert(0, "Groups" + slash);
                }
                //System.out.println(filePath.toString());
            }
            return filePath.toString();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector getModelVersionData(String modelId) throws XmlRpcException {
        Vector v = new Vector();
        v.addElement(getModelVersionInfo(modelId));
        v.addElement(getInterfacesVersionInfo(modelId));
        return v;
    }

    public static Vector getProjectVersionData(String projectId) throws XmlRpcException {
        //todo...
        Vector v = new Vector();
        v.addElement(getProjectVersionInfo(projectId));
        v.addElement(getInterfacesVersionInfo(projectId));
        v.addElement(getProjectIModelVersionInfo(projectId));
        return v;
    }

    public static Vector getAnalysisToolVersionData(String analysisToolId) throws XmlRpcException
    {
        Vector v = new Vector();
        v.addElement(getAnalysisToolVersionInfo(analysisToolId));
        v.addElement(getInterfacesVersionInfo(analysisToolId));
        return v;
    }

    /**
     *
     * @param modelInfo vector of (folderId, description, xmlDefinition, permissions)
     * @param fileEventId
     * @return model version information
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector deployNewModel(Vector modelInfo, int fileEventId) throws XmlRpcException {
        if (modelInfo.size() != 4 || !HDL.validateParameterTypes(modelInfo, new Class[]{HDL.PARAM_INT, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deploy model information (int folderId, String description, String xmlDefinition, Vector permissions");
        int folderId = HDL.getInt(modelInfo.get(0));
        String description = (String) modelInfo.get(1);
        String xmlDefinition = (String) modelInfo.get(2);
        Vector permissions = (Vector) modelInfo.get(3);
        String modelId = insertModelInfo(folderId, description, xmlDefinition, fileEventId);
        if (!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(modelId, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES, permissions);
        return getModelVersionInfo(modelId);
    }

    public static Vector deployNewAnalysisTool(Vector toolInfo, int fileEventId) throws XmlRpcException
    {
        if(toolInfo.size() != 4 || !HDL.validateParameterTypes(toolInfo, new Class[]{HDL.PARAM_INT, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deploy tool information (int folderId, String description, String xmlDefinition, Vector permissions");
        int folderId = HDL.getInt(toolInfo.get(0));
        String description = (String) toolInfo.get(1);
        String xmlDefinition = (String) toolInfo.get(2);
        Vector permissions = (Vector) toolInfo.get(3);
        String toolId = insertToolInfo(folderId, description, xmlDefinition, fileEventId);
        if(!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(toolId, PermissionUtils.ANALYSIS_TOOL_EDIT_PRIVILEGES,  permissions);
        return getAnalysisToolVersionInfo(toolId);
    }

    public static void insertAnalysisToolProjectId(String analysisToolId, String projectId) throws XmlRpcException
    {
        try
        {
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertAnalysisToolProjectId);
            stmt.setString(1, projectId);
            stmt.setString(2, analysisToolId);
            if(DbUtils.executeUpdate(stmt) != 1)
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
                        "error inserting project id " + projectId + " into analysis tool " + analysisToolId);
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }

    /**
     *
     * @param projectInfo vector of (folderId, description, xmlDefinition, permissions)
     * @param fileEventId
     * @return project version information
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector deployNewProject(Vector projectInfo, int fileEventId) throws XmlRpcException {
        if (projectInfo.size() != 5 || !HDL.validateParameterTypes(projectInfo, new Class[]{HDL.PARAM_INT, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deploy project information (int folderId, String description, String xmlDefinition, Vector editPermissions, Vector contentVisibilityPermissions");
        int folderId = HDL.getInt(projectInfo.get(0));
        String description = (String) projectInfo.get(1);
        String xmlDefinition = (String) projectInfo.get(2);
        Vector editPermissions = (Vector) projectInfo.get(3);
        Vector contentVisibilityPermissions = (Vector) projectInfo.get(4);
        String projectId = insertProjectInfo(folderId, description, xmlDefinition, fileEventId);
        if (!editPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(projectId, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES, editPermissions);
        if (!contentVisibilityPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(projectId, PermissionUtils.PROJECT_VISIBILITY_PRIVILEGES, contentVisibilityPermissions);
        return getProjectVersionInfo(projectId);
    }

    /**
     *
     * @param iModelInfo vector of (projectId, description, xmlDefinition, permissions)
     * @param fileEventId
     * @return iModel version information
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector deployNewIModel(Vector iModelInfo, int fileEventId) throws XmlRpcException {
        if (iModelInfo.size() != 4 || !HDL.validateParameterTypes(iModelInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deploy integration model information (String projectId, String description, String xmlDefinition, Vector permissions");
        String projectId = (String) iModelInfo.get(0);
        String description = (String) iModelInfo.get(1);
        String xmlDefinition = (String) iModelInfo.get(2);
        Vector permissions = (Vector) iModelInfo.get(3);
        String iModelId = insertIModelInfo(projectId, description, xmlDefinition, fileEventId);
        if (!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(iModelId, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES, permissions);
        return getIModelVersionInfo(iModelId);
    }

    /**
     * Method to deploy models on the server.
     * @param folderId valid folderId (should be checked before calling this function)
     * @param description optional user-provided description for the model
     * @param xmlDefinition the xml for the model
     * @return generated unique model id
     * @throws org.apache.xmlrpc.XmlRpcException
     * todo: authenticate user has permission to deploy in this folder (function call)
     * todo: authenticate folder is valid (done before getting here)
     */
    public static String insertModelInfo(int folderId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            if (description == null) // enforce non-null description
                description = "";
            else
                description = description.trim();
            String modelId = UUIDGenerator.create(); // create unique id for model
            DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition); // parse model file for info
            PreparedStatement check = DbUtils.getPreparedStatement(checkIfModelExists);
            check.setString(1, data.getName());
            check.setInt(2, folderId);
            Vector v = DbUtils.executeQuery(check, true);
            if (!v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot deploy model: " + data.getName() + ".\nModel already exists in the specified folder.");

            // insert model metadata into database
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertModelInfoQuery);
            stmt.setString(1, modelId);
            stmt.setString(2, data.getModelType());
            stmt.setString(3, data.getName());
            stmt.setString(4, description);
            stmt.setInt(5, folderId);
            DbUtils.executeInsert(stmt, false);
            // insert model version info into database
            insertModelVersion(modelId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
            return modelId;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * Method to deploy analysis tool on the server.
     * @param folderId valid folderId (should be checked before calling this function)
     * @param description optional user-provided description for the tool
     * @param xmlDefinition the xml for the tool
     * @return generated unique tool id
     * @throws org.apache.xmlrpc.XmlRpcException
     * todo: authenticate user has permission to deploy in this folder (function call)
     * todo: authenticate folder is valid (done before getting here)
     */
    public static String insertToolInfo(int folderId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException
    {
        try
        {
//TODO:
//TODO:     get the project id in the future
//TODO:
            if (description == null) // enforce non-null description
                description = "";
            else
                description = description.trim();
            String toolId = UUIDGenerator.create(); // create unique id for model
            DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition); // parse model file for info
            PreparedStatement check = DbUtils.getPreparedStatement(checkIfAnalysisToolExists);
            check.setString(1, data.getName());
            check.setInt(2, folderId);
            Vector v = DbUtils.executeQuery(check, true);
            if (!v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot deploy analysis tool: " + data.getName() + ".\nAnalysis tool already exists in the specified folder.");

            // insert model metadata into database
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertAnalysisToolInfoQuery);
            stmt.setString(1, toolId);
            stmt.setString(2, data.getModelType());
            stmt.setString(3, data.getName());
            stmt.setString(4, description);
            stmt.setString(5, ""); // project id will be assigned later in the deploy
            stmt.setInt(6, folderId);
            DbUtils.executeInsert(stmt, false);
            // insert model version info into database
            insertAnalysisToolVersion(toolId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
            return toolId;
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * Method to deploy projects on the server.
     * @param folderId valid folderId (should be checked before calling this function)
     * @param description optional user-provided description for the project
     * @param xmlDefinition the xml for the project
     * @return generated unique project id
     * @throws org.apache.xmlrpc.XmlRpcException
     * todo: authenticate user has permission to deploy in this folder (function call)
     * todo: authenticate folder is valid (done before getting here)
     */
    public static String insertProjectInfo(int folderId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {

            if (description == null) // enforce non-null description
                description = "";
            else
                description = description.trim();
            String projectId = UUIDGenerator.create(); // create unique id for project
            DomeXmlData data = new DomeXmlData(DomeXmlData.PROJECT, xmlDefinition); // parse project file for info

            /**
             * folderId is -1 for projects that are embedded
             * in analysis tools.  Project inside an analysis tool
             * does not have a location on the server.
             */

            if (folderId != -1)
            {
                PreparedStatement check = DbUtils.getPreparedStatement(checkIfProjectExists);
                check.setString(1, data.getName());
                check.setInt(2, folderId);
                Vector v = DbUtils.executeQuery(check, true);

                if (!v.isEmpty())
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot deploy project: " + data.getName() + ".\nProject already exists in the specified folder.");
            }

            // insert project metadata into database
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertProjectInfoQuery);
            stmt.setString(1, projectId);
            stmt.setString(2, data.getName());
            stmt.setString(3, description);
            stmt.setInt(4, folderId);
            DbUtils.executeInsert(stmt, false);
            // insert project version info into database
            insertProjectVersion(projectId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
            return projectId;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * Method to deploy integration models on the server.
     * @param projectId valid projectId (should be checked before calling this function)
     * @param description optional user-provided description for the integration model
     * @param xmlDefinition the xml for the integration model
     * @return generated unique integration model id
     * @throws org.apache.xmlrpc.XmlRpcException
     * todo: authenticate user has permission to deploy in this folder (function call)
     * todo: authenticate folder is valid (done before getting here)
     */
    public static String insertIModelInfo(String projectId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            if (description == null) // enforce non-null description
                description = "";
            else
                description = description.trim();
            String iModelId = UUIDGenerator.create(); // create unique id for integration model
            DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition); // parse integration model file for info
            PreparedStatement check = DbUtils.getPreparedStatement(checkIfIModelExists);
            check.setString(1, data.getName());
            check.setString(2, projectId);
            Vector v = DbUtils.executeQuery(check, true);
            if (!v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot deploy iModel: " + data.getName() + ".\nIModel already exists in the specified project.");

            // insert iModel metadata into database
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertIModelInfoQuery);
            stmt.setString(1, iModelId);
            stmt.setString(2, data.getName());
            stmt.setString(3, description);
            stmt.setString(4, projectId);
            DbUtils.executeInsert(stmt, false);
            // insert iModel version info into database
            insertIModelVersion(iModelId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
            return iModelId;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static int insertModelVersion(String modelId, String xmlDefinition, int fileEventId) throws XmlRpcException {
        DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition);
        return insertModelVersion(modelId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
    }

    /**
     * Inserts information about a new version of a model. Also associates the model with its latest version.
     * @param modelId
     * @param buildId
     * @param buildVersion
     * @param xmlDefinition
     * @param fileEventId
     * @return returns new version of model
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static int insertModelVersion(String modelId, String buildId, String buildVersion, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            int newVersion = getMostRecentModelVersion(modelId) + 1;
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertModelVersionInfoQuery);
            stmt.setString(1, modelId);
            stmt.setInt(2, newVersion);
            stmt.setString(3, buildId);
            stmt.setString(4, buildVersion);
            stmt.setString(5, xmlDefinition);
            stmt.setInt(6, fileEventId);
            int versionId = DbUtils.executeInsert(stmt, true);
            updateModelVersionId(modelId, versionId);
            return newVersion;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static int insertAnalysisToolVersion(String analysisToolId, String xmlDefinition, int fileEventId) throws XmlRpcException
    {
        DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition);
        return insertAnalysisToolVersion(analysisToolId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
    }

    /**
     * Inserts information about a new version of an analysis tool. Also associates the model with its latest version.
     * @param analysisToolId
     * @param buildId
     * @param buildVersion
     * @param xmlDefinition
     * @param fileEventId
     * @return returns new version of model
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static int insertAnalysisToolVersion(String analysisToolId, String buildId, String buildVersion, String xmlDefinition, int fileEventId) throws XmlRpcException
    {
        try
        {
            int newVersion = getMostRecentAnalysisToolVersion(analysisToolId) + 1;
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertAnalysisToolVersionInfoQuery);
            stmt.setString(1, analysisToolId);
            stmt.setInt(2, newVersion);
            stmt.setString(3, buildId);
            stmt.setString(4, buildVersion);
            stmt.setString(5, xmlDefinition);
            stmt.setInt(6, fileEventId);
            int versionId = DbUtils.executeInsert(stmt, true);
            updateAnalysisToolVersionId(analysisToolId, versionId);
            return newVersion;
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static int insertProjectVersion(String projectId, String xmlDefinition, int fileEventId) throws XmlRpcException {
        DomeXmlData data = new DomeXmlData(DomeXmlData.PROJECT, xmlDefinition);
        return insertProjectVersion(projectId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
    }

    /**
     * Inserts information about a new version of a project. Also associates the project with its latest version.
     * @param projectId
     * @param buildId
     * @param buildVersion
     * @param xmlDefinition
     * @param fileEventId
     * @return returns new version of project
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static int insertProjectVersion(String projectId, String buildId, String buildVersion, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            int newVersion = getMostRecentProjectVersion(projectId) + 1;
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertProjectVersionInfoQuery);
            stmt.setString(1, projectId);
            stmt.setInt(2, newVersion);
            stmt.setString(3, buildId);
            stmt.setString(4, buildVersion);
            stmt.setString(5, xmlDefinition);
            stmt.setInt(6, fileEventId);
            int versionId = DbUtils.executeInsert(stmt, true);
            updateProjectVersionId(projectId, versionId);
            return newVersion;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static int insertIModelVersion(String iModelId, String xmlDefinition, int fileEventId) throws XmlRpcException {
        DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition);
        return insertIModelVersion(iModelId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
    }

    /**
     * Inserts information about a new version of a iModel. Also associates the iModel with its latest version.
     * @param iModelId
     * @param buildId
     * @param buildVersion
     * @param xmlDefinition
     * @param fileEventId
     * @return returns new version of iModel
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static int insertIModelVersion(String iModelId, String buildId, String buildVersion, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            int newVersion = getMostRecentIModelVersion(iModelId) + 1;
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertIModelVersionInfoQuery);
            stmt.setString(1, iModelId);
            stmt.setInt(2, newVersion);
            stmt.setString(3, buildId);
            stmt.setString(4, buildVersion);
            stmt.setString(5, xmlDefinition);
            stmt.setInt(6, fileEventId);
            int versionId = DbUtils.executeInsert(stmt, true);
            updateIModelVersionId(iModelId, versionId);
            return newVersion;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * Returns the most recent version for model with specified model id.
     * @param modelId
     * @return most recent version for model; if model versions can not be found, return 0
     */
    public static int getMostRecentModelVersion(String modelId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentModelVersionQuery);
            stmt.setString(1, modelId);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    /**
     * Returns the most recent version for model with specified model id.
     * @param analysisToolId
     * @return most recent version for model; if model versions can not be found, return 0
     */
    public static int getMostRecentAnalysisToolVersion(String analysisToolId)
    {
        try
        {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentAnalysisToolVersionQuery);
            stmt.setString(1, analysisToolId);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    /**
     * Returns the most recent version for project with specified project id.
     * @param projectId
     * @return most recent version for project; if project versions can not be found, return 0
     */
    public static int getMostRecentProjectVersion(String projectId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentProjectVersionQuery);
            stmt.setString(1, projectId);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    /**
     * Returns the most recent version for iModel with specified iModel id.
     * @param iModelId
     * @return most recent version for iModel; if iModel versions can not be found, return 0
     */
    public static int getMostRecentIModelVersion(String iModelId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentIModelVersionQuery);
            stmt.setString(1, iModelId);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    public static boolean updateModelVersionId(String modelId, int versionId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateModelVersionIdQuery);
            stmt.setInt(1, versionId);
            stmt.setString(2, modelId);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }
    }

    public static boolean updateAnalysisToolVersionId(String analysisToolId, int versionId)
    {
        try
        {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateAnalysisToolVersionIdQuery);
            stmt.setInt(1, versionId);
            stmt.setString(2, analysisToolId);
            return (DbUtils.executeUpdate(stmt) == 1);
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }
    }

    public static boolean updateProjectVersionId(String projectId, int versionId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateProjectVersionIdQuery);
            stmt.setInt(1, versionId);
            stmt.setString(2, projectId);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }
    }

    public static boolean updateIModelVersionId(String iModelId, int versionId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateIModelVersionIdQuery);
            stmt.setInt(1, versionId);
            stmt.setString(2, iModelId);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }
    }

    public static Vector deployNewInterfaces(String parentId, String parentType, Vector interfaceInfoVector, int fileEventId) throws XmlRpcException {
        Iterator it = interfaceInfoVector.iterator();
        while (it.hasNext()) {
            try {
                deployNewInterface(parentId, parentType, (Vector) it.next(), fileEventId);
            } catch (ClassCastException ex) {
                throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "interface information vector must contain vectors of interface information");
            }
        }
        return getInterfacesVersionInfo(parentId);
    }

    public static String deployNewInterface(String parentId, String parentType, Vector interfaceInfo, int fileEventId) throws XmlRpcException {
        if (interfaceInfo.size() != 5 || !HDL.validateParameterTypes(interfaceInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deploy interface information (String status, String description, String xmlDefinition, String xmlMappings, Vector permissions");
        String status = (String) interfaceInfo.get(0);
        String description = (String) interfaceInfo.get(1);
        String xmlDefinition = (String) interfaceInfo.get(2);
        String xmlMappings = (String) interfaceInfo.get(3);
        Vector permissions = (Vector) interfaceInfo.get(4);
        String interfaceId = insertInterfaceInfo(parentId, parentType, status, description, xmlDefinition, xmlMappings, fileEventId);
        if (!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(interfaceId, PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES, permissions);
        return interfaceId;
    }

    public static String insertInterfaceInfo(String parentId, String parentType, String status, String description, String xmlDefinition, String xmlMappings, int fileEventId) throws XmlRpcException {
        try {
            if (description == null) // enforce non-null description
                description = "";
            else
                description = description.trim();
            String ifaceId = UUIDGenerator.create(); // create unique id for model
            DomeXmlData data = null;
            if (parentType.equals(DbConstants.IFACE_PARENT_TYPE_MODEL)
                    || parentType.equals(DbConstants.IFACE_PARENT_TYPE_IMODEL)
                            || parentType.equals(DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL)) {
                data = new DomeXmlData(DomeXmlData.INTERFACE, xmlDefinition); // parse interface file for info
            } else if (parentType.equals(DbConstants.IFACE_PARENT_TYPE_PROJECT)) { //
                data = new DomeXmlData(DomeXmlData.PROJECTINTERFACE, xmlDefinition); // parse interface file for info
            }

            // insert interface metadata into database
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertInterfaceInfoQuery);
            stmt.setString(1, ifaceId);
            stmt.setString(2, status);
            stmt.setString(3, data.getName());
            stmt.setString(4, description);
            stmt.setString(5, parentId);
            stmt.setString(6, parentType);
            DbUtils.executeInsert(stmt, false);

            // insert interface version info into database
            insertInterfaceVersion(ifaceId, data.getId(), data.getVersion(), xmlDefinition, xmlMappings, fileEventId);
            return ifaceId;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param interfaceId
     * @param buildId
     * @param buildVersion
     * @param xmlDefinition
     * @param xmlMappings
     * @param fileEventId
     * @return new version number of interface
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static int insertInterfaceVersion(String interfaceId, String buildId, String buildVersion, String xmlDefinition, String xmlMappings, int fileEventId) throws XmlRpcException {
        try {
            int newVersion = getMostRecentInterfaceVersion(interfaceId) + 1;
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertInterfaceVersionInfoQuery);
            stmt.setString(1, interfaceId);
            stmt.setInt(2, newVersion);
            stmt.setString(3, buildId);
            stmt.setString(4, buildVersion);
            stmt.setString(5, xmlDefinition);
            stmt.setString(6, xmlMappings);
            stmt.setInt(7, fileEventId);
            int versionId = DbUtils.executeInsert(stmt, true);
            updateInterfaceVersionId(interfaceId, versionId);
            return newVersion;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * Returns the most recent version for interface with specified model id.
     * @param interfaceId
     * @return most recent version for interface; if interface versions can not be found, return 0
     */
    public static int getMostRecentInterfaceVersion(String interfaceId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentInterfaceVersionQuery);
            stmt.setString(1, interfaceId);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return -1;
        }
    }

    public static boolean updateInterfaceVersionId(String modelId, int versionId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateInterfaceVersionIdQuery);
            stmt.setInt(1, versionId);
            stmt.setString(2, modelId);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }

    }

    /**
     * @param modelId
     * @return vector of model information (modelId, version).
     */
    public static Vector getModelVersionInfo(String modelId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getModelVersionInfo");
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getModelVersionInfo);
            stmt.setString(1, modelId);
            return DbUtils.executeQuery(stmt, true);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param analysisToolId
     * @return vector of model information (modelId, version).
     */
    public static Vector getAnalysisToolVersionInfo(String analysisToolId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getAnalysisToolVersionInfo");
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolVersionInfo);
            stmt.setString(1, analysisToolId);
            return DbUtils.executeQuery(stmt, true);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param projectId
     * @return vector of project information (projectId, version).
     */
    public static Vector getProjectVersionInfo(String projectId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getProjectVersionInfo");
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getProjectVersionInfo);
            stmt.setString(1, projectId);
            return DbUtils.executeQuery(stmt, true);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param proejctId
     * @return vector of iModel information (iModelId, version).
     */
    public static Vector getProjectIModelVersionInfo(String proejctId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getIModelVersionInfo");
        Vector v = new Vector();

        Vector iModels = FileSystemDbFunctions.getIModelIdsFromProject(proejctId);
        Vector v1 = new Vector();
        for (int i = 0; i < iModels.size(); i++) {
            v1.add(getIModelVersionInfo((String) iModels.get(i)));
            v1.add(getInterfacesVersionInfo((String) iModels.get(i)));
            v.add(v1);
        }
        return v;
    }

    /**
     * @param iModelId
     * @return vector of iModel information (iModelId, version).
     */
    public static Vector getIModelVersionInfo(String iModelId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getIModelVersionInfo");
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getIModelVersionInfo);
            stmt.setString(1, iModelId);
            return DbUtils.executeQuery(stmt, true);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param parentId
     * @return vector of vectors of interface information (interfaceId, version, staticId).
     */
    public static Vector getInterfacesVersionInfo(String parentId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getInterfacesVersionInfo");
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getInterfacesVersionInfoQuery);
            stmt.setString(1, parentId);
            return DbUtils.executeQuery(stmt, false);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param modelInfo
     * @param fileEventId
     * @return returns model version info
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector updateModel(Vector modelInfo, int fileEventId) throws XmlRpcException {
        if (modelInfo.size() != 4 || !HDL.validateParameterTypes(modelInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for update model information (String modelId, String description, String xmlDefinition, Vector permissions");
        String modelId = (String) modelInfo.get(0);
        String description = (String) modelInfo.get(1);
        String xmlDefinition = (String) modelInfo.get(2);
        Vector permissions = (Vector) modelInfo.get(3);
        updateModelInfo(modelId, description, xmlDefinition, fileEventId);
        if (!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(modelId, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES, permissions);
        return getModelVersionInfo(modelId);
    }

    public static Vector updateAnalysisTool(Vector analysisToolInfo, int fileEventId) throws XmlRpcException
    {
        if (analysisToolInfo.size() != 4 || !HDL.validateParameterTypes(analysisToolInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for update project information (String projectId, String description, String xmlDefinition, Vector editPermissions");

        String analysisToolId = (String) analysisToolInfo.get(0);
        String description = (String) analysisToolInfo.get(1);
        String xmlDefinition = (String) analysisToolInfo.get(2);
        Vector editPermissions = (Vector) analysisToolInfo.get(3);

        updateAnalysisToolInfo(analysisToolId, description, xmlDefinition, fileEventId);

        if (!editPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(analysisToolId, PermissionUtils.ANALYSIS_TOOL_EDIT_PRIVILEGES, editPermissions);
        return getAnalysisToolVersionInfo(analysisToolId);
    }

    /**
     * @param projectInfo
     * @param fileEventId
     * @return returns project version info
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector updateProject(Vector projectInfo, int fileEventId) throws XmlRpcException {
        if (projectInfo.size() != 5 || !HDL.validateParameterTypes(projectInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for update project information (String projectId, String description, String xmlDefinition, Vector editPermissions, Vector contentVisibilityPermissions");
        String projectId = (String) projectInfo.get(0);
        String description = (String) projectInfo.get(1);
        String xmlDefinition = (String) projectInfo.get(2);
        Vector editPermissions = (Vector) projectInfo.get(3);
        Vector contentVisibilityPermissions = (Vector) projectInfo.get(4);
        updateProjectInfo(projectId, description, xmlDefinition, fileEventId);
        if (!editPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(projectId, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES, editPermissions);
        if (!contentVisibilityPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(projectId, PermissionUtils.PROJECT_VISIBILITY_PRIVILEGES, contentVisibilityPermissions);
        return getProjectVersionInfo(projectId);
    }

    /**
     * @param iModelInfo
     * @param fileEventId
     * @return returns iModel version info
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector updateIModel(Vector iModelInfo, int fileEventId) throws XmlRpcException {
        if (iModelInfo.size() != 4 || !HDL.validateParameterTypes(iModelInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for update iModel information (String iModelId, String description, String xmlDefinition, Vector permissions");
        String iModelId = (String) iModelInfo.get(0);
        String description = (String) iModelInfo.get(1);
        String xmlDefinition = (String) iModelInfo.get(2);
        Vector permissions = (Vector) iModelInfo.get(3);
        updateIModelInfo(iModelId, description, xmlDefinition, fileEventId);
        if (!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(iModelId, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES, permissions);
        return getIModelVersionInfo(iModelId);
    }

    /**
     *
     * @param modelId
     * @param description
     * @param xmlDefinition
     * @param fileEventId
     * @throws org.apache.xmlrpc.XmlRpcException if modelId not found
     */
    public static void updateModelInfo(String modelId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        // validate nothing is null going into database
        if (description == null) // enforce non-null description
            description = "";
        else
            description = description.trim();

        DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition); // parse model file for info

        if (modelId == null) // enforce non-null description
            throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "modelID can't be null");

        try {
            PreparedStatement check = DbUtils.getPreparedStatement(getRedeployModelInfo);
            check.setString(1, modelId);
            Vector v = DbUtils.executeQuery(check, true);
            if (v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_MODEL, DbErrors.XMLRPC_NO_SUCH_MODEL_MSG);

            if (!(v.get(0)).equals(data.getName())) {
                int folderId = ((Integer) v.get(1)).intValue();
                PreparedStatement nameConflicts = DbUtils.getPreparedStatement(getModelNameConflicts);
                nameConflicts.setInt(1, folderId);
                nameConflicts.setString(2, data.getName());
                Vector result = DbUtils.executeQueryColumnToVector(nameConflicts);
                if (!result.isEmpty())
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot redeploy model: " + data.getName() + ".\nAnother model with this name already exists in the current folder.");
            }

            String query = "update MODELS set NAME=?,DESCRIPTION=? where ID=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, data.getName());
            stmt.setString(2, description);
            stmt.setString(3, modelId);
            int rowAffected = DbUtils.executeUpdate(stmt);
            // check one record was update (if model id not found, zero records updated, throw "model not found" exception)
            if (rowAffected == 0)
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_MODEL, DbErrors.XMLRPC_NO_SUCH_MODEL_MSG);
            // check if xmlDefinition given is different from most current one on server (string comparison)
            // if so, insert new version for model; do not insert new version if content is the same
            String latestDefinition = getMostRecentModelXmlDefinition(modelId);
//			System.out.println("Can you see this too?");
            if (xmlDefinition.equals(latestDefinition)) return;
            insertModelVersion(modelId, xmlDefinition, fileEventId);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static void updateAnalysisToolInfo(String analysisToolId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException
    {
        if (description == null)
            description = "";
        else
            description = description.trim();

        DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition);

        if (analysisToolId == null)
            throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "analysisToolId can't be null");

        try
        {
            PreparedStatement check = DbUtils.getPreparedStatement(getRedeployAnalysisToolInfo);
            check.setString(1, analysisToolId);
            Vector v = DbUtils.executeQuery(check, true);
            if (v.isEmpty())
                throw new XmlRpcException (DbErrors.XMLRPC_NO_SUCH_ANALYSIS_TOOL, DbErrors.XMLRPC_NO_SUCH_ANALYSIS_TOOL_MSG);
            if (!(v.get(0)).equals(data.getName()))
            {
                int folderId = ((Integer) v.get(1)).intValue();
                PreparedStatement nameConflicts = DbUtils.getPreparedStatement(getAnalysisToolNameConflicts);
                nameConflicts.setInt(1, folderId);
                nameConflicts.setString(2, data.getName());
                Vector result = DbUtils.executeQueryColumnToVector(nameConflicts);
                if (!result.isEmpty())
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot redeploy analysis tool: " +
                            data.getName() + ".\nAnother analysis tool with this name already exists in the current folder.");
            }

            String query = "update ANALYSIS_TOOLS set NAME=?,DESCRIPTION=? where ID=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, data.getName());
            stmt.setString(2, description);
            stmt.setString(3, analysisToolId);
            int rowAffected = DbUtils.executeUpdate(stmt);
            // check one record was update (if analysis tool id not found, zero records updated, throw "analysis tool not found" exception)
            if (rowAffected == 0)
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_ANALYSIS_TOOL, DbErrors.XMLRPC_NO_SUCH_ANALYSIS_TOOL_MSG);
            // check if xmlDefinition given is different from most current one on server (string comparison)
            // if so, insert new version for project; do not insert new version if content is the same
            String latestDefinition = getMostRecentAnalysisToolXmlDefinition(analysisToolId);
            if (xmlDefinition.equals(latestDefinition)) return;
            insertAnalysisToolVersion(analysisToolId, xmlDefinition, fileEventId);
        }
        catch (SQLException e)
        {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }
    /**
     *
     * @param projectId
     * @param description
     * @param xmlDefinition
     * @param fileEventId
     * @throws org.apache.xmlrpc.XmlRpcException if projectId not found
     */
    public static void updateProjectInfo(String projectId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        // validate nothing is null going into database
        if (description == null) // enforce non-null description
            description = "";
        else
            description = description.trim();

        DomeXmlData data = new DomeXmlData(DomeXmlData.PROJECT, xmlDefinition); // parse project file for info

        if (projectId == null) // enforce non-null project Id
            throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "projectId can't be null");

        try {
            PreparedStatement check = DbUtils.getPreparedStatement(getRedeployProjectInfo);
            check.setString(1, projectId);
            Vector v = DbUtils.executeQuery(check, true);
            if (v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PROJECT, DbErrors.XMLRPC_NO_SUCH_PROJECT_MSG);

            if (!(v.get(0)).equals(data.getName())) {
                int folderId = ((Integer) v.get(1)).intValue();
                PreparedStatement nameConflicts = DbUtils.getPreparedStatement(getProjectNameConflicts);
                nameConflicts.setInt(1, folderId);
                nameConflicts.setString(2, data.getName());
                Vector result = DbUtils.executeQueryColumnToVector(nameConflicts);
                if (!result.isEmpty())
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot redeploy project: " + data.getName() + ".\nAnother project with this name already exists in the current folder.");
            }

            String query = "update PROJECTS set NAME=?,DESCRIPTION=? where ID=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, data.getName());
            stmt.setString(2, description);
            stmt.setString(3, projectId);
            int rowAffected = DbUtils.executeUpdate(stmt);
            // check one record was update (if project id not found, zero records updated, throw "project not found" exception)
            if (rowAffected == 0)
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PROJECT, DbErrors.XMLRPC_NO_SUCH_PROJECT_MSG);
            // check if xmlDefinition given is different from most current one on server (string comparison)
            // if so, insert new version for project; do not insert new version if content is the same
            String latestDefinition = getMostRecentProjectXmlDefinition(projectId);
            if (xmlDefinition.equals(latestDefinition)) return;
            insertProjectVersion(projectId, xmlDefinition, fileEventId);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param iModelId
     * @param description
     * @param xmlDefinition
     * @param fileEventId
     * @throws org.apache.xmlrpc.XmlRpcException if iModelId not found
     */
    public static void updateIModelInfo(String iModelId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        // validate nothing is null going into database
        if (description == null) // enforce non-null description
            description = "";
        else
            description = description.trim();

        DomeXmlData data = new DomeXmlData(DomeXmlData.MODEL, xmlDefinition); // parse iModel file for info

        if (iModelId == null) // enforce non-null modelId
            throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "iModelID can't be null");

        try {
            PreparedStatement check = DbUtils.getPreparedStatement(getRedeployIModelInfo);
            check.setString(1, iModelId);
            Vector v = DbUtils.executeQuery(check, true);
            if (v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL, DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL_MSG);

            if (!(v.get(0)).equals(data.getName())) {
                String projectId = (String) v.get(1);
                PreparedStatement nameConflicts = DbUtils.getPreparedStatement(getIModelNameConflicts);
                nameConflicts.setString(1, projectId);
                nameConflicts.setString(2, data.getName());
                Vector result = DbUtils.executeQueryColumnToVector(nameConflicts);
                if (!result.isEmpty())
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot redeploy iModel: " + data.getName() + ".\nAnother iModel with this name already exists in the current project.");
            }

            String query = "update INTEGRATION_MODELS set NAME=?,DESCRIPTION=? where ID=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, data.getName());
            stmt.setString(2, description);
            stmt.setString(3, iModelId);
            int rowAffected = DbUtils.executeUpdate(stmt);
            // check one record was update (if iModel id not found, zero records updated, throw "iModel not found" exception)
            if (rowAffected == 0)
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL, DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL_MSG);
            // check if xmlDefinition given is different from most current one on server (string comparison)
            // if so, insert new version for iModel; do not insert new version if content is the same
            String latestDefinition = getMostRecentIntegrationModelXmlDefinition(iModelId);
            if (xmlDefinition.equals(latestDefinition)) return;
            insertIModelVersion(iModelId, xmlDefinition, fileEventId);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param modelId Model id
     * @return xmlDefinition for most recent model version
     */
    public static String getMostRecentModelXmlDefinition(String modelId) throws XmlRpcException {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getModelVersionContentQuery);
            stmt.setString(1, modelId);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null || result.size() == 0) {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in getting recent model definition!");
            }
            String content = (String) result.get(0);
            return content;
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param projectId Project id
     * @return xmlDefinition for most recent model version
     */
    public static String getMostRecentProjectXmlDefinition(String projectId) throws XmlRpcException {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getProjectVersionContentQuery);
            stmt.setString(1, projectId);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null || result.size() == 0) {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
                        "Error in getting most recent project definition!");
            }
            String content = (String) result.get(0);
            return content;
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static String getMostRecentAnalysisToolXmlDefinition(String analysisToolId) throws XmlRpcException
    {
        try
        {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolVersionContentQuery);
            stmt.setString(1, analysisToolId);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null || result.size() == 0)
            {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
                        "Error in getting most recent analysis tool definition!");
            }
            String content = (String) result.get(0);
            return content;
        }
        catch (SQLException e)
        {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param iModelId Integration model id
     * @return xmlDefinition for most recent model version
     */
    public static String getMostRecentIntegrationModelXmlDefinition(String iModelId) throws XmlRpcException {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getIModelVersionContentQuery);
            stmt.setString(1, iModelId);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null || result.size() == 0) {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
                        "Error in getting most recent integration model definition!");
            }
            String content = (String) result.get(0);
            return content;
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector reDeployInterfaces(String parentId, String parentType, Vector interfaceInfoVector, int fileEventId) throws XmlRpcException {
        List currentInterfaceIds = new ArrayList();
        Iterator it = interfaceInfoVector.iterator();
        while (it.hasNext()) {
            try {
                currentInterfaceIds.add(updateInterface(parentId, parentType, (Vector) it.next(), fileEventId));
            } catch (ClassCastException ex) {
                throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "interface information vector must contain vectors of interface information");
            }
        }
        Vector allInterfaceIds = getAllInterfaceIds(parentId);
        Collection oldInterfaceIds = DSet.removeSet(allInterfaceIds, currentInterfaceIds);
        deleteInterfaces(oldInterfaceIds);
        return getInterfacesVersionInfo(parentId);
    }

    /**
     *
     * @param parentId
     * @param interfaceInfo vector (String status, String description, String xmlDefinition, String xmlMappings, Vector permissions)
     * @param fileEventId
     * @return interface id
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static String updateInterface(String parentId, String parentType, Vector interfaceInfo, int fileEventId) throws XmlRpcException {
        if (interfaceInfo.size() != 5 || !HDL.validateParameterTypes(interfaceInfo, new Class[]{HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_STR, HDL.PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for deploy interface information (String status, String description, String xmlDefinition, String xmlMappings, Vector permissions");
        String status = (String) interfaceInfo.get(0);
        String description = (String) interfaceInfo.get(1);
        String xmlDefinition = (String) interfaceInfo.get(2);
        String xmlMappings = (String) interfaceInfo.get(3);
        Vector permissions = (Vector) interfaceInfo.get(4);

        DomeXmlData data = null;
        if (parentType.equals(DbConstants.IFACE_PARENT_TYPE_MODEL) || parentType.equals(DbConstants.IFACE_PARENT_TYPE_IMODEL)
                || parentType.equals(DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL)) {
            data = new DomeXmlData(DomeXmlData.INTERFACE, xmlDefinition); // parse interface file for info
        } else if (parentType.equals(DbConstants.IFACE_PARENT_TYPE_PROJECT)) { //
            data = new DomeXmlData(DomeXmlData.PROJECTINTERFACE, xmlDefinition); // parse interface file for info
        }

        String interfaceId = getInterfaceId(parentId, data.getId());
        // if interface does not exist, call deployNewInterface and return interface id
        if (interfaceId.equals("")) {
            return deployNewInterface(parentId, parentType, interfaceInfo, fileEventId);
        }
        // update status, name, description in INTERFACES
        try {
            String query = "update INTERFACES set STATUS=?,NAME=?,DESCRIPTION=? where ID=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, data.getName());
            stmt.setString(3, description);
            stmt.setString(4, interfaceId);
            DbUtils.executeUpdate(stmt);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

        // if interface exists, see if it has changed from most recent version (compare both definition and mappings)
        String[] content_and_mapping = getMostRecentInterfaceXmlDefinitionAndMappings(interfaceId);
        if (content_and_mapping.length == 2) {
            String oldcontent = content_and_mapping[0];
            String oldmapping = content_and_mapping[1];
            // if definition or mappings have changed, update content
            if ((!xmlDefinition.equals(oldcontent)) || (!xmlMappings.equals(oldmapping)))
                insertInterfaceVersion(interfaceId, data.getId(), data.getVersion(), xmlDefinition, xmlMappings, fileEventId);
        }

        // reset permissions
        if (!permissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(interfaceId, PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES, permissions);

        // return interface id
        return interfaceId;
    }

    /**
     *
     * @param parentId
     * @param buildId
     * @return interface id for given parent id/build id (build id should be from most recent version of interface)
     * returns empty string if not found
     */
    public static String getInterfaceId(String parentId, String buildId) throws XmlRpcException {
        try {
            String query = "select INTERFACES.ID from  INTERFACES,INTERFACE_VERSIONS where INTERFACES.PARENT_ID=? AND BUILD_ID=? AND INTERFACE_VERSIONS.ID=LAST_VERSION_ID";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, parentId);
            stmt.setString(2, buildId);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null) {
                return "";
            }
            if (result.size() == 0) {
                return "";
            }
            String InterfaceId = (String) result.get(0);
            return InterfaceId;
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param interfaceId valid interface id (guaranteed by function that calls it)
     * @return String[] {xmlDefinition, xmlMappings} for most recent interface version
     */
    public static String[] getMostRecentInterfaceXmlDefinitionAndMappings(String interfaceId) throws XmlRpcException {
        try {
            int version = getMostRecentInterfaceVersion(interfaceId);
            String query = "select CONTENT,MAPPINGS from  INTERFACE_VERSIONS where INTERFACE_ID=? AND VERSION=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, interfaceId);
            stmt.setInt(2, version);
            Vector result = DbUtils.executeQuery(stmt, true);
            return (String[]) result.toArray(new String[]{});
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * @param parentId
     * @return vector of all interface ids for this parent
     */
    public static Vector getAllInterfaceIds(String parentId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getAllInterfaceIds");
        try {
            String query = "select ID from INTERFACES where PARENT_ID=? and (STATUS='";
            query += DbConstants.INTERFACE_STATUS_AVAILABLE + "' OR STATUS='" + DbConstants.INTERFACE_STATUS_UNAVAILABLE + "')";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, parentId);
            return DbUtils.executeQueryColumnToVector(stmt);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }

    /**
     * deletes interfaces -- set interface status to delete
     * @param interfaceIds
     */
    public static void deleteInterfaces(Collection interfaceIds) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "deleteInterfaces");

        PreparedStatement stmt = null;
        try {
            String query = "update INTERFACES set STATUS=? where ID=?";
            stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, DbConstants.INTERFACE_STATUS_DELETE);
            Iterator it = interfaceIds.iterator();
            while (it.hasNext()) {
                stmt.setString(2, (String) it.next());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param folderId
     * @param description
     * @param xmlDefinition
     * @param usePermissions
     * @param editPermissions
     * @param fileEventId
     * @return model version information
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector deployNewPlayspace(int folderId, String description, String xmlDefinition, Vector editPermissions,
                                            Vector usePermissions, int fileEventId) throws XmlRpcException {
        String playspaceId = insertPlayspaceInfo(folderId, description, xmlDefinition, fileEventId);
        if (!editPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(playspaceId, PermissionUtils.PLAYSPACE_EDIT_PRIVILEGES, editPermissions);
        if (!usePermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(playspaceId, PermissionUtils.PLAYSPACE_USE_PRIVILEGES, usePermissions);
        return getPlayspaceVersionInfo(playspaceId);
    }

    /**
     * Method to deploy playspaces on the server.
     * @param folderId valid folderId (should be checked before calling this function)
     * @param description optional user-provided description for the playspace
     * @param xmlDefinition the xml for the playspace
     * @return generated unique playspace id
     * @throws org.apache.xmlrpc.XmlRpcException
     * todo: authenticate user has permission to deploy in this folder (function call)
     * todo: authenticate folder is valid (done before getting here)
     */
    public static String insertPlayspaceInfo(int folderId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            if (description == null) // enforce non-null description
                description = "";
            else
                description = description.trim();
            String playspaceId = UUIDGenerator.create(); // create unique id for playspace
            DomeXmlData data = new DomeXmlData(DomeXmlData.PLAYSPACE, xmlDefinition); // parse playspace file for info
            PreparedStatement check = DbUtils.getPreparedStatement(checkIfPlayspaceExists);
            check.setString(1, data.getName());
            check.setInt(2, folderId);
            Vector v = DbUtils.executeQuery(check, true);
            if (!v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot deploy playspace: " + data.getName() + ".\nPlayspace already exists in the specified folder.");

            // insert playspace metadata into database
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertPlayspaceInfoQuery);
            stmt.setString(1, playspaceId);
            stmt.setString(2, data.getName());
            stmt.setString(3, description);
            stmt.setInt(4, folderId);
            DbUtils.executeInsert(stmt, false);

            // insert playspace version info database
            insertPlayspaceVersion(playspaceId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
            stmt.close();
            return playspaceId;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     * Inserts information about a new version of a playspace. Also associates the playspace with its latest version.
     * @param playspaceId
     * @param buildId
     * @param buildVersion
     * @param xmlDefinition
     * @param fileEventId
     * @return returns new version of playspace
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static int insertPlayspaceVersion(String playspaceId, String buildId, String buildVersion, String xmlDefinition, int fileEventId) throws XmlRpcException {
        try {
            int newVersion = getMostRecentPlayspaceVersion(playspaceId) + 1;
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertPlayspaceVersionInfoQuery);
            stmt.setString(1, playspaceId);
            stmt.setInt(2, newVersion);
            stmt.setString(3, buildId);
            stmt.setString(4, buildVersion);
            stmt.setString(5, xmlDefinition);
            stmt.setInt(6, fileEventId);
            int versionId = DbUtils.executeInsert(stmt, true);
            updatePlayspaceVersionId(playspaceId, versionId);
            return newVersion;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static int insertPlayspaceVersion(String playspaceId, String xmlDefinition, int fileEventId) throws XmlRpcException {
        DomeXmlData data = new DomeXmlData(DomeXmlData.PLAYSPACE, xmlDefinition);
        return insertPlayspaceVersion(playspaceId, data.getId(), data.getVersion(), xmlDefinition, fileEventId);
    }

    /**
     * Returns the most recent version for playspace with specified playspace id.
     * @param playspaceId
     * @return most recent version for playspace; if playspace versions can not be found, return 0
     */
    public static int getMostRecentPlayspaceVersion(String playspaceId) {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentPlayspaceVersionQuery);
            stmt.setString(1, playspaceId);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    public static boolean updatePlayspaceVersionId(String playspaceId, int versionId) {
        try {

            PreparedStatement stmt = DbUtils.getPreparedStatement(updatePlayspaceVersionIdQuery);
            stmt.setInt(1, versionId);
            stmt.setString(2, playspaceId);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }
    }

    /**
     * @param playspaceId
     * @return vector of playspace information (playspaceId, version).
     */
    public static Vector getPlayspaceVersionInfo(String playspaceId) throws XmlRpcException {
        //Debug.trace(Debug.ALL, "getPlayspaceVersionInfo");
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getPlayspaceVersionInfo);
            stmt.setString(1, playspaceId);
            return DbUtils.executeQuery(stmt, true);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param playspaceId
     * @param description
     * @param xmlDefinition
     * @param editPermissions
     * @param usePermissions
     * @param fileEventId
     * @return returns playspace version info
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public static Vector updatePlayspace(String playspaceId, String description, String xmlDefinition,
                                         Vector editPermissions, Vector usePermissions, int fileEventId) throws XmlRpcException {
        updatePlayspaceInfo(playspaceId, description, xmlDefinition, fileEventId);
        if (!editPermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(playspaceId, PermissionUtils.PLAYSPACE_EDIT_PRIVILEGES, editPermissions);
        if (!usePermissions.isEmpty())
            PermissionDbFunctions.setObjectPermissions(playspaceId, PermissionUtils.PLAYSPACE_USE_PRIVILEGES, usePermissions);
        return getPlayspaceVersionInfo(playspaceId);
    }

    /**
     *
     * @param playspaceId
     * @param description
     * @param xmlDefinition
     * @param fileEventId
     * @throws org.apache.xmlrpc.XmlRpcException if playspaceId not found
     */
    public static void updatePlayspaceInfo(String playspaceId, String description, String xmlDefinition, int fileEventId) throws XmlRpcException {
        // validate nothing is null going into database
        if (description == null) // enforce non-null description
            description = "";
        else
            description = description.trim();

        DomeXmlData data = new DomeXmlData(DomeXmlData.PLAYSPACE, xmlDefinition); // parse playspace file for info

        if (playspaceId == null) // enforce non-null playspace id
            throw new XmlRpcException(DbErrors.XMLRPC_DEPLOY_ERROR, "playspaceId can't be null");

        // update name, description in PLAYSPACES
        try {
            PreparedStatement check = DbUtils.getPreparedStatement(getRedeployPlayspaceInfo);
            check.setString(1, playspaceId);
            Vector v = DbUtils.executeQuery(check, true);
            if (v.isEmpty())
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PLAYSPACE, DbErrors.XMLRPC_NO_SUCH_PLAYSPACE_MSG);

            if (!(v.get(0)).equals(data.getName())) {
                int folderId = ((Integer) v.get(1)).intValue();
                PreparedStatement nameConflicts = DbUtils.getPreparedStatement(getPlayspaceNameConflicts);
                nameConflicts.setInt(1, folderId);
                nameConflicts.setString(2, data.getName());
                Vector result = DbUtils.executeQueryColumnToVector(nameConflicts);
                if (!result.isEmpty())
                    throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Cannot redeploy playspace: " + data.getName() + ".\nAnother playspace with this name already exists in the current folder.");
            }

            String query = "update PLAYSPACES set NAME=?,DESCRIPTION=? where ID=?";
            PreparedStatement stmt = DbUtils.getPreparedStatement(query);
            stmt.setString(1, data.getName());
            stmt.setString(2, description);
            stmt.setString(3, playspaceId);
            int rowAffected = DbUtils.executeUpdate(stmt);
            // check one record was update (if playspace id not found, zero records updated, throw "playspace not found" exception)
            if (rowAffected == 0)
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PLAYSPACE, DbErrors.XMLRPC_NO_SUCH_PLAYSPACE_MSG);
            // check if xmlDefinition given is different from most current one on server (string comparison)
            // if so, insert new version for playspace; do not insert new version if content is the same
            String latestDefinition = getMostRecentPlayspaceXmlDefinition(playspaceId);
            if (xmlDefinition.equals(latestDefinition)) return;
            insertPlayspaceVersion(playspaceId, xmlDefinition, fileEventId);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param playspaceId Playspace id
     * @return xmlDefinition for most recent playspace version
     */
    public static String getMostRecentPlayspaceXmlDefinition(String playspaceId) throws XmlRpcException {
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getPlayspaceVersionContentQuery);
            stmt.setString(1, playspaceId);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null || result.size() == 0) {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in getting recent playspace definition!");
            }
            String content = (String) result.get(0);
            return content;
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    // (ID int,AUX_FILE_ID varchar not null,MODEL_ID varchar not null, FILE_NAME varchar not null, LOCATION_ON_SERVER varchar default '',CONTENT varchar not null, VERSION int default '1' not null, FILE_EVENT_ID int not null)",

    //change : July 4th, don't insert content
    public static void insertAuxiliaryFileInfo(String aux_file_Id, String model_Id, String interface_id, String file_name, String location_server, int version, int file_event_Id) throws XmlRpcException {
        // AUX_FILES (ID int identity,AUX_FILE_ID varchar not null,MODEL_ID varchar not null, FILE_NAME varchar not null, LOCATION_ON_SERVER varchar default '',LOCATION_ORIGINAL varchar not null, VERSION int default '1' not null, FILE_EVENT_ID int not null)",
        String insertAuxFileInfoQuery = "insert into AUX_FILES (AUX_FILE_ID,MODEL_ID,INTERFACE_ID, FILE_NAME,LOCATION_ON_SERVER,VERSION,FILE_EVENT_ID) values (?,?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(insertAuxFileInfoQuery);
            stmt.setString(1, aux_file_Id);
            stmt.setString(2, model_Id);
            stmt.setString(3, interface_id);
            stmt.setString(4, file_name);
            stmt.setString(5, location_server);
            stmt.setInt(6, version);
            stmt.setInt(7, file_event_Id);
            DbUtils.executeInsert(stmt, false);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static void initially_insertAuxiliaryFileInfo(String aux_file_Id, String model_Id,String interface_id, String file_name, int file_event_Id) throws XmlRpcException {
        insertAuxiliaryFileInfo(aux_file_Id, model_Id, interface_id,file_name, "", 1, file_event_Id);
    }


    public static boolean update_location_AuxiliaryFileInfo(String aux_file_Id, String model_Id, String file_on_server_location, int version) {
        //first locate that record by  aux_file_Id  and model_Id and Max(version)
        String updateAuxFileInfoQuery = "update AUX_FILES set LOCATION_ON_SERVER=? where AUX_FILE_ID=? and MODEL_ID=? and VERSION=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateAuxFileInfoQuery);
            stmt.setString(1, file_on_server_location);
            stmt.setString(2, aux_file_Id);
            stmt.setString(3, model_Id);
            stmt.setInt(4, version);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }

    }

    //always deal with most recent version
    public static void update_location_AuxiliaryFileInfo(String aux_file_Id, String model_Id, String file_on_server_location) throws XmlRpcException {
        update_location_AuxiliaryFileInfo(aux_file_Id, model_Id, file_on_server_location, getMostRecentAuxFileVersion(aux_file_Id, model_Id));
    }

    /**
     * for customGui update
     * always deal with most recent version
     */
    public static boolean update_location_customGuiFileInfo(String interface_Id, String filename,String file_on_server_location) throws XmlRpcException {
        int maxVersion= getMostRecentCustomGuiFileVersion(filename,interface_Id);
        //first locate that record by  aux_file_Id  and model_Id and Max(version)
        String updateAuxFileInfoQuery = "update AUX_FILES set LOCATION_ON_SERVER=? where FILE_NAME=? and INTERFACE_ID=? and VERSION=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(updateAuxFileInfoQuery);
            stmt.setString(1, file_on_server_location);
            stmt.setString(2, filename);
            stmt.setString(3, interface_Id);
            stmt.setInt(4, maxVersion);
            return (DbUtils.executeUpdate(stmt) == 1);
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return false;
        }


    }

    //update content should create a new version
    public static boolean update_content_AuxiliaryFileInfo(String aux_file_Id, String model_Id, String filename, int file_event_Id) throws XmlRpcException {
        int maxVersion = getMostRecentAuxFileVersion(aux_file_Id, model_Id);
        String location_on_server = null;
        //String _content=null;

        //first locate that record by  aux_file_Id  and model_Id and Max(version)
        String infoQuery = "select FILE_NAME,LOCATION_ON_SERVER from AUX_FILES where AUX_FILE_ID=? and MODEL_ID=? and VERSION=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(infoQuery);
            stmt.setString(1, aux_file_Id);
            stmt.setString(2, model_Id);
            stmt.setInt(3, maxVersion);
            Vector result = DbUtils.executeQuery(stmt, true);
            if (result == null || result.size() == 0) {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in getting recent Auxiliary FileInfo!");
            }

            location_on_server = (String) result.get(1);
            //_content= (String)result.get(2);
        } catch (SQLException e) {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

        //always replace
        //if(_content!=null&&_content.equalsIgnoreCase(content.toString())) //same content, no need to update
        //{
        //      System.out.println("same file content, do no replace");
        //      return false;
        // }

        update_location_AuxiliaryFileInfo(aux_file_Id, model_Id, "");
        insertAuxiliaryFileInfo(aux_file_Id, model_Id,"", filename, location_on_server, maxVersion + 1, file_event_Id);

        return true;//sucessfully
    }

       //update content should create a new version
       public static boolean update_content_customGuiFileInfo(String aux_file_Id , String modelId, String interfaceId, String filename, int lastestVersion,int file_event_Id) throws XmlRpcException {
           String location_on_server = null;
           //first locate that record by  customGuiFilename  and interface_Id and Max(version)
           String infoQuery = "select LOCATION_ON_SERVER from AUX_FILES where FILE_NAME=? and INTERFACE_ID=? and VERSION=?";
           try {
               PreparedStatement stmt = DbUtils.getPreparedStatement(infoQuery);
               stmt.setString(1, filename);
               stmt.setString(2, interfaceId);
               stmt.setInt(3, lastestVersion);
               Vector result = DbUtils.executeQuery(stmt, true);
               if (result == null || result.size() == 0) {
                   throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in getting recent Auxiliary FileInfo!");
               }

               location_on_server = (String) result.get(0);
           } catch (SQLException e) {
               throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
           }


           update_location_customGuiFileInfo(interfaceId, filename,"");
           insertAuxiliaryFileInfo(aux_file_Id, modelId,interfaceId, filename, location_on_server, lastestVersion + 1, file_event_Id);

           return true;//sucessfully
       }

    /**
     * the most recent version, if not found return 0
     * @param aux_file_Id
     * @param model_Id
     */
    public static int getMostRecentAuxFileVersion(String aux_file_Id, String model_Id)
    {
        String getMostRecentAuxFileVersionQuery = "select MAX(VERSION) from AUX_FILES where AUX_FILE_ID=? AND MODEL_ID=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentAuxFileVersionQuery);
            stmt.setString(1, aux_file_Id);
            stmt.setString(2, model_Id);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    public static Vector getMostRecentAuxFileInfo(String aux_file_Id, String model_Id) throws XmlRpcException {
        return getAuxFileInfoForVersion(aux_file_Id, model_Id, getMostRecentAuxFileVersion(aux_file_Id, model_Id));
    }

     /**
     * the most recent version, if not found return 0
     * @param customgui_file_name
     * @param interface_Id
     */
    public static int getMostRecentCustomGuiFileVersion(String customgui_file_name, String interface_Id) throws XmlRpcException {
        String getMostRecentCustomGuiFileVersionQuery = "select MAX(VERSION) from AUX_FILES where FILE_NAME=? AND INTERFACE_ID=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getMostRecentCustomGuiFileVersionQuery);
            stmt.setString(1, customgui_file_name);
            stmt.setString(2, interface_Id);
            Vector v = DbUtils.executeQuery(stmt, true);
            Object o = v.get(0);
            if (o == null)
                return 0;
            else
                return ((Integer) o).intValue();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            return DbConstants.NULL;
        }
    }

    public static String getMostRecentCustomGuiFileLocation(String customGuiName, String interface_Id) throws XmlRpcException {
        String getCustomFileLocationForMostRecentVersionQuery = "select LOCATION_ON_SERVER,MAX(VERSION) from AUX_FILES where AUX_FILE_ID=? AND INTERFACE_ID=? AND FILE_NAME=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getCustomFileLocationForMostRecentVersionQuery);
            stmt.setString(1, "customGuiFile");
            stmt.setString(2, interface_Id);
            stmt.setString(3, customGuiName);
            Vector v = DbUtils.executeQuery(stmt, true);
            if (v == null || v.size() == 0)
                return null;
            return v.get(0).toString();
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    /**
     *
     * @param aux_file_Id
     * @param model_Id
     * @param version
     * @return
     * @throws XmlRpcException
     */
    public static Vector getAuxFileInfoForVersion(String aux_file_Id, String model_Id, int version) throws XmlRpcException {
        String getAuxFileLocationForVersionQuery = "select FILE_NAME,LOCATION_ON_SERVER,VERSION from AUX_FILES where AUX_FILE_ID=? AND MODEL_ID=? AND VERSION=?";
        try {
            PreparedStatement stmt = DbUtils.getPreparedStatement(getAuxFileLocationForVersionQuery);
            stmt.setString(1, aux_file_Id);
            stmt.setString(2, model_Id);
            stmt.setInt(3, version);
            Vector v = DbUtils.executeQuery(stmt, true);
            if (v == null || v.size() == 0)
                return null;
            return v;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static String getAuxFileLocation(String aux_file_Id, String model_Id) throws XmlRpcException {
        Vector info = getMostRecentAuxFileInfo(aux_file_Id, model_Id);
        if (info == null)
            return null;
        else
            return (String) info.get(1);
    }
}
