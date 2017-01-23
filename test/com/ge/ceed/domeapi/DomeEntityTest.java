package com.ge.ceed.domeapi;

import static org.junit.Assert.*;

import java.util.ArrayList;

import groovyjarjarantlr.collections.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DomeEntityTest {

	private Gson gson;
	private static final String TEST_TYPE = "testType";
	private static final int TEST_FOLDER_ID = 123;
	private static final String TEST_NAME = "TestNAME";
	private IDomeEntity entity;
	private static final Integer[] TEST_FOLDER_PATH = new Integer[]{6699};
	
	private static class DomeTestEntity extends DomeEntity {

		DomeTestEntity() {
			super(TEST_TYPE, TEST_FOLDER_PATH, TEST_FOLDER_ID, TEST_NAME);
		}
	}
	
	@Before
	public void setUp() throws Exception {
		gson = new GsonBuilder().registerTypeAdapter(DomeEntity.class, new DomeEntitySerializer()).create();
		entity = new DomeTestEntity();
	}

	@Test
	public void testDomeEntity() {		
	    String expectedJson = "{\"type\":\"" + TEST_TYPE + "\",\"name\":\"" + TEST_NAME + "\",\"path\":[" + TEST_FOLDER_PATH[0].toString() + ","+TEST_FOLDER_ID+"]}";
		String actualJson = gson.toJson(entity, DomeTestEntity.class);
		System.out.println(expectedJson+"\n"+actualJson);
	}

	@Test
	public void testGetType() {
		assertEquals(TEST_TYPE, entity.getType());
	}

	@Test
	public void testGetName() {
		assertEquals(TEST_NAME, entity.getName());
	}

	@Test
	public void testAddChild() {
		
	}

	@Test
	public void testGetFolderId() {
		Integer[] finalPath = new Integer[]{6699, 123};
		assertArrayEquals(finalPath, entity.getPath());
		assertEquals(finalPath.length, entity.getPath().length);
	}
}
