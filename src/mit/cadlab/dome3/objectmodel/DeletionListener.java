// DeletionListener.java
package mit.cadlab.dome3.objectmodel;

import java.util.EventListener;

/**
 * The listener that is notified when an object has been or
 * is about to be "deleted".
 */
public interface DeletionListener extends EventListener
{

	/**
	 * Notifies listener of object deletion.
	 */
	public void objectDeleted(DeletionEvent e);

}
