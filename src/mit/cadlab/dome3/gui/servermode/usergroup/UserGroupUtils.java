// UserGroupUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.servermode.usergroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

public class UserGroupUtils
{

	public static List loadUsersGroupsFromList(List data)
	{
		List items = new ArrayList();
		for (int i = 0; i < data.size(); i++) {
			List o = (List) data.get(i);
			String type = (String) o.remove(0);
			if (type.equals("U")) {
				switch (o.size()) {
					case 2:
						items.add(new UserInfo(((Integer) o.get(0)).intValue(), (String) o.get(1)));
						break;
					case 3:
						items.add(new UserInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2)));
						break;
					case 4:
						items.add(new UserInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2), (String) o.get(3)));
						break;
					default:
						System.err.println("loadUsersGroupsFromList: invalid number of arguments " + o);
				}
			} else {
				switch (o.size()) {
					case 2:
						items.add(new GroupInfo(((Integer) o.get(0)).intValue(), (String) o.get(1)));
						break;
					case 3:
						items.add(new GroupInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2)));
						break;
					case 4:
						items.add(new GroupInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2), (String) o.get(3)));
						break;
					default:
						System.err.println("loadUsersGroupsFromList: invalid number of arguments " + o);
				}
			}
		}
		return items;
	}

	public static List loadUsersFromList(List data)
	{
		List users = new ArrayList();
		for (int i = 0; i < data.size(); i++) {
			List o = (List) data.get(i);
			switch (o.size()) {
				case 2:
					users.add(new UserInfo(((Integer) o.get(0)).intValue(), (String) o.get(1)));
					break;
				case 3:
					users.add(new UserInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2)));
					break;
				case 4:
					users.add(new UserInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2), (String) o.get(3)));
					break;
				default:
					System.err.println("loadUsersFromList: invalid number of arguments " + o);
			}
		}
		return users;
	}

	public static List loadGroupsFromList(List data)
	{
		List groups = new ArrayList();
		for (int i = 0; i < data.size(); i++) {
			List o = (List) data.get(i);
			switch (o.size()) {
				case 2:
					groups.add(new GroupInfo(((Integer) o.get(0)).intValue(), (String) o.get(1)));
					break;
				case 3:
					groups.add(new GroupInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2)));
					break;
				case 4:
					groups.add(new GroupInfo(((Integer) o.get(0)).intValue(), (String) o.get(1), (String) o.get(2), (String) o.get(3)));
					break;
				default:
					System.err.println("loadGroupsFromList: invalid number of arguments " + o);
			}
		}
		return groups;
	}

	public static List loadUsersFromArray(Object[] data)
	{
		List users = new ArrayList();
		for (int i = 0; i < data.length; i++) {
			Object[] o = (Object[]) data[i];
			users.add(new UserInfo(((Integer) o[0]).intValue(), (String) o[1], (String) o[2], (String) o[3]));
		}
		return users;
	}

	public static List loadGroupsFromArray(Object[] data)
	{
		List groups = new ArrayList();
		for (int i = 0; i < data.length; i++) {
			Object[] o = (Object[]) data[i];
			groups.add(new GroupInfo(((Integer) o[0]).intValue(), (String) o[1], (String) o[2], (String) o[3]));
		}
		return groups;
	}

	/**
	 *
	 * @param data List of UserGroupCommonInfo
	 * @return Vector if Ids of items in list given
	 */
	public static Vector userGroupInfoToIdVector(List data)
	{
		Vector v = new Vector();
		Iterator it = data.iterator();
		while (it.hasNext()) {
			v.add(new Integer(((UserGroupCommonInfo) it.next()).getId()));
		}
		return v;
	}

}
