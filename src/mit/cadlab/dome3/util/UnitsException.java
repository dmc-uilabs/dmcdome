package mit.cadlab.dome3.util;

import edu.iupui.rg.ucum.units.UnitAtom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Name: UnitsException
 * User: thorek
 * Date: Mar 31, 2003
 * Time: 3:58:09 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class UnitsException extends DomeException
{
	String unit1, unit2;

	public UnitsException(String unit1, String unit2)
	{
		try {
			this.unit1 = UnitAtom.getUnitDescription(unit1);
		} catch (Exception e) {
			this.unit1 = unit1;
		}
		try {
			this.unit2 = UnitAtom.getUnitDescription(unit2);
		} catch (Exception e) {
			this.unit2 = unit2;
		}
	}

	public List getUnits()
	{
		ArrayList units = new ArrayList();
		units.add(unit1);
		units.add(unit2);
		return units;
	}

	public String getMessage()
	{
		return "Error converting from " + unit1 + " to " + unit2;
	}

	/**
	 * @param sourceName
	 * @return error message with the name of the source in it
	 */
	public String getMessage(String sourceName) {
		return "Error converting \"" + sourceName + "\" from " + unit1 + " to " + unit2;
	}

}