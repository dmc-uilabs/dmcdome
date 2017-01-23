package mit.cadlab.dome3.plugin.ug.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ug.UgPluginCaller;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2005
 * Time: 1:56:59 PM
 * To change this template use Options | File Templates.
 */
public class UgImportFileNew extends AbstractPluginData
{
	private Vector data;
	private UgPluginCaller caller;
	private long exImPtObj;   //C++ object
	boolean isResult;
	private BooleanData isChanged;
	private StringData name;
	private StringData fileName;

	public UgImportFileNew(UgPluginCaller caller, long modelPtr, String fileName, Parameter p)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileName;
		exImPtObj = caller.callObjectFunc(modelPtr, UgPluginCaller.MODEL_CREATE_IMPORTFILE, arr);
		if (exImPtObj == -1)
		{
			throw new RuntimeException("Argument number mismatch");
		}
		else if (exImPtObj == -2)
		{
			throw new RuntimeException("Argument type mismatch");
		}
		isResult = true;
		isChanged = new BooleanData();
		data = new Vector();
		this.name = new StringData();
		this.fileName = new StringData();
	}
	public void destroy()
	{
		if (exImPtObj != 0)
		{
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exImPtObj);
			//caller.callDestructor(CLASS, exImPtObj);
			exImPtObj = 0;
		}
	}
	public boolean getIsResult()
	{
		return isResult;
	}
	public void setIsResult(boolean value)
	{
		isResult = value;
	}
	public boolean getIsChanged()
	{
		return isChanged.getValue();
	}
	public boolean getIsChanged(boolean isNativeCall)
	{
		if (exImPtObj == 0)
		{
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callBoolFunc(exImPtObj, UgPluginCaller.IMPORTFILE_GET_CHANGED, null);
	}
	public void setIsChanged(boolean value)
	{
		isChanged.setValue(value);
	}
	public void setIsChanged(boolean value, boolean isNativeCall)
	{
		if (exImPtObj == 0)
		{
			throw new IllegalStateException("getValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new Boolean(value);
		caller.callVoidFunc(exImPtObj, UgPluginCaller.IMPORTFILE_SET_CHANGED, arr);
	}
	public void loadJavaData()
	{
	}
	public void loadNativeData()
	{
		setIsChanged(getIsChanged(), true);
	}

	public void resetObjectPointer()
	{
		exImPtObj = 0;
	}
}
