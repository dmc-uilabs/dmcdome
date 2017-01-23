// AbstractDomeObjectFactory.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DomeException;
import org.dom4j.Element;

import java.lang.reflect.Constructor;

public abstract class AbstractDomeObjectFactory implements DomeObjectFactory
{

	public abstract String getClassType();

	protected Object newInstance(Constructor ctr, Object[] params)
	{
		try {
			return ctr.newInstance(params);
		} catch (Exception ex) {
			throw new DomeException(ctr, "newInstance", ex);
		}
	}

	public Model createModel(Id id, String modelType)
	{
		return null;
	}

	public Model createModel(Id id, Model model)
	{
		return null;
	}

	public Model createModel(Element xml)
	{
		return null;
	}

	public ModelObject createModelObject(ModelObjectScope scope, Element xml)
	{
		String xmlTag = xml.getQName().getName();
		if (Parameter.XML_TAG.equals(xmlTag)) {
			return createParameter(scope, xml);
		} else if (Context.XML_TAG.equals(xmlTag)) {
			return createContext(scope, xml);
		} else if (Relation.XML_TAG.equals(xmlTag)) {
			return createRelation(scope, xml);
		} else {
			throw new IllegalArgumentException("createModelObject - unknown xml tag: " + xmlTag);
		}
	}

	public Parameter createParameter(ModelObjectScope scope, Id id, String dataType)
	{
		return null;
	}

	public Parameter createParameter(ModelObjectScope scope, Id id, Parameter param)
	{
		return null;
	}

	public Parameter createParameter(ModelObjectScope scope, Element xml)
	{
		return null;
	}

	public Relation createRelation(ModelObjectScope scope, Id id, String relationType)
	{
		return null;
	}

	public Relation createRelation(ModelObjectScope scope, Id id, Relation rel)
	{
		return null;
	}

	public Relation createRelation(ModelObjectScope scope, Element xml)
	{
		return null;
	}

	public Context createContext(ModelObjectScope scope, Id id)
	{
		return null;
	}

	public Context createContext(ModelObjectScope scope, Id id, Context cxt)
	{
		return null;
	}

	public Context createContext(ModelObjectScope scope, Element xml)
	{
		return null;
	}

	public DataObject createDataObject(String dataObjectType)
	{
		return null;
	}

	public DataObject createDataObject(DataObject data)
	{
		return null;
	}

	public DataObject createDataObject(Element xml)
	{
		return null;
	}

	public Documentation createDocumentation()
	{
		return null;
	}
}
