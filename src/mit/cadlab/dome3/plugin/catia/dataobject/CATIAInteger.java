/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.catia.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.catia.CATIAPluginCaller;
import mit.cadlab.dome3.plugin.catia.CATIAPlugin;


public class CATIAInteger extends AbstractPluginData
{
	protected CATIAPluginCaller caller;
	protected long exIntegerObj;   //C++ object
	private DomeInteger data;
	protected boolean isResult;

	// constructor - call create() to create native
	// object
	public CATIAInteger(CATIAPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null, null, -1);
	}

	public CATIAInteger(CATIAPluginCaller caller, long modelPtr, String name,
	                    Parameter dInteger, String libName, int libArgIndex)
	{
		this.caller = caller;
        this.parameter = dInteger;

		if (libName == null) {
			Object[] arr = new Object[1];
			arr[0] = name;
			exIntegerObj = caller.callObjectFunc(modelPtr, CATIAPluginCaller.MODEL_CREATE_INT, arr);
		}
		else {
			Object[] arr = new Object[3];
			arr[0] = name;
			arr[1] = libName;
			arr[2] = new Integer (libArgIndex);
			exIntegerObj = caller.callObjectFunc(modelPtr,
											    CATIAPluginCaller.MODEL_CREATE_INTEGER_USERLIB, arr);
		}

		if (exIntegerObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exIntegerObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dInteger == null)
			data = new IntegerData();
		else
			data = (IntegerData)dInteger.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exIntegerObj != 0) {
			System.out.println("destroy peerobj = " + exIntegerObj);
			//caller.callVoidFunc(exIntegerObj, FILE_DESTROY, null);
			exIntegerObj = 0;
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
	public int getValue()
	{
		return data.getValue();
	}

	// get value from native object
	public int getValue(boolean isNativeCall)
	{
		if (exIntegerObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callIntFunc(exIntegerObj, CATIAPluginCaller.INT_GETVALUE, null);
	}

	// set value of java object
	public void setValue(int value)
	{
		data.setValue(value);
	}

	// set value of native object
	public void setValue(int value, boolean isNativeCall)
	{
		if (exIntegerObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}

		Object[] arr = new Object[1];
		arr[0] = new Integer(value);
		caller.callVoidFunc(exIntegerObj, CATIAPluginCaller.INT_SETVALUE, arr);
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
		exIntegerObj = 0;
	}

	public String toString()
	{
		return ("CATIAInteger: " + exIntegerObj);
	}
}
