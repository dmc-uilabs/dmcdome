package toolinterfaceworkspace.gui.objectmodel.toolinterface.build.qmoointerface;

import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildTree;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.build.ToolInterfaceBuildPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 7:58:54 AM
 * To change this template use Options | File Templates.
 */
public class QMOOInterfaceBuildPanel extends ToolInterfaceBuildPanel
{
    public static final String QMOO = "QMOO";

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
        UPPER_COLUMN,
        LOWER_COLUMN,
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

    private QMOOInterfaceDefinitionBuildPanel _definitionPanel;
    private JPanel _configurationPanel;

    public QMOOInterfaceBuildPanel(ToolInterfaceBuilder toolInterface)
    {
        super(toolInterface);
        createComponents();
        layoutComponents();
    }

    protected void createComponents()
    {
        super.createComponents();

        _definitionPanel = new QMOOInterfaceDefinitionBuildPanel(_tInterface,
                (DefaultContextBuilder)_tInterface.getBuildContext(), definitionPanelColumnWidths.length,
                definitionPanelColumns, definitionPanelColumnWidths);

        contentTabs.addTab(DEFINITION, _definitionPanel);

        _configurationPanel = makeConfigurationPanel();

        contentTabs.addTab(CONFIGURATION, _configurationPanel);
        contentTabs.addTab(DOCUMENTATION, docPanel);

    }

    // Tool interface
	public String getTitlePrefix()
	{
		return QMOO + " Interface: ";
	}

	public String getTitle()
	{
		return getTitlePrefix();
	}

    public void setMenuContext()
    {

    }

    public String getHelpContext()
    {
        return "";
    }

    public JPanel makeConfigurationPanel()
    {
        JPanel pane = new JPanel();
        BuildTree tree = new BuildTree(new ArrayList());
        String[] columnNames = new String[]{"name", "value"};
        int[] columnWidths = new int[]{60, 160};
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
