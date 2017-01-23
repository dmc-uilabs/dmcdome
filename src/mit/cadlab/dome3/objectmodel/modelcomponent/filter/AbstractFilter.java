// AbstractFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DBag;
import mit.cadlab.dome3.util.DBagEvent;
import mit.cadlab.dome3.util.DBagListener;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * To do by subclasses:
 * protected DList createFilteredItemsList();
 */
public abstract class AbstractFilter extends AbstractDomeObject implements Filter
{

	protected Model model;
	protected HashMap m_views = null;
	protected boolean isInternalFilter = false;
	protected DArrayList filteredItems; // items in filter
	protected DArrayList listsToFilter; // lists to watch for items to add/remove from filter
	protected DBag itemsInListsToFilter; // number of occurrences of item in all lists to watch
	protected DeletionListener filterableDeletionListener;

	public AbstractFilter(Model m, Id id, String name, boolean isInternalFilter)
	{
		super(id, name);
		m_views = new HashMap();
		this.model = m;
		this.isInternalFilter = isInternalFilter;
		filteredItems = createFilteredItemsList();
		listsToFilter = createListsToFilterList();
		itemsInListsToFilter = new DBag();
		itemsInListsToFilter.addDBagListener(createItemsInListsToFilterListener());
		filterableDeletionListener = createFilterableDeletionListener();
	}

	protected TypeInfo getTypeInfo()
	{
		return Filter.TYPE_INFO;
	}

	public String getXmlTag()
	{
		return Filter.XML_TAG;
	}

	//generates xml for filter
	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		XMLUtils.addCollectionRef(xml, parametersXmlTag, filteredItems);
		return xml;
	}

	/**
	 * Create a list for the items in this filter
	 * Subclasses may override this method to place filter within list
	 */
	protected abstract DArrayList createFilteredItemsList();

	/**
	 * Create a list of lists to watch for items to filter
	 * Lists added should have elements added to bag and listener added to watch for changes.
	 * Lists removed should have elements removed from bag and listener removed.
	 */
	protected DArrayList createListsToFilterList()
	{
		return new DefaultListsToFilterList();
	}

	/**
	 * Add listener to to update filteredItems list when items are added or removed from bag.
	 */
	protected DBagListener createItemsInListsToFilterListener()
	{
		return new DefaultItemsInListsToFilterListener();
	}

	/**
	 * Pick to return either a model-level deletion listener
	 *     (applied to model object outside of model object)
	 * or an internal model-object deletion listener (null)
	 *     (which doesn't register and is ignored)
	 */
	protected DeletionListener createFilterableDeletionListener()
	{
		if (isInternalFilter)
			return null;
		else
			return new DefaultFilterableDeletionListener();
	}

	// ModelComponent interface
	public Model getModel()
	{
		if (model != null) {
			return model.getModel();
		} else
			return null;
	}

	// Filter interface
	public int getItemCount()
	{
		return filteredItems.size();
	}

	public List getItems()
	{
		return Collections.unmodifiableList(filteredItems);
	}

	public boolean containsItem(Object obj)
	{
		return filteredItems.contains(obj);
	}

	public void addFilterListener(DListListener l)
	{
		filteredItems.addDListListener(l);
	}

	public void removeFilterListener(DListListener l)
	{
		filteredItems.removeDListListener(l);
	}

	public List getListsToFilter()
	{
		return Collections.unmodifiableList(listsToFilter);
	}

	public void addListToFilter(Filterable listOfItems)
	{
		listsToFilter.add(listOfItems);
	}

	public void removeListToFilter(Filterable listOfItems)
	{
		listsToFilter.remove(listOfItems);
	}

	// Filterable interface
	public Collection addItemsToFilterListener(DListListener l)
	{
		addFilterListener(l);
		return Collections.unmodifiableList(filteredItems);
	}

	public Collection removeItemsToFilterListener(DListListener l)
	{
		removeFilterListener(l);
		return Collections.unmodifiableList(filteredItems);
	}

	protected void createViews()
	{
		// create input/output type view
		List inputOutputView = Collections.unmodifiableList(filteredItems);
		m_views.put(Filter.INPUT_OUTPUT_VIEW, inputOutputView);
		// create another view
		List modelCausalityView = Collections.unmodifiableList(filteredItems);
		m_views.put(Filter.MODEL_CAUSALITY_VIEW, modelCausalityView);
	}

	// MultiViewSupport interface
	public List getViewNames()
	{
		return viewNames;
	}

	public List getView(String viewName)
	{
		return (viewName == null ? null : (List) m_views.get(viewName));
	}

	// ViewSupport interface
	public List getView()
	{
		return Collections.unmodifiableList(filteredItems);
	}

	public void addViewListener(DListListener l)
	{
		filteredItems.addDListListener(l);
	}

	public void removeViewListener(DListListener l)
	{
		filteredItems.removeDListListener(l);
	}

	// supporting list and bag classes

	/**
	 * when lists are added to list, add its items to bag
	 * when lists are deleted from list, remove its items from bag
	 * add listener to list so changes are added/removed from bag
	 */
	protected class DefaultListsToFilterList extends DArrayList
	{
		protected DListListener listsToFilterListener;

		public DefaultListsToFilterList()
		{
			listsToFilterListener = createListsToFilterListener();
		}

		protected DListListener createListsToFilterListener()
		{
			return new DefaultListsToFilterListener();
		}

		protected boolean addHookBefore(Object obj)
		{
			return (obj instanceof Filterable);
		}

		protected boolean removeHookBefore(Object obj)
		{
			return (obj instanceof Filterable);
		}

		protected void addHookAfter(Object obj)
		{
			Filterable itemList = (Filterable) obj;
			itemsInListsToFilter.addAll(itemList.addItemsToFilterListener(listsToFilterListener));
			itemList.addDeletionListener(filterableDeletionListener);
		}

		protected void removeHookAfter(Object obj)
		{
			Filterable itemList = (Filterable) obj;
			itemsInListsToFilter.removeAll(itemList.removeItemsToFilterListener(listsToFilterListener));
			itemList.removeDeletionListener(filterableDeletionListener);
		}

		// propagates changes to list to bag
		protected class DefaultListsToFilterListener implements DListListener
		{
			public void intervalChanged(DListEvent e)
			{
			}

			public void intervalAdded(DListEvent e)
			{
				itemsInListsToFilter.addAll(e.getItems());
			}

			public void intervalRemoved(DListEvent e)
			{
				itemsInListsToFilter.removeAll(e.getItems());
			}

			public void itemsRemoved(DListEvent e)
			{
				itemsInListsToFilter.removeAll(e.getItems());
			}

			public void itemsReplaced(DListEvent e)
			{
				List oldNewItems = e.getItems();
				List oldItems = new ArrayList();
				List newItems = new ArrayList();
				for (int i = 0; i < oldNewItems.size(); ++i) {
					if (i % 2 == 0) // old item
						oldItems.add(oldNewItems.get(i));
					else // new item
						newItems.add(oldNewItems.get(i));
				}
				itemsInListsToFilter.addAll(newItems);
				itemsInListsToFilter.removeAll(oldItems);
			}
		}
	}

	// intended to be subclassed by subclasses
	// change listener to check filter function or whatever
	// default behavior has no filter function, creates set of items to watch
	protected class DefaultItemsInListsToFilterListener implements DBagListener
	{
		public void itemAdded(DBagEvent e)
		{
			// could check filter function here
			// or customize list filteredItems
			filteredItems.add(e.getItem());
		}

		public void itemRemoved(DBagEvent e)
		{
			filteredItems.remove(e.getItem());
		}

		public void itemIncremented(DBagEvent e)
		{
		}

		public void itemDecremented(DBagEvent e)
		{
		}

		public void bagEmpty(DBagEvent e)
		{
		}

		public void bagNonEmpty(DBagEvent e)
		{
		}
	}

	/**
	 * If filterable item is deleted, remove from this.
	 */
	protected class DefaultFilterableDeletionListener implements DeletionListener
	{
		public void objectDeleted(DeletionEvent e)
		{
			removeListToFilter((Filterable) e.getSource());
		}
	}

	public String toString()
	{
		return getName() + "\n" + Names.getNameIds(filteredItems);
	}

}
