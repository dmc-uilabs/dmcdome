// ServerMethodException.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.connection;

/**
 * Exception thrown by a method on the server.
 */
public class ServerMethodException extends RuntimeException
{
	int code;

	public ServerMethodException(int code)
	{
		this.code = code;
	}

	public ServerMethodException(int code, String message)
	{
		super(message);
		this.code = code;
	}

	public ServerMethodException(int code, String message, Throwable cause)
	{
		super(message, cause);
		this.code = code;
	}

	public ServerMethodException(int code, Throwable cause)
	{
		super(cause);
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}
}
