// Mapping.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// wrap this class to get specific kinds of mappings (type-checking)

public abstract class Mapping implements XMLSupport
{

	public static final String XML_TAG = "mapping";
	protected DomeObject mappedObj;
	protected List mappings;

	protected Mapping(DomeObject obj)
	{
		if (obj == null)
			throw new NullPointerException();
		this.mappedObj = obj;
		this.mappings = new ArrayList();
	}

	public boolean isEmpty()
	{
		return mappings.isEmpty();
	}

	protected boolean addMapping(DomeObject obj)
	{
		if (mappings.contains(obj)) { // duplicates are ignored
			return false;
		}
		mappings.add(obj);
		return true;
	}

	protected boolean removeMapping(DomeObject obj)
	{
		return mappings.remove(obj);
	}

	protected Collection addMappings(Collection objs, Class objType)
	{
		Collection addedObjs = new ArrayList();
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if ((objType == null || objType.isInstance(obj)) && addMapping((DomeObject) obj)) {
				addedObjs.add(obj);
			}
		}
		return addedObjs;
	}

	protected Collection removeMappings(Collection params, Class objType)
	{
		Collection removedObjs = new ArrayList();
		boolean changed = false;
		Iterator it = new ArrayList(params).iterator();    //to avoid concurrentModificationException
		while (it.hasNext()) {
			Object obj = it.next();
			if ((objType == null || objType.isInstance(obj)) && removeMapping((DomeObject) obj)) {
				removedObjs.add(obj);
			}
		}
		return removedObjs;
	}

	protected Object getMappedObject()
	{
		return mappedObj;
	}

	public Collection getMappings()
	{
		return Collections.unmodifiableList(mappings);
	}

	/*
	public Collection getMappings(boolean isModelParameter)
	{
		if (isModelParameter)
		{
			List paramMapping = new ArrayList(mappings.size());
			for (Iterator i = mappings.iterator(); i.hasNext();) {
				Parameter param = (Parameter) i.next();
				//Do not show interface param mapping in model
				if (!(param.getScope() instanceof ModelInterface))
				{
					paramMapping.add(param);
				}
			}
			return Collections.unmodifiableList(paramMapping);
		}
		else {
			return Collections.unmodifiableList(mappings);
		}
	}
	*/

	protected boolean isMappedTo(DomeObject obj)
	{
		return mappings.contains(obj);
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(getXmlTag()).addAttribute("idRef", mappedObj.getId().getIdString());
		XMLUtils.addCollectionRef(xml, null, mappings);
		return xml;
	}

	public String toString()
	{
		return Names.getNameId(mappedObj) + " --> " + Names.getNameIds(mappings);
	}

}
