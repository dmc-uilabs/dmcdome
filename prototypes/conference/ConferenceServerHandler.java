package conference;

import mit.cadlab.dome.network.server.AbstractXmlRpcHandler;
import mit.cadlab.dome.network.server.Debug;
import mit.cadlab.dome.server.db.DbConstants;
import mit.cadlab.dome.server.db.DbUtils;
import mit.cadlab.dome.server.db.DbErrors;

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
		Debug.trace(Debug.ALL, "ConferenceServerHandler.execute: " + methodName);
		try
		{
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
		}
		catch (XmlRpcException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}
	private Boolean joinConference(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			        DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_OBJ}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for joinConference(String clientURL, String clientId, Object playspaceId)");
		ConferenceClientConnection clientConn = new ConferenceClientConnection((String)params.elementAt(0));
		if(clientConn == null)
			return new Boolean(false);
		else
			return ConferenceServer.joinConference((String)params.elementAt(1), params.elementAt(2), clientConn);
	}
	private Vector whoIsInTheConferenceRoom(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			        DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_OBJ}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for whoIsInTheConferenceRoom(String clientURL, Object playspaceId)");
		return
		    ConferenceServer.whoIsInTheConferenceRoom((String)params.elementAt(0), params.elementAt(1));
	}
	private Boolean sendMessageToEveryone(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			        DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_OBJ}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for sendMessageToEveryone(String authorId, String message, Object playspaceId)");
		return
			ConferenceServer.sendMessageToEveryone((String)params.elementAt(0), (String)params.elementAt(1), params.elementAt(2));
	}
	private Boolean sendMessageToMember(Vector params) throws XmlRpcException
	{
		if(params.size() != 4)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			        DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR, PARAM_OBJ}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for sendMessageToEveryone(String authorId, String msgTo, String message, Object playspaceId)");
		return
			ConferenceServer.sendMessageToMember((String)params.elementAt(0), (String)params.elementAt(1), (String)params.elementAt(2), params.elementAt(3));
	}
	private Boolean leaveConference(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			        DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_OBJ}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for leaveConference(String memberId, Object playspaceId)");
		return
			ConferenceServer.leaveConference((String)params.elementAt(0), params.elementAt(1));
	}
}
