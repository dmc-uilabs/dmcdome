// ModelInterfaceBase.java
package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectSolver;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.touchgraph.graphlayout.GLPanel;


public abstract class ModelInterfaceBase extends AbstractDomeModelInterface {
    public static final String FILENAME = "fileName";
    public static final String CONTEXT = "Context";
    public static final String PROCEDURALRELATION = "Procedural Relation";
    public static final String MODEL_NODE = "*MODEL*";

    protected String fileName = "";
    protected boolean shouldSave = true;
    protected HashMap views = new HashMap(); // keyed by view name
    protected int currentInsertionIndex = -1;
    protected List currentItems = Collections.EMPTY_LIST;
    public String currentView = DomeModelInterface.BUILD_VIEW;

    //so that references can be distinguished from the actual objects in the model
    //and can be ignored.
    protected Set distinctModelObjs = new HashSet();
    //For view change in tree
    protected DArrayList viewList = new DArrayList();
    protected boolean isviewchangeOperation;
    protected boolean isviewListLoaded;

    protected DirectedGraph interfaceGraph;

    public ModelInterfaceBase(Model m, Id id) {
        super(m, id);
        createViews();
    }

    public ModelInterfaceBase(Model m, String id, String name) {
        super(m, new Id(id));
        setName(name);
        createViews();
    }

    public ModelInterfaceBase(Model m, Id id, ModelObjectScope mObjScope) {
        super(m, id, mObjScope);
        createViews();
    }

    public ModelInterfaceBase(Model m, Element xmlContent) {
        this(m, xmlContent, null);
    }

    public ModelInterfaceBase(Model m, Element xmlContent, Element xmlMappings) {
        super(m, xmlContent);

        createInternalCausalityFilters();
        createSystemCausalityFilters();

        // First populate modelViewObjects completely.
        //NOTE: This constructor removes all the modelview objects from
        //the xmlContent and XmlMappings variables.
        modelview = new InterfaceModelView(this, xmlContent, xmlMappings);

        // read parameters
        Parameter param;
        Element element;
        List params = xmlContent.selectNodes("/" + getXmlTag() + "/parameters/" + Parameter.XML_TAG);
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            param = null;
            element = (Element) iter.next();
            param = (Parameter) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (param != null) {
                modelObjects.add(param);
            }
        }
        // add list parameter references
        for (Iterator i = modelObjects.iterator(); i.hasNext();) {
            Parameter p = (Parameter) i.next();
            DataObject dobj = p.getDataObjectForType("List");
            if (dobj != null) {
                ((DomeListData) dobj).loadXmlData();
            }
        }

        //read visualizations
        Visualization vis;
        List vises = xmlContent.selectNodes("/" + getXmlTag() + "/visualizations/" + Visualization.XML_TAG);
        for (Iterator iter = vises.iterator(); iter.hasNext();) {
            vis = null;
            element = (Element) iter.next();
            vis = (Visualization) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (vis != null) {
                modelObjects.add(vis);
            }
        }

        //No relations exist in build and causal views
        // read contexts
        Context cxt;
        List contexts = xmlContent.selectNodes("/" + getXmlTag() + "/contexts/" + Context.XML_TAG);
        for (ListIterator iter = contexts.listIterator(contexts.size()); iter.hasPrevious();) { //so that nested contexts are loaded properly
            // construct the next context
            cxt = null;
            element = (Element) iter.previous();
            //interface contexts elements doesn't have the build context
            cxt = (Context) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (cxt != null) {
                modelObjects.add(0, cxt);           //so that nested contexts are loaded properly
            }
        }

        Element BuildContextElement = null;
        List viewList = xmlContent.selectNodes("/" + getXmlTag() + "/" + DomeModelInterface.VIEWS + "/" + DomeModelInterface.VIEW);
        for (Iterator i = viewList.iterator(); i.hasNext();) {
            Element viewElement = (Element) i.next();
            String viewName = viewElement.attributeValue(DomeModelInterface.NAME);
            if (viewName.equals(DomeModelInterface.BUILD_VIEW)) {
                BuildContextElement = (Element) viewElement.elements().get(0);
            }
        }
        if (BuildContextElement != null) {
            cxt = (Context) getModelObjectFactory().newInstance(BuildContextElement, new Object[]{this, BuildContextElement});
            if (cxt != null) {
                //populate build context to preserve order
                buildViewContext = cxt;
                modelObjects.add(buildViewContext); //add the build context with right order of modelobjects
            }
        }

        // read mappings, if any
        Element mappings = null;
        if (xmlMappings == null) { // try to find in content file - old way
            mappings = (Element) xmlContent.selectSingleNode("/" + getXmlTag() + "/mappings/interfaceMappings");
        } else { // try to find in mappings file - new way
            mappings = (Element) xmlMappings.selectSingleNode("/" + getXmlTag() + "/mappingsandintermediates/mappings/interfacemappings");
            //populate list of intermediate vars held in tempModelObjects first so that addMappings doesn't fail
            if (isdefaultIface) {
                Element tempElement = (Element) xmlMappings.selectSingleNode("/" + getXmlTag() + "/mappingsandintermediates/intermediateobjects");
                List templist = tempElement.elements();
                for (Iterator j = templist.iterator(); j.hasNext();) {
                    Element telement = (Element) j.next();
                    Parameter tempParam = (Parameter) getModelObjectFactory().newInstance(telement, new Object[]{this, telement});
                    tempModelObjects.add(tempParam);
                    tempModelObjectsMap.put(tempParam.getId(), tempParam);
                }
            }
        }
        if (mappings != null) {
            if (m instanceof DomeModel) {
                ((DomeModel) m).getMappingManager().addMappings(this, mappings);
            } else if (m instanceof IntegrationProject) {
                ((IntegrationProject) m).getMappingManager().addMappings(this, mappings);
            }
        }
    }

    public Set getDistinctModelObjects() {
        return distinctModelObjs;
    }

    public void setCurrentView(String view) {
        this.currentView = view;
    }

    public String getCurrentView() {
        return currentView;
    }

    public boolean isInputFilter(Object obj) {
        return inputFilter.equals(obj);
    }

    // MultiViewSupport interface
    public List getViewNames() {
        return viewNames;
    }

    public List getView(String viewName) {
        if (viewNames.contains(viewName)) {
            List view = (List) views.get(viewName);
            return (view == null) ? Collections.EMPTY_LIST : view;
        }
        return Collections.EMPTY_LIST;
    }

    protected void createViews() {
        // create interface causality view
        List ifaceCausalityView = new ArrayList();
        ifaceCausalityView.add(inputFilter);
        ifaceCausalityView.add(outputFilter);
        ifaceCausalityView.add(indeterFilter);
        views.put(INTERFACE_CAUSALITY_VIEW, Collections.unmodifiableList(ifaceCausalityView));
        // create system causality view
        List sysCausalityView = new ArrayList();
        sysCausalityView.add(independentFilter);
        sysCausalityView.add(intermediateFilter);
        sysCausalityView.add(resultFilter);
        views.put(SYSTEM_CAUSALITY_VIEW, Collections.unmodifiableList(sysCausalityView));
    }

    public void addViewListener(String viewName, DListListener l) {
        // do nothing, views do not change
    }

    public void removeViewListener(String viewName, DListListener l) {
        // do nothing, views do not change
    }

    // ViewSupport interface
    //needed for tree view
    /*
     public List getView() {
        //This case happens when interface is initially loaded up
        if(!isviewchangeOperation && !isviewListLoaded) {
            List temp = getView(DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
            if(!viewList.isEmpty()) {
                viewList.clear();
            }
            isviewListLoaded = true;  //must be done before addAll call
            viewList.addAll(temp);
            //Here we load the data but do not yet add to the view
            //since we know that getView method will be called again
            //and that will cause duplication of data
            return Collections.EMPTY_LIST;
        }
        if (!isviewchangeOperation && isviewListLoaded) {
            //second call to getView during initial loading
            //comes here and gets the correct data
            isviewListLoaded = false;
            return viewList;
        }
        else if (isviewchangeOperation) {
            //When we switch between views,
            //setView (see below) method populates the
            //viewlist which is returned from here.
            isviewchangeOperation = false;
            return viewList;
        }
        return viewList;
}

public void addViewListener(DListListener l) {
        viewList.addDListListener(l);
}

public void removeViewListener(DListListener l) {
        viewList.removeDListListener(l);
}

    public void setView(String viewName)
    {
        List newViewItems = getView(viewName);
        if (newViewItems.isEmpty()) // invalid view
            return;
        viewList.clear();
        isviewchangeOperation = true;  //must be done before addAll call
        viewList.addAll(newViewItems);

    }
*/

    public List getView() {
        return getView(DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
    }

    public void addViewListener(DListListener l) {
        //do nothing
    }

    public void removeViewListener(DListListener l) {
        //do nothing
    }

    public Collection getModelObjectParameters() {
        Collection coll = null;
        if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
            coll = modelview.getModelObjectParameters();
        } else { //other 3 views
            coll = new ArrayList(modelObjects.size() - 1);
            for (Iterator i = modelObjects.iterator(); i.hasNext();) {
                ModelObject mobj = (ModelObject) i.next();
                if (mobj instanceof Parameter) {
                    coll.add(mobj);
                }
            }
        }
        return Collections.unmodifiableCollection(coll);
    }

	public Element toXmlElement() {
	    return toXmlElement(true);
    }

	public Element toXmlElement(boolean generateViews) {
        Element xmlElement = super.toXmlElement(generateViews);
        xmlElement.add(this.getInterfaceGraph().toXmlElement(this.getId().toString()));
        XMLUtils.addStringMap(xmlElement, "interfaceToRelParamMap", "iToRpMap", "relParamId", "ifaceId", getInterfaceParamToModelParamMap());
        return xmlElement;
    }

    public List getValidModelObjectTypes() {
        List types = Registry.getDataObjectTypes();
        types.add("parameter");
        types.add(Registry.getRelationTypes());
        return types;
    }

    public ModelObject getTempModelObjectById(Id id) {
        return (ModelObject) tempModelObjectsMap.get(id);
    }

	private Boolean modelGraphChanged = Boolean.TRUE; // todo: listen to model changes; get model to fire graph changes

	public void setModelGraphChanged(Boolean modelGraphChanged)
	{
		synchronized(modelGraphChanged) {
			this.modelGraphChanged = modelGraphChanged;
		}
	}

    public DirectedGraph getInterfaceGraph()
    {
	    if (interfaceGraph == null || (DomeClientApplication.getMode() == Modes.BUILD_MODE) ||
	            modelGraphChanged.booleanValue() || DomeClientApplication.DOME_API_BUILD_MODE)
		    calculateInterfaceGraph();      //if build mode, update

	    //for run mode, won't change
	    return interfaceGraph; // not true; may change after external graphs received
    }

	protected void calculateInterfaceGraph()
    {
	    synchronized (modelGraphChanged) {
		    boolean useProjectGraphId = (model instanceof IntegrationProjectServerRuntime);
		    Map origMpToIp = getModelParamToInterfaceParamMap();
		    HashMap mpToIp = new HashMap();
		    Iterator entries = origMpToIp.entrySet().iterator();
		    while (entries.hasNext()) {
			    Map.Entry entry = (Map.Entry) entries.next();
			    Object key = entry.getKey();
			    if (useProjectGraphId)
			        key = IntegrationProjectSolver.getProjectGraphId((ModelObject) key);
			    List value = (List) entry.getValue();
			    if (value.size() > 1)
				    mpToIp.put(key, new MultiItemNode(DomeModelBase.getModelParamId((Parameter) entry.getKey()), value));
			    else if (value.size() == 1)
				    mpToIp.put(key, value.get(0));
		    }

		    List modelParams = new ArrayList(mpToIp.keySet());
		    DirectedGraph modelSubGraph = null;
		    if (model instanceof DomeModelRuntime) {
			    modelSubGraph = ((DomeModelRuntime) model).getGraph().getSubgraphForVariables(modelParams);
		    }
		    else if (model instanceof PluginModelRuntime) {
			    modelSubGraph = ((PluginModelRuntime) model).getGraph().getSubgraphForVariables(modelParams);
		    }
		    else if (model instanceof PluginModelBuilder) {
			    modelSubGraph = ((PluginModelBuilder) model).createModelGraph().getSubgraphForVariables(modelParams);
		    }
		    else if (model instanceof DomeModelBuilder) {
			    modelSubGraph = ((DomeModelBuilder) model).createModelGraph().getSubgraphForVariables(modelParams);
		    }
		    else if (model instanceof IntegrationProjectBuilder) {
			    modelSubGraph = ((IntegrationProjectBuilder) model).getGraph().getSubgraphForVariables(modelParams);
		    }
		    else if (model instanceof IntegrationProjectServerRuntime) { // todo: convert model params to strings in graph
			    modelSubGraph = ((IntegrationProjectServerRuntime) model).getGraph().getSubgraphForVariables(modelParams);
		    }
		    else {
			    throw new UnsupportedOperationException("ModelInterfaceBase.calculateInterfaceGraph: interface graph " +
			                                            "for interfaces of model types " + ClassUtils.getClassName(model) +
			                                            " has not been implemented");
		    }

		    interfaceGraph = new DirectedGraph(modelSubGraph, mpToIp);
            //GLPanel.visualizeGraph("interfaceGraph", interfaceGraph);
		    // add interface parameters not mapped to anything
		    Map ipToMp = getInterfaceParamToModelParamMap();
		    List iParams = new ArrayList();
		    Iterator mObjs = getModelObjects().iterator();
		    Object mObj;
		    while (mObjs.hasNext()) {
			    mObj = mObjs.next();
			    if (mObj instanceof Parameter)
			        if (!ipToMp.containsKey(mObj))
				        iParams.add(mObj);
		    }
		    if (!iParams.isEmpty())
		        interfaceGraph.addNodes(iParams);
		    modelGraphChanged = Boolean.FALSE;
	    }
    }

	private HashMap iParamToModelParamMap = null; // key is ifaceParam; value is modelParam
	private AggregatorMap modelParamToIfaceParamMap = null; // key is modelParam; value is list of ifaceParams
	private Boolean ifaceParamMappingsChanged = Boolean.TRUE; // calculate map if true; set to true initially to calculate at least once

	protected ConnectionMappingManager getConnectionMappingManager() {
		Model m = getModel();
		ConnectionMappingManager mManager = null;
		if (m instanceof DomeModel) {
			DomeModel model = (DomeModel) m;
			mManager = model.getMappingManager();
		}
		else if (m instanceof IntegrationProject) {
			IntegrationProject project = (IntegrationProject) m;
			mManager = project.getMappingManager();
		}
		return mManager;
	}

	public String getModelParameterProjectGraphIdForInterfaceParameter(Parameter p) {
		Parameter mdlParam = getModelParameterForInterfaceParameter(p);
		if (mdlParam != null)
			return IntegrationProjectSolver.getProjectGraphId(mdlParam);
		return null;
	}
	public Parameter getModelParameterForInterfaceParameter(Parameter p) {
		return (Parameter)getInterfaceParamToModelParamMap().get(p);
	}

	public Parameter getInterfaceParameterForModelParameter(Parameter p) {
		return (Parameter)getModelParamToInterfaceParamMap().get(p);
	}

	public Map getInterfaceParamToModelParamMap()
	{
		return getInterfaceParamToModelParamMap(false);
	}

    public Map getInterfaceParamToModelParamMap(boolean forceNew) {
        if (!forceNew && iParamToModelParamMap != null)
            return iParamToModelParamMap;
        ConnectionMappingManager cmm = getConnectionMappingManager();
        // create hash map between interface parameter and corresponding model parameter
        iParamToModelParamMap = new HashMap();
        Iterator iObjs = this.getModelObjects().iterator();
        while (iObjs.hasNext()) {
            Object iObj = iObjs.next();
            if (iObj instanceof Parameter) {
                try {
                    Parameter mObj = getModelParameterForInterfaceParameter((Parameter) iObj, cmm);
                    if (mObj != null) {
                        iParamToModelParamMap.put(iObj, mObj);
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
        if (DomeClientApplication.getMode() == Modes.BUILD_MODE || DomeClientApplication.DOME_API_BUILD_MODE) {
            cmm.addMappingChangeListener(new InterfaceMappingListener()); // update map when mappings change
        }
        return Collections.unmodifiableMap(iParamToModelParamMap);
    }


	public Map getModelParamToInterfaceParamMap()
	{
		synchronized (ifaceParamMappingsChanged) {
			if (!ifaceParamMappingsChanged.booleanValue())
				return modelParamToIfaceParamMap;
			modelParamToIfaceParamMap = new AggregatorMap();
			Iterator ifaceMappings = getInterfaceParamToModelParamMap(true).entrySet().iterator();
			Map.Entry entry;
			while (ifaceMappings.hasNext()) {
				entry = (Map.Entry) ifaceMappings.next();
				modelParamToIfaceParamMap.put(entry.getValue(),entry.getKey());
			}
			ifaceParamMappingsChanged = Boolean.FALSE;
			return Collections.unmodifiableMap(modelParamToIfaceParamMap);
		}
	}

	class InterfaceMappingListener implements MappingChangeListener
	{
		public void mappingChanged(MappingChangeEvent event)
		{
			synchronized (ifaceParamMappingsChanged) {
				if (ifaceParamMappingsChanged.booleanValue())
					return; // map has not been created yet
				Parameter p = event.getParameter();
				if (p != null) {
					if (p.getScope() == ModelInterfaceBase.this) { // update the map
						Parameter mObj = getModelParameterForInterfaceParameter(p, getConnectionMappingManager());
						if (mObj == null)
							iParamToModelParamMap.remove(p);
						else
							iParamToModelParamMap.put(p, mObj); // replaces any existing value
						ifaceParamMappingsChanged = Boolean.TRUE;
					}
				}
			}
		}
	}

	public static Parameter getModelParameterForInterfaceParameter(Parameter ifaceParam, ConnectionMappingManager cmm)
	{
		Collection mappedParams = cmm.getMappingsForParameter(ifaceParam);
		if (mappedParams == null || mappedParams.size() == 0) {
			return null; // iface param not mapped to model param
		}
		else if (mappedParams.size() > 1) {
			throw new IllegalArgumentException("ModelInterfaceBase.getModelParameterForInterfaceParameter: " +
			                                   "found multiple mappings for interface parameter " +
			                                   ifaceParam.getName());
		}
		return (Parameter) mappedParams.toArray()[0]; // todo: verify this works for subscription parameters
	}

}