package mit.cadlab.dome3.network.server;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 16, 2003
 * Time: 10:25:28 PM
 * To change this template use Options | File Templates.
 */
public class ServerPeerConstants
{
	public static final String FUNC_TYPE_SERVER_PEER = "ServerPeer"; // both a client- and server-side handler

	// methods
	public static final String DELETE_REMOTE_RESOURCE = "deleteRemoteResource";
	public static final String GET_RESOURCE_GRAPH = "getResourceGraph";
    public static final String SET_RESOURCE_EXTERNAL_GRAPH = "setResourceExternalGraph";
	public static final String NOTIFY_PROJECT_RUN_COMPLETE = "notifyProjectRunComplete";

}
