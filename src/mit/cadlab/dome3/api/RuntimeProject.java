package mit.cadlab.dome3.api;

import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeProject;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 23.
 */
public class RuntimeProject {

    private DomeConnection domeConn;
    private ClientPlayspaceRuntime playspace;
    private IntegrationProjectClientRuntime projectClient;
    private DomeProject domeProject;

    /**
     * First implement the transient playplace version, and later implement the version that works with given placement instance.
     * @param domeProject
     * @param domeConn
     */
    public RuntimeProject(DomeProject domeProject, DomeConnection domeConn) {
        this.domeConn = domeConn;
        this.domeProject = domeProject;

        /* let DomeConnection know a new RuntimeProject instance is open, which should be closed when dome connection closes. */
        domeConn.addToRuntimeProjectList(this);

        DomeRuntimeClient drClient = domeConn.getDomeRuntimeClient();
        ServerConnection projectConn = domeConn.getServerConnection();

		CompoundId projectId = new CompoundId();
        projectId.addParentProjectStaticId(domeProject.getProjectId());

        // create a new id--remove the playspace static id if the project is located
		// on a server different than where the playspace is located
		CompoundId newProjectId = new CompoundId(projectId);
		playspace = drClient.createTransientPlayspace(domeConn.getServerConnection());
        /* always equal */
        if (! projectConn.equals(playspace.getServerConnection()))
			newProjectId.setPlayspaceStaticId(null);

		// create the project in the playspace
		projectClient = playspace.createProject(newProjectId, projectConn);

		if (projectClient != null) {
			// set the playspace's runtime id and place it in the playspace map
			String playspaceRuntimeId = projectClient.getRuntimeId().getPlayspaceRuntimeId();
			playspace.setRuntimeId(playspaceRuntimeId);
			projectId.setPlayspaceRuntimeId(playspaceRuntimeId);

			Debug.trace(Debug.ALL, "Created project in a transient playspace " + projectClient.getName() + " (runid=\"" + projectClient.getRuntimeId().getFirstProjectRuntimeId() + "\")\n");
		}
    }

    /**
     * First implement the transient playplace version, and later implement the version that works with given placement instance.
     * @param domeProject
     * @param domeConn
     */
    public RuntimeProject(DomeProject domeProject, ClientPlayspaceRuntime playspace, DomeConnection domeConn) {
        this.domeConn = domeConn;

        /* let DomeConnection know a new RuntimeProject instance is open, which should be closed when dome connection closes. */
        domeConn.addToRuntimeProjectList(this);

        DomeRuntimeClient drClient = domeConn.getDomeRuntimeClient();
        ServerConnection projectConn = domeConn.getServerConnection();

		CompoundId projectId = new CompoundId();
        projectId.addParentProjectStaticId(domeProject.getProjectId());

        // create a new id--remove the playspace static id if the project is located
		// on a server different than where the playspace is located
		CompoundId newProjectId = new CompoundId(projectId);
		this.playspace = playspace;
		newProjectId.setPlayspaceStaticId(null);

		// create the project in the playspace
		projectClient = playspace.createProject(newProjectId, projectConn);

		if (projectClient != null) {
			// set the playspace's runtime id and place it in the playspace map
			String playspaceRuntimeId = projectClient.getRuntimeId().getPlayspaceRuntimeId();
			playspace.setRuntimeId(playspaceRuntimeId);
			projectId.setPlayspaceRuntimeId(playspaceRuntimeId);

			Debug.trace(Debug.ALL, "Created project in the distributed playspace " + projectClient.getName() + " (runid=\"" + projectClient.getRuntimeId().getFirstProjectRuntimeId() + "\")\n");
		}
    }

    public List getResources() {
        List ret = new ArrayList();
        List resourceList = projectClient.getResourceModels();
        for (int i = 0; i < resourceList.size(); i++) {
            ProjectResourceInfo resourceInfo = (ProjectResourceInfo) resourceList.get(i);

            if (ProjectResourceInfo.MODEL_RESOURCE.equals(resourceInfo.getType())) {
                String modelId = resourceInfo.getResourceDeployId();
                Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelId);
                Date lastModified = (Date) modifiedVec.get(0);
                int version = ((Integer) modifiedVec.get(1)).intValue();
                ret.add(new mit.cadlab.dome3.api.DomeModel(modelId, resourceInfo.getName(), resourceInfo.getResourceDescription(), resourceInfo.getType(), lastModified, version, true, domeProject.getDeployedPlayspace(), domeConn));
            } else if (ProjectResourceInfo.PROJECT_RESOURCE.equals(resourceInfo.getType())) {
                String projectId = resourceInfo.getResourceDeployId();
                Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectId);
                Date lastModified = (Date) modifiedVec.get(0);
                int version = ((Integer) modifiedVec.get(1)).intValue();
                ret.add(new DomeProject(projectId, resourceInfo.getName(), resourceInfo.getResourceDescription(), lastModified, version, true, domeProject.getDeployedPlayspace(), domeConn));
            }
        }
        return ret;
    }

    public List getIModels() {
        List ret = new ArrayList();
        List iModelList = projectClient.getIntegrationModels();
        for (int i = 0; i < iModelList.size(); i++) {
            ProjectIntegrationModelInfo integrationModelInfo = (ProjectIntegrationModelInfo) iModelList.get(i);
            String modelId = integrationModelInfo.getId();
            DomeModel domeModel = integrationModelInfo.getModel();
            domeModel.getModelInterfaces();
            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();
            ret.add(new mit.cadlab.dome3.api.DomeIModel(modelId, integrationModelInfo.getName(), "a imodel description goes here", lastModified, version, domeProject.getDeployedPlayspace(), domeConn));
        }
        return ret;
    }

    /**
     * This method is automatically called when DomeConnection close() is called.
     * release resouces associated with the RuntimeProject instance
     */
    protected void close() {
        // todo: ask someone which one is more correct way of killing model & playspace. both work fine.
        RuntimeFunctionsClient.leavePlayspace(domeConn.getServerConnection(), playspace.getCompoundId());
        //RuntimeFunctionsClient.killInterfaceParent(domeConn.getServerConnection(), interfaceClient.getRuntimeId());
    }
}
