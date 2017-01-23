package Permissions;

import mit.cadlab.dome.gui.servermode.usergroup.UserGroupCommonInfo;


/**
 * Created by IntelliJ IDEA.
 * User: wei
 * Date: Feb 25, 2003
 * Time: 2:04:40 PM
 * To change this template use Options | File Templates.
 */
public class UserGroupInfo extends UserGroupCommonInfo
{

	private String permissionNames[];
	private boolean permissions[];
	private int permissionID[];

	public UserGroupInfo(boolean isGroup, int id, String name, int[] permissionID, String[] permissionNames, boolean[] permissions)
	{
		super(isGroup, id, name);
		this.permissionID = permissionID;
		this.permissionNames = permissionNames;
		this.permissions = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++) {
			this.permissions[i] = permissions[i];
		}

	}

	public UserGroupInfo(boolean isGroup, int id, String name, String description, int[] permissionID, String[] permissionNames, boolean[] permissions)
	{
		super(isGroup, id, name, description);
		this.permissionID = permissionID;
		this.permissionNames = permissionNames;
		this.permissions = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++) {
			this.permissions[i] = permissions[i];
		}
	}

	public void setPermissionNames(String[] permissionNames)
	{
		this.permissionNames = permissionNames;
	}

	public void setPermissions(boolean[] permissions)
	{
		this.permissions = permissions;
	}

	public void setPermissionID(int[] permissionID)
	{
		this.permissionID = permissionID;
	}

	public String[] getPermissionNames()
	{
		return permissionNames;
	}

	public boolean[] getPermissions()
	{
		return permissions;
	}

	public int[] getPermissionID()
	{
		return permissionID;
	}
}
