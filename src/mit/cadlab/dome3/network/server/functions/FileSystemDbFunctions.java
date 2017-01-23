// FileSystemDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.functions.DeployFilesDbFunctions;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbConstants;

import org.apache.xmlrpc.XmlRpcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * set of functions for manipulating the virtual file system on the server
 */
public class FileSystemDbFunctions
{

	public static final String slash = System.getProperty("file.separator");

	private static final String deleteIModel = "delete from INTEGRATION_MODELS where ID=?";
	private static final String deleteProject = "delete from PROJECTS where ID=?";
	private static final String deleteInterfacesForParentId = "delete from INTERFACES where PARENT_ID=?";
	private static final String deleteInterfacePermissionsForParentId = "delete from OBJECT_PERMISSIONS where OBJECT_ID in (select ID from INTERFACES where PARENT_ID=?)";
	private static final String getIModelIdsFromProject = "select ID from INTEGRATION_MODELS where PROJECT_ID = ?";
	private static final String getIModelDeployAndBuildIdsFromProject = "select INTEGRATION_MODELS.ID, BUILD_ID from INTEGRATION_MODELS, " +
	        "INTEGRATION_MODEL_VERSIONS where PROJECT_ID = ? and LAST_VERSION_ID = INTEGRATION_MODEL_VERSIONS.ID";

    private static final String getAnalysisToolContents = "select ID, NAME, DESCRIPTION from ANALYSIS_TOOLS where FOLDER_ID=?";
    private static final String getAnalysisToolProjectId = "select PROJECT_ID from ANALYSIS_TOOLS where ID = ?";

	public static Integer getParentDbId(Object id, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getParentDbId");
		Integer parentId = new Integer(0);

		try {
			String query;
			Vector v;

			if (type.equalsIgnoreCase("dome_file") || type.equalsIgnoreCase("folder")) {
				//System.out.println("ileSystemDbFunctions: cheek for DomeFile");
				if (type.equalsIgnoreCase("dome_file")) {
					String Id = (String) id;
					query = "select FOLDER_ID from MODELS where ID='" + Id + "'";
					v = DbUtils.executeQuery(query, true);
					if (!v.isEmpty()) {
						//System.out.println("FileSystemDbFunctions: found a MODELS");
						return (Integer) v.get(0);
					}

					query = "select FOLDER_ID from PLAYSPACES where ID='" + Id + "'";
					v = DbUtils.executeQuery(query, true);
					if (!v.isEmpty()) {
						//System.out.println("FileSystemDbFunctions: found a PLAYSPACES");
						return (Integer) v.get(0);
					}

					query = "select FOLDER_ID from PROJECTS where ID='" + Id + "'";
					v = DbUtils.executeQuery(query, true);
					if (!v.isEmpty()) {
						//System.out.println("FileSystemDbFunctions: found a PROJECTS");
						return (Integer) v.get(0);
					}
				}
				//System.out.println("ileSystemDbFunctions: cheek for Folder");

				int Id = ((Integer) id).intValue();

				query = "select PARENT_ID from MODEL_FOLDERS where ID='" + Id + "'";
				v = DbUtils.executeQuery(query, true);
				if (!v.isEmpty()) {
					//System.out.println("FileSystemDbFunctions: found a MODEL_FOLDERS");
					return (Integer) v.get(0);
				}

				query = "select PARENT_ID from PLAYSPACE_FOLDERS where ID='" + Id + "'";
				v = DbUtils.executeQuery(query, true);
				if (!v.isEmpty()) {
					//System.out.println("FileSystemDbFunctions: found a PLAYSPACE_FOLDERS");
					return (Integer) v.get(0);
				}


			} else {
				System.out.println("FileSystemDbFunctions: Did not find anything...");

			}

		} catch (SQLException e) {
			System.out.println("FileSystemDbFunctions: exception");
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}


		return parentId;
	}

    public static Vector hasAnalysisToolPrototypeFolderBeenCreated(int parentFolderId, String folderName) throws XmlRpcException
    {
        Vector v = new Vector();
        boolean flag = false;

        if (parentFolderId < 0 && parentFolderId != DbConstants.NULL)
            throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, "");
        if (folderName == null || folderName.equals(""))
            throw new XmlRpcException(DbErrors.XMLRPC_EMPTY_FOLDER_NAME,
                                      DbErrors.XMLRPC_EMPTY_FOLDER_NAME_MSG);

        try
        {
            Statement stmt = DbUtils.getStatement();
            String query = null;
            ResultSet r;

            if (parentFolderId != DbConstants.NULL)
            {
                query = "select * from MODEL_FOLDERS where ID=" + parentFolderId;
                r = stmt.executeQuery(query);
                if (!r.next())
                {
                    throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, DbErrors.XMLRPC_NO_SUCH_FOLDER_MSG);
                }
            }
            query = "select * from MODEL_FOLDERS where PARENT_ID='" + parentFolderId + "' and NAME='" + folderName + "'";
            r = stmt.executeQuery(query);
            if (r.next())
                flag = true;
            else
                flag = false;

            v.add(new Boolean(flag));
            return v;
        }
        catch (SQLException e)
        {
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

	/**
	 * Create a new folder and assign its parent id.
	 * @param parentFolderId Id of the parent folder
	 * @param folderName Name of the new folder
	 * @return Vector containing new folder Id / folder Name / parent Folder Id.
	 */
	public static int createFolder(int parentFolderId, String folderName, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "createFolder");
		// check that parent folder is possibly valid
		if (parentFolderId < 0 && parentFolderId != DbConstants.NULL)
			throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, "");
		if (folderName == null || folderName.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_EMPTY_FOLDER_NAME,
			                          DbErrors.XMLRPC_EMPTY_FOLDER_NAME_MSG);

		try {
			Statement stmt = DbUtils.getStatement();
			String query = null;
			ResultSet r;
			// see if parent folder exists
			if (parentFolderId != DbConstants.NULL) {
				if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
					query = "select * from MODEL_FOLDERS where ID=" + parentFolderId;
				else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
					query = "select * from PLAYSPACE_FOLDERS where ID=" + parentFolderId;
				r = stmt.executeQuery(query);
				if (!r.next()) {
					// no such parent folder.. cannot create folder
					throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, DbErrors.XMLRPC_NO_SUCH_FOLDER_MSG);
				}
			}

			//TODO: replace code below by relying on uniqueness constraint (catch SQL exception on insert)
			// check that the folder name does not already exist in this scope
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "select * from MODEL_FOLDERS where PARENT_ID='";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "select * from PLAYSPACE_FOLDERS where PARENT_ID='";
			query += parentFolderId + "' and NAME='" + folderName + "'";
			r = stmt.executeQuery(query);
			if (r.next()) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_FOLDER_EXISTS,
				                          DbErrors.XMLRPC_FOLDER_EXISTS_MSG);
			}

			// add new folder
			folderName = "'" + folderName + "'";
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "insert into MODEL_FOLDERS";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "insert into PLAYSPACE_FOLDERS";
			query += "(NAME, PARENT_ID) values (" + folderName + ",'" + parentFolderId + "')";
			return DbUtils.executeInsert(query, true);
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 * Delete a folder.
	 * @param folderId Folder Id.
	 */
	public static void deleteFolder(int folderId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "deleteFolder");
		if (folderId < 0 && folderId != DbConstants.NULL)
			throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER,
			                          DbErrors.XMLRPC_NO_SUCH_FOLDER_MSG);

		try {
			Statement stmt = DbUtils.getStatement();
			String query = null;
			ResultSet r;

			// see if folder exists
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "select * from MODEL_FOLDERS where ID='" + folderId + "'";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "select * from PLAYSPACE_FOLDERS where ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			if (!r.next()) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER,
				                          DbErrors.XMLRPC_NO_SUCH_FOLDER_MSG);
			}

			// see if the folder is empty
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "select * from MODEL_FOLDERS where PARENT_ID='" + folderId + "'";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "select * from PLAYSPACE_FOLDERS where PARENT_ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			if (r.next()) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_FOLDER_NOT_EMPTY,
				                          DbErrors.XMLRPC_FOLDER_NOT_EMPTY_MSG);
			}
			query = "select * from MODELS where FOLDER_ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			if (r.next()) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_FOLDER_NOT_EMPTY,
				                          DbErrors.XMLRPC_FOLDER_NOT_EMPTY_MSG);
			}
			query = "select * from PROJECTS where FOLDER_ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			if (r.next()) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_FOLDER_NOT_EMPTY,
				                          DbErrors.XMLRPC_FOLDER_NOT_EMPTY_MSG);
			}

			// delete the folder from the folder list
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "delete from MODEL_FOLDERS where ID='" + folderId + "'";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "delete from PLAYSPACE_FOLDERS where ID='" + folderId + "'";
			stmt.execute(query);

			// commit transactions and close jdbc statement
			DbUtils.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static void renameFolder(int folderId, String newName, String type) throws XmlRpcException
	{
		try {
			Statement stmt = DbUtils.getStatement();
			String query = null;
			ResultSet r;

			// see if folder exists
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "select * from MODEL_FOLDERS where ID='" + folderId + "'";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "select * from PLAYSPACE_FOLDERS where ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			if (!r.next()) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER,
				                          DbErrors.XMLRPC_NO_SUCH_FOLDER_MSG);
			}

			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "select PARENT_ID from MODEL_FOLDERS where ID='" + folderId + "'";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "select PARENT_ID from PLAYSPACE_FOLDERS where ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			Vector v = DbUtils.oneRowResultSetToVector(r);
			if ((Integer) v.get(0) == new Integer(DbConstants.NULL)) {
				stmt.close();
				throw new XmlRpcException(DbErrors.XMLRPC_CAN_NOT_RENAME_PUBLIC_OR_PRIVATE_FOLDER,
				                          DbErrors.XMLRPC_CAN_NOT_RENAME_PUBLIC_OR_PRIVATE_FOLDER_MSG);
			}
			System.out.println("FileSystemDbFunctions: pass");
			if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
				query = "update MODEL_FOLDERS ";
			else if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
				query = "update PLAYSPACE_FOLDERS ";
			query += "set NAME = '" + newName + "' where ID='" + folderId + "'";
			DbUtils.executeInsert(query, false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

	}

	/**
	 * move folder
	 * @param folderId Id of the folder
	 * @param parentFolderId Id of the new parent folder
	 * @return Vector containing new folder Id / folder Name / parent Folder Id
	 */
	public static Vector moveFolder(int folderId, int parentFolderId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "moveFolder");

		// check that new parent folder is possibly valid
		if (parentFolderId < 0 && parentFolderId != DbConstants.NULL)
			throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, "");

		try {
			Statement stmt = DbUtils.getStatement();
			String query;
			ResultSet r;
			// see if parent folder exists
			if (parentFolderId != DbConstants.NULL) {
				query = "select * from FOLDERS where ID=" + parentFolderId;
				r = stmt.executeQuery(query);
				if (!r.next()) {
					throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, "");
				}
			}

			// check that folder to be moved exists
			query = "select * from FOLDERS where ID='" + folderId + "'";
			r = stmt.executeQuery(query);
			Vector v = DbUtils.oneRowResultSetToVector(r);
			if (!v.isEmpty()) { //folder exists
				// get the name of the folder
				String folderName = (String) v.get(1);

				// check that the folder name does not already exist in the new parent scope
				query = "select * from FOLDERS where PARENT_ID='" + parentFolderId + "' and NAME='" + folderName + "'";
				r = stmt.executeQuery(query);
				if (r.next()) {    // this name already exists in the scope
					stmt.close();
					throw new XmlRpcException(DbErrors.XMLRPC_FOLDER_EXISTS,
					                          DbErrors.XMLRPC_FOLDER_EXISTS_MSG);
				} else { // okay to move here
					query = "update FOLDERS set PARENT_ID='" + parentFolderId + "' " + "where ID='" + folderId + "'";
					stmt.executeUpdate(query);

					// get results
					query = "select ID,NAME,PARENT_ID from FOLDERS where ID='" + folderId + "'";
					r = stmt.executeQuery(query);
					v = DbUtils.oneRowResultSetToVector(r);
					//System.out.println(v.size() + ": " + v);

					// commit transactions and close jdbc statement
					DbUtils.commit();
					stmt.close();

					return v;
				}
			} else {
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_FOLDER, "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 * get both Public and Private folders of either model or playspace for a user or group
	 * @param id User or group id
	 * @param type has to be either MODEL or PLAYSPACE
	 * @return
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserGroupFolders(int id, String type) throws XmlRpcException
	{
		Vector v = new Vector();
		try {
			String getFolders = null;
			if (type.equals(DbConstants.MODEL_TYPE))
				getFolders = "select PUBLIC_MODEL_FOLDER_ID,PRIVATE_MODEL_FOLDER_ID ";
			else if (type.equals(DbConstants.PLAYSPACE_TYPE))
				getFolders = "select PUBLIC_PLAYSPACE_FOLDER_ID,PRIVATE_PLAYSPACE_FOLDER_ID ";
			getFolders += "from USER_GROUP_FOLDERS where USER_GROUP_ID=?";

			PreparedStatement stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			v = DbUtils.executeQuery(stmt, true);

			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP_OR_HAS_NO_FOLDER,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_OR_HAS_NO_FOLDER_MSG);
			}
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
		return v;
	}

	public static Vector getAvailableInterfaces(String Id) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getAvailableInterfaces");
		try {
			String getAvailableIds = "select BUILD_ID, STATUS, DESCRIPTION from interface_versions, interfaces where PARENT_ID = '"
			        + Id + "' and interface_versions.INTERFACE_ID = interfaces.ID";
			return DbUtils.executeQuery(getAvailableIds, false);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getAvailableInterfaceIds(String type, String id) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getAvailableInterfaceIds");
		try {
			String getAvailableIds = "select BUILD_ID, INTERFACES.ID from INTERFACES, INTERFACE_VERSIONS, FILE_EVENTS " +
									"where PARENT_ID = '" + id + "' and LAST_VERSION_ID = INTERFACE_VERSIONS.ID " +
									" and FILE_EVENT_ID = EVENT_ID and STATUS = 'AVAILABLE'";
			return DbUtils.executeQuery(getAvailableIds);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	public static Vector getAvailableInterfacesInfo(int userId, String logonType, String parentId, String permissionType) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getAvailableInterfacesInfo");


		try {

			String getAvailableInterfaces = "select NAME, INTERFACES.ID, DESCRIPTION, VERSION, FILE_EVENTS.DATE from INTERFACES, INTERFACE_VERSIONS, FILE_EVENTS where PARENT_ID = '"
			        + parentId + "' and STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE
			        + "' and LAST_VERSION_ID = INTERFACE_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";
			Vector interfaces = DbUtils.executeQuery(getAvailableInterfaces, false);


			if (logonType.equalsIgnoreCase(LoginUtils.USER) || logonType.equalsIgnoreCase(LoginUtils.GUEST))
			{

				Vector element = null;
				for (int i = interfaces.size() - 1; i >= 0; i--) {
					element = (Vector) interfaces.get(i);

					if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(1), userId, permissionType))
						interfaces.remove(i);
				}
			}
			return interfaces;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getAvailableProjectContents(int userId, String loginType, String projectId, String mode) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getAvailableProjectContents");

		try {
			String interfacePermissionType = null;
			String projectPermissionType = null;

			if (DbConstants.FILESYSTEM_BROWSE.equals(mode)) {
				projectPermissionType = PermissionUtils.PERMISSION_TO_SEE_CONTENTS_RUN_MODE;
				interfacePermissionType = PermissionUtils.PERMISSION_TO_VIEW_INTERFACE;
			} else if (DbConstants.FILESYSTEM_SUBSCRIBE.equals(mode)) {
				projectPermissionType = PermissionUtils.PERMISSION_TO_SEE_CONTENTS_SUBSCRIBE;
				interfacePermissionType = PermissionUtils.PERMISSION_TO_SUBSCRIBE_TO_INTERFACE;
			} else
				throw new RuntimeException("ProjectResourceInfo.loadResource - illegal mode: " + mode);

			boolean canSeeInside = true;
			if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST))
				canSeeInside = PermissionDbFunctions.checkPermissionUserAndGroups(projectId, userId, projectPermissionType);

			if (!canSeeInside)
				return new Vector();

			String projectXml = DeployFilesDbFunctions.getMostRecentProjectXmlDefinition(projectId);
			Vector iModelIds = getIModelIdsFromProject(projectId);
			Vector iModelInfo = new Vector();
			for (int i = 0; i < iModelIds.size(); i++) {
				String iModelId = (String) iModelIds.get(i);

				String getAvailableIModels = "select NAME, INTERFACES.ID, DESCRIPTION, VERSION, FILE_EVENTS.DATE from INTERFACES, INTERFACE_VERSIONS, FILE_EVENTS where PARENT_ID = '"
				        + iModelId + "' and STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE
				        + "' and LAST_VERSION_ID = INTERFACE_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";
				Vector interfaces = DbUtils.executeQuery(getAvailableIModels, false);

				if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST)) {

					Vector element = null;
					for (int j = interfaces.size() - 1; j >= 0; j--) {
						element = (Vector) interfaces.get(j);

						if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(1), userId, interfacePermissionType))
							interfaces.remove(j);
					}
				}

				if (!interfaces.isEmpty()) {
					String getIModelInfo = "select NAME, INTEGRATION_MODELS.ID, DESCRIPTION, VERSION, FILE_EVENTS.DATE " +
					        "from INTEGRATION_MODELS, INTEGRATION_MODEL_VERSIONS, FILE_EVENTS where INTEGRATION_MODELS.ID = '"
					        + iModelId + "' and LAST_VERSION_ID = INTEGRATION_MODEL_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";
					Vector anIModelInfo = DbUtils.executeQuery(getIModelInfo, true);
					iModelInfo.add(Vectors.create(anIModelInfo, interfaces));
				}
			}
			return Vectors.create(projectXml, iModelInfo);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static String[] getInterfaceStaticInfo(String ifaceId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getModelStaticInfo");

		String[] record = null;
		String getStaticInfoQuery = "select ID, NAME, DESCRIPTION from INTERFACES where ID='" + ifaceId + "'";
		try {
			ResultSet r;
			Statement stmt = DbUtils.getStatement();
			r = stmt.executeQuery(getStaticInfoQuery);
			if (r.next()) {
				String id = r.getString("ID");
				String name = r.getString("NAME");
				String descr = r.getString("DESCRIPTION");
				record = new String[]{id, name, descr};
			}
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

		return record;
	}

	public static String[] getModelStaticInfo(String modelId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getModelStaticInfo");

		String[] record = null;
		String getStaticInfoQuery = "select ID, TYPE, NAME, DESCRIPTION from MODELS where ID='" + modelId + "'";
		try {
			ResultSet r;
			Statement stmt = DbUtils.getStatement();
			r = stmt.executeQuery(getStaticInfoQuery);
			if (r.next()) {
				String id = r.getString("ID");
				String name = r.getString("NAME");
				String descr = r.getString("DESCRIPTION");
				String type = r.getString("TYPE");
				record = new String[]{id, name, descr, type};
			}
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

		return record;
	}

    public static String[] getAnalysisToolStaticInfo(String analysisToolId) throws XmlRpcException
    {
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getAnalysisToolStaticInfo");
        String[] record = null;
        String getStaticInfoQuery = "select ID, TYPE, NAME, DESCRIPTION, PROJECT_ID from ANALYSIS_TOOLS where ID='" + analysisToolId + "'";
        try
        {
            ResultSet r;
            Statement stmt = DbUtils.getStatement();
            r = stmt.executeQuery(getStaticInfoQuery);
            if (r.next())
            {
                String id = r.getString("ID");
                String name = r.getString("NAME");
                String description = r.getString("DESCRIPTION");
                String type = r.getString("TYPE");
                String projectId = r.getString("PROJECT_ID");
                record = new String[]{id, name, description, type, projectId};
            }
        } catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

        return record;
    }

	public static String[] getProjectStaticInfo(String modelId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getProjectStaticInfo");

		String[] record = null;
		String getStaticInfoQuery = "select ID, NAME, DESCRIPTION from PROJECTS where ID='" + modelId + "'";
		try {
			ResultSet r;
			Statement stmt = DbUtils.getStatement();
			r = stmt.executeQuery(getStaticInfoQuery);
			if (r.next()) {
				String id = r.getString("ID");
				String name = r.getString("NAME");
				String descr = r.getString("DESCRIPTION");
				record = new String[]{id, name, descr};
			}
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

		return record;
	}

	public static String[] getIntegrationModelStaticInfo(String modelId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getIntegrationModelStaticInfo");

		String[] record = null;
		String getStaticInfoQuery = "select ID, NAME, DESCRIPTION from INTEGRATION_MODELS where ID='" + modelId + "'";
		try {
			ResultSet r;
			Statement stmt = DbUtils.getStatement();
			r = stmt.executeQuery(getStaticInfoQuery);
			if (r.next()) {
				String id = r.getString("ID");
				String name = r.getString("NAME");
				String descr = r.getString("DESCRIPTION");
				record = new String[]{id, name, descr};
			}
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

		return record;
	}

	public static String getInterfaceDescription(String objectId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getInterfaceDescription");
		try {

			String getAvailableIds = "select CONTENT from INTERFACES, INTERFACE_VERSIONS where INTERFACES.ID = '"
			        + objectId + "' and STATUS = '" + DbConstants.INTERFACE_STATUS_AVAILABLE
			        + "' and LAST_VERSION_ID = INTERFACE_VERSIONS.ID";

			Vector v = DbUtils.executeQuery(getAvailableIds, true);
			if (v.isEmpty())
				return "";
			String s = (String) v.get(0);
			if (s != null)
				return s;
			return new String("");
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static String getPlayspaceDescription(String playspaceId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getPlayspaceDescription");
		try {

			String getAvailableIds = "select CONTENT from PLAYSPACES, PLAYSPACE_VERSIONS where PLAYSPACES.ID = '"
			        + playspaceId + "' and LAST_VERSION_ID = PLAYSPACE_VERSIONS.ID";
			Vector v = DbUtils.executeQuery(getAvailableIds, true);
			if (v == null || v.size() == 0) {
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in getting recent playspace definition!");
			}
			String s = (String) v.get(0);
			return s;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getPlayspaceInfo(String id) throws XmlRpcException
	{
		try {
			String getPlayspaceInfo = "select PLAYSPACES.ID, NAME, DESCRIPTION, VERSION, FILE_EVENTS.DATE " +
			        "from PLAYSPACES, PLAYSPACE_VERSIONS, FILE_EVENTS where PLAYSPACES.ID = '"
			        + id + "' and LAST_VERSION_ID = PLAYSPACE_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";
			PreparedStatement stmt = DbUtils.getPreparedStatement(getPlayspaceInfo);
			stmt.setString(1, id);
			return DbUtils.executeQuery(stmt, true);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static String getProjectDescription(String projectId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getProjectDescription");
		try {

			String getAvailableIds = "select CONTENT from PROJECTS, PROJECT_VERSIONS where PROJECTS.ID = '"
			        + projectId + "' and LAST_VERSION_ID = PROJECT_VERSIONS.ID";

			Vector v = DbUtils.executeQuery(getAvailableIds, true);
			if (v == null || v.size() == 0) {
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in getting recent project definition!");
			}
			String s = (String) v.get(0);
			return s;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	public static String getIntegrationModelDescription(String iModelId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getIntegrationModelDescription");
		try {

			String getAvailableIds = "select CONTENT from INTEGRATION_MODELS, INTEGRATION_MODEL_VERSIONS where INTEGRATION_MODELS.ID = '"
			        + iModelId + "' and LAST_VERSION_ID = INTEGRATION_MODEL_VERSIONS.ID";

			Vector v = DbUtils.executeQuery(getAvailableIds, true);
			if (v == null || v.size() == 0) {
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
				                          "Error in getting recent integration model definition!");
			}
			String s = (String) v.get(0);
			return s;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	public static String getModelDescription(String modeltId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getModelDescription");
		try {

			String getAvailableIds = "select CONTENT from MODELS, MODEL_VERSIONS where MODELS.ID = '"
			        + modeltId + "' and LAST_VERSION_ID = MODEL_VERSIONS.ID";

			Vector v = DbUtils.executeQuery(getAvailableIds, true);
			if (v == null || v.size() == 0) {
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
				                          "Error in getting recent model definition!");
			}
			String s = (String) v.get(0);
			return s;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

    public static String getAnalysisToolDescription(String analysisToolId) throws XmlRpcException
    {
        Debug.trace(Debug.ALL, "FileSystemDbFunctions : getAnalysisToolDescription");
        try
        {
            String getAvailableIds = "select CONTENT from ANALYSIS_TOOLS, ANALYSIS_TOOL_VERSIONS where " +
                    "ANALYSIS_TOOLS.ID = '" + analysisToolId + "' and LAST_VERSION_ID = ANALYSIS_TOOL_VERSIONS.ID";
            Vector v = DbUtils.executeQuery(getAvailableIds, true);
            if(v == null || v.size() == 0)
            {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR,
                                            "Error in getting recent analysis tool definition!");
            }
            String s = (String) v.get(0);
            return s;
        } catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }


	/**
	 * This function gets the contents of a model folder
	 * @param id Folder ID
	 * @return id, name, type
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */


	public static Vector getPlayspaceFolderContents(int userId, String loginType, int id) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "FileSystemDbFunctions: getPlayspaceFolderContents");
        try {
			Vector v = new Vector();
			String getFolders = "select ID, NAME from PLAYSPACE_FOLDERS where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			Vector folder_v = DbUtils.executeQuery(stmt, false);
			v.add(folder_v);

			Vector model_v = new Vector();

			getFolders = "select ID, NAME, DESCRIPTION from PLAYSPACES where FOLDER_ID=?";
			stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			model_v = DbUtils.executeQuery(stmt, false);

			if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST)) {
				Vector element = null;
				for (int i = model_v.size() - 1; i >= 0; i--) {
					element = (Vector) model_v.get(i);
					if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(0), userId, PermissionUtils.PLAYSPACE_CHANGE_PRIVILEGES))
						model_v.remove(i);
				}
			}

			v.add(model_v);
			return v;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	public static Vector getModelFolderContents(int userId, String loginType, int id) throws XmlRpcException
	{
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getModelFolderContents");
		try {
			Vector v = new Vector();
			String getFolders = "select ID, NAME from MODEL_FOLDERS where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			Vector folder_v = DbUtils.executeQuery(stmt, false);
			v.add(folder_v);
			Vector model_v = new Vector();

			getFolders = "select ID, NAME, DESCRIPTION, TYPE from MODELS where FOLDER_ID=?";
			stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			model_v = DbUtils.executeQuery(stmt, false);

			if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST)) {
				Vector element = null;
				for (int i = model_v.size() - 1; i >= 0; i--) {
					element = (Vector) model_v.get(i);
					if (getAvailableInterfacesInfo(userId, loginType, (String) element.get(0), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE).isEmpty()) {
						model_v.remove(i);
						System.out.println("EMPTY INTERFACE");
					}
				}
			}

			v.add(model_v);
			return v;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getAnalysisToolContents(int userId, String loginType, int id) throws XmlRpcException
    {
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getAnalysisToolContents");
        try
        {
            Vector v = new Vector();
            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolContents);
            stmt.setInt(1, id);
            v =  DbUtils.executeQuery(stmt, false);
            if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST))
            {
                Vector element = null;
                for (int i = v.size() - 1; i >= 0; i--)
                {
                    element = (Vector) v.get(i);
                    if (getAvailableInterfacesInfo(userId, loginType, (String) element.get(0), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE).isEmpty()
                            && !(PermissionDbFunctions.hasPermission(userId, "" + (String)element.get(0), PermissionUtils.PERMISSION_TO_SEE_CONTENTS_RUN_MODE)))
                    {
                        v.remove(i);
                    }
                }
            }
			return v;
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector getProjectInfoInsideAnalysisTool(int userId, String loginType, String analysisToolId) throws XmlRpcException
    {
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getProjectInfoInsideAnalysisTool");
        try
        {
            Vector v = new Vector();
            v = getProjectIdInsideAnalysisTool(analysisToolId);
            String projectId = (String)((Vector)v.get(0)).get(0);
            Vector projectInfo = getProjectInfo(projectId);

            if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST))
            {
                if (getAvailableInterfacesInfo(userId, loginType, (String) projectInfo.get(0), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE).isEmpty()
                        && !(PermissionDbFunctions.hasPermission(userId, "" + (String) projectInfo.get(0), PermissionUtils.PERMISSION_TO_SEE_CONTENTS_RUN_MODE)))
                {
                    projectInfo.removeAllElements();
                }

            }
			return projectInfo;
        }
        catch (XmlRpcException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector getProjectIdInsideAnalysisTool(String analysisToolId) throws XmlRpcException
    {
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getProjectIdInsideAnalysisTool");
        try
        {
            Vector v = new Vector();
            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolProjectId);
            stmt.setString(1, analysisToolId);
            v = DbUtils.executeQuery(stmt, false);
            return v;
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector getProjectInsideAnalysisTool(String analysisToolId) throws XmlRpcException
    {
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getProjectInsideAnalysisTool");
        try
        {
            Vector results = new Vector();

            /**
             * First, obtain the embedded project id.
             */
            Vector v = new Vector();
            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolProjectId);
            stmt.setString(1, analysisToolId);
            v = DbUtils.executeQuery(stmt, false);
            String projectId = (String)((Vector)v.get(0)).get(0);

            /**
             * Use the project id, to obtain info required to create
             * a IntegrationProjectServerRuntime object.
             */
            String xmlContent = FileSystemDbFunctions.getProjectDescription(projectId);
		    if (xmlContent.equals(""))
			    throw new XmlRpcException(DbErrors.XMLRPC_NO_XML_DESCRIPTION,
			                              DbErrors.XMLRPC_NO_XML_DESCRIPTION_MSG);

            results.add(projectId);     // adding project deploy id
            results.add(xmlContent);    // adding project xml content
            return results;

        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

    public static Vector getModelInfo(String id) throws XmlRpcException
	{
		try {
			String getModelInfo = "select MODELS.ID, NAME, DESCRIPTION, VERSION, FILE_EVENTS.DATE " +
			        "from MODELS, MODEL_VERSIONS, FILE_EVENTS where MODELS.ID = '"
			        + id + "' and LAST_VERSION_ID = MODEL_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";

			PreparedStatement stmt = DbUtils.getPreparedStatement(getModelInfo);
			stmt.setString(1, id);
			return DbUtils.executeQuery(stmt, true);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getProjectInfo(String id) throws XmlRpcException
	{
		try {
			String getProjectInfo = "select PROJECTS.ID, NAME, DESCRIPTION, VERSION, FILE_EVENTS.DATE " +
			        "from PROJECTS, PROJECT_VERSIONS, FILE_EVENTS where PROJECTS.ID = '"
			        + id + "' and LAST_VERSION_ID = PROJECT_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";

			PreparedStatement stmt = DbUtils.getPreparedStatement(getProjectInfo);
			stmt.setString(1, id);
			return DbUtils.executeQuery(stmt, true);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param id
	 * @return most recent version of interface; returns 0 if no interface found
	 * @throws XmlRpcException
	 */
	public static Integer getInterfaceVersion(String id) throws XmlRpcException
	{
		try {
			String getInterfaceVersion = "select MAX(VERSION) " +
			        "from INTERFACES, INTERFACE_VERSIONS where INTERFACES.ID = '"
			        + id + "' and INTERFACES.ID = INTERFACE_VERSIONS.INTERFACE_ID";

			PreparedStatement stmt = DbUtils.getPreparedStatement(getInterfaceVersion);
			stmt.setString(1, id);
			Vector result = DbUtils.executeQuery(stmt, true);
			if (result.size()==1)
				return (Integer)result.get(0);
			return new Integer(0);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

    public static Vector getAnalysisToolInfo(String id) throws XmlRpcException
    {
        try
        {
            String getAnalysisToolInfo = "select ANALYSIS_TOOLS.ID, NAME, DESCRIPTION, VERSION, FILE_EVENTS.DATE " +
                    "from ANALYSIS_TOOLS, ANALYSIS_TOOL_VERSIONS, FILE_EVENTS where ANALYSIS_TOOLS.ID = '"
                    + id + "' and LAST_VERSION_ID = ANALYSIS_TOOL_VERSIONS.ID and FILE_EVENT_ID = EVENT_ID";

            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolInfo);
            stmt.setString(1, id);
            return DbUtils.executeQuery(stmt, true);
        }
        catch (SQLException e)
        {
            Debug.trace(Debug.ERROR, e.toString());
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

	public static Vector getProjectsContents(int userId, String loginType, int id) throws XmlRpcException
	{
        //Debug.trace(Debug.ALL, "FileSystemDbFunctions: getProjectsContents");
		try {
			Vector v = new Vector();

			String query = "select ID, NAME, DESCRIPTION from PROJECTS where FOLDER_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setInt(1, id);
			v = DbUtils.executeQuery(stmt, false);

            if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST))
            {
                Vector element = null;
                for (int i = v.size() - 1; i >= 0; i--)
                {
                    element = (Vector) v.get(i);
                    if (getAvailableInterfacesInfo(userId, loginType, (String) element.get(0), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE).isEmpty()
                            && !(PermissionDbFunctions.hasPermission(userId, "" + (String)element.get(0), PermissionUtils.PERMISSION_TO_SEE_CONTENTS_RUN_MODE)))
                    {
                        v.remove(i);
                    }
                }
            }
			return v;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getSubscriptionModelFolderContents(int userId, String loginType, int id) throws XmlRpcException
	{
		try {
			Vector v = new Vector();
			String getFolders = "select ID, NAME from MODEL_FOLDERS where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			Vector folder_v = DbUtils.executeQuery(stmt, false);
			v.add(folder_v);

			Vector model_v = new Vector();

			getFolders = "select ID, NAME, DESCRIPTION, TYPE from MODELS where FOLDER_ID=?";
			stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			model_v = DbUtils.executeQuery(stmt, false);

			if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST)) {

				Vector element = null;
				for (int i = model_v.size() - 1; i >= 0; i--) {
					element = (Vector) model_v.get(i);
					if (getAvailableInterfacesInfo(userId, loginType, (String) element.get(0), PermissionUtils.PERMISSION_TO_SUBSCRIBE_TO_INTERFACE).isEmpty())
						model_v.remove(i);

				}
			}

			v.add(model_v);
			return v;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/*
	public static Vector getFolderContents(int userId, String loginType, int id, String type) throws XmlRpcException
	{
		try {
			Vector v = new Vector();
			String getFolders = "select ID, NAME from " + type + "_FOLDERS where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			Vector folder_v = DbUtils.executeQuery(stmt, false);
			v.add(folder_v);

			Vector model_v = new Vector();
			if (type.equals("MODEL")) {
				getFolders = "select ID, NAME, DESCRIPTION, TYPE from MODELS where FOLDER_ID=?";
				stmt = DbUtils.getPreparedStatement(getFolders);
				stmt.setInt(1, id);
				model_v = DbUtils.executeQuery(stmt, false);

				if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST)) {

					Vector element = null;
					for (int i = model_v.size() - 1; i >= 0; i--) {
						element = (Vector) model_v.get(i);
						if (getAvailableInterfacesInfo(userId, loginType, (String)element.get(0)).isEmpty())
							model_v.remove(i);

					}

				}
			}
			else if (type.equals("PLAYSPACE")) {

				getFolders = "select ID, NAME, DESCRIPTION from PLAYSPACES where FOLDER_ID=?";
				stmt = DbUtils.getPreparedStatement(getFolders);
				stmt.setInt(1, id);
				model_v = DbUtils.executeQuery(stmt, false);

				if (loginType.equalsIgnoreCase(LoginUtils.USER) || loginType.equalsIgnoreCase(LoginUtils.GUEST)) {
				 Vector element = null;
					for (int i = model_v.size() - 1; i >= 0; i--) {
						element = (Vector) model_v.get(i);
							if (!PermissionDbFunctions.checkPermissionUserAndGroups((String) element.get(0), userId, PermissionUtils.PLAYSPACE_CHANGE_PRIVILEGES))
								model_v.remove(i);
						}
				}
			}
			v.add(model_v);
			return v;
		}
		catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

     */
	public static Vector getLastModified(String id, String type) throws XmlRpcException
	{
		try {
			String getLastModifiedQuery = "select DATE, MAX(VERSION) from FILE_EVENTS, ";
			if (type.equals(DbConstants.MODEL_TYPE))
				getLastModifiedQuery += "MODEL_VERSIONS WHERE MODEL_ID=? and MODEL_VERSIONS";
			else if (type.equals(DbConstants.PLAYSPACE_TYPE))
				getLastModifiedQuery += "PLAYSPACE_VERSIONS WHERE PLAYSPACE_ID=? and PLAYSPACE_VERSIONS";
			else if (type.equals(DbConstants.PROJECT_TYPE))
				getLastModifiedQuery += "PROJECT_VERSIONS WHERE PROJECT_ID=? and PROJECT_VERSIONS";
            else if (type.equals(DbConstants.ANALYSIS_TOOL_TYPE))
                getLastModifiedQuery += "ANALYSIS_TOOL_VERSIONS WHERE ANALYSIS_TOOL_ID=? and ANALYSIS_TOOL_VERSIONS";
			else if (type.equals(DbConstants.INTERFACE_TYPE))
				getLastModifiedQuery += "INTERFACE_VERSIONS WHERE INTERFACE_ID=? and INTERFACE_VERSIONS";
			else if (type.equals(DbConstants.INTEGRATION_MODEL_TYPE))
				getLastModifiedQuery += "INTEGRATION_MODEL_VERSIONS WHERE MODEL_ID=? and INTEGRATION_MODEL_VERSIONS";
			else if (type.equals(DbConstants.AUX_FILE_TYPE))
				getLastModifiedQuery += "AUX_FILE_VERSIONS WHERE AUX_FILE_ID=? and AUX_FILE_VERSIONS";

			getLastModifiedQuery += ".FILE_EVENT_ID=FILE_EVENTS.EVENT_ID";

			PreparedStatement stmt = DbUtils.getPreparedStatement(getLastModifiedQuery);
			stmt.setString(1, id);
			Vector v = DbUtils.executeQuery(stmt, true);
			if (v.isEmpty())
				return v;
			Object o = v.get(0);
			if (o == null)
				return new Vector();
			return v;
		} catch (SQLException e) {
			System.out.println("FileSystemDbFunctions: " + e.toString());
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static void deleteModel(String modelId)
	        throws XmlRpcException
	{
		try {
			deleteInterfacesForParentId(modelId);
			PermissionDbFunctions.deleteAllObjectPermissions(modelId);
            deleteAuxFiles(modelId);
			String query;
			query = "delete from MODELS where ID = '" + modelId + "'";
			int modelDeleted = DbUtils.executeUpdate(query);
			if (modelDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_MODEL,
				                          DbErrors.XMLRPC_NO_SUCH_MODEL_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

    public static void deleteAnalysisTool(String analysisToolId)
            throws XmlRpcException
    {
        try
        {
            deleteInterfacesForParentId(analysisToolId);
            PermissionDbFunctions.deleteAllObjectPermissions(analysisToolId);

            PreparedStatement stmt = DbUtils.getPreparedStatement(getAnalysisToolProjectId);
            stmt.setString(1, analysisToolId);
            Vector v = DbUtils.executeQuery(stmt, false);
            String projectId = (String) ((Vector) v.get(0)).get(0);

            deleteProject(projectId);

            String query = "delete from ANALYSIS_TOOLS where ID = '" + analysisToolId + "'";
            int analysisToolDeleted = DbUtils.executeUpdate(query);
            if (analysisToolDeleted == 0)
                throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_MODEL,
                        DbErrors.XMLRPC_NO_SUCH_MODEL_MSG);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }
    }

	public static void deletePlayspace(String playspaceId)
	        throws XmlRpcException
	{
		PermissionDbFunctions.deleteAllObjectPermissions(playspaceId);
		try {
			String query;

			// update the interface versions table
			query = "delete from PLAYSPACES where ID = '" + playspaceId + "'";
			int modelDeleted = DbUtils.executeUpdate(query);
			if (modelDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PLAYSPACE,
				                          DbErrors.XMLRPC_NO_SUCH_PLAYSPACE_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static void deleteProject(String projectId) throws XmlRpcException
	{
		try {
			deleteIModels(getIModelIdsFromProject(projectId));
			deleteInterfacesForParentId(projectId);
			PermissionDbFunctions.deleteAllObjectPermissions(projectId);

            deleteAuxFiles(projectId);

			PreparedStatement stmt = DbUtils.getPreparedStatement(deleteProject);
			stmt.setString(1, projectId);
			int rowDeleted = DbUtils.executeUpdate(stmt);
			if (rowDeleted == 0) {
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in deleting project: " + projectId);
			}
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param ids iModel ids
	 */
	public static void deleteIModels(Vector ids) throws XmlRpcException
	{
		for (int i = 0; i < ids.size(); i++) {
			deleteIModel((String) ids.get(i));
		}
	}

	public static void deleteIModel(String imodelId) throws XmlRpcException
	{
		try {
			deleteInterfacesForParentId(imodelId);
			PermissionDbFunctions.deleteAllObjectPermissions(imodelId);
			PreparedStatement stmt = DbUtils.getPreparedStatement(deleteIModel);
			stmt.setString(1, imodelId);
			int rowDeleted = DbUtils.executeUpdate(stmt);
			if (rowDeleted == 0) {
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, "Error in deleting integration model: " + imodelId);
			}
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static void deleteInterfacesForParentId(String parentId) throws XmlRpcException
	{
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(deleteInterfacePermissionsForParentId);
			stmt.setString(1, parentId);
			DbUtils.executeUpdate(stmt);
			stmt = DbUtils.getPreparedStatement(deleteInterfacesForParentId);
			stmt.setString(1, parentId);
			DbUtils.executeUpdate(stmt); // hard to validate since some items may not have interfaces
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param projectId
	 * @return vector imodel ids for given project id
	 */
	public static Vector getIModelIdsFromProject(String projectId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getIModelIdsFromProject");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(getIModelIdsFromProject);
			stmt.setString(1, projectId);
			return DbUtils.executeQueryColumnToVector(stmt);
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param projectId
	 * @return vector of vectors of <imodel deploy id, imodel build id> for given project id
	 */
	public static Vector getIModelDeployAndBuildIdsForProject(String projectId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getIModelDeployAndBuildIdsForProject");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(getIModelDeployAndBuildIdsFromProject);
			stmt.setString(1, projectId);
			return DbUtils.executeQuery(stmt, false);
		}
		catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 *
	 * @param userId ID of user to get folders from
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserSpace(int userId, int requestorId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserSpace " + userId + "  " + type);
		try {
			// check for public folders
			Vector folderIdVec = getUserGroupFolders(userId, type);
			int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

			if (publicFolderId != DbConstants.NULL) // public folder exist
			{
				if (userId == requestorId) // the requestor is the user self
					return folderIdVec; // retrun both public and private folders
				else {
					folderIdVec.removeElementAt(1);
					return folderIdVec; // retrun only public private folder
				}
			}
			return new Vector();
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param groupId ID of group to get folders from
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getGroupSpace(int groupId, int requestorId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getGroupSpace");
		try {
			// check for public folders
			Vector folderIdVec = getUserGroupFolders(groupId, type);
			int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

			if (publicFolderId != DbConstants.NULL) // public folder exist
			{ // check whether the requestor is a member of the group
				Vector membersInfoVec = UserGroupDbFunctions.getMembersForGroup(groupId);

				for (int i = 0; i < membersInfoVec.size(); i++) {
					if (requestorId == ((Integer) ((Vector) membersInfoVec.get(i)).get(1)).intValue()) {
						return folderIdVec; // retrun both public and private folders
					}
				}
				// not a member
				folderIdVec.removeElementAt(1);
				return folderIdVec; // retrun only public private folder
			}
			return new Vector();
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param groupId ID of group to get folders from
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getGroupSpaceNoMembershipCheck(int groupId, int requestorId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getGroupSpace");
		try {
			// check for public folders
			Vector folderIdVec = getUserGroupFolders(groupId, type);
			int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

			if (publicFolderId != DbConstants.NULL) // public folder exist
			{
				return folderIdVec; // retrun both public and private folders

			}
			return new Vector();
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * get folders from the server scope
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getServerSpace(int requestorId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getServerSpace");
		try {
			// check for public folders
			int groupId = 1;    // administrators group
			return getGroupSpace(groupId, requestorId, type);
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * get folders from the server scope without membership check
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getServerSpaceNoMembershipCheck(int requestorId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getServerSpace");
		try {
			// check for public folders
			int groupId = 1;    // administrators group
			return getGroupSpaceNoMembershipCheck(groupId, requestorId, type);
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 *
	 * @param userOrGroup "U" or "G"
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing IDs and names of the users/groups who can save models/playspaces
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserGroupSpacesList(String userOrGroup, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserSpacesList");

		try {
			String query = "select ID, NAME from USERS_GROUPS where ";

			if (type.equals(DbConstants.MODEL_TYPE))
				query += " CAN_SAVE_MODEL";
			else if (type.equals(DbConstants.PLAYSPACE_TYPE))
				query += " CAN_SAVE_PLAYSPACE";
			query += "='true' and TYPE='" + userOrGroup + "'";
			return DbUtils.executeQuery(query, false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getInterfaces(String modelId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getInterfaces");

		try {
			String query;

			query = "select ID, NAME from INTERFACES where MODEL_ID='" + modelId + "'";
			return DbUtils.executeQuery(query, false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	public static Integer getUserIdFromSession(String sessisonId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserIdFromSession");

		try {
			String query;

			query = "select USER_ID from SESSIONS where ID='" + sessisonId + "'";
			Vector v = DbUtils.executeQuery(query, true);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_BAD_SESSION_ID,
				                          DbErrors.XMLRPC_BAD_SESSION_ID_MSG);
			}
			return (Integer) v.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static String getUserTypeFromSession(String sessisonId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserIdFromSession");

		try {
			String query;
			query = "select TYPE from SESSIONS where ID='" + sessisonId + "'";
			Vector v = DbUtils.executeQuery(query, true);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_BAD_SESSION_ID,
				                          DbErrors.XMLRPC_BAD_SESSION_ID_MSG);
			}
			return (String) v.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	public static boolean doesGroupContainMember(Integer groupId, Integer memberId) throws XmlRpcException
	{
		String query = "select MEMBER_ID from GROUP_MEMBERSHIP where GROUP_ID = '" + groupId + "'";
		try {
			Vector v = DbUtils.executeQueryColumnToVector(query);
			for (int i = 0; i < v.size(); i++) {
				if ((v.get(i)).equals(memberId)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
		return false;
	}

	public static Vector getPathForModel(String modelId, int requestorId, String loginType) throws XmlRpcException
	{
		try {
			String privateOrPublic = null;
			Vector filePath = new Vector();
			if (modelId == null) {
				System.out.println("Invalid modelId passed to FileSystemDbFunctions.getPathForModel");
				return null;
			}
			String query = "Select FOLDER_ID from MODELS where ID = '" + modelId + "'";
			Vector v = DbUtils.executeQuery(query, true);
			if (v.isEmpty())
				return DbConstants.EMPTY_VECTOR;

			int currentFolderId = ((Integer) v.elementAt(0)).intValue();

			if (currentFolderId == DbConstants.NULL)
				return DbConstants.EMPTY_VECTOR;

			Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '"
			                                 + currentFolderId + "'", true);

			while (currentFolderId != DbConstants.NULL) {
				filePath.add(0, new Integer(currentFolderId));
				currentFolderId = ((Integer) v2.elementAt(1)).intValue();
				if (currentFolderId != DbConstants.NULL) {
					v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '"
					                          + currentFolderId + "'", true);
				}
			}

			int parentFolderId = ((Integer) filePath.get(0)).intValue();
			if (((String) v2.elementAt(0)).endsWith("_public")) {
				privateOrPublic = "PUBLIC";
			}
			if (((String) v2.elementAt(0)).endsWith("_private")) {
				privateOrPublic = "PRIVATE";
			}

			Vector vFinal =
			        DbUtils.executeQuery("select TYPE, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_MODEL_FOLDER_ID='" + parentFolderId + "'", true);

			if (((Integer) vFinal.elementAt(1)).intValue() == requestorId)
				return filePath;
			else if (((Integer) vFinal.elementAt(1)).intValue() == DbConstants.ADMIN_GROUP_ID && loginType.equals(DbConstants.LOGIN_ADMIN))
				return filePath;
			else {
				filePath.add(0, vFinal.elementAt(1));
				return filePath;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

    public static Vector getPathForAnalysisTool(String analysisToolId, int requestorId, String loginType) throws XmlRpcException
    {
        try
        {
            String privateOrPublic = null;
            Vector filePath = new Vector();
            if (analysisToolId == null)
            {
                System.out.println("Invalid projectId passed to FileSystemDbFunctions.getPathForAnalysisTool");
            }
            String query = "select FOLDER_ID from ANALYSIS_TOOLS where ID = '" + analysisToolId + "'";
            Vector v = DbUtils.executeQuery(query, true);
            if (v.isEmpty())
                return DbConstants.EMPTY_VECTOR;

            int currentFolderId = ((Integer) v.elementAt(0)).intValue();

            if (currentFolderId == DbConstants.NULL)
                return DbConstants.EMPTY_VECTOR;

            Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '" +
                    currentFolderId + "'", true);
            while (currentFolderId != DbConstants.NULL)
            {
                filePath.add(0, new Integer(currentFolderId));
                currentFolderId = ((Integer) v2.elementAt(1)).intValue();
                if (currentFolderId != DbConstants.NULL)
                {
                    v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '"
                            + currentFolderId + "'", true);
                }
            }

            int parentFolderId = ((Integer) filePath.get(0)).intValue();
            if (((String) v2.elementAt(0)).endsWith("_public"))
            {
                privateOrPublic = "PUBLIC";
            }
            if (((String) v2.elementAt(0)).endsWith("_private"))
            {
                privateOrPublic = "PRIVATE";
            }

            Vector vFinal =
                    DbUtils.executeQuery("select TYPE, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_MODEL_FOLDER_ID='" + parentFolderId + "'", true);

            if (((Integer) vFinal.elementAt(1)).intValue() == requestorId)
                return filePath;
            else if (((Integer) vFinal.elementAt(1)).intValue() == DbConstants.ADMIN_GROUP_ID && loginType.equals(DbConstants.LOGIN_ADMIN))
                return filePath;
            else
            {
                filePath.add(0, vFinal.elementAt(1));
                return filePath;
            }

        }
        catch (SQLException e)
        {

            e.printStackTrace();
            throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
        }

    }

	public static Vector getPathForProject(String projectId, int requestorId, String loginType) throws XmlRpcException
	{
		try {
			String privateOrPublic = null;
			Vector filePath = new Vector();
			if (projectId == null) {
				System.out.println("Invalid projectId passed to FileSystemDbFunctions.getPathForProject");
				return DbConstants.EMPTY_VECTOR;
			}
			String query = "Select FOLDER_ID from PROJECTS where ID = '" + projectId + "'";
			Vector v = DbUtils.executeQuery(query, true);
			if (v.isEmpty())
				return DbConstants.EMPTY_VECTOR;

			int currentFolderId = ((Integer) v.elementAt(0)).intValue();

			if (currentFolderId == DbConstants.NULL)
				return DbConstants.EMPTY_VECTOR;

			Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '"
			                                 + currentFolderId + "'", true);

			while (currentFolderId != DbConstants.NULL) {
				filePath.add(0, new Integer(currentFolderId));
				currentFolderId = ((Integer) v2.elementAt(1)).intValue();
				if (currentFolderId != DbConstants.NULL) {
					v2 = DbUtils.executeQuery("select NAME, PARENT_ID from MODEL_FOLDERS where ID = '"
					                          + currentFolderId + "'", true);
				}
			}

			int parentFolderId = ((Integer) filePath.get(0)).intValue();
			if (((String) v2.elementAt(0)).endsWith("_public")) {
				privateOrPublic = "PUBLIC";
			}
			if (((String) v2.elementAt(0)).endsWith("_private")) {
				privateOrPublic = "PRIVATE";
			}

			Vector vFinal =
			        DbUtils.executeQuery("select TYPE, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_MODEL_FOLDER_ID='" + parentFolderId + "'", true);

			if (((Integer) vFinal.elementAt(1)).intValue() == requestorId)
				return filePath;
			else if (((Integer) vFinal.elementAt(1)).intValue() == DbConstants.ADMIN_GROUP_ID && loginType.equals(DbConstants.LOGIN_ADMIN))
				return filePath;
			else {
				filePath.add(0, vFinal.elementAt(1));
				return filePath;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getPathForPlayspace(String playspaceId, int requestorId, String loginType) throws XmlRpcException
	{
		try {
			String privateOrPublic = null;
			Vector filePath = new Vector();
			if (playspaceId == null) {
				System.out.println("Invalid playspaceId passed to FileSystemDbFunctions.getPathForPlayspace");
				return null;
			}
			String query = "Select FOLDER_ID from PLAYSPACES where ID = '" + playspaceId + "'";
			Vector v = DbUtils.executeQuery(query, true);
			if (v.isEmpty())
				return DbConstants.EMPTY_VECTOR;

			int currentFolderId = ((Integer) v.elementAt(0)).intValue();

			if (currentFolderId == DbConstants.NULL)
				return DbConstants.EMPTY_VECTOR;

			Vector v2 = DbUtils.executeQuery("select NAME, PARENT_ID from PLAYSPACE_FOLDERS where ID = '"
			                                 + currentFolderId + "'", true);

			while (currentFolderId != DbConstants.NULL) {
				filePath.add(0, new Integer(currentFolderId));
				currentFolderId = ((Integer) v2.elementAt(1)).intValue();
				if (currentFolderId != DbConstants.NULL) {
					v2 = DbUtils.executeQuery("select NAME, PARENT_ID from PLAYSPACE_FOLDERS where ID = '"
					                          + currentFolderId + "'", true);
				}
			}

			int parentFolderId = ((Integer) filePath.get(0)).intValue();
			if (((String) v2.elementAt(0)).endsWith("_public")) {
				privateOrPublic = "PUBLIC";
			}
			if (((String) v2.elementAt(0)).endsWith("_private")) {
				privateOrPublic = "PRIVATE";
			}

			Vector vFinal =
			        DbUtils.executeQuery("select TYPE, ID from users_groups, user_group_folders where USER_GROUP_ID=ID and " + privateOrPublic + "_PLAYSPACE_FOLDER_ID='" + parentFolderId + "'", true);

			if (((Integer) vFinal.elementAt(1)).intValue() == requestorId)
				return filePath;
			else if (((Integer) vFinal.elementAt(1)).intValue() == DbConstants.ADMIN_GROUP_ID && loginType.equals(DbConstants.LOGIN_ADMIN))
				return filePath;
			else {
				filePath.add(0, vFinal.elementAt(1));
				return filePath;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


    public static void deleteAuxFiles(String projectId) throws XmlRpcException
	{

		try {
            String query="delete from AUX_FILES where MODEL_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, projectId);
			int rowDeleted = DbUtils.executeUpdate(stmt);

		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/*
	public static String getObjectDescription(String objectId, int objectType)
	        throws XmlRpcException
	{
	    Debug.trace(Debug.ALL, "In FileSystemDbFunctions:getObjectDescription()");

	    int noSuchError = 0;
	    String noSuchErrorMessage = null;
	    String objectTableName = null;
	    String objectVersionTableName = null;

	    switch (objectType)
	    {
	    case DbConstants.PLAYSPACE:
	        objectTableName = "PLAYSPACES";
	        objectVersionTableName = "PLAYSPACE_VERSIONS";
	        noSuchError = DbErrors.XMLRPC_NO_SUCH_PLAYSPACE;
	        noSuchErrorMessage = DbErrors.XMLRPC_NO_SUCH_PLAYSPACE_MSG;
	        break;
	    case DbConstants.MODEL:
	        objectTableName = "MODELS";
	        objectVersionTableName = "MODEL_VERSIONS";
	        noSuchError = DbErrors.XMLRPC_NO_SUCH_MODEL;
	        noSuchErrorMessage = DbErrors.XMLRPC_NO_SUCH_MODEL_MSG;
	        break;
	    case DbConstants.INTERFACE:
	        objectTableName = "INTERFACES";
	        objectVersionTableName = "INTERFACE_VERSIONS";
	        noSuchError = DbErrors.XMLRPC_NO_SUCH_INTERFACE;
	        noSuchErrorMessage = DbErrors.XMLRPC_NO_SUCH_INTERFACE_MSG;
	        break;
	    case DbConstants.PROJECT:
	        objectTableName = "PROJECTS";
	        objectVersionTableName = "PROJECT_VERSIONS";
	        noSuchError = DbErrors.XMLRPC_NO_SUCH_PLAYSPACE;
	        noSuchErrorMessage = DbErrors.XMLRPC_NO_SUCH_PROJECT_MSG;
	        break;
	    case DbConstants.INTEGRATION_MODEL:
	        objectTableName = "INTEGRATION_MODELS";
	        objectVersionTableName = "INTEGRATION_MODELS_VERSIONS";
	        noSuchError = DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL;
	        noSuchErrorMessage = DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL_MSG;
	    }


	    try {
	        Statement stmt = DbUtils.getStatement();
	        String query;
	        ResultSet r;
	        int versionId;

	        // see if primary project record exists
	        query = "select ID, LAST_VERSION_ID from " + objectTableName + " where ID='" + objectId + "'";
	        r = stmt.executeQuery(query);

	        // bail out if no project record exists
	        if (!r.next()) {
	            stmt.close();
	            throw new XmlRpcException (noSuchError, noSuchErrorMessage);
	        }
	        else {
	            versionId= r.getInt ("VERSION_ID");
	        }

	        // see if the interface version already exists
	        query = "select CONTENT from " + objectVersionTableName + " where ID='" + versionId + "'";
	        r = stmt.executeQuery(query);
	        if (!r.next()) {
	            stmt.close();
	            throw new XmlRpcException (noSuchError, noSuchErrorMessage);
	        }
	        else {
	            String xmlContent = r.getString("CONTENT");
	            stmt.close ();
	            return xmlContent;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
	    }
	}

	public static Vector getIsUserMember(String sessisonId) throws XmlRpcException {
	Debug.trace(Debug.ALL, "getUserIdFromSession");

	try {
	String query;

	query = "select USER_ID from SESSIONS where ID='" + sessisonId + "'";
	Vector v = DbUtils.executeQuery(query, true);
	if (v.isEmpty()) {
	throw new XmlRpcException(DbConstants.XMLRPC_BAD_SESSION_ID,
	DbConstants.XMLRPC_BAD_SESSION_ID_MSG);
	}
	return (Integer) v.get(0);
	} catch (SQLException e) {
	e.printStackTrace();
	throw new XmlRpcException(DbConstants.XMLRPC_DB_ERROR, e.getMessage());
	}
	}
	*/
}
