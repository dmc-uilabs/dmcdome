// UnionFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;

/**
 * Creates union of all items to watch.
 */
public class UnionFilter extends AbstractFilter
{

	public UnionFilter(Model m, Id id, String name)
	{
		super(m, id, name, false); // global filter
	}

	/**
	 * Create a list for the items in this filter
	 * Subclasses may override this method to place filter within list
	 * Default accepts all items (creates union of all lists to watch)
	 */
	protected DArrayList createFilteredItemsList()
	{
		return new DArrayList();
	}

}
