// ClientDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbConstants;
import org.apache.xmlrpc.XmlRpcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Client authentication and session management functions
 * todo: check valid session, return userId
 */
public class ClientDbFunctions
{

	/**
	 * allows client to establish a adminsitrative connection with the server
	 * @param userName - username of the user
	 * @param encryptedPassword - encrypted password of the user
	 * @param clientUrl - url to be able to reach the client
	 * @return connection id to be used for all future connections
	 */
	public static String loginAdmin(String userName, byte[] encryptedPassword, String clientUrl) throws XmlRpcException
	{
		if (userName == null || userName.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
			                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
		if (clientUrl == null || clientUrl.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_EMPTY_CLIENT_URL,
			                          DbErrors.XMLRPC_EMPTY_CLIENT_URL_MSG);

		PreparedStatement pstmt = null;
		try {

			// check that the user exists and that the password matches
			String query = "select ID from USERS_GROUPS where NAME=? and PASSWORD=?";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setString(1, userName);
			pstmt.setBytes(2, encryptedPassword);
			ResultSet rs = pstmt.executeQuery();
			Vector v = DbUtils.oneRowResultSetToVector(rs);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
				                          DbErrors.XMLRPC_LOGIN_FAILED_MSG);
			}
			int user_id = ((Integer) v.get(0)).intValue();
			pstmt.close();

			// check whether user is in the admins group
			query = "select * from GROUP_MEMBERSHIP where GROUP_ID = '1' and MEMBER_ID = ?";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setInt(1, user_id);
			v = DbUtils.executeQuery(pstmt, false);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
				                          DbErrors.XMLRPC_LOGIN_FAILED_MSG);
			}

			// start a session
			String sessionId = UUIDGenerator.create();
			query = "insert into SESSIONS (ID,USER_ID,URL, TYPE) values (?,?,?,?)";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setString(1, sessionId);
			pstmt.setInt(2, user_id);
			pstmt.setString(3, clientUrl);
			pstmt.setString(4, DbConstants.LOGIN_TYPE_ADMIN);
			if (pstmt.executeUpdate() == 1)
				return sessionId;
			else
				throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
				                          DbErrors.XMLRPC_LOGIN_FAILED_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * allows client to establish a user account connection with the server
	 * @param userName - username of the user
	 * @param encryptedPassword - encrypted password of the user
	 * @param clientUrl - url to be able to reach the client
	 * @return connection id to be used for all future connections and login type (user or admin)
	 */
	public static String[] loginUser(String userName, byte[] encryptedPassword, String clientUrl) throws XmlRpcException
	{
		if (userName == null || userName.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
			                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
		if (clientUrl == null || clientUrl.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_EMPTY_CLIENT_URL,
			                          DbErrors.XMLRPC_EMPTY_CLIENT_URL_MSG);

		PreparedStatement pstmt = null;

        ProjectResourceInfoRuntime._password = encryptedPassword;

		try {

			// check that the user exists and that the password matches
			String query = "select ID from USERS_GROUPS where NAME=? and PASSWORD=? and STATUS='ACTIVE'";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setString(1, userName);
			pstmt.setBytes(2, encryptedPassword);
			ResultSet rs = pstmt.executeQuery();
			Vector v = DbUtils.oneRowResultSetToVector(rs);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
				                          DbErrors.XMLRPC_LOGIN_FAILED_MSG);
			}
			int user_id = ((Integer) v.get(0)).intValue();
			pstmt.close();

			// check whether user is in the admins group
			String loginType = DbConstants.LOGIN_TYPE_USER; // default
			query = "select * from GROUP_MEMBERSHIP where GROUP_ID = '1' and MEMBER_ID = ?";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setInt(1, user_id);
			v = DbUtils.executeQuery(pstmt, false);
			if (!v.isEmpty()) {
				loginType = DbConstants.LOGIN_TYPE_ADMIN;
			}

			// start a session
			String sessionId = UUIDGenerator.create();
			query = "insert into SESSIONS (ID,USER_ID,URL, TYPE) values (?,?,?,?)";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setString(1, sessionId);
			pstmt.setInt(2, user_id);
			pstmt.setString(3, clientUrl);
			pstmt.setString(4, loginType);
			if (pstmt.executeUpdate() == 1)
				return new String[]{sessionId, loginType};
			else
				throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
				                          DbErrors.XMLRPC_LOGIN_FAILED_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * allows client to establish a guest connection with the server
	 * @param clientUrl - url to be able to reach the client
	 * @return connection id to be used for all future connections
	 */
	public static String loginGuest(String clientUrl) throws XmlRpcException
	{
		if (clientUrl == null || clientUrl.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_EMPTY_CLIENT_URL,
			                          DbErrors.XMLRPC_EMPTY_CLIENT_URL_MSG);

		PreparedStatement pstmt = null;
		try {
			// start a session
			String sessionId = UUIDGenerator.create();
			String query = "insert into SESSIONS (ID, USER_ID,TYPE,URL) values (?,?,?,?)";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setString(1, sessionId);
			pstmt.setInt(2, DbConstants.GUEST_USER_ID);
			pstmt.setString(3, DbConstants.LOGIN_TYPE_GUEST);
			pstmt.setString(4, clientUrl);

			if (pstmt.executeUpdate() == 1)
				return sessionId;
			else
				throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
				                          DbErrors.XMLRPC_LOGIN_FAILED_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 * Logs out the client
	 * @param sessionId
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static void logout(String sessionId) throws XmlRpcException
	{
		if (sessionId == null || sessionId.equals(""))
			throw new XmlRpcException(DbErrors.XMLRPC_BAD_SESSION_ID,
			                          DbErrors.XMLRPC_BAD_SESSION_ID_MSG);

		PreparedStatement pstmt = null;
		try {
			String query = "update SESSIONS set STATUS='" + DbConstants.SESSION_STATUS_LOGGED_OUT
			        + "', LAST_ACTIVITY='now' where ID=?";
			pstmt = DbUtils.getPreparedStatement(query);
			pstmt.setString(1, sessionId);
			if (pstmt.executeUpdate() == 1)
				return;
            //we don't need want the following code bcz it is possible that the user has been deleted without logout so his/her session is no longer exist,
            //then we don't want to following exception to generate.
			//else
				 //throw new XmlRpcException(DbErrors.XMLRPC_BAD_SESSION_ID,
				                //          DbErrors.XMLRPC_BAD_SESSION_ID_MSG);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
			}
		}
	}

}
