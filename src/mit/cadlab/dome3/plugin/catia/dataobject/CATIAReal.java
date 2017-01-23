/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.catia.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.catia.CATIAPluginCaller;
import mit.cadlab.dome3.plugin.catia.CATIAPlugin;


public class CATIAReal extends AbstractPluginData
{
	protected CATIAPluginCaller caller;
	protected long exRealObj;   //C++ object
	private DomeReal data;
	protected boolean isResult;

	// constructor - call create() to create native
	// object
	public CATIAReal(CATIAPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null, null, -1);
	}

	public CATIAReal(CATIAPluginCaller caller, long modelPtr, String name, Parameter dReal,
	                 String libName, int libArgIndex)
	{
		this.caller = caller;
        this.parameter = dReal;

		if (libName == null) {
			Object[] arr = new Object[1];
			arr[0] = name;
			exRealObj = caller.callObjectFunc(modelPtr, CATIAPluginCaller.MODEL_CREATE_REAL, arr);
		}
		else {
			Object[] arr = new Object[3];
			arr[0] = name;
			arr[1] = libName;
			arr[2] = new Integer (libArgIndex);
			exRealObj = caller.callObjectFunc(modelPtr,
											  CATIAPluginCaller.MODEL_CREATE_REAL_USERLIB, arr);
		}

		if (exRealObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exRealObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dReal == null)
			data = new RealData();
		else
			data = (RealData)dReal.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exRealObj != 0) {
			System.out.println("destroy peerobj = " + exRealObj);
			//caller.callVoidFunc(exRealObj, REAL_DESTROY, null);
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
		return caller.callDoubleFunc(exRealObj, CATIAPluginCaller.REAL_GETVALUE, null);
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
		caller.callVoidFunc(exRealObj, CATIAPluginCaller.REAL_SETVALUE, arr);
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
		return ("CATIAReal: " + exRealObj);
	}
}
