package mit.cadlab.dome3.plugin.matlab.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.MatlabNativeCaller;

public class MatlabEnumeration extends MatlabReal
{
	public static final String CLASS = "MatlabEnumeration";
	public static final String GTVAL = "MatlabEnumeration::getValue";
	public static final String STVAL = "MatlabEnumeration::setValue";

	private DomeEnumeration data;

	// constructor - call create() to create native
	// object
	public MatlabEnumeration(MatlabNativeCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null);
	}

	public MatlabEnumeration(MatlabNativeCaller caller, long modelPtr, String name, Parameter enumParam)
	{
		super(caller, modelPtr,name);
        this.parameter = enumParam;
		if (parameter == null)
			data = new EnumerationData();
		else
			data = (DomeEnumeration) parameter.getCurrentDataObject();
	}

	// get value from java object
	public double getValue()
	{
		Object selectedItem = data.getElementValue(data.getLastSelection());
		if (selectedItem instanceof Number)
			return ((Number) selectedItem).doubleValue();
		else
			throw new UnsupportedOperationException("MatlabEnumeration.getValue: the enumerated value" +selectedItem+
			                                        " is not a number");
	}

	// set value of java object
	public void setValue(double value)
	{
		data.setLastSelectionToValue(new Double(value));
	}

	public String toString()
	{
		return ("MatlabEnumeration: " + data);
	}
}