// ImmutableCausalityManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import java.util.List;

public class ImmutableCausalityManager implements CausalityManager
{

	private CausalityManager causalityManager;

	public ImmutableCausalityManager(CausalityManager causalityManager)
	{
		if (causalityManager == null)
			throw new NullPointerException("ImmutableCausalityManager - null causalityManager");
		this.causalityManager = causalityManager;
	}

	// CausalitySupport interface
	public List getItems(CausalityStatus causality)
	{
		return causalityManager.getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return causalityManager.isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return causalityManager.getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		causalityManager.addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		causalityManager.removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		causalityManager.addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		causalityManager.removeCausalityChangeListener(obj, l);
	}

	// CausalityManager interface
	public List getIndependents()
	{
		return causalityManager.getIndependents();
	}

	public List getIntermediates()
	{
		return causalityManager.getIntermediates();
	}

	public List getResults()
	{
		return causalityManager.getResults();
	}

	public List getIndeterminates()
	{
		return causalityManager.getIndeterminates();
	}

	public List getDrivers()
	{ // Independents and Intermediates
		return causalityManager.getDrivers();
	}

	public List getOutputs()
	{ // Intermediates and Results
		return causalityManager.getOutputs();
	}

	public boolean isIndependent(Object obj)
	{
		return causalityManager.isIndependent(obj);
	}

	public boolean isIntermediate(Object obj)
	{
		return causalityManager.isIntermediate(obj);
	}

	public boolean isResult(Object obj)
	{
		return causalityManager.isResult(obj);
	}

	public boolean isIndeterminate(Object obj)
	{
		return causalityManager.isIndeterminate(obj);
	}

	public boolean isDriver(Object obj)
	{
		return causalityManager.isDriver(obj);
	}

	public boolean isOutput(Object obj)
	{
		return causalityManager.isOutput(obj);
	}

	public String toString()
	{
		return causalityManager.toString();
	}
}
