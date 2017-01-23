package mit.cadlab.dome3.search.datastructure.graph;

import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.UnitAnalyzer;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Nov 28, 2005
 * Time: 6:15:49 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class SimpleARG extends AttributedGraph {

     public AttributedNode addNode(String name, String datatype, String unit, Double dim,String inputoutput, int weight) {
        AttributedNode n=new SimpleAttributedNode(name,datatype,unit,inputoutput,dim);
        n.setWeight(weight);
        if(super.addNode(n))  return n;
        return null;
    }

    public AttributedNode addNode(String name, String datatype, String unit, String inputoutput,Double dim) {
        AttributedNode n=new SimpleAttributedNode(name,datatype,unit,inputoutput,dim);
        n.setWeight(1);
        if(super.addNode(n))  return n;
        return null;
    }

      public AttributedNode addNode(String name, String datatype, String unit, String inputoutput) {
        Double dim=new Double(UnitAnalyzer.getDimensionNumber(unit).doubleValue());
        AttributedNode n=new SimpleAttributedNode(name,datatype,unit,inputoutput,dim);
        n.setWeight(1);
        if(super.addNode(n))  return n;
        return null;
    }

}
