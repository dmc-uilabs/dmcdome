package mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport;

import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 15, 2003
 * Time: 1:48:04 AM
 * To change this template use Options | File Templates.
 */
public class UgExportFile extends AbstractPluginData
{
	public static final String CLASS = "IdeasExportFile";

	private Vector data;
	private UgNativeCaller caller;
	private long exExportObj;   //C++ object
	private boolean isResult;

	public UgExportFile(UgNativeCaller caller, long modelPtr, String fileExportType, Parameter p)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileExportType;
		exExportObj = caller.callObjectFunc(UgComponent.CLASS, modelPtr, UgComponent.CREATEEXPORTFILE, arr);
		if (exExportObj == -1)
		{
			throw new RuntimeException("Argument number mismatch");
		}
		else if (exExportObj == -2)
		{
			throw new RuntimeException("Argument type mismatch");
		}
		this.isResult = false;
	}
	public void destroy()
	{
		if (exExportObj != 0)
		{
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exExportObj);
			caller.callDestructor(CLASS, exExportObj);
			exExportObj = 0;
		}
	}
	public boolean getIsResult()
	{
		return isResult;
	}

	public void resetObjectPointer() {
		exExportObj = 0;
	}
}
