// CustomGuiInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.customGui;

import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * First Version: all text info
 * Second Version:
 * 1)adding a handle to auxfile ID
 * 2)the jarFilePath: local file location for build mode
 * 3)at run mode, the cresponding file location should be found through the database using ID
 *
 */
public class CustomGuiInfo implements XMLSupport
{
	String jarFilePath = "";
	String shortName = "", className = "";
	Id jarFileId;


	public static final String XML_TAG = "customGuiInfo";

	public CustomGuiInfo(String _className, String _shortName, String _jarFilePath, Id _jarFileId)
	{
		if (_jarFilePath == null)
			jarFilePath = "";
		else
			jarFilePath = _jarFilePath;
		shortName = _shortName;
		className = _className;
		if (_jarFileId == null)
			jarFileId = new Id("customGuiFile");
		else
			jarFileId = _jarFileId;
	}

	public CustomGuiInfo(String _className, String _shortName, String _jarFilePath)
	{
		this(_className, _shortName, _jarFilePath, null);
	}

	public CustomGuiInfo(Element xmlElement)
	{
		XMLUtils.makeRootElement(xmlElement);
		jarFilePath = xmlElement.elementText("jarFilepath");
		if (jarFilePath == null) jarFilePath = "";
		shortName = xmlElement.elementText("shortName");
		className = xmlElement.elementText("className");

		String id = xmlElement.elementText("jarFileId");
		if (id == null)
			jarFileId = new Id("customGuiFile");
		else
			jarFileId = new Id(id);
	}

	public String getJarFilePath()
	{
		return jarFilePath;
	}

	public void setJarFilePath(String jarFilePath)
	{
		this.jarFilePath = jarFilePath;
	}

	public String getShortName()
	{
		return shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getXmlTag()
	{
		return this.XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(XML_TAG);
		if (jarFilePath == null) jarFilePath = "";
		xml.addElement("jarFilepath").addText(jarFilePath);
		xml.addElement("shortName").addText(shortName);
		xml.addElement("className").addText(className);
		xml.addElement("jarFileId").addText(jarFileId.getIdString());
		return xml;
	}

	public Id getJarFileId()
	{
		return jarFileId;
	}

	public void setJarFileId(Id jarFileId)
	{
		this.jarFileId = jarFileId;
	}

}
