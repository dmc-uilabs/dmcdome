// LoginInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.login;

import mit.cadlab.dome3.network.client.connection.LoginUtils;

import java.util.Date;

/**
 * This class stores information about a user connection to a server
 * so that the user does not need to retype the password again
 */
public class LoginInfo implements Comparable
{
	private String username;
	private String server;
	private byte[] encryptedPwd;
	private String loginType = "";
	private Date lastLogin;

	public LoginInfo(String username, String server, byte[] encryptedPwd, String loginType)
	{
		this.username = username;
		this.server = server;
		this.encryptedPwd = encryptedPwd;
		setLoginType(loginType);
		resetLastLogin();
	}

	public String getUsername()
	{
		return username;
	}

	public String getServer()
	{
		return server;
	}

	public byte[] getEncryptedPwd()
	{
		return encryptedPwd;
	}

	public String getLoginType()
	{
		return loginType;
	}

	public void setLoginType(String loginType)
	{
		if (loginType == null || !(loginType == LoginUtils.GUEST || loginType.equals(LoginUtils.ADMIN) || loginType.equals(LoginUtils.USER)))
			throw new IllegalArgumentException("LoginInfo - invalid loginType: " + loginType);
		this.loginType = loginType;
	}

	public Date getLastLogin()
	{
		return lastLogin;
	}

	public void resetLastLogin()
	{
		lastLogin = new Date();
	}

	public int compareTo(Object o)
	{
		if (o instanceof LoginInfo)
			return this.lastLogin.compareTo(((LoginInfo) o).lastLogin);
		throw new ClassCastException("LoginInfo: can not compare Date to " + o.getClass());
	}

	public int hashCode()
	{
		return this.toString().hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof LoginInfo) {
			return this.toString().equals(obj.toString());
		} else
			throw new ClassCastException("LoginInfo equals: can not compare with " + obj.getClass());
	}

	public String toString()
	{
		return username + "@" + server;
	}

}
