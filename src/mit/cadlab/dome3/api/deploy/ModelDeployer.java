package mit.cadlab.dome3.api.deploy;

import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.ModelVersionData;
import mit.cadlab.dome3.util.DomeException;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.DomeInit;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.io.IOException;

public class ModelDeployer {
    DeployModelData modelData;
    String desc = "";
    String modelFilePath;
    Vector modelPermissions;

    public ModelDeployer(String modelFilePath) {
        if (!DomeInit.isInitialized())
            DomeInit.initializeDOME();
        modelData = DeployUtilities.loadModelForDeploy(modelFilePath);
        if (modelData ==null)
            throw new DomeException("Error loading model from the location provided.");
        this.modelFilePath = modelFilePath;
    }

    public List getInterfaceNames() {
        ArrayList list = new ArrayList();
        List ifaces = modelData.getModelInterfaces();
        for (int i = 0; i < ifaces.size(); i++) {
            DeployInterfaceData iface = (DeployInterfaceData) ifaces.get(i);
            list.add(iface.getName());
        }
        return list;
    }

    protected DeployInterfaceData getDeployInterfaceData(String name) {
        List ifaces = modelData.getModelInterfaces();
        for (int i = 0; i < ifaces.size(); i++) {
            DeployInterfaceData iface = (DeployInterfaceData) ifaces.get(i);
            if (iface.getName().equals(name))
                return iface;
        }
        return null;
    }

    public void selectInterfaceByName(String name) {
        DeployInterfaceData iface = getDeployInterfaceData(name);
        iface.setIsAvailable(new Boolean(true));
    }

    public void setModelDescription(String desc) {
        this.desc = desc;
        modelData.setModelDescription(desc);
    }

    public void setInterfaceDescription(String name, String desc) {
        DeployInterfaceData iface = getDeployInterfaceData(name);
        iface.setDescription(desc);
    }

    /** auto redeploy if the model is already in the specified folder */
    public void deployAuto(DeployServerConnection con, String space, String path) {
        String[] pathArr = path.split("/");
        int targetFolderId = con.getFolderIdForPath(space, pathArr);
        Vector verInfo;
        Vector interfaceInfo = DeployUtilities.createDeployInterfaceInfoVector(modelData.getModelInterfaces());
        String deployedId = con.getDeployedModelId(targetFolderId, modelData.getName());
        Vector auxFilesInfos;
        try {
            auxFilesInfos = DeployUtilities.loadAuxFilesForDeploy(modelData.getXmlContent());
        } catch (IOException e) {
            throw new DomeException("Could not load aux files for the model:" + e);
        }
        if (deployedId==null) { // never deployed there before
            Vector modelInfo = DeployUtilities.createNewModelInfoVector(new Integer(targetFolderId),
                    desc, modelData.getXmlContent(), modelPermissions);
            verInfo = DeployFilesFunctions.deployNewModel(con.getServerConnection(), modelInfo, interfaceInfo);
        } else {
            Vector modelInfo = DeployUtilities.createUpdateModelInfoVector(deployedId, desc, modelData.getXmlContent(),
                    modelPermissions);
            verInfo = DeployFilesFunctions.redeployModel(con.getServerConnection(), modelInfo, interfaceInfo);
        }
        if (auxFilesInfos != null)
            DeployFilesFunctions.uploadFile(con.getServerConnection(), (String) ((Vector) verInfo.get(0)).get(0), auxFilesInfos);
        DeployUtilities.writeModelVersionFile(modelFilePath, new ModelVersionData(verInfo, con.getServerConnection()));
    }

    public boolean isDeployed(DeployServerConnection con, String modelName, String space, String path) {
        String[] pathArr = path.split("/");
        int folderId = con.getFolderIdForPath(space, pathArr);
        return con.isModelInFolder(folderId, modelName);
    }

    public void giveModelPermission(int userId, int permissionId) {
        if (modelPermissions==null)
            modelPermissions = new Vector();
        modelPermissions.add(Vectors.create(new Integer(userId), new Integer(permissionId)));
    }

    public void giveAllModelPermissions(DeployServerConnection con, String userName) {
        int usrId = con.getUserGroupId(userName);
        Vector pers = con.getPermissionInfo(DeployServerConnection.MODEL_PERMISSIONS);
        for (int i = 0; i < pers.size(); i++) {
            Vector p = (Vector) pers.get(i);
            giveModelPermission(usrId, ((Integer)p.get(0)).intValue());
        }
    }

    public void giveInterfacePermission(String iface, int userId, int permissionId) {
        DeployInterfaceData ifacedata = getDeployInterfaceData(iface);
        Vector pers = ifacedata.getPermissions();
        pers.add(Vectors.create(new Integer(userId), new Integer(permissionId)));
    }

    public void giveAllInterfacePermissions(String iface, DeployServerConnection con, String userName) {
        int usrId = con.getUserGroupId(userName);
        Vector pers = con.getPermissionInfo(DeployServerConnection.INTERFACE_PERMISSIONS);
        for (int i = 0; i < pers.size(); i++) {
            Vector p = (Vector) pers.get(i);
            giveInterfacePermission(iface, usrId, ((Integer) p.get(0)).intValue());
        }
    }

    public int createFolder(DeployServerConnection con, int parentId, String name) {
        int newFolderId = FileSystemFunctions.createModelFolder(con.getServerConnection(), parentId, name);
        return newFolderId;
    }

    public int createFolder(DeployServerConnection con, String space, String parentPath, String name) {
        int parentId = con.getFolderIdForPath(space, parentPath.split("/"));
        return createFolder(con, parentId, name);
    }
}
