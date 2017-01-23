// ModelObject.java
package mit.cadlab.dome3.objectmodel.modelobject;

import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DocumentationSupport;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;

/**
 * The ModelObject is an object that exists in a ModelObjectScope.
 * A ModelObject is associated with the model of its ModelObjectScope.
 * The ModelObject's Scope is set at creation time.
 */
public interface ModelObject extends DomeObject, ModelComponent, DocumentationSupport
{

	public ModelObjectScope getScope();

	/**
	 * @param l DeletionListener to be removed
	 * @param deleteIfNoReference true means that objects with no more references are deleted
	 *        false means that objects with no more references are kept
	 * use removeDeletionListener(DeletionListener l) to get noReferenceWarning exceptions
	 */
	public void removeDeletionListener(DeletionListener l, boolean deleteIfNoReference);

	public interface ModelReference extends DeletionListener
	{
		public Model getModel();
	}

}
