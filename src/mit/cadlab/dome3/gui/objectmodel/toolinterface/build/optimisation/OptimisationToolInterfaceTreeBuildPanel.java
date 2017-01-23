package mit.cadlab.dome3.gui.objectmodel.toolinterface.build.optimisation;

import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardSelection;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.tree.GenericObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.browse.BrowseTreeObjectFactory;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceTreeBuilderPanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Mar 20, 2004
 * Time: 2:02:01 PM
 * To change this template use Options | File Templates.
 */
public class OptimisationToolInterfaceTreeBuildPanel extends ToolInterfaceTreeBuilderPanel
{
    public static TreeObjectFactory variablesTreeFactory = makeVariablesTreeFactory();
    public static TreeObjectFactory objectivesTreeFactory = makeObjectivesTreeFactory();

    private static final GridBagConstraints gbc = null;

    private static final String DESIGN_VARIABLES = "design variables:";
    private static final String DESIGN_OBJECTIVES = "design objectives:";

    protected DomeTree _variablesTree, _objectivesTree;

    private VariablesBuildPanel _variablesBuildPanel;
    private ObjectivesBuildPanel _objectivesBuildPanel;

    public OptimisationToolInterfaceTreeBuildPanel(ToolInterface iface, String interfaceView)
    {
        super(iface, interfaceView);
    }

    protected void createComponents()
    {
        _panelComponent = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        ((JSplitPane)_panelComponent).setTopComponent(makeVariablePanel());
        ((JSplitPane)_panelComponent).setBottomComponent(makeObjectivePanel());
        ((JSplitPane)_panelComponent).setDividerLocation(150);
    }

    protected void layoutComponents()
    {
        JComponent[] comps = {

            _panelComponent
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    protected JPanel makeVariablePanel()
    {
        JPanel p = new JPanel();

        _variablesTree = new DomeTree(new GenericObjectTreeNode(
                ((OptimizationInterfaceBuild)_iface).getVariablesFilter(), variablesTreeFactory), true);

        _variablesBuildPanel = new VariablesBuildPanel(_variablesTree);


        JComponent[] comps = {

            Templates.makeLabel(DESIGN_VARIABLES),
            _variablesBuildPanel

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
                        ((OptimizationInterfaceBuild)_iface).getObjectivesFilter(), objectivesTreeFactory), true);

        _objectivesBuildPanel = new ObjectivesBuildPanel(_objectivesTree);

        JComponent[] comps = {

            Templates.makeLabel(DESIGN_OBJECTIVES),
            _objectivesBuildPanel

		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),

		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }

    class VariablesBuildPanel extends TreeBuilderPanel
	{
		private BuildTreeTable treeTable;
		public VariablesBuildPanel(DomeTree tree)
		{
			String[] colNames = new String[] {"name", "value", "lower", "upper", "active", "mapping"};
			int[] colWidths = new int[] {250, 150, 100, 100, 75, 75};
			treeTable = new BuildTreeTable(tree, QMOOConfiguration.INTERFACE_VARIABLE, colNames.length, colNames, colWidths);
			layoutComponent(treeTable);
		}

		protected void moveDownAction()
		{
		}

		protected void moveUpAction()
		{
		}

        protected BuildTreeTable getBuildTreeTable()
        {
            return treeTable;
        }
	}

    class ObjectivesBuildPanel extends TreeBuilderPanel
	{
		private BuildTreeTable treeTable;
		public ObjectivesBuildPanel(DomeTree tree)
		{
			String[] colNames = new String[] {"name", "value", "direction", "active", "mapping"};
			int[] colWidths = new int[] {250, 150, 200, 75, 75};
			treeTable = new BuildTreeTable(tree, QMOOConfiguration.INTERFACE_OBJECTIVE, colNames.length, colNames, colWidths);
			layoutComponent(treeTable);
		}

		protected void moveDownAction()
		{
		}

		protected void moveUpAction()
		{
		}

        protected BuildTreeTable getBuildTreeTable()
        {
            return treeTable;
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

    public void addNewModelObject(String objectType)
    {
        _variablesTree.stopEditing();
        _objectivesTree.stopEditing();
        _iface.newModelObject(objectType);
    }

    public void addAndMapLastSelection()
	{
		ClipboardSelection sel = BuildMode.clipboard.getLastSelection();
		if (sel == null) return; // nothing in clipboard!
		addAndMapItems(sel.getItems());
	}

    protected void addAndMapItems(List items)
    {
        _variablesTree.stopEditing();
        _objectivesTree.stopEditing();
        items = filterForValidItems(_iface, items);
        try
        {
            if (_variablesTree.isSelectionEmpty() && _objectivesTree.isSelectionEmpty())
            { // paste in top container
                if (_iface instanceof OptimizationInterfaceBuild)
                {
                    ((OptimizationInterfaceBuild) _iface).addAndMapModelObjects(items);
                }
            }
            else
            { // find insertion point
                if (!_variablesTree.isSelectionEmpty())
                {
                    int[] selectedIndices = _variablesTree.getSelectionRows();
                    Arrays.sort(selectedIndices);
                    int selectedIndex = selectedIndices[0];
                    TreePath selectedPath = _variablesTree.getPathForRow(selectedIndex);
                    setCausalityBasedonPath(selectedPath);
                    DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                    if (_variablesTree.isExpanded(selectedPath))
                    {
                        if (_iface instanceof OptimizationInterfaceBuild)
                            ((OptimizationInterfaceBuild)_iface).addAndMapModelObjects(items);
                    }
                    else
                    {
                        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                        int childIndex = parentNode.getIndex(selectedNode);
                        if (_iface instanceof OptimizationInterfaceBuild)
                            ((OptimizationInterfaceBuild)_iface).addAndMapModelObjects(items, childIndex);
                    }

                }
                else
                {
                    int[] selectedIndices = _objectivesTree.getSelectionRows();
                    Arrays.sort(selectedIndices);
                    int selectedIndex = selectedIndices[0];
                    TreePath selectedPath = _objectivesTree.getPathForRow(selectedIndex);
                    setCausalityBasedonPath(selectedPath);
                    DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                    if (_objectivesTree.isExpanded(selectedPath))
                    {
                        if (_iface instanceof OptimizationInterfaceBuild)
                            ((OptimizationInterfaceBuild)_iface).addAndMapModelObjects(items);
                    }
                    else
                    {
                        DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                        int childIndex = parentNode.getIndex(selectedNode);
                        if (_iface instanceof OptimizationInterfaceBuild)
                            ((OptimizationInterfaceBuild)_iface).addAndMapModelObjects(items, childIndex);
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            handleMappingErrors(e);
        }
    }

    public void mapItems(List items)
	{
        _variablesBuildPanel.getBuildTreeTable().stopEditing();
        _objectivesBuildPanel.getBuildTreeTable().stopEditing();

		items = filterForValidItems(_iface, items);
		if (_variablesTree.isSelectionEmpty() && _objectivesTree.isSelectionEmpty())
        { // nothing to map to
            return;
        }
        else if ((_variablesTree.getSelectionCount() + _objectivesTree.getSelectionCount()) != 1)
        { // to many selected
            return;
        }
        try
        {
            if (!_variablesTree.isSelectionEmpty())
            {
                TreePath selectedPath = _variablesTree.getSelectionPath();
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject)node.getTreeObject()).getDomeObject();
                if (dObj instanceof Parameter)
                {
                    ConnectionMappingManager mm = null;
                    Model model = _iface.getModel();
                    if (model instanceof AnalysisToolBase)
                    {
                        if (model instanceof OptimizationToolBuild)
                        {
                            if (((OptimizationToolBuild)model).getOptimizationToolObjectiveParameterMap().containsKey(items.get(0)))
                            {
                                OneButton1Msg.showWarning(this, "Warning: Mapping", "Objective parameter cannot be mapped to " +
                                        "a variable parameter. ", "Ok", OneButton1Msg.DEFAULT_SIZE);
                                return;
                            }
                        }
                        mm = ((AnalysisToolBase) model).getMappingManager();
                    }
                    Collection mappings = mm.getMappingsForParameter((Parameter)dObj);
                    if (mappings == null || mappings.size() == 0)
                        mm.addMappings((Parameter)dObj, items);
                    else
                        OneButton1Msg.showWarning(this, "Warning: Mapping", "Interface parameter cannot be mapped " +
                                "to two model parameters.", "Ok", OneButton1Msg.DEFAULT_SIZE);
                }
            }
            else
            {
                TreePath selectedPath = _objectivesTree.getSelectionPath();
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject)node.getTreeObject()).getDomeObject();
                if (dObj instanceof Parameter)
                {
                    ConnectionMappingManager mm = null;
                    Model model = _iface.getModel();
                    if (model instanceof AnalysisToolBase)
                    {
                        if (model instanceof OptimizationToolBuild)
                        {
                            if (((OptimizationToolBuild)model).getOptimizationToolVariableParameterMap().containsKey(items.get(0)))
                            {
                                OneButton1Msg.showWarning(this, "Warning: Mapping", "Variable parameter cannot be mapped to " +
                                        "an objective parameter. ", "Ok", OneButton1Msg.DEFAULT_SIZE);
                                return;
                            }
                        }
                        mm = ((AnalysisToolBase) model).getMappingManager();
                    }
                    Collection mappings = mm.getMappingsForParameter((Parameter)dObj);
                    if (mappings == null || mappings.size() == 0)
                        mm.addMappings((Parameter)dObj, items);
                    else
                        OneButton1Msg.showWarning(this, "Warning: Mapping", "Interface parameter cannot be mapped" +
                                " to two model parameters. ", "Ok", OneButton1Msg.DEFAULT_SIZE);
                }
            }
        }
        catch (RuntimeException e)
        {
            handleMappingErrors(e);
        }
	}

    public void copySelectedModelObjects()
	{
        List selectedDomeObjects = new ArrayList();
		if (_variablesTree.isSelectionEmpty() && _objectivesTree.isSelectionEmpty()) return;
        if (!_variablesTree.isSelectionEmpty())
        {
            int[] selectionRows = _variablesTree.getSelectionRows();
            Arrays.sort(selectionRows);
            for (int i = 0; i < selectionRows.length; ++i)
            {
                TreePath selectedPath = _variablesTree.getPathForRow(selectionRows[i]);
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
                if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                {
                    selectedDomeObjects.add(dObj);
                }
            }
        }
        if (!_objectivesTree.isSelectionEmpty())
        {
            int[] selectionRows = _objectivesTree.getSelectionRows();
            Arrays.sort(selectionRows);
            for (int i = 0; i < selectionRows.length; ++i)
            {
                TreePath selectedPath = _objectivesTree.getPathForRow(selectionRows[i]);
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
                if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                {
                    selectedDomeObjects.add(dObj);
                }
            }
        }

        if (selectedDomeObjects.isEmpty()) return;
        BuildMode.clipboard.addSelection(selectedDomeObjects);
	}

    public void deleteSelectedModelObjects()
	{
        List selectedDomeObjects = new ArrayList();
		_variablesBuildPanel.getBuildTreeTable().stopEditing();
        _objectivesBuildPanel.getBuildTreeTable().stopEditing();

		if (_variablesTree.isSelectionEmpty() && _objectivesTree.isSelectionEmpty()) return;

        if (!_variablesTree.isSelectionEmpty())
        {
            TreePath[] selectedPaths = _variablesTree.getSelectionPaths();
            for (int i = 0; i < selectedPaths.length; ++i)
            {
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
                if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                {
                    selectedDomeObjects.add(dObj);
                }
                else
                    OneButton1Msg.showWarning(this, "Warning: Delete Operation", "Interface filters cannot be deleted.", "OK", OneButton1Msg.DEFAULT_SIZE);
            }
        }
        if (!_objectivesTree.isSelectionEmpty())
        {
            TreePath[] selectedPaths = _objectivesTree.getSelectionPaths();
            for (int i = 0; i < selectedPaths.length; ++i)
            {
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
                if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                {
                    selectedDomeObjects.add(dObj);
                }
                else
                    OneButton1Msg.showWarning(this, "Warning: Delete Operation", "Interface filters cannot be deleted.", "OK", OneButton1Msg.DEFAULT_SIZE);
            }
        }
		if (selectedDomeObjects.isEmpty()) return; // nothing to delete
		_iface.deleteModelObjects(selectedDomeObjects);
	}

    public void removeSelectedModelObjects()
    {
        List selectedDomeObjects = new ArrayList();

        _variablesBuildPanel.getBuildTreeTable().stopEditing();
        _objectivesBuildPanel.getBuildTreeTable().stopEditing();

        if (_variablesTree.isSelectionEmpty() && _objectivesTree.isSelectionEmpty()) return;

        if (!_variablesTree.isSelectionEmpty())
        {
            TreePath[] selectedPaths = _variablesTree.getSelectionPaths();
            for (int i = 0; i < selectedPaths.length; ++i)
            {
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
                if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                {
                    selectedDomeObjects.add(dObj);
                }
                else
                    OneButton1Msg.showWarning(this, "Warning: Edit", "System filters cannot be cut.", "OK", OneButton1Msg.DEFAULT_SIZE);
            }

        }
        if (!_objectivesTree.isSelectionEmpty())
        {
            TreePath[] selectedPaths = _objectivesTree.getSelectionPaths();
            for (int i = 0; i < selectedPaths.length; ++i)
            {
                DefaultObjectTreeNode node = (DefaultObjectTreeNode) selectedPaths[i].getLastPathComponent();
                DomeObject dObj = ((DomeTreeObject) node.getTreeObject()).getDomeObject();
                if (!selectedDomeObjects.contains(dObj) && !(dObj instanceof Filter))
                {
                    selectedDomeObjects.add(dObj);
                }
                else
                    OneButton1Msg.showWarning(this, "Warning: Edit", "System filters cannot be cut.", "OK", OneButton1Msg.DEFAULT_SIZE);
            }
        }
        if (selectedDomeObjects.isEmpty()) return; // nothing to delete

        if (_iface instanceof OptimizationInterfaceBuild)
        {
            ((OptimizationInterfaceBuild) _iface).removeModelObjects(selectedDomeObjects);
        }
    }

    protected void selectAllVisibleRows()
	{
		_variablesTree.selectAllVisibleRows();
        _objectivesTree.selectAllVisibleRows();
	}

    protected void clearSelection()
    {
        _variablesTree.clearSelection();
        _objectivesTree.clearSelection();
    }

    public void pasteCopies(List items)
	{
		_variablesBuildPanel.getBuildTreeTable().stopEditing();
        _objectivesBuildPanel.getBuildTreeTable().stopEditing();

		items = filterForValidItems(_iface, items);

        if (_variablesTree.isSelectionEmpty() && _objectivesTree.isSelectionEmpty())
            _iface.newModelObjects(items);
        else
        { // find insertion point
            if (!_variablesTree.isSelectionEmpty())
            {
                int[] selectedIndices = _variablesTree.getSelectionRows();
                Arrays.sort(selectedIndices);
                int selectedIndex = selectedIndices[0];
                TreePath selectedPath = _variablesTree.getPathForRow(selectedIndex);
                setCausalityBasedonPath(selectedPath);
                DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                if (_variablesTree.isExpanded(selectedPath))
                {
                    _iface.newModelObjects(items);
                }
                else
                {
                    DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                    int childIndex = parentNode.getIndex(selectedNode);
                    if (_iface instanceof OptimizationInterfaceBuild)
                    {
                        ((OptimizationInterfaceBuild)_iface).newModelObjects(items, childIndex);
                    }
                }
            }
            else if (!_objectivesTree.isSelectionEmpty())
            {
                int[] selectedIndices = _objectivesTree.getSelectionRows();
                Arrays.sort(selectedIndices);
                int selectedIndex = selectedIndices[0];
                TreePath selectedPath = _objectivesTree.getPathForRow(selectedIndex);
                setCausalityBasedonPath(selectedPath);
                DefaultObjectTreeNode selectedNode = (DefaultObjectTreeNode) selectedPath.getLastPathComponent();
                if (_objectivesTree.isExpanded(selectedPath))
                {
                    _iface.newModelObjects(items);
                }
                else
                {
                    DefaultObjectTreeNode parentNode = (DefaultObjectTreeNode) selectedNode.getParent();
                    int childIndex = parentNode.getIndex(selectedNode);
                    if (_iface instanceof OptimizationInterfaceBuild)
                    {
                        ((OptimizationInterfaceBuild)_iface).newModelObjects(items, childIndex);
                    }
                }
            }
        }
	}

    protected void handleMappingErrors(RuntimeException ex)
	{
		System.err.println(ex);
		ex.printStackTrace();
	}

    private void setCausalityBasedonPath(TreePath selectedPath)
	{

	}
}
