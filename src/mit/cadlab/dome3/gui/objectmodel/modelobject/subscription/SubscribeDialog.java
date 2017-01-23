// AddResourceDialog.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.subscription;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.GenericObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildListPanel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.causality.DirectedGraphCausalityManager;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.AbstractFilterTreeSelectionModel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.util.DomeException;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubscribeDialog extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(400, 300);
	public static final GridBagConstraints gbc = null;

	protected DomeModelBuilder modelBuilder;
	protected DomeTree resourceTree;

	public static void show(Component parent, DomeModelBuilder model)
	{
		JDialog d = DialogFactory.createDialog(parent, model.getName() + ": subscribe to interfaces", new SubscribeDialog(model), true, true);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	/**
	 * Class used to subscribe to interfaces
	 */
	private SubscribeDialog(DomeModelBuilder model)
	{
		this.modelBuilder = model;
		ArrayList resourcesAndiModels = new ArrayList(model.getIntegrationProject().getResourceModels());

        //keep only external resources since i-models will be displayed as i-models
        List toRemove = new ArrayList();
        for (Iterator i = resourcesAndiModels.iterator(); i.hasNext();) {
            ProjectResourceInfo resource = (ProjectResourceInfo) i.next();
            if ((resource.getType()).equals("imodel"))
                toRemove.add(resource);
        }
        resourcesAndiModels.removeAll(toRemove);


		ArrayList iModels = new ArrayList(model.getIntegrationProject().getIntegrationModels());
		List tobeRemoved = new ArrayList();
		for (Iterator i = iModels.iterator(); i.hasNext();) {
			BuildProjectIntegrationModelInfo bm = (BuildProjectIntegrationModelInfo) i.next();
			Model m = bm.getModel();
			if (m.equals(model)) {
				tobeRemoved.add(bm);
                break;
		    }
		}
		iModels.removeAll(tobeRemoved);
		if (!iModels.isEmpty())
			resourcesAndiModels.addAll(iModels);

		resourceTree = new DomeTree(new GenericObjectTreeNode(resourcesAndiModels,
		                                                      ProjectBuildListPanel.resourcesTreeFactory), false);
		resourceTree.setSelectionModel(new InterfaceOnlyTreeSelectionModel());
		resourceTree.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				Object o = ((GenericObjectTreeNode) event.getPath().getLastPathComponent()).getObject();
				if (o instanceof BuildProjectResourceInfo) {
					((BuildProjectResourceInfo) o).loadResource();
				} else if (o instanceof BrowseInterface) {
					((BrowseInterface) o).loadInterface(true);
				}
			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}
		});
		JButton addButton = Templates.makeButton("add subscriptions to iModel",
		                                         new ActionListener()
		                                         {
			                                         public void actionPerformed(ActionEvent e)
			                                         {
				                                         try {
				                                            addSelectedSubscriptions();
					                                         dispose();
				                                         }
				                                         catch (DomeException domeEx) {
				                                         }
			                                         }
		                                         });
		JButton cancelButton = Templates.makeButton("cancel",
		                                            new ActionListener()
		                                            {
			                                            public void actionPerformed(ActionEvent e)
			                                            {
				                                            dispose();
			                                            }
		                                            });

		JComponent[] comps = {new JScrollPane(resourceTree), addButton, cancelButton};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
		setPreferredSize(DEFAULT_SIZE);
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}

	private void addSelectedSubscriptions()
	{
		String name = null;

		if (resourceTree.isSelectionEmpty())
			return;
		TreePath[] ifaceInfos = resourceTree.getSelectionPaths();
		for (int i = 0; i < ifaceInfos.length; i++) {
			Object o = ((GenericObjectTreeNode) ifaceInfos[i].getLastPathComponent()).getObject();
			try {
				if (o instanceof BrowseInterface) {
					BrowseInterface ifaceInfo = (BrowseInterface) o;
					name = ifaceInfo.getName();
					modelBuilder.subscribe(ifaceInfo.getServerConnection(), ifaceInfo.getInterface(),
					                       ifaceInfo.getInterfaceId(), ifaceInfo.getVersion(),
					                       ifaceInfo.getParentId());
				} else if (o instanceof ModelInterfaceBuilder) {
					ModelInterfaceBuilder mi = (ModelInterfaceBuilder) o;
					name = mi.getName();
					modelBuilder.subscribe(null, mi, mi.getId().getIdString(), mi.getVersion().getMajorVersion(),
					                       mi.getModel().getId().getIdString());
				}
			} catch (DomeException e) {
				// catch exception and display an error dialog
				if (e.getSource() instanceof DirectedGraphCausalityManager) {
					DirectedGraphCausalityManager causalityMgr;
					causalityMgr = (DirectedGraphCausalityManager) e.getSource();
					if (causalityMgr.getResults().isEmpty()) {
						OneButton1Msg.showError(null, "Causality error",
						                        "No causality defined for '" + name + "'",
						                        "OK", new Dimension(1, 1));
					}
					throw e;
				}
			}
		}
	}

	static class InterfaceOnlyTreeSelectionModel extends AbstractFilterTreeSelectionModel
	{
		protected boolean isValidSelectionPath(TreePath path)
		{
			Object obj = ((GenericObjectTreeNode) path.getLastPathComponent()).getObject();
			return (obj instanceof BrowseInterface || obj instanceof ModelInterfaceBuilder);
		}
	}

}
