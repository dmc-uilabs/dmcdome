package mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement;

import java.util.ArrayList;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 8, 2007
 * Time: 1:44:18 PM
 * Class to house project data to input into a project template
 */
public class ProjectTemplate {
    private String id;
    public String name;
    private int matchCount = 0;   //used during matching
    private ArrayList subscribedModels;
    private ArrayList mappedModelPairs;
    private ArrayList finalMappedModelPairs = new ArrayList();

    public ProjectTemplate(String projectID, String projectName)
    {
        this.id = projectID;
        this.name = projectName;
        this.subscribedModels = new ArrayList();
        this.mappedModelPairs = new ArrayList();
    }

    public ProjectTemplate(String projectID, String projectName,ArrayList modelPairs,ArrayList subscribedModels){
        this.id = projectID;
        this.name = projectName;
        this.subscribedModels = subscribedModels;
        this.mappedModelPairs = modelPairs;
    }

    public void addSubscription(SubscribedModel model){
        subscribedModels.add(model);
    }

    public void addFinalMappedModels(MappedModels mappedModels){
        finalMappedModelPairs.add(mappedModels);
    }

    public SubscribedModel getSubscribedModel(String modelID)
    {
        SubscribedModel model = null;
        for(int i=0;i<subscribedModels.size();i++)
        {
            model = (SubscribedModel)subscribedModels.get(i);
            if(model.getModelID()==modelID)
                break;
        }
        return model;
    }


    public void addMapping(String firstName, String firstID, String secondName, String secondID, String firstModelID, String secondModelID)
    {
        MappedModels modelMap = null;
        //If this is the first model pair, create a new model mapping pair
        if(mappedModelPairs.size()==0)
        {
            modelMap = new MappedModels(firstModelID, secondModelID);
            modelMap.addMapping(firstName,firstID,firstModelID,secondName,secondID,secondModelID);
            mappedModelPairs.add(modelMap);
        }
        else
        {
            //find a model mapping pair which maps together these two models
            for(int pairIndex=0;pairIndex<mappedModelPairs.size();pairIndex++)
            {
                MappedModels modelCheck = (MappedModels)mappedModelPairs.get(pairIndex);
                if(modelCheck.mapsModels(firstModelID,secondModelID))
                {
                    modelCheck.addMapping(firstName,firstID,firstModelID,secondName,secondID,secondModelID);
                    mappedModelPairs.set(pairIndex,modelCheck);
                    break;
                }
                //if one does not exist, create a new one
                else if(pairIndex == (mappedModelPairs.size()-1))
                {
                    modelMap = new MappedModels(firstModelID, secondModelID);
                    modelMap.addMapping(firstName,firstID,firstModelID,secondName,secondID,secondModelID);
                    mappedModelPairs.add(modelMap);
                    break;
                }
            }
        }
    }

    public void setSubscriptionChecked(String modelID)
    {
        SubscribedModel model;
        for(int subIndex=0;subIndex<subscribedModels.size();subIndex++)
        {
            model = (SubscribedModel)subscribedModels.get(subIndex);
            if(model.getModelID().equalsIgnoreCase(modelID))
            {
                model.setChecked();
                subscribedModels.set(subIndex,model);
                break;
            }
        }
    }

    public boolean subscriptionStatus(String modelID)
    {
        SubscribedModel model;
        for(int subIndex=0;subIndex<subscribedModels.size();subIndex++)
        {
            model = (SubscribedModel)subscribedModels.get(subIndex);
            if(model.getModelID().equalsIgnoreCase(modelID))
                return model.checked();
        }
        return false;
    }


    //Returns all of the mapped subscription pairs that include a given subscribed model
    public ArrayList getModelMappings(String modelID)
    {
        ArrayList modelMappings = new ArrayList();
        MappedModels mappedModels;

        for(int mapIndex=0;mapIndex<mappedModelPairs.size();mapIndex++)
        {
            mappedModels = (MappedModels)mappedModelPairs.get(mapIndex);
            if(mappedModels.maps(modelID))
                modelMappings.add(mappedModels);
        }
        return modelMappings;
    }

    public Element toXmlElement()
    {
        Element xml = DocumentHelper.createElement("ProjectTemplate");
        xml.addElement("id").addText(id);
        xml.addElement("name").addText(name);
        Element subscriptions = DocumentHelper.createElement("subscriptions");
        for(int i=0;i<subscribedModels.size();i++)
        {
            SubscribedModel model = (SubscribedModel)subscribedModels.get(i);
            subscriptions.add(model.toXMLElement());
        }
        xml.add(subscriptions);
        Element mapping = DocumentHelper.createElement("mappings");
        for (int k=0;k<mappedModelPairs.size();k++)
        {
            MappedModels map = (MappedModels)mappedModelPairs.get(k);
            mapping.add(map.toXmlElement());
        }
        xml.add(mapping);

        return xml;
    }

    public void setMatchCount(int count){
        matchCount = count;
    }

    public String getID(){
        return id;
    }

    public int getMatchCount(){
        return matchCount;
    }

    public ArrayList getFinalMappedModelPairs()
    {
        if(finalMappedModelPairs.size()!=0)
            return finalMappedModelPairs;
        else
            return null;
    }

    public boolean finalMappingsContains(MappedModels mappedModels){
        if(finalMappedModelPairs.size()==0)
            return false;
        else
            return finalMappedModelPairs.contains(mappedModels);
    }

    public ArrayList getSubscriptions(){
        return subscribedModels;
    }

    public void reset(){
        finalMappedModelPairs = new ArrayList();
        for(int subIndex=0;subIndex<subscribedModels.size();subIndex++){
            SubscribedModel model = (SubscribedModel)subscribedModels.get(subIndex);
            model.reset();
        }
    }

    public ProjectTemplate copy(){
        ProjectTemplate temp = new ProjectTemplate(id,name,mappedModelPairs,(ArrayList)subscribedModels.clone());
        temp.reset();
        return temp;
    }
}
