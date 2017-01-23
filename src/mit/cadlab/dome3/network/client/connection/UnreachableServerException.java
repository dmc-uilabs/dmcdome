// UnreachableServerException.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.connection;

/**
 * Exception is thrown when there is a problem connecting to the server.
 */
public class UnreachableServerException extends RuntimeException
{
	public UnreachableServerException()
	{
	}

	public UnreachableServerException(String message)
	{
		super(message);
	}

	public UnreachableServerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnreachableServerException(Throwable cause)
	{
		super(cause);
	}
}