package mit.cadlab.dome3.network.server;

import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.RuntimeUtils;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.functions.MessageFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterRuntime;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer;
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.DomePropertyChangeEvent;

import java.util.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 28, 2003
 * Time: 2:22:04 PM
 * To change this template use Options | File Templates.
 */
public class ModelInterfaceListener
{
	HashMap objectsOfInterest = new HashMap(); // maps parameters to session ids of interested clients
	AggregatorMap subscriptionListeners = new AggregatorMap(); // maps parameters to session ids of subscription interfaces clients
	HashMap objectCompoundIds = new HashMap(); // maps parameters to their compound ids
	ValueChangeListener valueChangeListener = new ValueChangeListener();


	public ModelInterfaceListener()
	{
	}

	public void addListener(String sessionId, InterfaceParameterRuntime p, CompoundId objectId) {
		addListener(sessionId, p, objectId, false);
	}

	public void addListener(String sessionId, InterfaceParameterRuntime p, CompoundId objectId, boolean isProjectResource)
	{
		if (!objectCompoundIds.containsKey(p))
			p.addClientListener(valueChangeListener); // just add one per parameter to avoid duplicate messages!

		if (isProjectResource) {
			subscriptionListeners.put(p, sessionId);
			objectCompoundIds.put(p, objectId.toString());
			return;
		}
		// get user associated with this object
		ArrayList userList = (ArrayList) objectsOfInterest.get(p);
		if (userList == null) {
			// create the user list
			userList = new ArrayList();
			objectsOfInterest.put(p, userList);
			objectCompoundIds.put(p, objectId.toString());
		}

		// add the sessionId to the user list
		if (!userList.contains(sessionId))
			userList.add(sessionId);
	}

	private void removeListener(InterfaceParameterRuntime p, boolean isProjectResource) {
		p.clearListeners();
		if (isProjectResource)
			subscriptionListeners.remove(p);
		objectCompoundIds.remove(p);
		objectsOfInterest.remove(p);
	}

	public void addListeners(String sessionId, ModelInterfaceRuntimeServer iface, CompoundId objectCompoundId, boolean isProjectResource)
	{
//TODO add listeners on parameters in contexts
		// add listeners to interface objects
		Map ifaceObjMap = iface.getInterfaceObjectsFlatMap();
		for (Iterator mObjIter = ifaceObjMap.keySet().iterator(); mObjIter.hasNext();) {
			Id id = (Id) mObjIter.next();
			ModelObject mObj = (ModelObject) ifaceObjMap.get(id);
			if (mObj instanceof InterfaceParameterRuntime) {
				InterfaceParameterRuntime p = (InterfaceParameterRuntime) mObj;
				objectCompoundId.setDataObjectStaticId(p.getId().toString());
				objectCompoundId.setDataObjectRuntimeId(p.getId().toString());
				addListener(sessionId, p, objectCompoundId, isProjectResource);
			}
		}

		// object id is no longer needed -- discard it so it doesn't get passed back to the client
		objectCompoundId.setDataObjectStaticId((Id) null);
	}

    public void addListeners(String sessionId, mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer iface, CompoundId objectCompoundId)
    {
        Map ifaceObjMap = iface.getInterfaceObjectsFlatMap();
        for (Iterator mObjIter = ifaceObjMap.keySet().iterator(); mObjIter.hasNext();)
        {
            Id id = (Id) mObjIter.next();
            ModelObject mObj = (ModelObject) ifaceObjMap.get(id);
            if (mObj instanceof InterfaceParameterRuntime)
            {
                InterfaceParameterRuntime p = (InterfaceParameterRuntime) mObj;
                objectCompoundId.setDataObjectStaticId(p.getId().toString());
                objectCompoundId.setDataObjectRuntimeId(p.getId().toString());
                addListener(sessionId, p, objectCompoundId);
            }
        }

        Id nullId = null;
        objectCompoundId.setDataObjectStaticId(nullId);
    }

	public void removeListeners(ModelInterfaceRuntimeServer iface, boolean isProjectResource) {
		Map ifaceObjMap = iface.getInterfaceObjectsFlatMap();
		for (Iterator mObjIter = ifaceObjMap.keySet().iterator(); mObjIter.hasNext();) {
			Id id = (Id) mObjIter.next();
			ModelObject mObj = (ModelObject) ifaceObjMap.get(id);
			if (mObj instanceof InterfaceParameterRuntime) {
				removeListener((InterfaceParameterRuntime) mObj, isProjectResource);
			}
		}
	}

	public void removeListeners(mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer iface) {
		Map ifaceObjMap = iface.getInterfaceObjectsFlatMap();
		for (Iterator mObjIter = ifaceObjMap.keySet().iterator(); mObjIter.hasNext();) {
			Id id = (Id) mObjIter.next();
			ModelObject mObj = (ModelObject) ifaceObjMap.get(id);
			if (mObj instanceof InterfaceParameterRuntime) {
				removeListener((InterfaceParameterRuntime) mObj, false);
			}
		}
	}

	private class ValueChangeListener extends DataObjectChangeListener
	{
		public ValueChangeListener()
		{
			super(null);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			PropertyChangeEvent event = e.getEvent();
			String property = event.getPropertyName();
			Object newValue = event.getNewValue();
			if (property.equals(DataObject.VALUE) || property.equals(EnumerationData.LASTSELECTION))
				valueChanged((InterfaceParameterRuntime) e.getSource(), newValue);
			else if (property.equals(Parameter.VALUE_STATUS)) {
				Integer eventId = ((DomePropertyChangeEvent) event).getEventId();
				statusChanged((InterfaceParameterRuntime) e.getSource(), (String) newValue, eventId);
			}
		}
	}

	private void valueChanged(InterfaceParameterRuntime source, Object newValue)
	{
		DSet userList = new DSet();
		Vector v = RuntimeUtils.objectToVector(newValue);

		// subscription listeners do not care about values from InterfaceModelView
		Object sourceScope = source.getScope();
		if ((sourceScope instanceof ModelInterfaceRuntimeServer) ||
		        ((sourceScope instanceof ModelObject) && ((ModelObject)sourceScope).getScope() instanceof ModelInterfaceRuntimeServer)) {
			List subListeners = (List) subscriptionListeners.get(source);
			if (subListeners != null)
				userList.addAll(subListeners);
		}

		ArrayList clientList = (ArrayList) objectsOfInterest.get(source);
		if (clientList != null)
			userList.addAll(clientList);
		String objectId = (String) objectCompoundIds.get(source);

		if (!userList.isEmpty()) {
			new Thread(new MessageFunctions.SendMessageThread(RuntimeConstants.MESSAGE_ITEM_VALUE_CHANGED,
			                                                  userList,
			                                                  Vectors.create(objectId, v))).start();
		}
	}

	private void statusChanged(InterfaceParameterRuntime source, String newStatus, Integer eventId)
	{
		ArrayList userList = (ArrayList) objectsOfInterest.get(source);
		String objectId = (String) objectCompoundIds.get(source);

		if (userList != null) {
			new Thread(new MessageFunctions.SendMessageThread(RuntimeConstants.MESSAGE_ITEM_STATUS_CHANGED,
			                                                  userList,
			                                                  Vectors.create(objectId, eventId, newStatus))).start();
		}
	}

}
