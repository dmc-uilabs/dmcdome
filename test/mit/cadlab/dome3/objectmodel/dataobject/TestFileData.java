package mit.cadlab.dome3.objectmodel.dataobject;

import static org.junit.Assert.*;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.util.FileUtils;

import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

public class TestFileData {

	private static String testpath = "./test/" + TestFileData.class.getPackage().getName().replace('.', '/') + '/';
	private static String smallFile = testpath + "smallFile.txt";
	private static String bigFile = testpath + "bigFile.txt";
	private static String anotherSmallFile = testpath + "smallFileWithTest.txt";

	private static String getExpectedFilenamePropertyChange(boolean isFilePathEvent, boolean expectUrl, String filename)
	{
		String expected = "mit.cadlab.dome3.util.DomePropertyChangeEvent";
		if (isFilePathEvent) {
			expected += "[propertyName=filepath; oldValue=; ";
		} else {
			expected += "[propertyName=value; oldValue=true; ";
		}
		if (expectUrl) {
			try {
				InetAddress ip = InetAddress.getLocalHost();
				expected += "newValue=http://" + ip.getHostAddress() + ":" + FileData.defaultPort + "/";
			} catch (IOException ioe) {
				fail("unexpected IO Exception when getting IP address of self (for communicating big files): " + ioe.getMessage());
			}
		} else {
			expected += "newValue=" + testpath;
		}
		expected += filename;
		expected += "; propagationId=null; source=" + testpath;
		expected += filename;
		expected += "]";
		return expected;
	}
	// Create property change listeners to check if receiving correct event information
	// this one used for smallFile.txt
	public static PropertyChangeListener checkSetFileName = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			System.out.println("TestFileData::propertyChange with event = " + evt);
			String expected = "";
			
			if (evt.toString().indexOf("propertyName=filepath") >= 0) {
				expected = getExpectedFilenamePropertyChange(true, false, "smallFile.txt");
			} else if (evt.toString().indexOf("propertyName=value") >= 0) {
				expected = getExpectedFilenamePropertyChange(false, false, "smallFile.txt");
			} else {
				System.out.println("");
				fail("unexpected event occurred\n");
			}
			System.out.println("");
			assertEquals(expected, evt.toString());
		}
	};

	// Create property change listeners to check if receiving correct event information
	// this one used for bigFile.txt, so should be a http url.
	public static PropertyChangeListener checkSetFileNameBigFile = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			System.out.println("TestFileData::propertyChange with event = " + evt);
			String expected = "";
			
			if (evt.toString().indexOf("propertyName=filepath") >= 0) {
				expected = getExpectedFilenamePropertyChange(true, false, "bigFile.txt");
			} else if (evt.toString().indexOf("propertyName=value") >= 0) {
				expected = getExpectedFilenamePropertyChange(false, true, "bigFile.txt");
			} else {
				System.out.println("");
				fail("unexpected event occurred\n");
			}
			System.out.println("");
			assertEquals(expected, evt.toString());
		}
	};

	public TestFileData() 
	{
	}

	@Test
	public void testFileUrlTooLargeReturnsFalse() {
		System.out.println("\n*** start testFileUrlTooLargeReturnsFalse");
		File tempCurrentDirectory = new File( "." );
		try {
		System.out.println("Current dir:"+tempCurrentDirectory.getCanonicalPath());
		} catch (IOException ioe) {
			System.out.println("couldn't print current directory");
		}
		System.out.println("System.getProperty(\"user.dir\") ="+System.getProperty("user.dir"));
		System.out.println("this.getClass().getCanonicalName() = " + this.getClass().getCanonicalName());
		File checkFile = new File(anotherSmallFile);
		System.out.println("anotherSmallFile path = " + checkFile.getAbsolutePath());
		assertTrue(anotherSmallFile + " does not exist", checkFile.exists());
		FileData target = new FileData(checkFile);
		assertFalse(anotherSmallFile + " is too large, anotherSmallFile not too large", target.fileTooLarge());
	}
	@Test
	public void testFileTooLargeReturnsFalse() {
		System.out.println("\n*** start testFileTooLargeReturnsFalse");
		File checkFile = new File(smallFile);
		assertTrue(smallFile + " does not exist", checkFile.exists());
		FileData target = new FileData(checkFile);
		assertFalse(smallFile + " is too large, expected not too large", target.fileTooLarge());
	}
	@Test	
	public void testFileToolLargeReturnsTrue()
	{
		System.out.println("\n*** start testFileToolLargeReturnsTrue");
		File checkFile = new File(bigFile);
		assertTrue(bigFile + " does not exist", checkFile.exists());
		FileData target = new FileData(checkFile);
		assertTrue(bigFile + " is not too large, expected too large", target.fileTooLarge());
		long filesize = target.getFileSize(bigFile);
		System.out.println(bigFile + " size = " + filesize);
		assertTrue(bigFile + " size = " +  filesize + " and < " + FileUtils.MAXFILESIZE, filesize > FileUtils.MAXFILESIZE);
	}

	@Test
	public void testGetDomeFile() {
		System.out.println("\n*** testGetDomeFile");
		File checkFile = new File(smallFile);
		assertTrue(smallFile + " does not exist", checkFile.exists());
		FileData target = new FileData(checkFile);
		DomeFile domeFile = target.getDomeFile();
		assertNotNull(smallFile + " did not create DomeFile from FileData", domeFile);
	}

	@Test
	public void testGetFilePath() {
		System.out.println("\n*** testGetFilePath");
		File checkFile = new File(smallFile);
		assertTrue(smallFile + " does not exist", checkFile.exists());
		FileData target = new FileData(checkFile);
		// fix slashes in case test is run on Windows
		String fixedFilePath = FileUtils.fixPathnameSlashes(target.getFilePath());
		assertEquals(smallFile, fixedFilePath);
	}

	@Test
	public void testSetFilePath() {
		System.out.println("\n*** testSetFilePath");
		FileData target = new FileData();
		target.setFilePath(smallFile);
		String fixedFilePath = FileUtils.fixPathnameSlashes(target.getFilePath());
		assertEquals(smallFile, fixedFilePath);
	}

	//@Test  // ToDo: fix, issue DMC-619
	public void testSetFilePathAtRuntime() 
	{
		System.out.println("\n*** testSetFilePathAtRuntime");	
		FileData target = new FileData();
		target.addPropertyChangeListener(checkSetFileName);
		target.setFilePathAtRuntime(smallFile);
		// fix slashes in case test is run on Windows
		String fixedFilePath = FileUtils.fixPathnameSlashes(target.getFilePath());
		assertEquals(smallFile, fixedFilePath);
	}
	
	//@Test  // ToDo: fix, issue DMC-619
	public void testSetFilePathAtRuntimeForBigFile() 
	{
		System.out.println("\n*** testSetFilePathAtRuntimeForBigFile");	
		FileData target = new FileData();
		target.addPropertyChangeListener(checkSetFileNameBigFile);
		target.setFilePathAtRuntime(bigFile);
		// fix slashes in case test is run on Windows
		String fixedFilePath = FileUtils.fixPathnameSlashes(target.getFilePath());
		assertEquals(bigFile, fixedFilePath);
	}
	
}
