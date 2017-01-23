// DomeObjectFactory.java
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
import org.dom4j.Element;

/**
 * This factory allows one to create instances of most types of objects in Dome.
 * This includes models, modelobjects, dataobjects, documentation, and maybe other things
 * which have mode-dependent versions.
 */
public interface DomeObjectFactory
{

	/**
	 * obj is either object type name, type symbol, or object instance
	 * params must be the parameters to the constructor method in correct order
	 */
	public Object newInstance(Object obj, Object[] params);

	public Model createModel(Id id, String modelType);

	public Model createModel(Id id, Model model);

	public Model createModel(Element xml);

	public ModelObject createModelObject(ModelObjectScope scope, Element xml);

	public Parameter createParameter(ModelObjectScope scope, Id id, String dataType);

	public Parameter createParameter(ModelObjectScope scope, Id id, Parameter param);

	public Parameter createParameter(ModelObjectScope scope, Element xml);

	public Relation createRelation(ModelObjectScope scope, Id id, String relationType);

	public Relation createRelation(ModelObjectScope scope, Id id, Relation rel);

	public Relation createRelation(ModelObjectScope scope, Element xml);

	public Context createContext(ModelObjectScope scope, Id id);

	public Context createContext(ModelObjectScope scope, Id id, Context cxt);

	public Context createContext(ModelObjectScope scope, Element xml);

	public DataObject createDataObject(String dataObjectType);

	public DataObject createDataObject(DataObject data);

	public DataObject createDataObject(Element xml);

	public Documentation createDocumentation();

}
