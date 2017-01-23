//ModelInterfaceTreeBuilderPanel.java

package mit.cadlab.dome3.gui.objectmodel.modelinterface;

import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

public class ModelInterfaceTreePanel extends JPanel
{
	protected ModelInterface iface;
	protected ModelInterfaceTree tree;
	protected RunTreeTable treeTable;

	public ModelInterfaceTreePanel(ModelInterface iface,
	                               String view)
	{
		this.iface = iface;
		tree = new ModelInterfaceTree(new RunObjectTreeNode(iface, view), view, false);
		treeTable = new RunTreeTable(tree);
		layoutComponent(treeTable);
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
		tree.setPaths(view);
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

		protected final ModelInterfaceRunPanel getModelInterfaceRunPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof ModelInterfaceRunPanel) {
					return (ModelInterfaceRunPanel) o;
				}
			}
			JComponent comp = RunFocusTracker.getCurrentComponent();
			if (comp instanceof ModelInterfaceRunPanel)
				return (ModelInterfaceRunPanel) comp;
			/**/System.err.println("No current ModelInterfaceRunPanel");
			throw new NullPointerException("No current ModelInterfaceRunPanel");
		}
	}


	// --- actions for menus and buttons --------------------
	public static final AbstractAction submitAction = new FocusTrackerAction("Submit")
	{
		public void actionPerformed(ActionEvent e)
		{
			ModelInterfaceRunPanel runpane = getModelInterfaceRunPanel(e);
			runpane.doSubmitAction();
		}
	};

	public static final AbstractAction pauseResumeAction = new FocusTrackerAction("Pause/Resume")
	{
		public void actionPerformed(ActionEvent e)
		{
			ModelInterfaceRunPanel runpane = getModelInterfaceRunPanel(e);
			runpane.doPlayPauseAction();
		}
	};

	public static final AbstractAction saveVerAction = new FocusTrackerAction("Save version")
	{
		public void actionPerformed(ActionEvent e)
		{
//TODO
			System.out.println("Run Mode: Interface -> Save version action called");
		}
	};

	public static final AbstractAction saveAction = new FocusTrackerAction("Save")
	{
		public void actionPerformed(ActionEvent e)
		{
//TODO
			System.out.println("Run Mode: Interface -> Save action called");
		}
	};

	public static final AbstractAction loadVerAction = new FocusTrackerAction("Load version")
	{
		public void actionPerformed(ActionEvent e)
		{
//TODO
			System.out.println("Run Mode: Interface -> Load version action called");
		}
	};

	public static final AbstractAction closeAction = new FocusTrackerAction("Close")
	{
		public void actionPerformed(ActionEvent e)
		{
			ModelInterfaceRunPanel runpane = getModelInterfaceRunPanel(e);
			DomeFrame frame = (DomeFrame)SwingUtilities.windowForComponent(runpane);
			frame.selfClose();
		}
	};

}
