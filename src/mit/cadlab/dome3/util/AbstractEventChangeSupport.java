// AbstractEventChangeSupport.java
package mit.cadlab.dome3.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Derived from DomePropertyChangeSupport for use with custom events
 * See example methods at end of source code for how to customize this class
 */
public abstract class AbstractEventChangeSupport implements java.io.Serializable
{

	protected String name;
	protected String changeName; // used for error messages
	protected Object source;
	protected Vector genericChangeListeners;
	protected Hashtable specificChangeListeners;
	protected transient Object[] genericListenersArray;
	protected transient boolean isArrayUpToDate;

	public AbstractEventChangeSupport(String name, String changeName, Object source)
	{
		this.name = (name == null) ? ClassUtils.getClassName(this) : name;
		this.changeName = (changeName == null) ? "EventChange" : changeName;
		if (source == null)
			throw new NullPointerException(this.name + " constructor - null source");
		this.source = source;
	}

	protected synchronized void addEventChangeListener(Object listener)
	{
		if (listener == null) return;
		if (genericChangeListeners == null)
			genericChangeListeners = new Vector();
		if (genericChangeListeners.contains(listener)) return; // no duplicates allowed
		genericChangeListeners.add(listener);
		isArrayUpToDate = false;
	}

	protected synchronized void removeEventChangeListener(Object listener)
	{
		if ((genericChangeListeners == null) || (listener == null)) return;
		if (genericChangeListeners.remove(listener))
			isArrayUpToDate = false;
		if (genericChangeListeners.isEmpty()) { // no listeners left
			genericChangeListeners = null;
			genericListenersArray = null;
		}
	}

	protected synchronized void addEventChangeListener(Object target, Object listener)
	{
		if (target == null)
			throw new NullPointerException(name + ".add" + changeName + "Listener - null target");
		if (listener == null) return;
		if (specificChangeListeners == null)
			specificChangeListeners = new Hashtable();
		AbstractEventChangeSupport changeListeners = (AbstractEventChangeSupport) specificChangeListeners.get(target);
		if (changeListeners == null) {
			changeListeners = createEventChangeSupport();
			specificChangeListeners.put(target, changeListeners);
		}
		changeListeners.addEventChangeListener(listener);
	}

	// method to create similar instances of EventChangeSupport
	protected abstract AbstractEventChangeSupport createEventChangeSupport();

	protected synchronized void removeEventChangeListener(Object target, Object listener)
	{
		if (target == null)
			throw new NullPointerException(name + ".remove" + changeName + "Listener - null target");
		if ((specificChangeListeners == null) || (listener == null)) return;
		AbstractEventChangeSupport changeListeners = (AbstractEventChangeSupport) specificChangeListeners.get(target);
		if (changeListeners == null) return; // target not in Hashtable
		changeListeners.removeEventChangeListener(listener);
		if (changeListeners.hasGenericPropertyListeners()) return; // keep it
		specificChangeListeners.remove(target); // delete it, no more listeners for this target
		if (specificChangeListeners.isEmpty()) // no more targets at all
			specificChangeListeners = null;
	}

	protected boolean hasGenericPropertyListeners()
	{
		return (genericChangeListeners != null);
	}

	// an array is used so if list is modified, all notifications can be completed
	protected Object[] getGenericListenersArray()
	{
		if (genericChangeListeners == null) return null;
		if (isArrayUpToDate) return genericListenersArray;
		// create listeners array and cache for future use
		synchronized (this) {
			genericListenersArray = new Object[genericChangeListeners.size()];
			for (int i = 0; i < genericListenersArray.length; ++i)
				genericListenersArray[i] = (Object) genericChangeListeners.get(i);
			isArrayUpToDate = true;
		}
		return genericListenersArray;
	}

//    // Sample methods for managing event changes
//    public void fireEventChange(Object target) {
//        if (target == null)
//            throw new NullPointerException(name+".fireEventChange - null target");
//        fireEventChange(new EventChangeEvent(source, target, null, null));
//    }
//
//    public void fireEventChange(Object target, Object oldValue, Object newValue) {
//        if (target == null)
//            throw new NullPointerException(name+".fireEventChange - null target");
//        if (oldValue != null && newValue != null && oldValue.equals(newValue))
//            return;
//        fireEventChange(new EventChangeEvent(source, target, oldValue, newValue));
//    }
//
//    protected void fireEventChange(EventChangeEvent event) {
//        Object target = event.getObject();
//        Object[] genericListeners = null;
//        AbstractEventChangeSupport specificListeners = null;
//        synchronized (this) {
//            genericListeners = getGenericListenersArray();
//            if (specificChangeListeners != null)
//                specificListeners = (AbstractEventChangeSupport) specificChangeListeners.get(target);
//        }
//        if (specificListeners != null) // do specific listeners first, last to first
//            specificListeners.fireEventChange(event);
//        if (genericListeners != null) { // then generic listeners, last to first
//            for (int i = genericListeners.length - 1; i >= 0; --i)
//                genericListeners[i].eventChange(event);
//        }
//    }

}
