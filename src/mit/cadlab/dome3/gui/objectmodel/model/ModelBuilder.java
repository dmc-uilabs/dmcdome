// ModelBuilder.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.model;

import mit.cadlab.dome3.objectmodel.model.Model;

public interface ModelBuilder extends Model
{

	public static final String FILENAME = "fileName";

	public String getFileName();

	/**
	 * Returns whether or not this version of the model has been saved.
	 */
	public boolean isSaved();

	/**
	 * Saves the model overwriting file.
	 */
	public void save(String fileName);

	public void setShouldSave(boolean shouldSave);

	public boolean getShouldSave();

	public void save(boolean closeAfterSave); // invokes saveAs if not filename

	public void saveAs(boolean closeAfterSave); // invokes saveAsDialog
}
