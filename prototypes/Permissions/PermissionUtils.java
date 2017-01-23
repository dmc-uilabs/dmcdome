// PermissionUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package Permissions;

import java.util.Vector;

/**
 * Utility functions for converting from database format to user-friendly format
 */
public class PermissionUtils
{

	// function to convert from permission information to useable information
	public static Vector converttoUserGroupInfo(Vector permissionTypes, Vector permissions, Vector usersAndGroups)
	{
		Vector allUserGroupInfo = new Vector();
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
			}
			else {
				isGroup = false;
			}
			singleUserGroup = new UserGroupInfo(isGroup, ((Integer) ((Vector) usersAndGroups.elementAt(i)).elementAt(1)).intValue(),
			        ((Vector) usersAndGroups.elementAt(i)).elementAt(2).toString(), permissionID, permissionNames, permissionValues);
			allUserGroupInfo.addElement(singleUserGroup);
		}


		for (int i = 0; i < permissions.size(); i++) {
			userIndex = getUserGroup(allUserGroupInfo, ((Integer) ((Vector) permissions.elementAt(i)).elementAt(0)).intValue());
			permID = ((Integer) ((Vector) permissions.elementAt(i)).elementAt(1)).intValue();
			if (userIndex != -1) {
				int[] temp = ((UserGroupInfo) allUserGroupInfo.elementAt(userIndex)).getPermissionID();
				for (int j = 0; j < temp.length; j++) {
					if (temp[j] == permID) {
						((UserGroupInfo) allUserGroupInfo.elementAt(userIndex)).getPermissions()[j] = true;
					}
				}
			}
		}

		return allUserGroupInfo;
	}

	public static Vector converttoUserGroupInfo(Vector permissionTypes, Vector usersAndGroups)
	{
		Vector allUserGroupInfo = new Vector();
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
			}
			else {
				isGroup = false;
			}
			singleUserGroup = new UserGroupInfo(isGroup, ((Integer) ((Vector) usersAndGroups.elementAt(i)).elementAt(1)).intValue(),
			        ((Vector) usersAndGroups.elementAt(i)).elementAt(2).toString(), permissionID, permissionNames, permissionValues);
			allUserGroupInfo.addElement(singleUserGroup);
		}


		return allUserGroupInfo;
	}

	public static int getUserGroup(Vector AllUserGroup, String UGName)
	{
		int whichUG = -1;
		for (int i = 0; i < AllUserGroup.size(); i++) {
			if (((UserGroupInfo) AllUserGroup.elementAt(i)).getName() == UGName) {
				whichUG = i;
			}
		}

		return whichUG;
	}

	public static int getUserGroup(Vector AllUserGroup, int UGID)
	{
		int whichUG = -1;
		for (int i = 0; i < AllUserGroup.size(); i++) {
			if (((UserGroupInfo) AllUserGroup.elementAt(i)).getId() == UGID) {
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
