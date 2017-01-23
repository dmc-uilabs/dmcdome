package mit.cadlab.dome3.integrationwizards.mappingstorage;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 20, 2006
 * Time: 5:24:53 PM
 * This mapping matrix stores mappings based on the indicies of the models in the objectiveModels arraylist that are mapped together.
 * If there are 5 objective models. The mapping matrix would look like the following example. The stars indicate where entries exist.
 *
 *     0  1  2  3  4  5
 *  0
 *  1 *
 *  2 *  *
 *  3 *  *  *
 *  4 *  *  *  *
 *  5 *  *  *  *  *
 *
 * Each entry is a ModelMapping class which contains all of the mappings between two models
 */
public class MappingMatrix
{
	private ArrayList masterArray;
    private int row;
    private int column;

	public MappingMatrix(int numModels)
	{
		this.masterArray = new ArrayList(numModels-1);
		for (int i=0;i<(numModels-1);i++)
		{
			ArrayList subArray = new ArrayList(numModels);
			for (int k=0;k<numModels;k++)
			{
				subArray.add(k,"null");
			}
			masterArray.add(subArray.clone());
			subArray.clear();
		}
	}

	public void setEntry(int firstModelIndex, int secondModelIndex,ModelMapping Map)
	{
        setRowColumn(firstModelIndex,secondModelIndex);
        if(row<column){
            int oldRow = column;
            row = column;
            column = oldRow;
        }
        ((ArrayList)masterArray.get(column)).set(row,Map);
	}

	public ModelMapping getEntry(int firstModelIndex, int secondModelIndex)
	{
        setRowColumn(firstModelIndex,secondModelIndex);
		if(((ArrayList)masterArray.get(column)).get(row)!="null")
		{
			ModelMapping modelMap = (ModelMapping)((ArrayList)masterArray.get(column)).get(row);
			return modelMap;
		}
		return null;
	}

    public void addParameterPair(int columnModelIndex, int rowModelIndex, ParameterPair pair, String inout)
    {
        setRowColumn(columnModelIndex,rowModelIndex);
        ModelMapping mappings = getNewEntry(column,row);
        mappings.addParameterPair(pair,inout);
        setEntry(column,row,mappings);
    }

    public void removeParameterPair(int firstModelIndex, int secondModelIndex,ParameterPair pair, String inout)
    {
        setRowColumn(firstModelIndex,secondModelIndex);
        ModelMapping mappings = getNewEntry(column,row);
        mappings.removeParameterPair(pair,inout);
        setEntry(column,row,mappings);
    }

    private ModelMapping getNewEntry(int firstModelIndex, int secondModelIndex)
    {
        setRowColumn(firstModelIndex,secondModelIndex);
        Object entry = getEntry(column,row);
        if(entry instanceof ModelMapping)
            return (ModelMapping)entry;
        else
            return new ModelMapping(new ArrayList(), new ArrayList());
    }

    public int numModels(){
        return masterArray.size()+1;
    }

    private void setRowColumn(int firstIndex, int secondIndex){
        if(firstIndex>secondIndex){
            row = firstIndex;
            column = secondIndex;
        }
        else if(secondIndex>firstIndex){
            row = secondIndex;
            column = firstIndex;
        }
    }
}

