// PluginGraphSolver.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class PluginGraphSolver extends AbstractGraphSolver
{

	protected List independentInputSets;

	public PluginGraphSolver(String name, DirectedGraph modelGraph, HashMap externalGraphs)
	{
		super(name);
		this.localGraph = modelGraph;

		// create complete graph with local graph and external graphs
		// used for solving and system causality view
		if (externalGraphs.isEmpty())
			completeGraph = localGraph;
		else {
			completeGraph.addGraph(localGraph);
			Iterator it2 = externalGraphs.values().iterator();
			while (it2.hasNext()) {
				completeGraph.addGraph((DirectedGraph) it2.next());
			}
		}

		independentInputSets = Collections.unmodifiableList(completeGraph.getIndependentSets(localGraph.getInputs()));
	}

	public List getIndependentInputSets()
	{
		return independentInputSets;
	}

}
