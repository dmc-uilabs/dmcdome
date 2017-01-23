package mit.cadlab.dome3.plugin.catalog.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CInterfaceOutputParameter extends CParameter {
    private CMapping mapping;
    private CImplementation implementation;
    private CNamingService namingService;

    /* only invoked by CInterface. this constructor is called when CInterface.addImplementation() is called */
    protected CInterfaceOutputParameter(String name, CImplementation implementation, CNamingService namingService) {
        super(CConstant.ITF_ALIAS, name); // interface input/output parameter always uses namespace of 'itf'
        this.implementation = implementation;
        this.namingService = namingService;
        mapping = new CMapping(this.getQualifiedName(), namingService);
    }

    /* only invoked by CInterface. CInterface.addInterfaceOutputParameter() used to instantiate a new CInterfaceOutputParameter */
    protected CInterfaceOutputParameter(String name) {
        /* interface is at the top level of namespace */
        super(CConstant.ITF_ALIAS, name);
    }

    public CMapping getMapping() {
        return mapping;
    }

    public CImplementation getImplementation() {
        return implementation;
    }

    public void setMapping(CMapping mapping) {
        this.mapping = mapping;
    }

    /** returns qualified name set of drivers */
    public Set getDriversOf(boolean findOnlyInParentImplementation) {
        Set ret = new HashSet();

        Set inputNodes = getMapping().getInputNodes();
        for (Iterator i = inputNodes.iterator(); i.hasNext(); ) {
            CMappingNode mappingNode = (CMappingNode) i.next();
            CParameter param = namingService.getParameter(mappingNode.getMappedParameterName());
            /* param can be null, CInterfaceInputParameter, CRelationInputParameter or CRelationOutputParameter */
            if (param == null) {
                /* nothing added */
            } else if (param instanceof CInterfaceInputParameter) {
                ret.add(param.getQualifiedName());
            } else if (param instanceof CInterfaceOutputParameter) {
                ret.add(param.getQualifiedName());
                ret.addAll(((CInterfaceOutputParameter) param).getDriversOf(false));
            } else if (param instanceof CRelationInputParameter) {
                ret.add(param.getQualifiedName());
                ret.addAll(((CRelationInputParameter) param).getDriversOf(false));
            } else if (param instanceof CRelationOutputParameter) {
                ret.add(param.getQualifiedName());
                ret.addAll(((CRelationOutputParameter) param).getDriversOf(false));
            }
        }

        /* here remove what are not relevant */
        if (findOnlyInParentImplementation) {
            for (Iterator i = ret.iterator(); i.hasNext(); ) {
                String driverParamName = (String) i.next();
                CParameter driverParam = namingService.getParameter(driverParamName);
                if (driverParam instanceof CInterfaceInputParameter) {
                    /* does nothing to retain the driver param in the set */
                } else if (driverParam instanceof CInterfaceOutputParameter) {
                    /* does nothing to retain the driver param in the set */
                } else {
                    i.remove();
                }
            }
        }

        return ret;
    }

    public String toString() {
        return "[io-param:name=" + getQualifiedName() +", mapping=" + mapping + "]=" + super.toString();
    }

    /**
     * make a copy of this instance
     * since it is doing a deep copying name, namespace, data type, status, unit, and value except for the namingService,
     * every change to those is made to a clone instance will not affect the source of the copy, except for the namingService
     */
    public Object clone() {
        /* name and naming service is copied. a reference to implementation is also copied. CMapping instance is created in the contructor
           CMapping is initialized to this output parameter as its output nodes
           we already copied all member variables except for CMapping (called as copiedMapping in the below)
           now what's not copied to CMapping are inputNodes and mappingScript */
        CInterfaceOutputParameter ret = new CInterfaceOutputParameter(getName(), this.implementation, namingService);
        CParameter.copy(this, ret);

        /* get a reference to CMapping, which needs more copying */
        CMapping copiedMapping = ret.getMapping();

        /* make a set of input param names, which are found from CMapping of current instance */
        Set inputParamNames = new HashSet();
        for (Iterator i = mapping.getInputNodes().iterator(); i.hasNext(); ) {
            CMappingNode inputNode = (CMappingNode) i.next();
            inputParamNames.add(inputNode.getMappedParameterName());
        }

        /* setMappingScript will create CMappingNode instances which has a reference to copiedMapping,
           and the CMappingNode will be added to inputNodes of copiedMapping */
        copiedMapping.setMappingScript(mapping.getMappingScript(), inputParamNames);

        return ret;
    }

    /**
     * not only it change the naming service of this instance
     * but it also change the naming service in its mapping
     * @param namingService
     */
    public void changeNamingService(CNamingService namingService) {
        this.namingService = namingService;
        this.mapping.changeNamingService(namingService);
    }

    public CNamingService getNamingService() {
        return namingService;
    }

    /**
     * CRelationOutputParameter.getDrivensBy() explains the algorithm
     * (bolow is copied from it)
     *
     * To find driven of Y, (=calling getDrivensOf(false) of interface output param)
     * 1) we prepare a empty list 'ret'
     * 2) iterate through all relation input parameters and add them to the list if their mapping's input nodes are mapped to Y
     * 3) and addAll() the result of getDrivenBy(false) call to those relation input parameter.
     * 4) iterate through all interface output parameters, and add them to the list if their mapping's input nodes are mapped to C
     * 5) and addAll() the result of getDrivenBy(false) call to those interface output parameter. (new in 2006)
     */
    public Set getDrivensBy(boolean findOnlyInParentRelation) {
        Set ret = new HashSet();
        Collection objects = namingService.getMap().values();
        for (Iterator i = objects.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof CRelationInputParameter) {
                CRelationInputParameter riParam = (CRelationInputParameter) obj;
                for (Iterator j = riParam.getMapping().getInputNodes().iterator(); j.hasNext(); ) {
                    CMappingNode inputNode = (CMappingNode) j.next();
                    if (this.getQualifiedName().equals(inputNode.getMappedParameterName())) {
                        ret.add(riParam.getQualifiedName());
                        ret.addAll(riParam.getDrivensBy(false));

                        /* now that we know riParam is drivens by this relation output param,
                          move onto the next relation input param*/
                        break;
                    }

                }
            }

            if (obj instanceof CInterfaceOutputParameter) {
                CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) obj;
                for (Iterator j = ioParam.getMapping().getInputNodes().iterator(); j.hasNext(); ) {
                    CMappingNode inputNode = (CMappingNode) j.next();
                    if (this.getQualifiedName().equals(inputNode.getMappedParameterName())) {
                        ret.add(ioParam.getQualifiedName());
                        ret.addAll(ioParam.getDrivensBy(false));

                        /* now that we know riParam is drivens by this relation output param,
                          move onto the next relation input param*/
                        break;
                    }
                }
            }
        }

        /* here remove what are not relevant */
        if (findOnlyInParentRelation) {
            for (Iterator i = ret.iterator(); i.hasNext(); ) {
                String driverParamName = (String) i.next();
                CParameter driverParam = namingService.getParameter(driverParamName);
                if (driverParam instanceof CInterfaceInputParameter) {
                    /* does nothing to retain the driver param in the set */
                } else if (driverParam instanceof CInterfaceOutputParameter) {
                    /* does nothing to retain the driver param in the set */
                } else {
                    i.remove();
                }
            }
        }

        return ret;
    }
}
