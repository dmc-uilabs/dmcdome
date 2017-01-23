package mit.cadlab.dome3.plugin.ug.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ug.UgPluginCaller;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgDimensionInNew;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgDimensionOutNew;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgExportFileNew;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgMassPropertyNew;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2005
 * Time: 11:14:06 AM
 * To change this template use Options | File Templates.
 */
public class UgComponentNew extends AbstractPluginData
{
	private Vector data;
	private UgPluginCaller caller;
	private long exPartObj;   //C++ object
	private StringData dataComponent;
	private StringData dataUnit;
	private boolean isResult;

	public UgComponentNew(UgPluginCaller caller, long modelPtr, Parameter p, String componentName, String unit)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[2];
		arr[0] = componentName;
		arr[1] = unit;
		exPartObj = caller.callObjectFunc(modelPtr, UgPluginCaller.MODEL_CREATE_COMPONENT, arr);
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
			//caller.callDestructor(CLASS, exPartObj);
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
			if (obj instanceof UgDimensionInNew)
			{
				if (!((UgDimensionInNew) obj).getIsResult())
					((UgDimensionInNew)obj).loadNativeData();
			}
		}
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++)
		{
			Object obj = data.get(i);
			if (obj instanceof UgDimensionInNew)
			{
				if (((UgDimensionInNew) obj).getIsResult())
					((UgDimensionInNew) obj).loadJavaData();
			}
			if (obj instanceof UgDimensionOutNew)
			{
				if (((UgDimensionOutNew) obj).getIsResult())
					((UgDimensionOutNew) obj).loadJavaData();
			}
			if (obj instanceof UgMassPropertyNew)
			{
				if (((UgMassPropertyNew)obj).getIsResult())
					((UgMassPropertyNew)obj).loadJavaData();
			}
		}
	}
	public Object createMassProperty(String property)
	{
		return this.createMassProperty(property, null);
	}

	public UgMassPropertyNew createMassProperty(String property, Parameter real)
	{
		UgMassPropertyNew part = new UgMassPropertyNew(caller, exPartObj, property, real);
		data.addElement(part);
		return part;
	}

	public Object createExportFile(String fileType, Parameter p)
	{
		Object comExport = new UgExportFileNew(caller, exPartObj, fileType, p);
		data.addElement(comExport);
		return comExport;
	}
	public Object createDimensionOut(String dimName)
	{
		return this.createDimensionOut(dimName, null);
	}

	public UgDimensionOutNew createDimensionOut(String dimName, Parameter real)
	{
		UgDimensionOutNew dimOut = new UgDimensionOutNew(caller, exPartObj, dimName,real);
		data.addElement(dimOut);
		return dimOut;
	}

	public Object createDimensionIn(String dimName)
	{
		return this.createDimensionIn(dimName, null);
	}

	public UgDimensionInNew createDimensionIn(String dimName, Parameter real)
	{
		UgDimensionInNew _ptdimIn = new UgDimensionInNew(caller, exPartObj, dimName, real);
		data.addElement(_ptdimIn);
		return _ptdimIn;
	}

	public void resetObjectPointer()
	{
		exPartObj = 0;
	}
}
