package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.conference.ConferenceServer;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.client.conference.ConferenceClientConnection;

import java.util.Vector;
import java.util.ArrayList;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 31, 2003
 * Time: 8:52:16 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceServerHandler extends AbstractXmlRpcHandler
{
	/**
	 * This is the only method that an XMLRPC handler has to implement.
	 *
	 * @param methodName - name of the method on the sever that the client wants to invoke
	 * @param params - arguments to the method on the server
	 *
	 * @return results of the method execution on the server.
	 * @throws org.apache.xmlrpc.XmlRpcException wraps up any exceptions thrown by the method on the server or
	 * 					if a particular method is not found on the server.
	 */

	public Object execute(String methodName, Vector params) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "ConferenceServerHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.JOIN_CONFERENCE))
				return joinConference(params);
			else if (methodName.equals(DbConstants.WHO_IS_IN_THE_CONFERENCE_ROOM))
				return whoIsInTheConferenceRoom(params);
			else if (methodName.equals(DbConstants.SEND_MESSAGE_TO_EVERYONE))
				return sendMessageToEveryone(params);
			else if (methodName.equals(DbConstants.SEND_MESSAGE_TO_MEMBER))
				return sendMessageToMember(params);
			else if (methodName.equals(DbConstants.LEAVE_CONFERENCE))
				return leaveConference(params);
			else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	private Boolean joinConference(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for joinConference(String connectionId, String playspaceId)");
		String sessionId = (String) params.get(0);
		String playspaceId = (String) params.get(1);
		Vector v = ConferenceServer.getUserInfoFromSession(sessionId);
		int userId = getInt(v.elementAt(0));
		String name = (String) v.elementAt(1);
		String url = (String) v.elementAt(2);
		ConferenceClientConnection clientConn = new ConferenceClientConnection(url);
		if (clientConn == null)
			return new Boolean(false);
		else {
			boolean result = ConferenceServer.joinConference(name, sessionId, playspaceId, clientConn);
			return new Boolean(result);
		}
	}

	private Vector whoIsInTheConferenceRoom(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for whoIsInTheConferenceRoom(String connectionId, String playspaceId)");
		String sessionId = (String) params.get(0);
		String playspaceId = (String) params.get(1);
		return ConferenceServer.whoIsInTheConferenceRoom(playspaceId);
	}

	private Boolean sendMessageToEveryone(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for sendMessageToEveryone(String connectionId, String message, "
			                          + "String playspaceId)");
		String sessionId = (String) params.get(0);
		String message = (String) params.get(1);
		String playspaceId = (String) params.get(2);
		Vector v = ConferenceServer.getUserInfoFromSession(sessionId);
		int userId = getInt(v.elementAt(0));
		String name = (String) v.elementAt(1);
		boolean result = ConferenceServer.sendMessageToEveryone(name, message, playspaceId);
		return new Boolean(result);
	}

	private Boolean sendMessageToMember(Vector params) throws XmlRpcException
	{
		if (params.size() != 4)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for sendMessageToEveryone(String connectionId, "
			                          + "String msgTo, String message, String playspaceId)");
		String sessionId = (String) params.get(0);
		String messageRecipient = (String) params.elementAt(1);
		String message = (String) params.elementAt(2);
		String playspaceId = (String) params.get(3);
		Vector v = ConferenceServer.getUserInfoFromSession(sessionId);
		int userId = getInt(v.elementAt(0));
		String name = (String) v.elementAt(1);
		boolean result = ConferenceServer.sendMessageToMember(name, messageRecipient, message, playspaceId);
		return new Boolean(result);
	}

	private Boolean leaveConference(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for leaveConference(String connectionId, Object playspaceId)");
		String sessionId = (String) params.get(0);
		String playspaceId = (String) params.get(1);
		Vector v = ConferenceServer.getUserInfoFromSession(sessionId);
		int userId = getInt(v.elementAt(0));
		String name = (String) v.elementAt(1);
		boolean result = ConferenceServer.leaveConference(name, playspaceId);
		return new Boolean(result);
	}
}
