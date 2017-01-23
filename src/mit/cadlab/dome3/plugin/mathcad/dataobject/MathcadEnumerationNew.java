package mit.cadlab.dome3.plugin.mathcad.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.mathcad.MathcadPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 3, 2005
 * Time: 1:53:38 PM
 * To change this template use Options | File Templates.
 */
public class MathcadEnumerationNew extends MathcadRealNew
{
	private DomeEnumeration data;

	// constructor - call create() to create native
	// object
	public MathcadEnumerationNew(MathcadPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null);
	}

	public MathcadEnumerationNew(MathcadPluginCaller caller, long modelPtr, String name, Parameter enumParam)
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
			throw new UnsupportedOperationException("MathcadEnumerationNew.getValue: the enumerated value" +selectedItem+
			                                        " is not a number");
	}

	// set value of java object
	public void setValue(double value)
	{
		data.setLastSelectionToValue(new Double(value));
	}

	public String toString()
	{
		return ("MathcadEnumerationNew: " + data);
	}
}