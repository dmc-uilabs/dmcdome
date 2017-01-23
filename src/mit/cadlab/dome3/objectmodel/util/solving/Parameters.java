// Parameters.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;

/**
 * Functions to ask about the type of parameter.
 * todo move somewhere else in source tree
 */
public class Parameters
{

	public static boolean isRelationInput(Parameter p)
	{
		if (p.getScope() instanceof Relation) {
			CausalityStatus caus = ((Relation) p.getScope()).getCausality(p);
			return CausalityStatus.INDEPENDENT.equals(caus);
		}
		return false;
	}

	public static boolean isRelationOutput(Parameter p)
	{
		if (p.getScope() instanceof Relation) {
			CausalityStatus cs = ((Relation) p.getScope()).getCausality(p);
			return CausalityStatus.INTERMEDIATE.equals(cs) || CausalityStatus.RESULT.equals(cs);
		}
		return false;
	}

	public static boolean isSubscriptionInput(Parameter p)
	{
		if (isSubscriptionParameter(p)) {
			CausalityStatus cs = ((CausalitySupport) p.getScope()).getCausality(p);
			return CausalityStatus.INDEPENDENT.equals(cs);
		}
		return false;
	}

	public static boolean isSubscriptionOutput(Parameter p)
	{
		if (isSubscriptionParameter(p)) {
			CausalityStatus cs = ((CausalitySupport) p.getScope()).getCausality(p);
			return CausalityStatus.INTERMEDIATE.equals(cs) || CausalityStatus.RESULT.equals(cs);
		}
		return false;
	}

	public static boolean isInterfaceInput(Parameter p)
	{
		if (p.getScope() instanceof ModelInterface) {
			CausalityStatus caus = ((ModelInterface) p.getScope()).getCausality(p);
			return CausalityStatus.INDEPENDENT.equals(caus);
		}
		return false;
	}

	public static boolean isInterfaceOutput(Parameter p)
	{
		if (p.getScope() instanceof ModelInterface) {
			CausalityStatus cs = ((ModelInterface) p.getScope()).getCausality(p);
			return CausalityStatus.INTERMEDIATE.equals(cs) || CausalityStatus.RESULT.equals(cs);
		}
		return false;
	}

	public static boolean isModelParameter(Parameter p)
	{
		return (p.getScope() instanceof Model);
	}


	public static boolean isRelationParameter(Parameter p)
	{
		return (p.getScope() instanceof Relation);
	}


	public static boolean isInterfaceParameter(Parameter p)
	{
		//TODO causality for InterfaceModelView parameters
		return (p.getScope() instanceof ModelInterface); // || p.getScope() instanceof InterfaceModelView);
	}

	public static boolean isSubscriptionParameter(Parameter p)
	{
		return (p.getScope() instanceof Subscription) || (p.getScope() instanceof SubscriptionInterface);
	}

}
