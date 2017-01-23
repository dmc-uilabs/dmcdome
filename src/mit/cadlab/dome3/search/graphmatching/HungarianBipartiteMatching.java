package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.search.graphmatching.BipartiteGraph.Edge;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * this is the original Hungarian method for finding maximum matching for the bipartite mit.cadlab.dome3.search.datastructure.graph
 * reference pls see:  H. W. Kuhn, The Hungarian method for the assignment problem, Naval Res. Logist. Quart. 2:83-97, 1955.
 * Input: A bipartite Graph G,
 * Output: a new maximal matching M

 1.	M==0
 2.	Repeat
 3.	If |M|=|X|, then STOP, already maximum cardinality
 4.	Find a shortest augmenting path p=Find Augment Path, if nothing more augmenting path found, then STOP.
 5.	Form a new maximal matching by combining the current matching M and the augmenting path found: exclusive OR
 Go to step 3.

 */
public class HungarianBipartiteMatching extends BipartiteMatching {
    public HungarianBipartiteMatching(BipartiteGraph bGraph) {
        super(bGraph);
    }

    public void matching() {
        if (bGraph.getEdgeList().size() == 0) {
            System.out.println("BipartiteMatching: no linking edges. zero nodes have been matched!");
            return;
        }

        //initial matching
        GreedyBipartiteMatching initialMatchingalgorithm = new GreedyBipartiteMatching(bGraph);
        M = initialMatchingalgorithm.getMatching();

        HungarianMatching(M);
        solved = true;
    }

    private void HungarianMatching(Matching M) {
        while (true) {
            if (M.cardinality() == bGraph.cardinality()) break;

            DSet shortestAugmentingPath = find_shortest_augmenting_path(M);
            if (shortestAugmentingPath == null)
                break;
            else {
                Path augmenting_path = OptimalPath(shortestAugmentingPath);
                Exclusive_OR(M, augmenting_path);
            }
        }
    }

    private void Exclusive_OR(Matching m, Path augmenting_path) {
        DSet matchedEdges = m.getEdges();
        DSet edges_in_augmenting_path = bGraph.getEdgesForPath(augmenting_path);

        DSet edges_in_common = intersection(matchedEdges, edges_in_augmenting_path);

        if (edges_in_common.size() > 0) {
            for (Iterator it = edges_in_common.iterator(); it.hasNext();) {
                Edge e = (Edge) it.next();
                //remove this edge in m
                m.removeMapping(e);
            }
        }

        //add new mapping
        edges_in_augmenting_path.removeAll(edges_in_common);
        for (Iterator it = edges_in_augmenting_path.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            //remove this edge in m
            m.addMapping(e);
        }

    }

    /**
     * For weighted bipartite mit.cadlab.dome3.search.datastructure.graph
     * this function is going to apply criteria to pick from a set of possible augmenting paths
     *
     * @param pathes: a set of augmenting pathes.
     * @return
     */
    protected Path OptimalPath(DSet pathes) {
        return (Path) pathes.get(0);
    }

}
