package mit.cadlab.dome3.gui.objectmodel.model.tool.run;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.table.CachingTableObjectFactory;
import mit.cadlab.dome3.swing.table.TableObjectFactory;

import javax.swing.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 26, 2004
 * Time: 3:05:39 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolRunFileSystemTable extends AbstractAnalysisToolRunFileSystemTable
{
    protected static String[] columnNames = new String[]{"Names", "Value", "Status"};
    protected static int[] columnWidths = new int[]{300, 250, 100};

    public AnalysisToolRunFileSystemTable(ServerConnection conn, DomeFile analysisToolStaticInfo,
	                                   OptimizationToolClientRuntime analysisTool,
	                                 ClientPlayspaceRuntime playspace)
	{
		super(conn, analysisTool, analysisToolStaticInfo, playspace);
	}

    protected TableObjectFactory createTableObjectFactory()
	{
		CachingTableObjectFactory factory = new CachingTableObjectFactory("AnalysisToolRunTableFactory");
        factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord",
                                        "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientModelRecordTableObject");
		factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord",
		                                "mit.cadlab.dome3.gui.playspace.tableObject.ClientInterfaceRecordTableObject");
        factory.registerTableObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolInterfaceRecord",
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
		factory.registerTableObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter",
		                                "mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable$ParameterTableObject",
		                                "mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter");

		return factory;
	}

	protected JTree getTree()
	{
		return _tree;
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

	public OptimizationToolClientRuntime getAnalysisToolClientRuntime()
	{
		return _analysisTool;
	}

	protected ObjectTreeNode createRootNode()
	{
		ClientAnalysisToolRecord aRecord = new ClientAnalysisToolRecord(_analysisTool.getRuntimeId(), _analysisToolStaticInfo.getName(),
		                                                       _analysisToolStaticInfo.getDescription(),
		                                                       _analysisToolStaticInfo.getUrl(), _analysisTool, _playspace);
		aRecord.listChildren();
		return new AnalysisToolRunTreeNode(aRecord);
	}
}
