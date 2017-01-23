package mit.cadlab.dome3.gui.objectmodel.model.tool.run;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolInterfaceRecord;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationToolInterfaceRunPanel;
import mit.cadlab.dome3.gui.objectmodel.project.run.ProjectRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 26, 2004
 * Time: 2:58:50 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolRunPanel extends JPanel implements DomeGui
{
    public static final GridBagConstraints gbc = null;

    protected static final String PARTNAME = "Analysis Tool: ";

    protected AnalysisToolRunFileSystemTable _prfst;
    protected ServerConnection _svrConn;
    protected ClientPlayspaceRuntime _playspace;
    protected OptimizationToolClientRuntime _analysisTool;
    protected ClientObjectRecord _record;

    private DArrayList _ifaces = new DArrayList();
    protected JButton _submitButton, _closeButton;

    public AnalysisToolRunPanel(ServerConnection conn, OptimizationToolClientRuntime analysisTool,
	                       ClientPlayspaceRuntime playspace)
	{
		_svrConn = conn;
		_playspace =  playspace;
		_analysisTool = analysisTool;
		_analysisTool.incrementReferenceCount();
		DomeFile analysisToolStaticInfo = new DomeFile(DomeFile.ANALYSIS_TOOL_TYPE,
		                                          _analysisTool.getRuntimeId().getModelStaticId(),
		                                          _analysisTool.getName(),
		                                          "",
		                                          _svrConn.getServerPort(),
		                                          "",
		                                          _analysisTool.getVersion().getSaveVersion());

		_prfst = new AnalysisToolRunFileSystemTable(conn, analysisToolStaticInfo, _analysisTool, playspace);
		_prfst.addPropertyChangeListener(new InterfaceCreationListener());
		_prfst.getTree().addTreeSelectionListener(new AnalysisToolTreeSelectionListener());
		makeGui();
	}

    private class InterfaceCreationListener implements PropertyChangeListener
	{
		public synchronized void propertyChange(PropertyChangeEvent evt)
		{
			if (AbstractAnalysisToolRunFileSystemTable.PROPERTY_INTERFACES_CREATED.equals(evt.getPropertyName())) {
				List ifaceList = (List) evt.getNewValue();
				for (Iterator iter = ifaceList.iterator(); iter.hasNext();) {
					ClientInterfaceRecord ifaceRec = (ClientInterfaceRecord) iter.next();
					_ifaces.add(ifaceRec.getInterface());
				}
			}
		}
	}

    class AnalysisToolTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) _prfst.getTree().getLastSelectedPathComponent();
            if (node == null)
            {
                _record = null;
            }
            else
            {
                Object owf = node.getTreeNodeObject();
                if (owf instanceof ClientObjectRecord)
                {
                    _record = (ClientObjectRecord) owf;
                    if (_record instanceof ClientInterfaceRecord)
                    {
                        String view = ((ClientInterfaceRecord) _record).getCurrentView();
                        RunMenus.checkAnalysisToolViewMenu(view);
                    }
                    else if (_record instanceof ClientAnalysisToolInterfaceRecord)
                    {
                        String view = ((ClientAnalysisToolInterfaceRecord)_record).getCurrentView();
                        RunMenus.checkAnalysisToolViewMenu(view);
                    }
                }
            }
		}
	}

    private void makeGui()
	{
		_submitButton = Templates.makeButton("Start Optimization");
		_submitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				startAnalysisToolSolving();
			}
		});

        _closeButton = Templates.makeButton("Close");
        _closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                closeGui();
            }
        });

		JComponent[] comps = {_prfst, _submitButton, _closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
		this.setPreferredSize(DomeBuildFrame.DEFAULT_SIZE);
	}

    public void startAnalysisToolSolving()
    {
        AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) _prfst.getTree().getLastSelectedPathComponent();
        if (node == null)
            OneButton1Msg.showWarning(this, "Runtime Warning", "Please select an analysis tool interface before you begin solving.", "OK", OneButton1Msg.DEFAULT_SIZE);
        else
        {
            Object owf = node.getTreeNodeObject();
            if (owf instanceof ClientAnalysisToolInterfaceRecord)
            {
                _record = (ClientObjectRecord) owf;
                OptimizationInterfaceRuntimeClient iface = ((ClientAnalysisToolInterfaceRecord)_record).getInterface();
                _record = null;
                OptimizationToolInterfaceRunPanel pane = new OptimizationToolInterfaceRunPanel(iface);
                DomeRunFrame gui = new DomeRunFrame(pane, (WindowTracker)SwingUtilities.windowForComponent(this));
                gui.show();
                pane.startOptimizationAction();
            }
            else
                OneButton1Msg.showWarning(this, "Runtime Warning", "You have not selected an analysis tool interface.  \n" +
                        "Please select an analysis tool interface before you begin solving.", "OK", OneButton1Msg.DEFAULT_SIZE);
        }
    }

    public void closeGui()
    {
        close();
        RunFocusTracker.getCurrentWindow().dispose();
    }

    public void openAnalysisToolItemAction()
    {
        if (_record != null)
        {
            if (_record instanceof ClientInterfaceRecord)
            {
                ModelInterfaceRuntimeClient iface = ((ClientInterfaceRecord) _record).getInterface();
                _record = null;
                ModelInterfaceRunPanel pane = new ModelInterfaceRunPanel(iface);
                DomeRunFrame gui = new DomeRunFrame(pane, (WindowTracker) SwingUtilities.windowForComponent(this));
                gui.show();
            }
            else if (_record instanceof ClientAnalysisToolInterfaceRecord)
            {
                OptimizationInterfaceRuntimeClient iface = ((ClientAnalysisToolInterfaceRecord)_record).getInterface();
                _record = null;
                OptimizationToolInterfaceRunPanel pane = new OptimizationToolInterfaceRunPanel(iface);
                DomeRunFrame gui = new DomeRunFrame(pane, (WindowTracker) SwingUtilities.windowForComponent(this));
                gui.show();
            }
            else if (_record instanceof ClientProjectRecord)
            {
                ProjectRunPanel iGui = new ProjectRunPanel(_svrConn, ((ClientProjectRecord)_record).getProject(), _playspace);
                DomeRunFrame newFrame = new DomeRunFrame(iGui, (WindowTracker) SwingUtilities.windowForComponent(this));
                newFrame.show();
            }
        }

    }

    public void openAnalysisToolItem()
    {
        if (_record != null)
        {
            if (_record instanceof ClientInterfaceRecord || _record instanceof ClientAnalysisToolInterfaceRecord || _record instanceof ClientProjectRecord)
            {
                JFrame waitWin = StatusWindow.show(StatusWindow.STARTING,
                        _record.getName(), getLocationOnScreen());
                OpenWorker worker = new OpenWorker(this, waitWin);
                worker.start();
            }
        }
    }

    public void close()
    {
	    DomeRunFrame projFrame = (DomeRunFrame) SwingUtilities.windowForComponent(this);
	    if (projFrame.isTopLevelRunFrame())
		    RuntimeFunctionsClient.killAnalysisTool(_svrConn, _analysisTool.getRuntimeId());
    }

    public Object getGuiObject()
    {
        return null;
    }

    public String getHelpContext()
    {
        return null;
    }

    public String getTitle()
    {
        return PARTNAME + _analysisTool.getName();
    }

    public void setMenuContext()
    {
        MenuManager.setContext(ModeContexts.RUN_ANALYSIS_TOOL);
    }

    public String getMenuContext()
    {
        return ModeContexts.RUN_ANALYSIS_TOOL;
    }

    public void switchView(String view)
    {

    }

// --- focus tracking support --------------------
    public static abstract class FocusTrackerAction extends AbstractAction
    {

        public FocusTrackerAction(String name)
        {
            super(name);
        }

        protected final AnalysisToolRunPanel getAnalysisToolRunPanel(ActionEvent e)
        {
            if (e != null) {
                Object o = e.getSource();
                if (o instanceof AnalysisToolRunPanel) {
                    return (AnalysisToolRunPanel) o;
                }
            }
            JComponent comp = RunFocusTracker.getCurrentComponent();
            if (comp instanceof AnalysisToolRunPanel)
                return (AnalysisToolRunPanel) comp;
            /**/System.err.println("No current ProjectRunPanel");
            throw new NullPointerException("No current ProjectRunPanel");
        }
    }


    // --- actions for menus and buttons --------------------
    public static final AbstractAction openAction = new FocusTrackerAction("Open")
    {
        public void actionPerformed(ActionEvent e)
        {
            AnalysisToolRunPanel runpane = getAnalysisToolRunPanel(e);
            runpane.openAnalysisToolItem();
        }
    };

    public static final AbstractAction submitAction = new FocusTrackerAction("Start Solving")
    {
        public void actionPerformed(ActionEvent e)
        {
            AnalysisToolRunPanel runpane = getAnalysisToolRunPanel(e);
            runpane.startAnalysisToolSolving();
        }
    };

    public static final AbstractAction closeAction = new FocusTrackerAction("Close")
    {
        public void actionPerformed(ActionEvent e)
        {
            AnalysisToolRunPanel runpane = getAnalysisToolRunPanel(e);
	        DomeFrame frame = (DomeFrame) SwingUtilities.windowForComponent(runpane);
	        frame.selfClose();
        }
    };

    public static final AbstractAction viewBuildAction = new FocusTrackerAction(ToolInterface.BUILD_VIEW)
    {
        public void actionPerformed(ActionEvent e)
        {
            AnalysisToolRunPanel runpane = getAnalysisToolRunPanel(e);
            runpane.switchView(ToolInterface.BUILD_VIEW);
        }
    };

    public static final AbstractAction viewInterfaceCausalityAction = new FocusTrackerAction(ToolInterface.INTERFACE_CAUSALITY_VIEW)
    {
        public void actionPerformed(ActionEvent e)
        {
            AnalysisToolRunPanel runpane = getAnalysisToolRunPanel(e);
            runpane.switchView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
        }
    };

    static class OpenWorker extends SwingWorker {
        AnalysisToolRunPanel runpane;
        JFrame waitWin;

        public OpenWorker(AnalysisToolRunPanel runpane, JFrame waitWin) {
            this.runpane = runpane;
            this.waitWin = waitWin;
        }

        public Object construct() {
            runpane.openAnalysisToolItemAction();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }

    }
}
