package buildPlayspace;

import editPlayspace.EditPlayspaceTable;
import mit.cadlab.dome.gui.deploy.deployModel.DeployModelGui;

import javax.swing.JFrame;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 3, 2003
 * Time: 8:22:07 AM
 * To change this template use Options | File Templates.
 */

/**
 * Data model class for running the deploy playspace wizard
 */
public class BuildPlayspace
{

	private static EditPlayspaceTable table;

	//todo: Playspace context menu for build mode
	// should have the following items: New; Open...; separator; Save; Save as; Close -- put a separator after open

	//todo: need methods for open new, open ... save, save as
	//save presumably uses the a variant of the dumpl xml that pops up file save dialog and lets you name the file
	//decide with Elaine what sufix to use for the playspace files
	//should use the DomeDFileChooser class for the open/save dialogs
	// after save, save as, and open should call the public method gui.setFilePathField(); to update the textfield at the bottom of the GUI

	//todo: update the main Build menu for this new build type
	//should have the following items: New model -> (dome model, excel model sub menus); Open model ...; separator; New playspace;
	//                                 Open playspace...; separator; Close all; Save all; separator; Exit


	//todo: this is in the edit table (editPlayspace package in prototypes). When you select a cell it turns black. Could the selection behaviour be
	//the same as other build mode DOME tables?

	public static void openNewPlayspace(JFrame f)
	{
		f.setTitle("New playspace");
		table = new EditPlayspaceTable(null);
		gui.setTable(table);
	}

	public void setPlayspaceName(String name)
	{
		table.setName(name);
	}

	public static EditPlayspaceTable getTable()
	{
		return table;
	}

	private static BuildPlayspaceGui gui;

	public static JFrame createBuildGui(String windowName)
	{
		BuildPlayspace bps = new BuildPlayspace();
		JFrame f = new JFrame(windowName);
		f.getContentPane().add(bps.gui);
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		return f;
	}

	/**
	 * Constructor for the Deployplayspace class
	 */
	public BuildPlayspace()
	{
		gui = new BuildPlayspaceGui(this);
	}

	public static void main(String[] args)
	{
		JFrame f = createBuildGui("Build playspace");
		openNewPlayspace(f);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.show();
	}
}
