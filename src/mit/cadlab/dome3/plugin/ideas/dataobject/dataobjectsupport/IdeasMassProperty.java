package mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Jan 21, 2003
 * Time: 12:10:01 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;

public class IdeasMassProperty  extends AbstractPluginData
{
	public static final String CLASS = "IdeasMassProperty";
	public static final String GTNAME = "IdeasMassProperty::getName";
	public static final String GTUNIT = "IdeasMassProperty::getUnit";
	public static final String GTVAL = "IdeasMassProperty::getValue";

	private IdeasNativeCaller caller;
	private long exMPObj;
	private StringData mpName;
	private StringData mpUnit;
	private mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal mpValue;
	boolean isResult;

	public IdeasMassProperty(IdeasNativeCaller caller, long modelPtr, String propertyName, String PartOrAssembly, String classPorA)
	{
		this(caller, modelPtr, propertyName, PartOrAssembly, classPorA, null); // old method calls new one
	}

	public IdeasMassProperty(IdeasNativeCaller caller, long modelPtr, String propertyName, String PartOrAssembly, String classPorA, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal dReal)
	{
		this.caller = caller;
		String[] arr = new String[1];
		arr[0] = propertyName;
		exMPObj = caller.callObjectFunc(classPorA, modelPtr, PartOrAssembly, arr);
		if (exMPObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exMPObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		mpName = new StringData();
		mpUnit = new StringData();
		isResult = true;
		if (dReal == null)
			this.mpValue = new RealData();
		else
			this.mpValue = dReal;
	}

	public void destroy()
	{
		if (exMPObj != 0) {
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
	public String getMPName()
	{
		return mpName.getValue();
	}

	public String getMPUnit()
	{
		return mpUnit.getValue();
	}

	public double getMPValue()
	{
		return mpValue.getValue();
	}

	public String getMPName(boolean isNativeCall)
	{
		if (exMPObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exMPObj, GTNAME, null);
	}

	public String getMPUnit(boolean isNativeCall)
	{
		if (exMPObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exMPObj, GTUNIT, null);
	}

	public double getMPValue(boolean isNativeCall)
	{
		if (exMPObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exMPObj, GTVAL, null);
	}

	public void setMPName(String value)
	{
		mpName.setValue(value);
	}

	public void setMPUnit(String value)
	{
		mpUnit.setValue(value);
	}

	public void setMPValue(double value)
	{
		mpValue.setValue(value);
	}

	public void loadJavaData()
	{
		setMPName(getMPName(true));
		setMPUnit(getMPUnit(true));
		setMPValue(getMPValue(true));
	}

	public void resetObjectPointer()
	{
		exMPObj = 0;
	}
}