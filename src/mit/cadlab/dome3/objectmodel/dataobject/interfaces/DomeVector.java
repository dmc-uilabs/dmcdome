// DomeVector.java

package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;

import java.util.List;

public interface DomeVector extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Vector", "Vector");
	public static final String VALUETYPE = "valuetype";
	public static final String SIZE = "size";
	public static final String ITEM = "item";
	public static final String ITEMS = "items";
	public static final String DATA = "data";
	public static final String FIXEDSIZE = "fixedsize";
	public static final String ROWVECTOR = "rowvector";
	public static final String UNIT = "unit";


	public String getValueType();

	public void setValueType(String value);

	public int getSize();

	public void setSize(int size);

	public Number getItem(int index);

	public void setItem(int index, Number n);

	public List getData();

	public void setData(DomeVectorData v);

	public boolean isFixedSize();

	public void setFixedSize(boolean yesOrno);

	public boolean isRowVector();

	public void setRowVector(boolean yesOrno);

	public Unit getUnit();

	public void setUnit(Unit unit);

}
