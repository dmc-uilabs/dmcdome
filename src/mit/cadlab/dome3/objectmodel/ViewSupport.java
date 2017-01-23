// ViewSupport.java
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.util.DListListener;

import java.util.List;

/**
 * ViewSupport should be implemented by all collections
 * that will be viewed via the DomeTree. This interface
 * is used to query the object for its children in the
 * tree. The List returned should be a list of objects.
 */
public interface ViewSupport
{

	/**
	 * Gets the default view for this collection of items.
	 */
	public List getView();

	public void addViewListener(DListListener l);

	public void removeViewListener(DListListener l);

}
