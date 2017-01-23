// ProceduralRelationBuildMenus.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.*;
import java.util.List;

public class EqualRelationBuildMenus {
    public static final EqualRelationBuildMenus menus = new EqualRelationBuildMenus();

    // editMenu
    protected JMenu editMenu = mit.cadlab.dome3.swing.MenuUtils.makeBoldMenu("Edit equal relation");
    protected JMenu mapMenu = mit.cadlab.dome3.swing.MenuUtils.makeMenu("Map");
    protected JMenuItem copyMI, mapLastSelectionMI, mapClipboardMI;
    protected JMenuItem mapMI = mit.cadlab.dome3.swing.MenuUtils.makeMenuItem("Map");
    protected JMenuItem relationTestMI;

    // addMenu
    protected JMenu addMenu = mit.cadlab.dome3.swing.MenuUtils.makeBoldMenu("Add");//should be disabled
    protected JMenuItem[] dataTypeMIs;

    protected boolean isTestOk = true;
    protected boolean isAddOk = true;
    protected boolean isRemoveOk = true;
    protected boolean isClipboardEmpty;
 	protected int mapIndex;

	// to do: disable last reference if different from current tree

    private EqualRelationBuildMenus() {
//renu        List dataTypes = Registry.getValidDataTypes();
	    List dataTypes = mit.cadlab.dome3.config.Registry.getDataObjectTypes();
        dataTypeMIs = new JMenuItem[dataTypes.size()];
        for (int i=0; i<dataTypeMIs.length; ++i)
            dataTypeMIs[i] = MenuUtils.makeMenuItem(new EqualRelationTreeBuilderPanel.AddItemAction((String)dataTypes.get(i)));
        copyMI = MenuUtils.makeMenuItem(EqualRelationTreeBuilderPanel.copyAction);
        mapLastSelectionMI = MenuUtils.makeMenuItem(EqualRelationTreeBuilderPanel.mapLastSelectionAction);
        mapClipboardMI = MenuUtils.makeMenuItem(EqualRelationTreeBuilderPanel.mapClipboardAction);
        relationTestMI = MenuUtils.makeMenuItem(EqualRelationTreeBuilderPanel.TestAction);

        editMenu.add(copyMI);

        editMenu.addSeparator();
        editMenu.add(MenuUtils.makeMenuItem(EqualRelationTreeBuilderPanel.clearSelectionAction));
        editMenu.add(MenuUtils.makeMenuItem(EqualRelationTreeBuilderPanel.selectAllAction));


        mapMI.setEnabled(false);
        editMenu.addSeparator();
        relationTestMI.setEnabled(isTestOk);
        editMenu.add(relationTestMI);


	    mapIndex = addMenu.getItemCount();
		addMenu.add(mapMenu);
	    mapMenu.add(mapLastSelectionMI);
        mapMenu.add(mapClipboardMI);
	    addMenu.addSeparator();
        for (int i=0; i<dataTypeMIs.length; ++i)
            addMenu.add(dataTypeMIs[i]);

		disableDataTypeMIs();

        BuildMode.clipboard.addClipboardListener(new MenuClipboardListener());
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
                //editMenu.remove(pasteCopyIndex);
                //editMenu.insert(pasteCopyMI, pasteCopyIndex);
                disableMapMIs();
                //disableAddAndMapMIs();
            } else { // enable paste options
                //editMenu.remove(pasteCopyIndex);
                //editMenu.insert(pasteCopyMenu, pasteCopyIndex);
                enableMapMIs();
                //enableAddAndMapMIs();
            }
        } else { // paste disabled anyways
            isClipboardEmpty = BuildMode.clipboard.isEmpty();
        }
    }

    public void enableMapMIs() {
        addMenu.remove(mapIndex);
        addMenu.insert(mapMenu, mapIndex);
    }

    public void disableMapMIs() {
        addMenu.remove(mapIndex);
        addMenu.insert(mapMI, mapIndex);
    }

//  private void enableAddAndMapMIs() {
//        System.out.println("enableAddAndMap");
//        editMenu.remove(addAndMapIndex);
//        editMenu.insert(addAndMapMenu, addAndMapIndex);
//   }

//    private void disableAddAndMapMIs() {
//        System.out.println("disableAddAndMap");
//        editMenu.remove(addAndMapIndex);
//        editMenu.insert(addAndMapMI, addAndMapIndex);
//    }

//    private void enableDataTypeMIs() {
//        for (int i=0; i<dataTypeMIs.length; ++i)
//            dataTypeMIs[i].setEnabled(true);
//    }

   private void disableDataTypeMIs() {
        for (int i=0; i<dataTypeMIs.length; ++i)
            dataTypeMIs[i].setEnabled(false);
    }

//    public void enableAddMenus() {
//        if (!isAddOk) { // disabled before
//            if (!isClipboardEmpty) {
//              editMenu.remove(pasteCopyIndex);
//               editMenu.insert(pasteCopyMenu, pasteCopyIndex);
//               enableAddAndMapMIs();
//            }
//            enableDataTypeMIs();
//            isAddOk = true;
//        }
//   }

 //   public void disableAddMenus() {
//        if (!isAddOk) return; // already disabled
 //       if (!isClipboardEmpty) {
 //           editMenu.remove(pasteCopyIndex);
 //           editMenu.insert(pasteCopyMI, pasteCopyIndex);
 //           disableMapMIs();
 //           disableAddAndMapMIs();
 //       }
 //       disableDataTypeMIs();
 //       isAddOk = false;
 //   }

 // public void enableRemoveMenus() {
 //      if (isRemoveOk) return; // already enabled
 //      copyMI.setEnabled(true);
 // deleteMI.setEnabled(true);
 //      isRemoveOk = true;
//   }

 //  public void disableRemoveMenus() {
 //      if (!isRemoveOk) return; // already disabled
 //      copyMI.setEnabled(false);
 //  deleteMI.setEnabled(false);
 //     isRemoveOk = false;
 //}

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
