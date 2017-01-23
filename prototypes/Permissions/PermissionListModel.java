package Permissions;

import mit.cadlab.dome.gui.servermode.usergroup.MutableUserGroupListModel;
import mit.cadlab.dome.util.DArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Mar 7, 2003
 * Time: 11:25:57 AM
 * To change this template use Options | File Templates.
 */
public class PermissionListModel extends MutableUserGroupListModel
{
	public PermissionListModel(DArrayList data)
	{
		super(data);

	}

	public void addElement(Object objectWithPerm)
	{
		data.add(objectWithPerm);

	}

	public void remove(int index)
	{
		data.remove(index);

	}
}

