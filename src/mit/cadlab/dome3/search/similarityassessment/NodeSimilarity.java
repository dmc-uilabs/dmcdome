package mit.cadlab.dome3.search.similarityassessment;

import mit.cadlab.dome3.search.datastructure.graph.SimpleAttributedNode;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;
import mit.cadlab.dome3.search.datastructure.FuzzySet;
import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.SimilarityMeasures;
import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.UnitAnalyzer;

import java.util.List;
import java.util.Set;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Dec 12, 2005
 * Time: 9:31:33 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class NodeSimilarity {

    //Ligon Added weight_dim
    private static double weight_name = 1.0 / 4.0;
    private static double weight_datatype = 1.0 / 4.0;
    private static double weight_unit = 1.0 / 4.0;
    private static double weight_dim = 1.0 / 4.0;

    public static double calculateNodeSimilarity(FuzzyAttributedNode templateNode, SimpleAttributedNode ifaceNode) {
        boolean explain=false;
        if(explain) System.out.println("comparing templateNode " + templateNode + " and iface node " + ifaceNode);
        double sim_input = CrispSetSimilarity(templateNode.getInputoutput(), ifaceNode.getInput_output());
        if(explain) System.out.println("in/out=" + sim_input);
        double sim_dim = CrispSetSimilarity(templateNode.getDim(), ifaceNode.getDim());
        if(explain) System.out.println("template dim=" + templateNode.getDim() + " iface dim=" + ifaceNode.getDim() + " sim_dim=" + sim_dim);
        if (sim_input == 0 || sim_dim == 0) return 0;

        double sim_name = calculateNameSimilarity(templateNode.getName(), ifaceNode.getName());
        if(explain)  System.out.println("name=" + sim_name);
        double sim_datatype = calculateDatatypeSimilarity(templateNode.getDatatype(), ifaceNode.getDatatype());
        if(explain)  System.out.println("data type=" + sim_datatype);
        double sim_unit = calculateUnitSimilarity(templateNode.getUnit(), ifaceNode.getUnit());
        if(explain) System.out.println("unit=" + sim_unit);


        double sim_node = weight_name * sim_name + weight_datatype * sim_datatype + weight_unit * sim_unit;
        if(explain&&sim_node!=0) System.out.println("comparing templateNode " + templateNode + " and iface node " + ifaceNode+"="+sim_node);
        return sim_node;
    }

    /**
     * Qing May 31,06: the change is to solve an issue for the performance is sacrifised when identical parameters are actually
     * in different input/output groups.
     * don't check input/output at this point
     * @param templateNode
     * @param ifaceNode
     * @return
     */
    public static double calculateNodeSimilarityUpdated(FuzzyAttributedNode templateNode, SimpleAttributedNode ifaceNode) {
           boolean explain=false;
           if(explain) System.out.println("comparing templateNode " + templateNode + " and iface node " + ifaceNode);
           double sim_input = CrispSetSimilarity(templateNode.getInputoutput(), ifaceNode.getInput_output());
           if(explain) System.out.println("in/out=" + sim_input);
           double sim_dim = CrispSetSimilarity(templateNode.getDim(), ifaceNode.getDim());
           if(explain) System.out.println("template dim=" + templateNode.getDim() + " iface dim=" + ifaceNode.getDim() + " sim_dim=" + sim_dim);
           if (sim_dim == 0) return 0;

           double sim_name = calculateNameSimilarity(templateNode.getName(), ifaceNode.getName());
           if(explain)  System.out.println("name=" + sim_name);
           double sim_datatype = calculateDatatypeSimilarity(templateNode.getDatatype(), ifaceNode.getDatatype());
           if(explain)  System.out.println("data type=" + sim_datatype);
           double sim_unit = calculateUnitSimilarity(templateNode.getUnit(), ifaceNode.getUnit());
           if(explain) System.out.println("unit= " + sim_unit);
           double sim_node = weight_dim*sim_dim+weight_name * sim_name + weight_datatype * sim_datatype + weight_unit * sim_unit;
           if(explain&&sim_node!=0) System.out.println("comparing templateNode " + templateNode + " and iface node " + ifaceNode+"="+sim_node);
           return sim_node;
       }

    public static double CrispSetSimilarity(FuzzySet randomV, Object v) {

        double result = 0;

        //for each member in the random variable set
        Set members = randomV.getSampleSet();
        for (Iterator i = members.iterator(); i.hasNext();) {

            Object member = i.next();
            //compare simialrity
            double s = 0.0;
            if (member != null &&v!=null && member.toString().equals(v.toString())) {
                s = 1.0;
            }
            if (member==null && v==null)//weighted sum up using this member's frequency information
            {
                s = 1.0;
            }
            result = result + s * randomV.getProbabilty(member);
        }

        return result;
    }


    public static double calculateNameSimilarity(FuzzySet template_node_name, String iface_node_name) {
        double result = 0;

        //for each member in the random variable set
        Set members = template_node_name.getSampleSet();
        for (Iterator i = members.iterator(); i.hasNext();) {

            String member = (String) i.next();
            //compare simialrity
            double s = SimilarityMeasures.StringMatching_TFIDF_matching(member, iface_node_name);
            //weighted sum up using this member's frequency information
            result = result + s * template_node_name.getProbabilty(member);
        }

        return result;
    }

    public static double calculateDatatypeSimilarity(FuzzySet template_node_datatype, String iface_node_datatype) {
        double result = 0;

        //for each member in the random variable set
        Set members = template_node_datatype.getSampleSet();
        for (Iterator i = members.iterator(); i.hasNext();) {

            String member = (String) i.next();
            //compare simialrity
            double s = SimilarityMeasures.datatype_matching(member, iface_node_datatype);
            //weighted sum up using this member's frequency information
            result = result + s * template_node_datatype.getProbabilty(member);
        }

        return result;
    }

    public static double calculateUnitSimilarity(FuzzySet template_node_unit, String iface_node_unit) {
        double result = 0;

        //for each member in the random variable set
        Set members = template_node_unit.getSampleSet();
        for (Iterator i = members.iterator(); i.hasNext();) {

            String member = (String) i.next();
            //compare simialrity
            double s = SimilarityMeasures.unitMatching(member, iface_node_unit);
            //weighted sum up using this member's frequency information
            result = result + s * template_node_unit.getProbabilty(member);
        }

        return result;
    }

    public static boolean isDimensionCompatible(FuzzySet template_node_unit, String iface_node_unit) {
        boolean result = true;

        //for each member in the random variable set
        Set members = template_node_unit.getSampleSet();
        for (Iterator i = members.iterator(); i.hasNext();) {

            String member = (String) i.next();
            //compare simialrity
            boolean m = SimilarityMeasures.DimensionMatching(member, iface_node_unit);
            //Qing: at this version, all members should be the same.
            result = result & m;
        }

        return result;
    }

}
