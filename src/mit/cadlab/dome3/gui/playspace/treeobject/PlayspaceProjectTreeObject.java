package mit.cadlab.dome3.gui.playspace.treeobject;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: May 1, 2003
 * Time: 1:53:59 PM
 * To change this template use Options | File Templates.
 */
public class PlayspaceProjectTreeObject extends DefaultTreeObject
{
	//private List content = new ArrayList();
	private ClientProjectRecord file;

	public PlayspaceProjectTreeObject(ClientProjectRecord record)
	{
		super(record, record.getContent());
		file = record;
	}

	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.PROJECT_OPEN : DomeIcons.PROJECT);
	}

	public String getTreeValue()
	{
		return file.getName();
	}

	public List getChildren()
	{
		return file.getContent();
	}

}