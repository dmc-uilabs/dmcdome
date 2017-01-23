// DomeXmlData.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util.xml;

import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.CommonAuxFile;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;

import org.dom4j.Element;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;

public class DomeXmlData {
    public static final String MODEL = "model";
    private static final String MODEL_INFO = "modelinfo";

    public static final String PROJECT = "project";
    private static final String PROJECT_INFO = "projectinfo";

    public static final String INTERFACE = "modelinterface";
    private static final String INTERFACE_INFO = "interfaceinfo";

    public static final String PROJECTINTERFACE = "modelinterface";

    public static final String PLAYSPACE = "playspace";
    private static final String PLAYSPACE_INFO = "playspaceinfo";

    protected String objectType;
    protected String xmlDescription;

    protected String name;
    protected String id;
    protected Version version;

    protected String modelType;
    protected Vector modelAuxFiles;
    protected Vector customGuiFiles;

    // for analysis tool
    protected String _toolProjectPath = "";

    public DomeXmlData(String objectType, String xmlDescription)
    {
        this.objectType = objectType;
        this.xmlDescription = xmlDescription;

        // sanity check arguments
        if (!(objectType.equals(MODEL) || objectType.equals(PROJECT) ||
                objectType.equals(INTERFACE) || objectType.equals(PLAYSPACE) ||
                    objectType.equals(PROJECTINTERFACE)))
                        throw new IllegalArgumentException("unsupported object type: " + objectType);
        if (xmlDescription == null)
            throw new IllegalArgumentException("null xml description");
        Element xmlElement = XMLUtils.stringToXmlElement(xmlDescription);
        if (xmlElement == null)
            throw new IllegalArgumentException("invalid xml description");

        // parse XML for information
        if (xmlElement.getQName().getName().equals(objectType))
        {
            id = xmlElement.attributeValue("id");
            if (id == null)
                throw new IllegalArgumentException(objectType + " - no xml id");
            name = xmlElement.attributeValue("name");
            if (name == null)
                throw new IllegalArgumentException(objectType + " - no xml name");
            String objectInfoTag;
            if (objectType.equals(MODEL))
                objectInfoTag = MODEL_INFO;
            else if (objectType.equals(PROJECT))
                objectInfoTag = PROJECT_INFO;
            else if (objectType.equals(PLAYSPACE))
                objectInfoTag = PLAYSPACE_INFO;
            else
                objectInfoTag = INTERFACE_INFO;
            Element versionXml = (Element) xmlElement.selectSingleNode("/" + objectType + "/" + objectInfoTag + "/version");
            if (versionXml == null)
            {
                System.out.println(name + " - no xml version");
            }
            else
            {
                version = new Version(versionXml);
            }
            if (objectType.equals(MODEL))
            {
                try
                {
                    String type = xmlElement.attributeValue("type");
                    if (type == null)
                        throw new IllegalArgumentException(objectType + " - no model type");
                    if (type.equals(DomeModel.TYPE_INFO.getXmlType()))
                        modelType = DbConstants.MODEL_TYPE_DOME;
                    else if (type.equals(PluginModel.TYPE_INFO.getXmlType()))
                    {
                        modelType = xmlElement.attributeValue("pluginType");
                    }
                    else if (type.equals(AnalysisTool.TYPE_INFO.getXmlType()))
                    {
                        modelType = xmlElement.attributeValue("toolType");
                        Element projectPathXml = (Element) xmlElement.selectSingleNode("/" + objectType + "/" + objectInfoTag + "/project");
                        if(projectPathXml == null)
                        {
                            System.out.println(name + " - no project path");
                        }
                        else
                            _toolProjectPath = projectPathXml.getText();

                    }
                    else
                        throw new IllegalArgumentException(objectType + " - unknown type: " + type);
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException(objectType + " - no model type");
                }
                //add for auxiliary file
                modelAuxFiles = new Vector();
                // read auxFiles
                List auxFiles = xmlElement.selectNodes("/" + objectType + "/auxfiles/" + AuxFile.XML_TAG);
                for (Iterator iter = auxFiles.iterator(); iter.hasNext();)
                {
                    Element element = (Element) iter.next();
                    if (element != null)
                    {//loadAuxFiles;
                        modelAuxFiles.add(new CommonAuxFile(element));
                    }
                }

            }
            else if (objectType.equals(INTERFACE))
            {
                /*
                 * if the interface is a tool interface than the type of tool must be explicitly declared
                 * this is done in the following line - see below
                 */

                String interfaceType = xmlElement.attributeValue("type");
                if (interfaceType.equals(ToolInterface.TYPE_INFO.getXmlType()))
                    modelType = xmlElement.attributeValue("toolType");
                customGuiFiles = new Vector();
                // read customGuiFiles
                List customFiles = xmlElement.selectNodes("/" + objectType + "/customGUIs/" + CustomGuiInfo.XML_TAG);
                for (Iterator iter = customFiles.iterator(); iter.hasNext();)
                {
                    Element element = (Element) iter.next();
                    if (element != null)
                    {//loadcustomGuiFiles;
                        customGuiFiles.add(new CustomGuiInfo(element));
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(objectType + " - illegal xmlElement: " + xmlDescription);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version.toString();
    }

    public String getModelType() {
        return modelType;
    }

    public String getXmlDescription() {
        return xmlDescription;
    }

    public String getToolProjectPath()
    {
        return _toolProjectPath;
    }

    public String toString() {
        return name + " v" + version + ": " + id;
    }

    //Qing: add here for Auxiliary file of model
    public Vector getModelAuxFiles() {
        return modelAuxFiles;
    }

    public Vector getInterfaceCustomGuiFiles() {
        return customGuiFiles;
    }




}
