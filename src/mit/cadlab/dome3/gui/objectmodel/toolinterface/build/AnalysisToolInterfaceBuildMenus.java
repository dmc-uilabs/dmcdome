package mit.cadlab.dome3.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import javax.swing.*;
import java.util.List;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Mar 11, 2004
 * Time: 12:30:57 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolInterfaceBuildMenus
{
    public static final String[] variableDataTypes = new String[] {"Real"};
    public static final String[] objectiveDataTypes = new String[] {"Real"};

    public static final AnalysisToolInterfaceBuildMenus menus = new AnalysisToolInterfaceBuildMenus();

    // editMenu
    protected JMenu _editMenu = MenuUtils.makeBoldMenu("Edit Interface");
    protected JMenu _pasteCopyMenu = MenuUtils.makeMenu("Paste copy");
    protected JMenu _mapMenu = MenuUtils.makeMenu("Map");
    protected JMenu _addAndMapMenu = MenuUtils.makeMenu("Add and Map");
    protected JMenuItem _copyMI, _cutMI, _pasteCopyLastSelectionMI, _pasteCopyClipboardMI,
    _mapLastSelectionMI, _mapClipboardMI, _addAndMapLastSelectionMI, _addAndMapClipboardMI, _deleteMI;
    protected JMenuItem _pasteCopyMI = MenuUtils.makeMenuItem("Paste copy");
    protected JMenuItem _mapMI = MenuUtils.makeMenuItem("Map");
    protected JMenuItem _addAndMapMI = MenuUtils.makeMenuItem("Add and Map");
    protected JMenuItem _selectAllMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.selectAllAction);
    protected JMenuItem _clearSelMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.clearSelectionAction);

    // addMenu
    protected JMenu _addMenu = MenuUtils.makeBoldMenu("Add");
    protected JMenu _variableMenu, _objectiveMenu;
    protected JMenuItem[] _variableDataTypeMIs;
    protected JMenuItem[] _objectiveDataTypeMIs;
    protected JMenuItem _variableMI, _objectiveMI;
    protected JMenuItem _contextMI = MenuUtils.makeMenuItem(new ToolInterfaceTreeBuilderPanel.AddItemAction("Context"));
    protected JMenuItem _relationMI = MenuUtils.makeMenuItem(new ToolInterfaceTreeBuilderPanel.AddItemAction("Procedural Relation"));

    protected boolean _isAddOk = true;
    protected boolean _isRemoveOk = true;
    protected boolean _isClipboardEmpty;
    protected int _pasteCopyIndex, _mapIndex, _addAndMapIndex;

    private AnalysisToolInterfaceBuildMenus()
    {
        _variableMI = MenuUtils.makeMenuItem(new ToolInterfaceTreeBuilderPanel.AddItemAction(QMOOConfiguration.VARIABLE));
        _objectiveMI = MenuUtils.makeMenuItem(new ToolInterfaceTreeBuilderPanel.AddItemAction(QMOOConfiguration.OBJECTIVE));
        _copyMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.copyAction);
        _cutMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.cutAction);
        _pasteCopyLastSelectionMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.pasteCopyLastSelectionAction);
        _pasteCopyClipboardMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.pasteCopyClipboardAction);
        _mapLastSelectionMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.mapLastSelectionAction);
        _mapClipboardMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.mapClipboardAction);
        _addAndMapLastSelectionMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.addAndMapLastSelectionAction);
        _addAndMapClipboardMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.addAndMapClipboardAction);
        _deleteMI = MenuUtils.makeMenuItem(ToolInterfaceTreeBuilderPanel.deleteAction);

        _editMenu.add(_copyMI);
        _editMenu.add(_cutMI);
        _pasteCopyIndex = _editMenu.getItemCount();
        _editMenu.add(_pasteCopyMenu);
        _editMenu.addSeparator();
        _editMenu.add(_clearSelMI);
        _editMenu.add(_selectAllMI);
        _editMenu.addSeparator();
        _editMenu.add(_deleteMI);
        _pasteCopyMenu.add(_pasteCopyLastSelectionMI);
        _pasteCopyMenu.add(_pasteCopyClipboardMI);
        _pasteCopyMI.setEnabled(false);

        _mapIndex = _addMenu.getItemCount();
        _addMenu.add(_mapMenu);
        _addAndMapIndex = _addMenu.getItemCount();
        _addMenu.add(_addAndMapMenu);

        _mapMenu.add(_mapLastSelectionMI);
        _mapMenu.add(_mapClipboardMI);
        _addAndMapMenu.add(_addAndMapLastSelectionMI);
        _addAndMapMenu.add(_addAndMapClipboardMI);
        _mapMI.setEnabled(false);
        _addAndMapMI.setEnabled(false);

        _addMenu.addSeparator();
        _addMenu.add(_variableMI);
        _addMenu.add(_objectiveMI);

        BuildMode.clipboard.addClipboardListener(new MenuClipboardListener());
        updateClipboardStatus();
    }

    public JMenu getEditMenu()
    {
        return _editMenu;
    }

    public JMenu getAddMenu()
    {
        return _addMenu;
    }

    protected void updateClipboardStatus()
    {
        if (_isAddOk)
        {
            if (_isClipboardEmpty == BuildMode.clipboard.isEmpty()) return; // already consistent
            _isClipboardEmpty = BuildMode.clipboard.isEmpty();
            if (_isClipboardEmpty)
            { // disable paste options
                _editMenu.remove(_pasteCopyIndex);
                _editMenu.insert(_pasteCopyMI, _pasteCopyIndex);
                disableMapMIs();
                disableAddAndMapMIs();
            }
            else
            { // enable paste options
                _editMenu.remove(_pasteCopyIndex);
                _editMenu.insert(_pasteCopyMenu, _pasteCopyIndex);
                enableMapMIs();
                enableAddAndMapMIs();
            }
        }
        else
        { // paste disabled anyways
            _isClipboardEmpty = BuildMode.clipboard.isEmpty();
        }
    }

    public void enableMapMIs()
    {
        if (!_isAddOk) return;
        _addMenu.remove(_mapIndex);
        _addMenu.insert(_mapMenu, _mapIndex);
    }

    public void disableMapMIs()
    {
        _addMenu.remove(_mapIndex);
        _addMenu.insert(_mapMI, _mapIndex);
    }

    public void enableAddAndMapMIs()
    {
//            System.out.println("enableAddAndMap");
        _addMenu.remove(_addAndMapIndex);
        _addMenu.insert(_addAndMapMenu, _addAndMapIndex);
    }

    public void disableAddAndMapMIs()
    {
//            System.out.println("disableAddAndMap");
        _addMenu.remove(_addAndMapIndex);
        _addMenu.insert(_addAndMapMI, _addAndMapIndex);
    }

    private void enableDataTypeMIs()
    {
        for (int i = 0; i < _variableDataTypeMIs.length; ++i)
        {
            _variableDataTypeMIs[i].setEnabled(true);
            _objectiveDataTypeMIs[i].setEnabled(true);
        }
    }

    private void disableDataTypeMIs()
    {
        for (int i = 0; i < _variableDataTypeMIs.length; ++i)
        {
            _variableDataTypeMIs[i].setEnabled(false);
            _objectiveDataTypeMIs[i].setEnabled(false);
        }
    }

    public void enableContextRelationMIs()
    {
        _contextMI.setEnabled(true);
        _relationMI.setEnabled(true);
    }

    public void disableContextRelationMIs()
    {
        _contextMI.setEnabled(false);
        _relationMI.setEnabled(false);
    }

    public void enableContextMIs()
    {
        _contextMI.setEnabled(true);
    }

    public void disableContextMIs()
    {
        _contextMI.setEnabled(false);
    }

    public void enableRelationMIs()
    {
        _relationMI.setEnabled(true);
    }

    public void disableRelationMIs()
    {
        _relationMI.setEnabled(false);
    }

    public void enableSelectionMIs()
    {
        _selectAllMI.setEnabled(true);
        _clearSelMI.setEnabled(true);
    }

    public void disableSelectionMIs()
    {
        _selectAllMI.setEnabled(false);
        _clearSelMI.setEnabled(false);
    }

    public void enableAddMenus()
    {
        if (!_isAddOk)
        { // disabled before
            if (!_isClipboardEmpty)
            {
                _editMenu.remove(_pasteCopyIndex);
                _editMenu.insert(_pasteCopyMenu, _pasteCopyIndex);
                enableAddAndMapMIs();
            }
            enableDataTypeMIs();
            enableContextRelationMIs();
            _isAddOk = true;
        }
    }

    public void disableAddMenus()
    {
        if (!_isAddOk) return; // already disabled
        if (!_isClipboardEmpty)
        {
            _editMenu.remove(_pasteCopyIndex);
            _editMenu.insert(_pasteCopyMI, _pasteCopyIndex);
            disableMapMIs();
            disableAddAndMapMIs();
        }
        disableDataTypeMIs();
        disableContextRelationMIs();
        _isAddOk = false;
    }

    public void enableRemoveMenus()
    {
        if (_isRemoveOk) return; // already enabled
        _copyMI.setEnabled(true);
        _cutMI.setEnabled(true);
        _deleteMI.setEnabled(true);
        _isRemoveOk = true;
    }

    public void disableRemoveMenus()
    {
        if (!_isRemoveOk) return; // already disabled
        _copyMI.setEnabled(false);
        _cutMI.setEnabled(false);
        _deleteMI.setEnabled(false);
        _isRemoveOk = false;
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
