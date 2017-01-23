/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.example.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.NativeCaller;
import mit.cadlab.dome3.plugin.example.ExamplePlugin;

public class ExampleReal  extends AbstractPluginData
{
	public static final String CLASS = "ExampleReal";
	public static final String GTVAL = "ExampleReal::getValue";
	public static final String STVAL = "ExampleReal::setValue";

	private NativeCaller caller;
	private long exRealObj;   //C++ object
	private RealData data;
	private boolean isResult;

	// constructor - call create() to create native
	// object
	public ExampleReal(NativeCaller caller, long modelPtr)
	{
		this.caller = caller;
		exRealObj = caller.callObjectFunc(ExamplePlugin.MODEL,
		                                  modelPtr, ExamplePlugin.CRTRL, null);
		if (exRealObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exRealObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		data = new RealData();
		isResult = false;
	}

	public ExampleReal(NativeCaller caller, long modelPtr, double value)
	{
		this.caller = caller;
		Object[] arr = new Object[1];
		arr[0] = new Double(value);
		exRealObj = caller.callObjectFunc(ExamplePlugin.MODEL,
		                                  modelPtr, ExamplePlugin.DCRTRL, arr);
		if (exRealObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exRealObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		data = new RealData(value);
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

	public void resetObjectPointer()
	{
		exRealObj = 0;
	}

	public String toString()
	{
		return ("ExampleReal: " + exRealObj);
	}
}
