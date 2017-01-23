// Model.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.util.log.LogHandler;

/**
 * A Model is a collection of model objects
 * Models can be defined in many ways.
 * The basic model defines services that all models must provide.
 */
public interface Model extends ModelObjectScope, CausalitySupport
{

	public static final String XML_TAG = "model";
//    /**
//     * methods are public, but permission is checked by who the requester is
//     */
//    public Id getNextId(ModelObject mObj, Object requester);
//
//    public Id getNextId(String mObjType, Object requester);

	/**
	 * Returns MessageLog for Model. Never null.
	 */
	public LogHandler getLogHandler();

	public void setLogHandler(LogHandler log);

	/**
	 * Returns version of model.
	 */
	public Version getVersion();

}
