package mit.cadlab.dome3.gui.objectmodel.toolinterface.build.optimisation;


import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceDefinitionBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.optimisation.OptimisationToolInterfaceTreeBuildPanel;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 25, 2003
 * Time: 2:55:26 AM
 * To change this template use Options | File Templates.
 */
public class OptimisationInterfaceDefinitionBuildPanel extends ToolInterfaceDefinitionBuildPanel
{
    public static final String VIEW_RESULTS = "view results";

    public OptimisationInterfaceDefinitionBuildPanel(OptimizationInterfaceBuild tInterface, DefaultContextBuilder interfaceContext,
                                       int numberColumns, String[] columnNames, int[] columnWidths)
    {
        super(tInterface);
        createComponents(interfaceContext, numberColumns, columnNames, columnWidths);
        layoutComponents();
    }

    public void createComponents(DefaultContextBuilder interfaceContext, int numberColumns, String[] columnNames, int[]columnWidths)
    {
        super.createComponents(interfaceContext, numberColumns, columnNames, columnWidths);
        _causalityViewPanel = new OptimisationToolInterfaceTreeBuildPanel(_tInterface, ToolInterface.INTERFACE_CAUSALITY_VIEW);
        ifaceViewsPanel.add(ToolInterface.INTERFACE_CAUSALITY_VIEW, _causalityViewPanel);
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
        else if (newView.equals(ToolInterface.INTERFACE_CAUSALITY_VIEW))
        {
            _tInterface.setCurrentView(ToolInterface.INTERFACE_CAUSALITY_VIEW);
        }
        else if (newView.equals(ToolInterface.SYSTEM_CAUSALITY_VIEW))
        {
            _tInterface.setCurrentView(ToolInterface.SYSTEM_CAUSALITY_VIEW);
        }
    }

    protected void setMenuContext()
    {
        MenuManager.setContext(getMenuContext());
        JComponent comp = (JComponent) ifaceViewsCards.getActiveComponent();
        BuildFocusTracker.notifyInFocus(comp,  _tInterface);

    }

    protected void synchronizeViewControls()
    {
        String currentView = ifaceViewsCards.getActiveName();
        if (currentView.equals(ToolInterface.BUILD_VIEW))
        {
            nameField.setDomeObject(_buildViewPanel.getContextTree().getRootContext());
            nameField.setEditable(isNameFieldEditable);
            backButton.setEnabled(isBackButtonEnabled);
            return;
        }
        if (currentView.equals(ToolInterface.INTERFACE_CAUSALITY_VIEW) || currentView.equals(ToolInterface.SYSTEM_CAUSALITY_VIEW))
        {
            setViewName(currentView);
        }
    }
}
