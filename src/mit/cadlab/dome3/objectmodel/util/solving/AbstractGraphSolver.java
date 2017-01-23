// AbstractGraphSolver.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

public class AbstractGraphSolver
{
	protected DirectedGraph localGraph; // all relation/mapping graph for DomeModel; modelGraph for PluginModels
	protected DirectedGraph completeGraph; // internal and external graphs together
	protected String name;

	public AbstractGraphSolver(String name)
	{
		this.name = name;
		completeGraph = new DirectedGraph();
		localGraph = new DirectedGraph();
	}

	/**
	 * Clears data structures
	 */
	public void cleanup() {
		localGraph.cleanup();
		localGraph = null;
		completeGraph.cleanup();
		completeGraph = null;
	}

	public DirectedGraph getLocalGraph() {
		return localGraph;
	}

	public DirectedGraph getCompleteGraph() {
        return completeGraph;
    }

}
