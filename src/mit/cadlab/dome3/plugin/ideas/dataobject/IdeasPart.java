package mit.cadlab.dome3.plugin.ideas.dataobject;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Jan 21, 2003
 * Time: 5:52:15 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;
import mit.cadlab.dome3.plugin.ideas.IdeasPlugin;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasDimensionIn;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasDimensionOut;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasExportFile;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasMassProperty;

import java.util.Vector;

public class IdeasPart extends AbstractPluginData
{

	public static final String CLASS = "IdeasPart";
	public static final String GTCOMP = "IdeasPart::getCompName";
	public static final String GTBIN = "IdeasPart::getBinName";
	public static final String CREATEMP = "IdeasPart::createMassProperty";
	public static final String CREATEEXPORTFILE = "IdeasPart::createExportFile";
	public static final String CREATEDIMIN = "IdeasPart::createDimensionIn";
	public static final String CREATEDIMOUT = "IdeasPart::createDimensionOut";

	private Vector data;
	private IdeasNativeCaller caller;
	private long exPartObj;   //C++ object
	private StringData dataComp;
	private StringData dataBin;
	private boolean isResult;

	public IdeasPart(IdeasNativeCaller caller, long modelPtr, Parameter p, String bin, String compName)
	{
		this.caller = caller;
		this.parameter = p;

		String[] arr = new String[2];
		arr[0] = bin;
		arr[1] = compName;
		exPartObj = caller.callObjectFunc(IdeasPlugin.MODEL, modelPtr, IdeasPlugin.CREATEPART, arr);
		if (exPartObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exPartObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		dataComp = new StringData();
		dataBin = new StringData();
		data = new Vector();
		isResult = true;
	}

	public void destroy()
	{
		if (exPartObj != 0) {
			/*
			 * This .callDestructor() method, does it have to
			 * be implemented on the native side?
			 */
			System.out.println("destroy peerobj = " + exPartObj);
			caller.callDestructor(CLASS, exPartObj);
			exPartObj = 0;
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
		if (exPartObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		/*
		 * I don't think that NativeCaller.callStringFunc()
		 * has been implemented in the NativeCaller.cpp method
		 */
		return caller.callStringFunc(CLASS, exPartObj, GTCOMP, null);
	}

	public String getBinName()
	{
		return dataBin.getValue();
	}

	// get value from native object
	public String getBinName(boolean isNativeCall)
	{
		if (exPartObj == 0) {
			throw new IllegalStateException("getValue called on destroyed object");
		}
		return caller.callStringFunc(CLASS, exPartObj, GTBIN, null);
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
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof IdeasDimensionIn) {
				if (!((IdeasDimensionIn) obj).getIsResult()) ((IdeasDimensionIn) obj).loadNativeData();
			}
		}
		caller.callVoidFunc("IdeasModel", modelPtr, "IdeasModel::execute", null);
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof IdeasMassProperty) {
				if (((IdeasMassProperty) obj).getIsResult()) ((IdeasMassProperty) obj).loadJavaData();
			}
			if (obj instanceof IdeasDimensionIn) {
				if (((IdeasDimensionIn) obj).getIsResult()) ((IdeasDimensionIn) obj).loadJavaData();
			}
			if (obj instanceof IdeasDimensionOut) {
				if (((IdeasDimensionOut) obj).getIsResult()) ((IdeasDimensionOut) obj).loadJavaData();
			}
		}
	}

	public Object createMassProperty(String property)
	{
		return this.createMassProperty(property, null);
	}

	public IdeasMassProperty createMassProperty(String property, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal real)
	{
		IdeasMassProperty part = new IdeasMassProperty(caller, exPartObj, property, CREATEMP, CLASS, real);
		data.addElement(part);
		return part;
	}

	public Object createExportFile(String fileType)
	{
		Object _ptexp = new IdeasExportFile(caller, exPartObj, fileType, CREATEEXPORTFILE, CLASS);
		data.addElement(_ptexp);
		return _ptexp;
	}

	public Object createDimensionOut(String dimName)
	{
		return this.createDimensionOut(dimName, null);
	}

	public IdeasDimensionOut createDimensionOut(String dimName, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal real)
	{
		IdeasDimensionOut _ptdimOut = new IdeasDimensionOut(caller, exPartObj, dimName, CREATEDIMOUT, real);
		data.addElement(_ptdimOut);
		return _ptdimOut;
	}

	public Object createDimensionIn(String dimName)
	{
		return this.createDimensionIn(dimName, null);
	}

	public IdeasDimensionIn createDimensionIn(String dimName, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal real)
	{
		IdeasDimensionIn _ptdimIn = new IdeasDimensionIn(caller, exPartObj, dimName, CREATEDIMIN, real);
		data.addElement(_ptdimIn);
		return _ptdimIn;
	}

	public void resetObjectPointer()
	{
		exPartObj = 0;
	}
}





