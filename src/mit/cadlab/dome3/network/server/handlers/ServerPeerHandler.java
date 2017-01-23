package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.server.ServerPeerConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 16, 2003
 * Time: 10:27:44 PM
 * To change this template use Options | File Templates.
 */
public class ServerPeerHandler extends AbstractXmlRpcHandler
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
	public Object execute(String methodName, Vector params)
	        throws XmlRpcException
	{
		try {
			if (methodName.equals(ServerPeerConstants.DELETE_REMOTE_RESOURCE))
				deleteRemoteResource(params);
			else if (methodName.equals(ServerPeerConstants.GET_RESOURCE_GRAPH))
				return getResourceGraph(params);
            else if (methodName.equals(ServerPeerConstants.SET_RESOURCE_EXTERNAL_GRAPH))
                setResourceExternalGraph(params);
            else if (methodName.equals(ServerPeerConstants.NOTIFY_PROJECT_RUN_COMPLETE))
	            notifyProjectRunComplete(params);
			else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
			return RuntimeConstants.NO_VECTOR;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	private void deleteRemoteResource(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deleteRemoteProject (CompoundId runtimeId, " +
			                          "String resourceType)");

		// get parameters
		CompoundId runtimeId = new CompoundId((String) params.get(0));
		String type = new String((String) params.get(1));

		// call method
		RuntimeFunctionsServer.deleteRemoteResource(runtimeId, type);
	}

	private Vector getResourceGraph(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getResourceGraph (CompoundId resourceId, " +
			                          "Vector interfaceId) ");

		// get parameters
		CompoundId resourceId = new CompoundId((String) params.get(0));
		Vector interfaceId = (Vector) params.get(1);

		// call method
		return RuntimeFunctionsServer.getResourceGraph(resourceId, interfaceId);
	}

    private void setResourceExternalGraph(Vector params)
            throws XmlRpcException {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                    DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for setResourceExternalGraph (CompoundId resourceId, " +
                    "String extGraphXml) ");

        // get parameters
        CompoundId resourceId = new CompoundId((String) params.get(0));
        String extGraphXml = (String) params.get(1);

        // call method
        RuntimeFunctionsServer.setResourceExternalGraph(resourceId, extGraphXml);
    }

	private static void notifyProjectRunComplete(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for notifyProjectRunComplete (CompoundId resourceId)" );
		// get parameters
		CompoundId resourceId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.notifyProjectRunComplete(resourceId);
	}

}
