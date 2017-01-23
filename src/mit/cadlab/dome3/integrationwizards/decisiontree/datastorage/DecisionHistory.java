package mit.cadlab.dome3.integrationwizards.decisiontree.datastorage;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Nov 7, 2006
 * Time: 6:04:29 PM
 * To change this template use Options | File Templates.
 */
public class DecisionHistory {
    private ArrayList completeHistory = new ArrayList();
    private int numModels;

    public DecisionHistory(int numoneDecisions,int numtwoDecisions, int numthreeDecisions, int numModel)
    {
        this.numModels = numModel;
        completeHistory.add(initializedArrayList(numoneDecisions,1));
        completeHistory.add(initializedArrayList(numtwoDecisions,1));
        completeHistory.add(initializedArrayList(numthreeDecisions,1));
    }

    public void addModelDecision(int level, int modelNum, int decision)
    {
        ArrayList decisionLevel = (ArrayList)completeHistory.get(level);
        ArrayList modelDecisions = (ArrayList)((ArrayList)decisionLevel.get(modelNum)).clone();
        modelDecisions.set(decision, new Integer(1));
        decisionLevel.set(modelNum,modelDecisions);
        completeHistory.set(level,decisionLevel);
    }

    public int getModelDecision(int level, int modelNum, int decision)
    {
        int entry;
        ArrayList decisionLevel = (ArrayList)completeHistory.get(level);
        ArrayList modelDecisions = (ArrayList)decisionLevel.get(modelNum);
        entry = ((Integer)modelDecisions.get(decision)).intValue();
        return entry;
    }

    public void removeModel(int modelNum)
    {
        ArrayList levelList;
        for (int level=0;level<completeHistory.size();level++)
        {
            levelList = (ArrayList)completeHistory.get(level);
            levelList.remove(modelNum);
            completeHistory.set(level,levelList);
            numModels--;
        }
    }

    private ArrayList initializedArrayList(int numDecisions, int depth)
    {
        ArrayList newArray = new ArrayList();
        Object entry;
        int length;
        switch(depth)
        {
            case 1: length = numModels; entry = initializedArrayList(numDecisions,2); break;
            default: length = numDecisions; entry = new Integer(0); break;
        }
        for (int i=0;i<length;i++)
            newArray.add(entry);
        return newArray;
    }
}
