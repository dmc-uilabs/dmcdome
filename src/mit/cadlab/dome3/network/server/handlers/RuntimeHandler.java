package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.util.FileUtils;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Hashtable;
import java.util.Vector;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 25, 2003
 * Time: 5:58:05 PM
 * To change this template use Options | File Templates.
 */
public class RuntimeHandler extends AbstractXmlRpcHandler
{
	private HashMap lastModelStatusChangeIdByObjectId = new HashMap(); // key = objectId, value = changeId

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
		//Debug.trace(Debug.ALL, "*******************************************************************");
		//Debug.trace(Debug.ALL, "RuntimeHandler.execute: " + methodName);

		try {
			if (methodName.equals(RuntimeConstants.GET_MEMBERS))
				return getMembers(params);
			else if (methodName.equals(RuntimeConstants.JOIN_PLAYSPACE))
				return joinPlayspace(params);
			else if (methodName.equals(RuntimeConstants.CREATE_INTERFACE))
				return createInterface(params);
			else if (methodName.equals(RuntimeConstants.CREATE_INTERFACE_QUICK))
				return createInterfaceQuick(params);
			else if (methodName.equals(RuntimeConstants.START_INTERFACE_PARENT))
				startInterfaceParent(params);
			else if (methodName.equals(RuntimeConstants.GET_INTERFACE_STATUS))
				return getInterfaceStatus(params);
            else if (methodName.equals(RuntimeConstants.CREATE_ANALYSIS_TOOL_INTERFACE))
                return createAnalysisToolInterface(params);
            else if (methodName.equals(RuntimeConstants.CREATE_INTERFACE_AND_PLAYSPACE))
	            return createInterfaceAndPlayspace(params);
			else if (methodName.equals(RuntimeConstants.CREATE_PROJECT))
				return createProject(params);
            else if (methodName.equals(RuntimeConstants.CREATE_ANALYSIS_TOOL))
                return createAnalysisTool(params);
            else if (methodName.equals(RuntimeConstants.GET_TOOL_PROJECT_INFO))
	            return getToolProjectInfo(params);
			else if (methodName.equals(RuntimeConstants.SET_ITEMS))
				setItems(params);
            else if (methodName.equals(RuntimeConstants.START_ANALYSIS_TOOL_SOLVING))
                startAnalysisToolSolving(params);
            else if (methodName.equals(RuntimeConstants.SET_PROJECT_INSIDE_ANALYSIS_TOOL_ITEMS))
                setAnalysisToolClientProjectItems(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_ITEM_VALUE_CHANGED))
				itemValueChanged(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_ITEM_STATUS_CHANGED))
				itemStatusChanged(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_PARAMETERS_INCONSISTENT))
				setParametersInconsistent(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED))
				subscriptionModelStatusChanged(params);
			else if (methodName.equals(RuntimeConstants.MESSAGE_MODEL_EXECUTION_ERROR))
				handleSubscriptionModelExecutionError(params);
			else if (methodName.equals(RuntimeConstants.START_SOLVING))
				startSolving(params);
			else if (methodName.equals(RuntimeConstants.PAUSE_SOLVING))
				pauseSolving(params);
			else if (methodName.equals(RuntimeConstants.RESUME_SOLVING))
				resumeSolving(params);
			else if (methodName.equals(RuntimeConstants.KILL_SOLVING))
				killSolving(params);
			else if (methodName.equals(RuntimeConstants.LEAVE_PLAYSPACE))
				leavePlayspace(params);
			else if (methodName.equals(RuntimeConstants.KILL_INTERFACE_PARENT))
				killInterfaceParent(params);
			else if (methodName.equals(RuntimeConstants.KILL_PROJECT))
				killProject(params);
			else if (methodName.equals(RuntimeConstants.KILL_TOOL))
				killTool(params);
            else if (methodName.equals(RuntimeConstants.GET_FILE_CONTENT))
				return getFileContent(params);
			else if (methodName.equals(RuntimeConstants.GET_PARAMETER_SYSTEM_CAUSALITY))
				return getParameterSystemCausality(params);
			else if (methodName.equals(RuntimeConstants.GET_CUSTOM_GUI_JAR))
				return getCustomGuiJar(params);
			else if (methodName.equals(RuntimeConstants.GET_FILE))
				sendGetFileRequest(params);
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


	public static Object getFileContent(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getFileContent(CompoundId fileParameterId)");

		// prepare parameters
		CompoundId parameterId = new CompoundId((String) params.get(0));

		// call method
		Object fileContent = RuntimeFunctionsServer.getFileContent(parameterId);

		return fileContent;
	}

	public static Vector getMembers(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getMembers(String sessionId, CompoundId runtimeId)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId playspaceId = new CompoundId((String) params.get(1));

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		Vector models = RuntimeFunctionsServer.getMembers(sessionId, playspaceId);


		return models;
	}


	public static Vector joinPlayspace(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for joinPlayspace(String sessionId, CompoundId runtimeId)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId objectId = new CompoundId();
		objectId.setPlayspaceStaticId((String) params.get(1));

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		String xmlContent = RuntimeFunctionsServer.joinPlayspace(sessionId, objectId);
		Vector results = new Vector();
		results.add(objectId.toString());
		results.add(xmlContent);
		return results;
	}

	/**
	 * interfaceId should include playspace id if you want it to be in that playspace
	 * @param params
	 * @return Vector <playspaceInfo, interfaceInfo>
	 * playspaceInfo is Vector <playspaceRuntimeId, playspaceXml>
	 * interfaceInfo is Vector <interfaceRuntimeId, interfaceVersion, interfaceXml>
	 * @throws XmlRpcException
	 */
	public static Vector createInterfaceAndPlayspace(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 5)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL, PARAM_BOOL, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createInterfaceAndPlayspace(String sessionId, "
			                          + "CompoundId interfaceId, Boolean isDistributedPlayspace, "
			                          + "Boolean joinPlayspace, Boolean isProjectResource)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId interfaceId = new CompoundId((String) params.get(1));
		boolean isDistributedPlayspace = ((Boolean) params.get(2)).booleanValue();
		boolean joinPlayspace = ((Boolean) params.get(3)).booleanValue();
		boolean isProjectResource = ((Boolean) params.get(4)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		CompoundId playspaceId = new CompoundId();
		playspaceId.setPlayspaceRuntimeId(interfaceId.getPlayspaceRuntimeId());
		playspaceId.setPlayspaceStaticId(interfaceId.getPlayspaceStaticId());
		String playspaceXml = null;
		if (!isProjectResource)
			playspaceXml = RuntimeFunctionsServer.joinPlayspace(sessionId, playspaceId);
		else
            playspaceXml = RuntimeFunctionsServer.getPlayspaceXml(playspaceId);
		Vector playspaceInfo = Vectors.create(playspaceId.toString(), playspaceXml);

		// call method
		interfaceId.setPlayspaceRuntimeId(playspaceId.getPlayspaceRuntimeId()); // set in case a new playspace created
		String interfaceResults = RuntimeFunctionsServer.createInterface(sessionId, interfaceId,
		                                                                 isDistributedPlayspace,
		                                                                 joinPlayspace, isProjectResource);
		Vector interfaceInfo = null;
		if (interfaceResults == null) {
			interfaceInfo = Vectors.create(interfaceId.toString());
		} else {
			Integer version = FileSystemDbFunctions.getInterfaceVersion(interfaceId.getInterfaceStaticId());
			interfaceInfo = Vectors.create(interfaceId.toString(), version.toString(), interfaceResults);
		}
		return Vectors.create(playspaceInfo, interfaceInfo);
	}

	public static Vector createInterface(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 5)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL, PARAM_BOOL, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createInterface(String sessionId, "
			                          + "CompoundId interfaceId, Boolean isDistributedPlayspace, "
			                          + "Boolean joinPlayspace, Boolean isProjectResource)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId objectId = new CompoundId((String) params.get(1));
		boolean isDistributedPlayspace = ((Boolean) params.get(2)).booleanValue();
		boolean joinPlayspace = ((Boolean) params.get(3)).booleanValue();
		boolean isProjectResource = ((Boolean) params.get(4)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		String xml = RuntimeFunctionsServer.createInterface(sessionId, objectId,
		                                                        isDistributedPlayspace,
		                                                        joinPlayspace, isProjectResource);
		Vector finalResults = new Vector();
		finalResults.add(objectId.toString());
		if (xml != null) {
			finalResults.add(xml);
		}
		return finalResults;
	}

	/**
	 *
	 * @param params
	 * @return interfaceId, interfaceXml, boolean isInterfaceCreated, boolean startInterfaceParent
	 * @throws XmlRpcException
	 */
	public static Vector createInterfaceQuick(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 4)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createInterfaceQuick(String sessionId, "
			                          + "CompoundId interfaceId, Boolean isDistributedPlayspace, "
			                          + "Boolean joinPlayspace)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId objectId = new CompoundId((String) params.get(1));
		boolean isDistributedPlayspace = ((Boolean) params.get(2)).booleanValue();
		boolean joinPlayspace = ((Boolean) params.get(3)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		return RuntimeFunctionsServer.createInterfaceQuick(sessionId, objectId, isDistributedPlayspace, joinPlayspace);
	}

	public static void startInterfaceParent(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for startInterfaceParent(CompoundId interfaceId)");

		// prepare parameters
		CompoundId objectId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.startInterfaceParent(objectId);
	}

	public static String getInterfaceStatus(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getInterfaceStatus(CompoundId interfaceId)");

		// prepare parameters
		CompoundId objectId = new CompoundId((String) params.get(0));

		// call method
		String result = RuntimeFunctionsServer.getInterfaceStatus(objectId);
		if (result == null)
			result = "";
		return result;
	}

    public static Vector createAnalysisToolInterface(Vector params)
                                                                    throws XmlRpcException
    {
        if (params.size() != 4)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                    DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL, PARAM_BOOL}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for createAnalysisToolInterface(String sessionId, "
                    + "CompoundId interfaceId, Boolean isDistributedPlayspace, "
                    + "Boolean joinPlayspace)");

        String sessionId = (String) params.get(0);
        CompoundId objectId = new CompoundId((String) params.get(1));
        boolean isDistributedPlayspace = ((Boolean) params.get(2)).booleanValue();
        boolean joinPlayspace = ((Boolean) params.get(3)).booleanValue();

	    DomeServer.addClientConnection(sessionId);

	    String ifaceXml = RuntimeFunctionsServer.createAnalysisToolInterface(sessionId,
	                                                                         objectId,
	                                                                         isDistributedPlayspace,
	                                                                         joinPlayspace);
	    Vector finalResults = new Vector();
	    finalResults.add(objectId.toString());
	    if (ifaceXml != null)
	    {
		    finalResults.add (ifaceXml);
	    }
	    return finalResults;
    }

	public static Vector createProject(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createProject(String sessionId, CompoundId projectId, boolean joinPlayspace)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId projectId = new CompoundId((String) params.get(1));
		boolean joinPlayspace = ((Boolean) params.get(2)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		Object[] xmlContent_IdMap = RuntimeFunctionsServer.createProject(sessionId, projectId, joinPlayspace);
		Vector results = new Vector();
		results.add(xmlContent_IdMap[0]);  // id
		results.add(xmlContent_IdMap[1]);  // xml
		results.add(xmlContent_IdMap[2]);  // id map (hashtable)
		return results;
	}

    public static Vector createAnalysisTool(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createAnalysisTool(String sessionId, CompoundId analysisToolId, boolean joinPlayspace)");

		// prepare parameters
		String sessionId = (String) params.get(0);
		CompoundId analysisToolId = new CompoundId((String) params.get(1));
		boolean joinPlayspace = ((Boolean)params.get(2)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		Object[] xmlContent_IdMap = RuntimeFunctionsServer.createAnalysisTool(sessionId, analysisToolId, joinPlayspace);
		Vector results = new Vector();
		results.add(xmlContent_IdMap[0]);  // id
		results.add(xmlContent_IdMap[1]);  // analysis tool xml
        results.add(xmlContent_IdMap[2]);  // project id
        results.add(xmlContent_IdMap[3]);  // project name
        results.add(xmlContent_IdMap[4]);  // project description
		return results;
	}

	private Vector getToolProjectInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getToolProjectInfo(CompoundId toolId)");
		CompoundId toolId = new CompoundId((String) params.get(0));
		return RuntimeFunctionsServer.getAnalysisToolProjectInfo(toolId);
	}

	private void    setItems(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for setItem (String sessionId, Vector changeMaps, Boolean shouldSolve)");

		// get parameters
		String sessionId = (String) params.get(0);
		Vector changeMaps = (Vector) params.get(1);
		boolean shouldSolve = ((Boolean) params.get(2)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		RuntimeFunctionsServer.setItems(sessionId, changeMaps, shouldSolve);
	}

    private void startAnalysisToolSolving(Vector params) throws XmlRpcException
    {
        if (params.size() != 4)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                    DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_HASHTABLE, PARAM_BOOL}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for setItem (String sessionId, Vector changeMaps, Boolean shouldSolve)");

        String sessionId = (String) params.get(0);
        Vector compoundIds = (Vector) params.get(1);
        Hashtable parameterAttributes = (Hashtable) params.get(2);
        boolean shouldSolve = ((Boolean) params.get(3)).booleanValue();

        DomeServer.addClientConnection(sessionId);

        RuntimeFunctionsServer.startAnalysisToolSolving(sessionId, compoundIds, parameterAttributes, shouldSolve);
    }

    private void setAnalysisToolClientProjectItems(Vector params) throws XmlRpcException
    {
        if (params.size() != 3)
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
                    DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
        if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_VEC}))
            throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
                    "invalid arguments for setItem (String sessionId, String runtimeIds, Vector items)");

        String sessionId = (String) params.get(0);
        String compoundId = (String) params.get(1);
        Vector items = (Vector) params.get(2);

        DomeServer.addClientConnection(sessionId);
        RuntimeFunctionsServer.setAnalysisToolClientProjectItems(sessionId, compoundId, items);
    }


	private void itemValueChanged(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for messageItemValueChanged (CompoundId objectCompoundId, List values)");

		// get parameters
		CompoundId objectCompoundId = new CompoundId((String) params.get(0));
		Vector values = (Vector) params.get(1);

		// call method
		CompoundId ifaceId = new CompoundId(objectCompoundId);
		ifaceId.resetDataObjectRuntimeId();
		SubscriptionInterface iface;
		iface = (SubscriptionInterface) RuntimeFunctionsServer.getFromGlobalObjectMap(ifaceId);

		if (iface != null) {
			// get the interface parameter
			Parameter p = null;
			Id objectId = new Id(objectCompoundId.getDataObjectStaticId());
			if (objectId != null)
				p = (Parameter) iface.getInterfaceObjectsFlatMap().get(objectId);
			iface.setItem(p.getId(), values);
		}
	}


	private void itemStatusChanged(Vector params) throws XmlRpcException
	{
		// this gets called for advanced notification of parameter status to underlying resource models
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for messageItemStatusChanged (CompoundId objectCompoundId, "
			                          + "Integer changedId, String status)");

		// get parameters
		CompoundId objectCompoundId = new CompoundId((String) params.get(0));
		String status = (String) params.get(2);

		RuntimeFunctionsServer.setItemStatus(objectCompoundId, status);
	}

	private void setParametersInconsistent(Vector params) throws XmlRpcException
	{
		// this gets called for advanced notification of parameter status to underlying resource models
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for setParametersInconsistent(Vector paramIds)");

		Vector paramIdStrings = (Vector)params.get(0);
		Vector paramIds = new Vector();
		String id;
		for (int i = 0; i < paramIdStrings.size(); i++) {
			paramIds.add(new CompoundId((String) paramIdStrings.elementAt(i)));
		}
		RuntimeFunctionsServer.setParametersInconsistent(paramIds);
	}

	private void subscriptionModelStatusChanged(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for subscriptionModelStatusChanged (CompoundId subIfaceCompoundId, Integer msgId, String status)");

		// get parameters
		CompoundId ifaceId = new CompoundId((String) params.get(0));
		Integer msgId = (Integer) params.get(1);
		String status = (String) params.get(2);

		// avoid processing mixed up messages and setting earlier values on top of later values
		synchronized (lastModelStatusChangeIdByObjectId) {
			Integer lastMsgId = (Integer) lastModelStatusChangeIdByObjectId.get(ifaceId);
			if (lastMsgId == null || msgId.intValue() == Integer.MIN_VALUE || msgId.intValue() > lastMsgId.intValue())
				lastModelStatusChangeIdByObjectId.put(ifaceId, msgId);
			else
				return; // ignore changes that come in reverse
		}

		// call method
		SubscriptionInterface iface = (SubscriptionInterface) RuntimeFunctionsServer.getFromGlobalObjectMap(ifaceId);
		if (iface != null) {
			String resourceId = iface.getSubscription().getResourceId();
			IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime)((DomeModelRuntime)iface.getModel()).getIntegrationProject();
			project.updateResourceModelStatus(resourceId, status);
		} else {
			// todo: prevent these changes from being sent
			// changes are already sent to the subscription interface
			// the changes are also sent to the project/model (not the interface)
			// so these duplicate changes can be ignored
		}
	}

	private void handleSubscriptionModelExecutionError(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, "");
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for subscriptionModelError (CompoundId subIfaceCompoundId, String objectName, String msg)");

		// get parameters
		CompoundId ifaceId = new CompoundId((String) params.get(0));
		String objectName = (String) params.get(1);
		String msg = (String) params.get(2);

		// propagate to top level project
		SubscriptionInterface iface = (SubscriptionInterface) RuntimeFunctionsServer.getFromGlobalObjectMap(ifaceId);
		IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime)((DomeModelRuntime)iface.getModel()).getIntegrationProject();
		project.handleImodelAndResourceExecutionError(msg);
	}

	private void startSolving(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for startSolving (CompoundId interfaceId)");

		// get parameters
		CompoundId interfaceId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.startSolving(interfaceId);
	}


	private void pauseSolving(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for pauseSolving (CompoundId interfaceId)");

		// get parameters
		CompoundId interfaceId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.pauseSolving(interfaceId);
	}


	private void resumeSolving(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for resumeSolving (CompoundId interfaceId)");

		// get parameters
		CompoundId interfaceId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.resumeSolving(interfaceId);
	}


	private void killSolving(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for killSolving (CompoundId interfaceId)");

		// get parameters
		CompoundId interfaceId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.killSolving(interfaceId);
	}


	private void leavePlayspace (Vector params)
			throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for leavePlayspace (String sessionId, CompoundId playspaceId)");

	    // get parameters
        String sessionId = (String) params.get(0);
		CompoundId playspaceId = new CompoundId((String) params.get(1));

		// call method
		RuntimeFunctionsServer.leavePlayspace(sessionId, playspaceId.getPlayspaceRuntimeId());
	}

	private void killInterfaceParent (Vector params)
			throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for killInterfaceParent (CompoundId interfaceId)");

	    // get parameters
        CompoundId interfaceId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.killInterfaceParent(interfaceId);
	}

	private void killProject(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for killProject (CompoundId projectId)");

		// get parameters
		CompoundId projectId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.killProject(projectId);
	}

	private void killTool(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for killTool (CompoundId toolId)");

		// get parameters
		CompoundId toolId = new CompoundId((String) params.get(0));

		// call method
		RuntimeFunctionsServer.killTool(toolId);
	}

	private Hashtable getParameterSystemCausality(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getParameterSystemCausality (String sessionId, CompoundId interfaceId)");

		// get parameters
		String sessionId = (String) params.get(0);
		CompoundId interfaceId = new CompoundId((String) params.get(1));

		// call method
		return RuntimeFunctionsServer.getParameterSystemCausality(sessionId, interfaceId);
	}

	private Vector getCustomGuiJar(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getCustomGuiJar (String jarPath)");

		// get parameters
		String jarpath = (String) params.get(0);
		File f = new File(DomeServer.getServerAuxFileRoot()+File.separator+jarpath);
		if (f.exists()) {
			try {
				return FileUtils.loadFileAsByteArray(f);
			}
			catch (IOException e) {
				System.err.println("error loading jar: " + e);
				throw new RuntimeException("error loading jar: " + jarpath+"\n"+e, e);
			}
		} else {
			System.err.println("unable to find jar: "+jarpath);
			throw new RuntimeException("unable to find jar: "+jarpath);
		}
	}
	
	private void sendGetFileRequest(Vector params)
	        throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_VEC, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for sendGetFileRequest (String sessionId, Vector changeMaps, Boolean shouldSolve)");

		// get parameters
		String sessionId = (String) params.get(0);
		Vector changeMaps = (Vector) params.get(1);
		boolean shouldSolve = ((Boolean) params.get(2)).booleanValue();

		// add a client connection in the server
		DomeServer.addClientConnection(sessionId);

		// call method
		RuntimeFunctionsServer.sendGetFileRequest(sessionId, changeMaps, shouldSolve);
	}

}
