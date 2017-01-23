package mit.cadlab.dome3.plugin.ideas.dataobject;

import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;
import mit.cadlab.dome3.plugin.ideas.IdeasPlugin;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Feb 13, 2003
 * Time: 12:14:41 AM
 * To change this template use Options | File Templates.
 */
public class IdeasVrmlFile extends AbstractPluginData
{
	public static final String CLASS = "IdeasVrmlFile";

	private Vector data;
	private IdeasNativeCaller caller;
	private long exVrmlObj;   //C++ object
	boolean isResult;

	public IdeasVrmlFile(IdeasNativeCaller caller, long modelPtr, Parameter p, String fileName)
	{
		this.caller = caller;
		this.parameter = p;

		String[] arr = new String[1];
		arr[0] = fileName;
		exVrmlObj = caller.callObjectFunc(IdeasPlugin.MODEL, modelPtr, IdeasPlugin.CREATEVRML, arr);
		if (exVrmlObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exVrmlObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		isResult = true;
	}

	public void destroy()
	{
		if (exVrmlObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exVrmlObj);
			caller.callDestructor(CLASS, exVrmlObj);
			exVrmlObj = 0;
		}
	}

	public void resetObjectPointer() {
		exVrmlObj = 0;
	}

	public boolean getIsResult()
	{
		return isResult;
	}
}
