// FileObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.treeobject;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.model.tool.run.AnalysisToolRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationToolInterfaceRunPanel;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.swing.tree.GuiTreeObject;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import java.util.List;

/**
 * Example object that implements TreeTableData
 */
public class PlayspaceAnalysisToolInterfaceTreeObject extends DefaultTreeObject implements GuiTreeObject
{
	private ClientObjectRecord _file;
	protected JFrame _gui = null;

	public PlayspaceAnalysisToolInterfaceTreeObject(ClientAnalysisToolInterfaceRecord ifaceRecord)
	{
		super(ifaceRecord, ifaceRecord.getContent());
		_file = ifaceRecord;
	}

	public void setInterface(OptimizationInterfaceRuntimeClient iface)
	{
		((ClientAnalysisToolInterfaceRecord) data).setInterface(iface);
	}

	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.INTERFACE_OPEN : DomeIcons.INTERFACE);
	}

	public String getTreeValue()
	{
		return _file.getName();
	}

	public List getChildren()
	{
		return _file.getContent();
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		OptimizationInterfaceRuntimeClient mInterface = ((ClientAnalysisToolInterfaceRecord) data).getInterface();
		OptimizationToolInterfaceRunPanel pane = new OptimizationToolInterfaceRunPanel(mInterface);
		_gui = new DomeRunFrame(pane, parent);
	}

	public boolean showGui()
	{
		boolean statusChange = false;
		if (_gui == null)
        { // gui does not exist
            JComponent comp = RunFocusTracker.getCurrentComponent();
            if (comp instanceof AnalysisToolRunPanel)
            {
                ((AnalysisToolRunPanel)comp).openAnalysisToolItem();
            }
            statusChange = true;
        }
        else
        { // gui exists
            _gui.toFront(); // make sure it is showing
        }
        if (statusChange)
            fireNodeValueChanged();
        return statusChange;

	}

	public boolean hideGui()
	{
		if (_gui == null) { // gui does not exist
			return false;
		} else { // gui exists and is showing
			_gui.hide();
			_gui.dispose();
			_gui = null;
			fireNodeValueChanged();
			return true;
		}
	}
}