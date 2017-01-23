// DataObjectFactory.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import org.dom4j.Element;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;

public interface DataObjectFactory
{

	/**
	 * params must be the parameters to the constructor method in correct order
	 */
	public DataObject newInstance(String dObjType);

	public DataObject newInstance(DataObject dObj);

	public DataObject newInstance(Element xmlElement);

}
