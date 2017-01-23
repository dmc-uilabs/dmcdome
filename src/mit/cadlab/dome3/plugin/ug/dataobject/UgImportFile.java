package mit.cadlab.dome3.plugin.ug.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.ug.UgPlugin;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 15, 2003
 * Time: 2:13:31 AM
 * To change this template use Options | File Templates.
 */
public class UgImportFile extends AbstractPluginData
{
	public static final String CLASS = "UgImportFile";
	public static final String GETCHANGED = "UgImportFile::getChanged";
	public static final String SETCHANGED = "UgImportFile::setChanged";

	private Vector data;
	private UgNativeCaller caller;
	private long exImPtObj;   //C++ object
	boolean isResult;
	private BooleanData isChanged;
	private StringData name;
	private StringData fileName;

	public UgImportFile(UgNativeCaller caller, long modelPtr, String fileName, Parameter p)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileName;
		exImPtObj = caller.callObjectFunc(UgPlugin.MODEL, modelPtr, UgPlugin.CREATEIMPORTFILE, arr);
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
			caller.callDestructor(CLASS, exImPtObj);
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
		return caller.callBoolFunc(CLASS, exImPtObj, GETCHANGED, null);
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
		caller.callVoidFunc("IdeasDimensionOut", exImPtObj, SETCHANGED, arr);
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
