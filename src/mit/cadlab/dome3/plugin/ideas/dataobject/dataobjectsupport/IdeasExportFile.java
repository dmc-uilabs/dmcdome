package mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Feb 13, 2003
 * Time: 12:14:41 AM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;

import java.util.Vector;


public class IdeasExportFile  extends AbstractPluginData
{
	public static final String CLASS = "IdeasExportFile";

	private Vector data;
	private IdeasNativeCaller caller;
	private long exExportObj;   //C++ object
	private boolean isResult;

	public IdeasExportFile(IdeasNativeCaller caller, long modelPtr, String fileExportType, String PartOrAssembly, String classPorA)
	{
		this.caller = caller;
		String[] arr = new String[1];
		arr[0] = fileExportType;
		exExportObj = caller.callObjectFunc(classPorA, modelPtr, PartOrAssembly, arr);
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
			caller.callDestructor(CLASS, exExportObj);
			exExportObj = 0;
		}
	}

	public void resetObjectPointer()
	{
		exExportObj = 0;
	}

	public boolean getIsResult()
	{
		return isResult;
	}
}