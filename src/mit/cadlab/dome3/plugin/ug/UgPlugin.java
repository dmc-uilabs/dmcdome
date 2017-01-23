package mit.cadlab.dome3.plugin.ug;

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.UgNativeCaller;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponent;
import mit.cadlab.dome3.plugin.ug.dataobject.UgImportFile;
import mit.cadlab.dome3.plugin.ug.dataobject.UgVrmlFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.List;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 13, 2003
 * Time: 10:53:25 PM
 * To change this template use Options | File Templates.
 */
public class UgPlugin extends AbstractPlugin
{
	public static final String MODEL = "UgModel";
	public static final String LDMODEL = "UgModel::loadModel";
	public static final String ULDMODEL = "UgModel::unloadModel";
	public static final String EXPORT = "UgModel::export";
	public static final String IMPORT = "UgModel::import";
	public static final String ISMODELLD = "UgModel::isModelLoaded";
	public static final String EXECUTE = "UgModel::execute";
	public static final String EXECBFIP = "UgModel::execute";
	public static final String EXECAFOP = "UgModel::execute";
	public static final String CREATECOMPONENT = "UgModel::createComponent";
	public static final String CREATEVRML = "UgModel::createVrmlFile";
	public static final String CREATEIMPORTFILE = "UgModel::createImportFile";

	private Vector data;
	private UgNativeCaller caller;
	public long modelPtr;
	private String directory;
	private String model;

	public UgPlugin(String libname, String directory, String model)
	{
		modelPtr = 0; //model not created yet
		this.directory = directory;
		this.model = model;
		data = new Vector();
		System.loadLibrary(libname);
		System.out.println(libname);
		caller = new UgNativeCaller();
	}
	public void createModel()
	{
		Object[] arr = new Object[2];
		arr[0] = model;
		arr[1] = directory;
		modelPtr = caller.callConstructor(MODEL, arr);
	}
	public void loadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, LDMODEL, null);
	}
	public void unloadModel()
	{
		caller.callVoidFunc(MODEL, modelPtr, ULDMODEL, null);
	}

	public void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++)
		{
			Object obj = data.get(i);
			if (obj instanceof UgImportFile)
			{
				if (!((UgImportFile) obj).getIsResult()) ((UgImportFile) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(UgPlugin.MODEL, modelPtr, UgPlugin.EXECUTE, null);
		for (int i = 0; i < data.size(); i++)
		{
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if(obj instanceof UgComponent)
				{
					if(((UgComponent)obj).getIsResult())
						((UgComponent)obj).loadJavaData(modelPtr);
				}
			}
		}
	}

	public boolean isModelLoaded()
	{
		return caller.callBoolFunc(MODEL, modelPtr, ISMODELLD, null);
	}
	public void executeBeforeInput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECBFIP, null);
	}
	public void executeAfterOutput()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXECAFOP, null);
	}
	public void export()
	{
		caller.callVoidFunc(MODEL, modelPtr, EXPORT, null);
	}
	public void importNeutralFile()
	{
		caller.callVoidFunc(MODEL, modelPtr, IMPORT, null);
	}
	public void deleteModel()
	{
        //TODO complete the implementation and then uncomment following lines?
//		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
//			PluginData o = (PluginData) iterator.next();
//			o.resetObjectPointer();
//		}
	}
	public void fireModelChanged()
	{
	}
	public UgComponent createComponent(Parameter p, String componentName, String unit)
	{
		UgComponent _ugComp = new UgComponent(caller, modelPtr, p, componentName, unit);
		data.addElement(_ugComp);
		return _ugComp;
	}
	public Object createVrmlFile(String fileName, Parameter p)
	{
		Object _vrmlFile = new UgVrmlFile(caller, modelPtr, fileName, p);
		data.addElement(_vrmlFile);
		return _vrmlFile;
	}
	public Object createUgImportFile(String fileName, Parameter p)
	{
		Object _idImportPart = new UgImportFile(caller, modelPtr, fileName, p);
		data.addElement(_idImportPart);
		return _idImportPart;
	}
}

