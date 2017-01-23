package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.gui.permission.PermissionUtils;

import java.util.*;

/**
 * DomeModel is a static representation of Dome Model existing in the server.
 * User: Sangmok Han
 * Date: 2005. 1. 18.
 */
public class DomeModel implements DomeSimulation {

    private DomeConnection domeConn;
    private String modelId; // model id (ex) 101d8dd5-bba1-1004-8a1f-bfbdca7cf0cf
    private String modelName; // model name (ex) Bike Cost Model
    private String description; // model description (ex) (ex) This model gives the cost and price for a bike.
    private String modelType; // DOME or something else (?)
    private Date lastModified;
    private int version;
    private List interfaceList = null;

    private boolean isProjectResource; // If DomeModel is a resource of a DomeProject, this variable is initalized as true when it is contructed within DomeProject.
    private RuntimePlayspace deployedPlayspace;


    /**
     *
     * @param modelId
     * @param modelName
     * @param description
     * @param modelType
     * @param lastModified
     * @param version
     * @param deployedPlayspace If the DomeModel instance is supposed to stay in the transient playspace, this argument should be left null. If the DomeProject instance is supposed to stay in a specific playspace (=deployed playspace), this argument takes that playspace instance.
     * @param domeConn
     */
    public DomeModel(String modelId, String modelName, String description, String modelType, Date lastModified, int version, boolean isProjectResource, RuntimePlayspace deployedPlayspace, DomeConnection domeConn) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.description = description;
        this.modelType = modelType;
        this.lastModified = lastModified;
        this.version = version;
        this.domeConn = domeConn;

        this.deployedPlayspace = deployedPlayspace;
        this.isProjectResource = isProjectResource;
    }

    /**
     * look up all interfaces in the model and instantiate them in the interface list.
     */
    synchronized private void initInterfaceList() {
        interfaceList = new ArrayList();
        Vector vecInterface = FileSystemFunctions.getAvailableInterfacesInfo(domeConn.getServerConnection(), modelId, PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
        for (int i = 0; i < vecInterface.size(); i++) {
            Vector anInterface = (Vector) vecInterface.get(i);
            String interfaceName = (String) anInterface.get(0); // interface name (ex) Bike Cost Default Interface
            String interfaceId = (String) anInterface.get(1); // interface id (ex) 101d8dd6-bba1-1004-8a1f-bfbdca7cf0cf
            String description = (String) anInterface.get(2); // interface description
            int version = ((Integer) anInterface.get(3)).intValue(); // interface version
            Date lastModified = (Date) anInterface.get(4); // last modified date
            interfaceList.add(new DomeInterface(interfaceName, interfaceId, description, version, lastModified, this, domeConn));
        }
    }

    public String getModelId() {
        return modelId;
    }

    public String getModelName() {
        return modelName;
    }

    /** returns model id */
    public String getSimulationId() {
        return modelId;
    }

    /** returns model name */
    public String getSimulationName() {
        return modelName;
    }

    public String getDescription() {
        return description;
    }

    public String getModelType() {
        return modelType;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public int getVersion() {
        return version;
    }

    public String toString() {
        return "[DOME MODEL: '" + modelName + "' (ID = " + modelId + "), description = " + description + ", model type = " + modelType + ", last modified at = " + lastModified + ", version = " + version + ", in transient playspace = " + isInTransientPlayspace() + "]";
    }

    /**
     * retrieve the list of DomeInterface instances in this model.
     */
    synchronized public List getInterfaces() {
        if (interfaceList == null) {
            initInterfaceList();
        }
        return interfaceList;
    }

    /**
     * retrieve the DomeInterface instance with the given name. returns null when there is no match.
     */
    synchronized public DomeInterface getInterfaceById(String findingInterfaceId) {
        if (interfaceList == null) {
            initInterfaceList();
        }
        for (Iterator i = interfaceList.iterator(); i.hasNext(); ) {
            DomeInterface anInterface = (DomeInterface) i.next();
            if(anInterface.getInterfaceId().equals(findingInterfaceId)) {
                return anInterface;
            }
        }
        return null;
    }

    /**
     * retrieve the DomeInterface instance with the given name. returns null when there is no match.
     */
    synchronized public DomeInterface getInterfaceByName(String findingInterfaceName) {
        if (interfaceList == null) {
            initInterfaceList();
        }
        for (Iterator i = interfaceList.iterator(); i.hasNext(); ) {
            DomeInterface anInterface = (DomeInterface) i.next();
            if(anInterface.getInterfaceName().equals(findingInterfaceName)) {
                return anInterface;
            }
        }
        return null;
    }

    /**
     * If the DomeModel is supposed to stay in and create runtime interfaces in a specific playspace (= deployed playspace),
     * this method returns RuntimePlayspace instance of that playspace.
     * If the DomeModel has no specific playspace and stays in transient playspace,
     * this method returns null.
     * Note that even after DomeProject has created a runtime interface running in a transient playspace
     * this method returns null. If you want to access that transient playspace, use getPlayspace() of RuntimeInterface.
     */
    public RuntimePlayspace getDeployedPlayspace() {
        return deployedPlayspace;
    }

    /**
     * @return if runtime playspace is null, this method returns true.
     */
    public boolean isInTransientPlayspace() {
        return (deployedPlayspace == null);
    }

    /**
     * returns if the DomeModel is a resource of DomeProject
     */
    public boolean isProjectResource() {
        return isProjectResource;
    }

    public String getType() {
        return getModelType();
    }
}