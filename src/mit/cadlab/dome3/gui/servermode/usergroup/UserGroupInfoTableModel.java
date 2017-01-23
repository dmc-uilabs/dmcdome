// UserGroupInfoTableModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.table.AbstractTableModel;
import javax.swing.ImageIcon;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Feb 11, 2003
 * Time: 2:42:54 PM
 * To change this template use Options | File Templates.
 */
public class UserGroupInfoTableModel extends AbstractTableModel
{
	protected static ImageIcon activeIcon = DomeIcons.getIcon(DomeIcons.ACTIVE_USER_GROUP);
	protected static ImageIcon inactiveIcon = DomeIcons.getIcon(DomeIcons.INACTIVE_USER_GROUP);

	protected List data;

	public UserGroupInfoTableModel(List data)
	{
		this.data = data;
	}

	public int getRowCount()
	{
		return data.size();
	}

	public int getColumnCount()
	{
		return 3;
	}

	public Class getColumnClass(int columnIndex)
	{
		if (columnIndex == 0)
			return ImageIcon.class;
		else
			return String.class;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		UserGroupCommonInfo info = (UserGroupCommonInfo) data.get(rowIndex);
		if (columnIndex == 0) {
			if (info.getStatus().equals("ACTIVE"))
				return activeIcon;
			else
				return inactiveIcon;
		} else if (columnIndex == 1)
			return info.getName();
		else if (columnIndex == 2)
			return info.getDescription();
		else
			return null;
	}

	public String getColumnName(int column)
	{
		if (column == 0)
			return " ";
		else if (column == 1)
			return "name";
		else if (column == 2)
			return "description";
		else
			return null;
	}


}
