// GroupInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupInfo extends UserGroupCommonInfo
{

	protected List members = new ArrayList();

	public GroupInfo(int id, String name, String description, String status,
	                 boolean canSaveModels, boolean canSavePlayspaces)
	{
		super(true, id, name, description, status, canSaveModels, canSavePlayspaces);
	}

	public GroupInfo(int id, String name)
	{
		super(true, id, name);
	}

	public GroupInfo(int id, String name, String description)
	{
		super(true, id, name, description);
	}

	public GroupInfo(int id, String name, String description, String status)
	{
		super(true, id, name, description, status);
	}

	public List getMembers()
	{
		return Collections.unmodifiableList(members);
	}

	public void setMembers(List members)
	{
		this.members = members;
	}
}
