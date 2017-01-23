package mit.cadlab.dome3.integrationwizards.imodelwizard;

import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.integrationwizards.modeltranslators.DomeModelTranslator;
import mit.cadlab.dome3.integrationwizards.modeltranslators.DirectedGraphTranslator;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.MatchedModelPair;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Mar 6, 2007
 * Time: 2:46:50 PM
 * To change this template use Options | File Templates.
 */
public class SkeletonResource {

    private String resourceId;
    private Collection interfaces;
    private ArrayList interfaceGraphs;
    private ArrayList matchedInterfacePairs;
    private MatchedModelPair matchedPair;

    public SkeletonResource(String id,Collection interfaces){
        this.resourceId = id;
        this.interfaces = interfaces;
    }

    public void translateInterfaces(){
        interfaceGraphs = new ArrayList();
        BrowseInterface iface;
        DirectedGraph dGraph;
        for(Iterator iterator=interfaces.iterator();iterator.hasNext();){
            iface = (BrowseInterface)iterator.next();
            if(iface.getParentId().equals(resourceId)){
                dGraph = iface.getInterface().getInterfaceGraph();
                interfaceGraphs.add(DirectedGraphTranslator.translateDirectedGraph(dGraph,iface.getName(),iface.getInterfaceId()));
            }
        }
    }

    public void addMatchedInterfaces(ArrayList modelPairs){
        matchedInterfacePairs = modelPairs;
    }

    public boolean graphWasMatched(FuzzyARG graph){
        for(int pairIndex=0;pairIndex<matchedInterfacePairs.size();pairIndex++){
            MatchedModelPair pair = (MatchedModelPair)matchedInterfacePairs.get(pairIndex);
            if(pair.getObjectiveModel().equals(graph)){
                matchedPair = pair;
                return true;
            }
        }
        return false;
    }

    public MatchedModelPair getMatchedPair(){
        return matchedPair;
    }

    public ArrayList getMatchedInterfaces(){
        return matchedInterfacePairs;
    }

    public ArrayList getGraphs(){
        return interfaceGraphs;
    }

    public Collection getInterfaces(){
        return interfaces;
    }

    public String getId(){
        return resourceId;
    }
}
