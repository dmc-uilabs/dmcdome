/*
 * LogHandler.java
 * User: elaine
 * Date: May 17, 2002
 * Time: 7:32:16 AM
 * Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
 */
package mit.cadlab.dome3.util.log;


public abstract class LogHandler
{

	protected LogLevel level = LogLevel.ALL;
	protected String name = null;

	public LogHandler(String name)
	{
		this.name = (name == null) ? "LogHandler" : name;
	}

	public void setLevel(LogLevel level)
	{
		if (level == null)
			throw new NullPointerException("LogLevel.setLevel - null level");
		this.level = level;
	}

	public LogLevel getLevel()
	{
		return level;
	}

	public void add(LogRecord record)
	{
		if (record.getLevel().compareTo(getLevel()) == -1)
			return;
		log(record);
	}

	public String getName()
	{
		return name;
	}


	protected abstract void log(LogRecord record);

}
