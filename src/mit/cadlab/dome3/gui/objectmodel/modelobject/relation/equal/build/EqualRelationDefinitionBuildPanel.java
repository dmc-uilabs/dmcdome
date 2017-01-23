// ProceduralRelationDefinitionBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build;

import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.gui.objectmodel.model.dome.StandardViewBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.BuildEqualRelationTree;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.DataTypeComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.guiutils.ModelObjectsComboBoxListModel;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.config.Registry;

import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

public class EqualRelationDefinitionBuildPanel extends JPanel
{
	protected static GridBagConstraints gbc;
	protected static int[] columnWidths = {150, 150, 125};

	protected ConcreteEqualRelation relation;
	protected CardLayout2 relationViewsCards;
	protected JPanel relationViewsPanel;
	protected EqualRelationTreeBuilderPanel inputOutputViewPanel;
	protected EqualRelationTreeBuilderPanel modelCausalityViewPanel;
	protected DefaultComboBoxModel cbModel;
	protected JComboBox viewComboBox;
	protected DComboBox mapToInputComboBox;
	protected JButton listButton;
	protected DComboBox dataTypeComboBox;

	public EqualRelationDefinitionBuildPanel(ConcreteEqualRelation relation)
	{
		this.relation = relation;

		createComponents();

		ConnectionMappingManager mgr = ((AbstractDomeModel) relation.getModel()).getMappingManager();
		mgr.addMappingChangeListener(new MappingChangeListener()
		{
			public void mappingChanged(MappingChangeEvent event)
			{
				repaintMapToInputComboBox();
				dataTypeComboBox.setEnabled(!hasMappings());
			}

		});

	}

	protected ComboBoxModel makeDataTypeComboBoxModel()
	{

		List dataTypes = Registry.getDataObjectTypes();
		return new DefaultComboBoxModel(dataTypes.toArray()); // one, for now
	}

	protected void createComponents()
	{
		relationViewsCards = new CardLayout2();
		relationViewsPanel = new JPanel();
		relationViewsPanel.setLayout(relationViewsCards);
		inputOutputViewPanel = new EqualRelationTreeBuilderPanel(relation,
		                                                         EqualRelation.INPUT_OUTPUT_VIEW);
		inputOutputViewPanel.getTree().addTreeSelectionListener(new TreeSelectionListenerForComboBox());
		relationViewsPanel.add(EqualRelation.INPUT_OUTPUT_VIEW, inputOutputViewPanel);
		modelCausalityViewPanel = new EqualRelationTreeBuilderPanel(relation,
		                                                            EqualRelation.MODEL_CAUSALITY_VIEW);
		modelCausalityViewPanel.getTree().addTreeSelectionListener(new TreeSelectionListenerForComboBox());

		relationViewsPanel.add(EqualRelation.MODEL_CAUSALITY_VIEW, modelCausalityViewPanel);


		layoutComponent();


	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), relationViewsPanel, makeMapToInputPanel()};
		//JComponent[] comps = {makeControlPanel(), relationViewsPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)

		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}


	protected JPanel makeMapToInputPanel()
	{
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Map to input:");

		mapToInputComboBox = Templates.makeDComboBox(makeComboBoxModelForMapToInput());
		mapToInputComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				switchInput();
			}
		});
		mapToInputComboBox.setPreferredSize(viewComboBox.getPreferredSize());
		setSelectedObjectForMapComboBox();


		//JPanel fillerPanel = new JPanel();
		//Templates.setFixedSize(fillerPanel, listButton.getPreferredSize());

		//JComponent[] comps = {label, mapToInputComboBox, fillerPanel};
		JComponent[] comps = {label, mapToInputComboBox};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			//new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0),

		};
		Templates.layoutGridBagB(panel, comps, gbcs);
		return panel;
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		cbModel = new DefaultComboBoxModel(relation.getViewNames().toArray());
		viewComboBox = Templates.makeComboBox(cbModel);
		viewComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				switchView();
			}
		});


		// filler panel size of button
		listButton = Templates.makeListArrowButton("up");
		//JPanel fillerPanel = new JPanel();
		//Templates.setFixedSize(fillerPanel, listButton.getPreferredSize());
		dataTypeComboBox = Templates.makeDComboBox(makeDataTypeComboBoxModel());
		dataTypeComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				if (hasMappings()) return;

				//other vise change the type of datatype combobox
				String newType = (String) dataTypeComboBox.getSelectedItem();

				EqualRelationTreeBuilderPanel view = (EqualRelationTreeBuilderPanel) relationViewsCards.getActiveComponent();
				int selectionCount = (view.getTree()).getSelectionCount();
				if (selectionCount != 1) return;

				DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) (view.getTree()).getSelectionPath().getLastPathComponent();
				DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
				if (dObj instanceof Filter) return;

				if (newType.equals(((Parameter) dObj).getCurrentType())) return;

				switchDataType(newType);

			}
		});
		dataTypeComboBox.setPreferredSize(viewComboBox.getPreferredSize());
		dataTypeComboBox.setSelectedItem(((Parameter) relation.getRHS()).getCurrentType());
		dataTypeComboBox.setEnabled(false);

		JComponent[] comps1 = {new JPanel(),
		                       viewComboBox,
		                       dataTypeComboBox,

		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs1 = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps1, gbcs1);
		return p;
	}


	protected ComboBoxModel makeComboBoxModelForMapToInput()
	{
		//get mapped things out
		ConnectionMappingManager mgr = ((AbstractDomeModel) relation.getModel()).getMappingManager();
		Collection MapsForRHS = mgr.getMappingsForParameter((Parameter) relation.getRHS());
		Collection MapsForLHS = mgr.getMappingsForParameter((Parameter) relation.getLHS());

		ArrayList items = new ArrayList();
		for (Iterator iter = MapsForRHS.iterator(); iter.hasNext();) {
			items.add(iter.next());
		}
		for (Iterator iter = MapsForLHS.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (!items.contains(obj))
				items.add(obj);
		}

		return new ModelObjectsComboBoxListModel(items);
	}

	protected void setSelectedObjectForMapComboBox()
	{
		ConnectionMappingManager mgr = ((AbstractDomeModel) relation.getModel()).getMappingManager();
		Collection MapsForRHS = mgr.getMappingsForParameter((Parameter) relation.getRHS());

		if (MapsForRHS.size() == 0) {
			mapToInputComboBox.setSelectedIndex(-1);
		} else {
			mapToInputComboBox.setSelectedIndex(0);
		}
	}

	public void setMenuContext()
	{
		MenuManager.setContext(getMenuContext());
		JComponent comp = (JComponent) relationViewsCards.getActiveComponent();
		BuildFocusTracker.notifyInFocus(comp, (ModelObject) relation);
		if (comp.equals(inputOutputViewPanel))
			inputOutputViewPanel.setEditMenusForSelection();
	}

	protected String getMenuContext()
	{
		//not exist in plugin model
		String currentView = relationViewsCards.getActiveName();
		if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
			//setting the projext menu
			if (currentView.equals(EqualRelation.INPUT_OUTPUT_VIEW)) {
				return ModeContexts.BUILD_PROJECT_EQUALRELATION_DEFINITION;
			} else if (currentView.equals(EqualRelation.MODEL_CAUSALITY_VIEW)) {
				return ModeContexts.BUILD_PROJECT_EQUALRELATION_DEFINITION;
			} else {
				return ModeContexts.BUILD_PROJECT_STANDARD_VIEW;
			}
		} else {
			if (currentView.equals(EqualRelation.INPUT_OUTPUT_VIEW)) {
				return ModeContexts.BUILD_EQUALRELATION_DEFINITION;
			} else if (currentView.equals(EqualRelation.MODEL_CAUSALITY_VIEW)) {
				return ModeContexts.BUILD_EQUALRELATION_DEFINITION;
			} else {
				return ModeContexts.BUILD_STANDARD_VIEW;
			}
		}
	}

	protected void switchView()
	{
		String newView = cbModel.getSelectedItem().toString();
		relationViewsCards.show(relationViewsPanel, newView);
		setMenuContext(); // do this before the next line!
		synchronizeViewControls(); // needs correct menu showing
	}

	protected void switchDataType(String newType)
	{
		((Parameter) relation.getRHS()).setDataTypeSelection(new Parameter.DataTypeSelection(newType));
		((Parameter) relation.getLHS()).setDataTypeSelection(new Parameter.DataTypeSelection(newType));

	}


	protected void switchInput()
	{

		if (mapToInputComboBox.getSelectedIndex() == -1) return;

		Object selectedInput = mapToInputComboBox.getSelectedItem();

		ConnectionMappingManager mgr = ((AbstractDomeModel) relation.getModel()).getMappingManager();
		Collection MapsForRHS = mgr.getMappingsForParameter((Parameter) relation.getRHS());
		Collection MapsForLHS = mgr.getMappingsForParameter((Parameter) relation.getLHS());

		//should only have one
		if (MapsForRHS.size() == 0) {
			//nothing mapped
			//check if it is in MapsForLHS
			for (Iterator iter = MapsForLHS.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (selectedInput.equals(obj)) {
					mgr.removeMapping((Parameter) obj, (Parameter) relation.getLHS());
					break;
				}
			}
			//add to RHS mapping

			mgr.addMapping((Parameter) selectedInput, (Parameter) relation.getRHS());
		} else if (MapsForRHS.size() == 1) {
			//check if it's same
			if (MapsForRHS.contains(selectedInput)) return;
			//if not ,remove and map new
			for (Iterator iter = MapsForLHS.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (selectedInput.equals(obj)) {
					mgr.removeMapping((Parameter) obj, (Parameter) relation.getLHS());
					break;
				}
			}
			Object oldMap = MapsForRHS.iterator().next();
			mgr.removeMapping((Parameter) oldMap, (Parameter) relation.getRHS());
			mgr.addMapping((Parameter) oldMap, (Parameter) relation.getLHS());

			mgr.addMapping((Parameter) selectedInput, (Parameter) relation.getRHS());

		} else {
			//error
			System.err.println("RHS has multiple mappings");
		}


	}


	protected void synchronizeViewControls()
	{
		String currentView = relationViewsCards.getActiveName();
		if (currentView.equals(EqualRelation.INPUT_OUTPUT_VIEW)) {
			inputOutputViewPanel.setEditMenusForSelection();
		}
		if (currentView.equals(EqualRelation.MODEL_CAUSALITY_VIEW)) {
			modelCausalityViewPanel.setEditMenusForSelection();
		}
	}

	public void repaintMapToInputComboBox()
	{
		mapToInputComboBox.setModel(makeComboBoxModelForMapToInput());
		setSelectedObjectForMapComboBox();
	}

	private boolean hasMappings()
	{
		ConnectionMappingManager mgr = ((AbstractDomeModel) relation.getModel()).getMappingManager();
		Collection MapsForRHS = mgr.getMappingsForParameter((Parameter) relation.getRHS());
		Collection MapsForLHS = mgr.getMappingsForParameter((Parameter) relation.getLHS());

		return !(MapsForRHS.size() == 0 && MapsForLHS.size() == 0);
	}

	public void setComboBoxForSelection(Parameter p)
	{

		dataTypeComboBox.setSelectedObject(p.getCurrentType());


	}


	class TreeSelectionListenerForComboBox implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			Object tree = e.getSource();
			if (tree instanceof BuildEqualRelationTree) {

				int count = ((BuildEqualRelationTree) tree).getSelectionCount();
				int[] selectedRows = ((BuildEqualRelationTree) tree).getSelectionRows();
				if ((selectedRows == null && count != 0) ||
				        (selectedRows != null && (selectedRows.length != count))) {
					((BuildEqualRelationTree) tree).clearSelection();
					return;
				}
				int selectionCount = ((BuildEqualRelationTree) tree).getSelectionCount();
				if (selectionCount == 0) {
					//do nothing
				} else if (selectionCount == 1) {
					DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) ((BuildEqualRelationTree) tree).getSelectionPath().getLastPathComponent();
					DomeObject dObj = ((DomeTreeObject) selectedNode.getTreeObject()).getDomeObject();
					if (dObj instanceof Parameter && !(dObj instanceof Filter)) {
						dataTypeComboBox.setEnabled(!hasMappings());
						setComboBoxForSelection((Parameter) dObj);
					} else {
						dataTypeComboBox.setEnabled(false);
						//dataTypeComboBox.setSelectedIndex(-1);
					}
				} else {
					//dataTypeComboBox.setSelectedIndex(-1);
				}
			}
		}
	}
}
