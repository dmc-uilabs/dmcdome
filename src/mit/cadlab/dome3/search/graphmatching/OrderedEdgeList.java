package mit.cadlab.dome3.search.graphmatching;

import java.util.ArrayList;
import mit.cadlab.dome3.search.graphmatching.BipartiteGraph.Edge;

/**
 * Ordered decreasingly
 */
public class OrderedEdgeList extends ArrayList {
    public void insert(Edge new_edge) {
        if (this.contains(new_edge)) {
            System.out.println("OrderedEdgeList: Error: edge already exists in the list");
            return;
        }
        int pos = findPos(new_edge);
        add(pos, new_edge);

    }

    protected int findPos(Edge new_edge) {
        if (size() == 0) return 0;
        for (int i = 0; i < size(); i++) {
            if (new_edge.getWeight() > ((BipartiteGraph.Edge) get(i)).getWeight()) {
                return i;
            }
        }

        return size();//add to the end of the list
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            s = s + " " + get(i) + " ";
        }
        return s;
    }


}
