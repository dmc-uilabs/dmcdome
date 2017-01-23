package mit.cadlab.dome3.integrationwizards.templatecreation;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.*;

import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.ProjectTemplate;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.SubscribedModel;
import mit.cadlab.dome3.search.framework.utils.processing.FileHandlerException;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Nov 28, 2005
 * Time: 11:41:09 PM
 * Class which parses iModel XML files to be project template files
 */
public class DomeiModelXMLHandler {

    public static final String ProjectModel = "model";
    private ProjectTemplate data = null;

    public ProjectTemplate getProjectData(String xmlContent,String projectName) throws IOException {
        ProjectTemplate projectData = data;
        projectData = loadProjectModel(xmlContent,projectName);
        return projectData;
    }

    public ProjectTemplate loadProjectModel(String xmlString,String projectName) throws IOException {
        // read from file : in this case, model name might not be available.
        ProjectTemplate template = null;
        Element xmlElement;
        xmlElement = XMLUtils.stringToXmlElement(xmlString);
        if (xmlElement!=null)
            template = loadProjectModel(xmlElement,projectName);
       //data.setLocation(file.getAbsolutePath());
        return template;
    }


    public ProjectTemplate loadProjectModel(Element projectXML, String projectName) throws IOException
    {
        // parse XML for information
        ProjectTemplate projectData;
        String projectId = ""; //build id, to be used when locating the project
        String name;
        try  {name = projectXML.getQName().getName();}
        catch (Exception e)
        {return null;}

        if (name.equals(ProjectModel)) {
            projectId = projectXML.attributeValue("id");
            if (projectId == null)
                throw new IllegalArgumentException(" - no xml id");
            data = new ProjectTemplate(projectId, projectName);

            //Record the basic data for each subscribed model
            List subXML = projectXML.selectNodes("/model/subscriptions/subscription");
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
            List subsXML = projectXML.selectNodes("/model/mappings/modelMappings/mappedParameter");
            String mappedParameterID;
            String mappedParameterModelID;
            String parameterID;
            String parameterModelID;
            HashMap parameterMap = new HashMap();
            for (Iterator iterator = subsXML.iterator();iterator.hasNext();)
            {
                Element mappingElement = (Element) iterator.next();
                XMLUtils.makeRootElement(mappingElement);
                mappedParameterID = mappingElement.attributeValue("idRef");
                mappedParameterModelID = mappingElement.attributeValue("idModelRef");
                List parameterList = mappingElement.selectNodes("/mappedParameter/parameter");
                Iterator iterate = parameterList.iterator();
                Element parameterElement = (Element) iterate.next();
                parameterID = parameterElement.attributeValue("idRef");
                parameterModelID = parameterElement.attributeValue("idModelRef");
                //Only consider mapping between parameters which are not in procedural relations
                if(mappingElement.attributeValue("idRelationRef")==null && parameterElement.attributeValue("idRelationRef")==null)
                    parameterMap = loadMapping(mappedParameterID, mappedParameterModelID, parameterID, parameterModelID, parameterMap);
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

    private HashMap loadMapping(String mapParamID, String mapParamModelID,String paramID, String paramModelID, HashMap paramMap)
    {
        SubscribedModel mapModel;
        SubscribedModel paramModel;
        //If both parameters are model parameters, directly map them
        if(mapParamModelID==null && paramModelID==null)
        {
            String paramModelId = getParamModelID(paramID);
            String mapParamModelId = getParamModelID(mapParamID);
            mapModel = data.getSubscribedModel(mapParamModelId);
            paramModel = data.getSubscribedModel(paramModelId);
            String mapParamName = mapModel.getParameterName(mapParamID);
            String paramName = paramModel.getParameterName(paramID);
            data.addMapping(mapParamName,mapParamID,paramName,paramID,mapParamModelId,paramModelId);
        }
        //If one of the parameters is not a model parameter, you must determine which other model parameters
        //they might map to, otherwise retain the information for a later model parameter mapping
        else if(mapParamModelID!=null)
        {
            if(paramMap.containsKey(mapParamID))
                mapParameters(paramMap, mapParamID, paramID);
            else
                paramMap.put(mapParamID,paramID);
        }
        else if(paramModelID!=null)
        {
            if(paramMap.containsKey(paramID))
                mapParameters(paramMap, paramID, mapParamID);
            else
                paramMap.put(paramID,mapParamID);
        }
        return paramMap;

    }

    private void mapParameters(HashMap paramMap, String mapParamID, String paramID)
    {
        String previousParamID = (String)paramMap.get(mapParamID);
        String previousParamModelID = getParamModelID(previousParamID);
        String paramModelID = getParamModelID(paramID);
        SubscribedModel paramModel = data.getSubscribedModel(paramModelID);
        SubscribedModel previousParamModel = data.getSubscribedModel(previousParamModelID);
        String paramName = paramModel.getParameterName(paramID);
        String previousParamName = previousParamModel.getParameterName(previousParamID);
        data.addMapping(previousParamName,previousParamID,paramName,paramID,previousParamModelID,paramModelID);
    }

    private String getParamModelID(String paramID)
    {
        String modelID = null;
        ArrayList subscriptions = data.getSubscriptions();
        for(int i=0;i<subscriptions.size();i++)
        {
            SubscribedModel model = (SubscribedModel)subscriptions.get(i);
            if (model.getParameterName(paramID)!=null)
            {
                modelID = model.getModelID();
                break;
            }
        }
        return modelID;
    }
}



