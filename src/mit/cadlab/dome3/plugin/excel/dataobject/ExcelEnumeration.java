package mit.cadlab.dome3.plugin.excel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.ExcelNativeCaller;

public class ExcelEnumeration extends ExcelReal
{
	public static final String CLASS = "ExcelReal";
	public static final String GTVAL = "ExcelReal::getValue";
	public static final String STVAL = "ExcelReal::setValue";

	private DomeEnumeration data;

	public ExcelEnumeration(ExcelNativeCaller caller, long modelPtr, String sheet, String range)
	{
		this(caller, modelPtr, sheet, range, null);
	}

	// constructor - call create() to create native
	// object
	public ExcelEnumeration(ExcelNativeCaller caller, long modelPtr, String sheet, String range, Parameter enumParam)
	{
		super(caller, modelPtr, sheet, range, enumParam);
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
			throw new UnsupportedOperationException("ExcelEnumeration.getValue: the enumerated value" + selectedItem +
			                                        " is not a number");
	}

	// set value of java object
	public void setValue(double value)
	{
		data.setLastSelectionToValue(new Double(value));
	}
	
	public String toString()
	{
		return ("ExcelEnumeration: " + data);
	}
}
