/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 22, 2002
* Time: 11:26:08 AM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/

package mit.cadlab.dome3.plugin.mathcad;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadEnumerationNew;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadFile;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadMatrixNew;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadRealNew;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MathcadPluginNew extends AbstractPlugin
{
	private String file;
	private boolean isVisible;
	private boolean isExecutable;
	private Vector data;
	private MathcadPluginCaller caller;
	private long modelPtr;
	public boolean waitingToDie = false;

	public MathcadPluginNew(String libname, String file, boolean isVisible, boolean isExecutable)
	        throws UnsatisfiedLinkError
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		this.isExecutable = isExecutable;
		data = new Vector();
		try {
			System.loadLibrary(libname);
		} catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError(
			        "A link error occured when trying to load " + libname + ".DLL.\n\n"
			        + "Check the installed version of Mathcad against the version specified in the model.\n"
			        + "Check also that the directory containing the DLLs is in your path.");
		}
		caller = new MathcadPluginCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[3];
		arr[0] = file;
		arr[1] = new Boolean(isVisible);
		arr[2] = new Boolean(isExecutable);
		modelPtr = caller.callObjectFunc(0, MathcadPluginCaller.MODEL_INIT, arr);
	}

	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, MathcadPluginCaller.MODEL_DESTROY, null);
		modelPtr = 0;
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, MathcadPluginCaller.MODEL_IS_LOADED, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc(modelPtr, MathcadPluginCaller.MODEL_LOAD, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, MathcadPluginCaller.MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, MathcadPluginCaller.MODEL_EXEC_AF_INPUT, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(modelPtr, MathcadPluginCaller.MODEL_UNLOAD, null);
	}

	public Object createReal(String name)
	{
		return createReal(name, null);
	}

	public MathcadRealNew createReal(String name, Parameter realParam)
	{
		MathcadRealNew real = new MathcadRealNew(caller, modelPtr, name, realParam);
		data.addElement(real);
		return real;
	}

	public Object createMatrix(String name, int rows, int cols)
	{
		return createMatrix(name, rows, cols, null);
	}

	public MathcadMatrixNew createMatrix(String name, int rows, int cols, Parameter matrixParam)
	{
		MathcadMatrixNew matrix = new MathcadMatrixNew(caller, modelPtr, name, rows, cols, matrixParam);
		data.addElement(matrix);
		return matrix;
	}

	public int[][] testIntMatrix(int[][] arr)
	{
		Object[] args = new Object[]{arr, new Integer(arr.length), new Integer(arr[0].length)};
		int[][] ret = caller.call2DimIntArrayFunc(modelPtr, MathcadPluginCaller.MATRIX_TEST_INT_MATRIX, args);
		return ret;
	}

	public Object createEnumeration(String name)
	{
		return createEnumeration(name, null);
	}

	public MathcadEnumerationNew createEnumeration(String name, Parameter enumParam)
	{
        MathcadEnumerationNew enm = new MathcadEnumerationNew(caller, modelPtr, name, enumParam);
        data.addElement(enm);
        return enm;
	}

	public MathcadFile createFile(Parameter fileParam, boolean isResult)
	{
		MathcadFile file = new MathcadFile(fileParam, isResult);
		data.addElement(file);
		return file;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof MathcadEnumerationNew) {
				if (!((MathcadEnumerationNew) obj).getIsResult())
					((MathcadEnumerationNew) obj).loadNativeData();
			} else if (obj instanceof MathcadRealNew) {
				if (!((MathcadRealNew) obj).getIsResult())
					((MathcadRealNew) obj).loadNativeData();
			} else if (obj instanceof MathcadMatrixNew) {
				if (!((MathcadMatrixNew) obj).getIsResult())
					((MathcadMatrixNew) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(modelPtr, MathcadPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++) {
			if (waitingToDie)
				return; // do not set outputs if waiting to die
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof MathcadEnumerationNew) {
					if (((MathcadEnumerationNew) obj).getIsResult()) {
						((MathcadEnumerationNew) obj).loadJavaData();
					}
				} else if (obj instanceof MathcadRealNew) {
					if (((MathcadRealNew) obj).getIsResult()) {
						((MathcadRealNew) obj).loadJavaData();
					}
				} else if (obj instanceof MathcadMatrixNew) {
					if (((MathcadMatrixNew) obj).getIsResult()) {
						((MathcadMatrixNew) obj).loadJavaData();
					}
				} else if (obj instanceof MathcadFile) {
					if (((MathcadFile) obj).getIsResult()) {
						((MathcadFile) obj).notifyFileChanged();
					}
				}
			}
		}
	}

}

