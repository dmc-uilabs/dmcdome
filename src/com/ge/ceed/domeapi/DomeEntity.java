package com.ge.ceed.domeapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Base abstract implementation of IDomeEntity with common functionality for derived classes.
 * Sub-classes all have an identifier (name), a location (path), children, and a type.
 *
 */
public abstract class DomeEntity implements IDomeEntity {
	private static final Integer[] EMPTY_PATH = Collections.emptyList().toArray(new Integer[]{});

	private String type;
	private String name;
	private List<Integer> path;
	private List<IDomeEntity> children;
	
	/**
	 * Constructor to make a new DomeEntity that is a child of another IDomeEntity
	 * @param type
	 * @param e
	 * @param folderId
	 * @param name
	 */
	public DomeEntity(String type, IDomeEntity e, Integer folderId, String name) {
		this(type, e.getPath(), folderId, name);
	}
	
	public DomeEntity(String type, Integer[] path, Integer folderId, String name) {
		this.type = type;
		this.path = new ArrayList<Integer>();
		if (path != null) {
			this.path.addAll(Arrays.asList(path));
		}
		if (folderId != null) {
			this.path.add(folderId);
		}
		this.name = name;		
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IDomeEntity#getType()
	 */
	@Override
	public String getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IDomeEntity#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IDomeEntity#addChild(com.ge.ceed.domeapi.DomeEntity)
	 */
	@Override
	public void addChild(IDomeEntity child) {
		if (children == null) {
			children = new ArrayList<IDomeEntity>();
		}
		this.children.add(child);
	}
	
	/* (non-Javadoc)
	 * @see com.ge.ceed.domeapi.IDomeEntity#getPath()
	 */
	@Override
	public Integer[] getPath() {
		return (this.path==null ? EMPTY_PATH : this.path.toArray(new Integer[this.path.size()]));
	}

	@Override
	public String toString() {
		return "DomeEntity [type=" + type + ", name=" + name + ", path=" + path + ", children=" + children + "]";
	}
	
}
