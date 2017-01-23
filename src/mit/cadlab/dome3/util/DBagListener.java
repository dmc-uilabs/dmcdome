// DBagListener.java
package mit.cadlab.dome3.util;

import java.util.EventListener;

public interface DBagListener extends EventListener
{

	/**
	 * invoked first time item is added
	 */
	public void itemAdded(DBagEvent e);

	/**
	 * invoked when item is removed
	 */
	public void itemRemoved(DBagEvent e);

	/**
	 * invoked when existing item is added to
	 */
	public void itemIncremented(DBagEvent e);

	/**
	 * invoked when existing item is removed from but not totally
	 */
	public void itemDecremented(DBagEvent e);

	/**
	 * invoked when bag becomes empty
	 */
	public void bagEmpty(DBagEvent e);

	/**
	 * invoked when bag becomes nonempty
	 */
	public void bagNonEmpty(DBagEvent e);

}
