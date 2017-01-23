// ContextBuilderMenus.java
package mit.cadlab.dome3.gui.objectmodel.model.plugin;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.HashMap;

public class PluginBuildMenus
{

	public static final PluginBuildMenus menus = new PluginBuildMenus();
	private static final HashMap pluginDataTypeMIMap = new HashMap();

	protected String currentPluginType = "";

	// editMenu
	protected JMenu editMenu = MenuUtils.makeBoldMenu("Edit definition");
	protected JMenu pasteCopyMenu = MenuUtils.makeMenu("Paste copy");
	protected JMenu pasteReferenceMenu = MenuUtils.makeMenu("Paste reference");
	protected JMenuItem copyMI, cutMI, pasteCopyLastSelectionMI, pasteCopyClipboardMI,
	pasteReferenceLastSelectionMI, pasteReferenceClipboardMI, removeMI, deleteMI;
	protected JMenuItem pasteCopyMI = MenuUtils.makeMenuItem("Paste copy");
	protected JMenuItem pasteReferenceMI = MenuUtils.makeMenuItem("Paste reference");

	// addMenu
	protected JMenu addMenu = MenuUtils.makeBoldMenu("Add");
	protected JMenuItem contextMI = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction("Context"));
	protected JMenuItem[] dataTypeMIs = null;

	protected Boolean isAddOk = Boolean.TRUE;
	protected Boolean isRemoveOk = Boolean.TRUE;
	protected boolean isClipboardEmpty;
	protected int pasteCopyIndex, pasteReferenceIndex, dataTypeIndex;

	// to do: disable last reference if different from current tree

	public static void registerDataTypesForPlugin(String pluginType, String[] dataTypes)
	{
		JMenuItem[] pluginDataTypeMIs = new JMenuItem[dataTypes.length];
		for (int i = 0; i < pluginDataTypeMIs.length; ++i)
			pluginDataTypeMIs[i] = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction(dataTypes[i]));
		pluginDataTypeMIMap.put(pluginType, pluginDataTypeMIs);
	}

	private PluginBuildMenus()
	{
		copyMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.copyAction);
		cutMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.cutAction);
		pasteCopyLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteCopyLastSelectionAction);
		pasteCopyClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteCopyClipboardAction);
		pasteReferenceLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteReferenceLastSelectionAction);
		pasteReferenceClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteReferenceClipboardAction);
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

		addMenu.add(contextMI);
		addMenu.addSeparator();
		dataTypeIndex = addMenu.getItemCount();

		BuildMode.clipboard.addClipboardListener(new MenuClipboardListener());
		updateClipboardStatus();
	}

	public void setPluginDataTypes(String pluginType)
	{
		if (pluginType == null || currentPluginType.equals(pluginType))
			return;
		currentPluginType = pluginType;
		// clear old dataTypes
		if (dataTypeMIs != null)
			for (int i = 0; i < dataTypeMIs.length; i++) {
				addMenu.remove(dataTypeMIs[i]);
			}
		JMenuItem[] newDataTypeMIs = (JMenuItem[]) pluginDataTypeMIMap.get(pluginType);
		if (newDataTypeMIs == null) {
			dataTypeMIs = null;
		} else {
			dataTypeMIs = newDataTypeMIs;
			for (int i = 0; i < dataTypeMIs.length; i++)
				addMenu.add(dataTypeMIs[i]);
		}
		addMenu.repaint();
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
		if (isClipboardEmpty == BuildMode.clipboard.isEmpty()) return; // already consistent
			isClipboardEmpty = BuildMode.clipboard.isEmpty();
		if (isClipboardEmpty) { // disable paste options
			editMenu.remove(pasteCopyIndex);
			editMenu.insert(pasteCopyMI, pasteCopyIndex);
			editMenu.remove(pasteReferenceIndex);
			editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
    	}
		else { // enable paste options
			editMenu.remove(pasteCopyIndex);
			editMenu.insert(pasteCopyMenu, pasteCopyIndex);
			editMenu.remove(pasteReferenceIndex);
			editMenu.insert(pasteReferenceMenu, pasteReferenceIndex);
        }
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

	public void enableAddMenus()
	{
		synchronized (isAddOk) {
            editMenu.remove(pasteCopyIndex);
			editMenu.insert(pasteCopyMenu, pasteCopyIndex);
			editMenu.remove(pasteReferenceIndex);
			editMenu.insert(pasteReferenceMenu, pasteReferenceIndex);
			contextMI.setEnabled(true);
			enableDataTypeMIs();
		}
	}

	public void disableAddMenus()
	{
		synchronized (isAddOk) {
			editMenu.remove(pasteCopyIndex);
			editMenu.insert(pasteCopyMI, pasteCopyIndex);
			editMenu.remove(pasteReferenceIndex);
			editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
        	contextMI.setEnabled(false);
			disableDataTypeMIs();
		}
	}

	public void enableRemoveMenus()
	{
		synchronized (isRemoveOk) {
			copyMI.setEnabled(true);
			cutMI.setEnabled(true);
			removeMI.setEnabled(true);
			deleteMI.setEnabled(true);
		}
	}

	public void disableRemoveMenus()
	{
		synchronized (isRemoveOk) {
			copyMI.setEnabled(false);
			cutMI.setEnabled(false);
			removeMI.setEnabled(false);
			deleteMI.setEnabled(false);
		}
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
