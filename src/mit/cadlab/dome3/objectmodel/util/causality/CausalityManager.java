// CausalityManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import java.util.List;

/**
 * CausalityManager keeps track of the Causality data needed for CausalitySupport
 */
public interface CausalityManager extends CausalitySupport
{

	public List getIndependents();

	public List getIntermediates();

	public List getResults();

	public List getIndeterminates();

	public List getDrivers(); // Independents and Intermediates

	public List getOutputs();  // Intermediates and Results

	public boolean isIndependent(Object obj);

	public boolean isIntermediate(Object obj);

	public boolean isResult(Object obj);

	public boolean isIndeterminate(Object obj);

	public boolean isDriver(Object obj);

	public boolean isOutput(Object obj);

}
