// DomeModelDefinitionBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface.run;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import com.touchgraph.graphlayout.GLPanel;
import mit.cadlab.dome3.gui.dsm.DSMPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTreePanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTreePanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.run.RunContextTree;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.ObjectTree;
import mit.cadlab.dome3.swing.tree.TreeHistoryEvent;
import mit.cadlab.dome3.swing.tree.TreeHistoryListener;
import mit.cadlab.dome3.util.MatrixUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ModelInterfaceDefinitionRunPanel extends JPanel
{

	protected static GridBagConstraints gbc;
	protected static ImageIcon comboArrow = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrow.gif");
	protected static ImageIcon comboArrowOver = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrowOver.gif");
	protected static Color notEditableColor = new Color(105, 105, 105);
	protected static String[] visualizations = {"list", "graph", "dsm"};

	protected ModelInterface mInterface;
	protected CardLayout2 ifaceViewsCards;
	protected JPanel ifaceViewsPanel;

	protected ContextTreePanel buildViewPanel;
	protected JButton backButton;
	protected boolean isBackButtonEnabled = false;
	protected NameTextField nameField;
	protected boolean isNameFieldEditable = true;
	protected BasicComboPopup contextPopup;
	protected JButton rootContextsButton;
	protected int tfHeight = 0; // initialize textfield height

	protected ModelInterfaceTreePanel ifaceCausalityPanel;
	protected ModelInterfaceTreePanel sysCausalityPanel;
	protected JComboBox viewChoice,viewComboBox;
	protected DefaultComboBoxModel cbModel;
	protected GLPanel graph;
	protected DSMPanel dsm;
    protected boolean drawGrpah=false;

	public ModelInterfaceDefinitionRunPanel(ModelInterface mInterface)
	{
		this.mInterface = mInterface;
		setBackground(Templates.DARKER_BACKGROUND_COLOR);
		createComponents();
	}

	protected void createComponents()
	{
		ifaceViewsCards = new CardLayout2();
		ifaceViewsPanel = new JPanel();
		ifaceViewsPanel.setLayout(ifaceViewsCards);
		DefaultContextBuilder conBuilder = null;
		if (mInterface instanceof DomeModelInterface) {
			conBuilder = (DefaultContextBuilder) ((DomeModelInterface) mInterface).getBuildContext();
		}
		buildViewPanel = new ContextTreePanel(conBuilder, true);
		buildViewPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		ifaceViewsPanel.add(DomeModelInterface.BUILD_VIEW, buildViewPanel);
		ifaceCausalityPanel = new ModelInterfaceTreePanel(mInterface,
		                                                  DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
		ifaceCausalityPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		ifaceViewsPanel.add(DomeModelInterface.INTERFACE_CAUSALITY_VIEW, ifaceCausalityPanel);
		sysCausalityPanel = new ModelInterfaceTreePanel(mInterface,
		                                                DomeModelInterface.SYSTEM_CAUSALITY_VIEW);
		sysCausalityPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		ifaceViewsPanel.add(DomeModelInterface.SYSTEM_CAUSALITY_VIEW, sysCausalityPanel);

		if (mInterface instanceof ModelInterfaceBase) {
		//	graph = new GLPanel(((ModelInterfaceBase) mInterface).getInterfaceGraph()
            //not draw here, draw when first flip here
             graph = new GLPanel();
             drawGrpah =true;
		}
		graph.shouldPaint(false);
		ifaceViewsPanel.add("graph", graph);

		//dsm = createDSM();
		//ifaceViewsPanel.add("dsm", dsm);


		backButton = Templates.makeImageButton("mit/cadlab/dome3/icons/backArrow16.gif");
		backButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		backButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String currentView = ifaceViewsCards.getActiveName();
				if (currentView.equals(DomeModelInterface.BUILD_VIEW)) {
					buildViewPanel.getContextTree().setRootContextBack();
				}
				nameField.setForeground(Color.BLACK);
			}
		});
		backButton.setEnabled(false);


		nameField = new NameTextField();
		nameField.setForeground(Color.BLACK);
		nameField.setDisabledTextColor(notEditableColor);
		nameField.setDomeObject(buildViewPanel.getContextTree().getRootContext());
		nameField.setEditable(false);
		isNameFieldEditable = false;
		nameField.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		viewChoice = Templates.makeComboBox(visualizations);
		viewChoice.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		// associate backbutton and namefield with tree root and history
		RunContextTree contextTree = buildViewPanel.getContextTree();
		contextTree.addHistoryListener(new InterfaceTreeHistoryListener());
		contextTree.addPropertyChangeListener(ObjectTree.ROOT_PROPERTY, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();
				if (property.equals(ObjectTree.ROOT_PROPERTY)) {
					nameField.setDomeObject(buildViewPanel.getContextTree().getRootContext());
				}
			}
		});

		layoutComponent();
		configViewChoice();
	}

	protected void layoutComponent()
	{
/*		progressBar = new JProgressBar(0,30);

		progressBar.setVisible(false);
        progressBar.setForeground(Templates.DARKER_BACKGROUND_COLOR);
		statusLabel = Templates.makeLabel("");

		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        JComponent[] statusComps = {progressBar, statusLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] statusGbcs = {// 25 inset
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, -9),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
        };
        Templates.layoutGridBagB(statusPanel, statusComps, statusGbcs);*/

		JComponent[] comps = {makeControlPanel(), ifaceViewsPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(2, 5, 5, 5), 0, 0),
			//new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		if (mInterface instanceof DomeModelInterface) {
			cbModel = new DefaultComboBoxModel(((DomeModelInterface) mInterface).getViewNames().toArray());
		}
		//separate button for model view
		cbModel.removeElement(DomeModelInterface.MODEL_VIEW);

		viewComboBox = Templates.makeComboBox(cbModel);
		viewComboBox.setForeground(Color.BLACK);
		viewComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				switchView();
				contextPopup.hide();
			}
		});
		contextPopup = new BasicComboPopup(viewComboBox);
		//to display INTERFACE_CAUSALITY_VIEW in the run interface gui initially
        //Qing change here: to display Build_view instead
	    //viewComboBox.setSelectedItem(DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
        viewComboBox.setSelectedItem(DomeModelInterface.BUILD_VIEW);
        rootContextsButton = Templates.makeImageButton(comboArrow, comboArrow, comboArrowOver, comboArrow);
		rootContextsButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		rootContextsButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (tfHeight == 0) {
					Dimension tfSize = nameField.getSize();
					tfHeight = tfSize.height;
					Dimension popupSize = new Dimension(tfSize.width, tfHeight * cbModel.getSize());
					Templates.setFixedSize(contextPopup, popupSize);
				}
				contextPopup.show(nameField, 0, tfHeight);
			}
		});

		JComponent[] comps = {backButton,
		                      nameField,
		                      rootContextsButton,
		                      Templates.makeLabel("visualization"),
		                      viewChoice
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(3, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 15, 0, 0), 0, 0),
			new GridBagConstraints(4, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)

		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}


	public void setMenuContext()
	{
		MenuManager.setContext(getMenuContext());
		JComponent comp = (JComponent) ifaceViewsCards.getActiveComponent();
		//to direct messages to correct message log area
		BuildFocusTracker.notifyInFocus(comp, (ModelComponent) mInterface);
	}

	protected String getMenuContext()
	{
		String currentView = ifaceViewsCards.getActiveName();
		if (currentView.equals(DomeModel.BUILD_VIEW)) {
			return ModeContexts.RUN_DOMEMODEL_INTERFACE_BUILDVIEW;
		} else if (currentView.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
			return ModeContexts.RUN_DOMEMODEL_INTERFACE_CAUSALVIEW;
		} else if (currentView.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
			return ModeContexts.RUN_DOMEMODEL_INTERFACE_CAUSALVIEW;
		} else if (currentView.equals(DomeModelInterface.MODEL_VIEW)) {
			return ModeContexts.RUN_DOMEMODEL_INTERFACE_MODELVIEW;
		} else {//for other visualiztion
			return ModeContexts.RUN_DOMEMODEL_INTERFACE;
		}

	}

	protected void switchView()
	{
		String newView = cbModel.getSelectedItem().toString();
		ifaceViewsCards.show(ifaceViewsPanel, newView);
		setMenuContext(); // do this before the next line!
		synchronizeViewControls(); // needs correct menu showing
		if (newView.equals(DomeModelInterface.BUILD_VIEW)) {
			if (mInterface instanceof ModelInterfaceBase) {
				((ModelInterfaceBase) mInterface).setCurrentView(
				        DomeModelInterface.BUILD_VIEW);
				((ModelInterfaceBase) mInterface).resetNewObjectCausality();
			}
			return;
		}
		if (newView.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
			ifaceCausalityPanel.setPaths(newView);
			if (mInterface instanceof ModelInterfaceBase) {
				((ModelInterfaceBase) mInterface).setCurrentView(
				        DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
			}
			return;
		}
		if (newView.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
			sysCausalityPanel.setPaths(newView);
			if (mInterface instanceof ModelInterfaceBase) {
				((ModelInterfaceBase) mInterface).setCurrentView(
				        DomeModelInterface.SYSTEM_CAUSALITY_VIEW);
			}
			return;
		}
		if (newView.equals(DomeModelInterface.MODEL_VIEW)) {
			//only model ifaces have model view
			((ModelInterfaceBase) mInterface).setCurrentView(
			        DomeModelInterface.MODEL_VIEW);
			((ModelInterfaceBase) mInterface).resetNewObjectCausality();
			return;
		}
	}

	protected void synchronizeViewControls()
	{
		String currentView = ifaceViewsCards.getActiveName();
		if (currentView.equals(DomeModelInterface.BUILD_VIEW)) {
//			buildViewPanel.setEditMenusForSelection(
//					ModeContexts.BUILD_DOMEMODEL_INTERFACE_BUILDVIEW,
//                    mInterface.isDefaultInterface());
			// reset backbutton and nameField properties
			nameField.setDomeObject(buildViewPanel.getContextTree().getRootContext());
			nameField.setEditable(isNameFieldEditable);
			backButton.setEnabled(isBackButtonEnabled);
			return;
		}
		if (currentView.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
//			ifaceCausalityPanel.setEditMenusForSelection();
			setViewName(currentView);
			return;
		}
		if (currentView.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
//			sysCausalityPanel.setEditMenusForSelection();
			setViewName(currentView);
			return;
		}
	}

	private void setViewName(String currentView)
	{
		nameField.setText(currentView);
		nameField.setEditable(false);
		nameField.setCurrent();
		backButton.setEnabled(false);
	}

	protected class InterfaceTreeHistoryListener implements TreeHistoryListener
	{
		public void historyChanged(TreeHistoryEvent event)
		{
			switch (event.getType()) {
				case TreeHistoryEvent.EVENT_QUEUE_NONEMPTY:
					backButton.setEnabled(true);
					isBackButtonEnabled = true;
					nameField.setEditable(true);
					isNameFieldEditable = true;
					nameField.setForeground(Color.black);
					break;
				case TreeHistoryEvent.EVENT_QUEUE_EMPTY:
					backButton.setEnabled(false);
					isBackButtonEnabled = false;
					nameField.setEditable(false);
					isNameFieldEditable = false;
					nameField.setForeground(notEditableColor);
			}
		}
	}

	public void enablePlayPauseKillActions()
	{
		ModelInterfaceTreePanel.pauseResumeAction.setEnabled(true);
		//ModelInterfaceTreePanel.killAction.setEnabled(true);
	}

	public void disablePlayPauseKillActions()
	{
		ModelInterfaceTreePanel.pauseResumeAction.setEnabled(false);
		//ModelInterfaceTreePanel.killAction.setEnabled(false);
	}

	public void disableSubmitAction()
	{
		ModelInterfaceTreePanel.submitAction.setEnabled(false);
	}

	public void enableSubmitAction()
	{
		ModelInterfaceTreePanel.submitAction.setEnabled(true);
	}

	protected void configViewChoice()
	{
		viewChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!(viewChoice.getSelectedItem().toString().equals("list")))
					rootContextsButton.setEnabled(false);
				else {
					if (!rootContextsButton.isEnabled()) rootContextsButton.setEnabled(true);
				}
				showSelectedView();
			}
		});
	}

	protected void showSelectedView()
	{
		setIfGraphShouldPaint();
		if (viewChoice.getSelectedItem().toString().equals("list")) {
			switchView();
		} else if (viewChoice.getSelectedItem().toString().equals("dsm")) {
			if (dsm == null) {
				dsm = createDSM();
				ifaceViewsPanel.add("dsm", dsm);
			}
			updateDSM();
			ifaceViewsCards.show(ifaceViewsPanel, "dsm");
		} else if (viewChoice.getSelectedItem().toString().equals("graph")) {
			//always repaint to get the newest
            if(drawGrpah)
            {
                graph.updateGraph(((ModelInterfaceBase)mInterface).getInterfaceGraph());
                drawGrpah=false;
            }
			ifaceViewsCards.show(ifaceViewsPanel, "graph");
		} else
			ifaceViewsCards.show(ifaceViewsPanel, viewChoice.getSelectedItem().toString());
		setMenuContext();
	}

	public Object[] createDSMData()
	{
		DirectedGraph dg = null;
		if (mInterface instanceof ModelInterfaceBase) {
			dg = ((ModelInterfaceBase) mInterface).getInterfaceGraph();
		}
		java.util.List nodes = dg.getNodes();
		ArrayList names = new ArrayList();
        ArrayList bad_indeies = new ArrayList();
        for (int i = 0; i < nodes.size(); i++) {
            Object obj = nodes.get(i);
            if (obj instanceof Parameter)    //to deal with project interface dsm special case exceptions
            {
                Parameter p = (Parameter) obj;
                names.add(p.getName());
            } else
                bad_indeies.add(new Integer(i));
        }
		if (dg.getAdjMatrix() != null) {
			/**int[][] reach = dg.getAdjMatrix();
			//make a copy
			int n = reach.length;
			int[][] reachcopy = new int[n][n];
			int i,j;
			for (i = 0; i < n; i++) {
				for (j = 0; j < n; j++) {
					reachcopy[i][j] = reach[j][i];
					if (j == i)
						reachcopy[i][j] = 1;
				}
			} */

            DoubleMatrix2D reach = dg.getAdjMatrix();
            Algebra alg=new Algebra();
            DoubleMatrix2D reachCopy= alg.transpose(reach).copy();
            if (bad_indeies.size() != 0) {
                reachCopy = MatrixUtils.removeRowCol(reachCopy, bad_indeies);
            }

            int n=reachCopy.rows();
            for(int i=0;i<n;i++){
                reachCopy.setQuick(i,i,1);
            }
			return new Object[]{reachCopy, (String[]) names.toArray(new String[]{})};
		}
		return null;
	}

	public DSMPanel createDSM()
	{
		Object[] data = createDSMData();
		if (data != null) {
			//return new DSMPanel((int[][]) data[0], (String[]) data[1]);
            return new DSMPanel(MatrixUtils.toIntArray((DoubleMatrix2D) data[0]), (String[]) data[1]);
		} else
			return DSMPanel.createEmptyMatrixDSMPanel();
	}


	public void updateDSM()
	{
		Object[] data = createDSMData();
		if (data != null) {
			//dsm.setData((int[][]) data[0], (String[]) data[1]);
             dsm.setData(MatrixUtils.toIntArray((DoubleMatrix2D)data[0]), (String[]) data[1]);
		} else {
			int[][] nMatrix = new int[][]{};
			String[] headings = new String[]{};
			dsm.setData(nMatrix, headings);
		}

	}

	public void setIsDefinitionPanelOnTop(boolean defOnTop) {
		if (defOnTop)
			setIfGraphShouldPaint();
		else
			graph.shouldPaint(false);
	}

	protected void setIfGraphShouldPaint() {
		graph.shouldPaint(viewChoice.getSelectedItem().toString().equals("graph"));
	}

}
