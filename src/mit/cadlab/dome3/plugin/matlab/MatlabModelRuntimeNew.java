package mit.cadlab.dome3.plugin.matlab;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabEnumerationNew;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabMatrixNew;
import mit.cadlab.dome3.plugin.matlab.dataobject.MatlabRealNew;
import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Mar 11, 2003
 * Time: 7:05:39 PM
 * To change this template use Options | File Templates.
 */
public class MatlabModelRuntimeNew extends PluginModelRuntime
{
	protected MatlabPluginNew plg;

	public MatlabModelRuntimeNew(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(parentId, xml, isProjectResource);
		loadNativeModel();
		System.out.println("--------------------Matlab plugin of new architecture!------------------------------ ");
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
		if (!plg.isModelLoaded())
		{
			plg.loadModel();
		}
		plg.execute(affectedOutputParams);
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
		plg.deleteModel();
		super.deleteModel();
	}

	protected void loadNativeModel()
	{
		// get configuration parameters
		EnumerationData verData = (EnumerationData) pluginConfiguration.getSetupParameter(MatlabConfiguration.SOFTWARE_VERSION).getCurrentDataObject();
		String matlabVersion = verData.getElementName(verData.getLastSelection());
	    Debug.trace(Debug.ALL,"MatlabModelRuntimeNew:loadNativeModel matlabVersion = " + matlabVersion);
	      		
		String matlabDllName = MatlabConfiguration.MATLAB_R13_DLL; // default 6.5
		if (MatlabConfiguration.MATLAB_6.equals(matlabVersion))
			matlabDllName = MatlabConfiguration.MATLAB_R12_DLL;
		if (MatlabConfiguration.MATLAB_7.equals(matlabVersion))
			matlabDllName = MatlabConfiguration.MATLAB_R14_DLL;
		// Changes made in August 2015
		if (MatlabConfiguration.MATLAB_8p3.equals(matlabVersion))
			matlabDllName = MatlabConfiguration.MATLAB_R2014_DLL;
			
		
		boolean runInForeground = ((DomeBoolean) pluginConfiguration.getSetupParameter(MatlabConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();

		//String fileName = ((DomeFile) pluginConfiguration.getSetupParameter(MatlabConfiguration.FILE_LOCATION).getCurrentDataObject()).getFilePath();
		String fileName = getMainModelFileName();
		if (fileName == null)
			throw new UnsupportedOperationException("can not start Matlab model - no filename");
		fileName = new File(workingDirectory, new File(fileName).getName()).getAbsolutePath();
		System.out.println("main matlab file: " + fileName);
		plg = new MatlabPluginNew(matlabDllName, fileName,
		                       runInForeground, getFilesCanBeExecuted());
		plg.createModel();

		// create map of dome object to corresponding matlab object
		Iterator it = getModelObjects().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				Parameter p = (Parameter) o;
				Object map = getPluginMappingManager().getMappingObjectForParameter(p);
				if (map != null || p.getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName())) {
					createModelLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
				}
			}
		}
	}

	protected void createModelLink(Parameter p, String name, boolean isInput)
	{
		Object matlabObj = null;
		if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
			matlabObj = plg.createReal(name, p);
			if (!isInput)
				((MatlabRealNew) matlabObj).setIsResult(true);
		} else if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
			DomeMatrixData matrix = (DomeMatrixData) p.getCurrentDataObject();
			matlabObj = (plg).createMatrix(name, matrix.getRowCount(), matrix.getColumnCount(), p);
			if (!isInput)
				((MatlabMatrixNew) matlabObj).setIsResult(true);
		} else if (p.getCurrentType().equals(DomeEnumeration.TYPE_INFO.getTypeName())) {
            matlabObj = (plg).createEnumeration(name, p);
            if (!isInput)
                ((MatlabEnumerationNew) matlabObj).setIsResult(true);
        } else if (p.getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName())) {
            matlabObj = plg.createFile(p, !isInput);
        }
	}

}