package mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;
import edu.iupui.rg.ucum.units.Dimension;
import mit.cadlab.dome3.search.framework.searchagent.search.SearchInit;

/**
 * User: Qing
 * Date: Aug 17, 2005
 * Time: 4:59:36 PM
 * To analyze a unit of its basic dimension, and represent it as a DimInteger
 * and analyze its scale
 *
 * the Dimension integer:
 * Unit Vector as defined in UCUM
 * Index	Quantity	    Unit
 *     0 	length L	    meter
 *     1	mass	        gram
 *     2	time t	        second
 *     3	plane angle	    radian  (instead of molecular substance in SI)
 *     4	temperature	    Kelvin
 *     5	electric charge	Coulomb  (instead of electric current in SI)
 *     6	luminosity	    candela
 *     7	money	        dollar
 *
 * to transform the unit vector into a basic dimension integer, we need a
 * dimsize, the powers of base quantities.the dimsizes = [20 20 20 10 10 10 10 10];
 * the representation allows a power of +_(dimsize/2-1) for each quantity.
 * For example: dimsize[0] is 20, corresponding to a field size of 20 and
 * an allowable value range of +-9.
 */
public class UnitAnalyzer
{
	//dimension size vector
	//(length,	mass,	time,	plane angle,	temperature,	electric change,	luminous intensity,	dollar)
	private static final double[] dimSize = {20, 20, 20, 10, 10, 10, 10, 10};

	//Second, define another 8-vector dimVal
	// where the value is determined by :
	//	dimval[0]=1
	//	dimval[i]=dimval[i-1]*dimsize[i-1],
	private static final double[] dimVal = {1, 20, 400, 8000, 80000, 800000, 8000000, 80000000};

    private static final double dimBias=444444210;
	/**
	 * Return the Demension Definition in UCUM
	 * @param unit
	 * @return
	 */
	public static String getDimensionString(String unit)
	{
		//double check whether the units has been loaded or not
		if (!mit.cadlab.dome3.search.framework.searchagent.search.SearchInit.isInitialized())
            mit.cadlab.dome3.search.framework.searchagent.search.SearchInit.initialize();

		Unit s_unit = UnitAtom.getUnit(unit);
		if (s_unit == null) {
			System.out.println("Error in UnitAnalyzer:getting dimension for " + unit + " no information about this unit");
			return "";
		}
		return UnitAtom.getUnitCategory(unit);
	}

	public static Dimension getDimension(String unit)
	{
		//double check whether the units has been loaded or not
		if (!SearchInit.isInitialized()) SearchInit.initialize();
		Unit s_unit = UnitAtom.getUnit(unit);
		if (s_unit == null) {
			System.out.println("Error in UnitAnalyzer:getting dimension for " + unit + " no information about this unit");
			return null;
		}
		Dimension dim_vec = s_unit.getUvec();
		//in DOME implementation dimension vector is a 10 element vector of
		//(length,	mass,	time,	plane angle,	temperature,	electric change,	luminous intensity,	dollar, optimazation individual,optimazation objective)
		//we will get the first 8 elements out to calculate the dimInteger
		return dim_vec;

	}

	public static Integer getDimensionNumber(String unit)
	{
		//double check whether the units has been loaded or not
		if (!SearchInit.isInitialized()) SearchInit.initialize();
		Unit s_unit = UnitAtom.getUnit(unit);
		if (s_unit == null) {
			System.out.println("Error in UnitAnalyzer:getting dimension for " + unit + " no information about this unit");
			return null;
		}
		Dimension dim_vec = s_unit.getUvec();
		//in DOME implementation dimension vector is a 10 element vector of
		//(length,	mass,	time,	plane angle,	temperature,	electric change,	luminous intensity,	dollar, optimazation individual,optimazation objective)
		//we will get the first 8 elements out to calculate the dimInteger

		Integer dimV = calculateDimensionNumber(dim_vec);
		return dimV;
	}

	/**
	 * calcuate dimension number from dimension vector
	 * @param dim_vec
	 * @return
	 */
	private static Integer calculateDimensionNumber(Dimension dim_vec)
	{
		double dimN = 0;
		for (int i = 0; i < dimVal.length; i++) {
			dimN = dimN + dimVal[i] * dim_vec.elementAt(i);
		}
		return new Integer((new Double(dimN)).intValue());
	}

	/**
	 * original algorithm in Gordon S. Novak, Jr., "Conversion of Units of Measurement". IEEE Transcation on Software Engineering, vol 21, no. 8, 1995, pp 651-661
	 * integer m, sz, mm;
             m := n + dimbias;
      for i := 0 to 7 do
	   begin
	     sz := dimsizes[i];
	     mm := m / sz;
	     v[i] := (m - mm * sz) - sz / 2;
	     m := mm
	   end;
	 end;

	 * @param dimensionN
	 * @return
	 */
	public static Dimension backoutDimensionVector(Integer dimensionN)
	{
		if (dimensionN == null) return null;
		Dimension DIM = new Dimension();
		double value = dimensionN.doubleValue()+dimBias;
		for (int i =0;i< dimVal.length; i++) {
                double m_value=value/dimSize[i];
			    m_value=Math.floor(m_value);
			    double v=value-m_value*dimSize[i]-dimSize[i]/2;
				DIM.setElementAt(i, v);
				value = m_value;
	   }
		return DIM;
	}

    public static Double scaleToBase(double mu, String unit){
     //double check whether the units has been loaded or not
	    if (!SearchInit.isInitialized()) SearchInit.initialize();
	    Unit s_unit = UnitAtom.getUnit(unit);
	    if (s_unit == null) {
		    System.out.println("Error in UnitAnalyzer:getting dimension for " + unit + " no information about this unit");
		    return null;
	    }

	    Dimension u_vec = s_unit.getUvec();
        String name="";
        // build a name as a term of coherent base units
		for (int i = 0; i < Dimension.getMax(); i++) {
			double e = u_vec.elementAt(i);
			Unit u = UnitAtom.forDimension(new Dimension(i));
			if (u == null)
				throw new IllegalArgumentException("can't find base unit for dimension " + i);
			name =name+ u.getName() + e;
		}
	    //create a base unit of nu = 1;	cnv = null; cnv_pfx = 1;
        Unit baseUnit=new Unit(name,1,u_vec,null,1);

	    return new Double(s_unit.convertTo(mu,baseUnit));
    }


}