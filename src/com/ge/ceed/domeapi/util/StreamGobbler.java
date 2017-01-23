package com.ge.ceed.domeapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Pipes any output from the InputStream to the OutputStream
 * See {@link getCEEDStatus}
 * @author citrit
 *
 */
public final class StreamGobbler extends Thread
{
	InputStream is;
	OutputStream os;

	public StreamGobbler(InputStream is, OutputStream os)
	{
		this.is = is;
		this.os = os;
	}

	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			PrintWriter pw = new PrintWriter(os);
			String line=null;
			while ( (line = br.readLine()) != null) {
				pw.println(line);
				pw.flush();
			}
			pw.close();
			is.close();
			os.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();  
		}
	}

}