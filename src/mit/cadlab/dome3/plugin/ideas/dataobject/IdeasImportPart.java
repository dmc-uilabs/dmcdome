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
 * Time: 11:26:08 AM
 * To change this template use Options | File Templates.
 */
public class IdeasImportPart extends AbstractPluginData
{
	public static final String CLASS = "IdeasImportPart";
	public static final String GTSCH = "IdeasImportPart::getChanged";
	public static final String STSCH = "IdeasImportPart::setChanged";
	public static final String GTNAME = "IdeasImportPart::getName";
	public static final String FILENAME = "IdeasImportPart::getFileName";

	private Vector data;
	private IdeasNativeCaller caller;
	private long exImPtObj;   //C++ object
	boolean isResult;
	private BooleanData isChanged;
	private StringData Name;
	private StringData FileName;

	public IdeasImportPart(IdeasNativeCaller caller, long modelPtr, Parameter p, String fileName)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileName;
		exImPtObj = caller.callObjectFunc(IdeasPlugin.MODEL, modelPtr, IdeasPlugin.CREATEIPT, arr);
		if (exImPtObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exImPtObj == -2) {
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
		if (exImPtObj != 0) {
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
		if (exImPtObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callBoolFunc(CLASS, exImPtObj, GTSCH, null);
	}

	public void setIsChanged(boolean value)
	{
		isChanged.setValue(value);
	}

	public void setIsChanged(boolean value, boolean isNativeCall)
	{
		if (exImPtObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		Object[] arr = new Object[1];
		arr[0] = new Boolean(value);
		caller.callVoidFunc("IdeasDimensionOut", exImPtObj, STSCH, arr);
	}

	public String getName()
	{
		return Name.getValue();
	}

	// get value from native object
	public String getName(boolean isNativeCall)
	{
		if (exImPtObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callStringFunc(CLASS, exImPtObj, GTNAME, null);
	}

	public String getFileName()
	{
		return FileName.getValue();
	}

	// get value from native object
	public String getFileName(boolean isNativeCall)
	{
		if (exImPtObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exImPtObj, FILENAME, null);
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

	public void resetObjectPointer() {
		exImPtObj = 0;
	}
}
