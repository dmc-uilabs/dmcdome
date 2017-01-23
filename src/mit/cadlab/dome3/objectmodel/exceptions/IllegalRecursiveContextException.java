// IllegalRecursiveContextException.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.exceptions;




public class IllegalRecursiveContextException extends IllegalObjectModelOperationException
{

	public IllegalRecursiveContextException(String failedAction, Object object1, Object object2)
	{
		super(failedAction, object1, object2);
	}

}
