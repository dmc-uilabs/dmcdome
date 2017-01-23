// DomeBoolean.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface DomeBoolean extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Boolean", "Boolean");

	public DomeBoolean getDomeBoolean();

	public boolean getValue();

	public void setValue(boolean value);

	public Boolean getBooleanValue();

	public void setBooleanValue(Boolean value);

}
