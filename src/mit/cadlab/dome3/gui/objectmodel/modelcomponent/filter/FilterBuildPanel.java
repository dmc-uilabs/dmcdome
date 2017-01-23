// FilterBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.plugin.PluginModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.TreePath;

public class FilterBuildPanel extends AbstractDomeObjectGui
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("FilterBuildPanel");
	public static final String XML_TAG = "filterbuildpanel";

	protected static GridBagConstraints gbc;

	protected NameTextField nameField;
	protected Filter filter;
	protected BuildTree m_tree;
	//protected BuildTree inputOutputTree;
	//protected BuildTree modelCausalityTree;
	protected JPanel filterViewsPanel;
	protected CardLayout2 filterViewsCards;
	protected DefaultComboBoxModel cbModel;
	protected JComboBox viewComboBox;

	public FilterBuildPanel(Filter filter)
	{
		super(filter);
		if (filter == null)
			throw new NullPointerException("FilterBuildPanel - null filter");
		this.filter = filter;
		//createComponents ();
		layoutComponent();
	}

	public String getTitlePrefix()
	{
		return "Filter: ";
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		if (filter.getModel() instanceof PluginModel)
			MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
		else
			MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
		BuildFocusTracker.notifyInFocus(this, filter);
	}

	/* for MultiViewSupport
	protected void createComponents()
	{
		BuildTreeTable inputOutputTreeTable;
		BuildTreeTable modelCausalityTreeTable;

		filterViewsCards = new CardLayout2();
		filterViewsPanel = new JPanel();
		filterViewsPanel.setLayout(filterViewsCards);

		inputOutputTree = new BuildTree(filter, Filter.INPUT_OUTPUT_VIEW);
		inputOutputTreeTable = new BuildTreeTable(inputOutputTree);
		JScrollPane scrollPane = new JScrollPane(inputOutputTreeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = inputOutputTreeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		filterViewsPanel.add(Filter.INPUT_OUTPUT_VIEW, scrollPane);

		modelCausalityTree = new BuildTree(filter, Filter.MODEL_CAUSALITY_VIEW);
		modelCausalityTreeTable = new BuildTreeTable(modelCausalityTree);
		scrollPane = new JScrollPane(modelCausalityTreeTable);
		scrollPane.getViewport().setBackground(Color.white);
		d = modelCausalityTreeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		filterViewsPanel.add(Filter.MODEL_CAUSALITY_VIEW, scrollPane);
	}
	*/

	protected void layoutComponent()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(filter);
		nameField.setEditable(false);
		nameField.setCurrent();

		m_tree = new BuildTree(filter);
		BuildTreeTable treeTable = new BuildTreeTable(m_tree);
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));

		JComponent[] comps = {Templates.makeLabel("name:"),
		                      nameField,
		                      //makeControlPanel(),
		                      //filterViewsPanel};
		                      scrollPane};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			//new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	/* for MultiViewSupport
	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		cbModel = new DefaultComboBoxModel(((AbstractFilter)filter).getViewNames().toArray());
		viewComboBox = Templates.makeComboBox(cbModel);
		viewComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				switchView();
			}
		});

		// filler panel size of button
		JButton listButton = Templates.makeListArrowButton("up");
		JPanel fillerPanel = new JPanel();
		Templates.setFixedSize(fillerPanel, listButton.getPreferredSize());

		JComponent[] comps = {new JPanel(),
		                      viewComboBox,
		                      fillerPanel
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}
	*/

	public void copySelectedModelObjects()
	{
		List selectedObjects = getSelectedModelObjects();
		if (selectedObjects != null)
			BuildMode.clipboard.addSelection(selectedObjects);
	}

	public void deleteSelectedModelObjects()
	{
		/* for MultiViewSupport
		String currentView = getView ();
		if (currentView.equals(Filter.INPUT_OUTPUT_VIEW)) {
			inputOutputTree.stopEditing();
		}
		else
		if (currentView.equals(Filter.MODEL_CAUSALITY_VIEW)) {
			modelCausalityTree.stopEditing();
		}
		*/
		m_tree.stopEditing();
		List selectedObjects = getSelectedModelObjects();
		if (selectedObjects != null) {
			ModelObject mObj = (ModelObject) selectedObjects.get(0);
			mObj.getModel().deleteModelObjects(selectedObjects);
		}
	}

	protected List getSelectedModelObjects()
	{
		//BuildTree tree = getTree ();
		if (m_tree.isSelectionEmpty() || m_tree.getSelectionCount() < 1) return null;
		int[] selectionRows = m_tree.getSelectionRows();
		Arrays.sort(selectionRows);
		List selectedModelObjects = new ArrayList();
		for (int i = 0; i < selectionRows.length; ++i) {
			TreePath selectedPath = m_tree.getPathForRow(selectionRows[i]);
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
			DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
			if ((dObj instanceof ModelObject) && !selectedModelObjects.contains(dObj))
				selectedModelObjects.add(dObj);
		}
		return selectedModelObjects;
	}

	public void clearSelection()
	{
		m_tree.clearSelection();
		//getTree().clearSelection();
	}

	public void selectAll()
	{
		m_tree.selectAllVisibleRows();
		//getTree().selectAllVisibleRows();
	}

	/* for MultiViewSupport
	protected BuildTree getTree ()
	{
		String currentView = getView ();
		if (currentView.equals(Filter.INPUT_OUTPUT_VIEW)) {
			return inputOutputTree;
		}
		else
		if (currentView.equals(Filter.MODEL_CAUSALITY_VIEW)) {
			return modelCausalityTree;
		}
		else
			return null;
	}

	protected String getView ()
	{
		return filterViewsCards.getActiveName();
	}

	protected void switchView() {
	     String newView = cbModel.getSelectedItem().toString();
	     filterViewsCards.show(filterViewsPanel, newView);
	     setMenuContext(); // do this before the next line!
	     synchronizeViewControls(); // needs correct menu showing
	 }

	protected void synchronizeViewControls()
	{
		String currentView = getView ();
		if (currentView.equals(Filter.INPUT_OUTPUT_VIEW)) {
			//inputOutputViewPanel.setEditMenusForSelection();
		}
	}
	*/

	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final FilterBuildPanel getFilterBuildPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof FilterBuildPanel) {
					return (FilterBuildPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof FilterBuildPanel)
				return (FilterBuildPanel) comp;
			throw new NullPointerException("No current FilterBuildPanel");
		}
	}

	// --- actions for menus and buttons --------------------

	public static final AbstractAction copyAction = new FocusTrackerAction("Copy")
	{
		public void actionPerformed(ActionEvent e)
		{
			getFilterBuildPanel(e).copySelectedModelObjects();
		}
	};

	public static final AbstractAction clearAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getFilterBuildPanel(e).clearSelection();
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all")
	{
		public void actionPerformed(ActionEvent e)
		{
			getFilterBuildPanel(e).selectAll();
		}
	};

	public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			getFilterBuildPanel(e).deleteSelectedModelObjects();
		}
	};

	public static final JMenu menu = makeMenu();

	protected static JMenu makeMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Edit filter");
		m.add(MenuUtils.makeMenuItem(FilterBuildPanel.copyAction));
		m.add(MenuUtils.makeMenuItem(FilterBuildPanel.clearAction));
		m.add(MenuUtils.makeMenuItem(FilterBuildPanel.selectAllAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(FilterBuildPanel.deleteAction));
		return m;
	}

}
