package mit.cadlab.dome3.gui.objectmodel.project.run;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to browse and run projects in run mode
 */
public class ProjectRunPanel extends JPanel implements DomeGui
{
	public static final GridBagConstraints gbc = null;
	public static final String PARTNAME = "Integration Project: ";

	protected ProjectRunFileSystemTable prfst;
	protected IntegrationProjectClientRuntime mProject;
	protected ClientPlayspaceRuntime playspace;
	private ServerConnection _svrConn;

	protected JButton submitButton;
	protected ClientObjectRecord record;
	protected TreeSelectionListener tListener;
	// keep track of which interfaces were opened by this panel
	private DArrayList ifaces = new DArrayList();

	public ProjectRunPanel(ServerConnection conn, IntegrationProjectClientRuntime project,
	                       ClientPlayspaceRuntime playspace)
	{
		this._svrConn = conn;
		this.playspace =  playspace;
		this.mProject = project;
		//this.mProject.joinProject();  //for project on server side
		DomeFile projectStaticInfo = new DomeFile(DomeFile.PROJECT_TYPE,
		                                          mProject.getRuntimeId().getCurrentProjectStaticId(),
		                                          mProject.getName(),
		                                          "",
		                                          _svrConn.getServerPort(),
		                                          "",
		                                          mProject.getVersion().getSaveVersion());

		prfst = new ProjectRunFileSystemTable(conn, projectStaticInfo, mProject, playspace);
		prfst.addPropertyChangeListener(new InterfaceCreationListener());
 		prfst.getTree().addTreeSelectionListener(new ProjectTreeSelectionListener());
		makeGui();
	}



	public Object getGuiObject()
	{
		return mProject;
	}

	private void makeGui()
	{
		submitButton = Templates.makeButton("Submit");
		submitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				doSubmitAction();
			}
		});

		//Do not show the submit button for now as the project GUI doesn't work properly.
		JComponent[] comps = {prfst}; //, submitButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
//			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(this, comps, gbcs);
		this.setPreferredSize(DomeBuildFrame.DEFAULT_SIZE);
	}

	// DomeGui interface

	public String getTitle()
	{
		return PARTNAME + mProject.getName();
	}

	public String getHelpContext()
	{
		return null;
	}


	public void setMenuContext()
	{
		MenuManager.setContext(ModeContexts.RUN_PROJECT);
	}

	public String getMenuContext()
	{
		return ModeContexts.RUN_PROJECT;
	}


	public void close()
	{
		DomeRunFrame projFrame = (DomeRunFrame) SwingUtilities.windowForComponent(this);
		if (projFrame.isTopLevelRunFrame())
			if (mProject != null)
				mProject.killProject();
	}

	public void doSubmitAction()
	{
		mProject.submitChanges();
	}

	public void doOpenAction()
	{
		if (record != null) {
			if (record instanceof ClientInterfaceRecord) {
                ModelInterfaceRuntimeClient iface = ((ClientInterfaceRecord) record).getInterface();
				record = null;
				ModelInterfaceRunPanel pane = new ModelInterfaceRunPanel(iface);
				DomeRunFrame gui = new DomeRunFrame(pane, (WindowTracker)SwingUtilities.windowForComponent(this));
				gui.show();
			}
		}
	}

    static class OpenWorker extends SwingWorker {
        ProjectRunPanel runpane;
        JFrame waitWin;

        public OpenWorker(ProjectRunPanel runpane, JFrame waitWin) {
            this.runpane = runpane;
            this.waitWin = waitWin;
        }

        public Object construct() {
            runpane.doOpenAction();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }

    }

	protected void switchView(String view)
	{
		if (record != null) {
			if (record instanceof ClientInterfaceRecord) {
				((ClientInterfaceRecord) record).setCurrentView(view);
				record.listChildren();
				RunMenus.checkProjectViewMenu(view);
			}
		}
	}

	/**
	 * Listens to the playspace table for interface creation events. Interfaces that are
	 * created using the playspace run table are recorded so they may be destroyed later
	 * when the playspace run gui is closed.
	 */
	private class InterfaceCreationListener implements PropertyChangeListener
	{
		public synchronized void propertyChange(PropertyChangeEvent evt)
		{
			if (AbstractRunProjectFileSystemTable.PROPERTY_INTERFACES_CREATED.equals(evt.getPropertyName())) {
				List ifaceList = (List) evt.getNewValue();
				for (Iterator iter = ifaceList.iterator(); iter.hasNext();) {
					ClientInterfaceRecord ifaceRec = (ClientInterfaceRecord) iter.next();
                    //Qing add here: should prevent duplicate entry
                    //reason for duplicate entry: now you can create interface by expand the interface node, or double click to create the interface, however, if you first expand then double click to oepn the interface gui
                    //the same interface will be used but the interface creation property change will be fired(hard to check condition in the SwingWorker) , and i think vice verse
                    if(!ifaces.contains(ifaceRec.getInterface()))   ifaces.add(ifaceRec.getInterface());
				}
			}
		}
	}

// --- focus tracking support --------------------
	public static abstract class FocusTrackerAction extends AbstractAction
	{

		public FocusTrackerAction(String name)
		{
			super(name);
		}

		protected final ProjectRunPanel getProjectRunPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof ProjectRunPanel) {
					return (ProjectRunPanel) o;
				}
			}
			JComponent comp = RunFocusTracker.getCurrentComponent();
			if (comp instanceof ProjectRunPanel)
				return (ProjectRunPanel) comp;
			/**/System.err.println("No current ProjectRunPanel");
			throw new NullPointerException("No current ProjectRunPanel");
		}
	}


	// --- actions for menus and buttons --------------------
	public static final AbstractAction openAction = new FocusTrackerAction("Open")
	{
		public void actionPerformed(ActionEvent e)
		{
			ProjectRunPanel runpane = getProjectRunPanel(e);
			runpane.openProjectItem();
		}
	};

    public void openProjectItem() {
        if (record != null) {
            if (record instanceof ClientInterfaceRecord) {
                JFrame waitWin = StatusWindow.show(StatusWindow.STARTING,
                        record.getName(), getLocationOnScreen());
                OpenWorker worker = new OpenWorker(this, waitWin);
                worker.start();
            }
        }
    }

	public static final AbstractAction submitAction = new FocusTrackerAction("Submit")
	{
		public void actionPerformed(ActionEvent e)
		{
			ProjectRunPanel runpane = getProjectRunPanel(e);
			runpane.doSubmitAction();
		}
	};

	public static final AbstractAction closeAction = new FocusTrackerAction("Close")
	{
		public void actionPerformed(ActionEvent e)
		{
			ProjectRunPanel runpane = getProjectRunPanel(e);
			runpane.close();
			RunFocusTracker.getCurrentWindow().dispose();
		}
	};

	public static final AbstractAction viewBuildAction = new FocusTrackerAction(DomeModelInterface.BUILD_VIEW)
	{
		public void actionPerformed(ActionEvent e)
		{
			ProjectRunPanel runpane = getProjectRunPanel(e);
			runpane.switchView(DomeModelInterface.BUILD_VIEW);
		}
	};

	public static final AbstractAction viewInterfaceCausalityAction = new FocusTrackerAction(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)
	{
		public void actionPerformed(ActionEvent e)
		{
			ProjectRunPanel runpane = getProjectRunPanel(e);
			runpane.switchView(DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
		}
	};

	public static final AbstractAction viewSystemCausalityAction = new FocusTrackerAction(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)
	{
		public void actionPerformed(ActionEvent e)
		{
			ProjectRunPanel runpane = getProjectRunPanel(e);
			runpane.switchView(DomeModelInterface.SYSTEM_CAUSALITY_VIEW);
		}
	};

    public ProjectRunFileSystemTable getPrfst() {
        return prfst;
    }

    class ProjectTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) prfst.getTree().getLastSelectedPathComponent();
			if (node == null) {
				record = null;
			} else {
				Object owf = node.getTreeNodeObject();
				if (owf instanceof ClientObjectRecord) {
					record = (ClientObjectRecord) owf;
					if (record instanceof ClientInterfaceRecord) {
						String view = ((ClientInterfaceRecord) record).getCurrentView();
						RunMenus.checkProjectViewMenu(view);
					}
				}
			}
		}
	}
}
