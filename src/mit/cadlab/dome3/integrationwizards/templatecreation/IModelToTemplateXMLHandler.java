package mit.cadlab.dome3.integrationwizards.templatecreation;

import mit.cadlab.dome3.integrationwizards.mappingstorage.IModelData;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.ProjectTemplateRegistry;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.framework.templatemanagement.Template;
import mit.cadlab.dome3.search.framework.templatemanagement.TemplateRegistry;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.util.ArrayList;
import java.util.HashMap;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 13, 2007
 * Time: 2:33:59 PM
 * To change this template use Options | File Templates.
 */
public class IModelToTemplateXMLHandler {

    public static void loadIModel(String iModelXMLString,String projectXMLString) throws Exception
    {
        Element projectXMLElement = XMLUtils.stringToXmlElement(projectXMLString);
        String projectName = projectXMLElement.attributeValue("name");
        //HashMap resourceIds = DomeProjectXMLHandler.loadProjectModel(projectXMLElement);

        DomeiModelResourceXMLHandler handler = new DomeiModelResourceXMLHandler();
        {
            IModelData iModel = handler.getiModelData(iModelXMLString);
            ArrayList models = iModel.getObjectiveModels();
            for(int modelIndex=0;modelIndex<models.size();modelIndex++)
            {
                FuzzyARG graph = (FuzzyARG)models.get(modelIndex);
                Template template = new Template(graph,graph.name,graph.id);
                TemplateRegistry.saveTemplate(template);
            }
            ProjectTemplateRegistry.createTemplate(iModelXMLString,projectName);

        }
    }


    //test imodel loader
    public static void main(String args[])
    {
        String userdir = System.getProperty("user.dir");
        //loadIModel(userdir + "\\testmodels\\Bergey-IMODEL.dml");
    }
}
