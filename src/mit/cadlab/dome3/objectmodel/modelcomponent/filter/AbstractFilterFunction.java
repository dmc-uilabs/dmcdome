// AbstractFilterFunction.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

public abstract class AbstractFilterFunction implements FilterFunction
{

	protected String name;

	public AbstractFilterFunction(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}


}
