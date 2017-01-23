// Context.java
package mit.cadlab.dome3.objectmodel.modelobject.context;

import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collection;
import java.util.List;

/**
 * Contexts are used to organize ModelObjects.
 * They can be used to create hierarchical structures
 * or any kind of groupings/associations.
 * Contexts contain references to the ModelObjects.
 */
public interface Context extends ModelObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Context");
	public static final String XML_TAG = "context";
	public static final String XML_MAPPED_TAG = "mappedContext";

	public boolean isEmpty();

	/**
	 * Removes all the model object references
	 */
	public void clear();

	/**
	 * @param mObj
	 * @return true if this context contains a reference to the specified object
	 */
	public boolean containsReference(ModelObject mObj);

	/**
	 * returns set of all items in this context, once per item, recursive
	 */
	public Collection getFlattenedContentSet();

	/**
	 * Determines if the object is a descendant of this context.
	 *
	 * @param mObj the ModelObject in question
	 * @return true if mObj is descendant of context; false, otherwise
	 */
	public boolean isContextDescendant(ModelObject mObj);

	public List getModelObjectReferences();

	/**
	 * Add a reference to the ModelObject to this Context
	 * Duplicate references are not permitted.
	 * Objects must belong in same model.
	 */
	public void addModelObjectReference(ModelObject mObj);

	public void addModelObjectReferences(Collection modelObjects);

	/**
	 * Remove a reference to the ModelObject in this Context.
	 * References not found are ignored.
	 */
	public void removeModelObjectReference(ModelObject mObj);

	public void removeModelObjectReferences(Collection modelObjects);

	/**
	 * Adds a listener which is notified when references
	 * in the context are added/removed.
	 */
	public void addModelObjectReferencesListener(DListListener l);

	/**
	 * Removes listener which is notified when references
	 * in the context are added/removed.
	 */
	public void removeModelObjectReferencesListener(DListListener l);

	/**
	 * method from Destructor interface
	 * Deletes this Context and queries on no reference warnings.
	 */
	public void delete(DeletionListener notifier);

	/**
	 * Deletes this Context and boolean tells whether to keep or delete objects
	 *
	 */
	public void delete(DeletionListener notifier, boolean deleteNoReferenceObjects);

	/**
	 * Deletes this Context and all of its contents recursively.
	 */
	public void recursiveDelete(DeletionListener notifier);

}
