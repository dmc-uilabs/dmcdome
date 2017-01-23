package mit.cadlab.dome3.integrationwizards.decisiontree.datastorage;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Oct 31, 2006
 * Time: 10:39:21 AM
 * To change this template use Options | File Templates.
 */
public class ModelData {
    protected ArrayList parameterValues;
    protected ArrayList parameterCounts;
    protected ArrayList parameterLevels;
    protected ArrayList weights;
    protected ArrayList2D attributedDecisions;
    protected ArrayList2D attributedCounts;
    protected int inputCount;
    protected int outputCount;
    protected int totalWeight;
    protected int numParameters;

    public ModelData()
    {
        this.parameterValues = new ArrayList();
        this.parameterCounts = new ArrayList();
        this.weights = new ArrayList();
        this.attributedDecisions = new ArrayList2D(3,0);
        this.attributedCounts = new ArrayList2D(3,0);
        this.inputCount = 0;
        this.outputCount = 0;
        this.numParameters = 0;
    }

    public void newEntry(ArrayList parameterValue, int numOccurances)
    {
        String inputoutput = (String)parameterValue.get(0);
        parameterValues.add(parameterValue.clone());
        parameterCounts.add(new Integer(numOccurances));
        numParameters++;
        if (inputoutput.equalsIgnoreCase("input"))
        {
            inputCount += numOccurances;

        }
        else
            outputCount += numOccurances;
    }

    public void newEntry(ArrayList parameterValue, int numOccurances, int weight)
    {
        newEntry(parameterValue,numOccurances);
        weights.add(new Integer(weight));
        totalWeight += weight;
    }

    public void setParameterLevels()
    {
        int weight;
        int count;
        ArrayList parameterValue;
        String inputoutput;
        int averageWeight = totalWeight/numParameters;
        int averageCount = (inputCount+outputCount)/numParameters;
        for(int index=0;index<numParameters;index++)
        {
            parameterValue = (ArrayList)parameterValues.get(index);
            inputoutput = (String)parameterValue.get(0);
            weight = ((Integer)weights.get(index)).intValue();
            count = ((Integer)parameterCounts.get(index)).intValue();
            if(weight > averageWeight+2)
                attributeDecision(parameterValue,1,count);
            else if(count > averageCount+2)
                attributeDecision(parameterValue,1,count);
            else if(inputoutput.equalsIgnoreCase("output") && outputCount<2)
                attributeDecision(parameterValue,1,count);
            else
                attributeDecision(parameterValue,2,count);
        }
    }

    private void attributeDecision(Object parameterValue,int level, int count)
    {
        attributedDecisions.addEntry(parameterValue,level);
        attributedCounts.addEntry(new Integer(count),level);
    }

    public void attributeDecision(Integer decision,int level)
    {
        attributedDecisions.addEntry(decision,level);
    }

    public ArrayList2D getDecisions()
    {
        return attributedDecisions;
    }

    public ArrayList2D getDecisionCounts()
    {
        return attributedCounts;
    }

    public ArrayList getValues()
    {
        return parameterValues;
    }

    public ArrayList getCounts()
    {
        return parameterCounts;
    }

    public int getInputCount()
    {
        return inputCount;
    }

    public int numParameters()
    {
        return inputCount+outputCount;
    }
}
