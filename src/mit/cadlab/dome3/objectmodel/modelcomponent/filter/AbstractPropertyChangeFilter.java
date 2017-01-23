// AbstractPropertyChangeFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DBagEvent;
import mit.cadlab.dome3.util.DBagListener;

/**
 * Creates filter which takes FilterFunction object to watch an object change.
 */
public abstract class AbstractPropertyChangeFilter extends FunctionFilter
{

	protected PropertyChangeFilterFunction propertyFilterFunction;

	public AbstractPropertyChangeFilter(Model m, Id id, boolean isInternalFilter)
	{
		super(m, id, (String) null, isInternalFilter);
		this.propertyFilterFunction = createFilterFunction();
		this.filterFunction = propertyFilterFunction;
		setName(filterFunction.getName());
	}

	protected abstract PropertyChangeFilterFunction createFilterFunction();

	protected interface PropertyChangeFilterFunction extends FilterFunction
	{

		public void addListenerTo(Object obj);

		public void removeListenerFrom(Object obj);

	}

	protected DBagListener createItemsInListsToFilterListener()
	{
		return new PropertyChangeItemsInListsToFilterListener();
	}

	protected class PropertyChangeItemsInListsToFilterListener extends DefaultItemsInListsToFilterListener
	{
		public void itemAdded(DBagEvent e)
		{
			propertyFilterFunction.addListenerTo(e.getItem());
			super.itemAdded(e);
		}

		public void itemRemoved(DBagEvent e)
		{
			propertyFilterFunction.removeListenerFrom(e.getItem());
			super.itemRemoved(e);
		}
	}

}
