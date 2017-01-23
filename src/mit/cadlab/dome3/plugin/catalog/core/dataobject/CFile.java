package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.FileTransport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;
import mit.cadlab.dome3.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * User: Sangmok Han
 * Date: 2006. 4. 3.
 */
public class CFile extends CDataObject {

	private String actualFilename=null; // name of file that the file contents originated from (different from filename at end of <filePath>
    private String absolutePathAndFilename;

    /** create CReal instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CFile(String value, String unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setFileValue(value.getBytes());
        this.setUnit(unit);
    }

    /** create CFile instance that has the same value and unit of the given data. contructed CReal is not an interface parameter */
    public CFile(String value, CUnit unit) {
        super(); // make this catalog data object is not associated with interface input parameter

        this.setFileValue(value.getBytes());
        this.setUnit(unit);
    }

    /** new CFile("Empty"); */
    public CFile(String fileNameAndFileValueStr) {
        this(DataObjectUtil.getFileNameAndFileValue(fileNameAndFileValueStr) [1], CUnit.NO_UNIT);
    }

    /** create CFile instance corresponding to DomeReal parameter. contructed CFile is an interface parameter  */
    public CFile(Parameter fileParam) {
        super(fileParam);
        this.copyFromDomePluginDataObjectToCatalogDataObject();
    }

    /** copy From DomePluginDataObject To CatalogDataObject */
    public void copyFromDomePluginDataObjectToCatalogDataObject() {
//        String pluginDataObjPath = ((FileData) getDomePluginDataObject()).getFilePath();
//        //byte[] pluginDataObjValue = ((FileData) getDomePluginDataObject()).getFilePath();
//
//        byte[] pluginDataObjValue = new byte[0];
//        try {
//            System.out.println("pluginDataObjPath: " + pluginDataObjPath);
//            pluginDataObjValue = FileUtils.readBinaryFileAsByteArray(pluginDataObjPath);
//        } catch (IOException e) {
//            System.out.println("error in reading");
//            e.printStackTrace();
//        }
//        setValue(pluginDataObjValue);

//
//        String pluginDataObjPath = ((FileData) getDomePluginDataObject()).getFilePath();
    	FileData fileDataObj = (FileData) getDomePluginDataObject();
    	//MAK: copy in the actualFilename to maintain the chain of origin information
    	setActualFilename(fileDataObj.getActualFilename());
    	
        Object pluginDataObjValue = ((FileData) getDomePluginDataObject()).getValues().get(0);
        
        if (pluginDataObjValue instanceof String) {
            setFileValue(((String) pluginDataObjValue).getBytes());
        } 
        else if (pluginDataObjValue instanceof FileTransport) {
        	System.out.println("CFile.copyFromDomePluginDataObjectToCatalogDataObject(): copied FileTransport obj. Copy contents instead?");
        	FileTransport fileTransportObj = (FileTransport) pluginDataObjValue;
        	setFileValue(fileTransportObj);
        	setActualFilename(fileTransportObj.getFileName());
        }
        else if (pluginDataObjValue instanceof byte[]) {
            setFileValue((byte[]) pluginDataObjValue);
        }
        else if (pluginDataObjValue instanceof URL) {
        	setFileValue((URL) pluginDataObjValue);
        }
    }

    /** copy From CatalogDataObject To DomePluginDataObject*/
    public void copyFromCatalogDataObjectToDomePluginDataObject() {
        byte[] catDataObjValue = (byte[]) getValue();
        //((FileData) getDomePluginDataObject()).setFileValue(null, catDataObjValue);
        //MAK: ORIG: ((FileData) getDomePluginDataObject()).setValues(Vectors.create(catDataObjValue));
        FileData domePluginDataObj = (FileData) getDomePluginDataObject();
        domePluginDataObj.setValues(Vectors.create(catDataObjValue));
        domePluginDataObj.setActualFilename(actualFilename);
    }

    //public void setFileValue(byte[] fileValue) {
    //    setValue(fileValue);
    //}
    
    public void setFileValue(Object fileValue) {
        setValue(fileValue);
    }
    
    public void setFileValue(URL fileValue) {
        setValue(fileValue);
    }

    //public byte[] getFileValue() {
    //    return (byte[]) getValue();
    //}

    public Object getFileValue() {
        return getValue();
    }
    
    public String getFilePath() {
        return absolutePathAndFilename;
    }

    public void setFilePath(String filePath) {
        this.absolutePathAndFilename = filePath;
    }

    // Return the directory path portion of the full filename
    public String getPath() {
    	String pathComponent = FileUtils.getFilePath(absolutePathAndFilename);
    	return pathComponent;
    }
    
    public void setActualFilename(String filename) {
    	actualFilename = filename;
    }
    
    public String getActualFilename() {
    	return actualFilename;
    }
    
    /** plus operator on two files assumes that both files are text files. */
    public Object plus(Object obj) {
        if (obj instanceof CFile) {
			CFile dataObj = (CFile) obj;
			Object fileValueObj = dataObj.getFileValue();
			byte[] objValue = null;
			
			if (fileValueObj instanceof byte[])
				objValue = (byte[]) dataObj.getFileValue();
            //String filePath = dataObj.getFilePath();

            CUnit resultUnit = getResultantUnitAfterPlusMinus(dataObj);

            Object thisFileValueObj = this.getFileValue();
            byte[] thisObjValue = null;
            if (thisFileValueObj instanceof byte[])
            	thisObjValue = (byte[]) thisFileValueObj;
            
            //return new CFile(new String(this.getFileValue()) + new String(objValue), resultUnit);
            return new CFile(new String(thisObjValue) + new String(objValue), resultUnit);
		} else if (obj instanceof String) {
            String strValue = (String) obj;
            Object thisFileValueObj = this.getFileValue();
            byte[] thisFileValue = null;
            if (thisFileValueObj instanceof byte[])
            	thisFileValue = (byte[]) thisFileValueObj;
            //return new CFile(new String(this.getFileValue()) + strValue, this.getUnit().cloneUnit());
            return new CFile(new String(thisFileValue) + strValue, this.getUnit().cloneUnit());
		} else {
			throw new IllegalArgumentException("Can't add " + this + " to " + obj);
		}
    }

    public Object leftShift(Object obj) {
        //checkUnitConversionRule(obj);
        if (obj instanceof CFile) {
			CFile dataObj = (CFile) obj;
			Object dataObjFileValue = dataObj.getFileValue();
            this.setFileValue(dataObjFileValue);
            this.setActualFilename(dataObj.getActualFilename());
        } else if (obj instanceof String) {
            String strValue = (String) obj;
            this.setFileValue(strValue.getBytes());
		} else {
			throw new IllegalArgumentException("error in LHR.leftShift(RHS)");
		}

        /* for the File parameter, the value is stored as a file with a name specified by defaultValue of the File parameter */
        storeValueAsFile();

        return this;
    }

    /* this should be called after file value is set to this File data object */
    private void storeValueAsFile() {
        /* create a download folder named like "relation_A" under the working directory of current model runtime */
    	Object fileValueObj = this.getFileValue();
    	byte[] fileValue = null;
    	
   	
    	if (fileValueObj instanceof byte[]) {
    		fileValue = (byte[]) fileValueObj;
            
    		ByteBuffer byteBuffer = ByteBuffer.wrap(fileValue);
            File file = new File(absolutePathAndFilename);

            try {
                FileChannel fc = new FileOutputStream(file, false).getChannel();
                fc.write(byteBuffer);
                fc.close();
            } catch (IOException e) {
                System.err.println(e);
            }
    	}
    	else if (fileValueObj instanceof FileTransport) {
    		fileValue = ((FileTransport) fileValueObj).getFileContents();
            
    		ByteBuffer byteBuffer = ByteBuffer.wrap(fileValue);
            File file = new File(absolutePathAndFilename);

            try {
                FileChannel fc = new FileOutputStream(file, false).getChannel();
                fc.write(byteBuffer);
                fc.close();
            } catch (IOException e) {
                System.err.println(e);
            }
    	}
    	else if (fileValueObj instanceof URL) {
    		// file size too large to carry around in memory/msgs, so URL to file used instead
    		URL fileURL = (URL) fileValueObj;
    		//fileValue = fileURL.toString().getBytes();
    		// fetch file from URL and write to <filePath>
    		String fileURLString = fileURL.toString();
        	
			// invoke a request for the file
			NetworkUtils networkUtils = new NetworkUtils();
			// pull down the file and save to filePath
			//String serverURLstr = "localhost:7791";
			URL serverURL = null;
			try {
				String hostnameAndPort = DomeServer.getHostnameAndPort();
				//serverURL = new URL("http://localhost:7791");
				serverURL = new URL("http://" + hostnameAndPort);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			networkUtils.httpGetRemoteFile1(serverURL, fileURL, absolutePathAndFilename);
    	}
    	

    }

    public String toString() {
        String mapped = "false";
        if (isAssociatedWithInterfaceInputParameter()) {
            mapped = "itf input param";
        }

        if (isAssociatedWithInterfaceOutputParameter()) {
            mapped = "itf output param";
        }
        
        Object fileValueObj = getFileValue();
        byte[] fileValue = null;
        if (fileValueObj instanceof byte[])
        	fileValue = (byte[]) fileValueObj;
        
        // MAK: return "[CFile: value=byte[" + fileValue.length + "], path=" + getFilePath() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
        return "[CFile: actualFilename=\"" + actualFilename + "\" value=byte[" + fileValue.length + "], path=" + getFilePath() + ", unit=" + getUnit() + ", mapped=" + mapped + " ]";
    }
    
    public long getFileSize()
    {
    	File file = new File(absolutePathAndFilename);
        long fileSize = file.length();    	
    	return fileSize;
    }
}
