// Quantity.java
package mit.cadlab.dome3.util.units;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.util.Regex;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class Quantity implements XMLSupport
{

	public static final String NO_UNIT_STR = "No_Unit";
	public static final Unit NO_UNIT = new Unit(NO_UNIT_STR);

	private static final String XML_TAG = "quantity";

	private static List errorUnits = new ArrayList(); // keeps track of units that do not exist in units file

	protected Number magnitude;
	protected Unit unit;

	public Quantity(Element xmlElement)
	{
		if (xmlElement.getQName().getName().equals(XML_TAG)) {
			magnitude = createNumber(xmlElement.attributeValue("magnitude"));
			if (magnitude == null)
				throw new IllegalArgumentException("Quantity - invalid xml type: " +
				                                   xmlElement.attributeValue("type"));
            String term = xmlElement.attributeValue("unit");
			try {
				unit = new Unit(term);
			} catch (Exception e) {
				if (!errorUnits.contains(term)) {
                    errorUnits.add(term);
					OneButton1Msg.showError(null, "Unit retrieval error", "Could not find unit: " + term +"\nPlease update the unit file and try again", "OK", new Dimension(1,1));
				}
				unit = new Unit();
			}
		} else {
			throw new IllegalArgumentException("Quantity - illegal xmlElement: " + xmlElement);
		}
	}

	public Quantity()
	{
		magnitude = new Double(0.0);
		unit = NO_UNIT;
	}

	public Quantity(Number magnitude, Unit unit)
	{
		this.magnitude = magnitude;
		this.unit = unit;
	}

	public Quantity(Quantity q)
	{
		this.magnitude = q.magnitude;
		this.unit = (Unit) q.unit.clone();
	}

	public Quantity(String str)
	{
		List parts = Regex.split(Regex.whitespace, str);
		if (parts.size() != 2)
			throw new NumberFormatException("Quantity - invalid format: " + str);
		this.magnitude = createNumber((String) parts.get(0));
		if (this.magnitude == null)
			this.magnitude = new Double(0.0);
		try {
			this.unit = new Unit((String) parts.get(1));
		} catch (Exception e) {
			this.unit = NO_UNIT;
		}
	}

	public Quantity(String str, String type)
	{
		this(str);
		if (type.equals("Real") && (magnitude instanceof Integer))
			this.magnitude = new Double(magnitude.doubleValue());
		else if (type.equals("Integer") && (magnitude instanceof Double))
			this.magnitude = new Integer(magnitude.intValue());
	}

	public static Number createNumber(String str)
	{
		if (str == null)
			throw new NullPointerException("createNumber - null string");
		str = str.trim();
		try {
			if (str.indexOf('.') == -1) { // integer
				return new Integer(str);
			} else { // double
				return new Double(str);
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Number getMagnitude()
	{
		return magnitude;
	}

	public void setMagnitude(Number v)
	{
		if (v == null) return;
		this.magnitude = v;
	}

	public Unit getUnit()
	{
		return unit;
	}

	private void setUnit(Unit v)
	{
		if (v == null) return;
		this.unit = v;
	}

	public Quantity changeMagnitude(Number number)
	{
		return new Quantity(number, getUnit());
	}

	public Quantity changeUnit(Unit unit)
	{
		return new Quantity(getMagnitude(), unit);
	}

	public Quantity convertToUnit(Unit unit)
	{
		return new Quantity(getMagnitude(), unit); // not quite right...
	}

/*    public String str() {
        return magnitude.toString() + ((unit == NO_UNIT)?"":" " + unit.toString());
    }
*/
	public String toString()
	{
		return magnitude.toString() + ((NO_UNIT.equals(unit)) ? "" : " " + unit.toString());
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(XML_TAG);
		xml.addAttribute("magnitude", magnitude.toString());
		xml.addAttribute("unit", unit.toString());
		return xml;
	}

}
