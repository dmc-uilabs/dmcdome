package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseInterfaceDomeFile;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBrowse;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.util.*;

import org.dom4j.Element;

/**
 * DomeProject is a static representation of Dome Integration Project existing in the server.
 * when DomeProject in created, we should provide required authorization information (or ServerConnections) to access the resources in the project
 * User: Sangmok Han
 * Date: 2005. 1. 21.
 */
public class DomeProject implements DomeSimulation {

    public static final String TYPE = "PROJECT";

    private String projectId;
    private String projectName;
    private String description;
    private Date lastModified;
    private int version;
    private DomeConnection domeConn;
    private List interfaceList = null;
    private List resourceList = null;
    private List iModelList = null;

    private boolean isProjectResource; // If DomeProject is a resource of a DomeProject, this variable is initalized as true when it is contructed within DomeProject.
    private RuntimePlayspace deployedPlayspace;

    /**
     * Construct a DomeProject instance.
     * @param projectId
     * @param projectName
     * @param description
     * @param lastModified
     * @param version
     * @param deployedPlayspace If the DomeProject instance is supposed to stay in the transient playspace, this argument should be left null. If the DomeProject instance is supposed to stay in a specific playspace (=deployed playspace), this argument takes that playspace instance.
     * @param domeConn
     */
    public DomeProject(String projectId, String projectName, String description, Date lastModified, int version, boolean isProjectResource, RuntimePlayspace deployedPlayspace, DomeConnection domeConn) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.lastModified = lastModified;
        this.version = version;
        this.domeConn = domeConn;

        this.isProjectResource = isProjectResource;
        this.deployedPlayspace = deployedPlayspace;
    }

    /**
     * initialize project resources associated with the dome project.
     * also initialize project imodel associated with the dome project.
     * there are two types of project resources: one is DomeModel and another is DomeProject.
     * each DomeModel or DomeProject instance is created and put in the resourceList.
     * each iModel is created and put in iModelList.
     * when DomeModel, DomeProject, DomeIModel instance is created,
     * every DomeInterface associated with each object is also instantiated.
     */
    synchronized private void initResourceAndIModel() {
        resourceList = new ArrayList();
        iModelList = new ArrayList();

        /* add dome resources & iModel info in the project */
        Vector projContentsVec = FileSystemFunctions.getAvailableProjectContents(domeConn.getServerConnection(), projectId, DbConstants.FILESYSTEM_BROWSE);

        /* when the user has no permission to view project info projContentsVec is empty. */
        if (projContentsVec.isEmpty()) {
            return;
        }

        String projectXml = (String) projContentsVec.get(0);
        Element xmlElement = XMLUtils.stringToXmlElement(projectXml);

        List resourceNodes = xmlElement.selectNodes("resources/" + ProjectResourceInfo.XML_TAG);
		for (int i = 0; i < resourceNodes.size(); i++) {
            ProjectResourceInfo resourceInfo = new ProjectResourceInfo((Element) resourceNodes.get(i));

            if (ProjectResourceInfo.MODEL_RESOURCE.equals(resourceInfo.getType())) {
                String modelId = resourceInfo.getResourceDeployId();

                // initialize dome model
                Vector modelInfoVec = FileSystemFunctions.getModelInfo(domeConn.getServerConnection(), modelId);

                // when the user has no permission to view model info modelInfoVec is empty.
                if (! modelInfoVec.isEmpty()) {
                    String modelName = (String) modelInfoVec.get(1);
                    String modelDesc = (String) modelInfoVec.get(2);
                    int modelVersion = ((Integer) modelInfoVec.get(3)).intValue();
                    Date modelModifiedDate = (Date) modelInfoVec.get(4);

                    // todo: what is the right type for this dome model?
                    // In this case DomeModel is a resource of DomeProject so isProjectResource is set to 'true'
                    // if DomeProject lives in a transient playspace, DomeModel would also live in a transient one.
                    // If DomeProject lives in a specific deployed playspace, DomeModel would also live in a specific deployed one.
                    // so the deployedPlayspace of DomeModel copies that of this instance.
                    DomeModel domeModel = new DomeModel(modelId, modelName, modelDesc, "DOME", modelModifiedDate, modelVersion, true, deployedPlayspace, domeConn);
                    //DomeModel domeModl = new DomeModel(modelId, resourceInfo.getName(), resourceInfo.getResourceDescription(), resourceInfo.getType(), lastModified, version, domeConn));

                    resourceList.add(domeModel);
                }
            } else if (ProjectResourceInfo.PROJECT_RESOURCE.equals(resourceInfo.getType())) {
                // initialize dome project
                String projectIdInResource = resourceInfo.getResourceDeployId();
                //String projectDescInResource = FileSystemFunctions.getProjectDescription(domeConn.getServerConnection(), projectIdInResource);
                Vector projectModifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectIdInResource);

                resourceList.add(new DomeProject(projectIdInResource, resourceInfo.getName(), resourceInfo.getResourceDescription(), (Date) projectModifiedVec.get(0), ((Integer) projectModifiedVec.get(1)).intValue(), true, deployedPlayspace, domeConn));
            }
		}

//        List imodelNodes = xmlElement.selectNodes("integrationModels/" + ProjectIntegrationModelInfo.XML_TAG);
//		for (int i = 0; i < imodelNodes.size(); i++) {
//			ProjectIntegrationModelInfo iModelInfo = new ProjectIntegrationModelInfo((Element) imodelNodes.get(i));
//		}

        /* initialize iModel list */
        Vector iModelVecList = (Vector) projContentsVec.get(1);
        for (Iterator j = iModelVecList.iterator(); j.hasNext(); ) {
            Vector iModelVec = (Vector) j.next();

            Vector modelInfo = (Vector) iModelVec.get(0);

            String iModelName = (String) modelInfo.get(0);
            String iModelId = (String) modelInfo.get(1);
            String iModelDesc= (String) modelInfo.get(2);

            int iModelVersion = ((Integer) modelInfo.get(3)).intValue();
            Date iModelDate = (Date) modelInfo.get(4);

            // todo: IMODEL should be changed to something else
            DomeIModel domeIModel = new DomeIModel(iModelId, iModelName, iModelDesc, iModelDate, iModelVersion, deployedPlayspace, domeConn);

            Vector interfaceList = (Vector) iModelVec.get(1);

            for (Iterator k = interfaceList.iterator(); k.hasNext(); ) {
                Vector interfaceInfo = (Vector) k.next();
                String interfaceName = (String) interfaceInfo.get(0);
                String interfaceId = (String) interfaceInfo.get(1);
                String interfaceDesc = (String) interfaceInfo.get(2);
                int interfaceVersion = ((Integer) interfaceInfo.get(3)).intValue();
                Date interfaceDate = (Date) interfaceInfo.get(4);
                domeIModel.addInterface(new DomeInterface(interfaceId, interfaceName, interfaceDesc, interfaceVersion, interfaceDate, domeIModel, domeConn));
            }

            iModelList.add(domeIModel);
        }
    }

    /**
     * initialize project interfaces associated with the dome project.
     * each DomeInterface instance is created and put in the interfaceList.
     */
    synchronized private void initInterfaceList() {
        interfaceList = new ArrayList();

        Vector vecInterface = FileSystemFunctions.getAvailableInterfacesInfo(domeConn.getServerConnection(), projectId, PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
        for (int i = 0; i < vecInterface.size(); i++) {
            Vector anInterface = (Vector) vecInterface.get(i);
            String interfaceName = (String) anInterface.get(0); // interface name (ex) Bike Cost Default Interface
            String interfaceId = (String) anInterface.get(1); // interface id (ex) 101d8dd6-bba1-1004-8a1f-bfbdca7cf0cf
            String interfaceDesc = (String) anInterface.get(2); // interface description
            int interfaceVersion = ((Integer) anInterface.get(3)).intValue(); // interface version
            Date interfaceLastModified = (Date) anInterface.get(4); // last modified date
            interfaceList.add(new DomeInterface(interfaceName, interfaceId, interfaceDesc, interfaceVersion, interfaceLastModified, this, domeConn));
        }
    }


    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    /** returns project id */
    public String getSimulationId() {
        return projectId;
    }

    /** returns project name */
    public String getSimulationName() {
        return projectName;
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

    /**
     * If the DomeProject is supposed to stay in and create runtime interfaces in a specific playspace (= deployed playspace),
     * this method returns RuntimePlayspace instance of that playspace.
     * If the DomeProject has no specific playspace and stays in transient playspace,
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

    public DomeConnection getDomeConn() {
        return domeConn;
    }

    public String toString() {
        return "[DOME PROJECT: '" + projectName + "' (ID = " + projectId + "), description = " + description + ", last modified at = " + lastModified + ", version = " + version + ", in transient playspace = " + isInTransientPlayspace() + "]";
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
     * retrieve the list of resources, which consists of DomeModel and DomeProject instances, in this model.
     */
    synchronized public List getResources() {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }
        return resourceList;
    }

    /**
     * retrieve the list of DomeIModel instances in this model.
     */
    synchronized public List getIModels() {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }
        return iModelList;
    }

    /**
     * retrieve the DomeInterface instance with the given id. returns null when there is no match.
     */
    synchronized public DomeInterface getInterfaceById(String findingInterfaceId) {
        if (interfaceList == null) {
            initInterfaceList();
        }

        for (int i = 0; i < interfaceList.size(); i++) {
            DomeInterface anInterface = (DomeInterface) interfaceList.get(i);

            if (findingInterfaceId.equals(anInterface.getInterfaceId())) {
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

        for (int i = 0; i < interfaceList.size(); i++) {
            DomeInterface anInterface = (DomeInterface) interfaceList.get(i);

            if (findingInterfaceName.equals(anInterface.getInterfaceName())) {
                return anInterface;
            }
        }
        return null;
    }

    /**
     * retrieve the DomeModel-type resource with the given name. returns null when there is no match.
     */
    synchronized public DomeModel getDomeModelResourceByName(String findingResourceName) {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }

        for (int i = 0; i < resourceList.size(); i++) {
            if (resourceList.get(i) instanceof DomeModel) {
                DomeModel aModel = (DomeModel) resourceList.get(i);

                if (findingResourceName.equals(aModel.getModelName())) {
                    return aModel;
                }
            }
        }
        return null;
    }

    /**
     * retrieve the DomeModel-type resource with the given id. returns null when there is no match.
     */
    synchronized public DomeModel getDomeModelResourceById(String findingResourceId) {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }

        for (int i = 0; i < resourceList.size(); i++) {
            if (resourceList.get(i) instanceof DomeModel) {
                DomeModel aModel = (DomeModel) resourceList.get(i);

                if (findingResourceId.equals(aModel.getModelId())) {
                    return aModel;
                }
            }
        }
        return null;
    }

    /**
     * retrieve the DomeProject-type resource with the given name. returns null when there is no match.
     */
    synchronized public DomeProject getDomeProjectResourceByName(String findingResourceName) {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }

        for (int i = 0; i < resourceList.size(); i++) {
            if (resourceList.get(i) instanceof DomeProject) {
                DomeProject aProject = (DomeProject) resourceList.get(i);

                if (findingResourceName.equals(aProject.getProjectName())) {
                    return aProject;
                }
            }
        }
        return null;
    }

    /**
     * retrieve the DomeProject-type resource with the given id. returns null when there is no match.
     */
    synchronized public DomeProject getDomeProjectResourceById(String findingResourceId) {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }

        for (int i = 0; i < resourceList.size(); i++) {
            if (resourceList.get(i) instanceof DomeProject) {
                DomeProject aProject = (DomeProject) resourceList.get(i);

                if (findingResourceId.equals(aProject.getProjectId())) {
                    return aProject;
                }
            }
        }
        return null;
    }

    /**
     * retrieve the DomeIModel with the given name. returns null when there is no match.
     */
    synchronized public DomeIModel getDomeIModelByName(String findingIModelName) {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }

        for (int i = 0; i < iModelList.size(); i++) {
            DomeIModel aIModel = (DomeIModel) iModelList.get(i);

            if (findingIModelName.equals(aIModel.getModelName())) {
                return aIModel;
            }
        }
        return null;
    }

    /**
     * retrieve the DomeIModel with the given id. returns null when there is no match.
     */
    synchronized public DomeIModel getDomeIModelById(String findingIModelId) {
        if (resourceList == null || iModelList == null) {
            initResourceAndIModel();
        }

        for (int i = 0; i < iModelList.size(); i++) {
            DomeIModel aIModel = (DomeIModel) iModelList.get(i);

            if (findingIModelId.equals(aIModel.getModelId())) {
                return aIModel;
            }
        }
        return null;
    }

    /**
     * returns if the DomeProject is a resource of another DomeProject
     */
    public boolean isProjectResource() {
        return isProjectResource;
    }

    public String getType() {
        return TYPE;
    }
}
