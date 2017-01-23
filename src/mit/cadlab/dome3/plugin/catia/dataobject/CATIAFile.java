package mit.cadlab.dome3.plugin.catia.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.catia.CATIAPluginCaller;
import mit.cadlab.dome3.plugin.catia.CATIAConfiguration;
import mit.cadlab.dome3.plugin.catia.CATIAPlugin;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 16, 2003
 * Time: 8:40:24 PM
 * To change this template use Options | File Templates.
 */
public class CATIAFile  extends AbstractPluginData
{
	private CATIAPluginCaller caller;
	private long exFilObj;   //C++ object
	private DomeFile data;
	private boolean isResult;

	// constructor - call create() to create native object

	public CATIAFile(CATIAPluginCaller caller, String name, String type, long modelPtr)
	{
		this(caller, modelPtr, name, type, null);
	}

	public CATIAFile(CATIAPluginCaller caller, long modelPtr, String name, String type, Parameter dFile)
	{
		this.caller = caller;
		this.parameter = dFile;

		String[] arr = new String[2];
		arr[0] = name;

		// determine file type
		if (CATIAConfiguration.CATIA_IGES_FILE.equals(type))
			arr[1] = "igs";
		else
		if (CATIAConfiguration.CATIA_STEP_FILE.equals(type))
			arr[1] = "stp";
		else
		if (CATIAConfiguration.CATIA_VRML_FILE.equals(type))
			arr[1] = "wrl";
		else
			throw new RuntimeException("Cannot determine file type");

		exFilObj = caller.callObjectFunc(modelPtr, CATIAPluginCaller.MODEL_CREATE_FILE, arr);
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
			System.out.println("destroy peerobj = " + exFilObj);
			//caller.callVoidFunc(exFilObj, FILE_DESTROY, null);
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
		return caller.callBoolFunc(exFilObj, CATIAPluginCaller.FILE_SAVE, null);
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
