package mit.cadlab.dome3.plugin.Solidworks;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.Solidworks.dataobject.*;
import org.dom4j.Element;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2005
 * Time: 2:49:37 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksModelRuntimeNew extends PluginModelRuntime
{
	private static int lastRuntimeInstanceNumber = 0;
	private String specificPath;
	private Set specPaths;
    protected SolidworksPluginNew plg;
	private Vector _savedFiles = new Vector();

	public SolidworksModelRuntimeNew(CompoundId id, Element xml, boolean isProjectResource)
	{
		super(id, xml, isProjectResource);
		loadNativeModel();
		System.out.println("--------------------Solidworks plugin of new architecture!------------------------------ ");
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
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
			return;
		}
		stopModel ();
		plg.deleteModel();
		for (Iterator iterator = specPaths.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			File vDir = new File(s);
			File[] files = vDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			if (vDir.listFiles().length == 0) {
				vDir.delete();
			} else {
				Debug.trace(Debug.ALL, "Could not delete " + s);
			}
		}
		super.deleteModel();
	}

	protected void loadNativeModel()
	{
		// get configuration parameters
		//String fileName = ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile) pluginConfiguration.getSetupParameter(SolidworksConfiguration.FILE_LOCATION).getCurrentDataObject()).getFilePath();
        String fileName = getMainModelFileName();

        EnumerationData verData = (EnumerationData) pluginConfiguration.getSetupParameter(SolidworksConfiguration.SOFTWARE_VERSION).getCurrentDataObject();
        String solidworksVersion = verData.getElementName(verData.getLastSelection());
        String solidworksDllName = SolidworksConfiguration.SLDWKS_2K1_DLL; // default
        if (SolidworksConfiguration.SOLIDWORKS_2000.equals(solidworksVersion))
            solidworksDllName = SolidworksConfiguration.SLDWKS_2K_DLL;
        if (SolidworksConfiguration.SOLIDWORKS_2002.equals(solidworksVersion))
            solidworksDllName = SolidworksConfiguration.SLDWKS_2K2_DLL;
		if (SolidworksConfiguration.SOLIDWORKS_2003.equals(solidworksVersion))
		    solidworksDllName = SolidworksConfiguration.SLDWKS_2K3_DLL;
		if (SolidworksConfiguration.SOLIDWORKS_2004.equals(solidworksVersion))
		    solidworksDllName = SolidworksConfiguration.SLDWKS_2K4_DLL;
		boolean runInForeground = ((DomeBoolean) pluginConfiguration.getSetupParameter(SolidworksConfiguration.RUN_IN_FOREGROUND).getCurrentDataObject()).getValue();
		// todo: get solidworks version information
		if (fileName == null)
			throw new UnsupportedOperationException("can not start Solidworks model - no filename");
		plg = new SolidworksPluginNew(solidworksDllName, fileName, runInForeground);
		plg.createModel();

		specificPath = File.separator + "solidworks_export_" + lastRuntimeInstanceNumber + File.separator;
		specPaths = new HashSet();
		synchronized(this) {
			lastRuntimeInstanceNumber++;
		}
		// create map of dome object to corresponding solidworks object
		Iterator it = getModelObjects().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				Parameter p = (Parameter) o;
				Object map = getPluginMappingManager().getMappingObjectForParameter(p);
				if (map != null) {
					createSolidworksLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
				}
			}
		}
	}

	protected void createSolidworksLink(Parameter p, String name, boolean isInput)
	{
		Object solidworksObj = null;
		if (name.equalsIgnoreCase("Mass")) {
			solidworksObj = plg.createMass(p);
			((SolidworksMassNew) solidworksObj).setIsResult(true);
		} else if (name.equalsIgnoreCase("Angle unit")) {
			solidworksObj = plg.createAngleUnit(p);
			//((SolidworksAngleUnitNew) solidworksObj).setIsResult(true);
		} else if (name.equalsIgnoreCase("Color")) {
			solidworksObj = plg.createColors(p);
		} else if (name.equalsIgnoreCase("Length unit")) {
			solidworksObj = plg.createLengthUnit(p);
		} else if (name.equalsIgnoreCase("Surface area")) {
			solidworksObj = plg.createSurfaceArea(p);
			((SolidworksSurfaceAreaNew) solidworksObj).setIsResult(true);
		} else if (name.equalsIgnoreCase("Volume")) {
			solidworksObj = plg.createVolume(p);
			((SolidworksVolumeNew) solidworksObj).setIsResult(true);
		}
		else if (name.equalsIgnoreCase("IGES neutral file") ||
		         name.equalsIgnoreCase("STEP neutral file") ||
		         name.equalsIgnoreCase("VRML file")) {
			String filepath = ((DomeFile) p.getCurrentDataObject()).getFilePath();
			int index = filepath.lastIndexOf(File.separator);
			if (index == -1) {  //if no path is specified during build time
				File file = new File(filepath);
				filepath = file.getAbsolutePath();
				index = filepath.lastIndexOf(File.separator);
			}
			String dirpath = filepath.substring(0, index) + specificPath;
			specPaths.add(dirpath);
			filepath =  dirpath  + filepath.substring(index);
			File file = new File(filepath);
			File dir = file.getParentFile();
			if (dir != null || !dir.exists())
				dir.mkdir();
			((DomeFile) p.getCurrentDataObject()).setFilePath(filepath);
			solidworksObj = plg.createFile(filepath, p);
			System.out.println(filepath + "." + ((DomeFile) p.getCurrentDataObject()).getFileType());
			((SolidworksFileNew) solidworksObj).setIsResult(true);
			_savedFiles.addElement(p);
		}
		else if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
			solidworksObj = plg.createDimension(name, p);
			if (!isInput)
				((SolidworksDimensionNew) solidworksObj).setIsResult(true);
		}
	}
}

