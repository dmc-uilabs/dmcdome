// ModelInterfaceBuildMenus.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.*;
import java.util.List;

public class ModelInterfaceBuildMenus
{
	public static final ModelInterfaceBuildMenus menus = new ModelInterfaceBuildMenus();

	// editMenu
	protected JMenu editMenu = MenuUtils.makeBoldMenu("Edit Interface");
	protected JMenu pasteCopyMenu = MenuUtils.makeMenu("Paste copy");
	protected JMenu mapMenu = MenuUtils.makeMenu("Map");
	protected JMenu addAndMapMenu = MenuUtils.makeMenu("Add and Map");
	protected JMenuItem copyMI, cutMI, pasteCopyLastSelectionMI, pasteCopyClipboardMI,
	mapLastSelectionMI, mapClipboardMI, addAndMapLastSelectionMI, addAndMapClipboardMI, deleteMI;
	protected JMenuItem pasteCopyMI = MenuUtils.makeMenuItem("Paste copy");
	protected JMenuItem mapMI = MenuUtils.makeMenuItem("Map");
	protected JMenuItem addAndMapMI = MenuUtils.makeMenuItem("Add and Map");
	protected JMenuItem selectAllMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.selectAllAction);
	protected JMenuItem clearSelMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.clearSelectionAction);
	// addMenu
	protected JMenu addMenu = MenuUtils.makeBoldMenu("Add");
	protected JMenuItem[] dataTypeMIs;
	protected JMenuItem contextMI = MenuUtils.makeMenuItem(new ModelInterfaceTreeBuilderPanel.AddItemAction("Context"));
	protected JMenuItem relationMI = MenuUtils.makeMenuItem(new ModelInterfaceTreeBuilderPanel.AddItemAction("Procedural Relation"));

	protected boolean isAddOk = true;
	protected boolean isRemoveOk = true;
	protected boolean isClipboardEmpty;
	protected int pasteCopyIndex, mapIndex, addAndMapIndex;

	// to do: disable last reference if different from current tree

	private ModelInterfaceBuildMenus()
	{
		List dataTypes = Registry.getDataObjectTypes();
		dataTypeMIs = new JMenuItem[dataTypes.size()];
		for (int i = 0; i < dataTypeMIs.length; ++i)
			dataTypeMIs[i] = MenuUtils.makeMenuItem(new ModelInterfaceTreeBuilderPanel.AddItemAction((String) dataTypes.get(i)));
		copyMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.copyAction);
		cutMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.cutAction);
		pasteCopyLastSelectionMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.pasteCopyLastSelectionAction);
		pasteCopyClipboardMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.pasteCopyClipboardAction);
		mapLastSelectionMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.mapLastSelectionAction);
		mapClipboardMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.mapClipboardAction);
		addAndMapLastSelectionMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.addAndMapLastSelectionAction);
		addAndMapClipboardMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.addAndMapClipboardAction);
		deleteMI = MenuUtils.makeMenuItem(ModelInterfaceTreeBuilderPanel.deleteAction);

		editMenu.add(copyMI);
		editMenu.add(cutMI);
		pasteCopyIndex = editMenu.getItemCount();
		editMenu.add(pasteCopyMenu);
		//mapIndex = editMenu.getItemCount();
		//editMenu.add(mapMenu);
		//addAndMapIndex = editMenu.getItemCount();
		//editMenu.add(addAndMapMenu);
		editMenu.addSeparator();
		editMenu.add(clearSelMI);
		editMenu.add(selectAllMI);
		editMenu.addSeparator();
		editMenu.add(deleteMI);
		pasteCopyMenu.add(pasteCopyLastSelectionMI);
		pasteCopyMenu.add(pasteCopyClipboardMI);
		pasteCopyMI.setEnabled(false);

		addMenu.addSeparator();//for the subscription 
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
		addMenu.add(contextMI);
		addMenu.addSeparator();
		for (int i = 0; i < dataTypeMIs.length; ++i)
			addMenu.add(dataTypeMIs[i]);
		addMenu.addSeparator();
		addMenu.add(relationMI);

		BuildMode.clipboard.addClipboardListener(new MenuClipboardListener());
		updateClipboardStatus();
	}

	public JMenu getEditMenu()
	{
		return editMenu;
	}

	public JMenu getAddMenu()
	{
		return addMenu;
	}

	protected void updateClipboardStatus()
	{
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

	public void enableMapMIs()
	{
		if (!isAddOk) return;
		addMenu.remove(mapIndex);
		addMenu.insert(mapMenu, mapIndex);
	}

	public void disableMapMIs()
	{
		addMenu.remove(mapIndex);
		addMenu.insert(mapMI, mapIndex);
	}

	public void enableAddAndMapMIs()
	{
//            System.out.println("enableAddAndMap");
		addMenu.remove(addAndMapIndex);
		addMenu.insert(addAndMapMenu, addAndMapIndex);
	}

	public void disableAddAndMapMIs()
	{
//            System.out.println("disableAddAndMap");
		addMenu.remove(addAndMapIndex);
		addMenu.insert(addAndMapMI, addAndMapIndex);
	}

	private void enableDataTypeMIs()
	{
		for (int i = 0; i < dataTypeMIs.length; ++i)
			dataTypeMIs[i].setEnabled(true);
	}

	private void disableDataTypeMIs()
	{
		for (int i = 0; i < dataTypeMIs.length; ++i)
			dataTypeMIs[i].setEnabled(false);
	}

	public void enableContextRelationMIs()
	{
		contextMI.setEnabled(true);
		relationMI.setEnabled(true);
	}

	public void disableContextRelationMIs()
	{
		contextMI.setEnabled(false);
		relationMI.setEnabled(false);
	}

	public void enableContextMIs()
	{
		contextMI.setEnabled(true);
	}

	public void disableContextMIs()
	{
		contextMI.setEnabled(false);
	}

	public void enableRelationMIs()
	{
		relationMI.setEnabled(true);
	}

	public void disableRelationMIs()
	{
		relationMI.setEnabled(false);
	}

	public void enableSelectionMIs()
	{
		selectAllMI.setEnabled(true);
		clearSelMI.setEnabled(true);
	}

	public void disableSelectionMIs()
	{
		selectAllMI.setEnabled(false);
		clearSelMI.setEnabled(false);
	}

	public void enableAddMenus()
	{
		if (!isAddOk) { // disabled before
			if (!isClipboardEmpty) {
				editMenu.remove(pasteCopyIndex);
				editMenu.insert(pasteCopyMenu, pasteCopyIndex);
				enableAddAndMapMIs();
			}
			enableDataTypeMIs();
			enableContextRelationMIs();
			isAddOk = true;
		}
	}

	public void disableAddMenus()
	{
		if (!isAddOk) return; // already disabled
		if (!isClipboardEmpty) {
			editMenu.remove(pasteCopyIndex);
			editMenu.insert(pasteCopyMI, pasteCopyIndex);
			disableMapMIs();
			disableAddAndMapMIs();
		}
		disableDataTypeMIs();
		disableContextRelationMIs();
		isAddOk = false;
	}

	public void enableRemoveMenus()
	{
		if (isRemoveOk) return; // already enabled
		copyMI.setEnabled(true);
		cutMI.setEnabled(true);
		deleteMI.setEnabled(true);
		isRemoveOk = true;
	}

	public void disableRemoveMenus()
	{
		if (!isRemoveOk) return; // already disabled
		copyMI.setEnabled(false);
		cutMI.setEnabled(false);
		deleteMI.setEnabled(false);
		isRemoveOk = false;
	}

	class MenuClipboardListener implements DListListener
	{
		public void intervalChanged(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void intervalAdded(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void intervalRemoved(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void itemsRemoved(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void itemsReplaced(DListEvent e)
		{
			updateClipboardStatus();
		}
	}
}
