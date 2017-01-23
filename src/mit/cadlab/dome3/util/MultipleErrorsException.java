// MultipleErrorsException.java
package mit.cadlab.dome3.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Class for "throwing" multiple exceptions at once.
// Could also hold multiple error messages.

public class MultipleErrorsException extends DomeException
{

	protected List errors;
	protected String msg;

	public MultipleErrorsException(String msg, List objects)
	{
		super(makeMessage(msg, objects));
		this.msg = msg;
		if (objects == null)
			errors = new ArrayList();
		else
		//errors = Collections.unmodifiableList(objects); sometimes want to change
			errors = objects;
	}

	public MultipleErrorsException(List objects)
	{
		this(null, objects);
	}

	protected static String makeMessage(String msg, List objects)
	{
		if (objects == null || objects.isEmpty())
			return (msg == null) ? "no errors" : msg + " -- no errors";
		StringBuffer sb = new StringBuffer((msg == null) ? "errors -- " : msg + " -- ");
		Iterator it = objects.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj == null) {
				sb.append("\n  *null*");
			} else {
				sb.append("\n  " + obj);
			}
		}
		return sb.toString();
	}

	public List getErrorList()
	{
		return errors;
	}

}
