package mit.cadlab.dome3.network.client.handlers;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.RuntimeUtils;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 4, 2003
 * Time: 3:07:59 PM
 * To change this template use Options | File Templates.
 */

public class RuntimeClientHandler extends AbstractXmlRpcHandler
{
	DomeRuntimeClient m_client;

	private HashMap lastModelStatusChangeIdByObjectId = new HashMap(); // key = objectId, value = changeId

	public RuntimeClientHandler(DomeRuntimeClient client)
	{
		m_client = client;
	}


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
		Debug.trace(Debug.ALL, "RuntimeClientHandler.execute: " + methodName);
		try {
			if (methodName.equals(RuntimeConstants.MESSAGE_ITEM_VALUE_CHANGED))
				itemValueChanged(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_ITEM_STATUS_CHANGED))
				itemStatusChanged(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED))
				modelStatusChanged(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_MODEL_STARTUP_ERROR))
				handleModelStartupError(params);
            else if (methodName.equals(RuntimeConstants.MESSAGE_MODEL_EXECUTION_ERROR))
	            handleModelExecutionError(params);
            else if (methodName.equals(RuntimeConstants.PASS_INDIVIDUAL_TO_CLIENT))
                passIndividualToClient(params);
            else if (methodName.equals(RuntimeConstants.PREPARE_PLOT_FOR_NEXT_GENERATION))
                startPlottingNextIteration(params);
            else if (methodName.equals(RuntimeConstants.OPTIMIZATION_ANALYSIS_IS_COMPLETE))
                optimizationAnalysisIsComplete(params);
			else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
			return RuntimeUtils.NO_VECTOR;
		} catch (XmlRpcException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	private void itemValueChanged(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for messageItemValueChanged (CompoundId objectCompoundId, List values)");

		// get parameters
		CompoundId objectId = new CompoundId((String) params.get(0));
		Vector values = (Vector) params.get(1);

		// call method
		m_client.messageItemValueChanged(objectId, values);
	}


	private void itemStatusChanged(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for messageItemStatusChanged (CompoundId objectCompoundId, Integer changedId, String status)");

		// get parameters
		CompoundId objectId = new CompoundId((String) params.get(0));
		int changedId = ((Integer) params.get(1)).intValue();
		String status = (String) params.get(2);

		// call method
		m_client.messageItemStatusChanged(objectId, changedId, status);
	}

	/**
	 * Tell the client that the model status has changed (e.g., running, pause, stopped, done).
	 * @param params Model id and status
	 * @throws XmlRpcException
	 */
	private void modelStatusChanged(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for modelStatusChanged (CompoundId objectCompoundId, Integer msgId, String status)");

		// get parameters
		CompoundId objectId = new CompoundId((String) params.get(0));
		Integer msgId = (Integer) params.get(1);
		String status = (String) params.get(2);

		// avoid processing mixed up messages and setting earlier values on top of later values
		synchronized (lastModelStatusChangeIdByObjectId) {
			Integer lastMsgId = (Integer)lastModelStatusChangeIdByObjectId.get(objectId);
			if (lastMsgId ==  null || msgId.intValue()==Integer.MIN_VALUE || msgId.intValue()>lastMsgId.intValue())
				lastModelStatusChangeIdByObjectId.put(objectId, msgId);
			else
				return; // ignore changes that come in reverse
		}

		// call method
		m_client.modelStatusChanged(objectId, status);
	}

	private void handleModelStartupError(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for handleModelStartupError (CompoundId targetParameterId, String msg)");

		// get parameters
		CompoundId targetParameter = new CompoundId((String) params.get(0));
		String msg = (String) params.get(1);

		// call method
		m_client.handleModelExecutionError(targetParameter, null, msg);
	}

	private void handleModelExecutionError(Vector params)
	        throws XmlRpcException {
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			        "invalid arguments for handleModelExecutionError (CompoundId targetParameterId, String objectName, String msg)");

		// get parameters
		CompoundId targetParameter = new CompoundId((String) params.get(0));
		String objectName = (String) params.get(1);
		String msg = (String) params.get(2);

		// call method
		m_client.handleModelExecutionError(targetParameter, objectName, msg);
	}

    private void passIndividualToClient(Vector params)
            throws XmlRpcException
    {
        if (params.size() != 2)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for passIndividualToClient (String objectId, Vector variablesAndObjectives");
        CompoundId objectId = new CompoundId((String) params.get(0));
		Vector v = (Vector) params.get(1);

        // call method
        m_client.passIndividualToClient(objectId, v);
    }

    private void optimizationAnalysisIsComplete(Vector params)
            throws XmlRpcException
    {
        if (params.size() != 1)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
        if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for startPlottingNextIteration (String objectId)");
        CompoundId objectId = new CompoundId((String) params.get(0));

        //call method
        m_client.optimizationAnalysisIsComplete(objectId);
    }

    private void startPlottingNextIteration(Vector params)
            throws XmlRpcException
    {
        if (params.size() != 1)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
        if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for startPlottingNextIteration (String objectId)");
        CompoundId objectId = new CompoundId((String) params.get(0));

        // call method
        m_client.preparePlotForNextGeneration(objectId);
    }


}