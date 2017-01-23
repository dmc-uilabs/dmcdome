package mit.cadlab.dome3.plugin.catalog.core;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CModel {

    public static final String XML_TAG = "cmodel";

    private String name;
    private Map interfaceMap;

    public CModel(String name) {
        this.name = name;
        interfaceMap = new TreeMap();
    }
    
    public CModel() {
        interfaceMap = new HashMap();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** a map from interface name to CInterface instance */
    public Map getInterfaceMap() {
        return interfaceMap;
    }

    public CInterface addInterface(String interfaceName) {
        CInterface itf = new CInterface(interfaceName, this);
        interfaceMap.put(interfaceName, itf);
        return itf;
    }

    public void renameInterfaceName(String oldItfName, String newItfName) {
        Object obj = interfaceMap.get(oldItfName);
        interfaceMap.remove(oldItfName);
        interfaceMap.put(newItfName, obj);
    }

    public CInterface getInterface(String interfaceName) {
        return (CInterface) interfaceMap.get(interfaceName);
    }

    public void removeInterface(String interfaceName) {
        interfaceMap.remove(interfaceName);
    }

    public String toString() {
        return "[model:name=" + getName() + ", interfaces=" + interfaceMap.toString() + "]";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CModel)) return false;

        final CModel cModel = (CModel) o;

        if (!interfaceMap.equals(cModel.interfaceMap)) return false;
        if (!name.equals(cModel.name)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + interfaceMap.hashCode();
        return result;
    }
}
