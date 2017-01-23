// Units.java
package mit.cadlab.dome3.util.units;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Units
{

	// Dimensions and units
	public static String NO_DIMENSION = "No Unit";
	public static String ANY_DIMENSION = "All";
	public static String NO_UNIT = "No Unit";
	public static String ANY_UNIT = "All";

	// unit systems
	public static String ANY_SYSTEM = "All";
	public static String METRIC = "metric";
	public static String SI = "SI";
	public static String CUSTOMARY = "customary";

	private static List kindsOfQuantities = null;
	private static Comparator unitAlphabetizer = new UnitAlphabetizer();

	public static Unit get(String id)
	{

		return new Unit(id);
	}

	public static List getKindsOfQuantities()
	{
		// unmodifiable list, sorted into alphabetical order
		if (kindsOfQuantities == null) {
			kindsOfQuantities = UnitAtom.getCategories();
			Collections.sort(kindsOfQuantities);
		}
		return Collections.unmodifiableList(kindsOfQuantities);
	}

	public static List getUnitsForQuantity(String kindOfQuantity,
	                                       String unitSystem)
	{
		List result = Collections.EMPTY_LIST;
		if (unitSystem == Units.ANY_SYSTEM)
			result = UnitAtom.getUnits(kindOfQuantity, "all");
		else if (unitSystem == Units.METRIC)
			result = UnitAtom.getUnits(kindOfQuantity, "metric");
		else if (unitSystem == Units.CUSTOMARY)
			result = UnitAtom.getUnits(kindOfQuantity, "nonmetric");
		Collections.sort(result, unitAlphabetizer);
		return result;
	}

	public static class UnitAlphabetizer implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			if ((o1 instanceof UnitAtom) && (o1 instanceof UnitAtom)) {
				return UnitAtom.getUnitDescription(o1.toString()).compareTo(UnitAtom.getUnitDescription(o2.toString()));
			}
			else
				throw new IllegalArgumentException("Units.UnitAlphabetizer.compare: At least one of the compared object" +
				                                   "is not a unit");
		}
	}
}
