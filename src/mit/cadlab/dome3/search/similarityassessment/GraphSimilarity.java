package mit.cadlab.dome3.search.similarityassessment;

import mit.cadlab.dome3.search.datastructure.graph.*;
import mit.cadlab.dome3.search.graphmatching.GMAlgorithm;
import mit.cadlab.dome3.util.DSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 *
 */
public class GraphSimilarity {

    public static int Algorithm_StrictAlignment = 0;    // if no aligned arc, make similarity score zero
    public static int Algorithm_looseAlignment = 1;     //simply sum up aligned nodes and aligned arcs
    protected FuzzyARG templateGraph;
    protected SimpleARG ifaceGraph;

    protected DoubleMatrix2D assignment_consistency_Matrix; //a matrix which row is the template node, column is the interface node


    public GraphSimilarity(FuzzyARG templateGraph, SimpleARG ifaceGraph) {
        this.templateGraph = templateGraph;
        this.ifaceGraph = ifaceGraph;
        NodewiseConsistency();
    }

    public void NodewiseConsistency() {
        int rows = templateGraph.getNodes().size();
        int cols = ifaceGraph.getNodes().size();

        assignment_consistency_Matrix = new DenseDoubleMatrix2D(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //should also consider the input set and the output set
                FuzzyAttributedNode tNode = (FuzzyAttributedNode) templateGraph.getNode(i);
                SimpleAttributedNode iNode = (SimpleAttributedNode) ifaceGraph.getNode(j);
                double nodesimilarity = NodeSimilarity.calculateNodeSimilarityUpdated(tNode, iNode);
               //  double nodesimilarity = NodeSimilarity.calculateNodeSimilarity(tNode, iNode);
                assignment_consistency_Matrix.setQuick(i, j, nodesimilarity);
            }
        }
    }

    public DoubleMatrix2D getAssignment_consistency_Matrix() {
        return assignment_consistency_Matrix;
    }

    /**
     * nodewise_mapping: key: nodeindex in the template mit.cadlab.dome3.search.datastructure.graph, value: node index of mapped node in the iface mit.cadlab.dome3.search.datastructure.graph
     * arcwise_mapping: key: template mit.cadlab.dome3.search.datastructure.graph arc; value: iface mit.cadlab.dome3.search.datastructure.graph arc
     */
    public double calculateGraphSimilarity(HashMap nodewise_mapping, HashMap arcwise_mapping, int algorithm) {

        double result_node = 0.0;

        if (nodewise_mapping == null || nodewise_mapping.size() == 0) {
            System.out.println("no aligned nodes nor arcs");
            return 0;
        }
        //for each align nodewise mapping
        for (Iterator entries = nodewise_mapping.entrySet().iterator(); entries.hasNext();) {
            Map.Entry entry = (Map.Entry) entries.next();

            AttributedNode template_node = (AttributedNode) entry.getKey();
            AttributedNode iface_node = (AttributedNode) entry.getValue();
            int rowIndex = templateGraph.getNodeIndex(template_node);
            int colIndex = ifaceGraph.getNodeIndex(iface_node);

            double node_sim = assignment_consistency_Matrix.getQuick(rowIndex, colIndex);

            result_node = result_node + template_node.getWeight() * node_sim;
        }
        System.out.println("aligned nodewise similarity=" + result_node);

        double result_arc = 0;
        if (arcwise_mapping == null || arcwise_mapping.size() == 0) {
            System.out.println("no aligned arcs");
            if (algorithm == Algorithm_StrictAlignment) {
                return 0;
            } else if(result_node<1.0) {
                return 0;
            }
            else{
                return result_node;
            }
       }

        //for each align arcwise mapping
        for (Iterator entries = arcwise_mapping.entrySet().iterator(); entries.hasNext();) {
            Map.Entry entry = (Map.Entry) entries.next();
            AttributedGraph.Arc template_arc = (AttributedGraph.Arc) entry.getKey();
            AttributedGraph.Arc iface_arc = (AttributedGraph.Arc) entry.getValue();

            double arc_sim = arcwise_similarity(template_arc, iface_arc);
            result_arc = result_arc + template_arc.getWeight() * arc_sim;
        }
        System.out.println("aligned arcwise similarity=" + result_arc);

        double result = result_node + result_arc;
        System.out.println("overall mit.cadlab.dome3.search.datastructure.graph similarity=" + result);

        return result;
    }

    /**
     * nodewise_mapping: key: nodeindex in the template mit.cadlab.dome3.search.datastructure.graph, value: node index of mapped node in the iface mit.cadlab.dome3.search.datastructure.graph
     * arcwise_mapping: key: template mit.cadlab.dome3.search.datastructure.graph arc; value: iface mit.cadlab.dome3.search.datastructure.graph arc
     */
    public String calculateGraphSimilarityToString(HashMap nodewise_mapping, HashMap arcwise_mapping,int algorithm) {

        double result_node = 0.0;
        String progress_report = "";

        if (nodewise_mapping == null || nodewise_mapping.size() == 0) {
            return progress_report + "no aligned nodes nor arcs";
        }
        //for each align nodewise mapping
        for (Iterator entries = nodewise_mapping.entrySet().iterator(); entries.hasNext();) {
            Map.Entry entry = (Map.Entry) entries.next();

            AttributedNode template_node = (AttributedNode) entry.getKey();
            AttributedNode iface_node = (AttributedNode) entry.getValue();
            int rowIndex = templateGraph.getNodeIndex(template_node);
            int colIndex = ifaceGraph.getNodeIndex(iface_node);

            double node_sim = assignment_consistency_Matrix.getQuick(rowIndex, colIndex);

            result_node = result_node + template_node.getWeight() * node_sim;
        }
        progress_report = progress_report + "aligned nodewise similarity=" + result_node;

        double result_arc = 0;
        if (arcwise_mapping == null || arcwise_mapping.size() == 0) {
            progress_report = progress_report + "\t no aligned arcs";
             if (algorithm == Algorithm_StrictAlignment) {
                 progress_report = progress_report + "\t overall mit.cadlab.dome3.search.datastructure.graph similarity=" + 0;
            } else {
                 progress_report = progress_report + "\t overall mit.cadlab.dome3.search.datastructure.graph similarity=" + result_node;
            }
           return progress_report;
        }

        //for each align arcwise mapping
        for (Iterator entries = arcwise_mapping.entrySet().iterator(); entries.hasNext();) {
            Map.Entry entry = (Map.Entry) entries.next();
            AttributedGraph.Arc template_arc = (AttributedGraph.Arc) entry.getKey();
            AttributedGraph.Arc iface_arc = (AttributedGraph.Arc) entry.getValue();

            double arc_sim = arcwise_similarity(template_arc, iface_arc);
            result_arc = result_arc + template_arc.getWeight() * arc_sim;
        }
        progress_report = progress_report + " \t aligned arcwise similarity=" + result_arc;

        double result = result_node + result_arc;
        progress_report = progress_report + "\noverall mit.cadlab.dome3.search.datastructure.graph similarity=" + result;

        return progress_report;
    }

    protected double arcwise_similarity(AttributedGraph.Arc template_arc, AttributedGraph.Arc iface_arc) {
        FuzzyAttributedNode template_from_node = (FuzzyAttributedNode) template_arc.getFromN();
        int template_from_index = templateGraph.getNodeIndex(template_from_node);
        FuzzyAttributedNode template__to_node = (FuzzyAttributedNode) template_arc.getToN();
        int template_to_index = templateGraph.getNodeIndex(template__to_node);

        SimpleAttributedNode iface_from_node = (SimpleAttributedNode) iface_arc.getFromN();
        int iface_from_index = ifaceGraph.getNodeIndex(iface_from_node);
        SimpleAttributedNode iface_to_node = (SimpleAttributedNode) iface_arc.getToN();
        int iface_to_index = ifaceGraph.getNodeIndex(iface_to_node);

        double from_nodepair_sim = assignment_consistency_Matrix.getQuick(template_from_index, iface_from_index);
        double to_nodepair_sim = assignment_consistency_Matrix.getQuick(template_to_index, iface_to_index);
        return Math.min(from_nodepair_sim, to_nodepair_sim);
    }

}
