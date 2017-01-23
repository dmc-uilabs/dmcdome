// NumericQuantity.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

//import mit.cadlab.dome3.util.units.Quantity;
//import edu.iupui.rg.ucum.units.Unit;
//import units.Units;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.util.units.Quantity;

public interface NumericQuantity extends DataObject
{

	public static final String UNIT = "unit";

	public Unit getUnit();

	public void setUnit(Unit unit);

	public void setUnit(String unitId);

	public Quantity getQuantity();

	public void setQuantity(Quantity quantity);

}
