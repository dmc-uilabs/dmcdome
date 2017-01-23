package mit.cadlab.dome3.integrationwizards.directmethod;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import java.util.ArrayList;

import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 18, 2006
 * Time: 5:09:00 PM
 * To change this template use Options | File Templates.
 */
public class DirectMethod
{

	public static MappingMatrix directMethod(ArrayList objectiveModels,int toleranceLevel)
	{
        int numModels = objectiveModels.size();
        MappingMatrix mapMatrix = new MappingMatrix(numModels);
		CompareParameters compare = new CompareParameters();
        for (int firstIndex=0;firstIndex<numModels;firstIndex++)
        {
        	for (int secondIndex=(firstIndex+1);secondIndex<numModels;secondIndex++)
			    mapMatrix.setEntry(firstIndex,secondIndex,compare.compareParameters((FuzzyARG)objectiveModels.get(firstIndex),(FuzzyARG)objectiveModels.get(secondIndex)));
        }
		SelectMapping select = new SelectMapping(mapMatrix,objectiveModels,numModels,toleranceLevel);
		select.selectMapping("inputoutput");
		select.selectMapping("inputinput");
        return select.finalMappingMatrix;
	}
}
