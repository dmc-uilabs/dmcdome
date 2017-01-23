package mit.cadlab.dome3.integrationwizards.decisiontree;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import java.util.ArrayList;

import mit.cadlab.dome3.search.framework.templatemanagement.Template;
import mit.cadlab.dome3.integrationwizards.decisiontree.datastorage.DecisionData;
import mit.cadlab.dome3.integrationwizards.decisiontree.datastorage.ModelData;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Jul 19, 2006
 * Time: 11:36:01 AM
 * To change this template use Options | File Templates.
 */
public class DecisionTree
{
	public ArrayList finalModels = new ArrayList();
    private ArrayList templateModelList;
    private DecisionData decisionData;

	public DecisionTree(FuzzyARG objectiveModel, ArrayList models)
	{
        this.templateModelList = (ArrayList)models.clone();
		finalModels = startDecisionTree(objectiveModel);
	}

	public ArrayList startDecisionTree(FuzzyARG objectiveModel)
	{
		//System.out.println("Entering Decision Tree");
		int numModels = templateModelList.size();
		ArrayList modelDataArray = new ArrayList();
        CountModel objCountModel = new CountModel();
		ModelData objModelData = objCountModel.isolateParameters(objectiveModel);
        objModelData.setParameterLevels();

		//Based on these parameters and counts, find the counts of each of the template models
		//These two arrays store the overall model counts for each parameter value
		//and the history of which model contained which parameter

        decisionData = new DecisionData(2,objModelData.getDecisionCounts().getRow(1).size(),objModelData.getDecisionCounts().getRow(2).size(),numModels,templateModelList);
		for (int modelNum=0;modelNum<numModels;modelNum++)
		{
            //Select Current model and find its parameter values and counts
            ModelData tempModelData = new ModelData();
            CountModel countModel = new CountModel();
		    InformationGain modelGain = new InformationGain();
		    FuzzyARG currentModel = ((Template)templateModelList.get(modelNum)).getGraph();
			tempModelData = countModel.isolateParameters(currentModel);

            //Add the model information into the decision data
            decisionData = modelGain.infoGain(objModelData,tempModelData,decisionData,modelNum);
            modelDataArray.add(modelGain.tempModelData);
		}

		//Enter Decision Tree Loop
        int level = 0;
        int index;
        int oldNumModels=numModels;
        while (numModels>1)
		{
            index = decisionData.getMinLevelIndex(level);
            if(index==-1 && level<2)
                level++;
            else if(index==-1)
                numModels=1;
            else
            {
                modelDataArray = decisionData.applyDecision(level,index,modelDataArray);
                oldNumModels = numModels;
                numModels = decisionData.getNumModels();
            }
            if(oldNumModels==numModels && level>2)
                numModels=1;
		}
		return decisionData.getFinalModels();
	}
}