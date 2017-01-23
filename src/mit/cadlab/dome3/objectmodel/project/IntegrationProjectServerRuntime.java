package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.functions.MessageFunctions;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfoRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DirectedGraphCausalityManager;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.playspace.ServerPlayspace;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.DBag;
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.util.OrderedHashMap;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * Name: IntegrationProjectServerRuntime
 * User: thorek
 * Date: Apr 13, 2003
 * Time: 5:03:13 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class IntegrationProjectServerRuntime extends AbstractIntegrationProject
{
	CompoundId projectId;   /* The "location id" of this project. The location id describes
							   where the object lives on the server. */
	CompoundId parentId;    /* The location id of the parent object (which is a playspace
							   or another project). */

	private boolean isProjectResource;

	// maps resource ids (which describe the full resource hierarchy)
	// to location ids (which describe the actual location of the resources)
	private Hashtable resourceIdMap = new Hashtable();

	// client url of this server
	String clientUrl = "http://" + NetworkUtils.getHostName() + ":" + DomeServer.getPort() + "/RPC2";

	private boolean hasAtLeastOneIModelOrProjectInterfaceLoaded = false;

	private ModelRunStatusListener modelStatusListener = new ModelRunStatusListener();
	private String projectRunStatus = ModelRuntime.STATUS_DONE; // default
	private OrderedHashMap modelRunStatusMap = new OrderedHashMap(); // key=imodel; value=run_status

	protected IntegrationProjectSolver solver;


	/**
	 * Create a new integration project.
	 * @param projectId Project deploy and static id
	 * @param parentId Project parent id
	 * @param xmlElement Project xml description
	 * @throws XmlRpcException
	 */
	public IntegrationProjectServerRuntime(CompoundId projectId, CompoundId parentId, Element xmlElement, boolean isProjectResource)
	        throws XmlRpcException
	{
		super(xmlElement);

		//TODO instantiate proper subclass of ConnectionMappingManager

		// store the ids
        if (projectId.getFirstProjectRuntimeId() ==  null)
		    projectId.addProjectRuntimeId(UUIDGenerator.create());
		projectId.setModelStaticId(null);
		projectId.setInterfaceStaticId(null);
		this.projectId = new CompoundId(projectId);
		this.parentId = new CompoundId(parentId);
		this.isProjectResource = isProjectResource;

		// create integration models
		String projectStaticId = projectId.getCurrentProjectStaticId();
		List iModelDeployAndBuildIds = FileSystemDbFunctions.getIModelDeployAndBuildIdsForProject(projectStaticId);
		for (Iterator iModelIter = iModelDeployAndBuildIds.iterator(); iModelIter.hasNext();) {
			// create the model
			List deployAndBuildIds = (List) iModelIter.next();
			createIntegrationModel((String)deployAndBuildIds.get(0), (String)deployAndBuildIds.get(1));
		}

        // _i after initial loading of all i-models, finish off by loading subscriptions for each one. this is to
        //ensure that an i-model subscribed to another one runs at a single instance (otherwise it will be loaded both as a
        //subscription and as an i-model
        /* for (Iterator iModelIter = iModelDeployAndBuildIds.iterator(); iModelIter.hasNext();) {
			// finish loading i-models
			List deployAndBuildIds = (List) iModelIter.next();
			//createIntegrationModel((String)deployAndBuildIds.get(0), (String)deployAndBuildIds.get(1));
             ProjectIntegrationModelInfo iModelInfo = getIntegrationModel((String)deployAndBuildIds.get(0));  //get i-model build id
             DomeModelRuntime iModel = (DomeModelRuntime) iModelInfo.getModel();
             iModel.finishImodelLoading();
		}*/

		// add resource info to the resourceIdMap so clients know how to find the resources
		List resources = getResourceModels();
		ProjectResourceInfoRuntime prir;
		for (int i = 0; i < resources.size(); i++) {
			prir = (ProjectResourceInfoRuntime) resources.get(i);
			if (prir.isSubscribed()) {
				resourceIdMap.put(prir.getResourceUniqueId(), prir.getRuntimeId().toString());
			}
		}

		// create the solver
		solver = new IntegrationProjectSolver(this);
	}

	/**
	 * Construct an integration model.
	 * @param deployId Model deploy id
	 * @param buildId Model build id as stored in database
	 * @return Model runtime object
	 * @throws XmlRpcException
	 */
	private DomeModelRuntime createIntegrationModel(String deployId, String buildId)
	        throws XmlRpcException
	{
		DomeModelRuntime model;
		CompoundId modelId = new CompoundId(projectId);
		modelId.setModelStaticId(deployId);

		//check if it's already loaded (as another model's resource) - do this by trying to get the i-model by deployId _i
        ProjectIntegrationModelInfo imodelInfo = (ProjectIntegrationModelInfo) projectIntegrationModels.get(deployId);

        if(imodelInfo == null) { // i-model has not been loaded by a subscription yet
        // get the xml description from the database
		String xmlContent = FileSystemDbFunctions.getIntegrationModelDescription(deployId);
		Element xmlElement = XMLUtils.stringToXmlElement(xmlContent);

		// construct the model
		 model = new DomeModelRuntime(modelId, xmlElement, this);

        //construct the model  without loading any subscriptions yet - not used _i
        //model = new DomeModelRuntime(modelId, xmlElement, this, true);

		if (model == null)
			throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
			                          DbErrors.XMLRPC_MODEL_INVOCATION_MSG);

		// add model status listener
		model.addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);

		// map the model resource id to itself
		resourceIdMap.put(deployId, model.getRuntimeId().toString());

		// store in the iModel info structure
		ProjectIntegrationModelInfo iModelInfo = getIntegrationModel(buildId);
		if (iModelInfo == null)
			throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
			                          "Inconsistent iModel build id between project info and database for "+model.getName());
		iModelInfo.setModel(model);

		// key the imodel info structure by deploy id & runtime id
		projectIntegrationModels.put(deployId, iModelInfo);
		projectIntegrationModels.put(model.getRuntimeId().getModelRuntimeId(), iModelInfo);
		resourceIdMap.put(buildId, deployId);

        // add the imodel in the resources map of the playspace - previously done in ServerPlayspace.loadModel after
        // the project had finished loading but now needed here in case of nested i-model subscription
        ServerPlayspace playspace = RuntimeFunctionsServer.getPlayspace(null, projectId);
        playspace.addResource(model.getRuntimeId().getModelRuntimeId(), model);


		return model;
        }
        else {   // the i-model has already been loaded by another i-model as resource
            DomeModelRuntime imodel = (DomeModelRuntime) imodelInfo.getModel();
            return imodel;
        }
	}

    // this method creates an i-model as project resource, called by the ProjectResourceInfoRuntime _i
    public DomeModelRuntime createIntegrationModelAsResource(String deployId, String buildId)
            throws XmlRpcException
    {

        DomeModelRuntime model;
        CompoundId modelId = new CompoundId(projectId);
        modelId.setModelStaticId(deployId);

        String xmlContent = FileSystemDbFunctions.getIntegrationModelDescription(deployId);
        Element xmlElement = XMLUtils.stringToXmlElement(xmlContent);

        // construct the model
        model = new DomeModelRuntime(modelId, xmlElement, this);

        // construct the model  without loading any subscriptions yet - not used _i
        //model = new DomeModelRuntime(modelId, xmlElement, this, true);

        if (model == null)
            throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
                    DbErrors.XMLRPC_MODEL_INVOCATION_MSG);

        // add model status listener
        model.addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);

        // map the model resource id to itself
        resourceIdMap.put(deployId, model.getRuntimeId().toString());

        // store in the iModel info structure
        ProjectIntegrationModelInfo iModelInfo = getIntegrationModel(buildId);
        if (iModelInfo == null)
            throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
                    "Inconsistent iModel build id between project info and database for " + model.getName());
        iModelInfo.setModel(model);

        // key the imodel info structure by deploy id & runtime id
        projectIntegrationModels.put(deployId, iModelInfo);
        projectIntegrationModels.put(model.getRuntimeId().getModelRuntimeId(), iModelInfo);
        resourceIdMap.put(buildId, deployId);

        // add the imodel in the resources map of the playspace - previously done in ServerPlayspace.loadModel after
        // the project had finished loading but now needed here in case of nested i-model subscription
        ServerPlayspace playspace = RuntimeFunctionsServer.getPlayspace(null, projectId);
        playspace.addResource(model.getRuntimeId().getModelRuntimeId(), model);

        return model;
    }

	//from AbstractIntegrationProject
	protected ModelInterfaceManager createInterfacesManager()
	{
		return new ModelInterfaceManagerRuntime(this);
	}

	public boolean isProjectResource()
	{
		return isProjectResource;
	}

	public void markModelWaitingToBeExecuted()
	{
		if (!ModelRuntime.STATUS_RUNNING.equals(projectRunStatus))
			setProjectRunStatus(ModelRuntime.STATUS_WAITING_TO_BE_EXECUTED);
	}

	public void suspendStatusPropagation()
	{
		Iterator imodels = getIntegrationModels().iterator();
		while (imodels.hasNext()) {
			((DomeModelRuntime)((ProjectIntegrationModelInfo) imodels.next()).getModel()).suspendStatusPropagation();
		}
	}

	public void resumeStatusPropagation()
	{
		Iterator imodels = getIntegrationModels().iterator();
		while (imodels.hasNext()) {
			((DomeModelRuntime) ((ProjectIntegrationModelInfo) imodels.next()).getModel()).resumeStatusPropagation();
		}
	}

	public boolean hasAtLeastOneProjectOrIModelInterfaceLoaded()
	{
		if (hasAtLeastOneIModelOrProjectInterfaceLoaded)
			return hasAtLeastOneIModelOrProjectInterfaceLoaded;
		hasAtLeastOneIModelOrProjectInterfaceLoaded = (getProjectInterfacesManager().countInterfaces() > 0);
		if (hasAtLeastOneIModelOrProjectInterfaceLoaded)
			return hasAtLeastOneIModelOrProjectInterfaceLoaded;
		Iterator imodels = getIntegrationModels().iterator();
		ProjectIntegrationModelInfo imodel;
		while (imodels.hasNext()) {
			imodel = (ProjectIntegrationModelInfo) imodels.next();
			if (((DomeModelRuntime) imodel.getModel()).hasAtLeastOneInterfaceLoaded()) {
				hasAtLeastOneIModelOrProjectInterfaceLoaded = true;
				break;
			}
		}
		return hasAtLeastOneIModelOrProjectInterfaceLoaded;
	}

	/**
	 * Get an existing interface or creae a new one if necessary.
	 * @param interfaceId Interface id
	 * @return Interface object
	 * @throws XmlRpcException
	 */
	public ModelInterfaceRuntimeServer getInterface(CompoundId interfaceId)
	        throws XmlRpcException
	{
		String ifaceId = interfaceId.getInterfaceRuntimeId();
		if (ifaceId == null)
			ifaceId = interfaceId.getInterfaceStaticId();
		if (ifaceId == null)
			return null;
		ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)getProjectInterfacesManager().getById(ifaceId);
		if (iface != null && interfaceId.getInterfaceRuntimeId() == null)
			interfaceId.setInterfaceRuntimeId(iface.getRuntimeId().getInterfaceRuntimeId());
		return iface;
	}


	public synchronized ModelInterfaceRuntimeServer loadInterface(CompoundId interfaceId, String xmlContent, String xmlMappings) {
		return ((ModelInterfaceManagerRuntime) projectInterfaces).loadInterface(interfaceId, xmlContent, xmlMappings);
	}

	public ConnectionMappingManager getMappingManager()
	{
		if (mappingManager == null) {
			mappingManager = new ConnectionMappingManagerRuntime(this);
		}
		return mappingManager;
	}

	/**
	 * Get a model's solving graph.
	 * @param resourceId Resource id
	 * @param interfaceId Interface id
	 * @return Solving graph
	 */
	public Vector getResourceGraph(CompoundId resourceId, Vector interfaceId)
	{
		synchronized (projectResourceModels) {
			// assume its a model for now
			DomeModelRuntime model = (DomeModelRuntime) projectResourceModels.get(resourceId.getModelRuntimeId());
			return model.getAggregatedInterfaceInfo(interfaceId);
		}
	}

	/**
	 * Returns graph for each interface and information about which interface parameters
	 * are related to which other interface parameters. Placed in DomeModelBase so that
	 * methods are available to both Dome and Plugin models.
	 * @param interfaceIds
	 * @return Vector of <vector multiItemNodes, hashtable of <interfaceid,graphXML>>
	 * multiItemNodes have modelParamId as Id and ifaceParamIds<ifaceId.paramId> as items
	 */
	public Vector getAggregatedInterfaceInfo(List interfaceIds)
	{
		AggregatorMap mpToIp = new AggregatorMap();
		Hashtable interfaceInfo = new Hashtable(); // ifaceId, ifaceGraphXml

		for (Iterator iterator = projectInterfaces.getInterfaces().iterator(); iterator.hasNext();) {
			ModelInterfaceBase iface = (ModelInterfaceBase) iterator.next();
			String ifaceId = iface.getId().getIdString();
			if (iface instanceof AbstractModelInterfaceRuntime) {
				ifaceId = ((AbstractModelInterfaceRuntime) iface).getRuntimeId().getInterfaceStaticId();
			}
			if (!interfaceIds.contains(ifaceId))
				continue;
			interfaceInfo.put(ifaceId, iface.getInterfaceGraph().toXmlElement(ifaceId).asXML());
			mpToIp.putAll(iface.getModelParamToInterfaceParamMap());
		}

		// remove model parameters mapped to only one interface parameter // todo later
		Vector multiItemNodes = new Vector();
//		Collection modelParams = mpToIp.keySet();
//		for (Iterator iterator = modelParams.iterator(); iterator.hasNext();) {
//			Parameter modelParam = (Parameter) iterator.next();
//			List ifaceParams = (List) mpToIp.get(modelParam);
//			if (ifaceParams == null || ifaceParams.isEmpty() || ifaceParams.size() == 1)
//				continue; // skip
//			List ifaceParamIds = new ArrayList();
//			for (int i = 0; i < ifaceParamIds.size(); i++) {
//				ifaceParamIds.add(getInterfaceParamId((Parameter) ifaceParamIds.get(i)));
//			}
//			multiItemNodes.add(new MultiItemNode(getModelParamId(modelParam), ifaceParamIds).toXmlElement().asXML());
//		}

		return Vectors.create(multiItemNodes, interfaceInfo);
	}

	/**
	 * Get the project's compound runtime id.
	 * @return Compound runtime id
	 */
	public CompoundId getRuntimeId()
	{
		return projectId;
	}

	/**
	 * Get the project's resource id map. The id map maps resource ids to location (local or remote) ids.
	 * @return Resource id map
	 */
	public Hashtable getResourceIdMap()
	{
		return resourceIdMap;
	}


	public List getIntegrationModels()
	{
		DSet iModels = null;
		synchronized (projectIntegrationModels) {
			iModels = new DSet(projectIntegrationModels.values());
		}
		return iModels;
	}

	protected CausalityManager getCausalityManager()
	{
		if (internalCausalityManager == null) {
			internalCausalityManager = new IntegrationProjectServerRuntimeCausalityManager();
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


	/**
	 * Destroy a project once all the interfaces have been removed.
	 */
	public void killProject()
	{
		// close the interfaces
		// todo: how to really kill this?

		// kill all resource models
		Collection resources = getResourceModels();
		ProjectResourceInfoRuntime prir;
		for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
			prir = (ProjectResourceInfoRuntime) iterator.next();
			if (prir.isSubscribed()) {
				prir.closeResource(); // tell it to diel
			}
		}

		// kill all imodels
		Collection iModels = getIntegrationModels();
		for (Iterator iModelIter = iModels.iterator(); iModelIter.hasNext();) {
			ProjectIntegrationModelInfo iModelInfo = (ProjectIntegrationModelInfo) iModelIter.next();
			DomeModelRuntime iModel = (DomeModelRuntime) iModelInfo.getModel();
			iModel.deleteModel();
			iModelInfo.setModel(null);
		}

		solver.cleanup();
		Debug.trace(Debug.ALL, "project '" + name + "' killed");
		firePropertyChange(ModelRuntime.MODEL_KILLED);
	}

	/**
	 * Start all the imodels.
	 */
	public void startProject()
	{
		setProjectRunStatus(ModelRuntime.STATUS_RUNNING);
		Iterator imodelInfos = projectIntegrationModels.values().iterator();
		ProjectIntegrationModelInfo imodelInfo;
		while (imodelInfos.hasNext()) {
			imodelInfo = (ProjectIntegrationModelInfo) imodelInfos.next();
			((DomeModelRuntime)imodelInfo.getModel()).startModel();
		}
	}

	protected void stopProject()
	{
		Iterator imodelInfos = projectIntegrationModels.values().iterator();
		ProjectIntegrationModelInfo imodelInfo;
		while (imodelInfos.hasNext())
		{
			imodelInfo = (ProjectIntegrationModelInfo) imodelInfos.next();
			((DomeModelRuntime) imodelInfo.getModel()).pauseModel();
		}
	}

	public DirectedGraph getGraph() {
		return solver.getGraph();
	}

	public void addExternalGraph(String graphXml)
	{
		solver.addExternalGraph(graphXml);
	}

	public Hashtable getParameterSystemCausality(Collection ifaceParams)
	{  // todo: reimplement this method using graph from solver
		List imodels = getIntegrationModels();
		DirectedGraph completeGraph = new DirectedGraph();
		for (int i = 0; i < imodels.size(); i++) {
			ProjectIntegrationModelInfoRuntime imodelInfo = (ProjectIntegrationModelInfoRuntime) imodels.get(i);
			DomeModelRuntime dm = (DomeModelRuntime) imodelInfo.getModel();
			completeGraph.addGraph(dm.createModelGraph());
		}
		List disconnectedNodes = completeGraph.getDisconnectedNodes();
		List independents = completeGraph.getInputs();
		List intermediates = completeGraph.getIntermediates();
		List results = completeGraph.getResults();
		ConnectionMappingManager mgr = getMappingManager();
		Hashtable paramCausality = new Hashtable();
		for (Iterator iterator = ifaceParams.iterator(); iterator.hasNext();) {
			Parameter ifaceParam = (Parameter) iterator.next();
			Collection map = mgr.getMappingsForParameter(ifaceParam);
			for (Iterator iterator2 = map.iterator(); iterator2.hasNext();) {
				Parameter mappedParam = (Parameter) iterator2.next(); // would get subscription iface param for imodel
				if (independents.contains(mappedParam))
					paramCausality.put(ifaceParam.getId().getIdString(), CausalityStatus.INDEPENDENT.toString());
				else if (intermediates.contains(mappedParam))
					paramCausality.put(ifaceParam.getId().getIdString(), CausalityStatus.INTERMEDIATE.toString());
				else if (results.contains(mappedParam))
					paramCausality.put(ifaceParam.getId().getIdString(), CausalityStatus.RESULT.toString());
				else if (disconnectedNodes.contains(mappedParam))
					paramCausality.put(ifaceParam.getId().getIdString(), CausalityStatus.INDETERMINATE.toString());
				else
					throw new RuntimeException("IntegrationProjectServerRuntime:getParameterSystemCausality: invalid parameter: " + ifaceParam.getName());
			}
		}
		return paramCausality;
	}

	class IntegrationProjectServerRuntimeCausalityManager extends DirectedGraphCausalityManager {

		public IntegrationProjectServerRuntimeCausalityManager()
		{
			super(IntegrationProjectServerRuntime.this, IntegrationProjectServerRuntime.this.getGraph());
		}

		public CausalityStatus getCausality(Object obj)
		{
			if (obj instanceof Parameter)
				obj = IntegrationProjectSolver.getProjectGraphId((Parameter)obj);
			return super.getCausality(obj);
		}
	}

	class ModelRunStatusListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(ModelRuntime.RUN_STATUS)) {
				ModelRuntime model = (ModelRuntime) evt.getSource();
				String newStatus = (String) evt.getNewValue();
				synchronized (modelRunStatusMap) {
					if (ModelRuntime.STATUS_WAITING_TO_BE_EXECUTED.equals(newStatus))
						return; // ignore these
					modelRunStatusMap.put(model, newStatus);
					updateProjectStatusFromImodelAndResourceStatuses();
				}
			}
		}
	}

	public String getProjectRunStatus()
	{
		return projectRunStatus;
	}

	private void setProjectRunStatus(String projectRunStatus)
	{
		String oldProjectRunStatus = this.projectRunStatus;
		this.projectRunStatus = projectRunStatus;
		firePropertyChangeInSeparateThread(ModelRuntime.RUN_STATUS, oldProjectRunStatus, this.projectRunStatus);
	}

	public void updateResourceModelStatus(String resourceId, String status) {
		synchronized (modelRunStatusMap) {
			if (ModelRuntime.STATUS_WAITING_TO_BE_EXECUTED.equals(status))
				return; // ignore these
			modelRunStatusMap.put(resourceId, status);
			updateProjectStatusFromImodelAndResourceStatuses();
		}
	}

	private String getModelRunStatusMapAsString() {
		StringBuffer sb = new StringBuffer("");
		Iterator keys = modelRunStatusMap.keyList().iterator();
		Object mapKey;
		String status, name=null;
		boolean isFirstRow = true;
		while (keys.hasNext()) {
			if (isFirstRow)
				isFirstRow = false; // future rows are not the first row
			else
				sb.append("\n\t");
			mapKey = keys.next();
			status = (String)modelRunStatusMap.get(mapKey);
			if (mapKey instanceof String)
				name = this.getResource((String)mapKey).getName();
			else if (mapKey instanceof Model)
				name = ((Model)mapKey).getName();
			else
				name = mapKey.toString();
			sb.append(status+" -- "+name);
		}
		return sb.toString();
	}

	private void updateProjectStatusFromImodelAndResourceStatuses()
	{
		// todo: deal with paused models
		DBag runStatuses;
		if (modelRunStatusMap.isEmpty()) {
			return;
		}
		runStatuses = new DBag();
		Iterator imodelStatuses = modelRunStatusMap.values().iterator();
		while (imodelStatuses.hasNext()) {
			runStatuses.add(imodelStatuses.next());
		}
		Debug.trace(Debug.DETAIL, "IMODEL/RESOURCE STATUSES for \""+getName()+"\"\n\t" + getModelRunStatusMapAsString());

		int totalModels = modelRunStatusMap.size();
		int runningModels = runStatuses.getCount(ModelRuntime.STATUS_RUNNING);
		int abortedModels = runStatuses.getCount(ModelRuntime.STATUS_ABORTED);
		int waitingModels = runStatuses.getCount(ModelRuntime.STATUS_WAITING_FOR_CONFIRMATION);

		if (abortedModels > 0 && runningModels == 1) // a resource died; imodel still running
		{ // todo: assumes 1 imodel for now; should support arbitrary numbers of imodels
			this.stopProject();
			return;
		}
		if (runningModels > 0) {
			if (!ModelRuntime.STATUS_RUNNING.equals(projectRunStatus))
				setProjectRunStatus(ModelRuntime.STATUS_RUNNING);
			return;
		}
		if (abortedModels > 0 && runningModels == 0) // all of the models are done
		{
			modelRunStatusMap.clear();;
			setProjectRunStatus(ModelRuntime.STATUS_ABORTED);
			return;
		}
		if (waitingModels == totalModels) { // all of the models are waiting
			if (isProjectResource)
				setProjectRunStatus(ModelRuntime.STATUS_WAITING_FOR_CONFIRMATION);
			else
				notifyProjectRunComplete();
			return;
		}
		if (waitingModels == 0) {
			int doneModels = runStatuses.getCount(ModelRuntime.STATUS_DONE);
			if (doneModels == totalModels) { // all of the models are done
				modelRunStatusMap.clear();
				setProjectRunStatus(ModelRuntime.STATUS_DONE);
				return;
			}
		}
		else if (waitingModels > 0 &&
		        ((!isProjectResource && ModelRuntime.STATUS_RUNNING.equals(projectRunStatus)) ||
		         (isProjectResource && ModelRuntime.STATUS_WAITING_FOR_CONFIRMATION.equals(projectRunStatus)))) {
			// need to wait for waiting models to finish completing
			return;
		}
		System.err.println("IntegrationProjectServerRuntime.updateProjectStatusFromImodelStatus: unhandled state for project " +
		                   getName() + ":" + projectRunStatus + "\n\t" + runStatuses.getMembersMapAsString());
	}

	public void notifyProjectRunComplete() {
		// tell resource models project is done
		Iterator resources = getResourceModels().iterator();
		ProjectResourceInfoRuntime prir;
		while (resources.hasNext()) {
			prir = (ProjectResourceInfoRuntime) resources.next();
			if (prir.isSubscribed()) {
				prir.notifyProjectRunComplete();
			}
		}

		// tell imodels project is done
		Iterator imodels = getIntegrationModels().iterator();
		ProjectIntegrationModelInfoRuntime pimir;
		while (imodels.hasNext()) {
			pimir = (ProjectIntegrationModelInfoRuntime) imodels.next();
			new Thread(new ProjectCompleteNotificationThread((DomeModelRuntime) pimir.getModel())).start();
		}
	}

	public void handleImodelAndResourceExecutionError(String msg)
	{
		CompoundId projectId = getRuntimeId();
		MessageFunctions.errorMessageToClient(projectId, projectId.getCurrentProjectRuntimeId(), getName(),
		                                      msg);
	}

	// this class is intended to notify project guis of status of imodels and resources
	// should only be used once we know how to get to project guis and not anything else
	class ModelRunStatusNotificationThread implements Runnable {
		CompoundId modelId;
		String modelIdString;
		String runStatus;
		Integer eventId;

		public ModelRunStatusNotificationThread(CompoundId modelId, String runStatus, Integer eventId)
		{
			this.modelId = modelId;
			this.modelIdString = modelId.toString();
			this.runStatus = runStatus;
			this.eventId = eventId;
		}

		public void run() {
			// send to clients of model and project
			DSet clientIdList = new DSet(DomeServer.getResourceClients(modelId.getModelRuntimeId()));
			String projectId = IntegrationProjectServerRuntime.this.getRuntimeId().getFirstProjectRuntimeId();
			clientIdList.addAll(DomeServer.getResourceClients(projectId));
			for (Iterator clientIter = clientIdList.iterator(); clientIter.hasNext();) {
				String clientId = (String) clientIter.next();
				MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED,
				                             clientId,
				                             Vectors.create(modelIdString, eventId, runStatus));
			}
		}
	}

	class ProjectCompleteNotificationThread implements Runnable {
		DomeModelRuntime model;

		public ProjectCompleteNotificationThread(DomeModelRuntime model)
		{
			this.model = model;
		}

		public void run()
		{
			model.notifyProjectRunComplete();
		}
	}

}
