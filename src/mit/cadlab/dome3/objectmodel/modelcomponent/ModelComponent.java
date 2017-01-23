// ModelComponent.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent;

import mit.cadlab.dome3.objectmodel.model.Model;

/**
 * A ModelComponent is an item associated with a particular Model.
 */
public interface ModelComponent
{

	/**
	 * @return the Model this ModelComponent is associated with
	 * */
	public Model getModel();

}
