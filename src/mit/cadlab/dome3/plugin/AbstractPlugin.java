// AbstractPlugin.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.List;

/**
 * This class implements a dummy execute() method for backward compatibility
 * with the old Plugin interface.
 */
public abstract class AbstractPlugin implements Plugin
{

	/**
	 * This method only exists for test programs which call execute directly.
	 * Real execution of models should use the form of execute which
	 * gets the list of affected output parameters.
	 */
	public void execute() {
		execute(null);
	}

	/**
	 * Use this method to determine if parameter is an affected output parameter
	 * takes into account a null affected output parameter list
	 * @param p parameter to check
	 * @param affectedOutputParams list of affected output parameters; could be null
	 * @return true if list is null or list contains p
	 */
	protected boolean isAffectedOutputParameter(Parameter p, List affectedOutputParams) {
		return (p==null || affectedOutputParams==null || affectedOutputParams.contains(p));
	}

}
