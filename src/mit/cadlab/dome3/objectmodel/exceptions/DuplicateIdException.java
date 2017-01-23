// DuplicateIdException.java
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.util.DomeException;

public class DuplicateIdException extends DomeException
{
	public DomeObject container;
	public Object object;

	public DuplicateIdException(String description,
	                            DomeObject container, Object object)
	{
		super(description);
		this.container = container;
		this.object = object;
	}

}
