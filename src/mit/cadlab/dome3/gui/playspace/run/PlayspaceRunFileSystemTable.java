// BrowsePlayspaceFileSystemTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.run;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRecord;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;

import javax.swing.JTree;

public class PlayspaceRunFileSystemTable extends AbstractRunPlayspaceFileSystemTable
{
	protected static String[] columnNames = new String[]{"Names", "Value", "Status", "Description"};
	protected static int[] columnWidths = new int[]{250, 150, 150, 200};

	public PlayspaceRunFileSystemTable(ServerConnection conn, DomeFile playspaceStaticInfo,
	                                   ClientPlayspaceRuntime mPlayspace)
	{
		super(conn, mPlayspace, playspaceStaticInfo);
	}


	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("PlayspaceRunTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientInterfaceRecordTableObject");
        factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
      //   factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord",
		//                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");

		factory.registerTableObjectInfo("mit.cadlab.dome3.gui.fileSystem.Folder",
		                                "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject",
		                                "mit.cadlab.dome3.gui.fileSystem.FileSystemObject");

		//for filters
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                                "mit.cadlab.dome3.gui.deploy.components.DeployFilterTableObject",
		                                "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		//for DefaultContextBuilder
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.AbstractDomeObject",
		                                "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$DefaultTableObject",
		                                "mit.cadlab.dome3.objectmodel.DomeObject");
		//for parameters
        //Qing change here make playspace beheavior consistently with project run panel, not allow user input value here
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                                //"mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$ParameterTableObject",
                                        "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$ProjectRunParameterTableObject",
		                                "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");

		return factory;
	}

	protected JTree getTree()
	{
		return tree;
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

	protected int[] getColumnWidths()
	{
		return columnWidths;
	}

	public ClientPlayspaceRuntime getClientPlayspaceRuntime()
	{
		return playspace;
	}

	protected ObjectTreeNode createRootNode()
	{
		ClientPlayspaceRecord psRecord = new ClientPlayspaceRecord(playspace);
		psRecord.listChildren();
		return new PlayspaceRunTreeNode(psRecord);
	}
}
