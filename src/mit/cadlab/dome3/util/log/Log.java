/*
 * Log.java
 * User: elaine
 * Date: May 16, 2002
 * Time: 3:20:51 PM
 * Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
 */
package mit.cadlab.dome3.util.log;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Log
{

	private static ConsoleLogHandler defaultOut = null;
	private static LogHandler currentLog = null;

	public static ConsoleLogHandler getDefaultLog()
	{
		if (defaultOut == null) {
			//defaultOut = new ConsoleLogHandler(System.out);
			FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
			defaultOut = new ConsoleLogHandler(new PrintStream(new BufferedOutputStream(fdOut, 128), true));
			// redirect System.out and System.err to ConsoleLogHandler?
//			System.setOut(new PrintStream(new LogOutputStream(LogLevel.INFO), true));
//			System.setErr(new PrintStream(new LogOutputStream(LogLevel.ERROR), true));
			//System.out = new PrintStream(new LogOutputStream(LogLevel.INFO));
			//System.err = new PrintStream(new LogOutputStream(LogLevel.ERROR));
		}
		return defaultOut;
	}

	public static void add(LogRecord record)
	{
		getCurrentLog().add(record);
	}

	public static LogHandler getCurrentLog()
	{
		if (currentLog == null) {
			currentLog = getDefaultLog();
		}
		return currentLog;
	}

	public static void setCurrentLog(LogHandler log)
	{
		if (log == null)
			throw new NullPointerException("Log.setCurrentLog -  null LogHandler");
		currentLog = log;
	}

	public static void setDefaultLog()
	{
		currentLog = getDefaultLog();
	}

	public static void error(String msg)
	{
		add(new LogRecord(LogLevel.ERROR, msg));
	}

	public static void error(Throwable error)
	{
		add(new LogRecord(LogLevel.ERROR, error));
	}

	public static void error(String msg, Throwable error)
	{
		add(new LogRecord(LogLevel.ERROR, msg, error));
	}

	public static void warning(String msg)
	{
		add(new LogRecord(LogLevel.WARNING, msg));
	}

	public static void warning(Throwable error)
	{
		add(new LogRecord(LogLevel.WARNING, error));
	}

	public static void warning(String msg, Throwable error)
	{
		add(new LogRecord(LogLevel.WARNING, msg, error));
	}

	public static void info(String msg)
	{
		add(new LogRecord(LogLevel.INFO, msg));
	}

	public static void info(Throwable error)
	{
		add(new LogRecord(LogLevel.INFO, error));
	}

	public static void info(String msg, Throwable error)
	{
		add(new LogRecord(LogLevel.INFO, msg, error));
	}

	public static void debug(String msg)
	{
		add(new LogRecord(LogLevel.DEBUG, msg));
	}

	public static void debug(Throwable error)
	{
		add(new LogRecord(LogLevel.DEBUG, error));
	}

	public static void debug(String msg, Throwable error)
	{
		add(new LogRecord(LogLevel.DEBUG, msg, error));
	}

}
