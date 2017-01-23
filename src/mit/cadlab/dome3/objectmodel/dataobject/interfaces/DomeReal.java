// DomeReal.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

//import mit.cadlab.dome3.objectmodel.dataobject.interfaces.NumericQuantity;

public interface DomeReal extends NumericQuantity
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Real", "Real");

	public DomeReal getDomeReal();

	public double getValue();

	public void setValue(double value);

	public Double getRealValue();

	public void setRealValue(Double value);

}
