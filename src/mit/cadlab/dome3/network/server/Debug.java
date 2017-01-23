// Debug.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server;

public class Debug
{
	public static final int OFF = 0; // disables all printing
	public static final int STATUS = 10; // for important status messages
	public static final int ERROR = 20; // for errors
	public static final int METHOD = 30; // for method name tracing
	public static final int DETAIL = 40; // for details in method
	public static final int ALL = 50; // typically set to this value and change to more important values when necessary

	private static int debugLevel = ALL;

	public static int getDebugLevel()
	{
		return debugLevel;
	}

	public static void setDebugLevel(int level)
	{
		if (level < 0)
			debugLevel = 0;
		else if (level > ALL)
			debugLevel = ALL;
		else
			debugLevel = level;
	}

	public static void trace(int msgDebugLevel, String msg)
	{
		if (debugLevel != OFF) {
			if (msgDebugLevel <= debugLevel) {
				if (msgDebugLevel == ERROR)
					System.err.println(msg);
				else
					System.out.println(msg);
			}
		}
	}

}
