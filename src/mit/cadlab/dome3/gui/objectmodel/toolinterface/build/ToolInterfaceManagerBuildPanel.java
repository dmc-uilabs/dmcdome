package mit.cadlab.dome3.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.ModelComponentPanel;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.build.AnalysisToolInterfaceManagerBuild;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 9:22:23 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceManagerBuildPanel extends ModelComponentPanel
{
    public static final GridBagConstraints gbc = null;

    public static final Dimension DEFAULT_SIZE = new Dimension(400, 250);

    public String _menuContext;

    protected AnalysisToolInterfaceManagerBuild _tiBuilder;
    protected BuildTreeTable _treeTable;
    protected BuildTree _tree;
    protected JScrollPane _spane;
    protected JButton _moveUpButton;
    protected JButton _moveDownButton;


    public static DomeFrame createInterfacesTool(AnalysisToolInterfaceManagerBuild mInterfaces, String menuContext)
    {
        return new DomeBuildFrame(new ToolInterfaceManagerBuildPanel(mInterfaces, menuContext));
    }

    public ToolInterfaceManagerBuildPanel(AnalysisToolInterfaceManagerBuild iBuilder, String menuContext)
    {
        super(iBuilder,"Interfaces: " + iBuilder.getModel().getName());
        _tiBuilder = iBuilder;
		_menuContext = menuContext;
		Model tool = iBuilder.getModel();     // model is AnalysisToolModelBuild
		if (tool != null) {
			NameListener nameListener = new NameListener()
			{
				public void nameChanged(String newName)
				{
					Container cont = getTopLevelAncestor();
					((DomeBuildFrame) cont).setTitle("Interfaces: " + newName);
				}
			};
			tool.addPropertyChangeListener(NameListener.NAME, nameListener);
		}

		String[] cols = new String[]{"name", "value"};
		int[] colWidths = new int[]{135, 135};

		_tree = new BuildTree(iBuilder);
		_treeTable = new BuildTreeTable(_tree, cols.length, cols, colWidths, false, false);
		_tree.addTreeSelectionListener(new InterfacesTreeListener());
		_spane = new JScrollPane(_treeTable);
		_spane.getViewport().setBackground(Color.white);
		_spane.setPreferredSize(DEFAULT_SIZE);
		layoutComponents();

    }

    public void setMenuContext()
    {
        MenuManager.setContext(_menuContext);
		BuildFocusTracker.notifyInFocus(this, _tiBuilder);
    }

    public String getHelpContext()
    {
         return "";
    }

    private void layoutComponents()
    {
       _moveUpButton = Templates.makeListArrowButton("up", new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int[] selectedIndices = getSelectedIndices();
                if (selectedIndices == null)
                    return;
                _tiBuilder.moveInterfacesUp(selectedIndices);
                // calculate new indices
                Arrays.sort(selectedIndices);
                int[] newIndices;
                if (selectedIndices[0] == 0)
                { // skip
                    newIndices = new int[selectedIndices.length - 1];
                    for (int j = 1; j < selectedIndices.length; ++j)
                        newIndices[j - 1] = selectedIndices[j] - 1;
                }
                else
                { // shift all indices up
                    newIndices = new int[selectedIndices.length];
                    for (int j = 0; j < selectedIndices.length; ++j)
                        newIndices[j] = selectedIndices[j] - 1;
                }
                _tree.setSelectionRows(newIndices);
            }
        });
        _moveDownButton = Templates.makeListArrowButton("down", new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int[] selectedIndices = getSelectedIndices();
                if (selectedIndices == null)
                    return;
                _tiBuilder.moveInterfacesDown(selectedIndices);
                // calculate new indices
                Arrays.sort(selectedIndices);
                int[] newIndices;
                if (selectedIndices[selectedIndices.length - 1] == _tiBuilder.countInterfaces() - 1)
                { // skip
                    newIndices = new int[selectedIndices.length - 1];
                    for (int j = 0; j < (selectedIndices.length - 1); ++j)
                        newIndices[j] = selectedIndices[j] + 1;
                }
                else
                { // shift all indices down
                    newIndices = new int[selectedIndices.length];
                    for (int j = 0; j < selectedIndices.length; ++j)
                        newIndices[j] = selectedIndices[j] + 1;
                }
                _tree.setSelectionRows(newIndices);
            }
        });

        manipulateShiftSupport();

        JButton closeButton = Templates.makeButton("close", new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // should cleanup by removing DListListener
                dispose();
            }
        });

        JComponent[] comps = {
            _spane,
            _moveUpButton,
            _moveDownButton,
            closeButton
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(0, 2, 3, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(0, 2, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBagB(this, comps, gbcs);
    }


    private int[] getSelectedIndices()
	{
		if (_tree.getSelectionCount() == 0)
			return null;
		int[] selectedIndices = new int[_tree.getSelectionCount()];
		TreePath[] paths = _tree.getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			selectedIndices[i] = getSelectedIndex(paths[i]);
		}
		return selectedIndices;
	}

    private int getSelectedIndex()
	{
		if (_tree.getSelectionCount() == 0)
			return -1;
		return getSelectedIndex(_tree.getSelectionPath());
	}

	private int getSelectedIndex(TreePath path)
	{
		if (path == null)
			return -1;

		int selectionIndex = 0;
		Object ifaceNode = path.getPathComponent(1);
		Collection ifaces = _tiBuilder.getInterfaces();
		for (Iterator iter = ifaces.iterator(); iter.hasNext(); selectionIndex++) {
			ToolInterface iface = (ToolInterface) iter.next();
			if (iface.equals(((BuildObjectTreeNode) ifaceNode).getDomeObject())) {
				break;
			}
		}

		return selectionIndex;
	}

    protected void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}


	private void manipulateShiftSupport()
	{
		if (_tiBuilder.countInterfaces() > 1) {
			_moveUpButton.setEnabled(true);
			_moveDownButton.setEnabled(true);
		} else {
			_moveUpButton.setEnabled(false);
			_moveDownButton.setEnabled(false);
		}
	}

    // --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		/**
		 * Determines the component to use for the action.
		 * This if fetched from the source of the ActionEvent
		 * if it's not null and can be narrowed.  Otherwise,
		 * the last focused component is used.
		 *
		 * @param e the ActionEvent
		 * @return the component
		 */
		protected final ToolInterfaceManagerBuildPanel getToolInterfacesBuildPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof ToolInterfaceManagerBuildPanel) {
					return (ToolInterfaceManagerBuildPanel) o;
				}
			}
			JComponent comp = BuildFocusTracker.getCurrentComponent();
			if (comp instanceof ToolInterfaceManagerBuildPanel)
				return (ToolInterfaceManagerBuildPanel) comp;
			throw new NullPointerException("No current ModelInterfaceManagerBuildPanel");
		}
	}

    // --- actions for menus and buttons --------------------

	public static final AbstractAction newAction = new FocusTrackerAction("New")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolInterfacesBuildPanel(e).newInterface();
		}
	};

	public static final AbstractAction duplicateAction = new FocusTrackerAction("Duplicate")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolInterfacesBuildPanel(e).duplicateSelectedInterfaces();
		}
	};

	public static final AbstractAction clearSelectionAction = new FocusTrackerAction("Clear selection")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolInterfacesBuildPanel(e).clearSelection();
		}
	};

	public static final AbstractAction selectAllAction = new FocusTrackerAction("Select all ")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolInterfacesBuildPanel(e).selectAll();
		}
	};

	public static final AbstractAction deleteAction = new FocusTrackerAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			getToolInterfacesBuildPanel(e).deleteSelectedInterfaces();
		}
	};


	public static final JMenu menu = makeMenu();

	protected static JMenu makeMenu()
	{
		JMenu m = MenuUtils.makeBoldMenu("Interfaces");
		m.add(MenuUtils.makeMenuItem(ToolInterfaceManagerBuildPanel.newAction));
		m.add(MenuUtils.makeMenuItem(ToolInterfaceManagerBuildPanel.duplicateAction));

        //initially
        duplicateAction.setEnabled(false);

		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(ToolInterfaceManagerBuildPanel.clearSelectionAction));
		m.add(MenuUtils.makeMenuItem(ToolInterfaceManagerBuildPanel.selectAllAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(ToolInterfaceManagerBuildPanel.deleteAction));
		return m;
	}

    protected void newInterface()
	{
		if (_tree.getSelectionCount() == 0 || _tree.getSelectionCount() > 1)
        {
            // insert a new interface at the end
            _tiBuilder.newInterface();
        }
        else
        {
            // get the index of the currently selected interface node
            int selectionIndex = getSelectedIndex();
            if (selectionIndex >= 0)
            {
                // insert a new interface before the selected one
                _tiBuilder.newInterface(selectionIndex);
            }
        }
        manipulateShiftSupport();
	}

    protected void clearSelection()
	{
		_tree.clearSelection();
		_spane.requestFocus();
	}

    protected void selectAll()
	{
		_tree.setSelectionInterval(0, _tiBuilder.countInterfaces() - 1);
	}

    protected void duplicateSelectedInterfaces()
    {
        int[] selectedIndices = getSelectedIndices();
		if (selectedIndices == null)
			return;
		_tiBuilder.duplicateInterfaces(selectedIndices);
		manipulateShiftSupport();
    }

    protected void deleteSelectedInterfaces()
	{
		int[] selectedIndices = getSelectedIndices();
		if (selectedIndices == null)
			return;
		_tiBuilder.deleteInterfaces(selectedIndices);
		manipulateShiftSupport();
	}

    protected class InterfacesTreeListener implements TreeSelectionListener
	{
        public void valueChanged(TreeSelectionEvent te)
        {
            TreePath path = te.getNewLeadSelectionPath();
            if (path != null)
            {
                Object obj = path.getLastPathComponent();
                if (obj != null && obj instanceof BuildObjectTreeNode)
                {
                    DomeObject dobject = ((BuildObjectTreeNode) obj).getDomeObject();
                    if (dobject instanceof ToolInterface)
                        ToolInterfaceManagerBuildPanel.duplicateAction.setEnabled(true);
                    else
                        ToolInterfaceManagerBuildPanel.duplicateAction.setEnabled(false);
                }
            }
            else
                ToolInterfaceManagerBuildPanel.duplicateAction.setEnabled(false);
        }
	}
}
