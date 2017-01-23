// StreamSink.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import mit.cadlab.dome3.network.server.Debug;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

public class StreamSink extends Thread
{
	InputStream is;
	String linePrefix;

	/**
	 * Executes a command starting in the specified directory.
	 * Directed process output and error streams to the standard out stream if Debug level is not OFF
	 * @param command
	 * @param directory
	 * @param outputLinePrefix output lines are prefixed with the outputLinePrefix
	 * @return
	 * @throws RuntimeException if anything goes wrong; wraps exceptions in RuntimeException
	 */
	public static int runCommand(String command, File directory, String outputLinePrefix) throws RuntimeException
	{
		if (command == null || command.trim().equals(""))
			return 0;
		try {
			Process p = Runtime.getRuntime().exec(command, null, directory);
			StreamSink outSink = new StreamSink(p.getInputStream(), outputLinePrefix);
			StreamSink errSink = new StreamSink(p.getErrorStream(), outputLinePrefix);
			outSink.start();
			errSink.start();
			return p.waitFor();
		}
		catch (RuntimeException ex) {
			throw ex;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public StreamSink(InputStream is, String linePrefix)
	{
		this.is = is;
		this.linePrefix = (linePrefix==null ? "" : linePrefix);
	}

	public void run()
	{
		try {
			int debugLevel = Debug.getDebugLevel();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				if (debugLevel != Debug.OFF) {
					System.out.println(linePrefix + ">" + line);
				}
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
	}

}

