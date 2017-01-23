package com.ge.ceed.domeapi;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.util.xml.XMLUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CeedIntegrationProjectBuilderTest {
	static String newFileName = "/tmp/NewSquareRootIntegrationModel-DOME.dml.dpj";
	static File nuFile = new File(newFileName);
	static String newIProjectDirName = "/tmp/NewSquareRootIntegrationModel-DOME.dml-resources";
	static File newIProjectDir = new File(newIProjectDirName);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DomeInit.initializeDOME();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (nuFile.exists()) {
			FileUtils.deleteQuietly(nuFile);
			FileUtils.deleteDirectory(newIProjectDir);
		}
	}

	@Before
	public void setUp() throws Exception {
		if (nuFile.exists()) {
			FileUtils.deleteQuietly(nuFile);
			FileUtils.deleteDirectory(newIProjectDir);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void makeNull() {
		Element xmlElement = null;
		@SuppressWarnings("unused")
		CeedIntegrationProjectBuilder bldr = new CeedIntegrationProjectBuilder(xmlElement);
	}

	@Test
	public void makeFromFile() throws IOException {
        String fileName = "test/DrewModels/SquareRootIntegrationModel-DOME.dml.dpj";
		Element xmlElement = XMLUtils.fileToXmlElement(fileName);
		CeedIntegrationProjectBuilder bldr = new CeedIntegrationProjectBuilder(fileName, xmlElement);
		assertNotNull("Expected non-null CeedIntegrationProjectBuilder", bldr);
		
		bldr.saveQuietly(newFileName);
		assertTrue(String.format("Expected to find file '%s'", newFileName) , nuFile.exists());
		
	}

}
