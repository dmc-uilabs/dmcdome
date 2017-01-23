/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.catia.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.catia.CATIAPluginCaller;
import mit.cadlab.dome3.plugin.catia.CATIAPlugin;


public class CATIAString extends AbstractPluginData
{
	protected CATIAPluginCaller caller;
	protected long exStringObj;   //C++ object
	private DomeString data;
	protected boolean isResult;

	// constructor - call create() to create native
	// object
	public CATIAString(CATIAPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null, null, -1);
	}

	public CATIAString(CATIAPluginCaller caller, long modelPtr, String name, Parameter dString,
	                   String libName, int libArgIndex)
	{
		this.caller = caller;
        this.parameter = dString;

		if (libName == null) {
			Object[] arr = new Object[1];
			arr[0] = name;
			exStringObj = caller.callObjectFunc(modelPtr,
			                                    CATIAPluginCaller.MODEL_CREATE_STRING, arr);
		}
		else {
			Object[] arr = new Object[3];
			arr[0] = name;
			arr[1] = libName;
			arr[2] = new Integer (libArgIndex);
			exStringObj = caller.callObjectFunc(modelPtr,
											    CATIAPluginCaller.MODEL_CREATE_STRING_USERLIB, arr);
		}

		if (exStringObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exStringObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dString == null)
			data = new StringData();
		else
			data = (StringData)dString.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exStringObj != 0) {
			System.out.println("destroy peerobj = " + exStringObj);
			//caller.callVoidFunc(exStringObj, STRING_DESTROY, null);
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
		return caller.callStringFunc(exStringObj, CATIAPluginCaller.STRING_GETVALUE, null);
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
		caller.callVoidFunc(exStringObj, CATIAPluginCaller.STRING_SETVALUE, arr);
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
		return ("CATIAString: " + exStringObj);
	}
}
