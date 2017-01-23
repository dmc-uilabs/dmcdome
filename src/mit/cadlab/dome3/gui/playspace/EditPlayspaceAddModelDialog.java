// EditPlayspaceAddModelDialog.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace;

import mit.cadlab.dome3.gui.runbrowser.RunBrowser;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 *
 */
public class EditPlayspaceAddModelDialog extends JDialog
{
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
		return (DomeFile) selectedObject;
	}

	public String getServerConnectionPort()
	{
		return selectionServerConnection.getServerPort();
	}

	/**
	 * Class used to add models to playspaces or projects
	 */
	public EditPlayspaceAddModelDialog(EditPlayspaceTable ept)
	{
		this.setTitle(ept.getPlayspaceName() + ": add models");
		this.ept = ept;

        //Qing Change here, should allow tool to be added, so should set additional filter here,
		browser = new RunBrowser(ServerPanel.MODEL_SUBSCRIBE,ServerPanel.Models_Projects_Tools_Filter_Tree_Selection_Model); // call the right constructor

		browser.addSelectionListeners(new ServerPanelSelectionListener()
		{
			String mypath = new String();
			Object obj;

			public void selectionChanged(String path, Object id, ServerConnection svr)
			{
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
		addButton = Templates.makeButton("put selection in playspace",
		                                 new ActionListener()
		                                 {
			                                 public void actionPerformed(ActionEvent e)
			                                 {
				                                 ept.addRow();
				                                 //getSelectionAndClose();
			                                 }
		                                 });
		doneButton = Templates.makeButton("done",
		                                  new ActionListener()
		                                  {
			                                  public void actionPerformed(ActionEvent e)
			                                  {
				                                  closeThis();
			                                  }
		                                  });


		JComponent[] comps = {browser, addButton, doneButton};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public void closeDialog()
	{
		this.dispose();
	}

	private void getSelectionAndClose()
	{
		if (selectionObjectId != null) {
			//todo needs to be implemted to get the selectedObjectID and selected serverConnection to the parent
			browser.logoutOnClose();
			this.setVisible(false);
			//this.dispose();
		}
	}

	private void closeThis()
	{
		//maybe need to close connection
		this.dispose();
	}


	/*public static void main(String[] args)
	{
		JDialog d= new EditPlayspaceAddModelDialog();
		d.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		d.setSize(DomeBuildFrame.DEFAULT_SIZE);
		d.show();


	}*/
}
