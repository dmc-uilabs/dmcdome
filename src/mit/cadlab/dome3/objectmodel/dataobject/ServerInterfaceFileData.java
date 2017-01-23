// ServerInterfaceFileData.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;

import java.io.File;

import org.dom4j.Element;

/**
 * Version of FileData to be used for interface parameters on the server.
 * FilePath of interface parameters is set to match filepath of mapped model parameter.
 * FileName is used to preserve the original filename.
 */
public class ServerInterfaceFileData extends FileData
{
	private String fileName;

	public ServerInterfaceFileData()
	{
	}

	public ServerInterfaceFileData(File f)
	{
		super(f);
	}

	public ServerInterfaceFileData(DomeFile v)
	{
		super(v);
	}

	public ServerInterfaceFileData(String v)
	{
		super(v);
	}

	public ServerInterfaceFileData(String v, String type)
	{
		super(v, type);
	}

	public ServerInterfaceFileData(Element xmlElement)
	{
		super(xmlElement);
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileName() {
		if (fileName == null) {
			fileName = new File(filePath).getName();
		}
		return fileName;
	}

	/**
	 * Override to return only the filename, not the filepath, in the xml.
	 * @return
	 */
	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		Element filePathXml = xml.element("filePath");
		filePathXml.setText(fileName); // send the name without the path
		return xml;
	}

}
