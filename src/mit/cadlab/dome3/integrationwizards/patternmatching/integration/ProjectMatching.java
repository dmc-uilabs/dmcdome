package mit.cadlab.dome3.integrationwizards.patternmatching.integration;

import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.*;

import java.util.*;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 12, 2007
 * Time: 10:43:27 AM
 * To change this template use Options | File Templates.
 */
public class ProjectMatching {

    private ArrayList matchedModelPairs;        //List of matched model pairs from pattern matching
    private ArrayList matchedTemplates = new ArrayList();   //List of just the templates matched during pattern matching
    private ArrayList verifiedModels;       //Subscribed template models which were matched and successfully map with other template/objective model pairs
    private ArrayList matchedSubscriptions; //Subscribed template models which were matched (only)
    private ArrayList objectiveModels;
    private ProjectTemplate template;
    private JToggleButton toggleButton;

    public ProjectMatching(ArrayList matchedModelPairs, ProjectTemplate projectTemplate,ArrayList objModels)
    {
        this.matchedModelPairs = (ArrayList)matchedModelPairs.clone();
        this.template = projectTemplate;
        this.objectiveModels = objModels;
        this.verifiedModels = new ArrayList();
        this.matchedSubscriptions = new ArrayList();
        for(int modelIndex=0;modelIndex<this.matchedModelPairs.size();modelIndex++)
            matchedTemplates.add(((MatchedModelPair)this.matchedModelPairs.get(modelIndex)).getTemplateModel());
    }

    public boolean matchProject()
    {
        ArrayList subscriptions = template.getSubscriptions();  //This arraylist contains all of the subscribed models in a template template
        SubscribedModel model;          //This is a subscribed model in a template template
        String subscriptionID;          //The subscribed model id

        for(int subIndex=0;subIndex<subscriptions.size();subIndex++)
        {
            model = (SubscribedModel)subscriptions.get(subIndex);
            subscriptionID = model.getModelID();
            //If the subscribed template model was matched to an objective model, find which models it maps to
            if(subscriptionMatched(subscriptionID) && !verifiedModels.contains(subscriptionID))
            {
                verifyMappedSubscriptions(subscriptionID);
            }
        }
        return false;
    }


    //Recursive method which determines if a mapped subscription can be mapped
    //It checks if the model was matched to an objective model and if the mapped parameters were matched
    private void verifyMappedSubscriptions(String subscriptionID)
    {
        ArrayList modelMappings = template.getModelMappings(subscriptionID);

        for(int mappingIndex=0;mappingIndex<modelMappings.size();mappingIndex++)
        {
            MappedModels mappedModels = (MappedModels)modelMappings.get(mappingIndex);
            String mappedModelID = mappedModels.getMatchedModelID(subscriptionID);

            if(subscriptionMatched(mappedModelID) && !verifiedModels.contains(subscriptionID))
            {
                boolean verified = false;
                HashMap mappedParameters = mappedModels.getMappings();
                Set mappings = mappedParameters.entrySet();
                MappedParameter mappedParameter;
                MappedParameter parameter;
                for(Iterator iterator=mappings.iterator();iterator.hasNext();)
                {
                    Map.Entry map = (Map.Entry)iterator.next();
                    if(mappedModels.firstModel(subscriptionID))
                    {
                        parameter = (MappedParameter)map.getKey();
                        mappedParameter = (MappedParameter)map.getValue();
                    }
                    else
                    {
                        parameter = (MappedParameter)map.getValue();
                        mappedParameter = (MappedParameter)map.getKey();
                    }

                    boolean parameterMatched = verifyMappedParameter(parameter.getName(),subscriptionID);
                    boolean mappedParameterMatched = verifyMappedParameter(mappedParameter.getName(),mappedModelID);

                    //If the subscription parameter and mapped model parameter were both matched during pattern matching
                    //then accept that mappedModels and verify the other mappings in the mapped model
                    if(parameterMatched && mappedParameterMatched)
                        verified = true;
                    else
                        iterator.remove();
                }
                if(verified)
                {
                    mappedModels.setMappings(mappedParameters);
                    template.addFinalMappedModels(mappedModels);
                    if(!verifiedModels.contains(subscriptionID))
                        verifiedModels.add(subscriptionID);
                    if(!verifiedModels.contains(mappedModelID))
                    {
                        verifiedModels.add(mappedModelID);
                        verifyMappedSubscriptions(mappedModelID);
                    }
                }
            }
        }
    }


    //Returns TRUE if the subscribed template model was matched to an objective model
    private boolean subscriptionMatched(String modelID)
    {
        if(template.subscriptionStatus(modelID))
        {
            if(matchedSubscriptions.contains(modelID))
                return true;
            return false;
        }

        for (int matchIndex=0;matchIndex<matchedTemplates.size();matchIndex++)
        {
            FuzzyARG matchedTemplateModel = (FuzzyARG)matchedTemplates.get(matchIndex);
            if(matchedTemplateModel.id.equalsIgnoreCase(modelID))
            {
                //sets the subscription as checked and removes the matched template so it isn't searched again
                template.setSubscriptionChecked(modelID);
                matchedTemplates.remove(matchedTemplateModel);
                matchedSubscriptions.add(modelID);
                return true;
            }
        }
        template.setSubscriptionChecked(modelID);
        return false;
    }


    //Returns TRUE if a parameter which is used to map two subscription templates together
    //was matched to an objective model parameter during pattern matching
    private boolean verifyMappedParameter(String parameterName, String modelID)
    {
        FuzzyARG template;
        String templateID;
        MatchedModelPair match;
        //Search the templates to find the correct model match
        for(int modelIndex=0;modelIndex<matchedModelPairs.size();modelIndex++)
        {
            match = (MatchedModelPair)matchedModelPairs.get(modelIndex);
            template = match.getTemplateModel();
            templateID = template.id;
            if(templateID.equals(modelID))
            {
                ArrayList parameterPairs = match.getMatchedParameters();
                //Search the matched parameters to determine if template parameter was matched
                for(int pairIndex=0;pairIndex<parameterPairs.size();pairIndex++)
                {
                    int tempNodeIndex = ((Integer)((ArrayList)parameterPairs.get(pairIndex)).get(0)).intValue();
                    Set nodeName = ((FuzzyAttributedNode)template.getNode(tempNodeIndex)).getName().getSampleSet();
                    for(Iterator iterator=nodeName.iterator();iterator.hasNext();)
                    {
                        //if any of the names in the node's name fuzzyset equals the subscribed parameters name return TRUE
                        if(((String)iterator.next()).equalsIgnoreCase(parameterName));
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList getVerifiedModels()
    {
        return verifiedModels;
    }

    public ProjectTemplate getTemplate()
    {
        return template;
    }

    public void addCheckBox(JToggleButton box){
        this.toggleButton = box;
    }

    public ArrayList getMatchedModelPairs(){
        return matchedModelPairs;
    }

    public ArrayList getObjectiveModels(){
        return objectiveModels;
    }

    public ArrayList getModelPairs(){
        return matchedModelPairs;
    }

    public boolean checkBoxStatus(){
        if(toggleButton!=null)
            return toggleButton.isSelected();
        return false;
    }

}
