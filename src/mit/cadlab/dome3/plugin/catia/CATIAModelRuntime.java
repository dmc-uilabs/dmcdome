package mit.cadlab.dome3.plugin.catia;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.catia.dataobject.*;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 3, 2003
 * Time: 3:02:11 PM
 * To change this template use Options | File Templates.
 */
public class CATIAModelRuntime extends PluginModelRuntime
{
	protected CATIAPlugin plg;
	private Vector _savedFiles = new Vector();

	// only one catia model will be run at a time to ensure that clients get proper values
	// since running separate instances of the same model is currently unsupported
	// multiple dome clients can connect to the same model, but they are actually sharing
	// the same instance of the model within catia
	private static Object runOnlyOneCatiaModelLock = new Object();

	public CATIAModelRuntime(CompoundId id, Element xml, boolean isProjectResource)
	{
		super(id, xml, isProjectResource);
		loadNativeModel();
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
		synchronized (runOnlyOneCatiaModelLock) {
			if (!plg.isModelLoaded()) {
				plg.loadModel();
			}
			plg.execute(affectedOutputParams);
			if (isWaitingToDie())
				return; // don't set outputs if waiting to die
			for (int i = 0; i < this._savedFiles.size(); i++) {
				Parameter p = (Parameter) this._savedFiles.elementAt(i);
				if(affectedOutputParams.contains(p))
				{
					FileData temp = (FileData)p.getCurrentDataObject();
					temp.notifyFileChanged();
				}
			}
		}
	}

	/**
	 * Halt the native model.
	 */
	public void stopModel()
	{
		plg.unloadModel();
	}

	public void deleteModel ()
	{
		if (solver.isSolving()) {
			solver.stopSolving();
			waitingToDie = Boolean.TRUE;
			plg.waitingToDie = true;
			return;
		}
		stopModel ();
		plg.deleteModel();
        super.deleteModel();
	}

	protected void loadNativeModel()
	{
		// get configuration parameters
        String fileName = getMainModelFileName();
        String fileNameOnly = new File(fileName).getName();
        fileName = new File(workingDirectory,fileNameOnly).getAbsolutePath();

        String CATIADLLName = CATIAConfiguration.CATIA_DLL; // default
		/*
		EnumerationData verData = (EnumerationData) pluginConfiguration.getSetupParameter(CATIAConfiguration.SOFTWARE_VERSION).getCurrentDataObject();
		String CATIAVersion = verData.getElementName(verData.getLastSelection());
        if (CATIAConfiguration.CATIA_V5.equals(CATIAVersion))
            CATIADLLName = CATIAConfiguration.CATIA_V5_DLL;
        */
		boolean runInForeground = ((DomeBoolean) pluginConfiguration.getSetupParameter(CATIAConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();

		if (fileName == null)
			throw new UnsupportedOperationException("can not start CATIA model - no filename");
		plg = new CATIAPlugin(CATIADLLName, fileName, runInForeground);
		plg.createModel();

		///////////////////////////////////////////////////////////////////////////////////////////////
		// create map of dome objects to corresponding catia objects
		//
		// handle user library parameters first
		ArrayList listParams = new ArrayList ();
		ArrayList nonListParams = new ArrayList ();
		Iterator it = getModelObjects().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter)
			{
				if (!DomeList.TYPE_INFO.getTypeName().equals(((Parameter)o).getCurrentType())) {
					// this might be a non-list parameter: add it now, remove later if not
                    nonListParams.add (o);
				}
				else {
					// handle list parameters
					createCATIALinkForListParameters ((Parameter)o, listParams);
				}
			}
		}

		// handle all other parameters
		nonListParams.removeAll(listParams);    // remove list parameters
		it = nonListParams.iterator();
		while (it.hasNext()) {
			Parameter p = (Parameter) it.next();
			Object map = getPluginMappingManager().getMappingObjectForParameter(p);
			if (map != null) {
				createCATIALink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT),
								null, -1);
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////////
	}


	protected void createCATIALinkForListParameters (Parameter p, List listParams)
	{
		Object map = getPluginMappingManager().getMappingObjectForParameter(p);
		if (map != null)
		{
			DomeList list = (DomeList) p.getCurrentDataObject();
			int size = list.getSize();

			if (size > 0)
			{
				// set up the library
				Parameter userLibParam = (Parameter) list.getElementValue(0);
				StringData userLibString = (StringData) userLibParam.getCurrentDataObject();
				String libName = userLibString.getValue();
				plg.setupUserLibrary (libName, size-1);
				listParams.add(userLibParam);

				// add the list arguments
				for (int i = 1; i < size; i++)
				{
					Parameter param = (Parameter) list.getElementValue(i);
					createCATIALink(param,
					                param.getName(),
					                getCausality(param).equals(CausalityStatus.INDEPENDENT),
					                libName,
					                i-1);
					listParams.add(param);
				}
			}
		}
	}


	protected void createCATIALink(Parameter p, String name, boolean isInput,
	                               String userLibName, int libArgIndex)
	{
		Object CATIAObj = null;
		if (DomeReal.TYPE_INFO.getTypeName().equals(p.getCurrentType()))
		{
            // CATIAConfiguration.CATIA_REAL.equalsIgnoreCase(name))
			CATIAObj = plg.createReal(name, p,
			                          userLibName, libArgIndex);
			if (!isInput) {
				((CATIAReal) CATIAObj).setIsResult(true);
			}
		}
		else
		if (DomeString.TYPE_INFO.getTypeName().equals(p.getCurrentType()))
		{
			CATIAObj = plg.createString(name, p,
			                            userLibName, libArgIndex);
			if (!isInput) {
				((CATIAString) CATIAObj).setIsResult(true);
			}
		}
		else
		if (DomeInteger.TYPE_INFO.getTypeName().equals(p.getCurrentType()))
		{
			CATIAObj = plg.createInteger(name, p,
			                             userLibName, libArgIndex);
			if (!isInput) {
				((CATIAInteger) CATIAObj).setIsResult(true);
			}
		}
		else
		if (DomeBoolean.TYPE_INFO.getTypeName().equals(p.getCurrentType()))
		{
			CATIAObj = plg.createBoolean(name, p,
			                             userLibName, libArgIndex);
			if (!isInput) {
				((CATIABoolean) CATIAObj).setIsResult(true);
			}
		}
		else
		if (DomeVector.TYPE_INFO.getTypeName().equals(p.getCurrentType()))
		{
			CATIAObj = plg.createVector(name, p,
			                            userLibName, libArgIndex);
			if (!isInput) {
				((CATIAVector) CATIAObj).setIsResult(true);
			}
		}
		else
		if (DomeFile.TYPE_INFO.getTypeName().equals(p.getCurrentType()))
		{
			String filepath = ((DomeFile) p.getCurrentDataObject()).getFilePath();
			CATIAObj = plg.createFile(filepath,
			                          name,
			                          p);
			Debug.trace(Debug.ALL, filepath + "\t" + ((DomeFile) p.getCurrentDataObject()).getFileType());
			((CATIAFile) CATIAObj).setIsResult(true);
			_savedFiles.addElement(p);
		}
	}
}

