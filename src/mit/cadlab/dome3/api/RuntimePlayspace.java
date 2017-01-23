package mit.cadlab.dome3.api;

import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 23.
 */
public class RuntimePlayspace {
    ClientPlayspaceRuntime playspaceClient;
    DomeConnection domeConn;
    DomePlayspace playspace;

    public RuntimePlayspace(DomePlayspace playspace, DomeConnection domeConn) {
        domeConn.addToRuntimePlayspaceList(this);
        this.playspace = playspace;
        this.domeConn = domeConn;
        DomeRuntimeClient drClient = domeConn.getDomeRuntimeClient();
        playspaceClient = drClient.joinPlayspace(playspace.getPlayspaceId(), domeConn.getServerConnection());
    }

    /**
     * this constructor is for the case where the playspaceClient instance is already available
     * like when RuntimeInterface creates and returns a RuntimePlayspace instance. RuntimeInterface already has a playspaceClient instance.
     */
    public RuntimePlayspace(DomePlayspace playspace, ClientPlayspaceRuntime playspaceClient, DomeConnection domeConn) {
        domeConn.addToRuntimePlayspaceList(this);
        this.playspace = playspace;
        this.domeConn = domeConn;
        this.playspaceClient = playspaceClient;
    }

    /**
     * returns the list of DomeModels in the playspace
     */
    public List getModels() {
        List ret = new ArrayList();
        Collection models = playspaceClient.getModels();

        for (Iterator i = models.iterator(); i.hasNext(); ) {
            ClientModelRecord modelRecord = (ClientModelRecord) i.next();
            ClientModelRuntime modelRuntime = modelRecord.getModel();
            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelRuntime.getId());
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            ret.add(new DomeModel(modelRuntime.getId(), modelRuntime.getName(), modelRuntime.getDescription(), "DOME", lastModified, version, false, this, domeConn));
            //ret.add(new DomeModel(modelRuntime.getId(), modelRuntime.getName(), modelRuntime.getDescription(), "DOME", lastModified, version, false, this, new DomeConnection(modelRuntime.getServerConnection(), domeConn.getDomeRuntimeClient())));

        }
        return ret;
    }

    /**
     * returns DomeModel instance with a given name in the playspace
     * returns null if there is no match.
     */
    public DomeModel getModelByName(String findingModelName) {
        Collection models = playspaceClient.getModels();

        for (Iterator i = models.iterator(); i.hasNext(); ) {
            ClientModelRecord modelRecord = (ClientModelRecord) i.next();
            ClientModelRuntime modelRuntime = modelRecord.getModel();
            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelRuntime.getId());
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingModelName.equals(modelRuntime.getName())) {
                return new DomeModel(modelRuntime.getId(), modelRuntime.getName(), modelRuntime.getDescription(), "DOME", lastModified, version, false, this, domeConn);
                //return new DomeModel(modelRuntime.getId(), modelRuntime.getName(), modelRuntime.getDescription(), "DOME", lastModified, version, false, this, new DomeConnection(modelRuntime.getServerConnection(), domeConn.getDomeRuntimeClient()));
            }
        }
        return null;
    }

    /**
     * returns DomeModel instance with a given id in the playspace
     * returns null if there is no match.
     */
    public DomeModel getModelById(String findingModelId) {
        Collection models = playspaceClient.getModels();

        for (Iterator i = models.iterator(); i.hasNext(); ) {
            ClientModelRecord modelRecord = (ClientModelRecord) i.next();
            ClientModelRuntime modelRuntime = modelRecord.getModel();
            Vector modifiedVec = FileSystemFunctions.getModelLastModified(domeConn.getServerConnection(), modelRuntime.getId());
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            if (findingModelId.equals(modelRuntime.getId())) {
                return new DomeModel(modelRuntime.getId(), modelRuntime.getName(), modelRuntime.getDescription(), "DOME", lastModified, version, false, this, domeConn);
                //return new DomeModel(modelRuntime.getId(), modelRuntime.getName(), modelRuntime.getDescription(), "DOME", lastModified, version, false, this, new DomeConnection(modelRuntime.getServerConnection(), domeConn.getDomeRuntimeClient()));
            }
        }
        return null;
    }


    /**
     * returns the list of DomeProjects in the playspace
     */
    public List getProjects() {
        List ret = new ArrayList();
        Collection projects = playspaceClient.getProjects();

        for (Iterator i = projects.iterator(); i.hasNext(); ) {
            ClientProjectRecord projectRecord = (ClientProjectRecord) i.next();
            IntegrationProjectClientRuntime projectRuntime = projectRecord.getProject();
            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectRuntime.getId().getIdString());
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();
            //String projectDesc = FileSystemFunctions.getProjectDescription(domeConn.getServerConnection(), projectRuntime.getId().getIdString());

            //todo: project description should be accessed here
            /* here isInTransientPlayspace is false because DomeProject will stay in this playspace */
            ret.add(new DomeProject(projectRuntime.getId().getIdString(), projectRuntime.getName(), "", lastModified, version, false, this, domeConn));
            //ret.add(new DomeProject(projectRuntime.getId().getIdString(), projectRuntime.getName(), "", lastModified, version, false, this, new DomeConnection(projectRuntime.getServerConnection(), domeConn.getDomeRuntimeClient())));
        }
        return ret;
    }

    /**
     * returns DomeProject with a given name in the playspace
     */
    public DomeProject getProjectByName(String findingProjectName) {
        Collection projects = playspaceClient.getProjects();

        for (Iterator i = projects.iterator(); i.hasNext(); ) {
            ClientProjectRecord projectRecord = (ClientProjectRecord) i.next();
            IntegrationProjectClientRuntime projectRuntime = projectRecord.getProject();
            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectRuntime.getId().getIdString());
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();
            //String projectDesc = FileSystemFunctions.getProjectDescription(domeConn.getServerConnection(), projectRuntime.getId().getIdString());

            //todo: project description should be accessed here
            if (findingProjectName.equals(projectRuntime.getName())) {
                return new DomeProject(projectRuntime.getId().getIdString(), projectRuntime.getName(), "", lastModified, version, false, this, domeConn);
                //return new DomeProject(projectRuntime.getId().getIdString(), projectRuntime.getName(), "", lastModified, version, false, this, new DomeConnection(projectRuntime.getServerConnection(), domeConn.getDomeRuntimeClient()));
            }
        }
        return null;
    }

    /**
     * returns DomeProject with a given name in the playspace
     */
    public DomeProject getProjectById(String findingProjectId) {
        Collection projects = playspaceClient.getProjects();

        for (Iterator i = projects.iterator(); i.hasNext(); ) {
            ClientProjectRecord projectRecord = (ClientProjectRecord) i.next();
            IntegrationProjectClientRuntime projectRuntime = projectRecord.getProject();
            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(domeConn.getServerConnection(), projectRuntime.getId().getIdString());
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();
            //String projectDesc = FileSystemFunctions.getProjectDescription(domeConn.getServerConnection(), projectRuntime.getId().getIdString());

            //todo: project description should be accessed here
            if (findingProjectId.equals(projectRuntime.getId().getIdString())) {
                /* here isInTransientPlayspace is false because DomeProject will stay in this playspace */
                return new DomeProject(projectRuntime.getId().getIdString(), projectRuntime.getName(), "", lastModified, version, false, this, domeConn);
                //return new DomeProject(projectRuntime.getId().getIdString(), projectRuntime.getName(), "", lastModified, version, false, this, new DomeConnection(projectRuntime.getServerConnection(), domeConn.getDomeRuntimeClient()));
            }
        }
        return null;
    }

    /**
     * returns the list of members connected to the playspace
     */
    public List getMembers() {
        return playspaceClient.getMembers();
    }

    /**
     * returns runtime id of this runtime playspace
     */
    public String getRuntimeId() {
        return playspaceClient.getCompoundId().getPlayspaceRuntimeId();
    }

    /**
     * returns DomePlayspace containing static information of this runtime playspace
     */
    public DomePlayspace getDomePlayspace() {
        return playspace;
    }

    /**
     * This method is automatically called when DomeConnection close() is called.
     * invokes 'leave playspace'
     */
    protected void closePlayspace() {
        RuntimeFunctionsClient.leavePlayspace(domeConn.getServerConnection(), playspaceClient.getCompoundId());
    }

    /**
     * returns ClientPlayspaceRuntime associated with this RuntimePlayspace instance.
     * @return
     */
    protected ClientPlayspaceRuntime getClientPlayspaceRuntime() {
        return playspaceClient;
    }

    public String toString() {
        return "[RUNTIME PLAYSPACE: '" + playspace.getPlayspaceName() + "' (RUNTIMEID = " + playspaceClient.getCompoundId().getPlayspaceRuntimeId() + " / ID = " + playspace.getPlayspaceId() + " / STATICID = " + playspaceClient.getCompoundId().getPlayspaceStaticId() + "), description = " + playspace.getDescription() + ", last modified at = " + playspace.getLastModified() + ", version = " + playspace.getVersion() + "]";
    }

    /**
     * returns if the given object is a RuntimePlayspace instance with the same runtime id.
     * this method is mostly used to ensure duplicated RuntimePlayspaces not to exist in the runtimePlayspaceList of DomeConnection.
     */
    public boolean equals(Object comparedRuntimeInterface) {
        if (comparedRuntimeInterface instanceof RuntimeInterface) {
            if (((RuntimeInterface) comparedRuntimeInterface).getRuntimeId() != null
                    && ((RuntimeInterface) comparedRuntimeInterface).getRuntimeId().equals(getRuntimeId())) {
                return true;
            }
        }
        return false;
    }
}
