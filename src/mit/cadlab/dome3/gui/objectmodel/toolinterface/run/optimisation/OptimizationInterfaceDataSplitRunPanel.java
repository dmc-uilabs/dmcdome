package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation;

import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.GenericObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.browse.BrowseTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 28, 2003
 * Time: 6:20:05 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationInterfaceDataSplitRunPanel extends JPanel
                                                        implements ActionListener
{
    public static final GridBagConstraints gbc = null;

    private static final String DESIGN_VARIABLES = "design variables:";
    private static final String DESIGN_OBJECTIVES = "design objectives:";
    public static final String VIEW_RESULTS = "view results";

    public static TreeObjectFactory variablesTreeFactory = makeVariablesTreeFactory();
    public static TreeObjectFactory objectivesTreeFactory = makeObjectivesTreeFactory();

    protected DomeTree _variablesTree, _objectivesTree;

    protected DomeFrame _resultsFrame = null;
    protected DomeFrame _designSpaceFrame = null;

    private OptimizationInterfaceRuntimeClient _ti;

    private VariablesRunPanel _v;
    private ObjectivesRunPanel _o;

    private JSplitPane _p = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    public OptimizationInterfaceDataSplitRunPanel(OptimizationInterfaceRuntimeClient ti)
    {
        _ti = ti;

        _p.setTopComponent(makeVariablesPanel());
        _p.setBottomComponent(makeObjectivePanel());
        _p.setDividerLocation(125);

        JComponent[] comps = {

            _p
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);

    }

    protected JPanel makeVariablesPanel()
    {
        JPanel p = new JPanel();

        _variablesTree = new DomeTree(new GenericObjectTreeNode(
                _ti.getVariablesFilter(), variablesTreeFactory), false);

        _v = new VariablesRunPanel(_variablesTree);

        JComponent[] comps = {

            Templates.makeLabel(DESIGN_VARIABLES),
            _v
        };
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),

		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }

    protected JPanel makeObjectivePanel()
    {
        JPanel p = new JPanel();

        _objectivesTree = new DomeTree(new GenericObjectTreeNode(
                        _ti.getObjectivesFilter(), objectivesTreeFactory), true);

        _o = new ObjectivesRunPanel(_objectivesTree);

        JComponent[] comps = {

            Templates.makeLabel(DESIGN_OBJECTIVES),
            _o
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),

		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }

    class VariablesRunPanel extends TreeBuilderPanel
	{
		private RunTreeTable treeTable;
		public VariablesRunPanel(DomeTree tree)
		{
			String[] colNames = new String[] {"name", "value", "lower", "upper", "active"};
			int[] colWidths = new int[] {200, 150, 150, 150, 100};
			treeTable = new RunTreeTable(tree, QMOOConfiguration.INTERFACE_VARIABLE, colNames.length, colNames, colWidths);
			layoutComponent(treeTable);
		}

		protected void moveDownAction()
		{
		}

		protected void moveUpAction()
		{
		}
	}

    class ObjectivesRunPanel extends TreeBuilderPanel
	{
		private RunTreeTable treeTable;
		public ObjectivesRunPanel(DomeTree tree)
		{
			String[] colNames = new String[] {"name", "value", "direction", "active"};
			int[] colWidths = new int[] {200, 300, 150, 100};
			treeTable = new RunTreeTable(tree, QMOOConfiguration.INTERFACE_OBJECTIVE, colNames.length, colNames, colWidths);
			layoutComponent(treeTable);
		}

		protected void moveDownAction()
		{
		}

		protected void moveUpAction()
		{
		}
	}

    private static TreeObjectFactory makeVariablesTreeFactory()
	{
		DomeTreeObjectFactory factory = new DomeTreeObjectFactory("VariablesBuildTreeObjectFactory");
        BrowseTreeObjectFactory.registerVariablesBrowseTreeObjects(factory);
        return factory;
	}

    private static TreeObjectFactory makeObjectivesTreeFactory()
	{
		DomeTreeObjectFactory factory = new DomeTreeObjectFactory("ObjectivesBuildTreeObjectFactory");
        BrowseTreeObjectFactory.registerObjectivesBrowseTreeObjects(factory);
        return factory;
	}

    public void actionPerformed(ActionEvent e){}

    protected void showParetoFrontPanel()
    {
	    _resultsFrame = new DomeRunFrame(new OptimizationInterfaceResultsPanel(_ti),
	                                     (WindowTracker)SwingUtilities.windowForComponent(this));
        _resultsFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent event)
            {
                _resultsFrame = null;
            }
        });
        _resultsFrame.show();
    }

    protected void showDesignSpacePanel()
    {
        _designSpaceFrame = new DomeRunFrame(new OptimizationInterfaceDesignSpacePanel(_ti),
                                             (WindowTracker)SwingUtilities.windowForComponent(this));
        _designSpaceFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent event)
            {
                _designSpaceFrame = null;
            }
        });
        _designSpaceFrame.show();
    }

    public OptimizationInterfaceResultsPanel getResultsPanel()
    {
        if (_resultsFrame != null)
            return (OptimizationInterfaceResultsPanel) _resultsFrame.getGui();
        else return null;
    }

    public OptimizationInterfaceDesignSpacePanel getDesignSpacePanel()
    {
        if (_designSpaceFrame != null)
            return (OptimizationInterfaceDesignSpacePanel) _designSpaceFrame.getGui();
        else
            return null;
    }

    public DomeFrame getResultsPanelFrame()
    {
        return _resultsFrame;
    }

    public DomeFrame getDesignSpacePanelFrame()
    {
        return _designSpaceFrame;
    }
}
