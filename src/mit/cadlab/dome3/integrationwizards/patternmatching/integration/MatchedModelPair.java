package mit.cadlab.dome3.integrationwizards.patternmatching.integration;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 12, 2007
 * Time: 2:16:34 PM
 * To change this template use Options | File Templates.
 */
public class MatchedModelPair {

    private FuzzyARG objectiveModel;
    private FuzzyARG templateModel;
    private ArrayList matchedParameters = new ArrayList();

    public MatchedModelPair(FuzzyARG objModel, FuzzyARG match, ArrayList matches)
    {
        this.objectiveModel = objModel;
        this.templateModel = match;
        matchedParameters = matches;
    }

    public FuzzyARG getObjectiveModel(FuzzyARG tempModel)
    {
        if(tempModel==templateModel)
            return objectiveModel;
        else
            return null;
    }

    public FuzzyARG getTemplateModel()
    {
        return templateModel;
    }

    public FuzzyARG getObjectiveModel()
    {
        return objectiveModel;
    }

    public ArrayList getMatchedParameters()
    {
        return matchedParameters;
    }
}
