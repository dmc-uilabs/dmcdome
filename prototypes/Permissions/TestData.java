// TestData.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package Permissions;

import mit.cadlab.dome.util.DArrayList;
import mit.cadlab.dome.swing.DList;
import mit.cadlab.dome.swing.DListModel;

import java.util.Arrays;
import java.util.Vector;

public class TestData
{
	private static Object[][] modelEditPermissionTypesData = new Object[][]{
		// permissionId, permissionName
		new Object[]{new Integer(1), "set model/iProject edit permissions"},
		new Object[]{new Integer(3), "delete model/iProject"},
		new Object[]{new Integer(8), "modify model"},
		new Object[]{new Integer(5), "copy model"},
		new Object[]{new Integer(7), "set interface use permissions"},
	};

	public static Vector modelEditPermissionTypes = convertArrayToVector(modelEditPermissionTypesData);

	private static Object[][] modelEditPermissionsDependenciesData = new Object[][]{
		// permissionId, permissionId
		new Object[]{new Integer(8), new Integer(5)},
	};

	public static Vector modelEditPermissionsDependencies = convertArrayToVector(modelEditPermissionsDependenciesData);

	private static Object[][] modelEditPermissionsData = new Object[][]{
		// userId, permissionId
		new Object[]{new Integer(1), new Integer(1)},
		new Object[]{new Integer(1), new Integer(3)},
		new Object[]{new Integer(1), new Integer(8)},
		new Object[]{new Integer(1), new Integer(5)},
		new Object[]{new Integer(1), new Integer(7)},
		new Object[]{new Integer(3), new Integer(8)},
		new Object[]{new Integer(3), new Integer(5)},
		new Object[]{new Integer(5), new Integer(5)},
		new Object[]{new Integer(9), new Integer(1)},
		new Object[]{new Integer(9), new Integer(7)},
	};

	public static Vector modelEditPermissions = convertArrayToVector(modelEditPermissionsData);

	private static Object[][] userGroupWithPermissionsData = new Object[][]{
		// isGroup, id, name
/*		new Object[]{new Boolean(false), new Integer(1), "Mary"},
		new Object[]{new Boolean(false), new Integer(3), "Jack"},
		new Object[]{new Boolean(false), new Integer(5), "Keiko"},
		new Object[]{new Boolean(true), new Integer(7), "Coders"},
		new Object[]{new Boolean(true), new Integer(9), "Everyone"},
*/
		new Object[]{new String("U"), new Integer(1), "Mary"},
		new Object[]{new String("U"), new Integer(3), "Jack"},
		new Object[]{new String("U"), new Integer(5), "Keiko"},
		new Object[]{new String("G"), new Integer(7), "Coders"},
		new Object[]{new String("G"), new Integer(9), "Everyone"},

	};

	public static Vector userGroupWithPermissions = convertArrayToVector(userGroupWithPermissionsData);

	private static Object[][] allUserGroupData = new Object[][] {
		// isGroup, id, name
/*		new Object[]{new Boolean(false), new Integer(1), "Mary"},
		new Object[]{new Boolean(false), new Integer(12), "Dick"},
		new Object[]{new Boolean(false), new Integer(3), "Jack"},
		new Object[]{new Boolean(false), new Integer(14), "Jill"},
		new Object[]{new Boolean(false), new Integer(5), "Keiko"},
		new Object[]{new Boolean(true), new Integer(16), "Administrators"},
		new Object[]{new Boolean(true), new Integer(7), "Coders"},
		new Object[]{new Boolean(true), new Integer(18), "Students"},
		new Object[]{new Boolean(true), new Integer(9), "Everyone"},
*/
		new Object[]{new String("U"), new Integer(1), "Mary"},
		new Object[]{new String("U"), new Integer(12), "Dick"},
		new Object[]{new String("U"), new Integer(3), "Jack"},
		new Object[]{new String("U"), new Integer(14), "Jill"},
		new Object[]{new String("U"), new Integer(5), "Keiko"},
		new Object[]{new String("G"), new Integer(16), "Administrators"},
		new Object[]{new String("G"), new Integer(7), "Coders"},
		new Object[]{new String("G"), new Integer(18), "Students"},
		new Object[]{new String("G"), new Integer(9), "Everyone"},

	};

	public static Vector allUserGroup = convertArrayToVector(allUserGroupData);

	public static Vector convertArrayToVector(Object[][] arr)
	{
		Vector v = new Vector();
		for (int i = 0; i < arr.length; i++) {
			v.add(new Vector(Arrays.asList(arr[i])));
		}
		return v;
	}

    DArrayList aaa;
	DList bbb;
	DListModel ccc;

	public static void main(String[] args)
	{
		Object answer = PermissionsPanel.showPanel(null, modelEditPermissionTypes,
		        modelEditPermissions,allUserGroup, modelEditPermissionsDependencies);
		if (answer==null)
			System.out.println("cancelled");
		else
	    	System.out.println(answer);
		System.exit(0);
	}
}
