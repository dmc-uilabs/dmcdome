package mit.cadlab.dome3.plugin.excel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.excel.ExcelPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 2, 2005
 * Time: 6:59:31 PM
 * To change this template use Options | File Templates.
 */
public class ExcelRealNew extends AbstractPluginData
{
	private ExcelPluginCaller caller;
	private long exRealObj;   //C++ object
	private DomeReal data;
	private boolean isResult;

	public ExcelRealNew(ExcelPluginCaller caller, long modelPtr, String sheet, String range)
	{
		this(caller, modelPtr, sheet, range, null);
	}

	// constructor - call create() to create native
	// object
	public ExcelRealNew(ExcelPluginCaller caller, long modelPtr, String sheet, String range, Parameter realParam)
	{
		this.caller = caller;
		this.parameter = realParam;
		String[] arr = new String[2];
		arr[0] = sheet;
		arr[1] = range;
		exRealObj = caller.callObjectFunc(modelPtr,
		                                  ExcelPluginCaller.MODEL_CREATE_REAL, arr);
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
			//System.out.println("destroy peerobj = " + exRealObj);
			//caller.callDestructor(CLASS, exRealObj);
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
		return caller.callDoubleFunc(exRealObj, ExcelPluginCaller.REAL_GET_VALUE, null);
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
		caller.callVoidFunc(exRealObj, ExcelPluginCaller.REAL_SET_VALUE, arr);
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
		return ("ExcelRealNew: " + exRealObj);
	}
}
