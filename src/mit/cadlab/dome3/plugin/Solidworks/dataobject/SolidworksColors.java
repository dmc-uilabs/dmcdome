package mit.cadlab.dome3.plugin.Solidworks.dataobject;

/**
 * Created by Jacob Wronski
 * Date: Dec 30, 2002
 * Time: 2:17:39 AM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPlugin;
import mit.cadlab.dome3.plugin.SolidworksNativeCaller;

public class SolidworksColors extends AbstractPluginData
{
	public static final String CLASS = "SolidworksColors";
	public static final String GTVALS = "SolidworksColors::getValues";
	public static final String GTRED = "SolidworksColors::getRed";
	public static final String GTGRE = "SolidworksColors::getGreen";
	public static final String GTBLU = "SolidworksColors::getBlue";

	private SolidworksNativeCaller caller;
	private long exColObj;   //C++ object
	private DomeVectorData data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksColors(SolidworksNativeCaller caller, long modelPtr)
	{
		this(caller, modelPtr, null);
	}

	public SolidworksColors(SolidworksNativeCaller caller, long modelPtr, Parameter dVector)
	{
		this.caller = caller;
		this.parameter = dVector;
		exColObj = caller.callObjectFunc(SolidworksPlugin.MODEL, modelPtr, SolidworksPlugin.CREATECOL, null);
		if (exColObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exColObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dVector == null) {
			data = new DomeVectorData(3);
			data.setValueType("real");
		} else
			data = (DomeVectorData)dVector.getCurrentDataObject();
		isResult = true;
	}

	public void destroy()
	{
		if (exColObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exColObj);
			caller.callDestructor(CLASS, exColObj);
			exColObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	// get value from java object
	public DomeVectorData getValues()
	{
		return data;
	}

	// get value from native object
	public double[] getValues(boolean isNativeCall)
	{
		if (exColObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleArrayFunc(CLASS, exColObj, GTVALS, null);
	}

	public double getRed()
	{
		return (data.getItem(0)).doubleValue();
	}

	public double getRed(boolean isNativeCall)
	{
		if (exColObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exColObj, GTRED, null);
	}

	public double getGreen()
	{
		return (data.getItem(1)).doubleValue();
	}

	public double getGreen(boolean isNativeCall)
	{
		if (exColObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exColObj, GTGRE, null);
	}

	public double getBlue()
	{
		return (data.getItem(2)).doubleValue();
	}

	public double getBlue(boolean isNativeCall)
	{
		if (exColObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exColObj, GTBLU, null);
	}

	public void setValue(int index, double[] value)
	{
		data.setItem(index, new Double(value[index]));
	}

	public void loadJavaData()
	{
		setValue(0, getValues(true));
		setValue(1, getValues(true));
		setValue(2, getValues(true));
	}

	public void resetObjectPointer()
	{
		exColObj = 0;
	}
}


