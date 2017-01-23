// ProjectResourceInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * This class holds information about project resources.
 */
public class ProjectResourceInfo extends DomeJavaBean implements XMLSupport, ViewSupport, InfoInterface
{
	public static final String MODEL_RESOURCE = "model";
	public static final String PROJECT_RESOURCE = "project";
    public static final String IMODEL_RESOURCE = "imodel";
	public static final String XML_TAG = "resource";
	public static final String BROWSE = "browse";
	public static final String SUBSCRIBE = "subscribe";

	public static final String SUBSCRIBERS_CHANGED = "subscribers changed"; // add, remove, or namechange

	// property names
	public static final String NAME = "name";

	// project properties for this resource
	protected String type = null; // model/project
	protected String resourceUniqueId = null;
	protected String name = "";

	// information about the resource from the resource
	protected String resourceDeployId = "";
	protected String resourceHostName = "";
	protected String resourceIpAddress = "";
	protected String resourcePort = "";

	protected boolean resourceLoaded = false;
	protected String resourceName = "";
	protected String resourceDescription = "";
	protected String resourceVersion = "";
	//for a resource of type PROJECT_RESOURCE this will contain ids of resource models (in build mode)
	protected List resourceContents = new ArrayList();
	protected LocationInfo locationInfo = new LocationInfo();
	protected DArrayList view = new DArrayList();
	protected List subscribedInterfaceIds = new ArrayList();
    protected boolean isSubscribed = false;

	public ProjectResourceInfo(String type, String resourceId, String name, String description, String serverPort)
	{
		if (type != MODEL_RESOURCE && type != PROJECT_RESOURCE)
			throw new IllegalArgumentException("ProjectResourceInfo constructor - illegal type: " + type);
		this.type = type;
		resourceUniqueId = UUIDGenerator.create();
		setName(name);
		this.resourceDeployId = resourceId;
		this.resourceName = name;
		this.resourceDescription = description;
		String[] serverPortInfo = NetworkUtils.parseServerPortInfo(serverPort);
		if (serverPortInfo[0] == null) {
			resourceHostName = serverPortInfo[1]; // ipaddress
		} else {
			resourceHostName = serverPortInfo[0];
		}
		resourceIpAddress = serverPortInfo[1];
		resourcePort = serverPortInfo[2];
	}

    //for internal imodel resource (no server connection) _i
    public ProjectResourceInfo(String type, String resourceId, String name, String description)
	{
		if (type != IMODEL_RESOURCE)
			throw new IllegalArgumentException("ProjectResourceInfo constructor - illegal type: " + type);
		this.type = type;
		resourceUniqueId = resourceId;    //since it's an internal i-model, the two id's are kept the same! _i
		setName(name);
		this.resourceDeployId = resourceId;
		this.resourceName = name;
		this.resourceDescription = description;
	}

	public ProjectResourceInfo(ProjectResourceInfo info)
	{
		this.type = info.getType();
		resourceUniqueId = UUIDGenerator.create();
		this.resourceName = info.getName();
		setName(resourceName);
		this.resourceDeployId = info.getResourceDeployId();
		this.resourceDescription = info.getResourceDescription();
		this.resourceHostName = info.getResourceHostName(); // ipaddress
		this.resourceIpAddress = info.getResourceIpAddress();
		this.resourcePort = info.getResourcePort();
	}

	public ProjectResourceInfo(Element xmlDescription)
	{
		type = xmlDescription.attributeValue("type");
		if (type == null)
			throw new IllegalArgumentException("no xml type: " + xmlDescription.asXML());
		resourceUniqueId = xmlDescription.attributeValue("id");
		if (resourceUniqueId == null)
			throw new IllegalArgumentException("no xml id: " + xmlDescription.asXML());
		name = xmlDescription.attributeValue("name");
		if (name == null)
			name = "";
		Element loc = (Element) xmlDescription.selectSingleNode("location");
		if (loc == null)
			throw new IllegalArgumentException("no xml location: " + xmlDescription.asXML());

        //if resource is nested i-model, then xml description holds build Id , not deploy ID - look it up on database _i
        if(!type.equals("imodel")) {
            resourceDeployId = loc.attributeValue("id");
        }
        else {
//            DbUtils.setDbUrl(9001);
//            String buildId = loc.attributeValue("id");
//            if (buildId != null) {
//			String getStaticIdQuery = "select MODEL_ID from INTEGRATION_MODEL_VERSIONS where BUILD_ID='"
//			        + buildId + "'";
//			try {
//				Statement stmt = DbUtils.getStatement();
//				ResultSet r;
//
//				// get model id
//				r = stmt.executeQuery(getStaticIdQuery);
//				if (!r.next())
//					throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_MODEL,
//					                          DbErrors.XMLRPC_NO_SUCH_MODEL_MSG);   //todo create message for 'not such i-model'
//				else {
//					String deployId = r.getString("MODEL_ID");
//                    resourceDeployId = deployId;
//				}
//			}
//			catch (Exception e) {
//				System.out.println("Database query for resource i-model failed - using buildId as resourceDeployId");
//                resourceDeployId = buildId;
//                System.err.println("getting resource model static id failed : " + e.getMessage());
//			}
//		}
        }

		resourceHostName = loc.attributeValue("hostName");
		resourceIpAddress = loc.attributeValue("ipAddress");
		resourcePort = loc.attributeValue("port");
		if (resourceDeployId == null || resourceHostName == null || resourceIpAddress == null || resourcePort == null)
			throw new IllegalArgumentException("incomplete xml location: " + xmlDescription.asXML());
		Element ifaceIds = (Element) xmlDescription.selectSingleNode("subscribedInterfaceIds");
		if (ifaceIds != null)
			subscribedInterfaceIds.addAll(XMLUtils.parseStringCollection(ifaceIds, "subscribedInterfaceIds", "ifaceId"));

        //if resource is nested i-model then change subscribedInterfaceIds into their deployId values (since the xml description only contains build Id) _i

        String ifaceBuildId = null;
        String ifaceDeployId = null;
        if (type.equals("imodel")) {
//            for (int i = 0; i < subscribedInterfaceIds.size(); i++) {
//                ifaceBuildId = (String) subscribedInterfaceIds.get(i);
//                if (ifaceBuildId != null) {
//			String getStaticIdQuery = "select INTERFACE_ID from INTERFACE_VERSIONS where BUILD_ID='"
//			        + ifaceBuildId + "'";
//			try {
//				Statement stmt = DbUtils.getStatement();
//				ResultSet r;
//
//				// get model id
//				r = stmt.executeQuery(getStaticIdQuery);
//				if (!r.next())
//					throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_INTERFACE,
//					                          DbErrors.XMLRPC_NO_SUCH_INTERFACE_MSG);   //todo create message for 'not such i-model'
//				else {
//					ifaceDeployId = r.getString("INTERFACE_ID");
//				}
//			}
//			catch (Exception e) {
//				System.out.println("Database query for i-model subscription interface failed - using buildId as resourceDeployId");
//                ifaceDeployId = ifaceBuildId;
//                System.err.println("getting interface static id failed : " + e.getMessage());
//			}
//		}
//             if(ifaceDeployId != null) {
//             subscribedInterfaceIds.set(i,ifaceDeployId);
//             }
//        }
        }

		Element resourceCons = (Element) xmlDescription.selectSingleNode("resourceContents");
		if (resourceCons != null)
			resourceContents.addAll(XMLUtils.parseStringCollection(resourceCons, "resourceContents", "resourceContent"));
	}

	public String getType()
	{
		return type;
	}

	public String getResourceUniqueId()
	{
		return resourceUniqueId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String newName)
	{
		if (newName == null || newName.trim().equals(""))
			return;
		String oldName = name;
		this.name = newName.trim();
		firePropertyChange(NAME, oldName, name);
	}

	public String getResourceDeployId()
	{
		return resourceDeployId;
	}

	public String getResourceHostName()
	{
		return resourceHostName;
	}

	public String getResourceIpAddress()
	{
		return resourceIpAddress;
	}

	public String getResourcePort()
	{
		return resourcePort;
	}

	public boolean isResourceLoaded()
	{
		return resourceLoaded;
	}

	protected Vector loadResource(ServerConnection svrConn, String mode)
	{
		String interfacePermissionType = null;

		if (DbConstants.FILESYSTEM_BROWSE.equals(mode))
			interfacePermissionType = PermissionUtils.PERMISSION_TO_VIEW_INTERFACE;
		else if (DbConstants.FILESYSTEM_SUBSCRIBE.equals(mode))
			interfacePermissionType = PermissionUtils.PERMISSION_TO_SUBSCRIBE_TO_INTERFACE;
		else
			throw new RuntimeException("ProjectResourceInfo.loadResource - illegal mode: " + mode);

		Vector resourceInterfaces = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, resourceDeployId, interfacePermissionType);
		if (MODEL_RESOURCE.equals(type)) {
			return resourceInterfaces;
		} else { // project resource
			Vector projectContents = FileSystemFunctions.getAvailableProjectContents(svrConn, resourceDeployId, mode);
			return Vectors.create(projectContents, resourceInterfaces);
		}
	}

	public String getResourceName()
	{
		return resourceName;
	}

	public String getResourceDescription()
	{
		return resourceDescription;
	}

	public List getResourceContents()
	{
		return resourceContents;
	}

	public List getSubscribedInterfaceIds()
	{
		return Collections.unmodifiableList(subscribedInterfaceIds);
	}

	public String getXmlTag()
	{
		return XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(getXmlTag());
		xml.addAttribute("type", type);
		xml.addAttribute("id", resourceUniqueId);
		xml.addAttribute("name", name);
		Element loc = xml.addElement("location");
		loc.addAttribute("id", resourceDeployId);
		loc.addAttribute("hostName", resourceHostName);
		loc.addAttribute("ipAddress", resourceIpAddress);
		loc.addAttribute("port", resourcePort);
		List ifaceIds = getSubscribedInterfaceIds();
		if (!ifaceIds.isEmpty())
			XMLUtils.addStringCollection(xml, "subscribedInterfaceIds", "ifaceId", getSubscribedInterfaceIds());
		if (!resourceContents.isEmpty())
			XMLUtils.addStringCollection(xml, "resourceContents",
			                             "resourceContent", Collections.unmodifiableList(resourceContents));
		return xml;
	}

	public class LocationInfo
	{
		public String getName()
		{
			return resourceName;
		}

		public String getDescription()
		{
			return resourceDescription;
		}

		public String getServer()
		{
			return resourceHostName;
		}
	}

	public LocationInfo getLocationInfo()
	{
		return locationInfo;
	}

	public List getView()
	{
		return view;
	}

	public void addViewListener(DListListener l)
	{
		view.addDListListener(l);
	}

	public void removeViewListener(DListListener l)
	{
		view.removeDListListener(l);
	}

	public boolean isSubScribed() {
		return isSubscribed;
	}

}
