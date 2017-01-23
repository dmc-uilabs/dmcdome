package mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 22, 2007
 * Time: 3:59:32 PM
 * To change this template use Options | File Templates.
 */
public class MappedModels {
    public String modelOneID;
    public String modelTwoID;
    private HashMap parameterMappings;
    private String XML_TAG = "modelMappings";

    public MappedModels(String modelOneId, String modelTwoId)
    {
        this.modelOneID = modelOneId;
        this.modelTwoID = modelTwoId;
        this.parameterMappings = new HashMap();
    }

    //Mappings stored in a HashMap, Map key is modelOne's parameter, Map value is modelTwo's parameter
    public void addMapping(String paramOneName, String paramOneID, String paramOneModelID,String paramTwoName, String paramTwoID, String paramTwoModelID)
    {
        MappedParameter parameterOne = new MappedParameter(paramOneName,paramOneID,paramOneModelID);
        MappedParameter parameterTwo = new MappedParameter(paramTwoName,paramTwoID,paramTwoModelID);
        if(paramOneModelID.equals(modelOneID) && paramTwoModelID.equals(modelTwoID))
            parameterMappings.put(parameterOne,parameterTwo);
        else if(paramOneModelID.equals(modelTwoID) && paramTwoModelID.equals(modelOneID))
            parameterMappings.put(parameterTwo,parameterOne);
    }

    public HashMap getMappings()
    {
        return parameterMappings;
    }

    public void setMappings(HashMap mappedParameters)
    {
        parameterMappings = mappedParameters;
    }

    public String getMatchedModelID(String modelID)
    {
        if(modelID.equals(modelOneID))
            return modelTwoID;
        else if(modelID.equals(modelTwoID))
            return modelOneID;
        else
            return null;
    }

    public boolean mapsModels(String modelOneId, String modelTwoId)
    {
        if(modelOneId.equals(modelOneID) && modelTwoId.equals(modelTwoID))
            return true;
        else if(modelOneId.equals(modelTwoID) && modelTwoId.equals(modelOneID))
            return true;
        else
            return false;
    }

    public boolean maps(String modelID)
    {
        if(modelID.equals(modelOneID) || modelID.equals(modelTwoID))
            return true;
        else
            return false;
    }

    public boolean firstModel(String modelID)
    {
        if(modelID.equals(modelOneID))
            return true;
        else
            return false;
    }

    //Method used when creating a project template
    public Element toXmlElement()
    {
        Element xml = DocumentHelper.createElement(XML_TAG);
        xml.addAttribute("fistModelID",modelOneID);
        xml.addAttribute("secondModelID",modelTwoID);
        Set modelOneParameters = parameterMappings.keySet();
        for(Iterator iterator=modelOneParameters.iterator();iterator.hasNext();)
        {
            Element mappedParameter;
            MappedParameter paramOne = (MappedParameter)iterator.next();
            MappedParameter paramTwo = (MappedParameter)parameterMappings.get(paramOne);
            mappedParameter = paramOne.toXMLElement("mappedParameter");
            mappedParameter.add(paramTwo.toXMLElement("parameter"));
            xml.add(mappedParameter);
        }
        return xml;
    }

}
