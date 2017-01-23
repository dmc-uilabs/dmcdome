// UnitComboBoxModel.java
package mit.cadlab.dome3.gui.guiutils.units;

import mit.cadlab.dome3.swing.DListModel;
import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;

public class UnitComboBoxModel extends DefaultComboBoxModel
        implements DListModel
{

	public static final String CHANGE_LIST = "change unit...";
	protected boolean isBuildMode = false;

	public UnitComboBoxModel(Unit unit, boolean isBuildMode)
	{
		this(new Unit[]{unit}, isBuildMode);
	}

	public UnitComboBoxModel(Unit[] units, boolean isBuildMode)
	{
		this(units, units[0], isBuildMode);
	}

	public UnitComboBoxModel(Unit[] units, Unit selectedUnit, boolean isBuildMode)
	{
		super(units);
		this.isBuildMode = isBuildMode;
		if (isBuildMode)
			addElement(CHANGE_LIST);
		setSelectedItem(selectedUnit);
	}

	public Icon getIcon(int index)
	{
		return null;
	}

	public String getListText(int index)
	{
		Object obj = getElementAt(index);
		if (index == -1) // get selected item
			obj = getSelectedItem();
		if (obj == null) return " ";
		if (obj == CHANGE_LIST)
			return obj.toString();
		Unit unit = (Unit) obj;
		return UnitAtom.getUnitDescription(unit.toString());
	}

}
