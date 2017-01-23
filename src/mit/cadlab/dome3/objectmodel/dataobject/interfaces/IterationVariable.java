// RealIterationVariable.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface IterationVariable extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Iteration Variable", "Iteration Variable");
	public static final String TICK = "tick";

	public boolean tick();

}
