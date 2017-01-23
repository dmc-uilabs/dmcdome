// ConsoleLogHandler.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util.log;

import java.io.PrintStream;

public class ConsoleLogHandler extends LogHandler
{
	private PrintStream output;

	public ConsoleLogHandler(PrintStream outStream)
	{
		super("ConsoleLogHandler");
		if (outStream == null)
			throw new NullPointerException("ConsoleLogHandler - null PrintStream");
		this.output = outStream;
	}

	protected void log(LogRecord record)
	{
		output.println(record.toString());
	}

}
