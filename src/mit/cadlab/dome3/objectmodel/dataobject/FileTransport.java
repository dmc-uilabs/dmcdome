// FileTransport.java
package mit.cadlab.dome3.objectmodel.dataobject;


import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.network.server.Debug;

import mit.cadlab.dome3.network.NetworkUtils;

import mit.cadlab.dome3.network.server.DomeServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * This class acts as a transport mechanism for moving files around. If the file size <= MAXFILESIZE
 * the contents of the file contained within this class. If the file size > MAXFILESIZE, the destination
 * will have to retrieve the file from its end.
 */

public class FileTransport {

	public static String[] validFileType = {FileUtils.BINARY, FileUtils.GIF, FileUtils.IGES, FileUtils.JPEG, FileUtils.PDF, FileUtils.STEP, FileUtils.TEXT, FileUtils.EXCEL, FileUtils.MATLAB, FileUtils.VRML,FileUtils.JAR};
	
	public static final long MAXFILESIZE=  2000000;  // 2000; // 2000000;  // Max file size allowed to be included in file messages
	
	protected byte[] fileContents = null;
	protected String filePath;
	protected String fileType;
	protected long fileSize = -1; // if file size > MAXFILESIZE, don't load the file contents into messages
	
	//used only on client side
	//only initially, as soon as content is stored
	//first time, this will be set to true
	protected boolean hasCurrentChanges = false;
	protected boolean ifChecked = false;


	public FileTransport()
	{
		fileContents = null;
		filePath = "";
		fileType = FileUtils.BINARY; //by default
		fileSize = -1;
	}


	public FileTransport(String filename, byte[] fileContentsAsByteArray)
	{
		if (fileContentsAsByteArray != null) {
			fileContents = fileContentsAsByteArray;
			fileSize = fileContents.length;
		}
		
		filePath = filename;
		fileType = FileUtils.BINARY; //by default
	}


	public String getFileName() {
		return filePath;
	}
	
	public void setFileName(String filename) {
		filePath = filename;
	}

	public byte[] getFileContents() {
		return fileContents;
	}
	
	public void setFileContents(byte[] fileContentsAsByteArray) {
		fileContents = fileContentsAsByteArray;
	}
	
	// does file size exceed MAXFILESIZE threshold?
	public boolean fileTooLarge() {
		/*if (fileSize < 0) {
			// size not determined yet.
			fileSize = FileUtils.getFileSize(filePath);	
		}	
		*/
		fileSize = FileUtils.getFileSize(filePath);
		if (fileSize > MAXFILESIZE)
			return true;
				
		return false;
	}
	
	protected TypeInfo getTypeInfo()
	{
		return DomeFile.TYPE_INFO;
	}



	/**
	 * @return content of text file as String; content of binary file as byte[];
	 * null if unable to get content of file. File path as String if file size too large.
	 */
	private Object getFileContent ()
	{
		return getFileContent(Boolean.FALSE);
	}

	/**
	 * @param forceFileLoad Boolean flag indicating whether to read file contents regardless of file size. If false, and file too large, filepath is returned instead of file contents 
	 * 						 
	 * @return content of text file as String; content of binary file as byte[], content of a large file as a URL;
	 * null if unable to get content of file
	 */
	private Object getFileContent (Boolean forceFileLoad)
	{
		Object fileContent = null;
		try {
			// should we return the filepath as the content?
			if ( ! forceFileLoad.booleanValue() && fileTooLarge() ) {	
					// file contents too large for message content -- return a URL to the file
				
	            	URL fileURL = null;
	            	String fileURLString = "file:///" + filePath;
	            	// make the URL a HTTP GET call
	            	//String fileURLString = "http://localhost:9002" + filePath;
	            	// send URL to server, which will execute http GET using the URL
	            	//String fileURLString = "http://localhost:9002/file=" + filePath;
	            	//String fileURLString = "http://localhost:7791/file=" + filePath;
	            	
	            	try {
	            		fileURL = new URL(fileURLString);
	            		return fileURL;
	            	}
	            	catch (MalformedURLException me) {
	            		System.err.println("FileData.getFileContent() failed to create URL for file at:" + fileURLString);
	            	}
			}
		
			//if (true)
			//	return filePath; // MAK test: do we *ever* need to read in the file contents?
			
			if (fileType.equals(FileUtils.TEXT) || fileType.equals(FileUtils.VRML)) {
				fileContent = FileUtils.readTextFileAsString(filePath);
			}
			else {
				fileContent = FileUtils.readBinaryFileAsByteArray(filePath);
			}
		}
		catch (Exception e) {
			// ignore
		}
		return fileContent;
	}
	
	public List getValues()
	{
		return Collections.singletonList(getFileContent());
	}

	/**
	 * @return file content as String for text files and as byte[] for binary files
	 * returns an empty String if file can not be found.
	 */
	public Object getValuesForXmlRpcUse ()
	{
		Object content = getFileContent();
		if (content == null)
			return "";
		else
			return content;
	}
/*
	//specific to FileData class
	public void setFileValue(Component parent, Object value)
	{
        //ensureGoodFileName(parent);
		if (fileType.equals(FileUtils.TEXT)) {
			try {
				FileUtils.writeTextToFile((String) value, filePath);
			} catch (IOException e) {
				e.printStackTrace();
				String msg = "Error while opening " + filePath;
				System.err.println(msg);
			}
		} else {  //binary file
			try {
				FileUtils.writeByteArrayAsBinaryFile((byte[]) value, filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				String msg = filePath + " not found.";
				System.err.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
				String msg = "I/O error while opening " + filePath;
				System.err.println(msg);
			}
		}
	}
*/

	public void setValues(List values)
	{
		if (values.size() > 0) {
			String errMsg = null;
			Object value = values.get(0);
            //ensureGoodFileName(null);
            if (value instanceof List) { // just in case it was wrapped twice
				value = ((List) value).get(0);
			}

            // is <value> a valid/supported type?
            if (!((value instanceof String) || (value instanceof byte[]) || (value instanceof URL))) {
				errMsg = "FileTransport.setValues(): invalid content type " + ClassUtils.getClassName(value) + " for " + filePath;
				System.err.println(errMsg);
			}
			else {
				// only valid supported types here...
				try {
					if (value instanceof String) {
						Debug.trace(Debug.ALL, "Writing a text file to " + filePath + " with string length: " + ((String) value).length());
                        FileUtils.writeTextToFile((String) value, filePath);
                    } 
					else if (value instanceof byte[]) { 
						Debug.trace(Debug.ALL, "Writing a binary file to " + filePath + " with array length: " + ((byte[]) value).length);
                        FileUtils.writeByteArrayAsBinaryFile((byte[]) value, filePath);
                    }
					else if (value instanceof URL) {
						// if the value to set was supposed to be a large file, a URL to the file was supplied instead because
			            // large files can exceed the amount of memory available to simply stuff into the payload of a message.
			            // we must fetch the file through alternate means.
						
						URL fileURL = (URL) value;
						/*URL fileURL = null;
							try {
								String filePath = ((URL) value).getPath();
								fileURL = new URL("http://localhost:9002/FinsterFile.txt");
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							*/
						URL serverURL = null;
						try {
							System.out.println("*** DomeServer.getDefaultServerPort():" + DomeServer.getDefaultServerPort());
							System.out.println("*** DomeServer.getPort():" + DomeServer.getPort());
							
							
							serverURL = new URL("http://localhost:9002");
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			       	
						String fileURLString = fileURL.toString();
			            	            	
						// invoke a request for the file
						NetworkUtils networkUtils = new NetworkUtils();
						// pull down the file and save to filePath
						Debug.trace(Debug.ALL, "Using http to fetch a file to " + filePath);
						/*TODO: 
						 * Look more into this...
						 */
						networkUtils.copyLocalFile(serverURL, fileURL, filePath);
						System.out.println("From FileTransport.java, method setValue(...):  used copy local file here...");
						Debug.trace(Debug.ALL, "FINISHED Using http to fetch a file to " + filePath);	
					}
			             
					// update fileSize to reflect new file contents written
					fileSize = getFileSize(filePath);
					
					hasCurrentChanges = true; //when relation executes and values are set on client
					//firePropertyChange(FileTransport.VALUE, Boolean.TRUE, null);
				}
				catch (FileNotFoundException e) {
					System.err.println(e);
					hasCurrentChanges = false;
					String msg = filePath + " not found.";
					System.err.println(msg);
				}
				catch (IOException e) {
					System.err.println(e);
					hasCurrentChanges = false;
					String msg = "I/O error while opening " + filePath;
					System.err.println(msg);
				}
			}
		}
	}

	//update for relation calculation
	public void setValues(DataObject newObj)
	{
		if (this.filePath.equals(((FileTransport) newObj).filePath)) { // same location
			//firePropertyChange(FileTransport.VALUE, Boolean.FALSE, null); // don't go in a loop, but notify those who want to know file has been modified
			return;
		}
		//change the file type
		fileType = ((DomeFile) newObj).getFileType();
		String suffix = FileUtils.getDefaultSuffixForType(fileType);

		//keep the old filepath
		//append new file suffix
		if (filePath.length() > 0 && (!filePath.endsWith(suffix)))
			filePath = filePath + suffix;

		//TODO file content should actually be copied only if we have statement in the relation
		//TODO silmilar to (fileA = fileB)
		//TODO right now this code will always copy the file to local file system
		//copy the fileContent
		//FileUtils.copyFile((DomeFile) newObj, this, false); //don't show warnings
		//firePropertyChange(FileTransport.VALUE, Boolean.TRUE, null);
	}



	public Object __eq__(Object o)
	{
		return new BooleanData(equals(o));
	}

    public Object __ne__(Object o)
    {
        return new BooleanData(!equals(o));
    }



	public String getFilePath()
	{
		return filePath;
	}

    public void setFilePath(String filePath) {
        if (filePath == null) return;
        String oldFilePath = this.filePath;

        this.filePath = filePath;
        //firePropertyChange(FILEPATH, oldFilePath, this.filePath);
    }

    public void setFilePathAtRuntime(String filePath) {
        setFilePath(filePath);

        File newFile = new File(filePath);
        if (newFile.exists()) {
            byte[] newValue = null;
            
        	// if file length <= MAXFILESIZE, read the file content into a byte array for inclusion in the property change event.
        	// if file length > MAXFILESIZE, make the file payload for the message be empty -- force the message receiver to request the file instead.
            if (newFile.isFile() && newFile.length() <= MAXFILESIZE) {
            	/* read the file content and fire the value change event at runtime */
            	try {
            		newValue = FileUtils.readBinaryFileAsByteArray(filePath);
            	} catch (Exception e) {
            		Debug.trace(Debug.ERROR, "Fail to read bytes from new file path: " + filePath);
            	}
            	if (newValue != null) {
            		//firePropertyChange(VALUE, Boolean.TRUE, newValue);
            	}
            }
            else {
            	// file length > MAXFILESIZE ... send a URL to the file as the message payload instead of file contents
            	URL fileURL = null;
            	String fileURLString = "file:///" + filePath;
            	// make the URL a HTTP GET call
            	//String fileURLString = "http://localhost:9002" + filePath;
            	// send URL to server, which will execute http GET using the URL
            	//String fileURLString = "http://localhost:9002/file=" + filePath;
            	//String fileURLString = "http://localhost:7791/file=" + filePath;
            	
            	try {
            		fileURL = new URL(fileURLString);
            	}
            	catch (MalformedURLException me) {
            		System.err.println("FileData.setFilePathAtRuntime() failed to create URL for file at:" + fileURLString);
            	}

        		//firePropertyChange(VALUE, Boolean.TRUE, fileURL);
            }
        }
    }

	public void setFileType(String fileType)
	{
		if (!isValidFileType(fileType)) return;
		String oldFileType = this.fileType;
		this.fileType = fileType;
		//firePropertyChange(FILETYPE, oldFileType, this.fileType);
	}

	public boolean isIfChecked()
	{
		return ifChecked;
	}

	public void setIfChecked(boolean ifChecked)
	{
		this.ifChecked = ifChecked;
	}

	public static boolean isValidFileType(String type)
	{
		boolean isValid = false;
		for (int i = 0; i < validFileType.length; i++) {
			if (type.equals(validFileType[i])) {
				isValid = true;
				break;
			}
		}
		return isValid;
	}

    public long getFileSize(String fileName) {
    	fileSize = FileUtils.getFileSize(fileName);
    	return fileSize;
    }
    
	public String getFileType()
	{
		return fileType;
	}

	public String toString()
	{
		return filePath;
	}




}
