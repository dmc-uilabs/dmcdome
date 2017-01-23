package mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasPart;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Jan 21, 2003
 * Time: 6:26:40 PM
 * To change this template use Options | File Templates.
 */
public class IdeasDimensionOut extends AbstractPluginData
{
	public static final String CLASS = "IdeasDimensionOut";
	public static final String GTNAME = "IdeasDimensionOut::getName";
	public static final String GTUNIT = "IdeasDimensionOut::getUnit";
	public static final String GTVAL = "IdeasDimensionOut::getValue";
	public static final String STVAL = "IdeasDimensionOut::setValue";

	private IdeasNativeCaller caller;
	private long exDimObj;
	private StringData dimName;
	private StringData dimUnit;
	private DomeReal dimValue;
	boolean isResult;

	public IdeasDimensionOut(IdeasNativeCaller caller, long modelPtr, String dims, String DimInOrOut)
	{
		this(caller, modelPtr, dims, DimInOrOut, null);
	}

	public IdeasDimensionOut(IdeasNativeCaller caller, long modelPtr, String dims, String DimInOrOut, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal domeReal)
	{
		this.caller = caller;
		String[] arr = new String[1];
		arr[0] = dims;
		exDimObj = caller.callObjectFunc(IdeasPart.CLASS, modelPtr, DimInOrOut, arr);
		if (exDimObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exDimObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		dimName = new StringData();
		dimUnit = new StringData();
		if (domeReal == null)
			dimValue = new RealData();
		else
			dimValue = domeReal;
		isResult = true;
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

	// get value from java object
	public String getDimName()
	{
		return dimName.getValue();
	}

	// get value from native object
	public String getDimUnit()
	{
		return dimUnit.getValue();
	}

	public double getDimValue()
	{
		return dimValue.getValue();
	}

	public String getDimName(boolean isNativeCall)
	{
		if (exDimObj == 0) {
			throw new IllegalStateException("getDimName called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exDimObj, GTNAME, null);
	}

	public String getDimUnit(boolean isNativeCall)
	{
		if (exDimObj == 0) {
			throw new IllegalStateException("getDimUnit called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exDimObj, GTUNIT, null);
	}

	public double getDimValue(boolean isNativeCall)
	{
		if (exDimObj == 0) {
			throw new IllegalStateException("getDimValue called on destroyed object");
		}
		return caller.callDoubleFunc(CLASS, exDimObj, GTVAL, null);
	}

	// SHOULD WE HAVE setDimName() and setDimUnit() methods !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
	public void setDimName(String value)
	{
		dimName.setValue(value);
	}

	public void setDimUnit(String value)
	{
		dimUnit.setValue(value);
	}

	public void setDimValue(double value)
	{
		dimValue.setValue(value);
	}

	public void loadJavaData()
	{
		setDimName(getDimName(true));
		setDimUnit(getDimUnit(true));
		setDimValue(getDimValue(true));
	}

	public void resetObjectPointer()
	{
		exDimObj = 0;
	}
}
