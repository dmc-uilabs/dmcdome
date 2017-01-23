// DomeException.java
package mit.cadlab.dome3.util;

/**
 * Base class for exceptions thrown by Dome.
 * Convenience methods for including class and method names of where exception was thrown.
 * Can be used as exception wrapper.
 */
public class DomeException extends RuntimeException
{

	protected Object source, methodResult;
	protected Exception exception;

	public DomeException()
	{
		super();
	}

	public DomeException(String msg)
	{
		super(msg);
	}

	public DomeException(Object src, String method, String msg)
	{
		super(ClassUtils.getClassName(src) + "." + method + " - " + msg);
		this.source = src;
	}

	public DomeException(Object src, String method, Exception exception)
	{
		super(ClassUtils.getClassName(src) + "." + method + " - " + exception.toString());
		this.source = src;
		this.exception = exception;
	}

	public DomeException(Object src, String method, String msg, Exception exception)
	{
		super(ClassUtils.getClassName(src) + "." + method + " - " + msg + "\n  " + exception.toString());
		this.source = src;
		this.exception = exception;
	}

	public DomeException(Object src, String method, Exception exception, Object methodResult)
	{
		this(src, method, exception);
		this.exception = exception;
		this.methodResult = methodResult;
	}

	public Object getSource()
	{
		return source;
	}

	public Exception getException()
	{
		return exception;
	}

	public Object getMethodResult()
	{
		return methodResult;
	}

	public String toString()
	{
		String msg = getMessage();
		return ClassUtils.getClassName(this) + (msg == null ? "" : ": " + msg);
	}

}
