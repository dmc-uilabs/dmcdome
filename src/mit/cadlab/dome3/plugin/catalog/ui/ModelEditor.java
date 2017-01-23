package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CInterface;
import mit.cadlab.dome3.plugin.catalog.core.CLog;
import mit.cadlab.dome3.plugin.catalog.core.CModel;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.serialization.DomeSerialization;
import mit.cadlab.dome3.plugin.catalog.serialization.IDContainer;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Iterator;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 30.
 */
public class ModelEditor extends JPanel {
    private CModel model;
    private ComponentReference compRef;
    private ImplementationEditor implEditor;
    private ModelNavigationPanel naviPanel;
    private JSplitPane splitPane;
    private IDContainer idContainer;
    private String filePath;
    private File workingDir;
    private boolean hasUnsavedChanges = true;
    private CellConfig cellConfig = null;
    private Map dynamicComponentMap;
    private CellConfigDialog configDialog;
    private boolean isEvaluationMode = false;
    private boolean isTypeAndUnitQueryMode = false;
    private boolean isNavigationPanelVisible = true;
    private boolean isStopClickedBefore= false;

    private boolean isUpwardExpandingSelection = false;
    private boolean isDownwardExpandingSelection = false;

    //public static final String DEFAULT_WORKING_DIRECTORY = "C:\\dome3\\models\\catalog";
    //public static final String DEFAULT_WORKING_DIRECTORY = "C:/Documents and Settings/smhan/Desktop/research/user study";
    public static String DEFAULT_WORKING_DIRECTORY = System.getProperty("user.home");
    public static String DEFAULT_DOWNLOAD_DIRECTORY = System.getProperty("user.home") + File.separator + "CModelRuntime";

    static {
        CLog.info("default working directory: " + DEFAULT_WORKING_DIRECTORY);
        CLog.info("default download directory: " + DEFAULT_DOWNLOAD_DIRECTORY); // for EvaluationContext

    }

    protected static final String CENTER_PANEL_LABEL_ADJUSTER = "Center Panel Label Adjuster";
    protected static final String MAPPING_SCRIPT_REFERENCE_CHECKER = "Mapping Script Reference Checker";

    private Map evalContextMap;
    private Map interfaceBarMap;
    private Map relationBarsMap;
    private Map modifiedParamSetMap; // (itfName + "|" + implName) -> (Set of modified param name String)

    public ModelEditor() {
        workingDir = new File(DEFAULT_WORKING_DIRECTORY); // set default working dir
        implEditor = new ImplementationEditor();
        this.compRef = implEditor.getComponentReference();
        naviPanel = new ModelNavigationPanel(compRef);
        this.dynamicComponentMap = new HashMap();
        this.interfaceBarMap = new HashMap();
        this.relationBarsMap = new HashMap();
        this.evalContextMap = new HashMap();
        this.modifiedParamSetMap = new HashMap();
        initComponents();
        addDynamicComponent(CENTER_PANEL_LABEL_ADJUSTER, new CenterPanelLabelAdjuster(this));
        addDynamicComponent(MAPPING_SCRIPT_REFERENCE_CHECKER, new MappingScriptReferenceChecker(this));
    }

    static abstract class DefaultDynamicComponent implements DynamicComponent {
        boolean isEnabled = true;

        public void setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        public boolean isEnabled() {
            return isEnabled;
        }
    }

    static class MappingScriptReferenceChecker extends DefaultDynamicComponent {
        ModelEditor modelEditor;

        public MappingScriptReferenceChecker(ModelEditor modelEditor) {
            this.modelEditor = modelEditor;
        }

        public void update() {
            ComponentReference compRef = modelEditor.getComponentReference();

            if (compRef == null || modelEditor.getModel() == null || ! compRef.getImplementationEditor().isImplLoaded()) {
                return;
            }

            try {
                for (Iterator i = compRef.getInterfaceOutputCells().iterator(); i.hasNext(); ) {
                    ((BaseCell) i.next()).validateMappingScriptReference();
                }

                for (Iterator i = compRef.getRelationBars().iterator(); i.hasNext(); ) {
                    RelationBar bar = (RelationBar) i.next();
                    for (Iterator j = compRef.getRelationInputCells(bar.getRelAlias()).iterator(); j.hasNext(); ) {
                        ((BaseCell) j.next()).validateMappingScriptReference();
                    }
                }
            } catch (Exception e) {
                //speedup Clog.debug("an ignorable error occurred at MappingScriptReferenceChecker.update(): " + e.getClass().getName());
            }
        }
    }

    static class CenterPanelLabelAdjuster extends DefaultDynamicComponent {
        ModelEditor modelEditor;

        public CenterPanelLabelAdjuster(ModelEditor modelEditor) {
            this.modelEditor = modelEditor;
        }

        public void update() {
            ComponentReference compRef = modelEditor.getComponentReference();

            if (compRef == null || modelEditor.getModel() == null || ! compRef.getImplementationEditor().isImplLoaded()) {
                return;
            }

            InterfaceBar itfBar = compRef.getInterfaceEditor().getInterfaceBar();
            boolean isEvaluationMode = compRef.getModelEditor().isEvaluationMode();
            if (itfBar != null) {
                itfBar.getCenterPanel().updateRelNameLabelHeight();
                itfBar.getCenterPanel().revalidate();
//                for (int j = 0; j < itfBar.getLeftPanel().getComponentCount(); j++) {
//                    BaseCell cell = (BaseCell) itfBar.getLeftPanel().getComponent(j);
//                    cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
//                }
//                itfBar.getLeftPanel().invalidate();
//                for (int j = 0; j < itfBar.getRightPanel().getComponentCount(); j++) {
//                    BaseCell cell = (BaseCell) itfBar.getRightPanel().getComponent(j);
//                    cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
//                }
//                itfBar.getRightPanel().invalidate();
            }

            for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
                compRef.getRelationBar(i).getCenterPanel().updateRelNameLabelHeight();
                compRef.getRelationBar(i).getCenterPanel().revalidate();
//                for (int j = 0; j < compRef.getRelationBar(i).getLeftPanel().getComponentCount(); j++) {
//                    BaseCell cell = (BaseCell) compRef.getRelationBar(i).getLeftPanel().getComponent(j);
//                    cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
//                }
//                compRef.getRelationBar(i).getLeftPanel().invalidate();
//                for (int j = 0; j < compRef.getRelationBar(i).getRightPanel().getComponentCount(); j++) {
//                    BaseCell cell = (BaseCell) compRef.getRelationBar(i).getRightPanel().getComponent(j);
//                    cell.updateCellLayout(compRef.getCellConfig(), isEvaluationMode);
//                }
//                compRef.getRelationBar(i).getRightPanel().invalidate();
//                compRef.getRelationBar(i).invalidate();
            }
//            compRef.getInterfaceEditor().invalidate();
//            compRef.getRelationEditor().invalidate();
//            compRef.getImplementationEditor().invalidate();
//            compRef.getModelEditor().revalidate();
        }
    }

    private void initComponents() {
        //Create a split pane with the two scroll panes in it.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, naviPanel, implEditor);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(UIUtil.NAV_BT_PANEL_WIDTH);
        splitPane.setResizeWeight(0.0);

        //Provide minimum sizes for the two components in the split pane
//        Dimension minimumSize = new Dimension(UIUtil.NAV_BT_PANEL_WIDTH, 50);
//        naviPanel.setMinimumSize(minimumSize);
//        implEditor.setMinimumSize(minimumSize);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(splitPane);
        //this.add(splitPane, BorderLayout.CENTER);

        splitPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

        // load(null);
        // this.setVisible(true); // this is called from outside when needed
    }

    /** pops up file open dialog, and load a catalog model from a selected file. */
    public void openModelFromFile() {
        compRef.getImplementationEditor().unindexCurrentCModel();
        compRef.getImplementationEditor().closeScriptEditor(true);

        /* create file chooser */
        JFileChooser fc;
        if (compRef.getModelEditor().getWorkingDirectory() != null) {
            fc = new JFileChooser(compRef.getModelEditor().getWorkingDirectory());
        } else {
            fc = new JFileChooser();
        }

        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().matches(".*-CATALOG.dml");
            }

            public String getDescription() {
                return "DOME Catalog Model";
            }
        });



        File file;
        boolean foundValidFile = false;
        do {
            int returnVal = fc.showOpenDialog(compRef.getModelEditor());
            file = fc.getSelectedFile();
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                //speedup Clog.debug("User canceled opening a file.");
                return;
            }
            foundValidFile = (returnVal == JFileChooser.APPROVE_OPTION) && file.exists() && file.isFile();
        } while (! foundValidFile);

        /* close the current model before opening a new model */
        boolean isClosed = compRef.getModelEditor().closeModel();
        if (! isClosed) {
            return;
        }

        compRef.getModelEditor().setWorkingDirectory(file.getParentFile());
        this.setFilePath(file.getPath());

        /* load CModel, IDContainer from a file */
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load(filePath);
        this.model = modelAndIDContainer.getModel();
        this.idContainer = modelAndIDContainer.getIDContainer();


        /* update navigation panel */
        naviPanel.loadModel(model);
        naviPanel.selectDefaultTreeNode();

        //speedup Clog.debug("model loaded: " + modelAndIDContainer.getModel());
        //speedup Clog.debug("idcontainer loaded: " + modelAndIDContainer.getIDContainer());

        compRef.getImplementationEditor().indexCurrentCModel();
        compRef.getModelNavigationPanel().setModelNameEnabled(true);
        compRef.getModelNavigationPanel().setModelNameInNavPanel(compRef.getCurrentCModel().getName());
        ModelEditorKit.setEnabledOfAllActions(true);

        ModelEditorFrame.setFrameTitle(filePath);
        compRef.getModelEditor().setHasUnsavedChanges(false);
        UIUtil.updateCellAndBarLayout(compRef);
    }


    /** without popping up FileSaveDialog, save the model into a current file. model should already be loaded so model, id container, and file path are available. */
    public void saveModelAsCurrentFile() {
        try {
            if (model == null || idContainer == null || filePath == null) {
                throw new RuntimeException("ModelEditor does not have valid information to execute saveModelAsCurrentFile():" + model + ", " + idContainer + ", " + filePath);
            }
            DomeSerialization.save(model, idContainer, filePath);
            compRef.getModelEditor().setHasUnsavedChanges(false);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        }
    }

    /** pops up FileSaveDialog to save the model into a file */
    public void saveModelAsNewFile() {
        compRef.getImplementationEditor().closeScriptEditor(true);
        System.out.println("do save as");

        /* Create a file chooser */
        JFileChooser fc;
        if (compRef.getModelEditor().getWorkingDirectory() != null) {
            fc = new JFileChooser(compRef.getModelEditor().getWorkingDirectory());
        } else {
            fc = new JFileChooser();
        }

        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().matches(".*-CATALOG.dml");
            }

            public String getDescription() {
                return "DOME Catalog Model";
            }
        });


        boolean keepFileChooser = true;
        do {
            int returnVal = fc.showSaveDialog(compRef.getModelEditor());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                /* fix file name */
                if (! file.getName().endsWith("-CATALOG.dml")) {
                    if (file.getName().endsWith(".dml")) {
                        StringBuffer sb = new StringBuffer(file.getPath());
                        sb.insert(sb.indexOf(".dml"), "-CATALOG");
                        file = new File(sb.toString());
                    } else {
                        file = new File(file.getPath() + "-CATALOG.dml");
                    }
                }

                System.out.println("Save: " + file.getName() + ".");
                boolean doSaving = false;
                if (file.exists()) {
                    if (file.getPath().equals(compRef.getModelEditor().getFilePath())) {
                        doSaving = true; // do save
                        keepFileChooser = false; // close chooser
                    } else {
                        Object[] options = {"yes", "no", "cancel"};
                        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(fc), file.getName() + " is an existing file.\n Do you want to overwrite the file?", "Save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                        if (answer == 0) {
                            doSaving = true; // do save
                            keepFileChooser = false; // close chooser
                        } else if (answer == 1) {
                            doSaving = false; // do not save
                            keepFileChooser = true; // keep chooser
                        } else {
                            doSaving = false; // do save
                            keepFileChooser = false; // close chooser
                        }
                    }
                } else {
                    doSaving = true;
                    keepFileChooser = false; // close chooser
                }

                if (doSaving) {
                    try {
                        compRef.getModelEditor().getIDContainer().assignRandomIDToAllElements(compRef.getCurrentCModel());

                        DomeSerialization.save(compRef.getCurrentCModel(), compRef.getModelEditor().getIDContainer(), file.getPath());
                        compRef.getModelEditor().setFilePath(file.getPath());
                        compRef.getModelEditor().setWorkingDirectory(file.getParentFile());
                        compRef.getModelEditor().setHasUnsavedChanges(false);

                        ModelEditorFrame.setFrameTitle(filePath);
                        keepFileChooser = false;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (SAXException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                System.out.println("Save command cancelled by user.");
                keepFileChooser = false;
            }
        } while (keepFileChooser);
    }

    /** popups up new model dialog, and creates a new catalog when submitted */
    public void createNewModel() {
        boolean isClosed = compRef.getModelEditor().closeModel();
        if (! isClosed) {
            return;
        }
        compRef.getImplementationEditor().unindexCurrentCModel();

        ModelDialog modelDialog = new ModelDialog(compRef, false);
        modelDialog.updateLabelsForEditing(false);

        modelDialog.setModelName("my catalog model");
        modelDialog.setModelDescription("");

        modelDialog.setVisible(true);

        /* handles submission */
        if (modelDialog.isSubmitted()) {
            this.model = new CModel(modelDialog.getModelName());
            this.idContainer = new IDContainer();
            filePath = null;
            idContainer.setDocumentation(modelDialog.getModelDescription());
            CInterface itf = model.addInterface("default interface");
//            itf.addInputParameter("param A");
//            itf.addInputParameter("param B");
//            itf.addOutputParameter("param C");
//            itf.setDependency("param A", "param C");
//            itf.setDependency("param B", "param C");
            itf.addImplementation("default implementation");
            naviPanel.loadModel(model);
            naviPanel.selectDefaultTreeNode();

            compRef.getModelNavigationPanel().setModelNameEnabled(true);
            compRef.getModelNavigationPanel().setModelNameInNavPanel(compRef.getModelEditor().getModel().getName());
            ModelEditorKit.setEnabledOfAllActions(true);

            /* mark this model has been changed */
            compRef.markUnsavedChanges();

            ModelEditorFrame.setFrameTitle(filePath);
        }
    }

    /** close currently opened model */
    public boolean closeModel() {
        //speedup Clog.debug("close a model if any one is opened");

        /* quit evaluation mode */
        if (isEvaluationMode()) {
            ModelEditorKit.SwitchEvaluationModeAction.actionPerformed(new ActionEvent(this, 0, "EvaluationModeExit"));
        }
        /* clean up evaluation context */
        resetAllEvaluationContext();

        compRef.getImplementationEditor().unindexCurrentCModel();

        if (compRef.getImplementationEditor().isImplLoaded()) {
            /* close code completion and cell script editor */
            if (compRef.getImplementationEditor().isCodeCompletionShown()) {
                compRef.getImplementationEditor().closeCodeCompletion();
            }
            if (compRef.getImplementationEditor().isScriptEditorShown()) {
                compRef.getImplementationEditor().closeScriptEditor(true);
            }

            /* if there are unsaved changes, ask the user for an action */
            if (compRef.getModelEditor().getHasUnsavedChanges()) {
                Object[] options = {"yes", "no", "cancel"};
                int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(this), "Do you want to save changes?", "Save changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                if (answer == 0) {
                    /* YES: do file saving action */
                    ModelEditorKit.SaveAction.actionPerformed(new ActionEvent(this, 0, "save before unloading"));
                    /* CANCELED AT FILE SAVE DIALOG : if user cancels to save file, stop closing model */
                    if (compRef.getModelEditor().getFilePath() == null) {
                        return false;
                    }
                } else if (answer == 1) {
                    /* NO: continue closing model */
                } else {
                    /* CANCELED: stop closing model */
                    return false;
                }
            }
        }

        /* do closing */
        compRef.getInterfaceEditor().clearInterfaceBar();
        compRef.getRelationEditor().clearAllRelationBars();
        compRef.getImplementationEditor().setInterfaceName(null);
        compRef.getImplementationEditor().setImplementationName(null);
        compRef.getImplementationEditor().setImplLoaded(false);
        compRef.getModelNavigationPanel().unloadModel();
        compRef.getModelNavigationPanel().setModelNameEnabled(false);

        /** when closing a model, we call these maps associated with the current model. */
        clearModelEditorCache(null, null);

        this.model = null;
        ModelEditorKit.setEnabledOfAllActions(false);
        ModelEditorFrame.setFrameTitle(null);
        return true;
    }

    /** use null as a wild card name.
     * to clear some cache associated with "default interface", invoke with clearCache("default interface", null)
     * to clear some cache associated with "default implementation", invoke with clearCache(null, "default implementation")
     * to clear the whole cache, invoke with clearCache(null, null)
     */
    public void clearModelEditorCache(String itfName, String implName) {
        if (itfName == null && implName == null) {
            this.evalContextMap.clear(); // itfName + "|" + implName
            this.interfaceBarMap.clear(); // this.interfaceName + "|" + this.implementationName
            this.relationBarsMap.clear(); // this.interfaceName + "|" + this.implementationName
            this.modifiedParamSetMap.clear(); // itfName + "|" + implName
        }

        Map [] fourMaps = new Map[] { evalContextMap, interfaceBarMap, relationBarsMap, modifiedParamSetMap };
        for (int k = 0; k < fourMaps.length; k++) {
            for (Iterator i = fourMaps [k].entrySet().iterator(); i.hasNext(); ) {
                Map.Entry entry = (Map.Entry) i.next();
                String key = (String) entry.getKey();
                if (itfName == null && key.endsWith("|" + implName)) {
                    i.remove();
                } else if (implName == null && key.startsWith(itfName + "|")) {
                    i.remove();
                } else if (key.equals(itfName + "|" + implName)) {
                    i.remove();
                }
            }
        }
    }

    /** close currently opened model */
    public void quitEditor() {
        boolean isClosed = closeModel();
        /* if the current model is successfully closed, quit the editor */
        if (isClosed) {
            compRef.getRelationEditor().closeDomeConnnectionsOfRelationDialog();
            if(ModelEditorFrame.isStandAloneMode()) {
                System.exit(0);
            } else {
                ModelEditorFrame.getFrame().dispose();
            }
        }
    }

    public void slideCellConfig(int levelOfDetail) {
        cellConfig = CellConfig.createCellConfigFromLevelOfDetail(levelOfDetail);
        ModelEditorFrame.getCellConfigSlider().setValue(cellConfig.levelOfDetail);

        UIUtil.updateCellGeometryParameters(cellConfig);
        UIUtil.updateCellAndBarLayout(compRef);
        UIUtil.updateEditorBounds(compRef);
        UIUtil.adjustScriptEditorSizeOfAllCells(compRef);



        /* restore the last state */
        if (implEditor.isScriptEditorShown()) {
            compRef.getImplementationEditor().relocateCurrentlyShownScriptEditor();
        }
    }

    public void editCellConfig() {
        // initialize cell config dialog
        if (configDialog == null) {
            configDialog = new CellConfigDialog(ModelEditorFrame.getFrame(), this);
        }

        compRef.getModelEditor().pauseDynamicComponent(CENTER_PANEL_LABEL_ADJUSTER);

        configDialog.initRadioButtonsAndShowDialog();
        //configDialog.width4Rb.requestFocusInWindow();

        if (configDialog.isSubmitted()) {
            cellConfig = configDialog.getPreviewCellConfig();
            UIUtil.updateCellGeometryParameters(cellConfig);
            UIUtil.updateCellAndBarLayout(compRef);
            UIUtil.updateEditorBounds(compRef);
            UIUtil.adjustScriptEditorSizeOfAllCells(compRef);

            /* restore the last state */
            if (implEditor.isScriptEditorShown()) {
                compRef.getImplementationEditor().relocateCurrentlyShownScriptEditor();
            }
        } else {
            cellConfig = this.getCellConfig(); // restore geometry Parameters, which have been changed for the previewed cell.
            UIUtil.updateCellGeometryParameters(cellConfig);
        }
        compRef.getModelEditor().resumeDynamicComponent(CENTER_PANEL_LABEL_ADJUSTER);
    }

    public boolean isEvaluationMode() {
        return isEvaluationMode;
    }

    public void startEvaluationMode() {
        /* First finish TypeAndUnitQueryMode before starting the evaluation mode */
        if (isTypeAndUnitQueryMode()) {
            ModelEditorKit.SwitchTypeAndUnitQueryModeAction.actionPerformed(new ActionEvent(this, 0, "switch off the type-and-unit-query model"));
        }

        isEvaluationMode = true;
        List cells = compRef.getAllCells();
        for (int i = 0; i < cells.size(); i++) {
            BaseCell cell = (BaseCell) cells.get(i);
            CellConfig cellConfig = compRef.getCellConfig();
            cell.updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, compRef.getModelEditor().isTypeAndUnitQueryMode(), compRef.getModelEditor().isEvaluationMode());
        }
    }

    public void exitEvaluationMode() {
        isEvaluationMode = false;
        List cells = compRef.getAllCells();
        for (int i = 0; i < cells.size(); i++) {
            BaseCell cell = (BaseCell) cells.get(i);
            CellConfig cellConfig = compRef.getCellConfig();
            cell.updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, compRef.getModelEditor().isTypeAndUnitQueryMode(), compRef.getModelEditor().isEvaluationMode());
        }
    }

    public void setTypeAndUnitQueryMode(boolean isTypeAndUnitQueryMode) {
        this.isTypeAndUnitQueryMode = isTypeAndUnitQueryMode;
    }

    public boolean isTypeAndUnitQueryMode() {
        return isTypeAndUnitQueryMode;
    }

    public void startTypeAndUnitQueryMode() {
        if (! isTypeAndUnitQueryMode) {
            isTypeAndUnitQueryMode = true;
            List cells = compRef.getAllCells();
            for (int i = 0; i < cells.size(); i++) {
                BaseCell cell = (BaseCell) cells.get(i);
                CellConfig cellConfig = compRef.getCellConfig();
                cell.updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, true, compRef.getModelEditor().isEvaluationMode());
            }
        }
    }

    public void exitTypeAndUnitQueryMode() {
        if (isTypeAndUnitQueryMode) {
            isTypeAndUnitQueryMode = false;
            List cells = compRef.getAllCells();
            for (int i = 0; i < cells.size(); i++) {
                BaseCell cell = (BaseCell) cells.get(i);
                CellConfig cellConfig = compRef.getCellConfig();
                cell.updateVisibilityOfCellComponents(cellConfig.showTypeUnit, cellConfig.alignType, false, compRef.getModelEditor().isEvaluationMode());
            }
        }
    }

    /** open a JOptionPane that asks the name of newly created Implementation */
    public void addImplementation() {
        String answer = null;
        boolean isFirstTry = true;

        do {
            String msg = null;
            if (isFirstTry) {
                msg = "Please specify the name of the new implementation.";
            } else {
                msg = "Sorry you can't add use this implementation name. It should be unique in a model and not contain a period(.).";
            }
            answer = (String) JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(this), msg, "new implementation", JOptionPane.PLAIN_MESSAGE, null, null, (answer == null) ? "new implementation" : answer);
            System.out.println(answer);
            isFirstTry = false;
        } while (answer != null && ! isValidImplementationName(answer));

        if (answer == null) {
            System.out.println("cancel adding implementation");
            return;
        }

        compRef.getInterfaceEditor().addImplementationNameAndCModel(answer);
    }

//    /** remove selected implementatioin node. it starts by checking if the selected node is an implementation node, then, if true, remove the implementation */
//    public void removeImplementation() {
//        if ("implementation".equals(naviPanel.getSelectedNodeType())) {
//            String implName = naviPanel.getSelectedNodeInfo().implName;
//            Object[] options = { "OK", "Cancel" };
//            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Click OK to delete the selected implementation", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
//            if (answer != 0) {
//                return;
//            } else {
//                /* remove from CModel */
//                CModel model = compRef.getCurrentCModel();
//                Collection itfSet = model.getInterfaceMap().values();
//                for (Iterator i = itfSet.iterator(); i.hasNext();) {
//                    CInterface itf = (CInterface) i.next();
//                    itf.removeImplementation(implName);
//                }
//
//                /* remove from tree */
//                naviPanel.removeImplementationNode(implName);
//
//                /* mark this model has been changed */
//                compRef.markUnsavedChanges();
//            }
//        } else {
//            Object[] options = { "OK" };
//            JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Please select an implementation to be deleted in the navigation panel", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
//        }
//    }

    /** remove selected implementatioin or interface node. */
    public void removeImplementationOrInterface() {
        if ("implementation".equals(naviPanel.getSelectedNodeType())) {
            /* if to-be-deleted implementation is the last one, we can't delete it */
            if (compRef.getCurrentCInterface().getImplementationMap().size() == 1) {
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Sorry, you can't delete it. You need at least one implementation in a model.", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] { "OK" }, "OK");
                return;
            }

            String implName = naviPanel.getSelectedNodeInfo().implName;
            Object[] options = { "OK", "Cancel" };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Click OK to delete the selected implementation", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (answer != 0) {
                return;
            } else {
                /* remove from CModel */
                CModel model = compRef.getCurrentCModel();
                Collection itfSet = model.getInterfaceMap().values();

                for (Iterator i = itfSet.iterator(); i.hasNext();) {
                    CInterface itf = (CInterface) i.next();
                    itf.removeImplementation(implName);
                }

                /* remove from tree */
                naviPanel.removeImplementationNode(implName);

                naviPanel.selectDefaultTreeNode();
                compRef.getModelEditor().clearModelEditorCache(null, implName);

                /* mark this model has been changed */
                compRef.markUnsavedChanges();
            }
        } else if ("interface".equals(naviPanel.getSelectedNodeType())) {
            /* if to-be-deleted interface is the last one, we can't delete it */
            if (compRef.getCurrentCModel().getInterfaceMap().size() == 1) {
                JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Sorry, you can't delete it. You need at least one interface in a model.", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] { "OK" }, "OK");
                return;
            }

            String itfName = naviPanel.getSelectedNodeInfo().itfName;
            Object[] options = { "OK", "Cancel" };
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(naviPanel), "Click OK to delete the selected interface", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (answer != 0) {
                return;
            } else {
                /* remove from CModel */
                CModel model = compRef.getCurrentCModel();
                model.removeInterface(itfName);

                /* remove from tree */
                naviPanel.removeInterfaceNode(itfName);

                naviPanel.selectDefaultTreeNode();
                compRef.getModelEditor().clearModelEditorCache(itfName, null);

                /* mark this model has been changed */
                compRef.markUnsavedChanges();
            }
        }
    }

    public boolean isNavigationPanelVisible() {
        return isNavigationPanelVisible;
    }

    public boolean isStopClickedBefore() {
        return isStopClickedBefore;
    }

    public void setStopClickedBefore(boolean stopClickedBefore) {
        isStopClickedBefore = stopClickedBefore;
    }

    public void setNavigationPanelVisible(boolean visible) {
        if (visible) {
            int lastDividerLoation = splitPane.getLastDividerLocation();
            isNavigationPanelVisible = true;
            splitPane.setDividerLocation(lastDividerLoation);
        } else {
            isNavigationPanelVisible = false;
            splitPane.setDividerLocation(0);
        }
    }

    /** open a InterfaceDialog without showing implementation name option, and a new interface will be created based on InterfaceDialog inputs */
    public void addInterface() {
        compRef.getInterfaceEditor().addInterface();
    }

    private boolean isValidImplementationName(String newName) {
        if (newName.indexOf(".") != -1) {
            return false;
        }
        Collection itfSet = model.getInterfaceMap().values();
        for (Iterator i = itfSet.iterator(); i.hasNext();) {
            CInterface itf = (CInterface) i.next();
            Collection implNameSet = itf.getImplementationMap().keySet();
            for (Iterator j = implNameSet.iterator(); j.hasNext();) {
                String implName = (String) j.next();
                if (implName.trim().equals(newName)) {
                    return false;
                }
            }
            /* because all other interface has the same implementation names, we don't have to repeat the same operation again other interfaces */
            return true;
        }
        /* the model has no interface, it should not happen */
        throw new RuntimeException("current model does not have interface. we cannot add implementation when we have no interface to implement.");
    }

    public CModel getModel() {
        return model;
    }

    public ModelNavigationPanel getModelNavigationPanel() {
        return naviPanel;
    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

    public ImplementationEditor getImplementationEditor() {
        return implEditor;
    }

    public IDContainer getIDContainer() {
        return idContainer;
    }

    public boolean getHasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    public void setHasUnsavedChanges(boolean hasUnsavedChanges) {
        this.hasUnsavedChanges = hasUnsavedChanges;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setIDContainer(IDContainer idContainer) {
        this.idContainer = idContainer;
    }

    public File getWorkingDirectory() {
        return workingDir;
    }

    public void setWorkingDirectory(File workingDir) {
        this.workingDir = workingDir;
    }

    public boolean isDownwardExpandingSelection() {
        return isDownwardExpandingSelection;
    }

    public void setDownwardExpandingSelection() {
        isDownwardExpandingSelection = true;
        isUpwardExpandingSelection = false;
    }

    public boolean isUpwardExpandingSelection() {
        return isUpwardExpandingSelection;
    }

    public void stopExpandingSelection() {
        isUpwardExpandingSelection = false;
        isDownwardExpandingSelection = false;
    }

    public void setUpwardExpandingSelection() {
        isUpwardExpandingSelection = true;
        isDownwardExpandingSelection = false;
    }

    public CellConfig getCellConfig() {
        if (cellConfig == null) {
            cellConfig = CellConfig.createDefaultCellConfig();
        }
        return cellConfig;
    }

    public void setCellConfig(CellConfig cellConfig) {
        this.cellConfig = cellConfig;
    }

    public void addDynamicComponent(String compName, DynamicComponent dynamicComp) {
        dynamicComponentMap.put(compName, dynamicComp);
    }

    public void removeDynamicComponent(String compName) {
        dynamicComponentMap.remove(compName);
    }

    public DynamicComponent getDynamicComponent(String compName) {
        return (DynamicComponent) dynamicComponentMap.get(compName);
    }

    public boolean pauseDynamicComponent(String compName) {
        DefaultDynamicComponent dynComp = (DefaultDynamicComponent) dynamicComponentMap.get(compName);
        if (dynComp != null) {
            dynComp.setEnabled(false);
            return true;
        } else {
            return false;
        }
    }

    public boolean resumeDynamicComponent(String compName) {
        DefaultDynamicComponent dynComp = (DefaultDynamicComponent) dynamicComponentMap.get(compName);
        if (dynComp != null) {
            dynComp.setEnabled(true);
            return true;
        } else {
            return false;
        }
    }

    public Map getDynamicComponentMap() {
        return dynamicComponentMap;
    }

    public void editModel() {
        CModel model = compRef.getCurrentCModel();

        ModelDialog modelDialog = new ModelDialog(compRef, true);
        modelDialog.updateLabelsForEditing(true);

        modelDialog.setModelName(model.getName());
        modelDialog.setModelDescription(idContainer.getDocumentation());

        modelDialog.setVisible(true);

        /* handles submission */
        if (modelDialog.isSubmitted()) {
            model.setName(modelDialog.getModelName());
            idContainer.setDocumentation(modelDialog.getModelDescription());
            /* update tree */
            compRef.getModelNavigationPanel().setModelNameInNavPanel(modelDialog.getModelName());

            /* mark this model has been changed */
            compRef.markUnsavedChanges();
        }
    }

    /** a map of new String { itfName, implName } -> an InterfaceBar */
    public Map getInterfaceBarMap() {
        return interfaceBarMap;
    }

    /** a map of new String { itfName, implName } -> RelationBar[] { rel1, rel2, rel3 }*/
    public Map getRelationBarsMap() {
        return relationBarsMap;
    }

   /** a map of modified param set { itfName, implName } -> Set of modified param names */
    public Map getModifiedParamSetMap() {
        return modifiedParamSetMap;
    }
    /** return evaluation context instance for the given itfNamen and implName */
    protected EvaluationContext getEvaluationContext(String itfName, String implName) {
        String evalContextKey = itfName + "|" + implName;
        EvaluationContext evalContext = (EvaluationContext) evalContextMap.get(evalContextKey);
        if (evalContext == null) {
            evalContext = new EvaluationContext(compRef.getCurrentCModel().getInterface(itfName).getImplementation(implName));
            evalContext.setWorkingDirectory(DEFAULT_DOWNLOAD_DIRECTORY);
            //evalContext.addEvaluationListener(new SampleEvaluationListener(evalContext));
            evalContext.addEvaluationListener(new EvaluationModeListener(evalContext, compRef));
            evalContextMap.put(evalContextKey, evalContext);
        }
        return evalContext;
    }

    /** reset evaluation context instance for the given itfNamen and implName */
    protected void resetEvaluationContext(String itfName, String implName) {
        String evalContextKey = itfName + "|" + implName;
        EvaluationContext evalContext = (EvaluationContext) evalContextMap.get(evalContextKey);
        if (evalContext != null) {
            evalContextMap.remove(evalContextKey);
        }
    }

    /** reset evaluation context instance for the given itfNamen and implName */
    protected void resetAllEvaluationContext() {
        for (Iterator i = evalContextMap.values().iterator(); i.hasNext(); ) {
            EvaluationContext evalContext = (EvaluationContext) i.next();
            evalContext.close();
        }
        evalContextMap.clear();
    }
}