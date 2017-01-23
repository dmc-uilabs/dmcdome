// DomeInteger.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

//import mit.cadlab.dome3.objectmodel.dataobject.interfaces.NumericQuantity;

public interface DomeInteger extends NumericQuantity
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Integer", "Integer");

	public DomeInteger getDomeInteger();

	public int getValue();

	public void setValue(int value);

	public Integer getIntegerValue();

	public void setIntegerValue(Integer value);

}
