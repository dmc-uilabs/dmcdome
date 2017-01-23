package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;

import java.util.List;
import java.awt.Component;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 31, 2003
 * Time: 7:22:06 PM
 * To change this template use Options | File Templates.
 */
public class ClientModelRecord extends ClientObjectRecord
{
	private ClientPlayspaceRuntime playspaceRef = null;
	private boolean interfacesAdded;
    private ClientModelRuntime model;


	//for build
	public ClientModelRecord(CompoundId modelId, String name, String description, String url)
	{
		super(modelId, name, description, url);
	}

	//for project run
	public ClientModelRecord(CompoundId modelId, String name, String description,
	                          String url, ClientPlayspaceRuntime playspace)
	{
		super(modelId, name, description, url);
		this.playspaceRef = playspace;
		this.model = playspaceRef.getModelRuntime(modelId, playspaceRef.getServerConnection());
	}

	//for run
	public ClientModelRecord(ClientModelRuntime model, ClientPlayspaceRuntime playspace)
	{
		super(model.getCompoundId(), model.getName(), model.getDescription(), model.getUrl());
		this.model = model;
		this.playspaceRef = playspace;
	}

	public ClientPlayspaceRuntime getPlayspace()
	{
		return playspaceRef;
	}

	public ClientModelRuntime getModel()
	{
		return model;
	}

	public String getStaticId ()
	{
		return compoundId.getModelStaticId();
	}


   public void listChildren(boolean createInterface)
	{
       if(createInterface) {
			listChildren();
		}
		else {  //do not create interface on the server
		if (!interfacesAdded) {
			Component c = RunFocusTracker.getCurrentComponent();
			ServerConnection svrConn = playspaceRef.getServerConnection();
			ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);
			if (newSvrConn == null)
				return;
			WaitCursorUtils.showWaitCursor(true, c);
			List iFace = playspaceRef.getInterfaces(compoundId, newSvrConn,false);
			content.addAll(iFace);
			interfacesAdded = true;
			WaitCursorUtils.showWaitCursor(false, c);
		}
       }
	}

   public void listChildren()
	{
		if (!interfacesAdded) {
			ServerConnection svrConn = playspaceRef.getServerConnection();
			ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);
			if (newSvrConn == null)
				return;
			List iFace = playspaceRef.getInterfaces(compoundId, newSvrConn);
			content.addAll(iFace);
			interfacesAdded = true;
		}
	}

    public List getInterfaces ()
	{
		return content;
	}
}
