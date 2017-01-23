// DuplicateContentException.java
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.util.DomeException;

public class DuplicateContentException extends DomeException
{
	public DomeObject container;
	public Object object;

	public DuplicateContentException(String description,
	                                 DomeObject container, Object object)
	{
		super(description);
		this.container = container;
		this.object = object;
	}

}
