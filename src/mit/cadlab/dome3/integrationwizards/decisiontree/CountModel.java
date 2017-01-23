package mit.cadlab.dome3.integrationwizards.decisiontree;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;

import java.util.ArrayList;

import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.UnitAnalyzer;
import mit.cadlab.dome3.integrationwizards.decisiontree.datastorage.ModelData;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Jul 14, 2006
 * Time: 12:09:26 PM
 * To change this template use Options | File Templates.
 */

public class CountModel
{

	//ArrayLists which contain the model's paramter values and counts
	private ArrayList parameterValue = new ArrayList();
    private ModelData modelData = new ModelData();

	//Method which isolates the parameter values and determines how often they occur in the model
	public ModelData isolateParameters(FuzzyARG countModel)
	{
		int numParameters=countModel.getNodes().size();
		ArrayList parameterRow = new ArrayList();
		ArrayList parameterValues = new ArrayList();
		ArrayList parameter = new ArrayList();
        ArrayList weightValues = new ArrayList();

		//Pick out the aspect of the model parameters to be considered
		for (int a=0;a<(numParameters);a++)
		{
			FuzzyAttributedNode testNode = (FuzzyAttributedNode)countModel.getNodes().get(a);
				parameterRow.add(testNode.getInputoutput().getSampleSet().iterator().next());
                parameterRow.add(testNode.getDatatype().getSampleSet().iterator().next());
                //Parameter might not have a unit. If it doesn't, set unit as an empty string
                try { parameterRow.add(UnitAnalyzer.getDimensionString((String)testNode.getUnit().getSampleSet().iterator().next())); }
                catch(NullPointerException e)
                { parameterRow.add(""); }
                weightValues.add(new Integer(testNode.getWeight()));
			parameterValues.add((a),parameterRow.clone());
			parameterRow.clear();
			parameter.clear();
		}
		countParameters(parameterValues, weightValues, numParameters);
        return modelData;
	}

	public void countParameters(ArrayList parameterValues,ArrayList weightValues, int numParameters)
	{
        int count;
		int num = 0;
        int weight = 0;
		//Start storing each value and the number of times it occurs in the model
		for (int j=0;j<numParameters;j++)
		{
			//Check if parameter value has already been counted
			if (!(parameterValue.contains(parameterValues.get((j)))))
			{
				//If this is a new parameter value, count the number of times it occurs
				parameterValue.add(num,parameterValues.get((j)));
                count=1;
				for (int k=0;k<numParameters;k++)
				{
					if (j!=k)
					{
						if (parameterValues.get((j)).equals(parameterValues.get((k))))
							count++;
					}
				}
                if (weightValues.size()!=0)
                {
                    weight = ((Integer)weightValues.get(j)).intValue();
                    modelData.newEntry((ArrayList)parameterValues.get(j),count,weight);
                }
                else
                    modelData.newEntry((ArrayList)parameterValues.get(j),count);
				num++;
			}
		}
	}
}
