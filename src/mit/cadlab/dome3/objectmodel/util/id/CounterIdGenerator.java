// CounterIdGenerator.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.id;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Generates integer ids in numeric sequence.
 */
public class CounterIdGenerator implements IdGenerator
{

	public static final String XML_TAG = "counterIdGenerator";

	// future options: step size
	private String idHeader = "";
	private int nextInt = 0;

	public CounterIdGenerator(Element xmlElement)
	{
		if (xmlElement.getQName().getName().equals(getXmlTag())) {
			idHeader = xmlElement.attributeValue("idHeader");
			if (idHeader == null)
				idHeader = "";
			try {
				nextInt = Integer.parseInt(xmlElement.attributeValue("nextInt"));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("CounterIdGenerator - invalid xml nextInt: " +
				                                   xmlElement.attributeValue("nextInt"));
			}
		} else {
			throw new IllegalArgumentException("CounterIdGenerator - illegal xmlElement: " + xmlElement);
		}
	}

	public CounterIdGenerator()
	{
		// use defaults
	}

	public CounterIdGenerator(String tag, int firstId)
	{
		if (tag != null)
			idHeader = tag;
		nextInt = firstId;
	}

	public CounterIdGenerator(String tag)
	{
		if (tag != null)
			idHeader = tag;
	}

	public CounterIdGenerator(int firstId)
	{
		nextInt = firstId;
	}

	public Id nextId()
	{
		return new Id(idHeader + (nextInt++));
	}

	public Id nextId(String info)
	{
		return new Id(idHeader + info + (nextInt++));
	}

	public String toString()
	{
		return "\"" + idHeader + "\" " + nextInt;
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		Element idGen = DocumentHelper.createElement(getXmlTag());
		if (!idHeader.equals(""))
			idGen.addAttribute("idHeader", idHeader);
		idGen.addAttribute("nextInt", Integer.toString(nextInt));
		return idGen;
	}

}
