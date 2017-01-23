package mit.cadlab.dome3.plugin.maxwell;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.exceptions.ModelExecutionException;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.Regex;
import mit.cadlab.dome3.util.StreamSink;
import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MaxwellModelRuntime extends PluginModelRuntime {

	private static int lastRuntimeInstanceNumber = 0;
	private static final String outputLinePrefix = "MAXWELL";

	private boolean isModelLoaded = false;

	private String[] templateFileContents;
	private File[] origMaxwellFiles, projMaxwellFiles, templateFiles;
	private String executableCommand, postProcessingCommand, outputFileName;
	private File workingDirectory, projectDirectory;
	private HashMap inputParams = new HashMap(), outputParams = new HashMap();
	private List outputFiles = new ArrayList();

	private static synchronized int getNextRuntimeInstanceNumber() {
		return ++lastRuntimeInstanceNumber;
	}

    public MaxwellModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
        isModelLoaded = loadModelDefinition();
    }

    protected void executeNativePlugin(List affectedOutputParams) {
	    if (!isModelLoaded) {
		    String msg = "unable to run model: model did not load successfully";
		    System.err.println(msg);
		    throw new RuntimeException(msg);
	    }
	    if (writeInputParameters()) {
	        if (runModel()) {
		        readOutputParameters(affectedOutputParams);
		    }
	    }
    }

	protected boolean writeInputParameters()
	{
		FileUtils.deleteDirectoryContents(projectDirectory, false);
		String[] newTemplateFileContents = new String[templateFileContents.length];
		System.arraycopy(templateFileContents,0,newTemplateFileContents,0,templateFileContents.length);
		Iterator it = inputParams.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Parameter param = (Parameter) inputParams.get(key);
			DataObject dObj = param.getCurrentDataObject();
			String value = "";
			if (dObj instanceof DomeReal)
				value = ((DomeReal) dObj).getRealValue().toString();
			else if (dObj instanceof DomeInteger)
				value = ((DomeInteger) dObj).getIntegerValue().toString();
			else if (dObj instanceof DomeEnumeration) {
				DomeEnumeration enm = (DomeEnumeration)dObj;
				DataObject data = (DataObject)enm.getElementValue(enm.getLastSelection());
				if (data instanceof DomeReal)
					value = ((DomeReal) data).getRealValue().toString();
				else if (data instanceof DomeInteger)
					value = ((DomeInteger) data).getIntegerValue().toString();
				else if (data instanceof DomeString)
					value = ((DomeString) data).getValue();
				else if (data instanceof DomeText)
					value = ((DomeText) data).getValue();
				else if (data instanceof DomeFile)
					value = new File(((DomeFile) data).getFilePath()).getName();
			} else if (dObj instanceof DomeString)
				value = ((DomeString) dObj).getValue();
			else if (dObj instanceof DomeText)
				value = ((DomeText) dObj).getValue();
			else if (dObj instanceof DomeFile) {
				value = new File(((DomeFile) dObj).getFilePath()).getName();
			}
			for (int i = 0; i < newTemplateFileContents.length; i++) {
				newTemplateFileContents[i] = newTemplateFileContents[i].replaceAll(key, value);
			}
		}
		for (int i = 0; i < newTemplateFileContents.length; i++) {
			if (!FileUtils.writeStringToFile(newTemplateFileContents[i], templateFiles[i]))
				return false;
		}
		for (int i = 0; i < origMaxwellFiles.length; i++) {
			try {
				FileUtils.copyFile(origMaxwellFiles[i], projMaxwellFiles[i]);
			} catch (Exception ex) {
				System.err.println(ex);
				throw new RuntimeException(ex.getMessage());
			}
		}
		return true;
	}

	protected boolean runModel()
	{
		if (StreamSink.runCommand(executableCommand, workingDirectory, outputLinePrefix) != 0) {
			String msg = "error executing " + executableCommand;
			throw new ModelExecutionException(msg);
		}
		Debug.trace(Debug.ALL, "completed " + executableCommand);
		if (StreamSink.runCommand(postProcessingCommand, projectDirectory, outputLinePrefix) != 0) {
			String msg = "error executing " + postProcessingCommand;
			throw new ModelExecutionException(msg);
		}
		Debug.trace(Debug.ALL, "completed " + postProcessingCommand);
		return true;
	}

	/*protected void waitForModel() {
		File lockFile = new File(workingDirectory,baseName+".lck");
		while (!lockFile.exists()) { // wait till lock file is created
			try {
				Thread.currentThread().sleep(200);
			}
			catch (InterruptedException e) {

			}
		}
		System.out.println("found lock file");
		while (lockFile.exists()) { // wait till lock file disappears
			try {
				Thread.currentThread().sleep(500);
			}
			catch (InterruptedException e) {

			}
		}
		System.out.println("lock file gone");
	}*/

	public void deleteModel()
	{
		if (solver.isSolving()) {
			solver.stopSolving();
			waitingToDie = Boolean.TRUE;
			return;
		}
		super.deleteModel();
	}

	protected boolean readOutputParameters(List affectedOutputParams)
	{
		if (isWaitingToDie())
			return false; // don't set outputs if waiting to die
		if (outputFileName != null && !outputFileName.equals("")) {
			try {
				List lines = FileUtils.readTextFileAsList(new File(projectDirectory, outputFileName));
				String line, varName, value;
				List parts;
				Parameter outputParam;
				DataObject dObj;
				for (int i = 0; i < lines.size(); i++) {
					line = (String) lines.get(i);
					parts = Regex.split("=", line);
					varName = ((String) parts.get(0)).trim();
					value = ((String) parts.get(1)).trim();
					outputParam = (Parameter) outputParams.get(varName);
					if (outputParam == null || !affectedOutputParams.contains(outputParam))
						continue;
					dObj = outputParam.getCurrentDataObject();
					try {
						if (dObj instanceof DomeString) {
							((DomeString) dObj).setValue(value);
						}
						else if (dObj instanceof DomeInteger) {
							((DomeInteger) dObj).setValue((int) Double.parseDouble(value));
						}
						else if (dObj instanceof DomeReal) {
							((DomeReal) dObj).setValue(Double.parseDouble(value));
						}
						else {
							String msg = "unknown data type: " + dObj;
							System.err.println(msg);
							throw new RuntimeException(msg);
						}
					}
					catch (NumberFormatException e) {
						String msg = "unable to parse numeric value for " + varName + ": " + value;
						System.err.println(msg);
						throw new RuntimeException(msg);
					}
				}
			}
			catch (FileNotFoundException e) {
				System.err.println(e);
				throw new RuntimeException(e.getMessage());
			}
		}
		return true;
	}

	/**
	 * Loads model definition.
	 * @return true if load completes successfully; otherwise, returns false
	 */
    protected boolean loadModelDefinition() {
        // get configuration parameters
		// todo: figure out how to deal with files in multiple directories
		String mainFileName = getMainModelFileName();
		File mainFile = new File(mainFileName);
		System.out.println("main model file: " + mainFile.getAbsolutePath());

		File originalDirectory = mainFile.getParentFile().getParentFile(); // need two levels up to get parent directory
		System.out.println("originalDir=" + originalDirectory.getAbsolutePath());

		workingDirectory = new File(originalDirectory.getAbsolutePath(),"Run_"+getNextRuntimeInstanceNumber());
		try {
			if (!workingDirectory.exists() && !workingDirectory.mkdir()) {
				String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath();
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
		}
		catch (Exception e) {
			String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath()+"\n\t" + e;
			System.err.println(msg);
			throw new RuntimeException(msg);
		}
		System.out.println("workingDir=" + workingDirectory.getAbsolutePath());

		projectDirectory = new File(workingDirectory, mainFile.getParentFile().getName());
	    try {
		    if (!projectDirectory.exists() && !projectDirectory.mkdir()) {
			    String msg = "unable to create project directory: " + projectDirectory.getAbsolutePath();
			    System.err.println(msg);
			    throw new RuntimeException(msg);
		    }
	    }
	    catch (Exception e) {
		    String msg = "unable to create project directory: " + projectDirectory.getAbsolutePath() + "\n\t" + e;
		    System.err.println(msg);
		    throw new RuntimeException(msg);
	    }
		System.out.println("projectDir=" + projectDirectory.getAbsolutePath());

		// copy non-template auxiliary files to working directory
		List origTemplateFiles = new ArrayList(); // files in this list are not copied to working directory
		List origMaxwellFiles = new ArrayList();
		int numAuxFiles = AuxFiles.size();
		File f;
		String templateFileName, templateFileDomeName;
		for (int i=0; i<numAuxFiles; i++) {
			f = new File(this.getAuxFilePathName(i));
			templateFileName = f.getName();
			templateFileDomeName = this.getAuxFileDomeName(i);
			if (templateFileDomeName.startsWith("template"))
				origTemplateFiles.add(f);
			else
				origMaxwellFiles.add(f);
		}

		// cache template files
		templateFileContents = new String[origTemplateFiles.size()]; // stores templates
		templateFiles = new File[origTemplateFiles.size()];
		for (int i = 0; i < origTemplateFiles.size(); i++) {
			f = (File) origTemplateFiles.get(i);
			if (f.exists() && f.isFile()) {
				templateFileName = f.getName();
				try {
					templateFileContents[i] = FileUtils.readTextFileAsString(f);
				}
				catch (Exception e) {
					String msg = "unable to read template file: " + f.getAbsolutePath()+ "\n\t" + e;
					System.err.println(msg);
					throw new RuntimeException(msg);
				}
				templateFiles[i] = new File(projectDirectory, templateFileName);
			}
			else {
				String msg = "unable to find template file: " + f.getAbsolutePath();
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
		}

		// cache maxwell file names
		this.origMaxwellFiles = (File[])origMaxwellFiles.toArray(new File[]{});
		this.projMaxwellFiles = new File[this.origMaxwellFiles.length];
		File maxwellFile;
		for (int i = 0; i < projMaxwellFiles.length; i++) {
			maxwellFile = this.origMaxwellFiles[i];
			projMaxwellFiles[i] = new File(projectDirectory,maxwellFile.getName());
		}

		executableCommand = ((DomeString)pluginConfiguration.getSetupParameter(MaxwellConfiguration.EXECUTABLE_COMMAND).getCurrentDataObject()).getValue();
		postProcessingCommand = ((DomeString) pluginConfiguration.getSetupParameter(MaxwellConfiguration.POSTPROCESSING_COMMAND).getCurrentDataObject()).getValue();
		outputFileName = ((DomeString) pluginConfiguration.getSetupParameter(MaxwellConfiguration.OUTPUT_DATA_FILENAME).getCurrentDataObject()).getValue();

		System.out.println("executableCommand=" + executableCommand);
	    System.out.println("postProcessingCommand=" + postProcessingCommand);
		System.out.println("outputFileName=" + outputFileName);

        // create map of template variables to dome objects
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) { // todo: should non-mapped parameters be linked with abaqus?
                    createMaxwellLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
                }
            }
        }
		return true;
    }

    protected void createMaxwellLink (Parameter p, String refString, boolean isInput)
    {
	    if (isInput) {
		    inputParams.put(createTemplateReference(refString),p);
	    } else {
		    if (p.getCurrentDataObject() instanceof DomeFile) {
			    FileData d = (FileData)p.getCurrentDataObject();
			    inputParams.put(createTemplateReference(refString),p); // output file names may be used as inputs
			    // set location of output file to be in working directory
			    File f = new File(d.getFilePath());
			    File newF = new File(workingDirectory,f.getName());
			    d.setFilePath(newF.getAbsolutePath());
		        outputFiles.add(d);
		    } else
		        outputParams.put(refString, p); // not really used now
	    }
    }

	/**
	 * @param refString the string in the template with or without % signs around it
	 * @return the refString with % signs around it
	 */
	protected String createTemplateReference(String refString) {
		refString = refString.trim();
		if (!refString.startsWith("%"))
			refString = "%"+refString;
		if (!refString.endsWith("%"))
			refString = refString + "%";
		return refString;
	}

}
