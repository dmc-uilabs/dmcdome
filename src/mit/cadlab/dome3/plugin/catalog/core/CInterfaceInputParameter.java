package mit.cadlab.dome3.plugin.catalog.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CInterfaceInputParameter extends CParameter {

    private CImplementation implementation;
    private CNamingService namingService;

    /**
     * only invoked by CInterface. this constructor is called when CInterface.addImplementation() is called
     * also invoked by clone() in this class
     */
    protected CInterfaceInputParameter(String name, CImplementation implementation, CNamingService namingService) {
        super(CConstant.ITF_ALIAS, name); // interface input/output parameter always uses namespace of 'itf'
        this.implementation = implementation;
        this.namingService = namingService;
    }

    /* only invoked by CInterface. CInterface.addInterfaceInputParameter() used to instantiate a new CInterfaceInputParameter */
    protected CInterfaceInputParameter(String name) {
        /* interface is at the top level of namespace */
        super(CConstant.ITF_ALIAS, name);
    }

    public CImplementation getImplementation() {
        return implementation;
    }

    /** returns a set of qualified parameter names */
    public Set getDrivensBy(boolean findOnlyInParentImplementation) {
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
        return "[ii-param:name=" + getQualifiedName() +"]=" + super.toString();
    }

    /**
     * make a copy of this instance
     * since it is doing a deep copying name, namespace, data type, status, unit, and value except for the namingService,
     * every change to those is made to a clone instance will not affect the source of the copy, except for the namingService
     */
    public Object clone() {
        CInterfaceInputParameter ret = new CInterfaceInputParameter(getName(), this.implementation, namingService);
        CParameter.copy(this, ret);
        return ret;
    }

    public void changeNamingService(CNamingService namingService) {
        this.namingService = namingService;
    }

    public CNamingService getNamingService() {
        return namingService;
    }
}
