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
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabEnumerationNew;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabFile;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabMatrixNew;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabRealNew;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MatlabPluginNew extends AbstractPlugin
{
	private String file;
	private boolean isVisible;
	private boolean isExecutable;
	private Vector data;
	private MatlabPluginCaller caller;
	private long modelPtr;
	public boolean waitingToDie = false;

	public MatlabPluginNew(String libname, String file, boolean isVisible, boolean isExecutable)
	        throws UnsatisfiedLinkError
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		this.isExecutable = isExecutable;
		data = new Vector();
		String delmePath = System.getProperty("java.library.path");
		System.out.println(delmePath);
		try {
			System.out.println("MatlabPluginNew Before loading DLL" + libname );

			System.loadLibrary(libname);
		} catch (UnsatisfiedLinkError e) {
		    e.printStackTrace();
			throw new UnsatisfiedLinkError(
			        "MatlabPluginNew: A link error occured when trying to load " + libname + ".DLL.\n\n"
			        + "Check the installed version of Matlab against the version specified in the model.\n"
			        + "Check also that the directory containing the DLLs is in your path.");

		}
		caller = new MatlabPluginCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[3];
		arr[0] = file;
		arr[1] = new Boolean(isVisible);
		arr[2] = new Boolean(isExecutable);
		modelPtr = caller.callObjectFunc(0, MatlabPluginCaller.MODEL_INIT, arr);
	}

	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, MatlabPluginCaller.MODEL_DESTROY, null);
		modelPtr = 0;
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, MatlabPluginCaller.MODEL_IS_LOADED, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc(modelPtr, MatlabPluginCaller.MODEL_LOAD, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, MatlabPluginCaller.MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, MatlabPluginCaller.MODEL_EXEC_AF_INPUT, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(modelPtr, MatlabPluginCaller.MODEL_UNLOAD, null);
	}

	public Object createReal(String name)
	{
		return createReal(name, null);
	}

	public MatlabRealNew createReal(String name, Parameter realParam)
	{
		MatlabRealNew real = new MatlabRealNew(caller, modelPtr, name, realParam);
		data.addElement(real);
		return real;
	}

	public Object createMatrix(String name, int rows, int cols)
	{
		return createMatrix(name, rows, cols, null);
	}

	public MatlabMatrixNew createMatrix(String name, int rows, int cols, Parameter matrixParam)
	{
		MatlabMatrixNew matrix = new MatlabMatrixNew(caller, modelPtr, name, rows, cols, matrixParam);
		data.addElement(matrix);
		return matrix;
	}

	public int[][] testIntMatrix(int[][] arr)
	{
		Object[] args = new Object[]{arr, new Integer(arr.length), new Integer(arr[0].length)};
		int[][] ret = caller.call2DimIntArrayFunc(modelPtr, MatlabPluginCaller.MATRIX_TEST_INT_MATRIX, args);
		return ret;
	}

	public Object createEnumeration(String name)
	{
		return createEnumeration(name, null);
	}

	public MatlabEnumerationNew createEnumeration(String name, Parameter enumParam)
	{
        MatlabEnumerationNew enm = new MatlabEnumerationNew(caller, modelPtr, name, enumParam);
        data.addElement(enm);
        return enm;
	}

	public MatlabFile createFile(Parameter fileParam, boolean isResult)
	{
		MatlabFile file = new MatlabFile(fileParam, isResult);
		data.addElement(file);
		return file;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof MatlabEnumerationNew) {
				if (!((MatlabEnumerationNew) obj).getIsResult())
					((MatlabEnumerationNew) obj).loadNativeData();
			} else if (obj instanceof MatlabRealNew) {
				if (!((MatlabRealNew) obj).getIsResult())
					((MatlabRealNew) obj).loadNativeData();
			} else if (obj instanceof MatlabMatrixNew) {
				if (!((MatlabMatrixNew) obj).getIsResult())
					((MatlabMatrixNew) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(modelPtr, MatlabPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++) {
			if (waitingToDie)
				return; // do not set outputs if waiting to die
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof MatlabEnumerationNew) {
					if (((MatlabEnumerationNew) obj).getIsResult()) {
						((MatlabEnumerationNew) obj).loadJavaData();
					}
				} else if (obj instanceof MatlabRealNew) {
					if (((MatlabRealNew) obj).getIsResult()) {
						((MatlabRealNew) obj).loadJavaData();
					}
				} else if (obj instanceof MatlabMatrixNew) {
					if (((MatlabMatrixNew) obj).getIsResult()) {
						((MatlabMatrixNew) obj).loadJavaData();
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

