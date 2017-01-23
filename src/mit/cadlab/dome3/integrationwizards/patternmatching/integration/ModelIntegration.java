package mit.cadlab.dome3.integrationwizards.patternmatching.integration;

import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.ProjectTemplate;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.MappedModels;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.MappedParameter;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.SubscribedModel;

import java.util.*;

import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ParameterPair;
import mit.cadlab.dome3.integrationwizards.directmethod.SelectMapping;
import mit.cadlab.dome3.integrationwizards.automatedmapping.AutoMap;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;
import mit.cadlab.dome3.search.framework.templatemanagement.TemplateRegistry;
import mit.cadlab.dome3.search.framework.templatemanagement.Template;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 12, 2007
 * Time: 2:21:24 PM
 * To change this template use Options | File Templates.
 */
public class ModelIntegration {

    private AutoMap autoMap;
    private ArrayList projectTemplates;
    private ArrayList objectiveModels;
    private ArrayList acceptedProjects;
    private MappingMatrix finalMappingMatrix;
    private ArrayList unmappedObjectiveModels;

    public ModelIntegration(AutoMap autoMap, ArrayList objectiveModels, ArrayList integrationTemplates)
    {
        this.autoMap = autoMap;
        this.projectTemplates = integrationTemplates;
        this.objectiveModels = objectiveModels;
        this.acceptedProjects = new ArrayList();
        this.finalMappingMatrix = new MappingMatrix(objectiveModels.size());
        this.unmappedObjectiveModels = new ArrayList();
    }

    public ArrayList integrateModels(){
        ArrayList possibleProjects = findProjectMatches();
        if(possibleProjects.size()==1)
        {
            this.acceptedProjects = possibleProjects;
            return this.acceptedProjects;
        }
        else if(possibleProjects.size()>1)
        {
            processProjectMatches(possibleProjects);
            return this.acceptedProjects;
        }
        else
            return null;
    }

    public MappingMatrix mapProjects(ArrayList mapProjects){
        for(int projectIndex=0;projectIndex<mapProjects.size();projectIndex++)
            inputProjectMappings((ProjectMatching)mapProjects.get(projectIndex));
        return finalMappingMatrix;
    }

    //Method identifies integration project matches based on matched objective models and whether the objective model
    //contains the parameters which were matched to the mapped parameters in the project template
    private ArrayList findProjectMatches(){
        ArrayList possibleProjects = new ArrayList();
        for(int projectIndex=0;projectIndex<projectTemplates.size();projectIndex++)
        {
            ProjectTemplate template = (ProjectTemplate)projectTemplates.get(projectIndex);
            ArrayList modelPairs = matchSubscribedTemplates(template);
            if(modelPairs!=null){
                ProjectMatching project = new ProjectMatching(modelPairs,template.copy(),(ArrayList)objectiveModels.clone());
                project.matchProject();
                ArrayList models = project.getVerifiedModels();
                if(models.size()>1)
                    possibleProjects.add(project);
                else
                    template.reset();
            }
        }
        return possibleProjects;
    }

    private ArrayList matchSubscribedTemplates(ProjectTemplate projectTemplate){
        ArrayList templates = new ArrayList();
        ArrayList modelPairs = new ArrayList();
        ArrayList subscriptions = projectTemplate.getSubscriptions();
        for(int subIndex=0;subIndex<subscriptions.size();subIndex++){
            String id = ((SubscribedModel)subscriptions.get(subIndex)).getModelID();
            Template temp = TemplateRegistry.getTemplate(id);
            if(temp!=null)
                templates.add(temp);
        }
        if(templates!=null || templates.size()>1){
            modelPairs = autoMap.patternMatchingAlgorithm(templates);
            if(modelPairs.size()!=0)
                return modelPairs;
        }
        return null;
    }


    //If multiple projects were identified. This method only allows mulitple projects if they do not contain the same
    //verified subscribed models. If there is overlap, the project which contains more verified subscribed models is kept
    private void processProjectMatches(ArrayList projects){
        ArrayList models;
        ArrayList checkModels;
        ProjectMatching project;
        ProjectMatching checkProject;
        int numProjects = projects.size();

        //First for loop increments through the basis projects
        for(int projIndex=0;projIndex<numProjects;projIndex++)
        {
            project = (ProjectMatching)projects.get(projIndex);
            models = project.getVerifiedModels();
            //Second for loop increments through the projects to test if the basis projects
            //models are contained within the test project
            for(int projIndex2=(projIndex+1);projIndex2<numProjects;projIndex2++)
            {
                checkProject = (ProjectMatching)projects.get(projIndex2);
                //Third for loop increments through the models of the basis project to
                //check if they reside in the test project
                for(int modelIndex=0;modelIndex<models.size();modelIndex++)
                {
                    //modelID = (String)models.get(modelIndex);
                    checkModels = checkProject.getVerifiedModels();
                    boolean overlap = checkModels.contains(models.get(modelIndex));
                    //if the modelID exists delete the project which contains fewer verified subscribed models
                    if(overlap && models.size()>checkModels.size())
                        projects.remove(projIndex2);
                    else if(overlap && models.size()<checkModels.size())
                        projects.remove(projIndex);
                }
            }
            //If the project survived, input the mapping into the global matrix
            if(project == projects.get(projIndex))
                 acceptedProjects.add(project);
        }
    }


    //todo: try to clean this up
    private void inputProjectMappings(ProjectMatching project)
    {
        ProjectTemplate projectTemplate = project.getTemplate();
        ArrayList finalMappedModels = projectTemplate.getFinalMappedModelPairs();
        ArrayList modelPairs = project.getMatchedModelPairs();
        MatchedModelPair pairA = null;
        MatchedModelPair pairB = null;
        String parameterAName = null;
        String parameterBName = null;
        FuzzyAttributedNode nodeA = null;
        FuzzyAttributedNode nodeB = null;
        FuzzyARG objectiveModel;

        for(int mapIndex=0;mapIndex<finalMappedModels.size();mapIndex++)
        {
            MappedModels mappedModels = (MappedModels)finalMappedModels.get(mapIndex);
            //Select a single mapped pair of subscription models
            HashMap mappings = mappedModels.getMappings();
            Set modelOneParameters = mappings.keySet();
            //Find the matched objective/template model pair which corresponds to each of the
            //mapped subscription models in the current mapped pair of subscription models
            for(int matchIndex=0;matchIndex<modelPairs.size();matchIndex++){
                MatchedModelPair matchedModels = ((MatchedModelPair)modelPairs.get(matchIndex));
                String templateID = matchedModels.getTemplateModel().id;
                if(templateID.equals(mappedModels.modelOneID))
                    pairA = matchedModels;
                else if(templateID.equals(mappedModels.modelTwoID))
                    pairB = matchedModels;
            }
            //Determine the index for each objectiveModel, used when inputing mapping into the mappedModels matrix
            int indexA=-1;
            int indexB=-1;
            for(int objIndex=0;objIndex<objectiveModels.size();objIndex++){
                objectiveModel = (FuzzyARG)objectiveModels.get(objIndex);
                if(pairA.getObjectiveModel()==objectiveModel)
                    indexA = objIndex;
                else if(pairB.getObjectiveModel()==objectiveModel)
                    indexB = objIndex;
                else
                    unmappedObjectiveModels.add(objectiveModel);
            }

            int columnIndex=-1;
            int rowIndex=-1;
            for(Iterator mapIterator=modelOneParameters.iterator();mapIterator.hasNext();){
                MappedParameter modelOneParameter = (MappedParameter)mapIterator.next();
                parameterAName = modelOneParameter.getName();
                parameterBName = ((MappedParameter)mappings.get(modelOneParameter)).getName();

                if(indexA<indexB)
                {
                    columnIndex = indexA;
                    rowIndex = indexB;
                    nodeA = getModelNode(pairA, parameterAName);
                    nodeB = getModelNode(pairB, parameterBName);
                }
                else if(indexA>indexB)
                {
                    nodeA = getModelNode(pairB, parameterBName);
                    nodeB = getModelNode(pairA, parameterAName);
                    columnIndex = indexB;
                    rowIndex = indexA;
                }

                if(nodeA!=null && nodeB!=null)
                {
                    ParameterPair pair = null;
                    String inoutA = (String)nodeA.getInputoutput().getSampleSet().iterator().next();
                    String inoutB = (String)nodeB.getInputoutput().getSampleSet().iterator().next();
                    if(inoutA==inoutB && inoutA=="input")
                        pair = new ParameterPair(nodeA,nodeB,1, "input");
                    else if(inoutA=="input" && inoutB=="output")
                        pair = new ParameterPair(nodeA,nodeB,1,"output");
                    else if(inoutA=="output" && inoutB=="input")
                        pair = new ParameterPair(nodeA,nodeB,1,"output");
                    if(pair!=null)
                    {
                        pair.finalize();
                        finalMappingMatrix = SelectMapping.insertPairInToFinalMatrix(finalMappingMatrix,pair,columnIndex,rowIndex);
                    }
                }
            }
        }
    }

    private FuzzyAttributedNode getModelNode(MatchedModelPair pair, String name)
    {
        if(pair!=null&&name!=null)
        {
            ArrayList matchedPairIndicies = pair.getMatchedParameters();
            for(int pairIndex=0;pairIndex<matchedPairIndicies.size();pairIndex++)
            {
                int tempNodeIndex = ((Integer)((ArrayList)matchedPairIndicies.get(pairIndex)).get(0)).intValue();
                Set nodeName = ((FuzzyAttributedNode)pair.getTemplateModel().getNode(tempNodeIndex)).getName().getSampleSet();
                for(Iterator iterator=nodeName.iterator();iterator.hasNext();)
                {
                    //if any of the names in the node's name fuzzyset equals the subscribed parameters name return TRUE
                    if(((String)iterator.next()).equalsIgnoreCase(name)){
                        int objNodeIndex = ((Integer)((ArrayList)matchedPairIndicies.get(pairIndex)).get(1)).intValue();
                        return (FuzzyAttributedNode)pair.getObjectiveModel().getNode(objNodeIndex);
                    }
                }
            }
        }
        return null;
    }

    public ArrayList getUnmappedModels()
    {
        return unmappedObjectiveModels;
    }
}
