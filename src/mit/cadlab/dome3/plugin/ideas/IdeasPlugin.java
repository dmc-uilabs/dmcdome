package mit.cadlab.dome3.plugin.ideas;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Jan 20, 2003
 * Time: 10:12:59 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.IdeasNativeCaller;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasAssembly;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasImportAssembly;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasImportPart;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasPart;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasVrmlFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.List;
import java.util.Vector;

public class IdeasPlugin extends AbstractPlugin
{
	public static final String MODEL = "IdeasModel";
	public static final String LDMODEL = "IdeasModel::loadModel";
	public static final String ULDMODEL = "IdeasModel::unloadModel";
	public static final String EXPORT = "IdeasModel::export";
	public static final String IMPORT = "IdeasModel::import";
	public static final String ISMODELLD = "IdeasModel::isModelLoaded";
	public static final String EXEC = "IdeasModel::execute";
	public static final String EXECBFIP = "IdeasModel::executeBeforeInput";
	public static final String EXECAFOP = "IdeasModel::executeAfterOutput";
	public static final String CREATEASSEM = "IdeasModel::createAssembly";
	public static final String CREATEPART = "IdeasModel::createPart";
	public static final String CREATEVRML = "IdeasModel::createVrmlFile";
	public static final String CREATEIPT = "IdeasModel::createImportPart";
	public static final String CREATEIPA = "IdeasModel::createImportAssembly";

	private Vector data;
	private IdeasNativeCaller caller;
	public long modelPtr;
	private String server;
	private String project;
	private String directory;
	private String model;

	public IdeasPlugin(String libname, String server, String project, String directory, String model)
	{
		modelPtr = 0; //model not created yet
		this.server = server;
		this.project = project;
		this.directory = directory;
		this.model = model;
		data = new Vector();
		System.loadLibrary(libname);
		caller = new IdeasNativeCaller();
	}

	public void createModel()
	{
		Object[] arr = new Object[4];
		arr[0] = server;
		arr[1] = project;
		arr[2] = directory;
		arr[3] = model;
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
		for (int i = 0; i < data.size(); i++) {
			Object obj = data.get(i);
			if (obj instanceof IdeasImportPart) {
				if (!((IdeasImportPart) obj).getIsResult()) ((IdeasImportPart) obj).loadNativeData();
			}
		}
		caller.callVoidFunc(MODEL, modelPtr, EXEC, null);
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof IdeasPart) {
					if (((IdeasPart) obj).getIsResult()) ((IdeasPart) obj).loadJavaData(modelPtr);
				}
				else if (obj instanceof IdeasImportPart) {
					if (((IdeasImportPart) obj).getIsResult()) ((IdeasImportPart) obj).loadJavaData();
				}
			}
		}

		// though this second consecutive for loop looks akward, it is actually needed to force the IdeasPart objects to
		// update before the IdeasAssembly object is updated

		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (isAffectedOutputParameter(obj.getParameter(), affectedOutputParams)) {
				if (obj instanceof IdeasAssembly) {
					if (((IdeasAssembly) obj).getIsResult()) ((IdeasAssembly) obj).loadJavaData(modelPtr);
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
        //TODO implement this?
		//uncomment the following after implementating this method
//		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
//			PluginData dat = (PluginData) iterator.next();
//			dat.resetObjectPointer();
//		}
	}

	public void fireModelChanged()
	{
	}

	public IdeasAssembly createAssembly(Parameter p, String bin, String compName)
	{
		IdeasAssembly _assembly = new IdeasAssembly(caller, modelPtr, p, bin, compName);
		data.addElement(_assembly);
		return _assembly;
	}

	public IdeasPart createPart(Parameter p, String bin, String compName)
	{
		IdeasPart _part = new IdeasPart(caller, modelPtr, p, bin, compName);
		data.addElement(_part);
		return _part;
	}

	public Object createVrmlFile(Parameter p, String fileName)
	{
		Object _vrmlFile = new IdeasVrmlFile(caller, modelPtr, p, fileName);
		data.addElement(_vrmlFile);
		return _vrmlFile;
	}

	public Object createIdeasImportPart(Parameter p, String fileName)
	{
		Object _idImportPart = new IdeasImportPart(caller, modelPtr, p, fileName);
		data.addElement(_idImportPart);
		return _idImportPart;
	}

	public Object createIdeasImportAssembly(Parameter p, String fileName)
	{
		Object _idImportAssembly = new IdeasImportAssembly(caller, modelPtr, p, fileName);
		data.addElement(_idImportAssembly);
		return _idImportAssembly;
	}


}
