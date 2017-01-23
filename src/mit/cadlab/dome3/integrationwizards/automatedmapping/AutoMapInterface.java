package mit.cadlab.dome3.integrationwizards.automatedmapping;

import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 26, 2007
 * Time: 1:44:52 PM
 * Interface for Automated Mapping. The AutoMap constructor requires an ArrayList of "objective models" which are
 * models to be mapped together and must be represented by the FuzzyARG class. Models which are represented
 * differently will need to be translated into a FuzzyARG.
 */
public interface AutoMapInterface {

    //Resets the AutoMap class for a new round of objectiveModels while keeping the templates loaded
    public void newObjectiveModels(ArrayList objectiveModels);

    //This method begins the integration classification method. If an integration project is found
    //it will return the list of possible projects. It will not implement the mappings.
    //Note: The loadTemplates() method must be called before this method
    public ArrayList integrationMatching();

    //This method uses a pattern matching algorithm to compare model graph
    //Note: This is called within the integrationMatching() method.
    //The loadTemplates() method must be called first
    public ArrayList patternMatchingAlgorithm(ArrayList templates);

    //This method should be called after the integrationMatching method. This allows users to choose
    //which project template to accept. An ArrayList of accepted projects is the input to this method.
    public MappingMatrix integrationMapping(ArrayList userAcceptedProjects);

    /*The direct method compares model parameters directly and attempts to intelligently map them together.
      This method requires two inputs one which is a tolerance level. The level can range from 1-4
      1 being the strictest tolerance on parameter similarity matching. The other determines whether it should
      clear the mapping matrix and start fresh or if it is adding mappings on top of the classification mappings*/
    public MappingMatrix directMethod(int toleranceLevel,ArrayList models,MappingMatrix mappingMatrix);

    //If not all of the objective models were matching to templates in the integration model template,
    //All of the remaining objective models are contained in this ArrayList and can be used in the direct method
    public ArrayList getUnmappedModels();

    //Loads both the model templates and integration model templates. Returns true if templates successfully load
    public boolean loadTemplates();

}
