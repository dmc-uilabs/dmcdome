package mit.cadlab.dome3.plugin.catalog.core;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CRelationOutputParameter extends CParameter {
    private List derivedParamNames;
    private String relationName;
    private CNamingService namingService;

    /**
     *
     * @param name
     * @param relationName qualified name of its parent relation
     * @param namingService
     */
    public CRelationOutputParameter(String name, String relationName, CNamingService namingService) {
        super(relationName, name);
        this.relationName = relationName;
        this.namingService = namingService;
        derivedParamNames = new ArrayList();
    }

    /**
     *    X        -> [ Itf   ]  ->      Y
     * --------------------------------------
     *    X        -> [ Imp   ]  ->      Y
     *                                 [=F+1]
     * --------------------------------------
     *    A    B   -> [ Rel 1 ]  -> C
     *  [=X] [=A*2]
     *    D    E   -> [ Rel 2 ]  -> F  F1
     *  [=C*2]
     *
     * To find driver of C, (=calling getDriversOf(false) of relation output param)
     * 1) we prepare a empty list 'ret'
     * 2) get reference to 'Rel 1', and call getDrivers of 'C'; we get list of relation input params(='A' and 'B')
     * 3) add 'A' and 'B' to 'ret'
     * 4) call getDriversOf(false) for 'A' and 'B', and then union the results
     *
     * to find drivers of A( or B), (=calling getDriversOf(false) of relation input parameter)
     * 1) we prepare a empty list 'ret'
     * 2) get reference to Mapper of 'A', and call getInputNodes()
     * 3) for each input node call getMappedParameter().
     * 4) if the MappedParameter is null, nothing added to 'ret'
     *    if the MappedParameter is interface input param, MappedParameter is added to 'ret'
     *    if the MappedParameter is relation input/output param, MappedParameter is added to 'ret' and MappedParameter.getDriversOf(false) is added to 'ret'
     *
     * to find drivers of Y, (=calling getDriversOf(false) of interface output param)
     * 1) we prepare a empty list 'ret'
     * 2) get reference to Mapper of 'A', and call getInputNodes()
     * 3) for each input node call getMappedParameter().
     * 4) if the MappedParameter is null, nothing added to 'ret'
     *    if the MappedParameter is interface input param, MappedParameter is added to 'ret'
     *    if the MappedParameter is interface output param, MappedParameter is added to 'ret' and MappedParameter.getDriversOf(false) is added to 'ret' (new in 2006)
     *    if the MappedParameter is relation input/output param, MappedParameter is added to 'ret' and MappedParameter.getDriversOf(false) is added to 'ret'
     *
     *
     *
     * @param findOnlyInParentRelation
     * @return qualified name set of drivers
     */
    public Set getDriversOf(boolean findOnlyInParentRelation) {
        Set ret = new HashSet();
        Set drivers = getRelation().getDriversOf(getName());
        for (Iterator i = drivers.iterator(); i.hasNext(); ) {
            String driverName = (String) i.next();
            String qualifiedDriverName = getRelation().getQualifiedName() + "." + driverName;
            ret.add(qualifiedDriverName);
            CRelationInputParameter riParam = namingService.getRelationInputParameter(qualifiedDriverName);
            ret.addAll(riParam.getDriversOf(false));
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

    public CRelation getRelation() {
        return namingService.getRelation(relationName);
    }

    public CDerivedParameter addDerivedParameter(String paramName) {
        CDerivedParameter derivedParam = new CDerivedParameter(paramName, this.getQualifiedName(), namingService);
        derivedParamNames.add(derivedParam.getQualifiedName());
        namingService.register(derivedParam.getQualifiedName(), derivedParam);
        return derivedParam;
    }

    /**
     * derived parameter names are stored as qualified parameter name
     * @return
     */
    public List getDerivedParamNames() {
        return derivedParamNames;
    }

    /**
     *    X        -> [ Itf   ]  ->      Y       Z
     * ----------------------------------------------
     *    X        -> [ Imp   ]  ->      Y       Z
     *                                 [=F+1]   [=Y*2]
     * ----------------------------------------------
     *    A    B   -> [ Rel 1 ]  -> C
     *  [=X] [=A*2]
     *    D    E   -> [ Rel 2 ]  -> F  F1
     *  [=C*2]
     *
     * To find driven of Y, (=calling getDrivensOf(false) of interface output param)
     * 1) we prepare a empty list 'ret'
     * 2) iterate through all relation input parameters and add them to the list if their mapping's input nodes are mapped to Y
     * 3) and addAll() the result of getDrivenBy(false) call to those relation input parameter.
     * 4) iterate through all interface output parameters, and add them to the list if their mapping's input nodes are mapped to C
     * 5) and addAll() the result of getDrivenBy(false) call to those interface output parameter. (new in 2006)
     *
     * To find driven of C, (=calling getDrivensOf(false) of relation output param)
     * 1) we prepare a empty list 'ret'
     * 2) iterate through all relation input parameters and add them to the list if their mapping's input nodes are mapped to C
     * 3) and addAll() the result of getDrivenBy(false) call to those relation input parameter.
     * 4) iterate through all interface output parameters, and add them to the list if their mapping's input nodes are mapped to C
     * 5) and addAll() the result of getDrivenBy(false) call to those interface output parameter. (new in 2006)
     *
     * to find drivens of ralation input parameter A, (=calling getDrivensBy(false) of relation input parameter)
     * 1) we prepare a empty list 'ret'
     * 2) get a reference to relation and call getDrivens("A") for the relation
     * 3) for each relation output parameter, call getDrivensBy(false).
     *
     * to find drivens of X, (=calling getDriversOf(false) of interface input param)
     * 1) we prepare a empty list 'ret'
     * 2) iterate through all relation input parameters, and add them to the list if their mapping's input nodes are mapped to X
     * 3) and addAll() the result of getDrivenBy(false) call to those relation input parameter.
     * 4) iterate through all interface output parameters, and add them to the list if their mapping's input nodes are mapped to X
     * 5) and addAll() the result of getDrivenBy(false) call to those interface output parameter. (new in 2006)
     *
     * @param findOnlyInParentRelation
     * @return
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
        return "[ro-param:name=" + getQualifiedName() +", derived=" + derivedParamNames + "]=" + super.toString();
    }

    /**
     * not only it change the naming service of this instance
     * but it also change the naming service in its mapping
     * @param namingService
     */
    public void changeNamingService(CNamingService namingService) {
        this.namingService = namingService;
    }

    public CNamingService getNamingService() {
        return namingService;
    }
}
