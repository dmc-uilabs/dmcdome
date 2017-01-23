package mit.cadlab.dome3.integrationwizards.modeltranslators;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyAttributedNode;
import mit.cadlab.dome3.search.datastructure.graph.SimpleAttributedNode;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;

import mit.cadlab.dome3.objectmodel.dataobject.*;
import mit.cadlab.dome3.util.DSet;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 19, 2007
 * Time: 11:55:19 AM
 * To change this template use Options | File Templates.
 */
public class DomeModelTranslator {
    private static DirectedGraph graph;

    public static ArrayList translateDefaultSubscriptions(ArrayList subscriptions)
    {
        ArrayList objectiveModels = new ArrayList();
        DefaultSubscription sub;
        for(int subIndex=0;subIndex<subscriptions.size();subIndex++)
        {
            sub = (DefaultSubscription)subscriptions.get(subIndex);
            objectiveModels.add(translateDirectedGraph(sub.getGraph(),sub.getName(),sub.getResourceId()));
        }

        return objectiveModels;
    }

    public static FuzzyARG translateDirectedGraph(DirectedGraph dGraph,String name,String id)
    {
        FuzzyARG objectiveModel = new FuzzyARG();
        List acceptedParameters = new ArrayList();
        objectiveModel.name = name;
        objectiveModel.id = id;
        graph = dGraph;
        List parameters = graph.getNodes();
        for(Iterator paramIterator=parameters.iterator();paramIterator.hasNext();)
        {
            ConcreteParameter parameter = (ConcreteParameter)paramIterator.next();
            SimpleAttributedNode node = addNode(parameter);
            if(node!=null){
                objectiveModel.addParameterAndNode(new FuzzyAttributedNode(node),parameter);
                acceptedParameters.add(parameter);
            }
        }
        return addArcs(objectiveModel,graph.getArcs(),acceptedParameters);
    }

    private static SimpleAttributedNode addNode(ConcreteParameter parameter)
    {
        SimpleAttributedNode node = null;
        String name = parameter.getName();
        String datatype = parameter.getCurrentType();
        Object data = parameter.getDataObjects().get(0);
        String unit = "null";
        Double dim = null;
        if(data instanceof RealData)
        {
            RealData realData = (RealData)data;
            unit = realData.getUnit().getName();
            dim = realData.getRealValue();
        }
        else if(data instanceof IntegerData)
        {
            IntegerData integerData = (IntegerData)data;
            unit = integerData.getUnit().getName();
            dim = new Double(integerData.getIntegerValue().toString());
        }
        else if(data instanceof DomeVectorData)
        {
            DomeVectorData vectorData = (DomeVectorData)data;
            unit = vectorData.getUnit().getName();
        }
        else if(data instanceof DomeMatrixData)
        {
            DomeMatrixData matrixData = (DomeMatrixData)data;
            unit = matrixData.getUnit().getName();
        }
        else if(data instanceof EnumerationData)
        {
            unit = "NO_UNIT";
        }
        else if(data instanceof FileData)
        {
            FileData fileData = (FileData)data;
            unit = fileData.getFileType();
        }

        if(!unit.equals("null"))
        {
            if(graph.getInputs().contains(parameter))
                node = new SimpleAttributedNode(name,datatype,unit,"input",dim);
            else
                node = new SimpleAttributedNode(name,datatype,unit,"output",dim);
        }
        return node;
    }

    private static FuzzyARG addArcs(FuzzyARG model, Map arcs,List parameters)
    {
        int weights[] = new int[parameters.size()];
        ArrayList outputNodes = new ArrayList();
        Set inputNodes = arcs.keySet();
        //Iterates through each of the input parameter mappings
        for(Iterator inputIterator=inputNodes.iterator();inputIterator.hasNext();){
            ConcreteParameter inputParameter = (ConcreteParameter)inputIterator.next();
            DSet outputs = (DSet)arcs.get(inputParameter);
            int numMappings = outputs.size();
            int inIndex = parameters.indexOf(inputParameter);
            if(inIndex>=0)
            {
                model.updateNodeWeight(inIndex,numMappings);
                FuzzyAttributedNode fuzzyInputNode = (FuzzyAttributedNode)model.getNode(inIndex);

                //Input parameters into the Fuzzy Graph and add the Arcs
                for(int outputIndex=0;outputIndex<numMappings;outputIndex++){
                    ConcreteParameter outputParameter = (ConcreteParameter)outputs.get(outputIndex);
                    int outIndex = parameters.indexOf(outputParameter);
                    FuzzyAttributedNode fuzzyOutputNode = (FuzzyAttributedNode)model.getNode(outIndex);
                    if(outputNodes.contains(fuzzyOutputNode))
                        weights[outputNodes.indexOf(fuzzyOutputNode)] += 1;
                    else{
                        model.addNode(fuzzyOutputNode);
                        outputNodes.add(fuzzyOutputNode);
                        weights[outputNodes.indexOf(fuzzyOutputNode)] = 1;
                    }
                    model.addArc(fuzzyInputNode,fuzzyOutputNode);
                }
            }
        }
        //Update output node weights after counting has been completed
        for(int outputsIndex=0;outputsIndex<outputNodes.size();outputsIndex++)
        {
            int nodeIndex = model.getNodeIndex((FuzzyAttributedNode)outputNodes.get(outputsIndex));
            model.updateNodeWeight(nodeIndex,weights[outputsIndex]);
        }
        return model;
    }


}
