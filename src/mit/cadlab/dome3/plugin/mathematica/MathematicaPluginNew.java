package mit.cadlab.dome3.plugin.mathematica;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaIntegerNew;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaMatrixNew;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaRealNew;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaVectorNew;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2005
 * Time: 2:59:39 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaPluginNew extends AbstractPlugin
{
	private String file;
	private boolean isVisible;
	private boolean isExecutable;
	Vector data;
	MathematicaPluginCaller caller;
	long modelPtr;
	public boolean waitingToDie = false;

	public MathematicaPluginNew(String libname, String file, boolean isVisible, boolean isExecutable)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		this.isExecutable = isExecutable;
		data = new Vector();
		System.loadLibrary(libname);
		caller = new MathematicaPluginCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[3];
		arr[0] = file;
		arr[1] = new Boolean(isVisible);
		arr[2] = new Boolean(isExecutable);
		modelPtr = caller.callObjectFunc(0, MathematicaPluginCaller.MODEL_INIT, arr);
	}

	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, MathematicaPluginCaller.MODEL_DESTROY, null);
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, MathematicaPluginCaller.MODEL_IS_LOADED, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc( modelPtr, MathematicaPluginCaller.MODEL_LOAD, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, MathematicaPluginCaller.MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, MathematicaPluginCaller.MODEL_EXEC_AF_INPUT, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(modelPtr, MathematicaPluginCaller.MODEL_UNLOAD, null);
	}


	public Object createReal(String name)
	{
		return createReal(name, null);
	}

	public MathematicaRealNew createReal(String name, Parameter dReal)
	{
		MathematicaRealNew real = new MathematicaRealNew(caller, modelPtr, name, dReal);
		data.addElement(real);
		//System.out.println("real: " +real);
		return real;
	}

	public Object createInteger(String name)
	{
		return createInteger(name, null);
	}

	public MathematicaIntegerNew createInteger(String name, Parameter dInteger)
	{
		MathematicaIntegerNew integer = new MathematicaIntegerNew(caller, modelPtr, name, dInteger);
		data.addElement(integer);
		//System.out.println("real: " +real);
		return integer;
	}

	public Object createMatrix(String name, int rows, int cols)
	{
		return createMatrix(name, rows, cols, null);
	}

	public MathematicaMatrixNew createMatrix(String name, int rows, int cols, Parameter dMatrix)
	{
		MathematicaMatrixNew matrix = new MathematicaMatrixNew(caller, modelPtr, name, rows, cols, dMatrix);
		data.addElement(matrix);
		return matrix;
	}

	public Object createVector(String name, int cols)
	{
		return createVector(name, cols, null);
	}

	public MathematicaVectorNew createVector(String name, int cols, Parameter dVector)
	{
		MathematicaVectorNew vtr = new MathematicaVectorNew(caller, modelPtr, name, cols, dVector);
		data.addElement(vtr);
		return vtr;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof MathematicaRealNew) {
				if (!((MathematicaRealNew) obj).getIsResult()) {
					((MathematicaRealNew) obj).loadNativeData();
				}
			} else if (obj instanceof MathematicaMatrixNew) {
				if (!((MathematicaMatrixNew) obj).getIsResult()) {
					((MathematicaMatrixNew) obj).loadNativeData();
				}
			} else if (obj instanceof MathematicaIntegerNew) {
				if (!((MathematicaIntegerNew) obj).getIsResult()) {
					((MathematicaIntegerNew) obj).loadNativeData();
				}
			} else if (obj instanceof MathematicaVectorNew) {
				if (!((MathematicaVectorNew) obj).getIsResult()) {
					((MathematicaVectorNew) obj).loadNativeData();
				}
			}
		}
		caller.callVoidFunc(modelPtr, MathematicaPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++) {
			if (waitingToDie)
				return; // do not set outputs if waiting to die
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof MathematicaRealNew) {
					if (((MathematicaRealNew) obj).getIsResult()) {
						((MathematicaRealNew) obj).loadJavaData();
					}
				}
				else if (obj instanceof MathematicaMatrixNew) {
					if (((MathematicaMatrixNew) obj).getIsResult()) {
						((MathematicaMatrixNew) obj).loadJavaData();
					}
				}
				else if (obj instanceof MathematicaIntegerNew) {
					if (((MathematicaIntegerNew) obj).getIsResult()) {
						((MathematicaIntegerNew) obj).loadJavaData();
					}
				}
				else if (obj instanceof MathematicaVectorNew) {
					if (((MathematicaVectorNew) obj).getIsResult()) {
						((MathematicaVectorNew) obj).loadJavaData();
					}
				}
			}
		}
	}
}

