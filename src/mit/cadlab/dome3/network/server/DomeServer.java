// DomeServer.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.handlers.BrowseFileSystemHandler;
import mit.cadlab.dome3.network.server.handlers.CheckOutHandler;
import mit.cadlab.dome3.network.server.handlers.ClientHandler;
import mit.cadlab.dome3.network.server.handlers.DeployFilesHandler;
import mit.cadlab.dome3.network.server.handlers.DeployFileSystemHandler;
import mit.cadlab.dome3.network.server.handlers.FileSystemHandler;
import mit.cadlab.dome3.network.server.handlers.PermissionHandler;
import mit.cadlab.dome3.network.server.handlers.RuntimeHandler;
import mit.cadlab.dome3.network.server.handlers.ServerAdministrationHandler;
import mit.cadlab.dome3.network.server.handlers.ServerPeerHandler;
import mit.cadlab.dome3.network.server.handlers.UserGroupHandler;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbInit;
import mit.cadlab.dome3.network.server.db.DbUtils;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClientLite;
import org.apache.xmlrpc.XmlRpcException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import mit.cadlab.dome3.network.server.handlers.ConferenceServerHandler;
import mit.cadlab.dome3.network.server.conference.ConferenceServer;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;

/**
 * The DomeServer class consists of all the functionality required in the DOME Server.
 * This is a restructuring of the original implementation
 * TODO: support databases on machines other than DomeServer
 */
public class DomeServer {

    private String hostName;  //name or IP address of the host
    private WebServer webserver;
    private Process dbProcess;
    private static int port;
    private static String defaultServerPort = null;
    private static HashMap clientConnections = new HashMap();
    private static HashMap resourceClientMap = new HashMap();
    private static HashMap globalObjectMap = new HashMap();
    private static AggregatorMap subscriptionInterfacesMap = new AggregatorMap(); // resource->subscriptions
    private static Object resourceLock = new Object();
    private static PreparedStatement getUserURLQuery = null;


    public static void registerSubscriptionInterface(CompoundId resourceId, String sessionId, CompoundId subscriptionId) {
        subscriptionInterfacesMap.put(resourceId, new Object[]{sessionId, subscriptionId});
    }

    public static List getSubscriptionInterfaces(CompoundId resourceId) {
        return (List) subscriptionInterfacesMap.get(resourceId);
    }

    public static void removeSubscriptionInterfaces(CompoundId resourceId) {
        subscriptionInterfacesMap.remove(resourceId);
    }

    public static void addToGlobalObjectMap(CompoundId objectId, Object object) {
        CompoundId runtimeId = new CompoundId(objectId);
        runtimeId.resetStaticInfo();
        globalObjectMap.put(runtimeId.toString(), object);
    }


    public static Object getFromGlobalObjectMap(CompoundId objectId) {
        CompoundId runtimeId = new CompoundId(objectId);
        runtimeId.resetStaticInfo();
        return globalObjectMap.get(runtimeId.toString());
    }


    /**
     * Construct the default server port, consisting of the hostname and listen port.
     *
     * @return Server port string
     */
    public static String getDefaultServerPort() {
        if (defaultServerPort == null) {
            InetAddress addr = null;
            try {
                addr = InetAddress.getLocalHost();
                defaultServerPort = addr.getCanonicalHostName();
            } catch (UnknownHostException e) {
                defaultServerPort = "localhost";
            }
            defaultServerPort += ":8080";
        }
        return defaultServerPort;
    }


    /**
     * Dome server constructor
     * The server URL is "http://localhost:port/RPC2"
     *
     * @param serverPort The port number to be used by clients to talk to the server
     * @param dbPort     The port number used by server or client to talk to the db
     * @param dbFileName The name of the database file to start
     */
    public DomeServer(int serverPort, int dbPort, String dbFileName)
            throws XmlRpcException {
        DomeClientApplication.DOME_SERVER_MODE = true;
        DomeInit.initializeDOME();

        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (UnknownHostException e) {
            hostName = "localhost";
        }

        this.port = serverPort;
        try {
            // shutdown hook needs to be added first just in case anything wrong happens!!!
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    //System.out.println("cleaning up aux file folder");
                    //cleanUpAuxFileFilder();
                    shutdown(); // shutdown database and destroy database process

                    //qing add gabage collection here
                    System.gc();
                }
            });
            startDatabase(dbPort, dbFileName);

            webserver = new WebServer(port);
            webserver.addHandler(DbConstants.FUNC_TYPE_CLIENT, new ClientHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_USER_GROUP, new UserGroupHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_FILESYSTEM, new FileSystemHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_PERMISSION, new PermissionHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_DEPLOYFILESYSTEM, new DeployFileSystemHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_BROWSEFILESYSTEM, new BrowseFileSystemHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_SERVER_ADMIN, new ServerAdministrationHandler(this));
            webserver.addHandler(RuntimeConstants.FUNC_TYPE_RUNTIME, new RuntimeHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_DEPLOY_FILES, new DeployFilesHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_CHECKOUT_FILES, new CheckOutHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_CONFERENCE_SERVER, new ConferenceServerHandler());
            webserver.addHandler(DbConstants.FUNC_TYPE_SERVER_PEER, new ServerPeerHandler());

            System.out.println(hostName + ": DomeServer started on port " + port + " at " + new Date());
        } catch (IOException ie) {
            throw new RuntimeException("Could not instantiate DomeServer because of I/O exception");
        }

        // create prepared statements
        try {
            getUserURLQuery = DbUtils.getPreparedStatement("select URL from SESSIONS where ID=?");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new XmlRpcException(e.getErrorCode(), e.getMessage());
        }
    }

    public String getHostName() {
        return hostName;
    }

    public static int getPort() {
        return port;
    }

    /**
     * Return the name of the host this Server is running on, along with the specific port
     * that it's listening on. This may be different than than the default port.
     * 
     * @return String hostname:port
     */
    public static String getHostnameAndPort() {
    	String defaultHostnameAndPort = getDefaultServerPort();
		String serverPort = "" + getPort();
		String hostname = defaultHostnameAndPort.substring(0, defaultHostnameAndPort.indexOf(":") + 1);
		String hostnameAndPort = hostname + serverPort;
		
		return hostnameAndPort;
    }
    
    /**
     * Start the database in a new, detatched process.
     *
     * @param dbPort     Database listen port number
     * @param dbFileName Database file name
     */
    private void startDatabase(int dbPort, String dbFileName) {
        try {
            DbUtils.setDbUrl(dbPort);
            String classpath = System.getProperty("java.class.path");
            if (classpath == null)
                classpath = "";
            else
                classpath = "-cp " + classpath;
            String cmd = "java -Xms128M -Xmx720M " + classpath + " org.hsqldb.Server " +
                    "-port " + dbPort + " -database " + dbFileName;

            System.out.println(cmd);

            // start the database process
            System.out.print("Database: starting");
            System.out.flush();
            dbProcess = Runtime.getRuntime().exec(cmd);
            boolean dbSuccess = false;

            // test2 that the database started successfully
            for (int i = 0; dbSuccess == false && i < 20; i++) { // try up to 1 minute
                try {
                    DbUtils.getConnection();
                    dbSuccess = true;
                } catch (SQLException e) {
                    // failed connection; wait, then try again
                    System.out.print(".");
                    System.out.flush();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                }
            }

            System.out.println("");

            if (!dbSuccess) {
                System.err.println("Error starting database.");
                System.err.println(cmd);
                BufferedReader inStream = new BufferedReader(new InputStreamReader(dbProcess.getErrorStream()));
                String line = inStream.readLine();
                while (line != null) {
                    System.err.println(line);
                    line = inStream.readLine();
                }
                inStream.close();
                System.exit(-1);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.err.println("Could not start the database due to an I/O exception.");
            System.exit(-1);
        }
    }

    /**
     * Commit the database transaction log and shut down the database process.
     */
    public synchronized void shutdown() {
        if (dbProcess == null) // already shutdown once
            return;

        try {
            System.out.println("Stopping playspaces");
            RuntimeFunctionsServer.killPlayspaces();
            Statement stmt = DbUtils.getStatement();
            System.out.println("Database: about to write out database");
            stmt.execute("COMMIT");
            System.out.println("Database: committed");
            stmt.execute("SHUTDOWN");
            System.out.println("Database: shutdown");
        } catch (SQLException e) {
            System.out.println("SQL Exception: Database did not shutdown cleanly.\n\t" + e.getMessage());
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            System.out.println("Killing database process");
            if (dbProcess != null) {
                dbProcess.destroy();
                dbProcess = null;
            }
            System.out.println("Killed the database process");
        }
    }

    /**
     * Store a client session id. This uses the clientConnections map. The entry
     * being stored here will be replaced when the client performs a request
     * that requires a return connection.
     *
     * @param sessionId
     */
    public static void clientLogin(String sessionId) {
        clientConnections.put(sessionId, null);
    }


    /**
     * Get news about a client logout and inform any other modules that are interested.
     *
     * @param sessionId
     */
    public static void clientLogout(String sessionId) {
        removeClientConnection(sessionId);
        // todo: informing the other modules should probably be handled using listeners
        ConferenceServer.clientLogout(sessionId);
    }


    /**
     * Store a client connection object associated with the given session id.
     *
     * @param sessionId Client session id
     * @throws XmlRpcException
     */
    public static void addClientConnection(String sessionId)
            throws XmlRpcException {
        if (clientConnections.get(sessionId) == null) {
            String clientUrl = null;
            XmlRpcClientLite clientConnection = null;

            // get client URL
            try {
                getUserURLQuery.setString(1, sessionId);
                ResultSet rs = getUserURLQuery.executeQuery();
                Vector v = DbUtils.oneRowResultSetToVector(rs);
                if (v.isEmpty()) {
                    throw new XmlRpcException(DbErrors.XMLRPC_LOGIN_FAILED,
                            DbErrors.XMLRPC_LOGIN_FAILED_MSG);
                }
                if (v.size() == 0)
                    throw new XmlRpcException(DbErrors.XMLRPC_BAD_SESSION_ID, DbErrors.XMLRPC_BAD_SESSION_ID_MSG);
                clientUrl = (String) v.get(0);
            } catch (Exception e) {
                throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
            }

            try {
                clientConnection = new XmlRpcClientLite(clientUrl);
            } catch (MalformedURLException e) {
                throw new XmlRpcException(DbErrors.XMLRPC_CLIENT_CONNECTION_ERROR, e.getMessage());
            }

            // add the user to the connection map
            clientConnections.put(sessionId, clientConnection);
        }
    }


    /**
     * Get a client connection object associated with the session id.
     *
     * @param sessionId Client session id
     * @return
     */
    public static XmlRpcClientLite getClientConnection(String sessionId) {
        return (XmlRpcClientLite) clientConnections.get(sessionId);
    }


    /**
     * Remove a client connection associated with the session id. This
     * will happen when a client logs out.
     *
     * @param sessionId
     */
    public static void removeClientConnection(String sessionId) {
        clientConnections.remove(sessionId);
    }


    /**
     * Store a mapping between a resource object and the clients who have accessed it.
     *
     * @param resourceRuntimeId Resource object id
     * @return boolean indicating success of the operation
     */
    public static boolean addToResourceClientMap(String resourceRuntimeId, String sessionId) {
        if (resourceRuntimeId == null || sessionId == null)
            return false;

        synchronized (resourceLock) {
            ArrayList userList = (ArrayList) resourceClientMap.get(resourceRuntimeId);
            if (userList == null) {
                // create the user list
                userList = new ArrayList();
                resourceClientMap.put(resourceRuntimeId, userList);
            }

            // add the sessionId to the user list
            if (!userList.contains(sessionId)) {
                userList.add(sessionId);
                return true;
            }

            return false;
        }
    }


    /**
     * Get a list of client session ids associated with a resource object.
     *
     * @param resourceRuntimeId Resource id
     * @return List of client session ids
     */
    public static List getResourceClients(String resourceRuntimeId) {
        synchronized (resourceLock) {
            ArrayList list = (ArrayList) resourceClientMap.get(resourceRuntimeId);
            if (list == null)
                return Collections.EMPTY_LIST;
            return list;
        }
    }


    /**
     * Remove a client session id from a resource list.
     *
     * @param resourceRuntimeId Resource runtime id
     * @param sessionId         Client session id
     */
    public static void removeResourceClient(String resourceRuntimeId, String sessionId) {
        ArrayList list = (ArrayList) resourceClientMap.get(resourceRuntimeId);
        list.remove(sessionId);
    }


    /**
     * Method to start the DOME server from a command line
     *
     * @param args Command line arguments to the server
     *             Arguments are optional. Three arguments are possible, the first being the
     *             server port, the second is the database port, and the third is the database file.
     *             The default server port is 8080 and the default database port is 9001 and the
     *             default database files is specified in DbInit.getDbFileName().
     *             Arguments beyond the first three will be ignored.
     */
    public static void main(String[] args) {

        int debugIndex = 0;
        for (; debugIndex < args.length; debugIndex++) {
            String arg = args[debugIndex];
            if (arg.startsWith("-debug:")) {
                String debugLevelString = arg.substring(7);
                try {
                    int debugLevel = Integer.parseInt(debugLevelString);
                    Debug.setDebugLevel(debugLevel);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid debug level: " + debugLevelString);
                }
                break;
            }
        }

        String dbFileName = DbInit.getDbFileName();
        int svrPort = 8080;
        int dbPort = 9001;

        if (args.length > 0 && debugIndex > 0) {
            try {
                svrPort = Integer.parseInt(args[0]);
                if (args.length > 1 && debugIndex > 1) {
                    dbPort = Integer.parseInt(args[1]);
                    if (args.length > 2 && debugIndex > 2)
                        dbFileName = args[2];
                }
            } catch (NumberFormatException e) {
                usage();
                System.gc();
                System.exit(0);
            }
        }
        try {
            Debug.trace(Debug.DETAIL, "\nJAVAPATH = " + System.getProperty("java.library.path"));
            Debug.trace(Debug.DETAIL, "PATH = " + System.getenv("PATH") + "\n");

            new DomeServer(svrPort, dbPort, dbFileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //qing add gabage collection here
            System.gc();
            System.exit(0);
        }
    }

    /**
     * print the usage for the main method in the DomeServer class
     */
    public static void usage() {
        System.out.println("usage: DomeServer [serverPort] [dbPort] [dbFileName] [-debug:level]\n" +
                "      default - DomeServer 8080 9001 " + DbInit.getDbFileName() + "\n" +
                "      either specify serverPort, or both serverPort and dbPort, \n" +
                "      or serverPort, dbPort and dbFileName \n" +
                "      standard debug levels are 0,10,20,30,40,50 - default is 0 \n" +
                "      debug level must be last argument");
    }


    public static String FILE_SPACE_FOLDER_NAME_ON_SERVER = "AuxFiles";

    public static String getServerAuxFileRoot() {
        File root = new File(System.getProperty("user.dir"));
        return root.getPath() + File.separator + FILE_SPACE_FOLDER_NAME_ON_SERVER;
    }


}
