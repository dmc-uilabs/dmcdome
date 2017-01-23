package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.RuntimeUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.Debug;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 1, 2003
 * Time: 6:09:21 PM
 * To change this template use Options | File Templates.
 */

public class RuntimeFunctionsClient
{
	public static HashMap globalObjectMap = new HashMap();


	public static void addToGlobalObjectMap(CompoundId objectId, Object object)
	{
		CompoundId runtimeId = new CompoundId(objectId);
		runtimeId.resetStaticInfo();
		globalObjectMap.put(runtimeId.toString(), object);
	}


	public static Object getFromGlobalObjectMap(CompoundId objectId)
	{
		CompoundId runtimeId = new CompoundId(objectId);
		runtimeId.resetStaticInfo();
		Object obj = globalObjectMap.get(runtimeId.toString());
		return obj;
	}


	public static List getPlayspaceMembers(ServerConnection svrConn, CompoundId playspaceId)
	{
		List result;
		String connectionId = svrConn.getConnectionId();
		result = (List) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.GET_MEMBERS,
		                                Vectors.create(connectionId, playspaceId.toString()));
		return result;
	}

	public static Vector joinPlayspace(ServerConnection svrConn, String playspaceStaticId)
	{
		Vector result;
		String connectionId = svrConn.getConnectionId();
		result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.JOIN_PLAYSPACE,
		                                  Vectors.create(connectionId, playspaceStaticId.toString()));

		if (result.size() != 2)
			return DbUtils.NO_VECTOR;

		Vector v = new Vector();
		v.add(result.get(0));
		v.add(result.get(1));
		return v;
	}

	// todo: replace separate calls to joinPlayspace and createInterface with this method
	/**
	 * Creates interface and playspace, if necessary.
	 * @param svrConn
	 * @param interfaceId include the playspace id if a specific playspace is desired
	 * @param isDistributedPlayspace
	 * @param registerInterface
	 * @param isProjectResource
	 * @return Object[] {playspaceRuntimeId, playspaceXml, interfaceRuntimeId, interfaceVersion, interfaceXml}
	 * returns null if error
	 */
	public static Object[] createInterfaceAndPlayspace(ServerConnection svrConn, CompoundId interfaceId,
	                                                   boolean isDistributedPlayspace, boolean registerInterface, boolean isProjectResource)
	{
		Vector result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.CREATE_INTERFACE_AND_PLAYSPACE,
		                                         Vectors.create(svrConn.getConnectionId(), interfaceId.toString(),
		                                                        new Boolean(isDistributedPlayspace),
		                                                        new Boolean(registerInterface),
		                                                        new Boolean(isProjectResource)));

		if (result.size() != 2)
			return null;

		Vector psInfo = (Vector) result.get(0);
		Vector ifaceInfo = (Vector) result.get(1);
		Object[] results = new Object[5];
		results[0] = new CompoundId((String) psInfo.get(0));
		results[1] = psInfo.get(1);
		results[2] = new CompoundId((String) ifaceInfo.get(0));
		results[3] = ifaceInfo.get(1);
		results[4] = ifaceInfo.get(2);
		return results;
	}

	public static Vector createInterface(ServerConnection svrConn, CompoundId objectCompoundId,
	                                     boolean isDistributedPlayspace, boolean joinPlayspace, boolean isProjectResource)
	{
        //Debug.trace(Debug.ALL, "RuntimeFunctionsClient.createInterface "+objectCompoundId.getInterfaceStaticId());

		Vector result;
		String connectionId = svrConn.getConnectionId();
		String interfaceId = objectCompoundId.toString();
		result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.CREATE_INTERFACE,
		                                  Vectors.create(connectionId, interfaceId,
		                                                 new Boolean(isDistributedPlayspace),
		                                                 new Boolean(joinPlayspace),
		                                                 new Boolean(isProjectResource)));

		if (result.size() != 2)
			return DbUtils.NO_VECTOR;

		Vector v = new Vector();
		CompoundId objectId = new CompoundId((String) result.get(0));
		v.add(objectId);        // object id
		v.add(result.get(1));   // xml string
		return v;
	}

	public static Vector createInterfaceQuick(ServerConnection svrConn, CompoundId objectCompoundId,
	                                     boolean isDistributedPlayspace, boolean joinPlayspace)
	{
		//Debug.trace(Debug.ALL, "RuntimeFunctionsClient.createInterfaceQuick " + objectCompoundId.getInterfaceStaticId());

		Vector result;
		String connectionId = svrConn.getConnectionId();
		String interfaceId = objectCompoundId.toString();
		result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.CREATE_INTERFACE_QUICK,
		                                  Vectors.create(connectionId, interfaceId,
		                                                 new Boolean(isDistributedPlayspace),
		                                                 new Boolean(joinPlayspace)));

		if (result.size() != 4)
			return DbUtils.NO_VECTOR;

		Vector v = new Vector();
		CompoundId objectId = new CompoundId((String) result.get(0));
		v.add(objectId);        // object id
		v.add(result.get(1));   // xml string
		v.add(result.get(2));   // true/false if interface created on server already
		boolean startInterfaceParent = ((Boolean)result.get(3)).booleanValue();
		if (startInterfaceParent)
			startInterfaceParent(svrConn, new CompoundId(objectId));
		return v;
	}

	public static void startInterfaceParent(ServerConnection svrConn, CompoundId ifaceId)
	{
		String interfaceId = ifaceId.toString();
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.START_INTERFACE_PARENT,
		                     Vectors.create(interfaceId));
	}

	public static String getInterfaceStatus(ServerConnection svrConn, CompoundId ifaceId) {
		String interfaceId = ifaceId.toString();
		return (String) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.GET_INTERFACE_STATUS,
		                                  Vectors.create(interfaceId));
	}

    public static Vector createAnalysisToolInterface(ServerConnection svrConn, CompoundId objectCompoundId,
                                                     boolean isDistributedPlayspace, boolean joinPlayspace)
    {
        Vector result;
        String connectionId = svrConn.getConnectionId();
        String interfaceId = objectCompoundId.toString();
        result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.CREATE_ANALYSIS_TOOL_INTERFACE,
                                          Vectors.create(connectionId, interfaceId, new Boolean (isDistributedPlayspace), new Boolean (joinPlayspace)));

        Vector v = new Vector();
        CompoundId objectId = new CompoundId((String) result.get(0));
        v.add(objectId);
        v.add(result.get(1));
        return v;
    }

	public static Vector createProject(ServerConnection svrConn, CompoundId projectCompoundId, boolean joinPlayspace)
	{
		Vector result;
		String connectionId = svrConn.getConnectionId();
		String projectId = projectCompoundId.toString();
		result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "."
		                                  + RuntimeConstants.CREATE_PROJECT,
		                                  Vectors.create(connectionId, projectId, new Boolean(joinPlayspace)));

		if (result.size() != 3)
			return DbUtils.NO_VECTOR;

		Vector v = new Vector();
		CompoundId objectId = new CompoundId((String) result.get(0));
		v.add(objectId);       // project runtime id
		v.add(result.get(1));  // xml
		v.add(result.get(2));  // hashtable
		return v;
	}

    public static Vector createAnalysisTool(ServerConnection svrConn, CompoundId analysisToolCompoundId, boolean joinPlayspace)
	{
		Vector result;
		String connectionId = svrConn.getConnectionId();
		String analysisToolId = analysisToolCompoundId.toString();
		result = (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "."
		                                  + RuntimeConstants.CREATE_ANALYSIS_TOOL,
		                                  Vectors.create(connectionId, analysisToolId, new Boolean(joinPlayspace)));

		if (result.size() != 5)
			return DbUtils.NO_VECTOR;

		Vector v = new Vector();
		CompoundId objectId = new CompoundId((String) result.get(0));
        CompoundId projectId = new CompoundId((String) result.get(2));
		v.add(objectId);       // analysis tool runtime id
		v.add(result.get(1));                // analysis tool xml
        v.add(projectId);      // project runtime id
        v.add(result.get(3));  // project name
        v.add(result.get(4));  // project description
		return v;
	}

	public static Vector getToolProjectInfo(ServerConnection svrConn, String toolId)
	{
		return (Vector) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.GET_TOOL_PROJECT_INFO,
		                                Vectors.create(toolId));
	}

	/**
	 * Note: changedItems should be a hashtable that will not be changed since execAsync may not use it immediately!
	 */
	public static void setItems(ServerConnection svrConn, Hashtable changedItems, boolean shouldSolve)
	{
		setItems(svrConn, Vectors.create(changedItems), shouldSolve);
	}

	public static void setItems(ServerConnection svrConn, Vector changedItems, boolean shouldSolve)
	{
		// convert hashtable values into xmlrpc-friendly objects
		for (Iterator iter = changedItems.iterator(); iter.hasNext();) {
			Hashtable changeMap = (Hashtable) iter.next();

			for (Iterator itemIter = changeMap.keySet().iterator(); itemIter.hasNext();) {
				String id = (String) itemIter.next();
				List args = (List) changeMap.get(id);
				Vector v = RuntimeUtils.listToVector(args); // the conversion
				changeMap.put(id, v);
			}
		}

		String connectionId = svrConn.getConnectionId();
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.SET_ITEMS,
		                     Vectors.create(connectionId, changedItems, new Boolean(shouldSolve)));
	}

    public static void startAnalysisToolSolving(ServerConnection svrConn, CompoundId interfaceId, Hashtable parameterAttributes, boolean shouldSolve)
	{
		startAnalysisToolSolving(svrConn, Vectors.create(interfaceId.toString()), parameterAttributes, shouldSolve);
	}

    public static void setProjectInterfaceInsideAnalysisToolItems(ServerConnection svrConn, CompoundId interfaceId, Vector items)
	{
		String connectionId = svrConn.getConnectionId();
        svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.SET_PROJECT_INSIDE_ANALYSIS_TOOL_ITEMS,
                Vectors.create(connectionId, interfaceId.toString(), items));
	}

    public static void startAnalysisToolSolving(ServerConnection svrConn, Vector compoundIds, Hashtable parameterAttributes, boolean shouldSolve)
    {
        String connectionId = svrConn.getConnectionId();
        svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.START_ANALYSIS_TOOL_SOLVING,
                             Vectors.create(connectionId, compoundIds, parameterAttributes, new Boolean(shouldSolve)));
    }

	public static void pauseSolving(ServerConnection svrConn, CompoundId ifaceId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.PAUSE_SOLVING,
		                     Vectors.create(ifaceId.toString()));
	}

	public static void resumeSolving(ServerConnection svrConn, CompoundId ifaceId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.RESUME_SOLVING,
		                     Vectors.create(ifaceId.toString()));
	}

	public static void killSolving(ServerConnection svrConn, CompoundId ifaceId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.KILL_SOLVING,
		                     Vectors.create(ifaceId.toString()));
	}

	public static void startModel(ServerConnection svrConn, CompoundId modelId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.START_SOLVING,
		                     Vectors.create(modelId.toString()));
	}


	/**
	 * Quit a playspace.
	 * @param svrConn Server connection
	 * @param playspaceId Playspace compound id
	 */
	public static void leavePlayspace (ServerConnection svrConn, CompoundId playspaceId)
	{
		String connectionId = svrConn.getConnectionId();
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.LEAVE_PLAYSPACE,
		                     Vectors.create(connectionId, playspaceId.toString()));
	}

	public static void killInterfaceParent(ServerConnection svrConn, CompoundId ifaceId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.KILL_INTERFACE_PARENT,
		                     Vectors.create(ifaceId.toString()));
	}

	public static void killProject(ServerConnection svrConn, CompoundId projectId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.KILL_PROJECT,
		                     Vectors.create(projectId.toString()));
	}

	public static void killAnalysisTool(ServerConnection svrConn, CompoundId toolId)
	{
		svrConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.KILL_TOOL,
		                     Vectors.create(toolId.toString()));
	}

	public static Object getFileContent(ServerConnection svrConn, CompoundId fileParameterId)
	{
		Object filecontent = svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.GET_FILE_CONTENT,
		                                     Vectors.create(fileParameterId.toString()));
		return filecontent;
	}

    public static Hashtable getParameterSystemCausality(ServerConnection svrConn, CompoundId interfaceId) {
        return (Hashtable) svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "."
                + RuntimeConstants.GET_PARAMETER_SYSTEM_CAUSALITY, Vectors.create(svrConn.getConnectionId(),interfaceId.toString()));
    }

	public static Object[] getCustomGuiJar(ServerConnection svrConn, String jarPath)
	{
		Vector result = (Vector)svrConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "."
		                                   + RuntimeConstants.GET_CUSTOM_GUI_JAR, Vectors.create(jarPath));
		return result.toArray();
	}

}
