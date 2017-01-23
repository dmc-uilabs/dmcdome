// FileSystemFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;

import java.util.Vector;

/**
 * set of functions for manipulating the virtual file system on the server
 */
public class BrowseFileSystemFunctions extends FileSystemFunctions
{
	// todo: convert vector results to usable data structures


/*	public static Vector getUserModelSpace(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_SPACE,
		        Vectors.create(svrConn.getConnectionId(), new Integer(userId),  "MODEL"));
	}

	public static Vector getUserPlayspaceSpace(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_SPACE,
		        Vectors.create(svrConn.getConnectionId(), new Integer(userId),  "PLAYSPACE"));
	}
*/
	public static Vector getUserModelSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_SPACE_FOR_SESSION,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.MODEL_TYPE)); //
	}

	public static Vector getUserPlayspaceSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_SPACE_FOR_SESSION,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.PLAYSPACE_TYPE)); //
	}

/*	public static Vector getGroupModelSpace(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_GROUP_SPACE,
		        Vectors.create(svrConn.getConnectionId(), new Integer(groupId),  "MODEL"));
	}

	public static Vector getGroupPlayspaceSpace(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_GROUP_SPACE,
		        Vectors.create(svrConn.getConnectionId(), new Integer(groupId),  "PLAYSPACE"));
	}
*/
	public static Vector getServerModelSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_SERVER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.MODEL_TYPE)); //
	}

	public static Vector getServerPlayspaceSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_SERVER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), DbConstants.PLAYSPACE_TYPE)); //
	}

	public static Vector getUserModelSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("U", DbConstants.MODEL_TYPE)); //
	}

	public static Vector getUserPlayspaceSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("U", DbConstants.PLAYSPACE_TYPE)); //
	}

	public static Vector getGroupModelSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("G", DbConstants.MODEL_TYPE)); //
	}

	public static Vector getGroupPlayspaceSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("G", DbConstants.PLAYSPACE_TYPE)); //
	}

	/**
	 * get model or playspace folders for a user or group
	 * @param id User or group id
	 * @return
	 */
	public static Vector getUserGroupModelFolders(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_GROUP_FOLDERS,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(id), DbConstants.MODEL_FOLDER_TYPE));
	}

	/**
	 * get model or playspace folders for a user or group
	 * @param id User or group id
	 * @return
	 */
	public static Vector getUserGroupPlayspaceFolders(ServerConnection svrConn, int id)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM + "." + DbConstants.GET_USER_GROUP_FOLDERS,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(id), DbConstants.PLAYSPACE_FOLDER_TYPE));
	}

}
