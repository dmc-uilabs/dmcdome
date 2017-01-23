// UserGroupCommonInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Feb 11, 2003
 * Time: 2:04:40 PM
 * To change this template use Options | File Templates.
 */
public class UserGroupCommonInfo implements Comparable
{

	protected int id;
	protected String name;
	protected String description;
	protected String status;
	protected boolean canSaveModels;
	protected boolean canSavePlayspaces;
	protected boolean isGroup = false;

	protected UserGroupCommonInfo(boolean isGroup, int id, String name, String description, String status,
	                              boolean canSaveModels, boolean canSavePlayspaces)
	{
		this.isGroup = isGroup;
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
		this.canSaveModels = canSaveModels;
		this.canSavePlayspaces = canSavePlayspaces;
	}

	protected UserGroupCommonInfo(boolean isGroup, int id, String name)
	{
		this.isGroup = isGroup;
		this.id = id;
		this.name = name;
	}

	protected UserGroupCommonInfo(boolean isGroup, int id, String name, String description)
	{
		this.isGroup = isGroup;
		this.id = id;
		this.name = name;
		this.description = description;
	}

	protected UserGroupCommonInfo(boolean isGroup, int id, String name, String description, String status)
	{
		this.isGroup = isGroup;
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
	}

	public boolean isGroup()
	{
		return isGroup;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public boolean canSaveModels()
	{
		return canSaveModels;
	}

	public void setCanSaveModels(boolean canSaveModels)
	{
		this.canSaveModels = canSaveModels;
	}

	public boolean canSavePlayspaces()
	{
		return canSavePlayspaces;
	}

	public void setCanSavePlayspaces(boolean canSavePlayspaces)
	{
		this.canSavePlayspaces = canSavePlayspaces;
	}

	/**
	 * users, then groups, in alphabetical order by name
	 * @param o the object to be compared against this object
	 * @return
	 */
	public int compareTo(Object o)
	{
		if (o instanceof UserGroupCommonInfo) {
			UserGroupCommonInfo obj = (UserGroupCommonInfo) o;
			if (this.isGroup() == obj.isGroup()) {
				return this.getName().compareToIgnoreCase(obj.getName());
			} else if (this.isGroup()) { // obj is user
				return 1;
			} else { // this is user, obj is group
				return -1;
			}
		} else {
			throw new ClassCastException("can not compare " + this + " to " + o);
		}
	}

	public String getNameDescription()
	{
		if (description == null || description.equals(""))
			return name;
		else
			return name + "(" + description + ")";
	}

	public String toString()
	{
		return id + " " + name + " " + description + " " + super.toString();
	}

	public int hashCode()
	{
		return (id + name + isGroup).hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof UserGroupCommonInfo) {
			UserGroupCommonInfo info = (UserGroupCommonInfo) obj;
			return (this.id == info.id && this.isGroup() == info.isGroup() && this.getName().equals(info.getName()));
		} else {
			throw new ClassCastException("can not compare equality of " + this + " to " + obj);
		}
	}
}
