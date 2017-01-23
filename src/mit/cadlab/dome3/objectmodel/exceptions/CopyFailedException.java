// CopyFailedException.java
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.util.DomeException;

public class CopyFailedException extends DomeException
{
	public Exception exception;
	public DomeObject container;
	public Object object;

	public CopyFailedException(Exception exception,
	                           DomeObject container, Object object)
	{
		super(exception.toString());
		this.exception = exception;
		this.container = container;
		this.object = object;
	}

}
