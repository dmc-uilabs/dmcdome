// DuplicateContentError.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.util.DomeError;

public class DuplicateContentError extends DomeError
{
	public DomeObject container;
	public Object object;

	public DuplicateContentError(String description,
	                             DomeObject container, Object object)
	{
		super(description);
		this.container = container;
		this.object = object;
	}

}
