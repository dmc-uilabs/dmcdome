package mit.cadlab.dome3.objectmodel.playspace;

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord;
import mit.cadlab.dome3.network.server.RuntimeObjectInfo;
import mit.cadlab.dome3.objectmodel.ClientRuntimeScope;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.DSet;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Name: ClientPlayspaceRuntime
 * User: thorek
 * Date: Mar 13, 2003
 * Time: 2:59:28 PM
 * Copyright (c) 2003 Massachusetts Insitute of Technology. All rights reserved.
 */
public class ClientPlayspaceRuntime extends ClientPlayspace
{
	private RuntimeObjectInfo interfaceInfo = new RuntimeObjectInfo(); // interface static info/runtime object
	public final static String PROPERTY_LEAVE = "propertyLeave";    // playspace closed/client wants to leave

	private Object resourceLock = new Object();
	private boolean isTransient = false;

	private DSet playspaceConnections = new DSet(); // includes connections to items in playspace

	/**
	 * Create a transient playspace.
	 * @param serverConn Server connection
	 */
	public ClientPlayspaceRuntime(ServerConnection serverConn)
	{
		super(serverConn);
		if (serverConnection != null)
			serverConnection.addReference();
		isTransient = true;
	}

	/**
	 * Create a playspace from an XML description.
	 * @param runtimeId Runtime id (supplied by the server)
	 * @param serverConn Server connection
	 * @param xmlElement XML description
	 */
	public ClientPlayspaceRuntime(CompoundId runtimeId, ServerConnection serverConn, Element xmlElement)
	{
		super(serverConn, xmlElement);
		if (serverConnection != null) {
			serverConnection.addReference();
			playspaceConnections.add(serverConnection);
		}
		this.runtimeId = new CompoundId(runtimeId);

		// load project xml descriptions
		List projectElements = xmlElement.selectNodes("projects/" + IntegrationProject.XML_TAG);
		for (Iterator iter = projectElements.iterator(); iter.hasNext();) {
			Element projectElement = (Element) iter.next();
			addStaticObjectInfo(projectElement, projectInfo);
		}

		// load model xml descriptions
		List modelElements = xmlElement.selectNodes("models/" + DomeModelRuntime.XML_TAG);
		for (Iterator iter = modelElements.iterator(); iter.hasNext();) {
			Element modelElement = (Element) iter.next();
			addStaticObjectInfo(modelElement, modelInfo);
		}

        // load tool ids
        List toolElements = xmlElement.selectNodes("tools/" + OptimizationToolRuntime.XML_TAG);
        for (Iterator iter = toolElements.iterator(); iter.hasNext();) {
            Element toolElement = (Element) iter.next();
            addStaticObjectInfo(toolElement, analysisToolInfo);
        }
	}

	public void leavePlayspace()
	{
		firePropertyChange (PROPERTY_LEAVE);
		if (serverConnection != null) {
			if (!isTransient) {
				ServerConnection serverConnection;
				for (int i = 0; i < playspaceConnections.size(); i++) {
					serverConnection = (ServerConnection) playspaceConnections.get(i);
					RuntimeFunctionsClient.leavePlayspace(serverConnection, runtimeId);
				}
			}
			serverConnection.removeReference();
		}
	}

	public ServerConnection getServerConnection()
	{
		return serverConnection;
	}


	/**
	 * Set the runtime id of a transient playspace. The id comes from the server.
	 * @param runtimeId Runtime id string
	 */
	public void setRuntimeId(String runtimeId)
	{
		this.runtimeId.setPlayspaceRuntimeId(runtimeId);
	}

	/**
	 * Query the server playspace for a list of member names.
	 * @return List of members
	 */
	public List getMembers()
	{
		List memberList = RuntimeFunctionsClient.getPlayspaceMembers(serverConnection, runtimeId);
		return memberList;
	}


	/**
	 * Get a list of model records.
	 * @return List of model records
	 */
	public Collection getModels()
	{
		synchronized (resourceLock) {
			ArrayList list = new ArrayList();
			Collection staticIds = modelInfo.getAllStaticIds();
			for (Iterator modelIter = staticIds.iterator(); modelIter.hasNext();) {
				String id = (String) modelIter.next();
				String name = modelInfo.getName(id);
				String description = modelInfo.getDescription(id);
				String url = modelInfo.getUrl(id);
				// create a new model
				CompoundId modelId = new CompoundId (runtimeId);
				modelId.setModelStaticId(id);
				ClientModelRuntime model = new ClientModelRuntime(modelId, name, description, url);
				modelInfo.addObject(id, model);
				ClientModelRecord record = new ClientModelRecord(model, this);
				list.add(record);
			}
			return list;
		}
	}


	/**
	 * Get a list of project records.
	 * @return Collection of project records
	 */
	public Collection getProjects()
	{
		synchronized (resourceLock) {
			ArrayList list = new ArrayList();
			Collection staticIds = projectInfo.getAllStaticIds();
			for (Iterator modelIter = staticIds.iterator(); modelIter.hasNext();) {
				String id = (String) modelIter.next();
				String name = projectInfo.getName(id);
				String description = projectInfo.getDescription(id);
				String url = projectInfo.getUrl(id);
				CompoundId projectId = new CompoundId (runtimeId);
				projectId.addProjectStaticId(id);

				ClientProjectRecord record = new ClientProjectRecord(projectId, name, description, url,  this);
				//Jacob's code here:  record = new ClientProjectRecord(projectId, name, description, url, this);   ??//todo: to check with Jacob what he means
				list.add(record);
			}
			return list;
		}
	}

     /**
	 * Get a list of project records.
	 * @return Collection of project records
	 */
	public Collection getTools()
	{
		synchronized (resourceLock) {
			ArrayList list = new ArrayList();
			Collection staticIds = analysisToolInfo.getAllStaticIds();
			for (Iterator modelIter = staticIds.iterator(); modelIter.hasNext();) {
				String id = (String) modelIter.next();
				String name = analysisToolInfo.getName(id);
				String description = analysisToolInfo.getDescription(id);
				String url = analysisToolInfo.getUrl(id);
				CompoundId toolId = new CompoundId(runtimeId);
                toolId.setModelStaticId(id);//todo:  is this correct?? check with Jacob
                ServerConnection toolConn = LoginUtils.compareServersAndGetConnection(serverConnection,url);
                OptimizationToolClientRuntime tool=getOptimizationTool(toolId,toolConn);
                ClientAnalysisToolRecord record = new ClientAnalysisToolRecord(toolId, name, description, url, tool, this);
  				list.add(record);
			}
			return list;
		}
	}
	/**
	 * Query the server playspace for a list of interfaces associated with a given model.
	 * Add the static interface info to both the playspace's model info structure and
	 * the playspace's interface info structure. Return the info as a list.
	 * This method also instatiates all the interfaces for this model on the
	 * server.
	 * @param modelId Model id
	 * @param svrConn Server connection associated with the model
	 * @return List of interfaces
	 */
	public List getInterfaces(CompoundId modelId, ServerConnection svrConn)
	{
		ClientModelRuntime model = getModelRuntime(modelId, svrConn);
        model.instantiateAllInterfaces();
		return model.getInterfaceStaticInfo();
	}

    /**
	 * Query the server playspace for a list of interfaces associated with a given model.
	 * Add the static interface info to both the playspace's model info structure and
	 * the playspace's interface info structure. Return the info as a list.
	 * This method also instatiates all the interfaces for this model on the
	 * server.
	 * @param modelId Model id
	 * @param svrConn Server connection associated with the model
     * @param createIface to createInterface on the server side or not
	 * @return List of interfaces
	 */
	public List getInterfaces(CompoundId modelId, ServerConnection svrConn,boolean createIface)
	{
		ClientModelRuntime model = getModelRuntime(modelId, svrConn);
        if(createIface) model.instantiateAllInterfaces();
		return model.getInterfaceStaticInfo();
	}

	/**
	 * Create the runtime model object, which is a dumb model that contains a list
	 * of interfaces, a server connection and a reference to the playspace.
	 * This operation requires getting a list of model's interfaces
	 * and putting them in the interface info structures of the playspace and model.
	 * @param modelId Model id
	 * @param svrConn Server connection
	 * @return Runtime model object
	 */
	public ClientModelRuntime getModelRuntime(CompoundId modelId, ServerConnection svrConn)
	{
		ClientModelRuntime model;
		RuntimeObjectInfo modelInterfaces = new RuntimeObjectInfo();

		synchronized (resourceLock)
		{
			// get the model object
			model = (ClientModelRuntime) modelInfo.getObjectFromStaticId(modelId.getModelStaticId());

			//if the model doesn't exist or if it exists but doesn't have the interface information
			//then get the interface info from the server
			if (model == null || (model != null && model.getInterfaceStaticInfo().isEmpty())) {
				// get the interfaces info from the server and add it to the model
				Vector v = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, modelId.getModelStaticId(),
																		  PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
				for (Iterator ifaceIter = v.iterator(); ifaceIter.hasNext();) {
					Vector record = (Vector) ifaceIter.next();
					if (record.size() > 0) {
						interfaceInfo.addStaticInfo((String) record.get(1),
													(String) record.get(0),
													(String) record.get(2));
						modelInterfaces.addStaticInfo((String) record.get(1),
													  (String) record.get(0),
													  (String) record.get(2));
					}
				}
			}

			//if the model doesn't exist create the model by passing the interface info and
			//other parameters(see below)
			if (model == null) {
				// create a new model
				String modelStaticId = modelId.getModelStaticId();
				model = new ClientModelRuntime(modelId,
											   modelInfo.getName(modelStaticId),
											   modelInfo.getDescription(modelStaticId),
											   modelInterfaces,
											   svrConn, this);
				modelInfo.addObject(modelStaticId, model);
				RuntimeFunctionsClient.addToGlobalObjectMap(model.getCompoundId(), model);
				boolean isDistributedPlayspace = !serverConnection.equals(svrConn);
				if (isDistributedPlayspace)
					playspaceConnections.add(svrConn);
			} else { //store the interface info in the model instance
				if (model.getInterfaceStaticInfo().isEmpty()) {
					model.createInterfaceRecords(svrConn, modelInterfaces, this);
				}
			}


			return model;
		}
	}


	public IntegrationProjectClientRuntime getProject(CompoundId projectId)
	{
		// try to retrieve existing interface
		if (projectId != null) {
			CompoundId projectIdCopy = new CompoundId(projectId);
			projectIdCopy.setInterfaceStaticId(null); // delete interface data
			Object obj = RuntimeFunctionsClient.getFromGlobalObjectMap(projectIdCopy);
			if (obj instanceof IntegrationProjectClientRuntime)
				return (IntegrationProjectClientRuntime) obj;
		}
		return null;
	}

	public IntegrationProjectClientRuntime createProject(CompoundId projectId, ServerConnection svrConn) {
		// create project on the server side
		boolean isDistributedPlayspace = !serverConnection.equals(svrConn);
		Vector results = RuntimeFunctionsClient.createProject(svrConn, projectId, isDistributedPlayspace);

		// create local project
		if (results.size() == 3) {
			// get parameters
			CompoundId id = (CompoundId) results.get(0);
			String xml = (String) results.get(1);
			Hashtable idMap = (Hashtable) results.get(2);
			Element xmlElement = XMLUtils.stringToXmlElement(xml);


			// create the interface
			IntegrationProjectClientRuntime project = new IntegrationProjectClientRuntime(id, svrConn,
			                                              xmlElement,
			                                              idMap, this);
			projectInfo.addObject(id.getFirstProjectStaticId(),
			                      id.getFirstProjectRuntimeId(),
			                      project);

			RuntimeFunctionsClient.addToGlobalObjectMap(project.getRuntimeId(), project);
			if (isDistributedPlayspace)
				playspaceConnections.add(svrConn);
			return project;
		}
		return null;
	}

    public OptimizationToolClientRuntime getOptimizationTool(CompoundId analysisToolId, ServerConnection svrConn)
    {
        OptimizationToolClientRuntime analysisTool = null;

        String analysisToolStaticId = analysisToolId.getModelStaticId();

        synchronized (resourceLock)
		{
			if (analysisToolStaticId != null)
			{
				analysisTool = (OptimizationToolClientRuntime) analysisToolInfo.getObjectFromStaticId(analysisToolStaticId);

				// get analysis tool from the server
				if (analysisTool == null) {
					// create analysis tool on the server side
					boolean isDistributedPlayspace = !serverConnection.equals(svrConn);
					Vector results = RuntimeFunctionsClient.createAnalysisTool(svrConn, analysisToolId, isDistributedPlayspace);

					// create local analysis tool
					if (results.size() == 5) {
						// get parameters
						CompoundId id = (CompoundId) results.get(0);
                        String xml = (String) results.get(1);
                        CompoundId projectId = (CompoundId) results.get(2);
                        String projectName = (String) results.get(3);
                        String projectDescription = (String) results.get(4);
                        Element xmlElement = XMLUtils.stringToXmlElement(xml);

						// create the interface
						analysisTool = new OptimizationToolClientRuntime(id, svrConn, xmlElement, projectId, projectName, projectDescription, this);
                        /*projectInfo.addObject(projectId.getFirstProjectStaticId(),
                                              projectId.getFirstProjectRuntimeId(),
                                              ((IntegrationProjectClientRuntime)analysisTool.getIntegrationProject()));
                        analysisTool.getIntegrationProject().addPropertyChangeListener(new ProjectListener());*/
                        analysisToolInfo.addObject(id.getModelStaticId(),
											  id.getModelRuntimeId(),
											  analysisTool);

						if (isDistributedPlayspace)
							playspaceConnections.add(svrConn);
					}
				}
			}

			return analysisTool;
		}
    }


	/**
	 * Retrieve an existing interface object from its runtime id.
	 * @param interfaceId Interface id
	 * @return Interface object
	 */
	private ModelInterfaceRuntimeClient getInterface(CompoundId interfaceId)
	{
		ModelInterfaceRuntimeClient iface = null;

		synchronized (resourceLock)
		{
			if (interfaceId.getFirstProjectRuntimeId() != null) {
				IntegrationProjectClientRuntime project;
				String projectRuntimeId = interfaceId.getFirstProjectRuntimeId();
				project = (IntegrationProjectClientRuntime) projectInfo.getObjectFromRuntimeId(projectRuntimeId);
				iface = project.getInterface(interfaceId);
			} else {
				// retrieve the interface
				String ifaceRuntimeId = interfaceId.getInterfaceRuntimeId();
				iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromRuntimeId(ifaceRuntimeId);
			}
		}

		return iface;
	}

    private OptimizationInterfaceRuntimeClient getAnalysisToolInterface(CompoundId interfaceId)
    {
        mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient iface = null;

        synchronized (resourceLock)
        {
            String interfaceRuntimeId = interfaceId.getInterfaceRuntimeId();
            iface = (OptimizationInterfaceRuntimeClient) interfaceInfo.getObjectFromRuntimeId(interfaceRuntimeId);
        }

        return iface;
    }

	/**
	 * Try to retrieve an existing interface object.
	 * @param interfaceId Interface id
	 * @param ifaceSvrConn Interface server connection
	 * @return Interface object
	 */
	public ModelInterfaceRuntimeClient getInterface(CompoundId interfaceId,
	                                                ServerConnection ifaceSvrConn)
	{
		// try to retrieve existing interface
		if (interfaceId != null) {
			Object obj = RuntimeFunctionsClient.getFromGlobalObjectMap(interfaceId);
			if (obj instanceof ModelInterfaceRuntimeClient)
				return (ModelInterfaceRuntimeClient) obj;
		}
		return null;
	}

    public OptimizationInterfaceRuntimeClient getOptimizationToolInterface(CompoundId interfaceId, ServerConnection ifacesSvrConn)
    {
        OptimizationInterfaceRuntimeClient iface = null;

        synchronized (resourceLock)
        {
            // try to retrieve existing interface
            if (interfaceId != null)
                iface = (OptimizationInterfaceRuntimeClient) interfaceInfo.getObjectFromStaticId(interfaceId.getInterfaceStaticId());

            return iface;
        }
    }



	/**
	 * Create a new interface given the interface deploy id.
	 * @param interfaceId Interface static id
	 * @param ifaceSvrConn Interface server connection
	 * is being created in a distributed playspace. A distributed playspace is one
	 * that is created on a server other than where the deployed description lives.
	 * This allows models to be deployed on servers other than where the playspace
	 * has been deployed.
	 * @return
	 */
	public ModelInterfaceRuntimeClient createInterface(CompoundId interfaceId,
	                                                   ServerConnection ifaceSvrConn,
	                                                   boolean isProjectResource)
	{
		synchronized (resourceLock) // only allow one interface to be created at a time (?)
		{
			ModelInterfaceRuntimeClient iface = null;
			// create interface on the server side; this call is *always* made, because the server
			// has to keep track of the number of open client connections to each interface
			boolean isDistributedPlayspace = !serverConnection.equals(ifaceSvrConn);
			// isDistributedPlayspace: flag indicating whether the interface
			// should be created in a distributed playspace. A distributed playspace is one
			// that is created on a server other than where the deployed description lives.
			// This allows models to be deployed on servers other than where the playspace
			// has been deployed.
			boolean joinPlayspace = playspaceConnections.contains(ifaceSvrConn);
			if (isProjectResource) {
				Vector v = RuntimeFunctionsClient.createInterface(ifaceSvrConn,
				                                                  interfaceId,
				                                                  isDistributedPlayspace,
				                                                  joinPlayspace, isProjectResource);
				// create interface
				if (v.size() == 2) {
					// get parameters
					interfaceId = (CompoundId) v.get(0);
					String xmlContent = (String) v.get(1);
					Element ifaceElement = XMLUtils.stringToXmlElement(xmlContent);

					// use the model id to create the client model runtime object
					ClientModelRuntime model = getModelRuntime(interfaceId, ifaceSvrConn);

					// create the interface
					iface = new ModelInterfaceRuntimeClient(interfaceId, ifaceSvrConn,
					                                        model, ifaceElement);
					if (joinPlayspace)
						playspaceConnections.add(ifaceSvrConn);
				}
			} else {
				Vector v = RuntimeFunctionsClient.createInterfaceQuick(ifaceSvrConn,
				                                                       interfaceId,
				                                                       isDistributedPlayspace,
				                                                       joinPlayspace);
				// create interface
				if (v.size() == 3) {
					// get parameters
					interfaceId = (CompoundId) v.get(0);
					String xmlContent = (String) v.get(1);
					Element ifaceElement = XMLUtils.stringToXmlElement(xmlContent);
					boolean ifaceCreated = ((Boolean) v.get(2)).booleanValue();

					if (interfaceId.getFirstProjectRuntimeId() != null) { // project interface
						IntegrationProjectClientRuntime project = getProject(interfaceId);
						if (project == null) {
							project = new IntegrationProjectClientRuntime(interfaceId, ifaceSvrConn, this);
							RuntimeFunctionsClient.addToGlobalObjectMap(project.getRuntimeId(), project);
						}
						projectInfo.addObject(interfaceId.getFirstProjectStaticId(),
						                      interfaceId.getFirstProjectRuntimeId(),
						                      project);
						iface = new ModelInterfaceRuntimeClient(interfaceId, ifaceSvrConn,
						                                        project, ifaceElement, ifaceCreated);
					} else { // model interface
						// use the model id to create the client model runtime object
						ClientModelRuntime model = getModelRuntime(interfaceId, ifaceSvrConn); // fudge - may be a tool?
						modelInfo.addObject(interfaceId.getModelStaticId(), model);
						//RuntimeFunctionsClient.addToGlobalObjectMap(model.getCompoundId(), model); // shouldn't be necessary
						iface = new ModelInterfaceRuntimeClient(interfaceId, ifaceSvrConn,
						                                        model, ifaceElement, ifaceCreated);
					}
					if (joinPlayspace)
						playspaceConnections.add(ifaceSvrConn);
				}
			}

			if (iface != null) {
				interfaceId = iface.getRuntimeId();
				// store the interface locally
				interfaceInfo.addObject(interfaceId.getInterfaceStaticId(),
				                        interfaceId.getInterfaceRuntimeId(),
				                        iface);
				RuntimeFunctionsClient.addToGlobalObjectMap(interfaceId, iface); // can get messages now
			}

			return iface;
		}
	}

    public OptimizationInterfaceRuntimeClient createAnalysisToolInterface(CompoundId interfaceId,
                                                                          ServerConnection ifaceSvrConn)
    {
        OptimizationInterfaceRuntimeClient iface = null;

        synchronized (resourceLock)
        {
            if (iface == null)
            {
                boolean isDistributedPlayspace = !serverConnection.equals(ifaceSvrConn);
	            boolean joinPlayspace = playspaceConnections.contains(ifaceSvrConn);
                Vector v = RuntimeFunctionsClient.createAnalysisToolInterface(ifaceSvrConn,
                                                                              interfaceId,
                                                                              isDistributedPlayspace,
                                                                              joinPlayspace);

                if (v.size() == 2)
                {
                    interfaceId = (CompoundId) v.get(0);
                    String xmlContent = (String) v.get(1);

                    ClientModelRuntime model = getModelRuntime(interfaceId, ifaceSvrConn);

                    Element ifaceElement = XMLUtils.stringToXmlElement(xmlContent);
                    iface = new OptimizationInterfaceRuntimeClient(interfaceId, ifaceSvrConn,
                                                                   model, ifaceElement);
	                if (joinPlayspace)
		                playspaceConnections.add(ifaceSvrConn);
                }
            }

            if (iface != null)
            {
                interfaceId = iface.getRuntimeId();

                interfaceInfo.addObject(interfaceId.getInterfaceStaticId(),
                                        interfaceId.getInterfaceRuntimeId(),
                                        iface);
            }
        }

        return iface;

    }

	/**
	 * Submit all changed parameters to the server and run the necessary models.
	 * @param playspaceInterfaceCompoundId Playspace/interface id
	 */
	public void submitChanges(CompoundId playspaceInterfaceCompoundId)
	{
		ModelInterfaceRuntimeClient iface;
		iface = getInterface(playspaceInterfaceCompoundId);

		if (iface != null)
			iface.submitChanges();
	}

	/**
	 * Cycle through the playspace's models and projects, collecting interface change maps
	 * and submitting the maps to the server
	 */
	public void submitChanges()
	{
		synchronized (resourceLock)
		{
			// cycle through models
			for (Iterator modelIter = modelInfo.getAllStaticIds().iterator(); modelIter.hasNext();) {
				// get next model
				ClientModelRuntime model;
				String staticId = (String) modelIter.next();
				model = (ClientModelRuntime) modelInfo.getObjectFromStaticId(staticId);

				if (model != null) {
					// collect change maps for each of the model's interfaces
					Vector changeMaps = new Vector();
					List ifaces = model.getInterfaceStaticInfo();
					for (Iterator ifaceIter = ifaces.iterator(); ifaceIter.hasNext();) {
						// get next interface
						ClientInterfaceRecord ifaceRec = (ClientInterfaceRecord) ifaceIter.next();
						String ifaceId = ifaceRec.getStaticId();
						ModelInterfaceRuntimeClient iface;

						synchronized (interfaceInfo) {
							iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromStaticId(ifaceId);

							// add its change map to the list of change maps
							if (iface != null) {
								Hashtable changeMap = iface.getAndResetChangeMap();
								if (!changeMap.isEmpty()) {
									changeMaps.add(changeMap);
								}
							}
						}
					}
					// send the list of change maps
					if (!changeMaps.isEmpty()) {
						ServerConnection svrConn = model.getServerConnection();
						RuntimeFunctionsClient.setItems(svrConn, changeMaps, true);
					}
				}
			}

			// cycle through projects
			for (Iterator projIter = projectInfo.getAllStaticIds().iterator(); projIter.hasNext();) {
				IntegrationProjectClientRuntime project;
				String staticId = (String) projIter.next();
				project = (IntegrationProjectClientRuntime) projectInfo.getObjectFromStaticId(staticId);
				project.submitChanges();
			}
		}
	}


	/**
	 * Change the value of an interface parameter in response to a message from the server.
	 * @param objectCompoundId Object id
	 * @param values List of new values
	 */
	public void messageItemValueChanged(CompoundId objectCompoundId, List values)
	{
		ModelInterfaceRuntimeClient iface;
		iface = getInterface(objectCompoundId);

		if (iface != null) {
			// get the interface parameter
			Parameter p = null;
			Id objectId = new Id(objectCompoundId.getDataObjectStaticId());
			if (objectId != null)
				p = (Parameter) iface.getInterfaceObjectsFlatMap().get(objectId);
			iface.setItem(p.getId(), values);
		}
	}


	/**
	 * Change the solving status of an interface parameter.
	 * @param objectCompoundId Object id
	 * @param status Status constant
	 */
	public void messageItemStatusChanged(CompoundId objectCompoundId, int changedId, String status)
	{
		ModelInterfaceRuntimeClient iface;
		iface = getInterface(objectCompoundId);

		if (iface != null) {
			// get the interface parameter
			Parameter p = null;
			Id objectId = new Id(objectCompoundId.getDataObjectStaticId());
			if (objectId != null)
				p = (Parameter) iface.getInterfaceObjectsFlatMap().get(objectId);
			iface.setStatus(p.getId(), changedId, status);
		}
	}


	/**
	 * Retrieve an existing model object from its runtime id.
	 * @param modelId Model id
	 * @return Model object
	 */
	private ClientRuntimeScope getModel(CompoundId modelId)
	{
		ClientRuntimeScope model = null;

		synchronized (resourceLock) {
			if (modelId.getFirstProjectRuntimeId() != null) {
				IntegrationProjectClientRuntime project;
				String projectRuntimeId = modelId.getFirstProjectRuntimeId();
				project = (IntegrationProjectClientRuntime) projectInfo.getObjectFromRuntimeId(projectRuntimeId);
				if (modelId.getModelRuntimeId() != null)
					model = project.getRuntimeModel(modelId);
				else
					model = project;
			} else {
				// retrieve the model
				model = (ClientRuntimeScope) modelInfo.getObjectFromRuntimeId(modelId.getModelRuntimeId());
				if (model == null)
					model = (ClientRuntimeScope) modelInfo.getObjectFromStaticId(modelId.getModelStaticId());
			}
		}

		return model;
	}


	/**
	 * Change the run status of a model.
	 * @param modelId MOdel id
	 * @param status Status constant
	 */
	public void modelStatusChanged(CompoundId modelId, String status)
	{
		ClientRuntimeScope runmodel = getModel(modelId);
		if (runmodel != null) {
			runmodel.setStatus(status);
		}
		else
			System.err.println("modelStatusChanged - "+status+" - could not find model: "+modelId);
	}

    public void passIndividualToClient(CompoundId objectId, Vector v)
    {
        OptimizationInterfaceRuntimeClient iface;
        iface = getAnalysisToolInterface(objectId);
        if (iface != null)
            iface.addIndividualToClient(v);
    }

    public void preparePlotForNextGeneration(CompoundId objectId)
    {
        OptimizationInterfaceRuntimeClient iface = null;
        iface = getAnalysisToolInterface(objectId);
        if (iface != null)
            iface.preparePlotForNewData();
    }

    public void optimizationAnalysisIsComplete(CompoundId objectId)
    {
        OptimizationInterfaceRuntimeClient iface = null;
        iface = getAnalysisToolInterface(objectId);
        if (iface != null)
            iface.optimizationAnalysisIsComplete();
    }
}