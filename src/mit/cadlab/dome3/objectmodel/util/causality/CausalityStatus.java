// CausalityStatus.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CausalityStatus
{

	// Causality is managed in each scope -- each relation and each model

	// Independent is parameter which is not driven by anything in scope, free to change
	// Model parameter is independent if not mapped to anything or only mapped to relation input
	// Relation parameter is independent if it is not mapped to anything
	public static final CausalityStatus INDEPENDENT = new CausalityStatus("Independent");

	// Intermediate is parameter driven by output of relation and used as input to relation
	public static final CausalityStatus INTERMEDIATE = new CausalityStatus("Intermediate");

	// Result is parameter driven by output of relation and not used as input to anything else
	public static final CausalityStatus RESULT = new CausalityStatus("Result");

	// Causality can not be determined at the time.
	// Determined by type of parameter.
	public static final CausalityStatus INDETERMINATE = new CausalityStatus("Indeterminate");

	private String name;

	/**
	 * Can only be created here.
	 */
	private CausalityStatus(String name)
	{
		this.name = name;
	}

	public static Element toXmlElement(CausalityStatus status)
	{
		Element xmlElement = DocumentHelper.createElement("status");
		xmlElement.addText(status.name);
		return xmlElement;
	}

	public static CausalityStatus parseXml(Element xmlElement)
	{
		Element statusElement = (Element) xmlElement.selectSingleNode("status");
		if (statusElement == null) {
			throw new IllegalArgumentException("parameter has no causality status");
		}
		String status = statusElement.getText();
		if (status == null) {
			throw new IllegalArgumentException("parameter has no causality status");
		}
		return new CausalityStatus(status);
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof CausalityStatus)
			return name.equals(((CausalityStatus) obj).name);
		return false;
	}

	public String toString()
	{
		return name;
	}
}
