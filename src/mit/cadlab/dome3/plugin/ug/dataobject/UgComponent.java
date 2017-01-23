package mit.cadlab.dome3.plugin.ug.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.ug.UgPlugin;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgDimensionIn;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgDimensionOut;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgExportFile;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgMassProperty;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 14, 2003
 * Time: 4:56:38 PM
 * To change this template use Options | File Templates.
 */
public class UgComponent extends AbstractPluginData
{

	public static final String CLASS = "UgComponent";
	public static final String CREATEDIMENSIONIN = "UgComponent::createDimensionIn";
	public static final String CREATEDIMENSIONOUT = "UgComponent::createDimensionOut";
	public static final String CREATEMASSPROPERTY = "UgComponent::createMassProperty";
	public static final String CREATEEXPORTFILE = "UgComponent::createExportFile";

	private Vector data;
	private UgNativeCaller caller;
	private long exPartObj;   //C++ object
	private StringData dataComponent;
	private StringData dataUnit;
	private boolean isResult;

	public UgComponent(UgNativeCaller caller, long modelPtr, Parameter p, String componentName, String unit)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[2];
		arr[0] = componentName;
		arr[1] = unit;
		exPartObj = caller.callObjectFunc(UgPlugin.MODEL, modelPtr, UgPlugin.CREATECOMPONENT, arr);
		if (exPartObj == -1)
		{
			throw new RuntimeException("Argument number mismatch");
		}
		else if (exPartObj == -2)
		{
			throw new RuntimeException("Argument type mismatch");
		}
		dataComponent = new StringData();
		dataUnit = new StringData();
		data = new Vector();
		isResult = true;
	}

	public void destroy()
	{
		if (exPartObj != 0)
		{
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exPartObj);
			caller.callDestructor(CLASS, exPartObj);
			exPartObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	// get value from java object
	public String getComponentName()
	{
		return dataComponent.getValue();
	}

	// get value from native object
	public String getBinName()
	{
		return dataUnit.getValue();
	}

	// get value from native object
	public void setCompName(String value)
	{
		dataComponent.setValue(value);
	}

	public void setBinName(String value)
	{
		dataUnit.setValue(value);
	}

	public void loadJavaData(long modelPtr)
	{
		for (int i = 0; i < data.size(); i++)
		{
			Object obj = data.get(i);
			if (obj instanceof UgDimensionIn)
			{
				if (!((UgDimensionIn) obj).getIsResult())
					((UgDimensionIn)obj).loadNativeData();
			}
		}
		caller.callVoidFunc("UgModel", modelPtr, "UgModel::execute", null);
		for (int i = 0; i < data.size(); i++)
		{
			Object obj = data.get(i);
			if (obj instanceof UgDimensionIn)
			{
				if (((UgDimensionIn) obj).getIsResult())
					((UgDimensionIn) obj).loadJavaData();
			}
			if (obj instanceof UgDimensionOut)
			{
				if (((UgDimensionOut) obj).getIsResult())
					((UgDimensionOut) obj).loadJavaData();
			}
			if (obj instanceof UgMassProperty)
			{
				if (((UgMassProperty)obj).getIsResult())
					((UgMassProperty)obj).loadJavaData();
			}
		}
	}
	public Object createMassProperty(String property)
	{
		return this.createMassProperty(property, null);
	}

	public UgMassProperty createMassProperty(String property, Parameter real)
	{
		UgMassProperty part = new UgMassProperty(caller, exPartObj, property, real);
		data.addElement(part);
		return part;
	}

	public Object createExportFile(String fileType, Parameter p)
	{
		Object comExport = new UgExportFile(caller, exPartObj, fileType, p);
		data.addElement(comExport);
		return comExport;
	}
	public Object createDimensionOut(String dimName)
	{
		return this.createDimensionOut(dimName, null);
	}

	public UgDimensionOut createDimensionOut(String dimName, Parameter real)
	{
		UgDimensionOut dimOut = new UgDimensionOut(caller, exPartObj, dimName, CREATEDIMENSIONOUT, real);
		data.addElement(dimOut);
		return dimOut;
	}

	public Object createDimensionIn(String dimName)
	{
		return this.createDimensionIn(dimName, null);
	}

	public UgDimensionIn createDimensionIn(String dimName, Parameter real)
	{
		UgDimensionIn _ptdimIn = new UgDimensionIn(caller, exPartObj, dimName, CREATEDIMENSIONIN, real);
		data.addElement(_ptdimIn);
		return _ptdimIn;
	}

	public void resetObjectPointer()
	{
		exPartObj = 0;
	}
}
