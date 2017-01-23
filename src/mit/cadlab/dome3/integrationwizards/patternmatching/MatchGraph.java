package mit.cadlab.dome3.integrationwizards.patternmatching;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.util.*;

import mit.cadlab.dome3.search.graphmatching.RefinedGMAlgorithm;
import mit.cadlab.dome3.integrationwizards.directmethod.ParameterSimilarity;
import mit.cadlab.dome3.integrationwizards.patternmatching.clustering.ClusterAnalysis;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Oct 11, 2006
 * Time: 10:35:00 AM
 * To change this template use Options | File Templates.
 */
public class MatchGraph {
    private FuzzyARG templateModel;
    private FuzzyARG objectiveModel;
    private DenseDoubleMatrix2D nameSimilarityMatrix;
    private DenseDoubleMatrix2D scoreSimilarityMatrix;
    private DenseDoubleMatrix2D logDifferenceMatrix;
    private int numMatchedParameters;
    protected double score;
    protected ArrayList nodePairings;

    public MatchGraph(FuzzyARG tempModel,FuzzyARG objModel)
    {
        this.templateModel = tempModel;
        this.objectiveModel = objModel;
        matchingSetup();
        runPatternMatching();
    }

    private void runPatternMatching()
	{
        //Run the matching portion of the mit.cadlab.dome3.search.datastructure.graph matching algorithm using the full parameter similarity
        RefinedGMAlgorithm algorithm=new RefinedGMAlgorithm(templateModel,objectiveModel,nameSimilarityMatrix);
        algorithm.matching();
        algorithm.solved=true;
        //When determining the total mit.cadlab.dome3.search.datastructure.graph similarity, just use the dimension similarity
        algorithm.setConsistencyMatrix(scoreSimilarityMatrix);
        double totalScore = algorithm.getSimilarityScore(0);
        nodePairings = algorithm.nodeAlignmentArray;
        numMatchedParameters = nodePairings.size();
        if (totalScore!=0)
            score = totalScore/numMatchedParameters;
        //If the mit.cadlab.dome3.search.datastructure.graph similarityscore is below the 0.9 threshold, run the modifying matching method.
        //If the similarity score is still below 0.9, throwout the template
        //System.out.println("Graph Similarity Score: " + score);
        if(score<0.9)
        {
            ClusterAnalysis method = new ClusterAnalysis(scoreSimilarityMatrix, nodePairings);
            if(method.beginAnalysis())
                score = 0.9;
            //score = runModifiedPatternMatching();
            //System.out.println("Modified Score: " +score);
        }
	}

    //This method stores the two different similarity scores in two similarity matrices.
    //The Two similaries are 1: Complete Parmeter Similarity Calculation and 2: Just Dimension Similarity Calculation
    private void matchingSetup()
    {
        double nodeSimilarity;
        double nameSimilarity;
        double difference;
        int cols = objectiveModel.getNodes().size();
        int rows = templateModel.getNodes().size();
        nameSimilarityMatrix = new DenseDoubleMatrix2D(rows, cols);
		scoreSimilarityMatrix = new DenseDoubleMatrix2D(rows, cols);
        logDifferenceMatrix = new DenseDoubleMatrix2D(rows, cols);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                nodeSimilarity = 0;
                FuzzyAttributedNode objNode = (FuzzyAttributedNode) objectiveModel.getNode(i);
                FuzzyAttributedNode tempNode = (FuzzyAttributedNode) templateModel.getNode(j);
	            String checkA = (String)objNode.getInputoutput().getSampleSet().iterator().next();
	            String checkB = (String)tempNode.getInputoutput().getSampleSet().iterator().next();
                if(checkA.equalsIgnoreCase(checkB))
                {
	                nodeSimilarity = ParameterSimilarity.calculateParameterSimilarity(objNode,tempNode);
                    nameSimilarity = ParameterSimilarity.addNameSimilarity(objNode,tempNode,nodeSimilarity);
                    difference = ParameterSimilarity.calculateParameterDifference(objNode,tempNode);
                    scoreSimilarityMatrix.setQuick(j,i,nodeSimilarity);
		            nameSimilarityMatrix.setQuick(j, i, nameSimilarity);
                    logDifferenceMatrix.setQuick(j,i,difference);
                }
            }
        }
    }

  /*Models can be exactly the same yet have parameters scalings which are vastly different. This method checks
    whether or not this might be the case and determines the new similarity score.
    There are two possibile modifications:
    1) Input Nodes which aren't in the proper scale and have outputs which are also not in the proper range (scale & get new sim score)
    2) Input Nodes whose outputs weren't paired in bipartite matching (delete pair & get new sim score) */
    private double runModifiedPatternMatching()
    {
        double parameterPairSimilarityScore;
        int objNodeIndex;
        int tempNodeIndex;
        double newScore = score;
        FuzzyAttributedNode objNode;
        ArrayList deletePairs = new ArrayList();
        ArrayList scalePairs = new ArrayList();
        ArrayList scalingArray = new ArrayList();

        //Search Through the matched parameters to find the low similarity scores
        for(int i=0;i<numMatchedParameters;i++)
        {
            objNodeIndex = ((Integer)((ArrayList)nodePairings.get(i)).get(1)).intValue();
            tempNodeIndex = ((Integer)((ArrayList)nodePairings.get(i)).get(0)).intValue();
            objNode = (FuzzyAttributedNode)objectiveModel.getNode(objNodeIndex);

            parameterPairSimilarityScore = scoreSimilarityMatrix.getQuick(tempNodeIndex,objNodeIndex);
            //Check if the pair's similarity score is low and make sure it's an input pair
            if (parameterPairSimilarityScore<0.9 && objectiveModel.isInput(objNode))
            {
                Map objArcs = objectiveModel.getArcs();
                List outputNodeList = (List)objArcs.get(objNode);
                //Determine whether or not it's outputs are also below threshold
                boolean foundPairedOutput = false;
                boolean foundLowOutput = false;
                for(Iterator iterator=outputNodeList.iterator();iterator.hasNext();)
                {
                    FuzzyAttributedNode outputNode = (FuzzyAttributedNode)iterator.next();
                    int outputNodeIndex = objectiveModel.getNodeIndex(outputNode);
                    //Determine whether this output node is paired
                    for (int a=0;a<numMatchedParameters;a++)
                    {
                        int objIndex = ((Integer)((ArrayList)nodePairings.get(a)).get(1)).intValue();
                        if (objIndex==outputNodeIndex)
                        {
                            //If a paired output node is found, flag it to be kept
                            foundPairedOutput = true;
                            ArrayList outputNodePair = (ArrayList)nodePairings.get(a);
                            int tempIndex = ((Integer)outputNodePair.get(0)).intValue();
                            double outputPairSimilarityScore = scoreSimilarityMatrix.getQuick(tempIndex,objIndex);
                            //If the output node is paired AND it's similarity score is low
                            //flag it for scaling and find the input nodes scaling factor
                            if (outputPairSimilarityScore<0.9)
                            {
                                foundLowOutput = true;
                                scalingArray.add(nodeScale(objNode,tempNodeIndex));
                                if(!deletePairs.contains(outputNodePair))
                                    deletePairs.add(outputNodePair);
                            }
                        }
                    }
                }
                //if the pair is low and it's paired output is low scale it
                if (foundLowOutput)
                    scalePairs.add(nodePairings.get(i));
                //if the pair did not have a paired output, delelte it
                else if(!foundPairedOutput)
                    deletePairs.add(nodePairings.get(i));
            }
        }
        //If a parameter pair need to be modified,
        if(deletePairs.size()!=0 || scalePairs.size()!=0)
        {
            double scale =0;
            //Average the scaling factor to be applied to all of the parameters requiring scaling
            for(int x=0;x<scalingArray.size();x++)
                scale += ((Double)scalingArray.get(x)).doubleValue();
            scale = scale/scalingArray.size();
            newScore = calculateNewScore(scalePairs,deletePairs,scale);
        }

      return newScore;
    }

    private double calculateNewScore(ArrayList scalePairs,ArrayList deletePairs,double scale)
    {
        double newScore = 0;
        int newNumberMatched = numMatchedParameters;
        boolean deletePair;
        int objNodeIndex;
        int tempNodeIndex;

        for(int i=0;i<numMatchedParameters;i++)
        {
            deletePair = false;
            objNodeIndex = ((Integer)((ArrayList)nodePairings.get(i)).get(1)).intValue();
            tempNodeIndex = ((Integer)((ArrayList)nodePairings.get(i)).get(0)).intValue();
            for(int a=0;a<deletePairs.size();a++)
            {
                if(deletePairs.get(a)==nodePairings.get(i))
                {
                    deletePair = true;
                    break;
                }
            }
            if(deletePair)
                newNumberMatched--;
            else
            {
                for(int k=0;k<scalePairs.size();k++)
                {
                    if(scalePairs.get(k)==nodePairings.get(i))
                    {
                        FuzzyAttributedNode objNode = (FuzzyAttributedNode)objectiveModel.getNode(objNodeIndex);
                        FuzzyAttributedNode tempNode = (FuzzyAttributedNode)templateModel.getNode(tempNodeIndex);
                        double simScore = ParameterSimilarity.calculateScaledNodeSimilarity(objNode,tempNode,scale);
                        scoreSimilarityMatrix.setQuick(tempNodeIndex,objNodeIndex,simScore);
                        break;
                    }
                }
                double addScore =  scoreSimilarityMatrix.getQuick(tempNodeIndex,objNodeIndex);
                newScore = newScore + addScore;
            }
        }

        newScore = newScore/newNumberMatched;
        return newScore;
    }

    private Double nodeScale(FuzzyAttributedNode objNode,int tempNodeIndex)
    {
        FuzzyAttributedNode tempNode = (FuzzyAttributedNode)templateModel.getNode(tempNodeIndex);
        double tempDim;
        try  {tempDim = ((Double)tempNode.getDim().getSampleSet().iterator().next()).doubleValue();}
        catch(ClassCastException e)
        {tempDim = 0.00000000000001;}
        double objDim = ((Double)objNode.getDim().getSampleSet().iterator().next()).doubleValue();
        return new Double(tempDim/objDim);
    }

    public double getScore()
    {
        return score;
    }

    public ArrayList getMatchedParameters()
    {
        return nodePairings;
    }
}
