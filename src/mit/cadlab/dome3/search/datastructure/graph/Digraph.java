package mit.cadlab.dome3.search.datastructure.graph;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import mit.cadlab.dome3.util.DSet;

import java.util.*;

/**
 * A generic directed mit.cadlab.dome3.search.datastructure.graph representation class
 * adopted and modified from mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph class
 */
public class Digraph{
    protected DSet nodes=new DSet();
    protected DSet parameters = new DSet();  // added by Ligon stores corresponding Dome Parameters
    protected HashMap arcs = new HashMap(); // key is fromNode, value is set of toNodes
    // all fromNodes and toNodes must exist in nodes; else an error is raised
    DoubleMatrix2D adjMatrix,reachMatrix;
    protected boolean consistent = false;

    protected List disconnectedNodes; // no incoming or outgoing arcs, not even from internal relation
    protected List inputs; // no incoming arcs, has outgoing arcs
    protected List outputs; // has incoming arcs, may have outgoing arcs - same as intermediates and results together

    protected String INPUT = "input";
    protected String OUTPUT = "output";
    protected String DISCONNECTED = "disconnected";


    public Digraph() {
    }

    public boolean containsNode(Object n) {
        if (nodes == null || nodes.size() == 0) return false;
        return nodes.contains(n);
    }

   public DSet getNodes() {
        return nodes;
    }

    public HashMap getArcs() {
        return arcs;
    }

    public boolean containsArc(Object fromNode, Object toNode) {
        Object toNodes = arcs.get(fromNode);
        if (toNodes == null)
            return false;
        return ((Collection) toNodes).contains(toNode);
    }

    public boolean isEmpty() {
        return nodes.isEmpty(); // arcs should be empty then, as well
    }
    // protected methods

    protected void markInconsistent() {
        consistent = false;
    }

    public boolean  addArc(Object fromNode, Object toNode) {
        List fromNodeArcs = (List) arcs.get(fromNode);
        if (fromNodeArcs == null) {
            if (nodes.contains(fromNode)) {
                fromNodeArcs = new Digraph.NodeSet();
                arcs.put(fromNode, fromNodeArcs);
            } else {
                System.err.println("DiGraph error -- invalid fromNode: " + fromNode);
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
                fromNodeArcs = new AttributedGraph.NodeSet();
                arcs.put(fromNode, fromNodeArcs);
            } else {
                System.err.println("DiGraph error -- invalid fromNode: " + fromNode);
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

    public boolean addNode(Object node) {
        if (nodes.add(node)) {
            markInconsistent();
            return true;
        } else {
            return false;
        }
    }

    //Added by Ligon
    public void addParameterAndNode(Object node, Object parameter){
        addNode(node);
        parameters.add(parameter);
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

    protected void enforceConsistency() {
        if (!consistent)
            solveGraph();
    }

    protected void solveGraph() {
        if (nodes.size() == 0) {
            adjMatrix = null;
            reachMatrix = null;
            disconnectedNodes = Collections.EMPTY_LIST;
            inputs = Collections.EMPTY_LIST;
            outputs = Collections.EMPTY_LIST;
        } else {
            calculateMatrics();
            calculateInOuts();
        }
        consistent = true;
    }


    protected void calculateMatrics() {
//decide what kind of matrix we want
        int nodes_size = nodes.size();
        int arcs_size = arcs.size();
        double loadFactor = ((double) arcs_size) / (((double) nodes_size) * ((double) nodes_size));

        if (loadFactor <= 0.1) {    //this creteria is given the non-zero cell is >= arcs_size(because in arcs, it's from_node-->to_nodes( # >=1)
            adjMatrix = new SparseDoubleMatrix2D(nodes_size, nodes_size);
            reachMatrix = new SparseDoubleMatrix2D(nodes_size, nodes_size);
        } else {
            adjMatrix = new DenseDoubleMatrix2D(nodes_size, nodes_size);
            reachMatrix = new DenseDoubleMatrix2D(nodes_size, nodes_size);
        }


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
                        if (reachMatrix.getQuick(i, j) != 1 && reachMatrix.getQuick(k, j) == 1) {
                            reachMatrix.setQuick(i, j, 1);
                        }
                    }
                }
            }
        }
    }

    protected void calculateInOuts() {
        int nodes_size = nodes.size();
        inputs = new ArrayList();
        outputs = new ArrayList();
        disconnectedNodes = new ArrayList();


        for (int i = 0; i < nodes_size; i++) {
            String input_or_output = _isInput_Output(i);
            Object n = nodes.get(i);
            if (input_or_output.equals(INPUT)) {
                inputs.add(n);
            } else if (input_or_output.equals(OUTPUT)) {
                outputs.add(n);
            } else if (input_or_output.equals(DISCONNECTED)) {
                disconnectedNodes.add(n);
            }
        }
    }

    protected String _isInput_Output(int nodeIndex) {
        int nodes_size = nodes.size();
        // intermediate if it has both incoming and outgoing arcs
        // result if it has incoming but no outgoing arcs
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

        if (!arcInExists && arcOutExists)
            return INPUT;
        else if (!arcInExists && !arcOutExists)
            return DISCONNECTED;
        else
            return OUTPUT;
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
     * @return true if node does not have any incoming or outgoing arcs
     */
    public boolean isDisconnectedNode(Object node) {
        enforceConsistency();
        return disconnectedNodes.contains(node);
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

    /**
     * for checking whether a pair of nodes are reachable in the mit.cadlab.dome3.search.datastructure.graph
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
     * for checking whether a pair of nodes are adjacent in the mit.cadlab.dome3.search.datastructure.graph
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



    protected class NodeSet extends DSet {

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
                System.err.println("DiGraph error -- invalid toNode: " + obj);
                return false;
            }
        }
    }


}
