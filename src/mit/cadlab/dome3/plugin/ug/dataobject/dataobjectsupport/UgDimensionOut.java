package mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponent;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 14, 2003
 * Time: 6:02:43 PM
 * To change this template use Options | File Templates.
 */
public class UgDimensionOut extends AbstractPluginData
{
	public static final String CLASS = "UgDimensionOut";
	public static final String GETVALUE = "UgDimensionOut::getValue";

	private UgNativeCaller caller;
	private long exDimObj;
	private StringData dimName;
	private StringData dimUnit;
	private DomeReal dimValue;
	boolean isResult;

	public UgDimensionOut(UgNativeCaller caller, long modelPtr, String dims, String functionName)
	{
		this(caller, modelPtr, dims, functionName, null);
	}

	public UgDimensionOut(UgNativeCaller caller, long modelPtr, String dims, String functionName, Parameter domeReal)
	{
		this.caller = caller;
		this.parameter = domeReal;
		String[] arr = new String[1];
		arr[0] = dims;
		exDimObj = caller.callObjectFunc(UgComponent.CLASS, modelPtr, functionName, arr);
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
			caller.callDestructor(CLASS, exDimObj);
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
		return caller.callDoubleFunc(CLASS, exDimObj, GETVALUE, null);
	}
	// SHOULD WE HAVE setDimName() and setDimUnit() methods !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
/*
	public void setDimValue(double value, boolean isNativeCall)
	{
		if (exDimObj == 0) throw new IllegalStateException("setValue called on destroyed object");
		Object[] arr = new Object[1];
		arr[0] = new Double(value);
		caller.callVoidFunc(CLASS, exDimObj, STVAL, arr);
	}
*/
	public void setDimValue(double value)
	{
		dimValue.setValue(value);
	}
	public void loadJavaData()
	{
		setDimValue(getDimValue(true));
	}
/*
	public void loadNativeData()
	{
		setDimValue(getDimValue(), true);
	}
	*/

	public void resetObjectPointer()
	{
		exDimObj = 0;
	}
}