package mit.cadlab.dome3.plugin.catalog.core;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public abstract class CRelation {

    /* what is the difference between relAlias and relationName?
     * relationName indicate the name of a remote interface or a local interface.
     * (an implementation can subscribe the same interface multiple times,
     *  and therefore there can be many instances of the same interface.
     * note that this only applies to remote relations because remote relations can be subscribed many times, but local relations are not.
     * i.e. a local relation has only one instance per implementation.)
     */
    private String relAlias;
    private String relationName;
    private String namespace; // CRelation has is at the top of namespace, so "" is its namespace
    private List inputParameterNames; // local names without namespace are stored
    private List outputParameterNames; // local names without namespace are stored
    private Map driverToDrivenMap; // a local name mapping to a set of local names
    private Map drivenToDriverMap; // a local name mapping to a set of local names


    private CImplementation implementation;
    private CNamingService namingService;

    /** to identify CRelation in NamingService, relAlias is used, because relationName can be duplicated in a implementation if there exist multiple-times subscribed remote relations */
    public CRelation(String relationName, String relAlias, CImplementation implementation, CNamingService namingService) {
        if (! isValidRelAlias(relAlias)) {
            throw new RuntimeException("Invalid relAlias. The relation alias must begin with letters and contain only letters and numbers: " + relAlias);
        }

        this.relationName = relationName;
        this.implementation = implementation;
        this.namespace= ""; // implementation is the root of namespace for all relations and parameters in it

        this.namingService = namingService;

        this.inputParameterNames = new ArrayList();
        this.outputParameterNames = new ArrayList();
        this.driverToDrivenMap = new HashMap();
        this.drivenToDriverMap = new HashMap();

        this.relAlias = relAlias;
    }

    public CRelationInputParameter addInputParameter(int index, String paramName) {
        CRelationInputParameter riParam = new CRelationInputParameter(paramName, this.getQualifiedName(), namingService);
        namingService.register(riParam.getQualifiedName(), riParam);
        if (inputParameterNames.size() == index) {
            inputParameterNames.add(riParam.getName());
        } else {
            inputParameterNames.add(index, riParam.getName());
        }

        return riParam;
    }

    public CRelationInputParameter addInputParameter(String paramName) {
        CRelationInputParameter riParam = new CRelationInputParameter(paramName, this.getQualifiedName(), namingService);
        namingService.register(riParam.getQualifiedName(), riParam);
        inputParameterNames.add(riParam.getName());
        return riParam;
    }

    /** remove an input parameter from this CRelation. For paramName, a local name such as "width" is used
     * note that CNamingService.clearMappingsRelatedTo() is called within this method. as a result, all mapping scripts in the current implementation that reference this removed parameter will be cleared and set to an empty string, "" */
    public void removeInputParameter(String paramName) {
        namingService.clearMappingScriptsReferencing(this.getRelAlias() + "." + paramName);
        this.removeParameterFromDriverAndDrivenMap(paramName);
        namingService.unregister(this.getRelAlias() + "." + paramName);
        inputParameterNames.remove(paramName);
    }

    /** remove an output parameter from this CRelation. For paramName, a local name such as "width" is used
     * note that CNamingService.clearMappingsRelatedTo() is called within this method, and as a result, all mapping scripts in the current implementation that reference this removed parameter will be cleared and set to an empty string, "" */
    public void removeOutputParameter(String paramName) {
        namingService.clearMappingScriptsReferencing(this.getRelAlias() + "." + paramName);
        this.removeParameterFromDriverAndDrivenMap(paramName);
        namingService.unregister(this.getRelAlias() + "." + paramName);
        outputParameterNames.remove(paramName);
    }

    private void removeParameterFromDriverAndDrivenMap(String paramName) {
        for (Iterator i = drivenToDriverMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            String drivenParamName = (String) entry.getKey();
            Set driverParamNames = (Set) entry.getValue();
            if (drivenParamName.equals(paramName)) {
                i.remove();
                continue;
            }
            if (driverParamNames.contains(paramName)) {
                driverParamNames.remove(paramName);
            }
        }

        for (Iterator i = driverToDrivenMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            String driverParamName = (String) entry.getKey();
            Set drivenParamNames = (Set) entry.getValue();
            if (driverParamName.equals(paramName)) {
                i.remove();
                continue;
            }
            if (drivenParamNames.contains(paramName)) {
                drivenParamNames.remove(paramName);
            }
        }
    }

    /** remove all input & output parameters from this CRelation.
     * note that CNamingService.clearMappingsRelatedTo() is called within this method, and as a result, all mapping scripts in the current implementation that reference those removed parameters will be cleared and set to an empty string, "" */
    public void removeAllParameters() {
        for (int i = 0; i < inputParameterNames.size(); i++) {
            String inputParamName = (String) inputParameterNames.get(i);
            removeInputParameter(inputParamName);
        }

        for (int i = 0; i < outputParameterNames.size(); i++) {
            String outputParamName = (String) outputParameterNames.get(i);
            removeOutputParameter(outputParamName);
        }
    }

    public CRelationOutputParameter addOutputParameter(int index, String paramName) {
        CRelationOutputParameter roParam = new CRelationOutputParameter(paramName, this.getQualifiedName(), namingService);
        namingService.register(roParam.getQualifiedName(), roParam);
        if (outputParameterNames.size() == index) {
            outputParameterNames.add(roParam.getName());
        } else {
            outputParameterNames.add(index, roParam.getName());
        }
        return roParam;
    }

    public CRelationOutputParameter addOutputParameter(String paramName) {
        CRelationOutputParameter roParam = new CRelationOutputParameter(paramName, this.getQualifiedName(), namingService);
        namingService.register(roParam.getQualifiedName(), roParam);
        outputParameterNames.add(roParam.getName());
        return roParam;
    }

    public String getRelAlias() {
        return relAlias;
    }

    /** test if a relAlias is a valid one */
    private boolean isValidRelAlias(String testedRelAlias) {
        Pattern p = Pattern.compile("^(?:[a-zA-Z])+(?:[a-zA-Z_0-9])*$");
        Matcher m = p.matcher(testedRelAlias);
        return m.find();
    }

    /** there are a few information updated accordingly when relAlias is modified.
     * In the NamingService, relAlias is a key to this relation and used in the key to the parameters of the relation. (ex) string "relB.width" is a key to "width" parameter of relation "relB"
     * We should update those keys to reflect the changes made to relAlias.
     * (1) parameters under this relation. their namespace is updated as new relAlias as well as their NamingSevice key is modified
     * (2) mapping script containing this relAlias
     * (3) a key string for this CRelation, which is an old relAlias, in the NamingService */
    public void setRelAliasAndUpdateNamingService(String newRelAlias) {
        if (! isValidRelAlias(newRelAlias)) {
            throw new RuntimeException("Invalid newRelAlias. The relation alias must begin with letters and contain only letters and numbers: " + newRelAlias);
        }

        /* (1) */
        for (Iterator i = namingService.getParameters().iterator(); i.hasNext(); ) {
            CParameter param = (CParameter) i.next();
            /* find relation input/output parameters that belong to this CRelation by comparing current relAlias with param's namespace */
            if (relAlias.equals(param.getNamespace())) {
                namingService.getMap().remove(param.getQualifiedName());
                param.setNamespace(newRelAlias);
                namingService.register(param.getQualifiedName(), param);
            }

            /* find derived parameters that belong to this CRelation by comparing current relAlias with the beginning of param's namespace */
            if (param instanceof CDerivedParameter && CNamingService.containsRelAlias(relAlias, param.getNamespace())) {
                namingService.getMap().remove(param.getQualifiedName());
                param.setNamespace(CNamingService.updateRelAliasInNamespace(newRelAlias, param.getNamespace()));
                namingService.register(param.getQualifiedName(), param);
            }
        }

        // todo: (2)

        /* (3) */
        namingService.getMap().remove(relAlias);
        namingService.register(newRelAlias, this);
        this.relAlias = newRelAlias;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public CImplementation getImplementation() {
        return implementation;
    }

    public void setImplementation(CImplementation implementation) {
        this.implementation = implementation;
    }

    /* for CRelation the qualified name is the same as the var name of a CRelation object */
    public String getQualifiedName() {
        if ("".equals(namespace) || null == namespace) {
            return relAlias;
        }
        return namespace + "." + relAlias;
    }

    public List getOutputParameterNames() {
        return outputParameterNames;
    }

    public List getInputParameterNames() {
        return inputParameterNames;
    }

    public void setOutputParameterNames(List outputParameterNaems) {
        this.outputParameterNames = outputParameterNaems;
    }

    public void setInputParameterNames(List inputParameterNames) {
        this.inputParameterNames = inputParameterNames;
    }

    /** returns a list of CRelationInputParameter instances */
    public List getInputParameters() {
        List ret = new ArrayList();
        for (Iterator i = this.getInputParameterNames().iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter inputParam = getNamingService().getRelationInputParameter(getRelAlias() + "." + paramName);
            ret.add(inputParam);
        }
        return ret;
    }

    /** returns a list of CRelationOutputParameter instances */
    public List getOutputParameters() {
        List ret = new ArrayList();
        for (Iterator i = this.getOutputParameterNames().iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CParameter outputParam = getNamingService().getRelationOutputParameter(getRelAlias() + "." + paramName);
            ret.add(outputParam);
        }
        return ret;
    }

    /** returns CRelationInputParameter instances using its local(=unqualified) name such as "length", not "relA.length" */
    public CRelationInputParameter getInputParameter(String localParamName) {
        return getNamingService().getRelationInputParameter(getRelAlias() + "." + localParamName);
    }

    /** returns CRelationOutputParameter instances using its local(=unqualified) name such as "length", not "relA.length" */
    public CRelationOutputParameter getOutputParameter(String localParamName) {
        return getNamingService().getRelationOutputParameter(getRelAlias() + "." + localParamName);
    }

    public CNamingService getNamingService() {
        return namingService;
    }

    public void setDependency(String driverParamName, String drivenParamName) {
        /* update driver-to-drivens map */
        Set drivenParamNames = (Set) driverToDrivenMap.get(driverParamName);
        if (drivenParamNames == null) {
            drivenParamNames = new HashSet();
        }
        drivenParamNames.add(drivenParamName);
        driverToDrivenMap.put(driverParamName, drivenParamNames);

        /* update driven-to-drivers map */
        Set driverParamNames = (Set) drivenToDriverMap.get(drivenParamName);
        if (driverParamNames == null) {
            driverParamNames = new HashSet();
        }
        driverParamNames.add(driverParamName);
        drivenToDriverMap.put(drivenParamName, driverParamNames);
    }

    /** note!!! it returns local name set of drivers (=unqualified name or param name without relAlias)
     * not like getDrivers() of CRelationOutputParameter or CInterfaceOutputParameter.
     * getDrivers() of CRelationOutputParameter or CInterfaceOutputParameter returns qualified name set  */
    public Set getDriversOf(String drivenParamName) {
        Set ret = (Set) drivenToDriverMap.get(drivenParamName);
        return (ret == null) ? new HashSet() : ret;
    }

    /** returns local name set of drivens. see a note on CRelation.getDriversOf() */
    public Set getDrivensBy(String driverParamName) {
        Set ret = (Set) driverToDrivenMap.get(driverParamName);
        return (ret == null) ? new HashSet() : ret;
    }

    public void clearDependency() {
        driverToDrivenMap.clear();
        drivenToDriverMap.clear();
    }

    public String toString() {
        String driveStr = "";
        for (Iterator i = driverToDrivenMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            driveStr = driveStr + entry.getKey() + "->" + entry.getValue();
            if (i.hasNext()) {
                driveStr = driveStr + ", ";
            }
        }

        String drivenStr = "";
        for (Iterator i = drivenToDriverMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            drivenStr = drivenStr + entry.getKey() + "<-" + entry.getValue();
            if (i.hasNext()) {
                drivenStr = drivenStr + ", ";
            }
        }
        return "[relation:name=" + getQualifiedName() +", input=" + inputParameterNames + ", output=" + outputParameterNames + ", driver->drivens=" + driveStr + ", driven<-drivers=" + drivenStr + "]";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CRelation)) return false;

        final CRelation cRelation = (CRelation) o;

        if (relAlias != null ? !relAlias.equals(cRelation.relAlias) : cRelation.relAlias != null) return false;
        if (getImplementation().getQualifiedName() != null ? !getImplementation().getQualifiedName().equals(cRelation.getImplementation().getQualifiedName()) : cRelation.getImplementation().getQualifiedName() != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (relAlias != null ? relAlias.hashCode() : 0);
        result = 29 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    /**
     * if this relation is executed at some point when this method is called,
     * the set of output parameter names to be determined by evaluating this relation is returned
     * if it cannot determine any output parameter, it will return an empty set.
     * it uses local names to address each parameter
     * @return
     */
    public Set getDeterminableOutputParametersNames() {
        Set determinableSet = new HashSet();
        List outputParamNames = this.getOutputParameterNames();
        for (Iterator i = outputParamNames.iterator(); i.hasNext(); ) {
            String outputParamName = (String) i.next();

            /* if output parameter is already determined, it is not of our concern, which is looking for newly determinable */
            if (namingService.getParameter(getQualifiedName() + "." + outputParamName).getStatus() != CConstant.RED_STATUS) {
                continue;
            }

            Set driverNames = getDriversOf(outputParamName);
            boolean isDeterminable = true;
            for (Iterator j = driverNames.iterator(); j.hasNext(); ) {
                String driverName = (String) j.next();
                CRelationInputParameter riParam = namingService.getRelationInputParameter(this.getQualifiedName() + "." + driverName);
                /* if some driver has RED STATUS it cannot be determined */
                if (riParam.getStatus() == CConstant.RED_STATUS) {
                    isDeterminable = false;
                    break;
                } else if (riParam.getStatus() == CConstant.UNASSIGNED_STATUS) {
                    throw new RuntimeException("relation input param " + riParam.getName() + " has invalid (=unassigned) status. it happens when affectedParamNames is smaller than the actually changed paramNames: use evaluate(); to evaluate an implementation assuming all params as affected");
                }
            }
            if (isDeterminable) {
                determinableSet.add(outputParamName);
            }
        }
        return determinableSet;
    }

    abstract public String getRelationScript();

    /**
     * it change the naming service of this instance
     * @param namingService
     */
    public void changeNamingService(CNamingService namingService) {
        this.namingService = namingService;
    }

    /**
     * make a copy of CRelation
     */
    public static void copy(CRelation src, CRelation to) {
        to.setRelAlias(src.getRelAlias());
        to.setRelationName(src.getRelationName());
        to.setNamespace(src.getNamespace());
        to.setImplementation(src.getImplementation());

        to.setDriverToDrivenMap(new HashMap(src.getDrivenToDriverMap()));
        to.setDrivenToDriverMap(new HashMap(src.getDriverToDrivenMap()));
        to.setInputParameterNames(new ArrayList(src.getInputParameterNames()));
        to.setOutputParameterNames(new ArrayList(src.getOutputParameterNames()));

    }

    /* only invoked by clone() */
    private void setRelAlias(String relAlias) {
        this.relAlias = relAlias;
    }

    /** invoked only by clone() of this class */
    protected Map getDriverToDrivenMap() {
        return driverToDrivenMap;
    }

    /** invoked only by clone() of this class */
    protected void setDriverToDrivenMap(Map driverToDrivenMap) {
        this.driverToDrivenMap = driverToDrivenMap;
    }

    /** invoked only by clone() of this class */
    protected Map getDrivenToDriverMap() {
        return drivenToDriverMap;
    }

    /** invoked only by clone() of this class */
    protected void setDrivenToDriverMap(Map drivenToDriverMap) {
        this.drivenToDriverMap = drivenToDriverMap;
    }
}