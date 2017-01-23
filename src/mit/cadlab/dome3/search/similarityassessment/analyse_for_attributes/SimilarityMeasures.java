package mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes;

import com.wcohen.secondstring.JaroWinklerTFIDF;
import com.wcohen.secondstring.JaroWinkler;
import com.wcohen.secondstring.TFIDF;
import mit.cadlab.dome3.search.datastructure.FuzzySet;

import java.util.Set;
import java.util.Iterator;

/**
 * this class provide a bundle of similarity measures for estimating the similarity between Strings, DataTypes, Units and etc
 */
public class SimilarityMeasures {
    /** Inexact Matching
     * This method uses W.W.Cohen's Java package "SecondString" to do name matching, based on a hybrid
     * scheme combining a TFIDF weighting scheme with the Jaro-winkler string-similarity scheme.
     * @param str1
     * @param str2
     * @return
     */
    public static double StringMatching_TFIDF_matching(String str1, String str2) {
        //Qing: fixed a bug here:
        //the length of the two strings matters, for example: ("a longer str", "str") > 1, ("str", "a longer str")<1

        double score=0.0;
        JaroWinklerTFIDF  algorithm = new JaroWinklerTFIDF();
        if(str1.length()>=str2.length())
          score= algorithm.score(str2, str1);
        else
          score= algorithm.score(str1, str2);

        return score;
    }

    /**
     * this unit matching function should only be called for those numeric values don't have a scaleN(matrix and vectors), comparing their units(magnitude=1)
     * for Real and Integer, should be comparing their scaleN, since scaleN is scale to baseUnit say
     *   1km--> 1000 m;  2mm---> 0.002 m
     * then the unitMatching should return  1000 compared to 0.002
     *
     *
     * Unit Matching goes like this:
     * 1: if the two unit are the same, they are consider a similarity score of 1.0;
     * todo:think about how to solve when a no-unit is matched to unit ? 2: if any of the unit has a "No_Unit", we are not furthur comparing them, instead, just give it a very small similarity score, the final comparion will mostly based on name similarity
     * 3: if the two unit are dimensional different, return 0.0;
     * 4: if the two unit are all dimensionless units, if they are differnt, return 0.0;
     * 5: for the rest cases, combining scale.
     * @param sObj
     * @param tObj
     * @return
     */
    public static double unitMatching(String sObj, String tObj) {
        if (sObj == null || tObj == null) return 0;//dimensionless

        if (sObj.equals("No_Unit") && tObj.equals("No_Unit"))
            return 1.0E-9 ;

        if (sObj.equalsIgnoreCase(tObj))
            return 1.0;

        if (sObj.equals("No_Unit") || tObj.equals("No_Unit"))
            return //1.0E-9;  //return a tiny tiny non-zero number,in this case, will rely on name matching.
                    0;

        Integer sDim = UnitAnalyzer.getDimensionNumber(sObj);
        Integer tDim = UnitAnalyzer.getDimensionNumber(tObj);
        if (!sDim.equals(tDim))
            return 0.0; //dimensionly not compatible

        //if both are all dimensionless parameters, the unit need to be same to be compatible
        if (sDim.doubleValue() == 0 && tDim.doubleValue() == 0)
            return 0.0;

        double sScale = UnitAnalyzer.scaleToBase(1.0, sObj).doubleValue();
        double tScale = UnitAnalyzer.scaleToBase(1.0, tObj).doubleValue();

        double sim = order_of_mag_Matching(sScale, tScale);

       // System.out.println("Unit Matching: similarity from " + sObj + " to " + tObj + " is " + sim);
        return sim;
    }

    /**
     * the order of magnitude use a exponential function to map the value into a [0,1] value range
     * simlairy=exponent(0.5,abs(x));
     *            |
     *           *|*
     *         *  |  *
     * ---*-------|-------*--------
     * @param obj1
     * @param obj2
     * @return
     */
    public static double order_of_mag_Matching(Object obj1, Object obj2) {
        if (obj1 instanceof Number && obj2 instanceof Number) {
            return order_of_mag_Matching(((Number) obj1).doubleValue(), ((Number) obj2).doubleValue());
        }
        return 0.0;
    }

    //Changed by Ligon to linear distribution of difference in magnitude
    public static double order_of_mag_Matching(double obj1, double obj2) {
        if (obj1 == obj2)
            return 1.0;
        else {
            if(obj1<0 && obj2<0){
                obj1 = Math.abs(obj1);
                obj2 = Math.abs(obj2);
            }
            else if(obj1<0 || obj2<0)
                return 0;

            double difference = Math.abs(logDifference(obj1,obj2));       //order of magnitude
            if (difference>10)
                return 0;
            return 1-0.1*(difference);
        }
    }

    public static double logDifference(double obj1, double obj2)
    {
        return log10(obj1)-log10(obj2);
    }

    // log10: Logarithm base 10
    public static double log10(double d) {
        if (d==0)
            d = 1e-10;
        return Math.log(d) / Math.log(10.0);
    }


    /**
     * Qing: Membership Function Matrix

     *           Real	Integer	Boolean	String	Matrix	Vector  Enumeration  File
     * Real	     1	     0.75	0	    0	    0.5     0.5       0.25       0
     * Integer	 0.75	 1	    0	    0.125   0.5	    0.5       0.25       0
     * Boolean	 0	     0      1       0.75       0        0        0.25       0
     * String	 0	    0.125	0.75	    1	    0	     0        0.25       0
     * Matrix	 0.5	0.5	    0	    0	    1	    0.75        0        0
     * Vector	 0.5	0.5	    0       0	    0.75	 1	        0        0
     * Enumeration 0.25  0.25   0.25    0.25     0         0        1        0
     * File       0        0    0       0        0        0         0        1
     */
    private static final double[][] dTypeSimialrityMatrix = {
        {1, 0.75, 0, 0, 0.5, 0.5, 0.25, 0},
        {0.75, 1, 0, 0.125, 0.5, 0.5, 0.25, 0},
        {0, 0, 1, 0.75, 0, 0, 0.25, 0},
        {0, 0.125, 0.75, 1, 0, 0, 0.25, 0},
        {0.5, 0.5, 0, 0, 1, 0.75, 0, 0},
        {0.5, 0.5, 0, 0, 0.75, 1, 0, 0},
        {0.25, 0.25, 0.25, 0.25, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1},
    };

    public static double getDataTypeSimilarity(int i, int j) {
        if (i < 0 || i > 7 || j < 0 || j > 7) {
            System.out.println("Error in getting data type similarity--index out of bound, should be between 0~6");
            return 0;
        } else
            return dTypeSimialrityMatrix[i][j];
    }


    /**
     *
     *
     *
     * @param template_node_datatype
     * @param iface_node_datatype
     * @return
     */
    public static double datatype_matching(String template_node_datatype, String iface_node_datatype) {

        int i = isValidDataType(template_node_datatype);
        int j = isValidDataType(iface_node_datatype);
        if (i == -1 || j == -1) {
            //    if(i==-1)  System.out.println("Error in data type matching--invalid data type "+sObj);
            //     if(j==-1)  System.out.println("Error in data type matching--invalid data type "+tObj);
            return 0;
        }
        return getDataTypeSimilarity(i, j);
    }

    public final static String REAL = "Real";
    public final static String INTEGER = "Integer";
    public final static String BOOLEAN = "Boolean";
    public final static String STRING = "String";
    public final static String VECTOR = "Vector";
    public final static String MATRIX = "Matrix";
    public final static String ENUM = "Enumeration";
    public final static String FILE = "File";
    public final static String ITER = "IterativeVariable";


    private static int isValidDataType(String dtype) {
        if (dtype.equalsIgnoreCase(REAL))
            return 0;
        else if (dtype.equalsIgnoreCase(INTEGER))
            return 1;
        else if (dtype.equalsIgnoreCase(BOOLEAN))
            return 2;
        else if (dtype.equalsIgnoreCase(STRING))
            return 3;
        else if (dtype.equalsIgnoreCase(VECTOR))
            return 4;
        else if (dtype.equalsIgnoreCase(MATRIX))
            return 5;
        else if (dtype.equalsIgnoreCase(ENUM))
            return 6;
        else if (dtype.equalsIgnoreCase(FILE))
            return 7;
        else
            return -1;
    }

    /**
     *
     * @param sObj
     * @param tObj
     * @return  0.0: both have
     */
    public static boolean DimensionMatching(String sObj, String tObj) {
        if (sObj == null && tObj == null) return true; //both are non- dimensional

        else if(sObj==null ||tObj==null) return false; //one of them are non-dimensional

        if (sObj.equals("No_Unit") && tObj.equals("No_Unit")) //dimensionless
            return true;

        if (sObj.equalsIgnoreCase(tObj))
            return true;


        Integer sDim = UnitAnalyzer.getDimensionNumber(sObj);
        Integer tDim = UnitAnalyzer.getDimensionNumber(tObj);
        if (!sDim.equals(tDim))  return false; //dimensionly not compatible
        return true;
    }

	//New Unit&Dimension Similarity Measure **Added by Ligon**
	public static double calculateUnitDimensionSimilarity(String aUnit, String bUnit, String aDim, Double bDim)
	{
		double result = 0;
		if (aUnit.equals("No_Unit") && bUnit.equals("No_Unit"))
        {
            double aaDim = Double.parseDouble(aDim);
            double bbDim = bDim.doubleValue();
            result = order_of_mag_Matching(aaDim,bbDim);
            return result;
        }
		if (aUnit.equals("No_Unit") || bUnit.equals("No_Unit"))
            return  0;
		if (aUnit.equalsIgnoreCase(bUnit))
		{
			double aDoubleDim = Double.parseDouble(aDim);
			double bDoubleDim = Double.parseDouble(bDim.toString());
            result = order_of_mag_Matching(aDoubleDim,bDoubleDim);
		}
		else if(UnitAnalyzer.getDimensionString(aUnit).equalsIgnoreCase(UnitAnalyzer.getDimensionString(bUnit)))
		{
			double aDoubleDim = Double.parseDouble(aDim);
			double bDoubleDim = bDim.doubleValue();
            Double scaledTemplate = UnitAnalyzer.scaleToBase(aDoubleDim, aUnit);
			Double scaledInterface = UnitAnalyzer.scaleToBase(bDoubleDim, bUnit);
			result = order_of_mag_Matching(scaledTemplate.doubleValue(),scaledInterface.doubleValue());
        }
		return result;
	}
}
