package mit.cadlab.dome3.plugin.catalog.core;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CImplementation {
    private String name;
    private String namespace; // interface name
    private List outputParameterNames;
    private List inputParameterNames;
    private List relationAliases;
    private CNamingService namingService;

    private CInterface targetInterface;

    /* only invoked by CInterface. CInterface.addImplementation() used to instantiate a new CImplementation */
    protected CImplementation(String name, CInterface targetInterface) {
        this.name = name;
        this.inputParameterNames = new ArrayList();
        this.outputParameterNames = new ArrayList();
        this.relationAliases = new ArrayList();
        this.targetInterface = targetInterface;
        this.namespace = targetInterface.getName();
        this.namingService = new CNamingService(this);
    }

    /** to get a reference to the parent interface use getInterface() */
    public CInterface getParentInterface() {
        return targetInterface;
    }

    public String getName() {
        return name;
    }

    /** impl name is a key in CInterface.implementationMap, setting new name */
    public void setName(String name) {
        if (this.name != null && ! this.name.equals(name)) {
            targetInterface.renameImplementation(this.name, name);
        }
        this.name = name;
    }

    public CNamingService getNamingService() {
        return namingService;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getQualifiedName() {
        if ("".equals(namespace) || null == namespace) {
            return name;
        }
        return namespace + "." + name;
    }

    public List getOutputParameterNames() {
        return outputParameterNames;
    }

    public List getOutputParameterList() {
        List ret = new ArrayList();
        for (int i = 0; i < outputParameterNames.size(); i++) {
            String oParamName = (String) outputParameterNames.get(i);
            CInterfaceOutputParameter oParam = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + oParamName);
            ret.add(oParam);
        }
        return ret;
    }

    public List getInputParameterNames() {
        return inputParameterNames;
    }

    public List getInputParameterList() {
        List ret = new ArrayList();
        for (int i = 0; i < inputParameterNames.size(); i++) {
            String iParamName = (String) inputParameterNames.get(i);
            CInterfaceInputParameter iParam = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + iParamName);
            ret.add(iParam);
        }
        return ret;
    }

    /** return a CInterfaceInputParameter instance with the given paramName. the given name should be a local name such as "width"; it should NOT be "itf.width." */
    public CInterfaceInputParameter getInputParameter(String paramName) {
        return getNamingService().getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramName);
    }

    /** return a CInterfaceOutputParameter instance with the given paramName. the given name should be a local name such as "width"; it should NOT be "itf.width." */
    public CInterfaceOutputParameter getOutputParameter(String paramName) {
        return getNamingService().getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + paramName);
    }

    public List getRelationAliases() {
        return relationAliases;
    }

    /** initialize the status of all parameters. the initial status is CConstant.UNASSIGNED_STATUS */
    public void initializeParameterStatus() {
        for (Iterator i = namingService.getParameters().iterator(); i.hasNext(); ) {
            CParameter param = (CParameter) i.next();
            param.setStatus(CConstant.UNASSIGNED_STATUS);
            param.copyCurrentStatusToMappingNodes();
        }
//        for (Iterator i = namingService.findAllMappingNodes().iterator(); i.hasNext(); ) {
//            CMappingNode mappingNode = (CMappingNode) i.next();
//            mappingNode.setStatus(CConstant.UNASSIGNED_STATUS);
//        }
    }

    /**
     * this methods should be invoked after any changes such as adding or removing of parameters to CInterface.
     * for instance, now it is called within CInterface.addImplementation() and InterfaceEditor.updateInterfaceBarAndCModel()
     * it will iterate through current parameters in CInterface, and make this CImplementation instance have the same parameters as CInterface
     */
    public void synchronizeInterfaceParameters() {
        List paramList = targetInterface.getInputParameterList();

        /* check if some of interface params are removed */
        for (Iterator i = inputParameterNames.iterator(); i.hasNext(); ) {
            String curParamName = (String) i.next();
            boolean isFound = false;
            for (Iterator j = paramList.iterator(); j.hasNext(); ) {
                CInterfaceInputParameter iiParam = (CInterfaceInputParameter) j.next();
                if (iiParam.getName().equals(curParamName)) {
                    isFound = true;
                    CInterfaceInputParameter iiParamForImpl = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + curParamName);
                    iiParamForImpl.setDataType(iiParam.getDataType());
                    iiParamForImpl.setUnit(iiParam.getUnit());
                    iiParamForImpl.setDefaultValue(iiParam.getDefaultValue());
                    break;
                }
            }
            /* if not found, remove the ii-param name from the list */
            if (! isFound) {
                /* check this condition so that we are not unregistering CInterfaceOutputParameter with the same name, which occurs when we move some param from output to input */
                if (namingService.getParameter(CConstant.ITF_ALIAS + "." + curParamName) instanceof CInterfaceInputParameter) {
                    namingService.clearMappingScriptsReferencing(CConstant.ITF_ALIAS + "." + curParamName);
                    namingService.unregister(CConstant.ITF_ALIAS + "." + curParamName);
                }
                i.remove();
            }
        }

        /* check if some of interface params are added */
        for (Iterator i = paramList.iterator(); i.hasNext(); ) {
            CInterfaceInputParameter iiParam = (CInterfaceInputParameter) i.next();
            String qualifiedNameForLookup = (CConstant.ITF_ALIAS + "." + iiParam.getName());
            /* check if the qualifiedNameForLookup exists as one of CInterfaceInputParameters */
            if (! namingService.exist(qualifiedNameForLookup) || ! (namingService.getParameter(qualifiedNameForLookup) instanceof CInterfaceInputParameter)) {
                CInterfaceInputParameter iiParamForImpl = new CInterfaceInputParameter(iiParam.getName(), this, namingService);
                //System.out.println("iiparam: " + iiParam);
                //System.out.println("iiparam-impl: " + iiParamForImpl);
                iiParamForImpl.setDataType(iiParam.getDataType());
                iiParamForImpl.setUnit(iiParam.getUnit());
                iiParamForImpl.setDefaultValue(iiParam.getDefaultValue());
                inputParameterNames.add(iiParamForImpl.getName());
                namingService.register(iiParamForImpl.getQualifiedName(), iiParamForImpl);
                //System.out.println("during sync1:" + namingService.getParameters());
            }
        }

        /* below code ensures that the order of param names is the same for interface and implementations */
        inputParameterNames.clear();
        inputParameterNames.addAll(targetInterface.getInputParameterNames());

        paramList = targetInterface.getOutputParameterList();

        /* check if some of interface params are removed */
        for (Iterator i = outputParameterNames.iterator(); i.hasNext(); ) {
            String curParamName = (String) i.next();
            boolean isFound = false;
            for (Iterator j = paramList.iterator(); j.hasNext(); ) {
                CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) j.next();
                if (ioParam.getName().equals(curParamName)) {
                    isFound = true;
                    CInterfaceOutputParameter ioParamForImpl = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + curParamName);
                    ioParamForImpl.setDataType(ioParam.getDataType());
                    ioParamForImpl.setUnit(ioParam.getUnit());
                    ioParamForImpl.setDefaultValue(ioParam.getDefaultValue()); // for file output parameters, default value is used to set default output file name
                    break;
                }
            }
            /* if not found, remove the ii-param name from the list */
            if (! isFound) {
                /* check this condition so that we are not unregistering CInterfaceInputParameter with the same name, which occurs when we move some param from input to output */
                if (namingService.getParameter(CConstant.ITF_ALIAS + "." + curParamName) instanceof CInterfaceOutputParameter) {
                    namingService.clearMappingScriptsReferencing(CConstant.ITF_ALIAS + "." + curParamName);
                    namingService.unregister(CConstant.ITF_ALIAS + "." + curParamName);
                }
                i.remove();
            }
        }

        /* check if some of interface params are added */
        for (Iterator i = paramList.iterator(); i.hasNext(); ) {
            CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) i.next();
            String qualifiedNameForLookup = (CConstant.ITF_ALIAS + "." + ioParam.getName());
            if (! namingService.exist(qualifiedNameForLookup) || ! (namingService.getParameter(qualifiedNameForLookup) instanceof CInterfaceOutputParameter)) {
                CInterfaceOutputParameter ioParamForImpl = new CInterfaceOutputParameter(ioParam.getName(), this, namingService);
                //System.out.println("ioparam: " + ioParam);
                //System.out.println("ioparam-impl: " + ioParamForImpl);
                ioParamForImpl.setDataType(ioParam.getDataType());
                ioParamForImpl.setUnit(ioParam.getUnit());
                ioParamForImpl.setDefaultValue(ioParam.getDefaultValue()); // for file output parameters, default value is used to set default output file name
                outputParameterNames.add(ioParamForImpl.getName());
                namingService.register(ioParamForImpl.getQualifiedName(), ioParamForImpl);
                //System.out.println("during sync2:" + namingService.getParameters());
            }
        }

        /* below code ensures that the order of param names is the same for interface and implementations */
        outputParameterNames.clear();
        outputParameterNames.addAll(targetInterface.getOutputParameterNames());

        //System.out.println("please compare " + outputParameterNames + " vs " + targetInterface.getOutputParameterNames());
        //System.out.println("sync result: input=" + inputParameterNames + ", output=" + outputParameterNames);
    }

    /** sometimes we want to specify relName and relAlias of the created CRelation, such as replacing a relation with a newly created one; however the user should make it sure that the relAlias is unique */
    public CLocalRelation addLocalRelation(String relName, String relAlias) {
        CLocalRelation relation = new CLocalRelation(relName, relAlias, this, namingService);
        namingService.register(relation.getQualifiedName(), relation);
        relationAliases.add(relation.getRelAlias());
        return relation;
    }

    /** relAlias is automatically determined by counting the number of relations in this implementation */
    public CLocalRelation addLocalRelation(String relName) {
        return addLocalRelation(relName, namingService.findRelAliasForNewRelation());
    }

    /** sometimes we want to specify relName and relAlias of the created CRelation, such as replacing a relation with a newly created one; however the user should make it sure that the relAlias is unique */
    public CRemoteRelation addRemoteRelation(String relName, String relAlias) {
        CRemoteRelation relation = new CRemoteRelation(relName, relAlias, this, namingService);
        namingService.register(relation.getQualifiedName(), relation);
        relationAliases.add(relation.getRelAlias());
        return relation;
    }

    /** relAlias is automatically determined by counting the number of relations in this implementation */
    public CRemoteRelation addRemoteRelation(String relName) {
        return addRemoteRelation(relName, namingService.findRelAliasForNewRelation());
    }

    public void removeRelation(String relAlias) {
        namingService.unregister(relAlias); // relation's qualified name is relation's name itself.
        relationAliases.remove(relAlias);
    }

    public String toString() {
        return "[impl:name=" + getQualifiedName() + ", input=" + inputParameterNames + ", output=" + outputParameterNames + ", relations=" + relationAliases + "]";
    }

    /**
     *    X        -> [ Itf   ]  ->     Y      Z
     * -------------------------------------------
     *    X        -> [ Imp   ]  ->     Y      Z
     *                                 [=D]  [=E]
     * --------------------------------------
     *    A    B   -> [ Rel 1 ]  -> C    D
     *  [=X] [=G]
     *    E    F   -> [ Rel 2 ]  -> G
     *  [=C] [=A]
     *
     * [dependency]
     *  Rel 1: A->C, A->D, B->D
     *  Rel 2: E->G, F->G
     *
     *
     * [pseudo code]
     *  1. provided modifiedParamNames { "X" } is given
     *  2. Change the status of itf input param 'X' to GREEN
     *  3. Find all params driven by "X", and turn them into RED status
     *  4. Also find all mapping nodes that are mapped to "X", and turn them into RED status
     *  4.5 Any relation input parameter's mapping which has an empty input param and has RED status is added to the execSeqList
     *  4.6 Any interface output parameter's mapping which has an empty input param and has RED status is added to the execSeqList
     *  5. We create a list called greenList to store GREEN params. modified interface parameters like "X" are placed in the list at the beginning.
     *  5.5. (first execution) find relation input parameters with a mapping that can be executed immediately, and put them into greenList
     *  5.6. (first execution) find interface output parameters with a mapping that can be executed immediately, and put them into greenList
     *  5.7. (commented) // find relation output parameters and turn them into RED status
     *  5.7. (first execution) find all parameters and mapping nodes affected by any of interface input parameters are turned to red. it is because for the first run, all input params are considered as changed params. we may merge this step with 3.
     *  6. We start from the first param in the green list and proceed to the end of the list.
     *  7. An index called greenIdx is used to point current cursor position in the list, and
     *  8. we call the param pointed by greenIdx as curGreenParam.
     *  9. For each curGreenParam we do the followings to propagate its GREEN status to others
     *     1) if any node is mapped to curGreenParam, it should turn into GREEN.
     *     2) after turning any input node to GREEN, check if the change in the input node make the mapping associated with the node executable.
     *        (new in 2006) also check if the change in the interface output node make the mapping associated with the node executable.
     *     3) if the mapping is executable, we the turn output node and a param mapped to the node GREEN.
     *  10. Increase greenIdx to move to the next green param in the green list
     *  11. if greenIdx >= greenList.size(), that means we are at the end of the list.
     *      we can find new GREEN params in the following ways:
     *     1) Iterate through all relations to collect information about
     *        which are executable and how many output params will be determined from the execution
     *     2) if above search gives no executable relation, it is the end of current solving. proceed to check if solving is successful
     *     3) if above search gives many executable relations, we pick one having the most determinable output parameters.
     *        Turn green output parameters of the picked relation, and add those parameters to the end of the green list.
     *        Also, turn green the output parameters' derived parameters, and and add them to green list
     *  12. Now the loop iterating through greenList has finished, it is time we checked if the solving was successful
     *      Iterate through all parameters to see if there are any RED status parameters.
     *  13. If not found, solving is successful.
     *      If so, solving failed. possible reasons: wrong mapping, relation execcution fails
     *
     *  note. Putting them into the list means that it is reserved to propagate its GREEN status
     *
     * @param modifiedParamNames { "X", "Y" }
     * @return
     */
    public List getExecutionSequence(Set modifiedParamNames) {
        List greenList = new ArrayList();
        List execSequenceList = new ArrayList();

        /* if modifiedParamNames is null, regard it as all modified */
        if (modifiedParamNames == null) {
            modifiedParamNames = new HashSet(getInputParameterNames());
        }

        /* add modified param names to the green list after making them qualified names */
        for (Iterator i = modifiedParamNames.iterator(); i.hasNext(); ) {
            String modifiedParamName = (String) i.next();
            String qualifiedParamName = CConstant.ITF_ALIAS + "." + modifiedParamName;

            //speedup Clog.debug("modified name: " + modifiedParamName + " / qualified name: " + qualifiedParamName);
            CInterfaceInputParameter iiParam = namingService.getInterfaceInputParameter(qualifiedParamName);

            /* note that targetInterface.getDriverToDrivenMap() returns unqualified param names */
            Set drivenSetOfItfDefinition = (Set) targetInterface.getDriverToDrivenMap().get(modifiedParamName);
            for (Iterator j = drivenSetOfItfDefinition.iterator(); j.hasNext(); ) {
                String unqualifiedDrivenParamName = (String) j.next();
                String qualifiedDrivenParamName = CConstant.ITF_ALIAS + "." + unqualifiedDrivenParamName;
                namingService.getParameter(qualifiedDrivenParamName).toRedStatus();

                //speedup Clog.debug("turning interface output param affected by modified param to red : " + qualifiedDrivenParamName);

                /* find relation mapping nodes whose mapped parameters is red. then turn those node red */
                Set mappingNodes = namingService.findMappingNodes(qualifiedDrivenParamName);
                for (Iterator k = mappingNodes.iterator(); k.hasNext(); ) {
                    ((CMappingNode) k.next()).toRedStatus();
                }
            }

            /* find drivens of interface input parameters, and turn them into red */
            Set drivenSet = iiParam.getDrivensBy(false);

            /* we also need to find relation mapping nodes mapped to interface input param to turn those node into red.
               this should be done to prevent firing the same mapping many times.
               it keeps all mapping nodes to a unfired parameter in the green list should have red status */
            drivenSet.add(iiParam.getQualifiedName());
            for (Iterator j = drivenSet.iterator(); j.hasNext(); ) {
                String drivenParamName = (String) j.next();
                namingService.getParameter(drivenParamName).toRedStatus();

                /* find relation mapping nodes whose mapped parameters is red. then turn those node red */
                Set mappingNodes = namingService.findMappingNodes(drivenParamName);
                for (Iterator k = mappingNodes.iterator(); k.hasNext(); ) {
                    ((CMappingNode) k.next()).toRedStatus();
                }
            }

            greenList.add(iiParam.getQualifiedName());
            //System.out.println("greenList@4:" + greenList);
            iiParam.toGreenStatus();
        }

        /* 4.5. a mapping which has an empty input param and has red status is added to the execSeqList */
        for (Iterator i = namingService.getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getMapping().getInputNodes().isEmpty() && param.getStatus() == CConstant.RED_STATUS) {
                //greenList.add(param.getQualifiedName());
                execSequenceList.add(param.getMapping());
                //speedup Clog.debug("[param mapping] " + param.getMapping().getOutputNode().getMappedParameterName());
                param.getMapping().getOutputNode().toGreenStatus();
                param.toGreenStatus();
            }
        }

        /* 4.6. a mapping which has an empty input param and has red status is added to the execSeqList */
        for (Iterator i = namingService.getInterfaceOutputParameters().iterator(); i.hasNext();) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) i.next();
            if (param.getMapping().getInputNodes().isEmpty()) {
                //greenList.add(param.getQualifiedName());
                execSequenceList.add(param.getMapping());
                //speedup Clog.debug("[param mapping] " + param.getMapping().getOutputNode().getMappedParameterName());
                param.getMapping().getOutputNode().toGreenStatus();
                param.toGreenStatus();
            }
        }


//        /* do 5.5 */
//        if (isFirstExecution) {
//            /* now it is no more first execution. when one needs reset isFirstExecution, use setIsFirstExecution() method */
//            isFirstExecution = false;
//
//            Set riParams = namingService.getRelationInputParameters();
//            for (Iterator i = riParams.iterator(); i.hasNext();) {
//                CRelationInputParameter param = (CRelationInputParameter) i.next();
//                if (param.getMapping().getInputNodes().isEmpty()) {
//                    //greenList.add(param.getQualifiedName());
//                    execSequenceList.add(param.getMapping());
//                    System.out.println("[param mapping] " + param.getMapping().getOutputNode().getMappedParameterName());
//                    param.getMapping().getOutputNode().toGreenStatus();
//                    param.toGreenStatus();
//                }
//            }
//
//            /* do 5.6 */
//            Set ioParams = namingService.getInterfaceOutputParameters();
//            for (Iterator i = ioParams.iterator(); i.hasNext();) {
//                CInterfaceOutputParameter param = (CInterfaceOutputParameter) i.next();
//                if (param.getMapping().getInputNodes().isEmpty()) {
//                    //greenList.add(param.getQualifiedName());
//                    execSequenceList.add(param.getMapping());
//                    System.out.println("[param mapping] " + param.getMapping().getOutputNode().getMappedParameterName());
//                    param.getMapping().getOutputNode().toGreenStatus();
//                    param.toGreenStatus();
//                } else {
//                    // added apr 2006 //todo: seems unnessary (5/22/2006)
//                    param.toRedStatus();
//                }
//            }
//
////            /* do 5.7 (alternative better way) */
////            /* because for the first execution, all input parameters are considered to have been changed, so we mark all driven params by interface input param as red. also we have to add all interface input param to green list */
////            for (Iterator i = namingService.getInterfaceInputParameters().iterator(); i.hasNext(); ) {
////
////                CInterfaceInputParameter iiParam = (CInterfaceInputParameter) i.next();
////
////                /* find drivens of interface input parameters, and turn them into red */
////                Set drivenSet = iiParam.getDrivensBy(false);
////
////                /* we also need to find relation mapping nodes mapped to interface input param to turn those node into red.
////                   this should be done to prevent firing the same mapping many times.
////                   it keeps all mapping nodes to a unfired parameter in the green list should have red status */
////                drivenSet.add(iiParam.getQualifiedName());
////                for (Iterator j = drivenSet.iterator(); j.hasNext(); ) {
////                    String drivenParamName = (String) j.next();
////                    namingService.getParameter(drivenParamName).toRedStatus();
////
////                    /* find relation mapping nodes whose mapped parameters is red. then turn those node red */
////                    Set mappingNodes = namingService.findMappingNodes(drivenParamName);
////                    for (Iterator k = mappingNodes.iterator(); k.hasNext(); ) {
////                        ((CMappingNode) k.next()).toRedStatus();
////                    }
////                }
////
////                /* for the first run, all interface input params are put in the green list */
////                if (! greenList.contains(iiParam.getQualifiedName())) {
////                    greenList.add(iiParam.getQualifiedName());
////                    iiParam.toGreenStatus();
////                }
////                //System.out.println("greenList@4:" + greenList);
////
////            }
//
////            /* do 5.7 */
////            Set roParams = namingService.getRelationOutputParameters();
////            for (Iterator i = roParams.iterator(); i.hasNext();) {
////                CRelationOutputParameter param = (CRelationOutputParameter) i.next();
////                param.toRedStatus();
////            }
//        }

        for (int greenIdx = 0; greenIdx < greenList.size(); ) {
            String curGreenParamName = (String) greenList.get(greenIdx);

            Set mappingNodesToCurGreenParam = namingService.findMappingNodes(curGreenParamName);

            for (Iterator j = mappingNodesToCurGreenParam.iterator(); j.hasNext(); ) {
                CMappingNode mappingNodeToCurGreenParam = (CMappingNode) j.next();
                /* if any node is mapped to curGreenParam, it should turn GREEN */
                mappingNodeToCurGreenParam.toGreenStatus();

                /* check if CMapping associated with mappingNodeToCurGreenParam is executable
                   we also check if mappingNodeToCurGreenParam is inputNode
                   because only new GREEN input node can make mapping executable.
                   if a mapping is executable, we get more GREEN param, which should be added to the end of the green list */
                CMapping mapping = mappingNodeToCurGreenParam.getMapping();
                if (mappingNodeToCurGreenParam.isInputNode() && mapping.isExecutable()) {
                    String mappedParamName = mapping.getOutputNode().getMappedParameterName();
                    /* put the mapping relation's id to the execution sequence list */
                    //execSequenceList.add("[param mapping] " + mappedParamName);
                    execSequenceList.add(mapping);
                    //speedup Clog.debug("[param mapping] " + mappedParamName);
                    CParameter outputParam = namingService.getParameter(mappedParamName);

                    /* turn green the output node */
                    mapping.getOutputNode().toGreenStatus(); // since we know any output node's status should be the same as its mapped output param, we do not use this status in solving calculation

                    /* turn green the associated parameter, and put it at the end of the green list if the param is CRelationInputParameter. we don't have to */
                    outputParam.toGreenStatus();
                    if (! greenList.contains(mappedParamName)) {
                        /* we don't want a parameter to occur multiple times in the green list */
                        greenList.add(mappedParamName);
                    }
                    //System.out.println("greenList@1:" + greenList);
                }

                if (! mappingNodeToCurGreenParam.isInputNode() && mapping.isExecutable()) {

                }
            }

            /* move to the next param name in green list */
            greenIdx++;

            /* when we reached the end of green list, we look for executable relations */
            if (greenIdx >= greenList.size()) {

                // added apr 2006
                /* before we look for executable relations, we check if we really have to that
                 * because if we already know all interface output parameters, we can stop executing relations */
                Set ioParams = namingService.getInterfaceOutputParameters();
                boolean needToExecuteMoreRelations = false;
                for (Iterator k = ioParams.iterator(); k.hasNext();) {
                    CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) k.next();
                    if (ioParam.getStatus() == CConstant.RED_STATUS) {
                        needToExecuteMoreRelations = true;
                    }
                }

                if (! needToExecuteMoreRelations) {
                    /* if we found that we have made all interface output parameter to the green status, that is the end of the execution */
                    //speedup Clog.debug("Solving succeeded");
                    return execSequenceList;
                } else {
                    /* we keep on looking for executable relations and executing them until we turn all interface output params into green */
                }

                TreeMap executableRelationMap = new TreeMap();
                /* if there is no more in green list find all service relations that are executable */
                Set relations = namingService.getRelations();
                for (Iterator j = relations.iterator(); j.hasNext(); ) {
                    CRelation relation = (CRelation) j.next();
                    Set determinableSet = relation.getDeterminableOutputParametersNames();
                    if (determinableSet.size() > 0) {
                        executableRelationMap.put(new Integer(determinableSet.size()), relation);
                    }
                }

                // System.out.println(executableRelationMap);

                /* if we have found some executable relations */
                if (executableRelationMap.size() > 0) {
                    /* this relation is going to be executed */
                    CRelation maxExecutingRelation = (CRelation) executableRelationMap.get(executableRelationMap.lastKey());

                    /* put the relation's id to the execution sequence list. */
                    //execSequenceList.add("[relation] " + maxExecutingRelation.getQualifiedName());
                    execSequenceList.add(maxExecutingRelation);
                    //speedup Clog.debug("[relation] " + maxExecutingRelation.getQualifiedName());


                    /* turn green its affected output parameters. add those parameters to the end of the green list */
                    Set newGreenParamNamesSet = maxExecutingRelation.getDeterminableOutputParametersNames();
                    for (Iterator j = newGreenParamNamesSet.iterator(); j.hasNext(); ) {
                        String newGreenParamName = maxExecutingRelation.getQualifiedName() + "." + j.next();
                        CRelationOutputParameter roParam = namingService.getRelationOutputParameter(newGreenParamName);
                        /* turn green its affected output parameters. and add them to green list */
                        roParam.toGreenStatus();

                        if (! greenList.contains(roParam.getQualifiedName())) {
                            greenList.add(roParam.getQualifiedName());
                        }

                        //System.out.println("greenList@2:" + greenList);

                        for (Iterator k = roParam.getDerivedParamNames().iterator(); k.hasNext(); ) {
                            /* get dervied param's qualified name */
                            String qualifiedParamName = (String) k.next();

                            /* also turn green its derived parameters. and add them to green list */
                            namingService.getParameter(qualifiedParamName).toGreenStatus();
                            if (! greenList.contains(qualifiedParamName)) {
                                greenList.add(qualifiedParamName);
                            }

                            //System.out.println("greenList@3:" + greenList);
                        }
                    }
                }
            }

            /* if above trial doesn't add any more GREEN params to the green list, it will be the end of for-loop */

        }

        /* after trying to execute relations if still there is no parameter in green list,
           two possibility exist: successfully determined all parameters or solving stucked */
        Set paramSet = namingService.getParameters();
        boolean solvingSucceed = true;
        for (Iterator j = paramSet.iterator(); j.hasNext(); ) {
            CParameter param = (CParameter) j.next();
            if (param.getStatus() == CConstant.RED_STATUS) {
                //speedup Clog.debug("FAIL:" + param);
                solvingSucceed = false;
                //break;
            }
        }
        if (solvingSucceed) {
            //speedup Clog.debug("Solving succeeded");
        } else {
            //speedup Clog.debug("Solving failed");
        }

        return execSequenceList;
    }

    /**
     * @param modifiedParamMap key = parameter name, value = CParameter
     * @return
     */
    public String generateScript(Map modifiedParamMap) {
        String script = "";

        //script = script + generateScriptForVariableDefinition();
        //script = script + generateScriptForModifiedParameters(modifiedParamMap);

        List seqList = getExecutionSequence(modifiedParamMap.keySet());
        for (Iterator i = seqList.iterator(); i.hasNext(); ) {
            String seqName = (String) i.next();
            seqName = seqName.substring(seqName.indexOf("]") + 2);
            int objectType = namingService.getObjectType(seqName);

            if (objectType == CNamingService.REMOTE_RELATION_TYPE) {
                CRelation rel = namingService.getRelation(seqName);
                script = script + rel.getRelationScript() + "\n";
            } else if (objectType == CNamingService.INTERFACE_OUTPUT_PARAMETER_TYPE) {
                CInterfaceOutputParameter ioParam = namingService.getInterfaceOutputParameter(seqName);
                script = script + ioParam.getMapping().getMappingScript() + "\n";
            } else if (objectType == CNamingService.RELATION_INPUT_PARAMETER_TYPE) {
                CRelationInputParameter riParam = namingService.getRelationInputParameter(seqName);
                script = script + riParam.getMapping().getMappingScript() + "\n";
            }
        }
        return script;
    }

//    public String generateScriptForVariableDefinition() {
//        String script = "";
//        for (Iterator i = namingService.getParameters().iterator(); i.hasNext(); ) {
//            CParameter param = (CParameter) i.next();
//            /* create a new runtime context or reuse previous runtime context */
//            /* use groovy API to bind all parameters in the runtime context */
//
//            script = script + "def " + param.getQualifiedName() + ";"; // not this
//        }
//        return script;
//    }
//
//    public String generateScriptForModifiedParameters(Map modifiedParamMap) {
//        String script = "";
//        for (Iterator i = modifiedParamMap.entrySet().iterator(); i.hasNext(); ) {
//            /* use the same context of generateScriptForVariableDefinition()  */
//            /* find a parameter in runtime context binded with given modifiedParamMap's key */
//            /* and assign modified CParameter into it */
//
//            /* another idea: just bind runtime context to Groovy variable
//               and make all parameters should be looked up from the context
//               this solves the name with spaces issue */
//        }
//
//        return "";
//    }

    /** returns local name set of drivers
     * note!!! unlike CInterfaceOutputParameter.getDriversOf(),
     *         it returns local name set, which is unqualified and without "namespace." in front of local name. */
    public Set getDriversOf(String drivenParamName) {
        CInterfaceOutputParameter ioParam = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + drivenParamName);
        if (ioParam != null) {
            Set qualifiedNameSet = ioParam.getDriversOf(true);
            return CNamingService.convertToLocal(qualifiedNameSet);
        } else {
            throw new RuntimeException("no match is found for given param name '" + drivenParamName + "' within interface output parameters");
        }
    }
    /** returns local name set of drivens. please see a note on CImplementation.getDriversOf() */
    public Set getDrivensBy(String driverParamName) {
        CInterfaceInputParameter iiParam = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + driverParamName);
        if (iiParam != null) {
            Set qualifiedNameSet = iiParam.getDrivensBy(true);
            return CNamingService.convertToLocal(qualifiedNameSet);
        } else {
            throw new RuntimeException("no match is found for given param name '" + driverParamName + "' within interface input parameters");
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CImplementation)) return false;

        final CImplementation cImplementation = (CImplementation) o;

        if (!inputParameterNames.equals(cImplementation.inputParameterNames)) return false;
        if (!name.equals(cImplementation.name)) return false;
        if (!namespace.equals(cImplementation.namespace)) return false;
        if (!outputParameterNames.equals(cImplementation.outputParameterNames)) return false;
        if (!relationAliases.equals(cImplementation.relationAliases)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + namespace.hashCode();
        result = 29 * result + outputParameterNames.hashCode();
        result = 29 * result + inputParameterNames.hashCode();
        result = 29 * result + relationAliases.hashCode();
        return result;
    }
}
