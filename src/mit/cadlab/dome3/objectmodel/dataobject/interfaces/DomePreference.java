// DomePreference.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;

import java.util.List;

public interface DomePreference extends DataObject {
	public static final TypeInfo TYPE_INFO = new TypeInfo("Preference", "Preference");
	public static final String VALUETYPE = "valuetype";
	public static final String SIZE = "size";
	public static final String ROWSIZE = "rowsize";
	public static final String COLSIZE = "colsize";
	public static final String ITEM = "item";
	public static final String ITEMS = "items";
	public static final String DATA = "data";
	public static final String FIXEDSIZE = "fixedsize";

	public static final String UNIT = "unit";


	public String getValueType();
	public void setValueType(String value);
	public int getRowCount();
	public void setRowCount(int rc);
	public int getColumnCount();
	public void setColumnCount(int cc);
	public Number getItem(int index1, int index2);
	public void setItem(int index1, int index2, Number n);
	public List getData();
	public void setData(DomePreference v);
	public boolean isFixedSize();
	public void setFixedSize(boolean yesOrno);
	public Unit getUnit();
	public void setUnit(Unit unit);

}
