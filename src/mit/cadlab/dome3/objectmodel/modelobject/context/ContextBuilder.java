// ContextBuilder.java
package mit.cadlab.dome3.objectmodel.modelobject.context;

import mit.cadlab.dome3.gui.objectmodel.ShiftSupport;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;

import java.util.Collection;

public interface ContextBuilder extends Context, ShiftSupport
{

	public void addNewModelObject(String modelObjectType);

	public void addNewModelObject(String modelObjectType, int index);

	public void addModelObjectReference(ModelObject modelObject, int index);

	public void addModelObjectReferences(Collection modelObjects, int index);

	public void addModelObjectCopy(ModelObject modelObject, boolean deepCopy);

	public void addModelObjectCopy(ModelObject modelObject, int index, boolean deepCopy);

	public Collection addModelObjectCopies(Collection modelObjects, boolean deepCopy);

	public void addModelObjectCopies(Collection modelObjects, int index, boolean deepCopy);

	public void deleteModelObject(ModelObject modelObject);

	public void deleteModelObjects(Collection modelObjects);

}
