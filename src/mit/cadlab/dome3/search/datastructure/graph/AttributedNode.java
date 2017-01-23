package mit.cadlab.dome3.search.datastructure.graph;

/**
 * Node's attributes : name, datatype, unit, dimension, inout/output
 * Node also contains weight
 */
public abstract class AttributedNode {
    //5 Attributes
    public static final String NAME = "name";
    public static final String DATATYPE = "datatype";
    public static final String UNIT = "unit";
    public static final String DIM = "dimension";
    public static final String INOUT = "input_output";

    protected int weight=1;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public abstract String toString();

}
