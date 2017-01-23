// AbstractModelSolver.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;

import java.util.HashMap;

public abstract class AbstractModelSolver
{
	protected HashMap externalGraphs = new HashMap(); // key is model/interfaceId
	protected AbstractGraphSolver graphSolver;
	protected boolean isConsistent = false; // does graphSolver need to be recreated?

	/**
	 * @return the model being solved
	 */
	protected abstract DomeModelBase getModel();

	/**
	 * Clears data structures
	 */
	protected abstract void cleanup();

	/**
	 * Only used by projects to add external graph to imodel.
	 * Graph is already in imodel parameters.
	 * @param sourceId
	 * @param graph
	 */
	public void addExternalGraph(String sourceId, DirectedGraph graph)
	{
		//GLPanel.visualizeGraph("external graph for " + getModel().getName(), graph);
		externalGraphs.put(sourceId, graph);
		isConsistent = false;
	}

	public DirectedGraph getLocalGraph()
	{
		makeConsistent();
		return graphSolver.getLocalGraph();
	}

	public DirectedGraph getCompleteGraph() {
		makeConsistent();
		return graphSolver.getCompleteGraph();
	}

	public boolean isLocalInput(Object obj) {
		return getLocalGraph().isInput(obj);
	}

	protected void makeConsistent() {
		if (graphSolver == null || !isConsistent) {
			graphSolver = createGraphSolver();
			isConsistent = true;
		}
	}

	protected abstract AbstractGraphSolver createGraphSolver();

	/**
	 * Default implementation for models which do not support mappings
	 * @return graph representing mappings in a model
	 */
	protected DirectedGraph getMappingsGraph() {
		return null;
	}

}
