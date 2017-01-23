/*
 *  Copyright (c) 2000, Jacob Smullyan.
 *
 *  This is part of SkunkDAV, a WebDAV client.  See http://skunkdav.sourceforge.net/ 
 *  for the latest version.
 * 
 *  SkunkDAV is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 * 
 *  SkunkDAV  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with SkunkDAV; see the file COPYING.  If not, write to the Free
 *  Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
*/

package org.skunk.trace;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.MessageFormat;
import java.util.*;

public class Debug
{
    private static Properties debugProperties;
    public static final String DEBUG_PROPERTIES="debug.properties";
    public static final String DEBUG_LEVEL_PROPERTY="debugLevel";
    public static final String LOG_FILE_PROPERTY="logFile";
    public static final String DEFAULT_DEBUG_LEVEL_PROPERTY="defaultDebugLevel";
    public static final String DEFAULT_LOG_FILE_PROPERTY="defaultLogFile";
    public static final String DEBUG_PROPERTIES_FILE="debugPropertiesFile";
    public static final int DEFAULT_DEBUG_LEVEL=5;
    public static final String DEFAULT_LOG_FILE=null;

    public static final DebugPriority DP0=new DebugPriority(0);
    public static final DebugPriority DP1=new DebugPriority(1);
    public static final DebugPriority DP2=new DebugPriority(2);
    public static final DebugPriority DP3=new DebugPriority(3);
    public static final DebugPriority DP4=new DebugPriority(4);
    public static final DebugPriority DP5=new DebugPriority(5);
    public static final DebugPriority DP6=new DebugPriority(6);
    public static final DebugPriority DP7=new DebugPriority(7);
    public static final DebugPriority DP8=new DebugPriority(8);
    public static final DebugPriority DP9=new DebugPriority(9);

    //change this for production code
    public static final boolean DEBUG=true;
    
    static
    {
	initProperties();
    }
    public static boolean isDebug(Object caller, DebugPriority priority)
    {
	Class daClass=(caller instanceof Class)? (Class) caller : caller.getClass();
	return (priority.getPriority()<=getDebugLevel(daClass));
    }

    public static void trace(Object caller, DebugPriority priority, 
			     String formattedMessage, Object interpolations)
    {
	//permit the interpolations argument to be either an array or a single object
	Object[] interArray=(interpolations instanceof Object[])
	    ? (Object[]) interpolations
	    : new Object[] { interpolations };
	    
	Class daClass=(caller instanceof Class)? (Class) caller : caller.getClass();
	if (isDebug(daClass, priority))
	{
	    String message=MessageFormat.format(formattedMessage, interArray);
	    _trace(daClass, priority, message);
	}
    }
	    
    public static void trace(Object caller, DebugPriority priority, Object message)
    {
	Class daClass=(caller instanceof Class)? (Class) caller : caller.getClass();
	if (priority.getPriority()>getDebugLevel(daClass)) return;
	_trace(daClass, priority, message);
    }

    private static void _trace(Class daClass, DebugPriority priority, Object message)
    {
	
	Date currentDate=new Date();
	String logFileStr=getLogFile(daClass);
	/*if no log file is defined for that package,
	  default to System.out and System.err */
	if (logFileStr!=null)
	{
	    PrintWriter outWriter=null;
	    try
	    {
		File f=new File(logFileStr);
		if (!f.exists())
		{
		    File parent=f.getParentFile();
		    if (parent!=null && !parent.exists())
		    {
			parent.mkdirs();
		    }
		}
		outWriter=new PrintWriter(new FileWriter(logFileStr, true));
	    }
	    catch (IOException oyVeh)
	    {
		oyVeh.printStackTrace();
	    }
	    trace(daClass, currentDate, outWriter, message);
	    outWriter.flush();
	    outWriter.close();
	}
	else
	{
	    trace(daClass, currentDate, message);
	}
    }
    private static void trace(Class callClass, Date daDate, PrintWriter pw, Object o)
    {
	pw.print(getTraceHeader(callClass, daDate));
	if (o instanceof SQLWarning) doWarnings(pw, (SQLWarning) o);
	else if (o instanceof SQLException) doSQLExceptions(pw, (SQLException) o);
	else if (o instanceof Throwable) ((Throwable) o).printStackTrace(pw);
        else if (o.getClass().isArray())
        {
            try
            {
                for (int i=0;i<Array.getLength(o);i++)
                {
                    pw.println(Array.get(o, i));
                }
            }
            catch (Exception e)
            {
                pw.println(o);
            }
        }
	else pw.println(o);
    }
    

    private static void trace(Class callClass, Date daDate, Object o)
    {
	PrintStream out =(o instanceof Throwable)?System.err : System.out;
	out.print(getTraceHeader(callClass, daDate));
	if (o instanceof SQLWarning) doWarnings(out, (SQLWarning) o);
	else if (o instanceof SQLException) doSQLExceptions(out, (SQLException) o);
	else if (o instanceof Throwable) ((Throwable) o).printStackTrace(out);
        else if (o.getClass().isArray())
        {
            try
            {
                for (int i=0;i<Array.getLength(o);i++)
                {
                    out.println(Array.get(o, i));
                }
            }
            catch (Exception e)
            {
                out.println(o);
            }
	    
        }
	else out.println(o);
    }
    
    private static String getPackage(Object o)
    {
	String className;
	if (o instanceof Class)
	    className=((Class)o).getName();
	else
	    className=o.getClass().getName();
	return className.substring(0, className.lastIndexOf('.'));
    }
    
    private static String getTraceHeader(Class callClass, Date daDate)
    {
	return new StringBuffer()
	    .append(daDate)
	    .append(' ')
	    .append(callClass)
	    .append(" ==> ")
	    .toString();
    }
    
    public static int getDebugLevel(Class caller)
    {
	int defaultLevel=getDefaultDebugLevel();
	String levelStr=debugProperties.getProperty(getPackagedKey(caller, DEBUG_LEVEL_PROPERTY),
						    Integer.toString(defaultLevel));
	try
	{
	    return constrain(Integer.parseInt(levelStr), 0, 9);
	}
	catch (NumberFormatException numbForX)
	{
	    doLogError();
	    trace(Debug.class, DebugPriority.DP0, numbForX);
	}
	return defaultLevel;
    }

    public static int getDefaultDebugLevel()
    {
	String defLevStr=debugProperties.getProperty(DEFAULT_DEBUG_LEVEL_PROPERTY, ""+DEFAULT_DEBUG_LEVEL);
	try
	{
	    return constrain(Integer.parseInt(defLevStr), 0, 9);
	}
	catch(NumberFormatException nafta)
	{
	    doLogError();
	    trace(Debug.class, DP0, nafta);
	}
	return DEFAULT_DEBUG_LEVEL;
    }
	

    private static int constrain(int toBeConstrained, int min, int max)
    {
	return Math.min(Math.max(min, toBeConstrained), max);
    }
    
    public static String getLogFile(Class caller)
    {
	String logStr=debugProperties.getProperty(getPackagedKey(caller, LOG_FILE_PROPERTY),
						  getDefaultLogFile());
	return logStr;
    }

    public static String getDefaultLogFile()
    {
	return debugProperties.getProperty(DEFAULT_LOG_FILE_PROPERTY, DEFAULT_LOG_FILE);
    }
    
    private static String getPackagedKey(Class caller, String key)
    {
	return new StringBuffer(getPackage(caller))
	    .append('.')
	    .append(key)
	    .toString();
    }
    
    private static void initProperties()
    {
	debugProperties=new Properties();
	Object fileProp=System.getProperty(DEBUG_PROPERTIES_FILE);
	boolean customFile=false;
	if (fileProp!=null)
	{
	    try
	    {
		InputStream in=new BufferedInputStream(new FileInputStream(fileProp.toString()));
		debugProperties.load(in);
		customFile=true;
	    }
	    catch (Exception ex)
	    {
		doLogError();
		ex.printStackTrace();
		customFile=false;
	    }
	}
	if (!customFile)
	{
	    try
	    {
		debugProperties.load(Debug.class.getResourceAsStream(DEBUG_PROPERTIES));
	    }
	    catch (Exception ex)
	    {
		doLogError();
		ex.printStackTrace();
	    }
	}
	Object o=System.getProperty(DEFAULT_DEBUG_LEVEL_PROPERTY);
	if (o!=null)
	{
	    try
	    {
		int level=Integer.parseInt(o.toString());
		debugProperties.put(DEFAULT_DEBUG_LEVEL_PROPERTY, o);
	    }
	    catch (NumberFormatException nafta)
	    {
		System.err.println("improper value for defaultDebugLevel: "+o);
	    }
	}
    }
    
    private static void doWarnings(PrintWriter out, SQLWarning sw)
    {
	do
	{
	    sw.printStackTrace(out);
	}
	while ((sw=sw.getNextWarning())!=null);
    }
    
    private static void doWarnings(PrintStream out, SQLWarning sw)
    {
	do
	{
	    sw.printStackTrace(out);
	}
	while ((sw=sw.getNextWarning())!=null);
    }
    
    private static void doSQLExceptions(PrintWriter out, SQLException se)
    {
	do
	{
	    se.printStackTrace(out);
	}
	while ((se=se.getNextException())!=null);
    }
    
    private static void doSQLExceptions(PrintStream out, SQLException se)
    {
	do
	{
	    se.printStackTrace(out);
	}
	while ((se=se.getNextException())!=null);
    }
    
    private static void doLogError()
    {
	System.err.println( "ERROR IN " + getPackage(Debug.class)+"." +DEBUG_PROPERTIES+"!");
    }

    public static void main(String[] args)
    {
	if (args.length>0 && args[0].equals("refresh")) 
	    Debug.initProperties();
    }
    
    private Debug() 
    {
	//not meant to be instantiated
    }
    
}
class DebugPriority implements Serializable
{
    public static final DebugPriority DP0=new DebugPriority(0);
    public static final DebugPriority DP1=new DebugPriority(1);
    public static final DebugPriority DP2=new DebugPriority(2);
    public static final DebugPriority DP3=new DebugPriority(3);
    public static final DebugPriority DP4=new DebugPriority(4);
    public static final DebugPriority DP5=new DebugPriority(5);
    public static final DebugPriority DP6=new DebugPriority(6);
    public static final DebugPriority DP7=new DebugPriority(7);
    public static final DebugPriority DP8=new DebugPriority(8);
    public static final DebugPriority DP9=new DebugPriority(9);
    
    int priority;
    
    protected DebugPriority(int priority)
    {
	this.priority=priority;
    }
    
    public int getPriority()
    {
	return priority;
    }
    
    public String toString()
    {
	return new StringBuffer("Debug Priority [")
	    .append(priority)
	    .append("]")
	    .toString();
    }
}

