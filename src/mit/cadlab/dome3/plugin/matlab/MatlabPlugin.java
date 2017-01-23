/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 22, 2002
* Time: 11:26:08 AM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/

package mit.cadlab.dome3.plugin.matlab;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.MatlabNativeCaller;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.matlab.dataobject.*;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MatlabPlugin extends AbstractPlugin
{
	// see MatlabNativeWorker.h for integer values
	public static final int MODEL_INIT          = 0; // "MatlabModel::MatlabModel";
	public static final int MODEL_LOAD          = 1; // "MatlabModel::loadModel";
	public static final int MODEL_UNLOAD        = 2; // "MatlabModel::unloadModel";
	public static final int MODEL_IS_LOADED     = 3; // "MatlabModel::isModelLoaded";
	public static final int MODEL_EXEC          = 4; // "MatlabModel::execute";
	public static final int MODEL_EXEC_BF_INPUT = 5; // "MatlabModel::executeBeforeInput";
	public static final int MODEL_EXEC_AF_INPUT = 6; // "MatlabModel::executeAfterOutput";
	public static final int MODEL_CREATE_REAL   = 7; // "MatlabModel::createReal";
	public static final int MODEL_CREATE_MATRIX = 8; // "MatlabModel::createMatrix";
	public static final int MODEL_TEST_INT_MTX  = 9; // "MatlabModel::testIntMatrix";

	// only one MATLAB model can run at a time since they all share the same MATLAB instance
	// and MATLAB will get confused if you try to run two at once
	private static Object MATLAB_LOCK = new Object();

	private String file;
	private boolean isVisible;
	private boolean isExecutable;
	private Vector data;
	private MatlabNativeCaller caller;
	private long modelPtr;
	public boolean waitingToDie = false;

	public MatlabPlugin(String libname, String file, boolean isVisible, boolean isExecutable)
		throws UnsatisfiedLinkError
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		this.isExecutable = isExecutable;
		data = new Vector();
		try {
			System.out.println("MatlabPlugin Before loading DLL" + libname );

			System.loadLibrary(libname);
		}
		catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError (
			        "MatlabPluginOld: A link error occured when trying to load " + libname + ".dll.\n" +
			        e+"\n"
			        + "Check the installed version of Matlab against the version specified in the model.\n"
					+ "Check also that the directory containing the DLLs is in your path.");
		}
		caller = new MatlabNativeCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[3];
		arr[0] = file;
		arr[1] = new Boolean(isVisible);
		arr[2] = new Boolean(isExecutable);
		modelPtr = caller.callConstructor(MatlabDataObjects.CLASS_MODEL, arr);
	}

	//TODO  deleteModel should explicitly destroy all the native objects
	public void deleteModel()
	{
		caller.callDestructor(MatlabDataObjects.CLASS_MODEL, modelPtr);
		modelPtr = 0;
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData)iterator.next();
            dat.resetObjectPointer();
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(MatlabDataObjects.CLASS_MODEL, modelPtr, MODEL_IS_LOADED, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc(MatlabDataObjects.CLASS_MODEL, modelPtr, MODEL_LOAD, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(MatlabDataObjects.CLASS_MODEL, modelPtr, MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(MatlabDataObjects.CLASS_MODEL, modelPtr, MODEL_EXEC_AF_INPUT, null);
	}

	public int[][] testIntMatrix(int[][] arr)
	{
		Object[] args = new Object[]{arr, new Integer(arr.length), new Integer(arr[0].length)};
		int[][] ret = caller.call2DimIntArrayFunc(MatlabDataObjects.CLASS_MODEL,
                                                  modelPtr, MODEL_TEST_INT_MTX, args);
		return ret;
	}

	public void unloadModel()
	{
		caller.callVoidFunc(MatlabDataObjects.CLASS_MODEL, modelPtr, MODEL_UNLOAD, null);
	}

	public Object createReal(String name)
	{
		return createReal(name, null);
	}

	public MatlabReal createReal(String name, Parameter realParam)
	{
		MatlabReal real = new MatlabReal(caller, modelPtr, name, realParam);
		data.addElement(real);
		return real;
	}

	public Object createMatrix(String name, int rows, int cols)
	{
		return createMatrix(name, rows, cols, null);
	}

    public MatlabMatrix createMatrix(String name, int rows, int cols, Parameter matrixParam) {
        MatlabMatrix matrix = new MatlabMatrix(caller, modelPtr, name, rows, cols, matrixParam);
        data.addElement(matrix);
        return matrix;
    }

    public Object createEnumeration(String name) {
        return createEnumeration(name, null);
    }

    public MatlabEnumeration createEnumeration(String name, Parameter enumParam) {
        MatlabEnumeration enm = new MatlabEnumeration(caller, modelPtr, name, enumParam);
        data.addElement(enm);
        return enm;
    }

    public MatlabFile createFile(Parameter fileParam, boolean isResult) {
        MatlabFile file = new MatlabFile(fileParam, isResult);
        data.addElement(file);
        return file;
    }

	public void execute(List affectedOutputParams)
	{
		synchronized (MATLAB_LOCK) {
			for (int i = 0; i < data.size(); i++) {
				Object obj = data.get(i);
	            if (obj instanceof MatlabEnumeration) {
	                if (!((MatlabEnumeration) obj).getIsResult())
	                    ((MatlabEnumeration) obj).loadNativeData();
	            } else if (obj instanceof MatlabReal) {
					if (!((MatlabReal) obj).getIsResult())
						((MatlabReal) obj).loadNativeData();
				} else if (obj instanceof MatlabMatrix) {
					if (!((MatlabMatrix) obj).getIsResult())
						((MatlabMatrix) obj).loadNativeData();
				}
			}
			caller.callVoidFunc(MatlabDataObjects.CLASS_MODEL, modelPtr, MODEL_EXEC, null);
			for (int i = 0; i < data.size(); i++) {
				if (waitingToDie)
					return; // do not set outputs if waiting to die
				PluginData obj = (PluginData) data.get(i);
				if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
					if (obj instanceof MatlabEnumeration) {
						if (((MatlabEnumeration) obj).getIsResult()) {
							((MatlabEnumeration) obj).loadJavaData();
						}
					} else if (obj instanceof MatlabReal) {
						if (((MatlabReal) obj).getIsResult()) {
							((MatlabReal) obj).loadJavaData();
						}
					} else if (obj instanceof MatlabMatrix) {
						if (((MatlabMatrix) obj).getIsResult()) {
							((MatlabMatrix) obj).loadJavaData();
						}
					} else if (obj instanceof MatlabFile) {
                        if (((MatlabFile) obj).getIsResult()) {
                            ((MatlabFile) obj).notifyFileChanged();
                        }
                    }
				}
			}
		}
	}

}
