package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.search.datastructure.graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Jan 24, 2006
 * Time: 12:28:35 AM
 * To change this template use Options | File Templates.
 */

public class RefinedGMAlgorithm extends GMAlgorithm {
    protected HashMap indirect_nodewise_alignment; //key: dummy arc from template mit.cadlab.dome3.search.datastructure.graph; value: dummy arc from interface mit.cadlab.dome3.search.datastructure.graph; used for mapping indirect links
    public ArrayList nodeAlignmentArray = new ArrayList();  //Added by Ligon to retain nodewise pairing for subsequent automapping.

    public RefinedGMAlgorithm(FuzzyARG rGraph, SimpleARG iGraph) {
        super(rGraph, iGraph);
    }

    //Added by Ligon for AutoMapping classification implementation of mit.cadlab.dome3.search.datastructure.graph matching
    public RefinedGMAlgorithm(FuzzyARG rGraph,FuzzyARG iGraph,DoubleMatrix2D consistencyMatrix)
    {
        super(iGraph,rGraph);
        assignment_consistency_Matrix = consistencyMatrix;
    }

    /**
     * this function will use reacheable node relationship instead of arc
     * @return
     */
    public String matching() {
        String report = "";
        //1.Estimate node pair-wise similarity
        //Added by Ligon for AutoMapping implementation, AutoMapping with provide the Consistency Matrix, will not come from graphSim
        if(assignment_consistency_Matrix==null)
            assignment_consistency_Matrix = graphSim.getAssignment_consistency_Matrix();
        //2. Create a bipartite mit.cadlab.dome3.search.datastructure.graph from compatible node pairs
        BipartiteGraph bGraph = new BipartiteGraph(rGraph.getNodes(), iGraph.getNodes(), assignment_consistency_Matrix);

        //3.pick one matching:
        GreedyBipartiteMatching algorithm = new GreedyBipartiteMatching(bGraph);
        // HungarianBipartiteMatching algorithm2=new HungarianBipartiteMatching(bGraph);

        nodewise_alignment = algorithm.getMatchingInNodeAlignment();

        if (nodewise_alignment != null) {
            report = report + "nodewise alignment---" + nodewise_alignment.size() + " node pairs aligned:\n\t" + nodewise_alignment;
            // consider arc alignment,
            DSet templateArc = rGraph.getArcs(algorithm.getCorrespondingNodes(rGraph.getNodes(), algorithm.getMatching().getLeftIndices()));
            DSet ifaceArc = iGraph.getArcs(algorithm.getCorrespondingNodes(iGraph.getNodes(), algorithm.getMatching().getRightIndices()));

            arcwise_alignment = findAlignment(templateArc, ifaceArc);
            if (arcwise_alignment != null) {
                report = report + "\n arcwise alignment---" + arcwise_alignment.size() + " arc pairs aligned:\n\t" + arcwise_alignment;
            }

            // consider indirect arc alignment,
            DSet indirecttemplateArc = rGraph.getIndirectArcs(algorithm.getCorrespondingNodes(rGraph.getNodes(), algorithm.getMatching().getLeftIndices()));
            DSet indirectIfaceArc = iGraph.getIndirectArcs(algorithm.getCorrespondingNodes(iGraph.getNodes(), algorithm.getMatching().getRightIndices()));

            indirect_nodewise_alignment = findAlignment(indirecttemplateArc, indirectIfaceArc);
            //Qing:May 31, fix a bug here: should also compare the indirect arcs and direct arcs;
            addToIndirectAlignment(indirecttemplateArc,ifaceArc);
            addToIndirectAlignment(templateArc,indirectIfaceArc);

            if (indirect_nodewise_alignment != null) {
                report = report + "\n implied indirect nodewise alignment---" + indirect_nodewise_alignment.size() + " arc pairs aligned:\n\t" + indirect_nodewise_alignment;
            }
        }
        return report;
    }

    private AttributedGraph.Arc getArc(DSet arcs, AttributedNode fromN, AttributedNode toN) {
        if (arcs == null || fromN == null || toN == null) return null;
        if (arcs.size() == 0) return null;
        for (int i = 0; i < arcs.size(); i++) {
            AttributedGraph.Arc a = (AttributedGraph.Arc) arcs.get(i);
            if (a.getFromN() == fromN && a.getToN() == toN) {
                return a;
            }
        }
        return null;
    }

    private HashMap findAlignment(DSet templateArc, DSet ifaceArc) {
        if (templateArc == null || templateArc.size() == 0 ||
                ifaceArc == null || ifaceArc.size() == 0)
            return null;
        HashMap result = new HashMap();
        for (Iterator it = templateArc.iterator(); it.hasNext();) {
            AttributedGraph.Arc template_arc = (AttributedGraph.Arc) it.next();
            if (nodewise_alignment.get(template_arc.getFromN()) != null && nodewise_alignment.get(template_arc.getToN()) != null) {
                AttributedNode iface_fromN = (AttributedNode) nodewise_alignment.get(template_arc.getFromN());
                AttributedNode iface_toN = (AttributedNode) nodewise_alignment.get(template_arc.getToN());
                AttributedGraph.Arc iface_arc = getArc(ifaceArc, iface_fromN, iface_toN);
                if (iface_arc != null)
                    result.put(template_arc, iface_arc);
            }
        }
        if (result.size() != 0) return result;
        return null;
    }

    private void addToIndirectAlignment(DSet templateArc, DSet ifaceArc) {
        if (templateArc == null || templateArc.size() == 0 ||
                ifaceArc == null || ifaceArc.size() == 0)
            return;
        for (Iterator it = templateArc.iterator(); it.hasNext();) {
            AttributedGraph.Arc template_arc = (AttributedGraph.Arc) it.next();
            if (nodewise_alignment.get(template_arc.getFromN()) != null && nodewise_alignment.get(template_arc.getToN()) != null) {
                AttributedNode iface_fromN = (AttributedNode) nodewise_alignment.get(template_arc.getFromN());
                AttributedNode iface_toN = (AttributedNode) nodewise_alignment.get(template_arc.getToN());
                AttributedGraph.Arc iface_arc = getArc(ifaceArc, iface_fromN, iface_toN);
                if (iface_arc != null)   {
                    if(indirect_nodewise_alignment==null)    indirect_nodewise_alignment=new HashMap();
                    indirect_nodewise_alignment.put(template_arc, iface_arc);
                }

            }
        }
    }

    public double getSimilarityScore(int similarity_algorithm_choice) {
        boolean explain=false;
        if (!solved) matching();
        double graph_sim = 0.0;

        if (nodewise_alignment == null || nodewise_alignment.size() == 0) {
            //if(explain) System.out.println("no aligned nodes nor arcs");
            return 0;
        }
        if (arcwise_alignment == null || arcwise_alignment.size() == 0) {
            if (indirect_nodewise_alignment == null || indirect_nodewise_alignment.size() == 0) {
                //if(explain) System.out.println("no aligned arcs or aligned indirect causality");
                return 0;
            }
        }

        //Qing--fixed bug here, prevent a node pair similarity being addedd multiple times
        ArrayList processedNode = new ArrayList();

        ArrayList nodePairing = new ArrayList(); //Array which holds the individual node pairings
        nodeAlignmentArray.clear();

        if (arcwise_alignment != null) {
            for (Iterator entries = arcwise_alignment.entrySet().iterator(); entries.hasNext();) {
                Map.Entry entry = (Map.Entry) entries.next();
                AttributedGraph.Arc template_arc = (AttributedGraph.Arc) entry.getKey();
                AttributedGraph.Arc iface_arc = (AttributedGraph.Arc) entry.getValue();

                AttributedNode template_from_node = template_arc.getFromN();

                if (!processedNode.contains(template_from_node)) {
                    AttributedNode iface_from_node = iface_arc.getFromN();

                    int fromrowIndex = rGraph.getNodeIndex(template_from_node);
                    int fromcolIndex = iGraph.getNodeIndex(iface_from_node);

                    //Added by Ligon: Stores Parameter Pairing
	                nodePairing.add(new Integer(fromrowIndex));
	                nodePairing.add(new Integer(fromcolIndex));
	                nodeAlignmentArray.add(nodePairing.clone());
	                nodePairing.clear();

                    double fromnode_sim = assignment_consistency_Matrix.getQuick(fromrowIndex, fromcolIndex);
                    if(explain) System.out.println("S("+((FuzzyAttributedNode)template_from_node).getName()+","+(iface_from_node).NAME+")="+fromnode_sim+"\n");
                    graph_sim = graph_sim + template_from_node.getWeight() * fromnode_sim;
                    processedNode.add(template_from_node);
                }

                AttributedNode template_to_node = template_arc.getToN();
                if (!processedNode.contains(template_to_node)) {

                    AttributedNode iface_to_node = iface_arc.getToN();
                    int torowIndex = rGraph.getNodeIndex(template_to_node);
                    int tocolIndex = iGraph.getNodeIndex(iface_to_node);

                    //Added by Ligon: Stores Parameter Pairing
	                nodePairing.add(new Integer(torowIndex));
		            nodePairing.add(new Integer(tocolIndex));
		            nodeAlignmentArray.add(nodePairing.clone());
	                nodePairing.clear();
                    	                //System.out.println("[" + ((int[])nodesAlignmentArray.get(0))[0] +" " + ((int[])nodesAlignmentArray.get(0))[1]+ "]  ");

                    double tonode_sim = assignment_consistency_Matrix.getQuick(torowIndex, tocolIndex);
                    if(explain) System.out.println("S("+((FuzzyAttributedNode)template_to_node).getName()+","+(iface_to_node).NAME+")="+tonode_sim+"\n");

                    graph_sim = graph_sim + template_to_node.getWeight() * tonode_sim;
                    processedNode.add(template_to_node);
                }
            }
        }
        int processed = processedNode.size();
        if(explain) System.out.println(processed + " node pairs added due to direct arcs mapping");
        if (indirect_nodewise_alignment != null) {
            for (Iterator entries = indirect_nodewise_alignment.entrySet().iterator(); entries.hasNext();) {
                Map.Entry entry = (Map.Entry) entries.next();
                AttributedGraph.Arc template_arc = (AttributedGraph.Arc) entry.getKey();
                AttributedGraph.Arc iface_arc = (AttributedGraph.Arc) entry.getValue();

                AttributedNode template_from_node = template_arc.getFromN();

                if (!processedNode.contains(template_from_node)) {
                    AttributedNode iface_from_node = iface_arc.getFromN();
                    int fromrowIndex = rGraph.getNodeIndex(template_from_node);
                    int fromcolIndex = iGraph.getNodeIndex(iface_from_node);

                    ///Added by Ligon: Stores Parameter Pairing
	                nodePairing.add(new Integer(fromrowIndex));
		            nodePairing.add(new Integer(fromcolIndex));
		            nodeAlignmentArray.add(nodePairing.clone());
	                nodePairing.clear();


                    double fromnode_sim = assignment_consistency_Matrix.getQuick(fromrowIndex, fromcolIndex);
                    if(explain) System.out.println("S("+((FuzzyAttributedNode)template_from_node).getName()+","+(iface_from_node).NAME+")="+fromnode_sim+"\n");
                    graph_sim = graph_sim + template_from_node.getWeight() * fromnode_sim;
                    processedNode.add(template_from_node);

                }

                AttributedNode template_to_node = template_arc.getToN();
                if (!processedNode.contains(template_to_node)) {

                    AttributedNode iface_to_node = iface_arc.getToN();
                    int torowIndex = rGraph.getNodeIndex(template_to_node);
                    int tocolIndex = iGraph.getNodeIndex(iface_to_node);

                    //Added by Ligon: Stores Parameter Pairing
	                nodePairing.add(new Integer(torowIndex));
		            nodePairing.add(new Integer(tocolIndex));
		            nodeAlignmentArray.add(nodePairing.clone());
	                nodePairing.clear();

                    double tonode_sim = assignment_consistency_Matrix.getQuick(torowIndex, tocolIndex);
                    if(explain) System.out.println("S("+((FuzzyAttributedNode)template_to_node).getName()+","+(iface_to_node).NAME+")="+tonode_sim+"\n");

                    graph_sim = graph_sim + template_to_node.getWeight() * tonode_sim;
                    processedNode.add(template_to_node);

                }
            }
        }
        if(explain) System.out.println(processedNode.size() - processed + " node pairs added due to indirect node mapping");
        return graph_sim;
    }

    public String calculateGraphSimilarityToString() {
        if (!solved) matching();
        String progress_report = "";
        double graph_sim = 0.0;

        if (nodewise_alignment == null || nodewise_alignment.size() == 0) {
            return progress_report + "no aligned nodes nor arcs";
        }
        if (arcwise_alignment == null || arcwise_alignment.size() == 0) {
            if (indirect_nodewise_alignment == null || indirect_nodewise_alignment.size() == 0) {
                {
                    return progress_report + "no aligned arcs";
                }
            }
        }

        //Qing--fixed bug here, prevent a node pair similarity being addedd multiple times
        ArrayList processedNode = new ArrayList();

        if (arcwise_alignment != null) {
            for (Iterator entries = arcwise_alignment.entrySet().iterator(); entries.hasNext();) {
                Map.Entry entry = (Map.Entry) entries.next();
                AttributedGraph.Arc template_arc = (AttributedGraph.Arc) entry.getKey();
                AttributedGraph.Arc iface_arc = (AttributedGraph.Arc) entry.getValue();

                AttributedNode template_from_node = template_arc.getFromN();

                if (!processedNode.contains(template_from_node)) {
                    AttributedNode iface_from_node = iface_arc.getFromN();

                    int fromrowIndex = rGraph.getNodeIndex(template_from_node);
                    int fromcolIndex = iGraph.getNodeIndex(iface_from_node);

                    double fromnode_sim = assignment_consistency_Matrix.getQuick(fromrowIndex, fromcolIndex);
                    graph_sim = graph_sim + template_from_node.getWeight() * fromnode_sim;
                    processedNode.add(template_from_node);
                }

                AttributedNode template_to_node = template_arc.getToN();
                if (!processedNode.contains(template_to_node)) {

                    AttributedNode iface_to_node = iface_arc.getToN();
                    int torowIndex = rGraph.getNodeIndex(template_to_node);
                    int tocolIndex = iGraph.getNodeIndex(iface_to_node);

                    double tonode_sim = assignment_consistency_Matrix.getQuick(torowIndex, tocolIndex);

                    graph_sim = graph_sim + template_to_node.getWeight() * tonode_sim;
                    processedNode.add(template_to_node);

                }
            }
        }
        int processed = processedNode.size();
        progress_report = progress_report + processed + " node pairs added due to direct arcs mapping\n";
        if (indirect_nodewise_alignment != null) {
            for (Iterator entries = indirect_nodewise_alignment.entrySet().iterator(); entries.hasNext();) {
                Map.Entry entry = (Map.Entry) entries.next();
                AttributedGraph.Arc template_arc = (AttributedGraph.Arc) entry.getKey();
                AttributedGraph.Arc iface_arc = (AttributedGraph.Arc) entry.getValue();

                AttributedNode template_from_node = template_arc.getFromN();

                if (!processedNode.contains(template_from_node)) {
                    AttributedNode iface_from_node = iface_arc.getFromN();

                    int fromrowIndex = rGraph.getNodeIndex(template_from_node);
                    int fromcolIndex = iGraph.getNodeIndex(iface_from_node);

                    double fromnode_sim = assignment_consistency_Matrix.getQuick(fromrowIndex, fromcolIndex);
                    graph_sim = graph_sim + template_from_node.getWeight() * fromnode_sim;
                    processedNode.add(template_from_node);
                }

                AttributedNode template_to_node = template_arc.getToN();
                if (!processedNode.contains(template_to_node)) {

                    AttributedNode iface_to_node = iface_arc.getToN();
                    int torowIndex = rGraph.getNodeIndex(template_to_node);
                    int tocolIndex = iGraph.getNodeIndex(iface_to_node);

                    double tonode_sim = assignment_consistency_Matrix.getQuick(torowIndex, tocolIndex);

                    graph_sim = graph_sim + template_to_node.getWeight() * tonode_sim;
                    processedNode.add(template_to_node);

                }
            }
        }

        progress_report = progress_report + (processedNode.size() - processed) + " node pairs added due to indirect node mapping\n";
        progress_report = progress_report +"Overall mit.cadlab.dome3.search.datastructure.graph similarity:"+graph_sim;
        return progress_report;
    }


    public String generateReport(int similarity_algorithm_choice) {
        if (!solved) matching();
        return calculateGraphSimilarityToString();
    }

}
