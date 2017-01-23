// ProjectIntegrationModelInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.xml.XMLSupport;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * This class holds information about project integration models.  An iModel can subscribe to
 * interfaces of other imodels in the same project.  This information is stored in the
 * <code>subscribers</code> hashmap.
 */
public class ProjectIntegrationModelInfo extends DomeJavaBean implements XMLSupport, ViewSupport, InfoInterface
{

	public static final String XML_TAG = "imodel";

	// property names
	public static final String NAME = "name";
	public static final String SUBSCRIBERS_CHANGED = "iModel subscribers changed"; // add, remove, or namechange
	protected DomeModel model = null;
	protected String id = null;
	protected String name = "";
	protected HashMap subscribers = new HashMap(); // key is subscriber iModel instance; value is list of interfaces
	protected boolean isSubscribed = false;
	protected PropertyChangeListener iModelNameListener = new IModelNameListener();
	protected PropertyChangeListener iModelDeletionListener = new IModelDeletionListener();

	public ProjectIntegrationModelInfo(DomeModel model)
	{
		if (model == null)
			throw new IllegalArgumentException("ProjectIntegrationModelInfo constructor - null model");
		id = model.getId().getIdString();
		setModel(model);
	}

	public ProjectIntegrationModelInfo(ProjectIntegrationModelInfo modelinfo)
	{
		id = UUIDGenerator.create();
		DomeModel mod = new DomeModelBuilder(new Id(id), modelinfo.getModel(), true);
		setModel(mod);
	}

	public ProjectIntegrationModelInfo(Element xmlDescription)
	{
		id = xmlDescription.attributeValue("id");
		if (id == null)
			throw new IllegalArgumentException("no xml id: " + xmlDescription.asXML());
		name = xmlDescription.attributeValue("name");
		if (name == null)
			name = "";
	}

	public DomeModel getModel()
	{
		return model;
	}

	public void setModel(DomeModel m)
	{
		if (model != null && m == null) { // clear model when project is killed
			model = null;
			return;
		}
		if (model != null)
			throw new UnsupportedOperationException("ProjectIntegrationModelInfo.setModel - model is already set");
		if (m == null)
			throw new IllegalArgumentException("ProjectIntegrationModelInfo.setModel - null model");
		if (!m.getId().toString().equals(id))
			throw new UnsupportedOperationException("ProjectIntegrationModelInfo.setModel - model id does not match info id");
		this.model = m;
		name = model.getName();
		model.addPropertyChangeListener(DomeModel.NAME, new NameListener()
		{
			public void nameChanged(String newName)
			{
				setName(newName, false);
			}
		});
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String newName)
	{
		setName(newName, true);
	}

	protected void setName(String newName, boolean setModelName)
	{
		if (setModelName)
			model.setName(newName); // set this name when name comes back via property change listener
		else {
			if (newName == null || newName.trim().equals(""))
				return;
			String oldName = name;
			this.name = newName.trim();
			firePropertyChange(NAME, oldName, name);
		}
	}

	// ViewSupport interface

	public List getView()
	{
		return ((AbstractDomeModel) model).getModelInterfacesManager().getView();
	}

	public void addViewListener(DListListener l)
	{
		((AbstractDomeModel) model).getModelInterfacesManager().addViewListener(l);
	}

	public void removeViewListener(DListListener l)
	{
		((AbstractDomeModel) model).getModelInterfacesManager().removeViewListener(l);
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(getXmlTag());
		xml.addAttribute("id", id);
		xml.addAttribute("name", name);
		return xml;
	}

	public void addSubscriber(DomeModelBuilder model, String interfaceId)
	{
		DSet interfaceIds = (DSet) subscribers.get(model);
		if (interfaceIds == null) {
			interfaceIds = new DSet();
			subscribers.put(model, interfaceIds);
			model.addPropertyChangeListener(DomeObject.NAME, iModelNameListener);
			model.addPropertyChangeListener(iModelDeletionListener);
		}
		if (interfaceIds.add(interfaceId)) {
			if (!isSubscribed) {
				isSubscribed = true;
			}
			model.subscribeResource(this);
//			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	public void addSubscriber(DomeModelBuilder model, Collection ifaceIds)
	{
		DSet interfaceIds = (DSet) subscribers.get(model);
		if (interfaceIds == null) {
			interfaceIds = new DSet();
			subscribers.put(model, interfaceIds);
			model.addPropertyChangeListener(DomeObject.NAME, iModelNameListener);
			model.addPropertyChangeListener(iModelDeletionListener);
		}
		if (interfaceIds.addAll(ifaceIds)) {
			if (!isSubscribed) {
				isSubscribed = true;
			}
			model.subscribeResource(this);
//			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	//unused method
	public void removeSubscriber(DomeModelBuilder model, String interfaceId)
	{
		DSet interfaceIds = (DSet) subscribers.get(model);
		if (interfaceIds == null)
			return;
		if (interfaceIds.remove(interfaceId)) {
			if (interfaceIds.isEmpty()) {
				model.removePropertyChangeListener(DomeObject.NAME, iModelNameListener);
				model.removePropertyChangeListener(iModelDeletionListener);
				subscribers.remove(model);
			}
			if (subscribers.size() == 0) {
				isSubscribed = false;
			}
			model.unsubscribeResource(this);
//			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	class IModelNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	class IModelDeletionListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(DomeModel.IMODEL_DELETED)) {
				subscribers.remove(evt.getOldValue());
				if (subscribers.size() == 0) {
					isSubscribed = false;
				}
			}
		}
	}

	//retruns lists of subscribed interfaces
	public List getSubscribedInterfaceIds(DomeModel model)
	{
		DSet ifaceIds = new DSet();
		Collection values = (Collection) subscribers.get(model);
		Iterator iter = values.iterator();
		while (iter.hasNext()) {
			ifaceIds.add(iter.next());
		}
		return ifaceIds;
	}

	public boolean isSubscribed() {
		return isSubscribed;
	}

	public void removeAllSubscriptions()
	{
		for (Iterator i = subscribers.keySet().iterator(); i.hasNext();) {
			DomeModelBuilder model = (DomeModelBuilder) i.next();
			List interfaces = (List) subscribers.get(model);
			for (Iterator j = interfaces.iterator(); j.hasNext();) {
				String ifaceId = (String) j.next();
				Collection ids = model.getSubscriptionModelObjectIds(ifaceId);
				for (Iterator k = ids.iterator(); k.hasNext();) {
					Id subScriptionObjectId = (Id)k.next();
					ModelObject subScriptionObject = model.getModelObjectById(subScriptionObjectId);
					if (subScriptionObject != null) {
						model.deleteModelObject(subScriptionObject);
					}
				}
			}
		}
		isSubscribed = false;
		firePropertyChange(RESOURCE_REMOVED, this, null);
	}

 }
