package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.gui.permission.PermissionUtils;

import java.util.*;

/**
 * //TODO: April 1, 2005, RuntimeInterface does not work with a interface of DomeIModel.
 * DomeIModel is a static representation of Dome IModel existing in the server.
 * User: Sangmok Han
 * Date: 2005. 1. 23.
 */
public class DomeIModel implements DomeSimulation {

    public static final String TYPE = "IMODEL";

    private DomeConnection domeConn;
    private String modelId; // model id (ex) 101d8dd5-bba1-1004-8a1f-bfbdca7cf0cf
    private String modelName; // model name (ex) Bike Cost Model
    private String description; // model description (ex) (ex) This model gives the cost and price for a bike.
    private Date lastModified;
    private int version;
    private List interfaceList = new ArrayList();
    private RuntimePlayspace deployedPlayspace;

    /**
     *
     * @param modelId
     * @param modelName
     * @param description
     * @param lastModified
     * @param version
     * @param deployedPlayspace If the DomeIModel instance is supposed to stay in the transient playspace, this argument should be left null. If the DomeProject instance is supposed to stay in a specific playspace (=deployed playspace), this argument takes that playspace instance.
     * @param domeConn
     */
    public DomeIModel(String modelId, String modelName, String description, Date lastModified, int version, RuntimePlayspace deployedPlayspace, DomeConnection domeConn) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.description = description;
        this.lastModified = lastModified;
        this.version = version;
        this.domeConn = domeConn;
        this.deployedPlayspace = deployedPlayspace;

        initInterfaceList();
    }

    /**
     * look up all interfaces in the model and instantiate them in the interface list.
     */
    private void initInterfaceList() {
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

    /** returns i-model id */
    public String getSimulationId() {
        return modelId;
    }

    /** returns i-model name */
    public String getSimulationName() {
        return modelName;
    }


    public String getDescription() {
        return description;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public int getVersion() {
        return version;
    }

    public DomeConnection getDomeConnection() {
        return domeConn;
    }


    public String toString() {
        return "[DOME INTEGRATION MODEL: '" + modelName + "' (ID = " + modelId + "), description = " + description + ", last modified at = " + lastModified + ", version = " + version + "]";
    }

    /**
     * Invoked by DomeProject contructor when DomeProject is instantiated.
     * @param domeInterface
     */
    protected void addInterface(DomeInterface domeInterface) {
        interfaceList.add(domeInterface);
    }

    /**
     * retrieve the list of DomeInterface instances in this model.
     */
    public List getInterfaces() {
        return interfaceList;
    }

    /**
     * retrieve the DomeInterface instance with the given name. returns null when there is no match.
     */
    public DomeInterface getInterfaceById(String findingInterfaceId) {
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
    public DomeInterface getInterfaceByName(String findingInterfaceName) {
        for (Iterator i = interfaceList.iterator(); i.hasNext(); ) {
            DomeInterface anInterface = (DomeInterface) i.next();
            if(anInterface.getInterfaceName().equals(findingInterfaceName)) {
                return anInterface;
            }
        }
        return null;
    }

    /**
     * If the DomeIModel is supposed to stay in and create runtime interfaces in a specific playspace (= deployed playspace),
     * this method returns RuntimePlayspace instance of that playspace.
     * If the DomeIModel has no specific playspace and stays in transient playspace,
     * this method returns null.
     * Note that even after DomeIModel has created a runtime interface running in a transient playspace
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


    public String getType() {
        return TYPE;
    }
}