package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.SimpleARG;
import mit.cadlab.dome3.search.datastructure.graph.AttributedNode;
import mit.cadlab.dome3.search.datastructure.graph.AttributedGraph;
import mit.cadlab.dome3.search.datastructure.graph.AttributedGraph.Arc;
import mit.cadlab.dome3.search.similarityassessment.GraphSimilarity;
import mit.cadlab.dome3.util.DSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix2D;

/**
 *   Algorithm:
 *   Input: an instance mit.cadlab.dome3.search.datastructure.graph (attributed mit.cadlab.dome3.search.datastructure.graph), a template mit.cadlab.dome3.search.datastructure.graph (random mit.cadlab.dome3.search.datastructure.graph)
 *
 *   1. Estimate node pair-wise similarity
 *   2. Create a bipartite mit.cadlab.dome3.search.datastructure.graph from compatible node pairs
 *
 *   3
 *    call maximum bipartite matching embeded in discrete relaxation algorithm
 *    Use maximum bipartite matching to align nodes and consider arc alignment,
 *    if objective function is optimal,
 *
 *   4. Estimate mit.cadlab.dome3.search.datastructure.graph similarity

 *   Output: a mapping and similarity score
 */
public class GMAlgorithm {
    protected FuzzyARG rGraph;
    protected AttributedGraph iGraph;  //Ligon Changed to Attributed Graph to handle BOTH Fuzzy or Simple Graphs
    protected HashMap nodewise_alignment; //key: node from template mit.cadlab.dome3.search.datastructure.graph, value node from interface mit.cadlab.dome3.search.datastructure.graph
    protected HashMap arcwise_alignment; //key: arc from template mit.cadlab.dome3.search.datastructure.graph, value: arc from interface mit.cadlab.dome3.search.datastructure.graph
    public boolean solved = false;
    protected DoubleMatrix2D assignment_consistency_Matrix;
    protected GraphSimilarity graphSim;

    public GMAlgorithm(FuzzyARG rGraph, SimpleARG iGraph) {
        this.rGraph = rGraph;
        this.iGraph = iGraph;
        graphSim = new GraphSimilarity(rGraph, iGraph);
    }

    //Added by Ligon for AutoMapping Classification implementation of mit.cadlab.dome3.search.datastructure.graph matching
    public GMAlgorithm(FuzzyARG iGraph,FuzzyARG rGraph)
    {
        this.rGraph = rGraph;
        this.iGraph = iGraph;
    }

    /**
     * this is the original matching function, reverted if using original method
     * @return
     */
    public String matching() {
        String report = "";
        //1.Estimate node pair-wise similarity

        assignment_consistency_Matrix = graphSim.getAssignment_consistency_Matrix();
        //2. Create a bipartite mit.cadlab.dome3.search.datastructure.graph from compatible node pairs
        BipartiteGraph bGraph = new BipartiteGraph(rGraph.getNodes(), iGraph.getNodes(), assignment_consistency_Matrix);

        //3.pick one matching:
        GreedyBipartiteMatching algorithm = new GreedyBipartiteMatching(bGraph);
        // HungarianBipartiteMatching algorithm2=new HungarianBipartiteMatching(bGraph);

        nodewise_alignment = algorithm.getMatchingInNodeAlignment();

        if (nodewise_alignment != null) {
            report = report + "nodewise alignment---" + nodewise_alignment.size()+" node pairs aligned:\n\t"+nodewise_alignment;
            // consider arc alignment,
            DSet templateArc = rGraph.getArcs(algorithm.getCorrespondingNodes(rGraph.getNodes(), algorithm.getMatching().getLeftIndices()));
            DSet ifaceArc = iGraph.getArcs(algorithm.getCorrespondingNodes(iGraph.getNodes(), algorithm.getMatching().getRightIndices()));

            arcwise_alignment = findAlignment(templateArc, ifaceArc);
            if (arcwise_alignment != null) {
                report = report + "\n arcwise alignment---" + arcwise_alignment.size()+ " arc pairs aligned:\n\t"+arcwise_alignment;
            }
        }
        return report;
    }


    private HashMap findAlignment(DSet templateArc, DSet ifaceArc) {
        if (templateArc == null || templateArc.size() == 0 ||
                ifaceArc == null || ifaceArc.size() == 0)
            return null;
        HashMap result = new HashMap();
        for (Iterator it = templateArc.iterator(); it.hasNext();) {
            Arc template_arc = (Arc) it.next();
            if (nodewise_alignment.get(template_arc.getFromN()) != null && nodewise_alignment.get(template_arc.getToN()) != null) {
                AttributedNode iface_fromN = (AttributedNode) nodewise_alignment.get(template_arc.getFromN());
                AttributedNode iface_toN = (AttributedNode) nodewise_alignment.get(template_arc.getToN());
                Arc iface_arc = iGraph.getArc(iface_fromN, iface_toN);
                if(iface_arc!=null)
                result.put(template_arc, iface_arc);
            }
        }
        if (result.size() != 0) return result;
        return null;
    }

    public double getSimilarityScore(int similarity_algorithm_choice) {
        if (!solved) matching();
        double graph_sim = graphSim.calculateGraphSimilarity(nodewise_alignment, arcwise_alignment,similarity_algorithm_choice);
        return graph_sim;
    }

     public String generateReport(int similarity_algorithm_choice) {
        if (!solved) matching();
        return graphSim.calculateGraphSimilarityToString(nodewise_alignment, arcwise_alignment,similarity_algorithm_choice);
   }

    public HashMap getNodewise_alignment() {
        if (!solved) matching();
        return nodewise_alignment;
    }

    public HashMap getArcwise_alignment() {
        if (!solved) matching();
        return arcwise_alignment;
    }

    //Added by Ligon in order to switch matrices for mit.cadlab.dome3.search.datastructure.graph score process
    public void setConsistencyMatrix(DoubleMatrix2D consistencyMatrix)
    {
        this.assignment_consistency_Matrix = consistencyMatrix;
    }

}
