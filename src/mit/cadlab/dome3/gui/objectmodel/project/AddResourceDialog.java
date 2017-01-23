// AddResourceDialog.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.DomeFileTreeObject;
import mit.cadlab.dome3.gui.fileSystem.FileSystemTreeNode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.runbrowser.RunBrowser;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 */
public class AddResourceDialog extends JDialog
{
	public static final GridBagConstraints gbc = null;

	protected IntegrationProjectBuilder projBuilder;
	private RunBrowser browser;
    private boolean isRelocateResourceDialog;
	private BuildProjectResourceInfo oldResource;
	/**
	 * Class used to add models to playspaces or projects
	 */
	public AddResourceDialog(JFrame parent, IntegrationProjectBuilder ipb,
	                         boolean isRelocateResourceDialog, final String title)
	{
		super(parent, ipb.getName() + title, false);
		this.projBuilder = ipb;
		this.isRelocateResourceDialog = isRelocateResourceDialog;
		projBuilder.addPropertyChangeListener(IntegrationProjectBuilder.NAME, new NameListener()
		{
			public void nameChanged(String newName)
			{
				setTitle(newName + title);
			}
		});
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        if(isRelocateResourceDialog) {
	        browser = new RunBrowser(ServerPanel.MODEL_SUBSCRIBE, ServerPanel.Models_Projects_Filter_Tree_Selection_Model);
        }
        else {
			browser = new RunBrowser(ServerPanel.MODEL_SUBSCRIBE, ServerPanel.Multiple_Selection_Mode); // call the right constructor
        }
		this.getContentPane().add(makePanel());
		pack();
		setLocationRelativeTo(parent);
	}

	public AddResourceDialog(JFrame parent, IntegrationProjectBuilder ipb,
	                         boolean isRelocateResourceDialog, final String title,
	                         BuildProjectResourceInfo res)
	{
		this(parent, ipb,isRelocateResourceDialog, title);
		this.oldResource = res;
	}

	public void changeResourceInfo(BuildProjectResourceInfo res) {
		this.oldResource = res;
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		String addTitle;
		String doneTitle;
		if(isRelocateResourceDialog) {
			addTitle = "select";
			doneTitle = "cancel";
		}
		else {
			addTitle = "add resource to project";
			doneTitle = "done";
		}
		JButton addButton = Templates.makeButton(addTitle,
		                                         new ActionListener()
		                                         {
			                                         public void actionPerformed(ActionEvent e)
			                                         {
				                                         if (isRelocateResourceDialog) {
					                                         relocateResource();
				                                         }
				                                         else {
				                                            addSelectedResources();
				                                         }
			                                         }
		                                         });
		JButton doneButton = Templates.makeButton(doneTitle,
		                                          new ActionListener()
		                                          {
			                                          public void actionPerformed(ActionEvent e)
			                                          {
				                                            hide();
			                                          }
		                                          });

		JComponent[] comps = {browser, addButton, doneButton};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setPreferredSize(DomeBuildFrame.DEFAULT_SIZE);
		return p;
	}

	public void close()
	{
		browser.close();
	}

	private void addSelectedResources()
	{
		TreePath[] paths = (TreePath[]) browser.getSelectedObjects();
		for (int i = 0; i < paths.length; i++) {
			FileSystemTreeNode node = (FileSystemTreeNode)paths[i].getLastPathComponent();
			DomeFileTreeObject fObj = (DomeFileTreeObject)node.getUserObject();
			DomeFile f = (DomeFile)fObj.getData();
			if (f == null)
				continue;
			ServerConnection conn = browser.getCurrentServerConnection();
			String type = f.getType().equals(DomeFile.MODEL_TYPE) ? ProjectResourceInfo.MODEL_RESOURCE : ProjectResourceInfo.PROJECT_RESOURCE;
			projBuilder.addResourceModel(type, (String) f.getId(), f.getName(), f.getDescription(), conn);
		}

	}

	private void relocateResource() {
		TreePath[] paths = (TreePath[]) browser.getSelectedObjects();
		FileSystemTreeNode node = (FileSystemTreeNode) paths[0].getLastPathComponent();
		DomeFileTreeObject fObj = (DomeFileTreeObject) node.getUserObject();
		DomeFile f = (DomeFile) fObj.getData();
		if (f == null) {
			OneButton2Msg.showError(this, "Error: Relocate Resource", oldResource.getName() + " could not be relocated",
			                        "Resource information is not available", "OK", new Dimension(1,1));
			hide();
			return;
		}
		else {
			ServerConnection conn = browser.getCurrentServerConnection();
			if(oldResource.getResourceDeployId().equals(f.getId())) {
				OneButton2Msg.showOption(this, "Option: Relocate Resource", oldResource.getName() + " is in the same location.",
				                        "Relocation was cancelled", "OK", new Dimension(1, 1));
				hide();
				return;
			}
			String type = f.getType().equals(DomeFile.MODEL_TYPE) ? ProjectResourceInfo.MODEL_RESOURCE : ProjectResourceInfo.PROJECT_RESOURCE;
			Vector oldIfaceIdsVector = FileSystemFunctions.getAvailableInterfaceIds(oldResource.getServerConnection(),
			                                                                   oldResource.getType(), oldResource.getResourceDeployId());
			Vector newIfaceIdsVector = FileSystemFunctions.getAvailableInterfaceIds(conn,
			                                                                        type, (String)f.getId());

			Hashtable oldIdsHashMap = (Hashtable)oldIfaceIdsVector.get(0);
			Hashtable newIdsHashMap = (Hashtable) newIfaceIdsVector.get(0);
			HashMap deployIdsHashMap = new HashMap();
			for (Iterator iter = oldIdsHashMap.keySet().iterator(); iter.hasNext();) {
				Object buildid = iter.next();    //build id in old resource's hashmap
				Object newDeployId = newIdsHashMap.get(buildid);
				Object oldDeployId = oldIdsHashMap.get(buildid);
				if(newDeployId == null) {
					String name = oldResource.getInterface((String)oldDeployId).getName();
					OneButton1Msg.showError(this, "Error: Resource Relocate", "Cannot match originally subscribed interface " + name,
					                        "OK", new Dimension(1,1));
					hide();
					return;
				}
				deployIdsHashMap.put(oldDeployId, newDeployId);
			}
			java.util.List subscribedIfaceIds = oldResource.getSubscribedInterfaceIds();
			for (Iterator iterator = subscribedIfaceIds.iterator(); iterator.hasNext();) {
				Object subscribedIfaceId = iterator.next();
				Object newid = deployIdsHashMap.get(subscribedIfaceId);
				if(newid == null) {
					String name = oldResource.getInterface((String) subscribedIfaceId).getName();
					OneButton1Msg.showError(this, "Error: Resource Relocate", "Cannot match originally subscribed interface " + name,
					                        "OK", new Dimension(1, 1));
					hide();
					return;
				}
			}
			projBuilder.relocateResourceModel(oldResource, type, (String) f.getId(), f.getName(), f.getDescription(),
			                                  f.getVersion(),conn, deployIdsHashMap);
		}
		hide();
	}

}
