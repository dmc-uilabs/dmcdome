package com.ge.ceed.domeapi;

import mit.cadlab.dome3.api.DomeProject;

/**
 * A DomeEntity sub-class that holds data from {@link DomeProject}
 *
 */
public class DomeProjectEntity extends DomeEntity {
	
	public static final String TYPE = "project";
	private int version;
	private String projectId;
	private String description;
	private long dateModified;
	
	public DomeProjectEntity(Integer[] path, DomeProject project) {
		super(TYPE, path, null, project == null? null : project.getProjectName());
		if (project != null) { 
			this.version = project.getVersion();
			this.projectId = project.getProjectId();
			this.description = project.getDescription();
			this.dateModified = project.getLastModified().getTime();
		}
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public String getProjectId() {
		return this.projectId;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public long getDateModified() {
		return dateModified;
	}
		
}
