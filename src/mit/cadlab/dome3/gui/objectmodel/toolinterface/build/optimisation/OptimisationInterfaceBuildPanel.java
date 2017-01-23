package mit.cadlab.dome3.gui.objectmodel.toolinterface.build.optimisation;

import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGUIComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceBuildPanel;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 7:58:54 AM
 * To change this template use Options | File Templates.
 */
public class OptimisationInterfaceBuildPanel extends ToolInterfaceBuildPanel
{
    public static final String NAME_COLUMN = "name";
    public static final String VALUE_COLUMN = "value";
    public static final String ACTIVE_COLUMN = "active";
    public static final String MAPPING_COLUMN = "mapping";

    public static final String[] definitionPanelColumns = {

        NAME_COLUMN,
        VALUE_COLUMN,
        ACTIVE_COLUMN,
        MAPPING_COLUMN
    };

    public static final int[] definitionPanelColumnWidths = {

        100,
        150,
        100,
        150
    };

    public static final String UPPER_COLUMN = "upper";
    public static final String LOWER_COLUMN = "lower";

    public static final String[] definitionRunPanelColumns = {

        NAME_COLUMN,
        VALUE_COLUMN,
        LOWER_COLUMN,
        UPPER_COLUMN,
        ACTIVE_COLUMN,
        MAPPING_COLUMN
    };

    public static final int[] definitionRunPanelColumnWidths = {

        100,
        100,
        50,
        50,
        100,
        100
    };

    private OptimisationInterfaceDefinitionBuildPanel _defPanel;
    private JPanel _configurationPanel;

    public OptimisationInterfaceBuildPanel(AnalysisToolInterfaceBase toolInterface)
    {
        super(toolInterface);
        createComponents();
        layoutComponents();
    }

    protected void createComponents()
    {
        super.createComponents();

        _defPanel = new OptimisationInterfaceDefinitionBuildPanel((OptimizationInterfaceBuild)_tInterface,
                (DefaultContextBuilder)_tInterface.getBuildContext(), definitionPanelColumnWidths.length,
                definitionPanelColumns, definitionPanelColumnWidths);

        _definitionPanel.add(CustomGUIComboBoxModel.DEFAULT, _defPanel);

        addAllCustomGUIs();

        contentTabs.addTab(DEFINITION, _definitionPanel);

        _configurationPanel = makeConfigurationPanel();

        contentTabs.addTab(CONFIGURATION, _configurationPanel);
        contentTabs.addTab(DOCUMENTATION, docPanel);

        contentTabs.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                setMenuContext();
            }
        });

    }



    public void setMenuContext()
    {
        switch (contentTabs.getSelectedIndex())
        {
            case 0: // definition
                _defPanel.setMenuContext();
                return;
        }
        BuildFocusTracker.notifyInFocus(this, _tInterface);
    }

    public String getHelpContext()
    {
        return "";
    }

    public JPanel makeConfigurationPanel()
    {
        JPanel pane = new JPanel();
        BuildTree tree = new BuildTree(((OptimizationInterfaceBuild)_tInterface).getOptimizationInterfaceConfiguration().getSetupContextFolders());
        String[] columnNames = new String[]{"name", "value"};
        int[] columnWidths = new int[]{80, 140};
        BuildTreeTable table = new BuildTreeTable(tree, columnNames.length, columnNames, columnWidths, true);

        GridBagConstraints[] gbcs = {

            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.white);
        Dimension d = table.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(d.width, 200));
        JComponent[] components = {scrollPane};
        Templates.layoutGridBagB(pane, components, gbcs);
        return pane;
    }
}
