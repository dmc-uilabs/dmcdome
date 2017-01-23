// DomeJavaBean.java
package mit.cadlab.dome3.util;

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
public class DomeJavaBean implements JavaBeanSupport
{

	private DomePropertyChangeSupport propertyListeners;

	public DomeJavaBean()
	{
		this.propertyListeners = createDomePropertyChangeSupport();
	}

	protected DomePropertyChangeSupport createDomePropertyChangeSupport() {
		return new DomePropertyChangeSupport(this);
	}

	/**
	 * Adds a PropertyChangeListener for all properties.
	 * Duplicate listeners are not added.
	 *
	 * @param listener a PropertyChangeListener for all properties.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyListeners.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a PropertyChangeListener for all properties.
	 * Ignores listeners not found.
	 *
	 * @param listener a PropertyChangeListener for all properties
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyListeners.removePropertyChangeListener(listener);
	}

	/**
	 * Adds a PropertyChangeListener for specified property.
	 * Duplicate listeners for property are not added.
	 *
	 * @param propertyName name of property of interest to listener
	 * @param listener a PropertyChangeListener for specified property
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyListeners.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes a PropertyChangeListener for specified property.
	 * Ignores listeners not found.
	 *
	 * @param propertyName name of property of interest to listener
	 * @param listener a PropertyChangeListener for specified property
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyListeners.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Adds a PropertyChangeListener for modifications to all properties even if value did not change.
	 * Duplicate listeners are not added.
	 *
	 * @param listener a PropertyChangeListener for all properties.
	 */
	public void addPropertyModifiedListener(PropertyChangeListener listener)
	{
		propertyListeners.addPropertyModifiedListener(listener);
	}

	/**
	 * Removes a PropertyChangeListener for modifications to all properties even if value did not change.
	 * Ignores listeners not found.
	 *
	 * @param listener a PropertyChangeListener for all properties
	 */
	public void removePropertyModifiedListener(PropertyChangeListener listener)
	{
		propertyListeners.removePropertyModifiedListener(listener);
	}

	/**
	 * Adds a PropertyChangeListener for modification to specified property even if value did not change.
	 * Duplicate listeners for property are not added.
	 *
	 * @param propertyName name of property of interest to listener
	 * @param listener a PropertyChangeListener for specified property
	 */
	public void addPropertyModifiedListener(String propertyName, PropertyChangeListener listener)
	{
		propertyListeners.addPropertyModifiedListener(propertyName, listener);
	}

	/**
	 * Removes a PropertyChangeListener for modification to specified property even if value did not change.
	 * Ignores listeners not found.
	 *
	 * @param propertyName name of property of interest to listener
	 * @param listener a PropertyChangeListener for specified property
	 */
	public void removePropertyModifiedListener(String propertyName, PropertyChangeListener listener)
	{
		propertyListeners.removePropertyModifiedListener(propertyName, listener);
	}

	/**
	 * Fires a PropertyChangeEvent for this object.
	 *
	 * @param propertyName name of property
	 */
	protected void firePropertyChange(String propertyName)
	{
		propertyListeners.firePropertyChange(propertyName);
	}

	/**
	 * Fires a PropertyChangeEvent for this object
	 * A new thread is used for listener notification
	 *
	 * @param propertyName name of property
	 */
	public void firePropertyChangeInSeparateThread(String propertyName)
	{
		propertyListeners.firePropertyChange(propertyName, true);
	}

	/**
	 * Fires a PropertyChangeEvent for this object.
	 * If oldValue.equals(newValue) and both are not null, event is not fired.
	 *
	 * @param propertyName name of property
	 * @param oldValue old value of property; may be null
	 * @param newValue new value of property; may be null
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		propertyListeners.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Fires a PropertyChangeEvent for this object.
	 * If oldValue.equals(newValue) and both are not null, event is not fired.
	 * A new thread is used for listener notification
	 *
	 * @param propertyName name of property
	 * @param oldValue old value of property; may be null
	 * @param newValue new value of property; may be null
	 */
	public void firePropertyChangeInSeparateThread(String propertyName, Object oldValue, Object newValue)
	{
		propertyListeners.firePropertyChange(propertyName, oldValue, newValue, true);
	}

    /**
     * clears all listeners in this class
     * added to fix memory leakage problems
     */
    public void clearPropertyListeners() {
        propertyListeners.clearAllListeners();
    }

}
