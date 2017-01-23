// FilterFunctions.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;

public class FilterFunctions
{

	public static class Parameters extends AbstractFilterFunction
	{
		public Parameters()
		{
			super("Parameters");
		}

		public boolean keepInFilter(Object obj)
		{
			return (obj instanceof Parameter);
		}
	}

	public static class Relations extends AbstractFilterFunction
	{
		public Relations()
		{
			super("Relations");
		}

		public boolean keepInFilter(Object obj)
		{
			return (obj instanceof Relation);
		}
	}

	public static class Subscriptions extends AbstractFilterFunction
	{
		public Subscriptions()
		{
			super("Subscriptions");
		}

		public boolean keepInFilter(Object obj)
		{
			return (obj instanceof Subscription);
		}
	}

	public static class Contexts extends AbstractFilterFunction
	{
		public Contexts()
		{
			super("Contexts");
		}

		public boolean keepInFilter(Object obj)
		{
			return (obj instanceof Context);
		}
	}

}
