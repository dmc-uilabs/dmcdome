// DomePropertyChangeSupport.java
package mit.cadlab.dome3.util;

import mit.cadlab.dome3.objectmodel.util.Names;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is similar to java.beans.PropertyChangeSupport and
 * javax.swing.event.SwingPropertyChangeSupport.
 * Rewritten to remove duplicate listener entries and to try
 * to be a bit faster.
 * Class is not thread-safe (like SwingPropertyChangeSupport).
 */
public class DomePropertyChangeSupport implements java.io.Serializable
{

	protected Object source;
	protected Integer nextEventId = new Integer(1);

	protected Vector genericPropertyChangeListeners;
	protected Hashtable namedPropertyChangeListeners;
	protected transient PropertyChangeListener[] genericChangeListenersArray;
	protected transient boolean isChangedArrayUpToDate;

	protected Vector genericPropertyModifiedListeners;
	protected Hashtable namedPropertyModifiedListeners;
	protected transient PropertyChangeListener[] genericModifiedListenersArray;
	protected transient boolean isModifiedArrayUpToDate;

	public DomePropertyChangeSupport(Object sourceBean)
	{
        if (sourceBean == null)
			throw new NullPointerException("DomePropertyChangeSupport constructor - null sourceBean");
		source = sourceBean;
	}


    /**
     * clear all listeners in this class
     * used to clear meaningless reference to prevent memory leakage, when the class is deleted
     * added to fix memory leakage problems
     */
    public void clearAllListeners() {
        if (genericPropertyChangeListeners != null) genericPropertyChangeListeners.clear();
        if (namedPropertyChangeListeners != null) namedPropertyChangeListeners.clear();
        genericChangeListenersArray = null;
        if (genericPropertyModifiedListeners != null) genericPropertyModifiedListeners.clear();
        if (namedPropertyModifiedListeners != null) namedPropertyModifiedListeners.clear();
        genericModifiedListenersArray = null;

        /* remove reference stored in source */
        source = null;
    }

	private Integer getNextEventId() {
		synchronized (nextEventId) {
			Integer eventId = this.nextEventId;
			if (this.nextEventId.intValue() == Integer.MAX_VALUE) {
				System.err.println("WARNING: the property change support for the following item has run out of event ids - " +
				                   Names.getName(source) + "\n\tThe event ids are being rolled over to the start.");
				this.nextEventId = new Integer(1);
			} else
				this.nextEventId = new Integer(eventId.intValue() + 1);
			return eventId;
		}
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (listener == null) return;
		if (genericPropertyChangeListeners == null)
			genericPropertyChangeListeners = new Vector();
		if (genericPropertyChangeListeners.contains(listener)) return; // no duplicates allowed
		genericPropertyChangeListeners.add(listener);
		isChangedArrayUpToDate = false;
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if ((genericPropertyChangeListeners == null) || (listener == null)) return;
		if (genericPropertyChangeListeners.remove(listener))
			isChangedArrayUpToDate = false;
		if (genericPropertyChangeListeners.isEmpty()) { // no listeners left
			genericPropertyChangeListeners = null;
			genericChangeListenersArray = null;
		}
	}

	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		if (propertyName == null)
			throw new NullPointerException("DomePropertyChangeSupport.addPropertyChangeListener - null propertyName");
		if (listener == null) return;
		if (namedPropertyChangeListeners == null)
			namedPropertyChangeListeners = new Hashtable();
		DomePropertyChangeSupport propertyListeners = (DomePropertyChangeSupport) namedPropertyChangeListeners.get(propertyName);
		if (propertyListeners == null) {
            propertyListeners = new DomePropertyChangeSupport(source);
			namedPropertyChangeListeners.put(propertyName, propertyListeners);
		}
		propertyListeners.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		if (propertyName == null)
			throw new NullPointerException("DomePropertyChangeSupport.removePropertyChangeListener - null propertyName");
		if ((namedPropertyChangeListeners == null) || (listener == null)) return;
		DomePropertyChangeSupport propertyListeners = (DomePropertyChangeSupport) namedPropertyChangeListeners.get(propertyName);
		if (propertyListeners == null) return; // propertyName not in Hashtable
		propertyListeners.removePropertyChangeListener(listener);
		if (propertyListeners.hasGenericChangePropertyListeners()) return; // keep it
		namedPropertyChangeListeners.remove(propertyName); // delete it, no more listeners for this property
		if (namedPropertyChangeListeners.isEmpty()) // no more properties at all
			namedPropertyChangeListeners = null;
	}

	// PropertyModifiedListeners are notified of any modifications to data, even if value did not change.

	public synchronized void addPropertyModifiedListener(PropertyChangeListener listener)
	{
		if (listener == null) return;
		if (genericPropertyModifiedListeners == null)
			genericPropertyModifiedListeners = new Vector();
		if (genericPropertyModifiedListeners.contains(listener)) return; // no duplicates allowed
		genericPropertyModifiedListeners.add(listener);
		isModifiedArrayUpToDate = false;
	}

	public synchronized void removePropertyModifiedListener(PropertyChangeListener listener)
	{
		if ((genericPropertyModifiedListeners == null) || (listener == null)) return;
		if (genericPropertyModifiedListeners.remove(listener))
			isModifiedArrayUpToDate = false;
		if (genericPropertyModifiedListeners.isEmpty()) { // no listeners left
			genericPropertyModifiedListeners = null;
			genericModifiedListenersArray = null;
		}
	}

	public synchronized void addPropertyModifiedListener(String propertyName, PropertyChangeListener listener)
	{
		if (propertyName == null)
			throw new NullPointerException("DomePropertyChangeSupport.addPropertyModifiedListener - null propertyName");
		if (listener == null) return;
		if (namedPropertyModifiedListeners == null)
			namedPropertyModifiedListeners = new Hashtable();
		DomePropertyChangeSupport propertyListeners = (DomePropertyChangeSupport) namedPropertyModifiedListeners.get(propertyName);
		if (propertyListeners == null) {
			propertyListeners = new DomePropertyChangeSupport(source);
			namedPropertyModifiedListeners.put(propertyName, propertyListeners);
		}
		propertyListeners.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyModifiedListener(String propertyName, PropertyChangeListener listener)
	{
		if (propertyName == null)
			throw new NullPointerException("DomePropertyChangeSupport.removePropertyModifiedListener - null propertyName");
		if ((namedPropertyModifiedListeners == null) || (listener == null)) return;
		DomePropertyChangeSupport propertyListeners = (DomePropertyChangeSupport) namedPropertyModifiedListeners.get(propertyName);
		if (propertyListeners == null) return; // propertyName not in Hashtable
		propertyListeners.removePropertyChangeListener(listener);
		if (propertyListeners.hasGenericModifiedPropertyListeners()) return; // keep it
		namedPropertyModifiedListeners.remove(propertyName); // delete it, no more listeners for this property
		if (namedPropertyModifiedListeners.isEmpty()) // no more properties at all
			namedPropertyModifiedListeners = null;
	}

	protected boolean hasGenericChangePropertyListeners()
	{
		return (genericPropertyChangeListeners != null);
	}

	protected boolean hasGenericModifiedPropertyListeners()
	{
		return (genericPropertyModifiedListeners != null);
	}

	public void firePropertyChange(String propertyName) {
		firePropertyChange(propertyName, false);
	}

	public void firePropertyChange(String propertyName, boolean fireInSeparateThread)
	{
		if (propertyName == null)
			throw new NullPointerException("DomePropertyChangeSupport.firePropertyChange - null propertyName");
		firePropertyChange(new PropertyChangeEvent(source, propertyName, null, null), fireInSeparateThread);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		firePropertyChange(propertyName, oldValue, newValue, false);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue, boolean fireInSeparateThread)
	{
		if (propertyName == null)
			throw new NullPointerException("DomePropertyChangeSupport.firePropertyChange - null propertyName");
		PropertyChangeEvent event = new DomePropertyChangeEvent(source, propertyName, oldValue, newValue,
		                                                        this.getNextEventId());
		firePropertyModified(event, fireInSeparateThread); // for listeners that want the value even if it is the same as the old
		if (shouldFirePropertyChange(propertyName, oldValue, newValue))
			firePropertyChange(event, fireInSeparateThread); // for listeners that only want new values (avoids infinite loops)
	}

	protected void firePropertyChange(PropertyChangeEvent event, boolean fireInSeparateThread)
	{
		String propertyName = event.getPropertyName();
		PropertyChangeListener[] genericListeners = null;
		DomePropertyChangeSupport namedListeners = null;
		synchronized (this) {
			genericListeners = getGenericChangeListenersArray();
			if (namedPropertyChangeListeners != null)
				namedListeners = (DomePropertyChangeSupport) namedPropertyChangeListeners.get(propertyName);
		}
		if (namedListeners != null) // do named listeners first, last to first
			namedListeners.firePropertyChange(event, fireInSeparateThread);
		if (genericListeners != null) { // then generic listeners, last to first
			if (fireInSeparateThread)
				new Thread(new PropertyChangeNotificationThread(genericListeners, event)).start();
			else {
				for (int i = genericListeners.length - 1; i >= 0; --i)
					genericListeners[i].propertyChange(event);
			}
		}
	}

	protected void firePropertyModified(PropertyChangeEvent event, boolean fireInSeparateThread)
	{
		String propertyName = event.getPropertyName();
		PropertyChangeListener[] genericListeners = null;
		DomePropertyChangeSupport namedListeners = null;
		synchronized (this) {
			genericListeners = getGenericModifiedListenersArray();
			if (namedPropertyModifiedListeners != null)
				namedListeners = (DomePropertyChangeSupport) namedPropertyModifiedListeners.get(propertyName);
		}
		if (namedListeners != null) // do named listeners first, last to first
			namedListeners.firePropertyChange(event, fireInSeparateThread);
		if (genericListeners != null) { // then generic listeners, last to first
			if (fireInSeparateThread)
				new Thread(new PropertyChangeNotificationThread(genericListeners, event)).start();
			else {
				for (int i = genericListeners.length - 1; i >= 0; --i)
					genericListeners[i].propertyChange(event);
			}
		}
	}

	protected PropertyChangeListener[] getGenericChangeListenersArray()
	{
		if (genericPropertyChangeListeners == null) return null;
		if (isChangedArrayUpToDate) return genericChangeListenersArray;
		// create listeners array and cache for future use
		synchronized (this) {
			genericChangeListenersArray = new PropertyChangeListener[genericPropertyChangeListeners.size()];
			for (int i = 0; i < genericChangeListenersArray.length; ++i)
				genericChangeListenersArray[i] = (PropertyChangeListener) genericPropertyChangeListeners.get(i);
			isChangedArrayUpToDate = true;
		}
		return genericChangeListenersArray;
	}

	protected PropertyChangeListener[] getGenericModifiedListenersArray()
	{
		if (genericPropertyModifiedListeners == null) return null;
		if (isModifiedArrayUpToDate) return genericModifiedListenersArray;
		// create listeners array and cache for future use
		synchronized (this) {
			genericModifiedListenersArray = new PropertyChangeListener[genericPropertyModifiedListeners.size()];
			for (int i = 0; i < genericModifiedListenersArray.length; ++i)
				genericModifiedListenersArray[i] = (PropertyChangeListener) genericPropertyModifiedListeners.get(i);
			isModifiedArrayUpToDate = true;
		}
		return genericModifiedListenersArray;
	}

	/**
	 * Override this method to customize if propertyChangeEvent should be fired to listeners
	 * Default behavior is to fire a change if both values are null or if one value is null
	 * or if the two values are not the same.
	 * So, changes would only not be fired if both values are non-null and are equal to each other.
	 * @param property
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	protected boolean shouldFirePropertyChange(String property, Object oldValue, Object newValue) {
		return !(oldValue != null && newValue != null && oldValue.equals(newValue));
	}

	class PropertyChangeNotificationThread implements Runnable {
		PropertyChangeListener[] listeners;
		PropertyChangeEvent event;

		public PropertyChangeNotificationThread(PropertyChangeListener[] listeners, PropertyChangeEvent event)
		{
			this.listeners = listeners;
			this.event = event;
		}

		public void run()
		{
			// notify listeners, first to last
			for (int i = 0; i < listeners.length; ++i)
				listeners[i].propertyChange(event);
		}
	}

}
