package mit.cadlab.dome3.integrationwizards.decisiontree.datastorage;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Oct 31, 2006
 * Time: 10:01:25 AM
 * To change this template use Options | File Templates.
 */
public class DecisionData {

    //in 2D Matrices, first index is decision parameter, second index is the model number
    protected ArrayList2D decisionCounts;
    protected DecisionHistory decisionHistory;
    protected ArrayList templates;
    private int numModels;

    public DecisionData (int numoneDecisions, int numtwoDecisions, int numthreeDecisions, int numModel, ArrayList models)
    {
        this.numModels = numModel;
        this.templates = models;
        this.decisionHistory = new DecisionHistory(numoneDecisions,numtwoDecisions, numthreeDecisions, numModels);
        int[] lengths = new int[3];
        lengths[0]=numoneDecisions;
        lengths[1]=numtwoDecisions;
        lengths[2]=numthreeDecisions;
        decisionCounts = new ArrayList2D(lengths);
    }

    public int getDecisionCount(int level,int decision)
    {
        return ((Integer)decisionCounts.getEntry(level,decision)).intValue();
    }

    public void addEntry(int level, int modelNum, int decision)
    {
        int count;
        decisionHistory.addModelDecision(level,modelNum,decision);
        count = ((Integer)decisionCounts.getEntry(level,decision)).intValue();
        count++;
        decisionCounts.setEntry(new Integer(count),level,decision);
    }

    //Todo: think of a better way to deal with deleting the models in the model data array and FuzzyARG array
    public ArrayList applyDecision(int level, int decision, ArrayList modelDataArray)
    {
        for (int modelNum=0;modelNum<numModels;modelNum++)
        {
            ModelData model = (ModelData)modelDataArray.get(modelNum);
            if(decisionHistory.getModelDecision(level,modelNum,decision)==0)
            {
                deleteModel(modelNum,model);
                modelDataArray.remove(modelNum);
                modelNum--;
            }
        }
        //Set the count for performed decision to zero indicating it has been performed
        decisionCounts.setEntry(new Integer(0),level,decision);
        return modelDataArray;
    }

    private void deleteModel(int modelNum, ModelData modelData)
    {
        ArrayList modelDecisionNumbers;
        int decisionNumber;
        int count;
        for (int level=0;level<3;level++)
        {
            modelDecisionNumbers = modelData.getDecisions().getRow(level);
            for (int i=0;i<modelDecisionNumbers.size();i++)
            {
                decisionNumber = ((Integer)modelDecisionNumbers.get(i)).intValue();
                count = ((Integer)decisionCounts.getEntry(level,decisionNumber)).intValue();
                if (count>0)
                    count--;
                decisionCounts.setEntry(new Integer(count),level,decisionNumber);
            }
        }
        decisionHistory.removeModel(modelNum);
        templates.remove(modelNum);
        numModels--;
    }

    public int getLevelLength(int level)
    {
        return decisionCounts.getRow(level).size();
    }

    public int getMinLevelIndex(int level)
    {
        int currentCount = 0;
        int maxIndex = 0;
        int maxCount = 10000;
        for(int index=0;index<getLevelLength(level);index++)
        {
            currentCount = getDecisionCount(level,index);
            if (0<currentCount && currentCount<maxCount)
            {
                maxCount = currentCount;
                maxIndex = index;
            }
        }
        if(maxCount==10000)
            return -1;
        return maxIndex;
    }

    public void secureModel(int modelNum)
    {
        for(int levelIndex=0;levelIndex<3;levelIndex++)
        {
            for(int decisionIndex=0;decisionIndex<getLevelLength(levelIndex);decisionIndex++)
                decisionHistory.addModelDecision(levelIndex,modelNum,decisionIndex);
        }
    }

    public int getNumModels()
    {
        return numModels;
    }

    public ArrayList getFinalModels()
    {
        return templates;
    }
}
