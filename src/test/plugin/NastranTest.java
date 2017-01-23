package test.plugin;

import mit.cadlab.dome3.util.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Nov 21, 2003
 * Time: 3:47:09 PM
 * To change this template use Options | File Templates.
 */
public class NastranTest
{
    public static void main(String[] args) {
	    String runCommand = "C:\\msc\\bin\\nastran.exe v103guy.dat";
	    File workingDirectory = new File("C:\\nastran\\test");
 		try {
			Process p = Runtime.getRuntime().exec(runCommand, null, workingDirectory);
			p.waitFor();
			String file = FileUtils.readTextFileAsString("C:\\nastran\\test\\v103guy.f06");
			System.out.println(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
