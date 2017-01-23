package com.ge.ceed.domeapi;

import mit.cadlab.dome3.api.DomeFolder;

/**
 * A specific sub-class of DomeEntity to hold the DOME3 {@link DomeFolder} objects
 * @author dliscomb
 *
 */
public class DomeFolderEntity extends DomeEntity {

	public static final String TYPE = "folder";

	public DomeFolderEntity(Integer[] path, DomeFolder folder) {
		super(TYPE, path, folder.getFolderId(), folder.getFolderName());
	}
	
	//TODO: for testing
	DomeFolderEntity(Integer[] path, String folderName, Integer folderId) {
		super(TYPE, path, folderId, folderName);
	}
		
}
