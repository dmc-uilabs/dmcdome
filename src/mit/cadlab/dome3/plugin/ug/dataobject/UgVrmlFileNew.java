package mit.cadlab.dome3.plugin.ug.dataobject;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ug.UgPluginCaller;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2005
 * Time: 1:55:25 PM
 * To change this template use Options | File Templates.
 */
public class UgVrmlFileNew extends AbstractPluginData
{
	private Vector data;
	private UgPluginCaller caller;
	private long exVrmlObj;   //C++ object
	boolean isResult;

	public UgVrmlFileNew(UgPluginCaller caller, long modelPtr, String fileName, Parameter p)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileName;
		exVrmlObj = caller.callObjectFunc(modelPtr, UgPluginCaller.MODEL_CREATE_VRMLFILE, arr);
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
			//caller.callDestructor(CLASS, exVrmlObj);
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
