// IllegalObjectModelOperation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions;

import mit.cadlab.dome3.util.DomeError;

public class IllegalObjectModelOperation extends DomeError
{

	public Object object1;
	public Object object2;

	public IllegalObjectModelOperation(String failedAction, Object object1, Object object2)
	{
		super(failedAction);
		this.object1 = object1;
		this.object2 = object2;
	}

}
