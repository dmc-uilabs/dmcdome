// InterfaceParameterClientChangeSupport.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class manages dispatching change messages to server listeners for interface parameters.
 * There is typically one server listener on each interface parameter.
 * There are typically one client listeners, the interface (which will forward changes to server)
 *   (since GUI clients use JavaBean architecture).
 * Changes from server parameter are dispatched to clients.
 * Changes from clients are dipatched to server.
 */
public class InterfaceParameterClientChangeSupport implements java.io.Serializable
{

	// notification modes
	public static final int FROM_CLIENT = 0;
	public static final int FROM_SERVER = 1;

	protected Parameter source;
	// should be only one serverParameterListener...but make it a HashSet to allow for unforseen circumstances
	protected Set serverParameterListeners = new HashSet(); // listeners from things which will dispatch to server
	protected Set clientListeners = new HashSet();
	protected int notificationMode = FROM_CLIENT;

	public InterfaceParameterClientChangeSupport(Parameter interfaceParam)
	{
		if (interfaceParam == null)
			throw new NullPointerException("InterfaceParameterClientChangeSupport constructor - null interfaceParam");
		source = interfaceParam;
	}

	public void setServerParameterChanged()
	{
		notificationMode = FROM_SERVER;
	}

	public void clearServerParameterChanged()
	{
		notificationMode = FROM_CLIENT;
	}

	public synchronized void addServerParameterListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		serverParameterListeners.add(listener);
	}

	public synchronized void removeServerParameterListener(DataObjectChangeListener listener)
	{
		if (listener == null) return;
		serverParameterListeners.remove(listener);
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

	public synchronized void fireDataObjectChange(PropertyChangeEvent propertyChangeEvent)
	{
		// todo: remove debugging print statements
		DataObjectChangeEvent event = new DataObjectChangeEvent(source, propertyChangeEvent);
		if (notificationMode == FROM_CLIENT) { // send to server
			//**Debug.trace(Debug.ALL, "Change from gui for ClientParameter " + source.getName() + ": " + propertyChangeEvent.getNewValue());
			if (!serverParameterListeners.isEmpty()) {
				Iterator it = serverParameterListeners.iterator();
				while (it.hasNext()) {
					try {
						((DataObjectChangeListener) it.next()).dataObjectChanged(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (notificationMode == FROM_SERVER) {
			//**Debug.trace(Debug.ALL, "Change from server for ClientParameter " + source.getName() + ": " + propertyChangeEvent.getNewValue());
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
