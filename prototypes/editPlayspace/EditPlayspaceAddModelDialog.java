package editPlayspace;

import mit.cadlab.dome.gui.fileSystem.DomeFile;
import mit.cadlab.dome.gui.runBrowser.RunBrowser;
import mit.cadlab.dome.gui.serverPanel.ServerPanel;
import mit.cadlab.dome.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 1, 2003
 * Time: 3:01:32 PM
 * To change this template use Options | File Templates.
 */

/**
 * Class used select models for adding to something
 */
public class EditPlayspaceAddModelDialog extends JDialog
{
	public static final Dimension DEFAULT_SIZE = new Dimension(600, 400);
	public static final GridBagConstraints gbc = null;

	private RunBrowser browser;
	private JButton addButton;
	private JButton doneButton;
	private String selectionPath = "";
	private Object selectionObjectId = null;
	private Object selectedObject;
	private ServerConnection selectionServerConnection = null;
	private EditPlayspaceTable ept;

	/**
	 *
	 * @return
	 */
	public String getSelectionPath()
	{
		return selectionPath;
	}

	/**
	 *
	 * @return
	 */
	public DomeFile getSelectedDomeFile()
	{
		return (DomeFile)selectedObject;
	}

	public String getServerConnectionPort ()
	{
		return selectionServerConnection.getServerPort();
	}

	/**
	 * Class used to add models to playspaces or projects
	 */
	public EditPlayspaceAddModelDialog(EditPlayspaceTable ept)
	{
		this.setTitle("Edit Playspace: Add Models");
		this.ept = ept;

		browser = new RunBrowser(ServerPanel.MODEL_SUBSCRIBE); // call the right constructor

		browser.addSelectionListeners(new ServerPanelSelectionListener(){
			String mypath = new String();
			Object obj;
			public void selectionChanged(String path, Object id, ServerConnection svr) {
				selectionObjectId = id;
				selectionServerConnection = svr;
				selectedObject = browser.getCurrentSelectedObject();
			}

		});

		JPanel p = new JPanel();

		JComponent[] comps = {makePanel()};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		this.setModal(true);
		this.getContentPane().add(p);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		addButton = Templates.makeButton("add",
		        new ActionListener() {
			        public void actionPerformed(ActionEvent e) {
				        ept.addRow();
				        //getSelectionAndClose();
			        }
		        });
		doneButton = Templates.makeButton("done",
		        new ActionListener() {
			        public void actionPerformed(ActionEvent e) {
				        closeThis();
			        }
		        });


		JComponent[] comps = {browser, addButton, doneButton};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public void closeDialog(){
		this.dispose();
	}

	private void getSelectionAndClose(){
		if (selectionObjectId !=null) {
			//todo needs to be implemted to get the selectedObjectID and selected serverConnection to the parent
			browser.logoutOnClose();
			this.setVisible(false);
			//this.dispose();
		}
	}

	private void closeThis(){
		this.dispose();
	}


	/*public static void main(String[] args)
	{
		JDialog d= new EditPlayspaceAddModelDialog();
		d.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		d.setSize(DEFAULT_SIZE);
		d.show();


	}*/
}
