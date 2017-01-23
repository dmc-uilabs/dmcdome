// ContextBuilderMenus.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.build;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;

import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ContextBuilderMenus
{

	public static final ContextBuilderMenus menus = new ContextBuilderMenus();

	// editMenu
	protected JMenu editMenu = MenuUtils.makeBoldMenu("Edit definition");
	protected JMenu pasteCopyMenu = MenuUtils.makeMenu("Paste copy");
	protected JMenu pasteReferenceMenu = MenuUtils.makeMenu("Paste reference");
	protected JMenu mapMenu = MenuUtils.makeMenu("Map");
	protected JMenu addAndMapMenu = MenuUtils.makeMenu("Add and Map");
	protected JMenuItem copyMI, cutMI, pasteCopyLastSelectionMI, pasteCopyClipboardMI,
	pasteReferenceLastSelectionMI, pasteReferenceClipboardMI, removeMI, deleteMI,
	mapLastSelectionMI, mapClipboardMI, addAndMapLastSelectionMI, addAndMapClipboardMI;
	protected JMenuItem pasteCopyMI = MenuUtils.makeMenuItem("Paste copy");
	protected JMenuItem pasteReferenceMI = MenuUtils.makeMenuItem("Paste reference");
	protected JMenuItem mapMI = MenuUtils.makeMenuItem("Map");
	protected JMenuItem addAndMapMI = MenuUtils.makeMenuItem("Add and Map");

	// addMenu
	protected JMenu addMenu = MenuUtils.makeBoldMenu("Add");
	protected JMenuItem contextMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction("Context"));
	protected JMenuItem latchMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction("Latch"));

	protected JMenuItem[] dataTypeMIs;
	protected JMenuItem relationMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction("Procedural Relation"));
	protected JMenuItem visualizationMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction("Visualization"));
	protected JMenuItem equalrelationMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction("Equal Relation"));

    protected JMenu iterationrelationMenu = MenuUtils.makeMenu("Iteration Relation");
    protected JMenuItem whilerelationMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction(IterationRelation.WHILE_LOOP));
    protected JMenuItem dowhilerelationMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction(IterationRelation.DO_WHILE_LOOP));
    protected JMenuItem timesteprelationMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction(IterationRelation.Timestep_LOOP));

	protected boolean isRelationInsertion = false;
	protected boolean isAddOk = true;
	protected boolean isRemoveOk = true;
	protected boolean isClipboardEmpty;
	protected int pasteCopyIndex, pasteReferenceIndex, relationIndex, mapIndex, addAndMapIndex;

	// to do: disable last reference if different from current tree

	private ContextBuilderMenus()
	{
		List dataTypes = Registry.getDataObjectTypes();
		dataTypeMIs = new JMenuItem[dataTypes.size()];
		for (int i = 0; i < dataTypeMIs.length; ++i)
			dataTypeMIs[i] = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction((String) dataTypes.get(i)));
		copyMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.copyAction);
		cutMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.cutAction);
		pasteCopyLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteCopyLastSelectionAction);
		pasteCopyClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteCopyClipboardAction);
		pasteReferenceLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteReferenceLastSelectionAction);
		pasteReferenceClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteReferenceClipboardAction);
		mapLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.mapLastSelectionAction);
		mapClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.mapClipboardAction);
		addAndMapLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.addAndMapLastSelectionAction);
		addAndMapClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.addAndMapClipboardAction);
		removeMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.removeAction);
		deleteMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.deleteAction);

		editMenu.add(copyMI);
		editMenu.add(cutMI);
		pasteCopyIndex = editMenu.getItemCount();
		editMenu.add(pasteCopyMenu);
		pasteReferenceIndex = editMenu.getItemCount();
		editMenu.add(pasteReferenceMenu);
		editMenu.add(removeMI);

		editMenu.addSeparator();
		editMenu.add(MenuUtils.makeMenuItem(ContextTreeBuilderPanel.clearSelectionAction));
		editMenu.add(MenuUtils.makeMenuItem(ContextTreeBuilderPanel.selectAllAction));
		editMenu.addSeparator();
		editMenu.add(deleteMI);
		pasteCopyMenu.add(pasteCopyLastSelectionMI);
		pasteCopyMenu.add(pasteCopyClipboardMI);
		pasteReferenceMenu.add(pasteReferenceLastSelectionMI);
		pasteReferenceMenu.add(pasteReferenceClipboardMI);
		pasteCopyMI.setEnabled(false);
		pasteReferenceMI.setEnabled(false);
	    pasteCopyMI.setEnabled(false);

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
		addMenu.add(latchMI);
		addMenu.addSeparator();
		for (int i = 0; i < dataTypeMIs.length; ++i)
			addMenu.add(dataTypeMIs[i]);
		addMenu.addSeparator();
		relationIndex = addMenu.getItemCount();
		addMenu.add(relationMI);
		addMenu.add(equalrelationMI);
        addMenu.add(iterationrelationMenu);
        iterationrelationMenu.add(whilerelationMI);
        iterationrelationMenu.add(dowhilerelationMI);
        iterationrelationMenu.add(timesteprelationMI);

		addMenu.addSeparator();
		addMenu.add(visualizationMI);

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
				if (!isRelationInsertion) {
					editMenu.remove(pasteReferenceIndex);
					editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
				}
			} else { // enable paste options
				editMenu.remove(pasteCopyIndex);
				editMenu.insert(pasteCopyMenu, pasteCopyIndex);
				enableMapMIs();
				if (isRelationInsertion) {
					enableAddAndMapMIs();
				}
				if (!isRelationInsertion) {
					editMenu.remove(pasteReferenceIndex);
					editMenu.insert(pasteReferenceMenu, pasteReferenceIndex);
				}
			}
		} else { // paste disabled anyways
			isClipboardEmpty = BuildMode.clipboard.isEmpty();
		}
	}

	public void enableMapMIs()
	{
		mapIndex = getAddMenuItemIndex(mapMI);
		if (mapIndex < 0)
			return;
		addMenu.remove(mapIndex);
		addMenu.insert(mapMenu, mapIndex);
	}

	public void disableMapMIs()
	{
		mapIndex = getAddMenuItemIndex(mapMenu);
		if(mapIndex < 0)
			return;
		addMenu.remove(mapIndex);
		addMenu.insert(mapMI, mapIndex);
	}

	public void enableAddAndMapMIs()
	{
//      System.out.println("enableAddAndMap");
		addAndMapIndex = getAddMenuItemIndex(addAndMapMI);
		if (addAndMapIndex < 0)
			return;
		addMenu.remove(addAndMapIndex);
		addMenu.insert(addAndMapMenu, addAndMapIndex);
	}

	public void disableAddAndMapMIs()
	{
//      System.out.println("disableAddAndMap");
		addAndMapIndex = getAddMenuItemIndex(addAndMapMenu);
		if (addAndMapIndex < 0)
			return;
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


	public void enableAddMenus(boolean isRelation, boolean isEqualRelation)
	{
		if (!isEqualRelation) {
			enableAddMenus(isRelation);
//			removeMI.setEnabled(true);
//			deleteMI.setEnabled(true);
		} else {
			disableAddMenus();

//			removeMI.setEnabled(false);
//			deleteMI.setEnabled(false);

		}
	}


	public void enableMapMenusForEquals()
	{
		if (!isClipboardEmpty) {
			mapIndex = getAddMenuItemIndex(mapMI);
			if (mapIndex < 0)
				return;
			addMenu.remove(mapIndex);
			addMenu.insert(mapMenu, mapIndex);
		}
	}

	public void enableAddMenus(boolean isRelation)
	{

		if (!isAddOk) { // disabled before

			if (!isClipboardEmpty) {
				editMenu.remove(pasteCopyIndex);
				editMenu.insert(pasteCopyMenu, pasteCopyIndex);
				if (isRelation)
					enableAddAndMapMIs();
			}
			enableDataTypeMIs();
			if (!isRelation) {
				if (!isClipboardEmpty) {
					editMenu.remove(pasteReferenceIndex);
					editMenu.insert(pasteReferenceMenu, pasteReferenceIndex);
				}
				contextMI.setEnabled(true);
				latchMI.setEnabled(true);
				relationMI.setEnabled(true);
				equalrelationMI.setEnabled(true);
                iterationrelationMenu.setEnabled(true);
				visualizationMI.setEnabled(true);
//addMenu.remove(relationIndex);
//addMenu.insert(relationMenu,relationIndex);
			}
			isAddOk = true;
			isRelationInsertion = isRelation;
		} else { // already enabled; double-check if isRelation last time
			if (isRelationInsertion == isRelation) return;
			if (isRelation) { // disable options
				contextMI.setEnabled(false);
				latchMI.setEnabled(false);
				relationMI.setEnabled(false);
				equalrelationMI.setEnabled(false);
                iterationrelationMenu.setEnabled(false);
				visualizationMI.setEnabled(false);
				if (!isClipboardEmpty) {
					editMenu.remove(pasteReferenceIndex);
					editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
					enableAddAndMapMIs();
				}
//addMenu.remove(relationIndex);
//addMenu.insert(relationMI,relationIndex);
			} else { // enable options
				contextMI.setEnabled(true);
				latchMI.setEnabled(true);
				relationMI.setEnabled(true);
				equalrelationMI.setEnabled(true);
                iterationrelationMenu.setEnabled(true);
				visualizationMI.setEnabled(true);
				if (!isClipboardEmpty) {
					editMenu.remove(pasteReferenceIndex);
					editMenu.insert(pasteReferenceMenu, pasteReferenceIndex);
				}
				disableAddAndMapMIs();
//addMenu.remove(relationIndex);
//addMenu.insert(relationMenu,relationIndex);
			}
			isRelationInsertion = isRelation;
		}
	}

	public void disableAddMenus()
	{
		if (!isAddOk) return; // already disabled
		editMenu.remove(pasteCopyIndex);
		editMenu.insert(pasteCopyMI, pasteCopyIndex);
		disableMapMIs();
		disableAddAndMapMIs();
		disableDataTypeMIs();
		if (!isRelationInsertion) { // disable other things, too
			contextMI.setEnabled(false);
			latchMI.setEnabled(false);
			relationMI.setEnabled(false);
			equalrelationMI.setEnabled(false);
            iterationrelationMenu.setEnabled(false);
			visualizationMI.setEnabled(false);
			editMenu.remove(pasteReferenceIndex);
			editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
//addMenu.remove(relationIndex);
//addMenu.insert(relationMI,relationIndex);
			isRelationInsertion = true;
		}
		isAddOk = false;
	}

	public void enableRemoveMenus()
	{
		//if (isRemoveOk) return; // already enabled
		copyMI.setEnabled(true);
		cutMI.setEnabled(true);
		removeMI.setEnabled(true);
		deleteMI.setEnabled(true);
		isRemoveOk = true;
	}


    public void enableRemoveOnlyMenus()
	{
		//if (isRemoveOk) return; // already enabled
		copyMI.setEnabled(true);
		cutMI.setEnabled(true);
		removeMI.setEnabled(true);
		//deleteMI.setEnabled(true);
		isRemoveOk = true;
	}


	public void disableRemoveMenus()
	{
		if (!isRemoveOk) return; // already disabled
		copyMI.setEnabled(false);
		cutMI.setEnabled(false);
		removeMI.setEnabled(false);
		deleteMI.setEnabled(false);
		isRemoveOk = false;
	}

	public void disableDeleteMenus()
	{
		if (!isRemoveOk) return; // already disabled
		cutMI.setEnabled(false);
		removeMI.setEnabled(false);
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

    private int getAddMenuItemIndex(JMenuItem item) {
	    int index = -1;
		for(int i = 0; i < addMenu.getItemCount(); i++) {
			JMenuItem it = addMenu.getItem(i);
			if(item.equals(it)) {
				index = i;
				break;
			}
		}
	    return index;
    }

}
