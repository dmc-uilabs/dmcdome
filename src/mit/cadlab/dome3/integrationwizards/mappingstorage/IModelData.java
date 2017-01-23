package mit.cadlab.dome3.integrationwizards.mappingstorage;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Sep 28, 2006
 * Time: 10:19:11 AM
 * To change this template use Options | File Templates.
 */
public class IModelData {
    private ArrayList objectiveModels;
    private ArrayList iModelParameters;


    public IModelData()
    {
        objectiveModels = new ArrayList();
        iModelParameters = new ArrayList();
    }

    public IModelData(ArrayList objModels,ArrayList imodelParameters)
    {
        this.objectiveModels = objModels;
        this.iModelParameters = imodelParameters;
    }

    public ArrayList getObjectiveModels()
    {
        return objectiveModels;
    }

    public ArrayList getiModelParameters()
    {
        return iModelParameters;
    }

    public void addObjectiveMode(FuzzyARG objectiveModel)
    {
        objectiveModels.add(objectiveModel);
    }

    public FuzzyARG getObjectiveModel(int index)
    {
        return (FuzzyARG)objectiveModels.get(index);
    }
}
