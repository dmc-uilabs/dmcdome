// ProjectDefinitionBuildPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project.build;

import mit.cadlab.dome3.gui.dsm.DSMPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.tools.XmlViewPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildListPanel;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.MatrixUtils;

import com.touchgraph.graphlayout.GLPanel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class ProjectDefinitionBuildPanel extends JPanel
{
	protected static GridBagConstraints gbc;

	protected IntegrationProjectBuilder projBuilder;
	protected ProjectBuildListPanel projectBuildListPanel;
	protected JComboBox viewChoice;
	protected CardLayout2 projectViewsCards;
	protected JPanel projectViewsPanel;
	protected GLPanel graph;
	protected DSMPanel dsm;


	public ProjectDefinitionBuildPanel(IntegrationProjectBuilder projBuilder)
	{
		this.projBuilder = projBuilder;
		createComponents();
	}

	private void createComponents()
	{
		projectViewsCards = new CardLayout2();
		projectViewsPanel = new JPanel();
		projectViewsPanel.setLayout(projectViewsCards);
		projectBuildListPanel = new ProjectBuildListPanel(projBuilder);
		projectViewsPanel.add("list", projectBuildListPanel);

		graph = new GLPanel();
		projectViewsPanel.add("graph", graph);
		//dsm = DSMPanel.createExampleDSMPanel();
		//projectViewsPanel.add("dsm", dsm);
		projectViewsPanel.add("xml", new XmlViewPanel(projBuilder));

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

	private JPanel createControlPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalGlue());
		p.add(Templates.makeLabel("visualization"));
		p.add(Box.createHorizontalStrut(5));
		viewChoice = Templates.makeComboBox(new String[]{"list", "graph", "dsm", "xml"});
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
		} else if (viewChoice.getSelectedItem().toString().equals("graph")) {
			System.out.println("ProjectDefinitonBuildPanel.switchViwe - build new graph");
			graph.updateGraph(projBuilder);
			projectViewsCards.show(projectViewsPanel, "graph");
		} else
			projectViewsCards.show(projectViewsPanel, viewChoice.getSelectedItem().toString());
		setMenuContext();
	}

	public void setMenuContext()
	{
		if (viewChoice.getSelectedIndex() == 0) {
			MenuManager.setContext(ModeContexts.BUILD_PROJECT_DEFINITION);
		} else {
			MenuManager.setContext(ModeContexts.BUILD_PROJECT);
		}
		JComponent comp = (JComponent) projectViewsCards.getActiveComponent();
		BuildFocusTracker.notifyInFocus(comp, projBuilder);
	}

	public Object[] createDSMData()
	{
		DirectedGraph dg = projBuilder.createProjectGraph();
		java.util.List nodes = dg.getNodes();
		ArrayList names = new ArrayList();
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			Parameter p = (Parameter) i.next();
			names.add(p.getName());
		}
		if (dg.getAdjMatrix() != null) {
		/*	int[][] reach = dg.getAdjMatrix();
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

    public void cleanup() {
	    projectBuildListPanel.cleanup();
    }
}
