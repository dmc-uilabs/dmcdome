// ModelObjectScope.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DocumentationSupport;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collection;
import java.util.List;

/**
 * A ModelObjectScope defines the environment in which a ModelObject exists.
 * It is always associated with a particular Model.
 */
public interface ModelObjectScope extends DomeObject, ModelComponent, DocumentationSupport
{

	/**
	 * Retrieve model object from scope by model object ID
	 */
	public ModelObject getModelObjectById(Id id);

	/**
	 * Returns immutable collection of model objects in this model
	 */
	public Collection getModelObjects();

	public int countModelObjects();

	/**
	 * Returns list of valid ModelObject types.
	 * Return from modelobject factory.
	 */
	public List getValidModelObjectTypes();

	public boolean isValidModelObjectType(String modelObjectType);

	public boolean isValidModelObjectType(ModelObject modelObject);

	/**
	 * Creates a new ModelObject of the specified type and adds it to the model.
	 *
	 * @param modelObjectType the String identifier for the ModelObject type
	 * @return the ModelObject created; null if object creation not possible
	 */
	public ModelObject newModelObject(String modelObjectType);

	/**
	 * Creates a new ModelObject copy of given object and adds it to the model.
	 *
	 * @param modelObject the object to be copied
	 * @return the ModelObject created; null if object creation not possible
	 */
	public ModelObject newModelObject(ModelObject modelObject);

	public ModelObject newModelObject(ModelObject modelObject, boolean deepCopy);

	public Collection newModelObjects(Collection modelObjects);

	public Collection newModelObjects(Collection modelObjects, boolean deepCopy);

	/**
	 * Removes specified object from model. Ignores object if not in model.
	 * Object must have model as its model.
	 *
	 * @param ModelObject the object to be removed from the model
	 */
	public void deleteModelObject(ModelObject mObj);

	public void deleteModelObjects(Collection modelObjects);

	public void deleteAllModelObjects();

	/**
	 * Adds a listener which is notified when ModelObjects
	 * are added/removed from the Model.
	 */
	public void addModelObjectsListener(DListListener listener);

	/**
	 * Removes listener which is notified when ModelObjects
	 * are added/removed from the Model.
	 */
	public void removeModelObjectsListener(DListListener listener);

	public ModelObjectFactory getModelObjectFactory();

}
