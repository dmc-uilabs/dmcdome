// ParameterRuntime.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObjectFactory;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// TODO: update factory to use runtime versions of data objects when available

public abstract class ParameterRuntime extends AbstractParameter
{
	protected static String valueStatusXmlTag = "valueStatus";

	private DataObjectFactory factory;


	public ParameterRuntime(ModelObjectScope scope, Id id, String dataType)
	{
		super(scope, id, dataType);
		initParameter();
	}

	public ParameterRuntime(ModelObjectScope scope, Id id)
	{
		super(scope, id, (String) null);
		initParameter();
	}

	public ParameterRuntime(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);
		initParameter();
	}

	public ParameterRuntime(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
		Element valueStatusNode = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + valueStatusXmlTag);
		if (valueStatusNode != null)
			setValueStatus(valueStatusNode.attributeValue("value"));
		initParameter();
	}

	public DataObjectFactory getDataObjectFactory()
	{
		if (factory == null) {
			factory = createDataObjectFactory();
		}
		return factory;
	}

	protected DataObjectFactory createDataObjectFactory() {
		return new DataObjectBaseFactory();
	}

	protected void addXmlContent(Element xmlElement)
	{
		super.addXmlContent(xmlElement);
		xmlElement.addElement(valueStatusXmlTag).addAttribute("value", getValueStatus());
	}

	class DataObjectBaseFactory implements DataObjectFactory
	{
		public DataObject newInstance(String dObjType)
		{
			try {
				return (DataObject) Registry.getConstructor(dObjType, Registry.BASE_CLS).newInstance(null);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}

		public DataObject newInstance(DataObject dObj)
		{
			try {
				return (DataObject) Registry.getConstructor(dObj, Registry.BASE_CLS).newInstance(new Object[]{dObj});
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}

		public DataObject newInstance(Element xml)
		{
			try {
				Constructor ctr = Registry.getConstructor(xml, Registry.BASE_CLS);
				return (DataObject) ctr.newInstance(new Object[]{xml});
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}
	}

}
