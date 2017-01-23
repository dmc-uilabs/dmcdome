package mit.cadlab.dome3.plugin.ug;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.ug.dataobject.UgComponentNew;
import mit.cadlab.dome3.plugin.ug.dataobject.dataobjectsupport.UgDimensionInNew;
import mit.cadlab.dome3.util.FileUtils;
import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 16, 2005
 * Time: 4:45:13 PM
 * To change this template use Options | File Templates.
 */
public class UgModelRuntimeNew extends PluginModelRuntime
{
	private static int lastRuntimeInstanceNumber = 0;
    private String _vrmlDirectory;
	private String _modelName;
	private Vector _savedFiles = new Vector();

	protected UgPluginNew plg;

	public UgModelRuntimeNew(CompoundId id, Element xml, boolean isProjectResource)
	{
		super(id, xml, isProjectResource);
		loadNativeModel();
		System.out.println("--------------------Ug plugin of new architecture!------------------------------ ");
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
		if (!plg.isModelLoaded())
		{
			plg.loadModel();
		}
		plg.execute(affectedOutputParams);
		if (isWaitingToDie())
			return; // don't set outputs if waiting to die
		if (this._savedFiles.size() > 0)
			plg.export();
		for (int i = 0; i < this._savedFiles.size(); i++)
		{
			Parameter p = (Parameter)this._savedFiles.elementAt(i);
			if(affectedOutputParams.contains(p))
			{
				FileData temp = (FileData)p.getCurrentDataObject();
				temp.notifyFileChanged();
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

	public void deleteModel() {
		if (solver.isSolving()) {
			solver.stopSolving();
			waitingToDie = Boolean.TRUE;
			return;
		}
		File vDir = new File(_vrmlDirectory);
		FileUtils.deleteDirectoryContents(vDir, true);
		super.deleteModel();
	}

	protected void loadNativeModel()
	{
		//todo: currently the fileName is parsed using "\\" which is a system dependent slash.  This must be changed
		//todo: very soon!!!

		// get configuration parameters
		String fileName = getMainModelFileName();
		File model = new File(fileName);
		String modelName = model.getName().substring(0, model.getName().length() - 4);
		String directoryName = model.getParent();
		// todo: get Unigraphics version information
		if (fileName == null)
			throw new UnsupportedOperationException("can not start Unigraphics model - no filename");
		this._vrmlDirectory = new StringBuffer(directoryName).append(File.separator + "ug_export_" + lastRuntimeInstanceNumber + File.separator).toString();
		synchronized(this) {
			lastRuntimeInstanceNumber++;
		}
		this._modelName = modelName;

		plg = new UgPluginNew(UgConfiguration.UGDLL, directoryName, modelName);
		plg.createModel();

		Iterator it = getBuildContext().getModelObjectReferences().listIterator();
		while (it.hasNext())
		{

			Object o = it.next();
			if (o instanceof Parameter)
			{
				Parameter p = (Parameter) o;
				Object map = getPluginMappingManager().getMappingObjectForParameter(p);
				if (map != null)
				{
					createUgLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
				}
			}
		}
	}

	protected void createUgLink(Parameter p, String name, boolean isInput)
	{
		if (name.equalsIgnoreCase(UgConfiguration.PART))
		{
			this.createUgPart(p, isInput);
		}
		else if (name.equalsIgnoreCase(UgConfiguration.ASSEMBLY))
		{
			this.createUgAssembly(p, isInput);
		}
		else if (name.equalsIgnoreCase(UgConfiguration.VRML))
		{
			String filePath = this._vrmlDirectory + this._modelName + ".wrl";
			((DomeFile) p.getCurrentDataObject()).setFilePath(filePath);
			this.createUgVrml(p, isInput);
		}
	}

	protected void createUgAssembly(Parameter p, boolean isInput)
	{
		String componentName = null;
		UgComponentNew ugAssembly = null;
		List assemblyData = p.getCurrentDataObject().getValues();

		componentName = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter) assemblyData.remove(0));
		ugAssembly = plg.createComponent(p, componentName, "kg m");

		Iterator assemblyIterator = assemblyData.listIterator();
		while (assemblyIterator.hasNext())
		{
			Object tempObject = assemblyIterator.next();
			if (tempObject instanceof Parameter)
			{
				Parameter temp = (Parameter) tempObject;
				String map = (String)getPluginMappingManager().getMappingObjectForParameter(temp);
				if (UgConfiguration.VOLUME.equals(map))
					ugAssembly.createMassProperty(map, temp);
				else if (map.equals(UgConfiguration.MASS))
					ugAssembly.createMassProperty(map, temp);
				else if (map.equals(UgConfiguration.SURFACE_AREA))
					ugAssembly.createMassProperty(map, temp);
				else if (UgConfiguration.IGES.equals(map) || UgConfiguration.STEP.equals(map))
					ugAssembly.createExportFile(map, temp);
			}
		}
	}

	protected void createUgPart(Parameter p, boolean isInput)
	{

		//TODO
		//TODO : must change this code so that the units are not hard coded for the ug part
		//TODO

		Object ugPartDataObject = null;
		String partName = null;
		UgComponentNew ugPart = null;
		List partData = p.getCurrentDataObject().getValues();
		partName = (String)this.getPluginMappingManager().getMappingObjectForParameter((Parameter)partData.remove(0));
		ugPart = plg.createComponent(p, partName, "kg m");
		Iterator partIterator = partData.listIterator();
		while (partIterator.hasNext())
		{
			Object tempObject = partIterator.next();
			if (tempObject instanceof Parameter)
			{
				Parameter temp = (Parameter) tempObject;
				String map = (String)getPluginMappingManager().getMappingObjectForParameter(temp);
				boolean input = getCausality(temp).equals(CausalityStatus.INDEPENDENT);
				if (UgConfiguration.VOLUME.equals(map))
					ugPart.createMassProperty(map, temp);
				else if (UgConfiguration.MASS.equals(map))
					ugPart.createMassProperty(map, temp);
				else if (UgConfiguration.SURFACE_AREA.equals(map))
					ugPart.createMassProperty(map, temp);
				else if (UgConfiguration.IGES.equals(map) || UgConfiguration.STEP.equals(map))
					ugPart.createExportFile(map, temp);
				else
				{
					if (input)
					{
						ugPartDataObject = ugPart.createDimensionIn(map, temp);
						((UgDimensionInNew) ugPartDataObject).setIsResult(false);
					}
					else
						ugPartDataObject = ugPart.createDimensionOut(map, temp);
				}
			}
		}
	}

	protected void createUgVrml(Parameter p, boolean isInput)
	{
		DomeFile f = (DomeFile) p.getCurrentDataObject();
		Object ugVrml = plg.createVrmlFile(this._modelName, p);
		this._savedFiles.addElement(p);
	}

}
