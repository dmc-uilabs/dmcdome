// AbstractCausalityFilter.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

public abstract class AbstractCausalityFilter extends AbstractEventFilter
{

	public AbstractCausalityFilter(Model m, Id id, String name)
	{
		super(m, id, name);
	}

	protected void processCausalityChange(CausalityChangeEvent event)
	{
		Object param = event.getParameter();
		if (param instanceof Parameter) {
			CausalityStatus oldCS = event.getOldCausalityStatus();
			CausalityStatus newCS = event.getNewCausalityStatus();
			if (isCausalityOfInterest(newCS)) {
				filteredItems.add(param);
			}
			else if (isCausalityOfInterest(oldCS)) {
				filteredItems.remove(param);
			}
		}
	}

	protected abstract boolean isCausalityOfInterest(CausalityStatus causality);

	protected class CausalityFilterCausalityChangeListener implements CausalityChangeListener
	{
		public CausalityFilterCausalityChangeListener()
		{
		}

		public void causalityChanged(CausalityChangeEvent event)
		{
			processCausalityChange(event);
		}
	}

}
