package mit.cadlab.dome3.plugin.batch.nameValue;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.exceptions.ModelExecutionException;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.batch.AbstractBatchModelRuntime;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.Regex;
import mit.cadlab.dome3.util.StreamSink;
import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * This NameValue Model currently wraps input files which will be written with values
 * from the Dome Model (supports Integers, Reals, Strings). The input file is not parsed,
 * only written (the parsing should be done later). The executable is executed and the
 * output file is read and values are placed into mapped Dome parameters.
 *
 * Setup parameters:
 * inputfile: file name only
 * outputfile: file name only
 * They will be read/written to the working directory which is determined from the
 * "getMainModelFile" method of the auxiliary file stuff.
 * The executable will also execute itself with that working directory.
 * Note: files to be transferred during deploy should all be in the auxiliary files list.
 */
public class NameValueModelRuntime extends AbstractBatchModelRuntime
{
	private String inputFileName="", outputFileName=""; // names without paths
	private String exeFileName="", commandLineArgs = "";
	private String runCommand;
	//private String nameValueSeparator = "", commentIdentifier = "";
	private HashMap inputParams = new HashMap(), outputParams = new HashMap();
	private List outputFiles = new ArrayList();

    public NameValueModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
	    loadModelDefinition();
    }

	protected boolean writeInputParameters() {
		String separator = " = ";
		StringBuffer sb = new StringBuffer("");
		Iterator it = inputParams.keySet().iterator();
		while (it.hasNext()) {
			String varName = (String) it.next();
			sb.append(varName);
			sb.append(separator);
			DataObject dObj = ((Parameter) inputParams.get(varName)).getCurrentDataObject();
            if (dObj instanceof DomeEnumeration) {
                sb.append(((DomeEnumeration)dObj).getElementValue(((DomeEnumeration)dObj).getLastSelection()).toString());
            } else if (dObj instanceof DomeString) {
				sb.append(((DomeString)dObj).getValue());
			} else if (dObj instanceof NumericQuantity) {
				sb.append(((NumericQuantity)dObj).getQuantity().getMagnitude());
            } else if (dObj instanceof DomeBoolean) {
				sb.append(((DomeBoolean)dObj).getBooleanValue().toString());
			} else if (dObj instanceof FileData) {
				sb.append(((FileData)dObj).getFilePath());
            } else if (dObj instanceof DomeVector) {
				sb.append(vectorToString((DomeVector)dObj));
            } else if (dObj instanceof DomeMatrix) {
				sb.append(matrixToString((DomeMatrix)dObj));
			} else {
				String msg = "unknown data type: " + dObj;
				throw new RuntimeException(msg);
			}
			sb.append("\n");
		}
		FileUtils.writeStringToFile(sb.toString(),new File(workingDirectory,inputFileName));
		return true;
	}

    private String vectorToString(DomeVector v) {
        StringBuffer sb = new StringBuffer("[");
        List data = v.getData();
        if (v.isRowVector()) {
            Number n;
            for (int i = 0; i < data.size(); i++) {
                if (i > 0)
                    sb.append(" ");
                n = (Number) data.get(i);
                sb.append(n.toString());
            }
        } else {
            Number n;
            for (int i = 0; i < data.size(); i++) {
                if (i > 0)
                    sb.append("; ");
                n = (Number) data.get(i);
                sb.append(n.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String matrixToString(DomeMatrix v) {
        StringBuffer sb = new StringBuffer("[");
        List data = v.getData();
        List row;
        Number n;
        for (int i = 0; i < data.size(); i++) {
            if (i > 0)
                sb.append(";\n");
            row = (List) data.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (j > 0)
                    sb.append(" ");
                n = (Number) row.get(j);
                sb.append(n.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }

	protected boolean runModel() {
		if (StreamSink.runCommand(runCommand, workingDirectory, "NAME-VALUE") != 0) {
			String msg = "error executing " + runCommand;
			throw new ModelExecutionException(msg);
		}
		return true;
	}

	protected boolean readOutputParameters(List affectedOutputParams) {
		try {
			List lines = FileUtils.readTextFileAsList(new File(workingDirectory,outputFileName));
			String line, varName, value;
			List parts;
			for (int i = 0; i < lines.size(); i++) {
				line = ((String) lines.get(i)).trim();
				if (line.equals("") || line.startsWith(NameValueConfiguration.OUTPUT_COMMENT_CHARACTER))
					continue; // skip these lines
				parts = Regex.split("=",line,2);
				varName = ((String)parts.get(0)).trim();
				if (parts.size() != 2) {
					System.err.println("invalid output line format: "+line);
					continue;
				}
				value = ((String)parts.get(1)).trim();
				Parameter p = (Parameter) outputParams.get(varName);
				if (affectedOutputParams.contains(p)) {
					DataObject dObj = p.getCurrentDataObject();
					if (dObj == null)
						continue;
					try {
						if (dObj instanceof DomeString) {
							((DomeString) dObj).setValue(value);
						}
						else if (dObj instanceof DomeInteger) {
							((DomeInteger) dObj).setValue((int)Double.parseDouble(value));
						}
						else if (dObj instanceof DomeReal) {
							((DomeReal) dObj).setValue(Double.parseDouble(value));
						}
                        else if (dObj instanceof DomeBoolean) {
							((DomeBoolean) dObj).setBooleanValue(Boolean.valueOf(value));
						}
                        else if (dObj instanceof DomeEnumeration) {
                            ((DomeEnumeration) dObj).setLastSelectionToValue(value);
                        }
                        else if (dObj instanceof DomeVector) {
                            i = readVectorOutput((DomeVector)dObj, value, i, lines);
                        }
                        else if (dObj instanceof DomeMatrix) {
                            i = readMatrixOutput((DomeMatrix)dObj, value, i, lines);
                        }
						else {
							String msg = "unknown data type: " + dObj;
							throw new RuntimeException(msg);
						}
					}
					catch (NumberFormatException e) {
						String msg = "unable to parse numeric value for " + varName + ": " + value;
						throw new RuntimeException(msg, e);
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		for (int i = 0; i < outputFiles.size(); i++) {
			Parameter p = (Parameter) outputFiles.get(i);
			if (affectedOutputParams.contains(p))
				((FileData) p.getCurrentDataObject()).notifyFileChanged();
		}
		return true;
	}

    private int readVectorOutput(DomeVector v, String startData, int currentLine, List moreLines) {
        StringBuffer sb = new StringBuffer(startData);
        String line;
        while (sb.charAt(sb.length()-1) != ']') {
            currentLine = currentLine+1;
            if (currentLine >= moreLines.size()) {
                System.err.println("error finding end of vector data");
                break;
            }
            line = ((String) moreLines.get(currentLine)).trim();
            if (line.equals("") || line.startsWith(NameValueConfiguration.OUTPUT_COMMENT_CHARACTER))
			    continue; // skip these lines
            sb.append(" ");
            sb.append(line);
        }
        String data = sb.toString();
        int start = data.startsWith("[") ? 1 : 0;
        int end = data.endsWith("]") ? data.length() - 1 : data.length();
        data = data.substring(start, end);
        List numbers = Regex.split(";", data);
        if (numbers.size() > 1) {
            if (v.isRowVector())
                v.setRowVector(false);
        } else if (numbers.size() == 1) {
            numbers = Regex.split(" ", data);
            if (numbers.size() > 1) {
                if (!v.isRowVector())
                    v.setRowVector(true);
            }
        }
        String s;
        double[] n = new double[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            s = (String) numbers.get(i);
            n[i] = Double.parseDouble(s);
        }
        ((DomeVectorData)v).setValues(n);
        return currentLine;
    }

    private int readMatrixOutput(DomeMatrix m, String startData, int currentLine, List moreLines) {
        StringBuffer sb = new StringBuffer(startData);
        String line;
        while (sb.charAt(sb.length()-1) != ']') {
            currentLine = currentLine+1;
            if (currentLine >= moreLines.size()) {
                System.err.println("error finding end of matrix data");
                break;
            }
            line = ((String) moreLines.get(currentLine)).trim();
            if (line.equals("") || line.startsWith(NameValueConfiguration.OUTPUT_COMMENT_CHARACTER))
			    continue; // skip these lines
            sb.append(" ");
            sb.append(line);
        }
        String data = sb.toString();
        int start = data.startsWith("[") ? 1 : 0;
        int end = data.endsWith("]") ? data.length() - 1 : data.length();
        data = data.substring(start, end);
        List rows = Regex.split(";", data);
        int r = rows.size();
        int c = 0;
        if (r > 0) {
            List firstRowCols = Regex.split(" ", ((String)rows.get(0)).trim());
            c = firstRowCols.size();
        }
        double[][] n = new double[r][c];
        String rs, item;
        List cols;
        for (int i = 0; i < rows.size(); i++) {
            rs = ((String) rows.get(i)).trim();
            cols = Regex.split(" ", rs);
            for (int j = 0; j < cols.size(); j++) {
                item = (String) cols.get(j);
                n[i][j] = Double.parseDouble(item);
            }
        }
        ((DomeMatrixData)m).setValues(n);
        return currentLine;
    }

    protected void loadModelDefinition() {
        // get configuration parameters
	    inputFileName = ((DomeFile) pluginConfiguration.getSetupParameter(NameValueConfiguration.INPUT_FILE_LOCATION).getCurrentDataObject()).getFilePath();
	    outputFileName = ((DomeFile) pluginConfiguration.getSetupParameter(NameValueConfiguration.OUTPUT_FILE_LOCATION).getCurrentDataObject()).getFilePath();
	    exeFileName = ((DomeFile) pluginConfiguration.getSetupParameter(NameValueConfiguration.EXECUTABLE_LOCATION).getCurrentDataObject()).getFilePath();
	    commandLineArgs = ((DomeString) pluginConfiguration.getSetupParameter(NameValueConfiguration.COMMAND_LINE_ARGUMENTS).getCurrentDataObject()).getValue();
	    //nameValueSeparator = ((DomeString) pluginConfiguration.getSetupParameter(NameValueConfiguration.NAME_VALUE_SEPARATOR).getCurrentDataObject()).getValue();
	    //commentIdentifier = ((DomeString) pluginConfiguration.getSetupParameter(NameValueConfiguration.COMMENT_IDENTIFIER).getCurrentDataObject()).getValue();

		// determine runCommand
	    runCommand = exeFileName + " " + commandLineArgs;

	    // copy auxiliary files to working directory
	    int numAuxFiles = AuxFiles.size();
	    File f;
	    String templateFileName;
	    for (int i = 0; i < numAuxFiles; i++) {
		    f = new File(this.getAuxFilePathName(i));
		    templateFileName = f.getName();
		    try {
			    FileUtils.copyFile(f, new File(workingDirectory, templateFileName));
		    } catch (Exception e) {
			    throw new RuntimeException(e.getMessage(), e);
		    }
	    }

        // create map of input/output variables to dome objects
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
	            boolean isInput = getCausality(p).equals(CausalityStatus.INDEPENDENT);
                if (map != null) {
                    createNameValueLink(p, (String) map, isInput);
                }
	            if (p.getCurrentDataObject() instanceof DomeFile) {
		            FileData d = (FileData) p.getCurrentDataObject();
		            // set location of file to be in working directory
		            File dFile = new File(d.getFilePath());
		            File newF = new File(workingDirectory, dFile.getName());
		            d.setFilePath(newF.getAbsolutePath());
		            if (!isInput) { // output file
			            outputFiles.add(p);
		            }
	            }
            }
        }
    }

    protected void createNameValueLink (Parameter p, String refString, boolean isInput)
    {
	    if (isInput) {
		    inputParams.put(refString,p);
	    } else {
		    outputParams.put(refString, p);
		    if (p.getCurrentDataObject() instanceof DomeFile)
			    inputParams.put(refString, p); // output file names may be used as inputs
	    }
    }

}
