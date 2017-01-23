// EditPlayspaceTable.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceBuild;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.util.xml.XMLUtils;

import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public class EditPlayspaceTable extends JScrollPane implements TableModelListener
{

	private boolean ALLOW_COLUMN_SELECTION = false;
	private boolean ALLOW_ROW_SELECTION = true;
	JScrollPane scrollPane;
	EditPlayspaceTableModel tm;
	private String[] ColumnName = {"  ", "Name", "Description", "URL"};
	ServerConnection svrConn;
	JTable table;
	int selectedRow;
	ClientPlayspaceBuild cpd = null;
	EditPlayspaceAddModelDialog dialog;

	public static final String FILENAME = "fileName";
	//protected String fileName = "";
	protected CPD_PropertyChangeListener listener = new CPD_PropertyChangeListener();

	public EditPlayspaceTable(ServerConnection svrConn)
	{
		this.svrConn = svrConn;
		this.getViewport().setBackground(Color.white);
		initTable();
		cpd = new ClientPlayspaceBuild(svrConn);
		cpd.setName("Dome Playspace");
		cpd.addPropertyChangeListener(listener);

	}

	public EditPlayspaceTable(ServerConnection svrConn, String filename, Element XMLDescription)
	{
		initTable();
		this.getViewport().setBackground(Color.white);

		this.svrConn = svrConn;
		//this.fileName=filename;
		if (XMLDescription != null) {
			cpd = new ClientPlayspaceBuild(svrConn, XMLDescription);
			cpd.setFileName(filename);
			cpd.addPropertyChangeListener(listener);
			Collection models = cpd.getModels();
			Collection projects = cpd.getProjects();
            Collection tools = cpd.getTools();

			Iterator mi = models.iterator();
			String[] aModel;// <model name, model description, url, model id, type>
			//Qing-- should one for the icon column
			while (mi.hasNext()) {
				aModel = (String[]) mi.next();
				int i = aModel.length;
				ArrayList model = new ArrayList(i + 1);
				model.add(getIcon("model"));
				for (int j = 0; j < i; j++)
					model.add(aModel[j]);
				tm.addRow(model.toArray());
			}

			Iterator pi = projects.iterator();
			String[] aProject;// <project name, project description, url, project id, type>
			while (pi.hasNext()) {
				aProject = (String[]) pi.next();
				int i = aProject.length;
				ArrayList project = new ArrayList(i + 1);
				project.add(getIcon("project"));
				for (int j = 0; j < i; j++)
					project.add(aProject[j]);
				tm.addRow(project.toArray());

			}

            Iterator ti = tools.iterator();
			String[] aTool;// <project name, project description, url, project id, type>
			while (ti.hasNext()) {
				aTool = (String[]) ti.next();
				int i = aTool.length;
				ArrayList tool = new ArrayList(i + 1);
				tool.add(getIcon(DomeFile.ANALYSIS_TOOL_TYPE));
				for (int j = 0; j < i; j++)
					tool.add(aTool[j]);
				tm.addRow(tool.toArray());

			}

		}

	}

	public EditPlayspaceTable(ServerConnection svrConn, Element XMLDescription)
	{
		this(svrConn, "", XMLDescription);
	}

	private void initTable()
	{
		tm = new EditPlayspaceTableModel(ColumnName, 0);
		tm.addTableModelListener(this);
		table = new JTable(tm);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(ALLOW_ROW_SELECTION);
		table.setColumnSelectionAllowed(ALLOW_COLUMN_SELECTION);
		if (ALLOW_ROW_SELECTION) { // true by default
			ListSelectionModel rowSM = table.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					//Ignore extra messages.
					if (e.getValueIsAdjusting()) return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						//System.out.println("No rows are selected.");
					} else {
						selectedRow = lsm.getMinSelectionIndex();
						//	System.out.println("Row " + selectedRow
						//	        + " is now selected.");
					}
				}
			});
		} else {
			table.setRowSelectionAllowed(false);
		}

		if (ALLOW_COLUMN_SELECTION) { // false by default
			if (ALLOW_ROW_SELECTION) {
				//We allow both row and column selection, which
				//implies that we *really* want to allow individual
				//cell selection.
				table.setCellSelectionEnabled(true);
			}
			table.setColumnSelectionAllowed(true);
			ListSelectionModel colSM =
			        table.getColumnModel().getSelectionModel();
			colSM.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					//Ignore extra messages.
					if (e.getValueIsAdjusting()) return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						//System.out.println("No columns are selected.");
					} else {
						int selectedCol = lsm.getMinSelectionIndex();
						//System.out.println("Column " + selectedCol
						//     + " is now selected.");
					}
				}
			});
		}

		this.setViewportView(table);
		table.getColumnModel().getColumn(0).setMaxWidth(22);
		table.setRowHeight(22);
		table.setShowGrid(false);

		//table.setDefaultRenderer(Object.class, new Renderers.NothingRenderer());
		table.setDefaultEditor(Object.class, new PlayspaceTextCellEditor());
		DomeTable.customizeTable(table);//this only will constrain the table appearance, not the way renderer and editor work

	}

   //the following method is not used anywhere, if going to use it, pls refer to line 77 to 116 to fix the, might have to add another colume for showing the icons
	public void initEditPlayspaceTable(String objectId)
	{

		Element XMLDescription = XMLUtils.stringToXmlElement(
		        FileSystemFunctions.getPlayspaceDescription(svrConn, objectId));
		if (XMLDescription != null) {
			cpd = new ClientPlayspaceBuild(svrConn, XMLDescription);
			Collection models = cpd.getModels();
			Collection projects = cpd.getProjects();
            Collection tools = cpd.getTools();

			Iterator mi = models.iterator();
			String[] aModel;// <model name, model description, url, model id, type>
			while (mi.hasNext()) {
				aModel = (String[]) mi.next();
				tm.addRow(aModel);
			}

			Iterator pi = projects.iterator();
			String[] aProject;// <model name, model description, url, model id, type>
			while (pi.hasNext()) {
				aProject = (String[]) pi.next();
				tm.addRow(aProject);
			}

            Iterator ti = tools.iterator();
			String[] aTool;// <project name, project description, url, project id, type>
			while (ti.hasNext()) {
				aTool = (String[]) ti.next();
				tm.addRow(aTool);

			}
		}
	}

	public void showAddDialog()
	{
		dialog = new EditPlayspaceAddModelDialog(this);
		Point t = this.getLocationOnScreen();
		dialog.setLocation(t.x + 25, t.y + 25);
		dialog.pack();
		dialog.show();

	}

	public void addRows()
	{

	}


	public void addRow()
	{
		//Qing : make changes so that the same model or project should not be added twice!
        //Qing : added March 4th to allow add analysis tools
		DomeFile so;
		if ((so = dialog.getSelectedDomeFile()) != null) {
			if (so.getType().equalsIgnoreCase("model")) {
				if (!cpd.containsModel(so.getId().toString())) {
					cpd.addModel(so.getId().toString(), so.getName(), so.getDescription(), dialog.getServerConnectionPort());
					tm.addRow(new Object[]{getIcon("model"), so.getName(), so.getDescription(), dialog.getServerConnectionPort(), so.getId().toString(), so.getType()});
				} else {
					OneButton1Msg.showWarning(dialog, "Playspace add warning", so.getName() + "\ncannot be added to the playspace because duplicate \nmodels are not allowed in playspaces.", "ok", OneButton1Msg.DEFAULT_SIZE);
				}
			} else if (so.getType().equalsIgnoreCase("project")) {
				if (!cpd.containsProject(so.getId().toString())) {
					cpd.addProject(so.getId().toString(), so.getName(), so.getDescription(), dialog.getServerConnectionPort());
					tm.addRow(new Object[]{getIcon("project"), so.getName(), so.getDescription(), dialog.getServerConnectionPort(), so.getId().toString(), so.getType()});
				} else {
					OneButton1Msg.showWarning(dialog, "Playspace add warning", so.getName() + "\ncannot be added to the playspace because duplicate \nprojects are not allowed in playspaces.", "ok", OneButton1Msg.DEFAULT_SIZE);
				}
			} else if (so.getType().equalsIgnoreCase(DomeFile.ANALYSIS_TOOL_TYPE)) {
				if (!cpd.containsTool(so.getId().toString())) {
					cpd.addTool(so.getId().toString(), so.getName(), so.getDescription(), dialog.getServerConnectionPort());
					tm.addRow(new Object[]{getIcon(DomeFile.ANALYSIS_TOOL_TYPE), so.getName(), so.getDescription(), dialog.getServerConnectionPort(), so.getId().toString(), so.getType()});
				} else {
					OneButton1Msg.showWarning(dialog, "Playspace add warning", so.getName() + "\ncannot be added to the playspace because duplicate \ntools are not allowed in playspaces.", "ok", OneButton1Msg.DEFAULT_SIZE);
				}
			}
		}
	}


	private Icon getIcon(String type)
	{
		if (type.equalsIgnoreCase("model"))
			return DomeIcons.getIcon(DomeIcons.MODEL);
		else if (type.equalsIgnoreCase("project"))
			return DomeIcons.getIcon(DomeIcons.PROJECT);
        else if (type.equalsIgnoreCase(DomeFile.ANALYSIS_TOOL_TYPE))
			return DomeIcons.getIcon(DomeIcons.TOOL);
        return null;
	}


	public void deleteRows()
	{

	}

	public void deleteRow()
	{

		try {
			if ((tm.getType(selectedRow)).toLowerCase().equals("model")) {
				cpd.removeModel(tm.getId(selectedRow));
				tm.removeRow(selectedRow);

			} else if (tm.getType(selectedRow).toLowerCase().equals("project")) {
				cpd.removeProject(tm.getId(selectedRow));
				tm.removeRow(selectedRow);

			}
            else if (tm.getType(selectedRow).toLowerCase().equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
				cpd.removeTool(tm.getId(selectedRow));
				tm.removeRow(selectedRow);

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("No such row");
		}

	}

	public void setDescription(String description)
	{
		try {
			String id = tm.getId(selectedRow);
			cpd.setDescription(tm.getType(selectedRow), id, description);

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("No such row");
		}
	}

	public void save()
	{
		cpd.save();
	}

	public void saveAs()
	{
		cpd.saveAs();
	}

	public boolean isSaved()
	{
		return cpd.isSaved();
	}

	public ClientPlayspaceBuild getPlayspace()
	{
		return cpd;
	}

	public String getPlayspaceName()
	{
		return cpd.getName();
	}

	public void setPlayspaceName(String newName)
	{
		cpd.setName(newName);
	}

	public void tableChanged(TableModelEvent e)
	{
		//cpd.setShouldSave(false);
	}

	protected class CPD_PropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(ClientPlayspaceBuild.FILENAME)) {
				firePropertyChange(FILENAME, null, newValue);
			}
			if (property.equals(NameListener.NAME)) {
				firePropertyChange(NameListener.NAME, null, newValue);
			}
		}
	}

	protected class PlayspaceTextCellEditor extends TextCellEditor {
		PlayspaceTextCellEditor() {
			super();
			final JTextField tf = (JTextField)editorComponent;
			tf.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    EditPlayspaceTable.this.setDescription(tf.getText());
				}
			});
		}
	}
}

