// FileObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.treeobject;

import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterRuntime;
import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.treetable.TreeTableData;
import mit.cadlab.dome3.network.client.objectrecord.ClientParameterRecord;

import javax.swing.*;
import java.util.List;

/**
 * Example object that implements TreeTableData
 */
public class PlayspaceParameterTreeObject extends DefaultTreeObject
{
	private InterfaceParameterRuntime param = null;


	public PlayspaceParameterTreeObject(ClientParameterRecord paramRecord)
	{
		super(paramRecord);
	}

	public void setParameter(InterfaceParameterRuntime param)
	{
		this.param = param;
	}

	public InterfaceParameterRuntime getParameter()
	{
		return param;
	}

	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(DomeIcons.PARAMETER);
	}

	public List getChildren()
	{
		return null;
	}
}