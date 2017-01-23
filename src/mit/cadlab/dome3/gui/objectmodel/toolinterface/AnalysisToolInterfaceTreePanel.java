package mit.cadlab.dome3.gui.objectmodel.toolinterface;

import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationToolInterfaceRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationInterfaceResultsPanel;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 28, 2003
 * Time: 2:32:13 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolInterfaceTreePanel extends JPanel
{
    protected ToolInterface _iface;
    protected ToolInterfaceTree _tree;
    protected RunTreeTable _treeTable;

    public AnalysisToolInterfaceTreePanel(ToolInterface iface,
	                               String view)
	{
		_iface = iface;
		_tree = new ToolInterfaceTree(new RunObjectTreeNode(iface, view), view, false);
		_treeTable = new RunTreeTable(_tree);
		layoutComponent(_treeTable);
	}

    protected void layoutComponent(Component treeTable)
	{
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		JComponent[] comps = {scrollPane};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	public void setPaths(String view)
	{
		_tree.setPaths(view);
	}

	protected void moveDownAction()
	{
	}

	protected void moveUpAction()
	{
	}


	// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected OptimizationToolInterfaceRunPanel getOptimizationToolInterfaceRunPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof OptimizationToolInterfaceRunPanel) {
					return (OptimizationToolInterfaceRunPanel) o;
				}
			}
			JComponent comp = RunFocusTracker.getCurrentComponent();
			if (comp instanceof OptimizationToolInterfaceRunPanel)
				return (OptimizationToolInterfaceRunPanel) comp;
            throw new NullPointerException("No current OptimizationToolInterfaceRunPanel");
		}
	}


	// --- actions for menus and buttons --------------------
	public static final AbstractAction submitAction = new FocusTrackerAction("Submit")
	{
		public void actionPerformed(ActionEvent e)
		{
			OptimizationToolInterfaceRunPanel runpane = getOptimizationToolInterfaceRunPanel(e);
		}
	};

	public static final AbstractAction pauseResumeAction = new FocusTrackerAction("Pause/Resume")
	{
		public void actionPerformed(ActionEvent e)
		{
			OptimizationToolInterfaceRunPanel runpane = getOptimizationToolInterfaceRunPanel(e);
		}
	};

	public static final AbstractAction saveVerAction = new FocusTrackerAction("Save Version")
	{
		public void actionPerformed(ActionEvent e)
		{
		}
	};

	public static final AbstractAction saveAction = new FocusTrackerAction("Save")
	{
		public void actionPerformed(ActionEvent e)
		{
		}
	};

	public static final AbstractAction loadVerAction = new FocusTrackerAction("Load Version")
	{
		public void actionPerformed(ActionEvent e)
		{
		}
	};

    public static final AbstractAction saveResultsAction = new FocusTrackerAction("Save Results")
    {
        public void actionPerformed(ActionEvent e)
        {
            getOptimizationToolInterfaceRunPanel(e).saveResults();
        }
    };

    public static final AbstractAction loadResultsAction = new FocusTrackerAction("Load Results")
    {
        public void actionPerformed(ActionEvent e)
        {
            getOptimizationToolInterfaceRunPanel(e).loadResults();
        }
    };

    public static final AbstractAction viewResultsAction = new FocusTrackerAction("View Results")
    {
        public void actionPerformed(ActionEvent e)
        {
            getOptimizationToolInterfaceRunPanel(e).showResults();
        }
    };

    public static final AbstractAction viewDesignSpaceAction = new FocusTrackerAction("View Design Space")
    {
        public void actionPerformed(ActionEvent e)
        {
            getOptimizationToolInterfaceRunPanel(e).showDesignSpace();
        }
    };

	public static final AbstractAction closeAction = new FocusTrackerAction("Close")
	{
		public void actionPerformed(ActionEvent e)
		{
			OptimizationToolInterfaceRunPanel runpane = getOptimizationToolInterfaceRunPanel(e);
			runpane.close();
		}
	};

	public static final AbstractAction killAction = new FocusTrackerAction("Kill")
	{
		public void actionPerformed(ActionEvent e)
		{
			OptimizationToolInterfaceRunPanel runpane = getOptimizationToolInterfaceRunPanel(e);
			int answer = TwoButton1Msg.showWarning(runpane,
			                                       "Kill Process",
			                                       "Are you sure you want to kill this process?",
			                                       "Yes", "No",
			                                       TwoButton1Msg.DEFAULT_SIZE);
			if (answer == 0) {//No
				return;
			}
		}
	};

}
