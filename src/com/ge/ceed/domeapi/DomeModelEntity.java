package com.ge.ceed.domeapi;

import mit.cadlab.dome3.api.DomeModel;

/**
 * A DomeEntity sub-class that holds data from {@link DomeModel}
 * @author dliscomb
 *
 */
public class DomeModelEntity extends DomeEntity {
	
	public static final String TYPE = "model";
	private int version;
	private String modelId;
	private String description;
	private long dateModified;
	
	/**
	 * Default constructor 
	 * @param path
	 * @param model
	 */
	public DomeModelEntity(Integer[] path, DomeModel model) {
		super(TYPE, path, null, model.getModelName());
		this.version = model.getVersion();
		this.modelId = model.getModelId();
		this.description = model.getDescription();
		this.dateModified = model.getLastModified().getTime();
	}
	
	//TODO: for testing & debugging
	DomeModelEntity(Integer[] path, String modelName, int version, String modelId, String description, long modifiedDate) {
		super(TYPE, path, null, modelName);
		this.version = version;
		this.modelId = modelId;
		this.description = description;
		this.dateModified = modifiedDate;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public String getModelId() {
		return this.modelId;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public long getDateModified() {
		return dateModified;
	}
		
}
