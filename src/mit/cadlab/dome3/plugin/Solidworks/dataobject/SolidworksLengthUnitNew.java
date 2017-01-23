package mit.cadlab.dome3.plugin.Solidworks.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2005
 * Time: 4:39:50 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksLengthUnitNew  extends AbstractPluginData
{
	private SolidworksPluginCaller caller;
	private long exLuObj;   //C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksLengthUnitNew(SolidworksPluginCaller caller, long modelPtr)
	{
		this(caller, modelPtr, null);
	}

	public SolidworksLengthUnitNew(SolidworksPluginCaller caller, long modelPtr, Parameter dString)
	{
		this.caller = caller;
		this.parameter = dString;
		exLuObj = caller.callObjectFunc(modelPtr, SolidworksPluginCaller.MODEL_CREATE_LENGTHUNIT, null);
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
			//caller.callDestructor(CLASS, exLuObj);
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
		return caller.callStringFunc(exLuObj, SolidworksPluginCaller.LENGTHUNIT_GET_VALUE, null);
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
