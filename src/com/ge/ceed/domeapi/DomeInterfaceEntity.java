package com.ge.ceed.domeapi;

import mit.cadlab.dome3.api.DomeInterface;

/**
 * A DomeEntity sub-class that holds data from {@link DomeInterface}
 * @author dliscomb
 *
 */
public class DomeInterfaceEntity extends DomeEntity {
	
	public static final String TYPE = "interface";
	private int version;
	private String modelId;
	private String interfaceId;
	private String projectId;
	
	public DomeInterfaceEntity(DomeModelEntity model, DomeInterface i) {
		super(TYPE, model, null, i.getInterfaceName());
		this.version = i.getVersion();
		this.modelId = model.getModelId();
		this.interfaceId = i.getInterfaceId();
	}
	
	//TODO: for testing & debugging
	DomeInterfaceEntity(DomeModelEntity model, String domeInterfaceName, int version, String domeInterfaceId) {
		super(TYPE, model, null, domeInterfaceName);
		this.version = version;
		this.modelId = model.getModelId();
		this.interfaceId = domeInterfaceId;
	}
	
	public DomeInterfaceEntity(DomeProjectEntity project, DomeInterface i) {
		super(TYPE, project, null, i.getInterfaceName());
		this.version = i.getVersion();
		this.projectId = project.getName(); // FIXME: there is a bug in DomeFolder.class (line 340) where it compares Id to the Name for searching
		this.interfaceId = i.getInterfaceId();
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public String getModelId() {
		return this.modelId;
	}
	
	public boolean isModelInterface() {
		return this.modelId != null;
	}
	
	public boolean isProjectInterface() {
		return this.projectId != null;
	}
	
	public String getProjectId() {
		return this.projectId;
	}
	
	public String getInterfaceId() {
		return this.interfaceId;
	}

	@Override
	public String toString() {
		return "DomeInterfaceEntity [version=" + version + ", modelId=" + modelId + ", interfaceId=" + interfaceId + ", projectId=" + projectId
				+ "]";
	}
}
