// AbstractIntegrationProject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.Parameters;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.OrderedHashMap;
import mit.cadlab.dome3.util.log.Log;
import mit.cadlab.dome3.util.log.LogHandler;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public abstract class AbstractIntegrationProject extends AbstractModelObjectScope implements IntegrationProject
{
	protected LogHandler logHandler = Log.getDefaultLog(); // default, no logging
	protected Version version = new Version(0, 0, 1);
	protected HashMap projectResourceModels, projectIntegrationModels; // keyed by id string
	protected HashMap projectResourceModelsbyDeployId; // keyed by resource deploy id string
	protected ModelInterfaceManager projectInterfaces;
	protected ConnectionMappingManager mappingManager;
	protected CausalityManager internalCausalityManager;

	public AbstractIntegrationProject(Id id)
	{
		super(id);
		initProject();
	}

	public AbstractIntegrationProject(Element xmlElement)
	{
		super(xmlElement);
		initProject();
		parseProjectInfoElement((Element) xmlElement.selectSingleNode("/project/projectinfo"));
		loadXml(xmlElement);
	}

	/**
	 * This method is used to initialize data structures in the project.
	 */
	protected void initProject()
	{
		projectResourceModels = new OrderedHashMap();
		projectResourceModelsbyDeployId = new OrderedHashMap();
		projectIntegrationModels = new OrderedHashMap();
		projectInterfaces = createInterfacesManager();
	}

	protected void addProjectResourceInfo(ProjectResourceInfo resource)
	{
		projectResourceModels.put(resource.getResourceUniqueId(), resource);
		projectResourceModelsbyDeployId.put(resource.getResourceDeployId(), resource);
	}

	protected void addProjectIntegrationModelInfo(ProjectIntegrationModelInfo imodel)
	{
		projectIntegrationModels.put(imodel.getId(), imodel);
	}

	protected abstract ModelInterfaceManager createInterfacesManager();

	// Project interface
	public List getResourceModels()
	{
		return Collections.unmodifiableList((List) projectResourceModels.values());
	}

	public List getIntegrationModels()
	{
		return Collections.unmodifiableList((List) projectIntegrationModels.values());
	}

	public List getInterfaces()
	{
		Collection c = projectInterfaces.getInterfaces();
		List l = new ArrayList(c);
		return Collections.unmodifiableList(l);
	}

	public ModelInterfaceManager getProjectInterfacesManager()
	{
		return projectInterfaces;
	}

	public ProjectResourceInfo getResource(String id)
	{
		ProjectResourceInfo info = null;
		synchronized (projectResourceModels) {
			info = (ProjectResourceInfo) projectResourceModels.get(id);
		}
		return info;
	}

	public ProjectIntegrationModelInfo getIntegrationModel(String id)
	{
		ProjectIntegrationModelInfo info = null;
		synchronized (projectIntegrationModels) {
			info = (ProjectIntegrationModelInfo) projectIntegrationModels.get(id);
		}
		return info;
	}

	public Model getModel()
	{
		return this;
	}

	// Model interface
	public LogHandler getLogHandler()
	{
		return logHandler;
	}

	public void setLogHandler(LogHandler log)
	{
		if (log == null)
			throw new DomeObjectException("setLogHandler", "null log handler");
		this.logHandler = log;
	}

	public Version getVersion()
	{
		return version;
	}

	public ModelObjectFactory getModelObjectFactory()
	{
		throw new UnsupportedOperationException();
	}

	public List getValidModelObjectTypes()
	{
		throw new UnsupportedOperationException();
	}


	class IntegrationProjectCausalityManager extends AbstractCausalityManager
	{
		public IntegrationProjectCausalityManager()
		{
			super(AbstractIntegrationProject.this);
		}

		protected CausalityStatus getInitialCausality(Object obj)
		{
			return CausalityStatus.INDEPENDENT;
		}
	}


	// CausalitySupport interface
	protected abstract CausalityManager getCausalityManager();

	public List getItems(CausalityStatus causality)
	{
		return getCausalityManager().getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return getCausalityManager().isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return getCausalityManager().getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(obj, l);
	}

	protected TypeInfo getTypeInfo()
	{
		return IntegrationProject.TYPE_INFO;
	}

	public String getXmlTag()
	{
		return IntegrationProject.TYPE_INFO.getXmlType();
	}

	public String getXmlType()
	{
		return ""; // no subtypes of project
	}

	protected Element createProjectInfoElement()
	{
		Element xml = DocumentHelper.createElement("projectinfo");
		xml.add(version.toXmlElement());
		return xml;
	}

	protected void parseProjectInfoElement(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml project info");
		version = new Version((Element) xmlElement.selectSingleNode("/project/projectinfo/version"));
		if (version == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml version: " + xmlElement.asXML());
	}

	protected String contentToString()
	{
		return "  version: " + version.toString();
	}

	protected void addXmlContent(Element xmlElement)
	{
		xmlElement.add(createProjectInfoElement());
		XMLUtils.addCollection(xmlElement, "resources", getResourceModels());
		XMLUtils.addCollection(xmlElement, "integrationModels", getIntegrationModels());
	}

	protected void loadXml(Element xmlElement)
	{
		List resourceNodes = xmlElement.selectNodes("resources/" + ProjectResourceInfo.XML_TAG);
		for (int i = 0; i < resourceNodes.size(); i++) {
			addProjectResourceInfo(createProjectResourceInfo((Element) resourceNodes.get(i)));
		}
		List imodelNodes = xmlElement.selectNodes("integrationModels/" + ProjectIntegrationModelInfo.XML_TAG);
		for (int i = 0; i < imodelNodes.size(); i++) {
			addProjectIntegrationModelInfo(createProjectIntegrationModelInfo((Element) imodelNodes.get(i)));
		}
	}

	// methods below can be overridden in subclasses to create specific types of info classes

	protected ProjectResourceInfo createProjectResourceInfo(Element resourceXml)
	{
		return new ProjectResourceInfo(resourceXml);
	}

	protected ProjectIntegrationModelInfo createProjectIntegrationModelInfo(Element imodelXml)
	{
		return new ProjectIntegrationModelInfo(imodelXml);
	}

	/**
	 * to be used for creating DSM visualization (only?)
	 * @return
	 */
	public DirectedGraph createProjectGraph()
	{
		DirectedGraph projGraph = new DirectedGraph();
		List resource = getResourceModels();
		HashMap resourceModelObjMap = new HashMap(); //key=id, value=parameter (in resource)
		for (int i = 0; i < resource.size(); i++) {
			BuildProjectResourceInfo bpri = (BuildProjectResourceInfo) resource.get(i);
			if (bpri.getType().equals(ProjectResourceInfo.MODEL_RESOURCE)) {
				bpri.loadResource();
				List subscribedInterfaceIds = bpri.getSubscribedInterfaceIds();
				List viewList = bpri.getView();
				for (Iterator it = viewList.iterator(); it.hasNext();) {
					Object o = it.next();
					if (o instanceof BrowseInterface) {
						BrowseInterface bi = (BrowseInterface) o;
						if (subscribedInterfaceIds.contains(bi.getInterfaceId())) {
							bi.loadInterface();
							ModelInterfaceRuntimeClient iface = bi.getInterface();
							DirectedGraph ifaceGraph = iface.getInterfaceGraph();
							projGraph.addGraph(ifaceGraph);
							List nodes = ifaceGraph.getNodes();
							for (int j = 0; j < nodes.size(); j++) {
								Parameter p = (Parameter) nodes.get(j);
								resourceModelObjMap.put(p.getId().getIdString(), p);
							}
						}
					}
				}
			}
			else { //todo: support project in project
				System.out.println("AbstractIntegrationProject:creatProjectGraph - Invalid resource type - " + bpri.getType());
			}
		}

		Iterator it = getIntegrationModels().iterator();
		while (it.hasNext()) {
			// for each imodel
			ProjectIntegrationModelInfo aIModel = (ProjectIntegrationModelInfo) it.next();
			DomeModel dm = aIModel.getModel();
			List modelParams = new ArrayList(dm.getFilter(DomeModel.PARAMETERS_FILTER).getItems());
			for (int i = 0; i < modelParams.size(); i++) {
				Parameter mp = (Parameter) modelParams.get(i);
				projGraph.addNode(mp);
			}
			ConnectionMappingManager mgr = dm.getMappingManager();
			List subs = dm.getSubscriptions();
			HashMap paramIdMap = new HashMap();

			// create graph for relations within the imodel
			Collection mObjs = dm.getModelObjects();
			for (Iterator iterator = mObjs.iterator(); iterator.hasNext();) {
				Object o = iterator.next();
				if (o instanceof ProceduralRelation) {
					ProceduralRelation rel = (ProceduralRelation) o;
					DirectedGraph dg = new DirectedGraph(rel.getDependencyInfo());
					projGraph.addGraph(dg);
				}
			}

			// create paramIdMap (newId - > origId (iface param id)) for all subscriptions
			for (int i = 0; i < subs.size(); i++) {
				AbstractSubscription subscription = (AbstractSubscription) subs.get(i);
				paramIdMap.putAll(subscription.getParamIdMap());
			}

			// link graphs between subscriptions
			for (int i = 0; i < subs.size(); i++) {
				AbstractSubscription sub = (AbstractSubscription) subs.get(i);
				Collection params = sub.getModelObjects();
				for (Iterator iterator = params.iterator(); iterator.hasNext();) {
					Parameter subParam = (Parameter) iterator.next();
					Collection map = mgr.getMappingsForParameter(subParam);
					if (!map.isEmpty()) {
						Parameter resourceIfaceParam = (Parameter) resourceModelObjMap.get(paramIdMap.get(subParam.getId().getIdString()));
						for (Iterator iterator2 = map.iterator(); iterator2.hasNext();) {
							Parameter mappedParam = (Parameter) iterator2.next(); // could be either in a relation within the imodel or in a subscription
							Object mappedIfaceId = paramIdMap.get(mappedParam.getId().getIdString());
							if (mappedIfaceId != null) // would be null if in a relation within imodel
								mappedParam = (Parameter) resourceModelObjMap.get(mappedIfaceId);
							if (Parameters.isSubscriptionInput(subParam))
								projGraph.addArc(mappedParam, resourceIfaceParam);
							else
								projGraph.addArc(resourceIfaceParam, mappedParam);
						}
					}
				}
			}
		}
		return projGraph;
	}

	public Parameter getInterfaceParameter(String resourceId, String interfaceId, String interfaceParamId)
	{
		BuildProjectResourceInfo resource = (BuildProjectResourceInfo) getResource(resourceId);
		if (resource == null)
			return null;
		resource.loadResource();
		BrowseInterface browseIface = resource.getInterface(interfaceId);
		if (browseIface == null)
			return null;
		browseIface.loadInterface();
		return browseIface.getParameter(interfaceParamId);
	}

	public Vector getAggregatedGraph(List interfaceId)
	{
		DirectedGraph aggGraph;
		// get model objects mapping manager
		ConnectionMappingManager mManager;
		if (getModel() instanceof IntegrationProject)
			mManager = ((IntegrationProject) getModel()).getMappingManager();
		else
			mManager = ((DomeModel) getModel()).getMappingManager();

		// create hash map btn objects in model and model objects in interface
		Vector iToRpMap = new Vector(); // interface to relation parameter map
		Hashtable iToRpIdTable = new Hashtable(); // interface to relation parameter id-map
		HashMap rPToRpIdMap = new HashMap(); // relation parameter to relation parameter id map

		for (Iterator iterator = getInterfaces().iterator(); iterator.hasNext();) {
			ModelInterfaceBase iFace = (ModelInterfaceBase) iterator.next();
			String iFaceId = iFace.getId().getIdString();
			if (iFace instanceof AbstractModelInterfaceRuntime) {
				iFaceId = ((AbstractModelInterfaceRuntime) iFace).getRuntimeId().getInterfaceStaticId();
			}
			if (!interfaceId.contains(iFaceId))
				continue;

			Collection intefaceObj = iFace.getModelObjects();
			for (Iterator iterator2 = intefaceObj.iterator(); iterator2.hasNext();) {
				Object iObj = iterator2.next();

				if (iObj instanceof Parameter) {
					List mapping = new ArrayList(mManager.getMappingsForParameter((Parameter) iObj));
					if (mapping.size() > 1)
						throw new IllegalArgumentException("Error getting mapping for parameter " + iObj);
					else if (!(mapping == null || mapping.size() == 0)) {
						Object modelParam = mapping.get(0); // should be imodel param

						iToRpMap.add(Vectors.create(iObj, modelParam));
						iToRpIdTable.put(((Parameter) iObj).getId().getIdString(), ((Parameter) modelParam).getId().getIdString());
					}
				}
			}
		}
		DSet rps = new DSet();
		for (int i = 0; i < iToRpMap.size(); i++) {
			Parameter rp = (Parameter) ((Vector) iToRpMap.get(i)).get(1);
			rPToRpIdMap.put(rp, rp.getId().getIdString());
			rps.add(rp);
		}
		// get graph of keys
		DirectedGraph projectGraph = new DirectedGraph(); // only a combination of imodel graphs, since project iface only comes from imodels
		List imodels = getIntegrationModels();
		for (int i = 0; i < imodels.size(); i++) {
			ProjectIntegrationModelInfo imodelInfo = (ProjectIntegrationModelInfo) imodels.get(i);
			DomeModelBase imodel = (DomeModelBase) imodelInfo.getModel();
			projectGraph.addGraph(imodel.createModelGraph());
		}
		aggGraph = projectGraph.getSubgraphForVariables(rps);

		return Vectors.create((new DirectedGraph(aggGraph, rPToRpIdMap)).toXmlElement(this.getId().getIdString()).asXML(), iToRpIdTable);
	}

}
