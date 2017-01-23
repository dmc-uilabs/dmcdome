package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.search.graphmatching.BipartiteGraph.Edge;

import java.util.Iterator;


/**
 *this algorithm is to find a maximal matching for bipartite mit.cadlab.dome3.search.datastructure.graph
 * reference pls see: www.csc.liv.ac.uk/~michele/ TEACHING/COMP309/2005/Lec8.2.4.pdf
 */
public class GreedyBipartiteMatching extends BipartiteMatching {
    boolean explain=false;
    public GreedyBipartiteMatching(BipartiteGraph bGraph) {
        super(bGraph);
    }

    public void matching() {
        if (bGraph.getEdgeList()==null || bGraph.getEdgeList().size() == 0) {
            if(explain) System.out.println("BipartiteMatching: no linking edges. zero nodes have been matched!");
            return;
        }

        DSet E = new DSet(bGraph.getEdgeList());
        while (E.size() > 0) {
            //Pick lexicographically first e in  E(G)
            Edge first_e = (Edge) E.get(0);
            // M=M U {e}
            if(M==null) M=new Matching();
            M.addMapping(first_e);
            //Remove e and all other edges in E(G) that are adjacent to e
            E = removeAdjecentEdges(E, first_e);
        }

        solved = true;

    }

    protected DSet removeAdjecentEdges(DSet E, Edge e) {
        DSet toremove = new DSet();
        for (Iterator i = E.iterator(); i.hasNext();) {
            Edge next_edge = (Edge) i.next();
            if (isAdjacentEdge(next_edge, e))
                toremove.add(next_edge);
        }

        E.removeAll(toremove);
        return E;
    }

    public boolean isAdjacentEdge(Edge e1, Edge e2) {
        if (e1.getLeftIndex() == e2.getLeftIndex()) return true;
        if (e1.getRightIndex() == e2.getRightIndex()) return true;
        return false;
    }


}
