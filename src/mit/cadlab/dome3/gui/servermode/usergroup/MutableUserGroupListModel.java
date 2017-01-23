// MutableUserGroupListModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.swing.DListDListModel;
import mit.cadlab.dome3.swing.DListModel;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.List;

public class MutableUserGroupListModel extends DListDListModel implements DListModel
{
	protected static ImageIcon userIcon = DomeIcons.getIcon(DomeIcons.USER);
	protected static ImageIcon groupIcon = DomeIcons.getIcon(DomeIcons.GROUP);

	protected List data;

	/**
	 * This must be a DArrayList of UserInfo/GroupInfo objects
	 * Otherwise, there will be ClassCastExceptions!
	 * @param data
	 */
	public MutableUserGroupListModel(DArrayList data)
	{
		super(data);
		this.data = data;
	}

	public Icon getIcon(int index)
	{
		if (((UserGroupCommonInfo) getElementAt(index)).isGroup())
			return groupIcon;
		else
			return userIcon;
	}

	public String getListText(int index)
	{
		return ((UserGroupCommonInfo) getElementAt(index)).getName();
	}

}
