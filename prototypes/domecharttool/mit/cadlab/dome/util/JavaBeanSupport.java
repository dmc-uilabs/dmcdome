// JavaBeanSupport.java
package mit.cadlab.dome.util;

import java.beans.PropertyChangeListener;

/**
 * This class outlines what methods will be available for
 * DomeObjects which support the JavaBean standard.
 * Concrete classes may inherit from DomeJavaBean or use
 * DomePropertyChangeSupport.
 */
public interface JavaBeanSupport {

  /**
   * Adds a PropertyChangeListener for all properties.
   * Duplicate listeners are not added.
   *
   * @param listener a PropertyChangeListener for all properties.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener);

  /**
   * Removes a PropertyChangeListener for all properties.
   * Ignores listeners not found.
   *
   * @param listener a PropertyChangeListener for all properties
   */
  public void removePropertyChangeListener(PropertyChangeListener listener);

  /**
   * Adds a PropertyChangeListener for specified property.
   * Duplicate listeners for property are not added.
   *
   * @param propertyName name of property of interest to listener
   * @param listener a PropertyChangeListener for specified property
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

  /**
   * Removes a PropertyChangeListener for specified property.
   * Ignores listeners not found.
   *
   * @param propertyName name of property of interest to listener
   * @param listener a PropertyChangeListener for specified property
   */
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}
