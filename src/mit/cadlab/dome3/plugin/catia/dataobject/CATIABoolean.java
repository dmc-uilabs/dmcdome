/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.catia.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.catia.CATIAPluginCaller;
import mit.cadlab.dome3.plugin.catia.CATIAPlugin;


public class CATIABoolean extends AbstractPluginData
{
	protected CATIAPluginCaller caller;
	protected long exBooleanObj;   //C++ object
	private DomeBoolean data;
	protected boolean isResult;

	// constructor - call create() to create native
	// object
	public CATIABoolean(CATIAPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null, null, -1);
	}

	public CATIABoolean(CATIAPluginCaller caller, long modelPtr, String name,
	                    Parameter dBool, String libName, int libArgIndex)

	{
		this.caller = caller;
        this.parameter = dBool;

		if (libName == null) {
			Object[] arr = new Object[1];
			arr[0] = name;
			exBooleanObj = caller.callObjectFunc(modelPtr, CATIAPluginCaller.MODEL_CREATE_BOOL, arr);
		}
		else {
			Object[] arr = new Object[3];
			arr[0] = name;
			arr[1] = libName;
			arr[2] = new Integer (libArgIndex);
			exBooleanObj = caller.callObjectFunc(modelPtr,
											    CATIAPluginCaller.MODEL_CREATE_BOOLEAN_USERLIB, arr);
		}

		if (exBooleanObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exBooleanObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dBool == null)
			data = new BooleanData();
		else
			data = (BooleanData)dBool.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exBooleanObj != 0) {
			System.out.println("destroy peerobj = " + exBooleanObj);
			//caller.callVoidFunc(exBooleanObj, BOOL_DESTROY, null);
			exBooleanObj = 0;
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
		if (exBooleanObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callBoolFunc(exBooleanObj, CATIAPluginCaller.BOOL_GETVALUE, null);
	}

	// set value of java object
	public void setValue(boolean value)
	{
		data.setValue(value);
	}

	// set value of native object
	public void setValue(boolean value, boolean isNativeCall)
	{
		if (exBooleanObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}

		Object[] arr = new Object[1];
		arr[0] = new Boolean(value);
		caller.callVoidFunc(exBooleanObj, CATIAPluginCaller.BOOL_SETVALUE, arr);
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
		exBooleanObj = 0;
	}

	public String toString()
	{
		return ("CATIABoolean: " + exBooleanObj);
	}
}
