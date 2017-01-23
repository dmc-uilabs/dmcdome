/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.example.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.NativeCaller;
import mit.cadlab.dome3.plugin.example.ExamplePlugin;

public class ExampleBoolean extends AbstractPluginData
{
	public static final String CLASS = "ExampleBoolean";
	public static final String GTVAL = "ExampleBoolean::getValue";
	public static final String STVAL = "ExampleBoolean::setValue";

	private NativeCaller caller;
	private long exBoolObj;   //C++ object
	private BooleanData data;
	private boolean isResult;

	// constructor - call create() to create native
	// object
	public ExampleBoolean(NativeCaller caller, long modelPtr)
	{
		this.caller = caller;
		exBoolObj = caller.callObjectFunc(ExamplePlugin.MODEL,
		                                  modelPtr, ExamplePlugin.CRTBL, null);
		if (exBoolObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exBoolObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		data = new BooleanData();
		isResult = false;
	}

	public ExampleBoolean(NativeCaller caller, long modelPtr, boolean value)
	{
		this.caller = caller;
		Object[] arr = new Object[1];
		arr[0] = new Boolean(value);
		exBoolObj = caller.callObjectFunc(ExamplePlugin.MODEL,
		                                  modelPtr, ExamplePlugin.BCRTBL, arr);
		if (exBoolObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exBoolObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		data = new BooleanData(value);
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exBoolObj != 0) {
			System.out.println("destroy peerobj = " + exBoolObj);
			caller.callDestructor(CLASS, exBoolObj);
			exBoolObj = 0;
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
	public boolean getValue()
	{
		return data.getValue();
	}

	// get value from native object
	public boolean getValue(boolean isNativeCall)
	{
		if (exBoolObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callBoolFunc(CLASS, exBoolObj, GTVAL, null);
	}

	// set value of java object
	public void setValue(boolean value)
	{
		data.setValue(value);
	}

	// set value of native object
	public void setValue(boolean value, boolean isNativeCall)
	{
		if (exBoolObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new Boolean(value);
		caller.callVoidFunc(CLASS, exBoolObj, STVAL, arr);
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
		exBoolObj = 0;
	}

	public String toString()
	{
		return ("ExampleReal: " + exBoolObj);
	}
}
