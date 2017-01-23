// RunMode.java
package mit.cadlab.dome3.gui.mode.run;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.bookmark.BookmarkCache;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseInterfaceDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.Mode;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.model.tool.run.AnalysisToolRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.gui.objectmodel.project.run.ProjectRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationToolInterfaceRunPanel;
import mit.cadlab.dome3.gui.playspace.run.PlayspaceRunPanel;
import mit.cadlab.dome3.gui.runbrowser.RunBrowser;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.DomeRuntimeClient;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

public class RunMode implements Mode {

    public static final String NAME = "Run";
    public static final String BUILDVIEW = "Build";
    public static final String SYSTEMCAUSALITYVIEW = "System Causality";
    public static final String INTERFACECAUSALITYVIEW = "Interface Causality";
    public static final RunModeWindowTracker windows = new RunModeWindowTracker();
    public static int clientPort = DomeClientApplication.getClientPort();
    public static DomeRuntimeClient drClient = null;

    public static final DomeFileChooser runFileChooser = new DomeFileChooser(); // file chooser for saving analysis tool results at runtime

    public static WindowTracker getWindowTracker() {
        return windows;
    }

	public static WindowTracker getCurrentWindowTracker()
	{
		Window w = RunFocusTracker.getCurrentWindow();
		if (w instanceof DomeRunFrame) {
			if (((DomeRunFrame) w).getGui() instanceof RunBrowser)
				return RunMode.getWindowTracker();
		}
		if (w instanceof WindowTracker)
			return (WindowTracker) w;
		return RunMode.getWindowTracker();
	}

    public static Point getWindowLocation() {
        JComponent comp = RunFocusTracker.getCurrentComponent();
        if (comp == null) { // place in top left corner
            return new Point(0, DomeClientApplication.getBottomCoordinate());
        } else { // place offset to window of component
            Window win = RunFocusTracker.getCurrentWindow();
            if (win instanceof DomeRunFrame) {
                Point p = win.getLocationOnScreen();
                return new Point(p.x + 25, p.y + 25);
            } else { // what is it? place in top left corner
                return new Point(0, DomeClientApplication.getBottomCoordinate());
            }
        }
    }

    public static DomeRunFrame getCurrentModelFrame() {
        return (DomeRunFrame) RunFocusTracker.getCurrentWindow();
    }

    public static String getClientUrl() {
        if (drClient == null)
            drClient = new DomeRuntimeClient(clientPort);
        return drClient.getClientUrl();
    }

    // Mode interface
    public static String name() {
        return NAME;
    }

    public static void show() {
        windows.showAll();
    }

    public static void hide() {
        windows.hideAll();
    }

    public static void close() {
        windows.closeAll();
        MenuManager.setContext(ModeContexts.RUN_MODE);
    }

    public static void exit() {
        closeAll();
    }

    public static void closeAll() {
        windows.closeAll();
        MenuManager.setContext(ModeContexts.RUN_MODE);
        //ModelBuilderFrame.count=0;
    }

    //for broswer-->open menu option
    public static void open_in_browser() {
        JComponent currentComponent = RunFocusTracker.getCurrentComponent();
        if (currentComponent instanceof RunBrowser) {
            RunBrowser runBrowser = (RunBrowser) currentComponent;

            ServerConnection svrConn = runBrowser.getCurrentServerConnection();
            Object obj = runBrowser.getCurrentSelectedObject();
            if (svrConn == null || obj == null || !(obj instanceof DomeFile))
                return;

            JFrame waitWin = StatusWindow.show(StatusWindow.STARTING,
                    ((DomeFile) obj).getName(), currentComponent.getLocationOnScreen());
            openInBrowserWorker worker = new openInBrowserWorker(runBrowser, waitWin, ((DomeFile) obj).getName());
            worker.start();
        }
    }

    private static void startPlayspace(ServerConnection svrConn, CompoundId cid) {
        ClientPlayspaceRuntime playspace = drClient.joinPlayspace(cid.getPlayspaceStaticId(), svrConn);
        PlayspaceRunPanel iGui = new PlayspaceRunPanel(svrConn, playspace);
        DomeRunFrame newFrame = new DomeRunFrame(iGui, RunMode.getWindowTracker());
        newFrame.show();
    }

    private static void startProject(ServerConnection svrConn, CompoundId cid) {
        ClientPlayspaceRuntime playspace = drClient.createTransientPlayspace(svrConn);
        IntegrationProjectClientRuntime project = drClient.createProject(cid, playspace, svrConn);
        ProjectRunPanel iGui = new ProjectRunPanel(svrConn, project, playspace);
        DomeRunFrame newFrame = new DomeRunFrame(iGui, RunMode.getWindowTracker());
        newFrame.show();
    }

    private static void startAnalysisTool(ServerConnection svrConn, CompoundId cid) { // todo: make this method more general for all analysis tools
        ClientPlayspaceRuntime playspace = drClient.createTransientPlayspace(svrConn);
        OptimizationToolClientRuntime analysisTool = drClient.createOptimizationTool(cid, playspace, svrConn);
        AnalysisToolRunPanel iGui = new AnalysisToolRunPanel(svrConn, analysisTool, playspace);
        DomeRunFrame newFrame = new DomeRunFrame(iGui, RunMode.getWindowTracker());
        newFrame.show();
    }

    /**
     * Start interface in a non-transient playspace
     * @param playspaceSvrConn
     * @param ifaceSvrConn
     * @param interfaceId
     * @param runBrowser
     */
    private static void startAnalysisToolInterface(ServerConnection playspaceSvrConn,
                                                   ServerConnection ifaceSvrConn,
                                                   CompoundId interfaceId,
                                                   RunBrowser runBrowser) {
//      TODO:
//      TODO: in the future we want to use a general parent object,
//      TODO: for now we will use the OptimizationInterfaceRuntimeClient object
//      TODO:

        OptimizationInterfaceRuntimeClient interfaceClient = null;

	    ClientPlayspaceRuntime playspace = drClient.createTransientPlayspace(ifaceSvrConn);

	    interfaceClient = playspace.getOptimizationToolInterface(interfaceId, ifaceSvrConn);

	    if (interfaceClient != null) {
		    if (restoreAnalysisToolInterfacePanel(interfaceClient)) return;
	    } else {
		    interfaceClient = drClient.createAnalysisToolInterface(interfaceId, playspace, ifaceSvrConn);
	    }
        //TODO:
        //TODO: this will change of course
        //TODO:

        OptimizationToolInterfaceRunPanel iGui = new OptimizationToolInterfaceRunPanel(interfaceClient);

        DomeRunFrame newFrame = new DomeRunFrame(iGui, RunMode.getWindowTracker());

        newFrame.show();
    }

    /**
     * Start interface in a non-transient playspace.
     * @param playspaceSvrConn Playspace server connection
     * @param ifaceSvrConn Interface server connection
     * @param interfaceId Interface id
     */
    private static void startInterface(ServerConnection playspaceSvrConn,
                                       ServerConnection ifaceSvrConn,
                                       CompoundId interfaceId,
                                       RunBrowser runBrowser) {
        ModelInterfaceRuntimeClient interfaceClient = null;
	    // create predefined playspace
	    ClientPlayspaceRuntime playspace = null;
	    if (playspaceSvrConn != null) {
		    playspace = drClient.joinPlayspace(interfaceId.getPlayspaceStaticId(), playspaceSvrConn);
		    interfaceId.setPlayspaceRuntimeId(playspace.getRuntimeId());
	    }

	    boolean isProjectResource = false;
	    // create project
	    if (interfaceId.getFirstProjectStaticId() != null) {
		    ServerConnection projectSvrConn = runBrowser.getTopLevelProjectConnection(ifaceSvrConn);
		    if (playspace == null)
			    playspace = drClient.createTransientPlayspace(projectSvrConn);
		    isProjectResource = (interfaceId.getModelStaticId() != null); // imodel or resource interface
	    }

	    // create transient playspace
	    if (playspace == null) {
		    playspace = drClient.createTransientPlayspace(ifaceSvrConn);
	    }

	    // try to get the interface and restore an existing interface panel
	    interfaceClient = playspace.getInterface(interfaceId, ifaceSvrConn);
	    if (interfaceClient != null) {
		    if (restoreInterfacePanel(interfaceClient))
			    return;
	    } else {
		    // create a new interface
		    interfaceClient = drClient.createInterface(interfaceId, playspace, ifaceSvrConn, isProjectResource);
	    }

        // shows the interface runtime panel
        ModelInterfaceRunPanel iGui = new ModelInterfaceRunPanel(interfaceClient);
        DomeRunFrame newFrame = new DomeRunFrame(iGui, RunMode.getWindowTracker());
        newFrame.show();
    }

    //for broswer-->open in new playspace menu option
    public static void open_in_playspace() {

    }

    //for broswer-->close menu option
    public static void close_broswer() {
        if (RunFocusTracker.getCurrentWindow() instanceof DomeRunFrame) {
            ((DomeRunFrame) RunFocusTracker.getCurrentWindow()).selfClose();
        }
    }


    //for view-->build menu option
    public static void viewbuild() {

        JComponent currentComponent = RunFocusTracker.getCurrentComponent();
        if (currentComponent instanceof RunBrowser) {
            RunBrowser runBrowser = (RunBrowser) currentComponent;
            runBrowser.setView(BUILDVIEW);
        }
    }

    //for view-->interface causality menu option
    public static void view_interface_causality() {
        JComponent currentComponent = RunFocusTracker.getCurrentComponent();
        if (currentComponent instanceof RunBrowser) {
            RunBrowser runBrowser = (RunBrowser) currentComponent;
            runBrowser.setView(INTERFACECAUSALITYVIEW);
        }

    }

    //for view-->system causality menu option
    public static void view_system_causality() {
        JComponent currentComponent = RunFocusTracker.getCurrentComponent();
        if (currentComponent instanceof RunBrowser) {
            RunBrowser runBrowser = (RunBrowser) currentComponent;
            runBrowser.setView(SYSTEMCAUSALITYVIEW);
        }

    }

    public static void newWindow() {
        RunBrowser rb = new RunBrowser(ServerPanel.RUN_BROWSE);
        DomeRunFrame newFrame = new DomeRunFrame(rb, RunMode.getWindowTracker());
        newFrame.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());

        newFrame.show();
    }


    private static boolean restoreInterfacePanel(ModelInterfaceRuntimeClient interfaceClient) {
        // get the compound id from new interface
        CompoundId interfaceClientStaticId = new CompoundId(interfaceClient.getRuntimeId());
        String interfaceClientplayspaceRuntimeId = new String(interfaceClientStaticId.getPlayspaceRuntimeId());
        interfaceClientStaticId.resetRuntimeInfo();

        List children = windows.getChildren();
        for (Iterator ifacePanels = children.iterator(); ifacePanels.hasNext();) {
            DomeRunFrame frame = (DomeRunFrame) ifacePanels.next();
            if (frame.getGui() instanceof ModelInterfaceRunPanel) {
                ModelInterfaceRunPanel iGui = (ModelInterfaceRunPanel) frame.getGui();
                ModelInterfaceRuntimeClient iface = (ModelInterfaceRuntimeClient) iGui.getModelInterface();

                // get the compound id the old interface
                CompoundId ifaceStaticId = new CompoundId(iface.getRuntimeId());
                String playspaceRuntimeId = ifaceStaticId.getPlayspaceRuntimeId();
                ifaceStaticId.resetRuntimeInfo();

                // if the static compound ids and playspace runtime ids are the same, this is the same interface
                if (interfaceClientStaticId.equals(ifaceStaticId) &&
                        interfaceClientplayspaceRuntimeId.equals(playspaceRuntimeId)) {
                    frame.show();
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean restoreAnalysisToolInterfacePanel(OptimizationInterfaceRuntimeClient interfaceClient) {
        return false;
    }


    //Qing add for bookmark
    public static void addBookmark() {

        JComponent currentComponent = RunFocusTracker.getCurrentComponent();
        if (currentComponent instanceof RunBrowser) {
            RunBrowser runBrowser = (RunBrowser) currentComponent;
            ServerConnection svrConn = runBrowser.getCurrentServerConnection();
            Object obj = runBrowser.getCurrentSelectedObject();
            if (svrConn == null || obj == null)
                return;

            String spaceType = runBrowser.getCurrentFilterType(); // model or playspace
            if (ServerPanel.MODEL.equals(spaceType)) {
                // start the interface in "model space"

                if (obj instanceof DomeFile) {
                    DomeFile f = (DomeFile) obj;
                    if (f.getFileType().equals(DomeFile.INTERFACE_TYPE)) {
                        String type = DomeFile.INTERFACE_TYPE;
                        if (runBrowser.isAnalysisToolParent())
                            type = BookmarkCache.ANALYSIS_Interface;
                        else if (runBrowser.isProjectParent()) type = BookmarkCache.Project_Interface;
                        CompoundId interfaceId = runBrowser.getCurrentObjectId();
                        BrowseInterfaceDomeFile ifaceFile = (BrowseInterfaceDomeFile) obj;
                        ServerConnection ifaceSvrConn;
                        ifaceSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, ifaceFile.getUrl());
                        if (type.equals(BookmarkCache.Project_Interface)) {
                            ServerConnection projectSvrConn = runBrowser.getTopLevelProjectConnection(ifaceSvrConn);
//copy path info
                            String path = runBrowser.getCurrentFileSystemTable().getSelectedPath();
                            BookmarkCache.addBookmark(type, interfaceId.toString(), ifaceFile.getName(), ifaceSvrConn.getServerPort() + BookmarkCache.infoSeperator + projectSvrConn.getServerPort(), path, ifaceSvrConn.getLoginType() + BookmarkCache.infoSeperator + projectSvrConn.getLoginType(), ifaceSvrConn.getLoginName() + BookmarkCache.infoSeperator + projectSvrConn.getLoginName());

                        } else {
                            //copy path info
                            String path = runBrowser.getCurrentFileSystemTable().getSelectedPath();
                            BookmarkCache.addBookmark(type, interfaceId.toString(), ifaceFile.getName(), ifaceSvrConn.getServerPort(), path, ifaceSvrConn.getLoginType(), ifaceSvrConn.getLoginName());
                        }
                    } else if (f.getFileType().equals(DomeFile.PROJECT_TYPE)) {
                        CompoundId projectId = runBrowser.getCurrentObjectId();
                        BrowseDomeFile projFile = (BrowseDomeFile) obj;
                        ServerConnection projSvrConn;
                        projSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, projFile.getUrl());
                        //copy path info
                        String path = runBrowser.getCurrentFileSystemTable().getSelectedPath();
                        BookmarkCache.addBookmark(DomeFile.PROJECT_TYPE, projectId.toString(), projFile.getName(), projSvrConn.getServerPort(), path, projSvrConn.getLoginType(), projSvrConn.getLoginName());
                    } else if (f.getFileType().equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
                        CompoundId projectId = runBrowser.getCurrentObjectId();
                        BrowseDomeFile analysisFile = (BrowseDomeFile) obj;
                        ServerConnection projSvrConn;
                        projSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, analysisFile.getUrl());
                        //copy path info
                        String path = runBrowser.getCurrentFileSystemTable().getSelectedPath();
                        BookmarkCache.addBookmark(DomeFile.ANALYSIS_TOOL_TYPE, projectId.toString(), analysisFile.getName(), projSvrConn.getServerPort(), path, projSvrConn.getLoginType(), projSvrConn.getLoginName());
                    }
                }
            } else if (spaceType.equals(ServerPanel.PLAYSPACE)) {
                if (obj instanceof DomeFile) {
                    DomeFile f = (DomeFile) obj;
                    if (f.getFileType().equals(DomeFile.INTERFACE_TYPE)) {
                        String type = DomeFile.INTERFACE_TYPE;
                        if (runBrowser.isAnalysisToolParent()) type = BookmarkCache.ANALYSIS_Interface;
                        CompoundId interfaceId = runBrowser.getCurrentObjectId();
                        BrowseInterfaceDomeFile ifaceFile = (BrowseInterfaceDomeFile) obj;
                        ServerConnection ifaceSvrConn;
                        ifaceSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, ifaceFile.getUrl());
                        //copy path info
                        String path = runBrowser.getCurrentFileSystemTable().getSelectedPath();
                        BookmarkCache.addBookmark(type, interfaceId.toString(), ifaceFile.getName(), ifaceSvrConn.getServerPort(), path, ifaceSvrConn.getLoginType(), ifaceSvrConn.getLoginName());
                    } else if (f.getFileType().equals(DomeFile.PLAYSPACE_TYPE)) {
                        CompoundId playspacetId = runBrowser.getCurrentObjectId();
                        DomeFile playspaceFile = (DomeFile) obj;
                        ServerConnection playspaceSvrConn;
                        playspaceSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, playspaceFile.getUrl());
                        //copy path info
                        String path = runBrowser.getCurrentFileSystemTable().getSelectedPath();
                        BookmarkCache.addBookmark(DomeFile.PLAYSPACE_TYPE, playspacetId.toString(), playspaceFile.getName(), playspaceSvrConn.getServerPort(), path, playspaceSvrConn.getLoginType(), playspaceSvrConn.getLoginName());
                    }
                }
            }
        }

    }


     //Qing add for bookmark server
    public static void BookmarkServer() {
         JComponent currentComponent = RunFocusTracker.getCurrentComponent();
         if (currentComponent instanceof RunBrowser) {
            RunBrowser runBrowser = (RunBrowser) currentComponent;
            ServerConnection svrConn = runBrowser.getCurrentServerConnection();
            if (svrConn == null)
                return;

            BookmarkCache.addBookmark(BookmarkCache.Server,"Servers","",svrConn.getServerPort(),svrConn.getServerPort(),svrConn.getServerPort(),svrConn.getLoginType(),svrConn.getLoginName());
         }
     }
    private static void organizeBookmark() {
        BookmarkCache.showManager();
    }

    public static void goBookmark(String type, String id, String serverURL, String loginType, String loginName, String alias) {
        //add status window here
        if (loginType.equalsIgnoreCase(LoginUtils.GUEST)) {
            ServerConnection conn = LoginUtils.login(LoginUtils.GUEST, null, RunMode.getClientUrl(), serverURL, null);
	        if (conn == null) // login didn't work
	            return;
            JFrame waitWin = StatusWindow.show(StatusWindow.STARTING, alias, getWindowLocation());
            goBookmarkWorker worker = new goBookmarkWorker(type, conn, id, waitWin, alias);
            worker.start();
        } else {
            if (type.equals(BookmarkCache.Project_Interface)) {
                //need to parse the info first
                String ifaceServerURL = serverURL.substring(0, serverURL.indexOf(BookmarkCache.infoSeperator));
                String projServerURL = serverURL.substring(serverURL.indexOf(BookmarkCache.infoSeperator) + 2, serverURL.length());

                String ifaceloginType = loginType.substring(0, loginType.indexOf(BookmarkCache.infoSeperator));
                String projloginType = loginType.substring(loginType.indexOf(BookmarkCache.infoSeperator) + 2, loginType.length());


                String ifaceloginName = loginName.substring(0, loginName.indexOf(BookmarkCache.infoSeperator));
                String projloginName = loginName.substring(loginName.indexOf(BookmarkCache.infoSeperator) + 2, loginName.length());

                ServerConnection ifaceConn = LoginUtils.getServerConnection(RunFocusTracker.getCurrentComponent(), ifaceServerURL, RunMode.getClientUrl());
				if (ifaceConn == null) // login cancelled
					return;
                ServerConnection projConn = LoginUtils.compareServersAndGetConnection(ifaceConn, projServerURL);

                JFrame waitWin = StatusWindow.show(StatusWindow.STARTING, alias, getWindowLocation());
                goBookmarkWorker worker = new goBookmarkWorker(ifaceConn, projConn, id, waitWin, alias);
                worker.start();
            }
            else{
                ServerConnection conn = LoginUtils.getServerConnection(RunFocusTracker.getCurrentComponent(), serverURL, RunMode.getClientUrl());
                if (conn == null) // cancelled login
                    return;
	            JFrame waitWin = StatusWindow.show(StatusWindow.STARTING, alias, getWindowLocation());
                goBookmarkWorker worker = new goBookmarkWorker(type, conn, id, waitWin, alias);
                worker.start();
            }
        }
    }


    public static final AbstractAction newWidnowAction = new AbstractAction("New Window") {
        public void actionPerformed(ActionEvent e) {
            RunMode.newWindow();
        }
    };

    public static final AbstractAction closeAllAction = new AbstractAction("Close All") {
        public void actionPerformed(ActionEvent e) {
            RunMode.closeAll();
        }
    };

    public static final AbstractAction openInBroswerAction = new AbstractAction("Open") {
        public void actionPerformed(ActionEvent e) {
            RunMode.open_in_browser();
        }
    };

    public static final AbstractAction openInNewPlayspaceAction = new AbstractAction("Open in new playspace") {
        public void actionPerformed(ActionEvent e) {
            RunMode.open_in_playspace();
        }
    };

    public static final AbstractAction closeBroswerAction = new AbstractAction("close") {
        public void actionPerformed(ActionEvent e) {
            RunMode.close_broswer();
        }
    };

    public static final AbstractAction viewBuildAction = new AbstractAction(BUILDVIEW) {
        public void actionPerformed(ActionEvent e) {
            RunMode.viewbuild();
        }
    };

    public static final AbstractAction viewInterfaceCausalityAction = new AbstractAction(INTERFACECAUSALITYVIEW) {
        public void actionPerformed(ActionEvent e) {
            RunMode.view_interface_causality();
        }
    };

    public static final AbstractAction viewSystemCausalityAction = new AbstractAction(SYSTEMCAUSALITYVIEW) {
        public void actionPerformed(ActionEvent e) {
            RunMode.view_system_causality();
        }
    };

    public static final AbstractAction addBookmarkAction = new AbstractAction("Add Bookmark") {
        public void actionPerformed(ActionEvent e) {
            RunMode.addBookmark();
        }
    };

    public static final AbstractAction BookMarkServerAction = new AbstractAction("Bookmark Current Server") {
        public void actionPerformed(ActionEvent e) {
            RunMode.BookmarkServer();
        }
    };

    public static final AbstractAction organizeBookmarkAction = new AbstractAction("Organize Bookmark") {
        public void actionPerformed(ActionEvent e) {
            RunMode.organizeBookmark();
        }
    };


    private static class RunModeWindowTracker extends DefaultIconImageWindowTracker {

        public RunModeWindowTracker() {
            super(GuiConstants.FRAME_ICON_COLORS);
        }

        public void removeChildWindow(Window w) {
            super.removeChildWindow(w);

        }

    }

    /**
     * this class is added by Qing at Jan 27th for customized SwingWorker class
     */
    static class openInBrowserWorker extends SwingWorker {
        JFrame waitWin;
        RunBrowser runBrowser;
        String objName;

        public openInBrowserWorker(RunBrowser runBrowser, JFrame waitWin, String objName) {
            this.runBrowser = runBrowser;
            this.waitWin = waitWin;
            this.objName = objName;
        }

        public Object construct() {
            //put the old code here

            try {
                ServerConnection svrConn = runBrowser.getCurrentServerConnection();
                Object obj = runBrowser.getCurrentSelectedObject();
                if (svrConn == null || obj == null)
                    return new Object();

                String spaceType = runBrowser.getCurrentFilterType(); // model or playspace
                if (ServerPanel.MODEL.equals(spaceType)) {
                    // start the interface in "model space"
                    if (obj instanceof DomeFile) {
                        DomeFile f = (DomeFile) obj;

                        /**
                         * first, check if the interface belongs to an analysis tool
                         */

                        if (f.getFileType().equals(DomeFile.INTERFACE_TYPE)) {
                            if (runBrowser.isAnalysisToolParent()) {
                                CompoundId interfaceId = runBrowser.getCurrentObjectId();
                                BrowseInterfaceDomeFile ifaceFile = (BrowseInterfaceDomeFile) obj;
                                ServerConnection ifaceSvrConn;
                                ifaceSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, ifaceFile.getUrl());
                                startAnalysisToolInterface(null, ifaceSvrConn, interfaceId, runBrowser);
                            } else {
                                CompoundId interfaceId = runBrowser.getCurrentObjectId();
                                BrowseInterfaceDomeFile ifaceFile = (BrowseInterfaceDomeFile) obj;
                                ServerConnection ifaceSvrConn;
                                ifaceSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, ifaceFile.getUrl());
                                startInterface(null, ifaceSvrConn, interfaceId, runBrowser);
                            }
                        } else if (f.getFileType().equals(DomeFile.PROJECT_TYPE)) {
                            CompoundId cid = runBrowser.getCurrentObjectId();
                            startProject(svrConn, cid);
                        } else if (f.getFileType().equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
                            CompoundId cid = runBrowser.getCurrentObjectId();
                            startAnalysisTool(svrConn, cid);
                        }
                    }
                } else {
                    // start the interface in "playspace space"
                    CompoundId interfaceId = runBrowser.getCurrentObjectId();
                    if (interfaceId == null) {
                        Debug.trace(Debug.ALL, "must select interface to run.");
                        OneButton1Msg.showError(null, "No object selected",
                                "Must select an object to open.",
                                "ok", new Dimension(1, 1));
                        return new Object();
                    }
                    if (interfaceId.getInterfaceStaticId() == null) {
                        startPlayspace(svrConn, interfaceId);
                    } else {
                        System.err.println("can not start selected item in playspace: " + obj);
                    }
                }
            } catch (Exception e) {
                OneButton1Msg.showError(null, "Error starting " + objName,
                        e.getMessage(),
                        "ok", new Dimension(200, 1));
            }
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    };


    static class goBookmarkWorker extends SwingWorker {
        JFrame waitWin;
        ServerConnection conn, conn2;
        String id;
        String type;
	    String alias;

        public goBookmarkWorker(String type, ServerConnection conn, String id, JFrame waitWin, String alias) {
            this.type = type;
            this.conn = conn;
            this.id = id;
            this.waitWin = waitWin;
	        this.alias = alias;
        }

        public goBookmarkWorker(ServerConnection ifaceconn, ServerConnection projconn, String id, JFrame waitWin, String alias) {
            this.type = BookmarkCache.Project_Interface;
            this.conn = ifaceconn;
            this.conn2 = projconn;
            this.id = id;
            this.waitWin = waitWin;
	        this.alias = alias;
        }

        public Object construct() {
	        try {
		        if (type.equals(DomeFile.PROJECT_TYPE)) {
		            startProject(conn, new CompoundId(id));
		        } else if (type.equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
		            startAnalysisTool(conn, new CompoundId(id));
		        } else if (type.equals(DomeFile.PLAYSPACE_TYPE)) {
		            startPlayspace(conn, new CompoundId(id));
		        } else if (type.equals(BookmarkCache.ANALYSIS_Interface)) {
		            startAnalysisToolInterface(null, conn, new CompoundId(id), null);
		        } else if (type.equals(DomeFile.INTERFACE_TYPE)) {
		            startInterface(null, conn, new CompoundId(id), null);
		        } else if (type.equals(BookmarkCache.Project_Interface)) {
		            startProjectInterface(null, conn, conn2, new CompoundId(id));
		        } else if(type.equals(BookmarkCache.Server)){
		              //open a new runbrowser
		            RunBrowser rb = new RunBrowser(ServerPanel.RUN_BROWSE);
		            DomeRunFrame newFrame = new DomeRunFrame(rb, RunMode.getWindowTracker());
		            newFrame.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
		            rb.createFromBookmark(conn);
		            newFrame.show();
		         }
	        }
	        catch (Exception e) {
		        OneButton1Msg.showError(null, "Error opening bookmark " + alias,
		                                e.getMessage(),
		                                "ok", new Dimension(200, 1));
	        }
	        return new Object();
        }


        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }

    }

    /**
     * Start interface in a non-transient playspace.
     * @param playspaceSvrConn Playspace server connection
     * @param ifaceSvrConn Interface server connection
     * @param interfaceId Interface id
     */
    private static void startProjectInterface(ServerConnection playspaceSvrConn,
                                              ServerConnection ifaceSvrConn,
                                              ServerConnection ProjectSvrConn,
                                              CompoundId interfaceId
                                              ) {
        ModelInterfaceRuntimeClient interfaceClient = null;
	    // create predefined playspace
	    ClientPlayspaceRuntime playspace = null;
	    if (playspaceSvrConn != null) {
		    playspace = drClient.joinPlayspace(interfaceId.getPlayspaceStaticId(), playspaceSvrConn);
		    interfaceId.setPlayspaceRuntimeId(playspace.getRuntimeId());
	    }

	    boolean isProjectResource = false;
	    // create project
	    if (interfaceId.getFirstProjectStaticId() != null) {
		    if (playspace == null)
			    playspace = drClient.createTransientPlayspace(ProjectSvrConn);
		    isProjectResource = (interfaceId.getModelStaticId() != null); // imodel or resource interface
	    }

	    // create transient playspace
	    if (playspace == null) {
		    playspace = drClient.createTransientPlayspace(ifaceSvrConn);
	    }

	    // try to get the interface and restore an existing interface panel
	    interfaceClient = playspace.getInterface(interfaceId, ifaceSvrConn);
	    if (interfaceClient != null) {
		    if (restoreInterfacePanel(interfaceClient))
			    return;
	    } else {
		    // create a new interface
		    interfaceClient = drClient.createInterface(interfaceId, playspace, ifaceSvrConn, isProjectResource);
	    }

        // shows the interface runtime panel
        ModelInterfaceRunPanel iGui = new ModelInterfaceRunPanel(interfaceClient);
        DomeRunFrame newFrame = new DomeRunFrame(iGui, RunMode.getWindowTracker());
        newFrame.show();
    }
}