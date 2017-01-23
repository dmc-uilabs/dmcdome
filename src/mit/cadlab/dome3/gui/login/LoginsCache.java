// LoginsCache.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.login;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to cache LoginInfo.
 */
public class LoginsCache
{

	private static List cachedLoginInfo = new ArrayList();

	/**
	 * Get the list of LoginInfos saved
	 * @return an unmodifiable list of the currently cached login information
	 */
	public static List getLogins()
	{
		return Collections.unmodifiableList(cachedLoginInfo);
	}

	/**
	 * Adds the LoginInfo to the saved list.
	 * If it exists in the list, moves it to the front of the list.
	 * @param lInfo the login information to be saved
	 */
	public static void addLoginInfo(LoginInfo lInfo)
	{
		cachedLoginInfo.remove(lInfo); // move to front if already in list
		cachedLoginInfo.add(0, lInfo);
	}

	/**
	 * Removes the login info from the list.
	 * @param lInfo the LoginInfo to be removed
	 */
	public static void removeLoginInfo(LoginInfo lInfo)
	{
		cachedLoginInfo.remove(lInfo);
	}

	/**
	 * Removes all the login information from the list.
	 */
	public static void clearLoginInfo()
	{
		cachedLoginInfo.clear();
	}

}
