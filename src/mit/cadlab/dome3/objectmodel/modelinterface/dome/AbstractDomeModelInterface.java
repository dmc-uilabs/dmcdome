// AbstractDomeModelInterface.java
package mit.cadlab.dome3.objectmodel.modelinterface.dome;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractPropertyChangeFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.*;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.causality.*;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.ShiftSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

// todo: actually handle project input/output causality properly; right now it just mirrors the causality of the model parameter in its model

public abstract class AbstractDomeModelInterface extends AbstractModelInterface implements DomeModelInterface, Filterable
{
	protected ModelObjectFactory m_moFactory;
    //system causality
    protected CausalityManager systemCausalityManager;
	// internal causality support
	protected transient CausalityStatus newObjectCausality = CausalityStatus.INDETERMINATE; // default
	protected Filter inputFilter, outputFilter, indeterFilter;
	protected Filter independentFilter, intermediateFilter, resultFilter;

	protected Context buildViewContext;

	protected HashMap modelObjectMap = new HashMap();

	protected InterfaceModelView modelview;

	//when the relation parameter in the model changes its causality to intermediate, we
	//do not want to show those parameters in the interface.  So we hold those parameters
	//in the following list.  so that the parameter can be brought back in very easily.
	protected List tempModelObjects = new ArrayList();
	//key - intermediate object id, value - intermediate object
	protected HashMap tempModelObjectsMap = new HashMap();

	//for customer gui key is filedata, value is the name appear in combobox
	protected ArrayList customGUIList = new ArrayList();
	public static final String CUSTOMGUICHANGE = "custom gui changed";
    protected boolean isDuplicateOperation;   //used during interface duplication - initailly false

	public AbstractDomeModelInterface(Model m, Id id)
	{
        super(m, id);
        buildViewContext = createBuildContext();
        modelObjects.add(buildViewContext);
        createInternalCausalityFilters();
        createSystemCausalityFilters();
        Id modelviewId = new Id(UUIDGenerator.create());
        modelview = new InterfaceModelView(this, modelviewId);
	}

	public AbstractDomeModelInterface(Model m, Id id, ModelObjectScope mObjScope)
	{
		this(m, id, mObjScope, false);    // false = do not copy objects in AbstractModelObjectScope
        isDuplicateOperation = true;

		// copy objects and mappings copied if same model (operation: duplicate interface)
		// copy objects only if different model
		if (mObjScope instanceof DomeModelInterface) {
			boolean copyMappings = false;
			if(mObjScope.getModel() != null)
				copyMappings = mObjScope.getModel().equals(m);
			copyModelObjects((ModelInterface) mObjScope, copyMappings);
			Id modelviewId =  new Id(UUIDGenerator.create());
			InterfaceModelView viewToCopy = ((DomeModelInterface)mObjScope).getModelView();
			modelview = new InterfaceModelView(this, modelviewId, viewToCopy);
		}
		else {
			throw new UnsupportedOperationException("Unkonwn type " + mObjScope.getClass());
		}
		isDuplicateOperation = false;
	}

	protected AbstractDomeModelInterface(Model m, Id id, ModelObjectScope mObjScope, boolean copyObjects)
	{
		super(m, id, mObjScope, copyObjects);

		createInternalCausalityFilters();
		createSystemCausalityFilters();
	}

	public AbstractDomeModelInterface(Model m, Element xmlElement)
	{
		super(m, xmlElement);

		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml model interface description");
		Element customGUIElement = (Element) xmlElement.selectSingleNode("customGUIs");
		if (customGUIElement != null) {
			//loads the customGUI info
			List element = customGUIElement.selectNodes(CustomGuiInfo.XML_TAG);
			for (Iterator iter = element.iterator(); iter.hasNext();) {
				Element e = (Element) iter.next();
				customGUIList.add(new CustomGuiInfo(e));
			}
		}

	}

	public ArrayList getCustomGUIList()
	{
		return customGUIList;
	}

	public boolean hasCustomGui()
	{
		return customGUIList.size() == 0 ? false : true;
	}

	public InterfaceModelView getModelView() {
		return modelview;
	}

	protected Context createBuildContext()
	{
		Object[] ctrParams = new Object[]{this, BUILD_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("AbstractDomeModelIterface: createBuildContext failed");
		cxt.setName(BUILD_VIEW);
		return cxt;
	}

	public Context getBuildContext()
	{
		return (Context) getModelObjectById(BUILD_CONTEXT_ID);
	}

	public HashMap getModelObjectMap()
	{
		return modelObjectMap;
	}

	public ModelObject getModelViewObjectById(Id modelObjectId)
	{
		return modelview.getModelObjectById(modelObjectId);
	}

	public Context getModelContext()
	{
		return (Context) modelview.getModelObjectById(MODEL_CONTEXT_ID);
	}

	public List getModelViewObjects()
	{
		return (List)modelview.getModelObjects();
	}

    protected void changeCausality(Object obj, CausalityStatus cause)
	{
		((ModelInterfaceInternalCausalityManager) internalCausalityManager).changeCausality(
		        obj, cause);
	}

	protected void copyModelObjects(ModelInterface iface, boolean copyMappings)
	{
		//create build and model context
		buildViewContext = createBuildContext();
		ConnectionMappingManager mmgr = null;
		if(model instanceof DomeModel) {
			mmgr = ((DomeModel) model).getMappingManager();
		}
		else if(model instanceof IntegrationProject) {
			mmgr = ((IntegrationProject) model).getMappingManager();
		}

		//Copy modelObjects - causal views
		Collection origModelObjects = iface.getModelObjects();
		ArrayList mObjs = new ArrayList(origModelObjects); // mutable list
		modelObjectMap = super.copyModelObjects(mObjs);

		for (Iterator i = mObjs.iterator(); i.hasNext();) {
			ModelObject mObj = (ModelObject) i.next();
			if (mObj instanceof Parameter) {
				if (copyMappings) {
					Collection mapping = mmgr.getMappingsForParameter((Parameter) mObj);
					Parameter newobj = (Parameter) modelObjectMap.get(mObj.getId());
					if(newobj != null) {
						mmgr.addMappings(newobj, mapping);
					}
				}
			}
		}
       	//build view
		Context oldBuildContext = ((DomeModelInterface)iface).getBuildContext();
		for(Iterator i = oldBuildContext.getModelObjectReferences().iterator(); i.hasNext(); ) {
			ModelObject mObj = (ModelObject)i.next();
			ModelObject obj = (ModelObject)modelObjectMap.get(mObj.getId());
			if(obj != null) {
				buildViewContext.addModelObjectReference(obj);
			}
		}
		modelObjects.add(buildViewContext);
	}


	protected CausalityManager createInternalCausalityManager()
	{
		return new ModelInterfaceInternalCausalityManager();
	}

	protected CausalityManager createInternalCausalityManager(Element xmlElement)
	{
		return new ModelInterfaceInternalCausalityManager(xmlElement);
	}

	protected class ModelInterfaceInternalCausalityManager
	        extends AbstractModelObjectScope.AbstractInternalCausalityManager
	{
		protected ConnectionMappingManager mappingManager;
		protected MappingChangeListener mappingListener;
		protected CausalityChangeListener causalityListener;
		protected HashMap paramMappings = new HashMap(); // ifaceParam-modelParam mapping (since no way to get hold of old modelParam when mapping changes)

		public ModelInterfaceInternalCausalityManager()
		{
			initManager();
			addObjects();
		}

		public ModelInterfaceInternalCausalityManager(Element xmlElement)
		{
			super(xmlElement);
			initManager();
		}

		protected void initManager() {
			Model m = getModel();
			if (m instanceof AbstractDomeModel) {
				mappingManager = ((AbstractDomeModel) m).getMappingManager();
			}
			else if (m instanceof IntegrationProject) {
				mappingManager = ((IntegrationProject) m).getMappingManager();
			}
			mappingListener = new InterfaceMappingChangesListener();
			causalityListener = new InterfaceParentCausalityListener();
		}

		protected void addObjects() {
			ModelObject mObj;
			for (int i = 0; i < modelObjects.size(); i++) {
				mObj = (ModelObject) modelObjects.get(i);
				if (mObj instanceof Parameter) {

				}
			}
		}

		protected void addObject(Object obj, CausalityStatus cs)
		{
			super.addObject(obj, cs);
			mappingManager.addMappingChangeListener((Parameter)obj, mappingListener);
		}

		protected void removeObject(Object obj)
		{
			mappingManager.removeMappingChangeListener((Parameter)obj, mappingListener);
			super.removeObject(obj);

		}

		protected class InterfaceParentCausalityListener implements CausalityChangeListener {
			public void causalityChanged(CausalityChangeEvent event)
			{
				processCausalityChangeEvent(event);
			}
		}

		protected void processCausalityChangeEvent(CausalityChangeEvent event) {
			Parameter modelParam = (Parameter)event.getParameter();
			Collection ifaceParams = mappingManager.getInterfaceConnections(modelParam);
			Parameter ifaceParam;
			for (Iterator iterator = ifaceParams.iterator(); iterator.hasNext();) {
				ifaceParam = (Parameter) iterator.next();
				if (AbstractDomeModelInterface.this.modelObjects.contains(ifaceParam)) { // in this interface
					changeCausality(ifaceParam, event.getNewCausalityStatus());
				}
			}
		}

		protected CausalityStatus getInitialCausality(Object obj)
		{
			return getNewObjectCausality();
		}

		/**
		 * listens to all mapping changes for model parameters
		 */
		class InterfaceMappingChangesListener implements MappingChangeListener
		{
			public void mappingChanged(MappingChangeEvent event)
			{
				processMappingChange(event);
			}
		}

		protected void processMappingChange(MappingChangeEvent event) {
			Parameter ifaceParam = event.getParameter();
			if (ifaceParam == null || !modelObjects.contains(ifaceParam)) // not parameter or not in this interface
				return;
			Collection mappedParams = event.getMappings();
			Parameter newModelParam = null;
			if (mappedParams != null && mappedParams.size() == 1)
				newModelParam = (Parameter) mappedParams.toArray()[0];
			Parameter oldModelParam = null;
			if (newModelParam == null)
				oldModelParam = (Parameter) paramMappings.remove(ifaceParam);
			else
				oldModelParam = (Parameter) paramMappings.put(ifaceParam, newModelParam);
			if (oldModelParam != null)
				oldModelParam.getModel().removeCausalityChangeListener(oldModelParam, causalityListener);
			if (newModelParam != null) {
				newModelParam.getModel().addCausalityChangeListener(newModelParam, causalityListener);
				changeCausality(ifaceParam, newModelParam.getModel().getCausality(newModelParam));
			}
		}
	}

	public CausalityStatus getNewObjectCausality()
	{
		return newObjectCausality;
	}

	public void setNewObjectCausality(CausalityStatus causality)
	{
		newObjectCausality = causality;
	}

	public void resetNewObjectCausality()
	{
		newObjectCausality = CausalityStatus.INDEPENDENT; // default
	}

	// Filters
	protected void createInternalCausalityFilters()
	{
		inputFilter = new InputFilter();
		outputFilter = new OutputFilter();
		indeterFilter = new IndeterminateFilter();
	}

	protected class InputFilter extends InterfaceInternalCausalityFilter implements ViewSupport
	{
		//for run mode client - Model m is null
		public InputFilter(Model m)
		{
			super(m, "INPUTS_FILTER", DomeModelInterface.INPUTS);
		}

		public InputFilter()
		{
			super("INPUTS_FILTER", DomeModelInterface.INPUTS);
		}

		protected boolean isCausalityOfInterest(CausalityStatus cs)
		{
			return cs != null && cs.equals(CausalityStatus.INDEPENDENT);
		}
	}

	protected class OutputFilter extends InterfaceInternalCausalityFilter implements ViewSupport
	{
		//for run mode client - Model m is null
		public OutputFilter(Model m)
		{
			super(m, "OUTPUTS_FILTER", DomeModelInterface.OUTPUTS);
		}

		public OutputFilter()
		{
			super("OUTPUTS_FILTER", DomeModelInterface.OUTPUTS);
		}

		protected boolean isCausalityOfInterest(CausalityStatus cs)
		{
			return cs != null && (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT));
		}

		public Element toXmlElement()
		{
			Element xml = super.toXmlElement(); //from AbstractEventFilter
			//add causality attribute on the parameter ref
			Element paramsElement = (Element) xml.selectSingleNode("parameters");
			if (paramsElement == null) {
				return xml;
			}
			List paramElementList = paramsElement.elements();
			if (paramElementList == null || paramElementList.isEmpty()) {
				return xml;
			}
			for (Iterator i = paramElementList.iterator(); i.hasNext();) {
				Element paramElement = (Element) i.next();
				Id paramId = parseXmlRef(paramElement);
				Object paramObj = getModelObjectById(paramId);
				CausalityStatus stat = internalCausalityManager.getCausality(paramObj);
				paramElement.addAttribute("causality", stat.toString());
			}
			return xml;
		}
	}

	protected class IndeterminateFilter extends InterfaceInternalCausalityFilter implements ViewSupport
	{
		//for run mode client - Model m is null
		public IndeterminateFilter(Model m)
		{
			super(m, "INDETERMINATES_FILTER", DomeModelInterface.INDETERMINATES);
		}

		public IndeterminateFilter()
		{
			super("INDETERMINATES_FILTER", DomeModelInterface.INDETERMINATES);
		}

		protected boolean isCausalityOfInterest(CausalityStatus cs)
		{
			return cs != null && (cs.equals(CausalityStatus.INDETERMINATE));
		}
	}

	protected abstract class InterfaceInternalCausalityFilter extends AbstractCausalityFilter
	        implements ShiftSupport
	{

		//for run mode client - Model m is null
		public InterfaceInternalCausalityFilter(Model m, String idString, String name)
		{
			super(m, new Id(idString), name);
			internalCausalityManager.addCausalityChangeListener(new AbstractCausalityFilter.CausalityFilterCausalityChangeListener());
		}

		public InterfaceInternalCausalityFilter(String idString, String name)
		{
			super(AbstractDomeModelInterface.this.getModel(), new Id(idString), name);
			internalCausalityManager.addCausalityChangeListener(new AbstractCausalityFilter.CausalityFilterCausalityChangeListener());
		}

		// ShiftSupport interface
		public void shiftLeft(int[] indices)
		{
			filteredItems.shiftLeft(indices);
		}

		public void shiftRight(int[] indices)
		{
			filteredItems.shiftRight(indices);
		}

	}


	protected void createSystemCausalityFilters()
	{
		independentFilter = new SystemCausalityFilter(CausalityStatus.INDEPENDENT);
		intermediateFilter = new SystemCausalityFilter(CausalityStatus.INTERMEDIATE);
		resultFilter = new SystemCausalityFilter(CausalityStatus.RESULT);
	}

	protected class SystemCausalityFilter extends AbstractPropertyChangeFilter implements ViewSupport
	{
		private CausalityStatus causality;

		//for run mode client - model is null
		public SystemCausalityFilter(Model m, CausalityStatus cs)
		{
			super(m, AbstractDomeModelInterface.this.getNextId(), true);
			causality = cs;
			setName(cs.toString());
			//arguemnt has to be a Filterable
			addListToFilter(AbstractDomeModelInterface.this);
		}

		public SystemCausalityFilter(CausalityStatus cs)
		{
			super(AbstractDomeModelInterface.this.getModel(),
			      AbstractDomeModelInterface.this.getNextId(), true);
			causality = cs;
			setName(cs.toString());
			//arguemnt has to be a Filterable
			addListToFilter(AbstractDomeModelInterface.this);
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
			} else if (causality.equals(oldCS)) {
				filteredItems.remove(event.getParameter());
			}
		}

		protected boolean keepInFilter(Object obj)
		{
			if (getModel() != null) {
                if (AbstractDomeModelInterface.this instanceof ModelInterfaceBuilder) {
                    ModelInterfaceBuilder build = (ModelInterfaceBuilder) AbstractDomeModelInterface.this;
                    return !filteredItems.contains(obj) && build.isItemOfSystemCausality(obj, causality);
                    //return !filteredItems.contains(obj) && getModel().isItemOfCausality(obj, causality);
                } else
                    return false;
			} else { //for run time interface client look for system level causality somewhere else
				if (AbstractDomeModelInterface.this instanceof ModelInterfaceRuntimeClient) {            //TODO clean up
					ModelInterfaceRuntimeClient client = (ModelInterfaceRuntimeClient) AbstractDomeModelInterface.this;
					return !filteredItems.contains(obj) && client.isItemOfSystemCausality(obj, causality);
				} else
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
				return SystemCausalityFilter.this.keepInFilter(obj);
			}

			public void addListenerTo(Object obj)
			{
				if (getModel() != null) {
					getModel().addCausalityChangeListener(obj, ccListener);
				} else {
					if (AbstractDomeModelInterface.this instanceof ModelInterfaceRuntimeClient) {
						ModelInterfaceRuntimeClient client = (ModelInterfaceRuntimeClient) AbstractDomeModelInterface.this;
						client.getSystemCausalityManager().addCausalityChangeListener(obj, ccListener);
					}
				}
			}

			public void removeListenerFrom(Object obj)
			{
				if (getModel() != null) {
					getModel().removeCausalityChangeListener(obj, ccListener);
				} else {
					if (AbstractDomeModelInterface.this instanceof ModelInterfaceRuntimeClient) {
						ModelInterfaceRuntimeClient client = (ModelInterfaceRuntimeClient) AbstractDomeModelInterface.this;
						client.getSystemCausalityManager().removeCausalityChangeListener(obj, ccListener);
					}
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

	public boolean isSaved()
	{
		return hasChanged();
	}

	public boolean hasChanged()
	{
		Document xmlDoc = createXmlDocument(false);
		return hasChanged(xmlDoc);
	}

	public void updateSavedStatus()
	{
		lastSavedXml = createXmlDocument(false).asXML();
	}

	public Document createXmlDocument()
	{
		return createXmlDocument(true);
	}

	/**
	 * This should only be called once at a time!
	 * @param generateViews should view element be generated
	 * @return
	 */
	public synchronized Document createXmlDocument(boolean generateViews)
	{
		Document doc = DocumentFactory.getInstance().createDocument();
		Element xmlElement = toXmlElement(generateViews);
		doc.add(xmlElement);
		return doc;
	}

	public Element toXmlElement()
	{
		return toXmlElement(true);
	}

	public Element toXmlElement(boolean generateViews)
	{
		Element ifaceElement = super.toXmlElement();

		Element paramElement, relElement, cxtElement, visElement, viewsElement;
		paramElement = DocumentHelper.createElement("parameters");
		relElement = DocumentHelper.createElement("relations");
		cxtElement = DocumentHelper.createElement("contexts");
		visElement = DocumentHelper.createElement("visualizations");

		ifaceElement.add(paramElement);
		ifaceElement.add(visElement);
		ifaceElement.add(relElement);
		ifaceElement.add(cxtElement);

        modelview.initXmlSetup(paramElement, relElement, cxtElement, visElement);
		modelview.toXmlElement();

		for (Iterator iter = modelObjects.listIterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof Parameter) {
				Element param = ((Parameter) obj).toXmlElement();
				paramElement.add(param);
			} else if (obj instanceof Relation) {
				Element rel = ((Relation) obj).toXmlElement();
				relElement.add(rel);
			} else if (obj instanceof Visualization) {
				Element vis = ((Visualization) obj).toXmlElement();
				visElement.add(vis);
			} else if (obj instanceof Context) {
				if (!((Context) obj).getId().equals(BUILD_CONTEXT_ID)) {
					Element cxt = ((Context) obj).toXmlElement();
					cxtElement.add(cxt);
				}
			}
		}
		if (customGUIList.size() != 0)
			XMLUtils.addCollection(ifaceElement, "customGUIs", customGUIList);

		if (generateViews) {
			viewsElement = DocumentHelper.createElement(DomeModelInterface.VIEWS);
			ifaceElement.add(viewsElement);

			//add views
			//build view
			Element buildViewElement = DocumentHelper.createElement(DomeModelInterface.VIEW);
			buildViewElement.addAttribute(DomeModelInterface.NAME, DomeModelInterface.BUILD_VIEW);
			Element buildconElement = this.getBuildContext().toXmlElement();
			buildViewElement.add(buildconElement);
			viewsElement.add(buildViewElement);
			//interface causality view
			Element interfaceCausalityViewElement = DocumentHelper.createElement(DomeModelInterface.VIEW);
			interfaceCausalityViewElement.addAttribute(DomeModelInterface.NAME, DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
			Element inputFilterElement = inputFilter.toXmlElement();
			interfaceCausalityViewElement.add(inputFilterElement);
			Element outputFilterElement = outputFilter.toXmlElement();
			interfaceCausalityViewElement.add(outputFilterElement);
			Element indeterFilterElement = indeterFilter.toXmlElement();
			interfaceCausalityViewElement.add(indeterFilterElement);
			viewsElement.add(interfaceCausalityViewElement);
			//system causality view
			// won't be needed in deploy or run modes
/*		Element systemCausalityViewElement = DocumentHelper.createElement(DomeModelInterface.VIEW);
		systemCausalityViewElement.addAttribute(DomeModelInterface.NAME, DomeModelInterface.SYSTEM_CAUSALITY_VIEW);
		Element independentFilterElement = independentFilter.toXmlElement();
		systemCausalityViewElement.add(independentFilterElement);
		Element intermediateFilterElement = intermediateFilter.toXmlElement();
		systemCausalityViewElement.add(intermediateFilterElement);
		Element resultFilterElement = resultFilter.toXmlElement();
		systemCausalityViewElement.add(resultFilterElement);
		viewsElement.add(systemCausalityViewElement);*/
		}
		return ifaceElement;
	}

	public Element createMappingElement()
	{
		Element topElement = DocumentHelper.createElement("mappingsandintermediates");
		Element mapElement = DocumentHelper.createElement("mappings");

		// add mappings
		ConnectionMappingManager mgr = null;
		if(model instanceof DomeModel) {
			mgr = ((DomeModel) model).getMappingManager();
		}
		else if(model instanceof IntegrationProject) {
			mgr = ((IntegrationProject) model).getMappingManager();
		}

		Element mapSubElements = mgr.toXmlElement(this, "interfacemappings");
		//get modelview mappings
		if(mapSubElements == null) {
			mapSubElements = mgr.toXmlElement(modelview, "interfacemappings");
		}
		else {
			mapSubElements = mgr.toXmlElement(modelview, mapSubElements);
		}
		if (mapSubElements != null) {
			mapElement.add(mapSubElements);
		}

		//Also store temporary objects (intermediate variables) in the model here
		//so that these objects can be reloaded back into mapping manager later
		Element tempElement = DocumentHelper.createElement("intermediateobjects");
		//store tempObject values
		//these are intermediate variables not being shown in the build and causal views
		for (Iterator i = tempModelObjects.iterator(); i.hasNext();) {
			ModelObject tempobj = (ModelObject) i.next();
			tempElement.add(tempobj.toXmlElement());
		}

		topElement.add(mapElement);
		topElement.add(tempElement);

		return topElement;
	}


	public void addCustomGui(CustomGuiInfo cInfo)
	{
		customGUIList.add(cInfo);
		firePropertyChange(CUSTOMGUICHANGE, null, cInfo);
	}

	public void removeCustomGui(CustomGuiInfo cInfo)
	{
		customGUIList.remove(cInfo);
		firePropertyChange(CUSTOMGUICHANGE, cInfo, null);
	}
    public void editCustomGui(CustomGuiInfo oldC,CustomGuiInfo newC){
        oldC.setClassName(newC.getClassName());
        oldC.setJarFilePath(newC.getJarFilePath());
        oldC.setShortName(newC.getShortName());
		firePropertyChange(CUSTOMGUICHANGE, oldC, newC);
    }

	//abstract public Hashtable getParameterSystemCausality();
}
