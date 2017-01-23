// AbstractAuxFile.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.io.File;

/**
 *  
 */
public abstract class AbstractAuxFile extends AbstractDomeObject implements AuxFile
{

    public final static String HTTP="http://";
    public final static String FILE="file:///";


	protected Model model;
	protected File file; //file should be valid

  //  protected boolean shouldUpload=true;
    protected String fileType=FILE;
    protected boolean isMainModelFile=false;
	protected boolean executeOnServer=true;



    /**
	 * constructor, provided the file is a valid file
	 * @param m
	 * @param id
	 * @param name
	 * @param f
	 */
	public AbstractAuxFile(Model m, Id id, String name,File f)
	{
	    super(id, name);
		this.model = m;
		this.file=f;
	}

	/**
	 *
	 * @param xmlElement
	 */
	public AbstractAuxFile(Element xmlElement){
		super(xmlElement);
  //     	Element shouldUploadelement = (Element) xmlElement.selectSingleNode("shouldUpload");
  //		shouldUpload = new Boolean(shouldUploadelement.attributeValue("value")).booleanValue();
        Element isMainModelFileElement = (Element) xmlElement.selectSingleNode("isMainModelFile");
	    isMainModelFile = new Boolean(isMainModelFileElement.attributeValue("value")).booleanValue();
        Element isExecuteOnServerelement = (Element) xmlElement.selectSingleNode("isExcuteOnServer");
	    executeOnServer = new Boolean(isExecuteOnServerelement.attributeValue("value")).booleanValue();
        fileType=xmlElement.elementText("fileType");
		file=new File(xmlElement.elementText("file"));
	}

	protected TypeInfo getTypeInfo()
	{
		return AuxFile.TYPE_INFO;
	}

	public String getXmlTag()
	{
		return AuxFile.XML_TAG;
	}

	//generates xml for auxfile
	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
  //      xml.addElement("shouldUpload").addAttribute("value",new Boolean(isShouldUpload()).toString());
        xml.addElement("isMainModelFile").addAttribute("value",new Boolean(isMainModelFile()).toString());
        xml.addElement("isExcuteOnServer").addAttribute("value",new Boolean(isExecuteOnServer()).toString());
        xml.addElement("fileType").addText(fileType);
        xml.addElement("file").addText(file.getPath());
		return xml;
	}

	// ModelComponent interface
	public Model getModel()
	{
		if (model != null) {
			return model.getModel();
		} else
			return null;
	}

	public String toString()
	{
		return getName() + "\nfile locate at:" + file.getPath();
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

  //  public boolean isShouldUpload() {
 //       return shouldUpload;
  //  }

  //  public void setShouldUpload(boolean shouldUpload) {
  //      this.shouldUpload = shouldUpload;
  //  }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

     public boolean isExecuteOnServer() {
        return executeOnServer;
    }

    public void setExecuteOnServer(boolean executeOnServer) {
        this.executeOnServer = executeOnServer;
    }


    public boolean isMainModelFile() {
        return isMainModelFile;
    }

    public void setMainModelFile(boolean mainModelFile) {
        isMainModelFile = mainModelFile;
    }
}
