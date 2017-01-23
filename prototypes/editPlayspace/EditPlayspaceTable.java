package editPlayspace;

import mit.cadlab.dome.network.client.ClientPlayspaceBuild;
import mit.cadlab.dome.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.connection.LoginUtils;
import mit.cadlab.dome.util.xml.XMLUtils;
import mit.cadlab.dome.gui.swing.table.DomeTable;
import mit.cadlab.dome.gui.fileSystem.DomeFile;
import mit.cadlab.dome.server.db.DbConstants;
import mit.cadlab.dome.icons.DomeIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Element;
import editPlayspace.EditPlayspaceAddModelDialog;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 13, 2003
 * Time: 6:20:41 PM
 * To change this template use Options | File Templates.
 */
public class EditPlayspaceTable extends JScrollPane implements TableModelListener {

	private boolean ALLOW_COLUMN_SELECTION = false;
	private boolean ALLOW_ROW_SELECTION = true;
	JScrollPane scrollPane;
	EditPlayspaceTableModel tm;
	private String[] ColumnName = {"  ","Name", "Description", "URL"};
	ServerConnection svrConn;
	JTable table;
	int selectedRow;
	ClientPlayspaceBuild cpd = null;
	EditPlayspaceAddModelDialog dialog;


	public EditPlayspaceTable(ServerConnection svrConn)
	{
		this.svrConn = svrConn;
		initTable();
		cpd = new ClientPlayspaceBuild (svrConn);
	}

	private void initTable ()
	{
		tm = new EditPlayspaceTableModel(ColumnName,0);
		tm.addTableModelListener(this);
		table = new JTable(tm);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		this.setBackground(Color.white);

		this.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON3){
					System.out.println("Button-3 clicked");
				}
			}
			public void mouseExited(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e){
			}
		});

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (ALLOW_ROW_SELECTION) { // true by default
			ListSelectionModel rowSM = table.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					//Ignore extra messages.
					if (e.getValueIsAdjusting()) return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						System.out.println("No rows are selected.");
					} else {
						selectedRow = lsm.getMinSelectionIndex();
						System.out.println("Row " + selectedRow
						        + " is now selected.");
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
			colSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					//Ignore extra messages.
					if (e.getValueIsAdjusting()) return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						System.out.println("No columns are selected.");
					} else {
						int selectedCol = lsm.getMinSelectionIndex();
						System.out.println("Column " + selectedCol
						        + " is now selected.");
					}
				}
			});
		}

		DomeTable.customizeTable(table);
		this.setViewportView(table);
		table.getColumnModel().getColumn(0).setMaxWidth(22);
		//table.getColumnModel().getColumn(0).
		table.setRowHeight(22);
		table.setShowGrid(false);
	}


	public void initEditPlayspaceTable(String objectId){

		String xml = FileSystemFunctions.getPlayspaceDescription(svrConn, objectId);
		Element XMLDescription = XMLUtils.stringToXmlElement(xml);
		if(XMLDescription != null){
			cpd = new ClientPlayspaceBuild(svrConn, XMLDescription);
			Collection models = cpd.getModels();
			Collection projects = cpd.getProjects();

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

		}
	}

	public void showAddDialog(){
		dialog = new EditPlayspaceAddModelDialog(this);
		dialog.pack();
		dialog.show();
	}

	public void addRows() {

	}


	public void addRow() {
		DomeFile so;
		if ((so = dialog.getSelectedDomeFile()) != null) {
			if (so.getType().equalsIgnoreCase("model")) {
				cpd.addModel(so.getId().toString(), so.getName(), so.getDescription(), dialog.getServerConnectionPort());
				tm.addRow(new Object[]{getIcon("model"), so.getName(), so.getDescription(), dialog.getServerConnectionPort(), so.getId().toString(), so.getType()});
			}
			else if (so.getType().equalsIgnoreCase("project")) {
				cpd.addModel(so.getId().toString(), so.getName(), so.getDescription(), dialog.getServerConnectionPort());
				tm.addRow(new Object[]{getIcon("project"), so.getName(), so.getDescription(), dialog.getServerConnectionPort(), so.getId().toString(), so.getType()});
			}
		}
	}

	private Icon getIcon(String type) {
		if(type.equalsIgnoreCase("model"))
			return DomeIcons.getIcon(DomeIcons.MODEL);
		else if (type.equalsIgnoreCase("project"))
			return DomeIcons.getIcon(DomeIcons.PROJECT);
		return null;
	}



	public void deleteRows(){

	}

	public void deleteRow(){

		try{
			if((tm.getType(selectedRow)).equals("model")){
				cpd.removeModel(tm.getId(selectedRow));
			}
			else if(tm.getType(selectedRow).equals("project")){
				cpd.removeProject(tm.getId(selectedRow));
			}
			tm.removeRow(selectedRow);

		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("No such row");
		}

	}


	public void dumpXML ()
	{
		String xml = cpd.getXmlDescription();
		FileWriter writer = null;
		try {
			writer = new FileWriter ("playspace.xml");
			writer.write(xml);
			writer.close ();
		}
		catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
	}

	public void tableChanged(TableModelEvent e){

	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}
