// BrowsePlayspaceFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package playspaceRun;

import mit.cadlab.dome.gui.fileSystem.DomeFile;
import mit.cadlab.dome.network.client.ClientPlayspaceRecord;
import mit.cadlab.dome.network.client.ClientPlayspaceRuntime;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome.server.CompoundId;
import mit.cadlab.dome.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome.swing.table.TableObjectFactory;
import mit.cadlab.dome.swing.tree.ObjectTreeNode;
import mit.cadlab.dome.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.Vector;

public class PlayspaceRunFileSystemTable extends AbstractRunPlayspaceFileSystemTable
{
	protected static String[] columnNames = new String[]{"Names", "Value", "Status"};


	public PlayspaceRunFileSystemTable(ServerConnection conn, DomeFile playspaceStaticInfo)
	{
		super(conn, "playspaceRun", playspaceStaticInfo);
	}


	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("PlayspaceRunTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome.network.client.ClientModelRecord", "mit.cadlab.dome.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome.network.client.ClientInterfaceRecord", "mit.cadlab.dome.gui.playspace.tableObject.ClientInterfaceRecordTableObject");

		//factory.registerTableObjectInfo("mit.cadlab.dome.gui.fileSystem.FileSystemObject", "mit.cadlab.dome.gui.fileSystem.FileSystemObjectTableObject");
		//factory.registerTableObjectKeyLink("mit.cadlab.dome.gui.fileSystem.Folder", "mit.cadlab.dome.gui.fileSystem.FileSystemObject");
		//factory.registerTableObjectInfo("mit.cadlab.dome.network.client.ClientObjectRecord", PlayspaceRunPlayspaceTableObject");
		//for filters
		factory.registerTableObjectInfo("mit.cadlab.dome.objectmodel.modelcomponent.filter.AbstractEventFilter", "mit.cadlab.dome.gui.deploy.components.DeployFilterTableObject"
		                                , "mit.cadlab.dome.objectmodel.modelcomponent.filter.Filter");

		//for DefaultContextBuilder
		factory.registerTableObjectInfo("mit.cadlab.dome.objectmodel.AbstractDomeObject",
		                                "mit.cadlab.dome.gui.swing.treetable.BuildTreeTable$DefaultTableObject",
		                                "mit.cadlab.dome.objectmodel.DomeObject");
		//for parameters
		factory.registerTableObjectInfo("mit.cadlab.dome.objectmodel.AbstractParameter",
		                                "mit.cadlab.dome.gui.swing.treetable.BuildTreeTable$ParameterTableObject",
		                                "mit.cadlab.dome.objectmodel.Parameter");

		return factory;
	}


	public void addFolder()
	{
	}

	public void renameFolder()
	{
		//todo need to provide implementation of this
	}

	public void deleteSelectedItem()
	{
	}

	/**
	 * Note: column names array must be either static in the subclass or
	 * created in this method since this method is called in the constructor method.
	 * @return columnNames for this table; can not be null
	 */
	protected String[] getColumnNames()
	{
		return columnNames;
	}

	public ClientPlayspaceRuntime getClientPlayspaceRuntime()
	{
		return playspace;
	}

	protected ObjectTreeNode createRootNode(String scope)
	{
		//System.out.println(runningPlayspace.toString());
		//System.out.println(svrConn.toString());

		//ClientPlayspaceRuntime playspace = null;
		ClientPlayspaceRecord psRecord = null;
		// create playspace on the server side
		Vector v = RuntimeFunctionsClient.joinPlayspace(svrConn, (String) runningPlayspace.getId());

		// create interface
		if (v.size() == 2) {
			// get parameters
			CompoundId playspaceId = new CompoundId((String) v.get(0));
			String xmlContent = (String) v.get(1);

			// create the playspace
			Element playspaceElement = XMLUtils.stringToXmlElement(xmlContent);
			playspace = new ClientPlayspaceRuntime(playspaceId, svrConn, playspaceElement);
			psRecord = new ClientPlayspaceRecord(playspace);
			//pto = new PlayspaceTreeObject (psRecord);
		}
		else {
			System.out.println("PlayspaceRunFileSystemFunction.createRootNode: should not be here");
		}

		psRecord.listChildren();
		return new PlayspaceRunTreeNode(psRecord);
	}

}
