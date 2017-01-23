// DomeModelBuilder.java
package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectBaseFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.DomeCollectionUtils;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.util.causality.*;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.io.IOException;


public class ModelInterfaceBuilder extends ModelInterfaceBase
{
	ModelObjectsListener listener = null;
	//key - object in the model, value - interface object in model view (default interface)
	HashMap objectmap = new HashMap();
	//key -object in model view, value - object in causal views also referenced in the build view (in default interface)
	HashMap viewobjectmap = new HashMap();
	//key - object in the model, value - interface object in build & causal views (in default interface)
	HashMap relationobjectmap = new HashMap();
	//key - non-default interface parameter, value - causality change listener (in non-default interface)
	//key - default interface parameter inside DomeCollection, value - listener array(in default interface)
	HashMap sharedListenerMap = new HashMap();

	private boolean isSystemCausalityInitialized;
	private DArrayList systemCausalityList;

	public ModelInterfaceBuilder(Model m, Id id)
	{
		super(m, id);
		if (m instanceof IntegrationProject) {
			setName("Project Interface");
		}
	}

	public ModelInterfaceBuilder(Model m, String id, String name)
	{
		super(m, id, name);
		isdefaultIface = name.equals(DEFAULT_IFACE_TAG);
		if (isdefaultIface) {
			listener = new ModelObjectsListener((DomeModel) m);
			((DomeModel) m).getBuildContext().addModelObjectReferencesListener(listener);
		}
	}

	public ModelInterfaceBuilder(Model m, Id id, ModelObjectScope mObjScope)
	{
		this(m, id, mObjScope, true);
	}

	public ModelInterfaceBuilder(Model m, Id id, ModelObjectScope mObjScope, boolean changeName)
	{
		super(m, id, mObjScope);
		String name = this.getName();
		if (changeName)
			name = "Copy of " + name;
		this.setName(name);
		modelview.addListeners();
	}


	public ModelInterfaceBuilder(Model m, Element xmlContent)
	{
		this(m, xmlContent, null);
	}

	public ModelInterfaceBuilder(Model m, Element xmlContent, Element xmlMappings)
	{
		super(m, xmlContent, xmlMappings);
		createViews();
		if (isDefaultInterface())
			loadDefaultInterfaceInfo(xmlMappings);

		//lastSavedXml = createXmlDocumentWithoutViews().asXML();

		//for parameters inside DomeList
		for (Iterator i = modelObjects.iterator(); i.hasNext();) {
			ModelObject obj = (ModelObject) i.next();
			ConnectionMappingManager mgr = null;
			if (model instanceof DomeModel) {
				mgr = ((DomeModel) model).getMappingManager();
			}
			else if (model instanceof IntegrationProject) {
				mgr = ((IntegrationProject) model).getMappingManager();
			}
			if (DomeCollectionUtils.isCollectionParameter(obj)) {
				Collection mappings = mgr.getMappingsForParameter((Parameter) obj);
				//since interface param is mapped to only to a model param
				if(mappings.size() > 0) {
					addParameterListeners((Parameter) (mappings.iterator().next()),
				                          (Parameter) obj);
				}
			}
		}

		if (isdefaultIface) {
			//populate distinct model objects set - for references
			Object buildCxt = ((DomeModel) m).getBuildContext();
			Collection objs = m.getModelObjects();
			for (Iterator i = objs.iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj.equals(buildCxt))
					continue;
				if (!distinctModelObjs.contains(obj)) {
					distinctModelObjs.add(obj);
				}
			}
		}
	}

	public void addDModelViewDListListener(DListListener l)
	{
		modelview.addDListListener(l);
	}

	//for default interface
	private void loadDefaultInterfaceInfo(Element lookupElement)
	{
		if (lookupElement == null)
			return;
		Element objectmapElement = (Element) lookupElement.selectSingleNode("/" + getXmlTag() + "/lookupdata/objectmap");
		if (objectmapElement == null)
			return;
		Model m = getModel();
		List pairs = objectmapElement.elements();
		for (Iterator p = pairs.iterator(); p.hasNext();) {
			Element key = (Element) p.next();
			Id modid = parseXmlRef(key);
			ModelObject modobj = m.getModelObjectById(modid);
			if (modobj != null) { //object belongs directly to the model
				Element value = (Element) p.next();
				Id faceid = parseXmlRef(value);
				ModelObject faceobj = this.getModelViewObjectById(faceid);
				objectmap.put(modobj, faceobj);
			}
			else { //see if obj belongs to a relation in the model
				String relref = key.attributeValue("idRelationRef");
				if (relref != null) {
					Id relId = new Id(relref);
					Relation rel = (Relation) m.getModelObjectById(relId);
					modobj = rel.getModelObjectById(modid);
					Element value = (Element) p.next();
					Id faceid = parseXmlRef(value);
					ModelObject faceobj = this.getModelViewObjectById(faceid);
					objectmap.put(modobj, faceobj);
				}
			}
		}
		Element viewmapElement = (Element) lookupElement.selectSingleNode("/" + getXmlTag() + "/lookupdata/viewobjectmap");
		List viewpairs = viewmapElement.elements();
		for (Iterator vp = viewpairs.iterator(); vp.hasNext();) {
			Element key = (Element) vp.next();
			Id modviewid = parseXmlRef(key);
			ModelObject modviewobj = this.getModelViewObjectById(modviewid);
			Element value = (Element) vp.next();
			Id faceid = parseXmlRef(value);
			ModelObject faceobj = this.getModelObjectById(faceid);
			if(faceobj == null)
				faceobj = this.getTempModelObjectById(faceid);
			viewobjectmap.put(modviewobj, faceobj);
		}
		Element relationobjectElement = (Element) lookupElement.selectSingleNode("/" + getXmlTag() + "/lookupdata/relationobjectmap");
		List relationobjectpairs = relationobjectElement.elements();
		for (Iterator rp = relationobjectpairs.iterator(); rp.hasNext();) {
			Element key = (Element) rp.next();
			Id modid = parseXmlRef(key);
			Id relationId = new Id(key.attributeValue("idRelationRef"));
			Relation rel = (Relation) m.getModelObjectById(relationId);
			ModelObject modobj = rel.getModelObjectById(modid);
			Element value = (Element) rp.next();
			Id faceid = parseXmlRef(value);
			ModelObject faceobj = this.getModelObjectById(faceid);
			//TODO fix - iface faceobj is null after two attempts this will hide the problem
			if (faceobj != null) { //found in modelObjects list
				relationobjectmap.put(modobj, faceobj);
			}
			else {   //try in tempModelObjects list
				faceobj = this.getTempModelObjectById(faceid);
				if (faceobj != null) {
					relationobjectmap.put(modobj, faceobj);
				}
			}
		}
		listener = new ModelObjectsListener((DomeModel) m);
		listener.addListenersToExistingObjects();
		((DomeModel) m).getBuildContext().addModelObjectReferencesListener(listener);
	}

	//returns null for non-default interface
	public Element createDefaultLookupElement()
	{
		if (isdefaultIface) {
			Element topXmlElement = DocumentHelper.createElement("lookupdata");
			Element objectElement = DocumentHelper.createElement("objectmap");
			// store objectmap objects
			Set objectKeySet = objectmap.keySet();
			for (Iterator keys = objectKeySet.iterator(); keys.hasNext();) {
				ModelObject modelObj = (ModelObject) keys.next();
				ModelObject ifaceObj = (ModelObject) objectmap.get(modelObj);
				if (ifaceObj != null) {
					Element keyElement = modelObj.toXmlRef();
					keyElement.addAttribute("description", "key");
					objectElement.add(keyElement);
					Element valueElement = ifaceObj.toXmlRef();
					valueElement.addAttribute("description", "value");
					objectElement.add(valueElement);
				}
			}
			Element viewobjectElement = DocumentHelper.createElement("viewobjectmap");
			// store view objectmap objects
			objectKeySet = viewobjectmap.keySet();
			for (Iterator keys = objectKeySet.iterator(); keys.hasNext();) {
				ModelObject modelViewObj = (ModelObject) keys.next();
				ModelObject buildViewObj = (ModelObject) viewobjectmap.get(modelViewObj);
				Element keyElement = modelViewObj.toXmlRef();
				keyElement.addAttribute("description", "key");
				viewobjectElement.add(keyElement);
				Element valueElement = buildViewObj.toXmlRef();
				valueElement.addAttribute("description", "value");
				viewobjectElement.add(valueElement);
			}
			Element relationobjectElement = DocumentHelper.createElement("relationobjectmap");
			// store relation object map objects
			objectKeySet = relationobjectmap.keySet();
			for (Iterator keys = objectKeySet.iterator(); keys.hasNext();) {
				ModelObject modelObj = (ModelObject) keys.next();
				ModelObject buildViewObj = (ModelObject) relationobjectmap.get(modelObj);
				Element keyElement = modelObj.toXmlRef();
				keyElement.addAttribute("description", "key");
				relationobjectElement.add(keyElement);
				Element valueElement = buildViewObj.toXmlRef();
				valueElement.addAttribute("description", "value");
				relationobjectElement.add(valueElement);
			}
			topXmlElement.add(objectElement);
			topXmlElement.add(viewobjectElement);
			topXmlElement.add(relationobjectElement);
			return topXmlElement;
		}
		else
			return null;
	}

	public void setIsDefaultInterface()
	{
		isdefaultIface = true;
	}

	public ModelObjectFactory getModelObjectFactory()
	{
		if (m_moFactory == null)
			m_moFactory = new ModelObjectBaseFactory();
		return m_moFactory;
	}


	protected class ModelObjectsListener implements DListListener
	{
		HashMap listenermap = new HashMap(); //key -iface obj, value - listeners
		DomeModel model;
		ConnectionMappingManager mgr;

		protected ModelObjectsListener(DomeModel model)
		{
			this.model = model;
			this.mgr = model.getMappingManager();
		}

		//add listeners on pre-existing objects
		protected void addListenersToExistingObjects()
		{
			Collection mobjects = model.getModelObjects();
			for (Iterator i = mobjects.iterator(); i.hasNext();) {
				ModelObject mobj = (ModelObject) i.next();
				if(mobj instanceof AbstractSubscription) {
					Collection subObjects = ((AbstractSubscription)mobj).getModelObjects();
					for (Iterator iterator = subObjects.iterator(); iterator.hasNext();) {
						ModelObject o = (ModelObject) iterator.next();
						ModelObject iobj = (ModelObject) objectmap.get(o);
						addListeners(o, iobj);
					}
				}
				else {
					ModelObject iobj = (ModelObject) objectmap.get(mobj);
					addListeners(mobj, iobj);
				}
			}
		}

		//obj - model object, newobj - interface model view object
		public void addListeners(ModelObject obj, ModelObject ifaceobj)
		{
			if (ifaceobj instanceof Parameter) {
				Parameter viewobj = (Parameter) viewobjectmap.get(ifaceobj); //interface build & causal views parameter
				PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
				obj.addPropertyChangeListener(NameListener.NAME, nameListen);
				CausalityChangeListener cl = new ParameterCausalityListener(viewobj);
				Model m = getModel();
				m.addCausalityChangeListener(obj, cl);
				Object[] listenArray = {nameListen, cl};
				listenermap.put(viewobj, listenArray);
			} else 	if (ifaceobj instanceof Visualization) {
				Visualization viewobj = (Visualization) viewobjectmap.get(ifaceobj); //interface build & causal views parameter
				PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
				obj.addPropertyChangeListener(NameListener.NAME, nameListen);
				/*CausalityChangeListener cl = new ParameterCausalityListener(viewobj);
				Model m = getModel();
				m.addCausalityChangeListener(obj, cl);
				Object[] listenArray = {nameListen, cl};
				listenermap.put(viewobj, listenArray);  */
			}
			else if (ifaceobj instanceof Relation) {
				Relation viewobj = (Relation) ifaceobj; //interface model view relation
				PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
				obj.addPropertyChangeListener(NameListener.NAME, nameListen);
				RelationObjectsListener rl = new RelationObjectsListener(viewobj,
				                                                         (Relation) obj, mgr);
				((Relation) obj).addModelObjectsListener(rl);
				RelationPropertyListener cl = new RelationPropertyListener(viewobj,
				                                                           rl.getInterfaceObjectMap());
				obj.addPropertyChangeListener(cl);
				Object[] listeners = new Object[]{nameListen, rl, cl};
				listenermap.put(viewobj, listeners);
			}
			else if (ifaceobj instanceof Context) {
				PropertyChangeListener nameListen = new ModelObjectNameListener(ifaceobj);
				obj.addPropertyChangeListener(NameListener.NAME, nameListen);
				ContextObjectsListener cl = new ContextObjectsListener((Context) ifaceobj, (Context) obj, mgr, true);
				((Context)obj).addModelObjectReferencesListener(cl);
				Object[] listeners = new Object[]{nameListen, cl};
				listenermap.put(ifaceobj, listeners);
			}
		}

		public void intervalAdded(DListEvent e)
		{
			List items = e.getItems();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				if (obj instanceof AbstractSubscription) {
					// add for subscription
					distinctModelObjs.add(obj);
					Collection subscriptionObjects = ((AbstractSubscription)obj).getModelObjects();
					for (Iterator iterator = subscriptionObjects.iterator(); iterator.hasNext();) {
						ModelObject object = (ModelObject)iterator.next();
						final ModelObject newobj = modelview.addObjectAndReference(object, false);
						PropertyChangeListener nameListener = new ModelObjectNameListener(newobj);
						object.addPropertyChangeListener(NameListener.NAME, nameListener);
						objectmap.put(object, newobj);
						if (newobj instanceof Parameter) {
							listenermap.put(newobj, nameListener);
							Collection c = new ArrayList();
							c.add(object);
							mgr.addMappings((Parameter) newobj, c);
							//create obj in build and causal views
							Parameter viewobj =
							        (Parameter) ModelInterfaceBuilder.super.newModelObject(newobj);
							ModelInterfaceBuilder.this.buildViewContext.addModelObjectReference(viewobj);
							viewobjectmap.put(newobj, viewobj);
							mgr.addMappings(viewobj, c);
							PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
							object.addPropertyChangeListener(NameListener.NAME, nameListen);
							CausalityChangeListener cl = new ParameterCausalityListener(viewobj);
							Model m = getModel();
							m.addCausalityChangeListener(object, cl);
							Object[] listenArray = new Object[]{nameListen, cl};
							listenermap.put(viewobj, listenArray);
						} else if (newobj instanceof Visualization) {
							listenermap.put(newobj, nameListener);
							Collection c = new ArrayList();
							c.add(object);
							mgr.addMappings((Visualization) newobj, c);
							//create obj in build and causal views
							Visualization viewobj =
							        (Visualization) ModelInterfaceBuilder.super.newModelObject(newobj);
							ModelInterfaceBuilder.this.buildViewContext.addModelObjectReference(viewobj);
							viewobjectmap.put(newobj, viewobj);
							mgr.addMappings(viewobj, c);
							PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
							object.addPropertyChangeListener(NameListener.NAME, nameListen);
							/*CausalityChangeListener cl = new ParameterCausalityListener(viewobj);
							Model m = getModel();
							m.addCausalityChangeListener(object, cl);
							Object[] listenArray = new Object[]{nameListen, cl};
							listenermap.put(viewobj, listenArray); */
						}

					}
				}
				else {
					if (distinctModelObjs.contains(obj)) {
						//this is not a separate object but a reference
						Object modelViewObject = objectmap.get(obj);
						modelview.pasteRefrence(modelview.getModelViewContext(), (ModelObject) modelViewObject);
					}
					else {
						distinctModelObjs.add(obj);

                        final ModelObject newobj = modelview.addObjectAndReference(obj, false);
						PropertyChangeListener nameListener = new ModelObjectNameListener(newobj);
						obj.addPropertyChangeListener(NameListener.NAME, nameListener);
						objectmap.put(obj, newobj);
						if (newobj instanceof Relation) {
							RelationObjectsListener rl = new RelationObjectsListener((Relation) newobj,
							                                                         (Relation) obj, mgr);
							((Relation) obj).addModelObjectsListener(rl);
							RelationPropertyListener cl = new RelationPropertyListener((Relation) newobj,
							                                                           rl.getInterfaceObjectMap());
							obj.addPropertyChangeListener(cl);
							Object[] listeners = new Object[]{nameListener, rl, cl};
							listenermap.put(newobj, listeners);
						}
						else if (newobj instanceof Context) {
							ContextObjectsListener cl = new ContextObjectsListener((Context) newobj, (Context)obj, mgr, false);
							((Context) obj).addModelObjectReferencesListener(cl);
							Object[] listeners = new Object[]{nameListener, cl};
							listenermap.put(newobj, listeners);
						}
						else if (newobj instanceof Visualization) {
							listenermap.put(newobj, nameListener);
							Collection c = new ArrayList();
							c.add(obj);
							mgr.addMappings((Visualization) newobj, c);
							//create obj in build and causal views
							Visualization viewobj =
							        (Visualization) ModelInterfaceBuilder.super.newModelObject(newobj);
							ModelInterfaceBuilder.this.buildViewContext.addModelObjectReference(viewobj);
							viewobjectmap.put(newobj, viewobj);
							mgr.addMappings(viewobj, c);
							PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
							obj.addPropertyChangeListener(NameListener.NAME, nameListen);
							/*CausalityChangeListener cl = new ParameterCausalityListener(viewobj);
							Model m = getModel();
							m.addCausalityChangeListener(obj, cl);
							Object[] listenArray = new Object[]{nameListen, cl};
							listenermap.put(viewobj, listenArray); */
						}
						else if (newobj instanceof Parameter) {
							listenermap.put(newobj, nameListener);
							Collection c = new ArrayList();
							c.add(obj);
							mgr.addMappings((Parameter) newobj, c);
							//create obj in build and causal views
							Parameter viewobj =
							        (Parameter) ModelInterfaceBuilder.super.newModelObject(newobj);
							ModelInterfaceBuilder.this.buildViewContext.addModelObjectReference(viewobj);
							viewobjectmap.put(newobj, viewobj);
							mgr.addMappings(viewobj, c);
							PropertyChangeListener nameListen = new ModelObjectNameListener(viewobj);
							obj.addPropertyChangeListener(NameListener.NAME, nameListen);
							CausalityChangeListener cl = new ParameterCausalityListener(viewobj);
							Model m = getModel();
							m.addCausalityChangeListener(obj, cl);
							Object[] listenArray = new Object[]{nameListen, cl};
							listenermap.put(viewobj, listenArray);
						}
					}
				}
			}
		}

		public void intervalChanged(DListEvent e)
		{
			/* This method should probably never get called,
			// but it does when the parameters in the model are shifted up or down.
			// System.out.println("in intervalChanged");
			throw new UnsupportedOperationException();
			*/
		}

		public void itemsRemoved(DListEvent e)
		{
			intervalRemoved(e);
		}

		public void intervalRemoved(DListEvent e)
		{
			List items = e.getItems();
			List tobeRemoved = new ArrayList();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				if (obj instanceof AbstractSubscription) {
					// remove for subscription
					distinctModelObjs.remove(obj);
					Collection subscriptionObjects = ((AbstractSubscription) obj).getModelObjects();
					for (Iterator iterator = subscriptionObjects.iterator(); iterator.hasNext();) {
						ModelObject object = (ModelObject)iterator.next();
						Object newobj = objectmap.get(object);
						tobeRemoved.add(newobj);
						if (newobj instanceof Parameter) {
							NameListener listener = (NameListener) listenermap.get(newobj);
							obj.removePropertyChangeListener(NameListener.NAME, listener);
							mgr.removeMapping((Parameter) newobj, (Parameter) object);
							//remove obj from build and causal views
							Parameter viewobj = (Parameter) viewobjectmap.get(newobj);
							ModelInterfaceBuilder.this.deleteModelObject(viewobj);
							DefaultContextBuilder bv = (DefaultContextBuilder) ModelInterfaceBuilder.this.buildViewContext;
							bv.removeModelObjectReference(viewobj);
							mgr.removeMapping(viewobj, (Parameter) object);
							viewobjectmap.remove(newobj);
							Object[] listeners = (Object[]) listenermap.get(viewobj);
							if (listeners != null) {
								PropertyChangeListener nameListen = (PropertyChangeListener) listeners[0];
								obj.removePropertyChangeListener(NameListener.NAME, nameListen);
								CausalityChangeListener cl = (CausalityChangeListener) listeners[1];
								Model m = getModel();
								m.removeCausalityChangeListener(obj, cl);
							}
							listenermap.remove(viewobj);
							objectmap.remove(obj);
						} else 	if (newobj instanceof Visualization) {
							NameListener listener = (NameListener) listenermap.get(newobj);
							obj.removePropertyChangeListener(NameListener.NAME, listener);
							mgr.removeMapping((Visualization) newobj, (Visualization) object);
							//remove obj from build and causal views
							Visualization viewobj = (Visualization) viewobjectmap.get(newobj);
							ModelInterfaceBuilder.this.deleteModelObject(viewobj);
							DefaultContextBuilder bv = (DefaultContextBuilder) ModelInterfaceBuilder.this.buildViewContext;
							bv.removeModelObjectReference(viewobj);
							mgr.removeMapping(viewobj, (Visualization) object);
							viewobjectmap.remove(newobj);
							Object[] listeners = (Object[]) listenermap.get(viewobj);
							if (listeners != null) {
								PropertyChangeListener nameListen = (PropertyChangeListener) listeners[0];
								obj.removePropertyChangeListener(NameListener.NAME, nameListen);
								/*CausalityChangeListener cl = (CausalityChangeListener) listeners[1];
								Model m = getModel();
								m.removeCausalityChangeListener(obj, cl); */
							}
							listenermap.remove(viewobj);
							objectmap.remove(obj);
						}

					}
				}
				else {
					boolean isReference = !distinctModelObjs.remove(obj);
					if (isReference) {//do not proceed if it is a reference
						return;
					}
					Object newobj = objectmap.get(obj);
					tobeRemoved.add(newobj);
					if (newobj instanceof Relation) {
						//All Connections from interface relations to a model relation
						//get removed when a model relation is deleted
						//so we do not remove individual connections here again
						Object[] listeners = (Object[]) listenermap.get(newobj);
						if (listeners != null) {
							obj.removePropertyChangeListener(NameListener.NAME,
							                                 (NameListener) listeners[0]);
							RelationObjectsListener rl = (RelationObjectsListener) listeners[1];
							//delete corresponding objects in other 3 views first
							rl.deleteAll3ViewObjects();
							//remove all parameters inside the model relation from objectmap
							Collection modelRelObjects = ((Relation)obj).getModelObjects();
							for (Iterator iterator = modelRelObjects.iterator(); iterator.hasNext();) {
								objectmap.remove(iterator.next());
							}
							((Relation) obj).removeModelObjectsListener(rl);
							obj.removePropertyChangeListener((PropertyChangeListener) listeners[2]);
						}
					}
					else if (newobj instanceof Context) {
						Object[] listeners = (Object[]) listenermap.get(newobj);
						obj.removePropertyChangeListener(NameListener.NAME,
						                                 (NameListener) listeners[0]);
						//remove context objs listener
						((Context) obj).removeModelObjectReferencesListener((DListListener) listeners[1]);
						List conobjects = ((Context) obj).getModelObjectReferences();
						((ContextObjectsListener) listeners[1]).deleteParametersFrom3Views(conobjects);
					}
					else if (newobj instanceof Parameter) {
						NameListener listener = (NameListener) listenermap.get(newobj);
						obj.removePropertyChangeListener(NameListener.NAME, listener);
						mgr.removeMapping((Parameter) newobj, (Parameter) obj);
						//remove obj from build and causal views
						Parameter viewobj = (Parameter) viewobjectmap.get(newobj);
						ModelInterfaceBuilder.this.deleteModelObject(viewobj);
						DefaultContextBuilder bv = (DefaultContextBuilder) ModelInterfaceBuilder.this.buildViewContext;
						bv.removeModelObjectReference(viewobj);
						mgr.removeMapping(viewobj, (Parameter) obj);
						viewobjectmap.remove(newobj);
						Object[] listeners = (Object[]) listenermap.get(viewobj);
						if (listeners != null) {
							PropertyChangeListener nameListen = (PropertyChangeListener) listeners[0];
							obj.removePropertyChangeListener(NameListener.NAME, nameListen);
							CausalityChangeListener cl = (CausalityChangeListener) listeners[1];
							Model m = getModel();
							m.removeCausalityChangeListener(obj, cl);
						}
						listenermap.remove(viewobj);
					} else if (newobj instanceof Visualization) {
						NameListener listener = (NameListener) listenermap.get(newobj);
						obj.removePropertyChangeListener(NameListener.NAME, listener);
						mgr.removeMapping((Visualization) newobj, (Visualization) obj);
						//remove obj from build and causal views
						Visualization viewobj = (Visualization) viewobjectmap.get(newobj);
						ModelInterfaceBuilder.this.deleteModelObject(viewobj);
						DefaultContextBuilder bv = (DefaultContextBuilder) ModelInterfaceBuilder.this.buildViewContext;
						bv.removeModelObjectReference(viewobj);
						mgr.removeMapping(viewobj, (Visualization) obj);
						viewobjectmap.remove(newobj);
						Object[] listeners = (Object[]) listenermap.get(viewobj);
						if (listeners != null) {
							PropertyChangeListener nameListen = (PropertyChangeListener) listeners[0];
							obj.removePropertyChangeListener(NameListener.NAME, nameListen);
							/*CausalityChangeListener cl = (CausalityChangeListener) listeners[1];
							Model m = getModel();
							m.removeCausalityChangeListener(obj, cl); */
						}
						listenermap.remove(viewobj);
					}

					objectmap.remove(obj);
				}
			}
			modelview.removeObjectsAndListeners(tobeRemoved);
		}

		public void itemsReplaced(DListEvent e)
		{
			// System.out.println("in itemsReplaced");
			throw new UnsupportedOperationException();
		}

		public Object getInterfaceObject(Object modObject)
		{
			return objectmap.get(modObject);
		}
	}

	protected static class ModelObjectNameListener extends NameListener
	{
		ModelObject newobj;

		protected ModelObjectNameListener(ModelObject newobj)
		{
			this.newobj = newobj;
		}

		public void nameChanged(String newName)
		{
			newobj.setName(newName);
		}
	}

	//used by default interface only
	//see NondefaultRelationObjectsListener class for a listener
	//for non-default interface
	protected class RelationObjectsListener implements DListListener
	{
		Relation ifaceRel;
		Relation modelRel;
		HashMap listenermap; //key - interface object value - name listener
		ConnectionMappingManager mgr;

		/**
		 * Create a listener with a populated relation.
		 * @param ifacerel New interface relation
		 * @param modelRel Original relation
		 */
		protected RelationObjectsListener(Relation ifacerel, Relation modelRel,
		                                  ConnectionMappingManager mgr)
		{
			this.ifaceRel = ifacerel;
			this.modelRel = modelRel;
			listenermap = new HashMap();
			this.mgr = mgr;

			if (ifacerel instanceof AbstractProceduralRelation) {
				//save as
				HashMap objMap = ((AbstractProceduralRelation) ifacerel).getModelObjectMap();
				if (objMap != null && objMap.size() > 0) {
					Set keySet = objMap.keySet();
					for (Iterator keyIter = keySet.iterator(); keyIter.hasNext();) {
						Id modelObjId = (Id) keyIter.next();
						ModelObject modelObj = modelRel.getModelObjectById(modelObjId);
						ModelObject ifaceObj = (ModelObject) objMap.get(modelObjId);
						addModelObject(modelObj, ifaceObj);
					}
				}
				else {
					//load from xml
					//ModelObjectMap is not populated
					for (Iterator i = modelRel.getModelObjects().iterator(); i.hasNext();) {
						Parameter mp = (Parameter) i.next();
						Parameter ip = (Parameter) relationobjectmap.get(mp);
						NameListener nmlistener = new ModelObjectNameListener(ip);
						mp.addPropertyChangeListener(NameListener.NAME, nmlistener);
						ParameterCausalityListener causlistener = new ParameterCausalityListener(ip);
						modelRel.getModel().addCausalityChangeListener(mp, causlistener);
						Object[] listeners = new Object[]{nmlistener, causlistener};
						listenermap.put(ip, listeners);
					}
				}
			}
		}

		private void addModelObject(ModelObject modelObj, ModelObject ifaceObj)
		{
			NameListener namelistener = new ModelObjectNameListener(ifaceObj);
			modelObj.addPropertyChangeListener(NameListener.NAME, namelistener);
			objectmap.put(modelObj, ifaceObj);
			listenermap.put(ifaceObj, namelistener);
			Collection c = new ArrayList();
			c.add(modelObj);
			mgr.addMappings((Parameter) ifaceObj, c);
			//also add a copy of this relation obj in Build view and causal views
			Parameter new3ViewObject = (Parameter) ModelInterfaceBuilder.super.newModelObject(modelObj);
			buildViewContext.addModelObjectReference(new3ViewObject);
			relationobjectmap.put(modelObj, new3ViewObject);
			NameListener nmlistener = new ModelObjectNameListener(new3ViewObject);
			modelObj.addPropertyChangeListener(NameListener.NAME, nmlistener);
			ParameterCausalityListener causlistener = new ParameterCausalityListener(new3ViewObject);
			modelRel.getModel().addCausalityChangeListener(modelObj, causlistener);
			Object[] listeners = new Object[]{nmlistener, causlistener};
			listenermap.put(new3ViewObject, listeners);
			mgr.addMapping(new3ViewObject, (Parameter) modelObj);
		}

		public void intervalAdded(DListEvent e)
		{
			List items = e.getItems();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				if (distinctModelObjs.contains(obj)) {
					continue; //this is not a separate object but a reference
					//so ignore it
				}
				else {
					distinctModelObjs.add(obj);
					ModelObject newObject = ifaceRel.newModelObject(obj);
					addModelObject(obj, newObject);
				}
			}
		}


		public void intervalRemoved(DListEvent e)
		{
			List items = e.getItems();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				distinctModelObjs.remove(obj);
				ModelObject newObject = (ModelObject) objectmap.get(obj);
				NameListener listener = (NameListener) listenermap.get(newObject);
				obj.removePropertyChangeListener(listener);
				listenermap.remove(newObject);
				ifaceRel.deleteModelObject(newObject);
				objectmap.remove(obj);
				mgr.removeMapping((Parameter) newObject, (Parameter) obj);
				//also remove copy of this relation obj from Build view and causal views
				delete3ViewObject((Parameter) obj);
			}

		}


		public void intervalChanged(DListEvent e)
		{
			/* This method should probably never get called,
			// but it does when the parameters in the model are shifted up or down.
			// System.out.println("in intervalChanged");
			throw new UnsupportedOperationException();
			*/
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
			return Collections.unmodifiableMap(objectmap);
		}

		public void deleteAll3ViewObjects()
		{
			Collection relObjects = modelRel.getModelObjects();
			for (Iterator i = relObjects.iterator(); i.hasNext();) {
				Parameter obj = (Parameter) i.next();
				delete3ViewObject(obj);
			}
		}

		public void delete3ViewObject(Parameter obj)
		{
			Parameter new3ViewObject = (Parameter) relationobjectmap.get(obj);
			Object[] listeners = (Object[]) listenermap.get(new3ViewObject);
			NameListener namelistener = (NameListener) listeners[0];
			obj.removePropertyChangeListener(NameListener.NAME, namelistener);
			ParameterCausalityListener causlistener = (ParameterCausalityListener) listeners[1];
			modelRel.getModel().removeCausalityChangeListener(obj, causlistener);
			listenermap.remove(new3ViewObject);
			relationobjectmap.remove(obj);
			mgr.removeMapping(new3ViewObject, obj);
			if (tempModelObjects.contains(new3ViewObject)) {

                tempModelObjects.remove(new3ViewObject);
                tempModelObjectsMap.remove(new3ViewObject.getId());
			}
			ModelInterfaceBuilder.this.deleteModelObject(new3ViewObject);
			buildViewContext.removeModelObjectReference(new3ViewObject);
		}
	}

	//used by default as well as non-default interfaces
	protected static class RelationPropertyListener implements PropertyChangeListener
	{
		Map map;
		Relation rel;

		protected RelationPropertyListener(Relation ifaceRel, Map map)
		{
			this.map = map;
			rel = ifaceRel;
		}

		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(ProceduralRelation.BODY)) {
				((ProceduralRelation) rel).setBody((String) newValue);
				if (rel instanceof ConcreteProceduralRelation)
					((ConcreteProceduralRelation) rel).getPythonEditor().setText((String) newValue);
                if (rel instanceof ConditionIterationRelation)
					((ConditionIterationRelation) rel).getPythonEditor().setText((String) newValue);
			}
            else if (property.equals(IterationRelation.CONDITION)) {
				((IterationRelation) rel).setCondition((String) newValue);
				if (rel instanceof ConditionIterationRelation)
					((ConditionIterationRelation) rel).getPythonEditor_cond().setText((String) newValue);
			}
            else if (property.equals(IterationRelation.ITERATIONTYPE)) {
				((IterationRelation) rel).setIterationType((String) newValue);
			}
            else if (property.equals(IterationRelation.BROADCASTINGTYPE)) {
				((IterationRelation) rel).setBroadcasting_eachloop(((Boolean)newValue).booleanValue());
    		}
			else if (property.equals(ProceduralRelation.DEPENDENCY_INFO)) {
				Collection col = rel.getModelObjects();
				List objs = new ArrayList(col);
				DependencyInfo info = new DependencyInfo(objs);
				DependencyInfo dep = (DependencyInfo) newValue;
				Set keys = dep.getDependencyKeys();
				for (Iterator i = keys.iterator(); i.hasNext();) {
					Object obj = i.next();
					Collection dependents = dep.getDependentsForObject(obj);
					for (Iterator j = dependents.iterator(); j.hasNext();) {
						Object object = j.next();
						Object independObj = map.get(obj);
						Object dependObj = map.get(object);
						info.addDependency(independObj, dependObj);
					}
				}
				((ProceduralRelation) rel).setDependencyInfo(info);
			}
		}

	}

	protected class ContextObjectsListener implements DListListener
	{
		Context ifaceCon;
		Context modelCon;
		HashMap listenermap; //key - interface object value - name listener
		ConnectionMappingManager mgr;

		/**
		 * Create a listener with a populated context.
		 * @param ifaceCon New interface context
		 */
		protected ContextObjectsListener(Context ifaceCon,
		                                 Context modelCon,
		                                 ConnectionMappingManager mgr,
		                                 boolean addListenersOnly)
		{
			this.ifaceCon = ifaceCon;
			this.modelCon = modelCon;
			listenermap = new HashMap();
			this.mgr = mgr;
			if(!addListenersOnly)
				processModelObjects(ifaceCon.getModelObjectReferences(),
			                    modelCon.getModelObjectReferences());
		}

		private void processModelObjects(List ifaceObjs, List modelObjs)
		{
			Iterator j = modelObjs.iterator();
			for (Iterator i = ifaceObjs.iterator(); i.hasNext() && j.hasNext();) {
				ModelObject newObject = (ModelObject) i.next();
				ModelObject obj = (ModelObject) j.next();
				if (distinctModelObjs.contains(obj)) {
					//this is not a separate object but a reference
					Object modelViewObject = objectmap.get(obj);
					modelview.pasteRefrence(ifaceCon, (ModelObject) modelViewObject);
				}
				else {
					distinctModelObjs.add(obj);
					NameListener namelistener = new ModelObjectNameListener(newObject);
					obj.addPropertyChangeListener(NameListener.NAME, namelistener);
					if (obj instanceof Context) {
						ContextObjectsListener clistener = new ContextObjectsListener((Context) newObject, (Context)obj, mgr, false);
						((Context) obj).addModelObjectReferencesListener(clistener);
						Object[] listeners = new Object[]{namelistener, clistener};
						listenermap.put(newObject, listeners);
					} else if (obj instanceof Relation) {
						RelationObjectsListener rl = new RelationObjectsListener((Relation) newObject,
						                                                         (Relation) obj, mgr);
						((Relation) obj).addModelObjectsListener(rl);
						RelationPropertyListener cl = new RelationPropertyListener((Relation) newObject,
						                                                           rl.getInterfaceObjectMap());
						obj.addPropertyChangeListener(cl);
						Object[] listeners = new Object[]{namelistener, rl, cl};
						listenermap.put(newObject, listeners);
					}
					else if (obj instanceof Visualization) {
						//create obj in model view
						listenermap.put(newObject, namelistener);
						Collection c = new ArrayList();
						c.add(obj);
						mgr.addMappings((Visualization) newObject, c);
						//create obj in build and causal views
						ModelObject modobj = ModelInterfaceBuilder.super.newModelObject(newObject);
						buildViewContext.addModelObjectReference(modobj);
						mgr.addMappings((Visualization) modobj, c);
						NameListener listen = new ModelObjectNameListener(modobj);
						obj.addPropertyChangeListener(NameListener.NAME, listen);
						/*CausalityChangeListener cl = new ParameterCausalityListener((Parameter) modobj);
						Model m = getModel();
						m.addCausalityChangeListener(obj, cl);
						Object[] listenArray = {listen, cl};
						listenermap.put(modobj, listenArray);   */
						viewobjectmap.put(newObject, modobj);   //model view obj - BCVs obj
					} else {
						//create obj in model view
						listenermap.put(newObject, namelistener);
						Collection c = new ArrayList();
						c.add(obj);
						mgr.addMappings((Parameter) newObject, c);
						//create obj in build and causal views
						ModelObject modobj = ModelInterfaceBuilder.super.newModelObject(newObject);
						buildViewContext.addModelObjectReference(modobj);
						mgr.addMappings((Parameter) modobj, c);
						NameListener listen = new ModelObjectNameListener(modobj);
						obj.addPropertyChangeListener(NameListener.NAME, listen);
						CausalityChangeListener cl = new ParameterCausalityListener((Parameter) modobj);
						Model m = getModel();
						m.addCausalityChangeListener(obj, cl);
						Object[] listenArray = {listen, cl};
						listenermap.put(modobj, listenArray);
						viewobjectmap.put(newObject, modobj);   //model view obj - BCVs obj
					}
					objectmap.put(obj, newObject);  //model object - iface model view obj
				}
			}
		}

		public void intervalAdded(DListEvent e)
		{
			List items = e.getItems();
			List ifaceItems = new ArrayList();
			for (Iterator iterator = items.iterator(); iterator.hasNext();) {
				ModelObject o = (ModelObject) iterator.next();
				if (distinctModelObjs.contains(o)) {
					//this is not a separate object but a reference
					Object modelViewObject = objectmap.get(o);
					modelview.pasteRefrence(ifaceCon, (ModelObject) modelViewObject);
				}
				else {
                    ModelObject newObject = modelview.addObjectAndReference(o, true);
					this.ifaceCon.addModelObjectReference(newObject);
					ifaceItems.add(newObject);
				}
			}
			processModelObjects(ifaceItems, items);
		}


		public void intervalRemoved(DListEvent e)
		{
			List items = e.getItems();
			List tobeRemoved = new ArrayList();
			for (Iterator i = items.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				boolean isReference = !distinctModelObjs.remove(obj);
				if (isReference) {//do not proceed if it is a reference
					return;
				}
				ModelObject newObject = (ModelObject) objectmap.get(obj);
				if (obj instanceof Context) {
					Object[] listeners = (Object[]) listenermap.get(newObject);
					if(listeners != null) {
						obj.removePropertyChangeListener(NameListener.NAME, (NameListener) listeners[0]);
						((Context) obj).removeModelObjectReferencesListener(
								(ContextObjectsListener) listeners[1]);
						List conobjects = ((Context) obj).getModelObjectReferences();
						((ContextObjectsListener) listeners[1]).deleteParametersFrom3Views(conobjects);
					}
				}
				else if (obj instanceof Relation) {
					//All Connections from interface relations to a model relation
					//get removed when a model relation is deleted
					//so we do not remove individual connections here again
					Object[] listeners = (Object[]) listenermap.get(newObject);
					if (listeners != null) {
						obj.removePropertyChangeListener(NameListener.NAME, (NameListener) listeners[0]);
						RelationObjectsListener rl = (RelationObjectsListener) listeners[1];
						rl.deleteAll3ViewObjects();
						//remove all parameters inside the model relation from objectmap
						Collection modelRelObjects = ((Relation) obj).getModelObjects();
						for (Iterator iterator = modelRelObjects.iterator(); iterator.hasNext();) {
							objectmap.remove(iterator.next());
						}
						((Relation) obj).removeModelObjectsListener(rl);
						RelationPropertyListener cl = (RelationPropertyListener) listeners[2];
						obj.removePropertyChangeListener(cl);
					}
				} else if (obj instanceof Visualization){
					NameListener namelistener = (NameListener) listenermap.get(newObject);
					obj.removePropertyChangeListener(NameListener.NAME, namelistener);
					mgr.removeMapping((Visualization) newObject, (Visualization) obj);
					//remove obj from build and causal views
					Visualization modobj = (Visualization) viewobjectmap.get(newObject);
					ModelInterfaceBuilder.super.deleteModelObject(modobj);
					buildViewContext.removeModelObjectReference(modobj);
					Collection c = mgr.getMappingsForVisualization(modobj);
					mgr.removeMappings(modobj, c);
					Object[] listenArray = (Object[]) listenermap.get(modobj);
					if (listenArray != null) {
						obj.removePropertyChangeListener(NameListener.NAME, (NameListener) listenArray[0]);
						/*Model m = getModel();
						m.removeCausalityChangeListener(obj, (CausalityChangeListener) listenArray[1]); */
					}
					listenermap.remove(modobj);
					viewobjectmap.remove(newObject);
				}
				else {
					NameListener namelistener = (NameListener) listenermap.get(newObject);
					obj.removePropertyChangeListener(NameListener.NAME, namelistener);
					mgr.removeMapping((Parameter) newObject, (Parameter) obj);
					//remove obj from build and causal views
					Parameter modobj = (Parameter) viewobjectmap.get(newObject);
					ModelInterfaceBuilder.super.deleteModelObject(modobj);
					buildViewContext.removeModelObjectReference(modobj);
					Collection c = mgr.getMappingsForParameter(modobj);
					mgr.removeMappings(modobj, c);
					Object[] listenArray = (Object[]) listenermap.get(modobj);
					if (listenArray != null) {
						obj.removePropertyChangeListener(NameListener.NAME, (NameListener) listenArray[0]);
						Model m = getModel();
						m.removeCausalityChangeListener(obj, (CausalityChangeListener) listenArray[1]);
					}
					listenermap.remove(modobj);
					viewobjectmap.remove(newObject);
				}
				listenermap.remove(newObject);
				ifaceCon.removeModelObjectReference(newObject);
				tobeRemoved.add(newObject);
				objectmap.remove(obj);
			}
			modelview.removeObjectsAndListeners(tobeRemoved);
		}

		public void intervalChanged(DListEvent e)
		{
			/* This method should probably never get called,
			// but it does when the parameters in the model are shifted up or down.
			// System.out.println("in intervalChanged");
			throw new UnsupportedOperationException();
			*/
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
			return Collections.unmodifiableMap(objectmap);
		}

		public void deleteParametersFrom3Views(List modelobjs)
		{
			for (Iterator i = modelobjs.iterator(); i.hasNext();) {
				ModelObject obj = (ModelObject) i.next();
				if (obj instanceof Parameter) {
					//remove obj from build and causal views
					Parameter newObject = (Parameter) objectmap.get(obj);
					distinctModelObjs.remove(obj);
					Parameter viewObject = (Parameter) viewobjectmap.get(newObject);
					mgr.removeAllMappings(newObject);
					mgr.removeAllMappings(viewObject);
					ModelInterfaceBuilder.super.deleteModelObject(viewObject);
					buildViewContext.removeModelObjectReference(viewObject);
					Object listener = listenermap.get(newObject);
					if(listener instanceof Object[]) {
						Object[] listenArray = (Object[]) listener;
						obj.removePropertyChangeListener(NameListener.NAME, (NameListener) listenArray[0]);
						Model m = getModel();
						m.removeCausalityChangeListener(obj, (CausalityChangeListener) listenArray[1]);
					}
					else if(listener instanceof NameListener)  {
						obj.removePropertyChangeListener(NameListener.NAME, (NameListener) listener);
					}
					listenermap.remove(newObject);
					viewobjectmap.remove(newObject);
					objectmap.remove(obj);
				}
				else if(obj instanceof Context) {
					List list = ((Context)obj).getModelObjectReferences();
					deleteParametersFrom3Views(list);
				}
			}
		}
	}

	protected class ParameterCausalityListener implements CausalityChangeListener
	{
		Parameter newParam;
		Parameter source; //null in case of default interface

		protected ParameterCausalityListener(Parameter newParam)
		{
			this.newParam = newParam;
		}

		protected ParameterCausalityListener(Parameter source, Parameter target)
		{
			this.source = source;
			this.newParam = target;
		}

		public void causalityChanged(CausalityChangeEvent e)
		{
			CausalityStatus newstat = e.getNewCausalityStatus();
			ModelInterfaceBuilder.this.changeCausality(newParam, newstat);
			//do not show the intermediate params in default interface
			if (isdefaultIface) {
				CausalityStatus oldstat = e.getOldCausalityStatus();
				//NOTE: all comparison with nulls avoid NullPointerExceptions
				if (oldstat == null || !(oldstat.equals(CausalityStatus.INTERMEDIATE))) {
					if (newstat != null && newstat.equals(CausalityStatus.INTERMEDIATE)) {
						//remove it from interface build and causal views
						modelObjects.remove(newParam);
						buildViewContext.removeModelObjectReference(newParam);
//						//add it to temp store
						tempModelObjects.add(newParam);
						tempModelObjectsMap.put(newParam.getId(), newParam);
					}
				}
				else if (newstat == null || !(newstat.equals(CausalityStatus.INTERMEDIATE))) {
					if (oldstat != null && oldstat.equals(CausalityStatus.INTERMEDIATE)) {
						//add it to interface build and causal views
						modelObjects.add(newParam);
						buildViewContext.addModelObjectReference(newParam);
						//It seems like when a parameter is added, its causality is set to
						//independent.  The following statement sets the correct causality.
						ModelInterfaceBuilder.this.changeCausality(newParam, newstat);
//						//remove it from temp store
						tempModelObjects.remove(newParam);
						tempModelObjectsMap.remove(newParam.getId());
					}
				}
			}
		}

		public Parameter getSource()
		{
			return source;
		}
	}


	private void addCausalityListener(Object source, Object target)
	{
		if (!isdefaultIface) {
			if (source instanceof Parameter && target instanceof Parameter) {
				Model m = ((Parameter) source).getModel();
				CausalityChangeListener cl = new ParameterCausalityListener((Parameter) target);
				m.addCausalityChangeListener(source, cl);
				sharedListenerMap.put(target, cl);
			}
		}
	}

	private void removeCausalityListener(Object ob)
	{
		if (!isdefaultIface && ob instanceof Parameter) {
			Parameter p = (Parameter) ob;
			Object obj = sharedListenerMap.get(p);
			if (obj != null) {
				ParameterCausalityListener cl = (ParameterCausalityListener) obj;
				Parameter source = cl.getSource();
				if (source != null)
					source.getModel().removeCausalityChangeListener(cl);
				sharedListenerMap.remove(p);
			}
		}
	}

	private void addParameterListeners(Parameter modelParam, Parameter ifaceParam)
	{
		if (isdefaultIface) {
			PropertyChangeListener nameListen = new ModelObjectNameListener(ifaceParam);
			modelParam.addPropertyChangeListener(NameListener.NAME, nameListen);
			CausalityChangeListener cl = new ParameterCausalityListener(ifaceParam);
			Model m = getModel();
			m.addCausalityChangeListener(modelParam, cl);
			Object[] listenArray = {modelParam, nameListen, cl};
			sharedListenerMap.put(ifaceParam, listenArray);
		}
		else {
			addCausalityListener(modelParam, ifaceParam);
		}
	}

	private void removeParameterListeners(Parameter ifaceParam)
	{
		if (isdefaultIface) {
			Object[] listenArray = (Object[]) sharedListenerMap.get(ifaceParam);
			Parameter modelParam = (Parameter) listenArray[0];
			PropertyChangeListener nameListen = (PropertyChangeListener) listenArray[1];
			modelParam.removePropertyChangeListener(nameListen);
			CausalityChangeListener cl = (CausalityChangeListener) listenArray[2];
			Model m = getModel();
			m.removeCausalityChangeListener(modelParam, cl);
			sharedListenerMap.remove(ifaceParam);
		}
		else {
			removeCausalityListener(ifaceParam);
		}
	}

	private void addToContexts(Object mobj)
	{
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			modelview.addReference((ModelObject) mobj);
		}
		else if (!currentView.equals(DomeModelInterface.BUILD_VIEW)) {
			buildViewContext.addModelObjectReference((ModelObject) mobj);
		}
	}

	private void addToContexts(Collection mObjs)
	{
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			for (Iterator i = mObjs.iterator(); i.hasNext();) {
				ModelObject mobj = (ModelObject) i.next();
				modelview.addReference(mobj);
			}
		}
		else if (!currentView.equals(DomeModelInterface.BUILD_VIEW)) {
			buildViewContext.addModelObjectReferences(mObjs);
		}
	}

	//To take care of parameters in DomeCollection such as DomeList
	protected void objectDeletedAction(ModelObject mObj)
	{
		if (DomeCollectionUtils.isCollectionParameter(mObj)) {
			removeParameterListeners((Parameter) mObj);
		}
		super.objectDeletedAction(mObj);
	}

	public ModelObject newModelObject(String modelObjectType)
	{
		ModelObject mobj = null;
		//interface is being duplicated
		if (isDuplicateOperation) {
			mobj = super.newModelObject(modelObjectType);
		}
		else {
			currentInsertionIndex = getFilterItemCount();
			currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs

			if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
				mobj = modelview.newModelObject(modelObjectType);
			}
			else { //3 other views
				mobj = super.newModelObject(modelObjectType);
			}
			addToContexts(mobj);
		}
		return mobj;
	}

	public ModelObject newModelObject(String modelObjectType, int index)
	{
		if (index < 0 || index > getFilterItemCount())
			currentInsertionIndex = getFilterItemCount();
		else
			currentInsertionIndex = index;
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		ModelObject mobj = null;
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			mobj = modelview.newModelObject(modelObjectType);
		}
		else { //3 other views
			mobj = super.newModelObject(modelObjectType);
		}
		addToContexts(mobj);
		return mobj;
	}

	public ModelObject newModelObject(ModelObject modelObject)
	{
		ModelObject mobj = null;
		//interface is being duplicated
		if (isDuplicateOperation) {
			mobj = super.newModelObject(modelObject, true);
		}
		else {
			if (DomeCollectionUtils.isCollectionParameter(modelObject)) {
				ModelObject param = super.newModelObject(modelObject);
				if (isdefaultIface) { //in default interface
					//add listeners on parameter inside the DomeCollection
					addParameterListeners((Parameter) modelObject, (Parameter) param);
				}
				return param;
			}
			currentInsertionIndex = getFilterItemCount();
			currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
			if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
				mobj = modelview.newModelObject(modelObject);
			}
			else { //3 other views
				mobj = super.newModelObject(modelObject);
			}
			addToContexts(mobj);
		}
		return mobj;
	}

	public ModelObject newModelObject(ModelObject modelObject, int index)
	{
		if (index < 0 || index > getFilterItemCount())
			currentInsertionIndex = getFilterItemCount();
		else
			currentInsertionIndex = index;
		currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
		ModelObject mobj = null;
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			mobj = modelview.newModelObject(modelObject);
		}
		else { //3 other views
			mobj = super.newModelObject(modelObject);
		}
		addToContexts(mobj);
		return mobj;
	}

	public Collection newModelObjects(Collection mObjs)
	{
		if (!mObjs.isEmpty()) {
			Collection mobjs = null;
			if (isDuplicateOperation) {
				mobjs = super.newModelObjects(mObjs, true);
			}
			else {
				currentInsertionIndex = getFilterItemCount();
				currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs

				if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
					mobjs = modelview.newModelObjects(mObjs); // todo: add support for contexts; deep-copy?
				}
				else { //3 other views
					mobjs = super.newModelObjects(mObjs,true);      //TODO should the objects be deep copied?  this allows a user to add and map a relation in an interface - incorrect behavior
				}
				addToContexts(mobjs);
			}
			return mobjs;
		}
		return mObjs;
	}

	public Collection newModelObjects(Collection modelObjects, int index)
	{
		if (!modelObjects.isEmpty()) {
			if (index < 0 || index > getFilterItemCount())
				currentInsertionIndex = getFilterItemCount();
			else
				currentInsertionIndex = index;
			currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
			Collection mobjs = null;
			if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
				mobjs = modelview.newModelObjects(modelObjects);
			}
			else {  //3 other views
				mobjs = super.newModelObjects(modelObjects);
			}
			addToContexts(mobjs);
			return mobjs;
		}
		return modelObjects;
	}

	protected int getFilterItemCount()
	{
		CausalityStatus stat = getNewObjectCausality();
		if (stat == null) {
			return 0;
		}
		if (stat.equals(CausalityStatus.INDEPENDENT)) {
			return inputFilter.getItemCount();
		}
		else if (stat.equals(CausalityStatus.INDETERMINATE)) {
			return outputFilter.getItemCount();
		}
		else if (stat.equals(CausalityStatus.RESULT)) {
			return outputFilter.getItemCount();
		}
		else if (stat.equals(CausalityStatus.INTERMEDIATE)) {
			return indeterFilter.getItemCount();
		}
		return inputFilter.getItemCount();
	}

//TODO	//differenc between remove and delete is remove does not remove
//TODO	//mappings whereas delete does.  Otherwise both delete the
//TODO	//actual objects
//TODO store the mappings in remove and get back the mappings on paste
//right now the mappings are removed - easy solution
	public boolean removeModelObjects(Collection mObjects)
	{
		boolean ret = true;
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			modelview.removeObjectsAndListeners(mObjects);
		}
		else { //remove from other three views
			for (Iterator i = mObjects.iterator(); i.hasNext();) {
				removeCausalityListener(i.next());
			}
			removeModelObjectsFromBuildContext(mObjects);
			ret = modelObjects.removeAll(mObjects);
		}

//TODO replace the mapping removal code later - see comments above
		ConnectionMappingManager mgr = null;
		if (model instanceof DomeModel) {
			mgr = ((DomeModel) model).getMappingManager();
		}
		else if (model instanceof IntegrationProject) {
			mgr = ((IntegrationProject) model).getMappingManager();
		}
		for (Iterator i = mObjects.iterator(); i.hasNext();) {
			ModelObject obj = (ModelObject) i.next();
			if (obj instanceof Parameter) {
				mgr.removeAllMappings((Parameter) obj);
			} else if (obj instanceof Visualization) {
				mgr.removeAllMappings((Visualization) obj);
			}
			else if (obj instanceof Relation) {
				//remove connection between model and interface relation
				//done implicitly in removeAllMappings
//				mgr.removeConnection((Relation) obj);
				Collection relobjs = ((Relation) obj).getModelObjects();
				for (Iterator rl = relobjs.iterator(); rl.hasNext();) {
					ModelObject object = (ModelObject) rl.next();
					if (object instanceof Parameter) {
						mgr.removeAllMappings((Parameter) object);
					}
				}
			}
		}

		return ret;
	}

	public void deleteModelObjects(Collection mObjs)
	{
		if (isdefaultIface) {
			//to remove contents of DomeList in default interface when the DomeList
			//is deleted from the model
			for (Iterator i = mObjs.iterator(); i.hasNext();) {
				removeCausalityListener(i.next());
			}
			removeModelObjectsFromBuildContext(mObjs);
			super.deleteModelObjects(mObjs);
		}
		else {
			if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
				modelview.removeObjectsAndListeners(mObjs);
			}
			else {
				for (Iterator i = mObjs.iterator(); i.hasNext();) {
					removeCausalityListener(i.next());
				}
				removeModelObjectsFromBuildContext(mObjs);
				super.deleteModelObjects(mObjs);
			}
		}
		ConnectionMappingManager mgr = null;
		if (model instanceof DomeModel) {
			mgr = ((DomeModel) model).getMappingManager();
		}
		else if (model instanceof IntegrationProject) {
			mgr = ((IntegrationProject) model).getMappingManager();
		}
		for (Iterator i = mObjs.iterator(); i.hasNext();) {
			ModelObject obj = (ModelObject) i.next();
			if (obj instanceof Parameter) {
				mgr.removeAllMappings((Parameter) obj);
			} else if (obj instanceof Visualization) {
				mgr.removeAllMappings((Visualization) obj);
			}
			else if (obj instanceof Relation) {
				Collection relobjs = ((Relation) obj).getModelObjects();
				for (Iterator rl = relobjs.iterator(); rl.hasNext();) {
					ModelObject object = (ModelObject) rl.next();
					if (object instanceof Parameter) {
						mgr.removeAllMappings((Parameter) object);
					} else if (object instanceof Visualization) {
						mgr.removeAllMappings((Visualization) object);
					}
				}
			}
		}
	}

	public void removeMappingsAndConnectionsBeforeDelete()
	{
		ConnectionMappingManager mgr = null;
		if (model instanceof DomeModel) {
			mgr = ((DomeModel) model).getMappingManager();
		}
		else if (model instanceof IntegrationProject) {
			mgr = ((IntegrationProject) model).getMappingManager();
		}
		for (Iterator i = modelObjects.iterator(); i.hasNext();) {
			Object obj = i.next();
			if (obj instanceof Parameter) {
				//this will remove all param mappings
				//there are no relations in build or causal views
				mgr.removeAllMappings((Parameter) obj);
			} else if (obj instanceof Visualization) {
				//this will remove all param mappings
				//there are no relations in build or causal views
				mgr.removeAllMappings((Visualization) obj);
			}
		}
		modelview.removeMappingsAndConnections(mgr);
	}

	private void removeModelObjectsFromBuildContext(Collection mObjects)
	{
		for (Iterator i = mObjects.iterator(); i.hasNext();) {
			ModelObject mObject = (ModelObject) i.next();
			try {
				buildViewContext.removeModelObjectReference(mObject); //removes all parameters
			}
			catch (Exception ex) {
			}
		}
		//directly under build context
		Collection conObjs = buildViewContext.getModelObjectReferences();
		if (conObjs == null || conObjs.size() == 0) {
			return;
		}
		for (Iterator iter = mObjects.iterator(); iter.hasNext();) {
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
			if (obj instanceof Visualization && obj.equals(mobj)) {
				con.removeModelObjectReference((ModelObject) obj);
				break;
			}
		}
	}


	// mapping shortcuts support
	public Collection addAndMapModelObjects(Collection origModelObjects)
	{
		Collection objs = null;
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			objs = modelview.addAndMapModelObjects(origModelObjects);
		}
		else {
			for (Iterator iter = origModelObjects.iterator(); iter.hasNext();) {
				//todo: display a warning dialog
				Object obj = iter.next();
				if (obj instanceof Relation)
					iter.remove();
			}
			objs = newModelObjects(origModelObjects);
			mapModelObjects(origModelObjects, objs);
		}
		return objs;
	}

	public Collection addAndMapModelObjects(Collection origModelObjects, int index)
	{
		Collection objs = null;
		if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			objs = modelview.addAndMapModelObjects(origModelObjects);
		}
		else {
			for (Iterator iter = origModelObjects.iterator(); iter.hasNext();) {
				//todo: display a warning dialog
				Object obj = iter.next();
				if (obj instanceof Relation || obj instanceof Context)
					iter.remove();
			}
			objs = newModelObjects(origModelObjects, index);
			mapModelObjects(origModelObjects, objs);
		}
		return objs;
	}

	public void mapAndListenToModelParameter(Parameter modelParam, Parameter ifaceParam)
	{
		if (getModelViewObjects().contains(ifaceParam) || getModelViewObjects().contains(ifaceParam.getScope())) {
			ModelObjectScope modelScope = modelParam.getScope();
			ModelObjectScope ifaceScope = ifaceParam.getScope();
			if (modelScope instanceof Relation && ifaceScope instanceof Relation) {
				modelview.mapAndAddListeners(Collections.singletonList(modelScope),
				                             Collections.singletonList(ifaceScope),
				                             false);
			}
			else {
				modelview.mapAndAddListeners(Collections.singletonList(modelParam),
				                             Collections.singletonList(ifaceParam),
				                             false);
			}
		}
		else
			mapModelObjects(Collections.singletonList(modelParam), Collections.singletonList(ifaceParam));
	}

	protected void mapModelObjects(Collection parameters, Collection iterfaceParameters)
	{
		if (parameters.size() != iterfaceParameters.size()) {
			System.err.println("some objects not copied correctly -- no automapping");
			return;
		}
		ConnectionMappingManager mm = null;
		Model m = getModel();
		if (m instanceof DomeModel) {
			mm = ((DomeModel) m).getMappingManager();
		}
		else if (m instanceof IntegrationProject) {
			mm = ((IntegrationProject) m).getMappingManager();
		}
		Iterator interfaceParams = iterfaceParameters.iterator();
		Iterator params = parameters.iterator();
		List errors = new ArrayList();
		while (interfaceParams.hasNext()) {
			Object objrp = interfaceParams.next();
			Object objp = params.next();
			if (objrp instanceof Parameter && objp instanceof Parameter) {
				Parameter rp = (Parameter) objrp;
				Parameter p = (Parameter) objp;
				try {
					mm.addMapping(rp, p);
					addCausalityListener(p, rp); //for no-default interface only
				} catch (RuntimeException ex) {
					/**/System.out.println(ex);
					errors.add(ex);
				}
			} else if (objrp instanceof Visualization && objp instanceof Visualization) {
				Visualization rp = (Visualization) objrp;
				Visualization p = (Visualization) objp;
				try {
					mm.addMapping(rp, p);
					//*addCausalityListener(p, rp); //for no-default interface only
				} catch (RuntimeException ex) {
					/**/System.out.println(ex);
					errors.add(ex);
				}
			} else if (objrp instanceof Context && objp instanceof Context) {
				errors.addAll(mapContextContents(mm,(Context)objrp,(Context)objp));
			}
		}
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException(errors);
	}

	protected List mapContextContents(ConnectionMappingManager cmm, Context ifaceContext, Context modelContext) {
		Iterator ifaceContents = ifaceContext.getModelObjectReferences().iterator();
		Iterator modelContents = modelContext.getModelObjectReferences().iterator();
		ModelObject iObj, mObj;
		List errors = new ArrayList();
		while (modelContents.hasNext()) {
			iObj = (ModelObject)ifaceContents.next();
			mObj = (ModelObject)modelContents.next();
			if (iObj instanceof Parameter) {
				try {
					cmm.addMapping((Parameter)iObj, (Parameter)mObj);
					addCausalityListener(mObj, iObj); //for no-default interface only
				}
				catch (RuntimeException ex) {
					/**/System.out.println(ex);
					errors.add(ex);
				}
			} else if (iObj instanceof Context) {
				errors.addAll(mapContextContents(cmm,(Context)iObj,(Context)mObj));
			} else if (iObj instanceof Visualization) {
				try {
					cmm.addMapping((Visualization) iObj, (Visualization) mObj);
				}
				catch (RuntimeException ex) {
					/**/System.out.println(ex);
					errors.add(ex);
				}
			}
		}
		return errors;
	}

	// add BUILD_VIEW to list of views so it can be obtained via getView(viewName)
	// implementation assumes BUILD_VIEW does not change
	protected void createViews()
	{
		super.createViews(); // interface causality view & system causality view
		views.put(BUILD_VIEW, Collections.unmodifiableList(getBuildContext().getModelObjectReferences()));
	}

	public void save(String fileName) throws IOException
	{
		String oldFileName = this.fileName;
		this.fileName = fileName;
		saveFile(fileName);
		firePropertyChange(FILENAME, oldFileName, fileName);
	}

	protected void saveFile(String fileName) throws IOException
	{
		Document xmlDoc = createXmlDocument();

		if (version.compareTo(lastSavedVersion) == 1) { // version is newer
			save(xmlDoc, fileName);
			lastSavedVersion = version.duplicate();
		}
		else { // change version if content is different
			if (hasChanged(xmlDoc)) {
				save(xmlDoc, fileName);
			}
			else { // increment version
				version.revSaveVersion();
				Element versionNode = (Element) xmlDoc.selectSingleNode("/model/modelinfo/version");
				if (versionNode != null)
					versionNode.setText(version.toString());
				xmlDoc = createXmlDocument();
				save(xmlDoc, fileName);
				lastSavedVersion = version.duplicate();
			}
		}

		//save the mappings in a separate file
		Element mappingRootElement = headerToXmlElement();
		addXmlContent(mappingRootElement); //add interface version info
		Element mappingElement = this.createMappingElement();
		mappingRootElement.add(mappingElement);

		//save additional lookup info for default interface
		if (isdefaultIface) {
			Element lookupElement = this.createDefaultLookupElement();
			mappingRootElement.add(lookupElement);
		}

		int extensionIndex = 0;
		if (model instanceof IntegrationProject) {
			extensionIndex = fileName.indexOf(".dpi");
		}
		else {
			extensionIndex = fileName.indexOf(".dmi");
		}
		StringBuffer mappingFilename = new StringBuffer(fileName.substring(0, extensionIndex));
		mappingFilename.append("-mappings");
		Document mappingdoc = DocumentFactory.getInstance().createDocument();
		mappingdoc.add(mappingRootElement);
		save(mappingdoc, mappingFilename.toString());
	}

	protected void save(Document xmlDoc, String fileName) throws IOException
	{
		XMLUtils.writeToFile(xmlDoc, fileName);
		Document doc = createXmlDocument();
		Element root = doc.getRootElement();
		Node viewsNode = root.selectSingleNode("/" + getXmlTag() + "/" + DomeModelInterface.VIEWS);
		root.remove(viewsNode);
		lastSavedXml = doc.asXML();
	}

	public boolean isItemOfSystemCausality(Object obj, CausalityStatus cause)
	{
		if (!(obj instanceof Parameter))
			return false;
		Hashtable parameterSystemCausality = null; // key=parameter id, value=CausalityStatus.name

		if (!isSystemCausalityInitialized) { // calculate the system causality one once per view switch
			Model model = getModel();
			IntegrationProjectBuilder project;
			ConnectionMappingManager mgr;
			if (model instanceof IntegrationProject) {
				project = (IntegrationProjectBuilder) model;
				mgr = project.getMappingManager();
			}
			else {
				AbstractDomeModel aModel = (AbstractDomeModel) model;
				mgr = aModel.getMappingManager();
				project = (IntegrationProjectBuilder) aModel.getIntegrationProject();
			}
			Map paramMap = getInterfaceParamToModelParamMap();
			if (project == null) // regular standalone model
				parameterSystemCausality = ((DomeModelBuilder) model).getSystemCausality(paramMap);
			else
				parameterSystemCausality = project.getSystemCausality(paramMap);
			isSystemCausalityInitialized = true;
			//call setdata on sys causality manager
			((ModelInterfaceBuildCausalityManager) systemCausalityManager).setData(parameterSystemCausality);
		}
		CausalityStatus stat = systemCausalityManager.getCausality(obj);
		return (cause.equals(stat));
	}

	public Collection addItemsToFilterListener(DListListener l)
	{
		systemCausalityList.addDListListener(l);
		return Collections.unmodifiableList(systemCausalityList);
	}

	public Collection removeItemsToFilterListener(DListListener l)
	{
		systemCausalityList.removeDListListener(l);
		return Collections.unmodifiableList(systemCausalityList);
	}

	protected void createSystemCausalityFilters()
	{
		systemCausalityList = new DArrayList();
		systemCausalityManager = new ModelInterfaceBuildCausalityManager();
		independentFilter = new SystemCausalityFilter(CausalityStatus.INDEPENDENT);
		intermediateFilter = new SystemCausalityFilter(CausalityStatus.INTERMEDIATE);
		resultFilter = new SystemCausalityFilter(CausalityStatus.RESULT);
	}

	protected class ModelInterfaceBuildCausalityManager
	        extends AbstractModelObjectScope.AbstractInternalCausalityManager
	{

		public ModelInterfaceBuildCausalityManager()
		{
			causalityChangeListeners = new CausalityChangeSupport(ModelInterfaceBuilder.this);
		}

		public ModelInterfaceBuildCausalityManager(Element xmlElement)
		{
			super(xmlElement);
		}

		protected CausalityStatus getInitialCausality(Object obj)
		{
			return getNewObjectCausality();
		}

		public void setData(Hashtable causal)
		{
			causalityLists.put(CausalityStatus.INDEPENDENT, new ArrayList()); // key = CausalityStatus, value=list of objects
			causalityLists.put(CausalityStatus.INTERMEDIATE, new ArrayList());
			causalityLists.put(CausalityStatus.RESULT, new ArrayList());
			causalityLists.put(CausalityStatus.INDETERMINATE, new ArrayList());
			for (Iterator iterator = causal.keySet().iterator(); iterator.hasNext();) {
				Parameter param = (Parameter) iterator.next();
				if ((causal.get(param)).equals(CausalityStatus.INDEPENDENT.toString())) {
					objectCausality.put(param, CausalityStatus.INDEPENDENT); // key=object; value=CausalityStatus
					((List) causalityLists.get(CausalityStatus.INDEPENDENT)).add(param);
				}
				else if ((causal.get(param)).equals(CausalityStatus.INTERMEDIATE.toString())) {
					objectCausality.put(param, CausalityStatus.INTERMEDIATE);
					((List) causalityLists.get(CausalityStatus.INTERMEDIATE)).add(param);
				}
				else if ((causal.get(param)).equals(CausalityStatus.RESULT.toString())) {
					objectCausality.put(param, CausalityStatus.RESULT);
					((List) causalityLists.get(CausalityStatus.RESULT)).add(param);
				}
				else if ((causal.get(param)).equals(CausalityStatus.INDETERMINATE.toString())) {
					objectCausality.put(param, CausalityStatus.INDETERMINATE);
					((List) causalityLists.get(CausalityStatus.INDETERMINATE)).add(param);
				}
			}
		}
	}

	public void setCurrentView(String view)
	{
		super.setCurrentView(view);
		if (currentView == DomeModelInterface.SYSTEM_CAUSALITY_VIEW) {
			populateSystemCausalityList();
		}
	}

	private void populateSystemCausalityList()
	{
		Collection params = getModelObjectParameters();
		systemCausalityList.clear();
		systemCausalityList.addAll(params);
		isSystemCausalityInitialized = false; // needs to recalculate the whole system causality when the view is reselected
	}

	//to load only header info of default interfaces which fail to load up for some reason
	//This way the Id can be reused
	public void parseHeader(Element xml) {
		this.parseHeaderElement(xml);
	}
}