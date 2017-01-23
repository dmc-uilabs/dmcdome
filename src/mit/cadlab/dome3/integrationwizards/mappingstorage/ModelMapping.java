package mit.cadlab.dome3.integrationwizards.mappingstorage;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 18, 2006
 * Time: 6:12:11 PM
 * This class contains two ArrayLists. The first list contains all of the input-input mappings and the second contains
 * all of the input-output mappings. Each mappings is stored in the ParameterPair class.
 */
public class ModelMapping
{
	private ArrayList inputParameterMapping;
	private ArrayList outputParameterMapping;

	public ModelMapping(ArrayList inParameterMapping, ArrayList outParameterMapping)
	{
		this.inputParameterMapping = inParameterMapping;
		this.outputParameterMapping = outParameterMapping;
	}

	public void addParameterPair(FuzzyAttributedNode aNode, FuzzyAttributedNode bNode,double simScore, String pairType)
	{
		ParameterPair nodePair = new ParameterPair(aNode,bNode,simScore,pairType);
		if (pairType=="input")
            inputParameterMapping.add(nodePair);
		else
			outputParameterMapping.add(nodePair);
	}

	public void addParameterPair(ParameterPair pair, String pairType)
	{
		if (pairType.equals("input"))
            inputParameterMapping.add(pair);
		else if(pairType.equals("output"))
			outputParameterMapping.add(pair);
	}

    public void removeParameterPair(ParameterPair pair, String pairType)
    {
        if(pairType.equals("input"))
            inputParameterMapping.remove(pair);
        else if(pairType.equals("output"))
            outputParameterMapping.remove(pair);
    }

	public ArrayList getInputParameterMapping()
	{
		return inputParameterMapping;
	}

	public ArrayList getOutputParameterMapping()
	{
		return outputParameterMapping;
	}
	public void setInputParameterMapping(ArrayList inParameterMapping)
	{
		this.inputParameterMapping = inParameterMapping;
	}
	public void setOutputParameterMapping(ArrayList outParameterMapping)
	{
		this.outputParameterMapping = outParameterMapping;
	}

}
