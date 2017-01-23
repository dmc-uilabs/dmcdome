package toolinterfaceworkspace.gui.objectmodel.toolinterface.build;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;
import toolinterfaceworkspace.objectmodel.toolinterface.AbstractToolInterface;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGUIComboBoxModel;
import mit.cadlab.dome3.gui.guiutils.customGui.customGuiChooser;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.tool.ToolModel;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 11:23:51 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceBuildPanel extends AbstractDomeObjectGui
{
    public static final GridBagConstraints gbc = null;

    public static final TypeInfo TYPE_INFO = new TypeInfo("ToolInterfaceBuildPanel");
    public static final String XML_TAG = "toolinterfacebuildpanel";
    public static final String TOOLNAME = ", for ";
    public static final String DEFINITION = "definition";
    public static final String CONFIGURATION = "configuration";
    public static final String DOCUMENTATION = "documentation";

    protected ToolInterfaceBuilder _tInterface;
    protected NameTextField nameField;
    protected DefaultComboBoxModel cbModel;
    protected JTabbedPane contentTabs;
    protected JButton modelViewButton;
    protected DomeBuildFrame modelviewDialog;
    protected boolean isModelviewDialogOPen;
    protected DocumentationBuildPanel docPanel;
    protected HashMap GuiToComboBoxMap = new HashMap();//key: filedata of comboboxmodel, value, gui card. except DEFAULT

    public ToolInterfaceBuildPanel(ToolInterfaceBuilder ti)
    {
        super(ti);
        _tInterface = ti;

    }

    protected void createComponents()
    {
        nameField = new NameTextField();
		nameField.setDomeObject(_tInterface);
		NameListener ifaceNameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				Container cont = getTopLevelAncestor();
				((DomeBuildFrame) cont).setTitle(getTitlePrefix() + newName +
				                                 TOOLNAME + _tInterface.getModel().getName());
			}
		};

        ImageIcon modelIcon = Templates.makeImageIcon(DomeIcons.MODEL);
        modelViewButton = Templates.makeImageButton(modelIcon);
        modelViewButton.setEnabled(true);

		_tInterface.addPropertyChangeListener(NameListener.NAME, ifaceNameListener);

		NameListener modelNameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				Container cont = getTopLevelAncestor();
				((DomeBuildFrame) cont).setTitle(getTitlePrefix() + _tInterface.getName() +
				                                 TOOLNAME + newName);
			}
		};

		_tInterface.getModel().addPropertyChangeListener(NameListener.NAME, modelNameListener);

		//TODO:
        //TODO: in the future tool interfaces might have custom guis
        //TODO: when custom guis do get implemented, the code will go here
        //TODO:

		contentTabs = Templates.makeTabbedPane();

        contentTabs.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                setMenuContext();
            }
        });

        docPanel = new DocumentationBuildPanel(_tInterface.getDocumentation());

	}

    public void setMenuContext()
    {
    }

    public String getHelpContext()
    {
        return null;
    }

    public String getTitlePrefix()
    {
        return null;
    }

    protected void layoutComponents()
    {
		JComponent[] comps = {makeControlPanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

    protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();

        JComponent[] comps = {

            Templates.makeLabel("name:"),
            nameField,

        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 10), 0, 0),

        };

        Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}
}
