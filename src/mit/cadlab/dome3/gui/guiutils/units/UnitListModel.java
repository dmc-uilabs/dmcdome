// UnitListModel.java
package mit.cadlab.dome3.gui.guiutils.units;

import mit.cadlab.dome3.swing.DListModel;
import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Icon;

public class UnitListModel extends DefaultListModel
        implements DListModel
{

	public UnitListModel(Unit unit)
	{
		this(new Unit[]{unit});
	}

	public UnitListModel(Unit[] units)
	{
		super();
		ensureCapacity(units.length);
		for (int i = 0; i < units.length; ++i)
			addElement(units[i]);
	}

	public UnitListModel(List units)
	{
		super();
		ensureCapacity(units.size());
		Iterator it = units.iterator();
		while (it.hasNext())
			addElement(it.next());
	}

	public void setNewUnitList(List units)
	{
		clear();
		ensureCapacity(units.size());
		Iterator it = units.iterator();
		while (it.hasNext())
			addElement(it.next());
	}

	public Icon getIcon(int index)
	{
		return null;
	}

	public String getListText(int index)
	{
		Object obj = getElementAt(index);
		if (index == -1) {
			System.err.println("getListText for -1");
		}
		if (obj == null) return " ";
		Unit unit = (Unit) obj;
		String abbrev = unit.toString();
		if (abbrev.startsWith("<html>")) {
			abbrev = abbrev.substring(6, abbrev.length() - 7);
			return "<html>" + UnitAtom.getUnitDescription(unit.toString()) + "    (" + abbrev +")</html>";
			//return "<html>" + unit.toString() + " (" + abbrev + ")</html>";
		} else
			return UnitAtom.getUnitDescription(unit.toString()) + "    (" + abbrev+")";
	}

}
