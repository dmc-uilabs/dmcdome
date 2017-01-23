package mit.cadlab.dome3.integrationwizards.directmethod;

import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ParameterPair;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ModelMapping;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 21, 2006
 * Time: 2:23:31 PM
 * To change this template use Options | File Templates.
 */
public class TraceCausality
{
	private static MappingMatrix finalMappingMatrix;
	private static ArrayList objectiveModels;
	private static int numModels;

	public static boolean traceCausality(MappingMatrix finalMapMatrix,ArrayList objModels,ParameterPair checkPair,int numModel, int columnIndex, int rowIndex)
	{
		boolean linked = false;
		finalMappingMatrix = finalMapMatrix;
		objectiveModels = objModels;
		numModels = numModel;
		ArrayList nodeAOutputList = new ArrayList();
		ArrayList nodeBOutputList = new ArrayList();
		FuzzyAttributedNode inputNodeA = checkPair.getColumnNode();
		FuzzyAttributedNode inputNodeB = checkPair.getRowNode();
		FuzzyARG objModelA = (FuzzyARG)objectiveModels.get(columnIndex);
		FuzzyARG objModelB = (FuzzyARG)objectiveModels.get(rowIndex);

        nodeAOutputList = traceInputParameterCausality(inputNodeA,objModelA,columnIndex);
		if(rowIndex==numModels)
			rowIndex--;
        nodeBOutputList = traceInputParameterCausality(inputNodeB,objModelB,rowIndex);
        linked = findOutputMatches(nodeAOutputList,nodeBOutputList);
		return linked;
	}

	//Traces an input node through it's causality tree and collects all of the output nodes it effects
	private static ArrayList traceInputParameterCausality(FuzzyAttributedNode inputNode,FuzzyARG objModel,int columnIndex)
	{
		ArrayList outputNodesEffectedList = new ArrayList();
		ArrayList outputNodeEntry = new ArrayList(2);
		Map arcsA = objModel.getArcs();
		List outputNodeList = (List)arcsA.get(inputNode);

		if(outputNodeList!=null)
		{
			for (int i=0;i<outputNodeList.size();i++)
			{
				FuzzyAttributedNode outputNode = (FuzzyAttributedNode)outputNodeList.get(i);
				outputNodeEntry.add(0,outputNode);
				outputNodeEntry.add(1,objModel);
				outputNodesEffectedList.add(outputNode);
				//This for loop runs the 'column' of the finalMappingMatrix
				for(int k=(columnIndex+1);k<numModels;k++)
					outputNodesEffectedList = beginTrace(outputNodesEffectedList,outputNode,columnIndex,k);
				//This for loop funs the 'row' of the finalMappingMatrix
				for(int k=1;k<columnIndex;k++)
					outputNodesEffectedList = beginTrace(outputNodesEffectedList,outputNode,k,columnIndex);
			}
		}
		return outputNodesEffectedList;
	}

	private static ArrayList beginTrace(ArrayList outputNodesEffectedList,FuzzyAttributedNode outputNode,int columnIndex,int rowIndex)
	{
		ArrayList mappedInputs;
		ArrayList newOutputNodesEffected;
		FuzzyARG newObjModel;
		FuzzyAttributedNode newInputNode;
		mappedInputs = findMappedInputs(outputNode,columnIndex,rowIndex);
		if(mappedInputs.size()==0);
		else
		{
			newObjModel = (FuzzyARG)objectiveModels.get(rowIndex);
            for(int j=0;j<mappedInputs.size();j++)
            {
	            newInputNode = (FuzzyAttributedNode)mappedInputs.get(j);
                newOutputNodesEffected = traceInputParameterCausality(newInputNode,newObjModel,rowIndex);
		        outputNodesEffectedList.addAll((outputNodesEffectedList.size()-1),newOutputNodesEffected);
            }
		}
		return outputNodesEffectedList;
	}

	private static ArrayList findMappedInputs(FuzzyAttributedNode outputNode,int columnIndex,int rowIndex)
	{
		ArrayList mappedInputs = new ArrayList();
		ModelMapping modelMapping = finalMappingMatrix.getEntry(columnIndex,rowIndex);
		if(modelMapping!=null)
		{
			ArrayList outputParameterMapping = modelMapping.getOutputParameterMapping();
			for(int i=0;i<outputParameterMapping.size();i++)
			{
				ParameterPair paraPair = (ParameterPair)outputParameterMapping.get(i);
				if(paraPair.getColumnNode()==outputNode)
				{
                	mappedInputs.add(paraPair.getRowNode());
				}
			}
		}
     	return mappedInputs;
	}

	//This Method checks if the two input nodes share a common output in their output causality list
	//Returns true if a match is found
	private static boolean findOutputMatches(ArrayList nodeAOutputList,ArrayList nodeBOutputList)
	{
		boolean foundMatch = false;
		for (int nodeAIndex=0;nodeAIndex<nodeAOutputList.size();nodeAIndex++)
		{
			for(int nodeBIndex=0;nodeBIndex<nodeBOutputList.size();nodeBIndex++)
			{
				if(nodeAOutputList.get(nodeAIndex).equals(nodeBOutputList.get(nodeBIndex)))
				{
					foundMatch = true;
					break;
				}
			}
		}
        return foundMatch;
	}
}
