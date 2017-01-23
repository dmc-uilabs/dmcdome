package mit.cadlab.dome3.api;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.BrowseFileSystemFunctions;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.util.Regex;

import java.util.*;

/**
 * DomeConnection is the class that represent the connection to DOME server.
 * DomeConnection should be closed by close() when this connection is no more used.
 * This class is a member variable of DomeFolder, DomeModel and DomeInterface
 * to provide the access to the ServerConnection instance and DomeRuntimeClient instance.
 * User: Sangmok Han
 * Date: 2005. 1. 18.
 */
public class DomeConnection {
    private DomeRuntimeClient drClient = null;
    private ServerConnection serverConn = null;
    private String serverPort;
    private String user;
    private Set runtimeInterfaceList = new HashSet();
    private Set _runtimeAnalysisToolInterfaceList = new HashSet();
    private Set runtimeProjectList = new HashSet();
    private Set runtimePlayspaceList = new HashSet();
    private boolean isConnected = false;
    private String INIT_KEY = "";
    public static final String SERVER_SPACE = "server";
    public static final String USER_SPACE = "users";
    public static final String GROUP_SPACE = "groups";

    /**
     * Create connection to a DOME server
     * 1) initialize DOME classes
     * 2) open connection to a DOME server
     * 3) to open 'guest' connection set user = 'guest' and password = ''
     * @param user (ex) tutorialUser
     * @param password (ex) 123
     * @param serverPort (ex) localhost:8080
     */
    public DomeConnection(String user, String password, String serverPort) {

        this.user = user;
        this.serverPort = serverPort;

        synchronized (INIT_KEY) {
            if (! DomeInit.isInitialized()) {
                Debug.trace(Debug.ALL, "initializing DOME classes");
                DomeInit.initializeDOME();
            }
        }

        int clientPort = 9002;
        drClient = new DomeRuntimeClient(clientPort);

        String clientUrl = "http://" + NetworkUtils.getIpAddress() + ":" + drClient.getPort() + "/RPC2";
        serverConn = login(user, password, serverPort, clientUrl);

        if (serverConn == null) {
            Debug.trace(Debug.ALL, "Failed to login to DOME server at " + serverPort + " as " + user);

            /* cleanup drClient by shutting it down */
            Debug.trace(Debug.ALL, "shutdown client-side web server at " + drClient.getPort());
            if (drClient != null) {
                System.out.println("shutdown client-side web server at " + drClient.getPort());
                drClient.shutdown();
            }

            throw new RuntimeException("Login failed");
        }
        Debug.trace(Debug.ALL, "Successfully login to DOME server at " + serverPort + " as " + user);


        /* isConnected is set true only if there is no exception until this line */
        this.isConnected = true;

        DomeClientApplication.setIsRunningThroughDomeClient(true);
    }

    /**
     * alias for getServerPublicFolder()
     * Please refer the javadoc of getServerPublicFolder() for more information.
     */
    public DomeFolder getPublicFolder() {
        return getServerPublicFolder();
    }

    /**
     * alias for getServerPrivateFolder()
     * Please refer the javadoc of getServerPrivateFolder() for more information.
     */
    public DomeFolder getPrivateFolder() {
        return getServerPublicFolder();
    }

    /**
     * There is four kinds of public folder.
     *
     * - my public folder --> getMyPublicFolder()
     * - server's public folder --> getServerPublicFolder()
     * - user's public folder --> getUserPublicFolder(userName)
     * - group (to which user belong)'s public folder --> getGroupPublicFolder(groupName)
     *
     * Note. each method will return null if there is no public folder
     *       or if the public folder is not accessble to this user
     *
     * returns the public folder of server
     */
    public DomeFolder getServerPublicFolder() {
        Vector v = BrowseFileSystemFunctions.getServerModelSpace(serverConn);
		if (v.size() < 1) {
            return null; // there is no public folder because v.get(0) is public
		} else {
            int publicFolderDbId = ((Integer) v.get(0)).intValue();
            if (publicFolderDbId < -1) {
                return null; // 'publicFolderDbId is -1' means there is no accessible public folder
            }
            return new DomeFolder("Public", publicFolderDbId, this);
        }
    }

    /**
     * There is three kinds of private folder.
     *
     * - my private folder --> getMyPrivateFolder()
     * - server's private folder --> getServerPrivateFolder()
     * - user's private folder --> getUserPrivateFolder()
     * - group (to which user belong)'s private folder --> getGroupPrivateFolder()
     *
     * Note. each method will return null if there is no private folder
     *       or if the private folder is not accessble to this user
     *
     * returns the public folder of server
     */
    public DomeFolder getServerPrivateFolder() {
        Vector v = BrowseFileSystemFunctions.getServerModelSpace(serverConn);
		if (v.size() < 2) {
            return null; // there is no private folder because v.get(0) is public and v.get(1) is private
		} else {
            int privateFolderDbId = ((Integer) v.get(1)).intValue();
            if (privateFolderDbId < -1) {
                return null; // 'privateFolderDbId is -1' means there is no accessible private folder
            }
            return new DomeFolder("Private", privateFolderDbId, this);
        }
    }


    /**
     * returns user db id that corresponds to a given user name
     */
    private int getUserDbId(String searchingUserName) {
		Vector v = BrowseFileSystemFunctions.getUserModelSpacesList(serverConn);
		for (int i = 0; i < v.size(); i++) {
			try {
				Vector aUser = (Vector) v.get(i); //
				Integer userDbId = (Integer) (aUser.get(0)); // here userDbId contains groupDbId
				String userName = (String) aUser.get(1); // here userName contains groupName
				if (searchingUserName.equals(userName)) {
                    return userDbId.intValue();
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return -1;
    }

    /**
     * returns List of users who have public or private folders
     */
	public String[] getUserNames() {
		Vector v = BrowseFileSystemFunctions.getUserModelSpacesList(serverConn);
        List userNameList = new ArrayList();

		for (int i = 0; i < v.size(); i++) {
			try {
                String userName;
                Vector aUser;
				aUser = (Vector) v.get(i);
				userName = (String) aUser.get(1);
                userNameList.add(userName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return (String[]) userNameList.toArray(new String[userNameList.size()]);
	}


    /**
     * returns the public folder of a user specified by userName
     * Please refer the javadoc of getServerPublicFolder() for more information.
     */
    public DomeFolder getUserPublicFolder(String userName) {
        /* in case of retrieving group public folder, the given groupName can be non-existing */
        int userDbId = getUserDbId(userName);
        if (userDbId == -1) {
            throw new RuntimeException("such user '"+ userName + "' does not exist."); // there is no group with given groupName
        }

        Vector v = BrowseFileSystemFunctions.getUserGroupModelFolders(serverConn, userDbId);

		if (v.size() < 1) {
            return null; // there is no public folder because v.get(0) is public
		} else {
            int publicFolderDbId = ((Integer) v.get(0)).intValue();
            if (publicFolderDbId < -1) {
                return null; // 'publicFolderDbId is -1' means there is no accessible public folder
            }
            return new DomeFolder("Public", publicFolderDbId, this);
        }
    }

    /**
     * returns the private folder of a user specified by userName
     * Please refer the javadoc of getServerPrivateFolder() for more information.
     */
    public DomeFolder getUserPrivateFolder(String userName) {
        /* in case of retrieving group public folder, the given groupName can be non-existing */
        int userDbId = getUserDbId(userName);
        if (userDbId == -1) {
            throw new RuntimeException("such user '"+ userName + "' does not exist."); // there is no group with given groupName
        }

        Vector v = BrowseFileSystemFunctions.getUserGroupModelFolders(serverConn, userDbId);

		if (v.size() < 2) {
            return null; // there is no private folder because v.get(0) is public and v.get(1) is private
		} else {
            int privateFolderDbId = ((Integer) v.get(1)).intValue();
            if (privateFolderDbId < -1) {
                return null; // 'privateFolderDbId is -1' means there is no accessible private folder
            }
            return new DomeFolder("Private", privateFolderDbId, this);
        }
    }

    /**
     * returns the public folder of current user
     * Please refer the javadoc of getServerPublicFolder() for more information.
     */
    public DomeFolder getMyPublicFolder() {
        /* in case of retrieving group public folder, the given groupName can be non-existing */
        if (! Arrays.asList(getUserNames()).contains(user)) {
            return null; // there is no public folder for this user
            //throw new RuntimeException("current user does not have a public folder."); // there is no group with given groupName
        }

        Vector v = BrowseFileSystemFunctions.getUserModelSpace(serverConn);

		if (v.size() < 1) {
            return null; // there is no public folder because v.get(0) is public
		} else {
            int publicFolderDbId = ((Integer) v.get(0)).intValue();
            if (publicFolderDbId < -1) {
                return null; // 'publicFolderDbId is -1' means there is no accessible public folder
            }
            return new DomeFolder("Public", publicFolderDbId, this);
        }
    }

    /**
     * returns the private folder of current user
     * Please refer the javadoc of getServerPrivateFolder() for more information.
     */
    public DomeFolder getMyPrivateFolder() {
        /* in case of retrieving group public folder, the given groupName can be non-existing */
        if (! Arrays.asList(getUserNames()).contains(user)) {
            return null; // there is no private folder for this user
            //throw new RuntimeException("current user does not have a private folder."); // there is no group with given groupName
        }

        Vector v = BrowseFileSystemFunctions.getUserModelSpace(serverConn);

		if (v.size() < 2) {
            return null; // there is no private folder because v.get(0) is public and v.get(1) is private
		} else {
            int privateFolderDbId = ((Integer) v.get(1)).intValue();
            if (privateFolderDbId < -1) {
                return null; // 'privateFolderDbId is -1' means there is no accessible private folder
            }
            return new DomeFolder("Private", privateFolderDbId, this);
        }
    }

    /**
     * returns group db id that corresponds to a given group name
     */
    private int getGroupDbId(String groupName) {
		Vector v = BrowseFileSystemFunctions.getGroupModelSpacesList(serverConn);
		for (int i = 0; i < v.size(); i++) {
			try {
				Vector aUser = (Vector) v.get(i); //
				Integer userDbId = (Integer) (aUser.get(0)); // here userDbId contains groupDbId
				String userName = (String) aUser.get(1); // here userName contains groupName
				if (groupName.equals(userName)) {
                    return userDbId.intValue();
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return -1;
    }

    /**
     * returns array of group names to which current user belongs.
     */
    public String[] getGroupNames() {
		List groupNameList = new ArrayList();
        Vector v = BrowseFileSystemFunctions.getGroupModelSpacesList(serverConn);
		for (int i = 0; i < v.size(); i++) {
			try {
				Vector aUser = (Vector) v.get(i);
				String userName = (String) aUser.get(1);
				groupNameList.add(userName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return (String[]) groupNameList.toArray(new String[groupNameList.size()]);
    }

    /**
     * A user can belong to many groups. Each group is identified by its groupName.
     * Therefore, to pick one public folder, we need to supply the name of the group we are interested to access.
     * We can get the list of group names a user belongs to by invoking 'getGroupNames():String[]'
     * returns the public folder of a group with a given group name
     * Please refer the javadoc of getServerPublicFolder() for more information.
     */
    public DomeFolder getGroupPublicFolder(String groupName) {
        /* in case of retrieving group public folder, the given groupName can be non-existing */
        int groupDbId = getGroupDbId(groupName);
        if (groupDbId == -1) {
            throw new RuntimeException("such group '"+ groupName + "' does not exist."); // there is no group with given groupName
        }

        Vector v = BrowseFileSystemFunctions.getUserGroupModelFolders(serverConn, groupDbId);
		if (v.size() < 1) {
            return null; // there is no public folder because v.get(0) is public
		} else {
            int publicFolderDbId = ((Integer) v.get(0)).intValue();
            if (publicFolderDbId < -1) {
                return null; // 'publicFolderDbId is -1' means there is no accessible public folder
            }
            return new DomeFolder("Public", publicFolderDbId, this);
        }
    }

    /**
     * A user can belong to many groups. Each group is identified by its groupName.
     * Therefore, to pick one private folder, we need to supply the name of the group we are interested to access.
     * We can get the list of group names a user belongs to by invoking 'getGroupNames():String[]'
     * returns the private folder of a group with a given group name
     * Please refer the javadoc of getServerPrivateFolder() for more information.
     */
    public DomeFolder getGroupPrivateFolder(String groupName) {
        /* in case of retrieving group private folder, the given groupName can be non-existing */
        int groupDbId = getGroupDbId(groupName);
        if (groupDbId == -1) {
            throw new RuntimeException("The user does not belong to a group " + groupName + " or such group does not exist."); // there is no group with given groupName
        }

        Vector v = BrowseFileSystemFunctions.getUserGroupModelFolders(serverConn, groupDbId);
		if (v.size() < 2) {
            return null; // there is no private folder because v.get(0) is public and v.get(1) is private
		} else {
            int privateFolderDbId = ((Integer) v.get(1)).intValue();
            if (privateFolderDbId < -1) {
                return null; // 'privateFolderDbId is -1' means there is no accessible private folder
            }
            return new DomeFolder("Private", privateFolderDbId, this);
        }
    }

    /**
     * closes connection to a DOME server.
     * also closes all runtime interface associated with this connection.
     */
    public void close() {
        Debug.trace(Debug.ALL, "close all runtime interfaces associated with this connection");
        for (Iterator i = runtimeInterfaceList.iterator(); i.hasNext(); ) {
            ((RuntimeInterface) i.next()).closeInterfaceParent();
        }

        for (Iterator i = runtimeInterfaceList.iterator(); i.hasNext(); ) {
            ((RuntimeInterface) i.next()).clearValueAndStatusChangeListener();
        }

//        for (Iterator i = runtimeInterfaceList.iterator(); i.hasNext(); ) {
//            ((RuntimeInterface) i.next()).closePlayspace();
//        }


        /* below seems optional */
//        for (Iterator i = runtimePlayspaceList.iterator(); i.hasNext(); ) {
//            ((RuntimePlayspace) i.next()).closePlayspace();
//        }

        /* wait until closeInterfaceParent() finishes */
        try {
            Thread.sleep(1000);
        } catch (Exception e) { }

        Debug.trace(Debug.ALL, "shutdown client-side web server at " + drClient.getPort());
        if (drClient != null) {
            System.out.println("shutdown client-side web server at " + drClient.getPort());
            drClient.shutdown();
        }

        Debug.trace(Debug.ALL, "logout from DOME server at " + serverPort + " as " + user);
        if (serverConn != null) {
            serverConn.logout();
        }

        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * this method is invoked at the creation of RuntimeInterface instance,
     * where a RuntimeInterface instance put itself in RuntimeInterfaceList
     * in order that DomeConnection keeps the list of the interface that should be closed when the dome connection closes.
     * @param runtimeInterface
     */
    public void addToRuntimeInterfaceList(RuntimeInterface runtimeInterface) {
        runtimeInterfaceList.add(runtimeInterface);
    }

    /**
     * this method is invoked when user manually invoke close() of RuntimeInterface class
     * once RuntimeInterface is closed DomeConnection doesn't have to keep the RuntimeInterface in the list.
     * @param runtimeInterface
     */
    public void removeFromRuntimeInterfaceList(RuntimeInterface runtimeInterface) {
        runtimeInterfaceList.remove(runtimeInterface);
    }

    public void addToAnalysisToolRuntimeInterfaceList(RuntimeAnalysisToolInterface runtimeInterface)
    {
        _runtimeAnalysisToolInterfaceList.add(runtimeInterface);
    }

    /**
     * this method is invoked at the creation of RuntimePlayspace instance,
     * where a RuntimePlayspace instance put itself in runtimePlayspaceList
     * in order that DomeConnection keeps the list of the playspace that should be closed when the dome connection closes.
     * @param runtimePlayspace
     */
    public void addToRuntimePlayspaceList(RuntimePlayspace runtimePlayspace) {
        runtimePlayspaceList.add(runtimePlayspace);
    }

    /**
     * this method is invoked at the creation of RuntimeProject instance,
     * where a RuntimeProject instance put itself in runtimeProjectList
     * in order that DomeConnection keeps the list of the project that should be closed when the dome connection closes.
     * @param runtimeProject
     */
    public void addToRuntimeProjectList(RuntimeProject runtimeProject) {
        runtimeProjectList.add(runtimeProject);
    }

    /**
     * used by DomeFolder, DomeModel, DomeInterface to access the ServerConnection instance
     */
    public ServerConnection getServerConnection() {
        return serverConn;
    }

    /**
     * used by DomeInterface to access the DomeRuntimeClient instance
     */
    public DomeRuntimeClient getDomeRuntimeClient() {
        return drClient;
    }

    /***
     * if user is "guest", loginType automatically becomes LoginUtils.GUEST type.
     * returns null if login fails
     * @param user (ex) tutorialUser
     * @param password (ex) 123
     * @param serverPort (ex) localhost:8080
     * @param clientUrl (ex) http://serverName:port/RPC2
     */
    private static ServerConnection login(String user, String password, String serverPort, String clientUrl) {
		String loginType = null;
		if (user.equals("") || user.equalsIgnoreCase("guest")) { // guest login
			loginType = LoginUtils.GUEST;
		} else {
			loginType = LoginUtils.USER;
		}

		if (password == null) {
            password = "";
        }

        char[] pwdChars = password.toCharArray();
		String pwd = new String(pwdChars);
		// clear password char array
		for (int i = 0; i < pwdChars.length; ++i)
			pwdChars[i] = '0';
		byte[] encryptedPwd = LoginUtils.encryptPassword(pwd);

		if (serverPort == null || serverPort.trim().equals("")) {
			serverPort = DomeServer.getDefaultServerPort();
        } else if (serverPort.indexOf(':') == -1) {
			serverPort += ":8080";
        }

		try {
            ServerConnection svrCon = new ServerConnection(loginType, serverPort, clientUrl);
            if (svrCon.login(user, encryptedPwd)) {
                return svrCon;
            }
            return null; // should never get here since unsuccessful logins should throw exception;
		} catch (Exception e) {
			System.err.print(e);
            return null;
		}
    }

    /**
     * get an interface using a complete path on the server
     * @param space: only "server", "users", or "groups"
     * @param path: Example: "Public/photovoltaic/PV array operation/PV operation - simple Interface"
     * @return an interface
     * by Sangmok, Sittha
     */
    public DomeInterface getInterfaceByPath(String space, String path) {
        return getInterfaceByPath(space, path.split("/"));
    }

    /**
     * get an interface using a complete path in "server" space of the server
     * @param path: Example: "Public/photovoltaic/PV array operation/PV operation - simple Interface"
     * @return an interface
     */
    public DomeInterface getInterfaceByPath(String path) {
        return getInterfaceByPath(SERVER_SPACE, path.split("/"));
    }

    /**
     * get an interface using a complete path on the server
     * for the case when any path element (ex. interface name) has '/' in its literal
     * @param space: only "server", "users", or "groups"
     * @param pathArray: Example: { "Public", "photovoltaic", "PV array /OK/ operation", "PV operation /OK/ simple Interface"
     * @return an interface
     * by Sittha, Sangmok
     */
    public DomeInterface getInterfaceByPath(String space, String[] pathArray) {
        DomeFolder pFold = getPubPriFolder(space, pathArray [0], (String) pathArray [1]);
        int startIndex = space.equalsIgnoreCase("server") ? 1 : 2;
        // browse through the subfolders
        DomeFolder thisFold = pFold;
        for (int i = startIndex; i < pathArray.length - 2; i++) {
            thisFold = thisFold.getFolder(pathArray [i]);
        }

        /* second to last item can be either model or project.
         * therefore first find model with the given name,
         * and if there is no matching model,
         * a project with the same name will be searched for */
        DomeModel mod = thisFold.getModelByName(pathArray [pathArray.length - 2]); // second to last item
        if (mod != null) {
            return mod.getInterfaceByName(pathArray [pathArray.length - 1]); // last item
        } else {
            DomeProject proj = thisFold.getProjectByName(pathArray [pathArray.length - 2]); // second to last item
            if (proj == null) {
                throw new RuntimeException("invalid interface path: no such model or project named as '" + pathArray [pathArray.length - 2] + "'");
            }
            return proj.getInterfaceByName(pathArray [pathArray.length - 1]); // last item
        }
    }

    private DomeFolder getPubPriFolder(String space, String topName, String nextName) {
        // server space
        if (space.startsWith("s") || space.startsWith("S")) {
            // the top folder is either "Public" or "Private"
            if (topName.equalsIgnoreCase("Public")) {
                return getServerPublicFolder();
            } else if (topName.equalsIgnoreCase("Private")) {
                return getServerPrivateFolder();
            } else {
                throw new RuntimeException("Invalid path: " + topName + ". Should be either 'Public' or 'Private'");
            }
        }
        // users space
        else if (space.startsWith("u") || space.startsWith("U")) {
            // the top folder has a user name, the next folder is either "Public" or "Private"
            if (nextName.equalsIgnoreCase("Public")) {
                return getUserPublicFolder(topName);
            } else if (nextName.equalsIgnoreCase("Private")) {
                return getUserPrivateFolder(topName);
            } else {
                throw new RuntimeException("Invalid path: " + nextName + ". Should be either 'Public' or 'Private'");
            }
        }
        // groups space
        else if (space.startsWith("g") || space.startsWith("G")) {
            // the top folder has a group name, the next folder is either "Public" or "Private"
            if (nextName.equalsIgnoreCase("Public")) {
                return getGroupPublicFolder(topName);
            } else if (nextName.equalsIgnoreCase("Private")) {
                return getGroupPrivateFolder(topName);
            } else {
                throw new RuntimeException("Invalid path: " + nextName + ". Should be either 'Public' or 'Private'");
            }
        } else {
            throw new RuntimeException("Invalid type of space specified.");
        }
    }

    /**
     * get a model using a complete path on the server
     * @param space: only "server", "users", or "groups"
     * @param path: Example: "Public/photovoltaic/PV array operation"
     * @return a model
     * by Sittha
     */
    public DomeModel getModelByPath(String space, String path) {
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0,path.length()-1);
        return getModelByPath(space, path.split("/"));
    }

    /**
     * get a model using a complete path on the server
     * for the case when any path element (ex. interface name) has '/' in its literal
     * @param space: only "server", "users", or "groups"
     * @param pathArray: Example: { "Public", "photovoltaic", "PV array /OK/ operation"}
     * @return a model
     * by Sittha
     */
    public DomeModel getModelByPath(String space, String[] pathArray) {
        DomeFolder pFold = getPubPriFolder(space, pathArray[0], pathArray[1]);
        int startIndex = space.equalsIgnoreCase("server") ? 1 : 2;
        // browse through the subfolders
        DomeFolder thisFold = pFold;
        for (int i = startIndex; i < pathArray.length - 1; i++) {
            thisFold = thisFold.getFolder(pathArray[i]);
        }
        return thisFold.getModelByName(pathArray[pathArray.length - 1]); // last item
    }

    /**
     * get a project using a complete path on the server
     * @param space: only "server", "users", or "groups"
     * @param path: Example: "Public/photovoltaic/PV array operation"
     * @return a Project
     * by Sittha
     */
    public DomeProject getProjectByPath(String space, String path) {
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        return getProjectByPath(space, path.split("/"));
    }

    /**
     * get a Project using a complete path on the server
     * for the case when any path element (ex. interface name) has '/' in its literal
     * @param space: only "server", "users", or "groups"
     * @param pathArray: Example: { "Public", "photovoltaic", "PV array /OK/ operation"}
     * @return a Project
     * by Sittha
     */
    public DomeProject getProjectByPath(String space, String[] pathArray) {
        DomeFolder pFold = getPubPriFolder(space, pathArray[0], pathArray[1]);
        int startIndex = space.equalsIgnoreCase("server") ? 1 : 2;
        // browse through the subfolders
        DomeFolder thisFold = pFold;
        for (int i = startIndex; i < pathArray.length - 1; i++) {
            thisFold = thisFold.getFolder(pathArray[i]);
        }
        return thisFold.getProjectByName(pathArray[pathArray.length - 1]); // last item
    }
}
