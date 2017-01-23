// FileEventDbFunction.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import org.apache.xmlrpc.XmlRpcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

/**
 *
 */
public class FileEventDbFunction
{
	public final static String DOWNLOAD = "DOWNLOAD";
	public final static String UPLOAD = "UPLOAD";


	//create file event

	public static int createDownloadFileEvent(String sessionID) throws XmlRpcException
	{
		return createFileEvent(DOWNLOAD, sessionID);
	}


	public static int createUploadFileEvent(String sessionID) throws XmlRpcException
	{
		return createFileEvent(UPLOAD, sessionID);
	}

	public static int createFileEvent(String type, String sessionID) throws XmlRpcException
	{
		//EVENT_ID int identity, EVENT_TYPE varchar not null, SESSION_ID varchar not null, DATE timestamp default 'now' not null
		Debug.trace(Debug.ALL, "createFileEvent");
		try {
			String query = "insert into FILE_EVENTS (EVENT_TYPE,SESSION_ID) values (?,?)";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, type);
			stmt.setString(2, sessionID);
			return DbUtils.executeInsert(stmt, true);
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

	}

	//log file download event
	//table FILE_DOWNLOADS (EVENT_ID int identity, FILE_ID varchar not null, VERSION_ID int not null)"
	public static void logFileDownload(int eventID, String fileID, int versionID) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "logFileDownload");
		try {
			//should check eventID first
			String query = "select * from FILE_EVENTS where EVENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setInt(1, eventID);
			Vector v = DbUtils.executeQuery(stmt, true);
			if (v.isEmpty()) {
				System.err.println("invalid event id");
				return;
			}
			query = "insert into FILE_DOWNLOADS (eventID,fileID,versionID) values (?,?,?)";
			stmt = DbUtils.getPreparedStatement(query);
			stmt.setInt(1, eventID);
			stmt.setString(2, fileID);
			stmt.setInt(1, versionID);
			DbUtils.executeInsert(stmt, false);
		} catch (SQLException e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

	}

	//do delete
	//delete model,
	public static void deleteModel(String modelId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "In FileEventDbFunctions:deleteModel");
		try {

			// delete interface first
			String query = "delete from INTERFACES where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, modelId);
			DbUtils.executeUpdate(stmt);

			query = "delete from MODELS where ID=?";
			stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, modelId);
			int modelDeleted = DbUtils.executeUpdate(stmt);
			if (modelDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_MODEL,
				                          DbErrors.XMLRPC_NO_SUCH_MODEL_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	//delete playspace
	public static void deletePlayspace(String playspaceId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "In FileEventDbFunctions:deletePlayspace");
		try {
			String query = "delete from PLAYSPACES where ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, playspaceId);
			int playspaceDeleted = DbUtils.executeUpdate(stmt);
			if (playspaceDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PLAYSPACE,
				                          DbErrors.XMLRPC_NO_SUCH_PLAYSPACE_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	//delete project
	public static void deleteProject(String projectId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "In FileEventDbFunctions:deleteProject");
		try {
			//delete interface in that project
			String query = "delete from INTERFACES where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, projectId);
			DbUtils.executeUpdate(stmt);

			//delete interface in the Integration model
			query = "select ID from INTEGRATION_MODELS where PROJECT_ID=?";
			stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, projectId);
			Vector IntegrationModelIDs = DbUtils.executeQuery(stmt, true);
			if (IntegrationModelIDs.size() != 0) {
				query = "delete from INTERFACES where PARENT_ID=?";
				stmt = DbUtils.getPreparedStatement(query);
				for (int i = 0; i < IntegrationModelIDs.size(); i++) {
					String id = (String) IntegrationModelIDs.get(i);
					stmt.setString(1, id);
					DbUtils.executeUpdate(stmt);
				}

				//delete integration model in that project
				query = "delete from INTEGRATION_MODELS where PROJECT_ID=?";
				stmt = DbUtils.getPreparedStatement(query);
				stmt.setString(1, projectId);
				DbUtils.executeUpdate(stmt);
			}

			//delete integration model in that project
			query = "delete from PROJECTS where ID=?";
			stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, projectId);
			int playspaceDeleted = DbUtils.executeUpdate(stmt);
			if (playspaceDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_PLAYSPACE,
				                          DbErrors.XMLRPC_NO_SUCH_PLAYSPACE_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

//delete IntegrationModel
	public static void deleteIntegrationModel(String integrationModelId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "In FileEventDbFunctions:deleteIntegrationModel");
		try {
			// delete interface first
			String query = "delete from INTERFACES where PARENT_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, integrationModelId);
			DbUtils.executeUpdate(stmt);

			query = "delete from INTEGRATION_MODELS where ID=?";
			stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, integrationModelId);
			int rowDeleted = DbUtils.executeUpdate(stmt);
			if (rowDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL,
				                          DbErrors.XMLRPC_NO_SUCH_INTEGRATION_MODEL_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

}
