//
//  LogLevel.java
//  Copyright (c) 2002 MIT. All rights reserved.
//
package mit.cadlab.dome3.util.log;

import java.util.ArrayList;
import java.util.Iterator;

public class LogLevel implements Comparable
{

	private static ArrayList levels = new ArrayList();

	public static final LogLevel OFF = new LogLevel("OFF", Integer.MAX_VALUE);
	public static final LogLevel ERROR = new LogLevel("ERROR", 1000);
	public static final LogLevel WARNING = new LogLevel("WARNING", 900);
	public static final LogLevel INFO = new LogLevel("INFO", 800);
	public static final LogLevel DEBUG = new LogLevel("DEBUG", 700);
	public static final LogLevel ALL = new LogLevel("ALL", Integer.MIN_VALUE);

	private String name;
	private int level;

	public LogLevel(String name, int level)
	{
		if (name == null)
			throw new NullPointerException("LogLevel constructor - name is null");
		Iterator it = levels.iterator();
		while (it.hasNext()) {
			LogLevel l = (LogLevel) it.next();
			if (l.name.equals(name))
				throw new IllegalArgumentException("LogLevel constructor - duplicate name: " + name);
			if (l.level == level)
				throw new IllegalArgumentException("LogLevel constructor - duplicate level: " + level);
		}
		this.name = name;
		this.level = level;
		LogLevel.levels.add(this);
	}

	public String getName()
	{
		return name;
	}

	public int getLevel()
	{
		return level;
	}

	public String toString()
	{
		return name + " (" + level + ")";
	}

	public int compareTo(Object obj)
	{
		if (obj instanceof LogLevel) {
			LogLevel otherLevel = (LogLevel) obj;
			if (this.level < otherLevel.level) {
				return -1;
			} else if (this.level > otherLevel.level) {
				return 1;
			} else {
				return 0;
			}
		} else {
			throw new ClassCastException("can not compare LogLevel instance to " + obj.getClass().getName());
		}
	}
}
