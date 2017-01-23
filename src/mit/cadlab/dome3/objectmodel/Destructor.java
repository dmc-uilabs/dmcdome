// Destructor.java
package mit.cadlab.dome3.objectmodel;


/**
 * This interface expresses the need for objects to
 * know when objects are "deleted" from other objects
 * and therefore no longer valid.
 */
public interface Destructor
{

	/**
	 * Method tells object that is will be/has been deleted
	 * and therefore should notify other DeletionListeners
	 * of destruction and perform any necessary cleanup.
	 *
	 * @param notifier the DeletionListener for object which invoked delete
	 */
	public void delete(DeletionListener notifier);

	/**
	 * Adds a listener which is notified when object
	 * has its delete method invoked.
	 * Duplicate listeners are ignored.
	 */
	public void addDeletionListener(DeletionListener l);

	/**
	 * Removes a listener which is notified when object
	 * has its delete method invoked.
	 * Listeners not found are ignored.
	 */
	public void removeDeletionListener(DeletionListener l);

}
