package mit.cadlab.dome3.plugin.ideas.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;
import mit.cadlab.dome3.plugin.ideas.IdeasPlugin;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Feb 13, 2003
 * Time: 1:12:25 PM
 * To change this template use Options | File Templates.
 */
public class IdeasImportAssembly extends AbstractPluginData
{
	public static final String CLASS = "IdeasImportAssembly";
	public static final String GTSCH = "IdeasImportAssembly::getChanged";
	public static final String STSCH = "IdeasImportAssembly::setChanged";
	public static final String GTNAME = "IdeasImportAssembly::getName";
	public static final String FILENAME = "IdeasImportAssembly::getFileName";

	private Vector data;
	private IdeasNativeCaller caller;
	private long exImAssObj;   //C++ object
	boolean isResult;
	private BooleanData isChanged;
	private StringData Name;
	private StringData FileName;

	public IdeasImportAssembly(IdeasNativeCaller caller, long modelPtr, Parameter p, String fileName)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileName;
		exImAssObj = caller.callObjectFunc(IdeasPlugin.MODEL, modelPtr, IdeasPlugin.CREATEIPT, arr);
		if (exImAssObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exImAssObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		isResult = true;
		isChanged = new BooleanData();
		data = new Vector();
		Name = new StringData();
		FileName = new StringData();
	}

	public void destroy()
	{
		if (exImAssObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exImAssObj);
			caller.callDestructor(CLASS, exImAssObj);
			exImAssObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	public boolean getIsChanged()
	{
		return isChanged.getValue();
	}

	public boolean getIsChanged(boolean isNativeCall)
	{
		if (exImAssObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callBoolFunc(CLASS, exImAssObj, GTSCH, null);
	}

	public void setIsChanged(boolean value)
	{
		isChanged.setValue(value);
	}

	public void setIsChanged(boolean value, boolean isNativeCall)
	{
		if (exImAssObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new Boolean(value);
		caller.callVoidFunc("IdeasDimensionOut", exImAssObj, STSCH, arr);
	}

	public String getName()
	{
		return Name.getValue();
	}

	// get value from native object
	public String getName(boolean isNativeCall)
	{
		if (exImAssObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exImAssObj, GTNAME, null);
	}

	public String getFileName()
	{
		return FileName.getValue();
	}

	// get value from native object
	public String getFileName(boolean isNativeCall)
	{
		if (exImAssObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exImAssObj, FILENAME, null);
	}

	public void setName(String value)
	{
		Name.setValue(value);
	}

	public void setFileName(String value)
	{
		FileName.setValue(value);
	}

	public void loadJavaData()
	{
		setName(getName(true));
		setFileName(getFileName(true));
	}

	public void loadNativeData()
	{
		setIsChanged(getIsChanged(), true);

		// we will also set the Java data for Name and FileName

		setName(getName(true));
		setFileName(getFileName(true));
	}

	public void resetObjectPointer()
	{
		exImAssObj = 0;
	}

}
