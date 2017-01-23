// AbstractEventFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * To do by subclasses:
 * in constructor, add listener to event of interest and
 * implement a processEvent() method to process the changes
 */
public abstract class AbstractEventFilter extends AbstractDomeObject implements Filter
{

	protected Model model;
	protected DArrayList filteredItems; // items in filter

	public AbstractEventFilter(Model m, Id id, String name)
	{
		super(id, name);
		this.model = m;
		filteredItems = createFilteredItemsList();
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
	protected DArrayList createFilteredItemsList()
	{
		return new DSet();
	}

	// ModelComponent interface
	public Model getModel()
	{
		return model.getModel();
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
		return Collections.EMPTY_LIST;
	}

	public void addListToFilter(Filterable listOfItems)
	{
		throw new UnsupportedOperationException();
	}

	public void removeListToFilter(Filterable listOfItems)
	{
		throw new UnsupportedOperationException();
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

	public String toString()
	{
		return getName() + "\n" + Names.getNameIds(filteredItems);
	}

}
