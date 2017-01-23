package mit.cadlab.dome3.gui.objectmodel.project.run;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.table.TableObjectFactory;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;

import javax.swing.*;

public class ProjectRunFileSystemTable extends AbstractRunProjectFileSystemTable
{
	protected static String[] columnNames = new String[]{"Names", "Value", "Status"};
	protected static int[] columnWidths = new int[]{300, 250, 100};


	public ProjectRunFileSystemTable(ServerConnection conn, DomeFile projectStaticInfo,
	                                   IntegrationProjectClientRuntime project,
	                                 ClientPlayspaceRuntime playspace)
	{
		super(conn, project, projectStaticInfo, playspace);
	}


	protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("ProjectRunTableFactory");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientInterfaceRecordTableObject");

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
		//for now, do not let users edit name, value or status of parameters in the project run GUI
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
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

	public IntegrationProjectClientRuntime getClientProjectRuntime()
	{
		return this.project;
	}

	protected ObjectTreeNode createRootNode()
	{
		ClientProjectRecord pRecord = new ClientProjectRecord(project.getRuntimeId(), projectStaticInfo.getName(),
		                                                       projectStaticInfo.getDescription(),
		                                                       projectStaticInfo.getUrl(), project, playspace);
		pRecord.listChildren();
		return new ProjectRunTreeNode(pRecord);
	}
}

