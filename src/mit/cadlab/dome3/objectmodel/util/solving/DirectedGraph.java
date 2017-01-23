// DirectedGraph.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;


import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.FormatUtils;
import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

/**
 * todo: change representation so that nodes are indexed by id; xml arcs use node ids only
 * todo: trigger loop messages when loops found
 * todo: keep track of independent sets of inputs and resulting causality
 */
// for use in DOME, nodes should be object IDs

public class DirectedGraph implements Cloneable {
    protected DSet nodes = new DSet(); // list of nodes
    protected HashMap arcs = new HashMap(); // key is fromNode, value is set of toNodes
    // all fromNodes and toNodes must exist in nodes; else an error is raised

    // following items are generated from data above
    // protected int[][] adjMatrix;
    // protected int[][] reachMatrix;
    // protected int[][] correlationMatrix;

    DoubleMatrix2D adjMatrix,reachMatrix,correlationMatrix;

	protected List disconnectedNodes; // no incoming or outgoing arcs, not even from internal relation
    protected List inputs; // no incoming arcs, has outgoing arcs
    protected List outputs; // has incoming arcs, may have outgoing arcs - same as intermediates and results together
    protected List internalOutputs; // has incoming arcs from internal relation (null), may have outgoing arcs
    protected List intermediates; // has both incoming and outgoing arcs;
    protected List results; // has incoming arcs, no outgoing arcs (includes internal_results)
    protected List loops, loopVariables, nodeGroups; // nodeGroups only if no loops
    protected boolean consistent = false;

    protected HashMap superNodes; //key is one loop circle in the graph; value is supernode

    protected HashMap affectedGraphForInputs; // key is input; value is DirectedGraph

    protected OpenIntObjectHashMap parentIndices; //key in node index, value is IntArrayList

    public DirectedGraph() {
    }

    private DirectedGraph(List nodeList, Map arcMap) {
        nodes = new DSet(nodeList);
        arcs = new HashMap();
        Iterator entries = arcMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            arcs.put(entry.getKey(), new NodeSet((List) entry.getValue()));
        }
    }

    public DirectedGraph(DependencyInfo dInfo) {
        addNodes(dInfo.getNodes());
        for (Iterator iterator = dInfo.getDependencyKeys().iterator(); iterator.hasNext();) {
            Object toNode = iterator.next();
            Collection fromNodes = dInfo.getDependentsForObject(toNode);
            for (Iterator iterator2 = fromNodes.iterator(); iterator2.hasNext();) {
                Object fromNode = iterator2.next();
                addArc(fromNode, toNode);
            }
        }
    }

    public DirectedGraph(DirectedGraph dg, HashMap nodeMap) {
        // nodeMap is old DomeObject node ->new node
        // or old object->new object (if old object not a DomeObject)
        nodes = new DSet(mapCollection(dg.getNodes(), nodeMap));
        arcs = new HashMap();

        Iterator entries = dg.getArcs().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Object oldKey = entry.getKey();
            List oldValues = (List) entry.getValue();
            Object newKey = nodeMap.get(oldKey);
            if (newKey == null)
                newKey = oldKey; // map to itself
	        List newValues = mapCollection(oldValues, nodeMap);
            addArcs(newKey, newValues);
        }
    }

    // for making an exact copy
    public DirectedGraph(DirectedGraph dg) {
        nodes = new DSet(dg.getNodes());
        arcs = new HashMap();

        Iterator entries = dg.getArcs().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Object oldKey = entry.getKey();
            List oldValues = (List) entry.getValue();
            addArcs(oldKey, oldValues);
        }
    }

    public DirectedGraph(Element xmlElement) {
        nodes = new DSet();
        arcs = new HashMap();
        loadXml(xmlElement);
    }

    private List mapCollection(Collection values, HashMap map) {
        List result = new ArrayList();
        //System.out.println("mapCollection, map keys"+map.keySet());
        //System.out.println("mapCollection, map values" + map.values());

        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            Object nextO = iterator.next();
            if (nextO == null)
                continue;
            Object o = map.get(nextO);
            if (o == null)
                result.add(nextO);
            else
                result.add(o);
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        return new DirectedGraph(nodes, arcs);
    }

    // methods to get information about graph

    public List getNodes() {
        return Collections.unmodifiableList(nodes);
    }

	public boolean containsNode(Object obj) {
		return nodes.contains(obj);
	}

    public Map getArcs() {
        return Collections.unmodifiableMap(arcs);
    }

	public boolean containsArc(Object fromNode, Object toNode) {
		Object toNodes = arcs.get(fromNode);
		if (toNodes == null)
			return false;
		return ((Collection)toNodes).contains(toNode);
	}

    public boolean isEmpty() {
        return nodes.isEmpty(); // arcs should be empty then, as well
    }

	/**
	 * @param node
	 * @return true if node does not have any incoming or outgoing arcs
	 */
	public boolean isDisconnectedNode(Object node) {
		enforceConsistency();
		return disconnectedNodes.contains(node);
	}

	/**
	 * @param node
	 * @return true if node has no incoming arc, but has at least one outgoing arc
	 */
	public boolean isInput(Object node) {
		enforceConsistency();
		return inputs.contains(node);
	}

	/**
	 * @param node
	 * @return true if node has at least one incoming arc and may have outgoing arc
	 * could be an intermediate or a result
	 */
	public boolean isOutput(Object node) {
		enforceConsistency();
		return outputs.contains(node);
	}

	/**
	 * @param node
	 * @return true if node has an incoming arc not from a node in the graph
	 */
	public boolean isInternalOutput(Object node) {
		enforceConsistency();
		return internalOutputs.contains(node);
	}

	/**
	 * @param node
	 * @return true if node has at least one incoming arc and at least one outgoing arc
	 */
	public boolean isIntermediate(Object node) {
		enforceConsistency();
		return intermediates.contains(node);
	}

	/**
	 * @param node
	 * @return true if node has at least one incoming arc and has no outgoing arc.
	 * node could be an internal output.
	 */
	public boolean isResult(Object node) {
		enforceConsistency();
		return results.contains(node);
	}

	/**
	 * @return list of nodes which do not have any incoming or outgoing arcs
	 */
	public List getDisconnectedNodes()
	{
		enforceConsistency();
		return Collections.unmodifiableList(disconnectedNodes);
	}

	/**
	 * @return list of nodes which has no incoming arc, but has at least one outgoing arc
	 */
    public List getInputs() {
        enforceConsistency();
        return Collections.unmodifiableList(inputs);
    }

	/**
	 * @return list of nodes which have at least one incoming arc; may have outgoing arc
	 * same as intermediates and results together
	 */
    public List getOutputs() {
        enforceConsistency();
        return Collections.unmodifiableList(outputs);
    }

	/**
	 * @return list of nodes which has an incoming arc not from a node in the graph
	 * This represents a node which is driven by the application/environment.
	 */
    public List getInternalOutputs() {
        enforceConsistency();
        return Collections.unmodifiableList(internalOutputs);
    }

	/**
	 * @return list of nodes which have at least one incoming arc and at least one outgoing arc
	 */
    public List getIntermediates() {
        enforceConsistency();
        return Collections.unmodifiableList(intermediates);
    }

	/**
	 * @return list of nodes which have at least one incoming arc and has no outgoing arcs
	 * This list includes internal outputs.
	 */
    public List getResults() {
        enforceConsistency();
        return Collections.unmodifiableList(results);
    }

    public DoubleMatrix2D getReachabilityMatrix() {
        enforceConsistency();
        return reachMatrix;
    }

    public DoubleMatrix2D getAdjMatrix() {
        enforceConsistency();
        return adjMatrix;
    }

    public DoubleMatrix2D getCorrelationMatrix() {
        return correlationMatrix;
    }

    public boolean hasLoops() {
        enforceConsistency();
        return loops != null && !loops.isEmpty();
    }

    public List getLoops() {
        enforceConsistency();
        return Collections.unmodifiableList(loops);
    }

    public List getLoopVariables() {
        enforceConsistency();
        return Collections.unmodifiableList(loopVariables);
    }

    public List getNodeGroups() {
        enforceConsistency();
        return Collections.unmodifiableList(nodeGroups);
    }

	public List getConnectedSubGraphs() {
		List relatedNodeSets = getIndependentSets(getOutputs());
		if (relatedNodeSets.isEmpty() || relatedNodeSets.size()==1)
			return Collections.singletonList(new DirectedGraph(this));

		List subGraphs = new ArrayList();
		List relatedNodes;
		for (int i = 0; i < relatedNodeSets.size(); i++) {
			relatedNodes = (List) relatedNodeSets.get(i);
			DSet parentNodeIndices = new DSet();
			Object node;
			for (int j = 0; j < relatedNodes.size(); j++) {
				node = relatedNodes.get(j);
				parentNodeIndices.addAll(getParentIndices(nodes.indexOf(node)).toList());
			}
			List subGraphNodes = convertIndicesToNodes(parentNodeIndices);
			subGraphNodes.addAll(relatedNodes);
			subGraphs.add(getSubgraphForVariables(subGraphNodes));
		}

		// create a subgraph for each disconnected node
		Iterator disconnectedNodesIterator = disconnectedNodes.iterator();
		while (disconnectedNodesIterator.hasNext()) {
			Object node = disconnectedNodesIterator.next();
			DirectedGraph dg = new DirectedGraph();
			dg.addNode(node);
			subGraphs.add(dg);
		}

		return subGraphs;
	}

	// todo: fix to support independent sets with shared items between the sets (see example 6)
    public List getIndependentSets(List varList) {
        enforceConsistency();
        List vars = new ArrayList(varList); // copy we can mutate
        List indepSets = new ArrayList();
        while (vars.size() > 0) {
            Object var = vars.remove(0);
            ArrayList set = new ArrayList();
            set.add(var);
            int i = 0; // which item in set are we testing?
            while (i < set.size()) {
                Object node1 = set.get(i);
                for (int j = vars.size() - 1; j >= 0; j--) { // decreasing order
                    Object node2 = vars.get(j);
                    if (areNodesCorrelated(node1, node2)) {
                        set.add(node2);
                        vars.remove(j);
                    }
                }
                i++;
            }
            indepSets.add(set);
        }
        return indepSets;
    }

    public DirectedGraph getGraphForInput(Object inNode) {
        return (DirectedGraph) affectedGraphForInputs.get(inNode); // should be immutable
    }

    public void calculateAffectedGraphForInputs() {
        enforceConsistency();
        affectedGraphForInputs = new HashMap();
        Iterator inNodes = inputs.iterator();
        while (inNodes.hasNext()) {
            Object inNode = inNodes.next();
            affectedGraphForInputs.put(inNode, getAffectedGraphForNode(inNode));
        }
        affectedGraphForInputs.put(null, getAffectedGraphForNode(null));
    }

    protected DirectedGraph getAffectedGraphForNode(Object node) {
        DirectedGraph dg = new DirectedGraph();
        List toNodes = (List) arcs.get(node); // should use accessor!
        if (toNodes != null) {
            dg.addNode(node);
            dg.addNodes(toNodes);
            dg.addArcs(node, toNodes);
            List inNodes = new ArrayList(toNodes);
            while (!inNodes.isEmpty()) {
                Object inNode = inNodes.remove(0);
                toNodes = (List) arcs.get(inNode);
                if (toNodes != null) {
                    dg.addNodes(toNodes);
                    if (dg.addArcs(inNode, toNodes)) {
                        inNodes.addAll(toNodes);
                    }
                }
            }
        }
        return dg;
    }

    // methods to modify graph

    public void clear() {
        if (!arcs.isEmpty()) {
            arcs.clear();
            markInconsistent();
        }
    }

    public boolean addNode(Object node) {
        if (nodes.add(node)) {
            markInconsistent();
            return true;
        } else {
            return false;
        }
    }

    public boolean removeNode(Object node) {
        if (nodes.contains(node)) {
            nodes.remove(node);
            arcs.remove(node);
            for (Iterator iterator = arcs.values().iterator(); iterator.hasNext();) {
                List values = (List) iterator.next();
                values.remove(node);
            }
            markInconsistent();
            return true;
        }
        return false;
    }

    public boolean addNodes(Collection nodes) {
        if (this.nodes.addAll(nodes)) {
            markInconsistent();
            return true;
        } else {
            return false;
        }
    }

    public boolean addArc(Object fromNode, Object toNode) {
        List fromNodeArcs = (List) arcs.get(fromNode);
        if (fromNodeArcs == null) {
            if (nodes.contains(fromNode)) {
                fromNodeArcs = new NodeSet();
                arcs.put(fromNode, fromNodeArcs);
            } else {
                System.err.println("DirectedGraph error -- invalid fromNode: " + fromNode);
                return false;
            }
        }
        if (fromNodeArcs.add(toNode)) {
            markInconsistent();
            return true;
        } else {
            return false;
        }
    }

    public boolean addArcs(Object fromNode, List toNodes) {
        List fromNodeArcs = (List) arcs.get(fromNode);
        if (fromNodeArcs == null) {
            if (nodes.contains(fromNode)) {
                fromNodeArcs = new NodeSet();
                arcs.put(fromNode, fromNodeArcs);
            } else {
                System.err.println("DirectedGraph error -- invalid fromNode: " + fromNode);
                return false;
            }
        }
        if (fromNodeArcs.addAll(toNodes)) {
            markInconsistent();
            return true;
        } else {
            return false;
        }
    }

    public boolean addGraph(DirectedGraph dg) {
        if (dg.isEmpty())
            return false;
        boolean graphChanged = nodes.addAll(dg.getNodes());
        Iterator dgArcEntries = dg.getArcs().entrySet().iterator();
        while (dgArcEntries.hasNext()) {
            Map.Entry arcEntry = (Map.Entry) dgArcEntries.next();
            graphChanged = addArcs(arcEntry.getKey(), (List) arcEntry.getValue()) || graphChanged;
        }
        if (graphChanged) {
            markInconsistent();
            return true;
        } else {
            return false;
        }
    }

    // protected methods

    protected void markInconsistent() {
        consistent = false;
    }


    protected void enforceConsistency() {
        if (!consistent)
            solveGraph();
    }

    protected void solveGraph() {
        FormatUtils.sortAlphabeticalOrder(nodes); // todo: remove alphabetical node sort
        if (nodes.size() == 0) {
            adjMatrix = null;
            reachMatrix = null;
            correlationMatrix = null;
	        disconnectedNodes = Collections.EMPTY_LIST;
            inputs = Collections.EMPTY_LIST;
            outputs = Collections.EMPTY_LIST;
            internalOutputs = Collections.EMPTY_LIST;
            intermediates = Collections.EMPTY_LIST;
            results = Collections.EMPTY_LIST;
            nodeGroups = Collections.EMPTY_LIST;
            loops = Collections.EMPTY_LIST;
            loopVariables = Collections.EMPTY_LIST;
            parentIndices = null;
        } else {
			calculate3Matrics();
            calculateInOuts();
            findLoops();
            findNodeGroups();
        }
        consistent = true;
    }

    //to clean the int[] in memory
    protected void clearMatrix(int[][] matrix_to_clear) {
        if (matrix_to_clear != null) {
            for (int i = 0; i < matrix_to_clear.length; i++) {
                matrix_to_clear[i] = null;
            }
        }
    }

    public void cleanup() {
        adjMatrix = null;
        reachMatrix = null;
        correlationMatrix = null;
        nodes.clear();
        arcs.clear();
	    if (loops != null && !loops.isEmpty())
            loops.clear();
        if (loopVariables != null && !loopVariables.isEmpty())
		    loopVariables.clear();
		if (disconnectedNodes != null && !disconnectedNodes.isEmpty())
	        disconnectedNodes.clear();
	    if (inputs != null && !inputs.isEmpty())
	        inputs.clear();
        if (outputs != null && ! outputs.isEmpty())
	        outputs.clear();
        if (internalOutputs != null && ! internalOutputs.isEmpty())
	        internalOutputs.clear();
        if (intermediates != null && ! intermediates.isEmpty())
	        intermediates.clear();
        if (results != null && ! results.isEmpty())
	        results.clear();
        if (nodeGroups != null && !nodeGroups.isEmpty())
	        nodeGroups.clear();
    }

    protected void calculate3Matrics() {
//decide what kind of matrix we want
        int nodes_size = nodes.size();
        int arcs_size = arcs.size();
        double loadFactor = ((double) arcs_size) / (((double) nodes_size) * ((double) nodes_size));

        if (loadFactor <= 0.1) {    //this creteria is given the non-zero cell is >= arcs_size(because in arcs, it's from_node-->to_nodes( # >=1)
            adjMatrix = new SparseDoubleMatrix2D(nodes_size, nodes_size);
            reachMatrix = new SparseDoubleMatrix2D(nodes_size, nodes_size);
            correlationMatrix = new SparseDoubleMatrix2D(nodes_size, nodes_size);
        } else {
            adjMatrix = new DenseDoubleMatrix2D(nodes_size, nodes_size);
            reachMatrix = new DenseDoubleMatrix2D(nodes_size, nodes_size);
            correlationMatrix = new DenseDoubleMatrix2D(nodes_size, nodes_size);
        }


        parentIndices = new OpenIntObjectHashMap();
        for (int i = 0; i < nodes_size; i++) {

            Object fromNode = nodes.get(i);
            List toNodes = (List) arcs.get(fromNode);
            if (toNodes != null) {
                Iterator it = toNodes.iterator();
                while (it.hasNext()) {
                    Object toNode = it.next();
                    int toNodeIndex = nodes.indexOf(toNode);
                    adjMatrix.setQuick(i, toNodeIndex, 1);
                    reachMatrix.setQuick(i, toNodeIndex, 1); //make a copy first
                }
            }
        }


        // calculate matrix
        //calculate reachability matrix from adjmatrix

	    // reachability matrix is 1 if path exists from fromNode to toNode
        for (int k = 0; k < nodes_size; k++) {
            for (int i = 0; i < nodes_size; i++) {
                if (reachMatrix.getQuick(i, k) == 1) {
                    for (int j = 0; j < nodes_size; j++) {
                        //reachMatrix[i][j] |= (reachMatrix[i][k] & reachMatrix[k][j]);--- original implement
                        if (reachMatrix.getQuick(i, j) != 1  && reachMatrix.getQuick(k, j) == 1) {
                            reachMatrix.setQuick(i, j, 1);
                        }
                    }
                }
            }
        }


	    // correlationMatrix is 1 if node1 and node2 have a common parent
	    // correlationMatrix is not 1 if node1's root parent is node2 or vice versa
        for (int i = 0; i < nodes_size; i++) {
            IntArrayList parentIndices_i = getParentIndices(i);
            for (int j = i + 1; j < nodes_size; j++) {
                if (DSet.intersects(parentIndices_i, getParentIndices(j))) {
                    correlationMatrix.setQuick(i, j, 1);
                    correlationMatrix.setQuick(j, i, 1);
                }
            }
        }
    }
    /*
     protected void calculateAdjacencyMatrix() {

         clearMatrix(adjMatrix);
         // if edge from a to b, set adjMatrix[a][b]=1
         int nodes_size = nodes.size();
         adjMatrix = new int[nodes_size][nodes_size];
         for (int i = 0; i < nodes_size; i++) {
             Object fromNode = nodes.get(i);
             List toNodes = (List) arcs.get(fromNode);
             if (toNodes != null) {
                 Iterator it = toNodes.iterator();
                 while (it.hasNext()) {
                     Object toNode = it.next();
                     int toNodeIndex = nodes.indexOf(toNode);
                     adjMatrix[i][toNodeIndex] = 1;
                 }
             }
         }
     }

     protected void calculateReachabilityMatrix() {
         clearMatrix(reachMatrix);
         int nodes_size = nodes.size();
         // if path from a to b, set reachMatrix[a][b]=1
         reachMatrix = new int[nodes_size][nodes_size];
         // copy adjacency matrix
         for (int i = 0; i < nodes_size; i++) {
             for (int j = 0; j < nodes_size; j++) {
                 reachMatrix[i][j] = adjMatrix[i][j];
             }
         }
         // calculate matrix
         for (int k = 0; k < nodes_size; k++) {
             for (int i = 0; i < nodes_size; i++) {
                 for (int j = 0; j < nodes_size; j++) {
                     reachMatrix[i][j] |= (reachMatrix[i][k] & reachMatrix[k][j]);
                 }
             }
         }
     }

     protected void calculateCorrelationMatrix() {
         clearMatrix(correlationMatrix);
         int nodes_size = nodes.size();
         // if a and b share any common parents, set correlationMatrix[a][b]=1 and correlationMatrix[b][a]=1
         // if a is parent of b, correlationMatrix[a][b]=0
         correlationMatrix = new int[nodes_size][nodes_size];
         for (int i = 0; i < nodes_size; i++) {
             List parentIndices_i = getParentIndices(i);
             for (int j = i + 1; j < nodes_size; j++) {
                 if (DSet.intersects(parentIndices_i, getParentIndices(j))) {
                     correlationMatrix[i][j] = 1;
                     correlationMatrix[j][i] = 1;
                 }
             }
         }
     }
         */

    protected IntArrayList getParentIndices(int nodeIndex) {
        IntArrayList parentIds = (IntArrayList) parentIndices.get(nodeIndex);
        if (parentIds == null) {
            parentIds = new IntArrayList();
            int nodes_size = nodes.size();
            for (int i = 0; i < nodes_size; i++) {
	            if (i == nodeIndex)
	                continue;
                else if (reachMatrix.getQuick(i, nodeIndex) == 1) {
                    parentIds.add(i);
                }
            }
            parentIndices.put(nodeIndex, parentIds);
        }
        return parentIds;
    }


    /* protected IntArrayList getParentIndices(int nodeIndex) {
         return (IntArrayList) parentIndices.get(nodeIndex);
     } */

    protected void calculateInOuts() {
        calculateAndSetInputs(); // nothing goes into it
	    calculateAndSetResults(); // nothing goes out
	    calculateAndSetIntermediates(); // something goes in and something goes out
	    calculateAndSetInternalOutputs();
        disconnectedNodes = new ArrayList(DSet.intersection(inputs, results));
	    if (!disconnectedNodes.isEmpty()) {
		    inputs.removeAll(disconnectedNodes);
		    results.removeAll(disconnectedNodes);
	    }
        outputs = new ArrayList(DSet.union(intermediates,results));
    }

    protected void calculateAndSetInputs() {
        int nodes_size = nodes.size();
        inputs = new ArrayList();
        for (int i = 0; i < nodes_size; i++) {
            if (_isInput(i)) {
                inputs.add(nodes.get(i));
            }
        }
    }

    protected boolean _isInput(int index) {
        int nodes_size = nodes.size();
        int sumInputsToNode = 0;
        for (int i = 0; i < nodes_size; i++) {
            sumInputsToNode += adjMatrix.getQuick(i, index);
        }
        return (sumInputsToNode == 0);
    }

    protected void calculateAndSetResults() {
        int nodes_size = nodes.size();
        results = new ArrayList();
        for (int i = 0; i < nodes_size; i++) {
            if (_isResult(i)) {
                results.add(nodes.get(i));
            }
        }
    }

    protected boolean _isResult(int index) {
        int nodes_size = nodes.size();
        int sumOutputsFromNode = 0;
        for (int j = 0; j < nodes_size; j++) {
            sumOutputsFromNode += adjMatrix.getQuick(index, j);
        }
        return (sumOutputsFromNode == 0);
    }

    protected void calculateAndSetInternalOutputs() {
        internalOutputs = new ArrayList();
        List drivenByRelationNodes = (List) arcs.get(null);
        if (drivenByRelationNodes != null)
            internalOutputs.addAll((List) arcs.get(null)); // driven by "null" (relation)
    }

    protected void calculateAndSetIntermediates() {
        int nodes_size = nodes.size();
        intermediates = new ArrayList();
        for (int i = 0; i < nodes_size; i++) {
            if (isNodeAnIntermediate(i)) {
                intermediates.add(nodes.get(i));
            }
        }
    }

    private boolean isNodeAnIntermediate(int nodeIndex) {
        int nodes_size = nodes.size();
        // intermediate if it has both incoming and outgoing arcs
        boolean arcInExists = false, arcOutExists = false;
        for (int i = 0; i < nodes_size; i++) {
            if (adjMatrix.getQuick(i, nodeIndex) == 1) {
                arcInExists = true;
                break;
            }
        }
        for (int i = 0; i < nodes_size; i++) {
            if (adjMatrix.getQuick(nodeIndex, i) == 1) {
                arcOutExists = true;
                break;
            }
        }
        return arcInExists && arcOutExists;
    }

    protected void findLoops() {
        int nodes_size = nodes.size();
        DSet loopVariableIndices = new DSet();
        for (int i = 0; i < nodes_size; i++) {
            for (int j = i + 1; j < nodes_size; j++) {
                if (reachMatrix.getQuick(i, j) == 1 && reachMatrix.getQuick(j, i) == 1) { // found loop
                    loopVariableIndices.add(new Integer(i));
                    loopVariableIndices.add(new Integer(j));
                }
            }
        }
        List loopsByIndices = new ArrayList();
        Collections.sort(loopVariableIndices);
        for (int i = 0; i < loopVariableIndices.size() - 1; i++) {
            List loopStart = new ArrayList();
            loopStart.add(loopVariableIndices.get(i));
            List moreLoops = findLoopsStartingWith(loopStart,
                    loopVariableIndices.subList(i + 1, loopVariableIndices.size()));
            if (!moreLoops.isEmpty()) {
                loopsByIndices.addAll(moreLoops);
            }
        }
        // convert node indices to nodes
        loopVariables = convertIndicesToNodes(loopVariableIndices);
        loops = new ArrayList();
        Iterator loopIt = loopsByIndices.iterator();
        while (loopIt.hasNext())
            loops.add(convertIndicesToNodes((Collection) loopIt.next()));
    }

    protected List convertIndicesToNodes(Collection nodeIndicesList) {
        List indicesNodes = new ArrayList();
        Iterator it = nodeIndicesList.iterator();
        while (it.hasNext())
            indicesNodes.add(nodes.get(((Integer) it.next()).intValue()));
        return indicesNodes;
    }

    protected List findLoopsStartingWith(List loopStart, List validNodes) {
        List loops = new ArrayList();
        List possibleNextNodes = getEdgesFromNode(((Integer) loopStart.get(loopStart.size() - 1)).intValue());
        Object firstNode = loopStart.get(0);
        if (possibleNextNodes.contains(firstNode)) { // found loop
            loops.add(loopStart);
        }
        Iterator nextNodes = DSet.intersection(possibleNextNodes, validNodes).iterator();
        while (nextNodes.hasNext()) {
            Object node = nextNodes.next();
            if (loopStart.contains(node)) // already in this loop
                continue;
            List nextLoopStart = new ArrayList(loopStart);
            nextLoopStart.add(node);
            List nextValidNodes = new ArrayList(validNodes);
            nextValidNodes.remove(node);
            List moreLoops = findLoopsStartingWith(nextLoopStart, nextValidNodes);
            if (!moreLoops.isEmpty()) {
                loops.addAll(moreLoops);
            }
        }
        return loops;
    }

    protected List getEdgesFromNode(int nodeIndex) {
        int nodes_size = nodes.size();
        // returns list of node indices for edges from nodeIndex
        List edgeEnds = new ArrayList();
        for (int i = 0; i < nodes_size; i++) {
            if (i != nodeIndex) {
                if (adjMatrix.getQuick(nodeIndex, i) == 1) {
                    edgeEnds.add(new Integer(i));
                }
            }
        }
        return edgeEnds;
    }

    public List getLinkedNodes(Object fromNode) {
        enforceConsistency();
        int nodeIndex = nodes.indexOf(fromNode);
        List linkedNodes = new ArrayList();
        int nodes_size = nodes.size();
        for (int i = 0; i < nodes_size; i++) {
            if (i != nodeIndex) {
                if (adjMatrix.getQuick(nodeIndex, i) == 1) {
                    linkedNodes.add(nodes.get(i));
                }
            }
        }
        return linkedNodes;
    }

    protected void findNodeGroups() {  //change here Qing Nov 7th
        if (!loops.isEmpty()) {
            nodeGroups = collapseLoops();
        } else {
            HashMap reversedArcs = reverseArcs(arcs);
            nodeGroups = breadthFirstGroups(nodes, reversedArcs);
        }
    }


    //qing added Nov 13rd,
    protected List collapseLoops() {

        if (superNodes == null)
            superNodes = new HashMap();//init
        else
            superNodes.clear();//clear memory   , recalculate

        //the following code will do arcs
        //recalculate the arcs, make it link to supernode instead

        HashMap collapsedArcs = new HashMap();
        for (Iterator i = loops.iterator(); i.hasNext();) {
            Object loopSeq = i.next();
            //use a Id object to represent supernode  here
            Id supernode = new Id("superNode" + loops.indexOf(loopSeq));
            superNodes.put(supernode, loopSeq);

            //add entry for super node
            //List supernodedependents = (List) collapsedArcs.get(supernode);
            // if (supernodedependents == null) {
            //    supernodedependents = new ArrayList();
            //    collapsedArcs.put(supernode, supernodedependents);
            //}
            //replace the fromnode in loop with supernode
            Iterator it_loopSeq = ((List) loopSeq).iterator();
            while (it_loopSeq.hasNext()) {
                Object node = it_loopSeq.next();
                List nodedependents = (List) arcs.get(node);
                if (nodedependents != null) {
                    Iterator it_depend = nodedependents.iterator();
                    while (it_depend.hasNext()) {
                        Object node2 = it_depend.next();
                        if (!((List) loopSeq).contains(node2)) {     //not in this loop
                            //add an arc
                            //add entry for super node
                            List supernodedependents = (List) collapsedArcs.get(supernode);
                            if (supernodedependents == null) {
                                supernodedependents = new ArrayList();
                                collapsedArcs.put(supernode, supernodedependents);
                            }
                            supernodedependents.add(node2);
                        }
                    }
                }
            }
        }

        //then do tonode are in loop
        Iterator it_arcs = (arcs.keySet()).iterator();
        while (it_arcs.hasNext()) {
            Object fromnode = it_arcs.next();
            if (!loopVariables.contains(fromnode)) {
                //is some node won't be collapsed by the super node


                //add this arc
                List fromnodedependents = (List) arcs.get(fromnode);
                Iterator it_fromnodedependents = fromnodedependents.iterator();
                while (it_fromnodedependents.hasNext()) {
                    Object tonode = it_fromnodedependents.next();
                    Id superId = getSuperNodeId(tonode);
                    if (superId == null)//not in a SC
                    {
                        //add this arc
                        List nodeldependents = (List) collapsedArcs.get(fromnode);
                        if (nodeldependents == null) {
                            nodeldependents = new ArrayList();
                            collapsedArcs.put(fromnode, nodeldependents);
                        }
                        nodeldependents.add(tonode);

                    } else {
                        List nodeldependents = (List) collapsedArcs.get(fromnode);
                        if (nodeldependents == null) {
                            nodeldependents = new ArrayList();
                            collapsedArcs.put(fromnode, nodeldependents);
                            nodeldependents.add(superId);  //add arc end to SC
                        } else {
                            if (!nodeldependents.contains(superId)) {
                                nodeldependents.add(superId);  //add arc end to SC
                            }
                        }
                    }

                }
            }
        }

        //----- now we have collapsed graph with collapsedArcs

        //the following code will replace the loop nodes by it's super nodes
        List collapsedNode = new ArrayList();
        Iterator nodesIt = nodes.iterator();
        while (nodesIt.hasNext()) {
            Object node = nodesIt.next();
            if (!loopVariables.contains(node)) {
                collapsedNode.add(node);
            } else {
                Id superNode = getSuperNodeId(node);
                if (superNode != null && !collapsedNode.contains(superNode)) {
                    collapsedNode.add(superNode);
                }
            }
        }
        //---------now we have collapsed nodes

        //then we can find nodegroup in the result collapsed DAG
        HashMap reversedArcs = reverseArcs(collapsedArcs);

        List result = breadthFirstGroups(collapsedNode, reversedArcs);

        //replace the SC with an arraylist of it's component
        Iterator nodeGroupsIt = result.iterator();
        while (nodeGroupsIt.hasNext()) {
            Object nodeGroup = nodeGroupsIt.next();
            //shall be a list
            if (nodeGroup != null) {
                Iterator nodeGroupIt = ((List) nodeGroup).iterator();
                ArrayList dirtyIndex = new ArrayList();//to keep a record of SCs
                while (nodeGroupIt.hasNext()) {
                    Object nextNode = nodeGroupIt.next();
                    if (nextNode instanceof Id)//is a superNode{
                    {
                        dirtyIndex.add(nextNode);
                    }
                }

                if (dirtyIndex.size() > 0) {//there are SCs
                    for (int i = 0; i < dirtyIndex.size(); i++) {
                        Id supernode = (Id) dirtyIndex.get(i);
                        List loopSeq = (List) superNodes.get(supernode);
                        if (loopSeq != null) {
                            //replace this super node with an arraylist
                            int index = ((List) nodeGroup).indexOf(supernode);
                            //remove Supernode
                            ((List) nodeGroup).remove(supernode);
                            //insert an arraylist
                            ((List) nodeGroup).add(index, loopSeq);
                        } else {//is null, should remove this SC
                            ((List) nodeGroup).remove(supernode);
                        }

                    }
                }
            }
        }

        return result;

    }


    protected Id getSuperNodeId(Object node_in_loop) {
        if (superNodes == null) return null;
        Iterator it = superNodes.keySet().iterator();
        while (it.hasNext()) {
            Id supernode = (Id) it.next();
            List loopSeq = (List) superNodes.get(supernode);
            if (loopSeq.contains(node_in_loop)) return supernode;
        }

        return null;//not found
    }


    /**
     *
     * @param arcs
     * @return
     */
    public static HashMap reverseArcs_excludeloopArcs(HashMap arcs) {

        HashMap reversedArcs = new HashMap();
        Iterator it = arcs.keySet().iterator();
        while (it.hasNext()) {
            Object node1 = it.next();
            Iterator dependents = ((List) arcs.get(node1)).iterator();       //get tonodes
            while (dependents.hasNext()) {
                Object node2 = dependents.next();
                List node2dependents = (List) reversedArcs.get(node2);
                if (node2dependents == null) {
                    node2dependents = new ArrayList();
                    reversedArcs.put(node2, node2dependents);
                }
                node2dependents.add(node1);
            }
        }
        return reversedArcs;
    }

    public static HashMap reverseArcs(HashMap arcs) {
        HashMap reversedArcs = new HashMap();
        Iterator it = arcs.keySet().iterator();
        while (it.hasNext()) {
            Object node1 = it.next();
            Iterator dependents = ((List) arcs.get(node1)).iterator();
            while (dependents.hasNext()) {
                Object node2 = dependents.next();
                List node2dependents = (List) reversedArcs.get(node2);
                if (node2dependents == null) {
                    node2dependents = new ArrayList();
                    reversedArcs.put(node2, node2dependents);
                }
                node2dependents.add(node1);
            }
        }
        return reversedArcs;
    }

    public static List breadthFirstGroups(List nodes, HashMap arcs) {
        List nodesLeft = new ArrayList(nodes);
        HashMap arcsLeft = new HashMap(arcs);
        List result = new ArrayList();
        while (!nodesLeft.isEmpty()) {
            List currentIndependents = new ArrayList();
            Iterator nodesLeftIt = nodesLeft.iterator();
            while (nodesLeftIt.hasNext()) {
                Object node1 = nodesLeftIt.next();
                if (!arcsLeft.containsKey(node1))
                    currentIndependents.add(node1);
            }
            result.add(currentIndependents);
            Iterator currentIndependentsIt = currentIndependents.iterator();
            while (currentIndependentsIt.hasNext()) {
                Object node2 = currentIndependentsIt.next();
                Object[] node3s = arcsLeft.keySet().toArray();
                for (int i = 0; i < node3s.length; i++) {
                    Object node3 = node3s[i];
                    List node3dependents = (List) arcsLeft.get(node3);
                    if (node3dependents.contains(node2)) {
                        node3dependents.remove(node2);
                        if (node3dependents.isEmpty()) {
                            arcsLeft.remove(node3);
                        }
                    }
                }
                nodesLeft.remove(node2);
            }
        }
        return result;
    }

    protected boolean areNodesCorrelated(Object node1, Object node2) {
        enforceConsistency();
        return correlationMatrix.getQuick(nodes.indexOf(node1), nodes.indexOf(node2)) == 1;
    }

    // methods to print state of graph

    public String toString() {
        return "nodes: " + Names.getNames(nodes) + "\narcs: " + FormatUtils.mapToString(arcs);
    }

    public void printState() {
        enforceConsistency();
        System.out.println("\nadjacency matrix:\n" + FormatUtils.formatMatrixWithLabels(adjMatrix, nodes));
        System.out.println("\nreachability matrix:\n" + FormatUtils.formatMatrixWithLabels(reachMatrix, nodes));
        System.out.println("\ncorrelation matrix:\n" + FormatUtils.formatMatrixWithLabels(correlationMatrix, nodes));
	    System.out.println("\ndisconnected nodes: "+disconnectedNodes);
        System.out.println("inputs: " + inputs);
        System.out.println("outputs: " + outputs);
        System.out.println("internalOutputs: " + internalOutputs);
        System.out.println("intermediates: " + intermediates);
        System.out.println("results: " + results);
        System.out.println("loops: " + loops);
        System.out.println("loopVariables: " + loopVariables);
        System.out.println("nodeGroups: " + nodeGroups);
    }

    public class NodeSet extends DSet {

        public NodeSet() {

        }

        public NodeSet(Collection objs) {
            super(objs);
        }

        public NodeSet(Object[] objs) {
            super(objs);
        }

        protected boolean addHookBefore(Object obj) {
            if (obj == null || nodes.contains(obj)) {
                return super.addHookBefore(obj);
            } else {
                System.err.println("DirectedGraph error -- invalid toNode: " + obj);
                return false;
            }
        }
    }

    public DirectedGraph getSubgraphForVariables(List variables) {
        enforceConsistency();
        if (reachMatrix == null) { // todo: what else should be done here?
            System.err.println("DirectedGraph.getSubgraphForVariables - no reachability matrix available");
            return new DirectedGraph();
        }

        DirectedGraph subGraph = new DirectedGraph();
        subGraph.addNodes(variables);

        int variablesize = variables.size();
        int[] oldGraphIndexes = new int[variablesize];
        for (int i = 0; i < variablesize; i++) {
            oldGraphIndexes[i] = nodes.indexOf(variables.get(i));
        }

        //  int[][] tempSubConMat = new int[variablesize][variablesize];
        DoubleMatrix2D tempSubConMat = new DenseDoubleMatrix2D(variablesize, variablesize);

        for (int i = 0; i < variablesize; i++) {
            for (int j = 0; j < variablesize; j++) {
                int index1 = oldGraphIndexes[i];
                int index2 = oldGraphIndexes[j];
                if (index1 == -1 || index2 == -1) {
                    tempSubConMat.setQuick(i, j, 0);
                } else if (reachMatrix.getQuick(index1, index2) == 1) {
                    //subGraph.addArc(nodes.get(oldGraphIndexes[i]), nodes.get(oldGraphIndexes[j]));
                    tempSubConMat.setQuick(i, j, 1);
                } else {
                    tempSubConMat.setQuick(i, j, 0);
                }
            }
        }
        //System.out.println("\ntempSubConMat:\n" + FormatUtils.formatMatrixWithLabels(tempSubConMat, variables));
        return getSubGraphLongestPath(tempSubConMat, oldGraphIndexes, subGraph);
    }

    public DirectedGraph getSubGraphLongestPath(DoubleMatrix2D subAdjMat, int[] fullGraphIndex, DirectedGraph subGraph) {
        int row = subAdjMat.rows();
        int col = row; // always a sqaure matrix
        // int[][] refMat = new int[row][col]; // reference matrix

        DoubleMatrix2D refMat = subAdjMat.copy();
        /*  for (int i = 0; i < row; i++) {
              for (int j = 0; j < col; j++) {
                  refMat[i][j] = subAdjMat[i][j];
              }
          }  */

        for (int i = 0; i < row; i++) { // go through all rows
            for (int j = 0; j < col; j++) { // go through all columns
                if (refMat.getQuick(i, j) == 1) { // found a "1" entry
                    for (int k = 0; k < col; k++) { // search other columns for another "1"
                        if (k == j)
                            continue;
                        if (refMat.getQuick(i, k) == 1) { // found a "1" entry
                            if (refMat.getQuick(k, j) == 1) { // found a longer path
                                subAdjMat.setQuick(i, j, 0); // remove the shorter path
                                break; // stop searching other columns for another "1"
                            }
                        }
                    }
                }
            }
        } // finished and got an adjacency matrix for the subgraph


        // check the adjacency matrix of the full graph to see if anything is missing .. and also add arcs
        for (int i = 0; i < row; i++) { // go through all rows
            for (int j = 0; j < col; j++) { // go through all columns
                int fullMatRowIndex = fullGraphIndex[i];
                int fullMatcolIndex = fullGraphIndex[j];
                if (fullMatRowIndex == -1 || fullMatcolIndex == -1)
                    continue;
                if (adjMatrix.get(fullMatRowIndex, fullMatcolIndex) == 1)
                    subAdjMat.set(i, j, 1);
                if (subAdjMat.get(i, j) == 1)
                    subGraph.addArc(nodes.get(fullGraphIndex[i]), nodes.get(fullGraphIndex[j]));
            }
        }

        return subGraph;
    }

    public static Element toXmlRef(String tag, Object obj) {
        Element xmlElement = DocumentHelper.createElement(tag);
        if (obj instanceof DomeObject) {
            xmlElement.addAttribute("idRef", ((DomeObject) obj).getId().getIdString());
        } else if (obj instanceof NameIdNode) {
            xmlElement.addAttribute("idRef", ((NameIdNode) obj).getId());
            xmlElement.addAttribute("name", ((NameIdNode) obj).getName());
	        xmlElement.addAttribute("type", ((NameIdNode) obj).getType());
        } else if (obj instanceof MultiItemNode) {
	        xmlElement.addAttribute("idRef", ((MultiItemNode) obj).getId());
	        xmlElement.addAttribute("name", ((MultiItemNode) obj).getName());
			Iterator items = ((MultiItemNode)obj).getItems().iterator();
	        while (items.hasNext()) {
		        xmlElement.add(toXmlRef("item", items.next()));
	        }
        } else {
            xmlElement.addAttribute("idRef", obj.toString());
        }
        return xmlElement;
    }

    public static Object parseXmlRef(Element xmlElement) {
        String idString = xmlElement.attributeValue("idRef");
        if (idString == null)
            throw new IllegalArgumentException("no xml idRef: " + xmlElement.asXML());

        String nameString = xmlElement.attributeValue("name");
        if (nameString == null)
            return idString;
	    String typeString = xmlElement.attributeValue("type");
	    if (typeString!=null)
	        return new NameIdNode(idString, nameString, typeString);
        XMLUtils.makeRootElement(xmlElement);
	    List itemsXml = xmlElement.elements("item");
	    if (itemsXml==null || itemsXml.isEmpty())
		    return new NameIdNode(idString, nameString);
	    List items = new ArrayList();
	    for (int i = 0; i < itemsXml.size(); i++) {
		    Element itemXml = (Element) itemsXml.get(i);
		    items.add(parseXmlRef(itemXml));
	    }
	    return new MultiItemNode(idString, nameString, items);
    }


    public Element toXmlElement(String id) {
	    XMLUtils.DomeObjectComparator idComparator = new XMLUtils.DomeObjectComparator(); // order items by id
        Element xml = DocumentHelper.createElement("directedGraph");
	    if (id!=null)
            xml.addAttribute("id", id);

        Element nodeXml = xml.addElement("nodes");
        Element arcXml = xml.addElement("arcs");
	    List nodeList = new ArrayList(nodes);
		Collections.sort(nodeList, idComparator);
        for (Iterator iterator = nodeList.iterator(); iterator.hasNext();) {
            nodeXml.add(toXmlRef("node", iterator.next()));
        }

	    List arcKeys = new ArrayList(arcs.keySet());
	    Collections.sort(arcKeys, idComparator);
	    Element fromXml;
	    List toNodes;
        for (Iterator iterator = arcKeys.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            fromXml = toXmlRef("from", o);
            arcXml.add(fromXml);
            toNodes = new ArrayList((NodeSet) arcs.get(o));
	        Collections.sort(toNodes, idComparator);
            for (Iterator iterator2 = toNodes.iterator(); iterator2.hasNext();) {
                fromXml.add(toXmlRef("to", iterator2.next()));
            }
        }
        return xml;
    }

    protected void loadXml(Element xmlElement) {
        XMLUtils.makeRootElement(xmlElement);
        List nodeXml = xmlElement.selectNodes("/directedGraph/nodes/node");

        for (Iterator iterator = nodeXml.iterator(); iterator.hasNext();) {
            addNode(parseXmlRef((Element) iterator.next()));
        }

        List fromXml = xmlElement.selectNodes("/directedGraph/arcs/from");

        for (int i = 0; i < fromXml.size(); i++) {
            Element from = (Element) fromXml.get(i);
            Object fromNode = parseXmlRef(from);
            XMLUtils.makeRootElement(from);
            List toXml = from.selectNodes("/from/to");

            for (Iterator iterator2 = toXml.iterator(); iterator2.hasNext();) {
                addArc(fromNode, parseXmlRef((Element) iterator2.next()));
            }
        }
    }

	public static String getGraphIdFromXml(Element xml) {
		return xml.attributeValue("id");
	}

    public List getAffectedNodes(Object changedNode) {
        enforceConsistency();
        int changedNodeIndex = nodes.indexOf(changedNode);
        if (changedNodeIndex == -1) {
            System.err.println("DirectedGraph.getAffectedNodes: could not find node " + Names.getName(changedNode));
            return Collections.EMPTY_LIST;
        }
        DArrayList affectedNodes = new DArrayList();
        int size = reachMatrix.rows();
        for (int i = 0; i < size; i++) {
            if (reachMatrix.getQuick(changedNodeIndex, i) == 1)
                affectedNodes.add(nodes.get(i));
        }
        return affectedNodes;
    }

	public List getAffectedNodes(Collection changedNodes)
	{
		if (changedNodes == null || changedNodes.isEmpty())
			return Collections.EMPTY_LIST;
		enforceConsistency();
		List affectedNodes = new ArrayList();
		List unaffectedNodeIndicesList = new ArrayList(); // fill it with outputs
		for (int i = 0; i < outputs.size(); i++) {
			unaffectedNodeIndicesList.add(new Integer(nodes.indexOf(outputs.get(i))));
		}
		Object changedNode;
		for (Iterator iterator = changedNodes.iterator(); iterator.hasNext();) {
			changedNode = iterator.next();
			int changedNodeIndex = nodes.indexOf(changedNode);
			if (changedNodeIndex == -1) {
				System.err.println("DirectedGraph.getAffectedNodes: could not find node " + Names.getName(changedNode));
				continue;
			}

			int[] unaffectedNodesIndices = Converters.toIntArray(unaffectedNodeIndicesList);
			unaffectedNodeIndicesList = new ArrayList();
			int nodeIndex;
			for (int i = 0; i < unaffectedNodesIndices.length; i++) {
				nodeIndex = unaffectedNodesIndices[i];
				if (reachMatrix.getQuick(changedNodeIndex, nodeIndex) == 1)
					affectedNodes.add(nodes.get(nodeIndex));
				else
					unaffectedNodeIndicesList.add(new Integer(nodeIndex));
			}
			if (unaffectedNodeIndicesList.isEmpty())
				break;
		}
		return affectedNodes;
	}

    /**
     * for checking whether a pair of nodes are correlated in the graph
     * @param node1
     * @param node2
     * @return
     */
    public boolean areCorrelatedNodes(Object node1, Object node2) {
        enforceConsistency();
	    int index1 = nodes.indexOf(node1);
	    int index2 = nodes.indexOf(node2);
	    if (index1 == -1 || index2 == -1)
		    return false;
        return (correlationMatrix.getQuick(index1, index2) == 1);
    }

    /**
     * for checking whether a pair of nodes are reachable in the graph
     * fromNode goes to toNode
     * @param fromNode
     * @param toNode
     * @return
     */
    public boolean areReachableNodes(Object fromNode, Object toNode) {
        enforceConsistency();
	    int index1 = nodes.indexOf(fromNode);
	    int index2 = nodes.indexOf(toNode);
	    if (index1 == -1 || index2 == -1)
		    return false;
        return (reachMatrix.getQuick(index1, index2) == 1);
    }

    /**
     * for checking whether a pair of nodes are adjacent in the graph
     * fromNode goes to toNode
     * @param fromNode
     * @param toNode
     * @return
     */
    public boolean areAdjacentNodes(Object fromNode, Object toNode) {
        enforceConsistency();
	    int index1 = nodes.indexOf(fromNode);
	    int index2 = nodes.indexOf(toNode);
	    if (index1 == -1 || index2 == -1)
		    return false;
        return (adjMatrix.getQuick(index1, index2) == 1);
    }

	/**
	 * Returns common parents of node1 and node2.
	 * Note that a node is not a parent of itself.
	 * @param node1
	 * @param node2
	 * @return
	 */
	public List getCommonParents(Object node1, Object node2) {
		int index1 = nodes.indexOf(node1);
		int index2 = nodes.indexOf(node2);
		if (index1==-1 || index2==-1)
			return Collections.EMPTY_LIST;
		IntArrayList parents1 = getParentIndices(index1);
		IntArrayList parents2 = getParentIndices(index2);
		Collection commonParentIndices = DSet.intersection(parents1.toList(),parents2.toList());
		List commonParents = new ArrayList(commonParentIndices.size());
		for (Iterator iterator = commonParentIndices.iterator(); iterator.hasNext();) {
			commonParents.add(nodes.get(((Integer) iterator.next()).intValue()));
		}
		return commonParents;
	}

    /**
     * use in the case where 2 inputs are reachable within the porject context
     * @param fromNode
     * @param toNode
     * @return
     */
    public Vector getPath(Object fromNode, Object toNode) {
        Vector path = new Vector();
        boolean hasMore = true;
        while (hasMore) {
            if (areReachableNodes(fromNode, toNode) && !areAdjacentNodes(fromNode, toNode)) {
                List linkedNodes = getLinkedNodes(fromNode);
                for (int i = 0; i < linkedNodes.size(); i++) {
                    Object linkedNode = linkedNodes.get(i);
                    if (areReachableNodes(linkedNode, toNode)) {
                        if (areAdjacentNodes(linkedNode, toNode)) // reaches the toNode --> end of path
                            hasMore = false;
                        else
                            hasMore = true;
                        path.add(linkedNode);
                        fromNode = linkedNode;
                        break;
                    }
                }
            }
        }
        return path;
    }


    public void updateGraph() {
        if (consistent) return;
        enforceConsistency();
    }

	/**
	 * Returns a new graph which has the elements of g1 not in g2.
	 * Nodes which are in g1 and not in g2 are included as are arcs (and the nodes involved)
	 * which exist in g1 and not in g2.
	 * @param g1
	 * @param g2
	 * @return new graph which is the result of removing elements of g2 from g1
	 */
	public static DirectedGraph removeGraph(DirectedGraph g1, DirectedGraph g2) {
		Collection nodesInG1NotInG2 = DSet.removeSet(g1.getNodes(), g2.getNodes());
		DirectedGraph resultGraph = new DirectedGraph();
		resultGraph.addNodes(nodesInG1NotInG2);
		Map arcs = g1.getArcs();
		Iterator entrys = arcs.entrySet().iterator();
		Map.Entry entry;
		while (entrys.hasNext()) {
			entry = (Map.Entry) entrys.next();
			Object fromNode = entry.getKey();
			List toNodes = (List) entry.getValue();
			if (nodesInG1NotInG2.contains(fromNode)) {
				resultGraph.addNodes(toNodes);
				resultGraph.addArcs(fromNode,toNodes);
			}
			else {
				List newToNodes = new ArrayList();
				Object toNode;
				for (int i = 0; i < toNodes.size(); i++) {
					toNode = toNodes.get(i);
					if (!g2.containsArc(fromNode, toNode)) {
						newToNodes.add(toNode);
					}
				}
				if (!newToNodes.isEmpty()) {
					resultGraph.addNode(fromNode);
					resultGraph.addNodes(newToNodes);
					resultGraph.addArcs(fromNode, newToNodes);
				}
			}
		}
		return resultGraph;
	}

	public static DirectedGraph mapGraph(DirectedGraph graph, NodeConverter func) {
		HashMap nodeMap = new HashMap();
		Iterator nodes = graph.getNodes().iterator();
		Object node;
		Object newNode;
		while (nodes.hasNext()) {
			node = nodes.next();
			newNode = func.mapNode(node);
			if (newNode != null)
				nodeMap.put(node,newNode);
		}
		return new DirectedGraph(graph,nodeMap);
	}

	/**
	 * Extend this class to map nodes via some function
	 */
	public static class NodeConverter {
		/**
		 *
		 * @param node
		 * @return null, if node should not have mapping in new map (equivalent to returning itself)
		 * otherwise, return new object for node
		 */
		public Object mapNode(Object node) {
			return node;
		}
	}

    public static void main(String[] args) {

	/*	DirectedGraph dg1 = new DirectedGraph();
		dg1.addNodes(Arrays.asList(new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k","l","m"}));
		System.out.println(dg1);
		dg1.addArc("a","c");
		dg1.addArc("a","d");
		dg1.addArc("b","c");
		dg1.addArc("c","g");
		dg1.addArc("c","h");
		dg1.addArc("d","f");
		dg1.addArc("e","f");
		dg1.addArc("e","j");
		dg1.addArc("f","i");
		dg1.addArc("f","m");
		dg1.addArc("g","m");
		dg1.addArc("h","i");
		dg1.addArc("h","j");
		dg1.addArc("k","i");
		dg1.addArc("l","m");
		System.out.println(dg1);
		//dg1.printState();

		System.out.println("==================================");

		DirectedGraph sub = dg1.getSubgraphForVariables(Arrays.asList(new String[]{"a", "c", "h"}));
		System.out.println(sub);
		sub.printState();*/

/*		// independent sets
		System.out.println("\nindependent sets");
		System.out.println(dg1.getIndependentSets(Arrays.asList(new Object[]{"g","h","f"})));
		System.out.println(dg1.getIndependentSets(Arrays.asList(new Object[]{"k","h","f"})));
		System.out.println(dg1.getIndependentSets(Arrays.asList(new Object[]{"g","k","e"})));
		System.out.println(dg1.getIndependentSets(Arrays.asList(new Object[]{"l","f","m","j"})));

		// adding graphs
		System.out.println("\nadding graphs");
		DirectedGraph dg2 = new DirectedGraph();
		dg2.addNodes(Arrays.asList(new String[]{"b","l","c","h","d","j"}));
		dg2.addArc("b","l");
		dg2.addArc("c","h");
		dg2.addArc("d","j");
		System.out.println(dg2);
		dg1.addGraph(dg2);
		System.out.println(dg1);
		dg1.printState();
		// loop detection
/*		System.out.println("\nloop detection");
		DirectedGraph dg3 = new DirectedGraph();
		dg3.addNodes(Arrays.asList(new String[]{"a","b","c","d","e","f","g","h","i","j","k"}));
		dg3.addArc("a", "b");
		dg3.addArc("a", "c");
		dg3.addArc("c", "f");
		dg3.addArc("b", "d");
		dg3.addArc("e", "b");
		dg3.addArc("d", "e");
		dg3.addArc("c", "g");
		dg3.addArc("g", "h");
		dg3.addArc("h", "i");
		dg3.addArc("i", "c");
		dg3.addArc("h", "k");
		dg3.addArc("k", "g");
		System.out.println(dg3);
		dg3.printState();
		*/



     /*   DirectedGraph dg4 = new DirectedGraph();
        dg4.addNodes(Arrays.asList(new String[]{"h", "w", "h1", "w1", "a", "a1", "a2", "l2", "v2", "v"}));
        //System.out.println(dg4);
        dg4.addArc("h", "h1");
        dg4.addArc("w", "w1");
        dg4.addArc("h1", "a1");
        dg4.addArc("w1", "a1");
        dg4.addArc("a", "a1");
        dg4.addArc("a1", "a2");
        dg4.addArc("a2", "v2");
        dg4.addArc("l2", "v2");
        dg4.addArc("v2", "v");
        dg4.addArc("v", "h");


        System.out.println("linked nodes: " + dg4.getLinkedNodes("w1"));
        System.out.println("linked nodes: " + dg4.getLinkedNodes("l2"));

        dg4.enforceConsistency();

        System.out.println(dg4.loopVariables.size());*/

        //DirectedGraph sub = dg4.getSubgraphForVariables(Arrays.asList(new String[]{"h1", "w1", "a1", "l2", "v2"}));
        //System.out.println(sub);
        //sub.printState();

        //GLPanel.visualizeGraph(sub);
        //dg4.toXmlFile("myGraph");
        //Element xmlElement = sub.toXmlElement("fake");
        //XMLUtils.print(xmlElement);
        //DirectedGraph ng = new DirectedGraph(xmlElement);
        //System.out.println("ng " + ng);

//	    // MultiItemNodes
//	    Parameter p = new ConcreteParameter(null, new Id("1234"));
//	    NameIdNode n1 = new NameIdNode("abc","stringthing");
//	    MultiItemNode n2 = new MultiItemNode(Arrays.asList(new Object[]{"first","second","third"}));
//	    Parameter[] params = new Parameter[]{new ConcreteParameter(null, new Id("10")),
//	                                         new ConcreteParameter(null, new Id("11"),DomeInteger.TYPE_INFO.getTypeName())};
//	    MultiItemNode n3 = new MultiItemNode(Arrays.asList(params));
//	    NameIdNode n4 = new NameIdNode("def", "total project", NameIdNode.PROJECT_NODE);
//
//	    DirectedGraph d5 = new DirectedGraph();
//	    d5.addNodes(Arrays.asList(new Object[]{p,n1,n2,n3,n4}));
//	    d5.addArc(p,n1);
//	    d5.addArc(p,n2);
//	    d5.addArc(n1,n3);
//	    d5.addArc(n1,n4);
//        System.out.println(d5);
//	    GLPanel.visualizeGraph("first pass",d5);
//	    Element d5Xml = d5.toXmlElement("d5");
//	    XMLUtils.print(d5Xml);
//	    DirectedGraph d5_2 = new DirectedGraph(d5Xml);
//	    System.out.println(d5_2);
//	    GLPanel.visualizeGraph("second pass", d5_2);

//	    // independent sets with shared parameters in sets
//	    DirectedGraph d6 = new DirectedGraph();
//	    d6.addNodes(Arrays.asList(new String[]{"a","b","c","d","k","m"}));
//	    d6.addArc("a","d");
//	    d6.addArc("b","d");
//	    d6.addArc("c","d");
//	    d6.addArc("k","a");
//	    d6.addArc("k","b");
//	    d6.addArc("m","b");
//	    d6.addArc("m","c");
//	    //System.out.println(d6.getIndependentSets(Arrays.asList(new Object[]{"a","b","c"}))); // [[a,b],[b,c]]
//
//	    // removeGraph
//	    DirectedGraph d7 = new DirectedGraph();
//	    d7.addNodes(Arrays.asList(new String[]{"a", "b", "c", "d", "k",}));
//	    d7.addArc("a", "d");
//	    d7.addArc("c", "d");
//	    d7.addArc("k", "a");
//	    d7.addArc("k", "b");
//	    System.out.println(DirectedGraph.removeGraph(d6,d7));

	    // get connected subgraphs
	    DirectedGraph d8 = new DirectedGraph();
	    d8.addNodes(Arrays.asList(new String[]{"a","b","c","d","e","f","g","h","m"}));
	    d8.addArc("a","b");
	    d8.addArc("c","d");
	    d8.addArc("c","e");
	    d8.addArc("f","h");
	    d8.addArc("f","g");
	    List subgraphs = d8.getConnectedSubGraphs();
	    for (int i = 0; i < subgraphs.size(); i++) {
		    DirectedGraph dg = (DirectedGraph) subgraphs.get(i);
		    System.out.println(dg);
	    }
    }
}
