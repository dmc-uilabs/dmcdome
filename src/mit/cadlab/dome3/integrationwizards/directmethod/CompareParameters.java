package mit.cadlab.dome3.integrationwizards.directmethod;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;

import java.util.*;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ModelMapping;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ParameterPair;
import mit.cadlab.dome3.search.graphmatching.BipartiteGraph;
import mit.cadlab.dome3.search.graphmatching.GreedyBipartiteMatching;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 8, 2006
 * Time: 3:53:42 PM
 * To change this template use Options | File Templates.
 */

public class CompareParameters
{
	private DenseDoubleMatrix2D inputSimilarityMatrix;
	private DenseDoubleMatrix2D outputSimilarityMatrix;
    private DenseDoubleMatrix2D dimensionSimilarityMatrix;
	private HashMap nodeAlignment;
	private FuzzyARG objModelA;
	private FuzzyARG objModelB;

	public ModelMapping compareParameters(FuzzyARG objectiveModelA, FuzzyARG objectiveModelB)
	{
		this.objModelA = objectiveModelA;
		this.objModelB = objectiveModelB;
		int rows = objModelA.getNodes().size();
        int cols = objModelB.getNodes().size();
		inputSimilarityMatrix = new DenseDoubleMatrix2D(rows,cols);
		outputSimilarityMatrix = new DenseDoubleMatrix2D(rows,cols);
        dimensionSimilarityMatrix = new DenseDoubleMatrix2D(rows,cols);

        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < cols; columnIndex++) {
                FuzzyAttributedNode aNode = (FuzzyAttributedNode) objModelA.getNode(rowIndex);
                FuzzyAttributedNode bNode = (FuzzyAttributedNode) objModelB.getNode(columnIndex);
	            String checkA = (String)aNode.getInputoutput().getSampleSet().iterator().next();
	            String checkB = (String)bNode.getInputoutput().getSampleSet().iterator().next();
	            double nodeSimilarity = 0;
                double dimSimilarity = 0;
	            if ((checkA.toLowerCase()!=checkB.toLowerCase()))
	            {
                    dimSimilarity = ParameterSimilarity.calculateParameterSimilarity(aNode,bNode);
                    nodeSimilarity = ParameterSimilarity.addNameSimilarity(aNode,bNode,dimSimilarity);
		            outputSimilarityMatrix.setQuick(rowIndex, columnIndex, nodeSimilarity);
                    dimensionSimilarityMatrix.setQuick(rowIndex,columnIndex,dimSimilarity);
	            }
	            else if ((checkA.equalsIgnoreCase("input"))&&(checkB.equalsIgnoreCase("input")))
	            {
                    dimSimilarity = ParameterSimilarity.calculateParameterSimilarity(aNode,bNode);
                    nodeSimilarity = ParameterSimilarity.addNameSimilarity(aNode,bNode,dimSimilarity);
                    inputSimilarityMatrix.setQuick(rowIndex, columnIndex, nodeSimilarity);
                    dimensionSimilarityMatrix.setQuick(rowIndex,columnIndex,dimSimilarity);
	            }
            }
        }
		ArrayList inputMappedParameters = findMatches(inputSimilarityMatrix,"input");
        ArrayList outputMappedParameters = findMatches(outputSimilarityMatrix,"output");
		ModelMapping modelMapping = new ModelMapping(inputMappedParameters,outputMappedParameters);

		return modelMapping;
	}

	public ArrayList findMatches(DenseDoubleMatrix2D similarityMatrix,String pairType)
	{
		ArrayList mappedParameters = new ArrayList();
		ParameterPair mappedParameterPair;
        BipartiteGraph bGraph = new BipartiteGraph(objModelA.getNodes(), objModelB.getNodes(), similarityMatrix);
		GreedyBipartiteMatching algorithm = new GreedyBipartiteMatching(bGraph);
        nodeAlignment = algorithm.getMatchingInNodeAlignment();

		if (nodeAlignment!=null)
		{
            Set aMatched = nodeAlignment.keySet();
			Collection bMatched = nodeAlignment.values();
			Iterator j = bMatched.iterator();
			for(Iterator i = aMatched.iterator(); i.hasNext();)
			{
                FuzzyAttributedNode aaNode = (FuzzyAttributedNode)i.next();
				FuzzyAttributedNode bbNode = (FuzzyAttributedNode)j.next();
                int torowIndex = objModelA.getNodeIndex(aaNode);
                int tocolIndex = objModelB.getNodeIndex(bbNode);

                double dimSim = dimensionSimilarityMatrix.getQuick(torowIndex,tocolIndex);
                double nodeSim = similarityMatrix.getQuick(torowIndex,tocolIndex);
				if(nodeSim>0.75)
				{
                    mappedParameterPair = new ParameterPair(aaNode,bbNode,dimSim,pairType);
                    mappedParameters.add(mappedParameterPair);
				}
			}
		}
		return mappedParameters;
	}
}
