package mit.cadlab.dome3.plugin.catalog.core;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 3.
 */
public class CUnit {

    public static final CUnit NO_UNIT = new CUnit("No_Unit");

    private Unit unit;

    public CUnit(String abbrevation) {
        this.unit = new Unit(abbrevation);
    }

    public CUnit(Unit unit) {
        if (unit == null) {
            this.unit = CConstant.NO_UNIT;
        } else {
            this.unit = unit;
        }
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String toString() {
		String abbrev = unit.toString();
		if (abbrev.startsWith("<html>")) {
			abbrev = abbrev.substring(6, abbrev.length() - 7);
			return "<html>" + UnitAtom.getUnitDescription(unit.toString()) + "    (" + abbrev +")</html>";
			//return "<html>" + unit.toString() + " (" + abbrev + ")</html>";
		} else {
	        return getDescription() + "    (" + abbrev+")";
        }
    }

    public String getAbbreviation() {
        return unit.toString();
    }

    public String getDescription() {
		return UnitAtom.getUnitDescription(unit.toString());
	}

    /** create CUnit list from Unit list */
    public static List createCUnitList(List unitList) {
        List ret = new ArrayList(unitList.size());
        for (Iterator i = unitList.iterator(); i.hasNext(); ) {
            ret.add(new CUnit((Unit) i.next()));
        }
        return ret;
    }

    public CUnit cloneUnit() {
        return new CUnit(((Unit) unit.clone()));
    }

    public Object clone() {
        return unit.clone();
    }
}
