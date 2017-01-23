package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.util.xml.DomeXmlData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 9, 2003
 * Time: 1:10:10 PM
 * To change this template use Options | File Templates.
 */
public class DeployAnalysisToolData
{
    public static final String NUMBER_TOOL_INTERFACES_AVAILABLE = "number tool interfaces available";

    private DomePropertyChangeSupport _listeners = new DomePropertyChangeSupport(this);

    protected String _xmlContent, _description, _fileName, _buildId, _name = "", _toolProjectPath = "";

    protected List _analysisToolInterfaces = new ArrayList();

    private int _numAnalysisToolInterfacesAvailable = 0;

    private Vector _editPermissions = new Vector();

    public DeployAnalysisToolData(String toolFilePath)
    {
        _fileName = toolFilePath;
		try
        {
            _xmlContent = FileUtils.readTextFileAsString(_fileName);
            DomeXmlData xmlData = new DomeXmlData(DomeXmlData.MODEL, _xmlContent);
            _buildId = xmlData.getId();
            _name = xmlData.getName();
            _toolProjectPath = xmlData.getToolProjectPath();
            _analysisToolInterfaces = DeployUtilities.loadAnalysisToolInterfacesForDeploy(toolFilePath);
            registerListeners();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setDescription(String newDescription)
	{
		if (newDescription == null)
			_description = "";
		else
			_description = newDescription;
	}

    /**
     * sets the number of available analysis tool
     * interfaces for a deploy analysis tool data object
     */
    protected void setNumAvailable()
	{
		Integer oldAvailable = new Integer(_numAnalysisToolInterfacesAvailable);
		_numAnalysisToolInterfacesAvailable = 0;
		ListIterator iterator = _analysisToolInterfaces.listIterator();
		while (iterator.hasNext()) {
			if (((DeployAnalysisToolInterfaceData) iterator.next()).getIsAvailable().booleanValue()) _numAnalysisToolInterfacesAvailable++;
		}
		_listeners.firePropertyChange(DeployAnalysisToolData.NUMBER_TOOL_INTERFACES_AVAILABLE, oldAvailable, new Integer(_numAnalysisToolInterfacesAvailable));
	}

    /**
	 * @return list of DeployInterfaceData structures
	 */

	public List getToolInterfaces()
	{
		return _analysisToolInterfaces;
	}

    public String getDescription()
    {
        return _description;
    }

    public String getName()
    {
        return _name;
    }

    public String getXmlContent()
	{
		return this._xmlContent;
	}

    public String getToolProjectPath()
    {
        return _toolProjectPath;
    }

    public String getAnalysisToolFileName()
    {
        return _fileName;
    }

    /**
     * @return number of available analysis tool interfaces
     */

    public int getNumAvailable()
	{
		return _numAnalysisToolInterfacesAvailable;
	}

    /**
     * registers the interface available listener
     */

    protected void registerListeners()
	{
		InterfaceAvailableListener l = new InterfaceAvailableListener();
		ListIterator iterator = _analysisToolInterfaces.listIterator();
		while (iterator.hasNext())
        {
            ((DeployAnalysisToolInterfaceData) (iterator.next())).addPropertyChangeListener(DeployAnalysisToolInterfaceData.IS_AVAILABLE, l);
        }
	}

    public Vector getEditPermissions()
	{
		return _editPermissions;
	}

    public void setEditPermissions(Vector editPermissions)
	{
		if (editPermissions == null)
			editPermissions = DbConstants.EMPTY_VECTOR;
		else
			_editPermissions = editPermissions;
	}

    /**
     * listener for available interfaces
     */

	class InterfaceAvailableListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			setNumAvailable();
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
}
