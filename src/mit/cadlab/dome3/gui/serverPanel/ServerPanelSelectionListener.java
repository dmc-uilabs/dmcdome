package mit.cadlab.dome3.gui.serverPanel;

import mit.cadlab.dome3.network.client.connection.ServerConnection;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 9, 2003
 * Time: 7:14:03 PM
 * To change this template use Options | File Templates.
 */
public interface ServerPanelSelectionListener
{
	public void selectionChanged(String path, Object id, ServerConnection svr);
}
