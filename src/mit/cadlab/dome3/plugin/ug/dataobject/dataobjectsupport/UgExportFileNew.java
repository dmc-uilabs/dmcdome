package mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ug.UgPluginCaller;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2005
 * Time: 4:55:08 PM
 * To change this template use Options | File Templates.
 */
public class UgExportFileNew extends AbstractPluginData
{
	private Vector data;
	private UgPluginCaller caller;
	private long exExportObj;   //C++ object
	private boolean isResult;

	public UgExportFileNew(UgPluginCaller caller, long modelPtr, String fileExportType, Parameter p)
	{
		this.caller = caller;
		this.parameter = p;
		String[] arr = new String[1];
		arr[0] = fileExportType;
		exExportObj = caller.callObjectFunc(modelPtr, UgPluginCaller.COMPONENT_CREATE_EXPORTFILE, arr);
		if (exExportObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exExportObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		this.isResult = false;
	}

	public void destroy()
	{
		if (exExportObj != 0) {
			/*
			* This .callDestructor() method, does it have to
			* be implemented on the native side?
			*/
			System.out.println("destroy peerobj = " + exExportObj);
			//caller.callDestructor(CLASS, exExportObj);
			exExportObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	public void resetObjectPointer()
	{
		exExportObj = 0;
	}
}
