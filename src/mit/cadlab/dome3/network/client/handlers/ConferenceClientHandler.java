package mit.cadlab.dome3.network.client.handlers;

import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.client.conference.ConferenceClient;

import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 31, 2003
 * Time: 8:52:16 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceClientHandler extends AbstractXmlRpcHandler
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
		//Debug.trace(Debug.ALL, "ConferenceClientHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.INFORM_CONFERENCE_OF_NEW_MEMBER))
				return informConferenceOfNewMember(params);
			else if (methodName.equals(DbConstants.SEND_MESSAGE_TO_CONFERENCE_MEMBER))
				return sendMessageToConferenceMember(params);
			else if (methodName.equals(DbConstants.INFORM_CONFERENCE_OF_OLD_MEMBER))
				return informConferenceOfOldMember(params);
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

	private Boolean informConferenceOfNewMember(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for informConferenceOfNewMember(String newMember, String ownerGUI)");
		boolean result = ConferenceClient.informConferenceOfNewMember((String) params.elementAt(0),
		                                                              (String) params.elementAt(1));
		return new Boolean(result);
	}

	private Boolean sendMessageToConferenceMember(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR,PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for sendMessageToConferenceMembers(String msgAuthor, String message)");
		boolean result = ConferenceClient.sendMessageToConferenceMembers((String) params.elementAt(0),(String) params.elementAt(1),
		                                                                 (String) params.elementAt(2));
		return new Boolean(result);
	}

	private Boolean informConferenceOfOldMember(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for informConferenceOfOldMember(String oldMember, String ownerGUI)");
		boolean result = ConferenceClient.informConferenceOfOldMember((String) params.elementAt(0),
		                                                              (String) params.elementAt(1));
		return new Boolean(result);
	}
}
