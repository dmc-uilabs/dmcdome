package mit.cadlab.dome3.gui.playspace.treeobject;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.*;
import java.util.List;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 27, 2004
 * Time: 11:31:50 AM
 * To change this template use Options | File Templates.
 */
public class PlayspaceAnalysisToolTreeObject extends DefaultTreeObject
{
	//private List content = new ArrayList();
	private ClientAnalysisToolRecord _file;

	public PlayspaceAnalysisToolTreeObject(ClientAnalysisToolRecord record)
	{
		super(record, record.getContent());
		_file = record;
	}

	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.ANALYSIS_TOOL_OPEN : DomeIcons.ANALYSIS_TOOL);
	}

	public String getTreeValue()
	{
		return _file.getName();
	}

	public List getChildren()
	{
		return _file.getContent();
	}

}