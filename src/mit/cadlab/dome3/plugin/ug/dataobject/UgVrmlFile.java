package mit.cadlab.dome3.plugin.ug.dataobject;

import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.ug.UgPlugin;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 14, 2003
 * Time: 1:59:09 PM
 * To change this template use Options | File Templates.
 */
public class UgVrmlFile extends AbstractPluginData
{
	public static final String CLASS = "UgVrmlFile";

	private Vector data;
	private UgNativeCaller caller;
	private long exVrmlObj;   //C++ object
	boolean isResult;

	public UgVrmlFile(UgNativeCaller caller, long modelPtr, String fileName, Parameter p)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileName;
		exVrmlObj = caller.callObjectFunc(UgPlugin.MODEL, modelPtr, UgPlugin.CREATEVRML, arr);
		if (exVrmlObj == -1)
		{
			throw new RuntimeException("Argument number mismatch");
		}
		else if (exVrmlObj == -2)
		{
			throw new RuntimeException("Argument type mismatch");
		}
		isResult = true;
	}

	public void destroy()
	{
		if (exVrmlObj != 0)
		{
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exVrmlObj);
			caller.callDestructor(CLASS, exVrmlObj);
			exVrmlObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	public void resetObjectPointer()
	{
		exVrmlObj = 0;
	}
}
