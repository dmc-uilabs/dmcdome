// AbstractDataObject.java
package mit.cadlab.dome3.objectmodel.dataobject;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

/**
 * Partial implementation of DataObject interface with JavaBean support.
 * to do by subclasses:
 * public String getType();
 * public DataObject duplicate();
 */
public abstract class AbstractDataObject extends DomeJavaBean
        implements DataObject
{

	protected PropertyChangeListener shadowListener = null;

	protected AbstractDataObject()
	{
	}

	protected AbstractDataObject(Element xmlElement)
	{
		if (xmlElement.getQName().getName().equals(getXmlTag())) {
			String typeName = getTypeName();
			if (!xmlElement.attributeValue("type").equalsIgnoreCase(typeName))
				throw new IllegalArgumentException("DataObject - invalid xml type: " +
				                                   xmlElement.attributeValue("type"));
			String xp = xmlElement.getStringValue();
			XMLUtils.makeRootElement(xmlElement); // necessary to use XPath locally
		} else {
			throw new IllegalArgumentException("DataObject - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	/**
	 * a generic get method
	 * @return List of data values
	 */
	public List getValues()
	{
		return Collections.EMPTY_LIST;
	}

	public Object getValuesForXmlRpcUse ()
	{
		return null;
	}


	/**
	 * A generic set method
	 * @param values List of data values
	 */
	public void setValues(List values)
	{
	}

	public void setValues(DataObject obj)
	{
	}

	public PropertyChangeListener getValueShadowListener()
	{
		if (shadowListener == null)
			shadowListener = createValueShadowListener();
		return shadowListener;
	}

	public PropertyChangeListener getValueUnitShadowListener()
	{
		if (shadowListener == null)
			shadowListener = createValueUnitShadowListener();
		return shadowListener;
	}

	protected abstract PropertyChangeListener createValueShadowListener();

	protected abstract PropertyChangeListener createValueUnitShadowListener();

	// DataObject interface
	protected abstract TypeInfo getTypeInfo();

	public String getTypeName()
	{
		return getTypeInfo().getTypeName();
	}

	public String getXmlType()
	{
		return getTypeInfo().getXmlType();
	}

	public DataObject getDataObject()
	{
		return this;
	}

	public String toString()
	{
		return getTypeName();
	}

	public String getXmlTag()
	{
		return DataObject.XML_TAG;
	}

	public Element toXmlElement()
	{
		return DocumentHelper.createElement(getXmlTag()).addAttribute("type", getXmlType());
	}

	// RegistrySupport interface
	public String getRegistryKey()
	{
		String xmlType = getXmlType();
		if (xmlType.equals(""))
			return getXmlTag();
		else
			return getXmlTag() + "." + xmlType;
	}

	// convenience functions
	protected void handleException(String methodName, Exception ex)
	{
		if (ex instanceof java.lang.reflect.InvocationTargetException) {
			System.err.println(methodName + " error:");
			ex.printStackTrace();
		} else {
			System.err.println(methodName + ": " + ex);
		}
	}


	public Unit getUnit()
	{
		return null;
	}
    
}
