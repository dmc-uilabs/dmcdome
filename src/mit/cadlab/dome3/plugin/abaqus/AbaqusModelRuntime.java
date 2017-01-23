package mit.cadlab.dome3.plugin.abaqus;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
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
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.Regex;
import mit.cadlab.dome3.util.StreamSink;
import mit.cadlab.dome3.util.TextFileIterator;
import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AbaqusModelRuntime extends PluginModelRuntime {

	private static final String outputLinePrefix = "ABAQUS";

	private boolean isModelLoaded = false;
	private boolean isIterativeModel = false;

	private String[] templateFileContents;
	private File[] templateFiles;
	private String executableCommand, postProcessingCommand, outputFileName;
	private DomeString modelName; // used to calculate lock file name; parameter mapped to "lock_file"
	private HashMap inputParams = new HashMap();
	private AggregatorMap outputParams = new AggregatorMap();
	private List outputFiles = new ArrayList();

	// for iterative model only
	private String initialModelContents, restartModelContents;
	private boolean isFirstRunComplete = false;

    public AbaqusModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
        isModelLoaded = loadModelDefinition();
    }

    protected void executeNativePlugin(List affectedOutputParams) {
	    if (!isModelLoaded) {
		    throw new RuntimeException("unable to run model: model did not load successfully");
	    }
	    if (writeInputParameters()) {
	        if (runModel()) {
		        if (readOutputParameters(affectedOutputParams))
		            if (isIterativeModel && !isFirstRunComplete)
			            isFirstRunComplete = true;
		    }
	    }
    }

	protected boolean writeInputParameters()
	{
		int numberTemplateFiles = templateFileContents.length;
		int modelTemplateIndex = 0; // only used for iterative models
		if (isIterativeModel) {
			modelTemplateIndex = numberTemplateFiles;
			numberTemplateFiles++; // add model template to list
		}
		String[] newTemplateFileContents = new String[numberTemplateFiles];
		System.arraycopy(templateFileContents,0,newTemplateFileContents,0,templateFileContents.length);
		if (isIterativeModel) {
			if (isFirstRunComplete)
				newTemplateFileContents[modelTemplateIndex] = restartModelContents;
			else
				newTemplateFileContents[modelTemplateIndex] = initialModelContents;
		}

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
			else if (dObj instanceof DomeString)
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
		for (int i = 0; i < templateFiles.length; i++) {
			if (!FileUtils.writeStringToFile(newTemplateFileContents[i], templateFiles[i]))
				return false;
		}
		if (isIterativeModel)
			if (!FileUtils.writeStringToFile(newTemplateFileContents[modelTemplateIndex],
			                                 new File(workingDirectory, modelName.getValue()+".inp")))
				return false;
		return true;
	}

	protected boolean runModel()
	{
		if (StreamSink.runCommand(executableCommand, workingDirectory, outputLinePrefix) != 0) {
			String msg = "error executing " + executableCommand;
			throw new ModelExecutionException(msg);
		}
		if (modelName != null)
			waitForModel();
		Debug.trace(Debug.ALL, "completed " + executableCommand);
		if (StreamSink.runCommand(postProcessingCommand, workingDirectory, outputLinePrefix) != 0) {
			String msg = "error executing " + postProcessingCommand;
			throw new ModelExecutionException(msg);
		}
		Debug.trace(Debug.ALL, "completed " + postProcessingCommand);
		return true;
	}

	protected void waitForModel() {
		File lockFile = new File(workingDirectory, modelName.getValue()+".lck");
		while (!lockFile.exists()) { // wait till lock file is created
			try {
				Thread.currentThread().sleep(200);
			}
			catch (InterruptedException e) {

			}
		}
		Debug.trace(Debug.ALL, "found lock file: "+lockFile.getName());
		while (lockFile.exists()) { // wait till lock file disappears
			try {
				Thread.currentThread().sleep(500);
			}
			catch (InterruptedException e) {

			}
		}
		Debug.trace(Debug.ALL, "lock file gone");
	}

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
		if (outputFileName == null || outputFileName.equals(""))
			return true; // no outputs, but that's ok
		File outputFile = new File(workingDirectory, outputFileName);
		TextFileIterator tfi = new TextFileIterator(outputFile);
		String line, varName, value = null;
		double[][] values = null;
		List parts, params;
		Parameter param;
		DataObject dObj;
		while (tfi.hasNext()) {
			if (isWaitingToDie())
				return false; // don't set outputs if waiting to die
		
			line = ((String) tfi.next()).trim();
			if (line.startsWith(AbaqusConfiguration.OUTPUT_COMMENT_CHARACTER) || line.equals(""))
				continue;
			parts = Regex.split("=", line, 2);
			if (parts.size() != 2) {
				System.err.println("Invalid output line format (no equal sign found): "+line);
				continue;
			}
			varName = ((String) parts.get(0)).trim();
			params = (List) outputParams.get(varName);
			if (params==null || params.isEmpty())
				continue; // no output parameter mapped to this input
			value = ((String) parts.get(1)).trim();
			param = (Parameter) params.get(0); // use the first parameter to determine type
			dObj = param.getCurrentDataObject();
			boolean isMatrix = false;
			if (dObj instanceof DomeMatrixData) {
				values = readMatrixData(value, tfi, varName);
				isMatrix = true;
			}

			// set the values in the parameter(s)
			for (int i = 0; i < params.size(); i++) {
				param = (Parameter) params.get(i);
				if (affectedOutputParams.contains(param)) {
					dObj = param.getCurrentDataObject();
					if (isMatrix)
						((DomeMatrixData) dObj).setValues(values);
					else
						setDataObjectValue(dObj, value, param.getName());
				}
			}
		}

		for (int i = 0; i < outputFiles.size(); i++) {
			Parameter p = (Parameter) outputFiles.get(i);
			if (affectedOutputParams.contains(p))
				((FileData) p.getCurrentDataObject()).notifyFileChanged();
		}

		return true;
	}

	/**
	 * Parses matrix data of the form [1 2 3; 4 5 6]
	 * Identifies start of matrix with [ and end of matrix with ]
	 * Use semicolons to separate rows; spaces to separate columns
	 * The matrix may span many rows and will end with the first right bracket, ], found.
	 * Rows may span multiple lines
	 * @param firstLine
	 * @param tfi
	 * @return
	 */
	private double[][] readMatrixData(String firstLine, TextFileIterator tfi, String varName) {
		String content = firstLine;
		if (firstLine.indexOf("]") == -1) { // matrix spans multiple lines
			StringBuffer sb = new StringBuffer(firstLine);
			String line = null;
			while (tfi.hasNext()) {
				line = ((String)tfi.next()).trim();
				if (line.startsWith("#") || line.equals(""))
					continue;
				sb.append(" "+line);
				if (line.indexOf("]") != -1) // found end of matrix
					break;
			}
			if (line!=null && !line.endsWith("]")) // check if end of file reached before end of matrix
				throw new RuntimeException("unable to find end of matrix for " + varName);
			content = sb.toString();
		}

		content = content.substring(1,content.length()-1); // strip off [ and ]
		List rows = Regex.split(";",content); // always at least one row; split guarantees this
		double[][] result = new double[rows.size()][];
		String rowString;
		List cols;
		String item;
		double[] row;
		for (int i = 0; i < rows.size(); i++) {
			rowString = ((String) rows.get(i)).trim();
			cols = Regex.split(Regex.whitespace, rowString);
			row = new double[cols.size()];
			for (int j = 0; j < cols.size(); j++) {
				item = (String) cols.get(j);
				try {
					row[j] = Double.parseDouble(item);
				}
				catch (NumberFormatException e) {
					System.err.println("invalid matrix data  at " + varName + "[" + i + "][" + j + "]: " + item);
					row[j] = 0.0;
				}
			}
			result[i] = row;
		}
		return result;
	}

	private void setDataObjectValue(DataObject dObj, String value, String varName) {
		try {
			if (dObj instanceof DomeString) {
				((DomeString) dObj).setValue(value.toString());
			}
			else if (dObj instanceof DomeInteger) {
				((DomeInteger) dObj).setValue(Integer.parseInt(value));
			}
			else if (dObj instanceof DomeReal) {
				((DomeReal) dObj).setValue(Double.parseDouble(value));
			}
			else {
				String msg = "Abaqus Model - unsupported output data type: " + ClassUtils.getClassName(dObj);
				throw new RuntimeException(msg);
			}
		}
		catch (NumberFormatException e) {
			String msg = "unable to parse numeric value for " + varName + ": " + value;
			throw new RuntimeException(msg);
		} // todo: test class cast exception handling
	}

	/**
	 * Loads model definition.
	 * @return true if load completes successfully; otherwise, returns false
	 */
    protected boolean loadModelDefinition() {
		// todo: figure out how to deal with files in multiple directories

		Parameter isIterativeModelParam = pluginConfiguration.getSetupParameter(AbaqusConfiguration.ITERATIVE_MODEL);
		if (isIterativeModelParam != null)
			isIterativeModel = ((DomeBoolean) isIterativeModelParam.getCurrentDataObject()).getValue();

		// copy non-template auxiliary files to working directory
		List origTemplateFiles = new ArrayList(); // files in this list are not copied to working directory
		int numAuxFiles = AuxFiles.size();
		File f, initialModelFile=null, restartModelFile=null;
		String templateFileName, templateFileDomeName;
		for (int i=0; i<numAuxFiles; i++) {
			f = new File(this.getAuxFilePathName(i));
			templateFileName = f.getName();
			templateFileDomeName = this.getAuxFileDomeName(i);
			if (templateFileDomeName.indexOf("template") != -1) { // name contains "template"
				if (isIterativeModel) {
					if (templateFileDomeName.equals(AbaqusConfiguration.INITIAL_MODEL_TEMPLATE)) {
						initialModelFile = f;
					} else if (templateFileDomeName.equals(AbaqusConfiguration.RESTART_MODEL_TEMPLATE)) {
						restartModelFile = f;
					} else {
						origTemplateFiles.add(f);
					}
				} else
					origTemplateFiles.add(f);
			} else
				try {
					FileUtils.copyFile(f,new File(workingDirectory, templateFileName));
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
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
					String msg = "unable to read template file: " + f.getAbsolutePath();
					throw new RuntimeException(msg);
				}
				templateFiles[i] = new File(workingDirectory, templateFileName);
			}
			else {
				String msg = "unable to find template file: " + f.getAbsolutePath();
				throw new RuntimeException(msg);
			}
		}

		// cache iterative model files
		if (isIterativeModel) {
			if (initialModelFile!=null && initialModelFile.exists() && initialModelFile.isFile()) {
				try {
					initialModelContents = FileUtils.readTextFileAsString(initialModelFile);
				}
				catch (FileNotFoundException e) {
					String msg = "unable to read initial model template file: " + initialModelFile.getAbsolutePath()
					        + "\n\t" + e;
					throw new RuntimeException(msg);
				}
			} else {
				String msg = "unable to find initial model template; this file's DOME name must be " +
				        AbaqusConfiguration.INITIAL_MODEL_TEMPLATE;
				throw new RuntimeException(msg);
			}
			if (restartModelFile != null && restartModelFile.exists() && restartModelFile.isFile()) {
				try {
					restartModelContents = FileUtils.readTextFileAsString(restartModelFile);
				}
				catch (FileNotFoundException e) {
					String msg = "unable to read restart model template file: " + restartModelFile.getAbsolutePath() + "\n\t" + e;
					throw new RuntimeException(msg);
				}
			}
			else {
				String msg = "unable to find restart model template; this file's DOME name must be " +
				        AbaqusConfiguration.RESTART_MODEL_TEMPLATE;
				throw new RuntimeException(msg);
			}
		}

		executableCommand = ((DomeString)pluginConfiguration.getSetupParameter(AbaqusConfiguration.EXECUTABLE_COMMAND).getCurrentDataObject()).getValue();
		postProcessingCommand = ((DomeString) pluginConfiguration.getSetupParameter(AbaqusConfiguration.POSTPROCESSING_COMMAND).getCurrentDataObject()).getValue();

	    System.out.println("executableCommand=" + executableCommand);
	    System.out.println("postProcessingCommand=" + postProcessingCommand);

		try {
			outputFileName = ((DomeString) pluginConfiguration.getSetupParameter(AbaqusConfiguration.OUTPUT_DATA_FILENAME).getCurrentDataObject()).getValue();
			System.out.println("outputFileName=" + outputFileName);
		}
		catch (Exception e) {
			// ignore; model does not have outputFileName parameter (old-style model)
		}

		// create map of template variables to dome objects
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) { // todo: should non-mapped parameters be linked with abaqus?
                    createAbaqusLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
                }
            }
        }
		return true;
    }

    protected void createAbaqusLink (Parameter p, String refString, boolean isInput)
    {
	    if (p.getCurrentDataObject() instanceof DomeFile) {
		    FileData d = (FileData) p.getCurrentDataObject();
		    inputParams.put(createTemplateReference(refString), p); // output file names may be used as inputs
		    if (!isInput) // output file
		        outputFiles.add(p);
	    } else if (isInput) {
		    inputParams.put(createTemplateReference(refString),p);
	    } else {
		    outputParams.put(refString, p);
	    }
	    if (refString.equals(AbaqusConfiguration.MODEL_NAME_MAPPING)) {
		    modelName = (DomeString)p.getCurrentDataObject();
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
