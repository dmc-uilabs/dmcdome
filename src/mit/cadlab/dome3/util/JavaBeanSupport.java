// JavaBeanSupport.java
package mit.cadlab.dome3.util;

import java.beans.PropertyChangeListener;

/**
 * This class outlines what methods will be available for
 * DomeObjects which support the JavaBean standard.
 * Concrete classes may inherit from DomeJavaBean or use
 * DomePropertyChangeSupport.
 */
public interface JavaBeanSupport
{

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

	// The methods below are for adding property change listeners that want to be notified
	// in all modifications to the object, even if the value is the same as before.

	/**
	 * Adds a PropertyChangeListener for modifications to all properties even if value did not change.
	 * Duplicate listeners are not added.
	 *
	 * @param listener a PropertyChangeListener for all properties.
	 */
	public void addPropertyModifiedListener(PropertyChangeListener listener);

	/**
	 * Removes a PropertyChangeListener for modifications to all properties even if value did not change.
	 * Ignores listeners not found.
	 *
	 * @param listener a PropertyChangeListener for all properties
	 */
	public void removePropertyModifiedListener(PropertyChangeListener listener);

	/**
	 * Adds a PropertyChangeListener for modification to specified property even if value did not change.
	 * Duplicate listeners for property are not added.
	 *
	 * @param propertyName name of property of interest to listener
	 * @param listener a PropertyChangeListener for specified property
	 */
	public void addPropertyModifiedListener(String propertyName, PropertyChangeListener listener);

	/**
	 * Removes a PropertyChangeListener for modification to specified property even if value did not change.
	 * Ignores listeners not found.
	 *
	 * @param propertyName name of property of interest to listener
	 * @param listener a PropertyChangeListener for specified property
	 */
	public void removePropertyModifiedListener(String propertyName, PropertyChangeListener listener);
}
