package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.server.RuntimeObjectInfo;
import mit.cadlab.dome3.objectmodel.ClientRuntimeScope;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfoRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * Name: IntegrationProjectClientRuntime
 * User: thorek
 * Date: Apr 16, 2003
 * Time: 5:54:38 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class IntegrationProjectClientRuntime extends AbstractIntegrationProject implements ClientRuntimeScope
{
	public static final String PROJECTSTATUS = "projectStatus";

	private Hashtable resourceIdMap;
	private CompoundId projectId;
	private RuntimeObjectInfo interfaceInfo = new RuntimeObjectInfo();  // resource model interfaces
	protected RuntimeObjectInfo projectInfo = new RuntimeObjectInfo();  // subprojects
	protected ServerConnection serverConnection = null;
	private ClientPlayspaceRuntime parentPlayspace = null;
	private String status = "";
	private Object resourceLock = new Object();

	public IntegrationProjectClientRuntime(CompoundId projectId, ServerConnection serverConn,
	                                       Element xmlElement, Hashtable resourceIdMap,
	                                       ClientPlayspaceRuntime playspace)
	{
		super(xmlElement);
		this.resourceIdMap = resourceIdMap;
		this.projectId = new CompoundId(projectId);
		this.projectId.setInterfaceStaticId(null); // clear interfaceInfo
		this.serverConnection = serverConn;
		this.parentPlayspace = playspace;
		if (serverConnection != null)
			serverConnection.addReference();

		if (serverConn != null)
			serverConn.addReference();

		// key the integration model info structures by their deploy id
		Object[] iModels = projectIntegrationModels.keySet().toArray();
		for (int i = 0; i < iModels.length; i++) {
			String id = (String) iModels[i];
			ProjectIntegrationModelInfoRuntime iModel;
			iModel = (ProjectIntegrationModelInfoRuntime) projectIntegrationModels.get(id);
			String deployId = (String) resourceIdMap.get(iModel.getId());
			projectIntegrationModels.put(deployId, iModel);

		}
	}

	public IntegrationProjectClientRuntime(CompoundId projectId, ServerConnection serverConn,
	                                       ClientPlayspaceRuntime playspace)
	{
		super(new Id(projectId.getFirstProjectRuntimeId()));
		this.projectId = new CompoundId(projectId);
		this.projectId.setInterfaceStaticId(null); // clear interfaceInfo
		this.serverConnection = serverConn;
		this.parentPlayspace = playspace;
		if (serverConnection != null)
			serverConnection.addReference();

		if (serverConn != null)
			serverConn.addReference();
	}

	/**
	 * Retrieves runtime id for resources and imodels by imodel build or deploy id or resource unique id
	 * @param id
	 * @return
	 */
	public String getResourceRuntimeId(String id)
	{
		return (String) resourceIdMap.get(id);
	}

	public ConnectionMappingManager getMappingManager()
	{
		if(mappingManager == null) {
			mappingManager = new ConnectionMappingManagerRuntime(this);
		}
		return mappingManager;
	}

	//from AbstractIntegrationProject
	protected ModelInterfaceManager createInterfacesManager()
	{
		return null;
	}

	/**
	 * Query the server playspace for a list of interfaces associated with a given model.
	 * Add the static interface info to both the playspace's model info structure and
	 * the playspace's interface info structure. Return the info as a list.
	 * @param modelId Model id
	 * @param svrConn Server connection associated with the model
	 * @return List of interfaces
	 */
	public List getInterfaces(String modelId, ServerConnection svrConn)
	{
		synchronized (resourceLock) {
			ClientModelRuntime model = getModelRuntime(modelId, svrConn);
			return model.getInterfaceStaticInfo();
		}
	}


	/**
	 * Query the server playspace for a list of interfaces associated with a given model.
	 * Add the static interface info to both the playspace's model info structure and
	 * the playspace's interface info structure. Return the info as a list.
	 * @param modelId Model id
	 * @return List of interfaces
	 */
	public List getInterfaces(String modelId)
	{
		synchronized (resourceLock) {
			ClientModelRuntime model = getModelRuntime(modelId, null);
			return model.getInterfaceStaticInfo();
		}
	}


	/**
	 * Create the runtime model object, which is a dumb model that contains a list
	 * of interfaces, a server connection and a reference to the playspace.
	 * This operation requires getting a list of model's interfaces
	 * and putting them in the interface info structures of the playspace and model.
	 * @param modelRuntimeId Model id
	 * @param svrConn Server connection
	 * @return Runtime model object
	 */
	private ClientModelRuntime getModelRuntime(String modelRuntimeId, ServerConnection svrConn)
	{
		String name, description = null;
		ClientModelRuntime model = null;
		RuntimeObjectInfo modelInterfaces = new RuntimeObjectInfo();

		synchronized (resourceLock) {

			// get the model object
			if (projectResourceModels.get(modelRuntimeId) != null) {
				// check the connection and get a new one if the model is located elsewhere
				ProjectResourceInfoRuntime modelResource;
				modelResource = (ProjectResourceInfoRuntime) projectResourceModels.get(modelRuntimeId);
				model = (ClientModelRuntime) modelResource.getObject();
				// get the name and description
				name = modelResource.getName();
				description = modelResource.getResourceDescription();
			}
			else
			if (projectResourceModelsbyDeployId.get(modelRuntimeId) != null) {
				// check the connection and get a new one if the model is located elsewhere
				ProjectResourceInfoRuntime modelResource;
				modelResource = (ProjectResourceInfoRuntime) projectResourceModelsbyDeployId.get(modelRuntimeId);
				model = (ClientModelRuntime) modelResource.getObject();
				// get the name and description
				name = modelResource.getName();
				description = modelResource.getResourceDescription();
			}
			else {
				// get the name and description
				ProjectIntegrationModelInfoRuntime iModelResource;
				iModelResource = (ProjectIntegrationModelInfoRuntime) projectIntegrationModels.get(modelRuntimeId);
				model = (ClientModelRuntime) iModelResource.getObject();
				// get the name
				name = iModelResource.getName();
			}

			//if the model doesn't exist or if it exists but doesn't have the interface information
			//then get the interface info from the server
			if (model == null || (model != null && model.getInterfaceStaticInfo().isEmpty())) {
				// get the interfaces info from the server and add it to the model
				Vector v = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, modelRuntimeId,
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
				String modelId = (String) resourceIdMap.get(modelRuntimeId);
				CompoundId modelCompoundId = new CompoundId(modelId);
				model = new ClientModelRuntime(modelCompoundId,
				                               name,
				                               description,
				                               modelInterfaces,
				                               svrConn, parentPlayspace);

				// set the model object
				if (projectResourceModels.get(modelRuntimeId) != null) {
					ProjectResourceInfoRuntime modelResource;
					modelResource = (ProjectResourceInfoRuntime) projectResourceModels.get(modelRuntimeId);
					modelResource.setObject(model);
				}
				if (projectResourceModelsbyDeployId.get(modelRuntimeId) != null) {
					ProjectResourceInfoRuntime modelResource;
					modelResource = (ProjectResourceInfoRuntime) projectResourceModelsbyDeployId.get(modelRuntimeId);
					modelResource.setObject(model);
				}
				else {
					// get the name and description
					ProjectIntegrationModelInfoRuntime iModelResource;
					iModelResource = (ProjectIntegrationModelInfoRuntime) projectIntegrationModels.get(modelRuntimeId);
					iModelResource.setObject(model);
				}

			}
			else { //store the interface info in the model instance
				if (model.getInterfaceStaticInfo().isEmpty()) {
					model.createInterfaceRecords(svrConn, modelInterfaces, null);
				}
			}


			return model;
		}
	}


	public ModelInterfaceRuntimeClient getInterface(CompoundId interfaceId)
	{
		ModelInterfaceRuntimeClient iface = null;

		synchronized (resourceLock) {
			String projectRuntimeId = interfaceId.getNextProjectRuntimeId();
			if (projectRuntimeId != null) {
				ProjectResourceInfoRuntime info = (ProjectResourceInfoRuntime)projectResourceModels.get(projectRuntimeId);
				Object obj = info.getObject();
				if(obj instanceof IntegrationProjectClientRuntime)   {
					IntegrationProjectClientRuntime project = (IntegrationProjectClientRuntime)obj;
					iface = project.getInterface(interfaceId);
				}
			}
			else {
				// retrieve the interface
				String ifaceRuntimeId = interfaceId.getInterfaceRuntimeId();
				iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromRuntimeId(ifaceRuntimeId);
			}

			return iface;
		}
	}


	public ClientRuntimeScope getRuntimeModel(CompoundId modelId)
	{
		ClientRuntimeScope model = null;

		CompoundId newModelId = new CompoundId(modelId);

		synchronized (resourceLock) {
			// look inside subprojects
			String projectRuntimeId = newModelId.getNextProjectRuntimeId();
			if (projectRuntimeId != null) {
				ProjectResourceInfoRuntime resource;
				IntegrationProjectClientRuntime project;
				resource = (ProjectResourceInfoRuntime) getResource(projectRuntimeId);
				project = (IntegrationProjectClientRuntime) resource.getObject();
				model = project.getRuntimeModel(newModelId);
			}
			else {   //no subprojects: look in iModels and resource models
				ProjectIntegrationModelInfoRuntime info = (ProjectIntegrationModelInfoRuntime)projectIntegrationModels.get(
				        modelId.getModelStaticId());
				if(info != null) {
					model = (ClientRuntimeScope)info.getObject();
				}
				else {
					ProjectResourceInfoRuntime resource = (ProjectResourceInfoRuntime) projectResourceModels.get(modelId.getModelRuntimeId());
					if(resource != null) {
						model = (ClientRuntimeScope) resource.getObject();
					}
					//  get the model from the interface
					else {
						ModelInterfaceRuntimeClient iface;
						String ifaceStaticId = modelId.getInterfaceStaticId();
						iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromStaticId(ifaceStaticId);
						model = iface.getModelRuntime();
					}
				}
			}
		}

		return model;
	}


	public void expandModel(String modelUniqueId, ServerConnection ifaceSvrConn)
	{
		String name, description = null;
		String modelId = (String) resourceIdMap.get(modelUniqueId);
		CompoundId modelCompoundId = new CompoundId(modelId);

		synchronized (resourceLock) {
			// get some information from the model info structure
			if (projectResourceModels.get(modelUniqueId) != null) {
				// check the connection and get a new one if the model is located elsewhere
				ProjectResourceInfoRuntime modelResource;
				modelResource = (ProjectResourceInfoRuntime) projectResourceModels.get(modelUniqueId);
				// get the name and description
				name = modelResource.getName();
				description = modelResource.getResourceDescription();
				if (modelResource.getObject() != null)
					return;
			}
			//When modelUniqueId is resources static or deploy id
			else if (projectResourceModelsbyDeployId.get(modelUniqueId) != null) {
				// check the connection and get a new one if the model is located elsewhere
				ProjectResourceInfoRuntime modelResource;
				modelResource = (ProjectResourceInfoRuntime) projectResourceModelsbyDeployId.get(modelUniqueId);
				// get the name and description
				name = modelResource.getName();
				description = modelResource.getResourceDescription();
				modelCompoundId.setModelStaticId(modelUniqueId);
				if (modelResource.getObject() != null)
					return;
			}
			else {
				// get the name and description
				ProjectIntegrationModelInfoRuntime iModelResource;
				iModelResource = (ProjectIntegrationModelInfoRuntime) projectIntegrationModels.get(modelUniqueId);
				name = iModelResource.getName();
				if (iModelResource.getObject() != null)
					return;
			}

			// expand the model: get the interfaces info from the server and add it to the model
			Vector v = FileSystemFunctions.getAvailableInterfacesInfo(ifaceSvrConn,
																	  modelCompoundId.getModelStaticId(),
																	  PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
			for (Iterator ifaceIter = v.iterator(); ifaceIter.hasNext();) {
				Vector record = (Vector) ifaceIter.next();
				if (record.size() > 0) {
					// extract the interface info
					interfaceInfo.addStaticInfo((String) record.get(1),
												(String) record.get(0),
												(String) record.get(2));
					RuntimeObjectInfo modelInterfaces = new RuntimeObjectInfo();
					modelInterfaces.addStaticInfo((String) record.get(1),
												  (String) record.get(0),
												  (String) record.get(2));
					// create a model and add the interface info to it
					ClientModelRuntime model = new ClientModelRuntime(modelCompoundId,
																	  name,
																	  description,
																	  modelInterfaces,
																	  ifaceSvrConn,
																	  parentPlayspace);

					// add to the global id object map
					RuntimeFunctionsClient.addToGlobalObjectMap(modelCompoundId, model);

					// put the model object in the resource structure
					if (projectResourceModels.get(modelUniqueId) != null) {
						// check the connection and get a new one if the model is located elsewhere
						ProjectResourceInfoRuntime modelResource;
						modelResource = (ProjectResourceInfoRuntime) projectResourceModels.get(modelUniqueId);
						modelResource.setObject(model);
					}
					else if (projectResourceModelsbyDeployId.get(modelUniqueId) != null) {
						// check the connection and get a new one if the model is located elsewhere
						ProjectResourceInfoRuntime modelResource;
						modelResource = (ProjectResourceInfoRuntime) projectResourceModelsbyDeployId.get(modelUniqueId);
						modelResource.setObject(model);
					}
					else {
						// get the name and description
						ProjectIntegrationModelInfoRuntime iModelResource;
						iModelResource = (ProjectIntegrationModelInfoRuntime) projectIntegrationModels.get(modelUniqueId);
						iModelResource.setObject(model);
					}
				}
			}
		}
	}


	/**
	 * Create a new interface given the interface deploy id.
	 * @param interfaceId Interface static id
	 * @param ifaceSvrConn Interface server connection
	 * @return
	 */
	public ModelInterfaceRuntimeClient createInterface(CompoundId interfaceId,
	                                                   ServerConnection ifaceSvrConn,
	                                                   boolean isProjectResource)
	{
		ModelInterfaceRuntimeClient iface = null;

		synchronized (resourceLock)
		{
			// see if the interface lives inside a project
			IntegrationProjectClientRuntime project = getProject(interfaceId, ifaceSvrConn);
			if (project != null) {
				// any sub-project creation will be handled by the project's load interface method
				// the interface returned will live inside the deepest sub-project
				interfaceId.getNextProjectRuntimeId(); //increment the id pointer
				iface = project.createInterface(interfaceId, ifaceSvrConn, isProjectResource);
			}

			// create model runtime structures if the interface's parent is a model
			if (iface == null)
			{
				if (interfaceId.getModelStaticId() != null)
					expandModel(interfaceId.getModelStaticId(), ifaceSvrConn);

				// try to retrieve existing interface
				if (interfaceId != null)
					iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromStaticId(interfaceId.getInterfaceStaticId());

				// get interface from the server
				if (iface == null) {
					// create interface on the server side
					boolean isDistributedPlayspace = !ifaceSvrConn.equals(serverConnection);
					// isDistributedPlayspace: flag indicating whether the interface
					// should be created in a distributed playspace. A distributed playspace is one
					// that is created on a server other than where the deployed description lives.
					// This allows models to be deployed on servers other than where the playspace
					// has been deployed.
					if (isProjectResource) {
						Vector v = RuntimeFunctionsClient.createInterface(ifaceSvrConn, interfaceId,
																		  isDistributedPlayspace, true, isProjectResource);

						// create interface
						if (v.size() == 2) {
							// get parameters
							interfaceId = (CompoundId) v.get(0);
							String xmlContent = (String) v.get(1);
							Element ifaceElement = XMLUtils.stringToXmlElement(xmlContent);

							// create the interface
							String modelId = interfaceId.getModelStaticId();
							if (modelId != null) {
								// use the model id to create the client model runtime object
								ClientModelRuntime model = getModelRuntime(modelId, ifaceSvrConn);
								iface = new ModelInterfaceRuntimeClient(interfaceId, ifaceSvrConn, model, ifaceElement);
							}
							else {
								iface = new ModelInterfaceRuntimeClient(interfaceId, serverConnection, this, ifaceElement);
							}


							// add to interface map
							interfaceInfo.addObject(interfaceId.getInterfaceStaticId(),
													interfaceId.getInterfaceRuntimeId(),
													iface);

							// add to the global id object map
							RuntimeFunctionsClient.addToGlobalObjectMap(interfaceId, iface);
						}
					} else {
						Vector v = RuntimeFunctionsClient.createInterfaceQuick(ifaceSvrConn, interfaceId,
						                                                  isDistributedPlayspace, true);
						// create interface
						if (v.size() == 3) {
							// get parameters
							interfaceId = (CompoundId) v.get(0);
							String xmlContent = (String) v.get(1);
							Element ifaceElement = XMLUtils.stringToXmlElement(xmlContent);
							boolean ifaceCreated = ((Boolean) v.get(2)).booleanValue();

							String modelId = interfaceId.getModelStaticId();
							if (modelId != null) {
								// use the model id to create the client model runtime object
								ClientModelRuntime model = getModelRuntime(modelId, ifaceSvrConn);
								iface = new ModelInterfaceRuntimeClient(interfaceId, ifaceSvrConn, model, ifaceElement);
							}
							else {
								iface = new ModelInterfaceRuntimeClient(interfaceId, serverConnection, this, ifaceElement);
							}

							// add to interface map
							interfaceInfo.addObject(interfaceId.getInterfaceStaticId(),
							                        interfaceId.getInterfaceRuntimeId(),
							                        iface);

							// add to the global id object map
							RuntimeFunctionsClient.addToGlobalObjectMap(interfaceId, iface);
						}
					}
				}
			}

			return iface;
		}
	}

	public IntegrationProjectClientRuntime getProject(CompoundId projectId, ServerConnection svrConn)
	{
		// retrieve the project
		IntegrationProjectClientRuntime project = null;

		// try to retrieve an existing project
		CompoundId nextProjectId = new CompoundId(projectId);
		String projectStaticId = nextProjectId.getNextProjectStaticId(); // todo: understand id structure; modified from original!

		synchronized (resourceLock) {
			if (projectStaticId != null) {
				project = (IntegrationProjectClientRuntime) projectInfo.getObjectFromStaticId(projectStaticId);
				// get project from the server
				if (project == null) {
					// create project on the server side
					String url = projectInfo.getUrl(projectStaticId);
					ServerConnection projectSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);
					Vector results = RuntimeFunctionsClient.createProject(projectSvrConn, projectId, false);

					// create local project
					if (results.size() == 3) {
						// get parameters
//						CompoundId id = (CompoundId) results.get(0);
						String xml = (String) results.get(1);
						Hashtable idMap = (Hashtable) results.get(2);
						Element xmlElement = XMLUtils.stringToXmlElement(xml);

						// create the interface
						project = new IntegrationProjectClientRuntime(nextProjectId, projectSvrConn,
						                                              xmlElement,
						                                              idMap, parentPlayspace);
						projectInfo.addObject(nextProjectId.getCurrentProjectStaticId(),
						                      nextProjectId.getCurrentProjectRuntimeId(),
						                      project);

					}
				}
			}

			return project;
		}
	}


	/**
	 * Submit all changed parameters to the server and run the necessary models.
	 * @param playspaceInterfaceCompoundId Playspace/interface id
	 */
	public void submitChanges(CompoundId playspaceInterfaceCompoundId)
	{
		synchronized (resourceLock) {
			ModelInterfaceRuntimeClient iface;
			iface = getInterface(playspaceInterfaceCompoundId);
			if (iface != null)
				iface.submitChanges();
		}
	}


	/**
	 * Cycle through the playspace's models and projects, collecting interface change maps
	 * and submitting the maps to the server
	 */
	public void submitChanges()
	{
		synchronized (resourceLock) {

			// cycle through project interfaces
			Vector changeMps = new Vector();
			for (Iterator i = interfaceInfo.getAllObjects().iterator(); i.hasNext();) {
				// get next interface
				ModelInterfaceRuntimeClient iface = (ModelInterfaceRuntimeClient) i.next();
				// add its change map to the list of change maps
				if (iface != null && iface.getModelRuntime().equals(this)) {
					Hashtable changeMap = iface.getAndResetChangeMap();
					if (!changeMap.isEmpty())
						changeMps.add(changeMap);
				}
			}
			// send the list of change maps
			if (!changeMps.isEmpty()) {
				RuntimeFunctionsClient.setItems(serverConnection, changeMps, true);
			}

			// cycle through models
			List models = getResourceModels();
 			for (Iterator modelIter = models.iterator(); modelIter.hasNext();) {
				// get next model
				ProjectResourceInfoRuntime modelResource;
				modelResource = (ProjectResourceInfoRuntime) modelIter.next();
				Object model = modelResource.getObject();

				if ((model != null) && (model instanceof ClientModelRuntime)) {
					// collect change maps for each of the model's interfaces
					Vector changeMaps = new Vector();
					List ifaces = ((ClientModelRuntime)model).getInterfaceStaticInfo();
					for (Iterator ifaceIter = ifaces.iterator(); ifaceIter.hasNext();) {
						// get next interface
						ClientInterfaceRecord ifaceRec = (ClientInterfaceRecord) ifaceIter.next();
						String ifaceId = ifaceRec.getStaticId();

						ModelInterfaceRuntimeClient iface;
						iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromStaticId(ifaceId);

						// add its change map to the list of change maps
						if (iface != null) {
							Hashtable changeMap = iface.getAndResetChangeMap();
							if (!changeMap.isEmpty())
								changeMaps.add(changeMap);
						}
					}

					// send the list of change maps
					if (!changeMaps.isEmpty()) {
						ServerConnection svrConn = ((ClientModelRuntime) model).getServerConnection();
						RuntimeFunctionsClient.setItems(svrConn, changeMaps, true);
					}
				}
				else if(model instanceof IntegrationProjectClientRuntime) {
					((IntegrationProjectClientRuntime)model).submitChanges();
				}
			}

			List iModels = getIntegrationModels();
			for (Iterator iter = iModels.iterator(); iter.hasNext();) {
				// get next model
				ProjectIntegrationModelInfoRuntime imodel;
				imodel = (ProjectIntegrationModelInfoRuntime) iter.next();
				ClientModelRuntime model = (ClientModelRuntime) imodel.getObject();

				if (model != null) {
					// collect change maps for each of the imodel's interfaces
					Vector changeMaps = new Vector();
					List ifaces = model.getInterfaceStaticInfo();
					for (Iterator ifaceIter = ifaces.iterator(); ifaceIter.hasNext();) {
						// get next interface
						ClientInterfaceRecord ifaceRec = (ClientInterfaceRecord) ifaceIter.next();
						String ifaceId = ifaceRec.getStaticId();

						ModelInterfaceRuntimeClient iface;
						iface = (ModelInterfaceRuntimeClient) interfaceInfo.getObjectFromStaticId(ifaceId);

						// add its change map to the list of change maps
						if (iface != null) {
							Hashtable changeMap = iface.getAndResetChangeMap();
							if (!changeMap.isEmpty())
								changeMaps.add(changeMap);
						}
					}
					// send the list of change maps
					if (!changeMaps.isEmpty()) {
						ServerConnection svrConn = model.getServerConnection();
						RuntimeFunctionsClient.setItems(svrConn, changeMaps, true);
					}
				}
			}

			// cycle through projects    //TODO is this required?
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
		synchronized (resourceLock) {
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
	}


	/**
	 * Change the solving status of an interface parameter. Status may be one of:
	 * submitted, obsolete, current
	 * @param objectCompoundId Object id
	 * @param status Status constant
	 */
	public void messageItemStatusChanged(CompoundId objectCompoundId, int changedId, String status)
	{
		synchronized (resourceLock) {
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
	}

	/**
	 * Change the solving status of an interface parameter. Status may be one of:
	 * submitted, obsolete, current
	 * @param objectCompoundId Object id
	 * @param status Status constant
	 */
	public void modelStatusChanged(CompoundId objectCompoundId, String status)
	{
		synchronized (resourceLock) {
			String modelId = objectCompoundId.getModelRuntimeId();
			ProjectResourceInfoRuntime modelResource;
			modelResource = (ProjectResourceInfoRuntime) projectResourceModels.get(modelId);
			ClientModelRuntime model = (ClientModelRuntime) modelResource.getObject();
			model.setStatus(status);
		}
	}

	public void killProject()
	{
		if (serverConnection != null) {
			RuntimeFunctionsClient.killProject(serverConnection, projectId);
			serverConnection.removeReference();
		}
	}

	public CompoundId getRuntimeId()
	{
		return projectId;
	}

	protected CausalityManager getCausalityManager()
	{
		if(internalCausalityManager == null) {
			internalCausalityManager = new IntegrationProjectCausalityManager();
		}
		return internalCausalityManager;
	}

	protected ProjectResourceInfo createProjectResourceInfo(Element resourceXml)
	{
		return new ProjectResourceInfoRuntime(resourceXml);
	}

	protected ProjectIntegrationModelInfo createProjectIntegrationModelInfo(Element imodelXml)
	{
		return new ProjectIntegrationModelInfoRuntime(imodelXml);
	}

	public void setStatus(String status)
	{
		String oldStatus = this.status;
		this.status = status;
		firePropertyChange(PROJECTSTATUS, oldStatus, status);
	}

	public ServerConnection getServerConnection() {
		return  serverConnection;
	}
}


