// InterfaceParameterRuntimeChangeSupport.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.util.DomePropertyChangeEvent;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class manages dispatching change messages to runtime listeners for interface parameters.
 * There is one model parameter mapped to each interface parameter.
 * There can be any number of subscriptions and clients to each interface parameter.
 * Changes from model parameter are dispatched to subscriptions and clients.
 * Changes from subscriptions and clients are dipatched to model.
 */
public class InterfaceParameterRuntimeChangeSupport implements java.io.Serializable
{

	// notification modes
	public static final int FROM_SUBSCRIPTION_OR_CLIENT = 0;
	public static final int FROM_MODEL_PARAMETER = 1;

	protected Parameter source;
	protected DataObjectChangeListener modelParameterListener; // listener from the model parameter
	protected Set subscriptionListeners = new HashSet();
	protected Set clientListeners = new HashSet();
	protected int notificationMode = FROM_SUBSCRIPTION_OR_CLIENT;

	public InterfaceParameterRuntimeChangeSupport(Parameter interfaceParam)
	{
		if (interfaceParam == null)
			throw new NullPointerException("InterfaceParameterRuntimeChangeSupport constructor - null interfaceParam");
		source = interfaceParam;
	}

	public void setModelParameterChanged()
	{
		notificationMode = FROM_MODEL_PARAMETER;
	}

	public void clearModelParameterChanged()
	{
		notificationMode = FROM_SUBSCRIPTION_OR_CLIENT;
	}

	public synchronized void addModelParameterListener(DataObjectChangeListener mParamListener)
	{
		modelParameterListener = mParamListener;
	}

	public synchronized void removeModelParameterListener()
	{
		modelParameterListener = null;
	}

	public synchronized void addSubscriptionListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		subscriptionListeners.add(listener);
	}

	public synchronized void removeSubscriptionListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		subscriptionListeners.remove(listener);
	}

	public synchronized void addClientListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		clientListeners.add(listener);
	}

	public synchronized void removeClientListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		clientListeners.remove(listener);
	}

	public synchronized void clearListeners() {
		clientListeners.clear();
		subscriptionListeners.clear();
	}

	public synchronized void fireDataObjectChange(PropertyChangeEvent propertyChangeEvent)
	{
		// todo: remove debugging print statements
		DataObjectChangeEvent event = new DataObjectChangeEvent(source, propertyChangeEvent);
		if (notificationMode == FROM_SUBSCRIPTION_OR_CLIENT) { // send to model
			//**Debug.trace(Debug.ALL,"Change from client for InterfaceParameter "+source.getName()+": "+propertyChangeEvent.getNewValue());
			if (modelParameterListener != null)
				modelParameterListener.dataObjectChanged(event);
		} else if (notificationMode == FROM_MODEL_PARAMETER) {
			if (source.getCurrentDataObject() instanceof DomeFile) { // make sure event includes file content
				if (propertyChangeEvent.getPropertyName().equals(DomeFile.VALUE) && propertyChangeEvent.getNewValue() == null) {
					propertyChangeEvent = new DomePropertyChangeEvent(propertyChangeEvent.getSource(),
					                                                  propertyChangeEvent.getPropertyName(), propertyChangeEvent.getOldValue(),
					                                                  source.getCurrentDataObject().getValuesForXmlRpcUse(),
					                                                  (propertyChangeEvent instanceof DomePropertyChangeEvent) ?
					                                                  ((DomePropertyChangeEvent) propertyChangeEvent).getEventId() : new Integer(0));
					event = new DataObjectChangeEvent(source, propertyChangeEvent);
				}
			}
			//**Debug.trace(Debug.ALL, "Change from model for InterfaceParameter " + source.getName() + ": " + propertyChangeEvent.getNewValue());
			// send to subscriptions
			if (!subscriptionListeners.isEmpty()) {
				Iterator it = subscriptionListeners.iterator();
				while (it.hasNext()) {
					try {
						((DataObjectChangeListener) it.next()).dataObjectChanged(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// sent to clients
			if (!clientListeners.isEmpty()) {
				Iterator it = clientListeners.iterator();
				while (it.hasNext()) {
					try {
						((DataObjectChangeListener) it.next()).dataObjectChanged(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
