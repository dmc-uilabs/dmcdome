package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.util.DSet;

import java.util.HashMap;
import mit.cadlab.dome3.search.graphmatching.BipartiteGraph.Edge;

/**
 * This class acts as a container to hold matching(temporirary matching) result..
 */
public class Matching {
    public DSet L;   //a set of indices, not the real node in bipartite mit.cadlab.dome3.search.datastructure.graph
    public DSet R;    //a set of indices, not the real node in bipartite mit.cadlab.dome3.search.datastructure.graph
    public HashMap mapping; //key: left index, value: right index

    public DSet M;//set of edges

    public Matching() {
        L=new DSet();
        R=new DSet();
        mapping=new HashMap();
        M=new DSet();
    }

    public void addMapping(Edge e){
       L.add(new Integer(e.getLeftIndex()));
       R.add(new Integer(e.getRightIndex()));
       mapping.put(new Integer(e.getLeftIndex()),new Integer(e.getRightIndex()));
       M.add(e);
    }

    public DSet getLeftIndices() {
        return L;
    }

    public DSet getRightIndices() {
        return R;
    }
    public boolean containsLeftIndex(int left_index){
        return (L.contains(new Integer(left_index)));
    }

     public boolean containsRightIndex(int right_index){
        return (R.contains(new Integer(right_index)));
    }
    public HashMap getMapping() {
        return mapping;
    }

    public DSet getEdges() {
        return M;
    }

    public String toString(){
      return mapping.toString();
    }

    public int cardinality(){
        return mapping.size();
    }

    public void removeMapping(Edge e) {
        if(!(M.contains(e)))return;
        L.remove(new Integer(e.getLeftIndex()));
        R.remove(new Integer(e.getRightIndex()));
        mapping.remove(new Integer(e.getLeftIndex()));
        M.remove(e);

    }
}
