package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import java.util.Vector;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 17, 2003
 * Time: 9:07:03 PM
 * To change this template use Options | File Templates.
 */
public class DeployAnalysisToolInterfaceData
{
    public static final String IS_AVAILABLE = "is available";

    private DomePropertyChangeSupport _listeners;

    private String _pathName, _xmlContent, _xmlMappingsContent, _idXml, _nameXml, _description = "", _deployId = "", _typeXml;
    private Boolean _isAvailable;
    private Vector _permissions;

    private ToolInterface _interfaceRuntime = null; // since each tool interface will be different we will use the base interface

    public DeployAnalysisToolInterfaceData(String path, String xmlContent, String xmlMappingsContent, String id, String name, String xmlType)
	{
		_pathName = path;
		_xmlContent = xmlContent;
		_xmlMappingsContent = xmlMappingsContent;
		_idXml = id;
		_nameXml = name;
        _typeXml = xmlType;
		_isAvailable = new Boolean(false);
		_permissions = new Vector();
		_listeners = new DomePropertyChangeSupport(this);
        loadXml();
	}

    public String getName()
    {
        return _nameXml;
    }

    public String getId()
    {
        return _idXml;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String newDescription)
    {
        _description = newDescription;
    }

    /**
     * @return a boolean to indicate if the interface was
     * chosen to be available for deployment
     */

    public Boolean getIsAvailable()
    {
        return _isAvailable;
    }

    public void setIsAvailable(Boolean newValue)
    {
        Boolean oldAvailable = this._isAvailable;
        this._isAvailable = newValue;
        _listeners.firePropertyChange(IS_AVAILABLE, oldAvailable, this._isAvailable);
    }

    public String getXmlContent()
	{
		return _xmlContent;
	}

    public String getXmlMappingsContent()
	{
		return _xmlMappingsContent;
	}

    public Vector getPermissions()
	{
		return _permissions;
	}

    public void setPermissions(Vector permissions)
    {
        _permissions = permissions;
    }

    public String getDeployId()
	{
		return _deployId;
	}

    public void setDeployId(String deployId)
	{
		_deployId = deployId;
	}


    /**
	 * Return a list of filetrs which contain parameters
	 * @return
	 */
	public List getFilters()
    {
        if (_interfaceRuntime == null) return Collections.EMPTY_LIST;
		return _interfaceRuntime.getView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
    }

    public void loadXml()
    {
        if(_interfaceRuntime == null)
             if(_typeXml.equals(QMOOConfiguration.TYPE_INFO.getXmlType()))
                 _interfaceRuntime = new mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient(XMLUtils.stringToXmlElement(_xmlContent));
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
