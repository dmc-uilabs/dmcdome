// PermissionFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.util.Vector;

/**
 * set of functions for manipulating permissions for objects on the server
 */
public class PermissionFunctions
{

	/**
	 * Returns information about a specified permission category.
	 * @param categoryName a permission category
	 * @return a Vector of two Vectors. The first vector has permission type information for the category (permissionTypeId, permissionTypeName).
	 *    The second vector has permission dependency information for the category (permissionTypeId, dependentPermissionTypeId).
	 *    Vectors are empty if there is no data of that type for this category.
	 * throws exception if categoryName is invalid
	 */
	public static Vector getPermissionCategoryInfo(ServerConnection svrConn, String categoryName)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.GET_PERMISSION_CATEGORY_INFO,
		                                Vectors.create(categoryName));
	}

	/**
	 * Returns permission information about a particular object in a particular permission category
	 * @param objectId
	 * @param categoryName
	 * @return a Vector of two Vectors. The first vector has user/group information (userGroupType, userId, userName).
	 *    The second vector has permission information (userId,permissionId).
	 *    Vectors are empty if there is no data of that type for this category.
	 * throws exception if categoryName is invalid
	 */
	public static Vector getObjectPermissionInfo(ServerConnection svrConn, String objectId, String categoryName)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.GET_OBJECT_PERMISSION_INFO,
		                                Vectors.create(objectId, categoryName));
	}

	/**
	 * Sets permissions in specified category for an object to be the given set of permissions.
	 * @param objectId id of object
	 * @param categoryName name of permission category
	 * @param permissions vector of users and permissions (userId,permissionId)
	 */
	public static void setObjectPermissions(ServerConnection svrConn, String objectId, String categoryName, Vector permissions)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.SET_OBJECT_PERMISSIONS,
		                Vectors.create(objectId, categoryName, permissions));
	}

	public static boolean sessionUserHasPermission(ServerConnection svrConn, String modelObjectId, String permissionName)
	{
		return ((Boolean) svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.SESSION_USER_HAS_PERMISSION,
		                                  Vectors.create(svrConn.getConnectionId(), modelObjectId, permissionName))).booleanValue();
	}

	public static boolean userHasPermission(ServerConnection svrConn, int userId, String modelObjectId, String permissionName)
	{
		return ((Boolean) svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.USER_HAS_PERMISSION,
		                                  Vectors.create(new Integer(userId), modelObjectId, permissionName))).booleanValue();
	}

	public static Vector getPlayspaceMembers(ServerConnection svrConn, String playspaceId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.GET_PLAYSPACE_MEMBER,
		                                Vectors.create(playspaceId));
	}

	public static Vector getPlayspaceUserMembers(ServerConnection svrConn, String playspaceId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_PERMISSION + "." + DbConstants.GET_PLAYSPACE_USER_MEMBER,
		                                Vectors.create(playspaceId));
	}

}

