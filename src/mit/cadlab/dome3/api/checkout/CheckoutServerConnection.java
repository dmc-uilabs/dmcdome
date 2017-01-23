package mit.cadlab.dome3.api.checkout;

import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.util.DomeException;
import mit.cadlab.dome3.gui.permission.PermissionUtils;

import java.util.Vector;

public class CheckoutServerConnection {
    public static String USER = "USER";
    public static String GUEST = "GUEST";
    public static String USERS = "USERS";
    public static String GROUPS = "GROUPS";
    public static String SERVER = "SERVER";
    ServerConnection conn;

    public CheckoutServerConnection(String username, String password, String logintype, String serverIP) {
        if (logintype!=USER && logintype != GUEST)
            throw new DomeException("Invalid login type.");
        byte[] encryptedPwd = LoginUtils.encryptPassword(password);
        if (serverIP.equals(""))
            serverIP = DomeServer.getDefaultServerPort();
        else if (serverIP.indexOf(':') == -1)
            serverIP += ":8080";
        try {
            conn = LoginUtils.login(logintype, username, serverIP, encryptedPwd);
        } catch (Exception e) {
            throw new DomeException("Could not log in: " + e.getMessage());
        }
    }

    public ServerConnection getServerConnection() {
        return conn;
    }

    public String getModelId(String space, String[] path, String modelName) {
        int parentid = getFolderIdForPath(space, path);
        Vector v = DeployFileSystemFunctions.getModelFolderContents(conn, parentid);
        Vector vecModel = (Vector) v.get(1);
        for (int i = 0; i < vecModel.size(); i++) {
            Vector modelInfo = (Vector) vecModel.get(i);
            if (modelName.equals(modelInfo.get(1)))
                return (String) modelInfo.get(0);
        }
        return null;
    }

    public int getFolderIdForPath(String space, String[] path) {
        if (space.equalsIgnoreCase(USERS) || space.equalsIgnoreCase(GROUPS)) {  // top-most level = list of users/groups
            Vector v = space.equalsIgnoreCase(USERS) ? DeployFileSystemFunctions.getUserModelSpacesList(conn) :
                    DeployFileSystemFunctions.getGroupModelSpacesList(conn);
            for (int i = 0; i < v.size(); i++) {
                int id = ((Integer) ((Vector) v.get(i)).get(0)).intValue();
                String name = (String) ((Vector) v.get(i)).get(1);
                if (name.equals(path[0])) {
                    int parentid = id;
                    for (int j=1; j<path.length; j++) {
                        parentid = getFolderIdOfChild(parentid, path[j]);
                        if (j==path.length-1) // last one
                            return parentid;
                    }
                }
            }
        } else if (space.equalsIgnoreCase(SERVER)) {
            Vector v = DeployFileSystemFunctions.getServerModelSpaceNoMembershipCheck(conn);
            int parentid;
            if (path[0].equals("Public"))
                parentid = ((Integer) v.get(0)).intValue();
            else if (path[0].equals("Private"))
                parentid = ((Integer) v.get(1)).intValue();
            else
                throw new DomeException("Invalid path provided.");
            for (int j = 1; j < path.length; j++) {
                parentid = getFolderIdOfChild(parentid, path[j]);
                if (j == path.length - 1) // last one
                    return parentid;
            }
        }
        throw new DomeException("Could not locate the path: " + path + " in the space: " + space);
    }

    private int getFolderIdOfChild(int parentFolderId, String childName) {
        Vector v = DeployFileSystemFunctions.getModelFolderContents(conn, parentFolderId);
        Vector vecFolder = (Vector) v.get(0);
        for (int i = 0; i < vecFolder.size(); i++) {
            int id = ((Integer) ((Vector) vecFolder.get(i)).get(0)).intValue();
            String name = (String) ((Vector) vecFolder.get(i)).get(1);
            if (name.equals(childName))
                return id;
        }
        return -1;
    }

/*    public boolean isModelInFolder(int folderId, String modelName) {
        Vector v = DeployFileSystemFunctions.getModelFolderContents(conn, folderId);
        Vector vecModel = (Vector) v.get(1);
        for (int i = 0; i < vecModel.size(); i++) {
            Vector modelInfo = (Vector) vecModel.get(i);
            String name = (String) modelInfo.get(1);
            if (name.equals(modelName))
                return true;
        }
        return false;
    }

    public String getDeployedModelId(int folderId, String modelName) {
        Vector v = DeployFileSystemFunctions.getModelFolderContents(conn, folderId);
        Vector vecModel = (Vector) v.get(1);
        for (int i = 0; i < vecModel.size(); i++) {
            Vector modelInfo = (Vector) vecModel.get(i);
            String name = (String) modelInfo.get(1);
            if (name.equals(modelName))
                return (String) modelInfo.get(0);
        }
        return null;
    }

    public int getUserGroupId(String name) {
        Vector v = UserGroupFunctions.getSimpleActiveUsersAndGroupsList(conn);
        for (int i = 0; i < v.size(); i++) {
            Vector ug = (Vector) v.get(i);
            if ((ug.get(2)).equals(name))
                return ((Integer)ug.get(1)).intValue();
        }
        return -1;
    }

    public Vector getPermissionInfo(String category) {
        Vector permissionInfo = PermissionUtils.getCategoryInfo(conn, category);
        return (Vector) permissionInfo.get(0);
    }*/
}
