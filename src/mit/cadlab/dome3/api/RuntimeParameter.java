package mit.cadlab.dome3.api;

import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationItem;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime;
import mit.cadlab.dome3.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * RuntimeParameter provides a restricted access to properties of InterfaceParameterClient instance.
 * RuntimeParameter is worth defining because it helps DOME API be simple, easier and stay in dome3.api package
 * RuntimeParameter mirrors the properties of InterfaceParameterClient,
 * so this class does not carry actual data about paramId, paramName, dataType, etc.
 * It retrieve paramId, paramName, etc. from an InterfaceParameterClient instance.
 * When the value of parameter changes, the change will be forwarded to InterfaceParameterClient and RuntimeInterface will
 * keep the track of all parameteric changes in the HashMap member variable called 'changedParameters'.
 * User: Sangmok Han
 * Date: 2005. 1. 18.
 */
public class RuntimeParameter {
    public static final String INDEPENDENT = "INDEPENDENT";
    public static final String INDETERMINATE = "INDETERMINATE";
    public static final String INTERMEDIATE = "INTERMEDIATE";
    public static final String RESULT = "RESULT";

    private String causalityStatus;
    private RuntimeInterface runtimeInterface;
    private InterfaceParameterClient interfaceParamClient;

    public RuntimeParameter(String causalityStatus, RuntimeInterface runtimeInterface, InterfaceParameterClient interfaceParamClient) {
        this.causalityStatus = causalityStatus;
        this.interfaceParamClient = interfaceParamClient;
        this.runtimeInterface = runtimeInterface;
    }

    public String getDataType() {
        return interfaceParamClient.getCurrentType();
    }

    /**
     * This method returns the parameter value that is directly obtained by calling 'interfaceParamClient.getCurrentDataObject().getValues()'
     * The structure and java data type of returned List object varies depending on dome data type of the returned object.
     * (ex) for Dome Integer - List containing Integer as the first element.
     * (ex) for Dome Matrix - Vector containing Vector for each row
     * So it is recommended to use other getXXXXValue() methods that have explicit return data types.
     * (ex) getIntegerValue(), getRealValue(), getMatrixValue(), getEnumerationValue(), getStringValue(),
     *      getBooleanValue(),
     */
    private List getValue() {
        return interfaceParamClient.getCurrentDataObject().getValues();
    }

    public String getCausalityStatus() {
        return causalityStatus;
    }

    public String getParamId() {
        return interfaceParamClient.getId().getIdString();
    }

    public String getParamName() {
        return interfaceParamClient.getName();
    }

    public String getValueStatus() {
        return interfaceParamClient.getValueStatus();
    }


    /**
     * change the parameter value if this parameter is INDEPENDENT.
     * newValue accepts datatypes such as Integer, Double, Real, String, List(=Vector), Matrix(?), Preference(?)
     * @param newValue
     */
    public void setValue(Object newValue) throws RuntimeException {
        if (! INDEPENDENT.equals(causalityStatus)) {
            throw new RuntimeException("Only INDEPENDENT parameters are allow to change");
        }
        Debug.trace(Debug.ALL, "[DOME API] setting parameter \"" + this.getParamName() + "\" of \"" + runtimeInterface.getDomeInterface().getParentName() +"\": new value = " + newValue);
        interfaceParamClient.setValues(Vectors.create(newValue));
        runtimeInterface.addChangedParameter(this, newValue);
    }

    /**
     * returns string description of this folder
     */
    public String toString() {
        return "[DOME PARAMETER: '" + getParamName() + "' (ID = " + getParamId() + "), type = " + getDataType() + ", value = " + getValue() + ", causualty status = " + getCausalityStatus() + ", value status = " + getValueStatus() + "]";
    }

    /**
     * this method is called by addParameterValueChangeListener() in RuntimeInterface
     * which iterates through whole parameters and calls addParameterValueChangeListener() in each parameter.
     * that is why it has protected access.
     * @param valueChangeListener
     */
    protected void addParameterValueChangeListener(ParameterValueChangeListener valueChangeListener) {
        interfaceParamClient.addParameterValueChangeListener(valueChangeListener);
    }

    /**
     * this method is called by addParameterStatusChangeListener() in RuntimeInterface
     * which iterates through whole parameters and calls addParameterStatusChangeListener() in each parameter.
     * that is why it has protected access.
     * @param statusChangeListener
     */
    public void addParameterStatusChangeListener(ParameterStatusChangeListener statusChangeListener) {
        interfaceParamClient.addParameterStatusChangeListener(statusChangeListener);
    }

    /**
     * this method has protected access and is supposed to be called only by removeParameterValueChangeListener() in RuntimeInterface
     */
    protected void removeParameterValueChangeListener(ParameterValueChangeListener valueChangeListener) {
        interfaceParamClient.removeParameterValueChangeListener(valueChangeListener);
    }

    /**
     * this method has protected access and is supposed to be called only by removeParameterStatusChangeListener() in RuntimeInterface
     */
    public void removeParameterStatusChangeListener(ParameterStatusChangeListener statusChangeListener) {
        interfaceParamClient.removeParameterStatusChangeListener(statusChangeListener);
    }

    /**
     * this method has protected access and is supposed to be called only by disableParameterValueChangeListener() in RuntimeInterface
     */
    protected void clearParameterValueChangeListener() {
        interfaceParamClient.clearParameterValueChangeListener();
    }

    /**
     * this method has protected access and is supposed to be called only by disableParameterStatusChangeListener() in RuntimeInterface
     */
    public void clearParameterStatusChangeListener() {
        interfaceParamClient.clearParameterStatusChangeListener();
    }

    /**
     * This method sets the value of a matrix-type parameter.
     * A matrix is represented as a vector of rows. Each row is also represented as a vector of numbers.
     * The number can be either Integer, Long, Real or Double.
     *
     * (example) setting 2x3 matrix
     *
     *       | 1 2 3 |
     *       | 4 5 6 |
     *
     * Vector matrixData = new Vector();
     * Vector row1 = new Vector();
     * row1.add(new Integer(1)); row1.add(new Integer(2)); row1.add(new Integer(3));
     * matrixData.add(row1);
     * Vector row2 = new Vector();
     * row2.add(new Integer(4)); row2.add(new Integer(5)); row2.add(new Integer(6));
     * matrixData.add(row2);
     * matrixParam.setMatrixValue(matrixData);
     */
    public void setMatrixValue(Vector matrixData) {
        Hashtable table = new Hashtable();
        table.put(DomeMatrix.DATA, matrixData);
        //table.put(DomeMatrix.UNIT, Quantity.NO_UNIT_STR);
        table.put(DomeMatrix.UNIT, interfaceParamClient.getCurrentDataObject().getUnit().toString());
        this.setValue(table);
    }

    /**
     * This method returns the value of a matrix-type parameter.
     * A matrix is represented as a vector of rows. Each row is also represented as a vector of numbers.
     * The number can be either Integer, Long, Real or Double.
     */
    public Vector getMatrixValue() {
        if (this.getValue() instanceof Vector) {
            return (Vector) this.getValue();
        } else {
            /* for such case when actual value is Integer or Real */
            return new Vector(this.getValue());
        }
    }

    public void setIntegerValue(int intData) {
        this.setValue(new Integer(intData));
    }

    public int getIntegerValue() {
        return ((Integer) this.getValue().get(0)).intValue();
    }

    public void setRealValue(double realData) {
        this.setValue(new Double(realData));
    }

    public double getRealValue() {
        return ((Double) this.getValue().get(0)).doubleValue();
    }

    public void setBooleanValue(boolean booleanData) {
        this.setValue(new Boolean(booleanData));
    }

    public boolean getBooleanValue() {
        // System.out.println(this.getValue().get(0).getClass().getName());
        return ((Boolean) this.getValue().get(0)).booleanValue();
    }

    public void setStringValue(String stringData) {
        this.setValue(new String(stringData));
    }

    public String getStringValue() {
        return (String) this.getValue().get(0);
    }

    /** use the given text string as contents of the file parameter */
    public void setFileValue(String textFileData) {
        this.setValue(textFileData);
    }

    /** use the given byte array as contents of the file parameter */
    public void setFileValue(byte[] byteArrayFileData) {
        this.setValue(byteArrayFileData);
    }

    /** use the given File instance as contents of the file parameter */
    public void setFileValue(File file) {
        byte[] byteArrayFileData = null;

        if (file == null) {
            throw new RuntimeException("The file argument cannot be null");
        }

        try {
            byteArrayFileData = FileUtils.readBinaryFileAsByteArray(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Cannot read the given file: " + file.getAbsolutePath());
        }

        setFileValue(byteArrayFileData);
    }

    /** returned object is either String or byte[]. use instanceof or getFileType() to determine the data type of the returned object */
    public Object getFileValue() {
        FileData fileData = null;
        try {
            fileData = (FileData) interfaceParamClient.getCurrentDataObject();
        } catch (ClassCastException e) {
            throw new RuntimeException("getFileValue() should be invoked only when current data object is FileData. current data object is " + interfaceParamClient.getCurrentDataObject().getClass().getName());
        }

        if (! RESULT.equals(causalityStatus)) {
            /* for non-RESULT file data, we need to retrieve the file data from DOME server */
            /* RuntimeFunctionsClient.getFileContent() does that */
            ParameterRuntime p = interfaceParamClient;
            ModelObjectScope scope = p.getScope();
            ServerConnection con = ((ModelInterfaceRuntimeClient) scope).getServerConnection();
            CompoundId cId = ((ModelInterfaceRuntimeClient) scope).getRuntimeId();
            cId.setDataObjectStaticId(p.getId());

            Object fileContent = null;
            try {
                fileContent = RuntimeFunctionsClient.getFileContent(con, cId);
            } catch (RuntimeException e) {
                Debug.trace(Debug.ALL, "[DOME API] Failed to get file contents: " + e.getMessage());
                e.printStackTrace();
            }

            fileData.setFileValue(null, fileContent);
        } else {
            /* we need wait until file data of this result parameter are downloaded and returned */
            File fileToBeDownloaded = new File(fileData.getFilePath());
            Debug.trace(Debug.ALL, "[DOME API] checking the download status of file data: path=" + fileToBeDownloaded.getPath() + ", is downloaded=" + fileToBeDownloaded.exists());
            FileDataDownloadingTracker downloadingTracker = new FileDataDownloadingTracker(fileToBeDownloaded);
            downloadingTracker.start();

            try {
                downloadingTracker.join();
            } catch (InterruptedException e) { System.err.println(e); }

            /* for RESULT file data which is null because it is before execution, we need to retrieve the file data from DOME server */
//            if (this.getValue().get(0) == null) {
//                ParameterRuntime p = interfaceParamClient;
//                ModelObjectScope scope = p.getScope();
//                ServerConnection con = ((ModelInterfaceRuntimeClient) scope).getServerConnection();
//                CompoundId cId = ((ModelInterfaceRuntimeClient) scope).getRuntimeId();
//                cId.setDataObjectStaticId(p.getId());
//                System.out.println("check poing 2-1: " + cId);
//
//                Object fileContent = null;
//                try {
//                    fileContent = RuntimeFunctionsClient.getFileContent(con, cId);
//                } catch (RuntimeException e) {
//                    System.out.println("Failed to get file contents: " + e.getMessage());
//                }
//
//                System.out.println("check poing 2-2: " + fileContent);
//                fileData.setFileValue(null, fileContent);
//                System.out.println("check poing 2-3: " + fileData);
//            }
        }

        return this.getValue().get(0); // same as fileData.getValues().get(0)
    }

    /** get the file type of FileData. this method should be used only when the current data object is FileData. otherwise, a runtime exception is thrown. */
    public String getFileType() {
        try {
            return ((FileData) interfaceParamClient.getCurrentDataObject()).getFileType();
        } catch (ClassCastException e) {
            throw new RuntimeException("getFileType() should be invoked only when current data object is FileData. current data object is " + interfaceParamClient.getCurrentDataObject().getClass().getName());
        }
    }

    /** get the file path (ex. c:\myfolder\mysubfolder) of FileData. this method should be used only when the current data object is FileData. otherwise, a runtime exception is thrown. */
    public String getFilePath() {
        try {
            return ((FileData) interfaceParamClient.getCurrentDataObject()).getFilePath();
        } catch (ClassCastException e) {
            throw new RuntimeException("getFileType() should be invoked only when current data object is FileData. current data object is " + interfaceParamClient.getCurrentDataObject().getClass().getName());
        }
    }

    /** get the file name of FileData. this method should be used only when the current data object is FileData. otherwise, a runtime exception is thrown. */
    public String getFileName() {
        try {
            return new File(((FileData) interfaceParamClient.getCurrentDataObject()).getFilePath()).getName();
        } catch (ClassCastException e) {
            throw new RuntimeException("getFileType() should be invoked only when current data object is FileData. current data object is " + interfaceParamClient.getCurrentDataObject().getClass().getName());
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setVectorValue(Vector vectorData) {
        Hashtable table = new Hashtable();
        table.put(DomeVector.DATA, vectorData);
        //table.put(DomeMatrix.UNIT, Quantity.NO_UNIT_STR);
        table.put(DomeVector.UNIT, interfaceParamClient.getCurrentDataObject().getUnit().toString());
        this.setValue(table);
    }

    public Vector getVectorValue() {
        return (Vector) this.getValue();
    }

    /**
     * This method returns the List object that is directly obtained by calling 'interfaceParamClient.getCurrentDataObject().getValues()'.
     * The way the List object contains actual data varys depending on data types.
     * So it is convenient and recommended to use other data type-specific methods such as getIntegerValue(), getRealValue(), getStringValue(), getVectorMatrixValue(), getMatrixValue();
     */
    public List getRawValue() {
        return this.getValue();
    }

    /** set enumeration selection by index */
    public void setEnumerationValue(int enumerationIndex) {
        this.setValue(new Integer(enumerationIndex));
    }

    /** set enumeration selection by the name of the enumrated item */
    public void setEnumerationValue(String enumerationName) {
        int enumerationIndex = -1;
        List enumList = getEnumerationList();
        for (int i = 0; i < enumList.size(); i++) {
            Object[] enm = (Object[]) enumList.get(i);
            String enumName = (String) enm[0];
            if (enumName.equals(enumerationName)) {
                enumerationIndex = i;
            }
        }
        this.setValue(new Integer(enumerationIndex));
    }

    /**
     * the value of Enumeration-type parameter consists of three kinds of information
     * 1) index of current selection
     * 2) the list of enumeration keys
     * 3) the list of enumeration values
     * Those three kinds of information is returned in an Object array as follows:
     * Object[0] = Integer, enumeration selection index
     * Object[1] = String[], enumeration key string array
     * Object[2] = Object[], enumeration value object array
     *
     * (example)
     * Object[] enumData = enumParam.getEnumerationValue();
     * int enumIndex = ((Integer) enumData [0]).intValue();
     * String[] enumNames = (String[]) enumData [1];
     * Object[] enumValues = (Object[]) enumData [2];
     * System.out.println("MyEnumeration = " + enumNames[enumIndex] + "(" + enumValues[enumIndex] + ")");
     */
    public Object[] getEnumerationValue() {
        Object[] ret = new Object[3];
        int lastSelectionIndex = ((EnumerationData) interfaceParamClient.getCurrentDataObject()).getLastSelection();
        ret[0] = new Integer(lastSelectionIndex);

        Vector enumVector = (Vector) this.getValue();
        String[] enumNames = new String[enumVector.size()];
        Object[] enumValues= new Object[enumVector.size()];
        for (int i = 0; i < enumVector.size(); i++) {
            EnumerationItem aEnumItem = (EnumerationItem) enumVector.get(i);
            enumNames[i] = aEnumItem.getName();
            enumValues[i] = aEnumItem.getValue();
        }
        ret[1] = enumNames;
        ret[2] = enumValues;
        return ret;
    }

    /** returns a List of Object[] of { String enumName, Integer or Double enumValue, Boolean isSelected } */
    public List getEnumerationList() {
        List ret = new ArrayList();
        int lastSelectionIndex = ((EnumerationData) interfaceParamClient.getCurrentDataObject()).getLastSelection();

        Vector enumVector = (Vector) this.getValue();
        for (int i = 0; i < enumVector.size(); i++) {
            EnumerationItem aEnumItem = (EnumerationItem) enumVector.get(i);
            ret.add(new Object[] { aEnumItem.getName(), aEnumItem.getValue(), new Boolean(i == lastSelectionIndex) });
        }
        return ret;
    }

    /**
     * proper form of the argument for Real parameter: String
     * proper form of the argument for Integer parameter: String
     * proper form of the argument for String parameter: String
     * proper form of the argument for Boolean parameter: String
     * proper form of the argument for Vector parameter: Vector
     * proper form of the argument for Matrix parameter: Vector of Vectors
     * proper form of the argument for Enumeration parameter: index String (ex. if we have 'key-value' pairs of 'Ton-Thai, Kim-Korea, Qing-China' and the second one is selected, index String is "1": index starting from zero)
     * @param value
     */
    public void setValueAuto(Object value) {
        String dataType = getDataType();
        if ("Real".equals(dataType))
            setRealValue(Double.parseDouble((String)value));
        else if ("Integer".equals(dataType))
            setIntegerValue(Integer.parseInt((String) value));
        else if ("String".equals(dataType))
            setStringValue((String) value);
        else if ("Vector".equals(dataType))
            setVectorValue((Vector) value);
        else if ("Matrix".equals(dataType))
            setMatrixValue((Vector) value);
        else if ("Enumeration".equals(dataType))
            setEnumerationValue(Integer.parseInt((String) value));
        else if ("Boolean".equals(dataType))
            setBooleanValue((new Boolean((String) value)).booleanValue());
    }

    public boolean isVector() {
        return "Vector".equals(getDataType());
    }

    public boolean isMatrix() {
        return "Matrix".equals(getDataType());
    }

    public String getUnitDesc() {
        Unit unit = interfaceParamClient.getCurrentDataObject().getUnit();
        return unit == null ? "" : UnitAtom.getUnitDescription(unit.toString());
    }

    public InterfaceParameterClient getInterfaceParameterClient() {
        return interfaceParamClient;
    }

    public Unit getUnit() {
        return interfaceParamClient.getCurrentDataObject().getUnit();
    }
}

class FileDataDownloadingTracker extends Thread {
    private final long LOADING_TIME_LIMIT = 120000;  // 120 seconds

    private File fileToBeDownloaded;
    private long CHECKING_PERIOD = 300;  // check if loaded every 300 miliseconds

    public FileDataDownloadingTracker(File fileToBeDownloaded) {
        this.fileToBeDownloaded = fileToBeDownloaded;
    }

    public void run() {
        downloadFile();
    }

    private void downloadFile() {
        long startTime = System.currentTimeMillis();
        while (! (fileToBeDownloaded.exists() && fileToBeDownloaded.canRead())) {
            try {
                sleep(CHECKING_PERIOD);
                Debug.trace(Debug.ALL, "[DOME API] downloading file data at " + fileToBeDownloaded.getPath());
                if ((System.currentTimeMillis() - startTime) > LOADING_TIME_LIMIT) {
                    System.err.println("[DOME API] Fail to download file data at " + fileToBeDownloaded.getPath() + " : it took longer than time limit (" + LOADING_TIME_LIMIT / 1000 + " seconds)");
                    return;
                }
            } catch (InterruptedException e) { System.out.println(e); }
        }
    }
}