package mit.cadlab.dome3.plugin.catalog.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CRelationInputParameter extends CParameter {
    private CMapping mapping;
    private String relationName;
    private CNamingService namingService;

    /**
     * only invoked by CRelation.addInputParameter()
     * @param name
     * @param relationName qualified relation name
     * @param namingService
     */
    public CRelationInputParameter(String name, String relationName, CNamingService namingService) {
        super(relationName, name);
        this.relationName = relationName;
        this.namingService = namingService;
        mapping = new CMapping(this.getQualifiedName(), namingService);
    }

    public CRelation getRelation() {
        return namingService.getRelation(relationName);
    }

    public CMapping getMapping() {
        return mapping;
    }

    /** a boolean argument findOnlyInParentRelation gives an option to colllect parameters in the same relation */
    public Set getDriversOf(boolean findOnlyInParentRelation) {
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
            } else if (param instanceof CRelationInputParameter) {
                ret.add(param.getQualifiedName());
                ret.addAll(((CRelationInputParameter) param).getDriversOf(false));
            } else if (param instanceof CRelationOutputParameter) {
                ret.add(param.getQualifiedName());
                ret.addAll(((CRelationOutputParameter) param).getDriversOf(false));
            }
        }

        /* here remove what are not relevant */
        if (findOnlyInParentRelation) {
            for (Iterator i = ret.iterator(); i.hasNext(); ) {
                String driverParamName = (String) i.next();
                CParameter driverParam = namingService.getParameter(driverParamName);
                if (driverParam instanceof CRelationInputParameter && ((CRelationInputParameter) driverParam).getRelation().equals(this.getRelation())) {
                    /* does nothing to retain the driver param in the set */
                } else if (driverParam instanceof CRelationOutputParameter && ((CRelationOutputParameter) driverParam).getRelation().equals(this.getRelation())) {
                    /* does nothing to retain the driver param in the set */
                } else {
                    i.remove();
                }
            }
        }

        return ret;
    }

    public Set getDrivensBy(boolean findOnlyInParentRelation) {
        Set ret = new HashSet();
        Set drivens = getRelation().getDrivensBy(this.getName());
        for (Iterator i = drivens.iterator(); i.hasNext(); ) {
            String drivenName = (String) i.next();
            String qualifiedDrivenName = getRelation().getQualifiedName() + "." + drivenName;
            CRelationOutputParameter roParam = namingService.getRelationOutputParameter(qualifiedDrivenName);
            ret.add(qualifiedDrivenName);
            ret.addAll(roParam.getDerivedParamNames());
            ret.addAll(roParam.getDrivensBy(false));
        }

        /* relation input parameter can be an input node of mappings
           for this case, we do the same treatment of relation output parameter */
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
                if (driverParam instanceof CRelationInputParameter && ((CRelationInputParameter) driverParam).getRelation().equals(this.getRelation())) {
                    /* does nothing to retain the driver param in the set */
                } else if (driverParam instanceof CRelationOutputParameter && ((CRelationOutputParameter) driverParam).getRelation().equals(this.getRelation())) {
                    /* does nothing to retain the driver param in the set */
                } else {
                    i.remove();
                }
            }
        }

        return ret;
    }

    public String toString() {
        return "[ri-param:name=" + getQualifiedName() +", mapping=" + mapping + "]=" + super.toString();
    }

    /**
     * make a copy of this instance
     * since it is doing a deep copying name, namespace, data type, status, unit, and value except for the namingService,
     * every change to those is made to a clone instance will not affect the source of the copy, except for the namingService
     */
    public Object clone() {
        /* name, relation name, and naming service is copied. CMapping instance is created in the contructor
           CMapping is initialized to this output parameter as its output nodes
           we already copied all member variables except for CMapping (called as copiedMapping in the below)
           now what's not copied to CMapping are inputNodes and mappingScript */
        CRelationInputParameter ret = new CRelationInputParameter(getName(), relationName, namingService);
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


}
