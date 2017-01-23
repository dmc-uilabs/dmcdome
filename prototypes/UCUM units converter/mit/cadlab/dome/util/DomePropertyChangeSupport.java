// DomePropertyChangeSupport.java
package mit.cadlab.dome.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This class is similar to java.beans.PropertyChangeSupport and
 * javax.swing.event.SwingPropertyChangeSupport.
 * Rewritten to remove duplicate listener entries and to try
 * to be a bit faster.
 * Class is not thread-safe (like SwingPropertyChangeSupport).
 */
public class DomePropertyChangeSupport implements java.io.Serializable {

  protected Object source;
  protected Vector genericPropertyListeners;
  protected Hashtable namedPropertyListeners;
  protected transient PropertyChangeListener[] genericListenersArray;
  protected transient boolean isArrayUpToDate;

  public DomePropertyChangeSupport(Object sourceBean) {
    if (sourceBean == null)
      throw new NullPointerException("DomePropertyChangeSupport constructor - null sourceBean");
    source = sourceBean;
  }

  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null) return;
    if (genericPropertyListeners == null)
      genericPropertyListeners = new Vector();
    if (genericPropertyListeners.contains(listener)) return; // no duplicates allowed
    genericPropertyListeners.add(listener);
    isArrayUpToDate = false;
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    if ((genericPropertyListeners == null) || (listener == null)) return;
    genericPropertyListeners.remove(listener);
    if (genericPropertyListeners.isEmpty()) { // no listeners left
      genericPropertyListeners = null;
      genericListenersArray = null;
    }
  }

  public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (propertyName == null)
      throw new NullPointerException("DomePropertyChangeSupport.addPropertyChangeListener - null propertyName");
    if (listener == null) return;
    if (namedPropertyListeners == null)
      namedPropertyListeners = new Hashtable();
    DomePropertyChangeSupport propertyListeners = (DomePropertyChangeSupport)namedPropertyListeners.get(propertyName);
    if (propertyListeners == null) {
      propertyListeners = new DomePropertyChangeSupport(source);
      namedPropertyListeners.put(propertyName,propertyListeners);
    }
    propertyListeners.addPropertyChangeListener(listener);
  }

  public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (propertyName == null)
      throw new NullPointerException("DomePropertyChangeSupport.removePropertyChangeListener - null propertyName");
    if ((namedPropertyListeners == null) || (listener == null)) return;
    DomePropertyChangeSupport propertyListeners = (DomePropertyChangeSupport)namedPropertyListeners.get(propertyName);
    if (propertyListeners == null) return; // propertyName not in Hashtable
    propertyListeners.removePropertyChangeListener(listener);
    if (propertyListeners.hasGenericPropertyListeners()) return; // keep it
    namedPropertyListeners.remove(propertyName); // delete it, no more listeners for this property
    if (namedPropertyListeners.isEmpty()) // no more properties at all
      namedPropertyListeners = null;
  }

  protected boolean hasGenericPropertyListeners() {
    return (genericPropertyListeners != null);
  }

  public void firePropertyChange(String propertyName) {
    if (propertyName == null)
      throw new NullPointerException("DomePropertyChangeSupport.firePropertyChange - null propertyName");
    firePropertyChange(new PropertyChangeEvent(source,propertyName,null,null));
  }

  public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    if (propertyName == null)
      throw new NullPointerException("DomePropertyChangeSupport.firePropertyChange - null propertyName");
    if (oldValue != null && newValue != null && oldValue.equals(newValue))
      return;
    firePropertyChange(new PropertyChangeEvent(source,propertyName,oldValue,newValue));
  }

  protected void firePropertyChange(PropertyChangeEvent event) {
    String propertyName = event.getPropertyName();
    PropertyChangeListener[] genericListeners = null;
    DomePropertyChangeSupport namedListeners = null;
    synchronized(this) {
      genericListeners = getGenericListenersArray();
      if (namedPropertyListeners != null)
	namedListeners = (DomePropertyChangeSupport)namedPropertyListeners.get(propertyName);
    }
    if (namedListeners != null) // do named listeners first, last to first
      namedListeners.firePropertyChange(event);
    if (genericListeners != null) { // then generic listeners, last to first
      for (int i=genericListeners.length-1; i>=0; --i)
	genericListeners[i].propertyChange(event);
    }
  }

  protected PropertyChangeListener[] getGenericListenersArray() {
    if (genericPropertyListeners == null) return null;
    if (isArrayUpToDate) return genericListenersArray;
    // create listeners array and cache for future use
    synchronized(this) {
      genericListenersArray = new PropertyChangeListener[genericPropertyListeners.size()];
      for (int i=0; i<genericListenersArray.length; ++i)
	genericListenersArray[i] = (PropertyChangeListener)genericPropertyListeners.get(i);
      isArrayUpToDate = true;
    }
    return genericListenersArray;
  }

}
