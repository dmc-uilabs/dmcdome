package mit.cadlab.dome3.plugin.nastran;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.StreamSink;
import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NastranModelRuntime extends PluginModelRuntime
{
	private static int lastRuntimeInstanceNumber = 0;
	public static final String OPENARRAY = "[";
	public static final String CLOSEDARRAY = "]";
	private String outputBaseName = "", runCommand = "";
	private File workingDirectory;
	private HashMap inputParams = new HashMap(), outputParams = new HashMap();
	private List savedFiles = new ArrayList();
	private NumberFormat numFormat;
	private String templateFileContents;
	private File templatizedInputFile, inputFile;

	public NastranModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(parentId, xml, isProjectResource);
		numFormat = NumberFormat.getNumberInstance();
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
		if(templateFileContents != null) {  //small files
			String newTemplateFileContents =  replaceInputs(templateFileContents);
			if (!FileUtils.writeStringToFile(newTemplateFileContents, inputFile)) {
				String msg = "Error writing template inputs to " + workingDirectory;
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
			return true;
		}
		else {  //large files
			try {
				RandomAccessFile templateRandomAccessFile = new RandomAccessFile(templatizedInputFile, "r");
				if (inputFile.exists())
					try {
						inputFile.delete();
					}
					catch (Exception e) {
						System.err.println("Error deleting previous version of " + inputFile.getAbsolutePath() + "\n\t" + e);
					}
				RandomAccessFile copyRandomAccessFile = new RandomAccessFile(inputFile, "rwd");
				byte[] content = new byte[1024];
				long filesize = templateRandomAccessFile.length();
				int remainder = (int)(filesize % 1024);
				//System.out.println("remainder = " + remainder);
				while(true) {
					int result = templateRandomAccessFile.read(content);
					String fileContents = null;
					if(filesize == remainder && result == -1) {
						//System.out.println("filesize = " + filesize);
						byte[] lastContent = new byte[(int)filesize];
						System.arraycopy(content, 0, lastContent, 0, (int)filesize);
						fileContents = new String(lastContent);
						String newTemplateFileContents = replaceInputs(fileContents);
						appendStringToFile(copyRandomAccessFile, newTemplateFileContents);
						break;
					}
					else if (filesize > remainder) {
						fileContents = new String(content);
						String newTemplateFileContents = replaceInputs(fileContents);
						appendStringToFile(copyRandomAccessFile, newTemplateFileContents);
						if (filesize >= 1024)
							filesize = filesize - 1024;
					}
				}
				try {
					templateRandomAccessFile.close();
				}
				catch (IOException e) {
				}
				try {
					copyRandomAccessFile.close();
				}
				catch (IOException e) {
				}
			}
			catch(IOException ioe) {
				String msg = "IOException in reading " + inputFile.getName();
				System.err.println(msg);
				System.err.println(ioe);
				throw new RuntimeException(msg, ioe);
			}
			return true;
		}
	}

	private void appendStringToFile(RandomAccessFile file, String text)
	{
		try {
			byte[] bytes = text.getBytes();
			file.write(bytes);
		} catch (Exception ex) {
			String msg = "file append error: " + inputFile.getAbsolutePath() + "\n  " + ex;
			System.err.println(msg);
			throw new RuntimeException(msg);
		}
	}

	private String replaceInputs(String contents) {
		String newTemplateFileContents = new String(contents);
		Iterator it = inputParams.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Parameter param = (Parameter) inputParams.get(key);
			DataObject dObj = param.getCurrentDataObject();
			String value = "";
			if (dObj instanceof DomeReal) {
				value = numFormat.format(((DomeReal) dObj).getRealValue());
			} else if (dObj instanceof DomeInteger)
				value = ((DomeInteger) dObj).getIntegerValue().toString();
			else if (dObj instanceof DomeString)
				value = ((DomeString) dObj).getValue();
			else if (dObj instanceof DomeVector) {
				List data = ((DomeVector) dObj).getData();
				StringBuffer sb = new StringBuffer();
				for (Iterator iterator = data.iterator(); iterator.hasNext();) {
					Object o = iterator.next();
					if (sb.length() > 0)
						sb.append(", ");
					if (o instanceof Double) {
						String numStr = numFormat.format(((Double) o).doubleValue());
						sb.append(numStr);
					} else if (o instanceof Integer) {
						sb.append(((Integer) o).intValue());
					}
				}
				value = sb.toString();
			} else if (dObj instanceof DomeFile) {
				String path = ((DomeFile) dObj).getFilePath();
				try {
					value = FileUtils.readTextFileAsString(path);
				} catch (FileNotFoundException e) {
					Debug.trace(Debug.ALL, "File " + ((DomeFile) dObj).getFilePath() + "not found");
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}
			//TODO take care of files as strings in enum
			else if (dObj instanceof DomeEnumeration) {
				int index = ((DomeEnumeration) dObj).getLastSelection();
				Object valObject = ((DomeEnumeration) dObj).getElementValue(index);
				if (valObject instanceof Integer || valObject instanceof Double ||
				        valObject instanceof String) {
					value = valObject.toString();
				} else if (valObject instanceof Boolean) {
					if (valObject.equals(Boolean.TRUE)) {
						value = "1";
					} else {
						value = "0";
					}
				}
			}

			String[] results = formatKeyValue(key, value);
			newTemplateFileContents = newTemplateFileContents.replaceAll(results[0], results[1]);
		}
		return newTemplateFileContents;
	}

	private String[] formatKeyValue(String key, String value) {
		if (value.length() > 7) { // beyond the space allowd in a column (7 max)
			boolean hasE = false;
			for (int i = 0; i < value.length(); i++) {
				if (value.charAt(i) == 'E')
					hasE = true;
			}
			//todo
		} else {
			if (key.length() > value.length()) {
				int diff = key.length() - value.length();
				for (int i = 0; i < diff; i++) {
					value += " ";
				}
			} else if (key.length() < value.length()) {
				int diff = value.length() - key.length();
				for (int i = 0; i < diff; i++) {
					key += " ";
				}
			}
		}
		String[] results = new String[2];
		results[0] = key;
		results[1] = value;
		return results;
	}

	protected boolean runModel()
	{
		try {
			//clean up the previous o/p files from the working dir
			File[] files = workingDirectory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if(!files[i].equals(inputFile)) {
					files[i].delete();
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		// todo: once models work properly in NASTRAN, check again for return value
//		if (StreamSink.runCommand(runCommand, workingDirectory, "NASTRAN") != 0) {
//			String msg = "error executing " + runCommand;
//			throw new ModelExecutionException(msg);
		int returnValue = StreamSink.runCommand(runCommand, workingDirectory, "NASTRAN");
		if (returnValue != 0) {
			System.err.println("Warning: " + runCommand + " exited with value " + returnValue);
		}
		return true;
	}

	public boolean readOutputParameters(List affectedOutputParams)
	{
		if (isWaitingToDie())
			return false; // don't set outputs if waiting to die
		if (outputBaseName == null) {
			return false;
		}
		else {
			RandomAccessFile outputFile = null;
			try {
				outputFile = new RandomAccessFile(outputBaseName, "r");
				if(outputFile.length() > 8192) { //if length > 8192 (8K) file too big to be read as a String
					handleLargeOutputFile(outputFile, affectedOutputParams);
				}
				else {
                	String output = FileUtils.readTextFileAsString(outputBaseName);
					readOutputsFromstring(output, affectedOutputParams);
				}
			}
			catch(FileNotFoundException fe) {
				String msg = "File " + outputBaseName + " not found." + fe.getMessage();
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
			catch(IOException ioe) {
				String msg = "IO exception in " + outputBaseName + ioe.getMessage();
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
		}
		//if file is an output param fire a file change event
		for (Iterator iterator = savedFiles.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			if (affectedOutputParams.contains(p)) {
				FileData domeFile = (FileData) p.getCurrentDataObject();
				domeFile.notifyFileChanged();
			}
		}
		return true;
	}

	private void handleLargeOutputFile(RandomAccessFile outputFile,
	                                   List affectedParams) {

		byte[] byteArr = new byte[1024];
		int result = 0;
		while(result != -1 && !isWaitingToDie()) {
			try {
				result = outputFile.read(byteArr);
				String output = new String(byteArr);
				readOutputsFromstring(output, affectedParams);
			}
			catch (IOException ioe) {
				String msg = "IO exception in " + outputBaseName +ioe.getMessage();
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
		}
		try {
			outputFile.close();
		}
		catch (IOException ioe1) {
			String msg = "IO exception in closing " + outputBaseName + ioe1.getMessage();
			System.err.println(msg);
			throw new RuntimeException(msg);
		}
	}

	private void readOutputsFromstring(String str, List affectedParams)
	{
		ArrayList list = new ArrayList();
		String[] tResult = str.split("\\n");
		for (int i = 0; i < tResult.length; i++) {
			if (tResult[i].equals("") || tResult[i].equals(" ") || tResult[i].equals("\n")) {
				continue;
			} else {
				String[] tSubResult = tResult[i].split("\\s");
				for (int j = 0; j < tSubResult.length; j++) {
					if (!tSubResult[j].equals("")) {
						list.add(tSubResult[j]);
					}
				}
			}
		}
		for (Iterator iterator = outputParams.keySet().iterator(); iterator.hasNext();) {
			if (isWaitingToDie())
				return; // don't set outputs if waiting to die

			String key = (String)iterator.next();
			Parameter p = (Parameter) outputParams.get(key);
			if(affectedParams.contains(p)) {
				DataObject dobj = p.getCurrentDataObject();
				if (dobj instanceof DomeReal || dobj instanceof DomeInteger) {
					int begindex = key.indexOf(OPENARRAY);
					String id = key.substring(0, begindex);
					int idindex = list.indexOf(id);
					if (idindex != -1) {
						int endindex = key.indexOf(CLOSEDARRAY);
						String valueindexstr = key.substring(begindex + 1, endindex);
						int valueindex = new Integer(valueindexstr).intValue();
						if(valueindex <= 2) {
							valueindex = valueindex + idindex + 2;
						}
						else if (valueindex > 2) {
							valueindex = valueindex + idindex + 4;
						}
						String value = (String)list.get(valueindex);
						if(dobj instanceof DomeReal) {
							((DomeReal)dobj).setValue(new Double(value).doubleValue());
						}
						else {
							((DomeInteger) dobj).setValue(new Integer(value).intValue());
						}
						affectedParams.remove(p);
					}
				}
			}
		}

	}

	protected void loadModelDefinition()
	{
		// get configuration parameters
		String programName = ((DomeString) pluginConfiguration.getSetupParameter(
		        NastranConfiguration.SOFTWARE_LOCATION).getCurrentDataObject()).getValue();
		Debug.trace(Debug.ALL, "path to Nastran executable = " + programName);

		String originalInputFileName = getMainModelFileName();

		File originalDirectory = new File(originalInputFileName.substring(0, originalInputFileName.lastIndexOf(File.separator)));
		System.out.println("originalDir=" + originalDirectory.getAbsolutePath());

		workingDirectory = new File(originalDirectory.getAbsolutePath(), "Run_" + getNextRuntimeInstanceNumber());
		Debug.trace(Debug.ALL, "workingDir = " + workingDirectory.getAbsolutePath());
		try {
			if (!workingDirectory.exists() && !workingDirectory.mkdir()) {
				String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath();
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
		} catch (Exception e) {
			String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath() + "\n\t" + e;
			System.err.println(msg);
			throw new RuntimeException(msg);
		}

		// cache template files
		templatizedInputFile = new File(originalInputFileName);
		if (templatizedInputFile.exists() && templatizedInputFile.isFile()) {
			String	templateFileName = templatizedInputFile.getName();
			try {
				RandomAccessFile rf = new RandomAccessFile(originalInputFileName, "r");
				long length = rf.length();
				if(length < 8192) { //if length > 8192 (8K) file too big to be read as a String
					templateFileContents = FileUtils.readTextFileAsString(templatizedInputFile);
				}
				rf.close();
			} catch (Exception e) {
				String msg = "unable to read template file: " + templatizedInputFile.getAbsolutePath() + "\n\t" + e;
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
			this.inputFile = new File(workingDirectory, templateFileName);
		} else {
			String msg = "unable to find template file: " + templatizedInputFile.getAbsolutePath();
			System.err.println(msg);
			throw new RuntimeException(msg);
		}

		String inputFileName = originalInputFileName.substring(originalInputFileName.lastIndexOf(File.separator) + 1, originalInputFileName.length());

		runCommand = programName + " " + inputFileName;
		Debug.trace(Debug.ALL, "runCommand = " + runCommand);

		outputBaseName = ((DomeString) pluginConfiguration.getSetupParameter(
		        NastranConfiguration.OUTPUT_FILENAME).getCurrentDataObject()).getValue();

		outputBaseName = workingDirectory + File.separator + outputBaseName.trim();
		Debug.trace(Debug.ALL, "output file path = " + outputBaseName);

		// create map of template variables to dome objects
		Iterator it = getModelObjects().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				Parameter p = (Parameter) o;
				Object map = getPluginMappingManager().getMappingObjectForParameter(p);
				if (map != null) {
					createNastranLink(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));
				}
			}
		}
	}

	protected void createNastranLink(Parameter p, String refString, boolean isInput)
	{
		if (isInput) {
			inputParams.put(createTemplateReference(refString), p);
		} else {
			if ((p.getCurrentDataObject() instanceof DomeFile)) {
				DomeFile output = (DomeFile) p.getCurrentDataObject();
				String name = output.getFilePath();
				output.setFilePath(workingDirectory + File.separator + name);
				savedFiles.add(p);
			}
			else {
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


	private static synchronized int getNextRuntimeInstanceNumber()
	{
		return ++lastRuntimeInstanceNumber;
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

}



