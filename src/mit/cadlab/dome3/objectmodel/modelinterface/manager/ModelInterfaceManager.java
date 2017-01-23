// ModelInterfaceManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelinterface.manager;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

abstract public class ModelInterfaceManager extends AbstractDomeObject implements ModelComponent, ViewSupport
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("ModelInterfaceManager");
	public static final String XML_TAG = "modelInterfaces";
	protected static final String slash = System.getProperty("file.separator");

	protected Model model;
	protected DArrayList interfaces;
	protected HashMap interfacesById; // key=id; value=interface
	protected String modelFileName = null;
	protected HashMap interfaceMappingFileMap;

	public ModelInterfaceManager(Id id, Model model)
	{
		super(id);
		this.model = model;
		interfaces = new InterfacesList();
		interfacesById = new HashMap();
	}

	public Collection getInterfaces()
	{
		return Collections.unmodifiableCollection(interfaces);
	}

	public boolean isEmpty()
	{
		return interfaces.isEmpty();
	}

	public int countInterfaces()
	{
		return interfaces.size();
	}

	/**
	 * returns interface for id specified
	 * @param id - may be runtime or static id
	 * @return
	 */
	public ModelInterface getById(String id)
	{
		return (ModelInterface) interfacesById.get(id);
	}

	public ModelInterface getInterface(int index)
	{
		return (ModelInterface) interfaces.get(index);
	}

	public Model getModel()
	{
		return model;
	}

	public void addInterfacesListener(DListListener l)
	{
		interfaces.addDListListener(l);
	}

	public void removeInterfacesListener(DListListener l)
	{
		interfaces.removeDListListener(l);
	}

	class InterfacesList extends DArrayList
	{
		protected boolean addHookBefore(Object obj)
		{
			return !contains(obj) && (obj instanceof ModelInterface);
		}

		protected void addHookAfter(Object obj)
		{
			interfacesById.put(((ModelInterface) obj).getId().getIdString(), obj);
			if (obj instanceof AbstractModelInterfaceRuntime) {
				CompoundId cId = ((AbstractModelInterfaceRuntime) obj).getRuntimeId();
				interfacesById.put(cId.getInterfaceRuntimeId(), obj);
				String staticId = cId.getInterfaceStaticId();
				if (staticId == null)
					System.err.println("ModelInterfaceManager warning - no static id for " + Names.getName(obj));
				else
					interfacesById.put(staticId, obj);
			}
		}

		protected void removeHookAfter(Object obj)
		{
			interfacesById.remove(((ModelInterface) obj).getId().getIdString());
			if (obj instanceof AbstractModelInterfaceRuntime) {
				CompoundId cId = ((AbstractModelInterfaceRuntime) obj).getRuntimeId();
				interfacesById.remove(cId.getInterfaceRuntimeId());
				interfacesById.remove(cId.getInterfaceStaticId());
			}
		}
	}

	// ViewSupport interface
	public List getView()
	{
		return Collections.unmodifiableList(interfaces);
	}

	public void addViewListener(DListListener l)
	{
		interfaces.addDListListener(l);
	}

	public void removeViewListener(DListListener l)
	{
		interfaces.removeDListListener(l);
	}

	public boolean hasChanged()
	{
		for (Iterator iter = interfaces.iterator(); iter.hasNext();) {
			ModelInterfaceBuilder iface = (ModelInterfaceBuilder) iter.next();
			if (iface.hasChanged())
				return true;
		}
		return false;
	}

	public TypeInfo getTypeInfo()
	{
		return TYPE_INFO;
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

    /**
     * added to invoke cleanup() to prevent memory leakage problems
     */
	public void cleanup() {
        Debug.trace(Debug.ALL, "interface '" + getName() + "' cleaned up ");

        for (Iterator i = interfaces.iterator(); i.hasNext(); ) {
            ModelInterface aInterface = (ModelInterface) i.next();
            aInterface.cleanup();
        }

        interfaces.clear();
	}
}