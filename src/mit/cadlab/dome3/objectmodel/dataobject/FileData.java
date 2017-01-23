// FileData.java
package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.network.server.Debug;

import mit.cadlab.dome3.network.NetworkUtils;

import mit.cadlab.dome3.network.server.DomeServer;

import org.dom4j.Element;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

//TODO write a server and client specific class.  Both will extend from a common base class.
//TODO The server side class will manipulate or decide the file path by itself instead of popping
//TODO up a dialog to get the path whereas the client side class would ask the user where they
//TODO want to save the file.

/**
 * Note that VALUE changes from FILEDATA use the oldValue field to indicate if value has changed.
 * True means they have changed. False means they have not changed.
 */
public class FileData extends AbstractDataObject
        implements DomeFile
{

	public static String[] validFileType = {FileUtils.URL,FileUtils.BINARY, FileUtils.GIF, FileUtils.IGES, FileUtils.JPEG, FileUtils.PDF, FileUtils.STEP, FileUtils.TEXT, FileUtils.EXCEL, FileUtils.MATLAB, FileUtils.VRML,FileUtils.JAR};
	public static DomeFileChooser fileChooser = new DomeFileChooser();
	public static int defaultPort = 12347;
	
	protected String filePath;
	protected String fileType;
	protected long fileSize = -1; // if file size > FileUtils.MAXFILESIZE, don't load the file contents into messages
	protected String actualFilename = null; // name of file that the file contents originated from (different from filename at end of <filePath>

	//used only on client side
	//only initially, as soon as content is stored
	//first time, this will be set to true
	protected boolean hasCurrentChanges = false;
	protected boolean ifChecked = false;

    protected static final Object fileDialogLock = new Object();

	public FileData()
	{
		filePath = "";
		fileType = FileUtils.BINARY; //by default
		fileSize = -1;
	}

	public FileData(String v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeFile - null parameter");
		filePath = v;
		fileType = FileUtils.BINARY; //by default
		fileSize = -1;
	}

	public FileData(String v, String type)
	{
		if (v == null || type == null)
			throw new IllegalArgumentException("DomeFile - null parameter");
		if (!isValidFileType(type))
			throw new IllegalArgumentException("DomeFile - bad file type parameter");

		filePath = v;
		fileType = type;
		fileSize = -1;
	}

	public FileData(DomeFile v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeFile - null parameter");
		filePath = v.getFilePath();
		fileType = v.getFileType();
		//fileSize = getFileSize(filePath);
		fileSize = -1;
	}

    public FileData(File f){
       if (f == null)
			throw new IllegalArgumentException("DomeFile - null parameter");
		filePath = f.getPath();
		fileType = FileUtils.getTypeForFile(filePath);
		//fileSize = f.length();
		fileSize = -1;
    }

	public FileData(Element xmlElement)
	{
		super(xmlElement);
		filePath = (xmlElement.selectSingleNode("/dataobject/filePath")).getText();
		if (filePath == null)
			throw new IllegalArgumentException("DomeFile - invalid xml filePath: " +
			                                   xmlElement.selectSingleNode("/dataobject/filePath").asXML());
		fileType = (xmlElement.selectSingleNode("/dataobject/fileType")).getText();
		if (fileType == null)
			throw new IllegalArgumentException("DomeFile - invalid xml fileType: " +
			                                   xmlElement.selectSingleNode("/dataobject/fileType").asXML());
		fileSize = -1;
	}

	protected DomePropertyChangeSupport createDomePropertyChangeSupport()
	{
		return new DomePropertyChangeSupport(this) {
			protected boolean shouldFirePropertyChange(String propertyName, Object oldValue, Object newValue)
			{
                if (VALUE.equals(propertyName)) {
                    return ((Boolean)oldValue).booleanValue();
                }
				else
					return super.shouldFirePropertyChange(propertyName, oldValue, newValue);
			}
		};
	}

	// does file size exceed FileUtils.MAXFILESIZE threshold?
	public boolean fileTooLarge() {
		/*if (fileSize < 0) {
			// size not determined yet.
			fileSize = FileUtils.getFileSize(filePath);	
		}	
		*/
		fileSize = FileUtils.getFileSize(filePath);
		if (fileSize > FileUtils.MAXFILESIZE)
			return true;
				
		return false;
	}
	
    public void setActualFilename(String filename) {
    	actualFilename = filename;
    }
    
    public String getActualFilename() {
    	return actualFilename;
    }
    

	public boolean getHasCurrentChanges()
	{
		return hasCurrentChanges;
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return new FileContentAndTypeListener(); // for models
	}

	protected PropertyChangeListener createValueUnitShadowListener() // for interfaces
	{
		return new FilePathAndTypeListener();
	}

	// DataObject interface
	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof FileData);
	}

	protected TypeInfo getTypeInfo()
	{
		return DomeFile.TYPE_INFO;
	}

	public DataObject duplicate()
	{
		return new FileData(this);
	}

	/**
	 * @return content of text file as String; content of binary file as byte[];
	 * null if unable to get content of file. File path as String if file size too large.
	 */
	private Object getFileContent ()
	{
		return getFileContent(false);
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
			if ( ! forceFileLoad && fileTooLarge() ) {	
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

	//specific to FileData class
	public void setFileValue(Component parent, Object value)
	{
        ensureGoodFileName(parent);
		if (fileType.equals(FileUtils.TEXT)) {
			try {
				FileUtils.writeTextToFile((String) value, filePath);
			} catch (IOException e) {
				e.printStackTrace();
				if (e instanceof FileNotFoundException) {
					String msg = "Error while opening " + filePath;
					OneButton1Msg.showWarning(null, "Error opening file", msg, "OK", new Dimension(1, 1));
				}
				else {
					String msg = "Error while opening " + filePath;
					OneButton1Msg.showWarning(null, "I/O error", msg, "OK", new Dimension(1, 1));
				}
			}
		} else {  //binary file
			try {
				FileUtils.writeByteArrayAsBinaryFile((byte[]) value, filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				String msg = filePath + " not found.";
				OneButton1Msg.showError(null, "File not found", msg, "OK", new Dimension(1, 1));
			} catch (IOException e) {
				e.printStackTrace();
				String msg = "I/O error while opening " + filePath;
				OneButton1Msg.showWarning(null, "I/O error", msg, "OK", new Dimension(1, 1));
			}
		}
	}


	public void setValues(List values)
	{
		if (values.size() > 0) {
			String errMsg = null;
			Object value = values.get(0);
            //MAK: this checks filePath variable (filename AND path): ensureGoodFileName(null);
			ensureGoodFileName(null);
            if (value instanceof List) { // just in case it was wrapped twice
				value = ((List) value).get(0);
			}

            // is <value> a valid/supported type?
            //if (!((value instanceof String) || (value instanceof byte[]) || (value instanceof URL))) {
            if (!((value instanceof String) || (value instanceof byte[]) || (value instanceof FileTransport) || (value instanceof URL))) {
				errMsg = "invalid content type " + ClassUtils.getClassName(value) + " for " + filePath;
				OneButton1Msg.showWarning(null, "Error setting file content", errMsg, "OK", new Dimension(1, 1));
			}
			else {
				// only valid supported types here...
				try {
					if (value instanceof String) {
						Debug.trace(Debug.ALL, "Writing a text file to " + filePath + " with string length: " + ((String) value).length());
                        FileUtils.writeTextToFile((String) value, filePath);
                        
                     // update fileSize to reflect new file contents written
    					fileSize = getFileSize(filePath);
                    } 
					else if (value instanceof FileTransport) { 
						// write the file contents to the file with the given filename, not the parameter's default filename
						FileTransport fileTransportObj = (FileTransport) value;
						String actualFullpathFilename = fileTransportObj.getFileName();
						String actualFilename = FileUtils.getFileName(actualFullpathFilename);
						//String localFilePath = FileUtils.getFilePath(filePath);
						//String localFilename = localFilePath + actualFilename;
						//override default filename component of <filepath> with name of file 
						//filePath = localFilename;
						
						setActualFilename(actualFilename);
						
						byte[] fileContents = fileTransportObj.getFileContents();
						
						//Debug.trace(Debug.ALL, "Writing a binary file to " + filePath + " with array length: " + ((byte[]) value).length);
						Debug.trace(Debug.ALL, "1Writing a binary file to " + filePath + " with array length: " + (fileContents != null ? fileContents.length : 0 ));
						
                        //ORIG: FileUtils.writeByteArrayAsBinaryFile((byte[]) value, filePath);
						//FileUtils.writeByteArrayAsBinaryFile(fileContents, localFilename);
						FileUtils.writeByteArrayAsBinaryFile(fileContents, filePath);
						
						// update fileSize to reflect new file contents written
						fileSize = getFileSize(filePath);
                    }
					else if (value instanceof byte[]) { 
						Debug.trace(Debug.ALL, "2Writing a binary file to " + filePath + " with array length: " + ((byte[]) value).length);
                        FileUtils.writeByteArrayAsBinaryFile((byte[]) value, filePath);
                        
                     // update fileSize to reflect new file contents written
    					fileSize = getFileSize(filePath);
                    }
					else if (value instanceof URL) {
						// if the value to set was supposed to be a large file, a URL to the file was supplied instead because
			            // large files can exceed the amount of memory available to simply stuff into the payload of a message.
			            // we must fetch the file through alternate means.
						
						URL fileURL = (URL) value;
						String host = fileURL.getHost();
						int port = fileURL.getPort();
						String fileURLString = fileURL.toString();
						int position = fileURLString.lastIndexOf("/");
						String fName = fileURLString.substring(position+1);
						setActualFilename(fName);
			            	            	
						// Invoke a request for the file
						NetworkUtils networkUtils = new NetworkUtils();
						// Get the file from the port and and save to filePath
						Debug.trace(Debug.ALL, "Fetching " + fileURLString + " and store it at " + filePath);
						networkUtils.httpGetRemoteFile1((URL)value, fileURL, filePath);

						// update fileSize to reflect new file contents written
						fileSize = getFileSize(filePath);	
					}
					
					hasCurrentChanges = true; //when relation executes and values are set on client
					firePropertyChange(FileData.VALUE, Boolean.TRUE, null);
				}
				catch (FileNotFoundException e) {
					System.err.println(e);
					hasCurrentChanges = false;
					String msg = filePath + " not found.";
					OneButton1Msg.showError(null, "File not found", msg, "OK", new Dimension(1, 1));
				}
				catch (IOException e) {
					System.err.println(e);
					hasCurrentChanges = false;
					String msg = "I/O error while opening " + filePath;
					OneButton1Msg.showWarning(null, "I/O error", msg, "OK", new Dimension(1, 1));
				}
			}
		}
	}

	//update for relation calculation
	public void setValues(DataObject newObj)
	{
		//MAK: copy in the actual filename
		setActualFilename( ((FileData) newObj).actualFilename );

		if (this.filePath.equals(((FileData) newObj).filePath)) { // same location
			firePropertyChange(FileData.VALUE, Boolean.FALSE, null); // don't go in a loop, but notify those who want to know file has been modified
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
		
		//MAK: not sure why we should be copying files.
		FileUtils.copyFile((DomeFile) newObj, this, false); //don't show warnings
		//this.filePath = ((FileData) newObj).filePath;
		
		
		
		firePropertyChange(FileData.VALUE, Boolean.TRUE, null);
	}


	//to send the file to remote clients
	public void notifyFileChanged()
	{
		firePropertyChange(FileData.VALUE, Boolean.TRUE, null);
	}

	public Object __eq__(Object o)
	{
		return new BooleanData(equals(o));
	}

    public Object __ne__(Object o)
    {
        return new BooleanData(!equals(o));
    }

	public boolean equals(Object anotherfile)
	{
		if (anotherfile instanceof FileData)
			return FileUtils.compareFile(this, (FileData) anotherfile);
		else
			return false;
	}

	// DomeFile interface
	public DomeFile getDomeFile()
	{
		return this;
	}

	public String getFilePath()
	{
		return filePath;
	}

    public void setFilePath(String filePath) {
        if (filePath == null) return;
        String oldFilePath = this.filePath;

        this.filePath = filePath;
        firePropertyChange(FILEPATH, oldFilePath, this.filePath);
    }

    public void setFilePathAtRuntime(String filePath) {
		Debug.trace(Debug.ALL, "FileData.setFilePathAtRuntime: filePath = '" + filePath + "'");
        setFilePath(filePath);

        File newFile = new File(filePath);
        if (newFile.exists()) {
            byte[] newValue = null;
            FileTransport fileTransportObj = null;
            
        	// if file length <= FileUtils.MAXFILESIZE, read the file content into a byte array for inclusion in the property change event.
        	// if file length > FileUtils.MAXFILESIZE, make the file payload for the message be empty -- force the message receiver to request the file instead.
            if (newFile.isFile() && newFile.length() <= FileUtils.MAXFILESIZE) {
            	/* read the file content and fire the value change event at runtime */
            	try {
            		newValue = FileUtils.readBinaryFileAsByteArray(filePath);
            		// MAK: store the file contents byte array in a class instance that also contains other file info, such as the file name
            		fileTransportObj = new FileTransport(filePath, newValue);
            	} catch (Exception e) {
            		Debug.trace(Debug.ERROR, "Fail to read bytes from new file path: " + filePath);
            	}
            	if (newValue != null) {
            		// MAK: ORIG: firePropertyChange(VALUE, Boolean.TRUE, newValue);
            		// MAK: send a FileTransport obj instead of the raw byte array so we have other file info included
            		firePropertyChange(VALUE, Boolean.TRUE, fileTransportObj);
            	}
            }
            else {
            	// file length > FileUtils.MAXFILESIZE ... send a URL to the file as the message payload instead of file contents
            	URL fileURL = null;
            	String filePathString = filePath.toString();
            	String actualFileName = FileUtils.getFileName(filePathString);

            	String fileURLString = "/" + actualFileName;

            	try {
            		URI fileURI = new URI("http",null, NetworkUtils.getIpAddress(),FileData.defaultPort,fileURLString, null, null);
            		fileURL = fileURI.toURL();
            	}
            	catch (MalformedURLException me) {
            		Debug.trace(Debug.ERROR, "FileData.setFilePathAtRuntime() failed to create URL for file at:" + fileURLString);
            		me.printStackTrace();
            	} catch (URISyntaxException e) {
            		Debug.trace(Debug.ERROR, "FileData.setFilePathAtRuntime() badly formed URI");
					e.printStackTrace();
				}
            	
            	// Start a thread to serve big file
            	ServeBigFileByHttp handler = new ServeBigFileByHttp(newFile, FileData.defaultPort);

            	firePropertyChange(VALUE,Boolean.TRUE,fileURL);
            }
        }
    }

	public void setFileType(String fileType)
	{
		if (!isValidFileType(fileType)) return;
		String oldFileType = this.fileType;
		this.fileType = fileType;
		firePropertyChange(FILETYPE, oldFileType, this.fileType);
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

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		xml.addElement("filePath").addText(filePath);
		xml.addElement("fileType").addText(fileType);
		return xml;
	}

//	private boolean hasGoodFileName = false;

	public synchronized void ensureGoodFileName(Component parent)
	{
        String originFilePath = new String(filePath);

        File file = new File(filePath);
        String newFileName = null;

		if (!file.getPath().equals(file.getAbsolutePath())) {
            synchronized (fileDialogLock) {
                OneButton1Msg.showOption(parent, "Select a path", "This interface contains a file parameter for the file '" +
                        file.getName() + "'.\n" +
                        "DOME is sending a copy of the file to your computer so you can view it.\n" +
                        "Please select a location and name for the file.", "ok", new Dimension(240, 110));
                newFileName = FileData.fileChooser.showSaveDialog(parent,
                                                                         FileUtils.getFilterForType(getFileType()), file);
                String suffix = FileUtils.getDefaultSuffixForType(getFileType());
                if (!newFileName.endsWith(suffix)) { //this means user pick another type of file or forgot to add extension
                    if (newFileName.indexOf(".") == -1) {
                        int answer = TwoButton2Msg.showOption(parent, "No file extension", "The file name has no extension " + suffix + "!", "",
                                                              "add extension", "do not add extension", new Dimension(220, 80));
                        if (answer == TwoButton2Msg.LEFT_OPTION) {
                            newFileName = newFileName + suffix;
                        } else {// otherwise, keep it
                            setFileType(FileUtils.getTypeForFile(newFileName));
                        }

                    } else
                        setFileType(FileUtils.getTypeForFile(newFileName));
                }
            }
            setFilePath(newFileName);
		}
        else {
            String fileName = file.getName();
            String filePathAndName = file.getPath();
            String directory = filePathAndName.substring(0, filePathAndName.length() - fileName.length());
            File file1 = new File(directory);
            if (file1.exists() && file1.isDirectory())
                return;
            else {
                synchronized (fileDialogLock) {
                    OneButton1Msg.showOption(parent, "Select a path", "This interface contains a file parameter for the file '" +
                            fileName + "'.\n" +
                            "DOME is sending a copy of the file to your computer so you can view it.\n" +
                            "Please select a location and name for the file.", "ok", new Dimension(240, 110));
                    newFileName = FileData.fileChooser.showSaveDialog(parent,
                            FileUtils.getFilterForType(getFileType()), file);

                    String suffix = FileUtils.getDefaultSuffixForType(getFileType());
                    if (!newFileName.endsWith(suffix)) { //this means user pick another type of file or forgot to add extension
                        if (newFileName.indexOf(".") == -1) {
                            int answer = TwoButton2Msg.showOption(parent, "No extension", "The file name has no extension " + suffix + "!", "",
                                    "add extension", "don't add extension", new Dimension(300, 100));
                            if (answer == TwoButton2Msg.LEFT_OPTION) {
                                newFileName = newFileName + suffix;
                            } else {// otherwise, keep it
                                setFileType(FileUtils.getTypeForFile(newFileName));
                            }

                        } else
                            setFileType(FileUtils.getTypeForFile(newFileName));
                    }
                }
                setFilePath(newFileName);
//				hasGoodFileName = true;
            }
		}

        Debug.trace(Debug.ALL, "Ensuring file name: changed from" + originFilePath + " to " + filePath + ", is ServerInterfaceFileData instance? = " + (this instanceof ServerInterfaceFileData));
	}

	protected class FileContentAndTypeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof DomeFile && property.equals(DomeFile.FILETYPE)) {
				setFileType(((DomeFile) obj).getFileType());
			}
			if (obj instanceof FileData && property.equals(DataObject.VALUE)) {
				Object source = evt.getSource();
				if (source instanceof FileData) {
					setValues((FileData) source);
				}
				else {
					System.out.println("FileData.FileContentAndTypeListener change event from unsupported object type: " + ClassUtils.getClassName(source));
				}
			}
		}
	}

	protected class FilePathAndTypeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof DomeFile && property.equals(DomeFile.FILEPATH)) {
				//setFilePath();
				setFilePath((new File(((DomeFile) obj).getFilePath()).getName()));
			}
			if (obj instanceof DomeFile && property.equals(DomeFile.FILETYPE)) {
				setFileType(((DomeFile) obj).getFileType());
			}
		}
	}

}
