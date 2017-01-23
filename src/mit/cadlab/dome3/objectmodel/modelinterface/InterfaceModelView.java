package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.DomeCollectionUtils;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.mapping.ParameterMapping;
import mit.cadlab.dome3.objectmodel.util.mapping.VisualizationMapping;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomeException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Data model for model view of an interface.
 */
public class InterfaceModelView extends AbstractModelObjectScope
{
    protected ModelInterface mi;
    protected Context modelViewContext;

	//for xml generation
	protected Element paramElement;
	protected Element relElement;
	protected Element cxtElement;
	protected Element visElement;
	//to keep track of listeners in a non-default interface
	protected HashMap modelViewListenerMap = new HashMap();

	//to keep track of all the contexts and DomeLists in the model view
	protected HashMap xmlElementMap = new HashMap();
	protected HashMap modelViewObjectMap = new HashMap();

    public InterfaceModelView(ModelInterface mi, Id id) {
	    super(id);
	    this.mi = mi;
	    modelViewContext = createModelViewContext();
	    modelObjects.add(modelViewContext);
    }

	public InterfaceModelView(ModelInterface mi, Id id, ModelObjectScope mObjScope) {
		super(id, mObjScope, false);
		this.mi = mi;
		if (mObjScope instanceof InterfaceModelView) {
			boolean copyMappings = false;
			if(mObjScope.getModel() != null)
				mObjScope.getModel().equals(mi.getModel());
			copyModelObjects((InterfaceModelView) mObjScope, copyMappings);
		}
	}

	public InterfaceModelView(ModelInterface mi, Element xmlElement, Element xmlMappings) {
		super(xmlElement);
		this.mi = mi;
		List flatListofIds = getModelViewContentIds(xmlElement);
        List tobeRemoved = new ArrayList();

		// read parameters
		Parameter param;
		Element element;
		List params = xmlElement.selectNodes("/" + getXmlTag() + "/parameters/" + Parameter.XML_TAG);
		for (Iterator iter = params.iterator(); iter.hasNext();) {
			param = null;
			element = (Element) iter.next();
			String idstr = element.attributeValue("id");
			if(flatListofIds.contains(idstr)) {
				param = (Parameter) getModelObjectFactory().newInstance(element, new Object[]{this, element});
			}
			if (param != null) {
				modelObjects.add(param);
				tobeRemoved.add(element);
			}
		}
        params.removeAll(tobeRemoved);
		tobeRemoved.clear();
		// add list parameter references
		for(Iterator i = modelObjects.iterator(); i.hasNext(); ) {
			Parameter p = (Parameter)i.next();
			DataObject dobj = p.getDataObjectForType("List");
			if(dobj != null) {
				((DomeListData)dobj).loadXmlData();
			}
		}

        //read visulizations
		Visualization vis;
		List visualizations = xmlElement.selectNodes("/" + getXmlTag() + "/visualizations/" + Visualization.XML_TAG);
		for (Iterator iter = visualizations.iterator(); iter.hasNext();) {
			vis = null;
			element = (Element) iter.next();
			String idstr = element.attributeValue("id");
			if(flatListofIds.contains(idstr)) {
				vis = (Visualization) getModelObjectFactory().newInstance(element, new Object[]{this, element});
			}
			if (vis != null) {

				modelObjects.add(vis);
				tobeRemoved.add(element);
			}
		}
        visualizations.removeAll(tobeRemoved);
		tobeRemoved.clear();

		// read relations
		Relation rel;
		List relations = xmlElement.selectNodes("/" + getXmlTag() + "/relations/" + Relation.XML_TAG);
		for (Iterator iter = relations.iterator(); iter.hasNext();) {
			rel = null;
			element = (Element) iter.next();
			String idstr = element.attributeValue("id");
			if(flatListofIds.contains(idstr)) {
				rel = (Relation) getModelObjectFactory().newInstance(element, new Object[]{this, element});
			}
			if (rel != null) {
				modelObjects.add(rel);
				Collection relContent = rel.getModelObjects();
				for(Iterator r = relContent.iterator(); r.hasNext(); ) {
					Parameter p = (Parameter)r.next();
					flatListofIds.add(p.getId().getIdString());
				}
				tobeRemoved.add(element);
			}
		}
		relations.removeAll(tobeRemoved);
		tobeRemoved.clear();
		// add list parameter references
		Parameter parameter;
		for(Iterator i = modelObjects.iterator(); i.hasNext(); ) {
			Object object = i.next();
			if(object instanceof Relation) {
				rel = (Relation) object;
				Collection relParams = rel.getModelObjects();
				for (Iterator iterator = relParams.iterator(); iterator.hasNext();) {
					parameter = (Parameter) iterator.next();
					Object obj = parameter.getDataObjectForType("List");
					if (obj != null) {
						((DomeListData) obj).loadXmlData();
					}
				}
			}
		}

		// read contexts
		Element modelViewConElement = null;
		Context cxt;
		List contexts = xmlElement.selectNodes("/" + getXmlTag() + "/contexts/" + Context.XML_TAG);
		for (ListIterator iter = contexts.listIterator(contexts.size()); iter.hasPrevious();) {    //so that nested contexts are loaded properly
			// construct the next context
			cxt = null;
			element = (Element) iter.previous();
			String cxtId = element.attributeValue("id");
			//interface contexts element doesn't have the build context
			if(cxtId.equals(DomeModelInterface.MODEL_CONTEXT_ID.getIdString())) {
				modelViewConElement = element;
				tobeRemoved.add(element);
			}
			else {
				if(flatListofIds.contains(cxtId)) {
					cxt = (Context) getModelObjectFactory().newInstance(element, new Object[]{this, element});
					tobeRemoved.add(element);
				}
			}

			if (cxt != null) {
				modelObjects.add(0, cxt);      //so that nested contexts are loaded properly
			}
		}
        contexts.removeAll(tobeRemoved);

	    modelViewContext = (Context)getModelObjectFactory().newInstance(modelViewConElement,
	                                                           new Object[]{this, modelViewConElement});
		modelObjects.add(modelViewContext);

		// read mappings, if any
		Element mappings = null;
		Element modelViewMappings = DocumentHelper.createElement("modelviewmappings");
		if (xmlMappings == null) { // try to find in content file - old way
			mappings = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/mappings/interfaceMappings");
		}
		else { // try to find in mappings file - new way
			mappings = (Element) xmlMappings.selectSingleNode("/" + getXmlTag() + "/mappingsandintermediates/mappings/interfacemappings");
		}
		if (mappings != null) {
			//remove non - model view objects
            for(Iterator i = mappings.elements().iterator(); i.hasNext(); ) {
	            Element ele = (Element)i.next();
	            String id = ele.attributeValue("idRef");
	            if(flatListofIds.contains(id)) {
		            mappings.remove(ele);
		            modelViewMappings.add(ele);
	            }
            }
			Model m = getModel();
			if(m instanceof DomeModel) {
				((DomeModel)m).getMappingManager().addMappings(this, modelViewMappings);
			}
			else if(m instanceof IntegrationProject) {
				((IntegrationProject)m).getMappingManager().addMappings(this, modelViewMappings);
			}
		}
		addListeners();
	}


	protected Element getModelViewContextElement(Element xmlElement) {
		Element modelViewConElement = null;
		List cxtList = xmlElement.selectNodes("/" + ModelInterface.XML_TAG + "/contexts/" + Context.XML_TAG);
		for(Iterator i = cxtList.iterator(); i.hasNext(); ) {
			Element element = (Element)i.next();
			String id =  element.attributeValue("id");
			if(id.equals(DomeModelInterface.MODEL_CONTEXT_ID.getIdString())) {
				modelViewConElement = element;
			}
			else {
				xmlElementMap.put(id, element);
			}
		}
		//Also store DomeLists in the XML element map for future reference
		//TODO do not store lists in build and causal views in xmlElementMap
		List paramList = xmlElement.selectNodes("/" + getXmlTag() + "/parameters/" + Parameter.XML_TAG);
		for (Iterator iter = paramList.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Element type = element.element("currentType");
			if(type.attributeValue("value").equals("List")) {
				String id =  element.attributeValue("id");
				xmlElementMap.put(id, element);
			}
		}

		//Also store Visualization in the XML element map for future reference
		List visList = xmlElement.selectNodes("/" + getXmlTag() + "/visualizations/" + Visualization.XML_TAG);
		for (Iterator iter = visList.iterator(); iter.hasNext();) {
			    Element element = (Element) iter.next();
				String id =  element.attributeValue("id");
				xmlElementMap.put(id, element);
		}

		if(modelViewConElement == null) {
			throw new DomeException("InterfaceModelView getModelViewContextElement: null model view element");
		}
		return modelViewConElement;
	}

	protected List getModelViewContentIds(Element xmlElement) {
		List ids = new ArrayList();
        Element modelViewElement = getModelViewContextElement(xmlElement);
		Element modelObjectsElement  = modelViewElement.element("modelobjects");
		if(modelObjectsElement == null)
			return ids; //this interface's model view is empty
		List contents = modelObjectsElement.elements();
        for(Iterator j = contents.iterator(); j.hasNext(); ) {
	        Element e = (Element)j.next();
	        String idstr = e.attributeValue("idRef");
            ids.add(idstr);
	        //if this is a context, add it contents to ids list
	        if(e.getName().equals(Context.XML_TAG)) {
		        addContextContentIds(idstr, ids);
	        }
	        //if this is a DomeList or Visualization, add it contents to ids list
	        else if(xmlElementMap.containsKey(idstr)) {
			        addListContentIds(idstr, ids);
	        }
        }
		return ids;
	}

	private void addContextContentIds(String cxtIdString, List ids) {
		Element element = (Element)xmlElementMap.get(cxtIdString);
		Element modelObjectsElement  = element.element("modelobjects");
		//process non-empty context
		if(modelObjectsElement != null)  {
			for(Iterator j = modelObjectsElement.elements().iterator(); j.hasNext(); ) {
				Element e = (Element)j.next();
				String idstr = e.attributeValue("idRef");
				ids.add(idstr);
				//if this is a context, add it contents to ids list
				if(e.getName().equals(Context.XML_TAG)) {
					addContextContentIds(idstr, ids);
				}
			}
		}
	}

	private void addListContentIds(String id, List ids)
	{
		Element element = (Element) xmlElementMap.get(id);
		if (!element.getQName().getName().equals(Visualization.XML_TAG)) {
			Element dnode = element.element("data").element("dataobject");
			Element paramsElement = dnode.element("parameters");
			//process non-empty DomeList
			if (paramsElement != null) {
				for (Iterator j = paramsElement.elements().iterator(); j.hasNext();) {
					Element e = (Element) j.next();
					String idstr = e.attributeValue("idRef");
					ids.add(idstr);
					//if this is a DomeList, add it contents to ids list
					if (xmlElementMap.containsKey(idstr)) {
						addListContentIds(idstr, ids);
					}
				}
			}

		} else {
			//process non-empty Visualization
			Element visElement = element.element("AvailableList");
			if (visElement != null) {
				for (Iterator j = visElement.elements().iterator(); j.hasNext();) {
					Element e = (Element) j.next();
					String idstr = e.attributeValue("idRef");
					ids.add(idstr);
					//if this is a DomeList, add it contents to ids list
					if (xmlElementMap.containsKey(idstr)) {
						addListContentIds(idstr, ids);
					}
				}
			}
		}
	}

//from AbstractModelObjectScope
	public List getValidModelObjectTypes() {
		List types = Registry.getDataObjectTypes();
		types.add("parameter");
		types.add("visualization");
		types.add(Registry.getRelationTypes());
		return types;
	}

	public String contentToString() {
		return "\n  model View Objects: " + Names.getNameIds(modelObjects);
	}

	//To take care of parameters in DomeCollection such as DomeList
	protected void objectDeletedAction(ModelObject mObj) {
		if(DomeCollectionUtils.isCollectionParameter(mObj)) {
			//mappings are removed in ModelInterfaceBuilder object
			removeListeners(Collections.singletonList(mObj));
		}
		super.objectDeletedAction(mObj);
	}

	public ModelObject newModelObject(ModelObject obj) {
		if(obj instanceof Context) {
			return super.newModelObject(obj, true);
		}
		else {
			ModelObject mobj = super.newModelObject(obj);
			mapAndAddListenersToCollectionParameter(obj, mobj);
			return mobj;
		}
	}

	public Collection newModelObjects(Collection objs) {
		Collection ifaceObjs = super.newModelObjects(objs);
		Iterator j = objs.iterator();
		for(Iterator i = objs.iterator(); i.hasNext(); ) {
			ModelObject obj = (ModelObject)j.next();
			ModelObject ifaceObj = (ModelObject)i.next();
            mapAndAddListenersToCollectionParameter(obj, ifaceObj);
		}
		return ifaceObjs;
	}

	private void mapAndAddListenersToCollectionParameter(ModelObject obj, ModelObject ifaceObj) {
		if(DomeCollectionUtils.isCollectionParameter(obj))
		{  //if this param in inside DomeCollection such as DomeList
			mapAndAddListeners(Collections.singletonList(obj),
			                   Collections.singletonList(ifaceObj), true); //then add listeners
		}
	}

	public ModelObjectFactory getModelObjectFactory() {
		return mi.getModelObjectFactory();
	}

	public Model getModel() {
    	return mi.getModel();
	}

	public String getXmlTag() {
        return ModelInterface.XML_TAG;
	}

	public TypeInfo getTypeInfo() {
       return DomeModelInterface.TYPE_INFO;
	}

	protected void addXmlContent(Element xmlElement) {

	}

	public void initXmlSetup(Element paramElement, Element relElement,
	                            Element cxtElement, Element visElement) {
		this.paramElement = paramElement;
		this.relElement = relElement;
		this.cxtElement = cxtElement;
		this.visElement = visElement;

	}

	public Element toXmlElement()
	{
		Element modelContextElement = null;

		// add parameters, relations and contexts
		for (Iterator iter = modelObjects.listIterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof Parameter) {
				Element param = ((Parameter) obj).toXmlElement();
				paramElement.add(param);
			} else if (obj instanceof Relation) {
				Element rel = ((Relation) obj).toXmlElement();
				relElement.add(rel);
			} else if (obj instanceof Visualization) {
				Element vis = ((ConcreteVisualization) obj).toXmlElement();
				visElement.add(vis);
			} else if (obj instanceof Context) {
				Element cxt = ((Context)obj).toXmlElement();
				if (((Context)obj).getId().equals(DomeModelInterface.MODEL_CONTEXT_ID)) {
					modelContextElement = cxt;
				}
				else {
					cxtElement.add(cxt);
				}
			}
		}
		if (modelContextElement != null) {
			cxtElement.add(modelContextElement);
		}
		return modelContextElement;
	}

	public boolean isDefaultInterface() {
		return ((DomeModelInterface)mi).isDefaultInterface();
	}

	protected Collection getModelObjectParameters() {
		Collection coll = new ArrayList(modelObjects.size() - 1);
		for (Iterator i = modelObjects.iterator(); i.hasNext();) {
			ModelObject mobj = (ModelObject) i.next();
			if (mobj instanceof Parameter) {
				coll.add(mobj);
			}
		}
		return coll;
	}

	protected void addDListListener(DListListener l) {
        modelObjects.addDListListener(l);
	}

	protected void removeMappingsAndConnections(ConnectionMappingManager mgr) {
		for (Iterator j = modelObjects.iterator(); j.hasNext();) {
			Object obj = j.next();
			if (obj instanceof Parameter) {
				//this will remove mappings and interface connections
				mgr.removeAllMappings((Parameter) obj);
			} else if (obj instanceof ProceduralRelation) {
				for (Iterator r = ((ProceduralRelation) obj).getModelObjects().iterator();
				     r.hasNext();) {
					Parameter p = (Parameter) r.next();
					mgr.removeAllMappings(p);
				}
			}
		}
	}

	protected Context createModelViewContext()
	{
		Object[] ctrParams = new Object[]{this, DomeModelInterface.MODEL_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("InterfaceModelView: createModelViewContext failed");
		cxt.setName(DomeModelInterface.MODEL_VIEW);
		return cxt;
	}

	public Context getModelViewContext() {
		return modelViewContext;
	}

	//adds object to modelViewContext
	protected void addReference(ModelObject mdelobject)
	{
		modelViewContext.addModelObjectReference(mdelobject);
	}

	//adds object to modelObjects list and modelViewContext
	protected ModelObject addObjectAndReference(ModelObject mdelobject,
	                                        boolean isInsideContext)
	{
		ModelObject mobj = newModelObject(mdelobject);
		if(!isInsideContext) {
			modelViewContext.addModelObjectReference(mobj);
		}
		return mobj;
	}

	/** To add or paste references of existing objects
	 *  obj - existing object in model view
	 * */
	protected void pasteRefrence(Context con, ModelObject obj) {
			con.addModelObjectReference(obj);
	}

    protected void removeListeners(Collection modelObjects) {
	    for (Iterator i = modelObjects.iterator(); i.hasNext();) {
		    ModelObject newobj = (ModelObject) i.next();
		    ModelObject modobj =  null;
		    Object[] objs = (Object[]) modelViewListenerMap.get(newobj);
		    if(objs != null)	{
			    modobj = (ModelObject) objs[0];
			    NameListener listener = (NameListener) objs[1];
			    modobj.removePropertyChangeListener(NameListener.NAME, listener);
		    }
		    if (newobj instanceof Relation && objs != null) {
			    NondefaultRelationObjectsListener rl = (NondefaultRelationObjectsListener) objs[2];
			    ModelInterfaceBuilder.RelationPropertyListener cl = (ModelInterfaceBuilder.RelationPropertyListener) objs[3];
			    //remove name listeners on all parameters if they exist
			    Collection relobjs = ((Relation) newobj).getModelObjects();
			    for (Iterator r = relobjs.iterator(); r.hasNext();) {
				    ModelObject relnewobj = (ModelObject) r.next();
				    Object[] listenerobjs = (Object[]) modelViewListenerMap.get(relnewobj);
				    if(listenerobjs != null) {
						ModelObject relmodobj = (ModelObject) listenerobjs[0];
						NameListener rellistener = (NameListener) listenerobjs[1];
						if ((relmodobj != null) || (rellistener != null)) {
							relmodobj.removePropertyChangeListener(NameListener.NAME, rellistener);
						}
						modelViewListenerMap.remove(relnewobj);
				    }
			    }
			    ((Relation) modobj).removeModelObjectsListener(rl);
			    modobj.removePropertyChangeListener(cl);
		    }

		    else if (newobj instanceof Context) {
			    removeListeners(((Context)newobj).getModelObjectReferences());
		    }
		    modelViewListenerMap.remove(newobj);
	    }
    }

	protected void removeObjectsAndListeners(Collection modelObjects)
	{
		removeListeners(modelObjects);
		removeObjectsAndReferences(modelObjects);
	}

	private void removeObjectsAndReferences(Collection mObjects) {
		removeObjects(mObjects);
		removeReferences(mObjects);
	}

	private void removeObjects(Collection mObjects) {
		// todo: check if deleting all model object is fine
		// sangmok : memory leakage debugging
        try {
            for (Iterator i = mObjects.iterator(); i.hasNext(); ) {
                ModelObject mObject = (ModelObject) i.next();
                mObject.delete(null);
            }
        } catch (Exception ea) {
            ea.printStackTrace();
        }
        // sangmok edit ends

        modelObjects.removeAll(mObjects);
	}

	private void removeReferences(Collection mObjects) {
		for (Iterator i = mObjects.iterator(); i.hasNext();) {
			ModelObject mObject = (ModelObject) i.next();
			try {
				modelViewContext.removeModelObjectReference(mObject); //removes all parameters
			} catch (Exception ex) {
			}
		}
		Collection conObjs = modelViewContext.getModelObjectReferences();
		if (conObjs == null || conObjs.size() == 0) {
			return;
		}
		Collection objects = new ArrayList(mObjects);   //to avoid iterator exceptions
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			ModelObject mobj = (ModelObject) iter.next();
			for (Iterator i = conObjs.iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof Relation) {
					((Relation) obj).deleteModelObject(mobj);
					continue;
				}
				if (obj instanceof Context) {
					removeObjectFromContext((Context) obj, mobj);
					continue;
				}
			}

		}
	}

	private void removeObjectFromContext(Context con, ModelObject mobj)
	{
		Collection conObjs = con.getModelObjectReferences();
		for (Iterator iter = conObjs.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof Relation) {
				((Relation) obj).deleteModelObject(mobj);
				break;
			}
			if (obj instanceof Context) {
				removeObjectFromContext((Context) obj, mobj);
				break;
			}
			if (obj instanceof Parameter && obj.equals(mobj)) {
				con.removeModelObjectReference((ModelObject) obj);
				break;
			}
		}
	}

	protected void addListeners() {
		// add listeners to objects in the model view
		Model mod = getModel();
		if(mod != null) {   //model is null during deploy mode
            ConnectionMappingManager mgr = null;
			if(mod instanceof DomeModel) {
				mgr = ((DomeModel)mod).getMappingManager();
            }
			else if(mod instanceof IntegrationProject) {
				mgr = ((IntegrationProject)mod).getMappingManager();
			}
			HashMap mappingMap = mgr.getMappings(this);
			for (Iterator mappingIter = mappingMap.keySet().iterator(); mappingIter.hasNext();) {
				Object obj = mappingIter.next();
				if (obj instanceof Parameter) {
					Parameter ifaceParameter = (Parameter) obj;
					ParameterMapping mapping = (ParameterMapping) mappingMap.get(ifaceParameter);
					Collection modelParams = mapping.getMappings();
					for (Iterator modelParamIter = modelParams.iterator(); modelParamIter.hasNext();) {
						Parameter modelParam = (Parameter) modelParamIter.next();
						addParameterListeners(modelParam, ifaceParameter);
					}
				} else if (obj instanceof Visualization) {
					Visualization ifaceParameter = (Visualization) obj;
					VisualizationMapping mapping = (VisualizationMapping) mappingMap.get(ifaceParameter);
					Collection modelParams = mapping.getMappings();
					for (Iterator modelParamIter = modelParams.iterator(); modelParamIter.hasNext();) {
						Visualization modelParam = (Visualization) modelParamIter.next();
						addVisualizationListeners(modelParam, ifaceParameter);
					}

				}

			}
		}
	}

	private void addParameterListeners(Parameter modelParam, Parameter ifaceParam)
	{
		if (modelObjects.contains(ifaceParam) || modelObjects.contains(ifaceParam.getScope())) {
			ModelObjectScope modelScope = modelParam.getScope();
			ModelObjectScope ifaceScope = ifaceParam.getScope();
			if (modelScope instanceof Relation && ifaceScope instanceof Relation) {
				addListeners(Collections.singletonList(modelScope),
				                        Collections.singletonList(ifaceScope));
			} else {
				addListeners(Collections.singletonList(modelParam),
				                        Collections.singletonList(ifaceParam));
			}
		}
	}

	private void addVisualizationListeners(Visualization modelParam, Visualization ifaceParam)
	{
		if (modelObjects.contains(ifaceParam) || modelObjects.contains(ifaceParam.getScope())) {
			ModelObjectScope modelScope = modelParam.getScope();
			ModelObjectScope ifaceScope = ifaceParam.getScope();
			if (modelScope instanceof Relation && ifaceScope instanceof Relation) {
				addListeners(Collections.singletonList(modelScope),
				                        Collections.singletonList(ifaceScope));
			} else {
				addListeners(Collections.singletonList(modelParam),
				                        Collections.singletonList(ifaceParam));
			}
		}
	}

	protected Collection addAndMapModelObjects(Collection origModelObjects)
	{
		Collection objs = newModelObjects(origModelObjects, true);
		mapAndAddListeners(origModelObjects, objs, false);
		return objs;
	}

	private void addListeners(Collection origModelObjects, Collection objs)
	{
		mapAndAddListeners(origModelObjects, objs, true);
	}

	protected void mapAndAddListeners(Collection origModelObjects, Collection objs, boolean listenersOnly)
	{
		Iterator j = origModelObjects.iterator();
		for (Iterator i = objs.iterator(); i.hasNext();) {
			ModelObject ifaceobj = (ModelObject) i.next();
			ModelObject modobj = (ModelObject) j.next();
			//to track name change in (model parameter)/relation
			NameListener listener = new ModelInterfaceBuilder.ModelObjectNameListener(ifaceobj);
			modobj.addPropertyChangeListener(NameListener.NAME, listener);

			if (ifaceobj instanceof Parameter) {
				Object[] objects = new Object[]{modobj, listener};
				modelViewListenerMap.put(ifaceobj, objects);
				//No causality listener are added on model view parameters as there are no causality filters
				//in this view and more importantly causality of these parameters does not affect anything else.
				if (!listenersOnly) {
					ConnectionMappingManager mgr = null;
					Model m = this.getModel();
					if(m instanceof DomeModel) {
						mgr = ((DomeModel)m).getMappingManager();
					}
					else if(m instanceof IntegrationProject) {
						mgr = ((IntegrationProject)m).getMappingManager();
					}
					mgr.addMapping((Parameter) ifaceobj, (Parameter) modobj);
				}
			} else 	if (ifaceobj instanceof Visualization) {
				Object[] objects = new Object[]{modobj, listener};
				modelViewListenerMap.put(ifaceobj, objects);
				//No causality listener are added on model view parameters as there are no causality filters
				//in this view and more importantly causality of these parameters does not affect anything else.
				if (!listenersOnly) {
					ConnectionMappingManager mgr = null;
					Model m = this.getModel();
					if(m instanceof DomeModel) {
						mgr = ((DomeModel)m).getMappingManager();
					}
					else if(m instanceof IntegrationProject) {
						mgr = ((IntegrationProject)m).getMappingManager();
					}
					mgr.addMapping((Visualization) ifaceobj, (Visualization) modobj);
				}
			}

			else if (ifaceobj instanceof Relation) {
				Model m = this.getModel();
				ConnectionMappingManager mgr = null;
				if(m instanceof DomeModel) {
					mgr = ((DomeModel) m).getMappingManager();
				}
				else if(m instanceof IntegrationProject) {
					mgr = ((IntegrationProject) m).getMappingManager();
				}
				NondefaultRelationObjectsListener rl = new NondefaultRelationObjectsListener((Relation) ifaceobj,
				                                                                             (Relation) modobj, mgr, !listenersOnly);
				((Relation) modobj).addModelObjectsListener(rl);
				ModelInterfaceBuilder.RelationPropertyListener cl = new ModelInterfaceBuilder.RelationPropertyListener((Relation) ifaceobj,
				                                                           rl.getInterfaceObjectMap());
				modobj.addPropertyChangeListener(cl);
				Object[] listeners = new Object[]{modobj, listener, rl, cl};
				modelViewListenerMap.put(ifaceobj, listeners);
			}
			else if(ifaceobj instanceof Context) {
				mapAndAddListeners(((Context)modobj).getModelObjectReferences(),
				                    ((Context)ifaceobj).getModelObjectReferences(), listenersOnly);
			}
		}
	}


	protected class NondefaultRelationObjectsListener implements DListListener
	{
		Relation ifaceRel;
		Relation modelRel;
		HashMap map;   //key - model object  value - interface object in model view
		HashMap nondefaultobjectmap;
//instead of local lookup, do object level lookup using ModelViewListenerMap
//		HashMap listenermap; //key - interface object value - name listener
		ConnectionMappingManager mgr;
		boolean mapParameters;

		/**
		 * Create a listener with a populated relation.
		 * @param ifacerel New interface relation
		 * @param modelRel Original relation
		 */
		protected NondefaultRelationObjectsListener(Relation ifacerel, Relation modelRel,
		                                            ConnectionMappingManager mgr,
		                                            boolean mapParameters)
		{
			this.ifaceRel = ifacerel;
			this.modelRel = modelRel;
			map = new HashMap();
			nondefaultobjectmap = new HashMap();
			this.mgr = mgr;
			this.mapParameters = mapParameters;

			// add the relation's objects
			Collection ifaceRelObjs = ifacerel.getModelObjects();
			Collection modRelObjs = modelRel.getModelObjects();
			Iterator miter = modRelObjs.iterator();
			for (Iterator iter = ifaceRelObjs.iterator(); iter.hasNext();) {
				Parameter modelObj = (Parameter) miter.next();
				Parameter ifaceObj = (Parameter) iter.next();
				addModelObject(modelObj, ifaceObj);
			}
			//after initial objects are copied, we always want to map parameters
			//when a new interval gets added
			this.mapParameters = true;
		}

		private void addModelObject(ModelObject modelObj, ModelObject ifaceObj)
		{
			NameListener namelistener = new ModelInterfaceBuilder.ModelObjectNameListener(ifaceObj);
			modelObj.addPropertyChangeListener(NameListener.NAME, namelistener);
			map.put(modelObj, ifaceObj);
			Object[] objects = new Object[2];
			objects[0] = modelObj;
			objects[1] = namelistener;
			modelViewListenerMap.put(ifaceObj, objects);
			if (mapParameters) {
				Collection c = new ArrayList();
				c.add(modelObj);
				mgr.addMappings((Parameter) ifaceObj, c);
			}
		}

		public void intervalAdded(DListEvent e)
		{
			List items = e.getItems();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				if (((ModelInterfaceBuilder)mi).getDistinctModelObjects().contains(obj)) {
					continue; //this is not a separate object but a reference
					//so ignore it
				} else {
					((ModelInterfaceBuilder)mi).getDistinctModelObjects().add(obj);
				}
				ModelObject newObject = ifaceRel.newModelObject(obj);
				addModelObject(obj, newObject);
			}
		}

		public void intervalRemoved(DListEvent e)
		{
			List items = e.getItems();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();

                if (mi instanceof ModelInterfaceBuilder) {
                    ((ModelInterfaceBuilder) mi).getDistinctModelObjects().remove(obj);
                } else if (mi instanceof AbstractModelInterfaceRuntime) {
                    ((AbstractModelInterfaceRuntime) mi).getDistinctModelObjects().remove(obj);
                }
				ModelObject newObject = (ModelObject) map.get(obj);
				Object[] objects = (Object[]) modelViewListenerMap.get(newObject);
				if(objects != null) {
					NameListener listener = (NameListener) objects[1];
					obj.removePropertyChangeListener(listener);
					modelViewListenerMap.remove(newObject);
//					ifaceRel.deleteModelObject(newObject);
					map.remove(obj);
					Collection c = mgr.getMappingsForParameter((Parameter) newObject);
					if (c != null || c.size() > 0) {
						mgr.removeMapping((Parameter) newObject, (Parameter) obj);
					}
				}
			}
		}

		public void intervalChanged(DListEvent e)
		{
			throw new UnsupportedOperationException();
		}

		public void itemsRemoved(DListEvent e)
		{
			intervalRemoved(e);
		}

		public void itemsReplaced(DListEvent e)
		{
			throw new UnsupportedOperationException();
		}

		public Map getInterfaceObjectMap()
		{
			return Collections.unmodifiableMap(map);
		}
	}

	public ModelInterface getContainerInterface() {
		return mi;
	}

	public HashMap getModelViewObjectMap()
	{
		return modelViewObjectMap;
	}

	protected CausalityManager getCausalityManager() {
		return mi.getCausalityManager();
	}

	protected void copyModelObjects(InterfaceModelView iface, boolean copyMappings)
	{
		modelViewContext = createModelViewContext();
		Model m = getModel();
		ConnectionMappingManager mmgr = null;
		if(m instanceof DomeModel) {
			mmgr = ((DomeModel) m).getMappingManager();
		}
		else if(m instanceof IntegrationProject) {
			mmgr = ((IntegrationProject) m).getMappingManager();
		}
		//Copy modelObjects
		Collection origModelObjects = iface.getModelObjects();
		ArrayList mObjs = new ArrayList(origModelObjects); // mutable list
		modelViewObjectMap = super.copyModelObjects(mObjs);

		for (Iterator i = mObjs.iterator(); i.hasNext();) {
			ModelObject mObj = (ModelObject) i.next();
			if (mObj instanceof Parameter) {
				if (copyMappings) {
					Collection mapping = mmgr.getMappingsForParameter((Parameter) mObj);
					Parameter newobj = (Parameter) modelViewObjectMap.get(mObj.getId());
					if(newobj != null) {
						mmgr.addMappings(newobj, mapping);
					}
				}
			}
			else if (mObj instanceof Relation) {
				if(copyMappings) {
					Relation oldRel = (Relation) mObj;
					Relation newRel = (Relation)modelViewObjectMap.get(mObj.getId());
					Collection oldRelObjs = oldRel.getModelObjects();
					//add mappings for all relation parameters
					for (Iterator iter = oldRelObjs.iterator(); iter.hasNext();) {
						Parameter p = (Parameter) iter.next();
						//Following line takes care of procedural as well as equals relation
						Parameter newp = (Parameter) ((AbstractProceduralRelation)newRel).getModelObjectMap().get(p.getId());
						Collection mapping = mmgr.getMappingsForParameter(p);
						if(newp != null) {
							mmgr.addMappings(newp, mapping);
						}
					}
				}
			}
		}
       	//model view
		Context oldModelViewContext = iface.getModelViewContext();
		for(Iterator i = oldModelViewContext.getModelObjectReferences().iterator(); i.hasNext(); ) {
			ModelObject mObj = (ModelObject)i.next();
			ModelObject obj = (ModelObject)modelViewObjectMap.get(mObj.getId());
			if(obj != null) {
				modelViewContext.addModelObjectReference(obj);
			}
		}
		modelObjects.add(modelViewContext);
	}
}