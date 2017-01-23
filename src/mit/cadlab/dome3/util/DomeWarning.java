// DomeWarning.java
package mit.cadlab.dome3.util;

public class DomeWarning extends DomeException
{

	public DomeWarning()
	{
		super();
	}

	public DomeWarning(String msg)
	{
		super(msg);
	}

	public DomeWarning(Object src, String method, String msg)
	{
		super(src, method, msg);
	}

	public DomeWarning(Object obj, String method, Exception exception)
	{
		super(obj, method, exception);
	}

	public DomeWarning(Object obj, String method, Exception exception, Object methodResult)
	{
		super(obj, method, exception, methodResult);
	}

}
