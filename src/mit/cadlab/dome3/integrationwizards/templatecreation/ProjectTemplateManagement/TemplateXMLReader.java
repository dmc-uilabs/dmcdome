package mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement;

import mit.cadlab.dome3.search.framework.utils.processing.FileHandlerException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 10, 2007
 * Time: 2:49:30 PM
 * To change this template use Options | File Templates.
 */
public class TemplateXMLReader {

        public static final String Template = "ProjectTemplate";
        private ProjectTemplate data = null;

        public ProjectTemplate getTemplateData(File f) throws FileHandlerException {
            ProjectTemplate projectData = data;
            try {
                projectData = loadTemplate(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return projectData;
        }

        public ProjectTemplate loadTemplate(File file) throws IOException {
            // read from file : in this case, model name might not be available.
            if (!file.exists())
                return null;
            String xmlString = FileUtils.readTextFileAsString(file);
            Element xmlElement = XMLUtils.stringToXmlElement(xmlString);
            loadTemplate(xmlElement);
           //data.setLocation(file.getAbsolutePath());
            return data;
        }


        public ProjectTemplate loadTemplate(Element projectXML) throws IOException {
            // parse XML for information
            ProjectTemplate projectData;
            String projectName = "";
            String projectId = ""; //build id, to be used when locating the project

            if (projectXML.getQName().getName().equals(Template))
            {
                projectId = projectXML.elementText("id");
                projectName = projectXML.elementText("name");
                data = new ProjectTemplate(projectId, projectName);

                //Record the basic data for each subscribed model
                List subXML = projectXML.selectNodes("/ProjectTemplate/subscriptions/subscription");
                String modelName;
                String modelID;
                for (Iterator iterator=subXML.iterator();iterator.hasNext();)
                {

                    Element subscriptionElement = (Element) iterator.next();
                    XMLUtils.makeRootElement(subscriptionElement);
                    modelName = subscriptionElement.attributeValue("name");
                    modelID = subscriptionElement.attributeValue("id");
                    SubscribedModel model = new SubscribedModel(modelName,modelID);
                    if (projectId == null)
                        throw new IllegalArgumentException(" - no xml id");
                    List paramXML = subscriptionElement.selectNodes("/subscription/parameters/parameter");
                    model = loadParameters(paramXML, model);
                    data.addSubscription(model);
                }

                //Record the mappings between the subscribed models
                List subsXML = projectXML.selectNodes("/ProjectTemplate/mappings/modelMappings/mappedParameter");
                String mapParamID;
                String mapParamName;
                String mapParamModelID;
                String paramID;
                String paramName;
                String paramModelID;
                for (Iterator iterator = subsXML.iterator();iterator.hasNext();)
                {
                    Element mappingElement = (Element) iterator.next();
                    XMLUtils.makeRootElement(mappingElement);
                    mapParamID = mappingElement.attributeValue("id");
                    mapParamName = mappingElement.attributeValue("name");
                    mapParamModelID = mappingElement.attributeValue("modelID");
                    List parameterList = mappingElement.selectNodes("/mappedParameter/parameter");
                    Iterator iterate = parameterList.iterator();
                    Element parameterElement = (Element) iterate.next();
                    paramID = parameterElement.attributeValue("id");
                    paramName = parameterElement.attributeValue("name");
                    paramModelID = parameterElement.attributeValue("modelID");
                    data.addMapping(mapParamName,mapParamID,paramName,paramID,mapParamModelID,paramModelID);
                }
                projectData = data;
                return projectData;
            }
            return null;
        }

        private SubscribedModel loadParameters(List paramXML, SubscribedModel model)
        {
            for (Iterator iterator = paramXML.iterator(); iterator.hasNext();)
            {
                Element paramElement = (Element) iterator.next();
                XMLUtils.makeRootElement(paramElement);
                String paramId = paramElement.attributeValue("id");
                String paramName = paramElement.attributeValue("name");
                model.addParameter(paramName, paramId);
            }
            return model;
        }
}
