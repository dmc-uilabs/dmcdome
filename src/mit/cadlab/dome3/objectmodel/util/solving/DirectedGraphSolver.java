// DirectedGraphSolver.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.Names;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.touchgraph.graphlayout.GLPanel;

/**
 * DirectedGraphSolver
 * determines relation execution sequence
 */
public class DirectedGraphSolver extends AbstractGraphSolver
{

	protected DirectedGraph relationGraph; // graph with relations/subscriptions/models as nodes

	protected List executionSequence; // relation execution sequence groups; used to determine function execution sequence
	protected HashMap relationInputs; // independent sets of inputs

	// todo change parameter names to use relations, not relation ids
	public DirectedGraphSolver(String name, HashMap modelGraphs, HashMap externalGraphs, DirectedGraph mappingsGraph)
	{
		super(name);
		// create local graphs of all relations
		Iterator it = modelGraphs.values().iterator();
		while (it.hasNext()) {
			localGraph.addGraph((DirectedGraph) it.next());
		}

		if (mappingsGraph != null)
			localGraph.addGraph(mappingsGraph);

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
//		if (name.equalsIgnoreCase("imodel"))
//		GLPanel.visualizeGraph(name+" graph", completeGraph);

		// calculate set of relation/subscription subgraphs to use to create relationSubGraph

		// find overconstrained parameters -- not implemented

		// create relation graph, determine desired relation execution sequence
		relationGraph = new DirectedGraph();
		relationGraph.addNodes(modelGraphs.keySet()); // could contain subgraphs for relations, subscriptions and models
		Object[] graphIds = modelGraphs.keySet().toArray();
		for (int i = 0; i < graphIds.length; i++) {
			Object fromGraphId = graphIds[i];
			for (int j = 0; j < graphIds.length; j++) {
				if (i==j)
					continue;
				Object toGraphId = graphIds[j];
				if (relationGraph.areAdjacentNodes(fromGraphId,toGraphId)) // arc already exists from fromRelId to toRelId
					continue;
				if (areAdjacentGraphs((DirectedGraph) modelGraphs.get(fromGraphId),
				                         (DirectedGraph) modelGraphs.get(toGraphId))) {
					relationGraph.addArc(fromGraphId,toGraphId);
				}
			}
		}
//		if (name.equalsIgnoreCase("imodel"))
//		GLPanel.visualizeGraph(name+": relation/subscription graph",relationGraph);
		executionSequence = new ArrayList(relationGraph.getNodeGroups());
		//System.out.println(getRelationExecutionSequenceString());

		// determine which inputs of relations are independent
		relationInputs = new HashMap();
		Iterator relIds2 = modelGraphs.keySet().iterator();
		while (relIds2.hasNext()) {
			Object relId = relIds2.next(); // todo: does this need to handle disconnected nodes?
			relationInputs.put(relId, completeGraph.getIndependentSets(((DirectedGraph) modelGraphs.get(relId)).getInputs()));
		}
		//--System.out.println("DirectedGraphSolver: "+localGraph);
	}

	public void cleanup() {
		relationGraph.cleanup();
		relationGraph = null;
		super.cleanup();
	}

	/**
	 * for checking whether a pair of graphs are directly connected (not via another graph)
	 * @param fromGraph
	 * @param toGraph
	 * @return true if fromGraph directly drives toGraph (model mappings in between ok)
	 */
	private boolean areAdjacentGraphs(DirectedGraph fromGraph, DirectedGraph toGraph) {
		List fromOutputs = fromGraph.getOutputs();
		List toInputs = toGraph.getInputs();
		Iterator fromNodes = fromOutputs.iterator();
		while (fromNodes.hasNext()) {
			Object fromNode = fromNodes.next();
			Iterator toNodes = toInputs.iterator();
			while (toNodes.hasNext()) {
				Object toNode = toNodes.next();
				if (completeGraph.areAdjacentNodes(fromNode,toNode))
					return true;
				else if (completeGraph.areReachableNodes(fromNode,toNode)) {
					// check if path contains parameters in another graph
					List path = completeGraph.getPath(fromNode,toNode);
					for (int i = 0; i < path.size(); i++) {
						Object o = path.get(i);
						if (o instanceof Parameter) {
							Parameter p = (Parameter)o;
							if (Parameters.isRelationParameter(p) || Parameters.isSubscriptionParameter(p))
								return false;
						}
					}
					return true;
				}
			}
		}
		// todo: handle disconnected nodes
		return false;
	}

	public List getRelationExecutionSequence()
	{
		return Collections.unmodifiableList(executionSequence);
	}

	public List getIndependentInputSets(Object relationId)
	{
		return (List) relationInputs.get(relationId);
	}

	public String getRelationExecutionSequenceString() {
		if (executionSequence.isEmpty())
			return "[]";
		StringBuffer sb = new StringBuffer("[" + Names.getNames((List)executionSequence.get(0)));
		List relOrSub;
		for (int i = 1; i < executionSequence.size(); i++) {
			relOrSub = (List) executionSequence.get(i);
			sb.append("\n\t" + Names.getNames(relOrSub));
		}
		sb.append("]");
		return sb.toString();
	}

}
