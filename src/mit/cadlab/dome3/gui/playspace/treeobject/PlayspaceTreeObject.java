// FileObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.treeobject;

import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.treetable.TreeTableData;
import mit.cadlab.dome3.util.xml.XMLUtils;

import javax.swing.*;
import java.util.*;

import org.dom4j.Element;

/**
 * Example object that implements TreeTableData
 */
public class PlayspaceTreeObject extends DefaultTreeObject
{

	private ClientObjectRecord file;

	public PlayspaceTreeObject(ClientPlayspaceRecord psRecord)
	{
		super(psRecord, true);
		file = psRecord;
	}

	public ClientPlayspaceRuntime getPlayspace()
	{
		return ((ClientPlayspaceRecord) data).getPlayspace();
	}

	/*public void addContent(PlayspaceModelTreeObject f)
	{
		content.add(f);
	}*/

	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.PLAYSPACE_OPEN : DomeIcons.PLAYSPACE);
	}

	public List getChildren()
	{
		return file.getContent();
	}
}