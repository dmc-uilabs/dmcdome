package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.CompoundId;

import java.util.List;
import java.util.Collections;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 27, 2004
 * Time: 9:56:36 PM
 * To change this template use Options | File Templates.
 */
public class ClientAnalysisToolInterfaceRecord extends ClientObjectRecord
{
    private mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient _iface;
	private boolean _ifaceOpened = false;
	private ClientPlayspaceRuntime _playspaceRef = null;
	private String _previousView;
	private ServerConnection _svrConnection = null;

	public ClientAnalysisToolInterfaceRecord(ServerConnection ifaceServerConn, CompoundId compoundId,
	                             String name, String description,
	                             String url, ClientPlayspaceRuntime playspace)
	{
		super(compoundId, name, description, url);
		_playspaceRef = playspace;
		_svrConnection = ifaceServerConn;
	}

	public String getStaticId ()
	{
		return compoundId.getInterfaceStaticId();
	}

	public CompoundId getRuntimeId()
	{
		return compoundId;
	}

	public void setInterface(mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient iface)
	{
		_iface = iface;
	}

	public mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient getInterface()
	{
		createInterface();
		return _iface;
	}

	public void setCurrentView(String view)
	{
		_iface.setCurrentView(view);
	}

	public String getCurrentView()
	{
		if (_iface == null) {
			//This is the case when listChildren has not been called and thus interface instance is
			//not yet created
			return ToolInterface.INTERFACE_CAUSALITY_VIEW;
		}
		return _iface.getCurrentView();
	}

	public void listChildren()
	{
		String cView = null;
		if (_iface != null) {
			cView = _iface.getCurrentView();
			if (cView.equals(_previousView))
				return;				//don't do any work if its the same view

			//otherwise remove old view objs
			if (!content.isEmpty()) {
				Object[] objs = content.toArray();
				content.removeAll(objs);
			}
			//and add new view objs
			List icv = _iface.getView(cView);
			content.addAll(icv);
			_previousView = cView;
		}
		else {
			createInterface();
			//otherwise remove old view objs
			if (!content.isEmpty()) {
				Object[] objs = content.toArray();
				content.removeAll(objs);
			}
			//and add new view objs
			cView = ToolInterface.INTERFACE_CAUSALITY_VIEW;
			List icv = _iface.getView(cView);
			content.addAll(icv);
			_previousView = cView;
		}
	}

	private void createInterface()
	{
		if (_ifaceOpened == false) {
			// try to get the interface and restore an existing interface panel
			_iface = _playspaceRef.getOptimizationToolInterface(compoundId, _svrConnection);
			if (_iface == null) {
				_iface = _playspaceRef.createAnalysisToolInterface(compoundId, _svrConnection);
			}
			_iface.setCurrentView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
			_iface.addPropertyChangeListener(new InterfaceRemovalListener());
			_ifaceOpened = true;
		}
	}

	/**
	 * Return a list of filters which contain parameters
	 * @return
	 */
	public List getFilters(mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient iface)
	{
		if (iface == null) return Collections.EMPTY_LIST;

		List interfaceCausalityView = iface.getView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
		List buildView = iface.getView(ToolInterface.BUILD_VIEW);

		return interfaceCausalityView;
	}


	/**
	 * Listens to the interface for a removal event. The interface is removed when a model
	 * is killed. When we receive this message we will remove the interface from the record.
	 */
	private class InterfaceRemovalListener implements PropertyChangeListener
	{
		public synchronized void propertyChange(PropertyChangeEvent evt)
		{
			if (mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient.PROPERTY_CLOSED.equals(evt.getPropertyName()))
			{
				_iface = null;
                _ifaceOpened = false;
			}
		}
	}
}
