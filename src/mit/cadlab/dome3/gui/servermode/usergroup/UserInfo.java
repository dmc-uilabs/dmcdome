// UserInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserInfo extends UserGroupCommonInfo
{

	protected List groups = new ArrayList();

	public UserInfo(int id, String name, String description, String status,
	                boolean canSaveModels, boolean canSavePlayspaces)
	{
		super(false, id, name, description, status, canSaveModels, canSavePlayspaces);
	}

	public UserInfo(int id, String name)
	{
		super(false, id, name);
	}

	public UserInfo(int id, String name, String description)
	{
		super(false, id, name, description);
	}

	public UserInfo(int id, String name, String description, String status)
	{
		super(false, id, name, description, status);
	}

	public List getGroups()
	{
		return Collections.unmodifiableList(groups);
	}

	public void setGroups(List groups)
	{
		this.groups = groups;
	}
}
