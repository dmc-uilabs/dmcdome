// DeletionEvent.java
package mit.cadlab.dome3.objectmodel;

import java.util.EventObject;

/**
 * Event that informs DeletionListener that object
 * has been or is about to be deleted.
 */
public class DeletionEvent extends EventObject
{

	public DeletionEvent(Object source)
	{
		super(source);
	}

	public String toString()
	{
		return "DeletionEvent for " + getSource();
	}

}
