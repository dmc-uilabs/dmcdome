/*
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Oct 29, 2002
 * Time: 2:08:28 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.objectmodel.dataobject;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.exolab.ID.UUIDGenerator;

public class EnumerationItem
{
	/**
	 *  for the name & value pair of enumeration item
	 */
	protected String name;
	protected Object obj;

	public EnumerationItem()
	{
		name= getId();
        obj = new String("");

	}

	public EnumerationItem(Element xmlElement)
	{
		name = xmlElement.attributeValue("name");
		String type = xmlElement.attributeValue("type");
		String value = xmlElement.getText();
		if (type.equalsIgnoreCase("integer")) {
			obj = new Integer(value);
		} else if (type.equalsIgnoreCase("real")) {
			obj = new Double(value);
		} else if (type.equalsIgnoreCase("boolean")) {
			obj = new Boolean(value);
		} else if (type.equalsIgnoreCase("string")) {
			obj = value;
		}
	}

	public EnumerationItem(Object d)
	{
		name=getId();
        obj = d;
	}

	public EnumerationItem(String n, Object d)
	{
		name = n;
		obj = d;
	}

    public String getId(){
        return UUIDGenerator.create();
    }

	public Object getValue()
	{
		return obj;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		if (obj == "") {
			return name;
		}
		return name + "(" + obj.toString() + ")";
	}

	public Element toXmlElement()
	{
		String type = "Unknown";
		Element xml = DocumentHelper.createElement("dataobject");
		if (obj instanceof Integer) {
			type = "Integer";
		} else if (obj instanceof Double) {
			type = "Real";
		} else if (obj instanceof Boolean) {
			type = "Boolean";
		} else if (obj instanceof String) {
			type = "String";
		}
		xml.addAttribute("name", name);
		xml.addAttribute("type", type);
		xml.addText(obj.toString());
		return xml;
	}
}
