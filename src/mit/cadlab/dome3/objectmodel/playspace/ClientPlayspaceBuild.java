package mit.cadlab.dome3.objectmodel.playspace;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Name: ClientPlayspaceBuild
 * User: thorek
 * Date: Mar 13, 2003
 * Time: 12:48:06 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */

public class ClientPlayspaceBuild extends ClientPlayspace {
    public static final String FILENAME = "fileName";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    protected String fileName = "";
    private boolean saveAsOp = false;
    protected boolean shouldSave = true;


    /**
     * Create an empty transient playspace.
     * @param serverConnection Server connection
     */
    public ClientPlayspaceBuild(ServerConnection serverConnection) {
        super(serverConnection);
        changeId();
        lastSavedXml = XMLUtils.createXmlDocument(toXmlElement()).asXML();
    }

    /**
     * Create a playspace from an XML description.
     * @param serverConnection Server connection
     * @param xmlElement
     */
    public ClientPlayspaceBuild(ServerConnection serverConnection, Element xmlElement) {
        super(serverConnection, xmlElement);

        // load project ids
        List projectElements = xmlElement.selectNodes("projects/" + IntegrationProject.XML_TAG);
        for (Iterator iter = projectElements.iterator(); iter.hasNext();) {
            Element projectElement = (Element) iter.next();
            addStaticObjectInfo(projectElement, projectInfo);
        }

        // load model ids
        List modelElements = xmlElement.selectNodes("models/" + DomeModelRuntime.XML_TAG);
        for (Iterator iter = modelElements.iterator(); iter.hasNext();) {
            Element modelElement = (Element) iter.next();
            addStaticObjectInfo(modelElement, modelInfo);
        }

// load tool ids
        List toolElements = xmlElement.selectNodes("tools/" + OptimizationToolRuntime.XML_TAG);
        for (Iterator iter = toolElements.iterator(); iter.hasNext();) {
            Element toolElement = (Element) iter.next();
            addStaticObjectInfo(toolElement, analysisToolInfo);
        }

        lastSavedXml = XMLUtils.createXmlDocument(toXmlElement()).asXML();
    }


    public String getXmlTag() {
        return super.getXmlTag();
    }


    protected TypeInfo getTypeInfo() {
        return super.getTypeInfo();
    }


    /**
     * Get project records as a collection of <project name, project description, project id, url, type>.
     * @return Collection of string arrays
     */
    public Collection getProjects() {
        ArrayList list = new ArrayList();
        Collection staticIds = projectInfo.getAllStaticIds();
        for (Iterator projectIter = staticIds.iterator(); projectIter.hasNext();) {
            String id = (String) projectIter.next();
            String name = projectInfo.getName(id);
            String description = projectInfo.getDescription(id);
            String url = projectInfo.getUrl(id);
            String[] record = new String[]{name, description, url, id, "project"};
            list.add(record);
        }
        return list;
    }

    // todo: eliminate this method in favor of getModelRecords
    /**
     * Get model records as a collection of <model name, model description, model id, url, type>.
     * @return Collection of string arrays
     */
    public Collection getModels() {
        ArrayList list = new ArrayList();
        Collection staticIds = modelInfo.getAllStaticIds();
        for (Iterator modelIter = staticIds.iterator(); modelIter.hasNext();) {
            String id = (String) modelIter.next();
            String name = modelInfo.getName(id);
            String description = modelInfo.getDescription(id);
            String url = modelInfo.getUrl(id);
            String[] record = new String[]{name, description, url, id, "model"};
            list.add(record);
        }
        return list;
    }

    public Collection getTools() {
        ArrayList list = new ArrayList();
        Collection staticIds = analysisToolInfo.getAllStaticIds();
        for (Iterator projectIter = staticIds.iterator(); projectIter.hasNext();) {
            String id = (String) projectIter.next();
            String name = analysisToolInfo.getName(id);
            String description = analysisToolInfo.getDescription(id);
            String url = analysisToolInfo.getUrl(id);
            String[] record = new String[]{name, description, url, id, DomeFile.ANALYSIS_TOOL_TYPE};
            list.add(record);
        }
        return list;
    }

    /**
     * Get model records as a list of ClientModelRecords. This should replace the method getModels() above
     * when time allows.
     * @return Collection of string arrays
     */
    public Collection getModelRecords() {
        ArrayList list = new ArrayList();
        Collection staticIds = modelInfo.getAllStaticIds();
        for (Iterator modelIter = staticIds.iterator(); modelIter.hasNext();) {
            String id = (String) modelIter.next();
            String name = modelInfo.getName(id);
            String description = modelInfo.getDescription(id);
            String url = modelInfo.getUrl(id);
            CompoundId modelId = new CompoundId(runtimeId);
            modelId.setModelStaticId(id);
            ClientModelRecord record = new ClientModelRecord(modelId, name, description, url);
            list.add(record);
        }
        return list;
    }

    /**
     * Get model records as a list of ClientAnalysisToolRecords.  his should replace the method getTools() above
     * when time allows.
     * @return Collection of string arrays
     */
    public Collection getToolRecords() {
        ArrayList list = new ArrayList();
        Collection staticIds = analysisToolInfo.getAllStaticIds();
        for (Iterator toolIter = staticIds.iterator(); toolIter.hasNext();) {
            String id = (String) toolIter.next();
            String name = analysisToolInfo.getName(id);
            String description = analysisToolInfo.getDescription(id);
            String url = analysisToolInfo.getUrl(id);
            CompoundId toolId = new CompoundId(runtimeId);
            toolId.setModelStaticId(id);//todo:  is this correct?? check with Jacob
            ClientAnalysisToolRecord record = new ClientAnalysisToolRecord(toolId, name, description, url, (OptimizationToolClientRuntime)analysisToolInfo.getObjectFromStaticId(id), null);
            list.add(record);
        }
        return list;
    }

    /**
     * Get model records as a list of ClientProjectRecords. This should replace the method getProjects() above
     * when time allows.
     * @return Collection of string arrays
     */
    public Collection getProjectRecords() {
        ArrayList list = new ArrayList();
        Collection staticIds = projectInfo.getAllStaticIds();
        for (Iterator projectIter = staticIds.iterator(); projectIter.hasNext();) {
            String id = (String) projectIter.next();
            String name = projectInfo.getName(id);
            String description = projectInfo.getDescription(id);
            String url = projectInfo.getUrl(id);
            CompoundId projectId = new CompoundId();
            projectId.addProjectStaticId(id);
            ClientProjectRecord record = new ClientProjectRecord(projectId, name, description, url);
            list.add(record);
        }
        return list;
    }


    public boolean containsModel(String modelId) {
        Collection staticIds = modelInfo.getAllStaticIds();
        return staticIds.contains(modelId);
    }

    public boolean containsProject(String projectId) {
        Collection staticIds = projectInfo.getAllStaticIds();
        return staticIds.contains(projectId);
    }

    public void addProject(String projectStaticId, String name, String description, String url) {
        projectInfo.addStaticInfo(projectStaticId, name, description, url);
    }

    public void addModel(String modelStaticId, String name, String description, String url) {
        modelInfo.addStaticInfo(modelStaticId, name, description, url);
    }

    public void removeProject(String projectStaticId) {
        projectInfo.removeUsingStaticId(projectStaticId);
    }

    public void removeModel(String modelStaticId) {
        modelInfo.removeUsingStaticId(modelStaticId);
    }

    //for tools
    public boolean containsTool(String toolId) {
        Collection staticIds = analysisToolInfo.getAllStaticIds();
        return staticIds.contains(toolId);
    }

    public void addTool(String toolStaticId, String name, String description, String url) {
        analysisToolInfo.addStaticInfo(toolStaticId, name, description, url);
    }

    public void removeTool(String toolStaticId) {
        analysisToolInfo.removeUsingStaticId(toolStaticId);
    }


    public void setFileName(String fileName) {
        String oldFileName = this.fileName;
        this.fileName = fileName;
        firePropertyChange(FILENAME, oldFileName, fileName);
    }

    public String getFileName() {
        return fileName;
    }

    protected static Dimension SAVE_ERROR_SIZE = new Dimension(250, 100);

    public void save() {
        try {
            if (fileName.equals("")) {
                saveAs();
            } else {
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, (new File(fileName)).getName(), getStatusWindowLocation());
                ClientPlayspaceBuild.saveModelWorker worker = new ClientPlayspaceBuild.saveModelWorker(this, fileName, waitWin);
                worker.start();
                //super.save(fileName); moved to saveModelWorker
            }
        } catch (Exception e) {
            e.printStackTrace();
            OneButton2Msg.showError(null, "Error: save", "could not be saved\nSee message log for details.",
                    getName(), "OK", SAVE_ERROR_SIZE);
        }
    }


    public void save(String fileName) {
        JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, (new File(fileName)).getName(), getStatusWindowLocation());
        ClientPlayspaceBuild.saveModelWorker worker = new ClientPlayspaceBuild.saveModelWorker(this, fileName, waitWin);
        worker.start();
        //super.save(fileName); moved to saveModelWorker
        setFileName(fileName);
    }

    protected void superSave(String filename) {
        super.save(filename);
    }

    public void saveAs() {
        saveAsOp = true;
        DomeFileChooser fc = new DomeFileChooser(fileName);
        fc.setFilter(DomeFileChooser.DOME_PLAYSPACE_FILTER);
        switch (fc.showSaveDialog(BuildMode.getCurrentPlayspaceFrame())) { // get parent component
            case JFileChooser.APPROVE_OPTION:
                break;
            default:
                return;
        }
        String newFileName = fc.getSelectedFile().getAbsolutePath(); // never empty
        if (!newFileName.endsWith(".dps")) { // no extension
            newFileName = newFileName + ".dps";
        }
        if (newFileName.equals(fileName)) { // same file as before
            String msg = "Can not save the new playspace in the original file.  Choose a different file name.";
            OneButton1Msg.showWarning(null, "Warning: Save As", msg, "OK", new Dimension(1, 1));
        } else {
            // check if file already exists
            File file = new File(newFileName);
            if (file.exists()) {
                String msg = null;
                msg = "File <" + file.getName() + "> already exists. Replace it?";
                int button = TwoButton1Msg.showWarning(null,
                        "Warning: File exists", msg, "Replace",
                        "Cancel", new Dimension(1, 1));
                if (button == 0) return;
            }

            if (fileName.equals("")) { // no file name before
                save(newFileName);
            } else {
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, file.getName(), getStatusWindowLocation());
                ClientPlayspaceBuild.saveAsModelWorker worker = new ClientPlayspaceBuild.saveAsModelWorker(this, newFileName, waitWin);
                worker.start();
                // this.setShouldSave(true); moved to saveAsModelWorker
                // BuildMode.playspaceSaveAs(this, newFileName); moved to saveAsModelWorker
            }
        }

    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }

    public boolean getShouldSave() {
        return shouldSave;
    }

    public void setName(String newName) {
        name = newName;
        firePropertyChange(NameListener.NAME, null, newName);
    }

    public String getName() {
        return name;
    }

    protected void changeId() {
        runtimeId = new CompoundId();
        runtimeId.setPlayspaceStaticId(UUIDGenerator.create());
    }


    public ClientPlayspaceBuild getSaveAsCopy() {
        if (saveAsOp) {
            this.changeId();
            saveAsOp = false;
            return this;
        } else
            return this;  //no changes to model for other ops
    }

    public void setDescription(String objectType, String id, String description) {
        if (objectType.equalsIgnoreCase("model")) {
            modelInfo.setDescription(id, description);
        } else if (objectType.equalsIgnoreCase("project")) {
            projectInfo.setDescription(id, description);
        }
         else if (objectType.equalsIgnoreCase(DomeFile.ANALYSIS_TOOL_TYPE)) {
            analysisToolInfo.setDescription(id, description);
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

    static class saveModelWorker extends SwingWorker {
        ClientPlayspaceBuild build;
        JFrame waitWin;
        String fileName;

        public saveModelWorker(ClientPlayspaceBuild build, String fileName, JFrame waitWin) {
            this.build = build;
            this.waitWin = waitWin;
            this.fileName = fileName;
        }

        public Object construct() {
            build.superSave(fileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    static class saveAsModelWorker extends SwingWorker {
        ClientPlayspaceBuild build;
        String newFileName;
        JFrame waitWin;

        public saveAsModelWorker(ClientPlayspaceBuild build, String newFileName, JFrame waitWin) {
            this.build = build;
            this.waitWin = waitWin;
            this.newFileName = newFileName;
        }

        public Object construct() {
            build.setShouldSave(true);
            BuildMode.playspaceSaveAs(build, newFileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    /*
    public void submitXmlDescription(int folderId)
    {
        Vector result;
        Element xml = toXmlElement();
        String xmlString = xml.asXML();
        result = FileSystemFunctions.updatePlayspace(serverConnection, runtimeId.getPlayspaceStaticId(),
                name, description, folderId, xmlString,
                version.getMajorMinorVersion());
    }
    */

}
