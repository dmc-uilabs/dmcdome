// CausalitySupport.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import java.util.List;

public interface CausalitySupport
{

	public List getItems(CausalityStatus causality);

	public boolean isItemOfCausality(Object obj, CausalityStatus causality);

	public CausalityStatus getCausality(Object obj);

	/**
	 * for all causality change events
	 */
	public void addCausalityChangeListener(CausalityChangeListener l);

	public void removeCausalityChangeListener(CausalityChangeListener l);

	/**
	 *
	 */
	public void addCausalityChangeListener(Object obj, CausalityChangeListener l);

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l);

}
