package mit.cadlab.dome3.search.datastructure.graph;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Nov 28, 2005
 * Time: 6:04:44 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class SimpleAttributedNode extends AttributedNode {

       //Dimension value now stored as Double and not Integer
       protected String name;
       protected String datatype;
       protected String unit;
       protected Double dim;
       protected String input_output;



       public SimpleAttributedNode(String name, String datatype, String unit, String inout,Double dim) {
           this.name = name;
           this.datatype = datatype;
           this.unit = unit;
           this.dim = dim;
           this.input_output=inout;
        }

       public String getName() {
           return name;
       }

       public void setName(String name) {
           this.name = name;
       }

       public String getDatatype() {
           return datatype;
       }

       public void setDatatype(String datatype) {
           this.datatype = datatype;
       }

       public String getUnit() {
           return unit;
       }

       public void setUnit(String unit) {
           this.unit = unit;
       }

       public Double getDim() {
           return dim;
       }

       public void setDim(Double dim) {
           this.dim = dim;
       }

    public String getInput_output() {
        return input_output;
    }

    public void setInput_output(String input_output) {
        this.input_output = input_output;
    }

    public String toString(){
        return name;
    }

    public String printDetail(){
        return "Node "+name+"("+dim+","+unit+","+datatype+","+input_output+")"+",weight= "+weight;
    }
}
