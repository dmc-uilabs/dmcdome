// FileObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.treeobject;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.model.tool.run.AnalysisToolRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.run.ModelInterfaceRunPanel;
import mit.cadlab.dome3.gui.objectmodel.project.run.ProjectRunPanel;
import mit.cadlab.dome3.gui.objectmodel.project.run.AbstractRunProjectFileSystemTable;
import mit.cadlab.dome3.gui.playspace.run.PlayspaceRunPanel;
import mit.cadlab.dome3.gui.playspace.run.AbstractRunPlayspaceFileSystemTable;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.playspace.AbstractPlayspace;
import mit.cadlab.dome3.objectmodel.project.AbstractIntegrationProject;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.swing.tree.GuiTreeObject;
import mit.cadlab.dome3.swing.WindowTracker;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Example object that implements TreeTableData
 */
public class PlayspaceInterfaceTreeObject extends DefaultTreeObject implements GuiTreeObject {
    private ClientObjectRecord file;
    protected JFrame gui = null;

    public PlayspaceInterfaceTreeObject(ClientInterfaceRecord ifaceRecord) {
        super(ifaceRecord, ifaceRecord.getContent());
        this.file = ifaceRecord;
    }

    public void setInterface(ModelInterfaceRuntimeClient iface) {
        ((ClientInterfaceRecord) data).setInterface(iface);
    }

    public Icon getIcon(int itemState) {
        return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.INTERFACE_OPEN : DomeIcons.INTERFACE);
    }

    public String getTreeValue() {
        return file.getName();
    }

    public List getChildren() {
        return file.getContent();
    }

    protected void makeGui() {
        // gui does not exist
        JComponent comp = RunFocusTracker.getCurrentComponent();
        if (comp instanceof PlayspaceRunPanel) {
            String name = ((AbstractPlayspace) (((PlayspaceRunPanel) comp).getGuiObject())).getName();
            JFrame waitWin = StatusWindow.show(StatusWindow.STARTING, name, comp.getLocationOnScreen());
            makeGuiWorker worker = new makeGuiWorker(waitWin, (ClientInterfaceRecord) data,false,((PlayspaceRunPanel)comp).getPrfst());
            worker.start();


        } else if (comp instanceof ProjectRunPanel) {
            String name = ((AbstractIntegrationProject) (((ProjectRunPanel) comp).getGuiObject())).getName();
            JFrame waitWin = StatusWindow.show(StatusWindow.STARTING, name, comp.getLocationOnScreen());
            makeGuiWorker worker = new makeGuiWorker(waitWin, (ClientInterfaceRecord) data,false,((ProjectRunPanel)comp).getPrfst());
            worker.start();


        } else if (comp instanceof AnalysisToolRunPanel)
            ((AnalysisToolRunPanel) comp).openAnalysisToolItem();
    }

    class makeGuiWorker extends SwingWorker {
        JFrame waitWin;
        ClientInterfaceRecord _data;
        boolean registerInterface;
        JComponent GuiListner;    //parse in the FileSystemTable so that we can fire interface creation property change when interfaces gets created

        public makeGuiWorker(JFrame waitWin, ClientInterfaceRecord _data,boolean _registerInterface,JComponent listener) {
            this.waitWin = waitWin;
            this._data = _data;
            this.registerInterface=_registerInterface;
            GuiListner=listener;
         }

        public Object construct() {
	        WindowTracker parent = RunMode.getCurrentWindowTracker();
            ModelInterfaceRuntimeClient mInterface = _data.getInterface();
            ModelInterfaceRunPanel pane = new ModelInterfaceRunPanel(mInterface);

            //Qing -- add here to fire interface creation property change!!!
            if(GuiListner instanceof AbstractRunProjectFileSystemTable) ((AbstractRunProjectFileSystemTable)GuiListner).fireInterfaceCreatedChange(_data);
            else if(GuiListner instanceof AbstractRunPlayspaceFileSystemTable) ((AbstractRunPlayspaceFileSystemTable)GuiListner).fireInterfaceCreatedChange(_data);

            gui = new DomeRunFrame(pane, parent);
            gui.addWindowListener(new WindowAdapter(){
                 public void windowClosed(WindowEvent e) {
                     gui=null;
                 }
             });
            gui.show();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);
            waitWin.dispose();
        }
    }

    public boolean showGui() {
        boolean statusChange = false;
        if (gui == null) {
            /* // gui does not exist
            JComponent comp = RunFocusTracker.getCurrentComponent();
              if (comp instanceof PlayspaceRunPanel)
              {
                  ((PlayspaceRunPanel) comp).openPlayspaceItem();
              }
              else if (comp instanceof ProjectRunPanel)
              {
                  ((ProjectRunPanel) comp).openProjectItem();
              }
              else if (comp instanceof AnalysisToolRunPanel)
                  ((AnalysisToolRunPanel)comp).openAnalysisToolItem();
              else
              {
                  System.err.println("PlayspaceInterfaceTreeObject.showGui: unknown object " + ClassUtils.getClassName(comp));
              }
             */
            makeGui();

            if (gui != null) {
                gui.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        hideGui();
                    }
                });
                gui.show();
                statusChange = true;
            }
        } else { // gui exists
            gui.toFront(); // make sure it is showing
        }
        if (statusChange)
            fireNodeValueChanged();
        return statusChange;

    }

    public boolean hideGui() {
        if (gui == null) { // gui does not exist
            return false;
        } else { // gui exists and is showing
            gui.hide();
            gui.dispose();
            gui = null;
            fireNodeValueChanged();
            return true;
        }
    }
}
