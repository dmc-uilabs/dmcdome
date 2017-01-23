package mit.cadlab.dome3.plugin.excel;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.excel.dataobject.*;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: June 2, 2005
 * Time: 6:50:36 PM
 * To change this template use Options | File Templates.
 */
public class ExcelPluginNew extends AbstractPlugin
{
	private String file;
	private boolean isVisible;
	Vector data;
	ExcelPluginCaller caller;
	long modelPtr;
	
	public ExcelPluginNew(String libname, String file, boolean isVisible)
	{
		modelPtr = 0; //model not created yet
		this.file = file;
		this.isVisible = isVisible;
		data = new Vector();
		try {
			System.out.println("ExcelPluginNew Before loading DLL" + libname );
			System.loadLibrary(libname);
		}
		catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError (
			        "A link error occured when trying to load " + libname + ".DLL.\n\n"
			        + "Check that the directory containing the DLLs is in your path.");
		}
		caller = new ExcelPluginCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = file;
		Boolean isVis = new Boolean(isVisible);
		arr[1] = isVis;
		modelPtr = caller.callObjectFunc(0, ExcelPluginCaller.MODEL_INIT, arr);
	}

	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, ExcelPluginCaller.MODEL_DESTROY, null);
		Debug.trace(Debug.ALL, "called native destructor");
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, ExcelPluginCaller.MODEL_IS_LOADED, null);
	}

	public void loadModel()
	{
		caller.callVoidFunc(modelPtr, ExcelPluginCaller.MODEL_LOAD, null);
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof ExcelMatrixNew) {
				((ExcelMatrixNew) obj).setRows(((ExcelMatrixNew) obj).getRows(true));
				((ExcelMatrixNew) obj).setColumns(((ExcelMatrixNew) obj).getColumns(true));
			}
		}
		if (!isModelLoaded()) {
			throw new RuntimeException("unable to load Excel model: " + file);
		}
	}

	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, ExcelPluginCaller.MODEL_EXEC_BF_INPUT, null);
	}

	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, ExcelPluginCaller.MODEL_EXEC_AF_INPUT, null);
	}

	public void unloadModel()
	{
		caller.callVoidFunc(modelPtr, ExcelPluginCaller.MODEL_UNLOAD, null);
	}

	public Object createReal(String sheet, String range)
	{
		return createReal(sheet, range, null);
	}

	public ExcelRealNew createReal(String sheet, String range, Parameter realParam)
	{
		ExcelRealNew real = new ExcelRealNew(caller, modelPtr, sheet, range, realParam);
		data.addElement(real);
		return real;
	}
	
	public Object createString(String sheet, String range)
	{
		return createString(sheet, range, null);
	}

	public ExcelStringNew createString(String sheet, String range, Parameter stringParam)
	{
		ExcelStringNew string = new ExcelStringNew(caller, modelPtr, sheet, range, stringParam);
		data.addElement(string);
		return string;
	}

	public Object createMatrix(String sheet, String range)
	{
		return createMatrix(sheet, range, null);
	}

	public ExcelMatrixNew createMatrix(String sheet, String range, Parameter matrixParam)
	{
		ExcelMatrixNew matrix = new ExcelMatrixNew(caller, modelPtr, sheet, range, matrixParam);
		data.addElement(matrix);
		return matrix;
	}

	public Object createEnumeration(String sheet, String range)
	{
		return createEnumeration(sheet, range, null);
	}

	public ExcelRealNew createEnumeration(String sheet, String range, Parameter enumParam)
	{
		ExcelEnumerationNew enm = new ExcelEnumerationNew(caller, modelPtr, sheet, range, enumParam);
		data.addElement(enm);
		return enm;
	}

	public synchronized void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof ExcelEnumerationNew) {
				if (!((ExcelEnumerationNew) obj).getIsResult())
					((ExcelEnumerationNew) obj).loadNativeData();
			}
            else if (obj instanceof ExcelRealNew) {
				if (!((ExcelRealNew) obj).getIsResult())
					((ExcelRealNew) obj).loadNativeData();
			}
            else if (obj instanceof ExcelStringNew) {
				if (!((ExcelStringNew) obj).getIsResult())
					((ExcelStringNew) obj).loadNativeData();
			}
            else if (obj instanceof ExcelMatrixNew) {
				if (!((ExcelMatrixNew) obj).getIsResult())
					((ExcelMatrixNew) obj).loadNativeData();
			}
		}
		
		caller.callVoidFunc(modelPtr, ExcelPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData)data.get(i);
			System.out.println(obj.getParameter().getTypeName());
			
			if (isAffectedOutputParameter(obj.getParameter(),affectedOutputParams)) {
				if (obj instanceof ExcelEnumerationNew) {
					if (((ExcelEnumerationNew) obj).getIsResult()) {
						((ExcelEnumerationNew) obj).loadJavaData();
					}
				}
				else if (obj instanceof ExcelRealNew) {
					if (((ExcelRealNew) obj).getIsResult()) {
						((ExcelRealNew) obj).loadJavaData();
					}
				}
				else if (obj instanceof ExcelStringNew) {
					if (((ExcelStringNew) obj).getIsResult()) {
						((ExcelStringNew) obj).loadJavaData();
					}
				}
				else if (obj instanceof ExcelMatrixNew) {
					if (((ExcelMatrixNew) obj).getIsResult()) {
						((ExcelMatrixNew) obj).loadJavaData();
					}
				}
			}
		}
	}

}

