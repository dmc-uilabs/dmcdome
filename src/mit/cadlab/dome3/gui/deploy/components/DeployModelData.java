package mit.cadlab.dome3.gui.deploy.components;


import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 11, 2003
 * Time: 3:37:27 PM
 * To change this template use Options | File Templates.
 */
public class DeployModelData
{
	public static final String NUM_AVAILABLE = "numAvailable";

	private DomePropertyChangeSupport _listeners;

	protected String _pathName, _xmlContent, _idXml, _nameXml;
	private String _modelDescription = "";;
	protected List _modelInterfaces;
	protected int _numAvailable;
	protected String deployId = "";
	protected Vector permissions = new Vector();

	public DeployModelData(String pathName, String xmlContent, String id, String name)
	{
		this._pathName = pathName;
		this._xmlContent = xmlContent;
		this._idXml = id;
		this._nameXml = name;
		_modelInterfaces = DeployUtilities.loadInterfacesForDeploy(pathName);
		this.registerListeners();
		this._listeners = new DomePropertyChangeSupport(this);
		this.setNumAvailable();
		this._modelDescription = "";
	}

	protected void setNumAvailable()
	{
		Integer oldAvailable = new Integer(_numAvailable);
		_numAvailable = 0;
		ListIterator iterator = _modelInterfaces.listIterator();
		while (iterator.hasNext()) {
			if (((DeployInterfaceData) iterator.next()).getIsAvailable().booleanValue()) _numAvailable++;
		}
		_listeners.firePropertyChange(DeployModelData.NUM_AVAILABLE, oldAvailable, new Integer(_numAvailable));

	}

	public int getNumAvailable()
	{
		return this._numAvailable;
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

	public List getModelInterfaces()
	{
		return this._modelInterfaces;
	}

	public String getDeployId()
	{
		return deployId;
	}

	public void setDeployId(String deployId)
	{
		this.deployId = deployId;
	}

	public Vector getPermissions()
	{
		return permissions;
	}

	public void setPermissions(Vector permissions)
	{
		if (permissions == null)
			permissions = DbConstants.EMPTY_VECTOR;
		else
			this.permissions = permissions;
	}

	protected void registerListeners()
	{
		InterfaceAvailableListener l = new InterfaceAvailableListener();
		ListIterator iterator = this._modelInterfaces.listIterator();
		while (iterator.hasNext()) {
			((DeployInterfaceData) (iterator.next())).addPropertyChangeListener(DeployInterfaceData.IS_AVAILABLE, l);
		}
	}

	class InterfaceAvailableListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			DeployModelData.this.setNumAvailable();
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_listeners.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		_listeners.removePropertyChangeListener(propertyName, listener);
	}

	public String getModelDescription()
	{
		if (_modelDescription == null)
			return "";
		else
			return _modelDescription;
	}

	public void setModelDescription(String value)
	{
		if (value == null)
			this._modelDescription = "";
		else
			this._modelDescription = value;
	}
}
