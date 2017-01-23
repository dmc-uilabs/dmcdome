// MultiViewSupport.java
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.util.DListListener;

import java.util.List;

/**
 * MultiViewSupport should be implemented by all collections
 * that support multiple views via the DomeTree. This interface
 * is used to query the object for its children in the
 * tree. The List returned should be a list of objects.
 */
public interface MultiViewSupport
{

	/**
	 * Gets the list of views supported by this object.
	 */
	public List getViewNames();

	/**
	 * Gets the default view for this collection of items.
	 */
	public List getView(String viewName);

	public void addViewListener(String viewName, DListListener l);

	public void removeViewListener(String viewName, DListListener l);

}
