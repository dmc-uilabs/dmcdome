package mit.cadlab.dome3.plugin.ug;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponentNew;
import mit.cadlab.dome3.plugin.ug.dataobject.UgImportFileNew;
import mit.cadlab.dome3.plugin.ug.dataobject.UgVrmlFileNew;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 16, 2005
 * Time: 4:22:18 PM
 * To change this template use Options | File Templates.
 */
public class UgPluginNew extends AbstractPlugin
{

	private Vector data;
	private UgPluginCaller caller;
	public long modelPtr;
	private String directory;
	private String model;

	public UgPluginNew(String libname, String directory, String model)
	{
		modelPtr = 0; //model not created yet
		this.directory = directory;
		this.model = model;
		data = new Vector();
		System.loadLibrary(libname);
		System.out.println(libname);
		caller = new UgPluginCaller();
	}
	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = directory;
		arr[1] = model;
		modelPtr = caller.callObjectFunc(0, UgPluginCaller.MODEL_INIT, arr);
	}
	public void loadModel()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_LOAD, null);
	}
	public void unloadModel()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_UNLOAD, null);
	}

	public void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++)
		{
			Object obj = data.get(i);
			if (obj instanceof UgImportFileNew)
			{
				if (!((UgImportFileNew) obj).getIsResult()) ((UgImportFileNew) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_EXECUTE, null);
		for (int i = 0; i < data.size(); i++)
		{
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if(obj instanceof UgComponentNew)
				{
					if(((UgComponentNew)obj).getIsResult())
						((UgComponentNew)obj).loadJavaData(modelPtr);
				}
			}
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(modelPtr, UgPluginCaller.MODEL_IS_LOADED, null);
	}
	public void executeBeforeInput()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_EXECUTE, null);
	}
	public void executeAfterOutput()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_EXECUTE, null);
	}
	public void export()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_EXPORT, null);
	}
	public void importNeutralFile()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_IMPORT, null);
	}
	public void deleteModel()
	{
		caller.callVoidFunc(modelPtr, UgPluginCaller.MODEL_DESTROY, null);
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			PluginData dat = (PluginData) iterator.next();
			dat.resetObjectPointer();
		}
        //TODO complete the implementation and then uncomment following lines?
//		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
//			PluginData o = (PluginData) iterator.next();
//			o.resetObjectPointer();
//		}
	}
	public void fireModelChanged()
	{
	}
	public UgComponentNew createComponent(Parameter p, String componentName, String unit)
	{
		UgComponentNew _ugComp = new UgComponentNew(caller, modelPtr, p, componentName, unit);
		data.addElement(_ugComp);
		return _ugComp;
	}
	public Object createVrmlFile(String fileName, Parameter p)
	{
		Object _vrmlFile = new UgVrmlFileNew(caller, modelPtr, fileName, p);
		data.addElement(_vrmlFile);
		return _vrmlFile;
	}
	public Object createUgImportFile(String fileName, Parameter p)
	{
		Object _idImportPart = new UgImportFileNew(caller, modelPtr, fileName, p);
		data.addElement(_idImportPart);
		return _idImportPart;
	}
}

