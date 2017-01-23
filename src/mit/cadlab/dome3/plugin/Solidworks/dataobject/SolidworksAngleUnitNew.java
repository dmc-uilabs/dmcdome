package mit.cadlab.dome3.plugin.Solidworks.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2005
 * Time: 4:32:21 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksAngleUnitNew extends AbstractPluginData
{
	private SolidworksPluginCaller caller;
	private long exAuObj;   //C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksAngleUnitNew(SolidworksPluginCaller caller, long modelPtr)
	{
		this(caller, modelPtr, null);
	}

	public SolidworksAngleUnitNew(SolidworksPluginCaller caller, long modelPtr, Parameter dString)
	{
		this.caller = caller;
		this.parameter = dString;
		exAuObj = caller.callObjectFunc(modelPtr, SolidworksPluginCaller.MODEL_CREATE_ANGLEUNIT, null);
		if (exAuObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exAuObj == -2) {
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
		if (exAuObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exAuObj);
			//caller.callDestructor(CLASS, exAuObj);
			exAuObj = 0;
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
		if (exAuObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callStringFunc(exAuObj, SolidworksPluginCaller.ANGLEUNIT_GET_VALUE, null);
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
		exAuObj = 0;
	}
}
