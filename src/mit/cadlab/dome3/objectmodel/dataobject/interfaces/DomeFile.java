// DomeFile.java
package mit.cadlab.dome3.objectmodel.dataobject.interfaces;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface DomeFile extends DataObject
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("File", "File");
	public static final String FILEPATH = "filepath";
	public static final String FILETYPE = "filetype";


	public DomeFile getDomeFile();

	public String getFilePath();

	public void setFilePath(String filePath);

	public void setFileType(String fileType);

	public String getFileType();

}
