/*
 * LogRecord.java
 * User: elaine
 * Date: May 16, 2002
 * Time: 1:31:26 PM
 * Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
 */
package mit.cadlab.dome3.util.log;

import java.text.DateFormat;
import java.util.Date;

public class LogRecord
{
	private static DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	private LogLevel level;
	private String msg = "";
	private Throwable error = null;
	private String date;

	public LogRecord(LogLevel level)
	{
		if (level == null)
			throw new NullPointerException("LogRecord constructor - null level");
		this.level = level;
		date = df.format(new Date());
	}

	public LogRecord(LogLevel level, String msg)
	{
		this(level);
		if (msg == null) return;
		this.msg = msg;
	}

	public LogRecord(LogLevel level, Throwable error)
	{
		this(level);
		this.error = error;
	}

	public LogRecord(LogLevel level, String msg, Throwable error)
	{
		this(level, msg);
		this.error = error;
	}

	public LogLevel getLevel()
	{
		return level;
	}

	public String getMsg()
	{
		return msg;
	}

	public Throwable getError()
	{
		return error;
	}

	public String getString()
	{
		if (msg.equals("")) {
			if (error == null) {
				return date;
			} else { // error exists
				return error.toString();
			}
		} else {
			if (error == null) {
				return msg;
			} else { // error exists
				return msg + "\n  " + error;
			}
		}
	}

	public String toString()
	{
		if (msg.equals("")) {
			if (error == null) {
				return level.getName();
			} else { // error exists
				return level.getName() + ": " + error;
			}
		} else {
			if (error == null) {
				return level.getName() + ": " + msg;
			} else { // error exists
				return level.getName() + ": " + msg + "\n  " + error;
			}
		}
	}

}
