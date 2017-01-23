// IllegalTypeException.java
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.util.DomeException;

public class IllegalTypeException extends DomeException
{
	public DomeObject source;
	public Object type;

	public IllegalTypeException(DomeObject source, Object type)
	{
		super(source.getName() + " does not support objects of type " + type.toString());
		this.source = source;
		this.type = type;
	}

}
