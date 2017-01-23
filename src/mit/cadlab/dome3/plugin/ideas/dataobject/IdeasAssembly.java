package mit.cadlab.dome3.plugin.ideas.dataobject;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Jan 20, 2003
 * Time: 11:05:31 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;
import mit.cadlab.dome3.plugin.ideas.IdeasPlugin;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasExportFile;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasMassProperty;

import java.util.Vector;

public class IdeasAssembly extends AbstractPluginData
{
	public static final String CLASS = "IdeasAssembly";
	public static final String GTCOMP = "IdeasAssembly::getCompName";
	public static final String GTBIN = "IdeasAssembly::getBinName";
	public static final String CREATEMP = "IdeasAssembly::createMassProperty";
	public static final String CREATEEXPORTFILE = "IdeasAssembly::createExportFile";

	private Vector data;
	private IdeasNativeCaller caller;
	private long exAssemblyObj;   //C++ object
	private StringData dataComp;
	private StringData dataBin;
	private boolean isResult;

	// constructor used to create IdeasAssembly object !

	public IdeasAssembly(IdeasNativeCaller caller, long modelPtr, Parameter p , String bin, String compName)
	{
		this.caller = caller;
		this.parameter = p;

		String[] arr = new String[2];
		arr[0] = bin;
		arr[1] = compName;
		exAssemblyObj = caller.callObjectFunc(IdeasPlugin.MODEL, modelPtr, IdeasPlugin.CREATEASSEM, arr);
		if (exAssemblyObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exAssemblyObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		dataComp = new StringData();
		dataBin = new StringData();
		data = new Vector();
		isResult = true;
	}

	public void destroy()
	{
		if (exAssemblyObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exAssemblyObj);
			caller.callDestructor(CLASS, exAssemblyObj);
			exAssemblyObj = 0;
		}
	}

	public boolean getIsResult()
	{
		return isResult;
	}

	// get value from java object
	public String getCompName()
	{
		return dataComp.getValue();
	}

	// get value from native object
	public String getCompName(boolean isNativeCall)
	{
		if (exAssemblyObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callStringFunc(CLASS, exAssemblyObj, GTCOMP, null);
	}

	public String getBinName()
	{
		return dataBin.getValue();
	}

	// get value from native object
	public String getBinName(boolean isNativeCall)
	{
		if (exAssemblyObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callStringFunc(CLASS, exAssemblyObj, GTBIN, null);
	}

	public void setCompName(String value)
	{
		dataComp.setValue(value);
	}

	public void setBinName(String value)
	{
		dataBin.setValue(value);
	}

	public void loadJavaData(long modelPtr)
	{
		setCompName(getCompName(true));
		setBinName(getBinName(true));
		caller.callVoidFunc("IdeasModel", modelPtr, "IdeasModel::execute", null);
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof IdeasMassProperty) {
				if (((IdeasMassProperty) obj).getIsResult()) ((IdeasMassProperty) obj).loadJavaData();
			}
		}
	}

	public Object createMassProperty(String property)
	{
		return this.createMassProperty(property, null);
	}

	public IdeasMassProperty createMassProperty(String property, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal real)
	{
		IdeasMassProperty assmp = new IdeasMassProperty(caller, exAssemblyObj, property, CREATEMP, CLASS, real);
		data.addElement(assmp);
		return assmp;
	}

	public Object createExportFile(String fileType)
	{
		Object _assexp = new IdeasExportFile(caller, exAssemblyObj, fileType, CREATEEXPORTFILE, CLASS);
		data.addElement(_assexp);
		return _assexp;
	}

	public void resetObjectPointer()
	{
		exAssemblyObj = 0;
	}
}



