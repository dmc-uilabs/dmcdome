// MathematicaReal.java

package mit.cadlab.dome3.plugin.mathematica.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.MathematicaNativeCaller;
import mit.cadlab.dome3.plugin.mathematica.MathematicaPlugin;

public class MathematicaReal extends AbstractPluginData
{
	public static final String CLASS = "MathematicaReal";
	public static final String GTVAL = "MathematicaReal::getValue";
	public static final String STVAL = "MathematicaReal::setValue";

	private MathematicaNativeCaller caller;
	private long exRealObj;   //C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal data;
	private boolean isResult;

	// constructor - call create() to create native
	// object
	public MathematicaReal(MathematicaNativeCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null);
	}

	public MathematicaReal(MathematicaNativeCaller caller, long modelPtr, String name, Parameter dReal)
	{
		this.caller = caller;
		parameter = dReal;
		String[] arr = new String[1];
		arr[0] = name;
		exRealObj = caller.callObjectFunc(MathematicaPlugin.MODEL,
		                                  modelPtr, MathematicaPlugin.CRTRL, arr);
		if (exRealObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exRealObj == -2) {
			throw new RuntimeException("Argument type mismatch1");
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
		//System.out.println("data is set: " + data);
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
		return ("MathematicaReal: " + exRealObj);
	}
}
