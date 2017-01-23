// ContextDefinitionBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.BuildContextTree;
import mit.cadlab.dome3.gui.objectmodel.tools.XmlViewPanel;
import mit.cadlab.dome3.gui.dsm.DSMPanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.ObjectTree;
import mit.cadlab.dome3.swing.tree.TreeHistoryEvent;
import mit.cadlab.dome3.swing.tree.TreeHistoryListener;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.util.MatrixUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.touchgraph.graphlayout.GLPanel;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class ContextDefinitionBuildPanel extends JPanel
{

	protected static GridBagConstraints gbc;

	//String[] defaultContexts = {"custom user view","model elements"};
	protected static String BUILD_CONTEXT = "Build View";
	protected static String[] visualizations = {"list", "graph", "dsm", "xml"};
	protected static Dimension DEFAULT_SIZE = new Dimension(400, 300);
	protected static int rightOffset = 25;
	protected static Color notEditableColor = new Color(105, 105, 105);

	protected DefaultContextBuilder contextBuilder;
	protected JButton backButton;
	protected NameTextField nameField;
	protected JComboBox viewChoice;
	protected CardLayout2 modelViewCards;
	protected JPanel modelViewPanel;
	protected ContextTreeBuilderPanel treeBuilderPanel;
	protected boolean isContextRootContext = true;
	protected boolean isBackButtonEnabled = false;
	protected boolean isNameFieldEditable = true;
	protected DomeObject currentTreeRoot = null;
	protected GLPanel graph;
	protected DSMPanel dsm;

	public ContextDefinitionBuildPanel(DefaultContextBuilder contextBuilder)
	{
		this.contextBuilder = contextBuilder;
		this.isContextRootContext = false; // take this out later!
		if (isContextRootContext) {
			setBackground(Templates.DARKER_BACKGROUND_COLOR);
		}
		createComponents();
	}

	protected void createComponents()
	{
		modelViewCards = new CardLayout2();
		modelViewPanel = new JPanel();
		modelViewPanel.setLayout(modelViewCards);
		treeBuilderPanel = new ContextTreeBuilderPanel(contextBuilder);
		modelViewPanel.add(BUILD_CONTEXT, treeBuilderPanel);
		if (isContextRootContext) {
			treeBuilderPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		}

		modelViewPanel.add("xml", new XmlViewPanel(contextBuilder));
		graph = new GLPanel();
		modelViewPanel.add("graph", graph);
		/*dsm = DSMPanel.createExampleDSMPanel();
		modelViewPanel.add("dsm", dsm);*/

		backButton = Templates.makeImageButton("mit/cadlab/dome3/icons/backArrow16.gif");
		backButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				treeBuilderPanel.getContextTree().setRootContextBack();
			}
		});
		backButton.setEnabled(false);
		nameField = new NameTextField();
		nameField.setDisabledTextColor(notEditableColor);
		currentTreeRoot = treeBuilderPanel.getContextTree().getRootContext();
		nameField.setDomeObject(currentTreeRoot);
		if (isContextRootContext) {
			nameField.setEditable(false);
			isNameFieldEditable = false;
			nameField.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		}
		viewChoice = Templates.makeComboBox(visualizations);
		// associate backbutton and namefield with tree root and history
		BuildContextTree contextTree = treeBuilderPanel.getContextTree();
		contextTree.addHistoryListener(new TreeHistoryListener()
		{
			public void historyChanged(TreeHistoryEvent event)
			{
				switch (event.getType()) {
					case TreeHistoryEvent.EVENT_QUEUE_NONEMPTY:
						backButton.setEnabled(true);
						isBackButtonEnabled = true;
						if (isContextRootContext) {
							nameField.setEditable(true);
							isNameFieldEditable = true;
							nameField.setForeground(Color.black);
						}
						break;
					case TreeHistoryEvent.EVENT_QUEUE_EMPTY:
						backButton.setEnabled(false);
						isBackButtonEnabled = false;
						if (isContextRootContext) {
							nameField.setEditable(false);
							isNameFieldEditable = false;
							nameField.setForeground(notEditableColor);
						}
				}
			}
		});
		contextTree.addPropertyChangeListener(ObjectTree.ROOT_PROPERTY, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String property = e.getPropertyName();
				Object newValue = e.getNewValue();
				if (property.equals(ObjectTree.ROOT_PROPERTY)) {
					currentTreeRoot = treeBuilderPanel.getContextTree().getRootContext();
					nameField.setDomeObject(currentTreeRoot);
				}
			}
		});

		layoutComponent();
		configViewChoice();
	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), modelViewPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 2, rightOffset), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
		setPreferredSize(DEFAULT_SIZE);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {backButton,
		                      nameField,
		                      Templates.makeLabel("visualization"),
		                      viewChoice
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 15, 0, 0), 0, 0),
			new GridBagConstraints(3, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		if (isContextRootContext) {
			p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		}
		return p;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(getMenuContext());
		BuildFocusTracker.notifyInFocus(treeBuilderPanel, contextBuilder);
		treeBuilderPanel.setEditMenusForSelection();
	}

	protected String getMenuContext()
	{
		if (((DomeModel) contextBuilder.getModel()).getIntegrationProject() != null) {
			//set project menu
			if (modelViewCards.getActiveName().equals(BUILD_CONTEXT))
				return ModeContexts.BUILD_PROJECT_DOMEMODEL_DEFINITION;
			else
				return ModeContexts.BUILD_PROJECT_DOMEMODEL_OTHERVISUALIZATION_VIEW;
		}
		if (contextBuilder.getModel() instanceof PluginModel) {
			if (modelViewCards.getActiveName().equals(BUILD_CONTEXT))
				return ModeContexts.BUILD_PLUGINMODEL_DEFINITION;
			else
				return ModeContexts.BUILD_PLUGINMODEL_OTHERVISUALIZATION_VIEW;
		}
		else {
			if (modelViewCards.getActiveName().equals(BUILD_CONTEXT))
				return ModeContexts.BUILD_DOMEMODEL_DEFINITION;
			else
				return ModeContexts.BUILD_DOMEMODEL_OTHERVISUALIZATION_VIEW;
		}
	}


	protected void configViewChoice()
	{
		viewChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showSelectedView();
			}
		});
	}

	protected void showSelectedView()
	{
		if (viewChoice.getSelectedItem().toString().equals("list"))
			modelViewCards.show(modelViewPanel, BUILD_CONTEXT);
	    else if (viewChoice.getSelectedItem().toString().equals("dsm")) {
	   	if (dsm == null) {
			dsm = createDSM();
				modelViewPanel.add("dsm", dsm);
			}
			updateDSM();
			modelViewCards.show(modelViewPanel, "dsm");
		}
	    else if (viewChoice.getSelectedItem().toString().equals("graph")) {
			//always repaint to get the newest
	    	graph.updateGraph(((DomeModelBase)contextBuilder.getModel()));
			modelViewCards.show(modelViewPanel, "graph");
		}
		else
			modelViewCards.show(modelViewPanel, viewChoice.getSelectedItem().toString());
		setMenuContext();
	}


	public Object[] createDSMData()
	{
		DirectedGraph dg = ((DomeModelBase)contextBuilder.getModel()).createModelGraph();
		java.util.List nodes = dg.getNodes();
		ArrayList names = new ArrayList();
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			Parameter p = (Parameter) i.next();
			names.add(p.getName());
		}
		if (dg.getAdjMatrix() != null) {
			/*int[][] reach = dg.getAdjMatrix();
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
			}  */
            DoubleMatrix2D reach = dg.getAdjMatrix();
            Algebra alg=new Algebra();
            DoubleMatrix2D reachCopy= alg.transpose(reach).copy();
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
}
