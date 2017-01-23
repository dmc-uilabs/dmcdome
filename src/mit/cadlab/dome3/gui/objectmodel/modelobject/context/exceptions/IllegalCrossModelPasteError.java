// IllegalCrossModelPasteError.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.exceptions;


public class IllegalCrossModelPasteError extends IllegalObjectModelOperation
{

	public IllegalCrossModelPasteError(String failedAction, Object object1, Object object2)
	{
		super(failedAction, object1, object2);
	}

}
