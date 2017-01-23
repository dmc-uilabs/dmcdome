// BrowseInterface.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.util.List;

public class BrowseInterface extends DomeJavaBean implements ViewSupport
{
	public static final String VIEW = "view";
	public static final String BUILD_VIEW = DomeModelInterface.BUILD_VIEW;
	public static final String CAUSAL_VIEW = DomeModelInterface.INTERFACE_CAUSALITY_VIEW;
	public static final String SYSTEM_VIEW = DomeModelInterface.SYSTEM_CAUSALITY_VIEW;

	protected ServerConnection svrConn;
	protected Object parentObj;
	protected String parentId;
	protected String ifaceDeployId, name, description, date;
	protected int version;
	protected ModelInterfaceRuntimeClient iface = null;
	protected String viewName;
	protected DArrayList view = new DArrayList();

	public BrowseInterface(ServerConnection svrConn, Object parent, String parentId,
	                       String ifaceDeployId, String name, String description, int version, String date)
	{
		this.svrConn = svrConn;
		this.parentObj = parent;
		this.parentId = parentId;
		this.ifaceDeployId = ifaceDeployId;
		this.name = name;
		this.description = description;
		this.version = version;
		this.date = date;
	}

	protected BrowseInterface(ServerConnection svrConn, Object parent, String parentId,
	                          String ifaceDeployId, String name, String description)
	{
		this.svrConn = svrConn;
		this.parentObj = parent;
		this.parentId = parentId;
		this.ifaceDeployId = ifaceDeployId;
		this.name = name;
		this.description = description;
	}

	public Object getParent()
	{
		return parentObj;
	}

	public String getParentId()
	{
		return parentId;
	}

	public String getInterfaceId()
	{
		return ifaceDeployId;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getDate()
	{
		return date;
	}

	public int getVersion()
	{
		return version;
	}

	public ServerConnection getServerConnection()
	{
		return svrConn;
	}

	public String getViewName()
	{
		return viewName;
	}

	public void setView(String viewName)
	{
		if (iface == null)
			return;
		List newViewItems = iface.getView(viewName);
		if (newViewItems.isEmpty()) // invalid view
			return;
		view.clear();
		view.addAll(newViewItems);
		String oldViewName = this.viewName;
		this.viewName = viewName;
		firePropertyChange(VIEW, oldViewName, this.viewName);
	}

	public void loadInterface()
	{
        loadInterface(false);
	}

    // if called from getInterface, notLoadingSystemCausality will be true
    public void loadInterface(boolean notLoadingSystemCausality) {
        if (iface == null && svrConn != null) {
            String ifaceXml = FileSystemFunctions.getInterfaceDescription(svrConn, ifaceDeployId);
            iface = new ModelInterfaceRuntimeClient(XMLUtils.stringToXmlElement(ifaceXml), notLoadingSystemCausality);
            setView(CAUSAL_VIEW);
        }
    }

	public ModelInterfaceRuntimeClient getInterface()
	{
		if (iface == null)
			loadInterface(true); // doesn't need to load system causality
		return iface;
	}

	/**
	 * Gets the default view for this collection of items.
	 */
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

	public Parameter getParameter(String paramId)
	{
		if (iface == null)
			return null;
		return (Parameter) iface.getModelObjectById(new Id(paramId));
	}

	public void relocate(ServerConnection svrConn, String ifaceDeployId, String name, String description,
	                     int version, String date)
	{
		this.svrConn = svrConn;
		this.ifaceDeployId = ifaceDeployId;
		this.name = name;
		this.description = description;
		this.version = version;
		this.date = date;
	}

	public void relocate(ServerConnection svrConn, Object parent, String parentId, String ifaceDeployId,
	                     String name, String description, int version, String date)
	{
		this.svrConn = svrConn;
		this.parentObj = parent;
		this.parentId = parentId;
		this.ifaceDeployId = ifaceDeployId;
		this.name = name;
		this.description = description;
		this.version = version;
		this.date = date;
	}
}
