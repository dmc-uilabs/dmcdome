package mit.cadlab.dome3.plugin.Solidworks.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPluginCaller;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2005
 * Time: 4:44:16 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksSurfaceAreaNew extends AbstractPluginData
{
	private SolidworksPluginCaller caller;
	private long exSuObj;   // C++ object
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksSurfaceAreaNew(SolidworksPluginCaller caller, long modelPtr)
	{
		this(caller, modelPtr, null);
	}

	public SolidworksSurfaceAreaNew(SolidworksPluginCaller caller, long modelPtr, Parameter dReal)
	{
		this.caller = caller;
		this.parameter = dReal;
		exSuObj = caller.callObjectFunc(modelPtr, SolidworksPluginCaller.MODEL_CREATE_SURFACEAREA, null);
		if (exSuObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exSuObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dReal == null)
			data = new RealData();
		else
			data = (RealData)dReal.getCurrentDataObject();
		isResult = true;
	}

	public void destroy()
	{
		if (exSuObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exSuObj);
			//caller.callDestructor(CLASS, exSuObj);
			exSuObj = 0;
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
		if (exSuObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(exSuObj, SolidworksPluginCaller.SURFACEAREA_GET_VALUE, null);
	}

	public void setValue(double value)
	{
		data.setValue(value);
	}

	public void loadJavaData()
	{
		setValue(getValue(true));
	}

	public void resetObjectPointer()
	{
		exSuObj = 0;
	}
}
