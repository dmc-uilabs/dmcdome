package mit.cadlab.dome3.network.client;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.handlers.ConferenceClientHandler;
import mit.cadlab.dome3.network.client.handlers.RuntimeClientHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.ClientRuntimeScope;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.ClassUtils;
import org.apache.xmlrpc.WebServer;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 4, 2003
 * Time: 3:11:48 PM
 * To change this template use Options | File Templates.
 */
public class DomeRuntimeClient
{
	// client-side server
	private int port;                   // web server port
	private String url;                 // client url
	private WebServer webserver;


	// map of playspaces
	private HashMap playspaceMap = new HashMap();   // maps playspace static ids to objects


	public DomeRuntimeClient(int clientPort)
	{
		this.port = clientPort;
		this.url = "http://" + NetworkUtils.getIpAddress() + ":" + port + "/RPC2";
		while (!createClientWebServer()) {
			this.url = "http://" + NetworkUtils.getIpAddress() + ":" + (++port) + "/RPC2";
		}
		System.out.println("Client started at " + NetworkUtils.getIpAddress() + ":" + port);
	}

	protected boolean createClientWebServer() {
		try {
			webserver = new WebServer(port);
			webserver.addHandler(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT, new ConferenceClientHandler());
			webserver.addHandler(RuntimeConstants.FUNC_TYPE_RUNTIME, new RuntimeClientHandler(this));
			return true;
		}
		catch (IOException ie) {
			// todo: try another port number
			//throw new RuntimeException("Could not instantiate DomeRuntimeClient because of I/O exception");
			return false;
		}
	}

	public String getClientUrl()
	{
		return url;
	}


	/**
	 * Create a playspace or retrieve an existing one. Add the client as a member.
	 * @param playspaceStaticId Playspace static id
	 * @param svrConn Playspace server connection
	 * @return Playspace compound id
	 */
	public ClientPlayspaceRuntime joinPlayspace(String playspaceStaticId, ServerConnection svrConn)
	{
		// load playspace from server: get xml and runtime id
		ClientPlayspaceRuntime playspace = null;

		// see if playspace already exists
		CompoundId playspaceId = new CompoundId();
		playspaceId.setPlayspaceStaticId(playspaceStaticId);
		playspace = getPlayspace (playspaceId);

		// if playspace already exists, copy its runtime id
		if (playspace != null) {
			playspaceId.setPlayspaceRuntimeId(playspace.getRuntimeId());
		}
		else
		{
			// create playspace on the server side
			Vector v = RuntimeFunctionsClient.joinPlayspace(svrConn, playspaceStaticId);

			// create interface
			if (v.size() == 2) {
				// get parameters
				playspaceId = new CompoundId((String) v.get(0));
				String xmlContent = (String) v.get(1);

				// create the playspace
				Element playspaceElement = XMLUtils.stringToXmlElement(xmlContent);
				playspace = new ClientPlayspaceRuntime(playspaceId, svrConn, playspaceElement);
				playspace.addPropertyChangeListener(new PlayspaceListener());
				playspaceMap.put(playspaceId.getPlayspaceRuntimeId(), playspace);
				playspaceMap.put(playspaceId.getPlayspaceStaticId(), playspace);
				System.out.println("Joined playspace " + playspaceStaticId
								   + " (runid=\"" + playspaceId.getPlayspaceRuntimeId() + "\")\n");
			}
		}

		return playspace;
	}


	/**
	 * Retrieve an existing playspace.
	 * @param playspaceId Playspace compound id
	 * @return Playspace object
	 */
	public ClientPlayspaceRuntime getPlayspace(CompoundId playspaceId)
	{
		ClientPlayspaceRuntime playspace = null;

		// try to retrieve an existing playspace via runtime id
		String playspaceRuntimeId = playspaceId.getPlayspaceRuntimeId();
		if (playspaceRuntimeId != null)
			playspace = (ClientPlayspaceRuntime) playspaceMap.get(playspaceRuntimeId);
		if (playspace == null) { // try via static id
			String playspaceStaticId = playspaceId.getPlayspaceStaticId();
			if (playspaceStaticId != null) {
				playspace = (ClientPlayspaceRuntime) playspaceMap.get(playspaceStaticId);
			}
		}
		return playspace;
	}


	/**
	 * Create an interface in a transient or pre-defined playspace. The server connection
	 * passed in will be the same as that in the client in case of a transient playspace.
	 * Otherwise, the server connection will be that of a model whose interface we want
	 * to create.
	 * @param interfaceId Interface compound id
	 * @param playspace Playspace object
	 * @param ifaceConn Interface server connection
	 * @return Interface object
	 */
	public ModelInterfaceRuntimeClient createInterface(CompoundId interfaceId,
	                                                   ClientPlayspaceRuntime playspace,
	                                                   ServerConnection ifaceConn,
	                                                   boolean isProjectResource)
	{
		ModelInterfaceRuntimeClient iface = null;

		// create the interface in the playspace
		iface = playspace.createInterface(interfaceId, ifaceConn, isProjectResource);

		if (iface != null) {
			// set the playspace's runtime id and place it in the playspace map
			String playspaceRuntimeId = iface.getRuntimeId().getPlayspaceRuntimeId();
			playspace.setRuntimeId(playspaceRuntimeId);
			playspaceMap.put(playspaceRuntimeId, playspace);

			// if the playspace has a static id (i.e., it's a deployed playspace),
			// put the playspace in the playspace map
			String playspaceStaticId = iface.getRuntimeId().getPlayspaceStaticId();
			if (playspaceStaticId != null)
				playspaceMap.put(playspaceStaticId, playspace);

			Debug.trace(Debug.ALL, "Created iface " + iface.getName() + " (runid=\""
								   + iface.getRuntimeId().getInterfaceRuntimeId() + "\")\n");
		}

		return iface;
	}

    public OptimizationInterfaceRuntimeClient createAnalysisToolInterface(CompoundId interfaceId,
                                                                          ClientPlayspaceRuntime playspace,
                                                                          ServerConnection ifaceConn)
    {
        OptimizationInterfaceRuntimeClient iface = null;

        // create the interface in the playspace
        iface = playspace.createAnalysisToolInterface(interfaceId, ifaceConn);

        if (iface != null)
        {
            String playspaceRuntimeId = iface.getRuntimeId().getPlayspaceRuntimeId();
            playspace.setRuntimeId(playspaceRuntimeId);
            playspaceMap.put(playspaceRuntimeId, playspace);

            String playspaceStaticId = iface.getRuntimeId().getPlayspaceStaticId();
            if (playspaceStaticId != null)
                playspaceMap.put(playspaceStaticId, playspace);

            Debug.trace(Debug.ALL, "Created iface " + iface.getName() + " (runid=\""
								   + iface.getRuntimeId().getInterfaceRuntimeId() + "\")\n");
        }

        return iface;
    }


	/**
	 * Create a project in a transient or pre-defined playspace.
	 * @param projectId Project id
	 * @param playspace Playspace object
	 * @param projectConn Project server connection
	 * @return Integration project object
	 */
	public IntegrationProjectClientRuntime createProject(CompoundId projectId,
	                                                     ClientPlayspaceRuntime playspace,
	                                                     ServerConnection projectConn)
	{
		IntegrationProjectClientRuntime project = null;

		// create a new id--remove the playspace static id if the project is located
		// on a server different than where the playspace is located
		CompoundId newProjectId = new CompoundId (projectId);
		if (!projectConn.equals(playspace.getServerConnection()))
			newProjectId.setPlayspaceStaticId(null);

		// create the project in the playspace
		project = playspace.createProject(newProjectId, projectConn);

		if (project != null) {
			// set the playspace's runtime id and place it in the playspace map
			String playspaceRuntimeId = project.getRuntimeId().getPlayspaceRuntimeId();
			playspace.setRuntimeId(playspaceRuntimeId);
			playspaceMap.put(playspaceRuntimeId, playspace);
			projectId.setPlayspaceRuntimeId(playspaceRuntimeId);

			// if the playspace has a static id (i.e., it's a deployed playspace),
			// put the playspace in the playspace map
			String playspaceStaticId = project.getRuntimeId().getPlayspaceStaticId();
			if (playspaceStaticId != null)
				playspaceMap.put(playspaceStaticId, playspace);
			Debug.trace(Debug.ALL, "Created project " + project.getName() + " (runid=\""
								   + project.getRuntimeId().getFirstProjectRuntimeId() + "\")\n");
		}

		return project;
	}

    public OptimizationToolClientRuntime createOptimizationTool(CompoundId analysisToolId,
                                                                ClientPlayspaceRuntime playspace,
                                                                ServerConnection analysisToolConn)
    {
        OptimizationToolClientRuntime analysisTool = null;

        CompoundId newAnalysisToolId = new CompoundId (analysisToolId);
        if (!analysisToolConn.equals(playspace.getServerConnection()))
            newAnalysisToolId.setPlayspaceStaticId(null);

        analysisTool = playspace.getOptimizationTool(newAnalysisToolId, analysisToolConn);

        if (analysisTool != null)
        {
            String playspaceRuntimeId = analysisTool.getRuntimeId().getPlayspaceRuntimeId();
            playspace.setRuntimeId(playspaceRuntimeId);
            playspaceMap.put(playspaceRuntimeId, playspace);
            analysisToolId.setPlayspaceRuntimeId(playspaceRuntimeId);

            String playspaceStaticId = analysisTool.getRuntimeId().getPlayspaceStaticId();
            if (playspaceStaticId != null)
                playspaceMap.put(playspaceStaticId, playspace);
            Debug.trace(Debug.ALL, "Created analysis tool " + analysisTool.getName() + " (runid=\""
                                + analysisTool.getRuntimeId().getModelRuntimeId() + "\")\n");
        }

        return analysisTool;
    }

	/**
	 * Create a transient playspace and add a playspace listener.
	 * @param playspaceConn Playspace server connection
	 * @return Playspace object
	 */
	public ClientPlayspaceRuntime createTransientPlayspace (ServerConnection playspaceConn)
	{
		ClientPlayspaceRuntime playspace = new ClientPlayspaceRuntime(playspaceConn);
		playspace.addPropertyChangeListener(new PlayspaceListener());
		return playspace;
	}


	/**
	 * Listens to playspaces.
	 */
	private class PlayspaceListener implements PropertyChangeListener
	{
		public synchronized void propertyChange(PropertyChangeEvent evt)
		{
			if (ClientPlayspaceRuntime.PROPERTY_LEAVE.equals(evt.getPropertyName())) {
				ClientPlayspaceRuntime playspace = (ClientPlayspaceRuntime) evt.getSource();
				removePlayspace (playspace);
			}
		}
	}


	/**
	 * Inform the server that the client is leaving a playspace. Remove the
	 * playspace entry from the map.
	 * @param playspace Playspace object
	 */
	private void removePlayspace (ClientPlayspaceRuntime playspace)
	{
		CompoundId playspaceId = new CompoundId (playspace.getCompoundId());
		playspaceMap.remove(playspaceId.getPlayspaceStaticId());
		playspaceMap.remove(playspaceId.getPlayspaceRuntimeId());
	}


	/**
	 * Called by the runtime handler when a change request is received from the server.
	 * @param objectCompoundId Object id
	 * @param values List of changed values
	 */
	public void messageItemValueChanged(CompoundId objectCompoundId, List values)
	{
		// try to get the object from the global id map
		CompoundId ifaceId = new CompoundId(objectCompoundId);
		ifaceId.resetDataObjectRuntimeId();
		ModelInterfaceRuntimeClient iface;
		iface = (ModelInterfaceRuntimeClient) RuntimeFunctionsClient.getFromGlobalObjectMap(ifaceId);

		if (iface != null) {
			// get the interface parameter
			Parameter p = null;
			Id objectId = new Id(objectCompoundId.getDataObjectStaticId());
			if (objectId != null)
				p = (Parameter) iface.getInterfaceObjectsFlatMap().get(objectId);
			iface.setItem(p.getId(), values);
		} else {
			ClientPlayspaceRuntime playspace = null;
			playspace = getPlayspace(objectCompoundId);
			if (playspace != null)
				playspace.messageItemValueChanged(objectCompoundId, values);
		}
	}


	/**
	 * Called by the runtime handler when a change request is received from the server.
	 * @param objectCompoundId Object id
	 * @param status Status constant
	 */
	public void messageItemStatusChanged(CompoundId objectCompoundId, int changedId, String status)
	{
		// try to get the interface from the global object map
		CompoundId ifaceId = new CompoundId(objectCompoundId);
		ifaceId.resetDataObjectRuntimeId();
		ModelInterfaceRuntimeClient iface;
		iface = (ModelInterfaceRuntimeClient) RuntimeFunctionsClient.getFromGlobalObjectMap(ifaceId);

		if (iface != null) {
			// get the interface parameter
			Parameter p = null;
			Id objectId = new Id(objectCompoundId.getDataObjectStaticId());
			if (objectId != null)
				p = (Parameter) iface.getInterfaceObjectsFlatMap().get(objectId);
			iface.setStatus(p.getId(), changedId, status);
		} else {
			ClientPlayspaceRuntime playspace = null;
			playspace = getPlayspace(objectCompoundId);
			if (playspace != null)
				playspace.messageItemStatusChanged(objectCompoundId, changedId, status);
		}
	}

	/**
	 * Called by the runtime handler when a change request is received from the server.
	 * @param objectCompoundId Object id
	 * @param status Status constant
	 */
	public void modelStatusChanged(CompoundId objectCompoundId, String status)
	{
		CompoundId modelId = new CompoundId(objectCompoundId);
		modelId.resetDataObjectRuntimeId();
		modelId.setInterfaceRuntimeId(null);

		Object modelOrProject;
		modelOrProject = RuntimeFunctionsClient.getFromGlobalObjectMap(modelId);

		if (modelOrProject instanceof ClientRuntimeScope) {
			((ClientRuntimeScope) modelOrProject).setStatus(status);
		} else {
			ClientPlayspaceRuntime playspace = null;
			playspace = getPlayspace(modelId);
			if (playspace != null)
				playspace.modelStatusChanged(modelId, status);
			else if (ModelRuntime.STATUS_IFACE_CREATED.equals(status) ||
			        ModelRuntime.STATUS_IFACE_STARTING.equals(status) ||
			        ModelRuntime.STATUS_IFACE_PARENT_STARTING.equals(status)) {
				// ignore since playspace may not be registered yet
			} else
				System.err.println("DomeRuntimeClient.modelStatusChanged - unable to get playspace for "+objectCompoundId);
		}
	}

	/**
	 * Handle model execution error messages from the server.
	 * @param targetId Target parameter id
	 * @param objectName name of object generating error message
	 * @param msg Error message
	 */
	public void handleModelExecutionError(CompoundId targetId, String objectName, String msg) {
		// todo: find window for id and put it into focus; use interface names instead of model names
		if (objectName == null) { // find a model/interface name
			Object targetObj = RuntimeFunctionsClient.getFromGlobalObjectMap(targetId);
			if (targetObj instanceof ClientModelRuntime) {
				objectName = ((ClientModelRuntime) targetObj).getName();
			} else if (targetObj instanceof ModelInterfaceRuntimeClient) {
				objectName = ((ModelInterfaceRuntimeClient)targetObj).getName();
			} else if (targetObj instanceof mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient) {
				objectName = ((mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient) targetObj).getName();
			} else {
				System.err.println("handleModelExecutionError - unsupported object type: " + ClassUtils.getClassName(targetObj));
				objectName = "";
			}
		}
		System.err.println(objectName + " ERROR: " + msg);
		OneButton1Msg.showError(null, "Error in " + objectName, msg, "ok", null);
	}

    public void passIndividualToClient(CompoundId objectId, Vector v)
    {
        CompoundId ifaceId = new CompoundId(objectId);
        ifaceId.resetDataObjectRuntimeId();
        ClientPlayspaceRuntime playspace = null;
        playspace = getPlayspace(objectId);
        if (playspace != null)
            playspace.passIndividualToClient(objectId, v);
    }

    public void preparePlotForNextGeneration(CompoundId objectId)
    {
        CompoundId ifaceId = new CompoundId(objectId);
        ifaceId.resetDataObjectRuntimeId();
        ClientPlayspaceRuntime playspace = null;
        playspace = getPlayspace(objectId);
        if (playspace != null)
            playspace.preparePlotForNextGeneration(objectId);
    }

    public void optimizationAnalysisIsComplete(CompoundId objectId)
    {
        CompoundId ifaceId = new CompoundId(objectId);
        ifaceId.resetDataObjectRuntimeId();

        ClientPlayspaceRuntime playspace = getPlayspace(objectId);
        if (playspace != null)
                playspace.optimizationAnalysisIsComplete(objectId);
    }

	/**
	 * Submit a batch of parameter changes to the server.
	 * @param playspaceInterfaceCompoundId Playspace/interface id
	 */
	public void submitChanges(CompoundId playspaceInterfaceCompoundId)
	{
		ClientPlayspaceRuntime playspace = null;

		playspace = getPlayspace(playspaceInterfaceCompoundId);
		if (playspace != null)
			playspace.submitChanges(playspaceInterfaceCompoundId);
	}

    /***
     * return port number
     * @return
     */
    public int getPort() {
        return port;
    }

    /***
     * webserver is started running when client is constructed.
     * when this DomeRuntimeClient instance is no more used, we need to shutdown the webserver.
     * Please use this method to do that.  
     */
    public void shutdown() {
        webserver.shutdown();
    }
}