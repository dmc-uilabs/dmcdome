/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Aug 22, 2002
* Time: 11:26:08 AM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/

package mit.cadlab.dome3.plugin.excel;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.ExcelNativeCaller;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelEnumeration;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelMatrix;
import mit.cadlab.dome3.plugin.excel.dataobject.ExcelReal;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ExcelPlugin extends AbstractPlugin
{
	public static final String MODEL = "ExcelModel";
	public static final String LDMODEL = "ExcelModel::loadModel";
	public static final String ULDMODEL = "ExcelModel::unloadModel";
	public static final String ISMODELLD = "ExcelModel::isModelLoaded";
	public static final String EXECBFIP = "ExcelModel::executeBeforeInput";
	public static final String EXEC = "ExcelModel::execute";
	public static final String EXECAFOP = "ExcelModel::executeAfterOutput";
	public static final String CRTRL = "ExcelModel::createReal";
	public static final String CRTMTX = "ExcelModel::createMatrix";

	private String file;
	private boolean isVisible;
	Vector data;
	ExcelNativeCaller caller;
	long modelPtr;

	public ExcelPlugin(String libname, String file, boolean isVisible)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		data = new Vector();
		try {
			System.out.println("ExcelPlugin Before loading DLL" + libname );

			System.loadLibrary(libname);
		}
		catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError (
			        "A link error occured when trying to load " + libname + ".DLL.\n\n"
			        + "Check that the directory containing the DLLs is in your path.");
		}
		caller = new ExcelNativeCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = file;
		Boolean isVis = new Boolean(isVisible);
		arr[1] = isVis;
		modelPtr = caller.callConstructor(MODEL, arr);
		System.out.println("modelPtr = " + modelPtr);
	}

	public void deleteModel()
	{
		caller.callDestructor(MODEL, modelPtr);
		Debug.trace(Debug.ALL, "called native destructor");
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
		Debug.trace(Debug.ALL,"ExcelPlugin:loadModel - 1" );
		
		caller.callVoidFunc(MODEL, modelPtr, LDMODEL, null);
		Debug.trace(Debug.ALL,"ExcelPlugin:loadModel - 2" );
	
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			System.out.println("ExcelPlugin:loadModel - 3" );

			if (obj instanceof ExcelMatrix) {
				((ExcelMatrix) obj).setRows(((ExcelMatrix) obj).getRows(true));
				((ExcelMatrix) obj).setColumns(((ExcelMatrix) obj).getColumns(true));
			}
		}
		if (!isModelLoaded()) {
			throw new RuntimeException("unable to load Excel model: " + file);
		}
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


	public Object createReal(String sheet, String range)
	{
		return createReal(sheet, range, null);
	}

	public ExcelReal createReal(String sheet, String range, Parameter realParam)
	{
		ExcelReal real = new ExcelReal(caller, modelPtr, sheet, range, realParam);
		data.addElement(real);
		return real;
	}

	public Object createMatrix(String sheet, String range)
	{
		return createMatrix(sheet, range, null);
	}

	public ExcelMatrix createMatrix(String sheet, String range, Parameter matrixParam)
	{
		ExcelMatrix matrix = new ExcelMatrix(caller, modelPtr, sheet, range, matrixParam);
		data.addElement(matrix);
		return matrix;
	}

	public Object createEnumeration(String sheet, String range)
	{
		return createEnumeration(sheet, range, null);
	}

	public ExcelReal createEnumeration(String sheet, String range, Parameter enumParam)
	{
		ExcelEnumeration enm = new ExcelEnumeration(caller, modelPtr, sheet, range, enumParam);
		data.addElement(enm);
		return enm;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof ExcelEnumeration) {
				if (!((ExcelEnumeration) obj).getIsResult())
					((ExcelEnumeration) obj).loadNativeData();
			}
            else if (obj instanceof ExcelReal) {
				if (!((ExcelReal) obj).getIsResult())
					((ExcelReal) obj).loadNativeData();
			}
			if (obj instanceof ExcelMatrix) {
				if (!((ExcelMatrix) obj).getIsResult())
					((ExcelMatrix) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData)data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(),affectedOutputParams)) {
				if (obj instanceof ExcelEnumeration) {
					if (((ExcelEnumeration) obj).getIsResult()) {
						((ExcelEnumeration) obj).loadJavaData();
					}
				}
				else if (obj instanceof ExcelReal) {
					if (((ExcelReal) obj).getIsResult()) {
						((ExcelReal) obj).loadJavaData();
					}
				}
				else if (obj instanceof ExcelMatrix) {
					if (((ExcelMatrix) obj).getIsResult()) {
						((ExcelMatrix) obj).loadJavaData();
					}
				}
			}
		}
	}

}

