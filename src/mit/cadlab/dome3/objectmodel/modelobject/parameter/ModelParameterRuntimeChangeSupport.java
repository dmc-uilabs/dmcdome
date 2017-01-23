// ModelParameterRuntimeChangeSupport.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.objectmodel.util.solving.Parameters;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class manages dispatching change messages to runtime listeners for model parameters.
 * All changes to the model parameter are dispatched to the model solving queue,
 *  subscriptions, and interfaces.
 */
public class ModelParameterRuntimeChangeSupport implements java.io.Serializable
{
	// notification modes
	public static final int FROM_MODEL_OR_INTERFACE = 0;
	public static final int FROM_SERVER = 1;

	protected int notificationMode = FROM_MODEL_OR_INTERFACE;
	protected Parameter source;
	protected Parameter cause = null;
	protected Set changeListener = new HashSet(); // listeners within the model (e.g. latches)
	protected Set interfaceListeners = new HashSet();
	protected DataObjectChangeListener queueListener; // listener from the model solving queue
	protected DataObjectChangeListener subscriptionInterfaceListener; // listener from the subscription interface

	public ModelParameterRuntimeChangeSupport(Parameter modelParam)
	{
		if (modelParam == null)
			throw new NullPointerException("ModelParameterRuntimeChangeSupport constructor - null modelParam");
		source = modelParam;
	}

	public void setServerParameterChanged()
	{
		notificationMode = FROM_SERVER;
	}

	public void clearServerParameterChanged()
	{
		notificationMode = FROM_MODEL_OR_INTERFACE;
	}

	public synchronized void setChangeTrigger(Parameter cause)
	{
		this.cause = cause;
	}

	public synchronized void clearChangeTrigger()
	{
		this.cause = null;
	}

	public synchronized void addModelQueueListener(DataObjectChangeListener mqListener)
	{
		queueListener = mqListener;
	}

	public synchronized void addSubscriptionInterfaceListener(DataObjectChangeListener mqListener)
	{
		subscriptionInterfaceListener = mqListener;
	}

	public synchronized void removeModelQueueListener()
	{
		queueListener = null;
	}

	public synchronized void addChangeListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		changeListener.add(listener);
	}

	public synchronized void removeChangeListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		changeListener.remove(listener);
	}

	public synchronized void addInterfaceListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		interfaceListeners.add(listener);
	}

	public synchronized void removeInterfaceListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		interfaceListeners.remove(listener);
	}

    /**
     * added to fix memory leakage problem (server side)
     */
	public synchronized void clearInterfaceListener()
	{
		interfaceListeners.clear();
	}


	public synchronized void fireDataObjectChange(PropertyChangeEvent propertyChangeEvent)
	{
		if (queueListener != null && !Parameter.VALUE_STATUS.equals(propertyChangeEvent.getPropertyName())) {
			queueListener.dataObjectChanged(new DataObjectChangeEvent(source, cause, propertyChangeEvent));
			if (!changeListener.isEmpty()) {
				Iterator it = changeListener.iterator();
				while (it.hasNext()) {
					try {
						((DataObjectChangeListener) it.next()).dataObjectChanged(new DataObjectChangeEvent(source, cause, propertyChangeEvent));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (notificationMode == FROM_MODEL_OR_INTERFACE) {
			if (subscriptionInterfaceListener != null && !Parameter.VALUE_STATUS.equals(propertyChangeEvent.getPropertyName()))
				subscriptionInterfaceListener.dataObjectChanged(new DataObjectChangeEvent(source, cause, propertyChangeEvent));
			if (Parameters.isSubscriptionInput(source) && !Parameter.VALUE_STATUS.equals(propertyChangeEvent.getPropertyName()))
				return; // do not propagate values from mapping changes to subscriptions or clients; wait for confirmation from resource
		}
		if (!changeListener.isEmpty() || !interfaceListeners.isEmpty()) {
			DataObjectChangeEvent event = new DataObjectChangeEvent(source, propertyChangeEvent);
			// send to subscriptions
//			if (!subscriptionListeners.isEmpty()) {
//				Iterator it = subscriptionListeners.iterator();
//				while (it.hasNext()) {
//					try {
//						((DataObjectChangeListener) it.next()).dataObjectChanged(event);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
			// send to interfaces
			if (!interfaceListeners.isEmpty()) {
				Iterator it = interfaceListeners.iterator();
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
