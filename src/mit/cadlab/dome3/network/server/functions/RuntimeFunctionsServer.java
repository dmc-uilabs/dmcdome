package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.objectmodel.playspace.ServerPlayspace;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.dom4j.Element;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 25, 2003
 * Time: 6:30:42 PM
 * To change this template use Options | File Templates.
 */

public class RuntimeFunctionsServer
{
	private static HashMap playspaceMap = new HashMap();   // maps playspace runtime ids to objects
	private static HashMap playspaceIdMap = new HashMap(); // maps playspace static ids to runtime ids
	public static HashMap globalObjectMap = new HashMap();


	public static void addToGlobalObjectMap(CompoundId objectId, Object object)
	{
		synchronized (globalObjectMap)
		{
			CompoundId runtimeId = new CompoundId(objectId);
			runtimeId.resetStaticInfo();
			globalObjectMap.put(runtimeId.toString(), object);
		}
	}

	public static Object getFromGlobalObjectMap(CompoundId objectId)
	{
		synchronized (globalObjectMap)
		{
			CompoundId runtimeId = new CompoundId(objectId);
			runtimeId.resetStaticInfo();
			return globalObjectMap.get(runtimeId.toString());
		}
	}


	/**
	 * Load a playspace description from the database and create a playspace instance.
	 * @param playspaceId Playspace id
	 * @return Playspace instance
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	private static ServerPlayspace loadPlayspace(String sessionId, CompoundId playspaceId)
	        throws XmlRpcException
	{
		synchronized (playspaceMap)
		{
			ServerPlayspace playspace = null;
			String playspaceStaticId = playspaceId.getPlayspaceStaticId();

			// if playspace does not exist, load description from the database and create it
			// get from the database
			String xmlContent = DeployFilesDbFunctions.getMostRecentPlayspaceXmlDefinition(playspaceStaticId);
			Element playspaceElement = XMLUtils.stringToXmlElement(xmlContent);
			playspace = new ServerPlayspace(playspaceElement, playspaceStaticId);
			if (playspace == null)
				throw new XmlRpcException(DbErrors.XMLRPC_INTERFACE_INVOCATION_ERROR,
										  DbErrors.XMLRPC_INTERFACE_INVOCATION_MSG);

			// store runtime ids
			String playspaceRuntimeId = playspace.getRuntimeId().toString();
			playspaceMap.put(playspaceRuntimeId, playspace);
			playspaceIdMap.put(playspaceStaticId, playspaceRuntimeId);
			playspaceId.setPlayspaceRuntimeId(playspaceRuntimeId);

			// listen for playspace activity
			playspace.addPropertyChangeListener(ServerPlayspace.PROPERTY_CLOSED, new PlayspaceActivityListener());

			return playspace;
		}
	}

	/**
	 * Listens for specific activity from a playspace.
	 */
	private static class PlayspaceActivityListener implements PropertyChangeListener
	{
		public synchronized void propertyChange(PropertyChangeEvent evt)
		{
			if (ServerPlayspace.PROPERTY_CLOSED.equals(evt.getPropertyName()))
			{
				ServerPlayspace playspace;
				playspace = (ServerPlayspace) evt.getSource();
                String playspaceRuntimeId = playspace.getRuntimeId();
				String playspaceStaticId = playspace.getStaticId();

				playspaceMap.remove(playspaceRuntimeId);
				playspaceIdMap.remove(playspaceRuntimeId);
				if (playspaceStaticId!=null) {
					playspaceMap.remove(playspaceStaticId);
					playspaceIdMap.remove(playspaceStaticId);
				}
				Debug.trace(Debug.ALL, "playspace '" + playspace.getName() + "' killed");
			}
		}
	}


	/**
	 * Retrieve an exitisting playspace from the playspace cache. If there is no
	 * existing playspace, try to load it from the database. If no id is provided,
	 * create a transient (empty) playspace.
	 * @param playspaceId Playspace id
	 * @return Playspace instance
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static ServerPlayspace getPlayspace(String sessionId, CompoundId playspaceId)
	        throws XmlRpcException
	{
		synchronized (playspaceMap)
		{
			ServerPlayspace playspace = null;
			String playspaceStaticId = playspaceId.getPlayspaceStaticId();
			String playspaceRuntimeId = playspaceId.getPlayspaceRuntimeId();

			// try to retrieve an existing playspace (using the runtime id)
			if (playspaceRuntimeId != null) {
				playspace = (ServerPlayspace) playspaceMap.get(playspaceRuntimeId);
				if (playspace != null)
					return playspace;
			}

			// try using the static id
			if (playspaceStaticId != null) {
				// another client may have already created the playspace: use the static id to find that instance
				playspaceRuntimeId = (String) playspaceIdMap.get(playspaceStaticId);
				if (playspaceRuntimeId != null) {
					playspace = (ServerPlayspace) playspaceMap.get(playspaceRuntimeId);
					playspaceId.setPlayspaceRuntimeId(playspaceRuntimeId);
				}

				// there is no existing playspace: load it from xml using the static id
				if (playspace == null)
					try {
						playspace = loadPlayspace(sessionId, playspaceId);
					}
					catch (XmlRpcException e) {
						// ignore and create transient playspace instead
					}
			}

			if (playspace ==  null)
			// no existing playspace: create a transient one
			{
				// create a new playspace
				if (playspaceRuntimeId != null) {
					playspace = new ServerPlayspace(playspaceRuntimeId);
                    playspace.setTempSessionId(sessionId); // _i
                }
				else {
					playspace = new ServerPlayspace();
                    playspace.setTempSessionId(sessionId);
                }



				if (playspace == null)
					throw new XmlRpcException(DbErrors.XMLRPC_INTERFACE_INVOCATION_ERROR,
											  DbErrors.XMLRPC_INTERFACE_INVOCATION_MSG);
				// store runtime ids
				playspaceRuntimeId = playspace.getRuntimeId();
				playspaceMap.put(playspaceRuntimeId, playspace);
				playspaceId.setPlayspaceRuntimeId(playspaceRuntimeId);

				// listen for playspace activity
				playspace.addPropertyChangeListener(ServerPlayspace.PROPERTY_CLOSED, new PlayspaceActivityListener());
			}


			return playspace;
		}
	}

	/**
	 * Retrieve an exitisting playspace from the playspace cache. If there is no
	 * existing playspace, try to load it from the database. If no id is provided,
	 * create a transient (empty) playspace.
	 * @param playspaceId Playspace id
	 * @return Playspace instance
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	private static ServerPlayspace getDistributedPlayspace(CompoundId playspaceId)
	        throws XmlRpcException
	{
		synchronized (playspaceMap)
		{
			ServerPlayspace playspace = null;
			String playspaceStaticId = playspaceId.getPlayspaceStaticId();
			String playspaceRuntimeId = playspaceId.getPlayspaceRuntimeId();

			// try to retrieve existing playspace object
			playspace = (ServerPlayspace) playspaceMap.get(playspaceRuntimeId);
			if (playspace == null && playspaceStaticId != null) {
				playspaceRuntimeId = (String) playspaceIdMap.get(playspaceStaticId);
				playspace = (ServerPlayspace) playspaceMap.get(playspaceRuntimeId);
			}

			if (playspace == null) {
				// create a new playspace
				playspace = new ServerPlayspace(playspaceId);
				if (playspace == null)
					throw new XmlRpcException(DbErrors.XMLRPC_INTERFACE_INVOCATION_ERROR,
											  DbErrors.XMLRPC_INTERFACE_INVOCATION_MSG);
				// store runtime ids
				playspaceRuntimeId = playspace.getRuntimeId();
				playspaceMap.put(playspaceRuntimeId, playspace);
				if (playspaceStaticId != null)
					playspaceIdMap.put(playspaceStaticId, playspaceRuntimeId);

				// listen for playspace activity
				playspace.addPropertyChangeListener(ServerPlayspace.PROPERTY_CLOSED, new PlayspaceActivityListener());
			}

			return playspace;
		}
	}


	/**
	 * Admit a user to the playspace.
	 * @param sessionId User session id
	 * @param playspaceId Playspace id
	 * @return Playspace XML description
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static String joinPlayspace(String sessionId, CompoundId playspaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;

		playspace = getPlayspace(sessionId, playspaceId);
		if (playspace != null) {
			playspace.join(sessionId);
			return playspace.getXmlDescription();
		}

		return null;
	}


    /**
	 * Admit a user to the playspace.  this function is esp used by when a project load subscibed interface, it will calll joinPlayspace, now not make it an active resource
	 * @param playspaceId Playspace id
	 * @return Playspace XML description
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static String getPlayspaceXml(CompoundId playspaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(null, playspaceId);
		if (playspace != null) {
			return playspace.getXmlDescription();
		}
		return null;
	}

	/**
	 * Get a list of active members in a playspace.
	 * @param sessionId Session id
	 * @param playspaceId Playspace id
	 * @return Member list
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getMembers(String sessionId, CompoundId playspaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(sessionId, playspaceId);
		if (playspace != null)
			return playspace.getMembers();

		return null;
	}

	/**
	 * Get file content.
	 * @param parameterId parameter id
	 * @return file content
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Object getFileContent(CompoundId parameterId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(null, parameterId);
		if (playspace != null) {
			Object fileContent = playspace.getFileContent(parameterId);
            if (fileContent == null) {
                return "";
            }
        }

		return null;
	}


	/**
	 * Create an interface or retrieve an existing one in a given playspace.
	 * @param sessionId User session id
	 * @param ifaceId Interface id
	 * @return Interface XML description
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static String createInterface(String sessionId, CompoundId ifaceId,
	                                     boolean isDistributedPlayspace,
	                                     boolean joinPlayspace, boolean isProjectResource)
	        throws XmlRpcException
	{
		Vector results = null;
		ServerPlayspace playspace = null;

		// get the playspace object
		if (isDistributedPlayspace) {
			playspace = getDistributedPlayspace(ifaceId);
			if (joinPlayspace)
				playspace.join(sessionId);
		}
		else
			playspace = getPlayspace(sessionId, ifaceId);


		if (playspace != null) {
            Debug.trace(Debug.ALL, "creating interface "+ifaceId.getInterfaceStaticId()+" in the playspace "+playspace.getName());
			return playspace.createInterface(sessionId, ifaceId, isProjectResource);
		}

		return null;
	}


	/**
	 * Create an interface or retrieve an existing one in a given playspace.
	 * @param sessionId User session id
	 * @param ifaceId Interface id
	 * @return interfaceId, interfaceXml, interfaceCreatedOnServer
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector createInterfaceQuick(String sessionId, CompoundId ifaceId,
	                                             boolean isDistributedPlayspace,
	                                             boolean joinPlayspace)
	        throws XmlRpcException
	{
		// get the playspace object
		ServerPlayspace playspace = null;
		if (isDistributedPlayspace) {
			playspace = getDistributedPlayspace(ifaceId);
			if (joinPlayspace)
				playspace.join(sessionId);
		}
		else
			playspace = getPlayspace(sessionId, ifaceId);

		if (playspace != null) {
				return playspace.createInterfaceQuick(sessionId, ifaceId);
		}
		return DbUtils.NO_VECTOR;
	}

	public static void startInterfaceParent(CompoundId ifaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(null, ifaceId);

		if (playspace != null) {
			playspace.startInterfaceParent(ifaceId.getInterfaceRuntimeId());
		} else
			System.err.println("startInterfaceParent - unable to find playspace: "+ifaceId);
	}

	public static String getInterfaceStatus(CompoundId ifaceId)
		throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(null, ifaceId);

		if (playspace != null) {
			return playspace.getInterfaceStatus(ifaceId);
		}
		return null;
	}

    public static String createAnalysisToolInterface(String sessionId, CompoundId ifaceId,
                                                     boolean isDistributedPlayspace,
                                                     boolean joinPlayspace)
                                                                                throws XmlRpcException
    {
        String ifaceXml = null;
        ServerPlayspace playspace = null;

        if(isDistributedPlayspace) {
	        playspace = getDistributedPlayspace(ifaceId);
	        if (joinPlayspace)
		        playspace.join(sessionId);
        }
        else
            playspace = getPlayspace(sessionId, ifaceId);

        if (playspace != null)
            ifaceXml = playspace.createAnalysisToolInterface(sessionId, ifaceId);

        return ifaceXml;
    }

    public static IntegrationProjectServerRuntime createProjectInPlayspace(CompoundId projectId)
	        throws XmlRpcException
	{
		IntegrationProjectServerRuntime project = null;
		ServerPlayspace playspace = getPlayspace(null, projectId);
		if (playspace != null)
			project = playspace.getProject(projectId, true);
		return project;
	}

	public static Object[] createProject(String sessionId, CompoundId projectId, boolean joinPlayspace)
	        throws XmlRpcException
	{
		Object[] xmlContent_idMap = null;
		ServerPlayspace playspace = null;

		playspace = getPlayspace(sessionId, projectId);
		if (playspace != null) {
			if (joinPlayspace)
				playspace.join(sessionId);
			xmlContent_idMap = playspace.createProject(sessionId, projectId);
		}
		return xmlContent_idMap;
	}

    public static Object[] createAnalysisTool(String sessionId, CompoundId analysisToolId, boolean joinPlayspace)
	        throws XmlRpcException
	{
		Object[] xmlContent_idMap = null;
		ServerPlayspace playspace = null;

		playspace = getPlayspace(sessionId, analysisToolId);
		if (playspace != null) {
			if (joinPlayspace)
				playspace.join(sessionId);
			xmlContent_idMap = playspace.createAnalysisTool(sessionId, analysisToolId);
		}
		return xmlContent_idMap;
	}

	public static void deleteRemoteResource (CompoundId runtimeId, String type)
			throws XmlRpcException
	{
		// delete the resource
		ServerPlayspace playspace = null;
		playspace = getPlayspace(null, runtimeId);
		if (playspace != null) {
			if (ProjectResourceInfoRuntime.PROJECT_RESOURCE.equals(type))
				playspace.killProject (runtimeId);
			else
				playspace.killModel(runtimeId);
		}
	}

	public static void killProject(CompoundId projectId) throws XmlRpcException {
		// delete the resource
		ServerPlayspace playspace = null;
		playspace = getPlayspace(null, projectId);
		if (playspace != null) {
			playspace.killProject(projectId);
		}
	}

	public static void killTool(CompoundId toolId) throws XmlRpcException
	{
		// delete the resource
		ServerPlayspace playspace = null;
		playspace = getPlayspace(null, toolId);
		if (playspace != null) {
			playspace.killTool(toolId);
		}
	}

	public static Vector getResourceGraph(CompoundId resourceId, Vector interfaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;
		playspace = getPlayspace(null, resourceId);
		if (playspace != null)
			return playspace.getResourceGraph(resourceId, interfaceId);

		return RuntimeConstants.NO_VECTOR;
	}

    public static void setResourceExternalGraph(CompoundId resourceId, String extGraphXml)
            throws XmlRpcException
    {
        ServerPlayspace playspace = null;
        playspace = getPlayspace(null, resourceId);
        if (playspace != null)
            playspace.setResourceExternalGraph(resourceId, extGraphXml);
    }

	public static void setItems(String sessionId, Vector changeMaps, boolean shouldSolve)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;
		CompoundId playspaceId = null;
		if (!changeMaps.isEmpty()) {
			Enumeration keys = ((Hashtable) changeMaps.get(0)).keys();
			if (keys.hasMoreElements())
				playspaceId = new CompoundId((String) keys.nextElement());

			if (playspaceId != null) {
				playspace = getPlayspace(sessionId, playspaceId);
				if (playspace != null)
					playspace.setItems(sessionId, changeMaps, shouldSolve);
				else
					System.err.println("RuntimeFunctionsServer.setItems - could not find playspace "+playspaceId);
			} else
				System.err.println("RuntimeFunctionsServer.setItems - playspaceId was null\t"+changeMaps);
		} else {
			System.err.println("RuntimeFunctionsServer.setItems - changeMap was empty");
		}
	}

	public static Vector getAnalysisToolProjectInfo(CompoundId toolId) throws XmlRpcException
	{
		ServerPlayspace playspace = null;
		if (toolId != null) {
			playspace = getPlayspace(null, toolId);
			if (playspace != null) {
				return playspace.getAnalysisToolProjectInfo(toolId);
			}
		}
		return DbUtils.NO_VECTOR;
	}

    public static void startAnalysisToolSolving(String sessionId, Vector compoundIds, Hashtable parameterAttributes, boolean shouldSolve)
	        throws XmlRpcException
	{
        ServerPlayspace playspace = null;
        CompoundId playspaceId = null;

        if (!compoundIds.isEmpty())
            playspaceId = new CompoundId((String)compoundIds.get(0));

        if (playspaceId != null)
        {
            playspace = getPlayspace(sessionId, playspaceId);
            if (playspace != null)
                playspace.startAnalysisToolSolving(sessionId, compoundIds, parameterAttributes, shouldSolve);
        }

    }

    public static void setAnalysisToolClientProjectItems(String sessionId, String compoundId, Vector items)
	        throws XmlRpcException
	{
        ServerPlayspace playspace = null;
        CompoundId playspaceId = null;

        if (compoundId != null)
            playspaceId = new CompoundId(compoundId);
        if (playspaceId != null)
        {
            playspace = getPlayspace(sessionId, playspaceId);
            if (playspace != null)
                playspace.setAnalysisToolClientProjectItems(sessionId, compoundId, items);
        }
    }

	public static void setItemStatus(CompoundId objectId, String newStatus)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(null, objectId);
		if (playspace != null)
			playspace.setItemStatus(objectId, newStatus);
	}

	public static void setParametersInconsistent(Vector paramIds)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = getPlayspace(null, (CompoundId)paramIds.get(0));
		if (playspace != null)
			playspace.setParametersInconsistent(paramIds);
	}

	public static void startSolving(CompoundId interfaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;

		playspace = getPlayspace(null, interfaceId);
		if (playspace != null)
			playspace.startSolving(interfaceId);
	}


	public static void pauseSolving(CompoundId interfaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;

		playspace = getPlayspace(null, interfaceId);
		if (playspace != null)
			playspace.pauseSolving(interfaceId);
	}


	public static void resumeSolving(CompoundId interfaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;

		playspace = getPlayspace(null, interfaceId);
		if (playspace != null)
			playspace.resumeSolving(interfaceId);
	}


	public static void killSolving(CompoundId interfaceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;

		playspace = getPlayspace(null, interfaceId);
		if (playspace != null)
			playspace.killSolving(interfaceId);
	}


	public static void killInterfaceParent (CompoundId interfaceId)
		throws XmlRpcException
	{
		ServerPlayspace playspace = null;

		playspace = getPlayspace(null, interfaceId);
		if (playspace != null) {
			playspace.killInterfaceParent(interfaceId);
		}
	}

	/**
	 * Client request to leave a playspace.
	 * @param sessionId Client session id
	 * @param playspaceRuntimeId Playspace runtime id
	 * @throws XmlRpcException
	 */
	public static void leavePlayspace (String sessionId, String playspaceRuntimeId)
		throws XmlRpcException
	{
		ServerPlayspace playspace;
		playspace = (ServerPlayspace) playspaceMap.get (playspaceRuntimeId);
		if (playspace != null) {
			playspace.leave (sessionId);
		}
		else {
			Debug.trace (Debug.ALL, "leavePlayspace - playspace not found");
		}
	}

	/**
	 * Kill all playspaces when the server shuts down.
	 */
	public static void killPlayspaces ()
	{
		Object[] playspaces = playspaceMap.values().toArray();
        for (int i = 0; i < playspaces.length;i++)
        {
            ServerPlayspace playspace = (ServerPlayspace)playspaces[i];
	        playspace.shutdown();
        }
	}

	public static Hashtable getParameterSystemCausality(String sessionId, CompoundId interfaceId)
	        throws XmlRpcException {
		ServerPlayspace playspace = null;

		playspace = getPlayspace(sessionId, interfaceId);
		if (playspace != null) {
			return playspace.getParameterSystemCausality(sessionId, interfaceId);
		}
		return new Hashtable();
	}

	public static void notifyProjectRunComplete(CompoundId resourceId)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;
		playspace = getPlayspace(null, resourceId);
		if (playspace != null)
			playspace.notifyProjectRunComplete(resourceId);
	}
	
	public static void sendGetFileRequest(String sessionId, Vector changeMaps, boolean shouldSolve)
	        throws XmlRpcException
	{
		ServerPlayspace playspace = null;
		CompoundId playspaceId = null;
		if (!changeMaps.isEmpty()) {
			Enumeration keys = ((Hashtable) changeMaps.get(0)).keys();
			if (keys.hasMoreElements())
				playspaceId = new CompoundId((String) keys.nextElement());

			if (playspaceId != null) {
				playspace = getPlayspace(sessionId, playspaceId);
				if (playspace != null)
					playspace.setItems(sessionId, changeMaps, shouldSolve);
				else
					System.err.println("RuntimeFunctionsServer.setItems - could not find playspace "+playspaceId);
			} else
				System.err.println("RuntimeFunctionsServer.setItems - playspaceId was null\t"+changeMaps);
		} else {
			System.err.println("RuntimeFunctionsServer.setItems - changeMap was empty");
		}
	}
}
