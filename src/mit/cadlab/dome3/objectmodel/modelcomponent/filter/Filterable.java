// Filterable.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.Destructor;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collection;

/**
 * To be implemented by items which can have a filter added to it.
 * Assumption is that the meaning of filtering a container is unambiguous.
 */
public interface Filterable extends Destructor
{

	/**
	 * adds listener to list of items to filter
	 * returns Collection of items currently in list of items to filter
	 */
	public Collection addItemsToFilterListener(DListListener l);

	/**
	 * removes listener from list of items to filter
	 * returns Collection of items currently in list of items to filter
	 */
	public Collection removeItemsToFilterListener(DListListener l);

}
