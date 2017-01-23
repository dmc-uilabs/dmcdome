package mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement;

import java.util.*;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 9, 2007
 * Time: 10:37:45 AM
 * Class which contains a simple model definition with it's Name and id
 * Intended for translating parameter id's to parameter names
 */
public class SubscribedModel {
    private String modelName = null;
    private String modelID;
    private boolean checked;
    private HashMap modelParameters = new HashMap();
    private static final String XML_TAG = "subscription";

    public SubscribedModel(String modelName, String modelID){
        this.modelName = modelName;
        this.modelID = modelID;
        this.checked = false;
    }

    public void addParameter(String parameterName, String parameterID){
        modelParameters.put(parameterID, parameterName);
    }

    public String getParameterName(String parameterID){
        return (String)modelParameters.get(parameterID);
    }

    public String getModelName(){
        return modelName;
    }

    public String getModelID(){
        return modelID;
    }

    public void setChecked(){
        checked = true;
    }

    public boolean checked(){
        return checked;
    }

    public void reset(){
        checked = false;
    }

    public Element toXMLElement(){
        Element xml = DocumentHelper.createElement(XML_TAG);
        xml.addAttribute("name", modelName);
        xml.addAttribute("id",modelID);
        Element parameters = DocumentHelper.createElement("parameters");
        Object[] modelNames = (modelParameters.values()).toArray();
        Object[] modelIDs = modelParameters.keySet().toArray();
        for(int i=0;i<modelNames.length;i++)
        {
            Element parameter = DocumentHelper.createElement("parameter");
            parameter.addAttribute("id",(String)modelIDs[i]);
            parameter.addAttribute("name", (String)modelNames[i]);
            parameters.add(parameter);
        }
        xml.add(parameters);
        return xml;
    }

}
