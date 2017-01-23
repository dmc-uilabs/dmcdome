package mit.cadlab.dome3.integrationwizards.automatedmapping;



import java.util.ArrayList;

import mit.cadlab.dome3.integrationwizards.directmethod.DirectMethod;
import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.integrationwizards.patternmatching.GraphMatchPair;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.MatchedModelPair;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.ModelIntegration;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.framework.templatemanagement.TemplateRegistry;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.ProjectTemplateRegistry;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Aug 23, 2006
 * Time: 3:27:23 PM
 * To change this template use Options | File Templates.
 */
public class AutoMap implements AutoMapInterface
{
	private static ArrayList templateModels;
    private static ArrayList projectTemplates;
	private static ArrayList objectiveModels;
    private ArrayList unmappedModels;
    private ModelIntegration integration;
    private boolean templatesLoaded;

    public AutoMap(){
        this.templatesLoaded = false;
    }

    //Starts the classiciation model integration algorithm
    public ArrayList integrationMatching(){
        if(templatesLoaded){
            integration = new ModelIntegration(this,objectiveModels,projectTemplates);
            ArrayList acceptedProjects = integration.integrateModels();
            return acceptedProjects;
        }
        else
            OneButton1Msg.showError(null, "Error: Automated Mapping Error" ,"Templates not Loaded, Automated Integration Terminated."
                    , "Ok", OneButton1Msg.DEFAULT_SIZE);
        return null;
    }

	public ArrayList patternMatchingAlgorithm(ArrayList templates){
        ArrayList modelPairs = new ArrayList();
        FuzzyARG objectiveModel = new FuzzyARG();
        int numObjectiveModels = objectiveModels.size();
        //Loop through all of the objective models
        for (int modelIndex=0;modelIndex<numObjectiveModels;modelIndex++)
        {
            objectiveModel = (FuzzyARG)objectiveModels.get(modelIndex);
            GraphMatchPair graphMatch = new GraphMatchPair(templates,objectiveModel);
            if (graphMatch.matched)
                modelPairs.add(new MatchedModelPair(objectiveModel,graphMatch.getMatchedModel(),graphMatch.getNodePairings()));
        }
        return modelPairs;
	}

    public MappingMatrix integrationMapping(ArrayList userAcceptedProjects){
        MappingMatrix mappingMatrix = integration.mapProjects(userAcceptedProjects);
        unmappedModels = integration.getUnmappedModels();
        return mappingMatrix;
    }

    //Direct Method Tolerance can range from 1-4 1 being the strictest tolerance on parameter similarity matching
    public MappingMatrix directMethod(int toleranceLevel,ArrayList directObjectiveModels,MappingMatrix currentMappingMatrix){
        MappingMatrix directMatrix = DirectMethod.directMethod(directObjectiveModels, toleranceLevel);
        if(currentMappingMatrix==null)
            return directMatrix;
        return insertDirectMatrix(currentMappingMatrix,directMatrix,directObjectiveModels);
    }

    public void newObjectiveModels(ArrayList objectiveModels){
        this.objectiveModels = objectiveModels;
        this.integration =  null;
    }

    public ArrayList getUnmappedModels(){
        return unmappedModels;
    }

    public boolean loadTemplates(){
        if(templatesLoaded)
            return true;

        TemplateRegistry registry = new TemplateRegistry();
        if(!TemplateRegistry.templatesLoaded())
            registry.loadTemplates();
        templateModels = registry.getAllTemplates();

        ProjectTemplateRegistry projectRegistry = new ProjectTemplateRegistry();
        if(!ProjectTemplateRegistry.templatesLoaded())
            projectRegistry.loadTemplates();
        projectTemplates = projectRegistry.getTemplates();

        if(templateModels!=null && projectTemplates!=null){
            if(templateModels.size()>0 && projectTemplates.size()>0){
                templatesLoaded = true;
                return true;
            }
        }
        return false;
    }

    private MappingMatrix insertDirectMatrix(MappingMatrix completeMatrix,MappingMatrix directMatrix,ArrayList directModels){
        //iterates through all of the direct matrix entries, determines the model index in the complete matrix
        //and inputs the mappings into the complete matrix
        int completeFirstIndex;
        int completeSecondIndex;
        for (int firstIndex=0;firstIndex<directModels.size();firstIndex++)
        {
        	for (int secondIndex=(firstIndex+1);secondIndex<directModels.size();secondIndex++){
                completeFirstIndex = objectiveModels.indexOf(directModels.get(firstIndex));
                completeSecondIndex = objectiveModels.indexOf(directModels.get(secondIndex));
                completeMatrix.setEntry(completeFirstIndex,completeSecondIndex,directMatrix.getEntry(firstIndex,secondIndex));
            }
        }
        return completeMatrix;
    }
}
