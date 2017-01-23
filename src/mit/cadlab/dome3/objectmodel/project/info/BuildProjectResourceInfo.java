package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseInterfaceDomeFile;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBrowse;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Collection;
public class BuildProjectResourceInfo extends ProjectResourceInfo
{

	protected ServerConnection svrConn;
	protected HashMap subscribers = new HashMap(); // key is iModel instance; value is list of interfaces
	protected PropertyChangeListener iModelNameListener = new iModelNameListener();
	protected PropertyChangeListener iModelDeletionListener = new IModelDeletionListener();
	protected HashMap interfaces = new HashMap(); // key is interface id, value is browseinterface instance

	public BuildProjectResourceInfo(String type, String resourceId, String name, String description, ServerConnection svrConn)
	{
		super(type, resourceId, name, description, svrConn.getServerPort());
		this.svrConn = svrConn;
		svrConn.addReference();
	}

    //for internal imodel resource _i
    public BuildProjectResourceInfo(String type, String resourceId, String name, String description)
    {
      super(type, resourceId, name, description);
    }


	public BuildProjectResourceInfo(BuildProjectResourceInfo info) {
		super(info);
	}

	public BuildProjectResourceInfo(Element xmlDescription)
	{
		super(xmlDescription);
	}

	public ServerConnection getServerConnection()
	{
		if (svrConn == null) {
			svrConn = LoginPrompt.showDialog(BuildFocusTracker.getCurrentComponent(),
			                                 resourceHostName + ":" + resourcePort,
			                                 NetworkUtils.getHostName());
			svrConn.addReference();
		}
		return svrConn;
	}

	public void releaseServerConnection()
	{
		if (svrConn != null) {
			svrConn.removeReference();
			svrConn = null;
		}
	}

	public void loadResource()
	{
		if (resourceLoaded)
			return;
		ServerConnection conn = getServerConnection();
		if (conn == null)
			return;
		Vector resourceInfo = null;
		if (MODEL_RESOURCE.equals(type))
			resourceInfo = FileSystemFunctions.getModelInfo(svrConn, resourceDeployId);
		else
			resourceInfo = FileSystemFunctions.getProjectInfo(svrConn, resourceDeployId);

		resourceName = (String) resourceInfo.get(1);
		resourceDescription = (String) resourceInfo.get(2);
		resourceVersion = resourceInfo.get(3).toString();

		view.add(locationInfo);
		Vector info = loadResource(svrConn, DbConstants.FILESYSTEM_SUBSCRIBE);
		if (MODEL_RESOURCE.equals(type)) {
			String ifaceName, ifaceId, ifaceDesc, ifaceDate;
			int ifaceVer;
			for (int i = 0; i < info.size(); i++) {
				Vector ifaceInfo = (Vector) info.elementAt(i);
				ifaceName = (String) ifaceInfo.get(0);
				ifaceId = (String) ifaceInfo.get(1);
				ifaceDesc = (String) ifaceInfo.get(2);
				ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
				ifaceDate = ifaceInfo.get(4).toString();
				BrowseInterface iface = new BrowseInterface(svrConn, this, this.getResourceUniqueId(), ifaceId, ifaceName, ifaceDesc, ifaceVer, ifaceDate);
				view.add(iface);
				interfaces.put(ifaceId, iface);
			}
		}
		else { //project - load project interfaces , then imodels and their interfaces
			BrowseModelFolder projectInterfaceFolder = new BrowseModelFolder("", new Integer(0), "interfaces");
			BrowseModelFolder iModelFolder = new BrowseModelFolder("", new Integer(0), "iModels");
			BrowseModelFolder resourceFolder = new BrowseModelFolder("", new Integer(0), "resources");

			String ifaceName, ifaceId, ifaceDesc, ifaceDate;
			int ifaceVer;
			if(info.size() == 2) {
				Vector projectIfaceInfoVector = (Vector) info.elementAt(1);
				for (int i = 0; i < projectIfaceInfoVector.size(); i++) {
					Vector ifaceInfo = (Vector)projectIfaceInfoVector.get(i);
					ifaceName = (String) ifaceInfo.get(0);
					ifaceId = (String) ifaceInfo.get(1);
					ifaceDesc = (String) ifaceInfo.get(2);
					ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
					ifaceDate = ifaceInfo.get(4).toString();
					BrowseInterface iface = new BrowseInterface(svrConn, this, this.getResourceUniqueId(), ifaceId, ifaceName, ifaceDesc, ifaceVer, ifaceDate);
					projectInterfaceFolder.getContent().add(iface);
					interfaces.put(ifaceId, iface);
				}

				Vector temp =  (Vector) info.elementAt(0);
				if(temp.size() == 2) {
					Vector temp1 =  (Vector)temp.elementAt(1);
					for(int k = 0; k < temp1.size(); k++) {
						Vector modelVector = (Vector) temp1.elementAt(k);
						Vector iModelVector = (Vector) modelVector.elementAt(0);
						String name = (String) iModelVector.get(0);
						String dbId = (String) iModelVector.get(1);
						String description = (String) iModelVector.get(2);
						Integer version = (Integer) iModelVector.get(3);
						String lastModified = iModelVector.get(4).toString();
						BrowseDomeFile df = new BrowseDomeFile(DomeFile.MODEL_TYPE, dbId, name, description, svrConn.getServerPort(), lastModified, version.intValue());
						iModelFolder.getContent().add(df);
						Vector iModelIfaceInfoVector = (Vector)modelVector.elementAt(1);
						for (int j = 0; j < iModelIfaceInfoVector.size(); j++) {
							Vector ifaceInfo = (Vector) iModelIfaceInfoVector.get(j);
							ifaceName = (String) ifaceInfo.get(0);
							ifaceId = (String) ifaceInfo.get(1);
							ifaceDesc = (String) ifaceInfo.get(2);
							ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
							ifaceDate = ifaceInfo.get(4).toString();
							BrowseInterface iface = new BrowseInterface(svrConn, this, this.getResourceUniqueId(), ifaceId, ifaceName, ifaceDesc, ifaceVer, ifaceDate);
							df.getContent().add(iface);
							interfaces.put(ifaceId, iface);
						}
					}
				}
				Vector temp2 = (Vector) info.get(0);
				if(temp2.size() >= 1) {
					Vector v;
					String projectXml = (String)(temp2).get(0);
					IntegrationProjectBrowse ip = new IntegrationProjectBrowse(XMLUtils.stringToXmlElement(projectXml));
					List resource = ip.getResourceModels();
					for (int i = 0; i < resource.size(); i++) {
						ProjectResourceInfo p = (ProjectResourceInfo) resource.get(i);
						BrowseDomeFile dfile = new BrowseDomeFile(p, svrConn);
						resourceFolder.getContent().add(dfile);
						v = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, p.getResourceDeployId(),
						                                                   PermissionUtils.PERMISSION_TO_SUBSCRIBE_TO_INTERFACE);
						for (int l = 0; l < v.size(); l++) {
							Vector anInterface = (Vector) v.get(l);
							String name = (String) anInterface.get(0);
							String interfaceId = (String) anInterface.get(1);
							String description = (String) anInterface.get(2);
							Integer version = (Integer) anInterface.get(3);
							String lastModified = anInterface.get(4).toString();
							BrowseInterface iface = new BrowseInterface(svrConn, p, p.getResourceUniqueId(), interfaceId,
							                    name, description,
							                    version.intValue(), lastModified);
							resourceContents.add(p.getResourceUniqueId());  //add id of resource model so that it can be looked up later
							dfile.getContent().add(iface);
							interfaces.put(interfaceId, iface);
						}

					}
				}
			}
			DArrayList piList = projectInterfaceFolder.getContent();
			DArrayList imList = iModelFolder.getContent();
			DArrayList rsList = resourceFolder.getContent();
			if(piList.isEmpty() & imList.isEmpty() & rsList.isEmpty()) { // no content
				//don't add anything
			}
			else {
				// contents in all folders
				if (!piList.isEmpty() & !imList.isEmpty() & !rsList.isEmpty()) {
					view.add(projectInterfaceFolder);
					view.add(iModelFolder);
					view.add(resourceFolder);
				}
				else {
					//exactly one folder has contents
					if(piList.isEmpty() ^ imList.isEmpty() ^ rsList.isEmpty()) {
						if(!piList.isEmpty())
							view.addAll(piList);
						else if(!imList.isEmpty())
							view.addAll(imList);
						else if (!rsList.isEmpty())
							view.addAll(rsList);
					}
					//exactly two folders have contents
					else {
						if (!piList.isEmpty())
							view.add(projectInterfaceFolder);
						else if (!imList.isEmpty())
							view.add(iModelFolder);
						else if (!rsList.isEmpty())
							view.add(resourceFolder);
					}

				}
			}
		}
		resourceLoaded = true;
	}

	public void relocateResource(String type, String resourceId,
	                             String name, String desc, double version,
	                             ServerConnection svrConn, HashMap ifaceDeployIdMap)
	{
		if (type != MODEL_RESOURCE && type != PROJECT_RESOURCE)
			throw new IllegalArgumentException("ProjectResourceInfo constructor - illegal type: " + type);
		this.type = type;
		this.resourceDeployId = resourceId;
		setName(name);
		this.resourceName = name;
		this.resourceDescription = desc;
		this.resourceVersion = Double.toString(version);
		String serverPort = svrConn.getServerPort();
		String[] serverPortInfo = NetworkUtils.parseServerPortInfo(serverPort);
		if (serverPortInfo[0] == null) {
			resourceHostName = serverPortInfo[1]; // ipaddress
		} else {
			resourceHostName = serverPortInfo[0];
		}
		resourceIpAddress = serverPortInfo[1];
		resourcePort = serverPortInfo[2];
		this.svrConn = svrConn;
		svrConn.addReference();

		reloadResource(ifaceDeployIdMap);
		for (Iterator iterator = subscribers.keySet().iterator(); iterator.hasNext();) {
			DomeModelBuilder m = (DomeModelBuilder) iterator.next();
			DSet ifaceIds = (DSet)subscribers.get(m);
			for (Iterator it = ifaceIds.iterator(); it.hasNext();) {
				String id = (String) it.next();
				String newid = (String)ifaceDeployIdMap.get(id);
				ifaceIds.remove(id);
				ifaceIds.add(newid);
			}
			//modify old subscriptions from imodel
			List subscriptions = m.getSubScriptions(resourceUniqueId);
			for (Iterator i = subscriptions.iterator(); i.hasNext();) {
				Subscription o = (Subscription)i.next();
				String oldIfaceId = o.getIfaceId();
				String newIfaceDepId = (String)ifaceDeployIdMap.get(oldIfaceId);
				m.relocateSubscription(o, svrConn, newIfaceDepId,
				                       0, oldIfaceId); //version set to "0" so that automatic update works later
			}
		}
	}

	private void reloadResource(HashMap ifaceDeployIdMap)
	{
		HashMap reverseMap = new HashMap(ifaceDeployIdMap.size());
		//create reverse hashmap
		for (Iterator iterator = ifaceDeployIdMap.keySet().iterator(); iterator.hasNext();) {
			Object key = iterator.next();
			Object value = ifaceDeployIdMap.get(key);
			reverseMap.put(value, key); //new iface deploy id  - old id
		}
		if (!resourceLoaded) {
			loadResource(); //this will create views with new ifaces etc if not created alredy
			return; //as we don't need to do anything else
		}
		Vector info = loadResource(svrConn, DbConstants.FILESYSTEM_SUBSCRIBE);
		if (MODEL_RESOURCE.equals(type)) {
			String ifaceName, ifaceId, ifaceDesc, ifaceDate;
			int ifaceVer;
			for (int i = 0; i < info.size(); i++) {
				Vector ifaceInfo = (Vector) info.elementAt(i);
				ifaceName = (String) ifaceInfo.get(0);
				ifaceId = (String) ifaceInfo.get(1);
				ifaceDesc = (String) ifaceInfo.get(2);
				ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
				ifaceDate = ifaceInfo.get(4).toString();
				String oldid = (String)reverseMap.get(ifaceId);
				BrowseInterface iface = (BrowseInterface)interfaces.get(oldid);
				iface.relocate(svrConn, ifaceId, ifaceName, ifaceDesc, ifaceVer, ifaceDate);
				interfaces.remove(oldid);
				interfaces.put(ifaceId, iface);
			}
		} else { //project - reload project interfaces , then imodels and their interfaces
			BrowseModelFolder iModelFolder = null;
			BrowseModelFolder resourceFolder = null;

			for (Iterator iterator = view.iterator(); iterator.hasNext();) {
				Object o = iterator.next();
				if(o instanceof BrowseModelFolder) {
					if (((BrowseModelFolder) o).getName().equals("iModels")) {
						iModelFolder = (BrowseModelFolder) o;
					}
					else if (((BrowseModelFolder) o).getName().equals("resources")) {
						resourceFolder = (BrowseModelFolder) o;
					}
				}
			}
			String ifaceName, ifaceId, ifaceDesc, ifaceDate;
			int ifaceVer;
			if (info.size() == 2) {
				Vector projectIfaceInfoVector = (Vector) info.elementAt(1);
				for (int i = 0; i < projectIfaceInfoVector.size(); i++) {
					Vector ifaceInfo = (Vector) projectIfaceInfoVector.get(i);
					ifaceName = (String) ifaceInfo.get(0);
					ifaceId = (String) ifaceInfo.get(1);
					ifaceDesc = (String) ifaceInfo.get(2);
					ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
					ifaceDate = ifaceInfo.get(4).toString();
					String oldid = (String) reverseMap.get(ifaceId);
					BrowseInterface iface = (BrowseInterface) interfaces.get(oldid);
					iface.relocate(svrConn, ifaceId, ifaceName, ifaceDesc, ifaceVer, ifaceDate);
					interfaces.remove(oldid);
					interfaces.put(ifaceId, iface);
				}

				//TODO for multiple imodels - need a hashmap of oldDeployId -> newDeployId
				//TODO following approach will work only when the project has 1 imodel
				Vector temp = (Vector) info.elementAt(0);
				if (temp.size() == 2) {
					Vector temp1 = (Vector) temp.elementAt(1);
					for (int k = 0; k < temp1.size(); k++) {
						Vector modelVector = (Vector) temp1.elementAt(k);
						Vector iModelVector = (Vector) modelVector.elementAt(0);
						String name = (String) iModelVector.get(0);
						String dbId = (String) iModelVector.get(1);
						String description = (String) iModelVector.get(2);
						Integer version = (Integer) iModelVector.get(3);
						String lastModified = iModelVector.get(4).toString();
						BrowseDomeFile df = (BrowseDomeFile)iModelFolder.getContent().get(k);
						df.relocate(DomeFile.MODEL_TYPE, dbId, name, description,
						                                       svrConn.getServerPort(), lastModified, version.intValue());
						Vector iModelIfaceInfoVector = (Vector) modelVector.elementAt(1);
						for (int j = 0; j < iModelIfaceInfoVector.size(); j++) {
							Vector ifaceInfo = (Vector) iModelIfaceInfoVector.get(j);
							ifaceName = (String) ifaceInfo.get(0);
							ifaceId = (String) ifaceInfo.get(1);
							ifaceDesc = (String) ifaceInfo.get(2);
							ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
							ifaceDate = ifaceInfo.get(4).toString();
							String oldid = (String) reverseMap.get(ifaceId);
							BrowseInterface iface = (BrowseInterface) interfaces.get(oldid);
							iface.relocate(svrConn, ifaceId, ifaceName, ifaceDesc, ifaceVer, ifaceDate);
							interfaces.remove(oldid);
							interfaces.put(ifaceId, iface);
						}
					}
				}
				Vector temp2 = (Vector) info.get(0);
				if (temp2.size() >= 1) {
					resourceContents.clear();
					Vector v;
					String projectXml = (String) (temp2).get(0);
					IntegrationProjectBrowse ip = new IntegrationProjectBrowse(XMLUtils.stringToXmlElement(projectXml));
					List resource = ip.getResourceModels();
					for (int i = 0; i < resource.size(); i++) {
						ProjectResourceInfo p = (ProjectResourceInfo) resource.get(i);
						BrowseDomeFile dfile = new BrowseDomeFile(p, svrConn);
						resourceFolder.getContent().add(dfile);
						v = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, p.getResourceDeployId(),
						                                                   PermissionUtils.PERMISSION_TO_SUBSCRIBE_TO_INTERFACE);
						for (int l = 0; l < v.size(); l++) {
							Vector anInterface = (Vector) v.get(l);
							String name = (String) anInterface.get(0);
							String interfaceId = (String) anInterface.get(1);
							String description = (String) anInterface.get(2);
							Integer version = (Integer) anInterface.get(3);
							String lastModified = anInterface.get(4).toString();
							String oldid = (String) reverseMap.get(interfaceId);
							BrowseInterface iface = (BrowseInterface) interfaces.get(oldid);
							iface.relocate(svrConn, p, p.getResourceUniqueId(), interfaceId, name,
							               description, version.intValue(), lastModified);
							interfaces.remove(oldid);
							interfaces.put(interfaceId, iface);
							resourceContents.add(p.getResourceUniqueId());  //add id of resource model so that it can be looked up later
						}
					}
				}
			}
		}
	}


	public BrowseInterface getInterface(String ifaceId)
	{
		return (BrowseInterface) interfaces.get(ifaceId);
	}

	public List getSubscribedInterfaceIds()
	{
		DSet ifaceIds = new DSet();
		Iterator values = subscribers.values().iterator();
		while (values.hasNext()) {
			ifaceIds.addAll((DSet) values.next());
		}
		return ifaceIds;
	}

	public List getSubscribedInterfaceIds(DomeModel model)
	{
		DSet ifaceIds = new DSet();
		Collection values = (Collection)subscribers.get(model);
		Iterator iter = values.iterator();
		while (iter.hasNext()) {
			ifaceIds.add(iter.next());
		}
		return ifaceIds;
	}

	public List getSubscribers()
	{
		return new ArrayList(subscribers.keySet());
	}

	public String getSubscriberNames()
	{
		if (subscribers.isEmpty())
			return "";
		List subs = getSubscribers();
		StringBuffer sb = new StringBuffer(Names.getName(subs.get(0)));
		for (int i = 0; i < subs.size(); i++) {
			sb.append(", ");
			sb.append(subs.get(i));
		}
		return sb.toString();
	}

	public void addSubscriber(DomeModelBuilder model, Collection ifaceIds)
	{
		DSet interfaceIds = (DSet) subscribers.get(model);
		if (interfaceIds == null) {
			interfaceIds = new DSet();
			subscribers.put(model, interfaceIds);
			model.addPropertyChangeListener(DomeObject.NAME, iModelNameListener);
			model.addPropertyChangeListener(iModelDeletionListener);
		}
		if (interfaceIds.addAll(ifaceIds)) {
			if(!isSubscribed) {
				isSubscribed = true;
			}
			model.subscribeResource(this);
			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	public void addSubscriber(DomeModelBuilder model, String interfaceId)
	{
		DSet interfaceIds = (DSet) subscribers.get(model);
		if (interfaceIds == null) {
			interfaceIds = new DSet();
			subscribers.put(model, interfaceIds);
			model.addPropertyChangeListener(DomeObject.NAME, iModelNameListener);
			model.addPropertyChangeListener(iModelDeletionListener);
		}
		if (interfaceIds.add(interfaceId)) {
			if (!isSubscribed) {
				isSubscribed = true;
			}
			model.subscribeResource(this);
			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	public void removeSubscriber(DomeModelBuilder model, String interfaceId)
	{
		DSet interfaceIds = (DSet) subscribers.get(model);
		if (interfaceIds == null)
			return;
		if (interfaceIds.remove(interfaceId)) {
			if (interfaceIds.isEmpty()) {
				model.removePropertyChangeListener(DomeObject.NAME, iModelNameListener);
				model.removePropertyChangeListener(iModelDeletionListener);
				subscribers.remove(model);
			}
			if(subscribers.size() == 0) {
				isSubscribed = false;
			}
			model.unsubscribeResource(this);

			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	class iModelNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			firePropertyChange(SUBSCRIBERS_CHANGED);
		}
	}

	class IModelDeletionListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if(evt.getPropertyName().equals(DomeModel.IMODEL_DELETED)) {
				subscribers.remove(evt.getOldValue());
				if (subscribers.size() == 0) {
					isSubscribed = false;
				}
			}
		}
	}

	public void removeAllSubscriptionsandMappings() {
		for (Iterator i = subscribers.keySet().iterator(); i.hasNext();) {
			DomeModelBuilder model = (DomeModelBuilder)i.next();
			List interfaces = (List)subscribers.get(model);
			for(Iterator j = interfaces.iterator(); j.hasNext(); ) {
				String ifaceId = (String)j.next();
				Collection ids = model.getSubscriptionModelObjectIds(ifaceId);
				for (Iterator iter = ids.iterator(); iter.hasNext();) {
					Id subScriptionObjectId = (Id) iter.next();
					ModelObject subScriptionObject = model.getModelObjectById(subScriptionObjectId);
					if (subScriptionObject != null) {
						Collection subscriptionParams = ((DefaultSubscription)subScriptionObject).getModelObjects();
						for (Iterator iterator = subscriptionParams.iterator(); iterator.hasNext();) {
							Object obj = iterator.next();
							if (obj instanceof Parameter) {
								model.getMappingManager().removeAllMappings((Parameter)obj);
							}
						}
						model.deleteModelObject(subScriptionObject);
					}
				}
			}
		}
		isSubscribed = false;
		firePropertyChange(RESOURCE_REMOVED, this, null);
	}

    public HashMap getInterfaces(){
        return interfaces;
    }

}
