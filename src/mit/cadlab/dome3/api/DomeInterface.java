package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;

import java.util.Date;

/**
 * DomeInterface is a static representation of an interface in the server.
 * Users can create the runnable interface by using createRuntimeInterface() method.
 *
 * There can be following three kinds of DomeInterface
 * 1) Interface of DomeModel: parentModel is not null. others are null.
 * 2) Interface of DomeProject: parentProject is not null. others are null.
 * 3) Interface of DomeIModel: parentIModel is not null. others are null.
 *
 * User: Sangmok Han
 * Date: 2005. 1. 18.
 */
public class DomeInterface {

    private DomeConnection domeConn;
    private String interfaceName;
    private String interfaceId;
    private String description;
    private int version;
    private Date lastModified;

    /* only one of these three parentXXX will be not null. */
    private DomeSimulation parentSimulation;

    /** parent simulation is one of DomeModel, DomeProject, DomeIModel, and DomeAnalysisTool */
    public DomeInterface(String interfaceName, String interfaceId, String description, int version, Date lastModified, DomeSimulation parentSimulation, DomeConnection domeConn) {
        this.interfaceName = interfaceName;
        this.interfaceId = interfaceId;
        this.description = description;
        this.version = version;
        this.lastModified = lastModified;
        this.domeConn = domeConn;
        this.parentSimulation = parentSimulation;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public String getDescription() {
        return description;
    }

    public int getVersion() {
        return version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public DomeSimulation getParentSimulation() {
        return parentSimulation;
    }

    public DomeModel getParentModel() {
        return (DomeModel) parentSimulation;
    }

    public DomeProject getParentProject() {
        return (DomeProject) parentSimulation;
    }

    public DomeIModel getParentIModel() {
        return (DomeIModel) parentSimulation;
    }

    public DomeAnalysisTool getParentAnalysisTool() {
        return (DomeAnalysisTool) parentSimulation;
    }

    public String getParentName() {
        if (parentSimulation != null) {
            return parentSimulation.getSimulationName();
        }
        return null;
    }

    /**
     * returns if the parent of this DomeInterface is a model.
     */
    public boolean isInterfaceOfModel() {
        return parentSimulation instanceof DomeModel;
    }

    /**
     * returns if the parent of this DomeInterface is a integration model.
     */
    public boolean isInterfaceOfIModel() {
        return parentSimulation instanceof DomeIModel;
    }

    /**
     * returns if the parent of this DomeInterface is a project.
     */
    public boolean isInterfaceOfProject() {
        return parentSimulation instanceof DomeProject;
    }

    /**
     * @return is the parent of this DomeInterface an analysis tool
     */
    public boolean isInterfaceOfAnalysisTool() {
        return parentSimulation instanceof DomeAnalysisTool;
    }

    public String getDefinition() {
        return FileSystemFunctions.getInterfaceDescription(domeConn.getServerConnection(), interfaceId);
    }

    public String toString() {
        return "[DOME INTERFACE: '" + interfaceName + "' (ID = " + interfaceId + "), description = " + description + ", last modified at = " + lastModified + ", version = " + version + "]";
    }

    /**
     * createRuntimeInterface() creates RuntimeInterface instance of this DomeInterface
     * There are two contructors for instantiating RuntimeInterface.
     * When parent is in a transient playspace (=parent.isIntransientPlayspace() is true), RuntimeInterface without a deployedPlayspace argument is used.
     * When parent is in a specific deployed playspace (=parent.isIntransientPlayspace() is false), RuntimeInterface with a deployedPlayspace argument is used.
     * @return RuntimeInterface instance either in a transient playspace or in a specific deployed playspace. It depends on the parent model/iModel/project.
     */
    public RuntimeInterface createRuntimeInterface() {
        if (isInterfaceOfModel()) {
            if (getParentModel().isInTransientPlayspace()) {
                return new RuntimeInterface(this, domeConn);
            } else {
                return new RuntimeInterface(this, getParentModel().getDeployedPlayspace(), domeConn);
            }
        } else if (isInterfaceOfIModel()) {
            if (getParentIModel().isInTransientPlayspace()) {
                return new RuntimeInterface(this, domeConn);
            } else {
                return new RuntimeInterface(this, getParentIModel().getDeployedPlayspace(), domeConn);
            }
        } else if (isInterfaceOfProject()) {
            if (getParentProject().isInTransientPlayspace()) {
                return new RuntimeInterface(this, domeConn);
            } else {
                return new RuntimeInterface(this, getParentProject().getDeployedPlayspace(), domeConn);
            }
        }
//        below code is not tested to work. Please use createRuntimeAnalysisToolInterface() to create a RuntimeInterface of AnalysisTool
//        else if (isInterfaceOfAnalysisTool()) {
//            if (getParentAnalysisTool().isInTransientPlayspace()) {
//                return new RuntimeInterface(this, domeConn);
//            } else {
//                return new RuntimeInterface(this, getParentAnalysisTool().getDeployedPlayspace(), domeConn);
//            }
//        }

        /* normally program flow nerver reaches here */
        return new RuntimeInterface(this, domeConn);
    }

    public RuntimeAnalysisToolInterface createRuntimeAnalysisToolInterface()
    {
        if (getParentAnalysisTool().isInTransientPlayspace())
            return new RuntimeAnalysisToolInterface (this, domeConn);
        else
            return new RuntimeAnalysisToolInterface(this, getParentAnalysisTool().getDeployedPlayspace(), domeConn);
    }

}
