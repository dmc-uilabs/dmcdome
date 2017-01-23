package mit.cadlab.dome3.integrationwizards.directmethod;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;

import java.util.ArrayList;

import mit.cadlab.dome3.integrationwizards.mappingstorage.ModelMapping;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ParameterPair;
import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 18, 2006
 * Time: 5:36:05 PM
 * To change this template use Options | File Templates.
 */
public class SelectMapping
{
	public MappingMatrix finalMappingMatrix;
	private ArrayList duplicateMappings = new ArrayList();
	private MappingMatrix mappingMatrix;
	private ArrayList objectiveModels;
	private int numModels;
    private double maxTolerance;
    private double normTolerance;

	public SelectMapping(MappingMatrix mappingsMatrix,ArrayList objModels, int numModel,int toleranceLevel)
	{
		this.mappingMatrix = mappingsMatrix;
		this.numModels = numModel;
		this.finalMappingMatrix = new MappingMatrix(numModels);
		this.objectiveModels = objModels;
        setTolerances(toleranceLevel);
	}

    public void selectMapping(String type)
    {
         for(int i=0;i<(numModels-1);i++)
         {
	         for(int k=(i+1);k<numModels;k++)
	         {
                 ModelMapping modelPossibleMapping = mappingMatrix.getEntry(i,k);
                 if (modelPossibleMapping!=null)
                 {
                     if(type=="inputoutput")
                         selectInputOutputMapping(modelPossibleMapping,i,k);
                     else if(type=="inputinput")
                         selectInputInputMapping(modelPossibleMapping,i,k);
                 }
	         }
         }
    }

	//this method choses which input-output parameter mapping should be implemented
	private void selectInputOutputMapping(ModelMapping modelPossibleMapping,int columnIndex,int rowIndex)
	{
		if(modelPossibleMapping.getOutputParameterMapping()!=null)
		{
            ArrayList modelOutputMapping = modelPossibleMapping.getOutputParameterMapping();
			for(int pairIndex=0;pairIndex<modelOutputMapping.size();pairIndex++)
			{
				ParameterPair currentParameterPair = (ParameterPair)modelOutputMapping.get(pairIndex);
				if(!currentParameterPair.status())
				{
					FuzzyAttributedNode inputNode = currentParameterPair.getInputNode();
                    int inputNodeModelIndex;
                    int currentModelIndex;
                    if(inputNode==currentParameterPair.getColumnNode()){
                        inputNodeModelIndex = columnIndex;
                        currentModelIndex = rowIndex;}
                    else{
                        inputNodeModelIndex = rowIndex;
                        currentModelIndex = columnIndex;}
                    if(findDuplicateMapping(inputNode,currentModelIndex,inputNodeModelIndex))
                    {
						System.out.println("I found a duplicate SubscriptionMapping");
                        selectFinalMapping(currentParameterPair,columnIndex,rowIndex);
                    }
					else
                    {
	                    currentParameterPair.finalize();
					    finalMappingMatrix = insertPairInToFinalMatrix(finalMappingMatrix, currentParameterPair,columnIndex,rowIndex);
                    }
				}
			}
		}
	}

	//This method choses which input-input parameter pairs should be implemented
	private void selectInputInputMapping(ModelMapping modelPossibleMapping,int columnIndex,int rowIndex)
	{
        if(modelPossibleMapping.getInputParameterMapping()!=null)
		{
            ArrayList modelInputMapping = modelPossibleMapping.getInputParameterMapping();
			for(int j=0;j<modelInputMapping.size();j++)
			{
				ParameterPair currentParameterPair = (ParameterPair)modelInputMapping.get(j);
				if(!currentParameterPair.status())
				{
                    double score = currentParameterPair.getSimScore();
                    if(!outputMapped(columnIndex,rowIndex,currentParameterPair) && score>=normTolerance)
                    {
                        if(score>=maxTolerance)
                        {
                            finalMappingMatrix = insertPairInToFinalMatrix(finalMappingMatrix,currentParameterPair,columnIndex,rowIndex);
                            currentParameterPair.finalize();
                        }
                        else if(!TraceCausality.traceCausality(finalMappingMatrix,objectiveModels,currentParameterPair,numModels,columnIndex,rowIndex))
                                finalMappingMatrix = insertPairInToFinalMatrix(finalMappingMatrix,currentParameterPair,columnIndex,rowIndex);
                    }
				}
			}
		}
	}

	//This method determines which input/output node mapping pairs should be considered
	//returns true if multiple output parameters might map to the same input parameter
	private boolean findDuplicateMapping(FuzzyAttributedNode searchNode,int currentModelIndex, int inputModelIndex)
	{
		ArrayList duplicateMap = new ArrayList(3);
		boolean found = false;
    	for(int columnIndex=(currentModelIndex+1);columnIndex<(numModels-1);columnIndex++)
	    {
		    if(columnIndex!=inputModelIndex)
		    {
			    for(int rowIndex=0;rowIndex<numModels;rowIndex++)
	            {
                    ModelMapping modelMapping = mappingMatrix.getEntry(columnIndex,rowIndex);
				    if(modelMapping!=null)
				    {
						if(modelMapping.getOutputParameterMapping()!=null)
						{
							ArrayList modelOutputMapping = modelMapping.getOutputParameterMapping();
							for(int outputIndex=0;outputIndex<modelOutputMapping.size();outputIndex++)
							{
								//if another possible output parameter is found, it's location is stored in the
								//duplicateMapping ArrayList
							    if(((ParameterPair)modelOutputMapping.get(outputIndex)).getInputNode().equals(searchNode))
								{
									found = true;
									duplicateMap.add(Integer.toString(columnIndex));      //mappingMatrix column
									duplicateMap.add(Integer.toString(rowIndex));         //mappingMatrix row
									duplicateMap.add(Integer.toString(outputIndex));      //outputMapping index
									duplicateMappings.add(duplicateMap.clone());
									duplicateMap.clear();
								}
							}
						}
				    }
                }
		    }
	    }
		return found;
	}

	private void selectFinalMapping(ParameterPair checkPair, int columnIndex, int rowIndex)
	{

		for(int i=0;i<duplicateMappings.size();i++)
		{
			ArrayList duplicateMap = (ArrayList)duplicateMappings.get(i);
			int currentColumnIndex = Integer.parseInt((String)duplicateMap.get(0));
			int currentRowIndex = Integer.parseInt((String)duplicateMap.get(1));
			ModelMapping modelMapping = mappingMatrix.getEntry(currentColumnIndex,currentRowIndex);
			ParameterPair currentPair = (ParameterPair)modelMapping.getOutputParameterMapping().get(Integer.parseInt((String)duplicateMap.get(2)));
            if(currentPair.getSimScore()>checkPair.getSimScore())
            {
	            checkPair.finalize();
	            checkPair = currentPair;
	            columnIndex = currentColumnIndex;
	            rowIndex = currentRowIndex;
            }
			else
				currentPair.finalize();
		}
		checkPair.finalize();
		finalMappingMatrix = insertPairInToFinalMatrix(finalMappingMatrix,checkPair,columnIndex,rowIndex);
	}

    //It is possible a model's input parameter might map to an input parameter of another model which is already
    //mapped to an output parameter of the first model. Returns true if this is the case
    private boolean outputMapped(int columnIndex, int rowIndex,ParameterPair pair)
    {
        Object matrixEntry = finalMappingMatrix.getEntry(columnIndex,rowIndex);
        if(matrixEntry instanceof ModelMapping){
            ArrayList outputMapping = ((ModelMapping)matrixEntry).getOutputParameterMapping();
            for(int outputMappingIndex=0;outputMappingIndex<outputMapping.size();outputMappingIndex++)
            {
                FuzzyAttributedNode inputNode= ((ParameterPair)outputMapping.get(outputMappingIndex)).getInputNode();
                if(inputNode.equals(pair.getColumnNode()) || inputNode.equals(pair.getRowNode()))
                    return true;
            }
        }
        return false;
    }

	//Once a mapping pair is chosen, this method places it into the finalMappingMatrix
	public static MappingMatrix insertPairInToFinalMatrix(MappingMatrix matrix, ParameterPair finalPair, int columnIndex, int rowIndex)
	{
		if(matrix.getEntry(columnIndex,rowIndex)==null)
		{
             ModelMapping finalModelMapping = new ModelMapping(new ArrayList(),new ArrayList());
			 finalModelMapping.addParameterPair(finalPair,finalPair.getPairType());
			 matrix.setEntry(columnIndex,rowIndex,finalModelMapping);
		}
        else
		{
			ModelMapping finalModelMapping = matrix.getEntry(columnIndex,rowIndex);
			finalModelMapping.addParameterPair(finalPair,finalPair.getPairType());
			matrix.setEntry(columnIndex,rowIndex,finalModelMapping);
		}
		System.out.println("SubscriptionMapping Finalized Between Model "+columnIndex+" and Model "+rowIndex);
		System.out.print("Mapped Parameter "+finalPair.getColumnNode().getName());
        System.out.print(" and Parameter " +finalPair.getRowNode().getName());
		System.out.println();
		System.out.println();
        return matrix;
	}

    //tolerance levels can range from 1 to 4 (1 is the strictest)
    private void setTolerances(double toleranceLevel)
    {
        if(toleranceLevel>4)
            toleranceLevel = 4;
        else if(toleranceLevel<1)
            toleranceLevel = 1;
        maxTolerance = 1-0.02*toleranceLevel;
        normTolerance = 1-0.01*toleranceLevel;
    }
}
