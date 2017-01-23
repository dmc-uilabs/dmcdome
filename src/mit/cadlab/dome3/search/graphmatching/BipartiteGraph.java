package mit.cadlab.dome3.search.graphmatching;

import cern.colt.matrix.DoubleMatrix2D;
import mit.cadlab.dome3.util.DSet;

import java.util.Iterator;

/**
 * a bipartite mit.cadlab.dome3.search.datastructure.graph  is a special mit.cadlab.dome3.search.datastructure.graph that vertices can be partitioned
 * into two sets, where edges only goes one set to the other
 *
 * bipartite matching is to find a cresponding one to one mapping between the left side and the right side
 *
 */
public class BipartiteGraph {
    //vertices
    protected DSet LeftList; //template mit.cadlab.dome3.search.datastructure.graph nodes
    protected DSet RightList; // interface mit.cadlab.dome3.search.datastructure.graph nodes
    //edges
    protected OrderedEdgeList EdgeList; //link designate possible matching pairs

    public BipartiteGraph() {
        LeftList = new DSet();
        RightList = new DSet();
    }

    public BipartiteGraph(DSet leftList, DSet rightList) {
        LeftList = leftList;
        RightList = rightList;
    }

    public BipartiteGraph(DSet leftList, DSet rightList, DoubleMatrix2D edge_configuration) {
        LeftList = leftList;
        RightList = rightList;
        if (LeftList.size() != edge_configuration.rows() || RightList.size() != edge_configuration.columns())
            new Exception("BipartiteGraph: Error in creating bipartite mit.cadlab.dome3.search.datastructure.graph, inconsistent configuration row/col number");
        addEdge(edge_configuration);
    }

    public void addVertex(Object v, boolean to_left) {
        if (to_left) {
            if (!LeftList.contains(v)) LeftList.add(v);
        } else {
            if (!RightList.contains(v)) RightList.add(v);
        }
    }

    private void addEdge(DoubleMatrix2D edge_configuration) {
        //check if edge_configuration is valid sized
        int left_index = edge_configuration.rows();
        int right_index = edge_configuration.columns();
        if (left_index > LeftList.size()) {
            new Exception("BipartiteGraph:Error in adding edge: invalid index ");
        }
        if (right_index > RightList.size()) {
            new Exception("BipartiteGraph:Error in adding edge: invalid index ");
        }

        for (int i = 0; i < edge_configuration.rows(); i++) {
            for (int j = 0; j < edge_configuration.columns(); j++) {
                double sim = edge_configuration.getQuick(i, j);
                if (sim != 0) {
                    Edge e = createEdge(i, j, sim);
                    if (EdgeList == null) EdgeList = new OrderedEdgeList();
                    EdgeList.insert(e);
                }
            }
        }
    }

    public void addEdge(Edge e) {
        //check if e is a valid index
        int left_index = e.getLeftIndex();
        int right_index = e.getRightIndex();
        if (left_index < 0 || left_index >= LeftList.size()) {
            new Exception("BipartiteGraph:Error in adding edge: invalid index ");
        }
        if (right_index < 0 || right_index >= RightList.size()) {
            new Exception("BipartiteGraph:Error in adding edge: invalid index ");
        }
        if (EdgeList == null)
            EdgeList = new OrderedEdgeList();
        if (!EdgeList.contains(e))
            EdgeList.insert(e);

    }

    public DSet adjcentVertices(int node_index, boolean is_in_Left_set) {
        DSet adjVertices = new DSet();
        for (Iterator itor = getEdgeList().iterator(); itor.hasNext();) {
            Edge e = (Edge) itor.next();
            if (is_in_Left_set) {
                if (e.getLeftIndex() == node_index)
                    adjVertices.add(RightList.get(e.getRightIndex()));
            } else {
                if (e.getRightIndex() == node_index)
                    adjVertices.add(LeftList.get(e.getLeftIndex()));
            }
        }
        if (adjVertices.size() == 0) return null;//not found
        return adjVertices;
    }

    public DSet getEdgesByIncidentVertex(int left_index) {
        return getEdgesByVertex(left_index, true);
    }

    public DSet getEdgesByTerminusVertex(int right_index) {
        return getEdgesByVertex(right_index, false);
    }

    public DSet getEdgesByVertex(int index, boolean is_In_L) {
        DSet edges = new DSet();
        for (Iterator itor = getEdgeList().iterator(); itor.hasNext();) {
            Edge e = (Edge) itor.next();
            if (is_In_L) {
                if (e.getLeftIndex() == index)
                    edges.add(e);
            } else {
                if (e.getRightIndex() == index)
                    edges.add(e);
            }
        }
        if (edges.size() == 0) return null;//not found
        return edges;
    }

    public Edge createEdge(int leftIndex, int rightIndex, double weight) {
        return new Edge(leftIndex, rightIndex, weight);
    }

    public OrderedEdgeList getEdgeList() {
        return EdgeList;
    }

    public DSet getLeftList() {
        return LeftList;
    }

    public DSet getRightList() {
        return RightList;
    }

    public int cardinality() {
        if (LeftList.size() <= RightList.size())
            return LeftList.size();
        else
            return RightList.size();
    }

    public Edge getEdge(Object l, Object r) {
        int leftIndex = LeftList.indexOf(l);
        int rightIndex = RightList.indexOf(r);
        if (leftIndex == -1 || rightIndex == -1) return null;
        for (Iterator itor = getEdgeList().iterator(); itor.hasNext();) {
            Edge e = (Edge) itor.next();
            if (e.getLeftIndex() == leftIndex && e.getRightIndex() == rightIndex)
                return e;
        }
        return null;
    }

    public DSet getEdgesForPath(Path path) {
        DSet vertices = path.vertices_sequence;
        DSet edges=new DSet();
        for(int i=0;i<vertices.size();i++){
            Object v=vertices.get(i);
            if(LeftList.contains(v)){
               Object rightV=path.next(v);
               if(rightV!=null){
                   Edge toadd=getEdge(v, rightV);
                   if(toadd!=null) edges.add(toadd);
               }
            }
            else if(RightList.contains(v)){
               Object leftV=path.next(v);
                 if(leftV!=null){
                   Edge toadd=getEdge(leftV,v);
                   if(toadd!=null) edges.add(toadd);
             }
            }
        }
        if(edges.size()==0) return null;
        return edges;
    }

    protected class Edge {
        protected int leftIndex;
        protected int rightIndex;
        protected double weight;

        public Edge(int leftIndex, int rightIndex, double weight) {
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
            this.weight = weight;
        }

        public int getLeftIndex() {
            return leftIndex;
        }

        public int getRightIndex() {
            return rightIndex;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public String toString() {
            return "(" + leftIndex + "," + rightIndex + "," + weight + ")";
        }
    }
}
