package mit.cadlab.dome3.integrationwizards.decisiontree;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Jul 16, 2006
 * Time: 3:25:26 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.integrationwizards.decisiontree.datastorage.ModelData;
import mit.cadlab.dome3.integrationwizards.decisiontree.datastorage.ArrayList2D;
import mit.cadlab.dome3.integrationwizards.decisiontree.datastorage.DecisionData;

import java.util.ArrayList;

public class InformationGain
{
	public ModelData tempModelData;

	//Method which inputs the correct values into the decision history and count
	public DecisionData infoGain(ModelData objData, ModelData tempData, DecisionData decisionData,int modelNum)
    {
        ArrayList objValues;
        ArrayList objCounts;
        ArrayList2D objectiveValues = objData.getDecisions();
        ArrayList2D objectiveCounts = objData.getDecisionCounts();
        ArrayList tempValues = tempData.getValues();
        ArrayList tempCounts = tempData.getCounts();
        int numObjParameters;
        int numTempParameters = tempValues.size();
        int numAcceptedParameters = 0;

        if(FuzzyDecision.near(objData.getInputCount(),tempData.getInputCount()))
            decisionData.addEntry(0,modelNum,0);

        //the first loop runs through the two levels of objective model parameters
        for (int level=1;level<3;level++)
        {
            objValues = objectiveValues.getRow(level);
            objCounts = objectiveCounts.getRow(level);
            numObjParameters = objValues.size();
            //The second loop runs through all of the objective models parameters which were isolated using parameterCounts
            for (int decisionNum=0;decisionNum<numObjParameters;decisionNum++)
            {
                //The third loop runs through each parameter from parameterCounts in the current template model
                for (int k=0;k<numTempParameters;k++)
                {
                    //First check if the current template model contains the current decision parameter
                    if (objValues.get(decisionNum).equals(tempValues.get(k)))
                        {
                        //If it does, check if they have a similar number of parameters
                        int intObjCount = Integer.parseInt(objCounts.get(decisionNum).toString());
                        int intModelCount = Integer.parseInt(tempCounts.get(k).toString());
                        if (FuzzyDecision.near(intObjCount,intModelCount))
                        {
                            decisionData.addEntry(level,modelNum,decisionNum);
                            tempData.attributeDecision(new Integer(decisionNum),level);
                            numAcceptedParameters += intObjCount;
                        }
                    }
                }
            }
        }
        if(FuzzyDecision.greaterThan(0.75*objData.numParameters(),numAcceptedParameters) && FuzzyDecision.lessThan(2*objData.getInputCount(),tempData.getInputCount()))
            decisionData.secureModel(modelNum);
        tempModelData = tempData;
        return decisionData;
	}
}