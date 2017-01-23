package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.search.graphmatching.BipartiteGraph.Edge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * abstract class for bipartite matching,
 * direct subclass: greedy_maximal_bipartite_matching
 *
 */
public abstract class BipartiteMatching {
    protected BipartiteGraph bGraph;//bipartite mit.cadlab.dome3.search.datastructure.graph to be matched
    protected boolean solved = false;
    protected Matching M;


    public BipartiteMatching(BipartiteGraph bGraph) {
        this.bGraph = bGraph;
    }

    public Matching getMatching() {
        if (!solved) matching();
        return M;
    }

    public HashMap getMatchingInNodeAlignment() {
        if (!solved) matching();
        return matching_to_node_alignment(M);
    }

    protected HashMap matching_to_node_alignment(Matching m){
        if(m==null) return null;
        HashMap result = new HashMap();

       for (Iterator i = m.getMapping().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            result.put(bGraph.getLeftList().get(((Integer) entry.getKey()).intValue()), bGraph.getRightList().get(((Integer) entry.getValue()).intValue()));

        }
        return result;
    }

    public abstract void matching();

    /**
     *  an M_augmenting path is a path that starts and ends with free vertices
     *  and the edge of the path alternates between edge in M and edge not in M
     * @return DSet: a set of shortest augmenting path
     */
    public DSet find_shortest_augmenting_path(Matching currentM) {
        //check if we already have:

        //nodes in M
        DSet L = bGraph.getLeftList();
        DSet L_in_M_indices = currentM.getLeftIndices();
        DSet L_in_M = getCorrespondingNodes(L, L_in_M_indices);

        DSet R = bGraph.getRightList();
        DSet R_in_M_indices = currentM.getRightIndices();
        DSet R_in_M = getCorrespondingNodes(R, R_in_M_indices);

        //nodes not in M
        DSet L_not_in_M = new DSet(L);
        L_not_in_M.removeAll(L_in_M);

        DSet R_not_in_M = new DSet(R);
        R_not_in_M.removeAll(R_in_M);

        HashMap result = new HashMap();
        //Now starting from a node in L_not_in_M
        if (L_not_in_M.size() == 0 || R_not_in_M.size() == 0) return null;//no augmenting path can be found

        for (int i = 0; i < L_not_in_M.size(); i++) {
            Object left_obj = L_not_in_M.get(i);
            int left_obj_index = L.indexOf(left_obj);
            for (int j = 0; j < R_not_in_M.size(); j++) {
                Object right_obj = R_not_in_M.get(j);
                int right_obj_index = R.indexOf(right_obj);
                DSet p = findAugmentingPath(currentM, left_obj_index, right_obj_index);
                if (p != null) {
                    for(Iterator it=p.iterator();it.hasNext();){
                        Path nextpath=(Path) it.next();
                        if(result.get(nextpath.length())==null) result.put(nextpath.length(),new DSet());
                        ((DSet)result.get(nextpath.length())).add(nextpath);
                    }
                }
            }
        }
        if (result.size() == 0) return null;
        //find shortest/optimal augmenting path
        int shortest = -1;
        for (Iterator i = result.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry=(Map.Entry ) i.next();
            int length=((Integer)entry.getKey()).intValue();

            if (shortest == -1)
            {
                shortest = length;
            }
            else {
                if (shortest > length)
                    shortest = length;
            }
        }

        if (result.size() == 0) return null;

        if (shortest != -1) return (DSet) result.get(new Integer(shortest));  //return set of shortest augmenting path
        return null;

    }



    public DSet Pathes = new DSet();

    /**
     * Algorithm:
     * Start DFS at a unmatched vertex in L
     * if current vertex is in L
     *    follow an edge not in M
     * else
     *    follow an edge in M
     * STOP if we find an unmatched vertex in R
     *
     * @param currentM
     * @param left_obj_index
     * @param right_obj_index
     * @return
     */
    public DSet findAugmentingPath(Matching currentM, int left_obj_index, int right_obj_index) {

        //edges in M
        DSet M = currentM.getEdges();

        // edges not in M

        DSet E_M = new DSet(bGraph.getEdgeList());  //copy all the edges in E first

        E_M.removeAll(M);

        //clean Pathes
        Pathes = new DSet();
        //find all augmenting path from left_obj_index
        Object leftV = bGraph.getLeftList().get(left_obj_index);
        Object rightV = bGraph.getRightList().get(right_obj_index);

        DFS(M, E_M, leftV, rightV, true, new Path());
        if (Pathes.size() == 0) return null;

        DSet result = new DSet();
        for (Iterator itor = Pathes.iterator(); itor.hasNext();) {
            Path p = (Path) itor.next();
            result.add(p);
        }
        return result;

    }


    private boolean DFS(DSet edge_in_M, DSet edge_not_in_M, Object currentV, Object targetV, boolean isInL, Path path) {

        path.addVertex(currentV);
        //System.out.println("current path:" + path);
        if (isInL)//left nodes
        {
            int currentVectex = bGraph.getLeftList().indexOf(currentV);
            DSet adjcent_nodes = bGraph.adjcentVertices(currentVectex, isInL);
            DSet adjcent_edges = bGraph.getEdgesByVertex(currentVectex, isInL);


            if (adjcent_nodes == null) {

                return false;
            }

            if (adjcent_nodes.contains(targetV)) {
                path.addVertex(targetV);
                return true;
            }

            adjcent_nodes.removeAll(path.vertices_sequence);  //also adjcent_nodes shouldn't be in the path

//follow an edge not in M
            DSet adjcent_edge_not_in_M = intersection(adjcent_edges, edge_not_in_M);
            if (adjcent_edge_not_in_M == null || adjcent_edge_not_in_M.size() == 0) {
//reach the end, keep a snapshot of current path

                return false;
            }
            for (int i = 0; i < adjcent_edge_not_in_M.size(); i++) {

                Edge firstEdge = (Edge) ((DSet) adjcent_edge_not_in_M).get(i);
                if (DFS(edge_in_M, edge_not_in_M, bGraph.getRightList().get(firstEdge.getRightIndex()), targetV, !isInL, path)) {
                    Pathes.add(path.clone());
                   // System.out.println("added to Pathes:" + path);
                    //move back to original data structure
                    path.remove_last_added();
                }
                else{
                  //move back to original data structure
                  path.remove_last_added();
                }

            }
        } else//right nodes
        {
            int currentVectex = bGraph.getRightList().indexOf(currentV);

            DSet adjcent_nodes = bGraph.adjcentVertices(currentVectex, isInL);       //a set of vertices
            DSet adjcent_edges = bGraph.getEdgesByVertex(currentVectex, isInL);
            adjcent_nodes.removeAll(path.vertices_sequence);  //also adjcent_nodes shouldn't be in the path


            if (adjcent_nodes == null) {
               return false;
            }
            if (adjcent_nodes.contains(targetV)) {
                path.addVertex(targetV);
                return true;
            }
//follow an edge in M
            Collection adjcent_edges_in_M = DSet.intersection(adjcent_edges, edge_in_M);
            if (adjcent_edges_in_M == null || adjcent_edges_in_M.size() == 0) {

                return false;
            }
            for (int i = 0; i < adjcent_edges_in_M.size(); i++) {

                Edge firstEdge = (Edge) ((DSet) adjcent_edges_in_M).get(0);
                if (DFS(edge_in_M, edge_not_in_M, bGraph.getLeftList().get(firstEdge.getLeftIndex()), targetV, !isInL, path)) {
                    Pathes.add(path.clone());
                    //System.out.println("added to Pathes:" + path);
                }
                //move back to original data structure
                path.remove_last_added();

            }
        }
        //move back to original data structure
        path.remove_last_added();
        return false;
    }

    public DSet getCorrespondingNodes(DSet nodes, DSet indices) {
        DSet result = new DSet();
        for (int i = 0; i < indices.size(); i++) {
            Integer index = (Integer) indices.get(i);
            if (index.intValue() <= nodes.size()) {
                result.add(nodes.get(index.intValue()));
            }
        }
        return result;
    }

    public DSet intersection(Collection items1, Collection items2) {
        DSet result = new DSet();
        if (items1.size() > items2.size()) { // force items1 to be smaller set
            Collection temp = items1;
            items1 = items2;
            items2 = temp;
        }
        Iterator it = items1.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (items2.contains(obj))
                result.add(obj);
        }
        return result;
    }

}
