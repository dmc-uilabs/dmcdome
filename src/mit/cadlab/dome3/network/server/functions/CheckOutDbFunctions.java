package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.util.Vector;
import java.sql.SQLException;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 23, 2003
 * Time: 1:07:00 PM
 * To change this template use Options | File Templates.
 */
public class CheckOutDbFunctions {


    public static Vector checkoutModel(int userId, String modelId, String loginType) throws XmlRpcException {

        Vector checkoutInfo = new Vector();

        //	if (!PermissionDbFunctions.hasPermission(userId, modelId, PermissionUtils.MODEL_COPY_PRIVILEGES))
        //		return checkoutInfo;

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(modelId, userId, PermissionUtils.MODEL_COPY_PRIVILEGES))
                return checkoutInfo;


        checkoutInfo.add(CheckOutDbFunctions.modelGetTypeLastBuildIdAndNameAndContent(modelId));
        checkoutInfo.add(CheckOutDbFunctions.modelGetLastIntfBuildIdAndDefAndMap(modelId, loginType, userId));



        //	if (PermissionDbFunctions.hasPermission(userId, modelId, PermissionUtils.MODEL_MODIFY_PRIVILEGES))
        //		checkoutInfo.add(DeployFilesDbFunctions.getModelVersionData(modelId));

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(modelId, userId, PermissionUtils.MODEL_MODIFY_PRIVILEGES))
                return checkoutInfo;

        checkoutInfo.add(DeployFilesDbFunctions.getModelVersionData(modelId));

        return checkoutInfo;
    }


    public static Vector checkoutAuxFileForModel(int userId, String auxFileId, String modelId, String loginType, int fileEventId) throws XmlRpcException {
        Vector checkoutInfo = new Vector();

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(modelId, userId, PermissionUtils.MODEL_COPY_PRIVILEGES))
                return checkoutInfo;

        //FILE_NAME,LOCATION_ON_SERVER,CONTENT,VERSION
        Vector auxFileInfo = DeployFilesDbFunctions.getMostRecentAuxFileInfo(auxFileId, modelId);
        if (auxFileInfo == null || auxFileInfo.size() == 0) return auxFileInfo;

        checkoutInfo.add(auxFileInfo.get(0));  //filename
        checkoutInfo.add(auxFileInfo.get(1));  //location on server

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(modelId, userId, PermissionUtils.MODEL_MODIFY_PRIVILEGES))
                return checkoutInfo;

        checkoutInfo.add(auxFileInfo.get(2));  //version

        return checkoutInfo;
    }


    public static Vector checkoutPlayspace(int userId, String playspaceId, String loginType) throws XmlRpcException {


        Vector checkoutInfo = new Vector();

//		if (!PermissionDbFunctions.hasPermission(userId, playspaceId, PermissionUtils.PLAYSPACE_COPY_PRIVILEGES))
//			return checkoutInfo;

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(playspaceId, userId, PermissionUtils.PLAYSPACE_COPY_PRIVILEGES))
                return checkoutInfo;


        checkoutInfo.add(CheckOutDbFunctions.playspaceGetNameAndContent(playspaceId));

//		if (PermissionDbFunctions.hasPermission(userId, playspaceId, PermissionUtils.PLAYSPACE_MODIFY_PRIVILEGES))
//			checkoutInfo.add(DeployFilesDbFunctions.getPlayspaceVersionInfo(playspaceId));

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(playspaceId, userId, PermissionUtils.PLAYSPACE_MODIFY_PRIVILEGES))
                return checkoutInfo;

        checkoutInfo.add(DeployFilesDbFunctions.getPlayspaceVersionInfo(playspaceId));
        return checkoutInfo;
    }

    public static Vector checkoutProject(int userId, String projectId, String loginType) throws XmlRpcException {


        Vector checkoutInfo = new Vector();

//		if (!PermissionDbFunctions.hasPermission(userId, playspaceId, PermissionUtils.PLAYSPACE_COPY_PRIVILEGES))
//			return checkoutInfo;

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(projectId, userId, PermissionUtils.MODEL_COPY_PRIVILEGES))
                return checkoutInfo;


        checkoutInfo.add(CheckOutDbFunctions.projectGetNameAndContent(projectId));
        checkoutInfo.add(CheckOutDbFunctions.projectGetLastIntfBuildIdAndDefAndMap(projectId, loginType, userId));
        checkoutInfo.add(CheckOutDbFunctions.imodelsGetLastBuildIdAndNameAndContent(projectId));
        checkoutInfo.add(CheckOutDbFunctions.iModelsGetLastIntfBuildIdAndDefAndMap(projectId, loginType, userId));

//		if (PermissionDbFunctions.hasPermission(userId, playspaceId, PermissionUtils.PLAYSPACE_MODIFY_PRIVILEGES))
//			checkoutInfo.add(DeployFilesDbFunctions.getPlayspaceVersionInfo(playspaceId));

        if (loginType.equalsIgnoreCase(LoginUtils.USER))
            if (!PermissionDbFunctions.checkPermissionUserAndGroups(projectId, userId, PermissionUtils.MODEL_MODIFY_PRIVILEGES))
                return checkoutInfo;

        checkoutInfo.add(DeployFilesDbFunctions.getProjectVersionData(projectId));
        return checkoutInfo;
    }

    public static Vector modelGetTypeLastBuildIdAndNameAndContent(String modelId) throws XmlRpcException {
        String modelInfo = "select TYPE, Build_id, name, content FROM Models,MODEL_VERSIONS WHERE Models.ID = '" + modelId + "' AND MODEL_VERSIONS.ID =Models.Last_Version_id";

        try {
            return DbUtils.executeQuery(modelInfo, true);

        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }

    public static Vector modelGetLastIntfBuildIdAndDefAndMap(String modelId, String loginType, int userId) throws XmlRpcException {

        //String intInfo = "select interface_versions.build_id, interface_versions.content, interface_versions.mappings from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id  and STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE + "' AND PARENT_ID = '" + modelId + "'";
        //String intInfoPermission = "select interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE + "' AND PARENT_ID = '" + modelId + "'";

        //checkout all interfaces
        String intInfo = "select interface_versions.build_id, interface_versions.content, interface_versions.mappings, interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND PARENT_ID = '" + modelId + "'";
        String intInfoPermission = "select interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND PARENT_ID = '" + modelId + "'";

        try {

            Vector interfaces = DbUtils.executeQuery(intInfo, false);
            Vector interfacesPermission = DbUtils.executeQuery(intInfoPermission, false);

            if (loginType.equalsIgnoreCase(LoginUtils.USER)) {

                Vector element = null;
                for (int i = interfaces.size() - 1; i >= 0; i--) {
                    element = (Vector) interfacesPermission.get(i);
                    if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(0), userId, PermissionUtils.PERMISSION_TO_VIEW_INTERFACE))
                        interfaces.remove(i);
                }

            }
            return interfaces;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }


    public static Vector playspaceGetNameAndContent(String playspaceId) throws XmlRpcException {
        String playspaceInfo = "select name, content FROM Playspace_versions,Playspaces WHERE Playspaces.ID = '" + playspaceId + "' AND Playspace_versions.ID =Playspaces.Last_Version_id";

        try {
            return DbUtils.executeQuery(playspaceInfo, true);

        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector projectGetNameAndContent(String projectId) throws XmlRpcException {
        String projectInfo = "select name, Build_id, content FROM Project_versions,Projects WHERE Projects.ID = '" + projectId + "' AND Project_versions.ID =Projects.Last_Version_id";

        try {
            return DbUtils.executeQuery(projectInfo, true);

        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector projectGetLastIntfBuildIdAndDefAndMap(String projectId, String loginType, int userId) throws XmlRpcException {

        //String intInfo = "select interface_versions.build_id, interface_versions.content, interface_versions.mappings from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id  and STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE + "' AND PARENT_ID = '" + modelId + "'";
        //String intInfoPermission = "select interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE + "' AND PARENT_ID = '" + modelId + "'";

        //checkout all interfaces
        String intInfo = "select interface_versions.build_id, interface_versions.content, interface_versions.mappings,interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND PARENT_ID = '" + projectId + "'";
        String intInfoPermission = "select interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND PARENT_ID = '" + projectId + "'";

        try {

            Vector interfaces = DbUtils.executeQuery(intInfo, false);
            Vector interfacesPermission = DbUtils.executeQuery(intInfoPermission, false);

            if (loginType.equalsIgnoreCase(LoginUtils.USER)) {

                Vector element = null;
                for (int i = interfaces.size() - 1; i >= 0; i--) {
                    element = (Vector) interfacesPermission.get(i);
                    if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(0), userId, PermissionUtils.PERMISSION_TO_VIEW_INTERFACE))
                        interfaces.remove(i);
                }

            }
            return interfaces;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }

    public static Vector imodelsGetLastBuildIdAndNameAndContent(String projectId) throws XmlRpcException {
        String imodelsInfo = "select Build_id, name, content FROM integration_models, integration_model_versions WHERE PROJECT_ID = '" + projectId + "' AND integration_model_versions.ID = integration_models.Last_Version_id";

        try {
            return DbUtils.executeQuery(imodelsInfo, false);

        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }

    public static Vector iModelsGetLastIntfBuildIdAndDefAndMap(String projectId, String loginType, int userId) throws XmlRpcException {

        //String intInfo = "select interface_versions.build_id, interface_versions.content, interface_versions.mappings from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id  and STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE + "' AND PARENT_ID = '" + modelId + "'";
        //String intInfoPermission = "select interfaces.id from Interface_versions, Interfaces     WHERE interfaces.last_version_id = interface_versions.id AND STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE + "' AND PARENT_ID = '" + modelId + "'";

        //checkout all interfaces
        String intInfo = "select integration_models.name, integration_model_versions.build_id, interface_versions.build_id, interface_versions.content, interface_versions.mappings from Interface_versions, Interfaces, integration_models, integration_model_versions    " +
                "WHERE PROJECT_ID = '" + projectId + "' AND integration_model_versions.ID = integration_models.Last_Version_id AND interfaces.last_version_id = interface_versions.id AND PARENT_ID = integration_models.ID";
        String intInfoPermission = "select interfaces.id from Interface_versions, Interfaces, integration_models, integration_model_versions     " +
                "WHERE PROJECT_ID = '" + projectId + "' AND integration_model_versions.ID = integration_models.Last_Version_id AND interfaces.last_version_id = interface_versions.id AND PARENT_ID = integration_models.ID";

        try {

            Vector interfaces = DbUtils.executeQuery(intInfo, false);
            Vector interfacesPermission = DbUtils.executeQuery(intInfoPermission, false);

            if (loginType.equalsIgnoreCase(LoginUtils.USER)) {

                Vector element = null;
                for (int i = interfaces.size() - 1; i >= 0; i--) {
                    element = (Vector) interfacesPermission.get(i);
                    if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(0), userId, PermissionUtils.PERMISSION_TO_VIEW_INTERFACE))
                        interfaces.remove(i);
                }

            }
            return interfaces;
        } catch (SQLException e) {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }


}
