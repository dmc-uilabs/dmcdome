package com.ge.ceed.domeapi;

import mit.cadlab.dome3.api.DomeModel;

@Deprecated
public class ModelDef implements Comparable<ModelDef> {
	private String name;
	private String guid;
	private String desc;
	private long dateModified;
	private String folder;
	private Server server; 
	
	public ModelDef(DomeModel dm, Server server, String folder) {
		this.name = dm.getModelName();
		this.guid = dm.getModelId();
		this.desc = dm.getDescription();
		this.dateModified = dm.getLastModified().getTime();
		this.folder = folder;
		this.server = server;
	}

	public String getName() {
		return name;
	}


	public String getGuid() {
		return guid;
	}


	public String getDesc() {
		return desc;
	}


	public long getDateModified() {
		return dateModified;
	}


	public String getFolder() {
		return folder;
	}


	public Server getServer() {
		return server;
	}


	@Override
	public int compareTo(ModelDef other) {
		return guid.compareTo(other.guid);
	}
	
}
