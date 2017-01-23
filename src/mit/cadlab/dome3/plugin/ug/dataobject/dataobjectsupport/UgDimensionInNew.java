package mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ug.UgPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2005
 * Time: 3:28:14 PM
 * To change this template use Options | File Templates.
 */
public class UgDimensionInNew extends AbstractPluginData
{
	private UgPluginCaller caller;
	private long exDimObj;
	private StringData dimName;
	private StringData dimUnit;
	private DomeReal dimValue;
	boolean isResult;

	public UgDimensionInNew(UgPluginCaller caller, long modelPtr, String dims)
	{
		this(caller, modelPtr, dims, null);
	}

	public UgDimensionInNew(UgPluginCaller caller, long modelPtr, String dims, Parameter domeReal)
	{
		this.caller = caller;
		this.parameter = domeReal;
		String[] arr = new String[1];
		arr[0] = dims;
		exDimObj = caller.callObjectFunc(modelPtr, UgPluginCaller.COMPONENT_CREATE_DIMENSIONIN, arr);
		if (exDimObj == -1)
		{
			throw new RuntimeException("Argument number mismatch");
		}
		else if (exDimObj == -2)
		{
			throw new RuntimeException("Argument type mismatch");
		}
		dimName = new StringData();
		dimUnit = new StringData();
		if (domeReal == null)
			dimValue = new RealData();
		else
			dimValue = (RealData)domeReal.getCurrentDataObject();
		isResult = true;
	}

	public void destroy()
	{
		if (exDimObj != 0)
		{
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exDimObj);
			//caller.callDestructor(CLASS, exDimObj);
			exDimObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	public void setIsResult(boolean val)
	{
		isResult = val;
	}

	// get value from java object
	public String getDimName()
	{
		return dimName.getValue();
	}

/*	// get value from native object
	public String getDimUnit()
	{
		return dimUnit.getValue();
	}
*/
	public double getDimValue()
	{
		return dimValue.getValue();
	}

/*	public String getDimName(boolean isNativeCall)
	{
		if (exDimObj == 0)
		{
			throw new IllegalStateException("getDimName called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exDimObj, GTNAME, null);
	}

	public String getDimUnit(boolean isNativeCall)
	{
		if (exDimObj == 0)
		{
			throw new IllegalStateException("getDimUnit called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exDimObj, GTUNIT, null);
	}
*/
	public double getDimValue(boolean isNativeCall)
	{
		if (exDimObj == 0)
		{
			throw new IllegalStateException("getDimValue called on destroyed object");
		}
		return caller.callDoubleFunc(exDimObj, UgPluginCaller.DIMENSIONIN_GET_VALUE, null);
	}
	// SHOULD WE HAVE setDimName() and setDimUnit() methods !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

	public void setDimValue(double value, boolean isNativeCall)
	{
		if (exDimObj == 0) throw new IllegalStateException("setValue called on destroyed object");
		Object[] arr = new Object[1];
		arr[0] = new Double(value);
		caller.callVoidFunc(exDimObj, UgPluginCaller.DIMENSIONIN_SET_VALUE, arr);
	}
/*	public void setDimName(String value)
	{
		dimName.setValue(value);
	}

	public void setDimUnit(String value)
	{
		dimUnit.setValue(value);
	}
*/
	public void setDimValue(double value)
	{
		this.dimValue.setValue(value);
	}
	public void loadJavaData()
	{
		setDimValue(getDimValue(true));
	}
	public void loadNativeData()
	{
		this.setDimValue(getDimValue(), true);
	}

	public void resetObjectPointer()
	{
		exDimObj = 0;
	}
}
