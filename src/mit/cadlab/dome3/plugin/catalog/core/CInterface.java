package mit.cadlab.dome3.plugin.catalog.core;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CInterface {
    private String name;
    private List outputParameterList;
    private List inputParameterList;
    private Map implementationMap;

    private Map driverToDrivenMap; // a local name mapping to a set of local names
    private Map drivenToDriverMap; // a local name mapping to a set of local names

    private CModel targetModel;

    /** this contructor is normally called at inside of CModel.addInterface(itfName) because CInterface always belong to a certain model. */
    protected CInterface(String name, CModel targetModel) {
        this.name = name;
        implementationMap = new TreeMap();
        outputParameterList = new ArrayList();
        inputParameterList = new ArrayList();

        this.targetModel = targetModel;

        this.driverToDrivenMap = new HashMap();
        this.drivenToDriverMap = new HashMap();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != null && ! this.name.equals(name)) {
            targetModel.renameInterfaceName(this.name, name);
        }
        this.name = name;
    }


    /** get the reference of the catalog model this interface belongs to */
    public CModel getParentModel() {
        return targetModel;
    }

    public CInterfaceInputParameter addInputParameter(int index, String paramName) {
        CInterfaceInputParameter iiParam = new CInterfaceInputParameter(paramName);
        if (inputParameterList.size() == index) {
            inputParameterList.add(iiParam);
        } else {
            inputParameterList.add(index, iiParam);
        }
        return iiParam;
    }

    /** after calling addInputParameter() and addOutputParameter(),
     * invoke synchronizeInterfaceParametersOfAllImplementations() to ensure that all implementations of the interface have the same parameters.
     * synchronizeInterfaceParametersOfAllImplementations() does not have to be called each time one invokes removeInputParameter() and removeOutputParameter(). One needs to call synchronizeInterfaceParametersOfAllImplementations() after the person finishes modifying the interface */
    public CInterfaceInputParameter addInputParameter(String paramName) {
        CInterfaceInputParameter iiParam = new CInterfaceInputParameter(paramName);
        inputParameterList.add(iiParam);
        return iiParam;
    }

    /** after calling removeInputParameter() and removeOutputParameter(),
     * invoke synchronizeInterfaceParametersOfAllImplementations() to ensure that all implementations of the interface have the same parameters and that no mapping script of all implementations has reference to a removed parameter.
     * synchronizeInterfaceParametersOfAllImplementations() does not have to be called each time one invokes removeInputParameter() and removeOutputParameter(). One needs to call synchronizeInterfaceParametersOfAllImplementations() after the person finishes modifying the interface */
    public void removeInputParameter(String paramName) {
        for (int i = 0; i < inputParameterList.size(); i++) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) inputParameterList.get(i);
            if (param.getName().equals(paramName)) {
                inputParameterList.remove(param);
                removeParameterFromDriverAndDrivenMap(paramName);
                return;
            }
        }
    }

    public CInterfaceOutputParameter addOutputParameter(int index, String paramName) {
        CInterfaceOutputParameter ioParam  = new CInterfaceOutputParameter(paramName);
        if (outputParameterList.size() == index) {
            outputParameterList.add(ioParam);
        } else {
            outputParameterList.add(index, ioParam);
        }
        return ioParam;
    }

    /** after calling addInputParameter() and addOutputParameter(),
     * invoke synchronizeInterfaceParametersOfAllImplementations() to ensure that all implementations of the interface have the same parameters.
     * synchronizeInterfaceParametersOfAllImplementations() does not have to be called each time one invokes removeInputParameter() and removeOutputParameter(). One needs to call synchronizeInterfaceParametersOfAllImplementations() after the person finishes modifying the interface */
    public CInterfaceOutputParameter addOutputParameter(String paramName) {
        CInterfaceOutputParameter ioParam  = new CInterfaceOutputParameter(paramName);
        outputParameterList.add(ioParam);
        return ioParam;
    }

    /** after calling removeInputParameter() and removeOutputParameter(),
     * invoke synchronizeInterfaceParametersOfAllImplementations() to ensure that all implementations of the interface have the same parameters and that no mapping script of all implementations has reference to a removed parameter.
     * synchronizeInterfaceParametersOfAllImplementations() does not have to be called each time one invokes removeInputParameter() and removeOutputParameter(). One needs to call synchronizeInterfaceParametersOfAllImplementations() after the person finishes modifying the interface */
    public void removeOutputParameter(String paramName) {
        for (int i = 0; i < outputParameterList.size(); i++) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) outputParameterList.get(i);
            if (param.getName().equals(paramName)) {
                outputParameterList.remove(param);
                removeParameterFromDriverAndDrivenMap(paramName);
                return;
            }
        }
    }

    /** after calling removeAllInputParameters() and removeAllOutputParameters(),
     * invoke synchronizeInterfaceParametersOfAllImplementations() to ensure that all implementations of the interface have the same parameters and that no mapping script of all implementations has reference to a removed parameter.
     * synchronizeInterfaceParametersOfAllImplementations() does not have to be called each time one invokes removeInputParameter() and removeOutputParameter(). One needs to call synchronizeInterfaceParametersOfAllImplementations() after the person finishes modifying the interface */
    public void removeAllInputParameters() {
        inputParameterList.clear();
        drivenToDriverMap.clear();
        driverToDrivenMap.clear();
    }

    /** after calling removeAllInputParameters() and removeAllOutputParameters(),
     * invoke synchronizeInterfaceParametersOfAllImplementations() to ensure that all implementations of the interface have the same parameters and that no mapping script of all implementations has reference to a removed parameter.
     * synchronizeInterfaceParametersOfAllImplementations() does not have to be called each time one invokes removeInputParameter() and removeOutputParameter(). One needs to call synchronizeInterfaceParametersOfAllImplementations() after the person finishes modifying the interface */
    public void removeAllOutputParameters() {
        outputParameterList.clear();
        drivenToDriverMap.clear();
        driverToDrivenMap.clear();
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

    /** after adding and removing a parameter of an interface, this method should be invoked
     * to ensure that all implementations of the interface have the same parameters and that no mapping script of all implementations has reference to a removed parameter */
    public void synchronizeInterfaceParametersOfAllImplementations() {
        for (Iterator i = getImplementationMap().values().iterator(); i.hasNext(); ) {
            CImplementation impl = (CImplementation) i.next();
            impl.synchronizeInterfaceParameters();
        }
    }

    /** a CInterfaceInputParameter instance list of this CInterface */
    public List getInputParameterList() {
        return inputParameterList;
    }

    /** a CInterfaceOutputParameter instance list of this CInterface */
    public List getOutputParameterList() {
        return outputParameterList;
    }

    /** a name list of CInterfaceInputParameter's in this CInterface */
    public List getInputParameterNames() {
        List ret = new ArrayList();
        for (Iterator i = inputParameterList.iterator(); i.hasNext();) {
            CInterfaceInputParameter iiParam = (CInterfaceInputParameter) i.next();
            ret.add(iiParam.getName());
        }
        return ret;
    }

    /** a name list of CInterfaceOutputParameter's in this CInterface */
    public List getOutputParameterNames() {
        List ret = new ArrayList();
        for (Iterator i = outputParameterList.iterator(); i.hasNext();) {
            CInterfaceOutputParameter ioParam = (CInterfaceOutputParameter) i.next();
            ret.add(ioParam.getName());
        }
        return ret;
    }

    public CImplementation addImplementation(String implementationName) {
        CImplementation implementation = new CImplementation(implementationName, this);
        implementation.synchronizeInterfaceParameters();
        implementationMap.put(implementation.getName(), implementation);
        return implementation;
    }

    public CImplementation getImplementation(String implementationName) {
        return (CImplementation) implementationMap.get(implementationName);
    }

    public void removeImplementation(String implementationName) {
        implementationMap.remove(implementationName);
    }

    public Map getImplementationMap() {
        return implementationMap;
    }

    public void renameImplementation(String oldImplName, String newImplName) {
        Object obj = implementationMap.get(oldImplName);
        implementationMap.remove(oldImplName);
        implementationMap.put(newImplName, obj);
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
        return "[itf:name=" + getName() + ", input=" + inputParameterList + ", output=" + outputParameterList + ", driver->drivens=" + driveStr + ", driven<-drivers=" + drivenStr + ", implementation=" + implementationMap + "]";
    }

    /** check if paramName is a name of the input parameter or a name of the output parameter (ex) isInputParameter("width"); */
    public boolean isInputParameter(String paramName) {
        if (getInputParameter(paramName) != null) {
            return true;
        }
        if (getOutputParameter(paramName) != null) {
            return false;
        }
        throw new RuntimeException("no such parameter found in the CInterface : " + paramName);
    }

    /** returns CParameter with a given name. returns null if no match found. */
    public CParameter getParameter(String paramName) {
        CParameter param = getInputParameter(paramName);
        if (param != null) {
            return param;
        } else {
            return getOutputParameter(paramName);
        }
    }

    /** returns CInterfaceInputParameter with a given name. returns null if no match found. */
    public CInterfaceInputParameter getInputParameter(String paramName) {
        for (int i = 0; i < inputParameterList.size(); i++) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) inputParameterList.get(i);
            if (paramName.equals(param.getName())) {
                return param;
            }
        }
        return null;
    }

    /** returns CInterfaceOutputParameter with a given name. returns null if no match found. */
    public CInterfaceOutputParameter getOutputParameter(String paramName) {
        for (int i = 0; i < outputParameterList.size(); i++) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) outputParameterList.get(i);
            if (paramName.equals(param.getName())) {
                return param;
            }
        }
        return null;
    }

    /** invoked only by clone() of this class */
    protected Map getDriverToDrivenMap() {
        return driverToDrivenMap;
    }

    /** invoked only by clone() of this class */
    protected Map getDrivenToDriverMap() {
        return drivenToDriverMap;
    }

    public void clearDependency() {
        driverToDrivenMap.clear();
        drivenToDriverMap.clear();
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

    /** clear the current dependency of this CInterface, and set up dependencies by summing up dependencies of implementations */
    public void setDependenciesFromImplementations() {
        drivenToDriverMap.clear();

        /* set up dependencies */
        for (Iterator k = this.getImplementationMap().values().iterator(); k.hasNext(); ) {
            CImplementation impl = (CImplementation) k.next();
            for (Iterator i = impl.getInputParameterList().iterator(); i.hasNext(); ) {
                CInterfaceInputParameter iParam = (CInterfaceInputParameter) i.next();
                for (Iterator j = impl.getOutputParameterList().iterator(); j.hasNext(); ) {
                    CInterfaceOutputParameter oParam = (CInterfaceOutputParameter) j.next();
                    if (oParam.getDriversOf(true).contains(iParam.getQualifiedName())) {
                        this.setDependency(iParam.getName(), oParam.getName());
                    }
                }
            }
        }
    }

    /** returns local name set of drivers.
     * note!!! unlike CInterfaceOutputParameter.getDriversOf(),
     *         it returns local name set, which is unqualified and without "namespace." in front of local name. */
    public Set getDriversOf(String drivenParamName) {
        Set ret = (Set) drivenToDriverMap.get(drivenParamName);
        return (ret == null) ? Collections.EMPTY_SET : ret;
    }
    /** returns local name set of drivens. please see a note on CImplementation.getDriversOf() */
    public Set getDrivensBy(String driverParamName) {
        Set ret = (Set) driverToDrivenMap.get(driverParamName);
        return (ret == null) ? Collections.EMPTY_SET : ret;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CInterface)) return false;

        final CInterface cInterface = (CInterface) o;

        if (!drivenToDriverMap.equals(cInterface.drivenToDriverMap)) return false;
        if (!driverToDrivenMap.equals(cInterface.driverToDrivenMap)) return false;
        if (!implementationMap.equals(cInterface.implementationMap)) return false;
        if (!inputParameterList.equals(cInterface.inputParameterList)) return false;
        if (!name.equals(cInterface.name)) return false;
        if (!outputParameterList.equals(cInterface.outputParameterList)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + outputParameterList.hashCode();
        result = 29 * result + inputParameterList.hashCode();
        result = 29 * result + implementationMap.hashCode();
        result = 29 * result + driverToDrivenMap.hashCode();
        result = 29 * result + drivenToDriverMap.hashCode();
        return result;
    }
}
