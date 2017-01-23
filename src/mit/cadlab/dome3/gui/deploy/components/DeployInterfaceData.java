package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 11, 2003
 * Time: 11:02:04 AM
 * To change this template use Options | File Templates.
 */
public class DeployInterfaceData
{
	public static final String IS_AVAILABLE = "isAvailable";

	private DomePropertyChangeSupport _listeners;

	private String _pathName, _xmlContent, _xmlMappingsContent, _idXml, _nameXml, _description = "", deployId = "";
	private Boolean _isAvailable;
	private Vector _permissions;
	private ModelInterfaceRuntimeClient _interfaceRuntime = null;


	public DeployInterfaceData(String path, String xmlContent, String xmlMappingsContent, String id, String name)
	{
		this._pathName = path;
		this._xmlContent = xmlContent;
		this._xmlMappingsContent = xmlMappingsContent;
		this._idXml = id;
		this._nameXml = name;
		this._isAvailable = new Boolean(false);
		this._permissions = new Vector();
		this._description = null;
		this._interfaceRuntime = null;
		this.loadXml();
		_listeners = new DomePropertyChangeSupport(this);
	}

	public void setPathName(String str)
	{
		_pathName = str;
	}

	public String getPathName()
	{
		return _pathName;
	}

	public void setXmlContent(String str)
	{
		this._xmlContent = str;
	}

	public String getXmlContent()
	{
		return this._xmlContent;
	}

	public void setId(String str)
	{
		this._idXml = str;
	}

	public String getId()
	{
		return this._idXml;
	}

	public void setName(String str)
	{
		this._nameXml = str;
	}

	public String getName()
	{
		return this._nameXml;
	}

	public Boolean getIsAvailable()
	{
		return _isAvailable;
	}

	public void setIsAvailable(Boolean _isDeployed)
	{
		Boolean oldAvailable = this._isAvailable;
		this._isAvailable = _isDeployed;
		_listeners.firePropertyChange(IS_AVAILABLE, oldAvailable, this._isAvailable);
	}

	public Vector getPermissions()
	{
		return _permissions;
	}

	public void setPermissions(Vector _permissions)
	{
		this._permissions = _permissions;
	}

	public String getXmlMappingsContent()
	{
		return this._xmlMappingsContent;
	}

	public void setXmlMappingsContent(String xmlMappingsContent)
	{
		this._xmlMappingsContent = xmlMappingsContent;
	}

	public String getDescription()
	{
		return this._description;
	}

	public void setDescription(String value)
	{
		if (value == null)
			this._description = "";
		else
			this._description = value;
	}

	public String getDeployId()
	{
		return deployId;
	}

	public void setDeployId(String deployId)
	{
		this.deployId = deployId;
	}

	/**
	 * To transform an interfaceXml into an interface object
	 */
	public void loadXml()
	{
		if (_interfaceRuntime == null) {
			_interfaceRuntime = new ModelInterfaceRuntimeClient(XMLUtils.stringToXmlElement(_xmlContent), true);
		}
	}

	/**
	 * Return a list of filetrs which contain parameters
	 * @return
	 */
	public List getFilters()
	{
		if (_interfaceRuntime == null) return Collections.EMPTY_LIST;
		return _interfaceRuntime.getView(ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_listeners.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		_listeners.removePropertyChangeListener(propertyName, listener);
	}
}
