/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 29, 2002
* Time: 2:42:00 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package mit.cadlab.dome3.plugin.catia.dataobject;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.catia.CATIAPluginCaller;
import mit.cadlab.dome3.plugin.catia.CATIAPlugin;

import java.util.List;


public class CATIAVector extends AbstractPluginData
{
	protected CATIAPluginCaller caller;
	protected long exVectorObj;   //C++ object
	private DomeVectorData data;
	protected boolean isResult;

	// constructor - call create() to create native
	// object
	public CATIAVector(CATIAPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null, null, -1);
	}

	public CATIAVector(CATIAPluginCaller caller, long modelPtr, String name, Parameter dVector,
	                   String libName, int libArgIndex)
	{
		this.caller = caller;
        this.parameter = dVector;

		if (libName == null) {
			Object[] arr = new Object[1];
			arr[0] = name;
			Debug.trace(Debug.ALL, "CATIA does not support vector types");
		}
		else {
			Object[] arr = new Object[3];
			arr[0] = name;
			arr[1] = libName;
			arr[2] = new Integer (libArgIndex);
			exVectorObj = caller.callObjectFunc(modelPtr,
			                                    CATIAPluginCaller.MODEL_CREATE_VECTOR_USERLIB,
			                                    arr);
		}

		if (exVectorObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exVectorObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dVector == null)
			data = new DomeVectorData();
		else
			data = (DomeVectorData) dVector.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exVectorObj != 0) {
			System.out.println("destroy peerobj = " + exVectorObj);
			//caller.callDestructor(exVectorObj, VECTOR_DESTROY, null);
			exVectorObj = 0;
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
	public List getValues()
	{
		return data.getValues();
	}

	// get value from native object
	public double[] getValues(boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleArrayFunc(exVectorObj, CATIAPluginCaller.VECTOR_GETVALUE, null);
	}

	// set value of java object
	public void setValues(double[] values)
	{
		data.setValues(values);
	}

	// set value of native object
	public void setValues(List values, boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}

		Object[] arr = new Object[2];
		arr[0] = new Integer (values.size());
		arr[1] = values;
		caller.callVoidFunc(exVectorObj, CATIAPluginCaller.VECTOR_SETVALUE, arr);
	}

	public void loadNativeData()
	{
		setValues(getValues(), true);
	}

	public void loadJavaData()
	{
		setValues(getValues(true));
	}

	// destroy native object if it's still around
	// when finalize called from garbage collection
	public void finalize()
	{
		destroy();
	}

	public void resetObjectPointer() {
		exVectorObj = 0;
	}

	public String toString()
	{
		return ("CATIAReal: " + exVectorObj);
	}
}
