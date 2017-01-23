/**
 * Created Jacob Wronski.
 * User: wronski
 * Date: Nov 27, 2002
 * Time: 5:15:06 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin.catia;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.catia.dataobject.CATIABoolean;
import mit.cadlab.dome3.plugin.catia.dataobject.CATIAFile;
import mit.cadlab.dome3.plugin.catia.dataobject.CATIAInteger;
import mit.cadlab.dome3.plugin.catia.dataobject.CATIAReal;
import mit.cadlab.dome3.plugin.catia.dataobject.CATIAString;
import mit.cadlab.dome3.plugin.catia.dataobject.CATIAVector;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class CATIAPlugin extends AbstractPlugin
{
	private Vector data;
	private CATIAPluginCaller caller;
	private long modelPtr;
	private String file;
	private boolean isVisible;
    private static Integer numberModelsRemaining = new Integer(0);
	public boolean waitingToDie = false;

	public CATIAPlugin(String libname, String file, boolean isVisible)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		data = new Vector();
		System.loadLibrary(libname);
		caller = new CATIAPluginCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = file;
		arr[1] = new Boolean(isVisible);
		modelPtr = caller.callObjectFunc(0, CATIAPluginCaller.MODEL_INIT, arr);
	}

	public void loadModel()
	{
        synchronized (numberModelsRemaining) {
            caller.callVoidFunc(modelPtr, CATIAPluginCaller.MODEL_LOAD, null);
            numberModelsRemaining = new Integer(numberModelsRemaining.intValue()+1);
        }
    }

	public void unloadModel()
	{
        synchronized (numberModelsRemaining) {
	        boolean isLoaded = this.isModelLoaded();
            Object[] arr = {numberModelsRemaining};
            caller.callVoidFunc(modelPtr, CATIAPluginCaller.MODEL_UNLOAD, arr);
	        //this is because numberModelsRemaining only increases when loadModel is called
	        if(isLoaded) numberModelsRemaining = new Integer(numberModelsRemaining.intValue()-1);
        }
    }

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, CATIAPluginCaller.MODEL_IS_LOADED, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, CATIAPluginCaller.MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, CATIAPluginCaller.MODEL_EXEC_AF_INPUT, null);
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof CATIAReal) {
				if (!((CATIAReal) obj).getIsResult())
					((CATIAReal) obj).loadNativeData();
			}
			else
				if (obj instanceof CATIAInteger) {
					if (!((CATIAInteger) obj).getIsResult())
						((CATIAInteger) obj).loadNativeData();
				}
				else
					if (obj instanceof CATIAString) {
						if (!((CATIAString) obj).getIsResult())
							((CATIAString) obj).loadNativeData();
					}
					else
						if (obj instanceof CATIABoolean) {
							if (!((CATIABoolean) obj).getIsResult())
								((CATIABoolean) obj).loadNativeData();
						}
						else
							if (obj instanceof CATIAVector) {
								if (!((CATIAVector) obj).getIsResult())
									((CATIAVector) obj).loadJavaData();
							}
		}

		int result = caller.callIntFunc(modelPtr, CATIAPluginCaller.MODEL_EXECUTE, null);

		for (int i = 0; i < data.size(); i++) {
			if (waitingToDie)
				return; // do not set outputs if waiting to die
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof CATIAReal) {
					if (((CATIAReal) obj).getIsResult())
						((CATIAReal) obj).loadJavaData();
				}
				else
					if (obj instanceof CATIAInteger) {
						if (((CATIAInteger) obj).getIsResult())
							((CATIAInteger) obj).loadJavaData();
					}
					else
						if (obj instanceof CATIAString) {
							if (((CATIAString) obj).getIsResult())
								((CATIAString) obj).loadJavaData();
						}
						else
							if (obj instanceof CATIABoolean) {
								if (((CATIABoolean) obj).getIsResult())
									((CATIABoolean) obj).loadJavaData();
							}
							else
								if (obj instanceof CATIAVector) {
									if (((CATIAVector) obj).getIsResult())
										((CATIAVector) obj).loadJavaData();
								}
			}
		}
	}

	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, CATIAPluginCaller.MODEL_DESTROY, null);
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public CATIAReal createReal(String name, Parameter dReal,
	                            String libName, int libArgIndex)
	{
		CATIAReal real = new CATIAReal(caller, modelPtr, name, dReal, libName, libArgIndex);
		data.addElement(real);
		return real;
	}

	public CATIAString createString(String name, Parameter dString,
	                                String libName, int libArgIndex)
	{
		CATIAString str = new CATIAString(caller, modelPtr, name, dString, libName, libArgIndex);
		data.addElement(str);
		return str;
	}

	public CATIAInteger createInteger(String name, Parameter dInteger,
	                                  String libName, int libArgIndex)
	{
		CATIAInteger integer = new CATIAInteger(caller, modelPtr, name, dInteger,
		                                        libName, libArgIndex);
		data.addElement(integer);
		return integer;
	}

	public CATIABoolean createBoolean(String name, Parameter dBool,
	                                  String libName, int libArgIndex)
	{
		CATIABoolean bool = new CATIABoolean(caller, modelPtr, name, dBool,
		                                     libName, libArgIndex);
		data.addElement(bool);
		return bool;
	}

	public CATIAVector createVector(String name, Parameter vec,
	                                String libName, int libArgIndex)
	{
		CATIAVector vector = new CATIAVector(caller, modelPtr, name, vec,
		                                     libName, libArgIndex);
		data.addElement(vector);
		return vector;
	}

	public CATIAFile createFile(String name, String type, Parameter dFile)
	{
		CATIAFile file = new CATIAFile(caller, modelPtr, name, type, dFile);
		data.addElement(file);
		return file;
	}

	public void setupUserLibrary (String libName, int numArguments)
	{
		Object[] arr = new Object[2];
		arr[0] = libName;
		arr[1] = new Integer (numArguments);
		caller.callVoidFunc(modelPtr, CATIAPluginCaller.MODEL_SETUP_USERLIB, arr);
	}
}

