package mit.cadlab.dome3.plugin.ideas;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasAssembly;
import mit.cadlab.dome3.plugin.ideas.dataobject.IdeasPart;
import mit.cadlab.dome3.plugin.ideas.dataobject.dataobjectsupport.IdeasDimensionIn;
import mit.cadlab.dome3.util.FileUtils;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 11, 2003
 * Time: 1:51:42 PM
 * To change this template use Options | File Templates.
 */
public class IdeasModelRuntime extends PluginModelRuntime
{
	private static int lastRuntimeInstanceNumber = 0;
	private String _vrmlDirectory;
	private String _modelName;
	private Vector _savedFiles = new Vector();

	protected IdeasPlugin plg;

	public IdeasModelRuntime(CompoundId id, Element xml, boolean isProjectResource)
	{
		super(id, xml, isProjectResource);
		loadNativeModel();
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
		if (!plg.isModelLoaded()) {
			plg.loadModel();
		}
		plg.execute(affectedOutputParams);
		if (isWaitingToDie())
			return; // don't set outputs if waiting to die
		if (this._savedFiles.size() > 0)
			plg.export();
		for (int i = 0; i < this._savedFiles.size(); i++) {
			Parameter p = (Parameter) this._savedFiles.elementAt(i);
			if(affectedOutputParams.contains(p))
			{
				FileData temp = (FileData)p.getCurrentDataObject();
				switchHeader(temp.getFilePath());
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

	public void deleteModel()
	{
		if (solver.isSolving()) {
			solver.stopSolving();
			waitingToDie = Boolean.TRUE;
			return;
		}
		stopModel ();
		File vDir = new File(_vrmlDirectory);
		File[] files = vDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		if (vDir.listFiles().length == 0) {
			vDir.delete();
		} else {
			Debug.trace(Debug.ALL, "Could not delete " + _vrmlDirectory);
		}
		plg.deleteModel();
		super.deleteModel();
	}

	protected void loadNativeModel()
	{
		// get configuration parameters
//The Auxilliary file name generally is more than 80 chars so Ideas fails to execute it
//        String fileName = getMainModelFileName();
        String fileName = ((DomeFile) pluginConfiguration.getSetupParameter(IdeasConfiguration.FILE_LOCATION).getCurrentDataObject()).getFilePath();
        File model = new File(fileName);
		String modelName = model.getName().substring(0, model.getName().length() - 4);
		String directoryName = model.getParent();
		String projectName = ((DomeString) pluginConfiguration.getSetupParameter(IdeasConfiguration.PROJECT_NAME).getCurrentDataObject()).getValue();
		// todo: get Ideas version information
		if (fileName == null)
			throw new UnsupportedOperationException("can not start Ideas model - no filename");
		this._vrmlDirectory = new StringBuffer(directoryName).append(File.separator + "ideas8_export_" + lastRuntimeInstanceNumber + File.separator).toString();
		synchronized(this) {
			lastRuntimeInstanceNumber++;
		}
		this._modelName = modelName;
		String serverName = null;
		try {
			serverName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			String msg = "ERROR: Cannot get local hostname";
			System.err.println(msg);
			throw new RuntimeException(msg);
		}

		//todo must change the plugin constructor arguments to correspond to the IdeasPlugin constructor arguments
        EnumerationData verData = (EnumerationData) pluginConfiguration.getSetupParameter(IdeasConfiguration.SOFTWARE_VERSION).getCurrentDataObject();
        String ideasVersion = verData.getElementName(verData.getLastSelection());
        String ideasDllName = IdeasConfiguration.IDEAS_8_DLL; // default
        if (IdeasConfiguration.IDEAS9.equals(ideasVersion))
            ideasDllName = IdeasConfiguration.IDEAS_9_DLL;
        if (IdeasConfiguration.IDEAS10.equals(ideasVersion))
            ideasDllName = IdeasConfiguration.IDEAS_10_DLL;

		plg = new IdeasPlugin(ideasDllName, modelName, directoryName, projectName, serverName);
		plg.createModel();

		// create map of dome object to corresponding solidworks object
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

	protected void createIdeasLink(Parameter p, String map, boolean isInput)
	{
		if (map.equalsIgnoreCase(IdeasConfiguration.PART)) {
			this.createIdeasPart(p, isInput);
		} else if (map.equalsIgnoreCase(IdeasConfiguration.ASSEMBLY)) {
			this.createIdeasAssembly(p, isInput);
		} else if (map.equalsIgnoreCase(IdeasConfiguration.VRML)) {
			String filePath = this._vrmlDirectory + this._modelName + ".wrl";
			((DomeFile) p.getCurrentDataObject()).setFilePath(filePath);
			this.createIdeasVrml(p, isInput);
		}

	}

	protected void createIdeasAssembly(Parameter p, boolean isInput)
	{
		String assemblyBin = null;
		String assemblyName = null;
		IdeasAssembly ideasAssembly = null;
		List assemblyData = p.getCurrentDataObject().getValues();

        assemblyBin = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter)assemblyData.remove(0));
		assemblyName = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter)assemblyData.remove(0));

		ideasAssembly = plg.createAssembly(p, assemblyBin, assemblyName);
		Iterator assemblyIterator = assemblyData.listIterator();
		while (assemblyIterator.hasNext())
        {
            Object tempObject = assemblyIterator.next();
            if (tempObject instanceof Parameter)
            {
                Parameter temp = (Parameter) tempObject;
                String map = (String)getPluginMappingManager().getMappingObjectForParameter(temp);
                if (IdeasConfiguration.VOLUME.equals(map))
                    ideasAssembly.createMassProperty(map, (DomeReal) temp.getCurrentDataObject());
                else if (IdeasConfiguration.MASS.equals(map))
                    ideasAssembly.createMassProperty(map, (DomeReal) temp.getCurrentDataObject());
                else if (IdeasConfiguration.SURFACE_AREA.equals(map))
                    ideasAssembly.createMassProperty(map, (DomeReal) temp.getCurrentDataObject());
	            else if (IdeasConfiguration.IGES.equals(map) || IdeasConfiguration.STEP.equals(map))
		            ideasAssembly.createExportFile(map);
            }
        }
    }

	protected void createIdeasPart(Parameter p, boolean isInput)
    {
        Object ideasPartDataObject = null;
        String partBin = null;
        String partName = null;
        IdeasPart ideasPart = null;
        List partData = p.getCurrentDataObject().getValues();

	    partBin = (String)this.getPluginMappingManager().getMappingObjectForParameter((Parameter)partData.remove(0));
	    partName = (String) this.getPluginMappingManager().getMappingObjectForParameter((Parameter)partData.remove(0));

	    ideasPart = plg.createPart(p, partBin, partName);
        Iterator partIterator = partData.listIterator();
        while (partIterator.hasNext())
        {
            Object tempObject = partIterator.next();
            if (tempObject instanceof Parameter)
            {
                Parameter temp = (Parameter) tempObject;
                String map = (String) getPluginMappingManager().getMappingObjectForParameter(temp);
                boolean input = getCausality(temp).equals(CausalityStatus.INDEPENDENT);
                if (IdeasConfiguration.VOLUME.equals(map))
                    ideasPart.createMassProperty(map, (DomeReal) temp.getCurrentDataObject());
                else if (IdeasConfiguration.MASS.equals(map))
                    ideasPart.createMassProperty(map, (DomeReal) temp.getCurrentDataObject());
                else if (IdeasConfiguration.SURFACE_AREA.equals(map))
                    ideasPart.createMassProperty(map, (DomeReal) temp.getCurrentDataObject());
                else if (IdeasConfiguration.STEP.equals(map) || IdeasConfiguration.IGES.equals(map))
					ideasPart.createExportFile(map);
	            else
                    if (input)
                    {
                        ideasPartDataObject = ideasPart.createDimensionIn(map, (DomeReal) temp.getCurrentDataObject());
                        ((IdeasDimensionIn) ideasPartDataObject).setIsResult(false);
                    }
                    else
                        ideasPartDataObject = ideasPart.createDimensionOut(map, (DomeReal) temp.getCurrentDataObject());
            }
        }
    }

    protected void createIdeasVrml(Parameter p, boolean isInput)
    {
        DomeFile f = (DomeFile) p.getCurrentDataObject();
        plg.createVrmlFile(p, this._modelName);
        this._savedFiles.addElement(p);
    }

    public static final String headerFile = "mit/cadlab/dome3/plugin/ideas/vrmlHeader.txt";

    public static void switchHeader(String vrmlFile)
    {


        URL headerURL = ClassLoader.getSystemResource(headerFile);
        if (headerURL == null)
        {
            throw new RuntimeException("File not found: " + headerFile);
        }

        BufferedReader br = null;
        BufferedReader brh = null;
        FileReader vrml = null;
        String aLine;
        String[] sa;
        StringBuffer sb = new StringBuffer();
        try
        {
            vrml = new FileReader(vrmlFile);

            br = new BufferedReader(vrml);
            boolean cond = true;
            do
            {
                aLine = br.readLine();
                sa = aLine.split(" ");
                if (sa.length > 1) cond = !sa[1].equals("MeasInfo");
            }
            while (cond);

            brh = new BufferedReader(new InputStreamReader(headerURL.openStream()));
            String s;
            while ((s = brh.readLine()) != null)
            {
                sb.append(s);
                sb.append("\n");
            }

            sb.append(aLine);
            while ((s = br.readLine()) != null)
            {
                sb.append(s);
                sb.append("\n");
            }

            br.close();
            brh.close();

            FileUtils.writeStringToFile(sb.toString(), vrmlFile);


        }
        catch (Exception e)
        {
            e.printStackTrace();
	        throw new RuntimeException(e.getMessage());
        }

    }
}