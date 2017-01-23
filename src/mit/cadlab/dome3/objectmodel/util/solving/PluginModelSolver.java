// PluginModelSolver.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.util.DSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;

public class PluginModelSolver extends AbstractModelSolver {

    protected PluginModelRuntime model;
    protected PluginParameterChangeTracker tracker;
	protected DirectedGraph modelGraph;

    public PluginModelSolver(PluginModelRuntime model, PluginParameterChangeTracker tracker) {
        this.model = model;
        this.tracker = tracker;
	    this.modelGraph = model.createModelGraph();
    }

	public void cleanup()
	{
		modelGraph.cleanup();
		modelGraph = null;
		if (graphSolver != null)
			graphSolver.cleanup();
	}

	protected DomeModelBase getModel()
	{
		return model;
	}

	protected AbstractGraphSolver createGraphSolver()
	{
		return new PluginGraphSolver(model.getName(), modelGraph, externalGraphs);
	}

	/**
	 * Determines if plugin model should be executed based on changed inputs
	 * @return true if plugin should execute; otherwise, returns false
	 */
    public boolean isReadyToExecutePlugin() {
		makeConsistent();
		return !findReadyChanges(tracker.getChangedPluginInputs()).isEmpty();
	}

	protected List findReadyChanges(List changedParams)
	{
		List inputs = modelGraph.getInputs();
		List changedInputs = new ArrayList(DSet.intersection(inputs, changedParams));
		if (changedInputs.isEmpty())
			return Collections.EMPTY_LIST; // nothing has changed
		Collection remainingInputs = DSet.removeSet(inputs, changedInputs);

		boolean waitForMoreChanges = false;
		Parameter input;
		for (Iterator iterator = remainingInputs.iterator(); iterator.hasNext();) {
			input = (Parameter) iterator.next();
			if (Parameter.VALUE_STATUS_INCONSISTENT.equals(input.getValueStatus())) {
				waitForMoreChanges = true;
				break;
			}
		}
		if (waitForMoreChanges)
			return Collections.EMPTY_LIST;
		else
			return changedInputs;
	}

}
