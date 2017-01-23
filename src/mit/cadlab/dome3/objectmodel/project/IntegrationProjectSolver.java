package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfoRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.objectmodel.util.solving.Parameters;
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.network.server.DomeServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.io.File;
import java.io.IOException;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import com.touchgraph.graphlayout.GLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 16, 2003
 * Time: 9:37:41 PM
 * To change this template use Options | File Templates.
 */
public class IntegrationProjectSolver
{
	private static final String PROJECT_GRAPH_FILENAME = "project_graph_info.txt";

	// constants used in xml read/writing
	private static final String XML_ID = "id";
	private static final String XML_VERSION = "version";

	IntegrationProjectServerRuntime project;
	File projectGraphInfoFile;
	DirectedGraph projectGraph;
	Element cachedProjectGraphXml;
	HashMap cachedResourceVersions = new HashMap(); // resourceId->{ifaceId->version}, or imodelId->version
	HashMap resourceVersions = new HashMap(); // resourceId->{ifaceId->version}, or imodelId->version
	HashMap converterGraphs = new HashMap(); // resource/imodelId, graph in projectGraph nodes
	HashMap converterMaps = new HashMap(); // resource/imodelId, projectGraphNodes->originalGraphNodes
	HashMap origParamIdToProjectNodeMap = new HashMap(); // origParamId = resourceId.ifaceId.paramId or imodelId.paramId
    AggregatorMap projectNodeToOrigParam;

	public IntegrationProjectSolver(IntegrationProjectServerRuntime isr)
	{
		this.project = isr;
		createInitialProjectGraph();
        //createAndSetExternalGraph();
	}

	/**
	 * @return true if info loaded from cache; otherwise, returns false
	 */
	private boolean loadCachedInfo() {
		File projDir = new File(DomeServer.getServerAuxFileRoot(), project.getRuntimeId().getCurrentProjectStaticId());
		projectGraphInfoFile = new File(projDir,PROJECT_GRAPH_FILENAME);
		if (!projDir.exists()) {
			projDir.mkdir(); // assume this works
			return false;
		}
		return parseProjectGraphInfoFile();
	}

	public void createInitialProjectGraph()
	{
		boolean graphChangedFromCache = !loadCachedInfo();
		List resources = project.getResourceModels();
		DirectedGraph origGraph, convertedGraph;
		String id;
		for (Iterator it = resources.iterator(); it.hasNext();) {
			ProjectResourceInfoRuntime prir = (ProjectResourceInfoRuntime) it.next();
			if (prir.isSubscribed()) { //&& shouldLoadResourceGraphInfo(prir)) {  todo shouldLoadResourceGraphInfo throws classCast exceptions - fix that
				graphChangedFromCache = true;                                     // todo check that Project graph is correct! imodels and imodel-resources are included twice in the graph???
				origGraph = prir.getResourceGraph();

				// add resourceId to beginning of each item
				id = prir.getResourceUniqueId();
				ResourceGraphNodeConverter converter = new ResourceGraphNodeConverter(id);
				convertedGraph = DirectedGraph.mapGraph(origGraph, converter);
				converterGraphs.put(id,convertedGraph);
				converterMaps.put(id,converter.nodeMap);
			}
		}

		List iModels = project.getIntegrationModels();
		for (Iterator iterator = iModels.iterator(); iterator.hasNext();) {
			ProjectIntegrationModelInfoRuntime pimir = (ProjectIntegrationModelInfoRuntime) iterator.next();
			DomeModelRuntime imodel = (DomeModelRuntime) pimir.getModel();
			if (shouldLoadIModelGraphInfo(imodel)) {
				graphChangedFromCache = true;
				origGraph = imodel.getGraph();

				// add imodelId to beginning of each imodel parameter id;
				// subscription parameters are mapped to project graph node (created in resource graphs above)
				id = imodel.getId().getIdString();
				IModelGraphNodeConverter converter = new IModelGraphNodeConverter();
				convertedGraph = DirectedGraph.mapGraph(origGraph, converter);
				converterGraphs.put(id, convertedGraph);
				converterMaps.put(id, converter.nodeMap);
			}
		}

		if (!graphChangedFromCache && (cachedResourceVersions.size()==resourceVersions.size())) {
			// load project graph from cached XML
			try {
				projectGraph = new DirectedGraph(cachedProjectGraphXml);
			}
			catch (Exception e) {
				System.err.println("error loading project graph from xml; will create new project graph for "+project.getName());
				projectGraph = null; // create from new information instead
			}
		}

		if (projectGraph == null) {
			// remove old information
			Collection oldIds = DSet.removeSet(cachedResourceVersions.keySet(),resourceVersions.keySet());
			String oldId;
			for (Iterator iterator = oldIds.iterator(); iterator.hasNext();) {
				oldId = (String) iterator.next();
				converterGraphs.remove(oldId);
				converterMaps.remove(oldId);
			}
			projectGraph = new DirectedGraph();
			Iterator iterator = converterGraphs.values().iterator();
			DirectedGraph graph;
			while (iterator.hasNext()) {
				graph = (DirectedGraph) iterator.next();
				projectGraph.addGraph(graph);
			}
			writeProjectGraphInfoFile();
		}

	}

	private boolean shouldLoadResourceGraphInfo(ProjectResourceInfoRuntime prir) {
		HashMap subscribedInterfaceVersions = prir.getSubscribedInterfaceVersions();
		HashMap cachedSubscribedInterfaceVersions = (HashMap)cachedResourceVersions.get(prir.getResourceUniqueId());
		resourceVersions.put(prir.getResourceUniqueId(), subscribedInterfaceVersions);
		if (cachedSubscribedInterfaceVersions==null ||
			(subscribedInterfaceVersions.size() != cachedSubscribedInterfaceVersions.size())) {
			return true;
		}
		// check that the two tables contain the same interfaces and same versions for each interface
		Iterator ifaceInfos = subscribedInterfaceVersions.entrySet().iterator();
		Map.Entry ifaceInfo;
		String ifaceId, ifaceVersion, cachedVersion;
		while (ifaceInfos.hasNext()) {
			ifaceInfo = (Map.Entry) ifaceInfos.next();
			ifaceId = (String)ifaceInfo.getKey();
			ifaceVersion = (String)ifaceInfo.getValue();
			cachedVersion = (String)cachedSubscribedInterfaceVersions.get(ifaceId);
			if (cachedVersion == null || !ifaceVersion.equals(cachedVersion)) {
				// ifaceId not found in cached info or version mismatch
				return true;
			}
		}
		return false; // hashmaps match, so do not get info again for this resource
	}

	private boolean shouldLoadIModelGraphInfo(DomeModelRuntime model) {
		String modelId = model.getId().getIdString();
		String modelVer = model.getVersion().toString();
		String cachedVer = (String)cachedResourceVersions.get(modelId);
		resourceVersions.put(modelId, modelVer);
		if (cachedVer == null || !cachedVer.equals(modelVer)) {
			return true;
		}
		return false;
	}

	/**
	 * How to convert resource graph nodes to project graph nodes
	 */
	public class ResourceGraphNodeConverter extends DirectedGraph.NodeConverter {

		public HashMap nodeMap = new HashMap(); // new->old
		private String resourceIdPrefix;

		// variables for mapNode
		MultiItemNode min;
		String newNodeId;
		List items;

		public ResourceGraphNodeConverter(String resourceId)
		{
			this.resourceIdPrefix = resourceId + ".";
		}

		public Object mapNode(Object node)
		{
			if (node instanceof MultiItemNode) {
				min = (MultiItemNode) node;
				newNodeId = resourceIdPrefix + min.getId(); // resourceId.modelObjectId
				items = min.getItems(); // each item is ifaceId.paramId
				min = new MultiItemNode(newNodeId, items);
				origParamIdToProjectNodeMap.put(newNodeId, min);

				// also note which parameters are in the MultiItemNode
				for (int i = 0; i < items.size(); i++) {
					origParamIdToProjectNodeMap.put(resourceIdPrefix + items.get(i), min);
				}
				nodeMap.put(min,node);
				return min;
			}
			else if (node instanceof String) { // ifaceId.paramId
				newNodeId = resourceIdPrefix + node; // resourceId.ifaceId.paramId
				origParamIdToProjectNodeMap.put(newNodeId, newNodeId);
				nodeMap.put(newNodeId,node);
				return newNodeId;
			}
			else {
				System.err.println("IntegrationProjectSolver.ResourceGraphNodeConverter: Unknown node type " +
				                   ClassUtils.getClassName(node));
				return null;
			}
		}
	}

	/**
	 * How to convert imodel graph nodes to project graph nodes
	 */
	public class IModelGraphNodeConverter extends DirectedGraph.NodeConverter
	{
		public HashMap nodeMap = new HashMap(); // new->old

		Object newNode; // for mapNode
		public Object mapNode(Object node)
		{
			if (node instanceof Parameter) { // could be imodel or subscription parameter
				String mObjId = getProjectGraphId((Parameter)node);
				if (Parameters.isSubscriptionParameter((Parameter)node)) {
					// find corresponding node in project graph
					newNode = origParamIdToProjectNodeMap.get(getProjectGraphId((Parameter) node));
					if (newNode != null)
						nodeMap.put(newNode, node);
					else
						System.err.println("IntegrationProjectSolver.IModelGraphNodeConverter: Could not find project graph node for " +
						                   ((Parameter)node).getName());
					return newNode;
				} else { // imodel parameter
					origParamIdToProjectNodeMap.put(mObjId, mObjId);
					nodeMap.put(mObjId, node);
					return mObjId;
				}
			} else if (node instanceof MultiItemNode) { // parameters must be subscription parameters
				MultiItemNode min = (MultiItemNode) node;
				// find corresponding MultiItemNode in project graph
				ModelObject mObj = (ModelObject)min.getItems().get(0);
				newNode =  origParamIdToProjectNodeMap.get(getProjectGraphId(mObj));
				if (newNode != null)
					nodeMap.put(newNode, node);
				else
					System.err.println("IntegrationProjectSolver.IModelGraphNodeConverter: Could not find project graph node for " +
					                   mObj.getName());
				return newNode;
			}
			else {
				System.err.println("IntegrationProjectSolver.IModelGraphNodeConverter: Unknown node type " +
				                   ClassUtils.getClassName(node));
				return null;
			}
		}
	}

	/**
	 * The projectGraph has a resource or model id before every item in the graph.
	 * @param mObj
	 * @return
	 */
	public static String getProjectGraphId(ModelObject mObj)
	{
		String mObjId = mObj.getId().getIdString();
		if (mObj.getScope() instanceof Subscription) {
			Subscription sub = ((Subscription) mObj.getScope());
			return sub.getResourceId() + "." + sub.getIfaceId() + "." + sub.getParamIdMap().get(mObjId);
		}
		else if (mObj.getScope() instanceof SubscriptionInterface) {
			Subscription sub = ((SubscriptionInterface) mObj.getScope()).getSubscription();
			return sub.getResourceId() + "." + sub.getIfaceId() + "." + mObjId;
		}
		else
			return mObj.getModel().getId().getIdString() + mObjId;
	}

    /**
     * go through each resource/imodel and calculate 'external graph'
     */
    public void createAndSetExternalGraph() {
	    String projectId = project.getId().getIdString();
	    List resources = project.getResourceModels();
        for (int i = 0; i < resources.size(); i++) {
            ProjectResourceInfoRuntime prir = (ProjectResourceInfoRuntime) resources.get(i);
	        if (prir.isSubscribed()) {
		        String resourceId = prir.getResourceUniqueId();
		        DirectedGraph externalInformationGraph = createExternalInformationGraph(resourceId);
		        //GLPanel.visualizeGraph("project: external graph for resource - " + prir.getResourceName(), externalInformationGraph);

		        if (!externalInformationGraph.isEmpty()) {
			        String graphXml = externalInformationGraph.toXmlElement(projectId).asXML();
			        // call method on remote resource
			        prir.setExternalGraph(graphXml);
		        }
	        }
        }

        List iModels = project.getIntegrationModels();
        for (int i = 0; i < iModels.size(); i++) {
            DomeModelRuntime imodel = (DomeModelRuntime) ((ProjectIntegrationModelInfoRuntime) iModels.get(i)).getModel();
	        DirectedGraph externalInformationGraph = createExternalInformationGraph(imodel.getId().getIdString());
	        //GLPanel.visualizeGraph("project: external graph for imodel - " + imodel.getName(), externalInformationGraph);

	        if (!externalInformationGraph.isEmpty()) {
                imodel.addExternalGraph(projectId, externalInformationGraph, false);
            }
        }
    }

	private DirectedGraph createExternalInformationGraph(String resourceOrModelId) {
		DirectedGraph convertedGraph = (DirectedGraph)converterGraphs.get(resourceOrModelId);
		HashMap converterMap = (HashMap)converterMaps.get(resourceOrModelId);

		// create graph of resource/model graph by itself with information about the project context it is in
		DirectedGraph standaloneGraph = projectGraph.getSubgraphForVariables(convertedGraph.getNodes());

		List disconnectedNodes = standaloneGraph.getDisconnectedNodes();
		Collection disconnectedNodesTurnedInput = DSet.intersection(disconnectedNodes, projectGraph.getInputs());
		Collection disconnectedNodesTurnedOutput = DSet.intersection(disconnectedNodes, projectGraph.getOutputs());

		// add project node for sets of inputs/disconnected nodes which are correlated in the project
		List inputs = standaloneGraph.getInputs();

		List inputsAndDisconnectedNodes = new ArrayList(inputs);
		inputsAndDisconnectedNodes.addAll(disconnectedNodesTurnedOutput);
		List inputSets = projectGraph.getIndependentSets(inputsAndDisconnectedNodes);
		List inputSet;
		for (int i = 0; i < inputSets.size(); i++) {
			inputSet = (List) inputSets.get(i);
			if (inputSet.size() > 1) {
				NameIdNode projectNode = new NameIdNode(UUIDGenerator.create(), project.getName(), NameIdNode.PROJECT_NODE);
			    standaloneGraph.addNode(projectNode);
				for (Iterator iterator = inputSet.iterator(); iterator.hasNext();) {
					standaloneGraph.addArc(projectNode,iterator.next());
				}
			}
		}

		// add project node for inputs/disconnected nodes which are not inputs in the projectGraph
		List projectInputs = projectGraph.getInputs();
		Collection inputsTurnedOutputs = DSet.removeSet(inputs, projectInputs);
		inputsTurnedOutputs.addAll(disconnectedNodesTurnedOutput);
		// if node is an output in standalone graph, it is shown being driven by something else in the model
		inputsTurnedOutputs = DSet.removeSet(inputsTurnedOutputs, standaloneGraph.getOutputs());
		if (!inputsTurnedOutputs.isEmpty()) {
			standaloneGraph.addNodes(inputsTurnedOutputs);
			for (Iterator iterator = inputsTurnedOutputs.iterator(); iterator.hasNext();) {
				// need a separate project node to drive each input; else correlation is inferred
				NameIdNode projectNode = new NameIdNode(UUIDGenerator.create(), project.getName(), NameIdNode.PROJECT_NODE);
				standaloneGraph.addNode(projectNode);
				standaloneGraph.addArc(projectNode,iterator.next());
			}
		}

		// add a project node for outputs which are not outputs in the projectGraph
		// and for disconnected nodes which are inputs in the projectGraph
		// one project node is used for all of these nodes; this makes the graph look cleaner.
		// it should not be inferred from the graph that these nodes drive anything common in the project
		List outputs = standaloneGraph.getOutputs();
		List projectResults = projectGraph.getResults();
		Collection outputsTurnedIntermediate = DSet.removeSet(outputs,projectResults);
		// if node is an intermediate in standalone graph, it is shown driving something else in the model
		outputsTurnedIntermediate = DSet.removeSet(outputsTurnedIntermediate, standaloneGraph.getIntermediates());
		outputsTurnedIntermediate.addAll(disconnectedNodesTurnedInput);
		if (!outputsTurnedIntermediate.isEmpty()) {
			standaloneGraph.addNodes(outputsTurnedIntermediate);
			NameIdNode projectNode = new NameIdNode(UUIDGenerator.create(), project.getName(), NameIdNode.PROJECT_NODE);
			standaloneGraph.addNode(projectNode);
			for (Iterator iterator = outputsTurnedIntermediate.iterator(); iterator.hasNext();) {
				standaloneGraph.addArc(iterator.next(),projectNode);
			}
		}

		//GLPanel.visualizeGraph("project: resource graph for - " + resourceOrModelId, standaloneGraph);

		DirectedGraph externalInformationGraph = DirectedGraph.removeGraph(standaloneGraph,convertedGraph);
		return new DirectedGraph(externalInformationGraph,converterMap); // convert external information back to original graph objects
	}

	public void addExternalGraph(String graphXml)
	{
		DirectedGraph externalIdGraph = new DirectedGraph(XMLUtils.stringToXmlElement(graphXml));
		List nodes = externalIdGraph.getNodes();
		HashMap idToParamMap = new HashMap();
		for (int i = 0; i < nodes.size(); i++) {
			Object o = nodes.get(i);
			if (o instanceof NameIdNode) {
				idToParamMap.put(o, o);
			}
			else {
				Parameter p = (Parameter) project.getModelObjectById(new Id((String) o));
				idToParamMap.put(o, p);
			}
		}
		projectGraph.addGraph(new DirectedGraph(externalIdGraph, idToParamMap));
		createAndSetExternalGraph();
	}


	public DirectedGraph getGraph() {
		return projectGraph;
	}

	public void cleanup() {
		projectGraph.cleanup();
		projectGraph = null;
		Iterator cGraphs = converterGraphs.values().iterator();
		DirectedGraph dg;
		while (cGraphs.hasNext()) {
			((DirectedGraph) cGraphs.next()).cleanup();
		}
		converterGraphs.clear();
		converterMaps.clear();
	}

	/**
	 * @return true if graph parsed; otherwise, return false
	 */
	private boolean parseProjectGraphInfoFile() {
		if (!projectGraphInfoFile.exists())
			return false;
		Element xml = XMLUtils.fileToXmlElement(projectGraphInfoFile);
		if (xml == null)
			return false;

		List resourceVersionsXml = xml.selectNodes("versionInfo/resource");
		Element e;
		String id, version;
		for (int i = 0; i < resourceVersionsXml.size(); i++) {
			e = (Element) resourceVersionsXml.get(i);
			id = e.attributeValue(XML_ID);
			HashMap ifaceVersions = new HashMap();
			XMLUtils.makeRootElement(e);
			List ifaceVersionsXml = e.selectNodes("iface");
			Element ifaceVerXml;
			for (int j = 0; j < ifaceVersionsXml.size(); j++) {
				ifaceVerXml = (Element) ifaceVersionsXml.get(j);
				ifaceVersions.put(ifaceVerXml.attributeValue(XML_ID),
				                  ifaceVerXml.attributeValue(XML_VERSION));
			}
			cachedResourceVersions.put(id, ifaceVersions);
		}
		List imodelVersionXml = xml.selectNodes("versionInfo/iModel");
		for (int i = 0; i < imodelVersionXml.size(); i++) {
			e = (Element) imodelVersionXml.get(i);
			id = e.attributeValue(XML_ID);
			version = e.attributeValue(XML_VERSION);
			cachedResourceVersions.put(id,version);
		}

		List converterGraphsXml = xml.selectNodes("converterGraphs/directedGraph");
		DirectedGraph graph;
		for (int i = 0; i < converterGraphsXml.size(); i++) {
			e = (Element) converterGraphsXml.get(i);
			id = DirectedGraph.getGraphIdFromXml(e);
			graph = new DirectedGraph(e);
			converterGraphs.put(id, graph);
		}

		List converterMapsXml = xml.selectNodes("converterMaps/converterMap");
		for (int i = 0; i < converterMapsXml.size(); i++) {
			e = (Element) converterMapsXml.get(i);
			id = e.attributeValue(XML_ID);
			HashMap converterMap = new HashMap();
			XMLUtils.makeRootElement(e);
			List entries = e.selectNodes("entry");
			Element entry, keyNode, valueNode;
			for (int j = 0; j < entries.size(); j++) {
				entry = (Element) entries.get(j);
				keyNode = (Element)entry.selectSingleNode("key");
				valueNode = (Element)entry.selectSingleNode("value");
				converterMap.put(DirectedGraph.parseXmlRef(keyNode),
				                 DirectedGraph.parseXmlRef(valueNode));
			}
			converterMaps.put(id, converterMap);
		}

		cachedProjectGraphXml = (Element) xml.selectSingleNode("projectGraph/directedGraph");
		return true;
	}

	private void writeProjectGraphInfoFile() {
		Element xml = DocumentHelper.createElement("projectGraphInfo");
		XMLUtils.DomeObjectComparator comparator = new XMLUtils.DomeObjectComparator();

		Element versionXml = xml.addElement("versionInfo");
		List resourceIds = new ArrayList(resourceVersions.keySet());
		Collections.sort(resourceIds, comparator);
		Iterator it = resourceIds.iterator();
		Object value;
		String keyString;
		while (it.hasNext()) {
			keyString = (String)it.next();
			value = resourceVersions.get(keyString);
			if (value instanceof String) {
				versionXml.addElement("iModel").addAttribute(XML_ID, keyString).addAttribute(XML_VERSION,(String)value);
			} else if (value instanceof HashMap) {
				Element resourceVersionXml = XMLUtils.addStringMap(versionXml,"resource","iface",XML_ID,XML_VERSION,(Map)value);
				resourceVersionXml.addAttribute(XML_ID, keyString);
			} else {
				System.err.println("writeProjectGraphInfoFile - resourceVersions has invalid info: "+keyString+"-->"+value);
			}
		}

		Element converterGraphsXml = xml.addElement("converterGraphs");
		resourceIds = new ArrayList(converterGraphs.keySet());
		Collections.sort(resourceIds, comparator);
		it = resourceIds.iterator();
		DirectedGraph graph;
		while (it.hasNext()) {
			keyString = (String)it.next();
			graph = (DirectedGraph)converterGraphs.get(keyString);
			converterGraphsXml.add(graph.toXmlElement(keyString));
		}

		Element converterMapsXml = xml.addElement("converterMaps");
		resourceIds = new ArrayList(converterMaps.keySet());
		Collections.sort(resourceIds, comparator);
		it = resourceIds.iterator();
		HashMap converterMap;
		Element mapXml, entryXml;
		while (it.hasNext()) {
			keyString = (String) it.next();
			converterMap = (HashMap)converterMaps.get(keyString);
			mapXml = converterMapsXml.addElement("converterMap").addAttribute("id",keyString);
			List keyNodes = new ArrayList(converterMap.keySet());
			Collections.sort(keyNodes, comparator);
			Object keyNode, valueNode;
			for (int i = 0; i < keyNodes.size(); i++) {
				keyNode = keyNodes.get(i);
				valueNode = converterMap.get(keyNode);
				entryXml = mapXml.addElement("entry");
				entryXml.add(DirectedGraph.toXmlRef("key",keyNode));
				entryXml.add(DirectedGraph.toXmlRef("value",valueNode));
			}
		}

		Element projectGraphXml = xml.addElement("projectGraph");
		projectGraphXml.add(projectGraph.toXmlElement(null));

		try {
			XMLUtils.writeToFile(xml,projectGraphInfoFile);
		}
		catch (IOException e) {
			System.err.println("Error writing project graph info for "+project.getName());
			System.out.println("\t"+e);
			if (projectGraphInfoFile.exists())
				projectGraphInfoFile.delete();
		}
	}

}
