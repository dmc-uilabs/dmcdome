package mit.cadlab.dome3.gui.playspace.run;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 2:51:44 PM
 * To change this template use Options | File Templates.
 */

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.network.client.conference.ConferenceClient;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
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
 * This class is used to browse playspaces in run mode
 */
public class PlayspaceRunPanel extends JPanel implements DomeGui {
    public static final GridBagConstraints gbc = null;
    public static final String PARTNAME = "Playspace: ";

    protected PlayspaceRunFileSystemTable prfst;
    protected ClientPlayspaceRuntime mPlayspace;
    private ServerConnection _svrConn;

    protected JButton submitButton;
    protected JButton conferenceButton;
    protected JButton killModelButton;
    protected JDialog conferenceDialog;
    protected ClientObjectRecord record;
    protected TreeSelectionListener tListener;
    ConferenceClient conferenceClient = null;

    // keep track of which interfaces were opened by this panel
    private DArrayList ifaces = new DArrayList();

    public PlayspaceRunPanel(ServerConnection conn, ClientPlayspaceRuntime mp) {
        this.mPlayspace = mp;
        this._svrConn = conn;
        DomeFile playspaceStaticInfo = new DomeFile(DomeFile.PLAYSPACE_TYPE,
                mp.getCompoundId().getPlayspaceStaticId(),
                mp.getName(),
                mp.getXmlDescription(),
                "",
                mp.getVersion().getSaveVersion());

        prfst = new PlayspaceRunFileSystemTable(conn, playspaceStaticInfo, mPlayspace);
        prfst.addPropertyChangeListener(new InterfaceCreationListener());
        prfst.getTree().addTreeSelectionListener(new PlaySpaceTreeSelectionListener());
        makeGui();
    }

    /**
     * Listens to the playspace table for interface creation events. Interfaces that are
     * created using the playspace run table are recorded so they may be destroyed later
     * when the playspace run gui is closed.
     */
    private class InterfaceCreationListener implements PropertyChangeListener {
        public synchronized void propertyChange(PropertyChangeEvent evt) {
            if (AbstractRunPlayspaceFileSystemTable.PROPERTY_INTERFACES_CREATED.equals(evt.getPropertyName())) {
                List ifaceList = (List) evt.getNewValue();
                for (Iterator iter = ifaceList.iterator(); iter.hasNext();) {
                    ClientInterfaceRecord ifaceRec = (ClientInterfaceRecord) iter.next();
                    //Qing add here: should prevent duplicate entry
                    //reason for duplicate entry: now you can create interface by expand the interface node, or double click to create the interface, however, if you first expand then double click to oepn the interface gui
                    //the same interface will be used but the interface creation property change will be fired(hard to check condition in the SwingWorker) , and i think vice verse
                    if (!ifaces.contains(ifaceRec.getInterface()))
                        ifaces.add(ifaceRec.getInterface());
                }
            }
        }
    }


    public Object getGuiObject() {
        return mPlayspace;
    }

    private void makeGui() {
        submitButton = Templates.makeButton("Submit");
        conferenceButton = Templates.makeButton("Conference");

        conferenceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doConference();
            }
        });
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSubmitAction();
            }
        });
        //Qing change here to remove the submit button-- Mar 13th
        // JComponent[] comps = {conferenceButton, prfst, submitButton};
        JComponent[] comps = {conferenceButton, prfst};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
            //  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0),
            //		new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
        };
        Templates.layoutGridBag(this, comps, gbcs);
        this.setPreferredSize(DomeBuildFrame.DEFAULT_SIZE);
    }

    // DomeGui interface

    public String getTitle() {
        return PARTNAME + mPlayspace.getName();
    }

    public String getHelpContext() {
        return null;
    }


    public void setMenuContext() {
        MenuManager.setContext(ModeContexts.RUN_PLAYSPACE);
    }

    public String getMenuContext() {
        return ModeContexts.RUN_PLAYSPACE;
    }


    public void close() {
        // leave the playspace
        mPlayspace.leavePlayspace();
    }

    public void doConference() {
        if (conferenceDialog == null) {
            conferenceClient = new ConferenceClient(PlayspaceRunPanel.this._svrConn);
            conferenceDialog = conferenceClient.createConferenceGUI(PlayspaceRunPanel.this,
                    PlayspaceRunPanel.this.mPlayspace);
        } else {
            conferenceClient.joinConference();
        }
        conferenceDialog.show();
    }

    public void doSubmitAction() {
        prfst.getClientPlayspaceRuntime().submitChanges();
    }

    public void doOpenAction() {
        if (record != null) {
            if (record instanceof ClientInterfaceRecord) {
                ModelInterfaceRuntimeClient iface = ((ClientInterfaceRecord) record).getInterface();
                ModelInterfaceRunPanel pane = new ModelInterfaceRunPanel(iface);
                DomeRunFrame gui = new DomeRunFrame(pane, (WindowTracker)SwingUtilities.windowForComponent(this));
                gui.show();
            }
        }
    }

    protected void switchView(String view) {
        if (record != null) {
            if (record instanceof ClientInterfaceRecord) {
                ((ClientInterfaceRecord) record).setCurrentView(view);
                record.listChildren();
                RunMenus.checkPlayspaceViewMenu(view);
            }
        }
    }

// --- focus tracking support --------------------
    public static abstract class FocusTrackerAction extends AbstractAction {

        public FocusTrackerAction(String name) {
            super(name);
        }

        protected final PlayspaceRunPanel getPlayspaceRunPanel(ActionEvent e) {
            if (e != null) {
                Object o = e.getSource();
                if (o instanceof PlayspaceRunPanel) {
                    return (PlayspaceRunPanel) o;
                }
            }
            JComponent comp = RunFocusTracker.getCurrentComponent();
            if (comp instanceof PlayspaceRunPanel)
                return (PlayspaceRunPanel) comp;
            /**/System.err.println("No current PlayspaceRunPanel");
            throw new NullPointerException("No current PlayspaceRunPanel");
        }
    }


    // --- actions for menus and buttons --------------------
    public static final AbstractAction openAction = new FocusTrackerAction("Open") {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            runpane.openPlayspaceItem();
        }
    };

    public void openPlayspaceItem() {
        if (record != null) {
            if (record instanceof ClientInterfaceRecord) {
                JFrame waitWin = StatusWindow.show(StatusWindow.STARTING,
                        record.getName(), getLocationOnScreen());
                OpenWorker worker = new OpenWorker(this, waitWin);
                worker.start();
            }
        }
    }

    static class OpenWorker extends SwingWorker {
        PlayspaceRunPanel runpane;
        JFrame waitWin;

        public OpenWorker(PlayspaceRunPanel runpane, JFrame waitWin) {
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

    public static final AbstractAction submitAction = new FocusTrackerAction("Submit") {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            WaitCursorUtils.showWaitCursor(true, runpane);
            runpane.doSubmitAction();
            WaitCursorUtils.showWaitCursor(false, runpane);
        }
    };

    public static final AbstractAction closeAction = new FocusTrackerAction("Close") {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            runpane.close();
            RunFocusTracker.getCurrentWindow().dispose();
        }
    };

    public static final AbstractAction conferenceAction = new FocusTrackerAction("ConferencePanel") {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            runpane.doConference();
        }
    };

    public static final AbstractAction viewBuildAction = new FocusTrackerAction(DomeModelInterface.BUILD_VIEW) {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            runpane.switchView(DomeModelInterface.BUILD_VIEW);
        }
    };

    public static final AbstractAction viewInterfaceCausalityAction = new FocusTrackerAction(DomeModelInterface.INTERFACE_CAUSALITY_VIEW) {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            runpane.switchView(DomeModelInterface.INTERFACE_CAUSALITY_VIEW);
        }
    };

    public static final AbstractAction viewSystemCausalityAction = new FocusTrackerAction(DomeModelInterface.SYSTEM_CAUSALITY_VIEW) {
        public void actionPerformed(ActionEvent e) {
            PlayspaceRunPanel runpane = getPlayspaceRunPanel(e);
            runpane.switchView(DomeModelInterface.SYSTEM_CAUSALITY_VIEW);
        }
    };

    public PlayspaceRunFileSystemTable getPrfst() {
        return prfst;
    }


    class PlaySpaceTreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) prfst.getTree().getLastSelectedPathComponent();
            if (node == null) {
                record = null;
            } else {
                Object owf = node.getTreeNodeObject();
                if (owf instanceof ClientObjectRecord) {
                    record = (ClientObjectRecord) owf;
                    if (record instanceof ClientInterfaceRecord) {
                        String view = ((ClientInterfaceRecord) record).getCurrentView();
                        RunMenus.checkPlayspaceViewMenu(view);
                    }
                }
            }
        }
    }
}
