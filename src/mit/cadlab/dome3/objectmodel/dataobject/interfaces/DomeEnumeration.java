// DomeEnumeration.java

package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface DomeEnumeration extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Enumeration", "Enumeration");
	public static final String ENUMERATION = "enumeration";
	public static final String ELEMENTNAME = "elementName";
	public static final String ELEMENTVALUE = "elementValue";
	public static final String SIZE = "size";
	public static final String LASTSELECTION = "last selection";

	//already exists in AbstractDataObject see getDataObject
	//public DomeEnumeration getEnumeration();

	public int getSize();

	public void setSize(int newSize);

	public Object getElementValue(int index);

	public void setElementValue(int index, Object newValue);

	public String getElementName(int index);

	public void setElementName(int index, String newName);

	public void setLastSelection(int index);

	public int getLastSelection();

	public void setLastSelectionToValue(Object value);

}
