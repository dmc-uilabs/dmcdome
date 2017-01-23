// ContextBuilderMenus.java
package mit.cadlab.dome3.gui.objectmodel.model.tool.build;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildListPanel;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.HashMap;

public class AnalysisToolBuildMenus
{

	public static final AnalysisToolBuildMenus menu = new AnalysisToolBuildMenus();
	private static final HashMap _toolDataTypeMIMap = new HashMap();

	protected String _currentToolType = "";

	// editMenu
	protected JMenu _editMenu = MenuUtils.makeBoldMenu("");
	protected JMenu _pasteCopyMenuObjective = MenuUtils.makeMenu("Paste copy");
	protected JMenu pasteCopyMenu = MenuUtils.makeMenu("Paste copy");
	protected JMenu pasteReferenceMenu = MenuUtils.makeMenu("Paste reference");
	protected JMenuItem copyMI, cutMI, pasteCopyLastSelectionMI, pasteCopyClipboardMI,
	pasteReferenceLastSelectionMI, pasteReferenceClipboardMI, removeMI, deleteMI, _addAndSubScribeMenuItem;
	protected JMenuItem pasteCopyMI = MenuUtils.makeMenuItem("Paste copy");
	protected JMenuItem pasteReferenceMI = MenuUtils.makeMenuItem("Paste reference");

	// addMenu
	protected JMenu addMenu = MenuUtils.makeBoldMenu("Add");

    // special add menu for the project tab inside an anlysis tool
    protected JMenu _projectAddMenu;

//  TODO:
//  TODO: in the future add a context menu item
//  TODO:

	protected JMenuItem[] dataTypeMIs = null;
	protected JMenu _mapMenu = MenuUtils.makeMenu("Map");
	protected JMenu _addAndMapMenu = MenuUtils.makeMenu("Add and Map");

	protected boolean isAddOk = true;
	protected boolean isRemoveOk = true;
	protected boolean isClipboardEmpty;

	protected int _mapMenuIndex, _addAndMapMenuIndex;
	protected int pasteCopyIndex, pasteReferenceIndex, dataTypeIndex;

	private JMenuItem _mapMI = MenuUtils.makeMenuItem("Map");
	private JMenuItem _addAndMapMI = MenuUtils.makeMenuItem("Add and Map");

	private JMenuItem _mapLastSelectionMI, _mapClipboardMI, _addAndMapLastSelectionMI, _addAndMapClipboardMI;

    // to do: disable last reference if different from current tree

	public static void registerDataTypesForTool(String toolType, String[] dataTypes)
	{
		JMenuItem[] toolDataTypeMIs = new JMenuItem[dataTypes.length];
		for (int i = 0; i < toolDataTypeMIs.length; ++i)
			toolDataTypeMIs[i] = MenuUtils.makeMenuItem(new ContextTreeBuilderPanel.AddItemAction(dataTypes[i]));
		AnalysisToolBuildMenus._toolDataTypeMIMap.put(toolType, toolDataTypeMIs);
	}

	public AnalysisToolBuildMenus()
	{
		copyMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.copyAction);
		cutMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.cutAction);
		pasteCopyLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteCopyLastSelectionAction);
		pasteCopyClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteCopyClipboardAction);
		pasteReferenceLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteReferenceLastSelectionAction);
		pasteReferenceClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.pasteReferenceClipboardAction);
		_mapLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.mapLastSelectionAction);
		_mapClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.mapClipboardAction);
		_addAndMapLastSelectionMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.addAndMapLastSelectionAction);
		_addAndMapClipboardMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.addAndMapClipboardAction);

		removeMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.removeAction);
		deleteMI = MenuUtils.makeMenuItem(ContextTreeBuilderPanel.deleteAction);

		_editMenu.add(copyMI);
		_editMenu.add(cutMI);
		pasteCopyIndex = _editMenu.getItemCount();
		this._editMenu.add(pasteCopyMenu);
		this.pasteReferenceIndex = this._editMenu.getItemCount();
		this._editMenu.add(pasteReferenceMenu);
		this._editMenu.add(removeMI);
		this._editMenu.addSeparator();
		this._editMenu.add(MenuUtils.makeMenuItem(ContextTreeBuilderPanel.clearSelectionAction));
		this._editMenu.add(MenuUtils.makeMenuItem(ContextTreeBuilderPanel.selectAllAction));
		this._editMenu.addSeparator();
		this._editMenu.add(deleteMI);
		pasteCopyMenu.add(pasteCopyLastSelectionMI);
		pasteCopyMenu.add(pasteCopyClipboardMI);
		pasteReferenceMenu.add(pasteReferenceLastSelectionMI);
		pasteReferenceMenu.add(pasteReferenceClipboardMI);
		pasteCopyMI.setEnabled(false);
		pasteReferenceMI.setEnabled(false);

        _projectAddMenu = MenuUtils.makeBoldMenu("Add");
        _projectAddMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.addResourceAction));
        _projectAddMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.addIModelAction));
        _projectAddMenu.addSeparator();
        _addAndSubScribeMenuItem = MenuUtils.makeMenuItem(ProjectBuildListPanel.addAndSubScribeAction);
        _projectAddMenu.add(_addAndSubScribeMenuItem);
        _addAndSubScribeMenuItem.setEnabled(false);
        _projectAddMenu.addSeparator();
        _projectAddMenu.add(MenuUtils.makeMenuItem(AnalysisToolModelBuildPanel.importProjectAction));
        BuildMode.clipboard.addClipboardListener(new MenuClipboardListener());
        updateClipboardStatus();


        this._mapMenuIndex = this.addMenu.getItemCount();
		this.addMenu.add(this._mapMenu);
		this._addAndMapMenuIndex = this.addMenu.getItemCount();
		this.addMenu.add(this._addAndMapMenu);

		this._mapMenu.add(this._mapLastSelectionMI);
		this._mapMenu.add(this._mapClipboardMI);
		this._addAndMapMenu.add(this._addAndMapLastSelectionMI);
		this._addAndMapMenu.add(this._addAndMapClipboardMI);

		this._mapMI.setEnabled(false);
		this._addAndMapMI.setEnabled(false);

		addMenu.addSeparator();

		dataTypeIndex = addMenu.getItemCount();

		BuildMode.clipboard.addClipboardListener(new MenuClipboardListener());
		updateClipboardStatus();
	}

	public void setToolDataTypes(String toolType)
	{
		if (toolType == null || this._currentToolType.equals(toolType))
			return;
		this._currentToolType = toolType;
		// clear old dataTypes
		if (dataTypeMIs != null)
			for (int i = 0; i < dataTypeMIs.length; i++)
			{
				addMenu.remove(dataTypeMIs[i]);
			}
		JMenuItem[] newDataTypeMIs = (JMenuItem[]) AnalysisToolBuildMenus._toolDataTypeMIMap.get(toolType);
		if (newDataTypeMIs == null)
		{
			dataTypeMIs = null;
		}
		else
		{
			dataTypeMIs = newDataTypeMIs;
			for (int i = 0; i < dataTypeMIs.length; i++)
				addMenu.add(dataTypeMIs[i]);
		}
		addMenu.repaint();
	}

	public JMenu getEditMenu()
	{
		return _editMenu;
	}

    public void setEditTitle(String editTitle)
    {
        this._editMenu.setText(editTitle);
    }

	public JMenu getAddMenu()
	{
		return addMenu;
	}

    public JMenu getProjectAddMenu()
    {
        return _projectAddMenu;
    }

	protected void updateClipboardStatus()
	{
		if (isAddOk) {
			if (isClipboardEmpty == BuildMode.clipboard.isEmpty()) return; // already consistent
			isClipboardEmpty = BuildMode.clipboard.isEmpty();
			if (isClipboardEmpty) { // disable paste options
				_editMenu.remove(pasteCopyIndex);
				_editMenu.insert(pasteCopyMI, pasteCopyIndex);
				_editMenu.remove(pasteReferenceIndex);
				_editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
                _addAndSubScribeMenuItem.setEnabled(false);
			} else { // enable paste options
				_editMenu.remove(pasteCopyIndex);
				_editMenu.insert(pasteCopyMenu, pasteCopyIndex);
				_editMenu.remove(pasteReferenceIndex);
				_editMenu.insert(pasteReferenceMenu, pasteReferenceIndex);
                _addAndSubScribeMenuItem.setEnabled(true);
			}
		} else { // paste disabled anyways
			isClipboardEmpty = BuildMode.clipboard.isEmpty();
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
		if (isAddOk) return; // already enabled
//		contextMI.setEnabled(true);
		enableDataTypeMIs();
		isAddOk = true;
		updateClipboardStatus();
	}

	public void disableAddMenus()
	{
		if (!isAddOk) return; // already disabled
		_editMenu.remove(pasteCopyIndex);
		_editMenu.insert(pasteCopyMI, pasteCopyIndex);
		_editMenu.remove(pasteReferenceIndex);
		_editMenu.insert(pasteReferenceMI, pasteReferenceIndex);
//		contextMI.setEnabled(false);
		disableDataTypeMIs();
		isAddOk = false;
	}

	public void enableRemoveMenus()
	{
		if (isRemoveOk) return; // already enabled
		copyMI.setEnabled(true);
		cutMI.setEnabled(true);
		removeMI.setEnabled(true);
		deleteMI.setEnabled(true);
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
