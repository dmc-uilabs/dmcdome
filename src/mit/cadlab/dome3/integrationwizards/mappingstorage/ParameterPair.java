package mit.cadlab.dome3.integrationwizards.mappingstorage;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 18, 2006
 * Time: 6:05:51 PM
 * This class contiains an indivdual mapping. The FuzzyAttributedNodes for each of the two models are contained here.
 * The columnNode is the mapped node that is contained in the model that corresponds to the column index in the mapping matrix
 * The rowNode is the mapped node that is contained in the model that corresponds to the row index in the mapping matrix
 * You must also designate whether they pair is an input-input mapping or an input-output mapping
 * The similarity score for the mapping pair is also stored here for use in the direct method.
 */
public class ParameterPair
{
	private FuzzyAttributedNode columnNode;
	private FuzzyAttributedNode rowNode;
	private double similarityScore;
	private boolean finalized;
    private String pairType;
    private JCheckBox checkBox;     //Used when user is accepting or rejecting mapping

	public ParameterPair(FuzzyAttributedNode columnNode,FuzzyAttributedNode rowNode,double simScore, String pairType)
	{
		this.rowNode = rowNode;
        this.columnNode = columnNode;
		this.similarityScore = simScore;
        this.finalized = false;
        this.pairType = pairType;
	}

    public String getPairType()
    {
        return pairType;
    }

    public FuzzyAttributedNode getInputNode()
    {
        if(((String)(columnNode.getInputoutput().getSampleSet().iterator().next())).equalsIgnoreCase("input"))
            return columnNode;
        else
            return rowNode;
    }

    public FuzzyAttributedNode getRowNode()
	{
		return rowNode;
	}

	public FuzzyAttributedNode getColumnNode()
	{
	    return columnNode;
	}

	public double getSimScore()
	{
		return similarityScore;
	}

	public boolean status()
	{
		return finalized;
	}

	public void finalize()
	{
		finalized = true;
	}

    public void addCheckBox(JCheckBox checkBox)
    {
        this.checkBox =  checkBox;
    }

    public boolean checkBoxStatus()
    {
        if(checkBox!=null)
            return checkBox.isSelected();
        return false;
    }
}
