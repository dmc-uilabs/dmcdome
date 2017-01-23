// FunctionFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;

/**
 * Creates filter which takes FilterFunction object to determine if item is in filter
 */
public class FunctionFilter extends AbstractFilter
{

	protected FilterFunction filterFunction;

	protected FunctionFilter(Model m, Id id, String name, boolean isInternalFilter)
	{
		super(m, id, name, isInternalFilter);
	}

	public FunctionFilter(Model m, Id id, FilterFunction filterFunction, boolean isInternalFilter)
	{
		super(m, id, (filterFunction == null) ? "" : filterFunction.getName(), isInternalFilter);
		if (filterFunction == null)
			throw new DomeObjectException("filterFunction can not be null!");
		this.filterFunction = filterFunction;
	}

	/**
	 * Create a list for the items in this filter
	 * Subclasses may override this method to place filter within list
	 */
	protected DArrayList createFilteredItemsList()
	{
		return new FunctionFilteredItemsList();
	}

	protected class FunctionFilteredItemsList extends DArrayList
	{
		protected boolean addHookBefore(Object obj)
		{
			if (filterFunction == null) {
				System.out.println("null filterFunction in " + getName());
				return true;
			}
			return filterFunction.keepInFilter(obj);
		}
	}

}
