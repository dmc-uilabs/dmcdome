package mit.cadlab.dome3.plugin.Solidworks.dataobject;

/**
 * Created by Jacob Wronski
 * Date: Dec 30, 2002
 * Time: 3:19:47 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPlugin;
import mit.cadlab.dome3.plugin.SolidworksNativeCaller;

public class SolidworksLengthUnit  extends AbstractPluginData
{
	public static final String CLASS = "SolidworksLengthUnit";
	public static final String GTVAL = "SolidworksLengthUnit::getValue";

	private SolidworksNativeCaller caller;
	private long exLuObj;   //C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksLengthUnit(SolidworksNativeCaller caller, long modelPtr)
	{
		this(caller, modelPtr, null);
	}

	public SolidworksLengthUnit(SolidworksNativeCaller caller, long modelPtr, Parameter dString)
	{
		this.caller = caller;
		this.parameter = dString;
		exLuObj = caller.callObjectFunc(SolidworksPlugin.MODEL, modelPtr, SolidworksPlugin.CREATELU, null);
		if (exLuObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exLuObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dString == null)
			data = new StringData();
		else
			data = (StringData)dString.getCurrentDataObject();
		isResult = true;
	}

	public void destroy()
	{
		if (exLuObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exLuObj);
			caller.callDestructor(CLASS, exLuObj);
			exLuObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	// get value from java object
	public String getValue()
	{
		return data.getValue();
	}

	// get value from native object
	public String getValue(boolean isNativeCall)
	{
		if (exLuObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callStringFunc(CLASS, exLuObj, GTVAL, null);
	}

	public void setValue(String value)
	{
		data.setValue(value);
	}

	public void loadJavaData()
	{
		setValue(getValue(true));
	}

	public void resetObjectPointer()
	{
		exLuObj = 0;
	}
}
