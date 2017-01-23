package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 10, 2003
 * Time: 3:04:24 PM
 * To change this template use Options | File Templates.
 */
public interface DomeList extends DataObject, ViewSupport, DomeCollection
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("List", "List");
	public static final String ITEMTYPE = "itemType";
	public static final String LIST = "list";
	public static final String ITEM = "item";
	public static final String ITEMS = "items";
	public static final String ELEMENTNAME = "elementName";
	public static final String ELEMENTVALUE = "elementValue";
	public static final String SIZE = "size";

	public DomeList getDomeList();

	public String getItemType();

	public void setItemType(String value);

	public int getSize();

	public void setSize(int newSize);

	public Object getElementValue(int index);

	public void setElementValue(int index, Object newValue);


}
