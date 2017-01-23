package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.util.Names;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.xml.XMLSupport;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;

import org.dom4j.Element;

/**
 * This node represents multiple items as a single item.
 */
public class MultiItemNode implements XMLSupport
{
	protected String name;
	protected String id;
	List items;

	public MultiItemNode(String id, String name, List objects)
	{
		this.id = id;
		this.name = name;
		if (objects == null || objects.isEmpty() || objects.size() == 1)
			throw new IllegalArgumentException("MultiItemNode must contain more than one object: " + objects);
		this.items = new ArrayList(objects);
	}

	public MultiItemNode(String id, List objects)
	{
		this(id, Names.getNames(objects), objects);
	}

	public MultiItemNode(List objects)
	{
		this(UUIDGenerator.create(), Names.getNames(objects), objects);
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public String getNameNoBrackets()
	{ // for graph displays
		int startIndex = name.startsWith("[") ? 1 : 0;
		int endIndex = name.length();
		if (name.endsWith("]"))
			endIndex--;
		return name.substring(startIndex, endIndex);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int hashCode()
	{
		return id.hashCode();
	}

	public List getItems()
	{
		return Collections.unmodifiableList(items);
	}

	public boolean isInNode(Object obj)
	{
		return items.contains(obj);
	}

	public int getNumberItems()
	{
		return items.size();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof MultiItemNode)
			return this.getId().equals(((MultiItemNode) obj).getId()) &&
			        containsSameItems(((MultiItemNode) obj).getItems(), getItems());
		else
			return false;
	}

	private boolean containsSameItems(List list1, List list2)
	{
		if (list1.size() != list2.size())
			return false;
		Collection union = DSet.union(list1, list2);
		return union.size() == list1.size();
	}

	public String toString()
	{
		return getName() + "(" + getId() + ")";
	}

	public String getXmlTag()
	{
		return "MultiItemNode";
	}

	public Element toXmlElement()
	{
		return DirectedGraph.toXmlRef(getXmlTag(), this);
	}

}
