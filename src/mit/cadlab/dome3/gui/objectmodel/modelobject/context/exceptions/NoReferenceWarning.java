// NoReferenceWarning.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions;

import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.exceptions.NoReferenceException;
import mit.cadlab.dome3.util.DomeWarning;

public class NoReferenceWarning extends DomeWarning
{
	public ModelObject modelObject;

	public NoReferenceWarning(ModelObject obj)
	{
		super(obj.getNameIdString());
		this.modelObject = obj;
	}

	public NoReferenceWarning(NoReferenceException ex)
	{
		super(ex.modelObject.getNameIdString());
		this.modelObject = ex.modelObject;
	}
}
