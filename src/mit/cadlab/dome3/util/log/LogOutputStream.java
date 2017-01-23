// LogOutputStream.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util.log;

import java.io.ByteArrayOutputStream;

public class LogOutputStream extends ByteArrayOutputStream
{

	LogLevel level;

	public LogOutputStream(LogLevel level)
	{
		if (level == null)
			throw new NullPointerException("LogOutputStream - null level");
		this.level = level;
	}

	public void flush()
	{
		String msg = this.toString();
		if (msg == null || msg.equals("\n") || msg.equals(""))
			return;
		Log.add(new LogRecord(level, msg.trim()));
		this.reset();
	}

}
