// TypeInfo.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util;

import java.util.ArrayList;

/**
 * Type names are unique.
 */
public class TypeInfo
{
	private static ArrayList typeNames = new ArrayList();

	private String typeName = "";
	private String xmlType = "";

	public TypeInfo(String typeName, String xmlType)
	{
		if (typeName == null)
			throw new NullPointerException("TypeInfo - null typeName");
		if (typeNames.contains(typeName))
			throw new IllegalArgumentException("TypeInfo - duplicate typeName: " + typeName);
		this.typeName = typeName;
		typeNames.add(typeName);
		if (xmlType != null)
			this.xmlType = xmlType;
	}

	public TypeInfo(String typeName)
	{
		this(typeName, null);
	}

	public String getTypeName()
	{
		return typeName;
	}

	public String getXmlType()
	{
		return xmlType;
	}

}
