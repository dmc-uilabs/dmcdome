package mit.cadlab.dome3.plugin.ideas10;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Assembly;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10InterferenceAnalysis;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Part;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10Dimension;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10RealValueProperty;
import org.dom4j.Element;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

public class Ideas10ModelRuntime extends PluginModelRuntime
{
	// only allow one instance of IDEAS to startup at a time
	// attempts to load same file at the same time result in second one not getting the file
	private static Object IDEAS_STARTUP_LOCK = new Object();

	private static int lastRuntimeInstanceNumber = 0;
    private String _vrmlDirectory;

	protected Ideas10Plugin plg;

	public Ideas10ModelRuntime(CompoundId id, Element xml, boolean isProjectResource)
	{
		super(id, xml, isProjectResource);
		loadNativeModel();
	}

    protected void loadNativeModel() {
        String fileName = getMainModelFileName();
        if (fileName == null)
            throw new UnsupportedOperationException("can not start Ideas model - no filename");
        File model = new File(fileName);
        String directoryName = model.getParent();
        String projectName = ((DomeString) pluginConfiguration.getSetupParameter(Ideas10Configuration.PROJECT_NAME).getCurrentDataObject()).getValue();
        if (projectName == null)
            throw new UnsupportedOperationException("can not start Ideas model - no projectname");
        this._vrmlDirectory = directoryName;
        File vDir = new File(_vrmlDirectory + File.separator + "ideas10_export_" + lastRuntimeInstanceNumber + File.separator);
        if (vDir.exists() || vDir.mkdir())
            this._vrmlDirectory = _vrmlDirectory + File.separator + "ideas10_export_" + lastRuntimeInstanceNumber + File.separator;
	    synchronized (this) {
		    lastRuntimeInstanceNumber++;
	    }
        boolean useOndemandSvr = ((DomeBoolean) pluginConfiguration.getSetupParameter(Ideas10Configuration.USING_ON_DEMAND_SERVER).getCurrentDataObject()).getValue();
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            String msg = "Cannot get local hostname";
	        System.err.println(msg);
	        throw new RuntimeException(msg);
        }


	    synchronized (IDEAS_STARTUP_LOCK) {
		    plg = new Ideas10Plugin(hostName, fileName, projectName, useOndemandSvr);
		    plg.createModel();
	    }

	    // create map of dome object to corresponding ideas object
        Iterator it = getBuildContext().getModelObjectReferences().listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) {
                    createIdeasLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
                }
            }
        }
    }

	protected void executeNativePlugin(List affectedOutputParams)
	{
		if (!plg.isModelLoaded()) {
			plg.loadModel();
		}
		plg.execute(affectedOutputParams);
/*		for (int i = 0; i < this._savedFiles.size(); i++) {
			Parameter p = (Parameter) this._savedFiles.elementAt(i);
			if(affectedOutputParams.contains(p))
			{
				FileData temp = (FileData)p.getCurrentDataObject();
				switchHeader(temp.getFilePath());
				temp.notifyFileChanged();
			}
		}*/
	}

	/**
	 * Halt the native model.
	 */
	public void stopModel()
	{
		plg.unloadModel();
	}

	public void deleteModel()
	{
		if (solver.isSolving()) {
			solver.stopSolving();
			waitingToDie = Boolean.TRUE;
			plg.waitingToDie = true;
			return;
		}
		stopModel ();
		//delete vrml files
		File vDir = new File(_vrmlDirectory);
		File[] files = vDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		if(vDir.listFiles().length == 0) {
			vDir.delete();
		}
		else {
			Debug.trace(Debug.ALL, "Could not delete " + _vrmlDirectory);
		}
		plg.deleteModel();
		super.deleteModel();
	}

	protected void createIdeasLink(Parameter p, String map, boolean isInput)
	{
		if (map.equalsIgnoreCase(Ideas10Configuration.PART)) {
			this.createIdeasPart(p, isInput);
		} else if (map.equalsIgnoreCase(Ideas10Configuration.ASSEMBLY)) {
			this.createIdeasAssembly(p, isInput);
		} else if (map.equalsIgnoreCase(Ideas10Configuration.INTF)) {
            this.createInterferenceAnalysis(p);
        }
        // ***vrml can be for a part or an assmbly only, not for the whole model
        /*else if (map.equalsIgnoreCase(IdeasConfiguration.VRML)) {
			String filePath = this._vrmlDirectory + this._modelName + ".wrl";
			((DomeFile) p.getCurrentDataObject()).setFilePath(filePath);
			this.createIdeasVrml(p, isInput);
		}*/

	}

	protected void createIdeasAssembly(Parameter p, boolean isInput)
	{
		String assemblyBin = null;
		String assemblyName = null;
		Ideas10Assembly ideas10Assembly = null;
		List assemblyData = p.getCurrentDataObject().getValues();

        assemblyBin = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter)assemblyData.remove(0));
		assemblyName = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter)assemblyData.remove(0));

        ideas10Assembly = plg.createAssembly(p, assemblyBin, assemblyName);
		Iterator assemblyIterator = assemblyData.listIterator();
		while (assemblyIterator.hasNext())
        {
            Object tempObject = assemblyIterator.next();
            if (tempObject instanceof Parameter)
            {
                Parameter temp = (Parameter) tempObject;
                String map = (String)getPluginMappingManager().getMappingObjectForParameter(temp);
                if (Ideas10Configuration.VOLUME.equals(map))
                    ideas10Assembly.createRealValueProperty(map, Ideas10RealValueProperty.VOLUME, temp);
                else if (Ideas10Configuration.MASS.equals(map))
                    ideas10Assembly.createRealValueProperty(map, Ideas10RealValueProperty.MASS, temp);
                else if (Ideas10Configuration.OPEN_SURFACE_AREA.equals(map))
                    ideas10Assembly.createRealValueProperty(map, Ideas10RealValueProperty.OPEN_SURFACE_AREA, temp);
                else if (Ideas10Configuration.SOLID_SURFACE_AREA.equals(map))
                    ideas10Assembly.createRealValueProperty(map, Ideas10RealValueProperty.SOLID_SURFACE_AREA, temp);
                else if (Ideas10Configuration.VRML.equals(map)) {
                    DomeFile f = (DomeFile) temp.getCurrentDataObject();
                    String filePath = this._vrmlDirectory + (new File(f.getFilePath())).getName();
                    f.setFilePath(filePath);
                    ideas10Assembly.createVrmlFile(temp, plg, f);
                }
            }
        }
    }


    protected void createInterferenceAnalysis(Parameter p) {
        Ideas10InterferenceAnalysis intf = plg.createInterferenceAnalysis(p);
        List intfData = p.getCurrentDataObject().getValues();
        Iterator intfIterator = intfData.listIterator();
        while (intfIterator.hasNext()) {
            Object tempObject = intfIterator.next();
            if (tempObject instanceof Parameter) {
                Parameter temp = (Parameter) tempObject;
                String map = (String) getPluginMappingManager().getMappingObjectForParameter(temp);
                if (Ideas10Configuration.VRML.equals(map)) {
                    DomeFile f = (DomeFile) temp.getCurrentDataObject();
                    String filePath = this._vrmlDirectory + (new File(f.getFilePath())).getName();
                    f.setFilePath(filePath);
                    intf.createVrmlFile(p, plg, f);
                }
            }
        }


    }

	protected void createIdeasPart(Parameter p, boolean isInput)
    {
        Object ideasPartDataObject = null;
        String partBin = null;
        String partName = null;
        Ideas10Part ideas10Part = null;
        List partData = p.getCurrentDataObject().getValues();

	    partBin = (String)this.getPluginMappingManager().getMappingObjectForParameter((Parameter)partData.remove(0));
	    partName = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter)partData.remove(0));

	    ideas10Part = plg.createPart(p, partBin, partName);
        Iterator partIterator = partData.listIterator();
        while (partIterator.hasNext())
        {
            Object tempObject = partIterator.next();
            if (tempObject instanceof Parameter)
            {
                Parameter temp = (Parameter) tempObject;
                String map = (String) getPluginMappingManager().getMappingObjectForParameter(temp);
                boolean input = getCausality(temp).equals(CausalityStatus.INDEPENDENT);
                if (Ideas10Configuration.VOLUME.equals(map))
                    ideas10Part.createRealValueProperty(map, Ideas10RealValueProperty.VOLUME, temp);
                else if (Ideas10Configuration.MASS.equals(map))
                    ideas10Part.createRealValueProperty(map, Ideas10RealValueProperty.MASS, temp);
                else if (Ideas10Configuration.OPEN_SURFACE_AREA.equals(map))
                    ideas10Part.createRealValueProperty(map, Ideas10RealValueProperty.OPEN_SURFACE_AREA, temp);
                else if (Ideas10Configuration.SOLID_SURFACE_AREA.equals(map))
                    ideas10Part.createRealValueProperty(map, Ideas10RealValueProperty.SOLID_SURFACE_AREA, temp);
                else if (Ideas10Configuration.VRML.equals(map)) {
                    DomeFile f = (DomeFile) temp.getCurrentDataObject();
//                    String filePath = this._vrmlDirectory + f.getFilePath();
                    String filePath = this._vrmlDirectory + (new File(f.getFilePath())).getName();
                    f.setFilePath(filePath);
                    ideas10Part.createVrmlFile(temp, plg, f);
                }
	            else {
                    if (input)
                    {
                        ideasPartDataObject = ideas10Part.createDimension(map, temp);
                        ((Ideas10Dimension) ideasPartDataObject).setIsResult(false);
                    }
                    else
                        ideasPartDataObject = ideas10Part.createDimension(map, temp);
                }
            }
        }
    }
}