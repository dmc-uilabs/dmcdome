package mit.cadlab.dome3.plugin.excel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.excel.ExcelPluginCaller;

/**
 * Author: Ashis Banerjee
 * Date: 10/23/2012
 */
public class ExcelStringNew extends AbstractPluginData
{
	private ExcelPluginCaller caller;
	private long exStringObj;   //C++ object
	private DomeString data;
	private boolean isResult;

	public ExcelStringNew(ExcelPluginCaller caller, long modelPtr, String sheet, String range)
	{
		this(caller, modelPtr, sheet, range, null);
	}

	// constructor - call create() to create native
	// object
	public ExcelStringNew(ExcelPluginCaller caller, long modelPtr, String sheet, String range, Parameter stringParam)
	{
		this.caller = caller;
		this.parameter = stringParam;
		String[] arr = new String[2];
		arr[0] = sheet;
		arr[1] = range;
		exStringObj = caller.callObjectFunc(modelPtr,
		                                  ExcelPluginCaller.MODEL_CREATE_STRING, arr);
		if (exStringObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exStringObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		
		if (parameter == null)
			data = new StringData();
		else
			data = (DomeString)parameter.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exStringObj != 0) {
			System.out.println("destroy peerobj = " + exStringObj);
			//caller.callDestructor(CLASS, exStringObj);
			exStringObj = 0;
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
	public String getValue()
	{
		return data.getValue();
	}

	// get value from native object
	public String getValue(boolean isNativeCall)
	{
		if (exStringObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(exStringObj, ExcelPluginCaller.STRING_GET_VALUE, null);
	}

	// set value of java object
	public void setValue(String value)
	{
		data.setValue(value);
	}

	// set value of native object
	public void setValue(String value, boolean isNativeCall)
	{
		if (exStringObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new String(value);
		caller.callVoidFunc(exStringObj, ExcelPluginCaller.STRING_SET_VALUE, arr);
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
		exStringObj = 0;
	}

	public String toString()
	{
		return ("ExcelStringNew: " + exStringObj);
	}
}