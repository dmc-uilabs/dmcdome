// DomeString.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface DomeString extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("String", "String");

	public DomeString getDomeString();

	public String getValue();

	public void setValue(String value);

}
