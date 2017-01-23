// AbstractSubscription.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.subscription;

import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractPropertyChangeFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.AbstractContext;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityFilter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DirectedGraphCausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AbstractSubscription extends AbstractModelObjectScope implements Subscription, ViewSupport
{
	// ModelObject support
	protected ModelObjectScope scope;

	protected DirectedGraphCausalityManager internalCausalityManager;
	protected DirectedGraph graph;

	protected String resourceId;
	protected String ifaceId;
    protected int ifaceVersion;

	protected String loginType;
	protected String loginName;
	protected byte[] encryptedPwd;
    protected String serverPort;

	protected Context buildViewContext;
	protected HashMap paramIdMap = new HashMap(); // newId->origId

	protected Filter inputFilter, outputFilter, indeterminatesFilter; // subscription (internal) causality filters
	protected ModelScopeCausalityFilter independentFilter, intermediateFilter, resultFilter, modelIndeterminatesFilter;

	protected HashMap views = new HashMap(); // keyed by view name
	protected String currentView; // initialize to default view in constructor
	protected DArrayList viewList = new DArrayList(); // used to hold the items in the current view

	public AbstractSubscription(ModelObjectScope mObjScope, Id id)
	{
		super(id);
		setScope(mObjScope);
		graph = new DirectedGraph();
		internalCausalityManager = new DirectedGraphCausalityManager(this, graph);
		createModelScopeCausalityFilters();
	}

	public AbstractSubscription(ModelObjectScope scope, Id id, Subscription s) // copy constructor is required
	{
		super(id);
		setScope(scope);
		throw new UnsupportedOperationException(s.getName() + ": Copying Subscriptions is not allowed.\nUse Add-->Subscriptions menu to add new Subscriptions");
	}

	//if mi instanceof ModelInterfaceRuntimeClient - subscriptions between iModels and resources
	//if mi instanceof ModelInterfaceBuilder - 	for subscriptions between iModels
	public AbstractSubscription(ModelObjectScope scope, Id id, ModelInterface mi, String ifaceDeployId,
	                            int ifaceVersion, String resourceId,
	                            ServerConnection svrConn, DirectedGraph graph)
	{
		super(id);
		setScope(scope);
		this.resourceId = resourceId;
		this.ifaceId = ifaceDeployId;
		this.ifaceVersion = ifaceVersion;
		if (svrConn != null)
		{
			this.loginType = svrConn.getLoginType();
			this.loginName = svrConn.getLoginName();
			this.encryptedPwd = svrConn.getEncryptedPassword();
			this.serverPort = svrConn.getServerPort();
		}

		setName(mi.getName());
		buildViewContext = createBuildContext();
		if (mi instanceof AbstractDomeModelInterface)
			copyInterfaceObjects((AbstractDomeModelInterface) mi, graph); // create objects and graph
		else
			throw new UnsupportedOperationException("can not create Subscription from interfaces of type " +
			                                        ClassUtils.getClassName(mi));
		createFiltersAndViews();
	}

	public AbstractSubscription(ModelObjectScope scope, Element xmlElement)
	{
		super(xmlElement);
		setScope(scope);
		loadXml(xmlElement); // create objects and graph
		createFiltersAndViews();
	}

	protected Context createBuildContext()
	{
		Object[] ctrParams = new Object[]{this, BUILD_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("AbstractSubscription: createBuildContext failed");
		cxt.setName(BUILD_VIEW);
		return cxt;
	}

	private void copyInterfaceObjects(AbstractDomeModelInterface mi, DirectedGraph graph)
	{
		// copy the build context
		Object[] contextCopyInfo = (Object[])this.deepCopyContext(mi.getBuildContext(), true);
		Context newBuildContext = (Context)contextCopyInfo[0];
		buildViewContext.addModelObjectReferences(newBuildContext.getModelObjectReferences());
		// delete the newBuildContext since it was just a temporary copy
		newBuildContext.clear(); // remove all references inside of it
		this.deleteModelObject(newBuildContext);

		// create maps between old and new object
		Map oldIdToNewObject = (Map)contextCopyInfo[1];
		HashMap paramMap = new HashMap(); // origParam->newParam; used to convert interface graph
		// paramIdMap is newIdString->oldIdString
		Iterator entries = oldIdToNewObject.entrySet().iterator();
		Map.Entry entry;
		Id oldId;
		ModelObject newObject, oldObject;
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			newObject = (ModelObject)entry.getValue();
			if (newObject instanceof Parameter) { // only parameters are in graph
				oldId = (Id)entry.getKey();
				oldObject = mi.getModelObjectById(oldId);
				paramMap.put(oldObject, newObject);
				paramIdMap.put(newObject.getId().getIdString(), oldObject.getId().getIdString());
			}
		}

		// create the graph for the subscription by converting interface graph
		this.setGraph(new DirectedGraph(graph, paramMap)); // also creates internalCausalityManager
	}

	// Filterable interface
	public Collection addItemsToFilterListener(DListListener l)
	{
		modelObjects.addDListListener(l);
		return Collections.unmodifiableList(modelObjects);
	}

	public Collection removeItemsToFilterListener(DListListener l)
	{
		modelObjects.removeDListListener(l);
		return Collections.unmodifiableList(modelObjects);
	}

	protected void createFiltersAndViews() {
		createInternalCausalityFilters();
		createModelScopeCausalityFilters();
		createViews();
	}

	/**
	 * This only needs to be called once. Filters will listen to changes from the internalCausalityManager.
	 */
	protected void createInternalCausalityFilters()
	{
		inputFilter = new SubscriptionInternalCausalityFilter("INPUTS_FILTER", INPUTS) {
			protected boolean isCausalityOfInterest(CausalityStatus cs)
			{
				return CausalityStatus.INDEPENDENT.equals(cs);
			}
		};
		outputFilter = new SubscriptionInternalCausalityFilter("OUTPUTS_FILTER", OUTPUTS) {
			protected boolean isCausalityOfInterest(CausalityStatus cs)
			{
				return CausalityStatus.INTERMEDIATE.equals(cs) || CausalityStatus.RESULT.equals(cs);
			}
		};
		indeterminatesFilter = new SubscriptionInternalCausalityFilter("INDETERMINATES_FILTER", INDETERMINATES) {
			protected boolean isCausalityOfInterest(CausalityStatus cs)
			{
				return CausalityStatus.INDETERMINATE.equals(cs);
			}
		};
	}

	protected abstract class SubscriptionInternalCausalityFilter extends AbstractCausalityFilter implements ViewSupport
	{
		//for run mode client - Model m is null
		public SubscriptionInternalCausalityFilter(Model m, String idString, String name)
		{
			super(m, new Id(idString), name);
			addAll(AbstractSubscription.this.getModelObjects());
			internalCausalityManager.addCausalityChangeListener(new CausalityFilterCausalityChangeListener());
		}

		public SubscriptionInternalCausalityFilter(String idString, String name)
		{
			this(AbstractSubscription.this.getModel(), idString, name);
		}

		public void clear() {
			filteredItems.clear();
		}

		public void addAll(Collection objs) {
			Object obj;
			for (Iterator iterator = objs.iterator(); iterator.hasNext();) {
				obj = iterator.next();
				if (obj instanceof Parameter) {
					if (isCausalityOfInterest(getCausality(obj)))
						filteredItems.add(obj);
				}
			}
		}
	}

	protected void createModelScopeCausalityFilters()
	{
		independentFilter = new ModelScopeCausalityFilter(CausalityStatus.INDEPENDENT);
		intermediateFilter = new ModelScopeCausalityFilter(CausalityStatus.INTERMEDIATE);
		resultFilter = new ModelScopeCausalityFilter(CausalityStatus.RESULT);
		modelIndeterminatesFilter = new ModelScopeCausalityFilter((CausalityStatus.INDETERMINATE));
	}

	protected class ModelScopeCausalityFilter extends AbstractPropertyChangeFilter implements ViewSupport
	{
		private CausalityStatus causality;

		public ModelScopeCausalityFilter(CausalityStatus cs)
		{
			super(AbstractSubscription.this.getModel(),
			      AbstractSubscription.this.getNextId(), true);
			causality = cs;
			setName(cs.toString());
			addListToFilter(AbstractSubscription.this);
		}

		protected AbstractPropertyChangeFilter.PropertyChangeFilterFunction createFilterFunction()
		{
			return new CausalityFilterFunction("Causality Filter");
		}

		protected void processCausalityChange(CausalityChangeEvent event)
		{
			CausalityStatus oldCS = event.getOldCausalityStatus();
			CausalityStatus newCS = event.getNewCausalityStatus();
			if (oldCS == newCS) return;
			if (causality.equals(newCS)) {
				filteredItems.add(event.getParameter());
			}
			else if (causality.equals(oldCS)) {
				filteredItems.remove(event.getParameter());
			}
		}

		protected boolean keepInFilter(Object obj)
		{
			if (getModel() != null) {
				return !filteredItems.contains(obj) && getModel().isItemOfCausality(obj, causality);
			}
			else {
				ModelObjectScope relScope = getScope();
				if (relScope instanceof ModelInterfaceRuntimeClient) {
					return !filteredItems.contains(obj) && ((ModelInterfaceRuntimeClient) relScope).isItemOfSystemCausality(obj, causality);
				}
				else
					return false;
			}
		}

		protected class CausalityFilterFunction extends AbstractFilterFunction
		        implements AbstractPropertyChangeFilter.PropertyChangeFilterFunction
		{
			CausalityChangeListener ccListener;

			public CausalityFilterFunction(String name)
			{
				super(name);
				ccListener = new CausalityFilterCausalityChangeListener();
			}

			public boolean keepInFilter(Object obj)
			{
				return ModelScopeCausalityFilter.this.keepInFilter(obj);
			}

			public void addListenerTo(Object obj)
			{
				if (getModel() != null) {
					getModel().addCausalityChangeListener(obj, ccListener);
				}
			}

			public void removeListenerFrom(Object obj)
			{
				if (getModel() != null) {
					getModel().removeCausalityChangeListener(obj, ccListener);
				}
			}

			protected class CausalityFilterCausalityChangeListener implements CausalityChangeListener
			{
				public void causalityChanged(CausalityChangeEvent event)
				{
					processCausalityChange(event);
				}
			}
		}

	}
	/**
	 * Call this to create the view and call again if there is a major structural change in the subscription.
	 */
	protected void createViews()
	{
		views.put(BUILD_VIEW, Collections.unmodifiableList(buildViewContext.getModelObjectReferences()));

		// create interface causality view
		List internalCausalityView = new ArrayList();
		if (inputFilter.getItemCount()>0)
			internalCausalityView.add(inputFilter);
		if (outputFilter.getItemCount()>0)
			internalCausalityView.add(outputFilter);
		if (indeterminatesFilter.getItemCount()>0)
			internalCausalityView.add(indeterminatesFilter);
		views.put(INPUT_OUTPUT_VIEW, Collections.unmodifiableList(internalCausalityView));

		// create system causality view
		List sysCausalityView = new ArrayList();
		sysCausalityView.add(independentFilter);
		sysCausalityView.add(intermediateFilter);
		sysCausalityView.add(resultFilter);
		if (indeterminatesFilter.getItemCount()>0) {
			sysCausalityView.add(modelIndeterminatesFilter);
		}
		views.put(MODEL_CAUSALITY_VIEW, Collections.unmodifiableList(sysCausalityView));

		String newView = (currentView == null ? BUILD_VIEW : currentView); // initialize default view to build view
		currentView = null; // so that viewlist will rebuild if it was set previously
		setView(newView);
	}

	public HashMap getParamIdMap()
	{
		return paramIdMap;
	}

	// ModelObject interface
	public ModelObjectScope getScope()
	{
		return scope;
	}

	private void setScope(ModelObjectScope scope)
	{
		if (this.scope != null)
			throw new DomeObjectException("setScope", "can not change scope of Subscription!");
		this.scope = scope;
	}

	public Model getModel()
	{
		return (Model) getScope();
	}

	public ModelObjectFactory getModelObjectFactory()
	{
		return getModel().getModelObjectFactory();
	}

	public String getIfaceId()
	{
		return ifaceId;
	}

	public int getIfaceVersion()
	{
		return ifaceVersion;
	}

	public String getResourceId()
	{
		return resourceId;
	}

	public DirectedGraph getGraph()
	{
		return graph;
	}

	public String getLoginName()
	{
		return loginName;
	}

	public byte[] getLoginPassword()
	{
		return encryptedPwd;
	}

	public String getLoginType()
	{
		return loginType;
	}

	public void setGraph(DirectedGraph graph)
	{
		this.graph = graph;
		if (internalCausalityManager==null)
			internalCausalityManager = new DirectedGraphCausalityManager(this, this.graph);
		else
			internalCausalityManager.setCausalityGraph(this.graph);
		firePropertyChange(GRAPH, null, this.graph);
	}

	protected TypeInfo getTypeInfo()
	{
		return Subscription.TYPE_INFO;
	}

	/**
	 * Returns list of valid ModelObject types.
	 * Return from modelobject factory.
	 */
	public List getValidModelObjectTypes()
	{
		return Collections.EMPTY_LIST;
	}

	public synchronized void removeDeletionListener(DeletionListener l, boolean deleteIfNoReference)
	{
		deletionListeners.remove(l);
		// ModelObject interface; not necessary to keep track of references here
	}

	// CausalitySupport
	public CausalityManager getCausalityManager()
	{
		return internalCausalityManager;
	};

	public List getItems(CausalityStatus causality)
	{
		return internalCausalityManager.getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return internalCausalityManager.isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return internalCausalityManager.getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		internalCausalityManager.addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		internalCausalityManager.removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		internalCausalityManager.addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		internalCausalityManager.removeCausalityChangeListener(obj, l);
	}

	public String getCurrentView() {
		return currentView;
	}

	// ViewSupport interface
	public List getView()
	{
		return Collections.unmodifiableList(viewList);
	}

	public void addViewListener(DListListener l)
	{
		viewList.addDListListener(l);
	}

	public void removeViewListener(DListListener l)
	{
		viewList.removeDListListener(l);
	}

	public synchronized void setView(String view) {
		if(MODEL_CAUSALITY_VIEW.equals(view)) {
			if(MODEL_CAUSALITY_VIEW.equals(currentView))
				return;
			viewList.clear();
			viewList.addAll((List) views.get(MODEL_CAUSALITY_VIEW));
			currentView = MODEL_CAUSALITY_VIEW;
		}
		else if(INPUT_OUTPUT_VIEW.equals(view))
		{
			if (INPUT_OUTPUT_VIEW.equals(currentView))
				return;
			viewList.clear();
			viewList.addAll((List)views.get(INPUT_OUTPUT_VIEW));
			currentView = INPUT_OUTPUT_VIEW;
		}
		else if (BUILD_VIEW.equals(view)) {
			if (BUILD_VIEW.equals(currentView))
				return;
			viewList.clear();
			viewList.addAll((List)views.get(BUILD_VIEW));
			currentView = BUILD_VIEW;
		}
	}

	public void deleteAllModelObjects()
	{
		//remove iModel mapping
		ConnectionMappingManager mgr = ((DomeModel) getModel()).getMappingManager();
		for (int i = 0; i < modelObjects.size(); i++) {
			Object o = modelObjects.get(i);
			if(o instanceof Parameter)  {
				Parameter parameter = (Parameter)o;
				mgr.removeAllMappings(parameter);
			}
		}
		//remove project mapping
		ConnectionMappingManager pmgr = ((DomeModel) getModel()).getIntegrationProject().getMappingManager();
		for (int i = 0; i < modelObjects.size(); i++) {
			Object o = modelObjects.get(i);
			if (o instanceof Parameter) {
				Parameter parameter = (Parameter) modelObjects.get(i);
				pmgr.removeAllMappings(parameter);
			}
		}
		super.deleteAllModelObjects();
	}

	protected String contentToString()
	{
		return "";
	}

	public String getXmlTag()
	{
		return Subscription.XML_TAG;
	}

	public String getResourceName() {
		IntegrationProject project = ((DomeModel)scope).getIntegrationProject();
		ProjectResourceInfo info = project.getResource(resourceId);
		String resourcename;

        if(info!=null){
         resourcename = info.getName();
        }
        //subscription is nested imodel - find it! _i
        else{
         ProjectIntegrationModelInfo  info_2 = project.getIntegrationModel(resourceId);
         resourcename = info_2.getName();
        }

		return resourcename;
	}

	public String getResourceType()
	{
		IntegrationProject project = ((DomeModel) scope).getIntegrationProject();
		ProjectResourceInfo info = project.getResource(resourceId);
		String type = info.getType();
		return type;
	}

	protected void loadXml(Element xmlElement)
	{
		XMLUtils.makeRootElement(xmlElement);
		String version = xmlElement.attributeValue("version");
		if(version != null)
			this.ifaceVersion = new Integer(version).intValue();
		Element locationXml = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "locationIds");
		this.resourceId = locationXml.attributeValue("resourceId");
		this.ifaceId = locationXml.attributeValue("ifaceId");
		this.loginType = xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "loginInfo/loginType").getText();
		if (!LoginUtils.GUEST.equals(loginType)) {
			this.loginName = xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "loginInfo/loginName").getText();
			this.encryptedPwd = Converters.stringToByteArray(xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "loginInfo/password").getText());
		}
		else {
			this.loginName = "";
		}

		HashMap idParamMap = loadParameters(xmlElement);
		loadVisualizations(xmlElement);
		loadContexts(xmlElement);

		Element paramIdMapXml = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "paramIdMap");
		this.paramIdMap = XMLUtils.parseStringMap(paramIdMapXml, "paramIdMap", "entry", "thisId", "origId");
		Element graphXml = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "directedGraph");
		DirectedGraph idGraph = new DirectedGraph(graphXml);
		this.setGraph(new DirectedGraph(idGraph, idParamMap));  // also creates internalCausalityManager
	}

	/**
	 * @param xmlElement
	 * @return map of paramIdString to parameter object
	 */
	protected HashMap loadParameters(Element xmlElement)
	{
		HashMap idParamMap = new HashMap();
		List domeLists = new ArrayList(); // add DomeLists so contents can be added after all parameters created
		Parameter param;
		Element element;
		List params = xmlElement.selectNodes("/" + getXmlTag() + "/parameters/" + Parameter.XML_TAG);
		for (Iterator iter = params.iterator(); iter.hasNext();) {
			element = (Element) iter.next();
			param = (Parameter) getModelObjectFactory().newInstance(element, new Object[]{this, element});
			if (param != null) {
				modelObjects.add(param);
				idParamMap.put(param.getId().getIdString(), param);
				if (param.getCurrentDataObject() instanceof DomeList)
					domeLists.add(param);
			}
		}
		// add list parameter references
		for (int i = 0; i < domeLists.size(); i++) {
			param = (Parameter) domeLists.get(i);
			((DomeListData)param.getCurrentDataObject()).loadXmlData();
		}
		return idParamMap;
	}

	protected void loadVisualizations(Element xmlElement)
	{
		Visualization vis;
		Element element;
		List visualizations = xmlElement.selectNodes("/" + getXmlTag() + "/visualizations/" + Visualization.XML_TAG);
		for (Iterator iter = visualizations.iterator(); iter.hasNext();) {
			vis = null;
			element = (Element) iter.next();
			vis = (Visualization) getModelObjectFactory().newInstance(element, new Object[]{this, element});
			if (vis != null) {
				modelObjects.add(vis);
			}
		}
	}

	protected void loadContexts(Element xmlElement)
	{
		List contexts = new ArrayList(); // add Contexts so references can be added after everything created
		List contextsXml = xmlElement.selectNodes("/" + getXmlTag() + "/contexts/" + Context.XML_TAG);
		Element cxtXml;
		Context cxt;
		for (int i = contextsXml.size()-1; i >= 0; i--) {
			cxtXml = (Element) contextsXml.get(i);
			// do not use model object factory until contexts are changed to not load their references immediately
			cxt = new DefaultContextBuilder(this, cxtXml, false);
			if (cxt != null) {
				modelObjects.add(cxt);
				contexts.add(cxt);
			}
		}

		// load references in the contexts
		for (int i = 0; i < contexts.size(); i++) {
			((AbstractContext) contexts.get(i)).loadReferencesFromXml();
		}

		// set the build context
		buildViewContext = (Context) modelObjectsById.get(BUILD_CONTEXT_ID);
		if (buildViewContext == null)
			throw new RuntimeException("Error loading Subscription \""+getName()+"\" from xml: no build context found");
		modelObjects.remove(buildViewContext);
	}

	protected void addXmlContent(Element xmlElement)
	{
		xmlElement.addAttribute("version", new Integer(ifaceVersion).toString());
		xmlElement.addElement("locationIds").addAttribute("resourceId", resourceId).addAttribute("ifaceId", ifaceId);
		Element loginInfoXml = xmlElement.addElement("loginInfo");
		if(loginType != null)
			loginInfoXml.addElement("loginType").setText(loginType);
		else
			loginInfoXml.addElement("loginType").setText("");
		if (!LoginUtils.GUEST.equals(loginType)) {
			if(loginName != null)
				loginInfoXml.addElement("loginName").setText(loginName);
			else
                loginInfoXml.addElement("loginName").setText("");
			if(encryptedPwd != null)
				loginInfoXml.addElement("password").setText(Converters.byteArrayToString(encryptedPwd));
			else
				loginInfoXml.addElement("password").setText("");
			if(serverPort != null)
				loginInfoXml.addElement("serverPort").setText(serverPort);
			else
				loginInfoXml.addElement("serverPort").setText("");
		}

		Element paramElement = DocumentHelper.createElement("parameters");
		Element visElement = DocumentHelper.createElement("visualizations");
		Element cxtElement = DocumentHelper.createElement("contexts");

		// add parameters, contexts, and visualizations
		cxtElement.add(buildViewContext.toXmlElement());
		for (Iterator iter = modelObjects.listIterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof Parameter) {
				Element param = ((Parameter) obj).toXmlElement();
				paramElement.add(param);
			}
			else if (obj instanceof Visualization) {
				Element vis = ((Visualization) obj).toXmlElement();
				visElement.add(vis);
			}
			else if (obj instanceof Context) {
				Element cxt = ((Context) obj).toXmlElement();
				cxtElement.add(cxt);
			}
			else {
				System.err.println("AbstractSubscription.toXMLElement: unsupported object type "+ClassUtils.getClassName(obj));
			}
		}

		if (paramElement.elements().size()>0)
			xmlElement.add(paramElement);
		if (visElement.elements().size()>0)
			xmlElement.add(visElement);
		if (cxtElement.elements().size() > 0)
			xmlElement.add(cxtElement);

		xmlElement.add(graph.toXmlElement(getId().getIdString()));
		XMLUtils.addStringMap(xmlElement, "paramIdMap", "entry", "thisId", "origId", paramIdMap);
	}

    public Collection getModelObjectParameters() {
        Collection coll = new ArrayList(modelObjects.size());
        for (Iterator i = modelObjects.iterator(); i.hasNext();) {
            ModelObject mobj = (ModelObject) i.next();
            if (mobj instanceof Parameter) {
                coll.add(mobj);
            }
        }
        return Collections.unmodifiableCollection(coll);
    }

    public String getServerPort()  {   // _i
        return serverPort;
    }

  }
