// ModelInterfaceManagerBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.ModelComponentPanel;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ModelInterfaceManagerBuildPanel extends ModelComponentPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("ModelInterfaceManagerBuildPanel");
	public static final String XML_TAG = "modelinterfacesbuildpanel";
    public static final Dimension DEFAULT_SIZE = new Dimension(400, 250);

	protected static GridBagConstraints gbc;
	protected ModelInterfaceManagerBuilder miBuilder;
	protected String menuContext;
	BuildTreeTable treeTable;
	BuildTree tree;
	protected JScrollPane spane;
	protected JButton moveUpButton;
	protected JButton moveDownButton;

	public static DomeFrame createInterfacesTool(ModelInterfaceManagerBuilder mInterfaces, String menuContext)
	{
		return new DomeBuildFrame(new ModelInterfaceManagerBuildPanel(mInterfaces, menuContext));
	}

	public ModelInterfaceManagerBuildPanel(ModelInterfaceManagerBuilder mib, String menuContext)
	{
		super(mib, "Interfaces: " + mib.getModel().getName());
		this.miBuilder = mib;
		this.menuContext = menuContext;
		Model model = mib.getModel();     // model is IntegrationProjectBuilder
		if (model != null) {
			NameListener nameListener = new NameListener()
			{
				public void nameChanged(String newName)
				{
					Container cont = getTopLevelAncestor();
					((DomeBuildFrame) cont).setTitle("Interfaces: " + newName);
				}
			};
			model.addPropertyChangeListener(NameListener.NAME, nameListener);
		}

		String[] cols = new String[]{"name", "value"};
		int[] colWidths = new int[]{135, 130};

		tree = new BuildTree(miBuilder);
		treeTable = new BuildTreeTable(tree, cols.length, cols, colWidths, true, false);
		tree.addTreeSelectionListener(new InterfacesTreeListener());
		spane = new JScrollPane(treeTable);
		spane.getViewport().setBackground(Color.white);
		//Dimension d = treeTable.getPreferredSize();
		//spane.setPreferredSize(new Dimension(d.width, 210));
        spane.setPreferredSize(DEFAULT_SIZE);
		layoutComponents();
	}

	private void layoutComponents()
	{
		moveUpButton = Templates.makeListArrowButton("up", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedIndices = getSelectedIndices();
				if (selectedIndices == null)
					return;
				miBuilder.moveInterfacesUp(selectedIndices);
				// calculate new indices
				Arrays.sort(selectedIndices);
				int[] newIndices;
				if (selectedIndices[0] == 0) { // skip
					newIndices = new int[selectedIndices.length - 1];
					for (int j = 1; j < selectedIndices.length; ++j)
						newIndices[j - 1] = selectedIndices[j] - 1;
				} else { // shift all indices up
					newIndices = new int[selectedIndices.length];
					for (int j = 0; j < selectedIndices.length; ++j)
						newIndices[j] = selectedIndices[j] - 1;
				}
				tree.setSelectionRows(newIndices);
			}
		});

		moveDownButton = Templates.makeListArrowButton("down", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedIndices = getSelectedIndices();
				if (selectedIndices == null)
					return;
				miBuilder.moveInterfacesDown(selectedIndices);
				// calculate new indices
				Arrays.sort(selectedIndices);
				int[] newIndices;
				if (selectedIndices[selectedIndices.length - 1] == miBuilder.countInterfaces() - 1) { // skip
					newIndices = new int[selectedIndices.length - 1];
					for (int j = 0; j < (selectedIndices.length - 1); ++j)
						newIndices[j] = selectedIndices[j] + 1;
				} else { // shift all indices down
					newIndices = new int[selectedIndices.length];
					for (int j = 0; j < selectedIndices.length; ++j)
						newIndices[j] = selectedIndices[j] + 1;
				}
				tree.setSelectionRows(newIndices);
			}
		});

		//moveUpButton.setEnabled(false);
		//moveDownButton.setEnabled(false);
		manipulateShiftSupport();

		JButton closeButton = Templates.makeButton("close", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// should cleanup by removing DListListener
				dispose();
			}
		});

		JComponent[] comps = {
			spane,
			moveUpButton,
			moveDownButton,
			closeButton
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(0, 2, 3, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(0, 2, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(menuContext);
		BuildFocusTracker.notifyInFocus(this, miBuilder);
	}

	public void close()
	{
		// stop listening to interfaces list
	}

	private int[] getSelectedIndices()
	{
		if (tree.getSelectionCount() == 0)
			return null;
		int[] selectedIndices = new int[tree.getSelectionCount()];
		TreePath[] paths = tree.getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			selectedIndices[i] = getSelectedIndex(paths[i]);
		}
		return selectedIndices;
	}

	private int getSelectedIndex()
	{
		if (tree.getSelectionCount() == 0)
			return -1;
		return getSelectedIndex(tree.getSelectionPath());
	}

	private int getSelectedIndex(TreePath path)
	{
		if (path == null)
			return -1;

		int selectionIndex = 0;
		Object ifaceNode = path.getPathComponent(1);
		Collection ifaces = miBuilder.getInterfaces();
		for (Iterator iter = ifaces.iterator(); iter.hasNext(); selectionIndex++) {
			ModelInterface iface = (ModelInterface) iter.next();
			if (iface.equals(((BuildObjectTreeNode) ifaceNode).getDomeObject())) {
				break;
			}
		}

		return selectionIndex;
	}

	protected void newInterface()
	{
		if (tree.getSelectionCount() == 0 || tree.getSelectionCount() > 1) {
			// insert a new interface at the end
			miBuilder.newInterface();
		} else {
			// get the index of the currently selected interface node
			int selectionIndex = getSelectedIndex();
			if (selectionIndex >= 0) {
				// insert a new interface before the selected one
				miBuilder.newInterface(selectionIndex);
			}
		}
		manipulateShiftSupport();
	}

	protected void duplicateSelectedInterfaces()
	{
		int[] selectedIndices = getSelectedIndices();
		if (selectedIndices == null)
			return;
		miBuilder.duplicateInterfaces(selectedIndices);
		manipulateShiftSupport();
	}

	protected void deleteSelectedInterfaces()
	{
		int[] selectedIndices = getSelectedIndices();
		if (selectedIndices == null)
			return;
		miBuilder.deleteInterfaces(selectedIndices);
		manipulateShiftSupport();
	}

	protected void clearSelection()
	{
		tree.clearSelection();
		spane.requestFocus();
	}

	protected void selectAll()
	{
		tree.setSelectionInterval(0, miBuilder.countInterfaces() - 1);
	}

	protected void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}


	private void manipulateShiftSupport()
	{
		if (miBuilder.countInterfaces() > 1) {
			moveUpButton.setEnabled(true);
			moveDownButton.setEnabled(true);
		} else {
			moveUpButton.setEnabled(false);
			moveDownButton.setEnabled(false);
		}
	}

// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		/**
		 * Determines the component to use for the action.
		 * This if fetched from the source of the ActionEvent
		 * if it's not null and can be narrowed.  Otherwise,
		 * the last focused component is used.
		 *
		 * @param e the ActionEvent
		 * @return the component
		 */
		protected final ModelInterfaceManagerBuildPanel getModelInterfacesBuildPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof ModelInterfaceManagerBuildPanel) {
					return (ModelInterfaceManagerBuildPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof ModelInterfaceManagerBuildPanel)
				return (ModelInterfaceManagerBuildPanel) comp;
			throw new NullPointerException("No current ModelInterfaceManagerBuildPanel");
		}
	}

// --- actions for menus and buttons --------------------

	public static final AbstractAction newAction = new FocusTrackerAction("New")
	{
		public void actionPerformed(ActionEvent e)
		{
			getModelInterfacesBuildPanel(e).newInterface();
		}
	};

	public static final AbstractAction duplicateAction = new FocusTrackerAction("Duplicate")
	{
		public void actionPerformed(ActionEvent e)
		{
			getModelInterfacesBuildPanel(e).duplicateSelectedInterfaces();
		}
	};

	public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getModelInterfacesBuildPanel(e).clearSelection();
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all ")
	{
		public void actionPerformed(ActionEvent e)
		{
			getModelInterfacesBuildPanel(e).selectAll();
		}
	};

	public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			getModelInterfacesBuildPanel(e).deleteSelectedInterfaces();
		}
	};


	public static final JMenu menu = makeMenu();

	protected static JMenu makeMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Interfaces");
		m.add(MenuUtils.makeMenuItem(ModelInterfaceManagerBuildPanel.newAction));
		m.add(MenuUtils.makeMenuItem(ModelInterfaceManagerBuildPanel.duplicateAction));
		duplicateAction.setEnabled(false); //initially
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(ModelInterfaceManagerBuildPanel.clearSelectionAction));
		m.add(MenuUtils.makeMenuItem(ModelInterfaceManagerBuildPanel.selectAllAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(ModelInterfaceManagerBuildPanel.deleteAction));
		return m;
	}

	protected class InterfacesTreeListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent te)
		{
			TreePath path = te.getNewLeadSelectionPath();
			if (path != null) {
				Object obj = path.getLastPathComponent();
				if (obj != null && obj instanceof BuildObjectTreeNode) {
					DomeObject dobject = ((BuildObjectTreeNode) obj).getDomeObject();
					if (dobject instanceof ModelInterface) {
						//System.out.println("ENABLE DUPLICATE INTERFACE");
						ModelInterfaceManagerBuildPanel.duplicateAction.setEnabled(true);
					} else {
						//System.out.println("disable duplicate interface");
						ModelInterfaceManagerBuildPanel.duplicateAction.setEnabled(false);
					}
				}
			} else {
				//System.out.println("disable duplicate interface");
				ModelInterfaceManagerBuildPanel.duplicateAction.setEnabled(false);
			}
		}
	}
}
