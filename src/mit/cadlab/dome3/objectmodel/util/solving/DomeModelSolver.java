// DomeModelSolver.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.config.DomeOptions;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.mapping.ParameterMapping;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.FormatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class DomeModelSolver extends AbstractModelSolver {
    protected static final String MAPPINGS_GRAPH = "mappings_graph";

    protected DomeModelRuntime model;
    protected DomeModelParameterChangeTracker tracker;
    protected HashMap subParamMap = new HashMap(); // key is subParam; value is subIfaceParam
    protected DirectedGraph mappingsGraph;
    protected HashMap modelGraphs = new HashMap(); // key is model/relation, value is graph

    public DomeModelSolver(DomeModelRuntime model, DomeModelParameterChangeTracker tracker) {
        this.model = model;
        this.tracker = tracker;
        registerRelationGraphs();
        if (model.isIntegrationModel())
            addSubscriptionGraph();
        createMappingsGraph();
//qing change Dec 21th, move here instead of inside getNextRelation to fire
// graphSolver = new DirectedGraphSolver(relationGraphs, externalGraphs);
    }

    public void cleanup() {
        mappingsGraph.cleanup();
        mappingsGraph = null;
        Iterator mGraphs = modelGraphs.values().iterator();
        while (mGraphs.hasNext()) {
            ((DirectedGraph) mGraphs.next()).cleanup();
        }
        modelGraphs.clear();
        if (graphSolver != null)
            graphSolver.cleanup();
    }

    protected DomeModelBase getModel() {
        return model;
    }

    protected AbstractGraphSolver createGraphSolver() {
        return new DirectedGraphSolver(model.getName(), modelGraphs, externalGraphs, getMappingsGraph());
    }

    protected void registerRelationGraphs() {
        Iterator it = model.getModelObjects().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof ProceduralRelation) {
                ProceduralRelation rel = (ProceduralRelation) obj;
                DependencyInfo dInfo = rel.getDependencyInfo();
                if (dInfo != null && !dInfo.isEmpty()) {
                    modelGraphs.put(rel, new DirectedGraph(dInfo));
                }
                // todo register listener on changes to dependency info
            }
        }
    }

    protected void addSubscriptionGraph() {
        Iterator it = model.getSubscriptions().iterator();
        while (it.hasNext()) {
            DefaultSubscription sub = (DefaultSubscription) it.next();
            DirectedGraph subGraph = sub.getGraph();
            SubscriptionInterface subIface = sub.getInterface();
            if (subIface != null) {
                HashMap subParamToSubIfaceParamMap = new HashMap();
                List subNodes = new ArrayList(subGraph.getNodes()); // make a copy so removing nodes will be ok
                Iterator mObjs = subNodes.iterator();
                Object obj;
                while (mObjs.hasNext()) {
                    obj = mObjs.next();
                    if (obj instanceof Parameter) {
                        subParamToSubIfaceParamMap.put(obj, sub.getInterfaceParameter((Parameter) obj));
                    } else {
                        subGraph.removeNode(obj);
                    }
                }
                subGraph = new DirectedGraph(subGraph, subParamToSubIfaceParamMap);
                subParamMap.putAll(subParamToSubIfaceParamMap);
            }
//            // mapping between subscription parameters
//            ConnectionMappingManager mgr = model.getMappingManager();
//            Collection subParams = sub.getModelObjects();
//
//            for (Iterator it3 = subParams.iterator(); it3.hasNext();) {
//                Parameter subParam = (Parameter) it3.next();
//                Collection subMap = mgr.getMappingsForParameter(subParam);
//                if (!subMap.isEmpty()) {
//                    for (Iterator it4 = subMap.iterator(); it4.hasNext();) {
//                        Object o = it4.next();
//                        if (o instanceof Parameter) { // might be model param, or subscription param
//                            Parameter mappedSubParam = (Parameter) o;
//                            if (!subsGraph.getNodes().contains(mappedSubParam))
//                                subsGraph.addNode(mappedSubParam);
//                            if (Parameters.isSubscriptionInput(subParam)) {
//                                subsGraph.addArc(mappedSubParam, subParam);
//                            } else if (Parameters.isSubscriptionOutput(subParam)) {
//                                subsGraph.addArc(subParam, mappedSubParam);
//                            }
//                        }
//                    }
//                }
//            }
            Object graphKey = sub;
            if (subIface != null)
                graphKey = subIface;
            modelGraphs.put(graphKey, subGraph);
        }
    }

    protected void createMappingsGraph() {
        mappingsGraph = new DirectedGraph();
        ConnectionMappingManager mgr = model.getMappingManager();
        HashMap mappings = mgr.getMappings(model);
        List processedParameters = new ArrayList();

        // traverse the list of mappings
        Iterator objs = mappings.keySet().iterator();
        while (objs.hasNext()) { // todo: rename variables to make them clearer
            // add mapped parameter heading
            Object obj = objs.next();
            if (obj instanceof Parameter) { // might be visualization instead
                Parameter p1 = (Parameter) obj;
                Collection c = ((ParameterMapping) mappings.get(p1)).getMappings();
                if (c.isEmpty())
                    continue;
                mappingsGraph.addNode(p1);
                for (Iterator mappedParams = c.iterator(); mappedParams.hasNext();) {
                    Parameter p2 = (Parameter) mappedParams.next();
                    if (processedParameters.contains(p2))
                        continue; // this mapping has already been processed
                    mappingsGraph.addNode(p2);
                    // figure out directionality of mapping and add the appropriate arc to the mappingsGraph
                    CausalityStatus cs1 = getLocalCausality(p1);
                    CausalityStatus cs2 = getLocalCausality(p2);
                    // todo: change this logic whenever it is possible to map more than one item to an input
                    if (cs1 == null || cs2 == null) {
                        continue; // skip these; error message printed in other method;
                    } else if (cs1 == CausalityStatus.INDEPENDENT &&
                            (cs2 == CausalityStatus.RESULT || cs2 == CausalityStatus.INDETERMINATE)) {
                        mappingsGraph.addArc(p2, p1);
                    } else if (cs2 == CausalityStatus.INDEPENDENT &&
                            (cs1 == CausalityStatus.RESULT || cs1 == CausalityStatus.INDETERMINATE)) {
                        mappingsGraph.addArc(p1, p2);
                    } else if (cs1 == CausalityStatus.RESULT && cs2 == CausalityStatus.INDETERMINATE) {
                        mappingsGraph.addArc(p1, p2);
                    } else if (cs2 == CausalityStatus.RESULT && cs1 == CausalityStatus.INDETERMINATE) {
                        mappingsGraph.addArc(p2, p1);
                    } else {
                        System.err.println("DomeModelSolver.registerMappingsGraph: unable to determine directionality of " +
                                p1.getName() + " (" + cs1 + ") and " + p2.getName() + " (" + cs2 + ") in " + model.getName());
                    }
                }
            }
            processedParameters.add(obj);
        }
    }

    protected DirectedGraph getMappingsGraph() {
        return mappingsGraph;
    }

    /**
     * Returns if the object is a relation/subscription input (INDEPENDENT) or output (RESULT).
     * If it is a model parameter, the CausalityStatus is INDETERMINATE.
     * @param p
     * @return null if local causality of parameter can not be identified; otherwise,
     * returns INDEPENDENT, RESULT, or INDETERMINATE as specified above
     */
    protected CausalityStatus getLocalCausality(Parameter p) {
        if (Parameters.isModelParameter(p))
            return CausalityStatus.INDETERMINATE;
        else if (Parameters.isRelationInput(p) || Parameters.isSubscriptionInput(p))
            return CausalityStatus.INDEPENDENT;
        else if (Parameters.isRelationOutput(p) || Parameters.isSubscriptionOutput(p))
            return CausalityStatus.RESULT;
        else {
            System.err.println("DomeModelSolver.getLocalCausality: unable to calculate localCausality for " +
                    p.getName() + " in " + model.getName());
            return null;
        }
    }

    // todo support other types of relations
    // todo search affected graph only
    /**
     *   Qing-July 31: there is a minor bug here: when you entered the loop, if the first is iteration relation, and its exit condition happen to
     *                 satisfied, the latest version won't propogate status change along the loop.
     * @return Object[] {relation, listOfParamChangesCovered}
     */
    public Object[] getNextRelationToFire() {

        makeConsistent();

        if (tracker.getLoopEntryPoint() != null) {//already in loop
             //find out whether the last executed relation is an iteration relation or not
            Object last_rel = tracker.getLastExecutedRelation();
            if (last_rel instanceof IterationRelation && !((ConditionIterationRelation) last_rel).isInIteration())
                tracker.setexit_loop(true);

            List loopList = getLoopComponents(last_rel);
            //Qing: if the last executed relation is an iteration relation
            // and it is done!, should quit the loop!
            Object[] next_rel_in_loop = getNextReadyRelInLoop(loopList, last_rel);
            if (next_rel_in_loop != null) {
                //some rel ready in loop
                if (!next_rel_in_loop[0].equals(tracker.getLoopEntryPoint()) && !DomeOptions.STOP_INFINITE_LOOPS) { // 20140105 Alex Iankoulski - Added option to stop infinite loops
                    //and still in first round propogate
                    return next_rel_in_loop;
                } else {    //reach the entry point
                    if (tracker.isexit_loop()) {
                        //should stop the change in loop
                        //now if there are changed param belongs to the loop, make them green!
                        if (tracker.hasChangedParametersLeftToProcess()) {
                            List changedParams = tracker.changedRelationParams;
                            for (int i = 0; i < changedParams.size(); i++) {
                                Object obj = changedParams.get(i);
                                if (obj instanceof Parameter) {
                                    //do advanced notification
                                    if (InLoopAsInput((Parameter) obj))
                                        tracker.notifyParamChangeProcessed((Parameter) obj);
                                }
                            }
                        }
                        if (getNextReadyRelOutsideLoop(last_rel) != null) {
                            tracker.setLoopEntryPoint(null);
                            tracker.setexit_loop(false);

                            return getNextReadyRelOutsideLoop(last_rel);
                        } else//nothing good outside the loop
                        {
                            return null;
                        }
                    } else {
                        //if broadcasting,, should find from outside loop
                        if (isBroadCastingEachLoopProperty(loopList)) {
                            //go out loop
                            if (getNextReadyRelOutsideLoop(last_rel) != null) {
                                tracker.setLoopEntryPoint(null);
                                tracker.setexit_loop(false);
                                return getNextReadyRelOutsideLoop(last_rel);
                            } else//nothing good outside the loop
                            {
                                return next_rel_in_loop;//go entry point again...
                            }

                        } else
                        //else stay in loop
                            return next_rel_in_loop;//go entry point again...
                    }
                }
            }
        }
        return getNextRelationToFireNormally();

    }

    /**
     * this method tells whether a param is in a loop and is an input param to a rel/sub
     * @param p
     * @return
     */
    protected boolean InLoopAsInput(Parameter p) {
        //to see in model graph, whether it can reach it self
        if (!getLocalCausality(p).equals(CausalityStatus.INDEPENDENT)) return false;
        if ((graphSolver).getCompleteGraph().getLoopVariables().contains(p)) return true;
        return false;

        /* List relList = getLoopComponents(rel);
        if(relList.contains(p.getScope())&&(Parameters.isRelationInput(p) || Parameters.isSubscriptionInput(p)))
           return true;
        return false; */
    }

    protected List getLoopComponents(Object rel) {
        List relationExecOrder = ((DirectedGraphSolver) graphSolver).getRelationExecutionSequence();

        //the following is to search from the very begining and execute the first ready relation found.

        for (int i = 0; i < relationExecOrder.size(); i++) {
            List relBatch = (List) relationExecOrder.get(i);
            if (relBatch.contains(rel)) //must not in a loop
                return null;
            for (int j = 0; j < relBatch.size(); j++) {
                Object rel_or_relList = relBatch.get(j);
                if (rel_or_relList instanceof List) {
                    List rels_in_loop = (List) rel_or_relList;
                    if (rels_in_loop.contains(rel)) return rels_in_loop;
                }
            }
        }
        return null;
    }


    protected boolean isBroadCastingEachLoopProperty(List loopRelList) {
        for (int i = 0; i < loopRelList.size(); i++) {
            Object rel = loopRelList.get(i);
            if (rel instanceof IterationRelation) {
                return ((IterationRelation) rel).isBroadcasting_eachloop();
            }
        }
        return true;//by default not hang in loops.
    }

    protected Object[] getNextReadyRelInLoop(List rels, Object lastRel) {
        List changedParams = tracker.getChangedRelationParameters();
        List readyChanges = null;

        int index = rels.indexOf(lastRel);
        int entryIndex = rels.indexOf(tracker.getLoopEntryPoint());
        if (index >= entryIndex) {
            //  .....EntryPoint.....lastExecuted....
            for (int i = index + 1; i < rels.size(); i++) {   //from next object to the end
                Object relOrSub = rels.get(i);
                readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(relOrSub), changedParams);
                if (!readyChanges.isEmpty())
                    return new Object[]{relOrSub, readyChanges};
                ;
            }
            //now search from first to the last executed relation
            for (int i = 0; i <= entryIndex; i++) {   //notice including the lastRel
                Object relOrSub = rels.get(i);
                readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(relOrSub), changedParams);
                if (!readyChanges.isEmpty())
                    return new Object[]{relOrSub, readyChanges};
            }
        } else {
            //  ....lastExecuted.....EntryPoint.....
            for (int i = index + 1; i <= entryIndex; i++) {
                Object relOrSub = rels.get(i);
                readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(relOrSub), changedParams);
                if (!readyChanges.isEmpty())
                    return new Object[]{relOrSub, readyChanges};
            }
        }
        //still not return,means no ready change for all relations in the loop
        return null;
    }


    protected Object[] getNextReadyRelOutsideLoop(Object lastRel) {
        //the method is called while lastRel is the entrypoint of a loop
        List changedParams = tracker.getChangedRelationParameters();
        List readyChanges = null;

        List loopList = getLoopComponents(lastRel);

        List relationExecOrder = ((DirectedGraphSolver) graphSolver).getRelationExecutionSequence();
        //the following is to search from the very begining and execute the first ready relation found.

        for (int i = 0; i < relationExecOrder.size(); i++) {
            List relBatch = (List) relationExecOrder.get(i);
            for (int j = 0; j < relBatch.size(); j++) {
                Object rel_or_relList = relBatch.get(j);
                if (rel_or_relList instanceof Relation || rel_or_relList instanceof Subscription) {
                    readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(rel_or_relList), changedParams);
                    if (!readyChanges.isEmpty())
                        return new Object[]{rel_or_relList, readyChanges};
                } else if (rel_or_relList instanceof List) {
                    List rels_in_loop = (List) rel_or_relList;
                    if (!rels_in_loop.contains(lastRel)) {
                        for (int k = 0; k < rels_in_loop.size(); k++) {
                            Object relOrSub = rels_in_loop.get(k);
                            readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(relOrSub), changedParams);
                            if (!readyChanges.isEmpty()) {
                                tracker.setLoopEntryPoint(relOrSub);
                                return new Object[]{relOrSub, readyChanges};
                            }
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    // for now, it fires first relation which is ready to fire
    /**
     *
     * @return Object[] {relation, listOfParamChangesCovered}
     */
    protected Object[] getNextRelationToFireNormally() {
        // create relation graph based on changed parameters
        List changedParams = tracker.getChangedRelationParameters();
        if (changedParams.isEmpty())
            return null;

        List readyChanges = null;
        List relationExecOrder = ((DirectedGraphSolver) graphSolver).getRelationExecutionSequence();

        //the following is to search from the very begining and execute the first ready relation found.

        for (int i = 0; i < relationExecOrder.size(); i++) {
            List relBatch = (List) relationExecOrder.get(i);
            for (int j = 0; j < relBatch.size(); j++) {
                Object rel_or_relList = relBatch.get(j);
                if (rel_or_relList instanceof Relation || rel_or_relList instanceof SubscriptionInterface) {
                    readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(rel_or_relList), changedParams);
                    if (!readyChanges.isEmpty())
                        return new Object[]{rel_or_relList, readyChanges};
                } else if (rel_or_relList instanceof List) {

                    List rels_in_loop = (List) rel_or_relList;
                    for (int k = 0; k < rels_in_loop.size(); k++) {
                        Object obj = rels_in_loop.get(k);
                        if (obj instanceof Relation || obj instanceof SubscriptionInterface) {
                            /* readyChanges = findReadyChanges((DirectedGraph) modelGraphs.get(obj), changedParams);
                             if (!readyChanges.isEmpty()) {
                                 tracker.setLoopEntryPoint(obj);
                                 return new Object[]{obj, readyChanges};
                             } else { //Qing change July 26th, there is  a loop, no parameters in loop is free to change: so we user a driver parameter to start
                                 if (obj instanceof ConditionIterationRelation && !((ConditionIterationRelation) obj).isInIteration()) {
 //criteria: itors are ready to change, other dependent parameters are in the loop
                                     if (changedParams.containsAll(((ConditionIterationRelation) obj).getIteratorItems())) {
                                         tracker.setLoopEntryPoint(obj);
                                         return new Object[]{obj, ((ConditionIterationRelation) obj).getIteratorItems()};
                                     }
                                 }
                             }*/
                            readyChanges = findReadyChangesForLoop((DirectedGraph) modelGraphs.get(obj), changedParams);
                            if (!readyChanges.isEmpty()) {
                                tracker.setLoopEntryPoint(obj);
                                return new Object[]{obj, readyChanges};
                            }
                        } else {
                            Debug.trace(50, "unknown item:" + ClassUtils.getClassName(obj) + "\t" + Names.getName(obj));
                        }
                    }

                } else {
                    return null;
                }
            }
        }
        return null;
    }


    /**
     *
     * @param independentInputSets
     * @param changedParams
     * @return empty list if no changes ready
     */
    protected List findReadyChanges(List independentInputSets, List changedParams) {
        List readyChanges = new ArrayList();
        for (int i = 0; i < independentInputSets.size(); i++) {
            List params = (List) independentInputSets.get(i);
            if (changedParams.containsAll(params)) {
                readyChanges.addAll(params);
            }
        }
        return readyChanges;
    }


    protected List findReadyChanges(DirectedGraph graph, List changedParams) {
        List inputs = graph.getInputs();
        List changedInputs = new ArrayList(DSet.intersection(inputs, changedParams));
        if (changedInputs.isEmpty())
            return Collections.EMPTY_LIST; // nothing has changed
        Collection remainingInputs = DSet.removeSet(inputs, changedInputs);
        boolean waitForMoreChanges = false;
        Parameter input;
        for (Iterator iterator = remainingInputs.iterator(); iterator.hasNext();) {
            input = (Parameter) iterator.next();
            if (Parameter.VALUE_STATUS_INCONSISTENT.equals(input.getValueStatus())) {
                waitForMoreChanges = true;
                break;
            }
        }
        if (waitForMoreChanges)
            return Collections.EMPTY_LIST;
        else
            return changedInputs;
    }

    //qing add July 29
    protected List findReadyChangesForLoop(DirectedGraph graph, List changedParams) {
        List inputs = graph.getInputs();
        List changedInputs = new ArrayList(DSet.intersection(inputs, changedParams));
        if (changedInputs.isEmpty())
            return Collections.EMPTY_LIST; // nothing has changed
        Collection remainingInputs = DSet.removeSet(inputs, changedInputs);
        boolean waitForMoreChanges = false;
        Parameter input;
        for (Iterator iterator = remainingInputs.iterator(); iterator.hasNext();) {
            input = (Parameter) iterator.next();
            if (Parameter.VALUE_STATUS_INCONSISTENT.equals(input.getValueStatus())) {
                if (!InLoopAsInput(input)) {
                    waitForMoreChanges = true;
                    break;
                }
            }
        }
        if (waitForMoreChanges)
            return Collections.EMPTY_LIST;
        else
            return changedInputs;
    }

    public String toString() {
        if (externalGraphs.isEmpty())
            return FormatUtils.mapToString(modelGraphs);
        else {
            makeConsistent();
            return graphSolver.completeGraph.toString();
        }
    }

}

