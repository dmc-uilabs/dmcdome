// PluginParameterChangeTracker.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.util.DSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

/**
 * tracks changed parameters
 */
public class PluginParameterChangeTracker extends AbstractParameterChangeTracker
{

	protected PluginModelRuntime model;
	protected DSet changedPluginInputs, affectedPluginOutputs;

	public PluginParameterChangeTracker(PluginModelRuntime model)
	{
		resetTracker();
		this.model = model;
	}

	public Collection resetTracker()
	{
		changedPluginInputs = new DSet();
		return super.resetTracker();
	}

	public boolean hasChangedParametersLeftToProcess()
	{
		return !changedPluginInputs.isEmpty();
	}

	public List getChangedPluginInputs()
	{
		return Collections.unmodifiableList(changedPluginInputs);
	}

	protected boolean isPluginInput(Parameter param) {
		return CausalityStatus.INDEPENDENT.equals(model.getCausality(param));
	}

	public void notifyParamChange(Parameter param)
	{
		if (isPluginInput(param)) {
			changedPluginInputs.add(param);
		}
		varSequence.add(param);
	}

	/**
	 * getAffectedOutputs should only be called by the execution manager during the
	 * runModel part of the solving cycle to guarantee that the affected output list
	 * matches the changed input list (set during the processChanges part of the solving cycle).
	 * @return list of outputs affected by the current set of changed inputs
	 */
	public List getAffectedOutputs() {
		if (affectedPluginOutputs==null) {
			affectedPluginOutputs = new DSet();
			Iterator it = changedPluginInputs.iterator();
			Parameter p;
			while (it.hasNext()) {
				p = (Parameter) it.next();
				affectedPluginOutputs.addAll(model.getAffectedParams(p));
			}
		}
		return affectedPluginOutputs;
	}

	public void notifyPluginModelExecuted()
	{
		changedPluginInputs.clear();
		if (affectedPluginOutputs!=null) {
			affectedPluginOutputs.clear();
			affectedPluginOutputs = null;
		}
	}

}
