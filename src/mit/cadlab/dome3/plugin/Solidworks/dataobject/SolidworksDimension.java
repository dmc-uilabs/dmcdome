package mit.cadlab.dome3.plugin.Solidworks.dataobject;

/**
 * Created by Jacob Wronski
 * Date: Dec 28, 2002
 * Time: 12:28:13 AM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPlugin;
import mit.cadlab.dome3.plugin.SolidworksNativeCaller;

public class SolidworksDimension  extends AbstractPluginData
{
	public static final String CLASS = "SolidworksDimension";
	public static final String GTVAL = "SolidworksDimension::getValue";
	public static final String STVAL = "SolidworksDimension::setValue";

	private SolidworksNativeCaller caller;
	private long exDimObj;   //C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksDimension(SolidworksNativeCaller caller, long modelPtr, String name)
	{
		this(caller, modelPtr, name, null);
	}

	public SolidworksDimension(SolidworksNativeCaller caller, long modelPtr, String name, Parameter dReal)
	{
		this.caller = caller;
		this.parameter = dReal;
		String[] arr = new String[1];
		arr[0] = name;
		exDimObj = caller.callObjectFunc(SolidworksPlugin.MODEL, modelPtr, SolidworksPlugin.CREATEDIM, arr);
		if (exDimObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exDimObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dReal == null)
			data = new RealData();
		else
			data = (RealData)dReal.getCurrentDataObject();
		isResult = false;
	}

	public void destroy()
	{
		if (exDimObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exDimObj);
			caller.callDestructor(CLASS, exDimObj);
			exDimObj = 0;
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
		if (exDimObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exDimObj, GTVAL, null);
	}

	public void setValue(double value)
	{
		data.setValue(value);
	}

	public void setValue(double value, boolean isNativeCall)
	{
		if (exDimObj == 0) throw new IllegalStateException("setValue called on destroyed object");
		Object[] arr = new Object[1];
		arr[0] = new Double(value);
		caller.callVoidFunc(CLASS, exDimObj, STVAL, arr);
	}

	public void loadJavaData()
	{
		setValue(getValue(true));
	}

	public void loadNativeData()
	{
		setValue(getValue(), true);
	}

	public void resetObjectPointer()
	{
		exDimObj = 0;
	}
}
