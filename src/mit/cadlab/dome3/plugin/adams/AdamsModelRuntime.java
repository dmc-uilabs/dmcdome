package mit.cadlab.dome3.plugin.adams;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.exceptions.ModelExecutionException;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.StreamSink;
import mit.cadlab.dome3.plugin.PluginModelRuntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Oct 8, 2003
 * Time: 11:29:12 AM
 * To change this template use Options | File Templates.
 */
public class AdamsModelRuntime extends PluginModelRuntime
{
	 public static final String  REQUEST = "Request";
	public static final String REQUEST1 = "request/";
	public static final String NUMBER = "Number";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String EXCLAMATION = "!";
	public static final String OPENARRAY = "[";
	public static final String CLOSEDARRAY = "]";
	public static final String DOT = ".";
	public static final String TIME = "Time";
	private String outputBaseName = "";
	private String[] runCommands;
	private HashMap inputParams = new HashMap(), outputParams = new HashMap();
    private List savedFiles = new ArrayList();
    private boolean useMultipleOutputs = false;
	private HashMap outputFiles;
	private HashMap paramScopeMap= new HashMap();
	private NumberFormat numFormat;
    private boolean isOutputPostProcessed;
    private String postProcessCommand;
	private String[] templateFileContents;
	private File[] templateFiles;

	public AdamsModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(parentId, xml, isProjectResource);
		numFormat = NumberFormat.getNumberInstance();
		numFormat.setMaximumFractionDigits(2);
		((DecimalFormat) numFormat).applyPattern("#0.0#");
		loadModelDefinition();
	}

	protected void executeNativePlugin(List affectedOutputParams)
	{
		if (writeInputParameters()) {
			if (runModel()) {
				readOutputParameters(affectedOutputParams);
			}
		}
	}

	protected boolean writeInputParameters()
	{
		String[] newTemplateFileContents = new String[templateFileContents.length];
		System.arraycopy(templateFileContents, 0, newTemplateFileContents, 0, templateFileContents.length);
		Iterator it = inputParams.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Parameter param = (Parameter) inputParams.get(key);
			DataObject dObj = param.getCurrentDataObject();
			String value = "";
			if (dObj instanceof DomeReal)  {
				value = numFormat.format(((DomeReal) dObj).getRealValue());
			}
			else if (dObj instanceof DomeInteger)
				value = ((DomeInteger) dObj).getIntegerValue().toString();
			else if (dObj instanceof DomeString)
				value = ((DomeString) dObj).getValue();
			else if(dObj instanceof DomeVector) {
				List data = ((DomeVector)dObj).getData();
				StringBuffer sb = new StringBuffer();
				for (Iterator iterator = data.iterator(); iterator.hasNext();) {
					Object o = iterator.next();
					if(sb.length() > 0)
						sb.append(", ");
                    if(o instanceof Double) {
	                    String numStr = numFormat.format(((Double) o).doubleValue());
	                    sb.append(numStr);
                    }
					else if(o instanceof Integer) {
						sb.append(((Integer)o).intValue());
					}
				}
				value = sb.toString();
			}
			else if(dObj instanceof DomeFile) {
                String path =  ((DomeFile)dObj).getFilePath();
				try {
					value =  FileUtils.readTextFileAsString(path);
				}
				catch(FileNotFoundException e) {
					Debug.trace(Debug.ALL, "File " +  ((DomeFile) dObj).getFilePath() + "not found");
					throw new RuntimeException("File " + ((DomeFile) dObj).getFilePath() + "not found", e);
				}
			}
			//TODO take care of files as strings in enum
			else if(dObj instanceof DomeEnumeration) {
				int index = ((DomeEnumeration) dObj).getLastSelection();
				Object valObject = ((DomeEnumeration)dObj).getElementValue(index);
				if(valObject instanceof Integer || valObject instanceof Double ||
				   valObject instanceof String) {
					value = valObject.toString();
				}
				else if(valObject instanceof Boolean) {
                    if(valObject.equals(Boolean.TRUE)) {
	                    value = "1";
                    }
					else {
	                    value = "0";
                    }
				}
			}
			for (int i = 0; i < newTemplateFileContents.length; i++) {
				newTemplateFileContents[i] = newTemplateFileContents[i].replaceAll(key, value);
			}
		}
		for (int i = 0; i < newTemplateFileContents.length; i++) {
			if (!FileUtils.writeStringToFile(newTemplateFileContents[i], templateFiles[i])) {
				String msg = "Error writing template inputs to " + workingDirectory;
				throw new RuntimeException(msg);
			}
		}
		return true;
	}

	protected boolean runModel()
	{
		for (int i = 0; i < runCommands.length; i++) {
			String runCommand = runCommands[i];
			if (StreamSink.runCommand(runCommand, workingDirectory, "ADAMS") != 0) {
				String msg = "error executing " + runCommand;
				throw new ModelExecutionException(msg);
			}
		}
		return true;
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

	public boolean readOutputParameters(List affectedOutputParams)
	{
		if (isWaitingToDie())
			return false; // don't set outputs if waiting to die
		if (outputFiles == null) {
			return false;
		}
		else {
			if(isOutputPostProcessed) { //to extract name vale pairs - for Ford DCE
				try {
	                //run post processor
					if (StreamSink.runCommand(postProcessCommand, workingDirectory, "ADAMS POST-PROCESSING") != 0) {
	                    String msg = "error executing " + postProcessCommand;
	                    throw new ModelExecutionException(msg);
					}
					if (isWaitingToDie())
						return false; // don't set outputs if waiting to die

					//read the postprocessed file
					String filepath = (String) outputFiles.values().iterator().next();
					String output = null;
					HashMap processedmap = null;

					try {
						output = FileUtils.readTextFileAsString(filepath);
					}
					catch (FileNotFoundException e) {
						System.err.println("ADAMS could not find output file: "+filepath);
						return false;
					}
					processedmap = readProcesedOutput(output);
					for (Iterator iterator = outputParams.keySet().iterator(); iterator.hasNext();) {
						if (isWaitingToDie())
							return false; // don't set outputs if waiting to die
		
						String key = (String) iterator.next();
						Parameter param = (Parameter) outputParams.get(key);
						if (affectedOutputParams.contains(param)) {
							String value = (String) processedmap.get(key);
							if (value == null) {
								System.err.println("cannot find value of " + key + " in output file");
								continue;
							}
							if (param.getCurrentDataObject() instanceof DomeReal) {
								double d;
								try {
									d = Double.parseDouble(value);
								}
								catch (NumberFormatException e) {
									System.err.println("invalid real for " + param.getName() + ": " + value);
									d = 0.;
								}
								((DomeReal) param.getCurrentDataObject()).setValue(d);
							}
							else if (param.getCurrentDataObject() instanceof DomeInteger) {
								int in;

								try {
									in = Integer.parseInt(value);
								}
								catch (NumberFormatException e) {
									System.err.println("invalid integer for " + param.getName() + ": " + value);
									in = 0;
								}
								((DomeInteger) param.getCurrentDataObject()).setValue(in);
							}
						}
					}
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			else {   // for multiple .out files
				HashMap tokensMap = new HashMap();
				HashMap outputMap = new HashMap();
				String output = null;
				ArrayList outputTokens = null;
				HashMap requestMap = null;
				for (Iterator iterator = outputParams.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					Parameter p = (Parameter) outputParams.get(key);

					if(useMultipleOutputs) {
						String filescope = (String) paramScopeMap.get(p);
						outputTokens = (ArrayList)tokensMap.get(filescope);
						if(outputTokens == null) {
							String filepath = (String)outputFiles.get(filescope);
							try {
								output = FileUtils.readTextFileAsString(filepath);
							}
							catch (IOException e) {
								String msg = "File " + filepath + " not found";
								throw new RuntimeException(msg, e);
							}
							outputTokens = readTokensInList(output);
							tokensMap.put(filescope, outputTokens);
							requestMap = getRequestMap(outputTokens);
						}
					}
					else {
						if(requestMap == null) {
							String filepath = null;
							try {
								filepath = (String)outputFiles.values().iterator().next();
								output = FileUtils.readTextFileAsString(filepath);
							} catch (IOException e) {
								String msg = "File " + filepath + " not found";
								throw new RuntimeException(msg, e);
							}
							outputTokens = readTokensInList(output);
							requestMap = getRequestMap(outputTokens);
						}
					}

					if (p.getCurrentDataObject() instanceof DomeMatrixData) {
						double[][] values = null;
						if (useMultipleOutputs) {
							String filescope = (String) paramScopeMap.get(p);
							values = (double[][]) outputMap.get(filescope + ":" + key);
						}
						else {
							values = (double[][])outputMap.get(key);
						}
						if(values == null) {
							Integer position = ((Integer) requestMap.get(key));
							DomeMatrixData d = (DomeMatrixData) p.getCurrentDataObject();
							int rows = d.getRowCount();
							int cols = d.getColumnCount();
							values = (double[][]) getValues(position, outputTokens, rows, cols);
							d.setValues(values);
							if (useMultipleOutputs) {
								String filescope = (String) paramScopeMap.get(p);
								outputMap.put(filescope + ":" + key, values);
							}
							else {
								outputMap.put(key, values);
							}
						}
					}
					else if(p.getCurrentDataObject() instanceof RealData ||
					        p.getCurrentDataObject() instanceof IntegerData){
						int colonindex = key.indexOf(COLON);
						String requestNumber = key.substring(0, colonindex);
						double[][] values = null;
						if (useMultipleOutputs) {
							String filescope = (String) paramScopeMap.get(p);
							values = (double[][]) outputMap.get(filescope + ":" + requestNumber);
						}
						else {
							values = (double[][])outputMap.get(requestNumber);
						}
						if(values == null) {
							Integer position = ((Integer) requestMap.get(requestNumber));
							int commaindex = key.indexOf(COMMA);
							String rws = key.substring(colonindex + 1, commaindex);
							int rows = Integer.parseInt(rws.trim());
							int exclamationindex = key.indexOf(EXCLAMATION);
							String cls = key.substring(commaindex + 1, exclamationindex);
							int cols = Integer.parseInt(cls.trim());
							values = (double[][]) getValues(position, outputTokens, rows, cols);
							if (useMultipleOutputs) {
								String filescope = (String) paramScopeMap.get(p);
								outputMap.put(filescope + ":" + requestNumber, values);
							}
							else {
								outputMap.put(requestNumber, values);
							}
						}
						int firstopenindex = key.indexOf(OPENARRAY);
						int firstclosedindex = key.indexOf(CLOSEDARRAY);
						String r = key.substring(firstopenindex + 1, firstclosedindex);
						int row = Integer.parseInt(r.trim());
						int lastopenindex = key.lastIndexOf(OPENARRAY);
						int lastclosedindex = key.lastIndexOf(CLOSEDARRAY);
						String c = key.substring(lastopenindex + 1, lastclosedindex);
						int col = Integer.parseInt(c.trim());
						if(p.getCurrentDataObject() instanceof RealData) {
							((RealData) p.getCurrentDataObject()).setValue(values[row][col]);
						}
						else {
							int in = new Double(values[row][col]).intValue();
							((IntegerData) p.getCurrentDataObject()).setValue(in);
						}
					}
				}
			}
		}
		//if file is an output param fire a file change event
		for (Iterator iterator = savedFiles.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			if(affectedOutputParams.contains(p)) {
				FileData domeFile = (FileData)p.getCurrentDataObject();
				domeFile.notifyFileChanged();
			}
		}
		return true;
	}

	private HashMap readProcesedOutput(String output) {
		HashMap map = new HashMap();
		String[] result = output.split("\\r\\n");
		for (int i = 0; i < result.length; i++) {
			String str = result[i];
			int index = str.indexOf(" ");
			if(index != -1) {
				String key = str.substring(0, index);
				key.trim();
				if(outputParams.containsKey(key)) {
					String value = str.substring(index + 1, str.length());
					value.trim();
					map.put(key, value);
				}
			}
		}
		return map;
	}

	private Object getValues(Integer position, ArrayList outputTokens, int rows, int cols) {
		double[][] values = null;
		if (position != null) {
			int pos = position.intValue();
			while (true) {
				String str = (String) outputTokens.get(++pos);
				if (str.equals(TIME)) {
					break;
				}
			}
			pos = pos + cols;
			values = new double[rows][cols];
			for (int j = 0; j < rows; j++) {
				for (int k = 0; k < cols; k++) {
					String vl = (String) outputTokens.get(pos++);
					values[j][k] = Double.parseDouble(vl);
				}
			}
		}
		return values;
	}

	private HashMap getRequestMap(List outputTokens) {
		int firstindex = outputTokens.indexOf(REQUEST);
		int lastindex = outputTokens.lastIndexOf(REQUEST);
		HashMap requestmap = new HashMap();
		for (int i = firstindex; i <= lastindex; i++) {
			String first = (String) outputTokens.get(i);
			String second = (String) outputTokens.get(i + 1);
			String requestNumber = (String) outputTokens.get(i + 2);
			if (first.equals(REQUEST) && second.equals(NUMBER)) {
				requestmap.put(requestNumber, new Integer(i));
			}
		}
		return requestmap;
	}

	private ArrayList readTokensInList(String str)
	{
		ArrayList list = new ArrayList();
		String[] tResult = str.split("\\n");
		for (int i = 0; i < tResult.length; i++) {
			if (tResult[i].equals("") || tResult[i].equals(" ") || tResult[i].equals("\n")) {
				continue;
			}
			else {
				String[] tSubResult = tResult[i].split("\\s");
				for (int j = 0; j < tSubResult.length; j++) {
					if (!tSubResult[j].equals("")) {
						list.add(tSubResult[j]);
					}
				}
			}
		}
		return list;
	}

	protected void loadModelDefinition()
	{
		// get configuration parameters
		String programName = ((DomeString) pluginConfiguration.getSetupParameter(
		                            AdamsConfiguration.SOFTWARE_LOCATION).getCurrentDataObject()).getValue();
		Debug.trace(Debug.ALL, "path to ADAMS executable = " + programName);

		// copy non-template auxiliary files to working directory
		List adamsCommandFiles = new ArrayList(); // files that end in .acf
		List origTemplateFiles = new ArrayList(); // files in this list are not copied to working directory
		int numAuxFiles = AuxFiles.size();
		File f;
		String templateFileName;
		for (int i = 0; i < numAuxFiles; i++) {
			f = new File(this.getAuxFilePathName(i));
			templateFileName = f.getName();
			if (templateFileName.endsWith(".adm"))
				origTemplateFiles.add(f);
			else if (templateFileName.endsWith(".acf")) {
				origTemplateFiles.add(f);
				adamsCommandFiles.add(f);
			} else
				try {
					FileUtils.copyFile(f, new File(workingDirectory, templateFileName));
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
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
				} catch (Exception e) {
					String msg = "unable to read template file: " + f.getAbsolutePath() + "\n\t" + e;
					throw new RuntimeException(msg, e);
				}
				templateFiles[i] = new File(workingDirectory, templateFileName);
			} else {
				String msg = "unable to find template file: " + f.getAbsolutePath();
				throw new RuntimeException(msg);
			}
		}

		runCommands = new String[adamsCommandFiles.size()];
		File adamsCmdFile;
		for (int i = 0; i < adamsCommandFiles.size(); i++) {
			adamsCmdFile = (File) adamsCommandFiles.get(i);
			runCommands[i] = programName + " " + adamsCmdFile.getName() + " exit";
			Debug.trace(Debug.ALL, "runCommand"+i+" = " + runCommands[i]);
		}

		isOutputPostProcessed = ((DomeBoolean) pluginConfiguration.getSetupParameter(
		        AdamsConfiguration.IS_OUTPUT_POSTPROCESSED).getCurrentDataObject()).getValue();

		if(isOutputPostProcessed)  {
			postProcessCommand =  ((DomeString) pluginConfiguration.getSetupParameter(
			        AdamsConfiguration.OUTPUT_POSTPROCESSOR_COMMAND).getCurrentDataObject()).getValue();
		}

		outputBaseName = ((DomeString) pluginConfiguration.getSetupParameter(
		        AdamsConfiguration.OUTPUT_FILENAME).getCurrentDataObject()).getValue();

		if(outputBaseName.indexOf(COMMA) == -1) { //single o/p file
			String outputfile = workingDirectory + File.separator + outputBaseName.trim();
			Debug.trace(Debug.ALL, "output file path = " + outputfile);
			if(outputfile != "")  {
				outputFiles = new HashMap();
				outputFiles.put(outputBaseName.trim(), outputfile);
			}
		}
		else {
			//multiple o/p files
			useMultipleOutputs = true;
			String[] result = outputBaseName.split("\\,");
			for (int i = 0; i < result.length; i++)  {
				String filename = result[i].trim();
				if(filename != "") {
					String outputfile = workingDirectory + File.separator + filename;
					Debug.trace(Debug.ALL, "output file path = " + outputfile);
					if(outputFiles == null) {
						outputFiles = new HashMap();
					}
					outputFiles.put(filename, outputfile);
				}
			}
		}
		// create map of template variables to dome objects
		Iterator it = getModelObjects().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				Parameter p = (Parameter) o;
				Object map = getPluginMappingManager().getMappingObjectForParameter(p);
				if (map != null) {
					createAdamsLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
				}
			}
		}
	}

	protected void createAdamsLink(Parameter p, String refString, boolean isInput)
	{
		if (isInput) {
			inputParams.put(createTemplateReference(refString), p);
		}
		else {
			if((p.getCurrentDataObject() instanceof DomeFile)) {
				savedFiles.add(p);
			}
			else {
				if(!isOutputPostProcessed) {
					String filescope = null;
					refString.trim();
					int dotindex = refString.indexOf(DOT);
					if(dotindex != -1) {
						int exclamationindex = refString.indexOf(EXCLAMATION);
						filescope = refString.substring(0, exclamationindex);
						filescope.trim();
						if(filescope != null || filescope != "") {
							paramScopeMap.put(p, filescope);
						}
						refString = refString.substring(exclamationindex+1);
					}
					if ((p.getCurrentDataObject() instanceof DomeMatrix)) {
						if(refString.substring(0,8).equalsIgnoreCase(REQUEST1)) {
							refString = refString.substring(8);
						}
					}
					else {
						if (refString.substring(0, 8).equalsIgnoreCase(REQUEST1)) {
							refString = refString.substring(8);
						}
					}
				}
				outputParams.put(refString, p);
			}
		}
	}

	/**
	 * @param refString the string in the template with or without % signs around it
	 * @return the refString with % signs around it
	 */
	protected String createTemplateReference(String refString)
	{
		refString = refString.trim();
		if (!refString.startsWith("%"))
			refString = "%" + refString;
		if (!refString.endsWith("%"))
			refString = refString + "%";
		return refString;
	}

 }

