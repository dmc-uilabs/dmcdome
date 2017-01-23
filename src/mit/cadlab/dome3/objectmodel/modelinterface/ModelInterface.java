// ModelInterface.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.util.log.LogHandler;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;

import java.util.ArrayList;

/**
 * A ModelInterface is a collection of ModelObjects that provide a view of a model.
 * The objects in the ModelInterface are linked to objects in the Model.
 */
public interface ModelInterface extends ModelObjectScope, CausalitySupport
{

	public static final String DEFAULT_IFACE_TAG = "Default Interface";
	public static final String XML_TAG = "modelinterface";
	public static final String DEFAULT_INTERFACE_XML_TAG = "defaultinterface";

	public CausalityManager getCausalityManager();

	/**
	 * Returns last version of Model this interface is good for.
	 */
	public Version getModelVersion();

	/**
	 * Returns version of modelinterface.
	 */
	public Version getVersion();

	/**
	 * Validate the interface with model.
	 */
	public void validate();

	public boolean isValidated();

	/**
	 * Returns whether or not this version of the interface has been saved.
	 */
	public boolean isSaved();

	/**
	 * Returns MessageLog for ModelInterface. Never null.
	 */
	public LogHandler getLogHandler();

	public void setLogHandler(LogHandler log);

	//TODO return more general type i.e. List instead of ArrayList
	public ArrayList getCustomGUIList();

	public void addCustomGui(CustomGuiInfo info);

	public void removeCustomGui(CustomGuiInfo info);

    public void cleanup();
}
