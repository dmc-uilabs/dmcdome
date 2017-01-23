package mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponent;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 15, 2003
 * Time: 1:24:01 AM
 * To change this template use Options | File Templates.
 */
public class UgMassProperty extends AbstractPluginData
{
	public static final String CLASS = "UgMassProperty";
	public static final String GETVALUE = "UgMassProperty::getValue";

	private UgNativeCaller caller;
	private long exMPObj;
	private StringData mpName;
	private StringData mpUnit;
	private DomeReal mpValue;
	boolean isResult;

	public UgMassProperty(UgNativeCaller caller, long modelPtr, String propertyName)
	{
		this(caller, modelPtr, propertyName, null); // old method calls new one
	}

	public UgMassProperty(UgNativeCaller caller, long modelPtr, String propertyName, Parameter dReal)
	{
		this.caller = caller;
		this.parameter = dReal;
		String[] arr = new String[1];
		arr[0] = propertyName;
		exMPObj = caller.callObjectFunc(UgComponent.CLASS, modelPtr, UgComponent.CREATEMASSPROPERTY, arr);
		if (exMPObj == -1)
		{
			throw new RuntimeException("Argument number mismatch");
		}
		else if (exMPObj == -2)
		{
			throw new RuntimeException("Argument type mismatch");
		}
		mpName = new StringData();
		mpUnit = new StringData();
		isResult = true;
		if (dReal == null)
			this.mpValue = new RealData();
		else
			this.mpValue = (RealData)dReal.getCurrentDataObject();
	}
	public void destroy()
	{
		if (exMPObj != 0)
		{
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exMPObj);
			caller.callDestructor(CLASS, exMPObj);
			exMPObj = 0;
		}
	}
	public boolean getIsResult()
	{
		return isResult;
	}
	// get value from java object

	public double getMassPropertyValue()
	{
		return mpValue.getValue();
	}
	public double getMassPropertyValue(boolean isNativeCall)
	{
		if (exMPObj == 0)
		{
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exMPObj, GETVALUE, null);
	}
	public void setMassPropertyValue(double value)
	{
		mpValue.setValue(value);
	}
	public void loadJavaData()
	{
		setMassPropertyValue(getMassPropertyValue(true));
	}

	public void resetObjectPointer()
	{
		exMPObj = 0;
	}
}
