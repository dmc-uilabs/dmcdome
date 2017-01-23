package mit.cadlab.dome3.integrationwizards.patternmatching;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import java.util.ArrayList;

import mit.cadlab.dome3.search.framework.templatemanagement.Template;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Oct 10, 2006
 * Time: 10:52:03 AM
 * To change this template use Options | File Templates.
 */
public class GraphMatchPair {

    private ArrayList nodePairings;
    private int numMatchedParameters = 0;
    private double score = 0;
    private int matchedIndex;
    private FuzzyARG matchedModel;
    private FuzzyARG objectiveModel;
    public boolean matched = false;

    public GraphMatchPair(ArrayList templateModels,FuzzyARG objModel)
    {
        this.objectiveModel = objModel;
        int numPossibleModels = templateModels.size();
        double newScore = 0;
        for (int i=0;i<numPossibleModels;i++)
		{
			FuzzyARG currentModel = ((Template)templateModels.get(i)).getGraph();
            MatchGraph match = new MatchGraph(currentModel,objectiveModel);
            newScore = match.getScore();

            int numMatched = match.getMatchedParameters().size();
            if (newScore>=0.89999999999999999999 && ((newScore>score && numMatched>=numMatchedParameters) || numMatched>numMatchedParameters))
            {
                matched = true;
                matchedIndex = i;
                score = newScore;
                matchedModel = currentModel;
                nodePairings = match.getMatchedParameters();
                numMatchedParameters = nodePairings.size();
            }
		}
        if (matched)
        {
            //matchedModel.name = ((Template)templateModels.get(matchedIndex)).getName();
            System.out.println(objectiveModel.name +" matched to template model " +((Template)templateModels.get(matchedIndex)).getName());
            System.out.println("Similarity Score: " + score);
            System.out.print("Node Pairing: ");
            for (int b=0;b<numMatchedParameters;b++)
            System.out.print("["+ ((ArrayList)nodePairings.get(b)).get(0) +" "+ ((ArrayList)nodePairings.get(b)).get(1)+ "]  ");
            System.out.println();
            System.out.println();
        }
    }

    public FuzzyARG getMatchedModel()
    {
        return matchedModel;
    }

    public ArrayList getNodePairings()
    {
        return nodePairings;
    }

}
