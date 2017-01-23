// ProceduralRelationBuildMenus.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildMenus;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.config.Registry;

import javax.swing.*;
import java.util.List;

public class IterationRelationBuildMenus {
    public static final IterationRelationBuildMenus menus = new IterationRelationBuildMenus();

   protected JMenu editMenu = MenuUtils.makeBoldMenu("Edit iteration relation");
    protected JMenu pasteCopyMenu = MenuUtils.makeMenu("Paste copy");
    protected JMenu mapMenu = MenuUtils.makeMenu("Map");
    protected JMenu addAndMapMenu = MenuUtils.makeMenu("Add and Map");
    protected JMenuItem copyMI, pasteCopyLastSelectionMI, pasteCopyClipboardMI,
        mapLastSelectionMI, mapClipboardMI, addAndMapLastSelectionMI, addAndMapClipboardMI, deleteMI;
    protected JMenuItem pasteCopyMI = MenuUtils.makeMenuItem("Paste copy");
    protected JMenuItem mapMI = MenuUtils.makeMenuItem("Map");
    protected JMenuItem addAndMapMI = MenuUtils.makeMenuItem("Add and Map");
    protected JMenuItem relationTestMI;

    // addMenu
    protected JMenu addMenu = MenuUtils.makeBoldMenu("Add");
    protected JMenuItem[] dataTypeMIs;

    protected boolean isTestOk = true;
    protected boolean isAddOk = true;
    protected boolean isRemoveOk = true;
    protected boolean isClipboardEmpty;
    protected int pasteCopyIndex, mapIndex, addAndMapIndex;

    // to do: disable last reference if different from current tree

    private IterationRelationBuildMenus() {
//renu        List dataTypes = Registry.getValidDataTypes();
	    List dataTypes = Registry.getDataObjectTypes();
        dataTypeMIs = new JMenuItem[dataTypes.size()];
        for (int i=0; i<dataTypeMIs.length; ++i)
            dataTypeMIs[i] = MenuUtils.makeMenuItem(new IterationRelationTreeBuilderPanel.AddItemAction((String)dataTypes.get(i)));
        copyMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.copyAction);
        pasteCopyLastSelectionMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.pasteCopyLastSelectionAction);
        pasteCopyClipboardMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.pasteCopyClipboardAction);
        mapLastSelectionMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.mapLastSelectionAction);
        mapClipboardMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.mapClipboardAction);
        addAndMapLastSelectionMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.addAndMapLastSelectionAction);
        addAndMapClipboardMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.addAndMapClipboardAction);
        deleteMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.deleteAction);
        relationTestMI = MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.TestAction);

        editMenu.add(copyMI);
        pasteCopyIndex = editMenu.getItemCount();
        editMenu.add(pasteCopyMenu);
       // mapIndex = editMenu.getItemCount();
       // editMenu.add(mapMenu);
       // addAndMapIndex = editMenu.getItemCount();
        //editMenu.add(addAndMapMenu);
        editMenu.addSeparator();
        editMenu.add(MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.clearSelectionAction));
        editMenu.add(MenuUtils.makeMenuItem(IterationRelationTreeBuilderPanel.selectAllAction));
        editMenu.addSeparator();
        editMenu.add(deleteMI);
        pasteCopyMenu.add(pasteCopyLastSelectionMI);
        pasteCopyMenu.add(pasteCopyClipboardMI);

        pasteCopyMI.setEnabled(false);

        editMenu.addSeparator();
        relationTestMI.setEnabled(isTestOk);
        editMenu.add(relationTestMI);

	    mapIndex = addMenu.getItemCount();
        addMenu.add(mapMenu);
        addAndMapIndex = addMenu.getItemCount();
        addMenu.add(addAndMapMenu);
	    mapMenu.add(mapLastSelectionMI);
        mapMenu.add(mapClipboardMI);
        addAndMapMenu.add(addAndMapLastSelectionMI);
        addAndMapMenu.add(addAndMapClipboardMI);
	    mapMI.setEnabled(false);
        addAndMapMI.setEnabled(false);
		addMenu.addSeparator();

        for (int i=0; i<dataTypeMIs.length; ++i)
            addMenu.add(dataTypeMIs[i]);

        BuildMode.clipboard.addClipboardListener(new IterationRelationBuildMenus.MenuClipboardListener());
        updateClipboardStatus();
    }

    public JMenu getEditMenu() {
        return editMenu;
    }

    public JMenu getAddMenu() {
        return addMenu;
    }

    protected void updateClipboardStatus() {
        if (isAddOk) {
            if (isClipboardEmpty == BuildMode.clipboard.isEmpty()) return; // already consistent
            isClipboardEmpty = BuildMode.clipboard.isEmpty();
            if (isClipboardEmpty) { // disable paste options
                editMenu.remove(pasteCopyIndex);
                editMenu.insert(pasteCopyMI, pasteCopyIndex);
                disableMapMIs();
                disableAddAndMapMIs();
            } else { // enable paste options
                editMenu.remove(pasteCopyIndex);
                editMenu.insert(pasteCopyMenu, pasteCopyIndex);
                enableMapMIs();
                enableAddAndMapMIs();
            }
        } else { // paste disabled anyways
            isClipboardEmpty = BuildMode.clipboard.isEmpty();
        }
    }

    public void enableMapMIs() {
        if (!isAddOk) return;
        addMenu.remove(mapIndex);
        addMenu.insert(mapMenu, mapIndex);
    }

    public void disableMapMIs() {
        addMenu.remove(mapIndex);
        addMenu.insert(mapMI, mapIndex);
    }

    private void enableAddAndMapMIs() {
//        System.out.println("enableAddAndMap");
        addMenu.remove(addAndMapIndex);
        addMenu.insert(addAndMapMenu, addAndMapIndex);
    }

    private void disableAddAndMapMIs() {
//        System.out.println("disableAddAndMap");
        addMenu.remove(addAndMapIndex);
        addMenu.insert(addAndMapMI, addAndMapIndex);
    }

    private void enableDataTypeMIs() {
        for (int i=0; i<dataTypeMIs.length; ++i)
            dataTypeMIs[i].setEnabled(true);
    }

    private void disableDataTypeMIs() {
        for (int i=0; i<dataTypeMIs.length; ++i)
            dataTypeMIs[i].setEnabled(false);
    }

    public void enableAddMenus() {
        if (!isAddOk) { // disabled before
            if (!isClipboardEmpty) {
                editMenu.remove(pasteCopyIndex);
                editMenu.insert(pasteCopyMenu, pasteCopyIndex);
                enableAddAndMapMIs();
            }
            enableDataTypeMIs();
            isAddOk = true;
        }
    }

    public void disableAddMenus() {
        if (!isAddOk) return; // already disabled
        if (!isClipboardEmpty) {
            editMenu.remove(pasteCopyIndex);
            editMenu.insert(pasteCopyMI, pasteCopyIndex);
            disableMapMIs();
            disableAddAndMapMIs();
        }
        disableDataTypeMIs();
        isAddOk = false;
    }

    public void enableRemoveMenus() {
        if (isRemoveOk) return; // already enabled
        copyMI.setEnabled(true);
        deleteMI.setEnabled(true);
        isRemoveOk = true;
    }

    public void disableRemoveMenus() {
        if (!isRemoveOk) return; // already disabled
        copyMI.setEnabled(false);
        deleteMI.setEnabled(false);
        isRemoveOk = false;
    }

    class MenuClipboardListener implements DListListener {
        public void intervalChanged(DListEvent e) {
            updateClipboardStatus();
        }

        public void intervalAdded(DListEvent e) {
            updateClipboardStatus();
        }

        public void intervalRemoved(DListEvent e) {
            updateClipboardStatus();
        }

        public void itemsRemoved(DListEvent e) {
            updateClipboardStatus();
        }

        public void itemsReplaced(DListEvent e) {
            updateClipboardStatus();
        }
    }
}
