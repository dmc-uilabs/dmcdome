package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;
import mit.cadlab.dome3.network.server.functions.ServerPeerFunctions;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.playspace.ServerPlayspace;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.gui.runbrowser.RunBrowser;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import org.apache.xmlrpc.XmlRpcException;
import org.dom4j.Element;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.lang.reflect.Array;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Apr 12, 2003
 * Time: 5:44:00 PM
 * To change this template use Options | File Templates.
 */
public class ProjectResourceInfoRuntime extends ProjectResourceInfo
{
	// client url of this server
	private static String clientUrl = "http://" + NetworkUtils.getHostName() + ":" + DomeServer.getPort() + "/RPC2";
	private Object resourceObject; // used in ClientProjectRecord // todo: get rid of this
	private String resourceXmlDescription;
	private CompoundId playspaceAndModelId; // all interfaces subscribed to this resource should be in the same playspace
	private ServerConnection svrConn; // for now, all subscriptions will use the same (first) server connection created
	private HashMap subscribedInterfaceVersions = new HashMap(); // key = resourceId.ifaceId; value = version
	private HashMap subscribedInterfaceGraphs;
	private HashMap modelParamIdToMultiItemNode;
	private HashMap ifaceParamIdToModelParamId; // ifaceParamId is <ifaceId.paramId>
	private DirectedGraph resourceGraph; // suitable for inclusion in project graph

    public static byte[] _password;

	public ProjectResourceInfoRuntime(Element xmlDescription)
	{
		super(xmlDescription);
	}

	public CompoundId getRuntimeId()
	{
		return playspaceAndModelId;
	}

	public void setXmlDescription(String xml)
	{
		resourceXmlDescription = new String(xml);
	}

	public String getXmlDescription()
	{
		return resourceXmlDescription;
	}

	public HashMap getSubscribedInterfaceVersions()
	{
		return subscribedInterfaceVersions;
	}

	public Object getObject()
	{
		return resourceObject;
	}

	public void setObject(Object resourceObject)
	{
		this.resourceObject = resourceObject;
	}

	public void closeResource()
	{
		ServerPeerFunctions.deleteRemoteResource(svrConn, playspaceAndModelId, getType());
	}

	public void notifyProjectRunComplete() {
		if (isSubscribed()) {
			ServerPeerFunctions.notifyProjectRunCompleted(svrConn, playspaceAndModelId);
		}
	}

	/**
	 * this method should only be called after all subscribed interfaces have been loaded
	 */
	public DirectedGraph getSubscribedInterfaceGraph(String ifaceId)
	{
		if (subscribedInterfaceIds.isEmpty())
			return new DirectedGraph();
		else if (subscribedInterfaceGraphs == null)
			loadSubscribedInterfaceInfo();
		return (DirectedGraph) subscribedInterfaceGraphs.get(ifaceId);
	}

	/**
	 * this method should only be called after all subscribed interfaces have been loaded
	 */
	private void loadSubscribedInterfaceInfo()
	{
		// call method on resource
		if (svrConn==null) {
			throw new RuntimeException("can not load Subscribed Interface info for " + resourceName +
			                           ": no server connection available; subscribed interface not loaded yet");
		}

        Vector graphInfo = null;
        if(!type.equals("imodel")){
        graphInfo = ServerPeerFunctions.getResourceGraph(svrConn, playspaceAndModelId, new Vector(subscribedInterfaceIds));
        }
        else { //nested i-model resource
            ServerPlayspace playspace =  null ; //current playspace
            try {
                playspace = RuntimeFunctionsServer.getPlayspace(null, playspaceAndModelId);  //here is the problem
                graphInfo = playspace.getResourceGraph(playspaceAndModelId, new Vector(subscribedInterfaceIds)); // correct
            } catch (XmlRpcException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }

        }

		modelParamIdToMultiItemNode = new HashMap();
		ifaceParamIdToModelParamId = new HashMap();
		Vector multiItemNodeXml = (Vector) graphInfo.get(0);
		Element xml;
		MultiItemNode node;
		String modelParamId;
		for (int i = 0; i < multiItemNodeXml.size(); i++) {
			xml = XMLUtils.stringToXmlElement((String) multiItemNodeXml.elementAt(i));
			node = (MultiItemNode) DirectedGraph.parseXmlRef(xml);
			modelParamId = node.getId();
			modelParamIdToMultiItemNode.put(modelParamId, node);
			Iterator ifaceParamIds = node.getItems().iterator();
			while (ifaceParamIds.hasNext()) {
				ifaceParamIdToModelParamId.put(ifaceParamIds.next(), modelParamId);
			}
		}

		subscribedInterfaceGraphs = new HashMap();
		Hashtable interfaceInfo = (Hashtable) graphInfo.get(1);
		Iterator infoList = interfaceInfo.entrySet().iterator();
		Map.Entry entry;
		String ifaceId;
		Element graphXml;
		DirectedGraph iGraph;
		while (infoList.hasNext()) {
			entry = (Map.Entry) infoList.next();
			ifaceId = (String) entry.getKey();
			graphXml = XMLUtils.stringToXmlElement((String) entry.getValue());
			iGraph = new DirectedGraph(graphXml);
			subscribedInterfaceGraphs.put(ifaceId, iGraph);
		}

		resourceGraph = getAggregateInterfaceGraph(subscribedInterfaceIds, modelParamIdToMultiItemNode);
	}

	/**
	 * This graph is the combined subscribed interfaces graph
	 * @return
	 */
	public DirectedGraph getResourceGraph()
	{
		if (resourceGraph == null)
		{
			if (!isSubscribed())
				resourceGraph = new DirectedGraph();
			else
				loadSubscribedInterfaceInfo();
		}
		return resourceGraph;
	}

	public DirectedGraph getAggregateInterfaceGraph(List ifaceIds)
	{
		if (ifaceIds == null || ifaceIds.isEmpty())
			return new DirectedGraph();
		else if (ifaceIds.size() == 1)
			return getSubscribedInterfaceGraph((String) ifaceIds.get(0));
		else {
			DSet s1 = new DSet(subscribedInterfaceIds);
			DSet s2 = new DSet(ifaceIds);
			if (s1.equals(s2))
				return resourceGraph;
			Collection missingIfaces = DSet.removeSet(s2, s1);
			if (!missingIfaces.isEmpty())
				throw new IllegalArgumentException("ProjectResourceRuntimeInfo.getAggregateInterfaceGraph: " +
				                                   "got unsubscribed interface id " + missingIfaces);

			// calculate the subset of multiItemNodes for the set of ifaceIds
			HashMap multiItemNodesSubset = new HashMap(); // modelParamId to MultiItemNode
			Iterator nodes = modelParamIdToMultiItemNode.entrySet().iterator();
			Map.Entry entry;
			String modelParamId, itemIfaceId;
			MultiItemNode node;
			List nodeItems, ifaceItems;
			while (nodes.hasNext()) {
				entry = (Map.Entry) nodes.next();
				modelParamId = (String) entry.getKey();
				node = (MultiItemNode) entry.getValue();
				nodeItems = node.getItems();
				ifaceItems = new ArrayList();
				String s;
				for (int i = 0; i < nodeItems.size(); i++) {
					s = (String) nodeItems.get(i);
					itemIfaceId = s.substring(0, s.indexOf("."));
					if (ifaceIds.contains(itemIfaceId))
						ifaceItems.add(s);
				}
				if (ifaceItems.size() > 1)
					multiItemNodesSubset.put(modelParamId, new MultiItemNode(modelParamId, ifaceItems));
			}
			return getAggregateInterfaceGraph(ifaceIds, multiItemNodesSubset);
		}
	}

	private DirectedGraph getAggregateInterfaceGraph(List ifaceIds, HashMap multiItemNodes)
	{
		DirectedGraph graph = new DirectedGraph();
		String ifaceId;
		DirectedGraph ifaceGraph;
		for (int i = 0; i < ifaceIds.size(); i++) {
			ifaceId = (String) ifaceIds.get(i);
			ifaceGraph = (DirectedGraph) subscribedInterfaceGraphs.get(ifaceId);

			// convert ifaceGraph to use MultiItemNodes if applicable; prefix iParamId w/ifaceId
			HashMap nodeMap = new HashMap(); // old node, new node
			Iterator nodes = ifaceGraph.getNodes().iterator();
			Object obj;
			String ipId, mpId;
			while (nodes.hasNext()) {
				obj = nodes.next();
				if (obj instanceof MultiItemNode) {
					mpId = ((MultiItemNode) obj).getId();
					nodeMap.put(obj, multiItemNodes.get(mpId)); // use the one in multiItemNodes map
				}
				else { // ifaceParamId
					ipId = ifaceId + "." + obj;
					mpId = (String) ifaceParamIdToModelParamId.get(ipId);
					if (mpId != null) {
						if (multiItemNodes.containsKey(mpId)) {
							nodeMap.put(obj, multiItemNodes.get(mpId));
						}
						else {
							nodeMap.put(obj, ipId);
						}
					}
					else {
						nodeMap.put(obj, ipId);
					}
				}
			}
			graph.addGraph(new DirectedGraph(ifaceGraph, nodeMap));
		}
		return graph;
	}

	/**
	 * Construct a dome or plugin model.
	 */
	public void loadSubscription(DefaultSubscription subscriptionInfo)
            throws XmlRpcException {

        if (svrConn == null) {
            if (!type.equals(IMODEL_RESOURCE)) {
                // create connection to resource
                String serverPort = getResourcePort();
                String serverHostName = getResourceHostName();
                svrConn = LoginUtils.login(subscriptionInfo.getLoginType(),
                        subscriptionInfo.getLoginName(),
                        clientUrl,
                        serverHostName + ":" + serverPort,
                        subscriptionInfo.getLoginPassword());
            }

//for nested i-model resources (connect to home server): _i
            if (type.equals(IMODEL_RESOURCE)) {
                String serverPort = "8080";    //  todo this only works when working on home machine
                String serverHostName;
                serverHostName = "localhost";   //  todo this only works when working on home machine
                String loginType = "ADMIN";
                String loginName = "root";
                byte[] loginPassword = _password;

                svrConn = LoginUtils.login(loginType,
                        loginName,
                        clientUrl,
                        serverHostName + ":" + serverPort,
                        loginPassword);

            }
        }


        CompoundId interfaceId = null;
        if (playspaceAndModelId != null)
            interfaceId = new CompoundId(playspaceAndModelId);
        else {
            CompoundId modelId = ((DomeModelRuntime) subscriptionInfo.getModel()).getRuntimeId();
            interfaceId = new CompoundId();
            interfaceId.setPlayspaceRuntimeId(modelId.getPlayspaceRuntimeId()); // project gui assumes all items in a project are in the same playspace
            //interfaceId.addParentProjectRuntimeId(modelId.getLastProjectRuntimeId());
            // also add parent project runtime id to this one to indicate it is a resource model
            // leave project static id out to distinguish this from an imodel
        }

        //check if subscription is i-model _i
        //if yes then find the static Id (= deploydId) through the buildId from the server database
        //since the subscription info has the build Id only

        if (!type.equals(IMODEL_RESOURCE)) {
            interfaceId.setInterfaceStaticId(subscriptionInfo.getIfaceId());
        } else if (type.equals(IMODEL_RESOURCE)) {
            //get subscription buildId
            String buildId = subscriptionInfo.getIfaceId();

            //find staticId from database - this is the deploy Id
            if (buildId != null) {
                String getStaticIdQuery = "select INTERFACE_ID from INTERFACE_VERSIONS where BUILD_ID='"
                        + buildId + "'";
                try {
                    Statement stmt = DbUtils.getStatement();
                    ResultSet r;

                    // get model id
                    r = stmt.executeQuery(getStaticIdQuery);
                    if (!r.next())
                        throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_INTERFACE,
                                DbErrors.XMLRPC_NO_SUCH_INTERFACE_MSG);
                    else {
                        String staticId = r.getString("INTERFACE_ID");
                        interfaceId.setInterfaceStaticId(staticId);
                    }
                } catch (Exception e) {
                    System.err.println("getting interface static id failed : " + e.getMessage());
                }
            } else {
                System.err.println("loadSubsription failed - no interface build id: " + buildId);
            }
            //since resource is nested i-model it should already be running. find its runtime id. _i
        }

        Object[] results = null;
        if (!type.equals(IMODEL_RESOURCE)) {
            results = RuntimeFunctionsClient.createInterfaceAndPlayspace(svrConn, interfaceId, false, false, true);
            if (results == null)
                return;
        } else {
            // two possibilities: either resource is already loaded as i-model (get its running instance) _i
            // or resource is not loaded yet (load the imodel)
            String imodelStaticId = this.resourceDeployId;
            DomeModelRuntime subscribing_model = (DomeModelRuntime) subscriptionInfo.getModel();
            IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) subscribing_model.getIntegrationProject();
            ProjectIntegrationModelInfo iModelInfo = project.getIntegrationModel(imodelStaticId); // check if imodel is already loaded using deployId
            String sessionId = svrConn.getConnectionId(); // trying out...

            if (iModelInfo == null) {  // i-model is not loaded yet - load it
                String buildId = resourceUniqueId; // get i-model build Id from the resource information
                project.createIntegrationModelAsResource(imodelStaticId, buildId);  // create the i-model inside the project
                iModelInfo = project.getIntegrationModel(imodelStaticId);   // now the i-model is loaded
            }

            //get the subscription interface from the existing i-model resource instance
            DomeModelRuntime subscription_model = (DomeModelRuntime) iModelInfo.model;
            subscription_model.setIsProjectResource(true);  //todo check if this causes solving problems in other cases!
            ServerPlayspace playspace = RuntimeFunctionsServer.getPlayspace(null, interfaceId);
            Vector imodelInterfaceResults = playspace.loadImodelInterfaceAsResource(interfaceId.getInterfaceStaticId(), subscription_model,sessionId);
            results = new Object[5];
            results[2] = imodelInterfaceResults.elementAt(2);
            results[3] = imodelInterfaceResults.elementAt(3);
            results[4] = imodelInterfaceResults.elementAt(4);
        }

        interfaceId = (CompoundId) results[2]; // has runtime interface id
        String version = (String) results[3];
        subscribedInterfaceVersions.put(interfaceId.getInterfaceStaticId(), version);

        if (playspaceAndModelId == null) {
            playspaceAndModelId = new CompoundId(interfaceId); // resource id is same as interface id without the interface info
            playspaceAndModelId.setInterfaceRuntimeId(null);
            playspaceAndModelId.setInterfaceStaticId(null);
        }

        // create the subscription interface
        Element ifaceElement = XMLUtils.stringToXmlElement((String) results[4]);
        SubscriptionInterface iface = new SubscriptionInterface(interfaceId, svrConn, ifaceElement);
        subscriptionInfo.setInterface(iface); // store the interface in the subscription structure

        // add to the global id object map
        RuntimeFunctionsServer.addToGlobalObjectMap(interfaceId, iface);
    }

	public void setExternalGraph(String graphXml) {
		ServerPeerFunctions.setResourceExternalGraph(svrConn, playspaceAndModelId, graphXml);
	}

	// todo: move this method into base class
	public boolean isSubscribed() {
		return !subscribedInterfaceIds.isEmpty();
	}
}

