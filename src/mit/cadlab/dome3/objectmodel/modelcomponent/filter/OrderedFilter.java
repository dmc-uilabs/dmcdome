// OrderedFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.ShiftSupport;

import java.util.List;

/**
 * Creates filter in which something else is telling it what is to be added/removed.
 */
public class OrderedFilter extends AbstractFilter implements ShiftSupport, ViewSupport
{

	public OrderedFilter(Model m, Id id, String name)
	{
		super(m, id, name, true);
		createViews();
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

	public void addFilterItem(Object obj)
	{
		filteredItems.add(obj);
	}

	public void removeFilterItem(Object obj)
	{
		filteredItems.remove(obj);
	}

	// Filter interface
	public List getListsToFilter()
	{
		throw new UnsupportedOperationException();
	}

	public void addListToFilter(Filterable listOfItems)
	{
		throw new UnsupportedOperationException();
	}

	public void removeListToFilter(Filterable listOfItems)
	{
		throw new UnsupportedOperationException();
	}

	// ShiftSupport interface
	public void shiftLeft(int[] indices)
	{
		filteredItems.shiftLeft(indices);
	}

	/**
	 *
	 */
	public void shiftRight(int[] indices)
	{
		filteredItems.shiftRight(indices);
	}


	public void addViewListener(String viewName, DListListener l)
	{
		List view = getView(viewName);
		//filteredItems.addDListListener(l);
	}

	public void removeViewListener(String viewName, DListListener l)
	{
		List view = getView(viewName);
		//filteredItems.removeDListListener(l);
	}

}
