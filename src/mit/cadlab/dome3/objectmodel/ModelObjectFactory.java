// ModelObjectFactory.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;

public interface ModelObjectFactory
{

	/**
	 * obj is either object type name, type symbol, or object instance
	 * params must be the parameters to the constructor method in correct order
	 */
	public ModelObject newInstance(Object obj, Object[] params);

}
