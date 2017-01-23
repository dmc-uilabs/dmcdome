/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 22, 2002
* Time: 11:26:08 AM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/

package mit.cadlab.dome3.plugin.example;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.NativeCaller;
import mit.cadlab.dome3.plugin.example.dataobject.ExampleBoolean;
import mit.cadlab.dome3.plugin.example.dataobject.ExampleReal;

import java.util.List;
import java.util.Vector;

public class ExamplePlugin extends AbstractPlugin
{
	public static final String MODEL = "ExampleModel";

	public static final String RETSTRING = "ExampleModel::returnString";
	public static final String RETDVEC = "ExampleModel::returnDoubleVector";
	public static final String RETIVEC = "ExampleModel::returnIntVector";
	public static final String RET2DDVEC = "ExampleModel::return2DimDoubleVector";
	public static final String RET2DIVEC = "ExampleModel::return2DimIntVector";

	public static final String LDMODEL = "ExampleModel::loadModel";
	public static final String ULDMODEL = "ExampleModel::unloadModel";
	public static final String ISMODELLD = "ExampleModel::isModelLoaded";
	public static final String EXECBFIP = "ExampleModel::executeBeforeInput";
	public static final String EXEC = "ExampleModel::execute";
	public static final String EXECAFOP = "ExampleModel::executeAfterOutput";
	public static final String CRTRL = "ExampleModel::createReal";  //no arg method
	public static final String CRTBL = "ExampleModel::createBoolean"; //no arg method
	public static final String DCRTRL = "ExampleModel::DcreateReal"; //takes "double" arg
	public static final String BCRTBL = "ExampleModel::BcreateBoolean"; //takes "bool" arg

	private Vector data;
	private NativeCaller caller;
	private long modelPtr;

	public ExamplePlugin(String libname)
	{
		modelPtr = 0; //model not created yet
		data = new Vector();
		System.out.println("path = " + System.getProperty("java.library.path"));
		System.loadLibrary(libname);
		caller = new NativeCaller();
	}

	public void createModel()
	{
		modelPtr = caller.callConstructor(MODEL, null);
	}

	public void deleteModel()
	{
		//delete all native objects
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof ExampleReal) {
				((ExampleReal) obj).destroy();
				((ExampleReal) obj).resetObjectPointer();
			} else if (obj instanceof ExampleBoolean) {
				((ExampleBoolean) obj).destroy();
				((ExampleBoolean) obj).resetObjectPointer();
			}
		}
		//delete model
		caller.callDestructor(MODEL, modelPtr);
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


	public Object createReal()
	{
		Object real = new ExampleReal(caller, modelPtr);
		data.addElement(real);
		return real;
	}

	public Object createReal(double value)
	{
		Object real = new ExampleReal(caller, modelPtr, value);
		data.addElement(real);
		return real;
	}

	public Object createBoolean()
	{
		Object bol = new ExampleBoolean(caller, modelPtr);
		data.addElement(bol);
		return bol;
	}

	public Object createBoolean(boolean value)
	{
		Object bol = new ExampleBoolean(caller, modelPtr, value);
		data.addElement(bol);
		return bol;
	}

	public String returnString()
	{
		String str = caller.callStringFunc(MODEL, modelPtr, RETSTRING, null);
		return str;
	}

	public double[] returnDoubleVector(double[] darr)
	{
		Object[] args = new Object[]{darr, new Integer(darr.length)};
		double[] arr = caller.callDoubleArrayFunc(MODEL, modelPtr, RETDVEC, args);
		return arr;
	}

	public int[] returnIntVector(int[] iarr)
	{
		Object[] args = new Object[]{iarr, new Integer(iarr.length)};
		int[] arr = caller.callIntArrayFunc(MODEL, modelPtr, RETIVEC, args);
		return arr;
	}

	public double[][] return2DimDoubleVector()
	{
		double[][] arr = caller.call2DimDoubleArrayFunc(MODEL, modelPtr, RET2DDVEC, null);
		return arr;
	}

	public int[][] return2DimIntVector()
	{
		int[][] arr = caller.call2DimIntArrayFunc(MODEL, modelPtr, RET2DIVEC, null);
		return arr;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof ExampleReal) {
				if (!((ExampleReal) obj).getIsResult())
					((ExampleReal) obj).loadNativeData();
			} else if (obj instanceof ExampleBoolean) {
				if (!((ExampleBoolean) obj).getIsResult())
					((ExampleBoolean) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof ExampleReal) {
				if (((ExampleReal) obj).getIsResult()) {
					((ExampleReal) obj).loadJavaData();
				}
			} else if (obj instanceof ExampleBoolean) {
				if (((ExampleBoolean) obj).getIsResult()) {
					((ExampleBoolean) obj).loadJavaData();
				}
			}
		}
	}

}

