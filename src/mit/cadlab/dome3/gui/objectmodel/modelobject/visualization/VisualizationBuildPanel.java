// VisualizationBuildPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;


import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;

import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton3Msg;
import mit.cadlab.dome3.objectmodel.DomeObject;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.*;

import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.plugin.PluginModel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

import org.jfree.chart.JFreeChart;

/**
 *
 */
public class VisualizationBuildPanel extends VisualizationBasePanel
{


	public VisualizationBuildPanel(Visualization vis)
	{
		super(vis);
	}

	protected ComboBoxModel makeChartTypeComboBoxModel()
	{
		// return new DataTypeComboBoxModel(dataModel.getCurrentType(), true); // one, for now
		return new DefaultComboBoxModel(dataModel.getValidVisTypes());
	}

	protected ComboBoxModel makeChartTypeSubComboBoxModel()
	{
		// return new DataTypeComboBoxModel(dataModel.getCurrentType(), true); // one, for now
		return new DefaultComboBoxModel(dataModel.getValidVisTypesForCurrentVisType());
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new GenericVisualizationPropertyChangeListener();
	}


	protected void configureComponents()
	{
		nameField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dataModel.setName(nameField.getText());
			}
		});

		if(dataModel.getSelectedSetIndex() == -1) valuePanel = new ChartVisualizationPanel(VisualizationUtils.createChart(null, dataModel.getCurrentVisType(), dataModel.getCurrentVisSubType(), dataModel.isVertical()), this); // build
        else {
            JFreeChart newchart = VisualizationUtils.createChart(dataModel.getSelectedSet(),dataModel.getCurrentVisType(), dataModel.getCurrentVisSubType(), dataModel.isVertical());
            if(dataModel.getSelectedSet().getJchartProperties() != null) {
                VisualizationUtils.setChartProperties(newchart,dataModel.getSelectedSet().getJchartProperties(),dataModel.getSelectedSet());
            }
            valuePanel = new ChartVisualizationPanel(newchart, this); // build
        }
		JPanel firstPanel = new JPanel();

		ButtonGroup orientGroup = new ButtonGroup();
		orientGroup.add(verticalButton);
		orientGroup.add(horizontalButton);

		JComponent[] dComps = {valuePanel, verticalButton, horizontalButton, chartTypeSubCombBox};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 5, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)};
		Templates.layoutGridBag(firstPanel, dComps, dGbcs);

		//**if (dataModel.getChart() != null) valuePanel.setChart(dataModel.getChart());
		//loadChart();
		docPanel = new DocumentationBuildPanel(dataModel.getDocumentation());
		//**dataPanel = new DataPanel(dataModel);


		//**editPanel = new EditPanel(this);
		//contentTabs.addTab("visualization", valuePanel);
		contentTabs.addTab("visualization", firstPanel);

		//**contentTabs.addTab("config datasets", dataPanel);
		//**contentTabs.addTab("edit datasets", editPanel);
		contentTabs.addTab("documentation", docPanel);
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				MenuManager.setContext(getMenuContext());
				//
		/**		if (!(contentTabs.getSelectedIndex() == 2)) {
					//System.out.println("press other tabs");
					editPanel.okButton_clicked();
				}     **/
			}
		});
		chartTypeComboBox.setSelectedItem(dataModel.getCurrentVisType());
		/**chartTypeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String choice = (String) chartTypeComboBox.getSelectedItem();
				if (choice.equals(dataModel.getCurrentVisType()))
					return;
				else
					dataModel.setCurrentVisType(choice);
			}
		});**/

		chartTypeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String choice = (String) chartTypeComboBox.getSelectedItem();
				if (choice.equals(dataModel.getCurrentVisType()))
					return;
				else {
					dataModel.setCurrentVisType(choice);
					chartTypeSubCombBox.setModel(makeChartTypeSubComboBoxModel());
					chartTypeSubCombBox.repaint();

					dataModel.setCurrentVisSubType((String) chartTypeSubCombBox.getSelectedItem());
				}

			}
		});
		chartTypeSubCombBox.setSelectedItem(dataModel.getCurrentVisSubType());
		chartTypeSubCombBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String choice = (String) chartTypeSubCombBox.getSelectedItem();
				if (choice.equals(dataModel.getCurrentVisSubType()))
					return;
				else
					dataModel.setCurrentVisSubType(choice);
			}
		});
		constantCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				dataModel.setConstant(e.getStateChange() == ItemEvent.SELECTED);
				chartTypeComboBox.setEnabled(!dataModel.isConstant());
				chartTypeSubCombBox.setEnabled(!dataModel.isConstant());
			}
		});

		verticalButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                if(dataModel.isVertical() != verticalButton.isSelected())  dataModel.setVertical(verticalButton.isSelected());
			}
		});

		horizontalButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                 if(dataModel.isVertical() == horizontalButton.isSelected()) dataModel.setVertical(!horizontalButton.isSelected());
			}
		});

	}


	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null) {
			//set project menu
			MenuManager.setContext(ModeContexts.BUILD_PROJECT_VISUALIZATION);
		} else if (dataModel.getModel() instanceof PluginModel)
			MenuManager.setContext(ModeContexts.BUILD_PLUGIN_VISUALIZATION);
		else
			MenuManager.setContext(ModeContexts.BUILD_VISUALIZATION);
		BuildFocusTracker.notifyInFocus(this, ((Visualization) this.getDomeObject()).getModel());
		this.refreshViewSetMenu();
	}

	public void updateTypeComboBox()    //**********some problem***************//
	{
		//**chartTypeComboBox.setSelectedItem(dataModel.getValidVisTypes());
		chartTypeComboBox.setSelectedItem(dataModel.getCurrentVisType());
		chartTypeSubCombBox.setSelectedItem(dataModel.getCurrentVisSubType());
	}

	protected String getMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
			case 0: // visualization
				if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null)
					return ModeContexts.BUILD_PROJECT_VISUALIZATION;
				else
					return ModeContexts.BUILD_VISUALIZATION;

			case 1: // documentation
				if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null)
					return ModeContexts.BUILD_PROJECT_DOCUMENTATION;
				else
					return ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION;
/*			case 1: // edit
				if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null)
					return ModeContexts.BUILD_PROJECT_VISUALIZATION;
				else
					return ModeContexts.BUILD_VISUALIZATION;
			case 2: // data
				if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null)
					return ModeContexts.BUILD_PROJECT_VISUALIZATION;
				else
					return ModeContexts.BUILD_VISUALIZATION;
			case 3: // documentation
				if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null)
					return ModeContexts.BUILD_PROJECT_DOCUMENTATION;
				else
					return ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION;
			*/
			default:
				if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null)
					return ModeContexts.BUILD_PROJECT_VISUALIZATION;
				else
					return ModeContexts.BUILD_VISUALIZATION;
		}
	}

	public void showDataDialog()
	{
		// DataPanel.makeDataDialog((Visualization)getDomeObject()).show();
		//**contentTabs.setSelectedIndex(2);
		JDialog dialog = DialogFactory.createDialog(this, "Setup Data: "+ this.getDomeObject().getName(), new DataPanel(dataModel), true, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.show();

	}

	public void showVis()
	{
		//  if(dataModel.getSetsList().size()>0)
		//        if(dataModel.getSelectedSetIndex()==-1) dataModel.setSelectedSet(0);
		contentTabs.setSelectedIndex(0);
	}

	public void pasteCopyLastSelection()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		pasteCopies(sel.getItems());
	}


	public void pasteCopyFromClipBoard()
	{
		ClipboardSelection[] selections = ClipboardViewer.showSelectionDialog(this, BuildMode.clipboard);
		if (selections == null) return; // nothing selected in clipboard!
		ArrayList allSelections = new ArrayList(); // items can be repeated
		for (int i = 0; i < selections.length; ++i)
			allSelections.addAll(selections[i].getItems());
		pasteCopies(allSelections);

	}


	protected void pasteCopies(List items)
	{
		Visualization vis = (Visualization) getDomeObject();
		items = filterForValidItems(items);
		vis.addToAvailabelList(items);
	}

	/**
	 *  customized to take in DomeVector only, for visualization
	 * @param items
	 * @return
	 */
	protected List filterForValidItems(List items)
	{
		ArrayList validItems = new ArrayList();
		ArrayList invalidItems = new ArrayList();
		Iterator it = items.iterator();

		while (it.hasNext()) {
			Object obj = it.next();
			if ((obj instanceof Parameter) && ((((Parameter) obj).getDataObjectForType("Vector") != null)
			        || (((Parameter) obj).getDataObjectForType("Matrix") != null)
			        || (((Parameter) obj).getDataObjectForType("Preference") != null)
			        ) ) {
				validItems.add(obj);

			} else
				invalidItems.add(obj);
		}
		if (!invalidItems.isEmpty()) {
			DefaultContextBuilder.showWarning(this, "cannot be pasted into", getNamesOfChildren(invalidItems)
			);
		}

		return validItems;
	}

	protected String getNamesOfChildren(List children)
	{
		if (children == null || children.size() == 0) return "";
		if (children.size() == 1) {
			return getObjName(children.get(0));
		}
		if (children.size() == 2)
			return getObjName(children.get(0)) +
			        " and " + getObjName(children.get(1));
		// 3 or more objects
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < children.size() - 1; ++i) {
			sb.append(getObjName(children.get(i)) + ", ");
		}
		sb.append("and " + getObjName(children.get(children.size() - 1)));
		return sb.toString();
	}

	protected String getObjName(Object obj)
	{
		if (obj instanceof DomeObject)
			return ((DomeObject) obj).getName();
		return obj.toString();
	}


	protected void editSets()
	{

		JDialog dialog = DialogFactory.createDialog(this, "Edit Data For Data Set: "+ this.getDomeObject().getName(), new DataPanel(dataModel), true, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.show();
		//show edit panel
		//**contentTabs.setSelectedIndex(1);
		//enable paste menu

	}

	//** NOTICE!!!!!!!!!!!!!!!!!
	public void refreshAccordingToDomeObjectSet(DomeObjectSet dos, String changeoption)
	{

		dataPanel.repaintToSetChange(changeoption);
		editPanel.repaintToSetChange(changeoption);
		//if this is the selected dos,
		//if setchange (size,order, etc)refresh dataPanel & editPanel ,refresh chart
		//if set inner selection change  refresh dataPanel ,refresh chart
		//if seriesname change refresh chart only
		if (dos.equals(dataModel.getSelectedSet())) {
			valuePanel.repaintToSetChange();
		}
	}


	protected ImageIcon icon = DomeIcons.getIcon(DomeIcons.CHECKED);
	protected ImageIcon icon2 = DomeIcons.getIcon(DomeIcons.UNCHECKED);

	public void refreshViewSetMenu()
	{
		JMenu menu = VisualizationBuildMenus.menus.getViewSeriesMenu();
		//clear first
		if (menu.getItemCount() > 0)
			menu.removeAll();

		//add into it
		DomeObjectSet[] sets = (DomeObjectSet[]) dataModel.getSetsList().toArray(new DomeObjectSet[]{});
		for (int i = 0; i < sets.length; i++) {
			sets[i].addPropertyChangeListener(new DomeObjectSetPropertyChangeListener());
            JMenu setMenu = MenuUtils.makeMenu(sets[i].getName());

			if (dataModel.getSelectedSet() != sets[i]) {
				setMenu.setIcon(icon2);
			} else
				setMenu.setIcon(icon);
			setMenu.setHorizontalTextPosition(SwingConstants.RIGHT);
			setMenu.addMouseListener(new VisualizationBuildPanel.checkMenuItemMouseAdaptor(setMenu, sets[i]));

			addSubMenuItem(setMenu, sets[i]);
			menu.add(setMenu);
		}

	}

	public void addSubMenuItem(JMenu menu, DomeObjectSet set)
	{

		for (int i = 0; i < set.getSeriesSize() ; i++) {
           if(i==set.getHorizontalIndex() && dataModel.getCurrentVisType().equals(Visualization.XYCHART )){

           } else
           {
			JMenuItem subMenu = MenuUtils.makeMenuItem(set.getSeriesAlias(i));

			if (set.isSelected(i)) {
				subMenu.setIcon(icon);
			} else
				subMenu.setIcon(icon2);

			subMenu.setHorizontalTextPosition(SwingConstants.RIGHT);
			subMenu.addMouseListener(new VisualizationBuildPanel.checkMenuSubItemMouseAdaptor(subMenu, set, i));

			menu.add(subMenu);
           }
		}


	}

	/**
	 * to repaint icon
	 */
	public void refreshIconInViewSetMenu()
	{
		JMenu menu = VisualizationBuildMenus.menus.getViewSeriesMenu();
		DomeObjectSet[] sets = (DomeObjectSet[]) dataModel.getSetsList().toArray(new DomeObjectSet[]{});
		if (menu.getItemCount() == 0) return;
		if (menu.getItemCount() != sets.length) System.err.println(menu.getItemCount() + "!=" + sets.length);
		for (int i = 0; i < menu.getItemCount(); i++) {
			JMenu setMenu = (JMenu) menu.getItem(i);
			if (sets[i] == dataModel.getSelectedSet()) {
				if (!(setMenu.getIcon() == icon))
					setMenu.setIcon(icon);
			} else {
				if (!(setMenu.getIcon() == icon2))
					setMenu.setIcon(icon2);
			}

/**			for (int j = 0; j < setMenu.getItemCount(); j++) {
				JMenuItem subMenu = setMenu.getItem(j);
				if (sets[i].isSelected(j)) {
					if (!(subMenu.getIcon() == icon))
						subMenu.setIcon(icon);
				} else {
					if (!(subMenu.getIcon() == icon2))
						subMenu.setIcon(icon2);
				}
			}
 **/
		}


	}

	class checkMenuItemMouseAdaptor extends MouseAdapter
	{
		JMenu setMenu;
		DomeObjectSet set;

		public checkMenuItemMouseAdaptor(JMenu sm, DomeObjectSet s)
		{
			super();
			setMenu = sm;
			set = s;
		}

		public void mousePressed(MouseEvent e)
		{
			setMenu.setPopupMenuVisible(false);
			if (setMenu.getIcon() == icon2) {
				//check
				setMenu.setIcon(icon);
				dataModel.setSelectedSet(dataModel.getSetsList().indexOf(set));
			} else if (setMenu.getIcon() == icon) {
				//uncheck
				setMenu.setIcon(icon2);
				if (dataModel.getSelectedSet() == set)
					dataModel.setSelectedSet(-1);
			}


		}
	}

	class checkMenuSubItemMouseAdaptor extends MouseAdapter
	{
		JMenuItem subMenu;
		DomeObjectSet set;
		int index;

		public checkMenuSubItemMouseAdaptor(JMenuItem sm, DomeObjectSet s, int i)
		{
			super();
			subMenu = sm;
			set = s;
			index = i;

		}

		public void mousePressed(MouseEvent e)
		{

			if (subMenu.getIcon() == icon2) {
				//check
				subMenu.setIcon(icon);
				set.setSeriesSelected(index, true);
			} else if (subMenu.getIcon() == icon) {
				//uncheck
				subMenu.setIcon(icon2);
				set.setSeriesSelected(index, false);
			}


		}
	}


	class GenericVisualizationPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			Object obj = e.getSource();
			if (property.equals(Visualization.NAME)) {
				//System.out.println("Visulaization panel gets name change");
				setName((String) newValue);
				nameField.setCurrent();
				//repaint build and config panel
				//editPanel.repaint();
				//dataPanel.repaint();
			} else if (property.equals(Visualization.CHARTTYPESELECTION)) {
				//reformate chart;
				String newType = (String) ((Visualization)obj).getCurrentVisType();
				setCurrentType(newType);
				String newSubType = (String) ((Visualization)obj).getCurrentVisSubType();
				setCurrentSubType(newSubType);
				refreshViewSetMenu();
				//chartpanel handle from its own propertylistener
				//valuePanel.showChart();
			} else if (property.equals(Visualization.VERTICAL)) {
				setVertical(((Boolean) newValue).booleanValue());
			} else if (property.equals(Visualization.DATACHANGED )) {

				refreshViewSetMenu();
				//**refreshIconInViewSetMenu();

				//chartpanel handle from its own propertylistener
				// valuePanel.showChart(dataModel.getCurrentVisType());

			} else if (property.equals(Visualization.SETSCHANGED)) {

				refreshViewSetMenu();
				//chartpanel handle from its own propertylistener
				// valuePanel.showChart(dataModel.getCurrentVisType());

			} else if (property.equals(Visualization.SETSELECTIONCHANGED)) {

				refreshIconInViewSetMenu();

				//chartpanel handle from its own propertylistener
				// valuePanel.showChart(dataModel.getCurrentVisType());

			} else if (property.equals(Visualization.CONSTANT)) {
				setConstant(((Boolean) newValue).booleanValue());
			} else if (property.equals(Visualization.INVALIDVECTORCHANGE) || property.equals(Visualization.INVALIDMATRIXCHANGE)) {
				DomeObject dobj = (DomeObject) newValue;


				//pops up error message
				OneButton3Msg.showWarning(valuePanel, "Visualization Data", "has changed its structure \nTherefore, it has been removed \nfrom datasets in the visualization", dobj.getName(), dataModel.getName(), "Ok", OneButton3Msg.DEFAULT_SIZE);

				//remove that object from set  (not remove from available set
				System.out.println(dobj.toString());
				((ConcreteVisualization) dataModel).removeFromSet(dobj);

			}


		}
	}

    class DomeObjectSetPropertyChangeListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String property = e.getPropertyName();
            Object newValue = e.getNewValue();
            if (property.equals(DomeObjectSet.SELECTIONCHANGED)) {
                valuePanel.repaintToSetChange();
            }
        }
    }
	// --- focus tracking support --------------------
	/**
	 * this function is to track the current gui window you r working with
	 */
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final VisualizationBuildPanel getVisualizationBuildPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof VisualizationBuildPanel) {
					return (VisualizationBuildPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof VisualizationBuildPanel)
				return (VisualizationBuildPanel) comp;
			/**/System.err.println("No current VisualizationBuildPanel");
			throw new NullPointerException("No current VisualizationBuildPanel");
		}

		protected final VisualizationBuildPanel getVisualizationBuildPanel(ActionEvent e, boolean isRun)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof VisualizationBuildPanel) {
					return (VisualizationBuildPanel) o;
				}
			}
			JComponent comp = RunFocusTracker.getCurrentComponent();
			if (comp instanceof VisualizationBuildPanel)
				return (VisualizationBuildPanel) comp;
			/**/System.err.println("No current VisualizationBuildPanel");
			throw new NullPointerException("No current VisualizationBuildPanel");
		}

	}

/***	public static final AbstractAction dataAction = new FocusTrackerAction("Configure Sets...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).showDataDialog();
		}
	};
***/

	public static final AbstractAction dataAction = new FocusTrackerAction("Setup Data...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).showDataDialog();
		}
	};


	public static final AbstractAction pasteCopyLastSelectionAction = new FocusTrackerAction("Last selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).pasteCopyLastSelection();
		}
	};


	public static final AbstractAction pasteCopyFromClipBoardAction = new FocusTrackerAction("From clipboard...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).pasteCopyFromClipBoard();
		}
	};


	public static final AbstractAction editAction = new FocusTrackerAction("Edit...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).editSets();
		}
	};


	//this is for  finish config datasets
	public static final AbstractAction showVisAction = new FocusTrackerAction("Ok")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).showVis();
		}
	};


	public static final AbstractAction showPropertyPanelAction = new FocusTrackerAction("Format Chart...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).valuePanel.attemptEditChartProperties();
		}
	};


	public static final AbstractAction saveChartAction = new VisualizationBuildPanel.FocusTrackerAction("Save...")
	{
		public void actionPerformed(ActionEvent e)
		{
			try {
				getVisualizationBuildPanel(e).valuePanel.doSaveAs();
			} catch (IOException ee) {
				System.err.println("VisualizationBuildPanel.doSaveAs: i/o exception = " + ee.getMessage());
			}
		}
	};

	public static final AbstractAction printChartAction = new VisualizationBuildPanel.FocusTrackerAction("Print...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationBuildPanel(e).valuePanel.createChartPrintJob();
		}
	};

	//this is for  finish config datasets
	public static void refreshCurrentViewSeriesMenu()
	{
		if (getCurrentVisualizationBuildPanel() == null)
			System.err.println("no current Visualization build panel");
		else
			getCurrentVisualizationBuildPanel().refreshViewSetMenu();
	}


	public static VisualizationBuildPanel getCurrentVisualizationBuildPanel()
	{
		JComponent comp = BuildFocusTracker.getCurrentComponent();
		if (comp instanceof VisualizationBuildPanel)
			return (VisualizationBuildPanel) comp;
		else
			return null;
	}
}
