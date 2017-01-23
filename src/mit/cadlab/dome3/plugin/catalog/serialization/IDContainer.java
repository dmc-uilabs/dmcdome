package mit.cadlab.dome3.plugin.catalog.serialization;

import mit.cadlab.dome3.plugin.catalog.core.CInterface;
import mit.cadlab.dome3.plugin.catalog.core.CModel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 3.
 */
public class IDContainer {

    private String modelID;

    /** (Map of itf name -> itf ID) */
    private Map itfIDMap;

    /** switchModelParamID */
    //private String switchModelParamID;

    /** (Map of itf name -> switchModelParamID) */
    private Map switchModelParamIDMap;

    /** (Map of itf name -> switchInterfaceParamID) */
    private Map switchInterfaceParamIDMap;

    /** (Map of itf name -> (Map of param name -> interface param ID )) */
    private Map interfaceParamIDMap;

    /** (Map of itf name -> (Map of param name -> model param ID )) */
    private Map modelParamIDMap;
    private String version;
    private String documentation;

    /**
     * used when creating a new model or loading a model from a file
     *
     * when loading a file, an IDContainer instance is created using this constructor,
     * and then setModel(), setInterfaceID(), setSwitchModelParamID(), setSwitchInterfaceParamID(),
     * setModelParameterID(), and setInterfaceParameterID() are called to initialize the IDContainer instance. */
    public IDContainer() {
        itfIDMap = new HashMap();
        interfaceParamIDMap = new HashMap();
        modelParamIDMap = new HashMap();
        switchModelParamIDMap = new HashMap();
        switchInterfaceParamIDMap = new HashMap();
        this.documentation = "";
        this.version = "0.0.0";
    }

    /** used for saving a model as a new file. it will renew all IDs in the model. */
    public void assignRandomIDToAllElements(CModel model) {
        setModelID(DomeSerialization.createUUID());
        //setSwitchModelParamID(DomeSerialization.createUUID());

        Collection interfaces = model.getInterfaceMap().values();
        for (Iterator i = interfaces.iterator(); i.hasNext();) {
            CInterface itf = (CInterface) i.next();

            setInterfaceID(itf.getName(), DomeSerialization.createUUID());
            setSwitchModelParamID(itf.getName(), DomeSerialization.createUUID());
            setSwitchInterfaceParamID(itf.getName(), DomeSerialization.createUUID());

            List iParamNames = itf.getInputParameterNames();
            List oParamNames = itf.getOutputParameterNames();
            for (int j = 0; j < iParamNames.size(); j++) {
                String paramName = (String) iParamNames.get(j);
                setInterfaceParamID(itf.getName(), paramName, DomeSerialization.createUUID());
                setModelParamID(itf.getName(), paramName, DomeSerialization.createUUID());
            }
            for (int j = 0; j < oParamNames.size(); j++) {
                String paramName = (String) oParamNames.get(j);
                setInterfaceParamID(itf.getName(), paramName, DomeSerialization.createUUID());
                setModelParamID(itf.getName(), paramName, DomeSerialization.createUUID());
            }
        }
    }

    /** used for saving a model. it will keep IDs of pre-existing elements and assign IDs for newly added elements in the model. */
    public void assignRandomIDToNewElements(CModel model) {
        if (getModelID() == null) {
            setModelID(DomeSerialization.createUUID());
        }

        Collection interfaces = model.getInterfaceMap().values();
        for (Iterator i = interfaces.iterator(); i.hasNext();) {
            CInterface itf = (CInterface) i.next();

            /* for new interface, assign UUID */
            if (getInterfaceID(itf.getName()) ==  null) {
                setInterfaceID(itf.getName(), DomeSerialization.createUUID());
                setSwitchInterfaceParamID(itf.getName(), DomeSerialization.createUUID());
                setSwitchModelParamID(itf.getName(), DomeSerialization.createUUID());
            }

            List iParamNames = itf.getInputParameterNames();
            List oParamNames = itf.getOutputParameterNames();
            for (int j = 0; j < iParamNames.size(); j++) {
                String paramName = (String) iParamNames.get(j);
                /* for new param, assign UUID */
                if (getInterfaceParamID(itf.getName(), paramName) ==  null) {
                    setInterfaceParamID(itf.getName(), paramName, DomeSerialization.createUUID());
                    setModelParamID(itf.getName(), paramName, DomeSerialization.createUUID());
                }
            }
            for (int j = 0; j < oParamNames.size(); j++) {
                String paramName = (String) oParamNames.get(j);
                /* for new param, assign UUID */
                if (getInterfaceParamID(itf.getName(), paramName) ==  null) {
                    setInterfaceParamID(itf.getName(), paramName, DomeSerialization.createUUID());
                    setModelParamID(itf.getName(), paramName, DomeSerialization.createUUID());
                }
            }
        }
    }

//    /** new random IDs are assigned to all IDs in this instance */
//    public void randomizeIDsForSaveAs() {
//        modelID = DomeSerialization.createUUID();
//        switchModelParamID = DomeSerialization.createUUID();
//        for (Iterator i = itfIDMap.entrySet().iterator(); i.hasNext();) {
//            Map.Entry entry = (Map.Entry) i.next();
//            entry.setValue(DomeSerialization.createUUID());
//        }
//
//        for (Iterator i = interfaceParamIDMap.values().iterator(); i.hasNext(); ) {
//            Map idMap = (Map) i.next();
//            for (Iterator j = idMap.entrySet().iterator(); j.hasNext(); ) {
//                Map.Entry jEntry = (Map.Entry) j.next();
//                jEntry.setValue(DomeSerialization.createUUID());
//            }
//        }
//
//        for (Iterator i = modelParamIDMap.values().iterator(); i.hasNext(); ) {
//            Map idMap = (Map) i.next();
//            for (Iterator j = idMap.entrySet().iterator(); j.hasNext(); ) {
//                Map.Entry jEntry = (Map.Entry) j.next();
//                jEntry.setValue(DomeSerialization.createUUID());
//            }
//        }
//
//        for (Iterator i = switchInterfaceParamIDMap.entrySet().iterator(); i.hasNext(); ) {
//            Map.Entry entry = (Map.Entry) i.next();
//            entry.setValue(DomeSerialization.createUUID());
//        }
//
//        this.version = "0.0.0";
//    }

    public Set getInterfaceNames() {
        return itfIDMap.keySet();
    }

    public String getInterfaceID(String itfName) {
        return (String) itfIDMap.get(itfName);
    }

    public void setInterfaceID(String itfName, String ID) {
        itfIDMap.put(itfName, ID);
    }

    /** [for loading] set interface param ID */
    public void setInterfaceParamID(String itfName, String paramName, String ID) {
        Map idMap = (Map) interfaceParamIDMap.get(itfName);
        if (idMap == null) {
            idMap = new HashMap();
            interfaceParamIDMap.put(itfName, idMap);
        }
        idMap.put(paramName, ID);
    }

    /** [for loading] set model param ID */
    public void setModelParamID(String itfName, String paramName, String ID) {
        Map idMap = (Map) modelParamIDMap.get(itfName);
        if (idMap == null) {
            idMap = new HashMap();
            modelParamIDMap.put(itfName, idMap);
        }
        idMap.put(paramName, ID);
    }

    public void setSwitchModelParamID(String itfName, String implSwitchID) {
        //this.switchModelParamID = implSwitchID;
        this.switchModelParamIDMap.put(itfName, implSwitchID);
    }

    public String getSwitchModelParamID(String itfName) {
        return (String) switchModelParamIDMap.get(itfName);
    }

    public void setSwitchInterfaceParamID(String itfName, String implSwitchID) {
        this.switchInterfaceParamIDMap.put(itfName, implSwitchID);
    }

    public String getSwitchInterfaceParamID(String itfName) {
        return (String) switchInterfaceParamIDMap.get(itfName);
    }


    /** return interface parameter ID for "interface 'itfA' / parameter 'param X'" */
    public String getInterfaceParamID(String itfName, String paramName) {
        if (interfaceParamIDMap.get(itfName) == null) {
            return null;
        }
        return (String) ((Map) interfaceParamIDMap.get(itfName)).get(paramName);
    }

    /** return model parameter ID for "itfA/param X" */
    public String getModelParamID(String itfName, String paramName) {
        if (modelParamIDMap.get(itfName) == null) {
            return null;
        }
        return (String) ((Map) modelParamIDMap.get(itfName)).get(paramName);
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    /** invoked before serialization so that IDContainer can provide IDs for all interfaces and params */
    public void synchronize(CModel model) {
        Set itfNames = model.getInterfaceMap().keySet();

        /* remove invalid itf names and invalid param names. we iterate through interfaceParamIDMap and modelParamIDMap as we check if the key of those maps exist in CModel. */
        Set entrySet = null;
        entrySet = interfaceParamIDMap.entrySet();
        for (Iterator i = entrySet.iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            if (! itfNames.contains(entry.getKey())) {
                i.remove();
            } else {
                String itfName = (String) entry.getKey();
                Map idMap = (Map) entry.getValue();
                CInterface itf = model.getInterface(itfName);
                List paramNames = itf.getInputParameterNames();
                paramNames.addAll(itf.getOutputParameterNames());
                for (Iterator j = idMap.entrySet().iterator(); j.hasNext(); ) {
                    Map.Entry jEntry = (Map.Entry) j.next();
                    if (! paramNames.contains(jEntry.getKey())) {
                        j.remove();
                    }
                }
            }
        }

        entrySet = modelParamIDMap.entrySet();
        for (Iterator i = entrySet.iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            if (! itfNames.contains(entry.getKey())) {
                i.remove();
            } else {
                String itfName = (String) entry.getKey();
                Map idMap = (Map) entry.getValue();
                CInterface itf = model.getInterface(itfName);
                List paramNames = itf.getInputParameterNames();
                paramNames.addAll(itf.getOutputParameterNames());
                for (Iterator j = idMap.entrySet().iterator(); j.hasNext(); ) {
                    Map.Entry jEntry = (Map.Entry) j.next();
                    if (! paramNames.contains(jEntry.getKey())) {
                        j.remove();
                    }
                }
            }
        }

        /* add newly found itf names and param names in each of the interfaces */
        for (Iterator i = itfNames.iterator(); i.hasNext();) {
            String itfName = (String) i.next();
            Map idMap = (Map) interfaceParamIDMap.get(itfName);
            if (idMap == null) {
                idMap = new HashMap();
                CInterface itf = model.getInterface(itfName);
                List paramNames = itf.getInputParameterNames();
                paramNames.addAll(itf.getOutputParameterNames());
                for (Iterator j = paramNames.iterator(); j.hasNext();) {
                    String paramName = (String) j.next();
                    idMap.put(paramName, DomeSerialization.createUUID());
                }
            } else {
                CInterface itf = model.getInterface(itfName);
                List paramNames = itf.getInputParameterNames();
                paramNames.addAll(itf.getOutputParameterNames());
                for (Iterator j = paramNames.iterator(); j.hasNext();) {
                    String paramName = (String) j.next();
                    if (idMap.get(paramName) == null) {
                        idMap.put(paramName, DomeSerialization.createUUID());
                    }
                }
            }
        }

        for (Iterator i = itfNames.iterator(); i.hasNext();) {
            String itfName = (String) i.next();
            Map idMap = (Map) modelParamIDMap.get(itfName);
            if (idMap == null) {
                idMap = new HashMap();
                CInterface itf = model.getInterface(itfName);
                List paramNames = itf.getInputParameterNames();
                paramNames.addAll(itf.getOutputParameterNames());
                for (Iterator j = paramNames.iterator(); j.hasNext();) {
                    String paramName = (String) j.next();
                    idMap.put(paramName, DomeSerialization.createUUID());
                }
            } else {
                CInterface itf = model.getInterface(itfName);
                List paramNames = itf.getInputParameterNames();
                paramNames.addAll(itf.getOutputParameterNames());
                for (Iterator j = paramNames.iterator(); j.hasNext();) {
                    String paramName = (String) j.next();
                    if (idMap.get(paramName) == null) {
                        idMap.put(paramName, DomeSerialization.createUUID());
                    }
                }
            }
        }
    }

    public String toString() {
        return "[IDContainer: model ID=" + modelID + ", itf ID map=" + itfIDMap + ", impl switch ID map=" + switchModelParamIDMap + ", itf param ID map=" + interfaceParamIDMap + ", model param ID map=" + modelParamIDMap + "]";
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /** in order to parse version string
     * @see IDContainer#parseVersion(String) */
    public void setVersion(String version) {
        this.version = version;
    }

    /** in order to parse version string
     * @see IDContainer#parseVersion(String) */
    public String getVersion() {
        if (version == null) {
            return "0.0.0";
        }
        return version;
    }

    public void increaseVersion() {
        int[] versionInts = IDContainer.parseVersion(version);
        version = versionInts[0] + "." + versionInts[1] + "." + (versionInts[2] + 1);
    }

    /** parse 0.0.1 into int[] {0,0,1} */
    public static int[] parseVersion(String version) {
        Pattern p = Pattern.compile("(\\d)\\.(\\d).(\\d)");
        Matcher m = p.matcher(version);
        boolean found = m.find();
        if (! found) {
            throw new RuntimeException("invalid version format: " + version);
        } else {
            return new int[] { Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)) };
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IDContainer)) return false;

        final IDContainer idContainer = (IDContainer) o;

        if (!interfaceParamIDMap.equals(idContainer.interfaceParamIDMap)) return false;
        if (!itfIDMap.equals(idContainer.itfIDMap)) return false;
        if (!modelID.equals(idContainer.modelID)) return false;
        if (!modelParamIDMap.equals(idContainer.modelParamIDMap)) return false;
        if (!switchInterfaceParamIDMap.equals(idContainer.switchInterfaceParamIDMap)) return false;
        if (!switchModelParamIDMap.equals(idContainer.switchModelParamIDMap)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = modelID.hashCode();
        result = 29 * result + itfIDMap.hashCode();
        result = 29 * result + switchModelParamIDMap.hashCode();
        result = 29 * result + switchInterfaceParamIDMap.hashCode();
        result = 29 * result + interfaceParamIDMap.hashCode();
        result = 29 * result + modelParamIDMap.hashCode();
        return result;
    }
}
