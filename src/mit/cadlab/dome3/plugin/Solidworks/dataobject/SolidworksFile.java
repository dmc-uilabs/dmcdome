package mit.cadlab.dome3.plugin.Solidworks.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksPlugin;
import mit.cadlab.dome3.plugin.SolidworksNativeCaller;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 16, 2003
 * Time: 8:40:24 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksFile  extends AbstractPluginData
{
	public static final String CLASS = "SolidworksFile";
	public static final String SAVE = "SolidworksFile::save";

	private SolidworksNativeCaller caller;
	private long exFilObj;   //C++ object
	private DomeFile data;
	private boolean isResult;

	// constructor - call create() to create native object

	public SolidworksFile(SolidworksNativeCaller caller, String name, long modelPtr)
	{
		this(caller, modelPtr, name, null);
	}

	public SolidworksFile(SolidworksNativeCaller caller, long modelPtr, String name, Parameter dFile)
	{
		this.caller = caller;
		this.parameter = dFile;
		String[] arr = new String[1];
		arr[0] = name;
		exFilObj = caller.callObjectFunc(SolidworksPlugin.MODEL, modelPtr, SolidworksPlugin.CREATEFIL, arr);
		if (exFilObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exFilObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (dFile == null)
			data = new FileData(name);
		else
			data = (FileData)dFile.getCurrentDataObject();
		isResult = true;
	}

	public void destroy()
	{
		if (exFilObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exFilObj);
			caller.callDestructor(CLASS, exFilObj);
			exFilObj = 0;
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

	public boolean save(boolean isNativeCall)
	{
		if (exFilObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callBoolFunc(CLASS, exFilObj, SAVE, null);
	}

	// get value from java object
	public String getValue()
	{
		return data.getFilePath();
	}

	// get value from native object
	public void setValue(String value)
	{
		data.setFilePath(value);
	}
/*      public void loadJavaData()
    {
        setValue(getValue(true));
    }
 */
	public void resetObjectPointer()
	{
		exFilObj = 0;
	}

}
