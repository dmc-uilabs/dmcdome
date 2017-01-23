// DomeJavaBean.java
package mit.cadlab.dome.util;

import java.beans.PropertyChangeListener;

/**
 * DomeJavaBeans can subclass this class or use the
 * DomePropertyChangeSupport themself to support bound
 * property changes
 * Classes in DOME which support property changes should implement this interface
 * which supports bound properties. PropertyChangeListeners for either all
 * properties or a specific property are supported.
 * Registering to listen to all properties is distinct from registering to
 * listen to a particular property.
 * Listeners should not modify values in PropertyChangeEvent. These are the
 * same values as in the original object to save execution time.
 */
public class DomeJavaBean implements JavaBeanSupport {

  private DomePropertyChangeSupport propertyListeners = new DomePropertyChangeSupport(this);

  /**
   * Adds a PropertyChangeListener for all properties.
   * Duplicate listeners are not added.
   *
   * @param listener a PropertyChangeListener for all properties.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyListeners.addPropertyChangeListener(listener);
  }

  /**
   * Removes a PropertyChangeListener for all properties.
   * Ignores listeners not found.
   *
   * @param listener a PropertyChangeListener for all properties
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyListeners.removePropertyChangeListener(listener);
  }

  /**
   * Adds a PropertyChangeListener for specified property.
   * Duplicate listeners for property are not added.
   *
   * @param propertyName name of property of interest to listener
   * @param listener a PropertyChangeListener for specified property
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyListeners.addPropertyChangeListener(propertyName,listener);
  }

  /**
   * Removes a PropertyChangeListener for specified property.
   * Ignores listeners not found.
   *
   * @param propertyName name of property of interest to listener
   * @param listener a PropertyChangeListener for specified property
   */
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyListeners.removePropertyChangeListener(propertyName,listener);
  }

  /**
   * Fires a PropertyChangeEvent for this object.
   * 
   * @param property name of property
   */
  protected void firePropertyChange(String propertyName) {
    propertyListeners.firePropertyChange(propertyName);
  }

  /**
   * Fires a PropertyChangeEvent for this object.
   * If oldValue.equals(newValue) and both are not null, event is not fired.
   * 
   * @param property name of property
   * @param oldValue old value of property; may be null
   * @param newValue new value of property; may be null
   */
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    propertyListeners.firePropertyChange(propertyName,oldValue,newValue);
  }

}
