package mit.cadlab.dome3.integrationwizards.directmethod;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;
import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.SimilarityMeasures;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 8, 2006
 * Time: 4:35:20 PM
 * To change this template use Options | File Templates.
 */
//todo: consider special case units when different log10 based dim matching isn't appropriate
public class ParameterSimilarity
{
    //This similarity method is for the direct mapping algorithm
    public static double calculateNodeSimilarity(FuzzyAttributedNode aNode, FuzzyAttributedNode bNode)
    {
        double sim =0;
        String aType = (String)aNode.getDatatype().getSampleSet().iterator().next();
        String bType = (String)bNode.getDatatype().getSampleSet().iterator().next();
        if(aType.equalsIgnoreCase(bType))
        {
            String aName = (String)aNode.getName().getSampleSet().iterator().next();
            String bName = (String)bNode.getName().getSampleSet().iterator().next();

            if(aType.equalsIgnoreCase("File")||aType.equalsIgnoreCase("Enumeration")||aType.equalsIgnoreCase("Vector"))
                sim = SimilarityMeasures.StringMatching_TFIDF_matching(aName,bName);
            else
            {
                String aUnit = (String)aNode.getUnit().getSampleSet().iterator().next();
                String bUnit = (String)bNode.getUnit().getSampleSet().iterator().next();
                String aDim = aNode.getDim().getSampleSet().iterator().next().toString();
                Double bDim;
                try  {bDim = new Double(bNode.getDim().getSampleSet().iterator().next().toString());}
                catch(ClassCastException e) {bDim = new Double(0.0000000000000000001);}
                double dimSim = SimilarityMeasures.calculateUnitDimensionSimilarity(aUnit,bUnit,aDim,bDim);
                if (dimSim>0.91)
                {
                    double nameSim = SimilarityMeasures.StringMatching_TFIDF_matching(aName,bName);
                    sim = (dimSim+nameSim)/2;
                }
            }
        }
        //System.out.println(aName);
        //System.out.println(bName);
	    //System.out.println("Similarity Score = " + sim);
	    return sim;
    }

    //This similarity method is for the graph matching algorithm
    public static double calculateParameterSimilarity(FuzzyAttributedNode aNode, FuzzyAttributedNode bNode)
    {
        double sim=0;
        String aType = (String)aNode.getDatatype().getSampleSet().iterator().next();
        String bType = (String)bNode.getDatatype().getSampleSet().iterator().next();
        if(aType.equalsIgnoreCase(bType))
        {
            if(aType.equalsIgnoreCase("File")||aType.equalsIgnoreCase("Enumeration")||aType.equalsIgnoreCase("Vector"))
                sim = 1;
            else
            {
                String aName  = (String)aNode.getName().getSampleSet().iterator().next();
                String bName =  (String)bNode.getName().getSampleSet().iterator().next();
                String aUnit = (String)aNode.getUnit().getSampleSet().iterator().next();
                String bUnit = (String)bNode.getUnit().getSampleSet().iterator().next();
                //double unitSim = SimilarityMeasures.unitMatching(aUnit,bUnit);
                String aDim = aNode.getDim().getSampleSet().iterator().next().toString();
                Double bDim;
                try  {bDim = new Double(bNode.getDim().getSampleSet().iterator().next().toString());}
                catch(ClassCastException e) {bDim = new Double(0.0000000000000000001);}
                double dimSim = SimilarityMeasures.calculateUnitDimensionSimilarity(aUnit,bUnit,aDim,bDim);
                //sim = (dimSim+unitSim)/2;
                sim = dimSim;
            }
        }
        return sim;
    }

    //This parameter difference method is for the clustering algorithm
    public static double calculateParameterDifference(FuzzyAttributedNode aNode, FuzzyAttributedNode bNode)
    {
        double diff=0;
        String aType = (String)aNode.getDatatype().getSampleSet().iterator().next();
        String bType = (String)bNode.getDatatype().getSampleSet().iterator().next();
        if(aType.equalsIgnoreCase(bType))
        {
            if(aType.equalsIgnoreCase("File")||aType.equalsIgnoreCase("Enumeration")||aType.equalsIgnoreCase("Vector"))
                diff = 0;
            else
            {
                Double aDim = new Double(aNode.getDim().getSampleSet().iterator().next().toString());
                Double bDim;
                try  {bDim = new Double(bNode.getDim().getSampleSet().iterator().next().toString());}
                catch(ClassCastException e) {bDim = new Double(0.0000000000000000001);}
                diff = SimilarityMeasures.logDifference(aDim.doubleValue(),bDim.doubleValue());
            }
        }
        return diff;
    }

    //This is used by the mit.cadlab.dome3.search.datastructure.graph matching algorithm, the name similarity is used for bipartite matching and not the mit.cadlab.dome3.search.datastructure.graph similarity score
    public static double addNameSimilarity(FuzzyAttributedNode aNode, FuzzyAttributedNode bNode, double simScore)
    {
        if (simScore!=0)
        {
            String aName = (String)aNode.getName().getSampleSet().iterator().next();
            String bName = (String)bNode.getName().getSampleSet().iterator().next();
            double nameSim = SimilarityMeasures.StringMatching_TFIDF_matching(aName,bName);
            simScore = (simScore+nameSim)/2;
        }
        return simScore;
    }

    //This method uses the aggregate scaling factor to recalculate the scaled mit.cadlab.dome3.search.datastructure.graph similarity score
    public static double calculateScaledNodeSimilarity(FuzzyAttributedNode aNode, FuzzyAttributedNode bNode, double scale)
    {
        String aUnit = (String)aNode.getUnit().getSampleSet().iterator().next();
        String bUnit = (String)aNode.getUnit().getSampleSet().iterator().next();
        String aDim = aNode.getDim().getSampleSet().iterator().next().toString();
        Double bDim;
        try  {bDim = (Double)bNode.getDim().getSampleSet().iterator().next();}
        catch (ClassCastException e) {bDim = new Double(0);}
        aDim = String.valueOf(Double.parseDouble(aDim)*scale);
        return SimilarityMeasures.calculateUnitDimensionSimilarity(aUnit,bUnit,aDim,bDim);
    }
}
