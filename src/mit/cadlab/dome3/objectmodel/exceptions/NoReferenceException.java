// NoReferenceException.java
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.util.DomeException;

public class NoReferenceException extends DomeException
{
	public ModelObject modelObject;

	public NoReferenceException(ModelObject obj)
	{
		super(obj.getNameIdString());
		this.modelObject = obj;
	}
}
