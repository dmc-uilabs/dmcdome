// PermissionUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.permission;

import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;

import java.util.Vector;
import java.util.List;
import java.util.HashMap;

/**
 *  Functions to work with permissions data
 */
public class PermissionUtils
{

	// permission categories
	// strings are in the database. do not change without changing the database!
	public static final String MODEL_IPROJECT_EDIT_PRIVILEGES = "MODEL OR iPROJECT EDITING PRIVILEGES";
    public static final String ANALYSIS_TOOL_EDIT_PRIVILEGES = "ANALYSIS TOOL EDITING PRIVILEGES";
	public static final String MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES = "INTERFACE USE PRIVILEGES";
	public static final String PLAYSPACE_EDIT_PRIVILEGES = "PLAYSPACE EDITING PRIVILEGES";
	public static final String PLAYSPACE_USE_PRIVILEGES = "PLAYSPACE USE PRIVILEGES";
	public static final String PROJECT_VISIBILITY_PRIVILEGES = "PROJECT VISIBILITY PRIVILEGES";

	public static final String PERMISSION_TO_SET_MODEL_EDIT_PRIV = "Set model or iProject edit permissions";
    public static final String PERMISSION_TO_SET_ANALYSIS_TOOL_EDIT_PRIV = "set analysis tool edit permissions";
	public static final String PERMISSION_TO_SET_INTERFACE_USE_PRIV = "Set interface use privilege";
	public static final String PERMISSION_TO_SET_PLAYSPACE_EDIT_PRIV = "Set playspace editing permissions";
	public static final String PERMISSION_TO_SET_PLAYSPACE_USE_PRIV = "Set playspace use privilege";
	public static final String PERMISSION_TO_SUBSCRIBE_TO_INTERFACE = "Subscribe to interface";

	public static final String MODEL_MODIFY_PRIVILEGES = "Modify model or iProject";
	public static final String MODEL_COPY_PRIVILEGES = "Copy model or iProject";
	public static final String PLAYSPACE_MODIFY_PRIVILEGES = "Modify playspace";
	public static final String PLAYSPACE_COPY_PRIVILEGES = "Copy playspace";
	public static final String PLAYSPACE_CHANGE_PRIVILEGES = "Change values";
	public static final String PERMISSION_TO_VIEW_INTERFACE = "View and run in a new playspace";

	public static final String PERMISSION_TO_SET_PROJECT_VISIBILITY_PRIVS = "May set iProject content visibility permissions";
	public static final String PERMISSION_TO_SET_IMODEL_PRIVS = "May set interface use permissions for iModels in project";
	public static final String PERMISSION_TO_SEE_CONTENTS_SUBSCRIBE = "May see contents while subscribing";
	public static final String PERMISSION_TO_SEE_CONTENTS_RUN_MODE = "May see contents while in run mode";
	public static final String PERMISSION_TO_SEE_CONTENTS_RUN_IN_NEW_PLAYSPACE = "May see contents and run iProject in new playspace";
	public static final String PERMISSION_TO_SEE_CONTENTS_SAVE_IN_NEW_PLAYSPACE = "May see contents and save iProject in new playspace";

	private static HashMap info = new HashMap();

	/**
	 * This method is used to get permission category information.
	 * It caches the information so that calls for the same permission information on the same server
	 * are fulfilled from the cache instead of requiring communication with the server.
	 * @param conn
	 * @param permissionCategory
	 * @return array of two vectors. First is vector of permissions. Second is vector of permission dependencies.
	 * returns null if information was unavailable.
	 */
	public static Vector getCategoryInfo(ServerConnection conn, String permissionCategory)
	{
		if (conn == null || permissionCategory == null)
			throw new IllegalArgumentException("PermissionUtils.getCategoryInfo - null parameters");
		HashMap serverCategories = (HashMap) info.get(conn.getServerPort());
		if (serverCategories == null) {
			serverCategories = new HashMap();
			info.put(conn.getServerPort(), serverCategories);
		}
		Vector categoryInfo = (Vector) serverCategories.get(permissionCategory);
		if (categoryInfo == null) { // get information from server and cache it
			categoryInfo = PermissionFunctions.getPermissionCategoryInfo(conn, permissionCategory);
		}
		return categoryInfo;
	}

	// function to convert from permission information to useable information
	public static DArrayList convertToUserGroupInfo(Vector permissionTypes, Vector permissions, Vector usersAndGroups)
	{
		DArrayList allUserGroupInfo = new DArrayList();
		UserGroupInfo singleUserGroup;
		int[] permissionID = new int[permissionTypes.size()];
		String[] permissionNames = new String[permissionTypes.size()];
		boolean[] permissionValues = new boolean[permissionTypes.size()];
		int userIndex;
		int permID;

		for (int i = 0; i < permissionTypes.size(); i++) {
			permissionID[i] = ((Integer) ((Vector) permissionTypes.elementAt(i)).elementAt(0)).intValue();
			permissionNames[i] = ((Vector) permissionTypes.elementAt(i)).elementAt(1).toString();
			permissionValues[i] = false;
		}

		for (int i = 0; i < usersAndGroups.size(); i++) {
			boolean isGroup;
			if (((Vector) usersAndGroups.elementAt(i)).elementAt(0).equals("G")) {
				isGroup = true;
			} else {
				isGroup = false;
			}
			singleUserGroup = new UserGroupInfo(isGroup, ((Integer) ((Vector) usersAndGroups.elementAt(i)).elementAt(1)).intValue(),
			                                    ((Vector) usersAndGroups.elementAt(i)).elementAt(2).toString(), permissionID, permissionNames, permissionValues);
			allUserGroupInfo.add(singleUserGroup);
		}


		for (int i = 0; i < permissions.size(); i++) {
			userIndex = getUserGroup(allUserGroupInfo, ((Integer) ((Vector) permissions.elementAt(i)).elementAt(0)).intValue());
			permID = ((Integer) ((Vector) permissions.elementAt(i)).elementAt(1)).intValue();
			if (userIndex != -1) {
				int[] temp = ((UserGroupInfo) allUserGroupInfo.get(userIndex)).getPermissionID();
				for (int j = 0; j < temp.length; j++) {
					if (temp[j] == permID) {
						((UserGroupInfo) allUserGroupInfo.get(userIndex)).getPermissions()[j] = true;
					}
				}
			}
		}

		return allUserGroupInfo;
	}

	public static DArrayList convertToUserGroupInfo(Vector permissionTypes, Vector usersAndGroups)
	{
		DArrayList allUserGroupInfo = new DArrayList();
		UserGroupInfo singleUserGroup;
		int[] permissionID = new int[permissionTypes.size()];
		String[] permissionNames = new String[permissionTypes.size()];
		boolean[] permissionValues = new boolean[permissionTypes.size()];

		for (int i = 0; i < permissionTypes.size(); i++) {
			permissionID[i] = ((Integer) ((Vector) permissionTypes.elementAt(i)).elementAt(0)).intValue();
			permissionNames[i] = ((Vector) permissionTypes.elementAt(i)).elementAt(1).toString();
			permissionValues[i] = false;
		}

		for (int i = 0; i < usersAndGroups.size(); i++) {
			boolean isGroup;
			if (((Vector) usersAndGroups.elementAt(i)).elementAt(0).equals("G")) {
				isGroup = true;
			} else {
				isGroup = false;
			}
			singleUserGroup = new UserGroupInfo(isGroup, ((Integer) ((Vector) usersAndGroups.elementAt(i)).elementAt(1)).intValue(),
			                                    ((Vector) usersAndGroups.elementAt(i)).elementAt(2).toString(), permissionID, permissionNames, permissionValues);
			allUserGroupInfo.add(singleUserGroup);
		}


		return allUserGroupInfo;
	}

	public static int getUserGroup(List allUserGroup, String UGName)
	{
		int whichUG = -1;
		for (int i = 0; i < allUserGroup.size(); i++) {
			if (((UserGroupInfo) allUserGroup.get(i)).getName() == UGName) {
				whichUG = i;
			}
		}

		return whichUG;
	}

	public static int getUserGroup(List allUserGroup, int UGID)
	{
		int whichUG = -1;
		for (int i = 0; i < allUserGroup.size(); i++) {
			if (((UserGroupInfo) allUserGroup.get(i)).getId() == UGID) {
				whichUG = i;
			}
		}

		return whichUG;
	}

	public static int getArrayIndex(int[] myArray, int elementValue)
	{
		int whichUG = -1;
		for (int i = 0; i < myArray.length; i++) {
			if (myArray[i] == elementValue) {
				whichUG = i;
			}
		}

		return whichUG;
	}

}
