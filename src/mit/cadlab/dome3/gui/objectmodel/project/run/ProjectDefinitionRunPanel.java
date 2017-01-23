// ProjectDefinitionBuildPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project.run;

import com.touchgraph.graphlayout.GLPanel;
import mit.cadlab.dome3.gui.dsm.DSMPanel;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.GenericObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunTreeObjectFactory;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.project.AbstractIntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.RunInterface;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.RunProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.MatrixUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class ProjectDefinitionRunPanel extends JPanel
{
	protected static GridBagConstraints gbc;
	protected static DomeTreeObjectFactory projectRunTreeFactory = makeRunProjectTreeObjectFactory();

	protected IntegrationProject project;
	protected JComboBox viewChoice;
	protected CardLayout2 projectViewsCards;
	protected JPanel projectViewsPanel;
	protected DomeTree projectTree;
	protected GLPanel graph;
	protected DSMPanel dsm;

	public ProjectDefinitionRunPanel(IntegrationProject project)
	{
		this.project = project;
		createComponents();
	}

	private void createComponents()
	{
		projectViewsCards = new CardLayout2();
		projectViewsPanel = new JPanel();
		projectViewsPanel.setLayout(projectViewsCards);
		createTreeTable();
		projectViewsPanel.add("list", new JScrollPane(projectTree));
		graph = new GLPanel();
		projectViewsPanel.add("graph", graph);

		JComponent[] comps = {createControlPanel(),
		                      projectViewsPanel
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private void createTreeTable()
	{
		List projectContent = new ArrayList();
		projectContent.addAll(project.getIntegrationModels());
		projectContent.addAll(project.getResourceModels());
		projectTree = new DomeTree(new GenericObjectTreeNode(projectContent, projectRunTreeFactory), false);
		projectTree.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				Object o = ((GenericObjectTreeNode) event.getPath().getLastPathComponent()).getObject();
				if (o instanceof ProjectResourceInfo) {
					((RunProjectResourceInfo) o).loadResource();
				}
				else if (o instanceof ProjectIntegrationModelInfo) {
					//((ProjectIntegrationModelInfo) o);
				}
				else if (o instanceof RunInterface) {
					((RunInterface) o).loadInterface();
				}
			}

			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}
		});
	}

	private JPanel createControlPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalGlue());
		p.add(Templates.makeLabel("visualization"));
		p.add(Box.createHorizontalStrut(5));
		viewChoice = Templates.makeComboBox(new String[]{"list", "graph", "dsm"});
		viewChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showSelectedView();
			}
		});
		p.add(viewChoice);
		return p;
	}

	protected void showSelectedView()
	{
		if (viewChoice.getSelectedItem().toString().equals("dsm")) {
			if (dsm == null) {
				dsm = createDSM();
				projectViewsPanel.add("dsm", dsm);
			}
			updateDSM();
			projectViewsCards.show(projectViewsPanel, "dsm");
		}
		else if (viewChoice.getSelectedItem().toString().equals("graph")) {
			graph.shouldPaint(true);
			projectViewsCards.show(projectViewsPanel, "graph");
		}
		else
			projectViewsCards.show(projectViewsPanel, viewChoice.getSelectedItem().toString());
		setMenuContext();
	}

	public void setMenuContext()
	{
		/*if (viewChoice.getSelectedIndex() == 0) {
			MenuManager.setContext(ModeContexts.BUILD_PROJECT_DEFINITION);
		}
		else {
			MenuManager.setContext(ModeContexts.BUILD_PROJECT);
		}*/
//			JComponent comp = (JComponent) projectViewsCards.getActiveComponent();
//			BuildFocusTracker.notifyInFocus(comp, proj);
	}

	public Object[] createDSMData()
	{
		DirectedGraph dg = ((AbstractIntegrationProject) project).createProjectGraph();
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
			} */
            DoubleMatrix2D reach = dg.getAdjMatrix();
            Algebra alg=new Algebra();
            DoubleMatrix2D reachcopy= alg.transpose(reach).copy();
            int n=reachcopy.rows();
            for(int i=0;i<n;i++){
                reachcopy.setQuick(i,i,1);
            }
			return new Object[]{reachcopy, (String[]) names.toArray(new String[]{})};
		}
		return null;
	}

	public DSMPanel createDSM()
	{
		Object[] data = createDSMData();
		if (data != null) {
			//return new DSMPanel((int[][]) data[0], (String[]) data[1]);
            return new DSMPanel(MatrixUtils.toIntArray((DoubleMatrix2D) data[0]), (String[]) data[1]);
		}
		else
			return DSMPanel.createEmptyMatrixDSMPanel();
	}


	public void updateDSM()
	{
		Object[] data = createDSMData();
		if (data != null) {
			//dsm.setData((int[][]) data[0], (String[]) data[1]);
            dsm.setData(MatrixUtils.toIntArray((DoubleMatrix2D)data[0]), (String[]) data[1]);
		}
		else {
			int[][] nMatrix = new int[][]{};
			String[] headings = new String[]{};
			dsm.setData(nMatrix, headings);
		}

	}

	private static DomeTreeObjectFactory makeRunProjectTreeObjectFactory()
	{
		DomeTreeObjectFactory factory = new DomeTreeObjectFactory("ProjectRunTreeObjectFactory");
		factory.registerTreeObjectInfo("mit.cadlab.dome.objectmodel.project.ProjectResourceInfo",
		                               "mit.cadlab.dome.gui.objectmodel.project.ProjectResourceTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome.objectmodel.project.ProjectIntegrationModelInfo",
		                               "mit.cadlab.dome.gui.objectmodel.project.ProjectIntegrationModelTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome.objectmodel.project.BrowseInterface",
		                               "mit.cadlab.dome.gui.objectmodel.project.BrowseInterfaceTreeObject");
		RunTreeObjectFactory.registerDomeRunTreeObjects(factory);
		return factory;
	}

}
