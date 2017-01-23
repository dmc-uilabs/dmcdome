package toolinterfaceworkspace.gui.objectmodel.toolinterface.build.qmoointerface;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.build.ToolInterfaceDefinitionBuildPanel;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.build.ToolInterfaceTreeBuilderPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.dsm.DSMPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import com.touchgraph.graphlayout.GLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 25, 2003
 * Time: 2:55:26 AM
 * To change this template use Options | File Templates.
 */
public class QMOOInterfaceDefinitionBuildPanel extends ToolInterfaceDefinitionBuildPanel
{
    private QMOOInterfaceDataSplitPanel _runViewPanel;


    public QMOOInterfaceDefinitionBuildPanel(ToolInterfaceBuilder tInterface, DefaultContextBuilder interfaceContext,
                                       int numberColumns, String[] columnNames, int[] columnWidths)
    {
        super(tInterface);
        createComponents(interfaceContext, numberColumns, columnNames, columnWidths);
        layoutComponents();
    }

    public void createComponents(DefaultContextBuilder interfaceContext, int numberColumns, String[] columnNames, int[]columnWidths)
    {
        super.createComponents(interfaceContext, numberColumns, columnNames, columnWidths);
        _runViewPanel = new QMOOInterfaceDataSplitPanel(_tInterface);
        ifaceViewsPanel.add(ToolInterface.RUN_VIEW, _runViewPanel);
    }

    public void layoutComponents()
    {
        super.layoutComponents();
    }

    protected void switchView()
    {
        String newView = cbModel.getSelectedItem().toString();
        ifaceViewsCards.show(ifaceViewsPanel, newView);

        setMenuContext(); // do this before the next line!
        synchronizeViewControls(); // needs correct menu showing
        if (newView.equals(ToolInterface.BUILD_VIEW))
        {
            _tInterface.setCurrentView(ToolInterface.BUILD_VIEW);
        }
        else if (newView.equals(ToolInterface.RUN_VIEW))
        {
            _tInterface.setCurrentView(ToolInterface.RUN_VIEW);
        }
        else if (newView.equals(ToolInterface.CAUSALITY_VIEW))
        {
            _tInterface.setCurrentView(ToolInterface.CAUSALITY_VIEW);
        }
    }

    protected void setMenuContext()
    {
        MenuManager.setContext(getMenuContext());
        JComponent comp = (JComponent) ifaceViewsCards.getActiveComponent();
        BuildFocusTracker.notifyInFocus(comp, _tInterface);
        if (comp.equals(_buildViewPanel))
        {
            _buildViewPanel.setEditMenusForSelection(ModeContexts.BUILD_DOMEMODEL_INTERFACE_BUILDVIEW, false);
        }
        else if (comp.equals(_runViewPanel))
        {
//            _runViewPanel.setEditMenusForSelection();
        }
        else if (comp.equals(_causalityViewPanel))
        {
//            _causalityViewPanel.setEditMenusForSelection();
        }
        else
            return;
    }

    protected void synchronizeViewControls()
    {
        String currentView = ifaceViewsCards.getActiveName();
        if (currentView.equals(ToolInterface.BUILD_VIEW))
        {
            if (_tInterface instanceof ToolInterface)
            {
                _buildViewPanel.setEditMenusForSelection(ModeContexts.BUILD_DOMEMODEL_INTERFACE_BUILDVIEW, false);
            }

            // reset backbutton and nameField properties
            nameField.setDomeObject(_buildViewPanel.getContextTree().getRootContext());
            nameField.setEditable(isNameFieldEditable);
            backButton.setEnabled(isBackButtonEnabled);
            return;
        }
        if (currentView.equals(ToolInterface.RUN_VIEW))
        {
//            _runViewPanel.setEditMenusForSelection();
            setViewName(currentView);
//            return;
        }
        if (currentView.equals(ToolInterface.CAUSALITY_VIEW))
        {
//            sysCausalityPanel.setEditMenusForSelection();
            setViewName(currentView);
//            return;
        }
    }
}
