// BuildMode.java
package mit.cadlab.dome3.gui.mode.build;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.checkout.Checkout;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.clipboard.Clipboard;
import mit.cadlab.dome3.gui.guiutils.clipboard.ClipboardViewer;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.Saveable;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.Mode;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.ModelFactory;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildPlayspaceFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.model.ModelBuilder;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.optimization.OptimizationToolBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.gui.playspace.build.PlayspaceBuildPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspace;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceBuild;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.swing.DFrame;
import mit.cadlab.dome3.swing.DefaultIconImageWindowTracker;
import mit.cadlab.dome3.swing.GuiConstants;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.WindowTracker;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;
import mit.cadlab.dome3.util.xml.XMLUtils;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class BuildMode implements Mode {

    public static final String NAME = "Build";
    public static final BuildModeWindowTracker windows = new BuildModeWindowTracker();

    private static int nModels = 0;

    private static HashMap models = new HashMap(); // models & modelFrames

    private static HashMap iModels = new HashMap(); // iModels & iModelFrames

    private static ArrayList playspaceFrames = new ArrayList();
    public static final DomeFileChooser buildFileChooser = new DomeFileChooser();
    protected static ServerConnection serverConnection = null;

    protected static boolean isCatalogBuilderShowing = false;

    public static WindowTracker getWindowTracker(Object obj) {
        if (obj instanceof ModelObject) { // return model frame
            Model m = ((ModelObject) obj).getModel();
            if (m instanceof ModelObject) { // relation
                m = m.getModel();
            }
            if (m instanceof DomeModel) {
                DomeModel dm = (DomeModel) m;
                if (dm.isIntegrationModel())
                    return (WindowTracker) iModels.get(m);
            }
            return (WindowTracker) models.get(m);
        } else if (obj instanceof Model) {
            if (obj instanceof DomeModel) {
                DomeModel m = (DomeModel) obj;
                if (m.isIntegrationModel())
                    return (WindowTracker) models.get(m.getIntegrationProject());
            }
            return windows;
        } else if (obj instanceof ModelComponent) {
            Model m = ((ModelComponent) obj).getModel();
            if (m instanceof DomeModel) {
                DomeModel dm = (DomeModel) m;
                if (dm.isIntegrationModel())
                    return (WindowTracker) iModels.get(m);
            }
            return (WindowTracker) models.get(m);
        } else {
            System.err.println("BuildMode.getWindowTracker error for " + obj);
            return null;
        }
    }

    /**
     * for playspace windows
     * @return
     */
    public static WindowTracker getWindowTracker() {
        return windows;
    }

    public static Point getWindowLocation(Object obj) {
        if (obj instanceof Model) {
            int offset = (nModels % 20) * 23;
            return new Point(offset, offset + DomeClientApplication.getBottomCoordinate());
        } else if (obj instanceof ModelObject || obj instanceof ModelComponent) {
            // place relative to current component window
            JComponent comp = BuildFocusTracker.getCurrentComponent();
            if (comp == null) { // place in top left corner
                return new Point(0, DomeClientApplication.getBottomCoordinate());
            } else { // place offset to window of component
                Point p = BuildFocusTracker.getCurrentWindow().getLocationOnScreen();
                return new Point(p.x + 25, p.y + 25);
            }
        } else { // what is it? place in top left corner
            return new Point(0, DomeClientApplication.getBottomCoordinate());
        }
    }

    /**
     * for determine the playspace location
     * @return
     */
    public static Point getWindowLocation() {
        JComponent comp = BuildPlayspaceFocusTracker.getCurrentComponent();
        if (comp == null) { // place in top left corner
            return new Point(0, DomeClientApplication.getBottomCoordinate());
        } else { // place offset to window of component
            Window win = BuildPlayspaceFocusTracker.getCurrentWindow();
            if (win instanceof DomeBuildPlayspaceFrame && win.isShowing()) {
                Point p = win.getLocationOnScreen();
                return new Point(p.x + 25, p.y + 25);
            } else { // what is it? place in top left corner
                return new Point(0, DomeClientApplication.getBottomCoordinate());
            }
        }
    }

    /**
     * for determine the status window location
     * @return
     */
    public static Point getStatusWindowLocation() {
        JComponent comp = BuildFocusTracker.getCurrentComponent();
        if (comp == null) { // place in top left corner
            return new Point(0, DomeClientApplication.getBottomCoordinate());
        } else { // place offset to window of component
            Window win = BuildFocusTracker.getCurrentWindow();
            if (win instanceof DomeBuildFrame && win.isShowing()) {
                Point p = win.getLocationOnScreen();
                return new Point(p.x + 25, p.y + 25);
            } else { // what is it? place in top left corner
                return new Point(0, DomeClientApplication.getBottomCoordinate());
            }
        }
    }

    public static DomeBuildFrame getCurrentModelFrame() {
        Model m = BuildFocusTracker.getCurrentModel();
        DomeBuildFrame frame = (DomeBuildFrame) models.get(m);
        if (frame == null) // maybe it is an integration model
            frame = (DomeBuildFrame) iModels.get(m);
        return frame;
    }

    public static DomeBuildPlayspaceFrame getCurrentPlayspaceFrame() {
        Window w = BuildPlayspaceFocusTracker.getCurrentWindow();
        if (w instanceof DomeBuildPlayspaceFrame) return (DomeBuildPlayspaceFrame) w;
        return null;
    }


    private static String getNextModelId() {
        return UUIDGenerator.create();
    }

    public static void modelSaveAs(Model origModel, String fileName) {
        //newModel is the same as origModel model except with a different id
        //contained objects have same id
        ModelBuilder newModel = ((DomeModelBuilder) origModel).getSaveAsCopy();
        newModel.save(fileName);
    }

    public static void newModel(String modelType) {


        DomeBuildFrame modelFrame = ModelFactory.newModel(modelType, getNextModelId());
        if (modelFrame == null) return;
        Model model = (Model) modelFrame.getGuiObject();
        models.put(model, modelFrame);
        nModels++;
        modelFrame.show();


    }

    public static void openModel(String modelType, String filePathName) {

        String localFileName;
        if (filePathName != null)
            localFileName = filePathName;
        else
            localFileName = buildFileChooser.showOpenDialog(null, modelType);
        if (localFileName == null) return; // cancelled

        File f = new File(localFileName);

        JFrame waitWin = StatusWindow.show(StatusWindow.OPENING_FILE, f.getName(), getStatusWindowLocation());
        openModelWorker worker = new openModelWorker(localFileName, modelType, waitWin);
        worker.start();

    }


    public static void newTool(String toolType) {
        DomeBuildFrame toolFrame = ModelFactory.newModel(toolType, getNextModelId());
        if (toolFrame == null)
            return;
        Model model = (Model) toolFrame.getGuiObject();
        if (model instanceof OptimizationToolBuild) {
            IntegrationProject project = ((OptimizationToolBuild) model).getIntegrationProject();
            models.put(project, toolFrame);
        }
        models.put(model, toolFrame);
        nModels++;
        toolFrame.show();
    }

    public static void openTool(String toolType, String filePathName) {
        String localFileName;
        if (filePathName != null)
            localFileName = filePathName;
        else
            localFileName = buildFileChooser.showOpenDialog(null, toolType);
        if (localFileName == null) return; // cancelled

        File f = new File(localFileName);

        JFrame waitWin = StatusWindow.show(StatusWindow.OPENING_FILE, f.getName(), getStatusWindowLocation());
        OpenToolWorker worker = new OpenToolWorker(localFileName, toolType, waitWin);
        worker.start();
    }

    public static void analysisToolSaveAs(AnalysisTool originalAnalysisTool, String fileName)
    {
        AnalysisToolBase newModel = ((AnalysisToolBase) originalAnalysisTool).getSaveAsCopy();
    }


    public static void openProject(String filePathName) {
        String localFileName;
        if (filePathName != null)
            localFileName = filePathName;
        else
            localFileName = buildFileChooser.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
        if (localFileName == null) return; // cancelled

        File f = new File(localFileName);

        JFrame waitWin = StatusWindow.show(StatusWindow.OPENING_FILE, f.getName(), getStatusWindowLocation());
        openProjectWorker worker = new openProjectWorker(localFileName, waitWin);
        worker.start();

    }


    public static IntegrationProjectBuilder importAnalysisToolProject(String filePathName) {
        String localFileName;
        IntegrationProjectBuilder iProjectBuilder = null;
        if (filePathName != null)
        {
            localFileName = filePathName;
            if (!new File(localFileName).exists())
            {
                OneButton1Msg.showWarning(null, "open analysis tool", "Integration project inside this analysis tool, does not exists" +
                        " in this path: \n" + localFileName + "\nYou have to manually find the project", "ok", new Dimension(175, 75));
                localFileName = buildFileChooser.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
            }
        }
        else
            localFileName = buildFileChooser.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);

        if (localFileName != null)
        {
            Element modelElement = XMLUtils.fileToXmlElement(localFileName);

            if (modelElement != null)
            {
                iProjectBuilder = new IntegrationProjectBuilder(localFileName, modelElement);
                Window w = BuildFocusTracker.getCurrentWindow();
                models.put(iProjectBuilder, w);
            }
        }
        return iProjectBuilder;
    }

    public static DomeBuildFrame openIModel(DomeModelBuilder m) {
        DomeBuildFrame f = new DomeBuildFrame(new DomeModelBuildPanel(m));
        iModels.put(m, f);
        return f;
    }

    public static void checkoutModel() {
        new Checkout(Checkout.CHECKOUT_MODEL, null);
    }

    public static void checkoutPlayspace() {
        new Checkout(Checkout.CHECKOUT_PLAYSPACE, null);
    }

    public static void checkoutProject() {
        new Checkout(Checkout.CHECKOUT_PROJECT, null);
    }

    /** actionAfterLaunch is either null, "new" or "open" */
    public static void launchCatalogBuilder(String actionAfterLaunch) {
        if (mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame() == null) {
            System.out.println("create and show GUI");
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.createAndShowGUI(false);
            isCatalogBuilderShowing = true;
        } else {
            System.out.println("show GUI again");
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame().setVisible(true);
            isCatalogBuilderShowing = true;
        }

        if ("new".equalsIgnoreCase(actionAfterLaunch)) {
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getComponentReference().getModelEditor().createNewModel();
        } else if ("open".equalsIgnoreCase(actionAfterLaunch)) {
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getComponentReference().getModelEditor().openModelFromFile();
        }
    }

    public static void newCatalogBuilder() {
        launchCatalogBuilder("new");
    }

    public static void openCatalogBuilder() {
        launchCatalogBuilder("open");
    }

    public static void newPlayspace() {
        //create a new playspace
        PlayspaceBuildPanel playspace = new PlayspaceBuildPanel(serverConnection);
        DomeBuildPlayspaceFrame newFrame = new DomeBuildPlayspaceFrame(playspace);
        newFrame.show();
    }


    public static void openPlayspace(String filePathName) {
        // get the model file name
        String localFileName;
        if (filePathName != null)
            localFileName = new String(filePathName);
        else
            localFileName = buildFileChooser.showOpenDialog(null, DomeFileChooser.DOME_PLAYSPACE_FILTER);
        if (localFileName == null) return; // cancelled

        File f = new File(localFileName);

        JFrame waitWin = StatusWindow.show(StatusWindow.OPENING_FILE, f.getName(), getWindowLocation());
        openPlayspaceWorker worker = new openPlayspaceWorker(localFileName, waitWin);
        worker.start();

    }


    public static void playspaceSaveAs(ClientPlayspace origPlayspace, String fileName) {
        //newModel is the same as origModel model except with a different id
        //contained objects have same id
        ClientPlayspaceBuild newPlayspace = ((ClientPlayspaceBuild) origPlayspace).getSaveAsCopy();
        newPlayspace.save(fileName);
    }

    private static void removeModel(Object obj) {
        if (obj instanceof Model) {
            if (obj instanceof OptimizationToolBuild) {
                Object frame = models.get(obj);
                if (frame != null) {
                    DomeBuildFrame toolFrame = (DomeBuildFrame) frame;
                    OptimizationToolBuildPanel pane = (OptimizationToolBuildPanel) toolFrame.getGui();
                    IntegrationProject project = ((AnalysisToolBase) pane.getModel()).getIntegrationProject();
                    models.remove(project);
                }
            }
            models.remove(obj);
            if (nModels > 0)
                nModels--;
        }
    }

    private static void removePlaySpaceFrame(DomeBuildPlayspaceFrame frame) {
        frame.getGui().close();
        playspaceFrames.remove(frame);
    }

    public static void closeAll() {
        windows.closeAll();
        MenuManager.setContext(ModeContexts.BUILD_MODE);
        //ModelBuilderFrame.count=0;
    }

    public static void saveAllModels() {
        Iterator it = windows.getChildren().iterator();
        if (!it.hasNext())
            return;
        else {
            JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, "all open files", getStatusWindowLocation());
            SaveAllWorker worker = new SaveAllWorker(it, waitWin);
            worker.start();
        }
    }

    // Mode interface
    public static String name() {
        return NAME;
    }

    public static void show() {
        windows.showAll();
        if (mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame() != null && isCatalogBuilderShowing) {
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame().setVisible(true);
        }
    }

    public static void hide() {
        windows.hideAll();
        if (mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame() != null) {
            isCatalogBuilderShowing = mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame().isVisible();
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame().setVisible(false);
        }
    }

    public static void exit() {
        for (Iterator iterator = models.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            DomeBuildFrame frame = (DomeBuildFrame) entry.getValue();
            frame.getGui().close();
        }
        windows.closeAll();

        if (mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame() != null) {
            isCatalogBuilderShowing = mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame().isVisible();
            mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame.getFrame().setVisible(false);
        }
    }

    // --- BuildMode clipboard ------------------------------
    public static final Clipboard clipboard = new Clipboard();
    protected static final DFrame clipboardViewer = makeClipboardViewer();
    protected static final String SHOW_CLIPBOARD = "Show Clipboard";
    protected static final String HIDE_CLIPBOARD = "Hide Clipboard";

    protected static DFrame makeClipboardViewer() {
        DFrame cViewer = ClipboardViewer.createClipboardViewer(windows, clipboard);
        cViewer.setIconImage(Templates.makeImageIcon("mit/cadlab/dome3/icons/domeWindow.gif").getImage());
        cViewer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        cViewer.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                MenuManager.setContext(ModeContexts.BUILD_MODE);
            }

            public void windowClosed(WindowEvent e) {
                clipboardAction.putValue(Action.NAME, SHOW_CLIPBOARD);
            }

            public void windowClosing(WindowEvent e) {
                hideClipboard();
            }

            public void windowDeiconified(WindowEvent e) {
                clipboardAction.putValue(Action.NAME, HIDE_CLIPBOARD);
            }

            public void windowIconified(WindowEvent e) {
                clipboardAction.putValue(Action.NAME, SHOW_CLIPBOARD);
            }
        });
        cViewer.pack();
        Dimension screen = cViewer.getToolkit().getScreenSize();
        Dimension size = cViewer.getSize();
        cViewer.setLocation(screen.width - size.width, DomeClientApplication.getBottomCoordinate());
        return cViewer;
    }

    public static void showClipboard() {
        if (clipboardViewer.getState() == Frame.ICONIFIED)
            clipboardViewer.setState(Frame.NORMAL);
        clipboardViewer.show();
        clipboardAction.putValue(Action.NAME, HIDE_CLIPBOARD);
    }

    public static void hideClipboard() {
        clipboardViewer.hide();
        clipboardAction.putValue(Action.NAME, SHOW_CLIPBOARD);
    }


    // --- actions for menus and buttons --------------------
    public static class NewModelAction extends AbstractAction {
        private String modelType;

        public NewModelAction(String name) {

            this(name, name);
        }

        public NewModelAction(String name, String modelType) {
            super(name);
            this.modelType = modelType;
        }

        public void actionPerformed(ActionEvent e) {
            /* a treatment for Catalog model type has been added */
            if ("Catalog Model".equalsIgnoreCase(modelType)) {
                BuildMode.newCatalogBuilder();
            } else {
                JFrame waitWin = StatusWindow.show(StatusWindow.CREATING, modelType, getStatusWindowLocation());
                newModelWorker worker = new newModelWorker(modelType, waitWin);
                worker.start();
            }
        }
    }


    public static class OpenModelAction extends AbstractAction {
        private String modelType;

        public OpenModelAction(String name) {
            this(name, name);
        }

        public OpenModelAction(String name, String modelType) {
            super(name);
            this.modelType = modelType;
        }

        public void actionPerformed(ActionEvent e) {
            /* a treatment for Catalog model type has been added */
            if ("Catalog Model".equalsIgnoreCase(modelType)) {
                BuildMode.openCatalogBuilder();
            } else {
                BuildMode.openModel(modelType, null);
            }

        }
    }

    public static class NewToolAction extends AbstractAction {
        private String _toolType;

        public NewToolAction(String name) {
            this(name, name);
        }

        public NewToolAction(String name, String toolType) {
            super(name);
            this._toolType = toolType;
        }

        public void actionPerformed(ActionEvent e) {
            JFrame waitWin = StatusWindow.show(StatusWindow.CREATING, _toolType, getStatusWindowLocation());
            newToolWorker worker = new newToolWorker(_toolType, waitWin);
            worker.start();
        }
    }


    public static class OpenToolAction extends AbstractAction {
        private String _toolType;

        public OpenToolAction(String name) {
            this(name, name);
        }

        public OpenToolAction(String name, String toolType) {
            super(name);
            this._toolType = toolType;
        }

        public void actionPerformed(ActionEvent e) {
            BuildMode.openTool(_toolType, null);
        }
    }

    public static class OpenProjectAction extends AbstractAction {
        public OpenProjectAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            BuildMode.openProject(null);
        }
    }

    public static class LaunchCatalogBuilderAction extends AbstractAction {
        private String modelType;

        public LaunchCatalogBuilderAction(String name) {

            this(name, name);
        }

        public LaunchCatalogBuilderAction(String name, String modelType) {
            super(name);
            this.modelType = modelType;
        }

        public void actionPerformed(ActionEvent e) {
            JFrame waitWin = StatusWindow.show(StatusWindow.CREATING, "DOME Catalog", getWindowLocation());
            launchCatalogBuilderWorker worker = new launchCatalogBuilderWorker(waitWin);
            worker.start();
        }
    }

    public static final AbstractAction newPlayspaceAction = new AbstractAction("New playspace") {
        public void actionPerformed(ActionEvent e) {
            JFrame waitWin = StatusWindow.show(StatusWindow.CREATING, "DOME Playspace", getWindowLocation());
            newPlayspaceWorker worker = new newPlayspaceWorker(waitWin);
            worker.start();
        }
    };


    public static final AbstractAction openPlayspaceAction = new AbstractAction("Open playspace...") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.openPlayspace(null);
        }
    };

    public static final AbstractAction checkoutModelAction = new AbstractAction("Checkout model...") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.checkoutModel();
        }
    };

    public static final AbstractAction checkoutPlayspaceAction = new AbstractAction("Checkout playspace...") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.checkoutPlayspace();
        }
    };

    public static final AbstractAction checkoutProjectAction = new AbstractAction("Checkout integration project...") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.checkoutProject();
        }
    };

    public static final AbstractAction checkoutToolAction = new AbstractAction("Checkout analysis tool...") {
        public void actionPerformed(ActionEvent e) {
            //              BuildMode.checkoutTool();
        }
    };


    public static final AbstractAction closeAllAction = new AbstractAction("Close All") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.closeAll();
        }
    };

    public static final AbstractAction saveAllAction = new AbstractAction("Save All") {
        public void actionPerformed(ActionEvent e) {
            BuildMode.saveAllModels();
        }
    };

    public static final AbstractAction clipboardAction = new AbstractAction(SHOW_CLIPBOARD) {
        public void actionPerformed(ActionEvent e) {
            if (clipboardViewer.isVisible() && clipboardViewer.getState() == Frame.NORMAL)
                hideClipboard();
            else
                showClipboard();
        }
    };

    private static class BuildModeWindowTracker extends DefaultIconImageWindowTracker {

        public BuildModeWindowTracker() {
            super(GuiConstants.FRAME_ICON_COLORS);
        }

        public void removeChildWindow(Window w) {
            super.removeChildWindow(w);
            if (w instanceof DomeBuildFrame)
                removeModel(((DomeBuildFrame) w).getGuiObject());
            else if (w instanceof DomeBuildPlayspaceFrame) {
                removePlaySpaceFrame((DomeBuildPlayspaceFrame) w);
            }
        }
    }

    /*
    *the following are added by Qing Jan 27th, customized swing worker classes
    */
    static class openModelWorker extends SwingWorker {
        String fn,modelType;
        JFrame waitWin;

        public openModelWorker(String fn, String modelType, JFrame waitWin) {
            this.fn = fn;
            this.modelType = modelType;
            this.waitWin = waitWin;
        }

        public Object construct() {
            //the old BuildMode.openModel(modelType, null) code
            Element modelElement = XMLUtils.fileToXmlElement(fn);
            DomeModelBuilder model = null;
            if (DomeModel.TYPE_INFO.getTypeName().equals(modelType))
                model = new DomeModelBuilder(fn, modelElement);
            else // plugin model
                model = new PluginModelBuilder(fn, modelElement);
            DomeBuildFrame modelFrame = ModelFactory.newModel(model);
            if (modelFrame == null) return new Object();
            models.put(model, modelFrame);
            nModels++;
            modelFrame.show();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class OpenToolWorker extends SwingWorker {
        JFrame waitWin;
        String filename;
        String toolType;

        public OpenToolWorker(String filename, String toolType, JFrame waitWin) {
            this.filename = filename;
            this.toolType = toolType;
            this.waitWin = waitWin;
        }

        public Object construct() {
            Element modelElement = XMLUtils.fileToXmlElement(filename);
            AnalysisToolBase toolModel = null;
            if (QMOOConfiguration.TYPE_INFO.getTypeName().equals(toolType))
                toolModel = new OptimizationToolBuild(filename, modelElement);
            else
                OneButton1Msg.showError(null, "unknown tool", "tool does not exist", "ok", new Dimension(150, 75));
            DomeBuildFrame toolFrame = ModelFactory.newTool(toolModel);
            if (toolFrame == null) return new Object();
            models.put(toolModel, toolFrame);
            if (toolModel instanceof OptimizationToolBuild) {
                IntegrationProject project = ((AnalysisToolBase) ((OptimizationToolBuildPanel) toolFrame.getGui()).getModel()).getIntegrationProject();
                models.put(project, toolFrame);
            }
            nModels++;
            toolFrame.show();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    };

    static class openProjectWorker extends SwingWorker {
        String fn;
        JFrame waitWin;

        public openProjectWorker(String fn, JFrame waitWin) {
            this.fn = fn;
            this.waitWin = waitWin;
        }

        public Object construct() {
            //the old  BuildMode.openProject(null) code
            Element modelElement = XMLUtils.fileToXmlElement(fn);
            IntegrationProjectBuilder ipbuilder = new IntegrationProjectBuilder(fn, modelElement);
            DomeObjectGui projectGui = new ProjectBuildPanel(ipbuilder);
            DomeBuildFrame modelFrame = new DomeBuildFrame(projectGui);
            if (modelFrame == null) return new Object();
            models.put(ipbuilder, modelFrame);
            nModels++;
            modelFrame.show();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class openPlayspaceWorker extends SwingWorker {
        String fn;
        JFrame waitWin;

        public openPlayspaceWorker(String fn, JFrame waitWin) {
            this.fn = fn;
            this.waitWin = waitWin;
        }

        public Object construct() {
            //the old BuildMode.openPlayspace(null) code
            SAXReader reader = new SAXReader();
            Document playspaceDoc = null;
            File playspaceFile = null;
            // parse the playspace file
            try {
                playspaceFile = new File(fn);
                playspaceDoc = reader.read(playspaceFile);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // construct the playspace and a frame for it
            Element playspaceElement = playspaceDoc.getRootElement();
            PlayspaceBuildPanel playspace = new PlayspaceBuildPanel(serverConnection, fn, playspaceElement);
            playspace.setFilePathField(fn);
            DomeBuildPlayspaceFrame newFrame = new DomeBuildPlayspaceFrame(playspace);
            playspaceFrames.add(newFrame);
            newFrame.show();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class newToolWorker extends SwingWorker {
        String toolType;
        JFrame waitWin;

        public newToolWorker(String toolType, JFrame waitWin) {
            this.toolType = toolType;
            this.waitWin = waitWin;
        }

        public Object construct() {
            BuildMode.newTool(toolType);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class newModelWorker extends SwingWorker {
        String modelType;
        JFrame waitWin;

        public newModelWorker(String modelType, JFrame waitWin) {
            this.modelType = modelType;
            this.waitWin = waitWin;
        }

        public Object construct() {
            BuildMode.newModel(modelType);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class newPlayspaceWorker extends SwingWorker {
        JFrame waitWin;

        public newPlayspaceWorker(JFrame waitWin) {
            this.waitWin = waitWin;
        }

        public Object construct() {
            BuildMode.newPlayspace();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class launchCatalogBuilderWorker extends SwingWorker {
        JFrame waitWin;

        public launchCatalogBuilderWorker(JFrame waitWin) {
            this.waitWin = waitWin;
        }

        public Object construct() {
            BuildMode.launchCatalogBuilder(null);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class SaveAllWorker extends SwingWorker {
        Iterator it;
        JFrame waitWin;
        boolean closeAfterSave = false; // never close the model window after invoking save all from main menu.

        public SaveAllWorker(Iterator it, JFrame waitWin) {
            this.it = it;
            this.waitWin = waitWin;
        }

        public Object construct() {
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof Saveable)
                    ((Saveable) item).save(closeAfterSave);
            }
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }
}
