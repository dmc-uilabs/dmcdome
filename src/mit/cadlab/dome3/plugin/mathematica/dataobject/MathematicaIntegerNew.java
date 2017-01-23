package mit.cadlab.dome3.plugin.mathematica.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.mathematica.MathematicaPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2005
 * Time: 4:24:07 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaIntegerNew extends AbstractPluginData
{
	private MathematicaPluginCaller caller;
	private long exIntegerObj;   //C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger data;
	private boolean isResult;

	// constructor - call create() to create native
	// object
	public MathematicaIntegerNew(MathematicaPluginCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null);
	}

	public MathematicaIntegerNew(MathematicaPluginCaller caller, long modelPtr, String name, Parameter dInteger)
	{
		this.caller = caller;
		parameter = dInteger;
		String[] arr = new String[1];
		arr[0] = name;
		exIntegerObj = caller.callObjectFunc(modelPtr, MathematicaPluginCaller.MODEL_CREATE_INTEGER, arr);
		if (exIntegerObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exIntegerObj == -2) {
			throw new RuntimeException("Argument type mismatch1");
		}
		if (dInteger == null)
			data = new IntegerData();
		else
			data = (IntegerData)parameter.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exIntegerObj != 0) {
			System.out.println("destroy peerobj = " + exIntegerObj);
			//caller.callDestructor(CLASS, exIntegerObj);
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
		return caller.callIntFunc(exIntegerObj, MathematicaPluginCaller.INTEGER_GET_VALUE, null);
	}

	// set value of java object
	public void setValue(int value)
	{
		data.setValue(value);
		//System.out.println("data is set: " + data);
	}

	// set value of native object
	public void setValue(int value, boolean isNativeCall)
	{
		if (exIntegerObj == 0) {
			throw new IllegalStateException("setValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new Integer(value);
		caller.callVoidFunc(exIntegerObj, MathematicaPluginCaller.INTEGER_SET_VALUE, arr);
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
		exIntegerObj = 0;
	}

	public String toString()
	{
		return ("MathematicaIntegerNew: " + exIntegerObj);
	}
}
