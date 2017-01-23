package mit.cadlab.dome3.plugin.catalog.core;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 *
 * Relation, Parameter, Iteration is registered here
 * each Implementation has its own NamingService instance
 */
public class CNamingService {

    public static final int NOT_FOUND_TYPE = -1;
    public static final int LOCAL_RELATION_TYPE = 10;
    public static final int REMOTE_RELATION_TYPE = 11;
    public static final int INTERFACE_INPUT_PARAMETER_TYPE = 12;
    public static final int INTERFACE_OUTPUT_PARAMETER_TYPE = 13;
    public static final int RELATION_INPUT_PARAMETER_TYPE = 14;
    public static final int RELATION_OUTPUT_PARAMETER_TYPE = 15;
    public static final int DERIVED_PARAMETER_TYPE = 16;

    private Map objectMap;
    private CImplementation impl;

    public CNamingService(CImplementation impl) {
        this.objectMap = new HashMap();
        this.impl = impl;
    }

    public CImplementation getImplementation() {
        return impl;
    }

    public Object register(String qualifiedName, Object object) {
        if (exist(qualifiedName)) {
            CLog.info("now overwriting an instance in NamingService: " + qualifiedName);
            unregister(qualifiedName);
        }
        return objectMap.put(qualifiedName, object);
    }

    public Object unregister(String qualifiedName) {
        Object ret = objectMap.remove(qualifiedName);
        if (ret instanceof CRelation) {
            CRelation rel = (CRelation) ret;
            String relAlias = qualifiedName;
            List nameList = null;

            nameList = rel.getInputParameterNames();
            for (int i = 0; i < nameList.size(); i++) {
                objectMap.remove(relAlias + "." + (String) nameList.get(i));
            }
            nameList = rel.getOutputParameterNames();
            for (int i = 0; i < nameList.size(); i++) {
                CRelationOutputParameter roParam = (CRelationOutputParameter) objectMap.remove(relAlias + "." + (String) nameList.get(i));
                if (roParam != null) {
                    List drParamNames = roParam.getDerivedParamNames();
                    for (int j = 0; j < drParamNames.size(); j++) {
                        objectMap.remove(relAlias + "." + roParam.getName() + "." + nameList.get(i));
                    }
                }
            }
        }
        return ret;
    }

    /** When an interface parameter or relation parameter is removed, we should clear all mappings related to the parameter.
     * This method enable us to find all mapping scripts that is referencing a parameter specified by given qualifedParamName and to set the mapping script as an empty string
     * For example, qualifiedParamName itf.width is given, a relation input parameter relA.length has a mapping script of "itf.width + relB.width".
     * By calling this method, the mapping script will be modified into "" because it contains a reference to itf.width.
     * This method is invoked within the removeInputParameter() or removeOutputParameter() of CRelation and synchronizeInterfaceParameters() of CImplementation
     * to ensure that no remaining mapping script references the removed parameter.
     */
    public void clearMappingScriptsReferencing(String qualifiedParamName) {
        Set qualifiedParamNames = new HashSet(1);
        qualifiedParamNames.add(qualifiedParamName);
        Set paramNames = findParametersWhoseMappingScriptReferences(qualifiedParamNames);
        for (Iterator i = paramNames.iterator(); i.hasNext();) {
            String paramName = (String) i.next();
            CParameter param = getParameter(paramName);
            if (param instanceof CInterfaceOutputParameter) {
                ((CInterfaceOutputParameter) param).getMapping().setMappingScript("");
            } else if (param instanceof CRelationInputParameter) {
                ((CRelationInputParameter) param).getMapping().setMappingScript("");
            }
        }
    }

    /**
     * @see this.findParametersWhoseMappingScriptReferences(Set)
     */
    public Set findParametersWhoseMappingScriptReferences(String qualifiedParamName) {
        Set qualifiedParamNames = new HashSet(1);
        qualifiedParamNames.add(qualifiedParamName);
        return findParametersWhoseMappingScriptReferences(qualifiedParamNames);
    }

    /** This method finds a set of CParameter whose mapping references a certain group of parameters.
     * The returned set contains either CRelationInputParameter or CInterfaceOutputParameter.
     * For a detailed explanation, please see the description of clearMappingScriptsReferencing(String qualifiedParamName) */
    public Set findParametersWhoseMappingScriptReferences(Set qualifiedParamNames) {
        Set ret = new HashSet();

        for (Iterator i = qualifiedParamNames.iterator(); i.hasNext();) {
            String qualifiedParamName = (String) i.next();
            if (! this.exist(qualifiedParamName)) {
                throw new RuntimeException("Could not found a parameter named: " + qualifiedParamName);
            }
        }

        for (Iterator i = getParameters().iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            if (param instanceof CRelationInputParameter) {
                CMapping mapping = ((CRelationInputParameter) param).getMapping();
                Set inputNodes = mapping.getInputNodes();
                for (Iterator j = inputNodes.iterator(); j.hasNext();) {
                    String mappedParamName = ((CMappingNode) j.next()).getMappedParameterName();
                    if (qualifiedParamNames.contains(mappedParamName)) {
                        ret.add(param.getQualifiedName());
                        //speedup Clog.debug("[findParametersWhoseMappingScriptReferences] collect " + param.getQualifiedName() + " because its mapping script contains one of " + qualifiedParamNames);
                    }
                }
            } else if (param instanceof CInterfaceOutputParameter) {
                CMapping mapping = ((CInterfaceOutputParameter) param).getMapping();
                Set inputNodes = mapping.getInputNodes();
                for (Iterator j = inputNodes.iterator(); j.hasNext();) {
                    String mappedParamName = ((CMappingNode) j.next()).getMappedParameterName();
                    if (qualifiedParamNames.contains(mappedParamName)) {
                        ret.add(param.getQualifiedName());
                        //speedup Clog.debug("[findParametersWhoseMappingScriptReferences] collect " + param.getQualifiedName() + " because its mapping script contains one of " + qualifiedParamNames);
                    }
                }
            }
        }
        return ret;
    }

    /** This method clears mappings related to a certain group of parameters.
     * For a detailed explanation, please see the description of clearMappingScriptsReferencing(String qualifiedParamName) */
    public void clearMappingScriptsReferencing(Set qualifiedParamNames) {
        Set params = findParametersWhoseMappingScriptReferences(qualifiedParamNames);
        for (Iterator i = params.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            if (param instanceof CInterfaceOutputParameter) {
                ((CInterfaceOutputParameter) param).getMapping().setMappingScript("");
            } else if (param instanceof CRelationInputParameter) {
                ((CRelationInputParameter) param).getMapping().setMappingScript("");
            }
        }
    }

    /** This method removes all mappings made between interface parameters and relation parameters
     * It can be used when one wants to re-generate an interface, and before she re-generates it, she may want to clear all mappings made so far.
     * It ensures that queryXXXX() methods such as queryIndependentParameters() can work as intended.
     * [Warning] As this method clears mappings, the mapping script of a relation input parameter that contains a mapping to interface parameters will be replaced with empty string.
     * [Warning] As this method clears mappings, the mapping script of a interface output parameter that contains a mapping to relation parameters will be replaced with empty string.
     *  */
    public void clearAllMappingsBetweenInterfaceAndRelations() {
        for (Iterator i = getParameters().iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            if (param instanceof CRelationInputParameter) {
                CMapping mapping = ((CRelationInputParameter) param).getMapping();
                Set inputNodes = mapping.getInputNodes();
                for (Iterator j = inputNodes.iterator(); j.hasNext();) {
                    String qualifiedParamName = ((CMappingNode) j.next()).getMappedParameterName();
                    CParameter inputParam = this.getParameter(qualifiedParamName);
                    if (inputParam instanceof CInterfaceInputParameter || inputParam instanceof CInterfaceOutputParameter) {
                        mapping.setMappingScript("");
                        //speedup Clog.debug("[clearAllMappingsBetweenInterfaceAndRelations] cleared the mapping script of " + param.getQualifiedName() + " because its mapping script contains " + qualifiedParamName);
                    }
                }
            } else if (param instanceof CInterfaceOutputParameter) {
                CMapping mapping = ((CInterfaceOutputParameter) param).getMapping();
                Set inputNodes = mapping.getInputNodes();
                for (Iterator j = inputNodes.iterator(); j.hasNext();) {
                    String qualifiedParamName = ((CMappingNode) j.next()).getMappedParameterName();
                    CParameter inputParam = this.getParameter(qualifiedParamName);
                    if (inputParam instanceof CRelationInputParameter || inputParam instanceof CRelationOutputParameter || inputParam instanceof CDerivedParameter) {
                        mapping.setMappingScript("");
                        //speedup Clog.debug("[clearAllMappingsBetweenInterfaceAndRelations] cleared the mapping script of " + param.getQualifiedName() + " because its mapping script contains " + qualifiedParamName);
                    }
                }
            }
        }
    }

    /** collect  relation parameters that has zero drivers and non-zero drivens */
    public Set queryIndependentParameters() {
        Set ret = new HashSet();
        for (Iterator i = getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getDriversOf(false).size() == 0 && param.getDrivensBy(false).size() > 0) {
                ret.add(param);
            }
        }

        for (Iterator i = getRelationOutputParameters().iterator(); i.hasNext();) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            if (param.getDriversOf(false).size() == 0 && param.getDrivensBy(false).size() > 0) {
                ret.add(param);
            }
        }
        return ret;
    }

    /** collect relation parameters that has non-zero drivers and non-zero drivens */
    public Set queryIntermediateParameters() {
        Set ret = new HashSet();
        for (Iterator i = getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getDriversOf(false).size() > 0 && param.getDrivensBy(false).size() > 0) {
                ret.add(param);
            }
        }

        for (Iterator i = getRelationOutputParameters().iterator(); i.hasNext();) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            if (param.getDriversOf(false).size() > 0 && param.getDrivensBy(false).size() > 0) {
                ret.add(param);
            }
        }
        return ret;
    }

    /** collect  relation parameters that has non-zero drivers and zero drivens */
    public Set queryResultParameters() {
        Set ret = new HashSet();
        for (Iterator i = getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getDriversOf(false).size() > 0 && param.getDrivensBy(false).size() == 0) {
                ret.add(param);
            }
        }

        for (Iterator i = getRelationOutputParameters().iterator(); i.hasNext();) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            if (param.getDriversOf(false).size() > 0 && param.getDrivensBy(false).size() == 0) {
                ret.add(param);
            }
        }
        return ret;
    }

    /** collect  relation parameters that has zero drivers and zero drivens */
    public Set queryIndeterminateParameters() {
        Set ret = new HashSet();
        for (Iterator i = getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getDriversOf(false).size() == 0 && param.getDrivensBy(false).size() == 0) {
                ret.add(param);
            }
        }

        for (Iterator i = getRelationOutputParameters().iterator(); i.hasNext();) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            if (param.getDriversOf(false).size() == 0 && param.getDrivensBy(false).size() == 0) {
                ret.add(param);
            }
        }
        return ret;
    }

    /** collect relation parameters that has zero drivers and non-zero drivens */
    public Set queryInputParameters() {
        Set ret = new HashSet();
        for (Iterator i = getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getDriversOf(false).size() == 0 && param.getDrivensBy(false).size() > 0) {
                ret.add(param);
            }
        }

        for (Iterator i = getRelationOutputParameters().iterator(); i.hasNext();) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            if (param.getDriversOf(false).size() == 0 && param.getDrivensBy(false).size() > 0) {
                ret.add(param);
            }
        }
        return ret;
    }

    /** collect relation parameters that are not input parameters */
    public Set queryOutputParameters() {
        Set ret = new HashSet();
        for (Iterator i = getRelationInputParameters().iterator(); i.hasNext();) {
            CRelationInputParameter param = (CRelationInputParameter) i.next();
            if (param.getDriversOf(false).size() > 0 || param.getDrivensBy(false).size() == 0) {
                ret.add(param);
            }
        }

        for (Iterator i = getRelationOutputParameters().iterator(); i.hasNext();) {
            CRelationOutputParameter param = (CRelationOutputParameter) i.next();
            if (param.getDriversOf(false).size() > 0 || param.getDrivensBy(false).size() == 0) {
                ret.add(param);
            }
        }
        return ret;
    }

    public CInterfaceInputParameter getInterfaceInputParameter(String paramName) {
        try {
            CInterfaceInputParameter ret = (CInterfaceInputParameter) objectMap.get(paramName);
            if (ret == null) {
                throw new RuntimeException("invalid parameter name: no such CInterfaceInputParameter named " + paramName);
            }
            return ret;
        } catch (ClassCastException e) {
            throw new RuntimeException("fail to retrieve a interface input parameter: " + paramName + " is an instance of " + objectMap.get(paramName).getClass().getName());
        }
    }

    public CInterfaceOutputParameter getInterfaceOutputParameter(String paramName) {
        try {
            CInterfaceOutputParameter ret = (CInterfaceOutputParameter) objectMap.get(paramName);
            if (ret == null) {
                throw new RuntimeException("invalid parameter name: no such CInterfaceOutputParameter named " + paramName);
            }
            return ret;
        } catch (ClassCastException e) {
            throw new RuntimeException("fail to retrieve a interface output parameter: " + paramName + " is an instance of " + objectMap.get(paramName).getClass().getName());
        }
    }

    public CRelationInputParameter getRelationInputParameter(String paramName) {
        try {
            CRelationInputParameter ret = (CRelationInputParameter) objectMap.get(paramName);
            if (ret == null) {
                throw new RuntimeException("invalid parameter name: no such CRelationInputParameter named " + paramName);
            }
            return ret;
        } catch (ClassCastException e) {
            throw new RuntimeException("fail to retrieve a relation input parameter: " + paramName + " is an instance of " + objectMap.get(paramName).getClass().getName());
        }
    }

    public CRelationOutputParameter getRelationOutputParameter(String paramName) {
        try {
            CRelationOutputParameter ret = (CRelationOutputParameter) objectMap.get(paramName);
            if (ret == null) {
                throw new RuntimeException("invalid parameter name: no such CRelationOutputParameter named " + paramName);
            }
            return ret;
        } catch (ClassCastException e) {
            throw new RuntimeException("fail to retrieve a relation output parameter: " + paramName + " is an instance of " + objectMap.get(paramName).getClass().getName());
        }
    }

    /** qualified name for derived parameter is (relAlias).(source output param name).(derived param name) */
    public CDerivedParameter getDerivedParameter(String paramName) {
        try {
            CDerivedParameter ret = (CDerivedParameter) objectMap.get(paramName);
            if (ret == null) {
                throw new RuntimeException("invalid parameter name: no such CDerivedParameter named " + paramName);
            }
            return ret;
        } catch (ClassCastException e) {
            throw new RuntimeException("fail to retrieve a derived parameter: " + paramName + " is an instance of " + objectMap.get(paramName).getClass().getName());
        }

    }

    public Set getParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    /** collect all CInterfaceInputParameter and CRelationInterfaceParameter in this implementation */
    public Set getInterfaceParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CInterfaceInputParameter || obj instanceof CInterfaceOutputParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    /** collect all CRelationInputParameter and CRelationOutputParameter in this implementation */
    public Set getRelationParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelationInputParameter || obj instanceof CRelationOutputParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    /** collect CRelationInputParameter and CRelationOutputParameter of a relation aliased relAlias */
    public Set getRelationParameters(String relAlias) {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelationInputParameter && relAlias.equals(((CRelationInputParameter) obj).getRelation().getRelAlias())) {
                ret.add(obj);
            } else if (obj instanceof CRelationOutputParameter && relAlias.equals(((CRelationOutputParameter) obj).getRelation().getRelAlias())) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public Set getInterfaceInputParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CInterfaceInputParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public Set getInterfaceOutputParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CInterfaceOutputParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public Set getRelationInputParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelationInputParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    /**
     * find all mapping nodes that are mapped to given parameter names
     * useful when we want to know which mapping nodes should go red together with a turning-to-red param
     * similarly, also useful to find which mapping nodes should go green together with a turning-to-green param
     * @param mappedParamName
     * @return
     */
    public Set findMappingNodes(String mappedParamName) {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            /* this object can be one of CRelationInputParameter, CRelationOutputParameter, CInterfaceInputParameter, CInterfaceOutputParameter, CRelation */
            Object obj = i.next();

            /* CMapping can be found in two CParameter's: CRelationInputParameter and CInterfaceOutputParameter */
            CMapping mapping = null;
            if (obj instanceof CRelationInputParameter) {
                mapping = ((CRelationInputParameter) obj).getMapping();
            } else if (obj instanceof CInterfaceOutputParameter) {
                mapping = ((CInterfaceOutputParameter) obj).getMapping();
            }

            /* if CMapping is found, find CMappingNode's corresponding to given mappedParamName */
            if (mapping != null) {
                /* find in input node */
                Set inputNodes = mapping.getInputNodes();
                for (Iterator j = inputNodes.iterator(); j.hasNext(); ) {
                    CMappingNode inputNode = (CMappingNode) j.next();
                    if (mappedParamName.equals(inputNode.getMappedParameterName())) {
                        ret.add(inputNode);
                    }
                }
                /* find in output node */
                CMappingNode outputNode = mapping.getOutputNode();
                if (mappedParamName.equals(outputNode.getMappedParameterName())) {
                    ret.add(outputNode);
                }
            }

        }
        return ret;
    }

    public Set findAllMappingNodes() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            /* this object can be one of CRelationInputParameter, CRelationOutputParameter, CInterfaceInputParameter, CInterfaceOutputParameter, CRelation */
            Object obj = i.next();

            /* CMapping can be found in two CParameter's: CRelationInputParameter and CInterfaceOutputParameter */
            CMapping mapping = null;
            if (obj instanceof CRelationInputParameter) {
                mapping = ((CRelationInputParameter) obj).getMapping();
            } else if (obj instanceof CInterfaceOutputParameter) {
                mapping = ((CInterfaceOutputParameter) obj).getMapping();
            }

            /* if CMapping is found, find CMappingNode's corresponding to given mappedParamName */
            if (mapping != null) {
                /* find in input node */
                Set inputNodes = mapping.getInputNodes();
                for (Iterator j = inputNodes.iterator(); j.hasNext(); ) {
                    CMappingNode inputNode = (CMappingNode) j.next();
                    ret.add(inputNode);
                }
                /* find in output node */
                CMappingNode outputNode = mapping.getOutputNode();
                ret.add(outputNode);
            }
        }
        return ret;
    }

    public Set getParameters(Set parameterNames) {
        Set ret = new HashSet();
        for (Iterator i = parameterNames.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter param = this.getParameter(paramName);
            if (param == null) {
                throw new RuntimeException("no such parameter named as " + paramName);
            }
            ret.add(param);
        }
        return ret;
    }

    public List getParameters(List parameterNames) {
        List ret = new ArrayList(parameterNames.size());
        for (Iterator i = parameterNames.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter param = this.getParameter(paramName);
            if (param == null) {
                throw new RuntimeException("no such parameter named as " + paramName);
            }
            ret.add(param);
        }
        return ret;
    }

    public Set getRelationOutputParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelationOutputParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public Set getDerivedParameters() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CDerivedParameter) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public Set getRelations() {
        Set ret = new HashSet();
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelation) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public String findRelAliasForNewRelation() {
//        int counter = -1;
//        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
//            Object obj = i.next();
//            if (obj instanceof CRelation) {
//                counter++;
//            }
//        }
//
//        counter++;

        int maxCounter = -1;
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelation) {
                String relAlias = ((CRelation) obj).getRelAlias();
                int prefixLength = CConstant.REL_ALIAS_PREFIX.length();
                if (relAlias.startsWith(CConstant.REL_ALIAS_PREFIX) && (relAlias.length() == (prefixLength + 1) || relAlias.length() == (prefixLength + 2))) {
                    String alphabets = relAlias.substring(prefixLength);
                    if (alphabets.length() == 1) {
                        char secondChar = alphabets.charAt(0);
                        int curCount = secondChar - 'A';
                        if (curCount > maxCounter) {
                            maxCounter = curCount;
                        }
                    } else if (alphabets.length() == 2) {
                        char firstChar = alphabets.charAt(0);
                        char secondChar = alphabets.charAt(1);
                        int curCount = ((firstChar - 'A') + 1) * ('Z' - 'A' + 1) + secondChar - 'A';
                        if (curCount > maxCounter) {
                            maxCounter = curCount;
                        }
                    } else {
                        // this is the case when a user have changed relation alias
                        // ignore it. it will never coincide with our new relAlias
                    }
                } else {
                    // this is the case when a user have changed relation alias
                    // ignore it. it will never coincide with our new relAlias
                }
            }
        }

        maxCounter++;

        // if counter is 0, 'A' should be returned
        // when it reaches 'Z', the next will be like 'AA', 'AB', etc.
        char firstChar = (char) ((maxCounter / ('Z' - 'A' + 1)) - 1 + 'A');
        char secondChar = (char) ((maxCounter % ('Z' - 'A' + 1)) + 'A');

        if (firstChar < 'A') {
            return new String(CConstant.REL_ALIAS_PREFIX + secondChar);
        } else {
            return new String(CConstant.REL_ALIAS_PREFIX + firstChar + "" + secondChar);
        }
    }

    /**
     * return CRelation with given name
     * @param relationAlias
     * @return
     */
    public CRelation getRelation(String relationAlias) {
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelation && relationAlias.equals(((CRelation) obj).getQualifiedName())) {
                return (CRelation) obj;
            }
        }
        return null;
    }

    public CParameter getParameter(String paramName) {
        return (CParameter) objectMap.get(paramName);
    }

    public String[] resolveUnqualifiedName(String unqualifiedName) {
        return null;
    }

    public boolean exist(String qualifiedName) {
        return (objectMap.get(qualifiedName) == null) ? false : true;
    }

    /** update input parameter names of all CMapping's. 
     * it invokes CMapping.updateInputParamNamesOfMappingScript() for all CMapping's in this implementation
     * see CMapping.updateInputParamNamesOfMappingScript() for details */
    public void updateInputParamNamesOfAllCMappings() {
        for (Iterator i = objectMap.values().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelationInputParameter) {
                CRelationInputParameter riParam = (CRelationInputParameter) obj;
                riParam.getMapping().updateInputParamNamesOfMappingScript();
            }
            if (obj instanceof CInterfaceOutputParameter) {
                CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) obj;
                ioParam.getMapping().updateInputParamNamesOfMappingScript();
            }
        }
    }

    public String toString() {
        String ret = "";
        for (Iterator i = objectMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            ret = ret + entry.getKey() + "=" + entry.getValue();
            if (i.hasNext()) {
                ret = ret + "\n";
            }
        }
        return ret;
    }

    public Map getMap() {
        return objectMap;
    }

    public int getObjectType(String key) {
        Object obj = objectMap.get(key);
        if (obj == null) {
            return NOT_FOUND_TYPE;
        } else if (obj instanceof CLocalRelation) {
            return LOCAL_RELATION_TYPE;
        } else if (obj instanceof CRemoteRelation) {
            return REMOTE_RELATION_TYPE;
        } else if (obj instanceof CInterfaceInputParameter) {
            return INTERFACE_INPUT_PARAMETER_TYPE;
        } else if (obj instanceof CInterfaceOutputParameter) {
            return INTERFACE_OUTPUT_PARAMETER_TYPE;
        } else if (obj instanceof CRelationInputParameter) {
            return RELATION_INPUT_PARAMETER_TYPE;
        } else if (obj instanceof CRelationOutputParameter) {
            return RELATION_OUTPUT_PARAMETER_TYPE;
        } else if (obj instanceof CDerivedParameter) {
            return DERIVED_PARAMETER_TYPE;
        }

        throw new RuntimeException("Invald type of class found in the object map: class type '" + obj.getClass().getName() + "' mapped to '" + key + "'");
    }

    /** convert a set of CParameters into a set of parameter names. The qualified parameter name is stored. */
    public static Set createNameSet(Set paramSet, boolean useQualifiedName) {
        Set nameSet = new HashSet();
        for (Iterator i = paramSet.iterator(); i.hasNext(); ) {
            CParameter param = (CParameter) i.next();
            if (useQualifiedName) {
                nameSet.add(param.getQualifiedName());
            } else {
                nameSet.add(param.getName());
            }
        }
        return nameSet;
    }

    /** convert local name (unqualified name without "namespace." in front of the local name) into qualified name */
    public static String convertToQualified(String namespace, String localName) {
        return namespace + "." + localName;
    }

    /** convert qualified name (namespace + '.' + local name) into local name.
     * there can be case like relAlias.outputParamName.derivedParamName, so we cut at the last index of "."  */
    public static String convertToLocal(String qualifiedName) {
        int idxOfDot = qualifiedName.lastIndexOf(".");
        if (idxOfDot == -1) {
            throw new RuntimeException("given string '" + qualifiedName + "' is not a valid qualified name");
        }
        return qualifiedName.substring(idxOfDot + 1);
    }

    /** extract from a qualified name (namespace + '.' + local name) into namespace.
     * there can be case like relAlias.outputParamName.derivedParamName, so we cut at the first index of "."  */
    public static String parseRelAlias(String qualifiedName) {
        int idxOfDot = qualifiedName.indexOf(".");
        if (idxOfDot == -1) {
            throw new RuntimeException("given string '" + qualifiedName + "' is not a valid qualified name");
        }
        return qualifiedName.substring(0, idxOfDot);
    }

    /** convert local name set into qualified name set */
    public static Set convertToQualified(String namespace, Set localNameSet) {
        Set ret = new HashSet();
        for (Iterator i = localNameSet.iterator(); i.hasNext();) {
            String localName = (String) i.next();
            ret.add(convertToQualified(namespace, localName));
        }
        return ret;
    }

    /** convert local name set into qualified name set */
    public static List convertToQualified(String namespace, List localNameList) {
        List ret = new ArrayList();
        for (Iterator i = localNameList.iterator(); i.hasNext();) {
            String localName = (String) i.next();
            ret.add(convertToQualified(namespace, localName));
        }
        return ret;
    }

    /** convert qualified name set into local name set i.e. it removes "namespace." in front of qualified names */
    public static Set convertToLocal(Set qualifiedNameSet) {
        Set ret = new HashSet();
        for (Iterator i = qualifiedNameSet.iterator(); i.hasNext();) {
            String qualifiedName = (String) i.next();
            ret.add(convertToLocal(qualifiedName));
        }
        return ret;
    }

    /** change the relAlias part of given qualified name with newRelAlias
     * (ex) if arguments of newRelAlias=RelB, namespace=RelA are given, returns RelB
     * (ex) if arguments of newRelAlias=RelE, namespace=RelD.width are given, returns RelE.width
     */
    public static String updateRelAliasInNamespace(String newRelAlias, String namespace) {
        int idxOfDot = namespace.indexOf("."); // relAlias is always from the beginning of a qualified name to the first dot.
        if (idxOfDot == -1) {
            return newRelAlias;
        }
        return newRelAlias + namespace.substring(idxOfDot);
    }

    /** returns if a given namingspace contains relAlias
     * Note. case when namingspace has "." in it:
     * (ex) for qualified name of RelA.width.widthInInch, RelA.width is namespace
     * (ex) for qualified name of RelA.width, RelA is namepspace */
    public static boolean containsRelAlias(String relAlias, String namingspace) {
        return namingspace.equals(relAlias) || namingspace.startsWith(relAlias + ".");
    }

    /* test if the move from src to target is just a rearrangement */
    public static boolean isRearrangement(String targetRelAlias, boolean targetOnLeft, String srcRelAlias, boolean srcOnLeft, boolean copyMode) {
        if (copyMode) {
            return false; // in copy mode no rearrangement occurs
        } else {
            return (targetRelAlias.equals(srcRelAlias) && targetOnLeft == srcOnLeft);
        }
    }

    public void moveParameters(List qualifiedParamNames, String targetRelAlias, boolean targetOnLeft, int insertParamIdx, Map renameMap, boolean copyMode, boolean copyWithMapping) {
//        System.out.println("insertParamIdx at the beginning: " + insertParamIdx);
        /* 1. populate oldQualNameToParamMap && adjust insertParamIdx */
        Map oldQualNameToParamMap = new HashMap();
        for (int i = 0; i < qualifiedParamNames.size(); i++) {
            String qualifieidParamName = (String) qualifiedParamNames.get(i);
            String srcRelAlias = CNamingService.parseRelAlias(qualifieidParamName);
            CParameter param = this.getParameter(qualifieidParamName);
            boolean srcOnLeft = (param instanceof CInterfaceInputParameter) || (param instanceof CRelationInputParameter);

            oldQualNameToParamMap.put(qualifieidParamName, param);

//            if (! isRearrangement(targetRelAlias, targetOnLeft, srcRelAlias, srcOnLeft, copyMode)) {
//                continue;
//            }

//            int indexOfMovedParam = 0;
//            if (CConstant.ITF_ALIAS.equals(srcRelAlias)) {
//                if (srcOnLeft) {
//                    indexOfMovedParam = this.getImplementation().getInputParameterNames().indexOf(param.getName());
//                } else {
//                    indexOfMovedParam = this.getImplementation().getOutputParameterNames().indexOf(param.getName());
//                }
//            } else {
//                if (srcOnLeft) {
//                    indexOfMovedParam = this.getRelation(srcRelAlias).getInputParameterNames().indexOf(param.getName());
//                } else {
//                    indexOfMovedParam = this.getRelation(srcRelAlias).getOutputParameterNames().indexOf(param.getName());
//                }
//            }

//            System.out.println("oldQualNameToParamMap = " + oldQualNameToParamMap);
//            System.out.println("indexOfMovedParam = " + indexOfMovedParam + ", insertParamIdx = " + insertParamIdx);
        }

        /* 2. remove moving params if they originally didn't belong to the target relation.
             note that for parameters which are re-arranged in the same, target relation, we do not remove them from the current relation
             note that we skip this step when copy mode is true. */
        for (int i = 0; ! copyMode && i < qualifiedParamNames.size(); i++) {
            String qualifieidParamName = (String) qualifiedParamNames.get(i);
            String srcRelAlias = CNamingService.parseRelAlias(qualifieidParamName);
            CParameter param = this.getParameter(qualifieidParamName);
            boolean srcOnLeft = (param instanceof CInterfaceInputParameter) || (param instanceof CRelationInputParameter);

            if (isRearrangement(targetRelAlias, targetOnLeft, srcRelAlias, srcOnLeft, copyMode)) {
                continue; // skip this param. do not remove it.
            }

            if (CConstant.ITF_ALIAS.equals(srcRelAlias)) {
                if (srcOnLeft) {
                    this.getImplementation().getParentInterface().removeInputParameter(param.getName());
                } else {
                    this.getImplementation().getParentInterface().removeOutputParameter(param.getName());
                }
                this.getImplementation().getParentInterface().synchronizeInterfaceParametersOfAllImplementations();
            } else {
                if (srcOnLeft) {
                    this.getRelation(srcRelAlias).removeInputParameter(param.getName());
                } else {
                    this.getRelation(srcRelAlias).removeOutputParameter(param.getName());
                }
            }

//            System.out.println(" we just removed moved parameters : " + param.getName());
        }


        /* 3. re-arrange qualified param name in the target relation
              we don't need to rearrange parameters when copyMode is true */
        for (int i = 0; ! copyMode && i < qualifiedParamNames.size(); i++) {
            String qualifieidParamName = (String) qualifiedParamNames.get(i);
            String srcRelAlias = CNamingService.parseRelAlias(qualifieidParamName);
            CParameter oldParam = (CParameter) oldQualNameToParamMap.get(qualifieidParamName);
            boolean srcOnLeft = (oldParam instanceof CInterfaceInputParameter) || (oldParam instanceof CRelationInputParameter);

            if (! isRearrangement(targetRelAlias, targetOnLeft, srcRelAlias, srcOnLeft, copyMode)) {
                continue; // skip params which are not re-arranged
            }

            /* retrieve a list that is directly backing the orderly arrangement of parameters: the list contains either param-name string or CParameter object depending on the bar type */
            List paramReferenceList = null;
            int currentParamIdx = -1;
            if (CConstant.ITF_ALIAS.equals(srcRelAlias) && targetOnLeft) {
                paramReferenceList = this.getImplementation().getParentInterface().getInputParameterList();
                for (int j = 0; j < paramReferenceList.size(); j++) {
                    String paramName = ((CParameter) paramReferenceList.get(j)).getName();
                    if (oldParam.getName().equals(paramName)) {
                        currentParamIdx = j; // find the index of old param in the paramReferenceList
                        break;
                    }
                }
//                System.out.println("*** paramReferenceList = " + paramReferenceList);
//                System.out.println("*** oldParam = " + oldParam + " / currentParamIdx = " + currentParamIdx);
            } else if (CConstant.ITF_ALIAS.equals(srcRelAlias) && ! targetOnLeft) {
                paramReferenceList = this.getImplementation().getParentInterface().getOutputParameterList();
                for (int j = 0; j < paramReferenceList.size(); j++) {
                    String paramName = ((CParameter) paramReferenceList.get(j)).getName();
                    if (oldParam.getName().equals(paramName)) {
                        currentParamIdx = j; // find the index of old param in the paramReferenceList
                        break;
                    }
                }
            } else if (! CConstant.ITF_ALIAS.equals(srcRelAlias) && targetOnLeft) {
                paramReferenceList = this.getRelation(srcRelAlias).getInputParameterNames();
                currentParamIdx = paramReferenceList.indexOf(oldParam.getName());
            } else if (! CConstant.ITF_ALIAS.equals(srcRelAlias) && ! targetOnLeft) {
                paramReferenceList = this.getRelation(srcRelAlias).getOutputParameterNames();
                currentParamIdx = paramReferenceList.indexOf(oldParam.getName());
            }

            if (currentParamIdx > insertParamIdx) {
                Object paramNameToBeMoved = paramReferenceList.get(currentParamIdx);
                paramReferenceList.add(insertParamIdx, paramNameToBeMoved);
                paramReferenceList.remove(currentParamIdx + 1);
                insertParamIdx++;
            } else {
                Object paramNameToBeMoved = paramReferenceList.get(currentParamIdx);
                if (paramReferenceList.size() == insertParamIdx) {
                    paramReferenceList.add(paramNameToBeMoved);
                } else {
                    paramReferenceList.add(insertParamIdx, paramNameToBeMoved);
                }
                paramReferenceList.remove(currentParamIdx);
                //insertParamIdx++;
            }

//            System.out.println("paramReferenceList = " + paramReferenceList);

            /* copy changes made to the current interface to its implementations */
            if (CConstant.ITF_ALIAS.equals(srcRelAlias)) {
                getImplementation().getParentInterface().synchronizeInterfaceParametersOfAllImplementations();
            }

//            System.out.println(" we now have re-arrangeda a param : " + oldParam + " to " + insertParamIdx);
        }

        /* 4. insert selected parameters, which are in qualifiedParamNameList, into target relation. Note that we don't have to add the parameters that have been re-arranged in step 3 again */
        for (int i = 0; i < qualifiedParamNames.size(); i++) {
            String qualifiedParamName = (String) qualifiedParamNames.get(i);
            String srcRelAlias = CNamingService.parseRelAlias(qualifiedParamName);
            CParameter oldParam = (CParameter) oldQualNameToParamMap.get(qualifiedParamName);
            boolean srcOnLeft = (oldParam instanceof CInterfaceInputParameter) || (oldParam instanceof CRelationInputParameter);

            if (isRearrangement(targetRelAlias, targetOnLeft, srcRelAlias, srcOnLeft, copyMode)) {
                continue; // skip this param
            }


            //String uniqueName = (renameMap.get(qualifiedParamName) == null) ? null : (String) ((Object []) renameMap.get(qualifiedParamName)) [0];
            Integer uniqueLocalNameIdx = new Integer(insertParamIdx);
            List renameKey = Arrays.asList(new Object[] { qualifiedParamName, uniqueLocalNameIdx });
            String uniqueLocalName = (String) renameMap.get(renameKey);
//            System.out.println("qualified param name = " + qualifiedParamName + " , unique local name idx " + insertParamIdx + " ===> " + uniqueLocalName);

            //Integer uniqueNameIdx = (renameMap.get(qualifiedParamName) == null) ? null : (Integer) ((Object []) renameMap.get(qualifiedParamName)) [1];

            if (uniqueLocalName == null) {
                uniqueLocalName = CNamingService.convertToLocal(qualifiedParamName);
            }

            CParameter newParam = null;
            if (CConstant.ITF_ALIAS.equals(targetRelAlias)) {
                if (targetOnLeft) {
//                    System.out.println("adding into interface input : idx = " + insertParamIdx + " , input list = " + this.getImplementation().getParentInterface().getInputParameterList());
                    CParameter itfNewParam = this.getImplementation().getParentInterface().addInputParameter(insertParamIdx++, uniqueLocalName);
                    itfNewParam.setDataType(oldParam.getDataType());
                    itfNewParam.setDefaultValue(oldParam.getDefaultValue());
                    itfNewParam.setUnit(oldParam.getUnit());
                    this.getImplementation().getParentInterface().synchronizeInterfaceParametersOfAllImplementations();
                    newParam = this.getImplementation().getInputParameter(uniqueLocalName);
                } else {
                    CParameter itfNewParam = this.getImplementation().getParentInterface().addOutputParameter(insertParamIdx++, uniqueLocalName);
                    itfNewParam.setDataType(oldParam.getDataType());
                    itfNewParam.setDefaultValue(oldParam.getDefaultValue());
                    itfNewParam.setUnit(oldParam.getUnit());
                    this.getImplementation().getParentInterface().synchronizeInterfaceParametersOfAllImplementations();
                    newParam = this.getImplementation().getOutputParameter(uniqueLocalName);
                }
            } else {
                if (targetOnLeft) {
//                    System.out.println("relation input param names : + " + this.getRelation(targetRelAlias).getInputParameterNames() + "insertParamIdx = " + insertParamIdx + " / uniqueName = " + uniqueLocalName);
                    newParam = this.getRelation(targetRelAlias).addInputParameter(insertParamIdx++, uniqueLocalName);
                    newParam.setDataType(oldParam.getDataType());
                    newParam.setDefaultValue(oldParam.getDefaultValue());
                    newParam.setUnit(oldParam.getUnit());
                } else {
                    newParam = this.getRelation(targetRelAlias).addOutputParameter(insertParamIdx++, uniqueLocalName);
                    newParam.setDataType(oldParam.getDataType());
                    newParam.setDefaultValue(oldParam.getDefaultValue());
                    newParam.setUnit(oldParam.getUnit());
                }
            }

//            System.out.println(" we now add a new param : " + newParam);

            if (copyMode && ! copyWithMapping) {
                if (newParam instanceof CInterfaceOutputParameter) {
                    if (oldParam instanceof CRelationInputParameter) {
                        String mappingScript = ((CRelationInputParameter) oldParam).getMapping().getMappingScript();
                        ((CInterfaceOutputParameter) newParam).getMapping().setMappingScript(mappingScript);
                    } else if (oldParam instanceof CInterfaceOutputParameter) {
                        String mappingScript = ((CInterfaceOutputParameter) oldParam).getMapping().getMappingScript();
                        ((CInterfaceOutputParameter) newParam).getMapping().setMappingScript(mappingScript);
                    } else {
                        String defaultMappingScript = CoreUtil.convertDefaultValueIntoMappingScript(oldParam.getDataType(), oldParam.getDefaultValue());
                        ((CInterfaceOutputParameter) newParam).getMapping().setMappingScript(defaultMappingScript);
                    }
                } else if (newParam instanceof CRelationInputParameter) {
                    if (oldParam instanceof CRelationInputParameter) {
                        String mappingScript = ((CRelationInputParameter) oldParam).getMapping().getMappingScript();
                        ((CRelationInputParameter) newParam).getMapping().setMappingScript(mappingScript);
                    } else if (oldParam instanceof CInterfaceOutputParameter) {
                        String mappingScript = ((CInterfaceOutputParameter) oldParam).getMapping().getMappingScript();
                        ((CRelationInputParameter) newParam).getMapping().setMappingScript(mappingScript);
                    } else {
                        String defaultMappingScript = CoreUtil.convertDefaultValueIntoMappingScript(oldParam.getDataType(), oldParam.getDefaultValue());
                        ((CRelationInputParameter) newParam).getMapping().setMappingScript(defaultMappingScript);
                    }
                }
            } else if (copyMode && copyWithMapping) {
                if (newParam instanceof CInterfaceOutputParameter) {
                    ((CInterfaceOutputParameter) newParam).getMapping().setMappingScript(oldParam.getQualifiedName());
                }

                if (newParam instanceof CRelationInputParameter) {
                    ((CRelationInputParameter) newParam).getMapping().setMappingScript(oldParam.getQualifiedName());
                }

                /* when copying relation input param to interface input param, */
                if (newParam instanceof CInterfaceInputParameter && oldParam instanceof CRelationInputParameter) {
                    /* if the relation input param's mapping script is empty or the same as the default value of the param, set the mapping script as a qualified param name of the interface input param.*/
                    String mappingScriptOfOldParam = ((CRelationInputParameter) oldParam).getMapping().getMappingScript();
                    String defaultMappingScript = CoreUtil.convertDefaultValueIntoMappingScript(oldParam.getDataType(), oldParam.getDefaultValue());

                    if ("".equals(mappingScriptOfOldParam) || defaultMappingScript.equals(mappingScriptOfOldParam)) {
                        ((CRelationInputParameter) oldParam).getMapping().setMappingScript(newParam.getQualifiedName());
                    }
                }

                if (newParam instanceof CRelationOutputParameter && oldParam instanceof CInterfaceOutputParameter) {
                    /* if the interface output param's mapping script is empty or the same as the default value of the param, set the mapping script as a qualified param name of the relation output param.*/
                    String mappingScriptOfOldParam = ((CInterfaceOutputParameter) oldParam).getMapping().getMappingScript();
                    String defaultMappingScript = CoreUtil.convertDefaultValueIntoMappingScript(oldParam.getDataType(), oldParam.getDefaultValue());

                    if ("".equals(mappingScriptOfOldParam) || defaultMappingScript.equals(mappingScriptOfOldParam)) {
                        ((CInterfaceOutputParameter) oldParam).getMapping().setMappingScript(newParam.getQualifiedName());
                    }
                }
            } else {
                if (newParam instanceof CInterfaceOutputParameter && oldParam instanceof CRelationInputParameter) {
                    String mappingScript = ((CRelationInputParameter) oldParam).getMapping().getMappingScript();
                    ((CInterfaceOutputParameter) newParam).getMapping().setMappingScript(mappingScript);
                }

                if (newParam instanceof CRelationInputParameter && oldParam instanceof CRelationInputParameter) {
                    String mappingScript = ((CRelationInputParameter) oldParam).getMapping().getMappingScript();
                    ((CRelationInputParameter) newParam).getMapping().setMappingScript(mappingScript);
                }

                if (newParam instanceof CRelationInputParameter && oldParam instanceof CInterfaceOutputParameter) {
                    String mappingScript = ((CInterfaceOutputParameter) oldParam).getMapping().getMappingScript();
                    ((CRelationInputParameter) newParam).getMapping().setMappingScript(mappingScript);
                }

                if (newParam instanceof CInterfaceOutputParameter && oldParam instanceof CInterfaceOutputParameter) {
                    String mappingScript = ((CInterfaceOutputParameter) oldParam).getMapping().getMappingScript();
                    ((CInterfaceOutputParameter) newParam).getMapping().setMappingScript(mappingScript);
                }
            }

//            System.out.println(" finally we configured new param as : " + newParam);
        }

//        if (CConstant.ITF_ALIAS.equals(targetRelAlias)) {
//            if (targetOnLeft) {
//                System.out.println("final input param names: " + this.getImplementation().getInputParameterNames());
//            } else {
//                System.out.println("final input param names: " + this.getImplementation().getOutputParameterNames());
//            }
//        } else {
//            if (targetOnLeft) {
//                System.out.println("final input param names: " + this.getRelation(targetRelAlias).getInputParameterNames());
//            } else {
//                System.out.println("final input param names: " + this.getRelation(targetRelAlias).getOutputParameterNames());
//            }
//        }

    }
}
