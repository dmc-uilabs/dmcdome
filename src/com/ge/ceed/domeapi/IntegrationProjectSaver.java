package com.ge.ceed.domeapi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeInterface;
import mit.cadlab.dome3.api.DomeModel;
import mit.cadlab.dome3.api.DomeProject;
import mit.cadlab.dome3.objectmodel.dataobject.FileTransport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;

import org.apache.commons.lang.BooleanUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;

/**
 * Takes an {@link IntegrationModel} and saves it to the indicated directory
 * @author dliscomb
 *
 */
public class IntegrationProjectSaver {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IntegrationProjectSaver.class);

	private Map<String, DefaultSubscription> integrationModelSubscriptions = new HashMap<String, DefaultSubscription>();

	public IntegrationProjectSaver() {
	}

	/**
	 * Takes an {@link IntegrationModel} and saves it to the indicated directory,
	 * and returns the full path to the model saved
	 * @param integrationModel
	 * @param targetDirectory
	 * @return file object for the file saved
	 * @throws IOException if there was an error saving the file
	 * @throws DomeProxyException if there was a failure to map the parameters
	 * @throws XmlRpcException 
	 */
	public File save(final Server parentServer, final DomeConnection parentDomeConnection, final IntegrationModel integrationModel, final File targetDirectory) throws IOException, DomeProxyException, XmlRpcException {

		Id id = new Id(UUID.randomUUID().toString());
		CeedIntegrationProjectBuilder integrationProjectBuilder = new CeedIntegrationProjectBuilder(id);
		integrationProjectBuilder.setName(integrationModel.getName());
		
		// add resources
		Map<String, IModel> instanceName2IModel = integrationModel.getInternalInterfaces();
		Map<String, BrowseInterface> resourceInstances = addResourceInfo(parentServer, parentDomeConnection, integrationProjectBuilder, instanceName2IModel);
		
		// add the integration model
		DomeModelBuilder modelBuilder = integrationProjectBuilder.newIntegrationModel();
		modelBuilder.setName(integrationModel.getName());

		// now subscribe to the interface for each resource
		// see mit.cadlab.dome3.gui.objectmodel.modelobject.subscription.SubscribeDialog.addSelectedSubscriptions()
		integrationModelSubscriptions = new HashMap<String, DefaultSubscription>(resourceInstances.size());
		for (String localName : resourceInstances.keySet()){
			BrowseInterface browseInterface = resourceInstances.get(localName);
			logger.debug("\n\t browseInterface: {}", browseInterface.getInterfaceId());
			String browseInterfaceId = browseInterface.getInterfaceId();
			logger.debug("Subscribing: {}\nconn:{}\niFace:{}\niFaceId:{}\nver:{}\nparenId:{}\n",  
					new Object[]{localName, browseInterface.getServerConnection(), browseInterface.getInterface(),
					browseInterfaceId, browseInterface.getVersion(), browseInterface.getParentId()});
            modelBuilder.subscribe(browseInterface.getServerConnection(), browseInterface.getInterface(),
            		browseInterfaceId, browseInterface.getVersion(), browseInterface.getParentId());
			@SuppressWarnings("unchecked")
			List<DefaultSubscription> modelSubs = modelBuilder.getSubscriptions();
			Collection<DefaultSubscription> alreadyFound = integrationModelSubscriptions.values();
			DefaultSubscription defaultSubscription = null;
			for (DefaultSubscription object : modelSubs) {
				if (!alreadyFound.contains(object)) {
					defaultSubscription = object;
				}
			}
			integrationModelSubscriptions.put(localName, defaultSubscription);
		}
		
		// create the iModel's default interface
		ModelInterfaceBuilder modelInterfaceBuilder = (ModelInterfaceBuilder) modelBuilder.getModelInterfaces().iterator().next();
		List<IConnector> externalConnectors = new ArrayList<IConnector>();
		// add relationships inside the IModel
		{
			List<IConnector> connectors = new ArrayList<IConnector>(integrationModel.getConnectors().values());
			// map the parameters
			ConnectionMappingManager mgr = modelBuilder.getMappingManager();
			for (IConnector connector : connectors) {
				ModelParam fromParam = connector.getFrom();
				ModelParam toParam = connector.getTo();
				
				Parameter parameterA = modelparam2param(fromParam, modelInterfaceBuilder);
				Parameter parameterB = modelparam2param(toParam, modelInterfaceBuilder);

				if (!(parameterA == null || parameterB == null)) {
					if (!mgr.addModelMapping(parameterA, parameterB)) {
						throw new DomeProxyException(String.format("Failed parameter mapping: \n%s\n%s", parameterA, parameterB));
					}
					logger.debug("\n\tAdded mapping: parameterA[{}]->parameterB[{}]", parameterA, parameterB);
				} else {
					// add this to the project interface
					externalConnectors.add(connector);
				}

			}
		}
		
		// Add the project interface, and expose the external parameters
		ModelInterfaceManagerBuilder interfacesManager = (ModelInterfaceManagerBuilder) integrationProjectBuilder.getProjectInterfacesManager();
		ModelInterfaceBuilder projectInterface = (ModelInterfaceBuilder) interfacesManager.newInterface();
		interfacesManager.setDefaultInterface(projectInterface);
		projectInterface.setName(integrationModel.getInterface().getName());
		
		for (IConnector connector : externalConnectors) {
			ModelParam fromParam = connector.getFrom();
			ModelParam toParam = connector.getTo();
			
			Parameter parameterA = modelparam2param(fromParam, modelInterfaceBuilder);
			Parameter parameterB = modelparam2param(toParam, modelInterfaceBuilder);

			// this is the new parameter to create
			ModelParam srcModelParam = (parameterA==null ? fromParam : toParam);
			// this is the mgr.addMapping() below will flip from/to if necessary
			parameterA = (parameterA==null ? parameterB : parameterA);
			
			logger.debug("\n\tAdding new projectParameter: '{}'", srcModelParam);
			
			// set default value from integration model.
			ModelParam inP = integrationModel.getInParam(parameterA.getName());
			if (inP != null) {
				((ConcreteParameter)parameterA).getCurrentDataObject().setValues(Collections.singletonList(getDataValue(inP)));
			}
			
			// Add parameters to the projects interface
			Collection c = projectInterface.addAndMapModelObjects(Collections.singleton(parameterA));
			// Map the parameters to the interface so they show in the runtime interface
			projectInterface.getBuildContext().addModelObjectReferences(c);
		}
		
		// Calculate the models dependency graph to ensure all events methods are hooked up.
		modelBuilder.calculateModelGraph();
		
		if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
			throw new IOException("Unable to create directories for " + targetDirectory);
		}
		File newModelFile = new File(targetDirectory, integrationModel.getName()+".dml.dpj");
		logger.debug("\n\tnewModelFile: '{}'",newModelFile);
		
		integrationProjectBuilder.saveQuietly(newModelFile.toString());
		
		// return the file name saved
		File modelFile = new File(integrationProjectBuilder.getFileName());
		return modelFile;
	}
	
	private ConcreteParameter makeNewConcreteParameter(ModelInterfaceBuilder projectInterface, Parameter originalParameter, String modelParamName, IntegrationModel integrationModel) throws DomeProxyException {
		// get the setup info from the integrationModel
		ModelParam modelParam = integrationModel.getInParam(modelParamName);
		if (modelParam==null) {
			modelParam = integrationModel.getOutParam(modelParamName);
		}
		
		if (modelParam==null) {
			throw new DomeProxyException("Interface parameter not found: a connection references missing parameter: " + modelParamName);
		}
		
		ConcreteParameter newParameter = (ConcreteParameter) projectInterface.newModelObject(originalParameter);
		newParameter.setName(modelParamName);
		
		//TODO: HANDLE DIFFERENT PARAMETER TYPES based on modelParam.getValue()
		DataObject dataObject = ((Parameter) newParameter).getCurrentDataObject();
		// now add the default value
        logger.debug("\ndataObject.getTypeName(): {}", dataObject.getTypeName());
        Object newValue = getDataValue(modelParam);
        dataObject.setValues(Collections.singletonList(newValue));
		return newParameter;
	}
	
	//TODO: HANDLE DIFFERENT PARAMETER TYPES based on modelParam.getValue()
	private static Object getDataValue(ModelParam modelParam) throws DomeProxyException {
		Object newValue = null;
		String stringValue = String.valueOf(modelParam.getValue());
		String valueType = modelParam.getType();
		if ("Real".equals(valueType))
			newValue = Double.valueOf(stringValue);
		else if (valueType.startsWith("Int"))
			newValue = Integer.valueOf(stringValue);
		else if (valueType.startsWith("Bool"))
			newValue = BooleanUtils.toBoolean(stringValue);
		else if ("String".equals(valueType)) {
			newValue = stringValue;
		} else if ("File".equals(valueType)) {
			newValue = new FileTransport(stringValue, null);
		}
		else {
			throw new DomeProxyException("Unknown value type: " + valueType);
		}
		return newValue;
	}

	private Parameter modelparam2param(ModelParam modelParam, ModelInterfaceBuilder modelInterfaceBuilder) {
		
		Parameter parameter = null;
		String instanceName = modelParam.getInstancename();
		DefaultSubscription sub = integrationModelSubscriptions.get(instanceName);
		if (sub != null) {
			// the Ids in these parameters don't match the originals, so we key by name
			@SuppressWarnings("rawtypes")
			Iterator iter = sub.getModelObjectParameters().iterator();
			while (parameter == null && iter.hasNext()) {
				Object obj = iter.next();
				if (obj instanceof Parameter) {
					Parameter param = (Parameter) obj;
					if (param.getName().equals(modelParam.getName())) {
						parameter = param;
					}
				} else {
					logger.debug("\nUnexpected object of type {} was ignored: {}", obj.getClass().getSimpleName(), obj);
				}
				
			}
			
			logger.debug("\n\t Parameter from {}: {}", instanceName, parameter);
		} else {
			logger.debug("\n\tNo parameter found named '{}'", instanceName);
		}
		return parameter;
	}
	
	private Map<String, BrowseInterface> addResourceInfo(final Server parentServer, final DomeConnection parentDomeConnection, IntegrationProjectBuilder integrationProjectBuilder, Map<String, IModel> instanceName2IModel) throws DomeProxyException {

		Map<Server, DomeConnection> servers = new HashMap<Server, DomeConnection>();
		servers.put(parentServer, parentDomeConnection);
		logger.debug("Added parent server: {}", parentServer);
		Map<String, BrowseInterface> resourceInstances = new HashMap<String, BrowseInterface>();	

		for (String instanceName : instanceName2IModel.keySet()) {
			IModel resource = instanceName2IModel.get(instanceName);
			DomeInterfaceEntity iFaceEntity = resource.getInterface();

			if (null == resourceInstances.get(instanceName)) {
				// We want the server information from the *resource*, 
				// not necessarily from _this_ server
				Server resourceServer = resource.getServer();
				DomeConnection resourceConnection = servers.get(resourceServer);
				if (resourceConnection == null) {
					logger.debug("Adding server: {}", resourceServer);
					resourceConnection = new DomeConnection(resourceServer.getUser(), resourceServer.getPw(), resourceServer.getName() + ":"
							+ resourceServer.getPort());
					servers.put(resourceServer, resourceConnection);
				}

				DomeInterface apiDomeInterface = DomeProxy.getDomeInterface(resourceConnection, iFaceEntity);
				String modelId = null; 
				String resourceType = null;
				if (apiDomeInterface.isInterfaceOfModel()) {
					DomeModel apiDomeModel = apiDomeInterface.getParentModel();
					modelId = apiDomeModel.getModelId();
					resourceType = ProjectResourceInfo.MODEL_RESOURCE;
				} else if (apiDomeInterface.isInterfaceOfProject()) {
					DomeProject apiDomeModel = apiDomeInterface.getParentProject();
					modelId = apiDomeModel.getProjectId();
					resourceType = ProjectResourceInfo.PROJECT_RESOURCE;
				}
				
				String modelDescription = apiDomeInterface.getDescription();
				integrationProjectBuilder.addResourceModel(resourceType, modelId, instanceName, modelDescription,
						resourceConnection.getServerConnection());

				// keep a reference from the instance name to the added resourceInfo
				// we don't know the unique id of the added resource, so we can't use
				// integrationProjectBuilder.getResource(resourceID);
				@SuppressWarnings("unchecked")
				Iterator<BuildProjectResourceInfo> iterResourceModels = ((List<BuildProjectResourceInfo>) integrationProjectBuilder.getExternalResourceModels()).iterator();
				BuildProjectResourceInfo resourceModel = null;
				while (resourceModel == null && iterResourceModels.hasNext()) {
					resourceModel = iterResourceModels.next();
					if (!instanceName.equals(resourceModel.getName())) {
						resourceModel = null;
					}
				}

				if (resourceModel == null) {
					throw new DomeProxyException("Error making integration model (missing ResourceInfo)");
				} else {// the rest of this is for later

					// get the xml description from the database
					resourceModel.loadResource();
					@SuppressWarnings("unchecked")
					HashMap<String, BrowseInterface> interfacesMap = resourceModel.getInterfaces();
					logger.debug("\n\tbuildProjectResourceInfo.getInterfaces: {}", interfacesMap);
					BrowseInterface browseInterface = (BrowseInterface) interfacesMap.get(apiDomeInterface.getInterfaceId());
					logger.debug("\n\tresourceInterface: {}", browseInterface);
					resourceInstances.put(instanceName, browseInterface);
				}

			} // end if resource is not already added

		} // end for each instanceName
		
		return resourceInstances;
	}
	
}
