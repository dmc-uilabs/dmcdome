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
public class DeployFileSystemFunctions extends FileSystemFunctions
{
	// todo: convert vector results to usable data structures

	public static Vector getUserModelSpace(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(userId), "MODEL"));
	}


	public static Vector getUserPlayspaceSpace(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(userId), "PLAYSPACE"));
	}

	public static Vector getUserModelSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_SPACE_FOR_SESSION,
		                                Vectors.create(svrConn.getConnectionId(), "MODEL"));
	}

	public static Vector getUserPlayspaceSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_PLAYSPACE_FOR_SESSION,
		                                Vectors.create(svrConn.getConnectionId(), "PLAYSPACE"));
	}

	public static Vector getGroupModelSpace(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_GROUP_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), "MODEL"));
	}

	public static Vector getGroupPlayspaceSpace(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_GROUP_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), "PLAYSPACE"));
	}

	public static Vector getServerModelSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_SERVER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), "MODEL"));
	}

	public static Vector getServerPlayspaceSpace(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_SERVER_SPACE,
		                                Vectors.create(svrConn.getConnectionId(), "PLAYSPACE"));
	}

	public static Vector getUserModelSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("U", svrConn.getConnectionId(), "MODEL"));
	}

	public static Vector getUserPlayspaceSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("U", svrConn.getConnectionId(), "PLAYSPACE"));
	}

	public static Vector getGroupModelSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("G", svrConn.getConnectionId(), "MODEL"));
	}

	public static Vector getGroupPlayspaceSpacesList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_USER_GROUP_SPACES_LIST,
		                                Vectors.create("G", svrConn.getConnectionId(), "PLAYSPACE"));
	}

	public static Vector getGroupModelSpaceNoMembershipCheck(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_GROUP_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), "MODEL"));
	}

	public static Vector getGroupPlayspaceSpaceNoMembershipCheck(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_GROUP_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), new Integer(groupId), "PLAYSPACE"));
	}

	public static Vector getServerModelSpaceNoMembershipCheck(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_SERVER_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), "MODEL"));
	}

	public static Vector getServerPlayspaceSpaceNoMembershipCheck(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM + "." + DbConstants.GET_SERVER_SPACE_NO_MEMBERSHIP_CHECK,
		                                Vectors.create(svrConn.getConnectionId(), "PLAYSPACE"));
	}

}
