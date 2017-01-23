package mit.cadlab.dome3.plugin.mathcad;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadEnumerationNew;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadMatrixNew;
import mit.cadlab.dome3.plugin.mathcad.dataobject.MathcadRealNew;
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
public class MathcadModelRuntimeNew extends PluginModelRuntime
{
	protected MathcadPluginNew plg;

	public MathcadModelRuntimeNew(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(parentId, xml, isProjectResource);
		loadNativeModel();
		System.out.println("--------------------Mathcad plugin of new architecture!------------------------------ ");
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
		EnumerationData verData = (EnumerationData) pluginConfiguration.getSetupParameter(MathcadConfiguration.SOFTWARE_VERSION).getCurrentDataObject();
		String mathcadVersion = verData.getElementName(verData.getLastSelection());
		String mathcadDllName = MathcadConfiguration.MATHCAD_R13_DLL; // default 13
		//if (MathcadConfiguration.MATHCAD_6.equals(mathcadVersion))
			//mathcadDllName = MathcadConfiguration.MATHCAD_R12_DLL;
		boolean runInForeground = ((DomeBoolean) pluginConfiguration.getSetupParameter(MathcadConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();

		//String fileName = ((DomeFile) pluginConfiguration.getSetupParameter(MathcadConfiguration.FILE_LOCATION).getCurrentDataObject()).getFilePath();
		String fileName = getMainModelFileName();
		if (fileName == null)
			throw new UnsupportedOperationException("can not start Mathcad model - no filename");
		fileName = new File(workingDirectory, new File(fileName).getName()).getAbsolutePath();
		System.out.println("main mathcad file: " + fileName);
		plg = new MathcadPluginNew(mathcadDllName, fileName,
		                       runInForeground, getFilesCanBeExecuted());
		plg.createModel();

		// create map of dome object to corresponding mathcad object
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
		Object mathcadObj = null;
		if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
			mathcadObj = plg.createReal(name, p);
			if (!isInput)
				((MathcadRealNew) mathcadObj).setIsResult(true);
		} else if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
			DomeMatrixData matrix = (DomeMatrixData) p.getCurrentDataObject();
			mathcadObj = (plg).createMatrix(name, matrix.getRowCount(), matrix.getColumnCount(), p);
			if (!isInput)
				((MathcadMatrixNew) mathcadObj).setIsResult(true);
		} else if (p.getCurrentType().equals(DomeEnumeration.TYPE_INFO.getTypeName())) {
            mathcadObj = (plg).createEnumeration(name, p);
            if (!isInput)
                ((MathcadEnumerationNew) mathcadObj).setIsResult(true);
        } else if (p.getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName())) {
            mathcadObj = plg.createFile(p, !isInput);
        }
	}

}
