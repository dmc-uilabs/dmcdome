package mit.cadlab.dome3.plugin.mathematica;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaIntegerNew;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaMatrixNew;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaRealNew;
import mit.cadlab.dome3.plugin.mathematica.dataobject.MathematicaVectorNew;
import mit.cadlab.dome3.plugin.matlab.MatlabConfiguration;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2005
 * Time: 3:09:50 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaModelRuntimeNew extends PluginModelRuntime
{
	protected MathematicaPluginNew plg;

	public MathematicaModelRuntimeNew(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(parentId, xml, isProjectResource);
		loadNativeModel();
		System.out.println("--------------------Mathematica plugin of new architecture!------------------------------ ");
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
		if (!plg.isModelLoaded()) {
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
		stopModel ();
		plg.deleteModel();
		super.deleteModel();
	}

	protected void loadNativeModel()
	{
		// get configuration parameters
		//String fileName = ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile) pluginConfiguration.getSetupParameter(MathematicaConfiguration.FILE_LOCATION).getCurrentDataObject()).getFilePath();
		String fileName = getMainModelFileName();
		if (fileName == null)
			throw new UnsupportedOperationException("can not start Mathematica model - no filename");
		fileName = new File(workingDirectory, new File(fileName).getName()).getAbsolutePath();
		//??boolean runInForeground = ((DomeBoolean)pluginConfiguration.getSetupParameter(ExcelConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();
		// todo: get mathematica version information
		fileName = fileName.replace('\\', '/');
		boolean runInForeground = ((DomeBoolean) pluginConfiguration.getSetupParameter(MatlabConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();
		System.out.println(fileName);
		plg = new MathematicaPluginNew("MathematicaPlugin", fileName, runInForeground, getFilesCanBeExecuted());
		plg.createModel();

		// create map of dome object to corresponding excel object
		Iterator it = getModelObjects().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				Parameter p = (Parameter) o;
				Object map = getPluginMappingManager().getMappingObjectForParameter(p);
				if (map != null) {
					createModelLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
				}
			}
		}
	}

	protected void createModelLink(Parameter p, String name, boolean isInput)
	{
		Object mathematicaObj = null;
		if (p.getCurrentType().equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal.TYPE_INFO.getTypeName())) {
			mathematicaObj = plg.createReal(name, p);
			if (!isInput)
				((MathematicaRealNew) mathematicaObj).setIsResult(true);
		}
		else if (p.getCurrentType().equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.TYPE_INFO.getTypeName())) {
			DomeMatrixData matrix = (DomeMatrixData) p.getCurrentDataObject();
			mathematicaObj = (plg).createMatrix(name, matrix.getRowCount(), matrix.getColumnCount(), p);
			if (!isInput)
				((MathematicaMatrixNew) mathematicaObj).setIsResult(true);
		}
		else if (p.getCurrentType().equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger.TYPE_INFO.getTypeName())) {
			mathematicaObj = plg.createInteger(name, p);
			if (!isInput)
				((MathematicaIntegerNew) mathematicaObj).setIsResult(true);
		}
		else if (p.getCurrentType().equals(DomeVectorData.TYPE_INFO.getTypeName())) {
			DomeVectorData vector = (DomeVectorData) p.getCurrentDataObject();
			mathematicaObj = plg.createVector(name, vector.getSize(), p);
			if (!isInput)
				((MathematicaVectorNew) mathematicaObj).setIsResult(true);
		}
	}

}

