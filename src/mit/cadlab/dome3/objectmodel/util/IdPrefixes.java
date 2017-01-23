// IdPrefixes.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.util.xml.XMLSupport;

public class IdPrefixes
{

	public static String get(Object obj)
	{
		String xmlTag;
		if (obj instanceof String) {
			xmlTag = getXmlTag(Registry.getRegistryKey((String) obj));
		} else if (obj instanceof XMLSupport) {
			xmlTag = ((XMLSupport) obj).getXmlTag();
		} else {
			return "";
		}
		if (Model.XML_TAG.equals(xmlTag))
			return "MDL";
		else if (Relation.XML_TAG.equals(xmlTag))
			return "REL";
		else if (Parameter.XML_TAG.equals(xmlTag))
			return "PARM";
		else if (Context.XML_TAG.equals(xmlTag))
			return "CXT";
		else if (Filter.XML_TAG.equals(xmlTag))
			return "FLTR";
		return "";
	}

	private static String getXmlTag(String registryKey)
	{
		if (registryKey == null)
			return "";
		int period = registryKey.indexOf(".");
		if (period == -1)
			return registryKey;
		else
			return registryKey.substring(0, period);
	}

}
