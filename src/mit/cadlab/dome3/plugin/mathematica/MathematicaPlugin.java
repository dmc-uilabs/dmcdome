// MathematicaPlugin.java

package mit.cadlab.dome3.plugin.mathematica;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.MathematicaNativeCaller;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaInteger;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaMatrix;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaReal;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaVector;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MathematicaPlugin extends AbstractPlugin
{
	public static final String MODEL = "MathematicaModel";
	public static final String LDMODEL = "MathematicaModel::loadModel";
	public static final String ULDMODEL = "MathematicaModel::unloadModel";
	public static final String ISMODELLD = "MathematicaModel::isModelLoaded";
	public static final String EXECBFIP = "MathematicaModel::executeBeforeInput";
	public static final String EXEC = "MathematicaModel::execute";
	public static final String EXECAFOP = "MathematicaModel::executeAfterOutput";
	public static final String CRTRL = "MathematicaModel::createReal";
	public static final String CRTMTX = "MathematicaModel::createMatrix";
	public static final String CRTINT = "MathematicaModel::createInteger";
	public static final String CRTVTR = "MathematicaModel::createVector";

	private String file;
	Vector data;
	MathematicaNativeCaller caller;
	long modelPtr;
	public boolean waitingToDie = false;

	public MathematicaPlugin(String libname, String file)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		data = new Vector();
		System.loadLibrary(libname);
		caller = new MathematicaNativeCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[1];
		arr[0] = file;
		modelPtr = caller.callConstructor(MODEL, arr);
	}

	public void deleteModel()
	{
		caller.callDestructor(MODEL, modelPtr);
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(MODEL, modelPtr, ISMODELLD, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, LDMODEL, null);
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECBFIP, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECAFOP, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, ULDMODEL, null);
	}


	public Object createReal(String name)
	{
		return createReal(name, null);
	}

	public MathematicaReal createReal(String name, Parameter dReal)
	{
		MathematicaReal real = new MathematicaReal(caller, modelPtr, name, dReal);
		data.addElement(real);
		//System.out.println("real: " +real);
		return real;
	}

	public Object createInteger(String name)
	{
		return createInteger(name, null);
	}

	public MathematicaInteger createInteger(String name, Parameter dInteger)
	{
		MathematicaInteger integer = new MathematicaInteger(caller, modelPtr, name, dInteger);
		data.addElement(integer);
		//System.out.println("real: " +real);
		return integer;
	}

	public Object createMatrix(String name, int rows, int cols)
	{
		return createMatrix(name, rows, cols, null);
	}

	public MathematicaMatrix createMatrix(String name, int rows, int cols, Parameter dMatrix)
	{
		MathematicaMatrix matrix = new MathematicaMatrix(caller, modelPtr, name, rows, cols, dMatrix);
		data.addElement(matrix);
		return matrix;
	}

	public Object createVector(String name, int cols)
	{
		return createVector(name, cols, null);
	}

	public MathematicaVector createVector(String name, int cols, Parameter dVector)
	{
		MathematicaVector vtr = new MathematicaVector(caller, modelPtr, name, cols, dVector);
		data.addElement(vtr);
		return vtr;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof MathematicaReal) {
				if (!((MathematicaReal) obj).getIsResult()) {
					((MathematicaReal) obj).loadNativeData();
				}
			} else if (obj instanceof MathematicaMatrix) {
				if (!((MathematicaMatrix) obj).getIsResult()) {
					((MathematicaMatrix) obj).loadNativeData();
				}
			} else if (obj instanceof MathematicaInteger) {
				if (!((MathematicaInteger) obj).getIsResult()) {
					((MathematicaInteger) obj).loadNativeData();
				}
			} else if (obj instanceof MathematicaVector) {
				if (!((MathematicaVector) obj).getIsResult()) {
					((MathematicaVector) obj).loadNativeData();
				}
			}
		}
		caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
		for (int i = 0; i < data.size(); i++) {
			if (waitingToDie)
				return; // do not set outputs if waiting to die
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof MathematicaReal) {
					if (((MathematicaReal) obj).getIsResult()) {
						((MathematicaReal) obj).loadJavaData();
					}
				}
				else if (obj instanceof MathematicaMatrix) {
					if (((MathematicaMatrix) obj).getIsResult()) {
						((MathematicaMatrix) obj).loadJavaData();
					}
				}
				else if (obj instanceof MathematicaInteger) {
					if (((MathematicaInteger) obj).getIsResult()) {
						((MathematicaInteger) obj).loadJavaData();
					}
				}
				else if (obj instanceof MathematicaVector) {
					if (((MathematicaVector) obj).getIsResult()) {
						((MathematicaVector) obj).loadJavaData();
					}
				}
			}
		}
	}
}

