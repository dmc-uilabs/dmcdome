/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.excel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ExcelNativeCaller;
import mit.cadlab.dome3.plugin.excel.ExcelPlugin;

public class ExcelReal extends AbstractPluginData
{
	public static final String CLASS = "ExcelReal";
	public static final String GTVAL = "ExcelReal::getValue";
	public static final String STVAL = "ExcelReal::setValue";

	private ExcelNativeCaller caller;
	private long exRealObj;   //C++ object
	private DomeReal data;
	private boolean isResult;

	public ExcelReal(ExcelNativeCaller caller, long modelPtr, String sheet, String range)
	{
		this(caller, modelPtr, sheet, range, null);
	}

	// constructor - call create() to create native
	// object
	public ExcelReal(ExcelNativeCaller caller, long modelPtr, String sheet, String range, Parameter realParam)
	{
		this.caller = caller;
		this.parameter = realParam;
		String[] arr = new String[2];
		arr[0] = sheet;
		arr[1] = range;
		exRealObj = caller.callObjectFunc(ExcelPlugin.MODEL,
		                                  modelPtr, ExcelPlugin.CRTRL, arr);
		if (exRealObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exRealObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (parameter == null)
			data = new RealData();
		else
			data = (DomeReal)parameter.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exRealObj != 0) {
			System.out.println("destroy peerobj = " + exRealObj);
			caller.callDestructor(CLASS, exRealObj);
			exRealObj = 0;
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
	public double getValue()
	{
		return data.getValue();
	}

	// get value from native object
	public double getValue(boolean isNativeCall)
	{
		if (exRealObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exRealObj, GTVAL, null);
	}

	// set value of java object
	public void setValue(double value)
	{
		data.setValue(value);
	}

	// set value of native object
	public void setValue(double value, boolean isNativeCall)
	{
		if (exRealObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new Double(value);
		caller.callVoidFunc(CLASS, exRealObj, STVAL, arr);
	}

	public void loadNativeData()
	{
		setValue(getValue(), true);
	}

	public void loadJavaData()
	{
		setValue(getValue(true));
	}

	// destroy native object if it's still around
	// when finalize called from garbage collection
	public void finalize()
	{
		destroy();
	}

	public void resetObjectPointer() {
		exRealObj = 0;
	}

	public String toString()
	{
		return ("ExcelReal: " + exRealObj);
	}
}
